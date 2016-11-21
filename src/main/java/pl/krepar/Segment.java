package pl.krepar;

/**
 * Char sequence backed by String that does not copy string on subsequence creation.
 * @author kretkowl
 *
 */
public class Segment implements CharSequence {

    private final String base;

    private final int offset;

    private final int length;

    public Segment(String s) {
        this.base = s;
        this.offset = 0;
        this.length = s.length();
    }

    public Segment(String s, int offset, int length) {
        this.base = s;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public char charAt(int arg0) {
        return base.charAt(arg0 + offset);
    }

    @Override
    public CharSequence subSequence(int arg0, int arg1) {
        return new Segment(base, arg0, arg1 - arg0);
    }

    @Override
    public String toString() {
        return base.substring(offset, offset + length);
    }


}
