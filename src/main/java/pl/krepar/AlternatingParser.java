package pl.krepar;

public interface AlternatingParser<A> extends BasicParser<A> {

    @Override
    default boolean oneOutput() {
        return false;
    }

    public BasicParser<A>[] getAlternatives();
}
