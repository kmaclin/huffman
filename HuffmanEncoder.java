import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;


public class HuffmanEncoder {
	
	public class FrequencyComparator<T> implements Comparator<T> {
	    
		public int compare(T obj1, T obj2) {
	    	int i = ((Node) obj1).getFrequency() - ((Node) obj2).getFrequency();
	    	return i;
	    }
	}
	
	private class Node implements Comparable {
			private char data;
			private String binaryNum;
			private int frequency;
			private TreeSet<Node> set = new TreeSet();
			private Node left = null;
			private Node right = null;
			private Node parent = null;
			private String code = "";
			
			public Node(int num) {
				frequency = num;
			}
			
			public void addToCode(String ch) {
				code = code + ch;
			}
			
			public TreeSet getSet() {
				return set;
			}
			
			public int compareTo(Object o) {
				Node node = (Node) o;
				return frequency;
			}
			
			public boolean equals(Object o) {
				Node in = (Node) o;
				return data == in.getChar();
			}
			
			public void setParent(Node node) {
				parent = node;
			}
			
			public Node getParent() {
				return parent;
			}
			
			public void setChar(char ch) {
				data = ch;
			}
			
			public char getChar() {
				return data;
			}
			
			public void addToSet(Node node) {
				set.add(node);
			}
			
			public void setFrequency(int newFreq) {
				frequency = newFreq;
			}
			
			public int getFrequency() {
				return frequency;
			}
			
			public boolean isLeaf() {
				return (left == null && right == null);
			}
			
			public String toString() {
				StringBuilder str = new StringBuilder("char: " + data + " code: " + code);
				return str.toString();
			}
	}
	
	private Map<Character, String> decoder = new HashMap<>();
	private List<Node> queue = new ArrayList<Node>();
	private Node root;
	private List<Node> list;
    
    public String encode(String text) {

        for (int i = 0; i < text.length(); i++) {
        	Node node = new Node(1);
        	node.setChar(text.charAt(i));
        	if (queue.contains(node)) {
        		for (Node each : queue) {
        			if (each.equals(node)) {
        				int freq = each.getFrequency() + 1;
        				each.setFrequency(freq);
        				break;
        			}
        		}
        	} else {
        		queue.add(node);
        	}   	
        }
        Collections.sort(queue, new FrequencyComparator<Node>());
        while (queue.size() > 1) {

        	Node node1 = queue.remove(0);
        	Node node2 = queue.remove(0);
        	
        	Node newNode = new Node(node1.getFrequency() + node2.getFrequency());
        	newNode.setChar('x');
        	newNode.left = node1;
        	node1.setParent(newNode);
        	newNode.right = node2;
        	node2.setParent(newNode);
        	
        	TreeSet newNodeSet = newNode.getSet();
        	if (node1.getSet() != null) {
        		TreeSet set1 = node1.getSet();
        		newNodeSet.addAll(set1);
        	}
        	if (node2.getSet() != null) {
        		TreeSet set2 = node2.getSet();
        		newNodeSet.addAll(set2);
        	}
        	
        	newNode.addToSet(node1);
        	newNode.addToSet(node2);
        	
        	queue.add(newNode);
        	Collections.sort(queue, new FrequencyComparator<Node>());
        }
        root = queue.remove(0);
        List<Node> newList = getPreOrder();
        
        String finalStr = "";
        for (int i = 0; i < text.length(); i++) {
        	if (decoder.containsKey(text.charAt(i))) {
        		finalStr = finalStr + decoder.get(text.charAt(i));
        	}
        }
       
        return finalStr;
    }
    
    public List<Node> getPreOrder() {
    	list = new LinkedList<>();
         preOrderHelper(root);
         return list;
    }

    private void preOrderHelper(Node current) {
    	if(current != null) {
			if (current.getParent() != null) {
				if (current.getParent().left == current) {
					current.binaryNum = "0";
				} else {
					current.binaryNum = "1";
				}
			}
			if (current.isLeaf()) {
				Node getcode = current;
				while (getcode.getParent() != null) {
					current.addToCode(getcode.binaryNum);
					getcode = getcode.getParent();
				}
				StringBuilder str = new StringBuilder(current.code);
				current.code = str.reverse().toString();
				decoder.put(current.getChar(), current.code);
			}
    		list.add(current);
    		preOrderHelper(current.left);
    		preOrderHelper(current.right);
    	}
    }
    
    public String encodeAdaptive(String filename) throws IOException {
    	String str = "";
    	try {
    		File graph = new File(filename);
			Scanner scan = new Scanner(graph);
		
			String next = scan.next();
		    str = encode(next);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return str;
    }
    
    public Map<Character, String> decodetable() {
    	if (decoder != null) {
    		return decoder;
    	}
    	return null;
    }
    
    public String decode(String encodedText, Map<Character, String> decodetable) {
    	String code = "";
    	String decode = "";
    	Set<Character> keyset = decodetable.keySet();
    	for (int i = 0; i < encodedText.length(); i++) {
    		code = code + encodedText.charAt(i);
    		if (decodetable.containsValue(code)) {
    			for (Character key : keyset) {
    				if (decodetable.get(key).equals(code)) {
    					decode = decode + key;
    					code = "";
    				}
    			}
    		}
    	}
    	return decode;
    }
    
    public static void main(String[] args) throws IOException {
    	String str = "a man a plan panama";
    	HuffmanEncoder encoder = new HuffmanEncoder();
    	String q = encoder.encode(str);
    	System.out.println(q);
    	Map<Character, String> map = encoder.decodetable();
    	String dec = encoder.decode(q, map);
    	System.out.println(dec);
    }
}
