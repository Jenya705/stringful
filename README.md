# stringful
yet another command framework

# dependency
see: https://jitpack.io/#Jenya705/stringful

# examples

## Only execution example
```java
AtomicReference<String> retValue = new AtomicReference<>();
StringfulArgument<String> helloCommand = StringfulArgument.from(String.class, "hello")
  .defaultNode(StringfulArgument
    .from(String.class, "name")
    .handler(data -> retValue.set("hello, " + data.getValue("name")))
  )
  .handler(data -> retValue.set("hello, world"));
StringfulArgumentParser parser = new StringfulArgumentParser();
parser.parse(helloCommand, "hello jenya705").handle();
Assertions.assertEquals("hello, jenya705", retValue.get());
parser.parse(helloCommand, "hello").handle();
Assertions.assertEquals("hello, world", retValue.get());
```
## Suggestions example
```java
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
```
