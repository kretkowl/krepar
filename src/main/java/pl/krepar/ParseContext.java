package pl.krepar;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import lombok.Value;
import lombok.val;

/**
 * For now, this class is a class responsible for memoization of parser results.
 *
 * Later it will allow for trampolining (to parse in constant stack) and all values if ambiguous grammars.
 *
 * @author kretkowl
 *
 */
public class ParseContext {

    private Map<Pair<BasicParser<?, ?>, Input>, ParseResult<?>>  memory = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <A> ParseResult<? extends A> getResult(BasicParser<A, ?> p, Input in) {
        Pair<BasicParser<?, ?>, Input> key = Pair.of(p, in);
        if (memory.containsKey(key))
            return ((ParseResult<? extends A>)memory.get(key));

        memory.put(key, ParseResult.failure("", -1)); // to allow left recurrence
        ParseResult<? extends A> v = p.parse(in, this);
        memory.put(key, v);
        return v;
    }

 }
