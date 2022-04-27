package com.github.jenya705.stringful;

import com.github.jenya705.stringful.error.RuntimeStringfulException;
import com.github.jenya705.stringful.error.StringfulError;
import com.github.jenya705.stringful.error.StringfulErrorManager;
import com.github.jenya705.stringful.error.StringfulErrorParser;

import java.util.Collection;
import java.util.function.Consumer;

/**
 *
 * Manager for stringful
 *
 * @author Jenya705
 */
public interface Stringful<A, C> {

    static <A, C> Stringful<A, C> create(Class<A> additionalClass,
                                         StringfulErrorParser<C, StringfulError<C>, Throwable> defaultErrorParser) {
        return new StringfulImpl<>(additionalClass, defaultErrorParser);
    }

    <T> StringfulArgument<T, A> createCommand(String name, Class<T> clazz);

    <T> StringfulArgument<T, A> createArgument(String name, Class<T> clazz);

    StringfulArgument<String, A> getRootCommand();

    <T> Stringful<A, C> argumentCreator(Class<T> clazz, Consumer<StringfulArgument<T, A>> argument);

    StringfulArgumentParser getParser();

    StringfulErrorManager<C> getErrorManager();

    default <T> Stringful<A, C> argumentParser(Class<T> clazz, StringfulArgumentParser.Parser<T> parser) {
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

    default StringfulResult<StringfulData<A>, C> handleWithErrorHandling(String input) {
        try {
            return StringfulResult.success(handleCommand(input));
        } catch (RuntimeStringfulException e) {
            return StringfulResult.error(
                    getErrorManager().toMessage(
                            e.getLeft(),
                            e.getCause() == null ? e: e.getCause(),
                            e.getRight()
                    )
            );
        }
    }

}
