package pl.krepar;

import lombok.val;

/**
 * Base interface for every parser. Has no fluent api methods (except for map).
 *
 * It is functional interface: if you create terminal parser (here understood as parser that
 * does not consists of alternative (OrParser)) you may just realize it as a lambda.
 *
 * @author kretkowl
 *
 * @param <A> return value
 */
public interface BasicParser<A> {

    public default ParseResult<? extends A> parse(Input in) {
        return parse(in, new ParseContext());
    }

    public default ParseResult<? extends A> parseM(Input in, ParseContext ctx) {
        val r = ctx.getResult(this, in);
        return r.orElseGet(() -> {
            val res = parse(in, ctx);
            ctx.putResult(this, in, res);
            return res;
        });
    }

    public ParseResult<? extends A> parse(Input in, ParseContext ctx);

    public default boolean oneOutput() { return true; }
}
