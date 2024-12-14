package ca.usask.cs.srlab.rack.server.similarity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CosineSimilarityMeasureTest {

    String firstText;
    String secondText;
    @BeforeEach
    public void setup(){
        firstText="Overall, the revised version satisfactorily addresses most of my initial concerns. I understand that the authors had to remove certain parts";
        secondText="Consider an instance of org.eclipse.search.ui.text.Match with an element that is neither an IResource nor an IJavaElement. It might be an element in a class diagram, for example.";
    }

    @Test
    public void testGetCosineSimilarityScores(){
        CosineSimilarityMeasure cosine=new CosineSimilarityMeasure(firstText, secondText);
        System.out.println(cosine.getCosineSimilarityScore(true));
    }
}
