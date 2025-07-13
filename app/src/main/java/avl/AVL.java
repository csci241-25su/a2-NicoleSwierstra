package avl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * Nicole Swierstra
 * AVL.java
 * 
 * Added enhancements
 * 1. added avl remove
 * 2. changed AVL to implement map functions - I might have taken this too literally
 * 3. removed old opmized remove and added Map remove with correct semantics - removes one instance and then if it's 0 removes it from the tree
 */
public class AVL implements Map<String, Integer> {

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

	/* recalculates the height of a single node using the values below it in O(n) time */
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
			default: n.num++; /* no insertion, just addition */
		}
		recalcHeight(n);
		rebalance(n);
	}

	/* is this the most efficient system for removing a node? probably not.
	 * does it have the least amount of pointer math? yes!
	 * 
	 * recursively rotates node n down the tree until it only has one child, then removes it.
	 */
	public void rotateToBottomAndRemove(Node n){
		Node child;
		switch((n.left != null ? 1 : 0) + (n.right != null ? 2 : 0)){
		case 0:
			if(n.parent.left == n)  n.parent.left  = null;
			else n.parent.right = null;
			return; /* if no children */
		case 1: /* if left but no right */
			child = n.left;
			if(n.parent.left == n){
				n.parent.left = child;
			}
			else {
				rightRotate(n);
				rotateToBottomAndRemove(n);
			}
			break;
		case 2: /* if right but no left */
			child = n.right;
			if(n.parent.right == n){
				n.parent.right = child;
			}
			else {
				leftRotate(n);
				rotateToBottomAndRemove(n);
			}
			break;
		case 3: /* if both */
			int b = balance(n);
			if (b < 0){
				rightRotate(n);
				rotateToBottomAndRemove(n);
			}
			else {
				leftRotate(n);
				rotateToBottomAndRemove(n);
			}	
		}
		recalcHeight(n.parent);
		rebalance(n.parent);
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

	/* gets the AVL balance of a node */
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

	/* returns size of the tree */
    public int size() {
        return getSize();
    }

	/* returns if the AVL is empty */
    public boolean isEmpty() {
        return root == null;
    }

	/* returns true if the key is in the tree */
    public boolean containsKey(Object key) {
        if(key.getClass() != String.class) return false;
		
		return search(root, (String)key) != null;
    }

	/* NOT IMPLEMENTED - Doesn't make any sense for this class */
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Doesn't make any sense for this type.");
    }

	/* returns the instances of the line */
    public Integer get(Object key) {
		if(key.getClass() != String.class) return null;
        Node n = search((String)key);
		if (n == null) return 0;
		else return n.num;
    }

	/* puts a value in the tree */
    public Integer put(String key, Integer value) {
        if(key.getClass() != String.class) return null;
		avlInsert(key);
        Node n = search((String)key);
		n.num = value;
		return n.num;
    }

	/* 
	 * Either decrements or removes completely a key - correct for the semantics of the AVLtree
	 * returns 0 if it doesn't exist
	 */
    public Integer remove(Object key) {
		if(key.getClass() != String.class) return null;
        
		Node n = search((String)key);
		if(n == null) return null;

		n.num--;
		if(n.num == 0) {
			rotateToBottomAndRemove(n);
			n = n.parent;
			while(n.parent != null){
				rebalance(n);
				recalcHeight(n);
				n = n.parent;
			}
			return 0;
		}
		else{
			return n.num;
		}
    }

	/*
	 * Allows you to combine AVL trees
	 */
    public void putAll(Map<? extends String, ? extends Integer> m) {
        for(String s : m.keySet()){
			put(s, m.get(s));
		}
    }

	/* clears the avl tree */
    public void clear() {
        root = null;
    }

	/* Gets set of all keys */
	private void inOrder(Node n, Set<String> set){
		if (n == null) return;
		inOrder(n.left, set);
		set.add(n.word);
		inOrder(n.right, set);
	}

	/* returns set of all keys */
    public Set<String> keySet() {
        Set<String> s = new HashSet<String>();
		inOrder(root, s);
		return s;
    }

	/* this doens't make any sense for this type */
    public Collection<Integer> values() {
        throw new UnsupportedOperationException("This doesn't make any sense for the type.");
    }

	/* what is this??? */
    public Set<Entry<String, Integer>> entrySet() {
        throw new UnsupportedOperationException("I have no idea what this is.");
    }

	/* recursively finds the most common node */
	private Node mostCommon(Node n){
		Node mc = n;
		if(n.left != null){
			Node l = mostCommon(n.left);
			if (l.num > mc.num) mc = l;
		}
		if(n.right != null){
			Node r = mostCommon(n.right);
			if (r.num > mc.num) mc = r;
		}
		return mc;
	}

	/* returns most common node in the AVL tree */
	public Node mostCommon(){
		if(root == null) return null;
		return mostCommon(root);
	}

	/** inner class representing a node in the tree. */
	public class Node {
		public String word;
		public Node parent;
		public Node left;
		public Node right;
		public int height;
		public int num; /* this is the MAP part */

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
