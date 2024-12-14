package ca.usask.cs.srlab.rack.server.text.normalizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TextNormalizerTest {

    String simpleText;
    String codeText;
    @BeforeEach
    public void setup(){
        simpleText="Overall, the revised version satisfactorily addresses most of my initial concerns. I understand that the authors had to remove certain parts";
        codeText="Consider an instance of org.eclipse.search.ui.text.Match with an element that is neither an IResource nor an IJavaElement. It might be an element in a class diagram, for example.";
    }

    @Test
    public void testNormalizeSimple(){
        TextNormalizer normalizer=new TextNormalizer(this.simpleText);
        System.out.println(normalizer.normalizeSimple());
    }


    @Test
    public void testNormalizeSimpleCodeDiscardSmall(){
        TextNormalizer normalizer=new TextNormalizer(this.codeText);
        System.out.println(normalizer.normalizeSimpleCodeDiscardSmall());
    }

}
