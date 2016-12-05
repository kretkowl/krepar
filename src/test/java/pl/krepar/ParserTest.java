package pl.krepar;

import org.junit.Test;

import lombok.val;

import static pl.krepar.Parsers.*;
import static pl.krepar.ParseContext.*;

import static org.junit.Assert.*;

public class ParserTest {

    @Test
    public void testRegexp() {
        assertTrue(parseFirst(string("A"), "A").isSuccessful());
        assertTrue(parseFirst(string("A"), "AB").isSuccessful());
        assertFalse(parseFirst(string("A"), "B").isSuccessful());
        assertFalse(parseFirst(string("A"), "BA").isSuccessful());

        assertTrue(parseFirst(regexp("a+"), "aaab").isSuccessful());
        assertTrue(parseFirst(regexp("a+"), "aaab").match(
                (fail) -> Boolean.FALSE,
                (__, r) -> Boolean.valueOf(r.getCharSequence().charAt(r.getOffset()) == 'b')));
    }

    @Test
    public void testString() {
        assertTrue(parseFirst(string("A"), "A").isSuccessful());
        assertTrue(parseFirst(string("A"), "AB").isSuccessful());
        assertFalse(parseFirst(string("A"), "B").isSuccessful());
        assertFalse(parseFirst(string("A"), "BA").isSuccessful());
    }

    @Test
    public void testThen() {
        assertTrue(parseFirst(string("A").then(string("B")), "AB").isSuccessful());
        assertFalse(parseFirst(string("A").then(string("B")), "AC").isSuccessful());
    }

    @Test
    public void testThenWithRest() {
        assertTrue(parseFirst(string("A").then(string("B")), "ABC").isSuccessful());
        assertTrue(parseFirst(string("A").then(string("B")), "ABC").match(
                (fail) -> Boolean.FALSE,
                (__, rest) -> Boolean.valueOf(rest.getCharSequence().charAt(rest.getOffset()) == 'C')
                ));
    }

    @Test
    public void testOr() {
        assertTrue(parseFirst(string("A").or(string("B")), "AC").isSuccessful());
        assertTrue(parseFirst(string("A").or(string("B")), "B").isSuccessful());
        assertFalse(parseFirst(string("A").or(string("B")), "C").isSuccessful());
    }

    @Test
    public void testOptional() {
        assertTrue(parseFirst(string("A").optional(), "AB").isSuccessful());
        assertTrue(parseFirst(string("A").optional(), "B").isSuccessful());
    }

    @Test
    public void testRecursive() {
        val refAs = new Ref<Parser<String, ?>>();

        val as = string("a").or(delay(refAs).then(string("a").hide())).setRef(refAs);

        assertTrue(parseFirst(as, "a").isSuccessful());
        assertTrue(parseFirst(as, "aa").isSuccessful());
    }

    @Test
    public void testRecursiveWithEmpty() {
        val refAs = new Ref<Parser<String, ?>>();

        val as = empty("a").or(delay(refAs).then(string("a").hide())).setRef(refAs);

        assertTrue(parseFirst(as, "").isSuccessful());
        assertTrue(parseFirst(as, "a").isSuccessful());
        assertTrue(parseFirst(as, "aa").isSuccessful());
        assertTrue(parseFirst(as, "aab").isSuccessful());
    }

    @Test
    public void testRepeat() {
        val rr = parse(string("A").repeat(), "AAB");
        assertEquals(3, rr.size());
        assertTrue(
                rr.stream().anyMatch(
                e -> e.match(
                (__) -> Boolean.FALSE,
                (a, r) -> {
                    System.out.println(a);
                    System.out.println(r);
                    return r.getOffset() == 0;
                })));

        assertTrue(
                rr.stream().anyMatch(
                e -> e.match(
                (__) -> Boolean.FALSE,
                (a, r) -> {
                    System.out.println(a);
                    System.out.println(r);
                    return r.getOffset() == 1;
                })));

        assertTrue(
                rr.stream().anyMatch(
                e -> e.match(
                (__) -> Boolean.FALSE,
                (a, r) -> {
                    System.out.println(a);
                    System.out.println(r);
                    return r.getOffset() == 2;
                })));


        assertTrue(parseFirst(string("A").repeat(), "B").isSuccessful());
    }

    @Test
    public void testMatchSimple() {
        assertTrue(parseFirst(regexp("a+b"), "aabb").getValue().get().group().equals("aab"));
    }

    @Test
    public void testMatchPair() {
        assertEquals("AB",
                parseFirst(string("A").then(string("B")), "AB").getValue()
                .map(match((a, b) -> a + b)).get());
    }

    @Test
    public void testMatchOptionalPair() {
        assertEquals("AB",
                parseFirst(string("A").optional().then(string("B")), "AB").getValue()
                .map(match((o, b) -> o.get() + b)).get());

        assertFalse(parseFirst(string("A").optional(), "B").getValue().get().isPresent());
    }

    @Test
    public void testHide() {
        assertEquals("B",
                parseFirst(string("A").hide().then(string("B")), "AB").getValue()
                .get());

        assertEquals("A", parseFirst(string("A").then(string("B").hide()), "AB").getValue().get());
    }

    @Test
    public void testMatchList() {
        val ress =
                parse(string("A").hide().then(string("B").or(string("b")).repeat().then(string("C").hide())), "ABbbC");

        val res = ress.stream().sorted((a,b) -> a.getValue().get().size() - b.getValue().get().size()).findFirst().get().getValue().get();

        System.out.println(res.size());
        assertEquals(3, res.size());
        assertEquals("B", res.get(0));
        assertEquals("b", res.get(1));
        assertEquals("b", res.get(2));
    }
}
