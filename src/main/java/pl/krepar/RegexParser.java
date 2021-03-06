package pl.krepar;

import static pl.krepar.ParseResult.success;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that allows to match regular expression against input.
 *
 * @author kretkowl
 *
 */
public class RegexParser implements Parser<MatchResult, RegexParser> {
    private Pattern pattern;

    /**
     * Creates new parser with given pattern.
     * @param pattern
     */
    public RegexParser(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public String prettyPrint() {
        return "#\"" + pattern.pattern() + "\"";
    }

    public Continuation tryParse(Input in, ParseContext ctx) {
        Matcher matcher = pattern.matcher(
                in.getCharSequence().subSequence(
                        in.getOffset(),
                        in.getCharSequence().length()));
        if (matcher.lookingAt()) {
            ctx.putResult(this, in, success(matcher.toMatchResult(), in.forward(matcher.end())));
        } else {
            ctx.putResult(this, in, ParseResult.failure("<" + pattern.toString() + "> expected", in.getOffset()));
        }

        return null;
    }
}