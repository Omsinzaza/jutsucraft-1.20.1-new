# JutsuCraft

A Minecraft mod for Forge 1.20.1 introducing an example chakra system, sample blocks, items and creative tabs. This repository contains the source code and build configuration for the mod.

## Features

- Example block and item registered under the `jutsucraft` namespace.
- Sample creative mode tab showcasing mod items.
- Basic chakra capability with automatic regeneration and synchronization.
- Demonstrates configuration via `forge` config API.

## Building

Ensure you have Java 17 installed. Clone the repository and run:

```bash
./gradlew build
```

This compiles the mod and places the jar in `build/libs`. You can also use the provided Gradle tasks such as `runClient` to launch a development environment.

## Current Limitations

The project depends on the ForgeGradle plugin (`net.minecraftforge.gradle`). In certain environments the plugin cannot be resolved, causing Gradle to fail before building. If you encounter errors related to missing ForgeGradle, verify your Gradle version and network access or wait for the plugin to be available again.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.