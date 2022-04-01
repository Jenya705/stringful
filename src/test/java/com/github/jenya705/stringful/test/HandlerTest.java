package com.github.jenya705.stringful.test;

import com.github.jenya705.stringful.StringfulArgument;
import com.github.jenya705.stringful.StringfulArgumentParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Jenya705
 */
public class HandlerTest {

    @Test
    public void test1() {
        AtomicReference<String> retValue = new AtomicReference<>();
        StringfulArgument<String, Void> helloCommand = StringfulArgument.from(String.class, Void.class, "hello")
                .defaultNode(StringfulArgument
                        .from(String.class, Void.class, "name")
                        .handler(data -> retValue.set("hello, " + data.getValue("name")))
                )
                .handler(data -> retValue.set("hello, world"));
        StringfulArgumentParser parser = new StringfulArgumentParser();
        parser.parse(helloCommand, "hello jenya705").handle();
        Assertions.assertEquals("hello, jenya705", retValue.get());
        parser.parse(helloCommand, "hello").handle();
        Assertions.assertEquals("hello, world", retValue.get());
    }

}
