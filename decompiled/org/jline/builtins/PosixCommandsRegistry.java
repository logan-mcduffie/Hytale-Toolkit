package org.jline.builtins;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.jline.terminal.Terminal;

public class PosixCommandsRegistry {
   private final PosixCommands.Context context;
   private final Map<String, PosixCommandsRegistry.CommandFunction> commands;

   public PosixCommandsRegistry(InputStream in, PrintStream out, PrintStream err, Path currentDir, Terminal terminal, Function<String, Object> variables) {
      this.context = new PosixCommands.Context(in, out, err, currentDir, terminal, variables);
      this.commands = new HashMap<>();
      populateDefaultCommands(this.commands);
   }

   public PosixCommandsRegistry(PosixCommands.Context context) {
      this.context = context;
      this.commands = new HashMap<>();
      populateDefaultCommands(this.commands);
   }

   private static void populateDefaultCommands(Map<String, PosixCommandsRegistry.CommandFunction> commands) {
      commands.put("cat", PosixCommands::cat);
      commands.put("echo", PosixCommands::echo);
      commands.put("grep", PosixCommands::grep);
      commands.put("ls", PosixCommands::ls);
      commands.put("pwd", PosixCommands::pwd);
      commands.put("head", PosixCommands::head);
      commands.put("tail", PosixCommands::tail);
      commands.put("wc", PosixCommands::wc);
      commands.put("date", PosixCommands::date);
      commands.put("sleep", PosixCommands::sleep);
      commands.put("sort", PosixCommands::sort);
      commands.put("clear", PosixCommands::clear);
   }

   public void registerDefaultCommands() {
      populateDefaultCommands(this.commands);
   }

   public void register(String name, PosixCommandsRegistry.CommandFunction command) {
      this.commands.put(name, command);
   }

   public void unregister(String name) {
      this.commands.remove(name);
   }

   public boolean hasCommand(String name) {
      return this.commands.containsKey(name);
   }

   public String[] getCommandNames() {
      return this.commands.keySet().toArray(new String[0]);
   }

   public void execute(String name, String[] argv) throws Exception {
      PosixCommandsRegistry.CommandFunction command = this.commands.get(name);
      if (command == null) {
         throw new IllegalArgumentException("Unknown command: " + name);
      } else {
         command.execute(this.context, argv);
      }
   }

   public void execute(String commandLine) throws Exception {
      if (commandLine != null && !commandLine.trim().isEmpty()) {
         String[] parts = commandLine.trim().split("\\s+");
         this.execute(parts[0], parts);
      }
   }

   public PosixCommands.Context getContext() {
      return this.context;
   }

   public PosixCommandsRegistry withCurrentDirectory(Path newCurrentDir) {
      PosixCommands.Context newContext = new PosixCommands.Context(
         this.context.in(), this.context.out(), this.context.err(), newCurrentDir, this.context.terminal(), this.context::get
      );
      return new PosixCommandsRegistry(newContext);
   }

   public void printHelp() {
      this.context.out().println("Available POSIX commands:");
      String[] names = this.getCommandNames();
      Arrays.sort((Object[])names);

      for (String name : names) {
         this.context.out().println("  " + name);
      }

      this.context.out().println();
      this.context.out().println("Use '<command> --help' for detailed help on each command.");
   }

   public void printHelp(String commandName) throws Exception {
      if (!this.hasCommand(commandName)) {
         this.context.err().println("Unknown command: " + commandName);
      } else {
         this.execute(commandName, new String[]{commandName, "--help"});
      }
   }

   @FunctionalInterface
   public interface CommandFunction {
      void execute(PosixCommands.Context var1, String[] var2) throws Exception;
   }
}
