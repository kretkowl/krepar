package pl.krepar;

import java.util.function.Function;

/**
 * Transforms parser B to parser A by mapping values returned by the first using given fuunction.
 *
 * @author kretkowl
 *
 * @param <A>
 * @param <B>
 */
public class MapParser<A, B> implements Parser<A> {

    private final BasicParser<B, ?> base;

    private final Function<B, A> mapping;

    @Override
    public ParseResult<A> parse(Input in, ParseContext ctx) {
        return ctx.getResult(base, in).match(
                ParseResult::failure,
                (b, rest) -> ParseResult.success(mapping.apply(b), rest));
    }

    @Override
    public boolean isTerminal() {
        return base.isTerminal();
    }

    public MapParser(BasicParser<B, ?> base, Function<B, A> mapping) {
        this.base = base;
        this.mapping = mapping;
    }


}
