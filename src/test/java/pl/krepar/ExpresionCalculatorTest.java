package pl.krepar;

import static pl.krepar.Parsers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class ExpresionCalculatorTest {

    private final Ref<Parser<Double>> exprRef = new Ref<>();

    private final Parser<Double> number = regexp("[0-9]+(\\.[0-9]+)?")
                        .map((mr) -> Double.parseDouble(mr.group()));

    private final Parser<Double> term = number
                .or(string("(").hide().then(exprRef).then(string(")").hide()));

    private final Ref<Parser<Double>> multExprRef = new Ref<>();

    private final Parser<Double> multExpr =
                term.then((string("*").or(string("/")))).then(multExprRef)
                        .map(match((t1, op, t2) -> op.equals("*")
                                ? Double.valueOf(t1.doubleValue() * t2.doubleValue())
                                : Double.valueOf(t1.doubleValue() / t2.doubleValue()) ))
                .or(term)
                .setRef(multExprRef);

    private final Ref<Parser<Double>> addExprRef = new Ref<>();

    private final Parser<Double> addExpr =
                multExpr.then((string("+").or(string("-")))).then(addExprRef)
                        .map(match((t1, op, t2) -> op.equals("+")
                                ? Double.valueOf(t1.doubleValue() + t2.doubleValue())
                                : Double.valueOf(t1.doubleValue() - t2.doubleValue()) ))
                .or(multExpr)
                .setRef(addExprRef);

    private final Parser<Double> expr = addExpr.setRef(exprRef);

    @Test
    public void testExpressions() {
        assertEquals(1., expr.parse("1+0").getValue().get().doubleValue(), .0);
        assertEquals(2., expr.parse("1+0+1").getValue().get().doubleValue(), .0);
        assertEquals(7., expr.parse("1+2*3").getValue().get().doubleValue(), .0);
        assertEquals(9., expr.parse("(1+2)*3").getValue().get().doubleValue(), .0);
        assertEquals(6., expr.parse("(1+3)*3/2").getValue().get().doubleValue(), .0);
        assertFalse(expr.parse("(1++3)*3/2").getValue().isPresent());
    }
}
