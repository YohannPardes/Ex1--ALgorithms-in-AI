import java.io.*;
public class Main {
    public static void main(String[] args) {
        String input_path = "input.txt";
        String output_path = "my_output.txt";

        if (args.length == 2) {
            input_path = args[0];
            output_path = args[1];
        }
        BayesBall myBayesBall = new BayesBall();
        VariableElimination myVariableElimination= new VariableElimination();

        // Clearing the output file
        clearFile(output_path);

        String xml_path = read_input(input_path, 1);

        Network My_network = new Network();
        XMLParsing myParser = new XMLParsing();

        // Extracting all the data and creating the graph with the CPT values
        myParser.extract_data(xml_path, My_network);
//        My_network.Print_Network();

        // Processing the queries
        String query = " ";
        int i = 2;
        String output;
        boolean bayes_ball_output;
        while (read_input(input_path, i) != null) {
            // Extracting queries from files
            query = read_input(input_path, i);
            System.out.println("current query: " + query);

            if (query.substring(0, 2).equals("P(")) {
                // Applying variable elimination
                float result = myVariableElimination.Call(My_network, query);
                String StrResult = Float.toString(result) + "0000000";
                output = StrResult.substring(0, 7) + "," + myVariableElimination.total_sum + "," + myVariableElimination.total_mult;
            } else {
                // Applying Bayse's ball algorithm
                bayes_ball_output = myBayesBall.Call(My_network, query);
                if (bayes_ball_output) {
                    System.out.println("the variables are dependent");
                    output = "no";
                } else {
                    System.out.println("the variables are independent");
                    output = "yes";
                }
            }

            My_network.HardReset(myVariableElimination, myBayesBall);
            writeToFile(output_path, output);
            i++; // incrementing to the next query
        }
    }

    public static String read_input(String path, int index) {
        // File path is passed as a parameter to the FileReader
        try (FileReader fileReader = new FileReader(path);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line = null;
            int i = 0;
            // Read file line by line
            while (i < index) {
                line = bufferedReader.readLine();
                i ++;
            }

            return line;
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO error occurred: " + e.getMessage());
        }
        return null;
    }

    public static void writeToFile(String filename, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.append(content);
            writer.newLine();
        }
        catch (IOException e) {
            System.err.println("An error occurred");
        }
    }

    public static void clearFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("");
        }
        catch (IOException e) {
            System.err.println("An error occurred");
        }
    }
}