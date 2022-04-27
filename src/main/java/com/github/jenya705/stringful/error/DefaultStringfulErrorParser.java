package com.github.jenya705.stringful.error;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultStringfulErrorParser<C> implements StringfulErrorParser<C, StringfulError<C>, Throwable> {

    @FunctionalInterface
    public interface JoinFunction<C> {
        C join(String left, C message, String right);
    }

    private final Function<String, C> wrapper;
    private final JoinFunction<C> joinFunction;

    @Override
    public StringfulError<C> toError(String left, Throwable throwable, String right) {
        return StringfulError.of(left, wrapper.apply(throwable.getMessage()), right);
    }

    @Override
    public C toMessage(StringfulError<C> error) {
        return joinFunction.join(error.getLeft(), error.getMessage(), error.getRight());
    }
}
