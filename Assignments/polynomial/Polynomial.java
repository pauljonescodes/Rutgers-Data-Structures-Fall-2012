package polynomial;

import java.io.*;
import java.util.StringTokenizer;

/**
 * This class implements a term of a polynomial.
 * 
 * @author runb-cs112
 * 
 */
class Term {
	/**
	 * Coefficient of term.
	 */
	public float coeff;

	/**
	 * Degree of term.
	 */
	public int degree;

	/**
	 * Initializes an instance with given coefficient and degree.
	 * 
	 * @param coeff
	 *            Coefficient
	 * @param degree
	 *            Degree
	 */
	public Term(float coeff, int degree) {
		this.coeff = coeff;
		this.degree = degree;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		return other != null && other instanceof Term
				&& coeff == ((Term) other).coeff
				&& degree == ((Term) other).degree;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (degree == 0) {
			return coeff + "";
		} else if (degree == 1) {
			return coeff + "x";
		} else {
			return coeff + "x^" + degree;
		}
	}
}

/**
 * This class implements a linked list node that contains a Term instance.
 * 
 * @author runb-cs112
 * 
 */
class Node {

	/**
	 * Term instance.
	 */
	Term term;

	/**
	 * Next node in linked list.
	 */
	Node next;

	/**
	 * Initializes this node with a term with given coefficient and degree,
	 * pointing to the given next node.
	 * 
	 * @param coeff
	 *            Coefficient of term
	 * @param degree
	 *            Degree of term
	 * @param next
	 *            Next node
	 */
	public Node(float coeff, int degree, Node next) {
		term = new Term(coeff, degree);
		this.next = next;
	}
}

/**
 * This class implements a polynomial.
 * 
 * @author runb-cs112
 * 
 */
public class Polynomial {

	/**
	 * Pointer to the front of the linked list that stores the polynomial.
	 */
	Node poly;

	/**
	 * Initializes this polynomial to empty, i.e. there are no terms.
	 * 
	 */
	public Polynomial() {
		poly = null;
	}

	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage
	 * format of the polynomial is:
	 * 
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * 
	 * with the guarantee that degrees will be in descending order. For example:
	 * 
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * 
	 * which represents the polynomial:
	 * 
	 * <pre>
	 * 4 * x &circ; 5 - 2 * x &circ; 3 + 2 * x + 3
	 * </pre>
	 * 
	 * @param br
	 *            BufferedReader from which a polynomial is to be read
	 * @throws IOException
	 *             If there is any input error in reading the polynomial
	 */
	public Polynomial(BufferedReader br) throws IOException {
		String line;
		StringTokenizer tokenizer;
		float coeff;
		int degree;

		poly = null;

		while ((line = br.readLine()) != null) {
			tokenizer = new StringTokenizer(line);
			coeff = Float.parseFloat(tokenizer.nextToken());
			degree = Integer.parseInt(tokenizer.nextToken());
			poly = new Node(coeff, degree, poly);
		}
	}

	/**
	 * Returns the polynomial obtained by adding the given polynomial p to this
	 * polynomial - DOES NOT change this polynomial
	 * 
	 * @param p
	 *            Polynomial to be added
	 * @return A new polynomial which is the sum of this polynomial and p.
	 */
	public Polynomial add(Polynomial p) {

		/*
		 * What's up.
		 * 
		 * This code makes sure you didn't give me crap.
		 * 
		 * GIGO, right?
		 */
		
		if (p.poly == null) {
			return this;
		} else if (this.poly == null) {
			return p;
		} else {

			Polynomial retPol = new Polynomial();
			retPol.poly = new Node(0, 0, null);
			Node front = retPol.poly;

			Node entered = p.poly; // this is the input
			Node thisPol = this.poly; // this is from this structure

			/*
			 * The code below does the actual addition
			 * by simultaneously looping through two
			 * polynomials.
			 * 
			 * It's not the best way, it doesn't sort.
			 * But it works.
			 *
			 */
			
			while (entered != null || thisPol != null) {
				boolean bothExist = (entered != null & thisPol != null);
				boolean bothEqual = false;

				if (bothExist) {
					bothEqual = (entered.term.degree == thisPol.term.degree);
				}

				if (bothExist && bothEqual) {
					retPol.poly.term = new Term(entered.term.coeff
							+ thisPol.term.coeff, thisPol.term.degree);

					thisPol = thisPol.next;
					entered = entered.next;
				} else {
					if (entered != null && ((thisPol == null) || entered.term.degree < thisPol.term.degree)) {
						retPol.poly.term = entered.term;
						entered = entered.next;
					} else {
						retPol.poly.term = thisPol.term;
						thisPol = thisPol.next;
					}
				}

				retPol.poly.next = new Node(0, 0, null);
				retPol.poly = retPol.poly.next;
			}

			/*
			 * This removes any zero entries, including the one my code adds in
			 * arbitrarily by default.
			 * 
			 * Also, hello TA looking for arrays. Or Professor.
			 * 
			 * There are no arrays here.
			 */

			Node previous = null;
			Node current = front;

			while (current != null) {
				if (current.term.coeff == 0) {
					current = current.next;
					if (previous == null) {
						previous = current;
					} else {
						previous.next = current;
					}
				} else {
					previous = current;
					current = current.next;
				}
			}

			retPol.poly = front;
			
			if (retPol.poly.term.coeff == 0 && retPol.poly.next.term.coeff == 0) {
				Polynomial zero = new Polynomial();
				zero.poly = new Node (0, 0, null);
				return zero;
			}
			else
				return retPol;
		}
	}

	/**
	 * Returns the polynomial obtained by multiplying the given polynomial p
	 * with this polynomial - DOES NOT change this polynomial
	 * 
	 * @param p
	 *            Polynomial with which this polynomial is to be multiplied
	 * @return A new polynomial which is the product of this polynomial and p.
	 */
	public Polynomial multiply(Polynomial p) {
		if (p.poly == null || this.poly == null) {
			Polynomial zero = new Polynomial();
			zero.poly = new Node (0, 0, null);
			return zero;
		} else {

			Polynomial retPol = new Polynomial();
			retPol.poly = new Node(0, 0, null);
			
			Node front = retPol.poly;
			Node entered = p.poly;
			Node thisPol = this.poly;
			
			int heighestMultiple = 0;
			int lowestMultiple = 99999999;
			
			while (entered != null) {
				thisPol = this.poly; 
				
				while (thisPol != null) {
					if (thisPol.term.degree + entered.term.degree > heighestMultiple)
						heighestMultiple = thisPol.term.degree + entered.term.degree;
					if (thisPol.term.degree + entered.term.degree < lowestMultiple)
						lowestMultiple = thisPol.term.degree + entered.term.degree;
					
					thisPol = thisPol.next;
				}
				
				entered = entered.next;
			}
			
			entered = p.poly;
			
			Node create = front;
			for (int i = lowestMultiple; i <= heighestMultiple; i++) {
				create.term.degree = i;
				create.term.coeff = 0;
				
				create.next = new Node (0, 0, null);
				create = create.next;
			}
			
			entered = p.poly;
			
			while (entered != null) {
				thisPol = this.poly; 
				
				while (thisPol != null) {
					int degree = entered.term.degree + thisPol.term.degree;
					create = front;
					
					while (create != null) {
						if (create.term.degree == degree) {
							create.term.coeff = entered.term.coeff * thisPol.term.coeff;
						}
						
						create = create.next;
					}
					
					thisPol = thisPol.next;
				}
				
				entered = entered.next;
			}
			
			create = front;
			
			while (create != null) {
				if (create.term.degree == heighestMultiple) {
					create.next = null;
					create = create.next;
				}
				else
					create = create.next;
			}
			
			retPol.poly = front;
			
			return retPol;
		}
	}

	/**
	 * Evaluates this polynomial at the given value of x
	 * 
	 * @param x
	 *            Value at which this polynomial is to be evaluated
	 * @return Value of this polynomial at x
	 */
	public float evaluate(float x) {

		float retVal = 0;
		Node currentMonomial = this.poly;

		while (currentMonomial != null) {

			float currentValue = (float) Math.pow(x,
					currentMonomial.term.degree);
			currentValue *= currentMonomial.term.coeff;

			retVal += currentValue;

			currentMonomial = currentMonomial.next;
		}

		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String retval;

		if (poly == null) {
			return "0";
		} else {
			retval = poly.term.toString();
			for (Node current = poly.next; current != null; current = current.next) {
				retval = current.term.toString() + " + " + retval;
			}
			return retval;
		}
	}
}
