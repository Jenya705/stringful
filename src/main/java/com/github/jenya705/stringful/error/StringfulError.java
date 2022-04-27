package com.github.jenya705.stringful.error;

/**
 * @author Jenya705
 */
public interface StringfulError<C> {

    static <C> StringfulError<C> of(String left, C message, String right) {
        return new StringfulErrorImpl<>(left, message, right);
    }

    String getLeft();

    C getMessage();

    String getRight();

}