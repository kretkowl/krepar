package pl.krepar;

import lombok.Value;

/**
 * This class represents input to the parser. It's immutable and consists of <code>CharSequence</code>
 * and <code>offset</code> within it (to limit creation of subsequences).
 *
 * @author kretkowl
 *
 */
@Value
public class Input {

    CharSequence charSequence;
    int offset;

    /**
     * Creates input that has offset moved by <code>chars</code> characters.
     * @param chars
     * @return new Input
     */
    public Input forward(int chars) {
        return new Input(charSequence, offset + chars);
    }
}