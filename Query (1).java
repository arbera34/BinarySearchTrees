
//This Query class builds a BSTOrderedDictionary from an input file, then enters a loop where it reads commands from the user to look up, modify, or list keys and their multimedia items. Depending on what sort of content type, it either prints text or plays the sound, image, or HTML file.

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Query {

    private static BSTOrderedDictionary dict;

    // This is a constructor where it reads input file and it will build the dictionary
    public Query(String inputFile) {
        dict = new BSTOrderedDictionary();

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));

            while (true) {
                //the key 
                String key = br.readLine();
                if (key == null) break;                // this means it has reached end of file

                // This is the content associated with that key
                String content = br.readLine();
                if (content == null) break;             

                String a = key.trim().toLowerCase();    // these are the keys stored in lowercase
                String c = content.trim();              // we must keep the content as-is
                int type = getTypeFromContent(c);       // this will determine the media type from extension

                //This Inserts into the dictionary
                dict.put(dict.getRoot(), a, c, type);
            }

            br.close();
        } catch (IOException e) {
            System.out.println("Error reading input file");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        StringReader keyboard = new StringReader();

        //This must pass exactly one file name
        if (args.length != 1) {
            System.out.println("Usage: java Query fileName");
            System.exit(0);
        }

        // This will build dictionary from the file
        new Query(args[0]);

        //This part will read and process the commands in a loop
        while (true) {
            String cmd = keyboard.read("Enter next command: ");
            String result = processCommand(cmd);
            if (!result.equals("")) {
                System.out.println(result);
            }
        }
    }

    // This part interprets and executes a single command line
    public static String processCommand(String command) {
        if (command == null) return "Invalid command";
        command = command.trim();
        if (command.equals("")) return "Invalid command";

        String[] parts = command.split("\\s+"); // this splits by whitespace
        String cmd = parts[0];                  // this means that the first word is the command name

        
        if (cmd.equals("end")) {
            System.exit(0);
        }

        
        
        if (cmd.equals("get")) {
            if (parts.length != 2) return "Invalid command";

            String originalKey = parts[1];
            String key = originalKey.toLowerCase();

            // this will look up the key
            ArrayList<MultimediaItem> list = dict.get(dict.getRoot(), key);

            // If the key is not found it will show the preceding and following words
            if (list == null) {
                BSTNode b = findNodeOrLeaf(dict.getRoot(), key);
                BSTNode pred = predecessorNode(b);
                BSTNode successor = successorNode(b);

                StringBuilder sb = new StringBuilder();
                sb.append("The word ").append(originalKey)
                  .append(" is not in the ordered dictionary.\n");

                sb.append("Preceding word: ");
                if (pred != null) sb.append(pred.getData().getName());
                sb.append("\n");

                sb.append("Following word: ");
                if (successor != null) sb.append(successor.getData().getName());

                return sb.toString();
            }

            // If the key is found it will either print text content or it will play the media
            StringBuilder out = new StringBuilder();
            for (MultimediaItem item : list) {
                int t = item.getType();
                String c = item.getContent();
                try {
                    if (t == 1) {
                        // this is the plain text
                        out.append(c).append("\n");
                    } else if (t == 2) {
                        // this is the sound file
                        SoundPlayer sp = new SoundPlayer();
                        sp.play(c);
                    } else if (t == 3) {
                        //this is the image file
                        PictureViewer pv = new PictureViewer();
                        pv.show(c);
                    } else if (t == 4) {
                        // this is the HTML file
                        ShowHTML sh = new ShowHTML();
                        sh.show(c);
                    }
                } catch (MultimediaException e) {
                    out.append("Multimedia error: ").append(c).append("\n");
                }
            }
            return out.toString().trim();
        }

        
        // add function
        if (cmd.equals("add")) {
            if (parts.length != 4) return "Invalid command";

            String originalKey = parts[1];
            String key = originalKey.toLowerCase();
            String content = parts[2];

            int type;
            try {
                type = Integer.parseInt(parts[3]);   // this will parse the type as an integer
            } catch (NumberFormatException e) {
                return "Invalid command";
            }

            dict.put(dict.getRoot(), key, content, type);
            return "";
        }

        
        // This part removes all media for that key
        if (cmd.equals("remove")) {
            if (parts.length != 2) return "Invalid command";

            String originalKey = parts[1];
            String key = originalKey.toLowerCase();

            try {
                dict.remove(dict.getRoot(), key);
                return "";
            } catch (DictionaryException e) {
                return "No record in the ordered dictionary has key " + originalKey + ".";
            }
        }

        
        // This part will remove only one type of media under that key
        if (cmd.equals("delete")) {
            if (parts.length != 3) return "Invalid command";

            String originalKey = parts[1];
            String key = originalKey.toLowerCase();

            int type;
            try {
                type = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                return "Invalid command";
            }

            try {
                dict.remove(dict.getRoot(), key, type);
                return "";
            } catch (DictionaryException e) {
                return "No record in the ordered dictionary has key " + originalKey + ".";
            }
        }

        
        // first : smallest key in dictionary
        if (cmd.equals("first")) {
            if (dict.getNumInternalNodes() == 0) {
                return "The ordered dictionary is empty.";
            }
            return dict.smallest(dict.getRoot()).getName();
        }

        
        // last : largest key in dictionary
        if (cmd.equals("last")) {
            if (dict.getNumInternalNodes() == 0) {
                return "The ordered dictionary is empty.";
            }
            return dict.largest(dict.getRoot()).getName();
        }

        
        //number of keys
        if (cmd.equals("size")) {
            return "There are " + dict.getNumInternalNodes()
                   + " keys in the ordered dictionary";
        }

        
        // next: the key and next d successors 
        if (cmd.equals("next")) {
            if (parts.length != 3) return "Invalid command";

            String originalKey = parts[1];
            String key = originalKey.toLowerCase();

            int d;
            try {
                d = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                return "Invalid command";
            }

            if (dict.getNumInternalNodes() == 0) {
                return "There are no keys larger than or equal to " + originalKey;
            }

            BSTNode b = findNodeOrLeaf(dict.getRoot(), key);
            ArrayList<String> ans = new ArrayList<>();

            // We can call this case 1 where the exact key exists
            if (!b.isLeaf()) {
                ans.add(b.getData().getName());
                BSTNode curr = b;
                for (int i = 0; i < d; i++) {
                    curr = successorNode(curr);
                    if (curr == null) break;
                    ans.add(curr.getData().getName());
                }
            } else {
                //We can call this case 2 where the key is not in tree, where we must start from successor of leaf position
                BSTNode curr = successorNode(b);
                if (curr == null) {
                    return "There are no keys larger than or equal to " + originalKey;
                }
                for (int i = 0; i < d && curr != null; i++) {
                    ans.add(curr.getData().getName());
                    curr = successorNode(curr);
                }
            }

            if (ans.isEmpty()) {
                return "There are no keys larger than or equal to " + originalKey;
            }

            return String.join(" ", ans);
        }

        
        // pre: key and next d predecessors 
        if (cmd.equals("prev")) {
            if (parts.length != 3) return "Invalid command";

            String originalKey = parts[1];
            String key = originalKey.toLowerCase();

            int d;
            try {
                d = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                return "Invalid command";
            }

            if (dict.getNumInternalNodes() == 0) {
                return "There are no keys smaller than or equal to " + originalKey;
            }

            BSTNode b = findNodeOrLeaf(dict.getRoot(), key);
            ArrayList<String> ans = new ArrayList<>();

            // We can call this case 1 where the key exists
            if (!b.isLeaf()) {
                ans.add(b.getData().getName());
                BSTNode curr = b;
                for (int i = 0; i < d; i++) {
                    curr = predecessorNode(curr);
                    if (curr == null) break;
                    ans.add(curr.getData().getName());
                }
            } else {
                // We can call this case 2 where if the key is not in tree it will start at predecessor of leaf position
                BSTNode curr = predecessorNode(b);
                if (curr == null) {
                    return "There are no keys smaller than or equal to " + originalKey;
                }
                for (int i = 0; i < d && curr != null; i++) {
                    ans.add(curr.getData().getName());
                    curr = predecessorNode(curr);
                }
            }

            if (ans.isEmpty()) {
                return "There are no keys smaller than or equal to " + originalKey;
            }

            
            return String.join(" ", ans);
        }

        
        return "Invalid command"; //this part will return if the command is invalid
    }

    //these are private helper methods

    //This part will determine the media type based on file extension 
    private static int getTypeFromContent(String s) {
        int dot = s.lastIndexOf('.');
        // this part must contain a dot, not at the beginning or end, and there should be no spaces where we must treat it as a file
        if (dot > 0 && dot < s.length() - 1 && s.indexOf(' ') == -1) {
            String ext = s.substring(dot + 1).toLowerCase();
            if (ext.equals("wav") || ext.equals("mid")) return 2;   // sound
            if (ext.equals("jpg") || ext.equals("gif")) return 3;   // jpg image
            if (ext.equals("html")) return 4;                       // HTML
        }
        return 1;  // plain text
    }

    // For this part, like findNode, but this time it returns the leaf where the search ends if the key isn't found
    private static BSTNode findNodeOrLeaf(BSTNode r, String key) {
        if (r == null || r.isLeaf()) return r;
        int cmp = key.compareTo(r.getData().getName());
        if (cmp == 0) return r;
        if (cmp < 0) return findNodeOrLeaf(r.getLeftChild(), key);
        return findNodeOrLeaf(r.getRightChild(), key);
    }

    // This is the smallest internal node in the subtree rooted at r
    private static BSTNode smallestNode(BSTNode r) {
        if (r == null || r.isLeaf()) return null;
        BSTNode curr = r;
        while (!curr.getLeftChild().isLeaf()) {
            curr = curr.getLeftChild();
        }
        return curr;
    }

    // This is the largest internal node in the subtree rooted at r
    private static BSTNode largestNode(BSTNode r) {
        if (r == null || r.isLeaf()) return null;
        BSTNode curr = r;
        while (!curr.getRightChild().isLeaf()) {
            curr = curr.getRightChild();
        }
        return curr;
    }

    // This is the successor node in BST but will produce null if none
    private static BSTNode successorNode(BSTNode n) {
        if (n == null) return null;

        // If the right subtree exists this will mean the successor is the smallest node
        if (n.getRightChild() != null && !n.getRightChild().isLeaf()) {
            return smallestNode(n.getRightChild());
        }

        // Else we must climb up until we come from a left child
        BSTNode curr = n;
        BSTNode b = curr.getParent();
        while (b != null && curr == b.getRightChild()) {
            curr = b;
            b = b.getParent();
        }
        return b;
    }

    // This is the predecessor node in BST but will produce null if none
    private static BSTNode predecessorNode(BSTNode n) {
        if (n == null) return null;

        // If the left subtree exists this means the predecessor is the largest node
        if (n.getLeftChild() != null && !n.getLeftChild().isLeaf()) {
            return largestNode(n.getLeftChild());
        }

        // like the code before but different, it will climb up until we come from a right child
        BSTNode curr = n;
        BSTNode b = curr.getParent();
        while (b != null && curr == b.getLeftChild()) {
            curr = b;
            b = b.getParent();
        }
        return b;
    }
}
