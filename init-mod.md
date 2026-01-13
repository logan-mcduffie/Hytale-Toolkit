---
description: Initialize a new Hytale mod project with interactive setup
allowed-tools: Read, Write, Bash, AskUserQuestion
---

# Initialize New Hytale Mod

You are initializing a new Hytale server mod. Follow these steps:

## Step 1: Gather Information

Use the AskUserQuestion tool to ask the user for the following information. Ask all questions in a single call:

1. **Mod Name**: The name of the mod (e.g., "MyAwesomeMod"). This will be used for the class name and artifact ID.
2. **Group ID**: The Maven group ID / Java package base (e.g., "com.example" or "dev.yourname"). Default: "dev.hytalemodding"
3. **Description**: A brief description of what the mod does.
4. **Author Name**: The mod author's name.
5. **Version**: Starting version (default: "1.0.0")

## Step 2: Validate and Derive Names

From the user's input, derive:
- `modName`: The mod name in PascalCase (e.g., "MyAwesomeMod")
- `modNameLower`: Lowercase version for commands (e.g., "myawesomemod")
- `groupId`: The group ID (e.g., "dev.hytalemodding")
- `packagePath`: Group ID as path (e.g., "dev/hytalemodding")
- `fullPackage`: Full package name `{groupId}.{modNameLower}` (e.g., "dev.hytalemodding.myawesomemod")
- `fullPackagePath`: Full package as path (e.g., "dev/hytalemodding/myawesomemod")

## Step 3: Create Directory Structure

The mod folder should be created at the same level as `plugin-template` in the HytaleMods directory.

Create this structure:
```
{modName}/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── {fullPackagePath}/
│       │       ├── {modName}.java
│       │       ├── commands/
│       │       │   └── {modName}Command.java
│       │       └── events/
│       │           └── {modName}Event.java
│       └── resources/
│           └── manifest.json
└── .gitignore
```

## Step 4: Generate Files

### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>{groupId}</groupId>
    <artifactId>{modName}</artifactId>
    <version>{version}-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>25</maven.compiler.source>
        <maven.compiler.target>25</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.hypixel.hytale</groupId>
            <artifactId>HytaleServer-parent</artifactId>
            <version>1.0-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

</project>
```

### manifest.json
```json
{
  "Group": "{groupId}",
  "Name": "{modName}",
  "Version": "{version}",
  "Description": "{description}",
  "Authors": [
    {
      "Name": "{authorName}"
    }
  ],
  "ServerVersion": "*",
  "Dependencies": {},
  "OptionalDependencies": {},
  "DisabledByDefault": false,
  "Main": "{fullPackage}.{modName}"
}
```

### {modName}.java (Main plugin class)
```java
package {fullPackage};

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import {fullPackage}.commands.{modName}Command;
import {fullPackage}.events.{modName}Event;

import javax.annotation.Nonnull;

public class {modName} extends JavaPlugin {

    public {modName}(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        // Register commands
        this.getCommandRegistry().registerCommand(new {modName}Command("{modNameLower}", "{description}"));

        // Register event listeners
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, {modName}Event::onPlayerReady);
    }
}
```

### {modName}Command.java
```java
package {fullPackage}.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;

import javax.annotation.Nonnull;

public class {modName}Command extends CommandBase {

    public {modName}Command(String name, String description) {
        super(name, description);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        context.sendMessage(Message.raw("Hello from {modName}!"));
    }
}
```

### {modName}Event.java
```java
package {fullPackage}.events;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;

public class {modName}Event {

    public static void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(Message.raw("{modName} is active!"));
    }
}
```

### .gitignore
```
target/
*.class
*.jar
*.log
.idea/
*.iml
```

## Step 5: Confirmation

After creating all files:
1. List all created files
2. Remind the user they can build with: `mvn package` (from the mod directory)
3. The JAR will be in `target/` and should be placed in the server's `mods/` folder
4. Suggest using the RAG search tool (`search_hytale_code`) to find relevant APIs for their mod
