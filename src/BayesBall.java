public class BayesBall {

    public boolean Call(Network network, String query) {

        // Extracting the variables
        String paramString = query.split("\\|")[0];
        String param1 = paramString.split("-")[0];
        String param2 = paramString.split("-")[1];

        // updating the node of their given state
        network.given_update(query);

        return this.BayesBallRecursive(network.find_node(param1), network.find_node(param2), network.find_node(param1), true, true);
    }

    public boolean BayesBallRecursive(NetNode current_node, NetNode goal_node, NetNode previous_node, boolean from_parent, boolean first_call) {

        // Stopping cases
        if (current_node == goal_node) { // if we have reached the goal
            return true;
        } else if (current_node.Childs.isEmpty() && from_parent && !current_node.given) { // if we reached a leaf child
            return false;
        } else if (current_node.Parents.isEmpty() && !from_parent && !first_call) { // if we reached a leaf parent while going up
            return false;
        } else if (current_node.BB_visited && !from_parent) { // if we already have been through this node and we are not from a parent
            return false;
        } else if (current_node.given && !from_parent) { // we reached a given parent from the bottom then we stop the recursion
            return false;
        }

        current_node.BB_visited = true; // marking the node as visited

        // Case 1 - if we are going from a parent to a given child
        boolean track = false;
        if (from_parent && current_node.given) {
            {
                for (NetNode child : current_node.Childs) {
                    track = track || (this.BayesBallRecursive(child, goal_node, current_node, true, false));
                }
            }
            for (NetNode parent : previous_node.Parents) {
                track = track || (this.BayesBallRecursive(parent, goal_node, current_node, false, false));
            }
        }
        // Case 2 - from a child to normal parent
        else if (!from_parent && !current_node.given || first_call) {
            for (NetNode parent : current_node.Parents) {
                track = track || (this.BayesBallRecursive(parent, goal_node, current_node, false, false));
            }
            for (NetNode child : current_node.Childs) {
                track = track || (this.BayesBallRecursive(child, goal_node, current_node, true, false));
            }
        }
        // Case 3 - from a parent to a normal child
        else if (from_parent && !current_node.given) {
            for (NetNode child : current_node.Childs) {
                track = track || (this.BayesBallRecursive(child, goal_node, current_node, true, false));
            }

        } else {
            throw new RuntimeException("This case should not happen");
        }
        return track;
    }
}
