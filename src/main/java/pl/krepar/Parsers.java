package pl.krepar;

import static pl.krepar.ParseResult.success;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Parsers {

    /**
     * Matches empty string -> essentially an always success parser.
     * @return
     */
    public static Parser<?> empty() {
        return empty(null);
    }

    /**
     * Matches empty string -> essentially an always success parser.
     * Allows to give success value.
     *
     * @return
     */
    public static <A> Parser<A> empty(A value) {
        return (in, __) -> success(value, in);
    }

    /**
     * Always-fail parser. Matches no input.
     * @param msg
     * @return
     */
    public static <B> Parser<B> failure(String msg) {
        return (in, __) -> ParseResult.failure(msg, in.getOffset());
    }

    /**
     * Creates alternative of parsers as single OrParser
     * @param parser
     * @param parsers
     * @return
     */
    @SafeVarargs
    public static <B> OrParser<B> choice(Parser<B>... parsers) {
        return new OrParser<B>(parsers);
    }

    /**
     * Start of string matcher
     * @return
     */
    public static Parser<?> begin() { return regexp("^"); }

    /**
     * End of string matcher
     * @return
     */
    public static Parser<?> end() { return regexp("$"); }

    /**
     * Constant string parser
     * @param string
     * @return
     */
    public static Parser<String> string(String string) {
        return regexp(Pattern.quote(string)).map(MatchResult::group);
    }

    /**
     * Regular expression parser based on string definition.
     * @param regex
     * @return
     */
    public static Parser<MatchResult> regexp(String regex) {
        return regexp(Pattern.compile(regex));
    }

    /**
     * REgular expression parser based on pattern
     * @param pattern
     * @return
     */
    public static Parser<MatchResult> regexp(Pattern pattern) {
        return new RegexParser(pattern);
    }



    // Functions to match on left-nested pairs, like those generated by a chain of .then(...) calls

    /** Match a single value, like the result of string("A") - note that match is mostly useful when dealing with multiple values. */
    public static <A, R> Function<A, R> match(Function<A, R> f) {
        return f;
    }

    /** Match two values, like the result of string("A").then(string("B")) */
    public static <A, B, R> Function<Pair<A, B>, R> match(BiFunction<A, B, R> f) {
        return p -> f.apply(p.first, p.second);
    }

    /** Match three values, like the result of string("A").then(string("B")).then(string("C")) */
    public static <A, B, C, R> Function<Pair<Pair<A, B>, C>, R> match(Function3<A, B, C, R> f) {
        return p -> f.apply(p.first.first, p.first.second, p.second);
    }

    /** Match four values, like the result of string("A").then(string("B")).then(string("C")).then(string("D")) */
    public static <A, B, C, D, R> Function<Pair<Pair<Pair<A, B>, C>, D>, R> match(Function4<A, B, C, D, R> f) {
        return p -> f.apply(p.first.first.first, p.first.first.second, p.first.second, p.second);
    }

    /** Match five values, like the result of string("A").then(string("B")).then(string("C")).then(string("D")).then(string("E")) */
    public static <A, B, C, D, E, R> Function<Pair<Pair<Pair<Pair<A, B>, C>, D>, E>, R> match(Function5<A, B, C, D, E, R> f) {
        return p -> f.apply(p.first.first.first.first, p.first.first.first.second, p.first.first.second, p.first.second, p.second);
    }

    /** Match six values, like the result of string("A").then(string("B")).then(string("C"))...then(string("F")) */
    public static <A, B, C, D, E, F, R> Function<Pair<Pair<Pair<Pair<Pair<A, B>, C>, D>, E>, F>, R> match(Function6<A, B, C, D, E, F, R> f) {
        return p -> f.apply(p.first.first.first.first.first, p.first.first.first.first.second, p.first.first.first.second, p.first.first.second, p.first.second, p.second);
    }

    /** Match seven values, like the result of string("A").then(string("B")).then(string("C"))...then(string("G")) */
    public static <A, B, C, D, E, F, G, R> Function<Pair<Pair<Pair<Pair<Pair<Pair<A, B>, C>, D>, E>, F>, G>, R> match(Function7<A, B, C, D, E, F, G, R> f) {
        return p -> f.apply(p.first.first.first.first.first.first, p.first.first.first.first.first.second, p.first.first.first.first.second, p.first.first.first.second, p.first.first.second, p.first.second, p.second);
    }

    /** Match eight values, like the result of string("A").then(string("B")).then(string("C"))...then(string("H")) */
    public static <A, B, C, D, E, F, G, H, R> Function<Pair<Pair<Pair<Pair<Pair<Pair<Pair<A, B>, C>, D>, E>, F>, G>, H>, R> match(Function8<A, B, C, D, E, F, G, H, R> f) {
        return p -> f.apply(p.first.first.first.first.first.first.first, p.first.first.first.first.first.first.second, p.first.first.first.first.first.second, p.first.first.first.first.second, p.first.first.first.second, p.first.first.second, p.first.second, p.second);
    }

    /** Match nine values, like the result of string("A").then(string("B")).then(string("C"))...then(string("I")) */
    public static <A, B, C, D, E, F, G, H, I, R> Function<Pair<Pair<Pair<Pair<Pair<Pair<Pair<Pair<A, B>, C>, D>, E>, F>, G>, H>, I>, R> match(Function9<A, B, C, D, E, F, G, H, I, R> f) {
        return p -> f.apply(p.first.first.first.first.first.first.first.first, p.first.first.first.first.first.first.first.second, p.first.first.first.first.first.first.second, p.first.first.first.first.first.second, p.first.first.first.first.second, p.first.first.first.second, p.first.first.second, p.first.second, p.second);
    }

    @FunctionalInterface public interface Function3<A, B, C, R> { public R apply(A a, B b, C c); }
    @FunctionalInterface public interface Function4<A, B, C, D, R> { public R apply(A a, B b, C c, D d); }
    @FunctionalInterface public interface Function5<A, B, C, D, E, R> { public R apply(A a, B b, C c, D d, E e); }
    @FunctionalInterface public interface Function6<A, B, C, D, E, F, R> { public R apply(A a, B b, C c, D d, E e, F f); }
    @FunctionalInterface public interface Function7<A, B, C, D, E, F, G, R> { public R apply(A a, B b, C c, D d, E e, F f, G g); }
    @FunctionalInterface public interface Function8<A, B, C, D, E, F, G, H, R> { public R apply(A a, B b, C c, D d, E e, F f, G g, H h); }
    @FunctionalInterface public interface Function9<A, B, C, D, E, F, G, H, I, R> { public R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i); }

}