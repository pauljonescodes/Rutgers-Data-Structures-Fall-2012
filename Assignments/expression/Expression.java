package expression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import dom.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
	/**
	 * Positions of opening brackets
	 */
	ArrayList<Integer> openingBracketIndex; 
    
	/**
	 * Positions of closing brackets
	 */
	ArrayList<Integer> closingBracketIndex; 

    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
        scalars = null;
        arrays = null;
        openingBracketIndex = null;
        closingBracketIndex = null;
    }

    /**
	 * Matches parentheses and square brackets. Populates the openingBracketIndex and
	 * closingBracketIndex array lists in such a way that closingBracketIndex[i] is
	 * the position of the bracket in the expression that closes an opening bracket
	 * at position openingBracketIndex[i]. For example, if the expression is:
	 * <pre>
	 *    (a+(b-c))*(d+A[4])
	 * </pre>
	 * then the method would return true, and the array lists would be set to:
	 * <pre>
	 *    openingBracketIndex: [0 3 10 14]
	 *    closingBracketIndex: [8 7 17 16]
	 * </pre>
	 * 
	 * @return True if brackets are matched correctly, false if not
	 */
	public boolean isLegallyMatched() {

		char[] expression = expr.toCharArray();
		int delimiterCount = 0;
		int squareBracketOpeningCount = 0;
		int squareBracketClosingCount = 0;
		int prnthsBracketOpeningCount = 0;
		int prnthsBracketClosingCount = 0;

		for (int i = 0; i < expression.length; i++) {
			if (this.isBracketCharacter(expression[i])) {
				delimiterCount++;

				if (expression[i] == '[') {
					squareBracketOpeningCount++;
				} else if (expression[i] == ']') {
					squareBracketClosingCount++;
				} else if (expression[i] == ')'){
					prnthsBracketClosingCount++;
				} else if (expression[i] == '(') {
					prnthsBracketOpeningCount++;
				}
			}
		}

		boolean isLegallyMatched = false;

		if (delimiterCount == 0) {
			isLegallyMatched = true;
		} else if (delimiterCount % 2 != 0) {
			isLegallyMatched = false;
		} else if (squareBracketOpeningCount != squareBracketClosingCount) {
			isLegallyMatched = false;
		} else if (prnthsBracketOpeningCount != prnthsBracketClosingCount) {
			isLegallyMatched = false;
		} else {
			isLegallyMatched = this.isLegallyMatched(expression);
		}

		if (isLegallyMatched) {

			openingBracketIndex = new ArrayList<Integer>();
			closingBracketIndex = new ArrayList<Integer>();

			for (int i = 0; i < expr.length(); i++) { 
				if (this.isOpeningBracket(expr.charAt(i))) {
					openingBracketIndex.add(i);

					System.out.println("Opening at: " + i);

					char startingBracket = expr.charAt(i);

					char equivilentBracket = this.getEquivalentBracket(startingBracket);
					char disruptiveBracket = this.getDisruptiveBracket(startingBracket);
					char conclusiveBracket = this.getConclusiveBracket(startingBracket);

					int skips = 1;

					for (int j = i + 1; j < expression.length; j++) {
						if (expr.charAt(j) == equivilentBracket){
							skips++;
						} else if (expr.charAt(j) == disruptiveBracket) {
							skips++;
						} else if (expr.charAt(j) == this.getConclusiveBracket(disruptiveBracket)) {
							skips--;
						} else if (expr.charAt(j) == conclusiveBracket) {
							skips--;
						}

						if (expr.charAt(j) == conclusiveBracket && skips == 0) {
							System.out.println("Closing bracket at: " + j);
							closingBracketIndex.add(j);
							break;
						}
					}
				}
			}

			return true;
		} 

		return false;
	}

	private boolean isLegallyMatched(char[] expression) {
		Stack <Character> brackets = new Stack<Character>();
		for (int i = 0; i < expression.length; i++) {
			if (expression[i] == '[' || expression[i] == '(') {
				brackets.push(expression[i]);
			} else if (expression[i] == ']' || expression[i] == ')') {
				if (brackets.isEmpty()) {
					return false;
				} 

				char lastBracket = brackets.pop();

				if (((expression[i] == ')' ) && (lastBracket != '(' ))
						||  ((expression[i] == ']' ) && (lastBracket != '['))) {
					return false;
				}
			}
		}

		return brackets.isEmpty();
	}

	@SuppressWarnings("unused")
	private boolean isLegallyMatched(String expression) {

		char startingBracket = this.getStartingBracket(expression);
		char endingBracket = this.getEndingBracket(expression);
		int bracketCount = this.getBracketCount(expression);

		if (bracketCount == 0) {
			return true;
		} else if (bracketCount == 1) {
			return false;
		} else if (bracketCount == 2 && this.getConclusiveBracket(startingBracket) == endingBracket) {
			return true;
		} else if (bracketCount == 2 && this.getConclusiveBracket(startingBracket) != endingBracket) {
			return false;
		} else if (this.isBracketCharacter(startingBracket)) {
			char equivalentBracket = this.getEquivalentBracket(startingBracket);
			char conclusiveBracket = this.getConclusiveBracket(startingBracket);
			char disruptiveBracket = this.getDisruptiveBracket(startingBracket);

			String[] subexpression = this.splitExpression(expression);

			for (int i = this.getStartingBracketIndex(expression) + 1; i < expression.length(); i++) {
				if (expression.charAt(i) == conclusiveBracket){
					return true;
				} else if (expression.charAt(i) == equivalentBracket || expression.charAt(i) == disruptiveBracket) {
					if (subexpression[1].length() == 0){
						return this.isLegallyMatched(this.shedOuterBrackets(expression));
					} else {
						return this.isLegallyMatched(subexpression[0]) && this.isLegallyMatched(subexpression[1]);
					}
				}
			}
		}

		return false; // this should never execute
	}

	private String[] splitExpression(String expression) {

		String[] expressions = new String[2];

		int skips = 1;
		int endex = 0;
		int index = this.getStartingBracketIndex(expression) + 1;

		char startingBracket = this.getStartingBracket(expression);

		char conclusiveBracket = this.getConclusiveBracket(startingBracket);
		char equivilentBracket = this.getEquivalentBracket(startingBracket);
		char disruptiveBracket = this.getDisruptiveBracket(startingBracket);

		for (; index < expression.length(); index++) {
			if (expression.charAt(index) == equivilentBracket){
				skips++;
			} else if (expression.charAt(index) == disruptiveBracket) {
				skips++;
			} else if (expression.charAt(index) == this.getConclusiveBracket(disruptiveBracket)) {
				skips--;
			} else if (expression.charAt(index) == conclusiveBracket) {
				skips--;
			}

			if (expression.charAt(index) == conclusiveBracket && skips == 0) {
				endex = index;
				break;
			}
		}

		endex++;

		expressions[0] = expression.substring(0, endex);
		expressions[1] = expression.substring(endex);

		return expressions;
	}

	private String shedOuterBrackets(String expression) {
		if (this.getConclusiveBracket(this.getStartingBracket(expression)) == this.getEndingBracket(expression))
			return expression.substring(this.getStartingBracketIndex(expression) + 1, this.getEndingBracketIndex(expression));
		else
			return expression;
	}

	private char getStartingBracket(String expression) {

		for (int i = 0; i < expression.length(); i++) {
			if (expression.charAt(i) == '[') {
				return '[';
			} else if (expression.charAt(i) == ']') {
				return ']';
			} else if (expression.charAt(i) == '(') {
				return '(';
			} else if (expression.charAt(i) == ')') {
				return ')';
			} 
		}

		return '{';
	}

	private char getEndingBracket(String expression) {
		for (int i = expression.length() - 1; i != 0; i--)
			if (this.isClosingBracket(expression.charAt(i)))
				return expression.charAt(i);
		return '}';
	}

	private int getEndingBracketIndex(String expression) {
		for (int i = expression.length() - 1; i != 0; i++)
			if (this.isClosingBracket(expression.charAt(i)))
				return i;
		return expression.length();
	}

	private int getBracketCount(String expression) {
		int count = 0;

		for (int i = 0; i < expression.length(); i++) {
			if (expression.charAt(i) == '[') {
				count++;
			} else if (expression.charAt(i) == ']') {
				count++;
			} else if (expression.charAt(i) == '(') {
				count++;
			} else if (expression.charAt(i) == ')') {
				count++;
			} 
		}

		return count;
	}

	private int getStartingBracketIndex(String expression) {
		for (int i = 0; i < expression.length(); i++)
			if (expression.charAt(i) == '[' || expression.charAt(i) == '(')
				return i;

		return 0;
	}

	private char getEquivalentBracket(char startingBracket) {
		return startingBracket;
	}

	private char getConclusiveBracket(char startingBracket) {
		if (startingBracket == '(') 
			return ')';
		else 
			return ']';
	}

	private char getDisruptiveBracket(char startingBracket) {
		if (startingBracket == '(') 
			return '[';
		else 
			return '(';
	}

	private boolean isBracketCharacter(char potentialBracket) {
		if (potentialBracket == '[' || potentialBracket == ']' || potentialBracket == '(' || potentialBracket == ')')
			return true;
		else return false;
	}

	private boolean isOpeningBracket(char potentialOpeningBracket) {
		if (potentialOpeningBracket == '(') {
			return true;
		} else if (potentialOpeningBracket == '[') {
			return true;
		} else return false;
	}

	private boolean isClosingBracket(char potentialOpeningBracket) {
		if (potentialOpeningBracket == ']') {
			return true;
		} else if (potentialOpeningBracket == ')') {
			return true;
		} else return false;
	}
	/**
	 * Populates the scalars and arrays lists with symbols for scalar and array
	 * variables in the expression. For every variable, a SINGLE symbol is created and stored,
	 * even if it appears more than once in the expression.
	 * At this time, values for all variables are set to
	 * zero - they will be loaded from a file in the loadSymbolValues method.
	 */
	public void buildSymbols() {
		scalars = new ArrayList<ScalarSymbol>();
		arrays = new ArrayList<ArraySymbol>();

		for (int i = 0; i < expr.length(); i++){
			if (Character.isLetter(expr.charAt(i))) {
				String currentSymbol = "";
				boolean isArray = false;

				while (i < expr.length() &&Character.isLetter(expr.charAt(i))){
					currentSymbol += expr.charAt(i);
					i++;
				}
				
				System.out.println("currentSumbol: " + currentSymbol);
				
				if (i < expr.length() && expr.charAt(i) == '[')
					isArray = true;

				if (isArray) {
					ArraySymbol currentArray = new ArraySymbol(currentSymbol);
					arrays.add(currentArray);
				} else {
					ScalarSymbol currentScalar = new ScalarSymbol(currentSymbol);
					scalars.add(currentScalar);
				}
			}
		}
	}
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
/**
	 * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
	 * subscript expressions.
	 * 
	 * @return Result of evaluation
	 */
	public float evaluate() {
		// a - (b+A[B[2]])*d + 3

		String expression = expr;

		for (int i = 0; i < this.scalars.size(); i++) {
			expression = expression.replace(this.scalars.get(i).name, "" + this.scalars.get(i).value);
		}
		System.out.println(expression);

		expression = this.evaluate(expression);

		System.out.println(expression);

		try {
			return Float.parseFloat(expression);
		} catch (Exception e) {
			return 0;
		}

	}

	private String evaluate (String expression) {
		
		System.out.println(expression);
		
		if (expression == null || expression.length() == 0) {
			System.out.println("I returned zero. I'm sorry. I know you hate that.");
			return "0";
		}
		
		if (expression.indexOf('[') == -1) {
			if (expression.indexOf('(') == -1) {
				expression = expression.replace(" ", "");
				expression = " " + expression;

				float firstNumber;
				float seconNumber;

				int firstNumdex;
				int seconNumdex;

				boolean isNegative = false;

				int numberOfOperators = 0;

				for (int i = 2; i < expression.length(); i++) {
					if (this.isAdditionCharacter(expression.charAt(i)))
						numberOfOperators++;
					if (this.isSubtractionCharacter(expression.charAt(i)))
						numberOfOperators++;
					if (this.isMultiplicationCharacter(expression.charAt(i)))
						numberOfOperators++;
					if (this.isDivisionCharacter(expression.charAt(i))) {
						numberOfOperators++;
					}
				}

				System.out.println("noo: " + numberOfOperators);

				if (numberOfOperators == 0)
					return expression;

				System.out.println(expression);

				for (int i = 0; i < expression.length(); i++) {
					if (i == 1 && this.isSubtractionCharacter(expression.charAt(i))) {
						isNegative = true;
					} else if (this.isMultiplicationCharacter(expression.charAt(i)) || this.isDivisionCharacter(expression.charAt(i))) {
						firstNumdex = seconNumdex = i;

						while (firstNumdex > 0 && (Character.isDigit(expression.charAt(firstNumdex - 1)) || expression.charAt(firstNumdex - 1) == '.')) {
							firstNumdex--;
						}

						while (seconNumdex < expression.length() - 1 && (Character.isDigit(expression.charAt(seconNumdex + 1)) || expression.charAt(seconNumdex + 1) == '.')) {
							seconNumdex++;
						}


						try {
							firstNumber = Float.parseFloat(expression.substring(firstNumdex, i));
						} catch (Exception e){
							firstNumber = 0;
						}

						boolean secondNumberIsSupposedToBeZero = true;

						try {
							seconNumber = Float.parseFloat(expression.substring(i + 1, seconNumdex + 1));
						} catch (Exception e) {
							seconNumber = 0;
							secondNumberIsSupposedToBeZero = false;
						}

						String result = "";

						if (isNegative)
							firstNumber = -1 * firstNumber;

						if (this.isMultiplicationCharacter(expression.charAt(i))) {
							result = "" + firstNumber * seconNumber;

						} else {
							if (seconNumber == 0 && secondNumberIsSupposedToBeZero)
								throw new IllegalArgumentException("Argument 'divisor' is 0");
							else if (seconNumber == 0 && !secondNumberIsSupposedToBeZero)
								seconNumber = 1;

							result = "" + firstNumber / seconNumber;
						}

						int stadex = 0;
						if (isNegative)
							stadex = 2;

						expression = expression.substring(stadex, firstNumdex) + result + expression.substring(seconNumdex+ 1);

						i = 0;
						isNegative = false;
					}
				}

				isNegative = false;

				for (int i = 0; i < expression.length(); i++) {
					if (i == 1 && this.isSubtractionCharacter(expression.charAt(i))) {
						isNegative = true;
					} else if (this.isAdditionCharacter(expression.charAt(i)) || this.isSubtractionCharacter(expression.charAt(i))) {
						firstNumdex = seconNumdex = i;

						while (firstNumdex > 0 && (Character.isDigit(expression.charAt(firstNumdex - 1)) || expression.charAt(firstNumdex - 1) == '.')) {
							firstNumdex--;
						}

						while (seconNumdex < expression.length() - 1 && (Character.isDigit(expression.charAt(seconNumdex + 1)) || expression.charAt(seconNumdex + 1) == '.')) {
							seconNumdex++;
						}

						try {
							firstNumber = Float.parseFloat(expression.substring(firstNumdex, i));
						} catch (Exception e) {
							firstNumber = 0;
						}
						
						try {
							seconNumber = Float.parseFloat(expression.substring(i + 1, seconNumdex + 1));
						} catch (Exception e) {
							seconNumber = 0;
						}

						String result = "";

						if (isNegative)
							firstNumber = -1 * firstNumber;

						if (this.isAdditionCharacter(expression.charAt(i))) {
							result = "" + (firstNumber + seconNumber);

						} else {
							result = "" + (firstNumber - seconNumber);
						}

						int stadex = 0;
						if (isNegative)
							stadex = 2;

						expression = expression.substring(stadex, firstNumdex) + result + expression.substring(seconNumdex+1);

						i = 0;
						isNegative = false;
					}
				}

				expression = expression.replace(" ", "");

				return expression;
			} else {
				for (int index = 0; index < expression.length(); index++){
					if (this.isOpeningBracket(expression.charAt(index))) {
						int begex = index;


						int skips = 1;
						index++;
						int endex = index;

						for (; endex < expression.length(); endex++) {
							if (expression.charAt(endex) == '(')
								skips++;
							if (expression.charAt(endex) == ')')
								skips--;

							if (skips == 0 && expression.charAt(endex) == ')')
								break;
						}

						String beginning = expression.substring(0, begex);
						String middle = this.evaluate(expression.substring(index, endex));
						String end = expression.substring(endex + 1);

						return this.evaluate(beginning + middle + end);
					}
				}
			}
		} else {
			for (int index = 0; index < expression.length(); index++){
				if (Character.isLetter(expression.charAt(index))) {
					String currentSymbol = "";
					boolean isArray = false;

					int begex = index;

					while (index < expression.length() && Character.isLetter(expression.charAt(index))){
						currentSymbol += expression.charAt(index);
						index++;
					}
					if (index < expression.length() && expression.charAt(index) == '[')
						isArray = true;

					if (isArray) {
						int skips = 1;
						index++;
						int endex = index;

						for (; endex < expression.length(); endex++) {
							if (expression.charAt(endex) == '[')
								skips++;
							if (expression.charAt(endex) == ']')
								skips--;

							if (skips == 0 && expression.charAt(endex) == ']')
								break;
						}

						String beforeArray = expression.substring(0, begex);
						String arrayAddress = expression.substring(index, endex);
						String afterArray = expression.substring(endex+1);

						String evaluatedAddress = this.evaluate(expression.substring(index, endex));

						String evaluatedArray = "" + this.arrayWithName(currentSymbol).values[(int)Float.parseFloat(evaluatedAddress)];

						return this.evaluate(beforeArray + evaluatedArray + afterArray); // HAI TA. Here's my recursion. Okay goodnight.
					}
				}
			}
		}
		return "";
	}

	private ArraySymbol arrayWithName(String name) {
		for (int i = 0; i < arrays.size(); i++) {
			if (arrays.get(i).name.equals(name)){
				return arrays.get(i);
			}
		}

		return null;
	}

	private boolean isMultiplicationCharacter(char c) {
		return c == '*';
	}

	private boolean isDivisionCharacter(char c) {
		return c == '/';
	}

	private boolean isAdditionCharacter(char c) {
		return c == '+';
	}

	private boolean isSubtractionCharacter(char c) {
		return c == '-';
	}

    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    	for (ArraySymbol as: arrays) {
    		System.out.println(as);
    	}
    }

}
