package com.github.jenya705.stringful;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Parses input using {@link StringfulArgument}
 * <p>
 * Parsing rules:
 * - words in " count as one argument (except when before " is \)
 * - words in brackets is array (except when before [ is \)
 *
 * @author Jenya705
 */
public class StringfulArgumentParser {

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
        T parse(ListIterator<String> values);

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
        newParser(String.class, ListIterator::next);
        newParser(String[].class, values -> {
            List<String> newList = new ArrayList<>();
            while (values.hasNext()) newList.add(values.next());
            return newList.toArray(String[]::new);
        });
    }

    public StringfulData parse(StringfulArgument<?> root, String input) {
        List<List<String>> parsedObjects = parseToObjects(input);
        StringfulArgument<?> currentArgument = root;
        Map<String, Object> values = new HashMap<>();
        List<StringfulArgument<?>> arguments = new ArrayList<>();
        arguments.add(currentArgument);
        bracketsLoop:
        for (List<String> brackets : parsedObjects) {
            ListIterator<String> currentIterator = brackets.listIterator();
            while (true) {
                if (currentArgument == null) break bracketsLoop;
                Object obj = parsers.get(currentArgument.getArgumentClass()).parse(currentIterator);
                values.put(currentArgument.getName(), obj);
                currentArgument = currentArgument.getNextNode(obj);
                arguments.add(currentArgument);
                if (!currentIterator.hasNext()) {
                    continue bracketsLoop;
                }
            }
        }
        return new StringfulData(arguments, values);
    }

    public <T> void newParser(Class<T> clazz, Parser<T> parser) {
        parsers.put(clazz, parser::parse);
    }

    private List<List<String>> parseToObjects(String input) {
        List<List<String>> endList = new ArrayList<>();
        List<String> currentBrackets = new ArrayList<>();
        boolean skipNext = false;
        int bracketsStart = -1;
        boolean inContainer = false;
        int wordStart = 0;
        int i = 0;
        for (char ch : input.toCharArray()) {
            if (!skipNext) {
                if (ch == '[') { // brackets start
                    if (bracketsStart != -1) throw new IllegalArgumentException("Brackets in brackets!");
                    bracketsStart = i;
                    if (!currentBrackets.isEmpty()) endList.add(currentBrackets);
                    wordStart++;
                    currentBrackets = new ArrayList<>();
                }
                else if (ch == ']') {
                    if (bracketsStart != -1) {
                        if (wordStart != i) {
                            currentBrackets.add(input.substring(wordStart, i));
                            wordStart = i + 1;
                        }
                        endList.add(currentBrackets);
                        currentBrackets = new ArrayList<>();
                    }
                }
                else if (ch == '\\') {
                    skipNext = true;
                }
                else if (ch == '"') {
                    if (inContainer) {
                        currentBrackets.add(input.substring(wordStart + 1, i));
                    }
                    if (wordStart == i) {
                        inContainer = true;
                    }
                }
                else if (ch == ' ') {
                    if (!inContainer && wordStart != i) {
                        currentBrackets.add(input.substring(wordStart, i));
                    }
                    wordStart = i + 1;
                }
            }
            else {
                skipNext = false;
            }
            i++;
        }
        if (inContainer) {
            throw new IllegalArgumentException("\" is not closed");
        }
        if (wordStart != i) {
            currentBrackets.add(input.substring(wordStart));
        }
        if (!currentBrackets.isEmpty()) {
            endList.add(currentBrackets);
        }
        return endList;
    }

}
