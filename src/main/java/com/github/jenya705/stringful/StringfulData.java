package com.github.jenya705.stringful;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class StringfulData {

    @Getter
    private final List<StringfulArgument<?>> arguments;
    private final Map<String, Object> values;

    @SuppressWarnings("unchecked")
    public <T> T getValue(String name) {
        return (T) values.get(name);
    }

    public <T> T getValue(StringfulArgument<T> argument) {
        return getValue(argument.getName());
    }

    public void handle() {
        for (int i = arguments.size() - 1; i >= 0; --i) {
            StringfulArgument<?> argument = arguments.get(i);
            Consumer<StringfulData> handler = argument.getHandler();
            if (handler != null) {
                handler.accept(this);
                break;
            }
        }
    }

    public Collection<String> handleTab(boolean nextArgument) {
        StringfulArgument<?> argument = arguments.get(arguments.size() - 1);
        if (nextArgument) {
            argument = argument.getNextNode(getValue(argument));
        }
        return argument == null ? Collections.emptyList() : argument.handleTab(this);
    }

}
