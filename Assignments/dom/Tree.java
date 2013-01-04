package dom;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {

	/**
	 * Root node
	 */
	TagNode root=null;

	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;

	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}

	/**
	 * Builds the DOM tree from input HTML file
	 */
	public void build() {
		if (sc == null)
			return;

		Stack<TagNode> tags = new Stack<TagNode>();
		String currentLine = sc.nextLine();
		root = new TagNode("html", null, null);
		tags.push(root);

		while (sc.hasNextLine()) {
			currentLine = sc.nextLine();

			if (currentLine.contains("<") && currentLine.contains(">") && currentLine.contains("/")) {
				tags.pop();
			} else if (currentLine.contains("<") && currentLine.contains(">") && !currentLine.contains("/")) {
				if (tags.peek().firstChild == null) {
					TagNode builder = new TagNode(currentLine.replace("<", "").replace(">", ""), null, null);
					tags.peek().firstChild = builder;
					tags.push(builder);
				} else { // must traverse to "right-most" sibling
					TagNode currentTag = tags.peek().firstChild;

					while (currentTag.sibling != null) // get rightmost sibling
						currentTag = currentTag.sibling;

					TagNode builder = new TagNode(currentLine.replace("<", "").replace(">", ""), null, null);
					currentTag.sibling = builder;
					tags.push(builder);
				}
			} else {
				if (tags.peek().firstChild == null) {
					tags.peek().firstChild = new TagNode(currentLine, null, null);
				} else {
					TagNode currentTag = tags.peek().firstChild;

					while (currentTag.sibling != null)
						currentTag = currentTag.sibling;

					currentTag.sibling = new TagNode(currentLine, null, null);
				}
			}
		}
	}

	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		if (root == null || oldTag == null || newTag == null)
			return;
		else 
			this.replaceTagHelper(oldTag, newTag, root.firstChild);
	}

	private void replaceTagHelper(String oldTag, String newTag, TagNode traverseNode) {
		if (traverseNode == null)
			return;
		else if (traverseNode.tag.compareTo(oldTag) == 0)
			traverseNode.tag = newTag;

		this.replaceTagHelper(oldTag, newTag, traverseNode.firstChild);
		this.replaceTagHelper(oldTag, newTag, traverseNode.sibling);
	}

	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		if(row <= 0)
			return;
		else
			this.boldRowHelper(row,0,root,root.firstChild);
	}

	private void boldRowHelper(int row, int count,TagNode previousNode, TagNode traverseNode) {
		if(traverseNode == null) {
			return;
		} else if(traverseNode.tag.equals("tr")) {
			count++;
		} if(count == row && traverseNode.firstChild == null) {
			previousNode.firstChild = new TagNode("b", traverseNode, null);
		} 

		boldRowHelper(row, count, traverseNode, traverseNode.firstChild); 
		boldRowHelper(row, count, traverseNode, traverseNode.sibling);
	}

	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		if (root == null)
			return;
		else 
			while (this.containsTag(tag, root))
				removeTagHelper(tag, root, root.firstChild);
	}

	private void removeTagHelper(String tag, TagNode previousNode, TagNode traverseNode) {
		if (traverseNode == null || previousNode == null){
			return;
		} else if (traverseNode.tag.equals(tag)){

			if (tag.equals("ul") || tag.equals("ol"))
				this.removeListTagHelper(traverseNode.firstChild); 

			if (previousNode.firstChild == traverseNode) {
				previousNode.firstChild = traverseNode.firstChild;
				this.addLastSibling(traverseNode.firstChild, traverseNode.sibling);
			} else if (previousNode.sibling == traverseNode) {
				this.addLastSibling(traverseNode.firstChild, traverseNode.sibling);
				previousNode.sibling = traverseNode.firstChild;
			}

			return;
		}

		previousNode = traverseNode;
		removeTagHelper(tag, previousNode, traverseNode.firstChild);
		removeTagHelper(tag, previousNode, traverseNode.sibling);
	}

	private void removeListTagHelper(TagNode traverseNode) {
		if (traverseNode == null)
			return;
		else if (traverseNode.tag.compareTo("li") == 0)
			traverseNode.tag = "p";

		this.removeListTagHelper(traverseNode.sibling);
	}

	private TagNode getLastSibling (TagNode traverseNode) {
		while (traverseNode.sibling != null)
			traverseNode = traverseNode.sibling;

		return traverseNode;
	}

	private void addLastSibling (TagNode traverseNode, TagNode newSibling) {
		traverseNode = this.getLastSibling(traverseNode);
		traverseNode.sibling = newSibling;
	}

	private boolean containsTag(String tag, TagNode traverseNode) {
		if (traverseNode == null)
			return false;
		else if (traverseNode.tag.compareTo(tag) == 0)
			return true;

		return this.containsTag(tag, traverseNode.firstChild) || this.containsTag(tag, traverseNode.sibling);
	}

	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		if (root == null || word == null || tag == null) {
			return;
		} else {
			this.addTagHelper(word, tag, root.firstChild);
		}
	}

	private void addTagHelper(String word, String tag, TagNode traverseNode) {
		if (traverseNode == null) {
			return;
		} else if (traverseNode.tag.toLowerCase().contains(word.toLowerCase())) {			
			if (traverseNode.tag.equalsIgnoreCase(word)) {
				traverseNode.tag = tag;
				traverseNode.firstChild = new TagNode (word, traverseNode.firstChild, null);
			} else if (traverseNode.tag.toLowerCase().contains(word.toLowerCase())) {
				TagNode sibling = traverseNode.sibling;

				String befr = traverseNode.tag.substring(0, traverseNode.tag.toLowerCase().indexOf(word.toLowerCase()));
				String aftr = traverseNode.tag.substring(traverseNode.tag.toLowerCase().indexOf(word.toLowerCase()) + word.length());
				String punc = "";
				String orig = traverseNode.tag.substring(traverseNode.tag.toLowerCase().indexOf(word.toLowerCase()), traverseNode.tag.toLowerCase().indexOf(word.toLowerCase()) + word.length());

				if (aftr.length() > 0) {
					if (aftr.length() > 1 && (this.isPunctuation(aftr.charAt(0)) && !this.isPunctuation(aftr.charAt(1)))) {
						punc = "" + aftr.charAt(0);
						aftr = aftr.substring(1);
					}
				}
				
				if (aftr.length() == 0 || (aftr.length() >= 1 && (aftr.charAt(0) == ' ' || this.isPunctuation(aftr.charAt(0))))){

					if (aftr.equals("!") || aftr.equals(",") || aftr.equals(".") || aftr.equals("?")) {
						orig = orig + aftr;
						aftr = "";
					}
					
					traverseNode.tag = befr;
					traverseNode.sibling = new TagNode(tag, new TagNode(orig + punc, null, null), null);


					if (aftr.length() > 0) {
						if (sibling != null)
							traverseNode.sibling.sibling = new TagNode(aftr, null, sibling);
						else
							traverseNode.sibling.sibling = new TagNode(aftr, null, null);
					} else if (sibling != null) {
						traverseNode.sibling.sibling = sibling;
					} 
				} 
			}

			this.addTagHelper(word, tag, traverseNode.sibling.sibling);

		} else {
			this.addTagHelper(word, tag, traverseNode.firstChild);
			this.addTagHelper(word, tag, traverseNode.sibling);
		}
	}

	private boolean isPunctuation(char c) {
		return (c == ',' || c == '.' || c == '!' || c == '?');
	}

	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);

		return sb.toString();
	}

	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}

}
