package pl.krepar;

import static pl.krepar.ParseResult.success;

import lombok.Value;

/**
 * Parser that does not expose its value in concatenations.
 * Does not support much of fluent API of other parsers, as it makes no sense to repeat or make optional
 * - probably you should do this before hiding parser.
 *
 *  Has special treatment of <code>then</code> method.
 *
 * @author kretkowl
 *
 */
@Value
public class HideParser implements BasicParser<Void> {

    public final Parser<?> hidden;

    /**
     * Creates new hidden parser based on given.
     * @param hidden parser to hide
     */
    public HideParser(Parser<?> hidden) {
        this.hidden = hidden;
    }

    /**
     * Always returns null on success.
     */
    @Override
    public ParseResult<Void> parse(Input in, ParseContext ctx) {
        return hidden.parseM(in, ctx).match(
                ParseResult::failure,
                (__, rest) -> success(null, rest));
    }

    /**
     * Deprecated as one should concatenate first and then hide.
     *
     * @param p
     * @return
     */
    @Deprecated
    public HideParser then(HideParser that) {
        return new ConcatParser<>(this, that).hide();
    }

    /**
     * Creates complex parser as concatenation with given one.
     * Return type is taken from the other parser.
     * @param p
     * @return
     */
    public <A> Parser<A> then(Parser<A> that) {
        return
                new ConcatParser<>(this, that).map((p) -> p.getSecond());
    }

}
