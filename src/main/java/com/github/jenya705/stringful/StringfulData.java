package com.github.jenya705.stringful;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

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

}
