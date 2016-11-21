package pl.krepar;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class ParseContext {

    private Map<Pair<BasicParser<?>, Input>, ParseResult<?>>  memory = new HashMap<>();

    @SuppressWarnings("rawtypes")
    private Deque<BasicParser<?>[]> toVisit = new LinkedList<>();

    public <A> void putResult(BasicParser<A> p, Input in, ParseResult<? extends A> res) {
        memory.put(Pair.of(p, in), res);
    }

    @SuppressWarnings("unchecked")
    public <A> Optional<ParseResult<? extends A>> getResult(BasicParser<A> p, Input in) {
        return Optional.ofNullable((ParseResult<? extends A>)memory.get(Pair.of(p, in)));
    }

    public <A> void pushAll(BasicParser<A> ...alts) {
        toVisit.push(alts);
    }

    public <A> BasicParser<A>[] pop() {
        return (BasicParser<A>[]) toVisit.pop();
    }
}
