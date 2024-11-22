# Minecraft Plugin Engine

## Overview
**Minecraft Command Engine** is a flexible and powerful library designed to help you build Minecraft plugins that are compatible with Minecraft versions 1.8+. It offers convenient and fluent utilities for handling commands, configuration management, versioning, and time operations, making plugin development easier and more efficient.

This engine includes the following main components:

- **ConfigLib**: Simplified, annotation-based configuration management.
- **CommandLib**: A command handling system supporting dynamic command registration, subcommands, and tab completion.
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
    <version>1.0.0-SNAPSHOT</version>
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
coming soon...