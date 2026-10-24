//Arber Aliu, CS2210, November 19, Assignment 4
//This BSTOrderedDictionary class stores pairs inside a binary search tree, where the keys are in alphabetical order. 
import java.util.ArrayList;

public class BSTOrderedDictionary implements BSTOrderedDictionaryADT {

    private BSTNode root;              // This is the root of the binary search tree
    private int numInternalNodes;      // this displays the total number of non-leaf nodes

    public BSTOrderedDictionary() {
        root = new BSTNode();          // this part starts with a single leaf node as the root
        numInternalNodes = 0;
    }

    public BSTNode getRoot() {
        return root;
    }

    public int getNumInternalNodes() {
        return numInternalNodes;
    }

    // This part will return all media items stored under this key it will return null if the key is not found
    public ArrayList<MultimediaItem> get(BSTNode r, String key) {
        BSTNode a = findNode(r, key);              // this part searches for the node
        if (a == null || a.isLeaf()) return null;  // this means that the key is not found
        if (a.getData().getName().equals(key))     // this will confirm that the key matches
            return a.getData().getMedia();
        return null;
    }

    // This part will insert a key or it will add a media item to a key that already exists
    public void put(BSTNode r, String key, String content, int type) {
        if (r == null) return;

        if (r.isLeaf()) {
            Data b = new Data(key);                                // this will create a new key
            b.add(new MultimediaItem(content, type));               // this part adds the first media item
            r.setData(b);                                           // this will convert the leaf to an internal node

            //This part will create the required leaf children for the BST structure
            BSTNode left = new BSTNode();
            BSTNode right = new BSTNode();
            left.setParent(r);
            right.setParent(r);
            r.setLeftChild(left);
            r.setRightChild(right);

            numInternalNodes++;                                    
            return;
        }

        // This part compares the keys to decide whether it will recurse to the left or to the right
        int cmp = key.compareTo(r.getData().getName());

        if (cmp == 0) {
            // This means that the key already exists and it will add another multimedia item
            r.getData().add(new MultimediaItem(content, type));
        } else if (cmp < 0) {
            put(r.getLeftChild(), key, content, type);              // searches left subtree
        } else {
            put(r.getRightChild(), key, content, type);             // searches right subtree
        }
    }

    // This part will remove a key as a whole
    public void remove(BSTNode r, String key) throws DictionaryException {
        BSTNode a = findNode(r, key);                               //this locates the node

        if (a == null || a.isLeaf() || !a.getData().getName().equals(key))
            throw new DictionaryException("Key not found");

        deleteNode(a);                                               // deletion
    }

    // This part will remove one type of media under a certain key and if no media remain it will delete the key 
    public void remove(BSTNode r, String key, int type) throws DictionaryException {
        BSTNode a = findNode(r, key);

        // This shows that the key is not present
        if (a == null || a.isLeaf() || !a.getData().getName().equals(key))
            throw new DictionaryException("Key not found");

        ArrayList<MultimediaItem> list = a.getData().getMedia();

        // This removes all matching media items
        for (int c = list.size() - 1; c >= 0; c--)
            if (list.get(c).getType() == type)
                list.remove(c);

        // If there is no media remaining, it will delete the entire node from the tree
        if (list.isEmpty())
            deleteNode(a);
    }

    // This will return the leftmost key in subtree
    public Data smallest(BSTNode r) {
        BSTNode a = smallestNode(r);
        return (a == null) ? null : a.getData();
    }

    // This will return the rightmost key in subtree
    public Data largest(BSTNode r) {
        BSTNode a = largestNode(r);
        return (a == null) ? null : a.getData();
    }

    // This will return the successor
    public Data successor(BSTNode r, String key) {
        BSTNode a = findNode(r, key);

        if (a == null || a.isLeaf() || !a.getData().getName().equals(key))
            return null;

        // We can call this case 1 where the successor is the smallest node in the right subtree
        if (!a.getRightChild().isLeaf()) {
            BSTNode e = smallestNode(a.getRightChild());
            return (e == null) ? null : e.getData();
        }

        // We can call this case 2 where we move upward until we find a parent 
        BSTNode curr = a;
        BSTNode f = curr.getParent();

        while (f != null && curr == f.getRightChild()) {
            curr = f;
            f = f.getParent();
        }

        return (f == null) ? null : f.getData();
    }

    // This part will return the next predecessor
    public Data predecessor(BSTNode r, String key) {
        BSTNode a = findNode(r, key);

        if (a == null || a.isLeaf() || !a.getData().getName().equals(key))
            return null;

        // This is case 1 where the predecessor is the largest in the left subtree
        if (!a.getLeftChild().isLeaf()) {
            BSTNode pNode = largestNode(a.getLeftChild());
            return (pNode == null) ? null : pNode.getData();
        }

        // This is case 2 but now we move up to find first parent we came from the right
        BSTNode curr = a;
        BSTNode f = curr.getParent();

        while (f != null && curr == f.getLeftChild()) {
            curr = f;
            f = f.getParent();
        }

        return (f == null) ? null : f.getData();
    }

    
    //helper methods
    // This part will recursively search for the node containing key
    private BSTNode findNode(BSTNode r, String key) {
        if (r == null || r.isLeaf()) return r;                     // This means we have reached a leaf, leading to it not being found
        int cmp = key.compareTo(r.getData().getName());

        if (cmp == 0) return r;                                    // found
        // these will search left and right
        if (cmp < 0) return findNode(r.getLeftChild(), key);       
        return findNode(r.getRightChild(), key);                   
    }

    // This part returns the leftmost internal node
    private BSTNode smallestNode(BSTNode r) {
        if (r == null || r.isLeaf()) return null;
        BSTNode curr = r;

        while (!curr.getLeftChild().isLeaf())                      // this will continue to go left until leaf
            curr = curr.getLeftChild();

        return curr;
    }

    //This returns the rightmost internal node
    private BSTNode largestNode(BSTNode r) {
        if (r == null || r.isLeaf()) return null;
        BSTNode curr = r;

        while (!curr.getRightChild().isLeaf())                     // this will continue to go right until leaf
            curr = curr.getRightChild();

        return curr;
    }

    //This part will delete a node from the BST where it will handle all deletion cases
    private void deleteNode(BSTNode node) {

        boolean lInternal = node.getLeftChild() != null && !node.getLeftChild().isLeaf();
        boolean rInternal = node.getRightChild() != null && !node.getRightChild().isLeaf();

        // This will replace data with successor and delete successor
        if (lInternal && rInternal) {
            BSTNode successor = smallestNode(node.getRightChild());
            node.setData(successor.getData());
            deleteNode(successor);
            return;
        }

        
        numInternalNodes--;

        // This replaces node with a leaf
        if (!lInternal && !rInternal) {

            // If it is the root, this will replace the root with a new leaf
            if (node == root) {
                root = new BSTNode();
                return;
            }

            BSTNode parent = node.getParent();
            BSTNode leaf = new BSTNode();
            leaf.setParent(parent);

            if (parent.getLeftChild() == node)
                parent.setLeftChild(leaf);
            else
                parent.setRightChild(leaf);

            return;
        }

        // With one internal child it will replace node with its child
        BSTNode child = lInternal ? node.getLeftChild() : node.getRightChild();

        if (node == root) {
            root = child;
            child.setParent(null);
        } else {
            BSTNode parent = node.getParent();
            child.setParent(parent);

            if (parent.getLeftChild() == node)
                parent.setLeftChild(child);
            else
                parent.setRightChild(child);
        }
    }
}
