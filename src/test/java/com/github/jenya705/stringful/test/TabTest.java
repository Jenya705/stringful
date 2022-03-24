package com.github.jenya705.stringful.test;

import com.github.jenya705.stringful.StringfulArgument;
import com.github.jenya705.stringful.StringfulArgumentParser;
import com.github.jenya705.stringful.StringfulData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Jenya705
 */
public class TabTest {

    @Test
    public void test1() {
        StringfulArgument<String> root = StringfulArgument
                .from(String.class, "hello")
                .node("jenya705", StringfulArgument
                        .from(String.class, "name")
                        .tab("jenya705", "dj5613")
                )
                .defaultNode(StringfulArgument
                        .from(String.class, "secondname")
                        .tab("secondname", "firstname")
                );
        StringfulArgumentParser parser = new StringfulArgumentParser();
        StringfulData data = parser.parse(root, "jenya705 ");
        Collection<String> tab = data.handleTab(true);
        Assertions.assertEquals(Arrays.asList("jenya705", "dj5613"), new ArrayList<>(tab));
        Collection<String> tab2 = data.handleTab(false);
        Assertions.assertTrue(tab2.isEmpty());
    }

}
