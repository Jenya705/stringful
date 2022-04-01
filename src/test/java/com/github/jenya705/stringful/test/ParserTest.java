package com.github.jenya705.stringful.test;

import com.github.jenya705.stringful.StringfulArgument;
import com.github.jenya705.stringful.StringfulArgumentParser;
import com.github.jenya705.stringful.StringfulData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Jenya705
 */
public class ParserTest {

    @Test
    public void test1() {

        StringfulArgumentParser parser = new StringfulArgumentParser();

        StringfulArgument<String, Void> argument = StringfulArgument
                .from(String.class, Void.class, "name")
                .node(
                        "jenya705",
                        StringfulArgument.from(int.class, Void.class, "ages")
                );

        StringfulData<Void> data = parser.parse(argument, "jenya705 32 ");

        Assertions.assertEquals("name", data.getArguments().get(0).getName());
        Assertions.assertEquals("ages", data.getArguments().get(1).getName());
        Assertions.assertEquals("jenya705", data.getValue("name"));
        Assertions.assertEquals(32, (int) data.getValue("ages"));

        data = parser.parse(argument, "anothername");

        Assertions.assertEquals("name", data.getArguments().get(0).getName());
        Assertions.assertEquals("anothername", data.getValue("name"));
        Assertions.assertNull(data.getValue("ages"));
        Assertions.assertNull(data.getValue("somevalue"));

    }

    @Test
    public void test2() {
        StringfulArgumentParser parser = new StringfulArgumentParser();
        StringfulArgument<String[], Void> argument = StringfulArgument
                .from(String[].class, Void.class,"message")
                .defaultNode(StringfulArgument.from(int.class, Void.class, "count"));
        StringfulData<Void> data = parser.parse(argument, "hello hello hello");
        Assertions.assertArrayEquals(new String[]{"hello", "hello", "hello"}, data.getValue("message"));
        data = parser.parse(argument, "[hello hello hello] 32");
        Assertions.assertArrayEquals(new String[]{"hello", "hello", "hello"}, data.getValue("message"));
        Assertions.assertEquals(32, (int) data.getValue("count"));
    }

}
