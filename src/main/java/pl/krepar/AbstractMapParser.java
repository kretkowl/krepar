package pl.krepar;

import static pl.krepar.ParseResult.failure;
import static pl.krepar.ParseResult.success;

import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.val;

@AllArgsConstructor
public abstract class AbstractMapParser<A, B, T extends AbstractMapParser<A, B, T>> implements BasicParser<A, T> {

    private final Function<B,A> mapping;

    protected abstract BasicParser<B, ?> getBase();


    @Override
    public Continuation tryParse(Input in, ParseContext pc) {
        return
                new Continuation(
                        getBase(),
                        in,
                        () -> {
                            mapResults(pc, in);
                            return null;
                        });
/*        val c = getBase().tryParse(in, pc);
        if (c == null) {
            mapResults(pc, in);
            return null;
        }
        return c;*/
    }

    protected Continuation wrapContinuation(ParseContext pc, Input in, Continuation c) {
        return new Continuation(
                c.getParserToCall(),
                c.getInput(),
                () -> {
                    val newC = c.getThen().get();

                    if (newC == null) {
                        mapResults(pc, in);
                        return null;
                    } else
                        return wrapContinuation(pc, in, newC);
                });
    }

    protected void mapResults(ParseContext pc, Input in) {
        pc.getResult(getBase(), in).stream()
        .map(
                (r) -> r.<ParseResult<A>>match(
                        (fail) -> failure(fail),
                        (res, rest) -> success(mapping.apply(res), rest)))
        .forEach((r) -> pc.putResult(this, in, r));
    }

}