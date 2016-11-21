package pl.krepar;

import static pl.krepar.ParseResult.failure;
import static pl.krepar.ParseResult.success;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import lombok.val;

/**
 * Class for alternating parsers. Can contain many parsers (not limited to two).
 */
public class OrParser<A> implements Parser<A> {

    private final Parser<? extends A>[] alts;

    /**
     * If every alternative fails, error message is built as  <code>msg1 " or " msg2 " or " ....</code>
     */
    @Override
    public ParseResult<A> parse(Input in) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for(int i=0; i< alts.length; i++) {
            Optional<ParseResult<A>> res =
                alts[i].parse(in).match(
                        (fail) -> {
                            if (!first)
                                sb.append(" or ");
                            sb.append(fail.getMessage());
                            return Optional.empty();
                        },
                        (v, rest) -> Optional.of(success(v, rest)));
            if (res.isPresent())
                return res.get();
        }
        return failure(sb.toString(), in.getOffset());
    }

    @Override
    public boolean oneOutput() {
        return alts.length > 1;
    }

    public OrParser<A> or(OrParser<A> that) {
        @SuppressWarnings("unchecked")
        Parser<A>[] arr = new Parser[alts.length + that.alts.length];
        System.arraycopy(alts, 0, arr, 0, alts.length);
        System.arraycopy(this.alts, 0, arr, alts.length, this.alts.length);
        return
                new OrParser<A>(arr);
    }

    /**
     * Creates new OrParser that has list of alternatives extended (not nested OrParser).
     */
    @SuppressWarnings("unchecked")
    @Override
    public OrParser<A> or(Parser<A> that) {
        val newAlts = Arrays.copyOf(alts, alts.length + 1);
        newAlts[alts.length] = that;
        return
                new OrParser<A>((Parser<A>[]) newAlts);
    }

    /**
     * Creates new OrParser that has list of alternatives extended (not nested OrParser).
     *
     * Lazy version.
     */
    @Override
    public OrParser<A> or(Supplier<Parser<A>> that) {
        return or((in) -> that.get().parse(in));
    }

    @SafeVarargs
    public OrParser(Parser<A> ...parsers) {
        alts = parsers.clone();
    }

}
