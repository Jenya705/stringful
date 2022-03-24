package com.github.jenya705.stringful;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Jenya705
 */
public class StringfulArgumentImpl<T> implements StringfulArgument<T> {

    private final String name;
    private final Class<T> argumentClass;

    private final List<String> tabs = new ArrayList<>();
    private final Map<T, StringfulArgument<?>> nodes = new ConcurrentHashMap<>();
    private StringfulArgument<?> defaultNode = null;
    private Consumer<StringfulData> handler = null;
    private Function<StringfulData, Collection<String>> tabFunction = null;

    public StringfulArgumentImpl(@NotNull String name, @NotNull Class<T> argumentClass) {
        this.name = name;
        this.argumentClass = argumentClass;
    }

    @Override
    public @NotNull StringfulArgument<T> tab(String... values) {
        tabs.addAll(Arrays.asList(values));
        return this;
    }

    @Override
    public @NotNull StringfulArgument<T> tab(Collection<String> values) {
        tabs.addAll(values);
        return this;
    }

    @Override
    public @NotNull StringfulArgument<T> tab(Function<StringfulData, Collection<String>> function) {
        this.tabFunction = function;
        return this;
    }

    @Override
    public @NotNull StringfulArgument<T> defaultNode(StringfulArgument<?> node) {
        defaultNode = node;
        return this;
    }

    @Override
    public @NotNull StringfulArgument<T> node(T byValue, StringfulArgument<?> node) {
        nodes.put(byValue, node);
        return this;
    }

    @Override
    public @NotNull StringfulArgument<T> handler(Consumer<StringfulData> handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull Class<T> getArgumentClass() {
        return argumentClass;
    }

    @Override
    public StringfulArgument<?> getNextNode(Object value) {
        return nodes.getOrDefault(value, defaultNode);
    }

    @Override
    public Consumer<StringfulData> getHandler() {
        return handler;
    }

    @Override
    public @NotNull Collection<String> handleTab(StringfulData data) {
        if (tabFunction != null) {
            Collection<String> tabFunctionResult = tabFunction.apply(data);
            if (tabs.isEmpty()) {
                return Collections.unmodifiableCollection(tabFunctionResult);
            }
            else {
                Collection<String> result = new ArrayList<>(tabs);
                result.addAll(tabFunctionResult);
                return Collections.unmodifiableCollection(result);
            }
        }
        return Collections.unmodifiableCollection(tabs);
    }
}
