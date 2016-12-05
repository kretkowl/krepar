package pl.krepar;

import java.util.function.Supplier;

/**
 * Delay - to allow recursion in grammar.
 *
 * @author kretkowl
 *
 * @param <A>
 */
class DelayParser<A> extends AbstractMapParser<A, A, DelayParser<A>> implements Parser<A, DelayParser<A>> {

    private final Supplier<? extends BasicParser<A, ?>> delay;

    private BasicParser<A, ?> realised;

    public DelayParser(Supplier<? extends BasicParser<A, ?>> delay) {
        super((a) -> a);
        this.delay = delay;
    }

    private void realize() {
        if (realised == null)
            realised = delay.get();
    }

    @Override
    public boolean isTerminal() {
        realize();
        return realised.isTerminal();
    }

    @Override
    protected BasicParser<A, ?> getBase() {
        realize();
        return realised;
    }

}
