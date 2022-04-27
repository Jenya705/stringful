package com.github.jenya705.stringful.error;

import lombok.Data;

/**
 * @author Jenya705
 */
@Data
class StringfulErrorImpl<C> implements StringfulError<C> {

    private final String left;
    private final C message;
    private final String right;

}
