import java.util.Comparator;

public class FactorComparator implements Comparator<Factor> {

//    @Override
//    public int compare(Factor o1, Factor o2) {
//        // comparing the title of the factors by their ASCII sum value
//        int sum1 = 0;
//        int sum2 = 0;
//        for (NetNode node : o1.title) {
//            sum1 += node.name.chars().sum();
//        }
//        for (NetNode node : o2.title) {
//            sum2 += node.name.chars().sum();
//        }
//        return sum1 - sum2;
//    }

    @Override
    public int compare(Factor o1, Factor o2) {
        // comparing the title of the factors by their Size sum value
        return o1.data.size() - o2.data.size();
    }
}
