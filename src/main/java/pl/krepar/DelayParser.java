package pl.krepar;

import java.util.function.Supplier;

class DelayParser<A> implements Parser<A> {

    private final Supplier<Parser<A>> delay;

    private Parser<A> realised;

    public DelayParser(Supplier<Parser<A>> delay) {
        this.delay = delay;
    }

    @Override
    public ParseResult<? extends A> parse(Input in, ParseContext ctx) {
        if (realised == null)
            realised = delay.get();
        return realised.parseM(in, ctx);
    }

}
