package pl.krepar;

import static pl.krepar.ParseResult.success;

public class ConcatParser<A, B> implements Parser<Pair<A, B>> {

    private final BasicParser<A> first;
    private final BasicParser<B> second;

    private Boolean terminal;

    @Override
    public ParseResult<? extends Pair<A, B>> parse(Input in, ParseContext ctx) {
        return first.parseM(in, ctx).match(
                ParseResult::failure,
                (a, rest) -> second.parseM(rest, ctx).match(
                        ParseResult::failure,
                        (b, rest2) -> success(Pair.of(a, b), rest2)));
    }

    public ConcatParser(BasicParser<A> first, BasicParser<B> second) {
        this.first = first;
        this.second = second;
    }


}
