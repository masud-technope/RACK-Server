package ca.usask.cs.srlab.rack.server.utility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class ItemSorterTest {

    HashMap<String, Double> realValueMap;
    HashMap<String, Integer> numberMap;

    @BeforeEach
    public void setup() {
        realValueMap = new HashMap<>();
        realValueMap.put("A", .5);
        realValueMap.put("C", .02);
        realValueMap.put("B", 1.0);

        numberMap = new HashMap<>();
        numberMap.put("A", 9);
        numberMap.put("C", 2);
        numberMap.put("B", 10);
    }

    @Test
    public void sortHashMapDouble(){
        System.out.println(ItemSorter.sortHashMapDouble(realValueMap));
    }

    @Test
    public void sortHashMapInt(){
        System.out.println(ItemSorter.sortHashMapInt(numberMap));
    }
}
