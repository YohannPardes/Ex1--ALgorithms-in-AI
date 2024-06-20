import java.util.*;

public class VariableElimination {

    ArrayList<NetNode> RelevantNodes = new ArrayList<>(); // the nodes that are relevant to the query
    ArrayList<NetNode> GivenNodes = new ArrayList<>();
    ArrayList<String> GivenVals = new ArrayList<>();
    ArrayList<NetNode> HiddenNodes = new ArrayList<>();
    NetNode QueryNode;
    String QueryVal;
    int total_sum = 0;
    int total_mult = 0;

    public Float Call(Network network, String query) {
        // Extracting the variables
        ProcessQueryString(network, query);

        // updating the node of their given state
        network.given_update(query);

        // First step - getting rid of the non-parent of query or evidence nodes
        this.eliminate_useless_nodes(network);

        // Second step - Keeping only relevant nodes using bayes ball
        bayesBallElimination(network);

        // Third step - Actually eliminating the variables
        Factor result = this.eliminate_variables(network);
        System.out.println("Result : " + result);



        // creating a key to get the result from the hash map
        ArrayList<String> key = new ArrayList<>();
        key.add(this.QueryVal);


        return result.data.get(key);
    }

    private void bayesBallElimination(Network network) {
        BayesBall my_algo = new BayesBall();
        for (int i = 0; i < this.RelevantNodes.size(); ) {
            NetNode node = this.RelevantNodes.get(i);
            System.out.println("Bayes ball");
            if (!my_algo.BayesBallRecursive(node, this.QueryNode, node, true, true)) {
                this.RelevantNodes.remove(node);
            } else {
                i++;
            }
            network.ResetNetwork();
        }
        System.out.println("The relevant nodes are :" + this.RelevantNodes);
    }

    private void Normalizing_factor(Factor result) {
//        System.out.println("Normalizing the factor :" + result);
        float sum = 0;
        this.total_sum += result.data.size() -1;
        for (ArrayList<String> key : result.data.keySet()) {
            sum += result.data.get(key);
        }
//        System.out.println(this.total_sum + "sums");
        for (ArrayList<String> key : result.data.keySet()) {
            result.data.put(key, result.data.get(key) / sum);
        }
    }

    private Factor eliminate_variables(Network network) {

        // First step - reducing the variables by evidences
        this.given_reduction();

        this.total_sum = 0;
        this.total_mult = 0;
        int total = 0;
        for (NetNode node : this.RelevantNodes){
            if (node.factor.title.contains(this.QueryNode)){
                total ++;
            }
        }
        if (total == 1 && this.QueryNode.factor.data.size() == 2) {
            System.out.println("The query is already computed");
            return this.QueryNode.factor;
        }

        // Second step - reducing the variables by hidden variables
        Factor result = this.hidden_reduction();

        // Third step returning the Normalized output
        Normalizing_factor(result);
        return result;

    }

    private Factor hidden_reduction() {
        // Get all the nodes cpt variables
        ArrayList<Factor> factors = this.get_relevant_factors(); // the list containing all the factors that are relevant to the query
        // print all factors titles
        System.out.println("Factors : ");
        for (Factor f : factors) {
            System.out.println("Factor : " + f);
        }
        ArrayList<Factor> temp_factors; // the list containing all the factors that contains the wanted parameter
        for (NetNode node : this.HiddenNodes) { // for each hidden node
//            if (this.RelevantNodes.contains(node)) { // if the hidden node his still relevant to the query
            System.out.println("eliminating name : " + node.name);
            // Get all the Factors containing the node name
            temp_factors = this.get_relevant_factors(node, factors);
            System.out.println("Current factors" + temp_factors);
            // sort the factors by their size
            Comparator<Factor> comp = new FactorComparator();
            temp_factors.sort(comp);
            // Join the factors
            Factor last_factor = this.join(temp_factors, node, factors);
            // Reduce the factor
            System.out.println("refactoring :" + last_factor);
            System.out.println(total_sum + "sums before reducing " + last_factor);
            if (last_factor != null) {
                if (last_factor.title.size() != 1) {
                    this.total_sum += last_factor.reduce(node);
                } else {
                    factors.remove(last_factor);
                }
            }
            System.out.println(total_sum + "sums after reducing " + last_factor);
        }

        this.join(factors, this.QueryNode, factors);

//        System.out.println("there is " + factors.size()+ " factors");
        for (Factor f : factors) {
            System.out.println("Final Factor : " + f);
        }
        return factors.get(0); // return the last factor containing the query variable
    }

    private Factor join(ArrayList<Factor> factors, NetNode node, ArrayList<Factor> all_factors) {

        if (factors.isEmpty()){
            return null;
        }
        if (factors.size() == 1) {
            if (!all_factors.contains(factors.get(0))){
                all_factors.add(factors.get(0));
            }
            return factors.get(0);
        }
        // get the first and second factors
        Factor first = factors.get(0);
        Factor second = factors.get(1);

        //removing those from the factors list
        all_factors.remove(first);
        all_factors.remove(second);

        // swap them by size so that the smaller is first
        if (first.data.size() > second.data.size()) {
            Factor temp = first;
            first = second;
            second = temp;
        }

        System.out.println("First factor : " + first);
        System.out.println("Second factor : " + second);

        // get the corresponding values of parameter 1 at the second factor
        ArrayList<Integer> matching_values = new ArrayList<>();
        for (NetNode param : first.title) {
            matching_values.add(second.title.indexOf(param));
        }
        System.out.println(matching_values);

        Factor newFactor = new Factor(first, second);
        // iterating over all the values of the first factor
        for (ArrayList<String> key : first.data.keySet()) {
            // Find the matching rows of the second factor
            for (ArrayList<String> key2 : second.data.keySet()) {
                boolean match = true;
                for (int i = 0; i < matching_values.size(); i++) {
                    if (matching_values.get(i) == -1) {
                        continue;
                    }
                    String value1 = key.get(i); // The value of the joined parameter in the first factor
                    String value2 = key2.get(matching_values.get(i)); // The value of the joined parameter in the second factor

                    if (!value1.equals(value2)) {// if the rows are matching we can multiply the values into the new factor
                        match = false;
                        break;
                    }
                }

                if (match){ // if the whole row matches
                    ArrayList<String> new_key = create_new_key(key, key2, first, second, newFactor);
                    newFactor.data.put(new_key, first.data.get(key) * second.data.get(key2));
                    this.total_mult++;
                }
            }
        }
        // New factor list with the new one instead of the two old ones
        ArrayList<Factor> new_factors = new ArrayList<>();
        new_factors.add(newFactor);
        for (int i = 2; i < factors.size(); i++) {
            new_factors.add(factors.get(i));
        }
        System.out.println("New factor : " + newFactor);
        return join(new_factors, node, all_factors);
    }

    private ArrayList<Factor> get_relevant_factors() {
        ArrayList<Factor> relevant_factors = new ArrayList<>();
        for (NetNode n : this.RelevantNodes) {
            if (n.factor.data.size() > 1) {
                relevant_factors.add(n.factor);
            }
        }
            return relevant_factors;
    }

    private ArrayList<Factor> get_relevant_factors(NetNode node, ArrayList<Factor> factors) {
        ArrayList<Factor> relevant_factors = new ArrayList<>();
        for (Factor f : factors) {
            if (f.title.contains(node)) {
                relevant_factors.add(f);
            }
        }
        return relevant_factors;
    }

    /**
     * Iterate over all the nodes and reduce their given values
     */
    private void given_reduction() {

        for (NetNode node : this.RelevantNodes) {
            ArrayList<NetNode> temp = (ArrayList<NetNode>) node.factor.title.clone();
            for (NetNode factorParam : temp) {
                if (this.GivenNodes.contains(factorParam) && node.factor.title.contains(factorParam)) {
                    node.collapse_given(factorParam, factorParam.Given_outcome);
                }
            }
        }
    }

    private void ProcessQueryString(Network network, String query) {
        String paramString = query.split("\\(")[1];
        String HiddenVar = paramString.split("\\)")[1];
        paramString = paramString.split("\\)")[0];
        String GivenVar = "";
        if (paramString.split("\\|").length == 2){
            GivenVar = paramString.split("\\|")[1];
        }
        paramString = paramString.split("\\|")[0];
        String QueryVar = paramString.split("=")[0];
        String QueryVal = paramString.split("=")[1];

        System.out.println("QueryVal - " + QueryVal);
        System.out.println("HiddenVar - " + HiddenVar);
        System.out.println("QueryVar - " + QueryVar);
        System.out.println("GivenVar - " + GivenVar);

        this.QueryNode = network.find_node(QueryVar);
        this.QueryVal = QueryVal;


        for (String hidden : HiddenVar.split("-")) {
            this.HiddenNodes.add(network.find_node(hidden.trim()));
        }

        for (String given : GivenVar.split(",")) {
            if (given.equals("")) {
                break;
            }
            this.GivenNodes.add(network.find_node(given.split("=")[0]));
            this.GivenVals.add(given.split("=")[1]);
        }
        for (NetNode node : this.GivenNodes) {
            System.out.println("Given node : " + node.name);
        }

        for (NetNode node : this.HiddenNodes) {
            System.out.println("Hidden node : " + node.name);
        }


    }

    /**
     * Eliminate the nodes that are not a parent of the query node or a given node
     *
     * @param network
     */
    private void eliminate_useless_nodes(Network network) {
        for (NetNode node : this.GivenNodes) {
            eliminate_useless_nodes_recursive(node);
        }
        eliminate_useless_nodes_recursive(this.QueryNode);
    }

    private void eliminate_useless_nodes_recursive(NetNode node) {
        if (!this.RelevantNodes.contains(node)) {
            this.RelevantNodes.add(node);
        }

        for (NetNode parent : node.Parents) {
            eliminate_useless_nodes_recursive(parent);
        }
    }

    private ArrayList<String> create_new_key(ArrayList<String> key, ArrayList<String> key2, Factor first, Factor second, Factor newFactor) {
        ArrayList<String> new_key = new ArrayList<>();
        // iterating aver all the parameters of the new factor
        for (NetNode param : newFactor.title) {
            if (first.title.contains(param)) {
                new_key.add(key.get(first.title.indexOf(param))); // add the value for the parameter
            } else {
                new_key.add(key2.get(second.title.indexOf(param)));
            }
        }
        return new_key;
    }

    public void reset() {
        // reset all the variables between queries
        this.RelevantNodes = new ArrayList<>();
        this.HiddenNodes = new ArrayList<>();
        this.GivenNodes = new ArrayList<>();
        this.GivenVals = new ArrayList<>();
        this.QueryNode = null;
        this.QueryVal = null;
    }
}

