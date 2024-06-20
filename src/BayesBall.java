public class BayesBall {

    public boolean Call(Network network, String query) {

        // Extracting the variables
        String paramString = query.split("\\|")[0];
        String param1 = paramString.split("-")[0];
        String param2 = paramString.split("-")[1];

        // updating the node of their given state
        network.given_update(query);

        boolean returned_val = this.BayesBallRecursive(network.find_node(param1), network.find_node(param2), network.find_node(param1), true, true);
        network.ResetNetwork();
        return returned_val;
    }

    public boolean BayesBallRecursive(NetNode current_node, NetNode goal_node, NetNode previous_node, boolean from_parent, boolean first_call) {
        // Stopping cases
        // If we reached the goal node
        if (current_node == goal_node) {
            return true;
        }
        // if we came from a child node into a given node
        if (!from_parent && current_node.given) {
            return false;
        }
        // if we reached a leaf node
        if (current_node.Childs.isEmpty() && from_parent && !current_node.given && !first_call) {
            return false;
        }
        // if we have reached a 'root' node from a child node
        if (!from_parent && current_node == previous_node) {
            return false;
        }

        current_node.BB_visited = true;

        // recursion cases
        boolean returned_val = false;
        // If this is the first call
        if (first_call) {
            // recursion to all the parents
            for (NetNode parent : current_node.Parents) {
                returned_val = returned_val || this.BayesBallRecursive(parent, goal_node, current_node, false, false);
            }
            // recursion to all the childs
            for (NetNode child : current_node.Childs) {
                if (child.BB_visited){
                    continue;
                }
                returned_val = returned_val || this.BayesBallRecursive(child, goal_node, current_node, true, false);
            }
        }
        // If the current node is given
        if (current_node.given) {
            // if we came from a parent node otherwise we already returned false
            for (NetNode parent : current_node.Parents) {
                returned_val = returned_val || this.BayesBallRecursive(parent, goal_node, current_node, false, false);
            }
        }
        // If the current node is not given
        else {
            // if we came from a parent node
            if (from_parent) {
                // we go over all the children of the current node
                for (NetNode child : current_node.Childs) {
                    returned_val = returned_val || this.BayesBallRecursive(child, goal_node, current_node, true, false);
                }
            // if we came from a child node
            } else {
                for (NetNode child : current_node.Childs) {
                    if (child.BB_visited) {
                        continue;
                    }
                    returned_val = returned_val || this.BayesBallRecursive(child, goal_node, current_node, true, false);
                }
                for (NetNode parent : current_node.Parents) {
                    returned_val = returned_val || this.BayesBallRecursive(parent, goal_node, current_node, false, false);
                }
            }
        }
        return returned_val;
    }

}
