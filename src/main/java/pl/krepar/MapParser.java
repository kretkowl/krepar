package pl.krepar;

import java.util.function.Function;

/**
 * Transforms parser B to parser A by mapping values returned by the first using given fuunction.
 *
 * @author kretkowl
 *
 * @param <A>
 * @param <B>
 */
public class MapParser<A, B> extends AbstractMapParser<A, B, MapParser<A, B>> implements Parser<A, MapParser<A, B>> {

    final BasicParser<B, ?> base;


    @Override
    public String prettyPrint() {
        return "\\[" + base.prettyPrint() + "]";
    }

    @Override
    public boolean isTerminal() {
        return base.isTerminal();
    }

    public MapParser(BasicParser<B, ?> base, Function<B, A> mapping) {
        super(mapping);
        this.base = base;
    }


    @Override
    protected BasicParser<B, ?> getBase() {
        return base;
    }
}
