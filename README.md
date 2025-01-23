# Minecraft Plugin Engine
![Version](https://img.shields.io/badge/version-1.1.11-blue.svg)

## Overview
**SYSEngine** is a flexible and powerful engine designed to help you build Minecraft plugins that are compatible with Minecraft versions 1.8 - 1.21. It offers convenient and fluent utilities for handling commands, configuration management, versioning, and much more, making plugin development easier and more efficient.

This engine includes the following main components:

- [**ConfigLib**](#configlib): Simplified, annotation-based configuration management.
- [**Actions**](#actions): A flexible system for defining and executing dynamic behaviors using configuration files.
- [**CommandLib**](#commandlib): A command handling system supporting dynamic command registration, subcommands, and tab completion.
- **Util Classes**: A collection of useful utilities, including [ColorUtil](https://github.com/SplitYoSis/SYSEngine/blob/master/src/main/java/dev/splityosis/sysengine/utils/ColorUtil.java), [TimeUtil](https://github.com/SplitYoSis/SYSEngine/blob/master/src/main/java/dev/splityosis/sysengine/utils/TimeUtil.java), [VersionUtil](https://github.com/SplitYoSis/SYSEngine/blob/master/src/main/java/dev/splityosis/sysengine/utils/VersionUtil.java), [Symbol](https://github.com/SplitYoSis/SYSEngine/blob/master/src/main/java/dev/splityosis/sysengine/utils/Symbol.java), [PapiUtil](https://github.com/SplitYoSis/SYSEngine/blob/master/src/main/java/dev/splityosis/sysengine/utils/PapiUtil.java) and more...
- **XSeries**: Shaded and relocated library which provides cross-version supported util classes such as `XMaterial`, `XSound`, `XBlock`, `XItemStack`, see more: https://github.com/CryptoMorin/XSeries
- **NBT API**: Shaded and relocated library which provides a nice and fluent api to manage NBTs, see more: https://github.com/tr7zw/Item-NBT-API

## Installation

To use the engine, include the following dependencies based on your build system.

## Installation

To use the engine, include the following dependencies based on your build system.

### Maven Dependency
Add the following repository and dependency to your `pom.xml` file:

#### Repository
```xml
    <repository>
        <id>octane</id>
        <url>https://repo.octanepvp.com/repository/maven-releases/</url>
    </repository>
```

#### Dependency
```xml
<dependency>
    <groupId>dev.splityosis</groupId>
    <artifactId>sysengine</artifactId>
    <version>{VERSION}</version>
    <scope>provided</scope>
</dependency>
```

### Gradle Dependency
If you're using Gradle, add this to your `build.gradle` file:

#### Repository
```gradle
repositories {
    maven {
        url 'https://repo.octanepvp.com/repository/maven-releases/'
    }
}
```

#### Dependency
```gradle
dependencies {
    implementation 'dev.splityosis:sysengine:{VERSION}'
}
```


## Shading
While shading the library into your plugin’s JAR file is possible, it is not recommended in most cases. This is because some cross-plugin features that rely on dynamically loaded classes or plugin-to-plugin interactions might not work as expected when shaded (e.g., `ConfigMappers`, `CommandArguments`, etc.). If you do decide to shade it in, you must call `SYSEngine#initialize(plugin)` to initialize the libraries. Additionally, ensure you relocate the package `dev.splityosis.sysengine` to avoid class conflicts.


# Documentation
## ConfigLib
The library's intention is to be intuitive and easy to use while not restricting any functionality you might want to achieve.
The library offers deep functionalities such as `ConfigMappers` and annotations such as:
- `@Field(path)` - Declares a field in the config, and if the parameter is left empty it will generate a path from the field name.
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

### Types:

The library includes several pre-registered mappers that simplify working with common data types. These can be explored in detail [here](https://github.com/SplitYoSis/SYSEngine/tree/master/src/main/java/dev/splityosis/sysengine/configlib/mappers). Additionally, collections like `List`, `Set`, and `Map` (with `String` keys) are automatically handled, ensuring seamless integration into your configuration files.

#### Example: Creating and Registering a Custom Mapper

You can define custom mappers for your specific needs. Here’s an example of a mapper for handling `Vector` objects:

```java
public class VectorMapper implements AbstractMapper<Vector> {

    @Override
    public Vector getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        String vecString = section.getString(path);
        if (vecString == null || vecString.isEmpty()) {
            return null;
        }

        String[] parts = vecString.split(" ");
        if (parts.length != 3) {
            return null;
        }

        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);

        return new Vector(x, y, z);
    }

    @Override
    public void setInConfig(ConfigManager manager, Vector instance, ConfigurationSection section, String path) {
        if (instance == null) {
            section.set(path, ""); // Set to an empty string if vector is null
            return;
        }

        String vecString = String.format("%f %f %f", instance.getX(), instance.getY(), instance.getZ());
        section.set(path, vecString);
    }
}
```

To register this custom mapper with `ConfigLib`:

```java
    ConfigLib.getMapperRegistry().registerMapper(new VectorMapper());
```

This allows `Vector` objects to be seamlessly serialized to and deserialized from your configuration files, enabling more versatile and dynamic plugin development.

## Actions

The `Actions` system enables developers and configurators to define dynamic behaviors and responses within configuration files, replacing the need to handle complex logic manually in code. Each action is defined as an `ActionType` and is executed with a specified target.

### Features
- Supports various action types such as sending messages, playing sounds, teleportation, and more.
- Fully configurable and flexible for different use cases.
- Targets can be any object (e.g., players, locations, or custom objects).
- Dynamic placeholder support using `PlaceholderAPI` (if available).

### Example Usage

**Programmatically defining actions:**
```java
Actions actions = new ActionsBuilder()
    .sendMessage("&aWelcome, player!")
    .playSound("LEVELUP")
    .teleport(100, 65, 200, "world")
    .wait(200)
    .sendMessage("Don't look back")
    .build();

actions.execute(targetPlayer);
```

**YAML Configuration Example:**
```yaml
on-game-start:
- sendMessage: '&aWelcome, player!'
- playSound: 'LEVELUP'
- teleport: '{100} {65} {200} {world}'
- wait: 200
- message: '&cDon\'t look back'
```

### Action Types

ActionTypes are modular units of behavior that define what an action does, its required parameters, and how it executes.

#### Example: Creating a Custom ActionType
```java
public class MessageActionType implements ActionType {

    @Override
    public String getName() {
        return "sendMessage";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("message", "msg");
    }

    @Override
    public String getDescription() {
        return "Sends a message to the target";
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("message");
    }

    @Override
    public List<String> getOptionalParameters() {
        return Arrays.asList();
    }

    @Override
    public void execute(Object target, @NotNull List<String> params, @NotNull Map<String, String> replacements) throws IllegalArgumentException {
        if (target == null) return;
        if (!(target instanceof CommandSender commandSender)) return;
        params = applyPlaceholders(target instanceof Player player ? player : null, params, replacements);
        commandSender.sendMessage(ColorUtil.colorize(params.get(0)));
    }
}
```

**Helper Method:**
The `applyPlaceholders` method is provided to substitute placeholders dynamically. It also integrates with `PlaceholderAPI` if installed.

**Registering the ActionType:**
```java
ActionTypeRegistry.get().registerActionType(new MessageActionType());
```

### Pre-Registered ActionTypes
Pre-registered ActionTypes can be found [here](https://github.com/SplitYoSis/SYSEngine/tree/master/src/main/java/dev/splityosis/sysengine/actions/actiontypes).

### Documentation Access
In-game documentation for registered `ActionTypes` can be accessed using:
```
/sysengine actions actiontypes
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
    public World parse(CommandSender sender, String input, Command command, int index, CommandContext context, InvalidInputException inputException) throws InvalidInputException {
        World world = Bukkit.getWorld(input);

        if (world == null)
            throw new InvalidInputException();

        return world;
    }

    @Override
    public void onInvalidInput(CommandSender sender, String input, Command command, int index, CommandContext context, InvalidInputException inputException) {
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
