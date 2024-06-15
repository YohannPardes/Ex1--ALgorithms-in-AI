import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class VariableElimination {

    ArrayList <NetNode> RelevantNodes = new ArrayList<>(); // the nodes that are relevant to the query
    ArrayList<NetNode> GivenNodes = new ArrayList<>();
    ArrayList<String> GivenVals = new ArrayList<>();
    ArrayList<NetNode> HiddenNodes = new ArrayList<>();
    NetNode QueryNode;
    String QueryVal;

    public String Call(Network network, String query) {
        // Extracting the variables
        ProcessQueryString(network, query);

        // updating the node of their given state
        network.given_update(query);

        // First step - getting rid of the non-parent of query or evidence nodes
        this.eliminate_useless_nodes(network);

        // Second step - Keeping only relevant nodes using bayes ball
        BayesBall my_algo = new BayesBall();
        for (int i = 0; i < this.RelevantNodes.size();) {
            NetNode node = this.RelevantNodes.get(i);
            if (!my_algo.BayesBallRecursive(node, this.QueryNode, node, true, true)) {
                this.RelevantNodes.remove(node);
                System.out.println("Removing node "+node.name+" from the list");
            }
            else {
                i++;
            }
            network.ResetNetwork();
        }

        // Third step - Actually eliminating the variables
        this.eliminate_variables();

        return "";
    }

    private void eliminate_variables() {

        // First step - reducing the variables by evidences
        this.given_reduction();

        // Second step - reducing the variables by hidden variables
        this.hidden_reduction();

    }

    private void hidden_reduction() {
        // Get all the nodes cpt variables
        for (NetNode node : this.RelevantNodes){ // for each hidden node
            if (this.HiddenNodes.contains(node)) { // if the hidden node his still relevant to the query
                System.out.println("Node name : "+node.name);
                // Get all the CPT of the node
                ArrayList<CPT> cptList = new ArrayList<>(); // adding childs cpt's to the list
                for (NetNode child : node.Childs){
                    cptList.add(child.CPT);
                }
                cptList.add(node.CPT); // adding hiw own cpt
                reduceVariables(cptList);
            }

        }

    }

    private void given_reduction() {
        for (NetNode node : this.RelevantNodes) {

            if (node.given){
                System.out.println("Node name : "+node.name);
                node.collapse_given();
                System.out.println("CPT After "+Arrays.toString(node.CPT.computed_values));
            }
        }
    }

    private void reduceVariables(ArrayList<CPT> cptList) {
        // Looping over all the

    }

    private void ProcessQueryString(Network network, String query) {
        String paramString = query.split("\\(")[1];
        String HiddenVar = paramString.split("\\)")[1];
        paramString = paramString.split("\\)")[0];
        String GivenVar = paramString.split("\\|")[1];
        paramString = paramString.split("\\|")[0];
        String QueryVar = paramString.split("=")[0];
        String QueryVal = paramString.split("=")[1];

        System.out.println("QueryVal - "+QueryVal);
        System.out.println("HiddenVar - "+HiddenVar);
        System.out.println("QueryVar - "+QueryVar);
        System.out.println("GivenVar - "+GivenVar);

        this.QueryNode = network.find_node(QueryVar);
        this.QueryVal = QueryVal;



        for (String hidden : HiddenVar.split("-")){
            this.HiddenNodes.add(network.find_node(hidden.strip()));
        }

        for (String given : GivenVar.split(",")){
            this.GivenNodes.add(network.find_node(given.split("=")[0]));
            this.GivenVals.add(given.split("=")[1]);
        }
        for (NetNode node : this.GivenNodes){
            System.out.println("Given node : "+node.name);
        }

        for (NetNode node : this.HiddenNodes){
            System.out.println("Hidden node : "+node.name);
        }


    }

    /**
     * Eliminate the nodes that are not a parent of the query node or a given node
     * @param network
     */
    private void eliminate_useless_nodes(Network network) {
        for(NetNode node: this.GivenNodes){
            eliminate_useless_nodes_recursive(node);
        }
        eliminate_useless_nodes_recursive(this.QueryNode);
    }

    private void eliminate_useless_nodes_recursive(NetNode node) {
        if (!this.RelevantNodes.contains(node)){
            System.out.println("Adding node "+node.name+" to the list");
            this.RelevantNodes.add(node);
        }

        for(NetNode parent: node.Parents){
            eliminate_useless_nodes_recursive(parent);
        }
    }
}
