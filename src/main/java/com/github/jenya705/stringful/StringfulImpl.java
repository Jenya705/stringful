package com.github.jenya705.stringful;

import com.github.jenya705.stringful.bukkit.BukkitStringful;
import com.github.jenya705.stringful.error.StringfulError;
import com.github.jenya705.stringful.error.StringfulErrorManager;
import com.github.jenya705.stringful.error.StringfulErrorManagerImpl;
import com.github.jenya705.stringful.error.StringfulErrorParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author Jenya705
 */
public class StringfulImpl<A, C> implements Stringful<A, C> {

    private final StringfulArgumentParser parser = new StringfulArgumentParser();
    private final StringfulErrorManager<C> errorManager = new StringfulErrorManagerImpl<>();
    private final Collection<String> commands = new ArrayList<>();
    private final Map<Class<?>, Consumer<StringfulArgument<Object, A>>> argumentCreators = new ConcurrentHashMap<>();

    private final StringfulArgument<String, A> rootCommand;
    private final Class<A> additionalClass;

    public StringfulImpl(Class<A> additionalClass,
                         StringfulErrorParser<C, StringfulError<C>, Throwable> defaultErrorParser) {
        BukkitStringful.addDefaultArgumentIfNeed(this);
        this.additionalClass = additionalClass;
        rootCommand = StringfulArgument
                .from(String.class, additionalClass,"stringful-root-command")
                .tab(data -> commands);
        errorManager.defaultParser(defaultErrorParser);
    }

    @Override
    public <T> StringfulArgument<T, A> createCommand(String name, Class<T> clazz) {
        StringfulArgument<T, A> argument = createArgument(name, clazz);
        rootCommand.node(name, argument);
        commands.add(name);
        return argument;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> StringfulArgument<T, A> createArgument(String name, Class<T> clazz) {
        StringfulArgument<T, A> argument = StringfulArgument.from(clazz, additionalClass, name);
        argumentCreators
                .getOrDefault(clazz, val -> StringfulArgument.from(clazz, additionalClass, name))
                .accept((StringfulArgument<Object, A>) argument);
        return argument;
    }

    @Override
    public StringfulArgument<String, A> getRootCommand() {
        return rootCommand;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Stringful<A, C> argumentCreator(Class<T> clazz, Consumer<StringfulArgument<T, A>> argument) {
        argumentCreators.put(clazz, obj -> argument.accept((StringfulArgument<T, A>) obj));
        return this;
    }

    @Override
    public StringfulArgumentParser getParser() {
        return parser;
    }

    @Override
    public StringfulErrorManager<C> getErrorManager() {
        return errorManager;
    }
}
