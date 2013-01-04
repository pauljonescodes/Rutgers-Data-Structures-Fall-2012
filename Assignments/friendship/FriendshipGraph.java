package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import structures.Friend;
import structures.Neighbor;
import structures.UndirGraph;

public class FriendshipGraph {

	public UndirGraph<Friend> rootFriendships;
	public String graphFile;

	public FriendshipGraph() {
		super();
	}

	public FriendshipGraph (String graphFile) throws FileNotFoundException {
		this.graphFile=graphFile;
		rootFriendships = new UndirGraph<Friend>();
		ArrayList<Friend> friends = new ArrayList<Friend>();
		Scanner scfile = null;

		try {
			scfile = new Scanner(new File(graphFile));
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException();
		}

		int numberOfVerteces = Integer.parseInt(scfile.nextLine());

		for (int i = 0; i < numberOfVerteces; i++) {
			String line = scfile.nextLine();
			String name = line.substring(0, line.indexOf('|'));

			int b = line.indexOf('|');
			int e = line.indexOf('|', b);

			String school = "";

			if (line.charAt(b+1) == 'y')
				school = line.substring(e + 3);

			rootFriendships.addVertex(new Friend(name, school));
			friends.add(new Friend(name, school));
		}

		while (scfile.hasNext()) {
			String line = scfile.next();

			String nameOne = line.substring(0, line.indexOf('|'));
			String nameTwo = line.substring(line.indexOf('|') + 1);

			int friendOne = 0;
			int friendTwo = 0;

			for (int i = 0; i < friends.size(); i++) {
				if (friends.get(i).name.equals(nameOne)) {
					friendOne = i;
				} else if (friends.get(i).name.equals(nameTwo)) {
					friendTwo = i;
				}
			}

			rootFriendships.addEdge(friendOne, new Neighbor(friendTwo));
		}

		scfile.close();
	}

	public FriendshipGraph(UndirGraph<Friend> graph) {
		this.rootFriendships = graph;
	}

	/* 
	 * You want to be able to focus exclusively on students in a particular school, 
	 * and all the friendships between them. 
	 * To do this, you will have to extract an appropriate subgraph out of the full graph.
	 */

	public FriendshipGraph getStudentsAtSchool(String school) {

		UndirGraph<Friend> schoolSubgraph = new UndirGraph<Friend>();
		int vCount = this.rootFriendships.numberOfVertices();

		for (int i = 0; i < vCount; i++) {
			if (this.rootFriendships.vertexInfoOf(i).school.compareTo(school) == 0) {
				schoolSubgraph.addVertex(this.rootFriendships.vertexInfoOf(i));
			}
		}

		vCount = schoolSubgraph.numberOfVertices();

		for (int i = 0; i < vCount; i++) {
			int firstRootRef = this.rootFriendships.vertexNumberOf(schoolSubgraph.vertexInfoOf(i));

			for (int j = 0; j < vCount; j++) {
				int secondRootRef = this.rootFriendships.vertexNumberOf(schoolSubgraph.vertexInfoOf(j));

				if (this.rootFriendships.containsEdge(firstRootRef, new Neighbor(secondRootRef)))
					schoolSubgraph.addEdge(i, new Neighbor(j));
			}
		}

		return new FriendshipGraph(schoolSubgraph);
	}

	/*
	 * Students tend to form cliques with their friends, which creates islands 
	 * that do not connect with each other. If these cliques could be identified, 
	 * particularly in the student population at a particular school, 
	 * introductions could be made between people in different cliques to build 
	 * larger networks of friendships at that school.
	 */

	public String getIntroductionChain(String firstPerson, String firstSchool, String secondPerson, String secondSchool) {
		Friend firstFriend = new Friend(firstPerson, firstSchool);
		Friend secondFriend = new Friend(secondPerson, secondSchool);
		String introductionChain = "";

		if (!this.rootFriendships.containsVertex(firstFriend) || !this.rootFriendships.containsVertex(secondFriend)) {
			introductionChain = "One or both of these people do not exist.";
		} else {
			ArrayList<Friend> path = this.rootFriendships.getShortestPath(
					this.rootFriendships.vertexNumberOf(firstFriend), 
					this.rootFriendships.vertexNumberOf(secondFriend));

			if (path != null) {
				if (path.size() == 0) {
					introductionChain += "These people cannot meet right now.";
				} else for (int i = 0; i < path.size(); i++) {
					introductionChain += path.get(i).name + (i < path.size() - 1 ? "<-->": "");
				}	
			}
		}

		return introductionChain;
	}

	public String getCliques(String school) {

		UndirGraph<Friend> schoolRestricted = this.getStudentsAtSchool(school).rootFriendships;
		ArrayList<ArrayList<Friend>> cliques = schoolRestricted.getCliques();
		String retstr = "";

		for (int i = 0; i < cliques.size(); i++) {
			retstr += "clique " + (i + 1) + ": \n\n" + cliques.get(i).size() + "\n";
			for (int j = 0; j < cliques.get(i).size(); j++) {
				retstr += cliques.get(i).get(j).toString() + "\n";
			}

			HashSet<String> s = new HashSet<String>();

			for (int j = 0; j < cliques.get(i).size(); j++) {
				for (int k = 0; k < cliques.get(i).size(); k++) {

					if (this.rootFriendships.containsEdge(this.rootFriendships.vertexNumberOf(cliques.get(i).get(j)), new Neighbor(this.rootFriendships.vertexNumberOf(cliques.get(i).get(k))))) {

						String nameOne = cliques.get(i).get(j).name;
						String nameTwo = cliques.get(i).get(k).name;

						String nameCom = "";

						if (nameOne.compareTo(nameTwo) < 0) {
							nameCom = nameOne + "|" + nameTwo;
						} else if (nameOne.compareTo(nameTwo) > 0) {
							nameCom = nameTwo + "|" + nameOne;
						} 

						s.add(nameCom);
					}
				}
			}

			for (String adj : s) {
				retstr += adj + "\n";
			}
		}




		return retstr;
	}

	public String getConnectors() {
		ArrayList<String> r= new ArrayList<String>();

		for(int i=0; i < rootFriendships.adjlists.size(); i++){

			String result= rootFriendships.findConnectors(i);
			if (result != "None") r.add(result);
		}

		String result= "";
		for (int l=0; l< r.size(); l++){
			result= result+ r.get(l) + "\n";
		}

		return result;

	}

	public String toString() {
		String retStr = "" + this.rootFriendships.numberOfVertices() + "\n";

		for (int i = 0; i < this.rootFriendships.numberOfVertices(); i++) {
			retStr += this.rootFriendships.vertexInfoOf(i).toString() + "\n";
		}

		HashSet<String> s = new HashSet<String>();

		for (int i = 0; i < this.rootFriendships.numberOfVertices(); i++) {
			for (int j = 0; j < this.rootFriendships.numberOfVertices(); j++) {
				if (this.rootFriendships.containsEdge(i, new Neighbor(j))) {
					String nameOne = this.rootFriendships.vertexInfoOf(i).name;
					String nameTwo = this.rootFriendships.vertexInfoOf(j).name;
					String nameCom = "";

					if (nameOne.compareTo(nameTwo) < 0) {
						nameCom = nameOne + "|" + nameTwo;
					} else {
						nameCom = nameTwo + "|" + nameOne;
					}

					s.add(nameCom);
				}
			}
		}

		for (String adj : s) {
			retStr += adj + "\n";
		}

		return retStr;
	}
}
