package com.github.jenya705.stringful;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author Jenya705
 */
public interface StringfulArgument<T> {

    static <T> StringfulArgument<T> from(Class<T> clazz, String argumentName) {
        return new StringfulArgumentImpl<>(argumentName, clazz);
    }

    StringfulArgument<T> tab(T... values);

    StringfulArgument<T> tab(Collection<T> values);

    StringfulArgument<T> tabString(String... values);

    StringfulArgument<T> tabString(Collection<String> values);

    StringfulArgument<T> nextArgument(StringfulArgument<?> nextArgument);

    StringfulArgument<T> node(T byValue, StringfulArgument<?> nextArgument);

    StringfulArgument<T> handler(Consumer<StringfulData> handler);

    String getName();

    Class<T> getArgumentClass();

    StringfulArgument<?> getNextNode(Object value);

    Consumer<StringfulData> getHandler();

}
