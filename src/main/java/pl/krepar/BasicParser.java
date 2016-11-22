package pl.krepar;

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

    /**
     * Main entry point. Returns first matching prefix of input on success (it does not have to be whole
     * input - you can use {@link Parsers.end} if you desire).
     *
     * @param in
     * @return
     */
    public default ParseResult<? extends A> parse(Input in) {
        return new ParseContext().getResult(this, in);
    }

    public ParseResult<? extends A> parse(Input in, ParseContext ctx);

    public default boolean isTerminal() { return true; }

    @SuppressWarnings("unchecked")
    public default T setRef(Ref<T> r) { r.set((T)this); return (T)this;}
}
