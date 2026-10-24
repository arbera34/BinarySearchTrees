//Arber Aliu, CS2210, November 19, Assignment 4
//This BSTNode class represents a single node in a binary search tree, where it stores a Data object and the links to its parent and the left and right child. It provides constructors, getter/setter methods, and a helper method to check whether the node is a leaf.

public class BSTNode {

    private BSTNode parent;
    private BSTNode leftChild;
    private BSTNode rightChild;
    private Data data;

    // These are default constructors where all instance variables set to null
    public BSTNode() {
        parent = null;
        leftChild = null;
        rightChild = null;
        data = null;
    }

    // This is a full constructor where the instance variables gets set to new
    public BSTNode(BSTNode newParent, BSTNode newLeftChild, BSTNode newRightChild, Data newData) {
        parent = newParent;
        leftChild = newLeftChild;
        rightChild = newRightChild;
        data = newData;
    }

    // These are the getter methods
    public BSTNode getParent() {
        return parent;
    }

    public BSTNode getLeftChild() {
        return leftChild;
    }

    public BSTNode getRightChild() {
        return rightChild;
    }

    public Data getData() {
        return data;
    }

    // These are the Setter methods we are using 
    public void setParent(BSTNode newParent) {
        parent = newParent;
    }

    public void setLeftChild(BSTNode newLeftChild) {
        leftChild = newLeftChild;
    }

    public void setRightChild(BSTNode newRightChild) {
        rightChild = newRightChild;
    }

    public void setData(Data newData) {
        data = newData;
    }

    //This part will return true if the node is a leaf
    public boolean isLeaf() {
        return (leftChild == null && rightChild == null);
    }

}
