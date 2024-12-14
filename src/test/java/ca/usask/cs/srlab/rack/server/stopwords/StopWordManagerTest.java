package ca.usask.cs.srlab.rack.server.stopwords;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StopWordManagerTest {

    StopWordManager manager;
    @BeforeEach
    public void setup(){
        this.manager=new StopWordManager();
    }

    @Test
    public void testGetRefinedSentence(){
        String sentence="Overall, the revised version satisfactorily addresses most of my initial concerns. I understand that the authors had to remove certain parts";
        System.out.println(manager.getRefinedSentence(sentence));
    }

}
