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
public class StringfulData<A> {

    @Getter
    private final List<StringfulArgument<?, A>> arguments;
    private final Map<String, Object> values;

    private final A additionalInformation;

    @SuppressWarnings("unchecked")
    public <T> T getValue(String name) {
        return (T) values.get(name);
    }

    public <T> T getValue(StringfulArgument<T, A> argument) {
        return getValue(argument.getName());
    }

    public void handle() {
        for (int i = arguments.size() - 1; i >= 0; --i) {
            StringfulArgument<?, A> argument = arguments.get(i);
            Consumer<StringfulData<A>> handler = argument.getHandler();
            if (handler != null) {
                handler.accept(this);
                break;
            }
        }
    }

    public Collection<String> handleTab(boolean nextArgument) {
        StringfulArgument<?, A> argument = arguments.get(arguments.size() - 1);
        if (nextArgument) {
            argument = argument.getNextNode(getValue(argument));
        }
        return argument == null ? Collections.emptyList() : argument.handleTab(this);
    }

    public A internalData() {
        return additionalInformation;
    }

}
