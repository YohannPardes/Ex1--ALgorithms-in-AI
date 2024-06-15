import java.util.*;

public class NetNode {

    boolean relevant = true;
    boolean given = false;
    boolean BB_visited = false;
    String Given_outcome = "";
    String name;
    CPT CPT = new CPT();
    ArrayList<NetNode> Parents = new ArrayList<>();
    ArrayList<NetNode> Childs = new ArrayList<>();
    int nb_outcomes = 0;
    HashMap<String, Integer> outcomes = new HashMap<>();

    public NetNode(String name){
        this.name = name;
    }

    public void add_outcome(String outcome){
        outcomes.put(outcome, this.nb_outcomes);
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
        System.out.println("and here they are " + outcomes.toString());
        System.out.println("");
        return "";

    }

    public void childs_update() {
        for (NetNode parent : this.Parents){
            if (!parent.Childs.contains(this)){
                parent.Childs.add(this);
            }
        }
    }

    public void collapse_given(){
        // deleting useless values
        int modulo = this.outcomes.get(this.Given_outcome);
        ArrayList<Float> new_values = new ArrayList<>();
        for (int i = 0; i < this.CPT.computed_values.length;i++){
            if (i % this.nb_outcomes == modulo){
                new_values.add(this.CPT.computed_values[i]);
            }
        }
        this.CPT.computed_values = new_values.toArray(new Float[0]);
    }
}