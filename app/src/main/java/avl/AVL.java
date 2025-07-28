package avl;

public class AVL {

	public Node root;

	private int size;

	public int getSize() {
		return size;
	}

	/** find w in the tree. return the node containing w or
	 * null if not found */
	public Node search(String w) {
		return search(root, w);
	}
	private Node search(Node n, String w) {
		if (n == null) {
			return null;
		}
		if (w.equals(n.word)) {
			return n;
		} else if (w.compareTo(n.word) < 0) {
			return search(n.left, w);
		} else {
			return search(n.right, w);
		}
	}

	/** insert w into the tree as a standard BST, ignoring balance */
	public void bstInsert(String w) {
		if (root == null) {
			root = new Node(w);
			size = 1;
			return;
		}
		bstInsert(root, w);
	}

	/* 1 if 1 is greater, -1 if 2 is greater, 0 if they're the same */
	private int strcmp(String s1, String s2){
		int s = s1.compareTo(s2);
		return (s != 0) ? (s / Math.abs(s)) : 0;
	}

	/* insert w into the tree rooted at n, ignoring balance
	* pre: n is not null */
	private void bstInsert(Node n, String w) {
		switch(strcmp(w, n.word)){
			case -1: /* w < node */
				if(n.left != null) bstInsert(n.left, w);
				else {
					n.left = new Node(w, n);
					size++;
				}
				break;
			case 1: /* w > node */
				if(n.right != null) bstInsert(n.right, w);
				else {
					n.right = new Node(w, n);
					size++;
				}
				break;
			default: break; /* no insertion, already exists */
		}
	}

	/** insert w into the tree, maintaining AVL balance
	 *  precondition: the tree is AVL balanced and any prior insertions have been
	*  performed by this method. */
	public void avlInsert(String w) {
		if (root == null) {
			root = new Node(w);
			size = 1;
			return;
		}
		avlInsert(root, w);	
	}

	private void recalcHeight(Node n){
		int hright = -1, hleft = -1;
		
		if(n.left != null) hleft = n.left.height;
		if(n.right != null) hright = n.right.height;
		
		n.height = Math.max(hright, hleft) + 1;
	}

	/* insert w into the tree, maintaining AVL balance
	*  precondition: the tree is AVL balanced and n is not null 
	*  returns true if 
	*/
	private void avlInsert(Node n, String w) {
		switch(strcmp(w, n.word)){
			case -1: /* w < node */
				if(n.left != null) {
					avlInsert(n.left, w);
				}
				else {
					n.left = new Node(w, n);
					size++;
				}
				break;
			case 1: /* w > node */
				if(n.right != null) {
					avlInsert(n.right, w);
				}
				else {
					n.right = new Node(w, n);
					size++;
				}
				break;
			default: break; /* no insertion, already exists */
		}
		recalcHeight(n);
		rebalance(n);
	}


	/** do a left rotation: rotate on the edge from x to its right child.
	 *  precondition: x has a non-null right child */
	public void leftRotate(Node x) {
		Node newrt = x.right;
		newrt.parent = x.parent;
		x.right = newrt.left;
		if(newrt.left != null) newrt.left.parent = x;
		newrt.left = x;
		x.parent = newrt;

		/* adjust parent */
		if(newrt.parent == null) root = newrt;
		else{
			if(newrt.parent.left == x)
				newrt.parent.left = newrt;
			else
				newrt.parent.right = newrt;
		}
	}

	/** do a right rotation: rotate on the edge from x to its left child.
	 *  precondition: y has a non-null left child */
	public void rightRotate(Node y) {
		Node newrt = y.left;
		newrt.parent = y.parent;
		y.left = newrt.right;
		if(newrt.right != null) newrt.right.parent = y;
		newrt.right = y;
		y.parent = newrt;

		/* adjust parent */
		if(newrt.parent == null) root = newrt;
		else{
			if(newrt.parent.left == y)
				newrt.parent.left = newrt;
			else
				newrt.parent.right = newrt;
		}
	}

	private int balance(Node n){
		int hright = -1, hleft = -1;
		
		if(n.left != null) hleft = n.left.height;
		if(n.right != null) hright = n.right.height;
		
		return hright - hleft;
	}

	/** rebalance a node N after a potentially AVL-violoting insertion.
	 *  precondition: none of n's descendants violates the AVL property */
	public void rebalance(Node n) {
		int balance = balance(n);
		if(balance > 1){
			if((balance(n.right) * balance) < 0) rightRotate(n.right);
			leftRotate(n);
			n = n.parent; /* be in the same part of the tree */
		}
		else if (balance < -1) {
			if((balance(n.left) * balance) < 0) leftRotate(n.left);
			rightRotate(n);
			n = n.parent;
		}

		if(n.parent != null) rebalance(n.parent);
	}

	/** remove the word w from the tree */
	public void remove(String w) {
		remove(root, w);
	}

	/* remove w from the tree rooted at n */
	private void remove(Node n, String w) {
		return; // (enhancement TODO - do the base assignment first)
	}

	/** print a sideways representation of the tree - root at left,
	 * right is up, left is down. */
	public void printTree() {
		printSubtree(root, 0);
	}
	private void printSubtree(Node n, int level) {
		if (n == null) {
		return;
		}
		printSubtree(n.right, level + 1);
		for (int i = 0; i < level; i++) {
		System.out.print("        ");
		}
		System.out.println(n);
		printSubtree(n.left, level + 1);
	}

	/** inner class representing a node in the tree. */
	public class Node {
		public String word;
		public Node parent;
		public Node left;
		public Node right;
		public int height;

		public String toString() {
			return word + "(" + height + ")";
		}

		/** constructor: gives default values to all fields */
		public Node() { }

		/** constructor: sets only word */
		public Node(String w) {
			word = w;
		}

		/** constructor: sets word and parent fields */
		public Node(String w, Node p) {
			word = w;
			parent = p;
		}

		/** constructor: sets all fields */
		public Node(String w, Node p, Node l, Node r) {
			word = w;
			parent = p;
			left = l;
			right = r;
		}
	}
}
