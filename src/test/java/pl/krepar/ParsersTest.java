package pl.krepar;

import static pl.krepar.Parsers.*;
import static pl.krepar.ParseContext.parseFirst;
import static org.junit.Assert.*;

import org.junit.Test;

public class ParsersTest {

    @Test
    public void testChoice() {
        assertTrue(parseFirst(choice(string("A"), string("B")), "A").isSuccessful());
        assertTrue(parseFirst(choice(string("A"), string("B")), "B").isSuccessful());

        assertTrue(parseFirst(choice(string("A"), string("B"), string("C")), "A").isSuccessful());
        assertTrue(parseFirst(choice(string("A"), string("B"), string("C")), "B").isSuccessful());
        assertTrue(parseFirst(choice(string("A"), string("B"), string("C")), "C").isSuccessful());
    }
}
