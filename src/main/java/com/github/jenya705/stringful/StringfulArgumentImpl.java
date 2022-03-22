package com.github.jenya705.stringful;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jenya705
 */
public class StringfulArgumentImpl<T> implements StringfulArgument<T> {

    private final String name;
    private final Class<T> argumentClass;

    private final List<String> tabs = new ArrayList<>();
    private final Map<T, StringfulArgument<?>> nodes = new ConcurrentHashMap<>();
    private StringfulArgument<?> defaultNode = null;

    public StringfulArgumentImpl(String name, Class<T> argumentClass) {
        this.name = name;
        this.argumentClass = argumentClass;
    }

    @Override
    public StringfulArgument<T> tab(T... values) {
        for (T value: values) tabs.add(value.toString());
        return this;
    }

    @Override
    public StringfulArgument<T> tab(Collection<T> values) {
        for (T value: values) tabs.add(value.toString());
        return this;
    }

    @Override
    public StringfulArgument<T> tabString(String... values) {
        tabs.addAll(Arrays.asList(values));
        return this;
    }

    @Override
    public StringfulArgument<T> tabString(Collection<String> values) {
        tabs.addAll(values);
        return this;
    }

    @Override
    public StringfulArgument<T> nextArgument(StringfulArgument<?> nextArgument) {
        defaultNode = nextArgument;
        return this;
    }

    @Override
    public StringfulArgument<T> node(T byValue, StringfulArgument<?> nextArgument) {
        nodes.put(byValue, nextArgument);
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<T> getArgumentClass() {
        return argumentClass;
    }

    @Override
    public StringfulArgument<?> getNextNode(Object value) {
        return nodes.getOrDefault(value, defaultNode);
    }
}
