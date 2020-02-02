import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

class Node {
    int index;
    ArrayList<String> strs = new ArrayList<>();
    ArrayList<Node> out_nodes = new ArrayList<>();
    ArrayList<Node> in_nodes = new ArrayList<>();

    Node(String str) {
        this.strs.add(str);
    }

    Node() {

    }

    void insert_instruction(String str) {
        this.strs.add(str);
    }
}


public class Main {
    private static ArrayList<Node> nodes = new ArrayList<>();
    private static Node first = new Node();
    private static Node lastNode = first;
    static String lastInput = "";

    public static void main(String[] args) throws FileNotFoundException {
        nodes.add(first);
        Scanner scanner = new Scanner(new File("code.txt"));

        while (scanner.hasNext()) {
            String input = scanner.nextLine().strip();
            if (input.strip().startsWith("//") || input.strip().equals(""))
                continue;
            input = input.strip();
            if (input.equals("exit;")) {
                lastNode.insert_instruction(input);
                break;
            }
            if (input.contains("bra") || input.contains("brx") || input.contains("@")) {
                lastNode.insert_instruction(input);
                Node node = new Node();
                lastNode.out_nodes.add(node);
                node.in_nodes.add(lastNode);
                lastNode = node;
                nodes.add(node);
            } else {
                lastNode.insert_instruction(input);
            }
            lastInput = input;
        }


        ////////////////////// @

        for (int k = 0; k < nodes.size(); k++) {
            Node node = nodes.get(k);
            String strInLoop = node.strs.get(node.strs.size() - 1);
            if (strInLoop.contains("@")) {
                for (int i = 0; i < strInLoop.length(); i++) {
                    char car = strInLoop.charAt(i);
                    if (car == '@') {
                        for (int j = i; j < strInLoop.length(); j++) {
                            if (strInLoop.charAt(j) == ' ') {
                                Node tempnode = new Node(strInLoop.substring(j).strip());
                                tempnode.out_nodes = node.out_nodes;
                                tempnode.in_nodes.add(node);
                                node.out_nodes.add(tempnode);
                                node.strs.remove(strInLoop);
                                node.strs.add(strInLoop.substring(0, j));
                                nodes.add(tempnode);
                                break;
                            }
                        }
                        break;
                    }
                }

            }
        }

        //////////////// bra
        for (int k = 0; k < nodes.size(); k++) {
            Node node = nodes.get(k);
            if (node.strs.get(node.strs.size() - 1).contains("bra")) {
                String label = "";
                String temp = node.strs.get(node.strs.size() - 1);
                for (int i = 0; i < temp.length(); i++) {
                    if (temp.substring(i).startsWith("bra") && (i == 0 || temp.charAt(i) == ' ' || temp.charAt(i) == ':')) {
                        for (int j = i; j < temp.length(); j++) {
                            if (temp.charAt(j) == ' ') {
                                label = temp.substring(j).strip().split(" ")[0];
                                break;
                            }
                        }
                        break;
                    }
                }
                l:
                if (!label.equals(""))
                    for (int t = 0; t < nodes.size(); t++) {
                        Node node1 = nodes.get(t);
                        ArrayList<String> storeString = new ArrayList<>();
                        for (int i = 0; i < node1.strs.size(); i++) {
                            String tempstr = node1.strs.get(i);
                            storeString.add(tempstr);
                            if (tempstr.startsWith(label.strip().substring(0, label.strip().length() - 1) + ":")) {

                                node.out_nodes = new ArrayList<>();
                                Node tempnode = new Node();
                                node.out_nodes.add(tempnode);
                                for (int j = i; j < node1.strs.size(); j++) {
                                    tempnode.insert_instruction(node1.strs.get(j));
                                }
                                storeString.remove(tempstr);
                                node1.strs = storeString;
                                tempnode.out_nodes = node1.out_nodes;
                                tempnode.in_nodes.add(node1);
                                nodes.add(tempnode);
                                node1.out_nodes = new ArrayList<>();
                                node1.out_nodes.add(tempnode);
                                break l;
                            }

                        }
                    }

            }
        }

        ////////////////////// brx
        for (int k = 0; k < nodes.size(); k++) {
            Node node = nodes.get(k);
            ArrayList<String> labels = new ArrayList<>();
            if (node.strs.get(node.strs.size() - 1).contains("brx")) {
                String label = "";
                String temp = node.strs.get(node.strs.size() - 1);
                for (int i = 0; i < temp.length(); i++) {
                    if (temp.substring(i).startsWith("brx")) {
                        for (int j = i; j < temp.length(); j++) {
                            if (temp.charAt(j) == ',') {
                                label = temp.substring(j).strip().split(",")[1];
                                label = label.strip().substring(0, label.strip().length() - 1).strip();
                                break;
                            }
                        }
                        break;
                    }
                }
                node.out_nodes = new ArrayList<>();
                outer:
                for (Node node1 : nodes) {
                    for (int i = 0; i < node1.strs.size(); i++) {
                        String tempstr = node1.strs.get(i);
                        if (tempstr.startsWith(label + ":")) {
                            String[] strings = tempstr.split(",");

                            for (int z = 0; z < strings.length; z++) {
                                if (z == 0) {
                                    strings[0] = strings[0].split(" ")[strings[0].split(" ").length - 1];
                                }
                                if (z == strings.length - 1) {
                                    strings[strings.length - 1] = strings[strings.length - 1].substring(0, strings[strings.length - 1].length() - 1);
                                }
                                strings[z] = strings[z].strip();
                                labels.add(strings[z]);
                            }
                            break outer;
                        }
                    }
                }
                for (String label1 : labels) {
                    l1:
                    for (int t = 0; t < nodes.size(); t++) {
                        Node node1 = nodes.get(t);
                        ArrayList<String> storeString = new ArrayList<>();
                        for (int i = 0; i < node1.strs.size(); i++) {
                            String tempstr = node1.strs.get(i);
                            storeString.add(tempstr);
                            if (tempstr.startsWith(label1 + ":")) {
                                //node.out_nodes = new ArrayList<>();
                                Node tempnode = new Node();
                                node.out_nodes.add(tempnode);
                                for (int j = i; j < node1.strs.size(); j++) {
                                    tempnode.insert_instruction(node1.strs.get(j));
                                }
                                storeString.remove(tempstr);
                                node1.strs = storeString;
                                tempnode.out_nodes = node1.out_nodes;
                                tempnode.in_nodes.add(node1);
                                nodes.add(tempnode);
                                node1.out_nodes = new ArrayList<>();
                                node1.out_nodes.add(tempnode);
                                break l1;
                            }

                        }
                    }
                }
            }
        }


        ////////////////////// {}




        int i = 0;
        for (Node node : nodes) {
            node.index = i;
            i++;
        }
        for (Node node : nodes) {
            System.out.print(node.index + ": ");
            for (Node temp : node.out_nodes)
                System.out.print(temp.index + ", ");
            System.out.println(node.strs.get(0));
        }


    }

}
