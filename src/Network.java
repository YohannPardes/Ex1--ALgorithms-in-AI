import java.util.ArrayList;

public class Network {
    ArrayList<NetNode> nodes = new ArrayList<>();
    String inputFile;

    public void Print_Network() {
        for (NetNode node : nodes) {
            System.out.println(node);
        }
    }

    public NetNode find_node(String name) {
        for (NetNode node : this.nodes) {
            if (name.equals(node.name)) {
                return node;
            }
        }
        return null;
    }

    public void Update_children() {
        for (NetNode node : this.nodes) {
            node.children_update();
        }
    }

    /**
     * This function updates the given nodes in the network
     * @param query - The current query as a string
     */
    void given_update(String query) {
        // extracting the Given part
        String[] givenString = query.split("\\|");
        if (givenString.length == 1) {
            return;
        }
        String givenPart = givenString[1];
        givenPart = givenPart.split("\\)")[0];
        for (String given : givenPart.split(",")) {
            if (given.isEmpty()) {
                break;
            }
            String node = given.split("=")[0];
            String value = given.split("=")[1];
            value = value.split("\\)")[0];

            NetNode current_node = this.find_node(node);
            current_node.given = true;
            current_node.Given_outcome = value;
        }
    }

    public void ResetNetwork() {
        for (NetNode node : this.nodes) {
            node.BB_visited = false;
        }
    }

    public void HardReset(VariableElimination VE){
        for (NetNode node : this.nodes){
            node.given = false;
            node.Given_outcome = "";
            node.BB_visited = false;
        }

        for (NetNode node : this.nodes){
            node.factor = null;
            node.factor = new Factor(node);
        }
        VE.reset();
    }
    
    public void create_factors() {
        for (NetNode node : this.nodes) {
            node.factor = new Factor(node);
        }
    }
}
