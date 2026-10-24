//Arber Aliu, CS2210, November 19, Assignment 4
// This code stores a piece of multimedia information using two attributes: a content string and a type number. It also includes a constructor to set all of these values and it has two getter methods to retrieve them.


public class MultimediaItem {

    private String content;
    private int type;

    // This is the constructor Multimedia Item
    public MultimediaItem(String newContent, int newType) {
        content = newContent;
        type = newType;
    }

    // This will return the multimedia content text/filename.
    public String getContent() {
        return content;
    }

    // This part returns the type of content 
    public int getType() {
        return type;
    }

    
}
