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
public class RegexParser implements Parser<MatchResult> {
    private Pattern pattern;

    /**
     * Creates new parser with given pattern.
     * @param pattern
     */
    public RegexParser(Pattern pattern) {
        this.pattern = pattern;
    }

    public ParseResult<MatchResult> parse(Input in, ParseContext ctx) {
        Matcher matcher = pattern.matcher(
                in.getCharSequence().subSequence(
                        in.getOffset(),
                        in.getCharSequence().length()));
        if (matcher.lookingAt()) {
            return success(matcher.toMatchResult(), in.forward(matcher.end()));
        } else {
            return ParseResult.failure("<" + pattern.toString() + "> expected", in.getOffset());
        }
    }
}