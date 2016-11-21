package pl.krepar;

import java.util.function.Function;

public class MapParser<A, B> implements Parser<A> {

    private final BasicParser<B> base;

    private final Function<B, A> mapping;

    @Override
    public ParseResult<A> parse(Input in, ParseContext ctx) {
        return base.parseM(in, ctx).match(
                ParseResult::failure,
                (b, rest) -> ParseResult.success(mapping.apply(b), rest));
    }

    @Override
    public boolean oneOutput() {
        return base.oneOutput();
    }

    public MapParser(BasicParser<B> base, Function<B, A> mapping) {
        this.base = base;
        this.mapping = mapping;
    }


}
