package pl.krepar;

import java.util.function.Supplier;

class DelayParser<A> implements Parser<A> {

    private final Supplier<? extends BasicParser<A, ?>> delay;

    private BasicParser<A, ?> realised;

    public DelayParser(Supplier<? extends BasicParser<A, ?>> delay) {
        this.delay = delay;
    }

    private void realize() {
        if (realised == null)
            realised = delay.get();
    }

    @Override
    public ParseResult<? extends A> parse(Input in, ParseContext ctx) {
        realize();
        return ctx.getResult(realised, in);
    }

    @Override
    public boolean isTerminal() {
        realize();
        return realised.isTerminal();
    }
}
