# Minecraft Plugin Engine

## Overview
**Minecraft Command Engine** is a flexible and powerful library designed to help you build Minecraft plugins that are compatible with Minecraft versions 1.8+. It offers convenient and fluent utilities for handling commands, configuration management, versioning, and time operations, making plugin development easier and more efficient.

This engine includes the following main components:

- **ConfigLib**: Simplified, annotation-based configuration management.
- **CommandLib**: A command handling system supporting dynamic command registration, subcommands, and tab completion.
- **Util Classes**: A collection of useful utilities, including time and version helpers.
- **XSeries**: The engine shades in the XSeries library which provides multi-version supported objects such as XMaterial, XSound, XBlock, XItemStack etc...

## Installation
To use the Minecraft Command Engine, add the following dependency to your project:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>minecraft-command-engine</artifactId>
    <version>1.0.0</version>
</dependency>