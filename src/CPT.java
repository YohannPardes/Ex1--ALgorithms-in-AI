import java.util.Arrays;

public class CPT {
    Float[] values;
    Float[] computed_values;

    /**
     * Given a string of the format "xxx xxx xx x xxx"
     * Build the right CPT
     */
    public void extracting_values_from_String(String str){
        String[] STRlist = str.split(" ");
        this.values = new Float[STRlist.length];
        this.computed_values = new Float[STRlist.length];
        for (int i = 0; i < STRlist.length; i++) {
            this.values[i] = Float.parseFloat(STRlist[i]);
            this.computed_values[i] = Float.parseFloat(STRlist[i]);
        }
    }

    @Override
    public String toString(){
        System.out.println(Arrays.toString(this.values));
        return "";
    }
}
