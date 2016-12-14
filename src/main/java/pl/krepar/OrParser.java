package pl.krepar;

import static pl.krepar.ParseResult.failure;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.val;

/**
 * Class for alternating parsers. Can contain many parsers (not limited to two).
 */
public class OrParser<A> implements Parser<A, OrParser<A>> {

    private final Parser<A, ?>[] alts;


    @Override
    public String prettyPrint() {
        return Arrays.stream(alts).map(BasicParser::prettyPrint).collect(Collectors.joining(" | "));
    }

    @Override
    public Continuation tryParse(Input in, ParseContext pc) {
        if (alts.length == 0) {
            pc.putResult(this,  in, failure("empty alternative", in.getOffset()));
            return null;
        }

        return createOrContinuation(0, in, pc);
    }

    @AllArgsConstructor
    private class OrContinuationSupplier implements Supplier<Continuation> {

        private final int i;
        private final Input in;
        private final ParseContext pc;

        @Override
        public Continuation get() {
            return createOrContinuation(i, in, pc);
        }

    }

    private Continuation createOrContinuation(int i, Input in, ParseContext pc) {
        return new Continuation(
                alts[i],
                in,
                () -> {
                    pc.putAllResults(OrParser.this, in, pc.getResult(alts[i], in));
                    if (alts.length>i+1)
                        return createOrContinuation(i+1, in, pc);
                    else
                        return null;
                });
    }

    @Override
    public boolean isTerminal() {
        return alts.length <= 1;
    }

    /**
     * Creates new OrParser that has list of alternatives extended (not nested OrParser).
     */
    @Override
    public OrParser<A> or(Parser<A, ?> that) {
        val newAlts = Arrays.copyOf(alts, alts.length + 1);
        newAlts[alts.length] = that;
        return
                new OrParser<A>(newAlts);
    }

    public OrParser<A> or(OrParser<A> that) {
        @SuppressWarnings("unchecked")
        Parser<A, ?>[] arr = new Parser[alts.length + that.alts.length];
        System.arraycopy(alts, 0, arr, 0, alts.length);
        System.arraycopy(this.alts, 0, arr, alts.length, this.alts.length);
        return
                new OrParser<A>(arr);
    }

    /**
     * Creates new OrParser that has list of alternatives extended (not nested OrParser).
     *
     * Lazy version.
     */
    @Override
    public OrParser<A> or(Supplier<Parser<A, ?>> that) {
        return or(new DelayParser<>(that));
    }

    @SafeVarargs
    public OrParser(Parser<A,?> ...parsers) {
        alts = parsers.clone();
    }

}
