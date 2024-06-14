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
            }
            else {
                i++;
            }
        }

        // Third step - Actually eliminating the variables
        this.eliminate_variables();

        return "";
    }

    private void eliminate_variables() {

        // First step - reducing the variables by evidences
        for (NetNode node : this.RelevantNodes) {
            if (node.given){
                System.out.println(Arrays.toString(node.CPT.computed_values));
                node.collapse_given();
                System.out.println(Arrays.toString(node.CPT.computed_values));
            }
        }


    }

    private void reduceVariables(ArrayList<CPT> cptList) {
        // Looping over all the

    }

    private void ProcessQueryString(Network network, String query) {
        String paramString = query.split("\\(")[1];
        String HiddenVar = paramString.split("\\)")[1];
        System.out.println("HiddenVar - "+HiddenVar);
        paramString = paramString.split("\\)")[0];
        String GivenVar = paramString.split("\\|")[1];
        System.out.println("GivenVar - "+GivenVar);
        paramString = paramString.split("\\|")[0];
        String QueryVar = paramString.split("=")[0];
        System.out.println("QueryVar - "+QueryVar);
        String QueryVal = paramString.split("=")[1];
        System.out.println("QueryVal - "+QueryVal);

        this.QueryNode = network.find_node(QueryVar);
        this.QueryVal = QueryVal;


        for (String hidden : HiddenVar.split("-")){
            this.HiddenNodes.add(network.find_node(hidden));
        }

        for (String given : GivenVar.split(",")){
            this.GivenNodes.add(network.find_node(given.split("=")[0]));
            this.GivenVals.add(given.split("=")[1]);
        }

    }

    private void eliminate_useless_nodes(Network network) {
        for(NetNode node: this.GivenNodes){
            eliminate_useless_nodes_recursive(node);
        }
        eliminate_useless_nodes_recursive(this.QueryNode);
    }

    private void eliminate_useless_nodes_recursive(NetNode node) {
        this.RelevantNodes.add(node);
        for(NetNode parent: node.Parents){
            eliminate_useless_nodes_recursive(parent);
        }

    }
}
