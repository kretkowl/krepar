package pl.krepar;

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
public interface BasicParser<A, T extends BasicParser<A, T>> {

    public default ParseResult<? extends A> parse(Input in) {
        return new ParseContext().getResult(this, in);
    }

    public ParseResult<? extends A> parse(Input in, ParseContext ctx);

    public default boolean isTerminal() { return true; }

    @SuppressWarnings("unchecked")
    public default T setRef(Ref<T> r) { r.set((T)this); return (T)this;}
}
