# Minecraft Plugin Engine
![Version](https://img.shields.io/badge/version-1.0.1-blue.svg)

## Overview
**Minecraft Command Engine** is a flexible and powerful library designed to help you build Minecraft plugins that are compatible with Minecraft versions 1.8 - 1.21. It offers convenient and fluent utilities for handling commands, configuration management, versioning, and time operations, making plugin development easier and more efficient.

This engine includes the following main components:

- [**ConfigLib**](#configlib): Simplified, annotation-based configuration management.
- [**CommandLib**](#commandlib): A command handling system supporting dynamic command registration, subcommands, and tab completion.
- **Util Classes**: A collection of useful utilities, including time and version helpers.
- **XSeries**: The engine shades in the XSeries library which provides multi-version supported objects such as `XMaterial`, `XSound`, `XBlock`, `XItemStack`, etc.

## Installation

To use the Minecraft Command Engine in your project, you can include the following dependencies based on your build system.

### Maven Dependency
Add the following Maven dependency to your `pom.xml` file under `dependencies`:

```xml
<dependency>
    <groupId>dev.splityosis</groupId>
    <artifactId>sysengine</artifactId>
    <version>1.0.1</version>
    <scope>provided</scope>
</dependency>
```

### Gradle Dependency
If you're using Gradle, add this to your `build.gradle` file under `dependencies`:
```gradle
dependencies {
    compileOnly 'dev.splityosis:sysengine:1.0.0-SNAPSHOT'
}
```

## Shading
While shading the library into your plugin’s JAR file is possible, it is not recommended in most cases. This is because some cross-plugin features that rely on dynamically loaded classes or plugin-to-plugin interactions might not work as expected when shaded (e.g, ConfigMappers, CommandArguments etc...).

# Documentation
## ConfigLib
The library's intention is to be intuitive and easy to use while not restricting any functionality you might want to achieve.
The library offers deep functionalities such as `ConfigMappers` and annotations such as:
- `@Field(path)` - Decalres a field in the config, and if the parameter is left empty it will generate a path from the field name.
- `@Section(path)` - Declares a section in the config and if the parameter is left empty it will go back to the root section.
- `@FieldComment(comments...)` - Sets comments above the field.
- `@SectionComment(comments...)` - Sets comments above the section.
- `@FieldInlineComment(comments...)` - Sets comments inline after the field.
- `@SectionInlineComment(comments...)` - Sets comments inline after the section.
- `@Mapper(mapper)` - Sets a custom mapper to be used when mapping the object to the config, if left empty or undeclared it will use the default one (if exists) or fall back to spigot's logic.

### Simple Usage Example
**Create a `ConfigManager` instance in your main plugin class.**
```java
    private ConfigManager configManager = ConfigLib.createConfigManager();
```

***(Optional)* Set a custom `ConfigOptions` to match your likings.**
```java
    private ConfigManager configManager = ConfigLib.createConfigManager()
            .setConfigOptions(new ConfigOptions()
                    .setSectionSpacing(1)
                    .setFieldSpacing(0));
```

**Make a class and implement `Configuration`, write your config using annotations:**
```java
public class ExampleConfig implements Configuration {
    
    @Section("section.example") // Everything under this will be inside the section until another @Section is declared
    
    @Field public Material blockMaterial = Material.BAMBOO; // Enums are automatically parsed
    @Field public int amount = 5;
    
    @FieldInlineComment("In format DD/MM/YYYY")
    @Field public String date = "22/11/2024";
}
```

```yaml
section:
  example:
    block-material: BAMBOO
    amount: 5
    date: 22/11/2024 # In format DD/MM/YYYY
```

**Initialize and register the config using the ConfigManager:**
```java
    private ExampleConfig exampleConfig;

    @Override
    public void onEnable() {
            exampleConfig = new ExampleConfig();
            configManager.registerConfig(exampleConfig, new File(getDataFolder(), "example-config.yml"));
    }
```

**Reload a registered config:**
```java
    public void reloadConfigs() {
            configManager.reload(exampleConfig);
    }
```

**Save a registered config:**
```java
    public void saveConfigs() {
            configManager.save(exampleConfig);
    }
```

## CommandLib
The library's intention is to be intuitive and easy to use while not restricting any functionality you might want to achieve.
The library offers deep functionalities such as `CommandHelpProvider` and required and optional `CommandArguments` support.

### Simple Usage Example
**Create a `CommandManager` instance in your main plugin class.**
```java
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        commandManager = CommandLib.createCommandManager(this);
    }
```

***(Optional)* Set a custom `CommandHelpProvider`.**
```java
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        commandManager = CommandLib.createCommandManager(this)
                .setCommandHelpProvider(new CustomHelpProvider());
    }
```

**Make a command and fill your logic in, this is an example of a `/Give <Receiver> <Item> [<Amount>]` command:**
```java
        Command giveCommand = new Command("Give", "Item", "GiveItem")
                .description("Gives item to player")
                .permission("example.give")
                .arguments(
                        new PlayerArgument("Receiver"),
                        new EnumArgument<>("Item", Material.class)
                )
                .optionalArguments(
                        new IntegerArgument("Amount")
                )
                .executes((sender, context) -> {

                    Player receiver = (Player) context.getArg("Receiver");
                    Material material = (Material) context.getArg("Item");
                    int amount = (Integer) context.getArgOrDefault("Amount", 1);

                    ItemStack item = XMaterial.matchXMaterial(material).parseItem(); // Using XSeries for multi-version compatibility
                    item.setAmount(amount);

                    receiver.getInventory().addItem(item);
                });
```

**Register the command using the CommandManager:**
```java
    public void registerCommands() {
        commandManager.registerCommands(giveCommand);
    }
```

**Example of a World CommandArgument:**
```java
public class WorldArgument implements CommandArgument<World> {

    private final String name;

    public WorldArgument(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public World parse(CommandSender sender, String input, Command command, int index, CommandContext context) throws InvalidInputException {
        World world = Bukkit.getWorld(input);

        if (world == null)
            throw new InvalidInputException();

        return world;
    }

    @Override
    public void onInvalidInput(CommandSender sender, String input, Command command, int index, CommandContext context) {
        sender.sendMessage(ChatColor.RED + "Invalid world name! Please enter a valid world name.");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String input, Command command, int index, RawCommandContext context) {
        return Bukkit.getWorlds().stream()
                .map(World::getName)
                .filter(worldName -> worldName.toLowerCase().startsWith(input.toLowerCase()))
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }
}
```
