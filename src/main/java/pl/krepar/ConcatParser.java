package pl.krepar;

import static pl.krepar.ParseResult.success;

/**
 * Parser that matches input which can be matched by two parser in such way that first matches prefix
 * of the string and second the rest.
 *
 * @author kretkowl
 *
 * @param <A>
 * @param <B>
 */
public class ConcatParser<A, B> implements Parser<Pair<A, B>> {

    private final BasicParser<A, ?> first;
    private final BasicParser<B, ?> second;

    private Boolean terminal;

    @Override
    public ParseResult<? extends Pair<A, B>> parse(Input in, ParseContext ctx) {
        return ctx.getResult(first, in).match(
                ParseResult::failure,
                (a, rest) -> ctx.getResult(second, rest).match(
                        ParseResult::failure,
                        (b, rest2) -> success(Pair.of(a, b), rest2)));
    }

    public ConcatParser(BasicParser<A, ?> first, BasicParser<B, ?> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean isTerminal() {
        if (terminal == null) {
            terminal = Boolean.FALSE; // hypothesis
            terminal = first.isTerminal() && second.isTerminal();
        }

        return terminal.booleanValue();
    }
}
