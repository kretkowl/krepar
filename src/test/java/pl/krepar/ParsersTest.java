package pl.krepar;

import static pl.krepar.Parsers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class ParsersTest {

    @Test
    public void testChoice() {
        assertTrue(choice(string("A"), string("B")).parse("A").isSuccessful());
        assertTrue(choice(string("A"), string("B")).parse("B").isSuccessful());

        assertTrue(choice(string("A"), string("B"), string("C")).parse("A").isSuccessful());
        assertTrue(choice(string("A"), string("B"), string("C")).parse("B").isSuccessful());
        assertTrue(choice(string("A"), string("B"), string("C")).parse("C").isSuccessful());
    }
}
