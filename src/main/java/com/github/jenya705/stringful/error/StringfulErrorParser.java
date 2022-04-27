package com.github.jenya705.stringful.error;

import java.util.function.Function;

/**
 * @author Jenya705
 */
public interface StringfulErrorParser<C, E extends StringfulError<C>, T extends Throwable> {

    static <C> StringfulErrorParser<C, StringfulError<C>, Throwable> defaultParser(
            Function<String, C> wrapper,
            DefaultStringfulErrorParser.JoinFunction<C> joinFunction
    ) {
        return new DefaultStringfulErrorParser<>(wrapper, joinFunction);
    }

    static StringfulErrorParser<String, StringfulError<String>, Throwable> defaultStringParser() {
        return new DefaultStringfulErrorParser<>(it -> it, (left, message, right) -> left + message + right);
    }

    StringfulError<C> toError(String left, T throwable, String right);

    C toMessage(StringfulError<C> error);

    default C toMessage(String left, T throwable, String right) {
        return toMessage(toError(left, throwable, right));
    }

}
