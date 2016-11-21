package pl.krepar;

import lombok.Value;

/**
 * Class that represents immutable pair of two elements.
 *
 * @author kretkowl
 *
 * @param <U>
 * @param <V>
 */
@Value(staticConstructor="of")
public class Pair<U, V> {
    public final U first;
    public final V second;

    @Override
    public String toString() {
        return "<" + first + ", " + second + ">";
    }
}