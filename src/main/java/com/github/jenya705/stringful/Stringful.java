package com.github.jenya705.stringful;

import java.util.Collection;
import java.util.function.Consumer;

/**
 *
 * Manager for stringful
 *
 * @author Jenya705
 */
public interface Stringful<A> {

    static <A> Stringful<A> create(Class<A> additionalClass) {
        return new StringfulImpl<>(additionalClass);
    }

    <T> StringfulArgument<T, A> createCommand(String name, Class<T> clazz);

    <T> StringfulArgument<T, A> createArgument(String name, Class<T> clazz);

    StringfulArgument<String, A> getRootCommand();

    <T> Stringful<A> argumentCreator(Class<T> clazz, Consumer<StringfulArgument<T, A>> argument);

    StringfulArgumentParser getParser();

    default <T> Stringful<A> argumentParser(Class<T> clazz, StringfulArgumentParser.Parser<T> parser) {
        getParser().newParser(clazz, parser);
        return this;
    }

    default StringfulData<A> parseCommand(String input) {
        return getParser().parse(getRootCommand(), input);
    }

    default StringfulData<A> handleCommand(String input) {
        StringfulData<A> data = parseCommand(input);
        data.handle();
        return data;
    }

    default Collection<String> tabCommand(String input) {
        StringfulData<A> data = parseCommand(input);
        return data.handleTab(input.endsWith(" "));
    }

}
