package pl.krepar;

import java.util.function.Supplier;

public class Ref<A> implements Supplier<A> {

    private A value;

    public void set(A value) {
        if (isSet())
            throw new IllegalStateException("reference already set!");
        this.value = value;
    }

    public boolean isSet() {
        return value != null;
    }

    @Override
    public A get() {
        if (!isSet())
            throw new IllegalStateException("reference not set!");

        return value;
    }

}
