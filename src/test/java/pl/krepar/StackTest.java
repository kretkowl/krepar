package pl.krepar;

import org.junit.Test;

public class StackTest {

    @Test
    public void testVeryLongString() {
        StringBuilder sb = new StringBuilder("aa");
        for (int i=0; i<2; i++)
            sb.append(sb.toString());

        ParseContext.parse(Parsers.string("a").repeat(), sb.toString());
    }
}
