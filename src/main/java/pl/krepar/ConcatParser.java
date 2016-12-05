package pl.krepar;

import static pl.krepar.ParseResult.success;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.krepar.ParseResult.failure;

import lombok.val;
import pl.krepar.ParseResult.Failure;

/**
 * Parser that matches input which can be matched by two parser in such way that first matches prefix
 * of the string and second the rest.
 *
 * @author kretkowl
 *
 * @param <A>
 * @param <B>
 */
public class ConcatParser<A, B> implements Parser<Pair<A, B>, ConcatParser<A, B>> {

    private final BasicParser<A, ?> first;
    private final BasicParser<B, ?> second;

    private Boolean terminal;


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

    @Override
    public Continuation tryParse(Input in, ParseContext pc) {
        return new Continuation(
                first,
                in,
                () -> {
                    val fr = pc.getResult(first, in);
                    if (fr.stream().noneMatch(ParseResult::isSuccessful)) {
                        pc.putResult(this, in,
                                failure(
                                        fr.stream()
                                        .map(
                                                (r) -> r.match(
                                                        Failure::getMessage,
                                                        (__, ___) -> ""))
                                        .collect(Collectors.joining(" or ")),
                                        in.getOffset()));
                        return null;
                    } else {
                        return getFirstContinuation(
                                new HashSet<>(fr.stream().filter(ParseResult::isSuccessful).collect(Collectors.toSet())),
                                in,
                                pc);
                    }
                });

    }

    private Continuation getFirstContinuation(Set<ParseResult<? extends A>> remaining, Input in, ParseContext pc) {
        val a=remaining.iterator().next();
        remaining.remove(a);

        Input secondIn = a.match(
                (fail) -> { throw new IllegalStateException("first continuation with failure"); },
                (res, rest) -> rest);

        return new Continuation(
                second,
                secondIn,
                () -> {
                    val sRess = pc.getResult(second, secondIn);

                    sRess.stream()
                    .filter(ParseResult::isSuccessful)
                    .map((res) -> res.match((f) -> null, (v, rest) -> Pair.of(v, rest)))
                    .forEach((s) -> {
                        pc.putResult(ConcatParser.this, in, success(Pair.of(a.getValue().get(), s.getFirst()), s.getSecond()));
                    });

                    return remaining.isEmpty() ? null : getFirstContinuation(remaining, in, pc);
                });
    }
}
