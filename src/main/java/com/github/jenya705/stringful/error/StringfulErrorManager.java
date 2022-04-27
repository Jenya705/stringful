package com.github.jenya705.stringful.error;

/**
 * @author Jenya705
 */
public interface StringfulErrorManager<C> extends StringfulErrorParser<C, StringfulError<C>, Throwable> {

    StringfulErrorManager<C> defaultParser(StringfulErrorParser<C, StringfulError<C>, Throwable> parser);

    <T extends Throwable> StringfulErrorManager<C> parser(Class<T> throwableClass,
                                                          StringfulErrorParser<C, StringfulError<C>, T> parser);

}
