package pl.krepar;

import static pl.krepar.ParseResult.success;

/**
 * Class that allows to match regular expression against input.
 *
 * @author kretkowl
 *
 */
public class StringParser implements Parser<String, StringParser> {
    private String token;

    @Override
    public String prettyPrint() {
        return "\"" + token + "\"";
    }

    /**
     * Creates new parser with given pattern.
     * @param pattern
     */
    public StringParser(String token) {
        this.token = token;
    }

    public Continuation tryParse(Input in, ParseContext ctx) {
        if (in.getCharSequence().length() - in.getOffset() < token.length()) {
            ctx.putResult(this, in, ParseResult.failure("<" + token + "> expected", in.getOffset()));
            return null;
        }

        for (int i=token.length() - 1; i>=0; i--) {
            if (in.getCharSequence().charAt(in.getOffset()+i) != token.charAt(i)) {
                ctx.putResult(this, in, ParseResult.failure("<" + token + "> expected", in.getOffset()));
                return null;
            }
        }
        ctx.putResult(this, in, success(token, in.forward(token.length())));

        return null;
    }
}