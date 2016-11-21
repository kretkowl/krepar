package pl.krepar;

import static pl.krepar.ParseResult.success;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Main interface for each stnadard parser. Defines fluent api.
 *
 * @author kretkowl
 *
 * @param <A> Value type returned from parser
 */
public interface Parser<A> extends BasicParser<A> {

    /**
     * Parses given string. String is wrapped in {@link Segment} object, to avoid duplication during parsing.
     * @param in String to parse
     * @return result value or failure
     */
    public default ParseResult<? extends A>parse(String in) {
        return parse(new Input(new Segment(in), 0));
    }


    /**
     * Joins given parser as alternative to this parser.
     * @param that parser to join
     * @return complex parser
     */
    public default OrParser<A> or(Parser<A> that) {
        return new OrParser<>(this, that);
    }

    /**
     * Joins given parser as alternative to this parser. Parser is supplied as lambda to allow lazy resolution.
     * @param that parser to join
     * @return complex parser
     */

    public default OrParser<A> or(Supplier<Parser<A>> that) {
        return or(new DelayParser<>(that));
    }

    /**
     * Creates complex parser as concatenation of this parser and parser with hidden value (i.e. one that
     * value is ignored). Because of hidden value, return type is the same as this parser.
     * @param that parser to join
     * @return complex parser
     */
    public default Parser<A> then(HideParser that) {
        return then((BasicParser<?>)that).map((p) -> p.getFirst());
    }

    /**
     * Creates complex parser as concatenation of this and another parser.
     * Value returned from resulting parser is orderer pair of values from elementary parsers.
     * @param that parser to join
     * @return complex parser
     */
    public default <B> Parser<Pair<A, B>> then(BasicParser<B> that) {
        return new ConcatParser<>(this, that);
    }

    /**
     * Creates complex parser as concatenation of this and another parser.
     * Value returned from resulting parser is orderer pair of values from elementary parsers.
     *
     * Lazy variant.
     *
     * @param that parser to join enclosed in Supplier interface
     * @return complex parser
     */
    public default <B> Parser<Pair<A, B>> then(Supplier<Parser<B>> that) {
        return then(new DelayParser<>(that));
    }

    /**
     * Creates modified version of this parser that matches also empty string.
     * It is equivalent to <code>this.or(Parsers.empty())</code> but has nicer
     * return type
     *
     * @return optional, empty when empty string matched
     */
    public default Parser<Optional<A>> optional() {
        return new OrParser<>(this, Parsers.empty((A) null)).map(Optional::ofNullable);
    }

    /**
     * Creates complex parser, that matches 0 or more repetition of this parser.
     *
     * @return parser that returns list of values from this parser
     */
    public default Parser<List<A>> repeat() {
        return
                new Parser<List<A>>() {

                    @Override
                    public ParseResult<? extends List<A>> parse(Input in) {
                        Input[] r = new Input[] { in };
                        List<A> result = new ArrayList<A>();
                        do {

                        } while (Parser.this.parse(r[0]).match(
                                    (f) -> Boolean.FALSE,
                                    (val, rest) -> {
                                        result.add(val);
                                        r[0] = rest;
                                        return Boolean.TRUE;
                                    }).booleanValue());
                        return success(result, r[0]);
                    }

                    @Override
                    public boolean oneOutput() {
                        return false;
                    }
                };
    }

    /**
     * Creates hidden version of this parser (its value won't appear in concatenated parser with
     * <code>then</code> methods).
     *
     * @return hidden variant of this parser
     */
    public default HideParser hide() {
        return new HideParser(this);
    }


    /**
     * Maps values of given parser to create new parser.
     * @param f function to map values
     * @return
     */
    public default <B> Parser<B> map(Function<A,B> f) {
        return new MapParser<>(this, f);
    }
}