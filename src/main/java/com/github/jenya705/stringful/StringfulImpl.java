package com.github.jenya705.stringful;

import com.github.jenya705.stringful.bukkit.BukkitStringful;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author Jenya705
 */
public class StringfulImpl implements Stringful {

    private final StringfulArgumentParser parser = new StringfulArgumentParser();
    private final Collection<String> commands = new ArrayList<>();
    private final StringfulArgument<String> rootCommand = StringfulArgument
            .from(String.class, "stringful-root-command")
            .tab(data -> commands);
    private final Map<Class<?>, Function<String, StringfulArgument<?>>> argumentCreators = new ConcurrentHashMap<>();

    public StringfulImpl() {
        BukkitStringful.addDefaultArgumentIfNeed(this);
    }

    @Override
    public <T> StringfulArgument<T> createCommand(String name, Class<T> clazz) {
        StringfulArgument<T> argument = createArgument(name, clazz);
        rootCommand.node(name, argument);
        commands.add(name);
        return argument;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> StringfulArgument<T> createArgument(String name, Class<T> clazz) {
        return (StringfulArgument<T>) argumentCreators
                .getOrDefault(clazz, val -> StringfulArgument.from(clazz, name))
                .apply(name);
    }

    @Override
    public StringfulArgument<String> getRootCommand() {
        return rootCommand;
    }

    @Override
    public <T> Stringful argumentCreator(Class<T> clazz, Function<String, StringfulArgument<T>> argument) {
        argumentCreators.put(clazz, argument::apply);
        return this;
    }

    @Override
    public StringfulArgumentParser getParser() {
        return parser;
    }
}
