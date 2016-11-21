package pl.krepar;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * Poor mans algebraic type. Represents success or failure of parsing.
 *
 * The result may be checked in three ways:
 *
 * <ul>1) {@link getValue} returns <code>Optional</code>, which is empty on failure and not empty on success</ul>
 *
 * <ul>2) {@link isSuccessful} returns true if parser matched the input</ul>
 *
 * <ul>3) {@link match} is a poor man matching. Takes two functions, executes first one on failure and second one on success.</ul>
 *
 * @author kretkowl
 *
 * @param <A> value type
 */
public abstract class ParseResult<A> {

    /**
     * Matcher. Takes two functions. Exactly one will be called - <code>failure</code> (with failure object) when parsing failed;
     * <code>success</code> when parsing succeed (with value from parser and <code>Input</code> representing unparsed part of input characters.
     *
     *  Value returned from match is value returned by function called.
     *
     * @param failure function to call on failure
     * @param success function to call on success
     * @return
     */
    public abstract <R> R match(Function<Failure<A>, R> failure, BiFunction<A, Input, R> success);

    /**
     * Method returns Optional which is not empty if and only if parsing was successful. Then it will contain value from parsing.
     * @return
     */
    public Optional<A> getValue() {
        return match( (__) -> Optional.empty(), (v, __) -> Optional.of(v));
    }

    /**
     * Returns true if parsing was successful.
     * @return
     */
    public abstract boolean isSuccessful();

    /**
     * Failure class.
     *
     * @author kretkowl
     *
     * @param <A> type is irrelevant - matches parser type
     */
    @Value
    @EqualsAndHashCode(callSuper=false)
    public static final class Failure<A> extends ParseResult<A> {

        /**
         * Message from failing parser
         */
        String message;

        /**
         * offset in input, when failure occured
         */
        int position;

        @Override
        public <R> R match(
                Function<Failure<A>, R> failure,
                BiFunction<A, Input, R> success)
        {
            return failure.apply(this);
        }

        @Override
        public boolean isSuccessful() {
            return false;
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode(callSuper=false)
    private static final class Success<A>extends ParseResult<A> {

        private final A value;

        private final Input rest;

        @Override
        public <R> R match(
                Function<Failure<A>, R> failure,
                BiFunction<A, Input, R> success)
        {
            return success.apply(value, rest);
        }

        @Override
        public boolean isSuccessful() {
            return true;
        }
    }

    /**
     * Success constructor
     * @param value
     * @param rest
     * @return
     */
    public static <A> ParseResult<A> success(A value, Input rest) {
        return new Success<A>(value, rest);
    }

    /**
     * Failure constructor
     * @param msg
     * @param position
     * @return
     */
    public static <A> ParseResult<A> failure(String msg, int position) {
        return new Failure<>(msg, position);
    }

    /**
     * Failure converter - convenience method to cast failure (which is essentially typeless)
     * to another parsers type.
     * @param f
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <A, B> ParseResult<A> failure(Failure<B> f) {
        return (Failure<A>) f;
    }

}
