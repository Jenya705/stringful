package com.github.jenya705.stringful.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Jenya705
 */
public class StringfulErrorManagerImpl<C> implements StringfulErrorManager<C> {

    @Getter
    @RequiredArgsConstructor
    private static class ProxyStringfulError<C> implements StringfulError<C> {
        @Delegate
        private final StringfulError<C> source;
        private final StringfulErrorParser<C, StringfulError<C>, ?> parser;
    }

    private final Map<Class<? extends Throwable>, StringfulErrorParser<C, StringfulError<C>, Throwable>>
            classParsers = new ConcurrentHashMap<>();
    private final AtomicReference<StringfulErrorParser<C, StringfulError<C>, Throwable>>
            defaultParser = new AtomicReference<>();

    @Override
    public StringfulErrorManager<C> defaultParser(StringfulErrorParser<C, StringfulError<C>, Throwable> parser) {
        defaultParser.set(parser);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Throwable> StringfulErrorManager<C> parser(Class<T> throwableClass, StringfulErrorParser<C, StringfulError<C>, T> parser) {
        classParsers.put(throwableClass, new StringfulErrorParser<>() {
            @Override
            public StringfulError<C> toError(String left, Throwable throwable, String right) {
                return parser.toError(left, (T) throwable, right);
            }

            @Override
            public C toMessage(StringfulError<C> error) {
                return parser.toMessage(error);
            }
        });
        return this;
    }

    @Override
    public StringfulError<C> toError(String left, Throwable throwable, String right) {
        StringfulErrorParser<C, StringfulError<C>, Throwable> errorParser =
                classParsers.getOrDefault(throwable.getClass(), defaultParser.get());
        if (errorParser == null) return null;
        StringfulError<C> error = errorParser.toError(left, throwable, right);
        return new ProxyStringfulError<>(error, errorParser);
    }

    @Override
    public C toMessage(StringfulError<C> error) {
        if (error instanceof ProxyStringfulError) {
            ProxyStringfulError<C> proxy = (ProxyStringfulError<C>) error;
            return proxy.getParser().toMessage(proxy.getSource());
        }
        return defaultParser.get().toMessage(error);
    }

}
