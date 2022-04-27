package com.github.jenya705.stringful;

import com.github.jenya705.stringful.bukkit.BukkitStringful;
import com.github.jenya705.stringful.error.RuntimeStringfulException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Parses input using {@link StringfulArgument}
 *
 * Parsing rules:
 * - words in " count as one argument (except when before " is \)
 * - words in brackets is array (except when before [ is \)
 *
 * @author Jenya705
 */
public class StringfulArgumentParser {

    @Getter
    @RequiredArgsConstructor
    private static class ParserIteratorImpl implements Iterator<String> {

        private final String input;

        private int current = 0;
        private boolean brackets = false;

        @Override
        public boolean hasNext() {
            return realHasNext() &&
                    !(brackets && (input.charAt(current) == ']' || input.charAt(current) == '['));
        }

        public boolean realHasNext() {
            return input.length() != current;
        }

        @Override
        public String next() {
            while (input.charAt(current) == ' ') current++;
            int lastWhitespace = input.indexOf(' ', current);
            int startCurrent = current;
            char startChar = input.charAt(current);
            if (startChar == '"') {
                boolean skipNext = false;
                while (true) {
                    if (skipNext) {
                        skipNext = false;
                    }
                    else {
                        if (input.length() == current) throw new RuntimeStringfulException("No end of quotes", input, "");
                        char ch = input.charAt(current);
                        if (ch == '"') {
                            return input.substring(startCurrent, current);
                        }
                        if (ch == '\\') {
                            skipNext = true;
                        }
                    }
                    current++;
                }
            }
            if ((startChar == ']' && brackets) || (startChar == '[' && !brackets)) {
                throw new IndexOutOfBoundsException();
            }
            if (lastWhitespace == -1) {
                current = input.length();
                int ends = input.length();
                if (brackets && input.charAt(ends - 1) == ']') {
                    ends--;
                }
                return input.substring(startCurrent, ends);
            }
            if (brackets && input.charAt(lastWhitespace - 1) == ']') {
                lastWhitespace--;
                current = lastWhitespace;
            }
            else {
                current = lastWhitespace + 1;
            }
            return input.substring(startCurrent, lastWhitespace);
        }

        public void newParser() {
            if (!realHasNext()) return;
            char currentChar = input.charAt(current);
            brackets = currentChar == '[';
            if (brackets || currentChar == ']') current++;
        }

    }

    /**
     * Value parser
     *
     * @param <T> value that will be returned
     */
    @FunctionalInterface
    public interface Parser<T> {

        /**
         * Parsing string value to need value
         *
         * @param values string values which is included in parser (if arguments in brackets list iterator will be limited)
         * @return Parsed value
         */
        @Nullable T parse(@NotNull Iterator<String> values);

    }

    private final Map<Class<?>, Parser<Object>> parsers = new ConcurrentHashMap<>();

    public StringfulArgumentParser() {
        newParser(byte.class, values -> Byte.parseByte(values.next()));
        newParser(short.class, values -> Short.parseShort(values.next()));
        newParser(int.class, values -> Integer.parseInt(values.next()));
        newParser(long.class, values -> Long.parseLong(values.next()));
        newParser(float.class, values -> Float.parseFloat(values.next()));
        newParser(double.class, values -> Double.parseDouble(values.next()));
        newParser(Byte.class, values -> Byte.valueOf(values.next()));
        newParser(Short.class, values -> Short.valueOf(values.next()));
        newParser(Integer.class, values -> Integer.valueOf(values.next()));
        newParser(Long.class, values -> Long.valueOf(values.next()));
        newParser(Float.class, values -> Float.valueOf(values.next()));
        newParser(Double.class, values -> Double.valueOf(values.next()));
        newParser(String.class, Iterator::next);
        newParser(String[].class, values -> {
            List<String> newList = new ArrayList<>();
            while (values.hasNext()) newList.add(values.next());
            return newList.toArray(String[]::new);
        });
        BukkitStringful.addParsersIfNeed(this);
    }

    public <A> @NotNull StringfulData<A> parse(@NotNull StringfulArgument<?, A> root, @NotNull String input) {
        return parse(root, input, null);
    }

    public <A> @NotNull StringfulData<A> parse(@NotNull StringfulArgument<?, A> root, @NotNull String input, @Nullable A additionalInformation) {
        ParserIteratorImpl iterator = new ParserIteratorImpl(input);
        StringfulArgument<?, A> currentArgument = root;
        Map<String, Object> values = new HashMap<>();
        List<StringfulArgument<?, A>> arguments = new ArrayList<>();
        iterator.newParser();
        while (iterator.realHasNext()) {
            arguments.add(currentArgument);
            try {
                Object obj = parsers.get(currentArgument.getArgumentClass()).parse(iterator);
                values.put(currentArgument.getName(), obj);
                currentArgument = currentArgument.getNextNode(obj);
                iterator.newParser();
            } catch (Throwable throwable) {
                throw new RuntimeStringfulException(
                        throwable,
                        iterator.getInput().substring(0, iterator.getCurrent()),
                        iterator.realHasNext() ?
                                iterator.getInput().substring(iterator.getCurrent()) : ""
                );
            }
        }
        return new StringfulData<>(arguments, values, additionalInformation);
    }

    public <T> void newParser(@NotNull Class<T> clazz, @NotNull Parser<T> parser) {
        parsers.put(clazz, parser::parse);
    }

}
