import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Tests {

    @Test
    public void testAll() {

        // For each file name in the list, run the program with the input file and compare the output with the expected output
        String[] files = {
                "BayesballClassExample.txt",
                "BayesballExample.txt",
                "CausalChain.txt",
                "CommonCause.txt",
                "CommonEffect.txt",
                "Example1.txt",
                "Example2.txt",
                "ExerciseExample.txt",
                "MyExample.txt",
                "UniversityExample.txt"
        };

        for (String file : files) {
            String input_path = "input/" + file;
            String output_path = "output/" + file;
            String my_output_path = "my_output/" + file;

            Main.main(new String[]{input_path, my_output_path});

            // Compare the output of the program with the expected output (many lines)
//            String expected_output = Main.read_input(output_path, 1);
//            String my_output = Main.read_input(my_output_path, 1);
            try (BufferedReader reader1 = new BufferedReader(new FileReader(output_path));
                 BufferedReader reader2 = new BufferedReader(new FileReader(my_output_path))) {
                String line1;
                String line2;

                int i = 2;
                while ((line1 = reader1.readLine()) != null && (line2 = reader2.readLine()) != null) {
                        System.out.println("Line " + i++);
                        assertEquals(line1, line2);
                    }
                } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
