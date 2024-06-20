import java.util.*;

public class NetNode {

    boolean given = false;
    boolean BB_visited = false;
    String Given_outcome = "";
    String name;
    CPT CPT = new CPT();
    ArrayList<NetNode> Parents = new ArrayList<>();
    ArrayList<NetNode> Childs = new ArrayList<>();
    int nb_outcomes = 0;
    HashMap<String, Integer> outcomes = new HashMap<>();
    Factor factor;
    ArrayList<String> outcome_list = new ArrayList<>();

    /**
     * Constructor of the class
     * @param name
     */
    public NetNode(String name){
        this.name = name;
    }

    /**
     * This function is used add an outcome to the node
     * @param outcome the outcome to add
     */
    public void add_outcome(String outcome){
        outcomes.put(outcome, this.nb_outcomes);
        this.outcome_list.add(outcome);
        this.nb_outcomes += 1;
    }

    @Override
    public String toString(){
        System.out.println("Node name :" + this.name);
        System.out.println("My CPT values are :"+this.CPT);

        System.out.println("Ma parents are :");
        for (NetNode parent : this.Parents){
            System.out.print(parent.name+ ", ");
        }
        System.out.println("\nMy childs are :");
        for (NetNode child : this.Childs){
            System.out.print(child.name+ ", ");
        }
        System.out.println("\nI have :"+this.nb_outcomes+" outcomes");
        System.out.println("and here they are " + outcomes.toString() + "\n");
        return "";

    }

    /**
     * This function update all the children of the parents node
     */
    public void children_update() {
        for (NetNode parent : this.Parents){
            if (!parent.Childs.contains(this)){
                parent.Childs.add(this);
            }
        }
    }

    /**
     * This function is used to update the factor of the node
     */
    public void collapse_given(NetNode node, String givenVal){
        this.factor.updateGiven(givenVal, node);
    }
}