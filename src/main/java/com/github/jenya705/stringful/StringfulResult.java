package com.github.jenya705.stringful;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Jenya705
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StringfulResult<S, E> {

    private final S success;
    private final E error;

    public static <S, E> StringfulResult<S, E> success(S success) {
        return new StringfulResult<>(success, null);
    }

    public static <S, E> StringfulResult<S, E> error(E error) {
        return new StringfulResult<>(null, error);
    }

    public boolean isSuccess() {
        return getSuccess() != null;
    }

    public boolean isError() {
        return getError() != null;
    }

}
