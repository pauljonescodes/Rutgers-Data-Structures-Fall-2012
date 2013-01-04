package src;

/*
 * Paul Jones
 * Megan Murray
 *  
 */

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Friends {

	static Scanner stdin = new Scanner(System.in);

	public static void main (String[] args) {
		System.out.print("Enter file name: ");

		String graphFile = "";

		graphFile = stdin.next();

		FriendshipGraph fg = null;
		
		try {
			fg = new FriendshipGraph(graphFile);
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(0);
		}
		
		int option;
		while ((option = getOption()) != '6') {
			if (option == '1') {
				System.out.println("Enter school name: ");
				String school = stdin.next();
				System.out.println(fg.getStudentsAtSchool(school));
			} else if (option == '2') {
				System.out.println("Enter the first person's name: ");
				String firstName = stdin.next();
				System.out.println("Enter the first person's school (or an asteriks ('*') if there is no school): ");
				String firstSchool = stdin.next();
				System.out.println("Enter the second person's name: ");
				String secondName = stdin.next();
				System.out.println("Enter the second person's school (or an asteriks ('*') if there is no school): ");
				String secondSchool = stdin.next();
				if (firstSchool.equals("*"))
					firstSchool = "";
				if (secondSchool.equals("*"))
					secondSchool = "";
				System.out.println(fg.getIntroductionChain(firstName, firstSchool, secondName, secondSchool));
			} else if (option == '3') {
				System.out.println("Enter the school of the cliques you want to see: ");
				String school = stdin.next();
				System.out.print(fg.getCliques(school));
			} else if (option == '4') {
				System.out.println(fg.getConnectors());
			} else if (option == '5') {
				System.out.println(fg.toString());
			}
		}
	}

	static int getOption() {
		System.out.println(" ");
		System.out.println("Choose action: ");
		System.out.println(" ");
		System.out.println("(1) Subgraph: Students at a school");
		System.out.println("(2) Shortest Path: Intro Chain");
		System.out.println("(3) Connected Islands: Cliques");
		System.out.println("(4) Connectors: Friends Who Keep Friends Together ");
		System.out.println("(5) Print graph (adjacency list form)");
		System.out.println(" ");
		System.out.println("(6) Quit");
		int response = stdin.next().charAt(0);
		while (response != '1' && response != '2' && response != '3' && response != '4' && response != '5' && response != '6') {
			System.out.print("\tYou must enter a valid selection: 1, 2, 3, 4, 5, or 6. => ");
			response = stdin.next().charAt(0);
		}
		return response;
	}

}
