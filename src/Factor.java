import java.util.ArrayList;
import java.util.HashMap;

public class Factor {

    HashMap<ArrayList<String>, Float> data = new HashMap<>();
    ArrayList<NetNode> title;

    /**
     * Given a node create the factor
     * @param node
     */
    Factor(NetNode node) {
        // Adding the node the to factor title and all the node parents
        this.title = (ArrayList<NetNode>) node.Parents.clone();
        this.title.add(node);

        // Creating the data of the factor
        // computing the total number of iterations
        int total_iterations = 1;
        for (NetNode parent : node.Parents) {
            total_iterations *= parent.nb_outcomes;
        }
        total_iterations *= node.nb_outcomes;

        // iterating over all the possible values of the factor
        ArrayList<NetNode> nodes_to_iterate = (ArrayList<NetNode>) node.Parents.clone();
        nodes_to_iterate.add(node);
        for (int i = 0; i < total_iterations; i++) { // Iterate over all the values of the factor
            ArrayList<String> key = getNewKey(nodes_to_iterate, i, total_iterations);
            // add the node value
            this.data.put(key, node.CPT.values[i]);
        }
    }

    /**
     * This function creates the key of the factor
     * @param nodes_to_iterate the nodes to iterate over
     * @param i the current iteration
     * @param total_iterations the total number of iterations
     * @return  the key of the factor
     */
    private static ArrayList<String> getNewKey(ArrayList<NetNode> nodes_to_iterate, int i, int total_iterations) {
        ArrayList<String> created_key = new ArrayList<>();
        int divisor = 1; // used to compute the index of the parent outcome
        for (NetNode parent : nodes_to_iterate) {
            divisor *= parent.nb_outcomes;
            int computed_index = (i / (total_iterations / divisor)) % parent.nb_outcomes;
            created_key.add(parent.outcome_list.get(computed_index)); // add the right parent outcome
        }
        return created_key;
    }

    /**
     * This function creates a factor from two factors
     * @param first the first factor
     * @param second the second factor
     */
    public Factor(Factor first, Factor second) {
        // get all the parameter of the new factor
        this.title = (ArrayList<NetNode>) first.title.clone();
        for (NetNode node : second.title) {
            if (!this.title.contains(node)) {
                this.title.add(node);
            }
        }
        this.data = new HashMap<>();
    }

    @Override
    public String toString() {
        String str = "";

        for (NetNode node : this.title) {
            str += node.name + ", ";
        }
        str += "\n";
        for (ArrayList<String> key : this.data.keySet()) {
            str += key.toString() + " : " + this.data.get(key) + "\n";
        }

        return str;
    }

    /**
     * This function updates the factor by deleting the given values
     * @param value the value to delete
     * @param node the node to delete
     */
    public void updateGiven(String value, NetNode node) {
        int index = 0;
        for (ArrayList<String> key : this.data.keySet().toArray(new ArrayList[0])) {
            float temp = this.data.get(key);
            this.data.remove(key);
            index = this.title.indexOf(node);
            if (key.get(index).equals(value)) {
                key.remove(index);
                this.data.put(key, temp);
            }
        }
        // removing the param from the title
        this.title.remove(node);
    }

    /**
     * This function reduces the factor by removing the node
     * @param node the node to remove
     * @return the number of times the node was found
     */
    public int reduce(NetNode node) {

        // creating new key
        int index = this.title.indexOf(node); // get the index of the node that we want to remove

        HashMap<ArrayList<String>, Float> new_data = new HashMap<>();
        ArrayList<String> temp_key;
        int total_sum = 0;
        // iterating aver all the rows of the factor and removing the collapsed parameter
        for (ArrayList<String> key : this.data.keySet().toArray(new ArrayList[0])) {
            float val = this.data.get(key);
            temp_key = (ArrayList<String>) key.clone();
            temp_key.remove(index);

            // if this is the second time we see this key we add the values to the previous one
            if (new_data.containsKey(temp_key)) {
                new_data.put(temp_key, new_data.get(temp_key) + val);
                total_sum += 1;
            } else { // otherwise we create a new key with the first met value
                new_data.put(temp_key, val);
            }
        }
        this.data = new_data;

        // removing the node from the title
        this.title.remove(node);
        return total_sum;
    }
}
