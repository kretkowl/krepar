package pl.krepar;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;


import lombok.val;
import pl.krepar.BasicParser.Continuation;

/**
 * For now, this class is a class responsible for memoization of parser results.
 *
 * Later it will allow for trampolining (to parse in constant stack) and all values if ambiguous grammars.
 *
 * @author kretkowl
 *
 */
public class ParseContext {

    public static <A> ParseResult<? extends A> parseFirst(BasicParser<A, ?> parser, String in) {
        return parse(parser, new Input(in, 0)).iterator().next();
    }

    public static <A> Set<ParseResult<? extends A>> parse(BasicParser<A, ?> parser, String in) {
        return parse(parser, new Input(in, 0));
    }

    public static <A> Set<ParseResult<? extends A>> parse(BasicParser<A, ?> parser, Input in) {
        return new ParseContext().doParse(parser, in);
    }

    @SuppressWarnings("rawtypes")
    private Map<Pair<BasicParser<?, ?>, Input>, Set<ParseResult>>  memory = new HashMap<>();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <A> Set<ParseResult<? extends A>> getResult(BasicParser<A, ?> p, Input in) {
        Pair<BasicParser<?, ?>, Input> key = Pair.of(p, in);
        if (memory.containsKey(key))
            return ((Set)memory.get(key));
        else
            throw new IllegalStateException("getResult called without computing value");
    }

    public static <K, V> V getOrPutDefault(java.util.Map<K, V> map, K key, Supplier<V> def) {
        if (!map.containsKey(key))
            map.put(key, def.get());

        return map.get(key);
    }

    public <A> void putResult(BasicParser<A, ?> p, Input in, ParseResult<A> res) {
        @SuppressWarnings("rawtypes")
        val ress = getOrPutDefault(memory, Pair.of(p, in), HashSet::new);
        if (res.isSuccessful()) {
            ress.removeIf((r) -> !r.isSuccessful());
            ress.add(res);
        } else if (ress.stream().noneMatch(ParseResult::isSuccessful))
            ress.add(res);
    }

    public <A> void putAllResults(BasicParser<A, ?> p, Input in, Set<ParseResult<? extends A>> res) {
        @SuppressWarnings("rawtypes")
        val ress = getOrPutDefault(memory, Pair.of(p, in), HashSet::new);

        ress.addAll(res);

        if (ress.stream().anyMatch(ParseResult::isSuccessful))
            ress.removeIf((r) -> !r.isSuccessful());
    }

    public <A> Set<ParseResult<? extends A>> doParse(BasicParser<A, ?> parser, Input in) {
        Deque<Continuation> stack = new LinkedList<>();

        putResult(parser, in, ParseResult.failure("no match", -1));
        Continuation cc = parser.tryParse(in, this);
        while (cc != null) {

            Continuation newCC = null;
            if (!memory.containsKey(Pair.of(cc.getParserToCall(), cc.getInput()))) {
                putResult(cc.getParserToCall(), cc.getInput(), ParseResult.failure("no match", -1));
                newCC = cc.getParserToCall().tryParse(cc.getInput(), this);
            }

            if (newCC != null) {
                stack.push(cc);
                cc = newCC;
            } else {
                do {
                    cc = cc.getThen().get();
                    if (cc == null)
                        if (stack.isEmpty())
                            break;
                        else
                            cc = stack.pop();
                    else
                        break;
                } while (cc != null);
            }
        }

        return getResult(parser, in);

    }

 }
