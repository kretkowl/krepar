package pl.krepar;

import static pl.krepar.Parsers.*;
import static pl.krepar.ParseContext.parseFirst;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Obligatory arithmetic expression example.
 *
 * Shows how to use references to create recursion, how to hide no value elements to simplify matching
 * and how to use all basic parsers (with exception to optional).
 *
 * @author kretkowl
 *
 */
public class ExpressionCalculatorTest {

    private final Ref<Parser<Double, ?>> exprRef = new Ref<>();

    private final Parser<Double, ?> number = regexp("[0-9]+(\\.[0-9]+)?")
                        .map((mr) -> Double.parseDouble(mr.group()));

    private final Parser<Double, ?> term = number
                .or(string("(").hide().then(exprRef).then(string(")").hide()));

    private final Ref<Parser<Double, ?>> multExprRef = new Ref<>();

    private final Parser<Double, ?> multExpr =
                term.then((string("*").or(string("/")))).then(multExprRef)
                        .map(match((t1, op, t2) -> op.equals("*")
                                ? Double.valueOf(t1.doubleValue() * t2.doubleValue())
                                : Double.valueOf(t1.doubleValue() / t2.doubleValue()) ))
                .or(term)
                .setRef(multExprRef);

    private final Ref<Parser<Double, ?>> addExprRef = new Ref<>();

    private final Parser<Double, ?> addExpr =
                multExpr.then((string("+").or(string("-")))).then(addExprRef)
                        .map(match((t1, op, t2) -> op.equals("+")
                                ? Double.valueOf(t1.doubleValue() + t2.doubleValue())
                                : Double.valueOf(t1.doubleValue() - t2.doubleValue()) ))
                .or(multExpr)
                .setRef(addExprRef);

    private final Parser<Double, ?> expr = addExpr.setRef(exprRef);

    private final Parser<Double, ?> exprWholeLine = expr.then(end()).map(Pair::getFirst);

    @Test
    public void testExpressions() {
        assertEquals(1., parseFirst(expr, "1+0").getValue().get().doubleValue(), .0);
        assertEquals(2., parseFirst(expr, "1+0+1").getValue().get().doubleValue(), .0);
        assertEquals(7., parseFirst(expr, "1+2*3").getValue().get().doubleValue(), .0);
        assertEquals(9., parseFirst(exprWholeLine, "(1+2)*3").getValue().get().doubleValue(), .0);
        assertEquals(6., parseFirst(exprWholeLine, "(1+3)*3/2").getValue().get().doubleValue(), .0);
        assertFalse(parseFirst(expr, "(1++3)*3/2").getValue().isPresent());
    }

}
