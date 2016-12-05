package pl.krepar;

import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Base interface for every parser. Has no fluent api methods (except for map).
 *
 * It is functional interface: if you create terminal parser (here understood as parser that
 * does not consists of alternative (OrParser)) you may just realize it as a lambda.
 *
 * You should not call method <code>parse(Input,ParseContext)</code> directly (in your client code nor
 * in parsers - the latter is more important). Calling should be delegated to {@link ParserContext#getResult}
 *
 * @author kretkowl
 *
 * @param <A> return value
 */
public interface BasicParser<A, T extends BasicParser<A, T>> {

    public default boolean isTerminal() { return true; }

    @SuppressWarnings("unchecked")
    public default T setRef(Ref<? super T> r) { r.set((T) this); return (T)this;}

    public Continuation tryParse(Input in, ParseContext pc);

    @Value
    @AllArgsConstructor
    public static class Continuation {
        final BasicParser<?, ?> parserToCall;
        final Input input;

        final Supplier<Continuation> then;
    }
}
