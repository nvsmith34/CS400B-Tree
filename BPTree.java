import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Implementation of a B+ tree to allow efficient access to
 * many different indexes of a large data set. 
 * BPTree objects are created for each type of index
 * needed by the program.  BPTrees provide an efficient
 * range search as compared to other types of data structures
 * due to the ability to perform log_m N lookups and
 * linear in-order traversals of the data items.
 * 
 * @author sapan (sapan@cs.wisc.edu)
 *
 * @param <K> key - expect a string that is the type of id for each item
 * @param <V> value - expect a user-defined type that stores all data for a food item
 */
public class BPTree<K extends Comparable<K>, V> implements BPTreeADT<K, V> {

    // Root of the tree
    private Node root;
    
    // Branching factor is the number of children nodes 
    // for internal nodes of the tree
    private int branchingFactor;
    
    //number of leaves in tree
    private int size;
    
    
    /**
     * Public constructor
     * 
     * @param branchingFactor 
     */
    public BPTree(int branchingFactor) {
        
        this.branchingFactor = branchingFactor;
        root = new LeafNode();
        size=0;
    }
    
    
    /*
     * (non-Javadoc)
     * @see BPTreeADT#insert(java.lang.Object, java.lang.Object)
     */
    @Override
    public void insert(K key, V value) {

    	if(key==null)throw new IllegalArgumentException();
        root.insert(key,value);
        size++;

    }
    
    
    /*
     * (non-Javadoc)
     * @see BPTreeADT#rangeSearch(java.lang.Object, java.lang.String)
     */
    @Override
    public List<V> rangeSearch(K key, String comparator) {
        if (!comparator.equals(">=") && !comparator.equals("==") && !comparator.equals("<=") )
            return new ArrayList<V>();
        if(key ==null)
        	return new ArrayList<V>();
        return root.rangeSearch(key, comparator);
    }
    
    /*
     * (non-Javadoc)
     * @see BPTreeADT#get(java.lang.Object)
     */
     @Override
     public V get(K key) {
       if(key==null)
    	   return null;
       if(root.rangeSearch(key, "==").size()==0) //return null if key not in tree
        	return null;								

        return root.rangeSearch(key, "==").get(0);//call search method to find the node
     }

    /*
     * (non-Javadoc)
     * @see BPTreeADT#size()
     */
     @Override
     public int size() {

        return size;
     }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        Queue<List<Node>> queue = new LinkedList<List<Node>>();
        queue.add(Arrays.asList(root));
        StringBuilder sb = new StringBuilder();
        while (!queue.isEmpty()) {
            Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
            while (!queue.isEmpty()) {
                List<Node> nodes = queue.remove();
                sb.append('{');
                Iterator<Node> it = nodes.iterator();
                while (it.hasNext()) {
                    Node node = it.next();
                    sb.append(node.toString());
                    if (it.hasNext())
                        sb.append(", ");
                    if (node instanceof BPTree.InternalNode)
                        nextQueue.add(((InternalNode) node).children);
                }
                sb.append('}');
                if (!queue.isEmpty())
                    sb.append(", ");
                else {
                    sb.append('\n');
                }
            }
            queue = nextQueue;
        }
        return sb.toString();
    }
    
    
    /**
     * This abstract class represents any type of node in the tree
     * This class is a super class of the LeafNode and InternalNode types.
     * 
     * @author sapan
     */
    private abstract class Node {
        
        // List of keys
        List<K> keys;
        
        /**
         * Package constructor
         */
        Node() {
            keys = new ArrayList<K>();
        }
        
        /**
         * Inserts key and value in the appropriate leaf node 
         * and balances the tree if required by splitting
         *  
         * @param key
         * @param value
         */
        abstract void insert(K key, V value);

        /**
         * Gets the first leaf key of the tree
         * 
         * @return key
         */
        abstract K getFirstLeafKey();
        
        /**
         * Gets the new sibling created after splitting the node
         * 
         * @return Node
         */
        abstract Node split();
        
        /*
         * (non-Javadoc)
         * @see BPTree#rangeSearch(java.lang.Object, java.lang.String)
         */
        abstract List<V> rangeSearch(K key, String comparator);

        /**
         * 
         * @return boolean
         */
        abstract boolean isOverflow();
        
        public String toString() {
            return keys.toString();
        }
    
        /**
         * if root overflows then rebalance
         */
        void rootOverflow() { 
        	Node sib = split();//call to split and make new node
        	InternalNode node = new InternalNode();
        	node.keys.add(sib.getFirstLeafKey());//add first leaf of sibling node to keys
          	node.children.add(this);//add this node to children of new node
        	node.children.add(sib);//add sibling to children of new node
        	
  
        	root = node;
        }
    } // End of abstract class Node
    
    /**
     * This class represents an internal node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations
     * required for internal (non-leaf) nodes.
     * 
     * @author sapan
     */
    private class InternalNode extends Node {

        // List of children nodes
        List<Node> children;
        
        /**
         * Package constructor
         */
        InternalNode() {
            super();
            children  = new ArrayList<Node>();
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            return children.get(0).getFirstLeafKey(); //get first leaf key of first child of this node
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {

            return children.size()>branchingFactor;//overflow if children goes greater than factor
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(java.lang.Comparable, java.lang.Object)
         */
        void insert(K key, V value) {
            Node child;
        	int index = Collections.binarySearch(keys, key);//search index of the key to insert
        	if(index>=0) child = children.get(index+1);//if its in the tree already, insert at the next index
        	else child =  children.get(-index -1);//if its not in the tree, negate index to enter where it should be
       
            child.insert(key,value);//call to insert this key from child node position
            if(child.isOverflow()) { //if this node overflows then split
            	Node sib = child.split();

            	int index2 = Collections.binarySearch(keys,sib.getFirstLeafKey());//search for index of first leaf key of new node
            	
            	if(index2>=0) children.set(index2+1, sib);//if its there already, set this node to child index after this
            	else {//otherwise if not there add first leafkey of new node to keys and add this node to children
            		index2 = -index2 - 1;
            		keys.add(index2,sib.getFirstLeafKey());

            		children.add(index2+1,sib);
            	}
            }
            if(root.isOverflow()) {
            	rootOverflow();
            }
            
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() {
        	
        	InternalNode sib = new InternalNode();
            int begin = keys.size()/2 +1; //add keys to new node from midway to end
            int end = keys.size();

            return addKeys(begin,end,sib);
            
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(java.lang.Comparable, java.lang.String)
         */
        List<V> rangeSearch(K key, String comparator) {
            if(comparator.equals("<="))//if its less than or equal to, call rangesearch on first child
            	return children.get(0).rangeSearch(key,comparator);
            else {//otherwise call rangesearch from this node so can search till end
            	int index = Collections.binarySearch(keys, key);//find the index of this key
            	if(index>=0) return children.get(index+1).rangeSearch(key,comparator);
            	else return children.get(-index -1).rangeSearch(key,comparator);
            }
     
            	
        }
        
   
        
        InternalNode addKeys(int begin, int end, InternalNode sib) {
        	sib.keys.addAll(keys.subList(begin, end));//add keys of previous node to new one

            
            sib.children.addAll(children.subList(begin, end+1));//add children as well
 
            //close sublists
            keys.subList(begin-1, end).clear();
            children.subList(begin, end+1).clear();
            return sib;
        }
        

    
    } // End of class InternalNode
    
    
    /**
     * This class represents a leaf node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations that
     * required for leaf nodes.
     * 
     * @author sapan
     */
    private class LeafNode extends Node {
        
        // List of values
        List<ArrayList<V>> values;
        
        // Reference to the next leaf node
        LeafNode next;
        
        // Reference to the previous leaf node
        LeafNode previous;
        
        /**
         * Package constructor
         */
        LeafNode() {
            keys = new ArrayList<K>();
            values = new ArrayList<ArrayList<V>>();
        }
        
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            if(keys.size()==0)return null;
           
            return keys.get(0);
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
           
            return values.size()>branchingFactor;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(Comparable, Object)
         */
        void insert(K key, V value) {
        	System.out.print(value.toString());
            int index = Collections.binarySearch(keys,key);
            if (index>=0) {
            	values.get(index).add(value);//if key is already there, then add value to array of same values
            	//keys.add(index,key);
            }
            else {
            	index = -index - 1;
            	keys.add(index,key);//if key is not already there then add
            	//System.out.print(keys.get(0) +" ");
            	ArrayList<V> duplicate = new ArrayList<V>();//create an array of duplicate values to add to
            	duplicate.add(value);
            	values.add(index,duplicate);//add duplicate array at this index
            }
            
            if(root.isOverflow()) {
            	super.rootOverflow();
            }
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() {
            LeafNode sib = new LeafNode(); //make new leafnode to add to
            int mid = (keys.size()+1)/2;
            sib = addKeysandValues(mid,keys.size(),sib);//add old values to new leafnode
            
            sib.next = next;//insert new node before current node
            next = sib;
            return sib;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(Comparable, String)
         */
        List<V> rangeSearch(K key, String comparator) {//implement a list that occurs at a spot with multiple keys, then just traverse that list
            List<V> range = new ArrayList<V>();

            LeafNode currNode = this; //start traversal at this node
            while(currNode!=null) { //go till node is null
            	for(int i = 0; i<currNode.keys.size();i++) {//loop through this nodes keys
            		//System.out.print(currNode.keys.get(i).toString());
            		if(this.compareTo(key,currNode.keys.get(i),comparator)) {//if node is in range then loop through values to add to
            			//System.out.print("x");
            				for(int z=0;z<currNode.values.get(i).size();z++) {//add same values to array
            					range.add(currNode.values.get(i).get(z));
            				}
            				
            		}

            	}
            	currNode = currNode.next;//loop to next node
            }
            return range;
            
        }
        
        /**
         * comparto method to parse the string argument
         * @param key1
         * @param key2
         * @param comparator
         * @return
         */
        boolean compareTo(K key1, K key2, String comparator){
        	if(comparator.equals("=="))return key1.compareTo(key2)==0;
        	else if(comparator.equals("<="))return key1.compareTo(key2)>=0;
        	else return key1.compareTo(key2)<=0;
        	
        }
        
        /**
         * add values to new sibling nodes
         * @param mid
         * @param end
         * @param sib
         * @return
         */
        LeafNode addKeysandValues(int mid,int end,LeafNode sib) {
        	 List<K> subKeys = keys.subList(mid, end);
             List<ArrayList<V>> subValues = values.subList(mid, end);
             sib.keys.addAll(subKeys);//add keys to new node from midway to end of old keys
             sib.values.addAll(subValues);//same with values
             
             keys.subList(mid, keys.size()).clear();
             values.subList(mid, keys.size()).clear();
             return sib;
        }
    } // End of class LeafNode
    
    
   
    /**
     * Contains a basic test scenario for a BPTree instance.
     * It shows a simple example of the use of this class
     * and its related types.
     * 
     * @param args
     */
    public static void main(String[] args) {
        // create empty BPTree with branching factor of 3
        BPTree<Double, Double> bpTree = new BPTree<>(3);

        // create a pseudo random number generator
        Random rnd1 = new Random();

        // some value to add to the BPTree
        Double[] dd = {0.0d, 0.5d, 0.2d, 0.8d};

        // build an ArrayList of those value and add to BPTree also
        // allows for comparing the contents of the ArrayList 
        // against the contents and functionality of the BPTree
        // does not ensure BPTree is implemented correctly
        // just that it functions as a data structure with
        // insert, rangeSearch, and toString() working.
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < 400; i++) {
            Double j = dd[rnd1.nextInt(4)];
            list.add(j);
            bpTree.insert(j, j);
            System.out.println("\n\nTree structure:\n" + bpTree.toString());
        }
        List<Double> filteredValues = bpTree.rangeSearch(0.2d, ">=");
        System.out.println("Filtered values: " + filteredValues.toString());
    }

} // End of class BPTree
