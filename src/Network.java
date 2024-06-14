import java.util.ArrayList;

public class Network {
    ArrayList<NetNode> nodes = new ArrayList<>();

    Network() {

    }

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

    public void Update_childs() {
        for (NetNode node : this.nodes) {
            node.childs_update();
        }
    }

    /**
     * This function updates the given nodes in the network
     * @param query
     */
    void given_update(String query) {
        // extracting the Given part
        String[] givenString = query.split("\\|");
        if (givenString.length == 1) {
            return;
        }
        String givenPart = givenString[1];
        for (String given : givenPart.split(",")) {
            if (given.equals("")) {
                break;
            }
            String node = given.split("=")[0];
            String value = given.split("=")[1];

            NetNode current_node = this.find_node(node);
            current_node.given = true;
            current_node.Given_outcome = value;
        }
    }

    public void ResetNetwork() {
        for (NetNode node : this.nodes) {
            node.BB_visited = false;
            node.given = false;
        }
    }
}
