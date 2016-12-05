# krepar
Library for ad-hoc Java parsers. API inspired by [Ahnfelt pearsercombinator](https://github.com/Ahnfelt/parsercombinator).

## Motivation
This lib was written to handle small ad-hoc grammars that appear in bigger projects. If you don't want
to learn ANTLR or JAVACC own syntax, drag another runtime dependency to your project, complicate your 
workflow by adding step of code generation, ponder how to formulate grammar to avoid left recursion or 
ambiguity or simply find idea of writing grammar in one language just to compile it to second language to
create parser of third language that is compiled with your application a bit ridiculous, then you may be 
interested in *krepar*

It exposes fluent Java API with few convenience methods to allow fast creating simple grammars.

You write your grammar in Java, there is no need of precompiling anything - this is as ordinary Java as the
rest of your code is.

Example:

```java
        Parser<?,?> number = regexp("[0-9]+(\\.[0-9]+)?");
        Parser<?,?>> op = string("+").or(string("-")).or(string("*")).or(string("/"));
        Parser<?,?> expr = number.then(op.then(number).repeat());

        System.out.println(parseFirst(expr, "1+2/3").match(
                (fail) -> "parsing error: " + fail.getMessage() + "; position: " + fail.getPosition(),
                (res, __) -> "parse successful"));
```

Prints "parse successful" on console.

If you want some more useful example, look into ExpressionCalculatorTest, which contains "obligatory" math expression
parser.


## Dependencies
Library depends only on [lombok](projectlombok.org) in compile time. You may delombok it, to get rid off
of this dependence.

## Future work
Currently, each recursion in parsing gives recursive call in java code, so stack can be easily exhausted.
This calls will be replaced with trampolines.

Also, returning of all possible parses in case of ambiguous grammars will be added.
