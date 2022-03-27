package com.github.jenya705.stringful;

import java.util.Collection;
import java.util.function.Function;

/**
 *
 * Manager for stringful
 *
 * @author Jenya705
 */
public interface Stringful {

    <T> StringfulArgument<T> createCommand(String name, Class<T> clazz);

    <T> StringfulArgument<T> createArgument(String name, Class<T> clazz);

    StringfulArgument<String> getRootCommand();

    <T> Stringful argumentCreator(Class<T> clazz, Function<String, StringfulArgument<T>> argument);

    StringfulArgumentParser getParser();

    default <T> Stringful argumentParser(Class<T> clazz, StringfulArgumentParser.Parser<T> parser) {
        getParser().newParser(clazz, parser);
        return this;
    }

    default StringfulData parseCommand(String input) {
        return getParser().parse(getRootCommand(), input);
    }

    default StringfulData handleCommand(String input) {
        StringfulData data = parseCommand(input);
        data.handle();
        return data;
    }

    default Collection<String> tabCommand(String input) {
        StringfulData data = parseCommand(input);
        return data.handleTab(input.endsWith(" "));
    }

}
