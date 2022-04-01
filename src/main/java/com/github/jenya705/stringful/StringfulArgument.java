package com.github.jenya705.stringful;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * Argument node
 *
 * @author Jenya705
 */
public interface StringfulArgument<T, A> {

    @NotNull
    static <T, A> StringfulArgument<T, A> from(Class<T> clazz, Class<A> additionalClazz, String argumentName) {
        return new StringfulArgumentImpl<>(argumentName, clazz);
    }

    /**
     *
     * Adds string values to tab collection.
     *
     * @param values string values that should be added to tab collection
     * @return argument with added tab
     */
    @NotNull
    StringfulArgument<T, A> tab(String... values);

    /**
     *
     * Adds string values to tab collection.
     *
     * @param values string values that should be added to tab collection
     * @return argument with added tab
     */
    @NotNull
    StringfulArgument<T, A> tab(Collection<String> values);

    /**
     *
     * Sets dynamic tab function. Should be executed on each tab handle
     *
     * @param function tab function
     * @return argument with that tab function
     */
    @NotNull
    StringfulArgument<T, A> tab(Function<StringfulData<A>, Collection<String>> function);

    /**
     *
     * Default next node. Will be returned if no node ({@link #node(T, StringfulArgument)} specified
     *
     * @param node that default argument
     * @return argument with set default argument
     */
    @NotNull
    StringfulArgument<T, A> defaultNode(StringfulArgument<?, A> node);

    /**
     *
     * Next argument by given value
     *
     * @param byValue that given value
     * @param node next argument
     * @return argument with added node
     */
    @NotNull
    StringfulArgument<T, A> node(T byValue, StringfulArgument<?, A> node);

    /**
     *
     * Sets argument handler.
     *
     * @param handler handler
     * @return argument with added handler
     */
    @NotNull
    StringfulArgument<T, A> handler(Consumer<StringfulData<A>> handler);

    @NotNull
    String getName();

    @NotNull
    Class<T> getArgumentClass();

    @Nullable
    StringfulArgument<?, A> getNextNode(Object value);

    @Nullable
    Consumer<StringfulData<A>> getHandler();

    @NotNull
    Collection<String> handleTab(StringfulData<A> data);

}
