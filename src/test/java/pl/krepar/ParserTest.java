package pl.krepar;

import org.junit.Test;

import lombok.val;

import static pl.krepar.Parsers.*;
import static org.junit.Assert.*;

public class ParserTest {

    @Test
    public void testRegexp() {
        assertTrue(string("A").parse("A").isSuccessful());
        assertTrue(string("A").parse("AB").isSuccessful());
        assertFalse(string("A").parse("B").isSuccessful());
        assertFalse(string("A").parse("BA").isSuccessful());

        assertTrue(regexp("a+").parse("aaab").isSuccessful());
        assertTrue(regexp("a+").parse("aaab").match(
                (__) -> Boolean.FALSE,
                (__, r) -> Boolean.valueOf(r.getCharSequence().charAt(r.getOffset()) == 'b')));
    }

    @Test
    public void testThen() {
        assertTrue(string("A").then(string("B")).parse("AB").isSuccessful());
        assertFalse(string("A").then(string("B")).parse("AC").isSuccessful());
    }

    @Test
    public void testOr() {
        assertTrue(string("A").or(string("B")).parse("A").isSuccessful());
        assertTrue(string("A").or(string("B")).parse("B").isSuccessful());
        assertFalse(string("A").or(string("B")).parse("C").isSuccessful());
    }

    @Test
    public void testOptional() {
        assertTrue(string("A").optional().parse("AB").isSuccessful());
        assertTrue(string("A").optional().parse("B").isSuccessful());
    }

    @Test
    public void testRepeat() {
        assertTrue(string("A").repeat().parse("AAB").match(
                (__) -> Boolean.FALSE,
                (__, r) -> Boolean.valueOf(r.getCharSequence().charAt(r.getOffset()) == 'B')));
        assertTrue(string("A").repeat().parse("B").isSuccessful());

    }

    @Test
    public void testMatchSimple() {
        assertTrue(regexp("a+b").parse("aabb").getValue().get().group().equals("aab"));
    }

    @Test
    public void testMatchPair() {
        assertEquals("AB",
                string("A").then(string("B")).parse("AB").getValue()
                .map(match((a, b) -> a + b)).get());
    }

    @Test
    public void testMatchOptionalPair() {
        assertEquals("AB",
                string("A").optional().then(string("B")).parse("AB").getValue()
                .map(match((o, b) -> o.get() + b)).get());

        assertFalse(string("A").optional().parse("B").getValue().get().isPresent());
    }

    @Test
    public void testHide() {
        assertEquals("B",
                string("A").hide().then(string("B")).parse("AB").getValue()
                .get());

        assertEquals("A",string("A").then(string("B").hide()).parse("AB").getValue().get());
    }

    @Test
    public void testMatchList() {
        val res = string("A").hide().then(string("B").or(string("b")).repeat().then(string("C").hide())).parse("ABbbC").getValue().get();

        assertEquals(3, res.size());
        assertEquals("B", res.get(0));
        assertEquals("b", res.get(1));
        assertEquals("b", res.get(2));
    }
}
