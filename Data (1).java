//Arber Aliu, CS2210, November 19, Assignment 4
//This Data class stores a key name and a list of related multimedia items, starting with an empty list when created. It adds new MultimediaItem objects and provides the methods to retrieve the name and the media list.


import java.util.ArrayList;

public class Data {

    private String name;
    private ArrayList<MultimediaItem> media;

    // This is a donstructor where we see a key attribute and a empty media list
    public Data(String newName) {
        name = newName;
        media = new ArrayList<MultimediaItem>();
    }

    // This adds a multimedia item to the list
    public void add(MultimediaItem newItem) {
        media.add(newItem);
    }

    // This part will return the key which we have it set to as name
    public String getName() {
        return name;
    }

    // This will return the list of all multimedia items
    public ArrayList<MultimediaItem> getMedia() {
        return media;
    }

}
