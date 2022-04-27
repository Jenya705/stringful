package com.github.jenya705.stringful.error;

import lombok.Getter;

/**
 * @author Jenya705
 */
@Getter
public class RuntimeStringfulException extends RuntimeException implements StringfulError<String> {

    private final String left;
    private final String right;

    public RuntimeStringfulException(String message, String left, String right) {
        super(message);
        this.left = left;
        this.right = right;
    }

    public RuntimeStringfulException(Throwable cause, String left, String right) {
        super(cause);
        this.left = left;
        this.right = right;
    }

}
