import java.util.ArrayList;
import java.util.HashMap;

public class Factor {

    HashMap<ArrayList<String>, Float> data = new HashMap<>();
    ArrayList<NetNode> title;

    /**
     * Given a node create the factor data
     *
     * @param node
     */
    Factor(NetNode node) {
        this.title = (ArrayList<NetNode>) node.Parents.clone();
        this.title.add(node);

        int total_iterations = 1;
        for (NetNode parent : node.Parents) {
            total_iterations *= parent.nb_outcomes;
        }
        total_iterations *= node.nb_outcomes;

        ArrayList<NetNode> nodes_to_iterate = (ArrayList<NetNode>) node.Parents.clone();
        nodes_to_iterate.add(node);
        for (int i = 0; i < total_iterations; i++) { // Iterate over all the possible
            ArrayList<String> current_values = new ArrayList<>();
            int divisor = 1;
            for (NetNode parent : nodes_to_iterate) {
                divisor *= parent.nb_outcomes;
                int computed_index = (i / (total_iterations / divisor)) % parent.nb_outcomes;
                current_values.add(parent.outcome_list.get(computed_index)); // add the right parent outcome
            }
            // add the node value
            this.data.put(current_values, node.CPT.values[i]);
        }
    }

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
     * This function updates the factor by deletting the given values
     *
     * @param value
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

    public int reduce(NetNode node) {

        // creating new key
        ArrayList<String> new_key = new ArrayList<>();
        int index = this.title.indexOf(node);
        HashMap<ArrayList<String>, Float> new_data = new HashMap<>();
        ArrayList<String> temp_key = new ArrayList<>();
        int total_sum = 0;
        // iterating aver all the rows of the factor and removing the collapsed parameter
        for (ArrayList<String> key : this.data.keySet().toArray(new ArrayList[0])) {
            float val = this.data.get(key);
            temp_key = (ArrayList<String>) key.clone();
            temp_key.remove(index);

            // if this is the second time we see this key we add the values to the previous one
            if (new_data.containsKey(temp_key)) {
                new_data.put(temp_key, new_data.get(temp_key) + val);
                total_sum += val;
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
