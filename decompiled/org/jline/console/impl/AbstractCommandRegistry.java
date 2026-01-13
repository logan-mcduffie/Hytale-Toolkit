package org.jline.console.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.jline.console.CmdDesc;
import org.jline.console.CommandInput;
import org.jline.console.CommandMethods;
import org.jline.console.CommandRegistry;
import org.jline.reader.impl.completer.SystemCompleter;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;

public abstract class AbstractCommandRegistry implements CommandRegistry {
   private AbstractCommandRegistry.CmdRegistry cmdRegistry;
   private Exception exception;

   public CmdDesc doHelpDesc(String command, List<String> info, CmdDesc cmdDesc) {
      List<AttributedString> mainDesc = new ArrayList<>();
      AttributedStringBuilder asb = new AttributedStringBuilder();
      asb.append(command.toLowerCase()).append(" -  ");

      for (String s : info) {
         if (asb.length() == 0) {
            asb.append("\t");
         }

         asb.append(s);
         mainDesc.add(asb.toAttributedString());
         asb = new AttributedStringBuilder();
         asb.tabs(2);
      }

      asb = new AttributedStringBuilder();
      asb.tabs(7);
      asb.append("Usage:");

      for (AttributedString as : cmdDesc.getMainDesc()) {
         asb.append("\t");
         asb.append(as);
         mainDesc.add(asb.toAttributedString());
         asb = new AttributedStringBuilder();
         asb.tabs(7);
      }

      return new CmdDesc(mainDesc, new ArrayList<>(), cmdDesc.getOptsDesc());
   }

   public <T extends Enum<T>> void registerCommands(Map<T, String> commandName, Map<T, CommandMethods> commandExecute) {
      this.cmdRegistry = new AbstractCommandRegistry.EnumCmdRegistry<>(commandName, commandExecute);
   }

   public void registerCommands(Map<String, CommandMethods> commandExecute) {
      this.cmdRegistry = new AbstractCommandRegistry.NameCmdRegistry(commandExecute);
   }

   @Override
   public Object invoke(CommandRegistry.CommandSession session, String command, Object... args) throws Exception {
      this.exception = null;
      CommandMethods methods = this.getCommandMethods(command);
      Object out = methods.execute().apply(new CommandInput(command, args, session));
      if (this.exception != null) {
         throw this.exception;
      } else {
         return out;
      }
   }

   public void saveException(Exception exception) {
      this.exception = exception;
   }

   @Override
   public boolean hasCommand(String command) {
      return this.cmdRegistry.hasCommand(command);
   }

   @Override
   public Set<String> commandNames() {
      return this.cmdRegistry.commandNames();
   }

   @Override
   public Map<String, String> commandAliases() {
      return this.cmdRegistry.commandAliases();
   }

   public <V extends Enum<V>> void rename(V command, String newName) {
      this.cmdRegistry.rename(command, newName);
   }

   public void alias(String alias, String command) {
      this.cmdRegistry.alias(alias, command);
   }

   @Override
   public SystemCompleter compileCompleters() {
      return this.cmdRegistry.compileCompleters();
   }

   public CommandMethods getCommandMethods(String command) {
      return this.cmdRegistry.getCommandMethods(command);
   }

   public Object registeredCommand(String command) {
      return this.cmdRegistry.command(command);
   }

   private interface CmdRegistry {
      boolean hasCommand(String var1);

      Set<String> commandNames();

      Map<String, String> commandAliases();

      Object command(String var1);

      <V extends Enum<V>> void rename(V var1, String var2);

      void alias(String var1, String var2);

      SystemCompleter compileCompleters();

      CommandMethods getCommandMethods(String var1);
   }

   private static class EnumCmdRegistry<T extends Enum<T>> implements AbstractCommandRegistry.CmdRegistry {
      private final Map<T, String> commandName;
      private Map<String, T> nameCommand = new HashMap<>();
      private final Map<T, CommandMethods> commandExecute;
      private final Map<String, String> aliasCommand = new HashMap<>();

      public EnumCmdRegistry(Map<T, String> commandName, Map<T, CommandMethods> commandExecute) {
         this.commandName = commandName;
         this.commandExecute = commandExecute;
         this.doNameCommand();
      }

      private void doNameCommand() {
         this.nameCommand = this.commandName.entrySet().stream().collect(Collectors.toMap(Entry::getValue, Entry::getKey));
      }

      @Override
      public Set<String> commandNames() {
         return this.nameCommand.keySet();
      }

      @Override
      public Map<String, String> commandAliases() {
         return this.aliasCommand;
      }

      @Override
      public <V extends Enum<V>> void rename(V command, String newName) {
         if (this.nameCommand.containsKey(newName)) {
            throw new IllegalArgumentException("Duplicate command name '" + command + "'!");
         } else if (!this.commandName.containsKey(command)) {
            throw new IllegalArgumentException("Command '" + command + "' does not exists!");
         } else {
            this.commandName.put((T)command, newName);
            this.doNameCommand();
         }
      }

      @Override
      public void alias(String alias, String command) {
         if (!this.nameCommand.containsKey(command)) {
            throw new IllegalArgumentException("Command '" + command + "' does not exists!");
         } else {
            this.aliasCommand.put(alias, command);
         }
      }

      @Override
      public boolean hasCommand(String name) {
         return this.nameCommand.containsKey(name) || this.aliasCommand.containsKey(name);
      }

      @Override
      public SystemCompleter compileCompleters() {
         SystemCompleter out = new SystemCompleter();

         for (Entry<T, String> entry : this.commandName.entrySet()) {
            out.add(entry.getValue(), this.commandExecute.get(entry.getKey()).compileCompleter().apply(entry.getValue()));
         }

         out.addAliases(this.aliasCommand);
         return out;
      }

      public T command(String name) {
         name = this.aliasCommand.getOrDefault(name, name);
         if (this.nameCommand.containsKey(name)) {
            return this.nameCommand.get(name);
         } else {
            throw new IllegalArgumentException("Command '" + name + "' does not exists!");
         }
      }

      @Override
      public CommandMethods getCommandMethods(String command) {
         return this.commandExecute.get(this.command(command));
      }
   }

   private static class NameCmdRegistry implements AbstractCommandRegistry.CmdRegistry {
      private final Map<String, CommandMethods> commandExecute;
      private final Map<String, String> aliasCommand = new HashMap<>();

      public NameCmdRegistry(Map<String, CommandMethods> commandExecute) {
         this.commandExecute = commandExecute;
      }

      @Override
      public Set<String> commandNames() {
         return this.commandExecute.keySet();
      }

      @Override
      public Map<String, String> commandAliases() {
         return this.aliasCommand;
      }

      @Override
      public <V extends Enum<V>> void rename(V command, String newName) {
         throw new IllegalArgumentException();
      }

      @Override
      public void alias(String alias, String command) {
         if (!this.commandExecute.containsKey(command)) {
            throw new IllegalArgumentException("Command '" + command + "' does not exists!");
         } else {
            this.aliasCommand.put(alias, command);
         }
      }

      @Override
      public boolean hasCommand(String name) {
         return this.commandExecute.containsKey(name) || this.aliasCommand.containsKey(name);
      }

      @Override
      public SystemCompleter compileCompleters() {
         SystemCompleter out = new SystemCompleter();

         for (String c : this.commandExecute.keySet()) {
            out.add(c, this.commandExecute.get(c).compileCompleter().apply(c));
         }

         out.addAliases(this.aliasCommand);
         return out;
      }

      public String command(String name) {
         return this.commandExecute.containsKey(name) ? name : this.aliasCommand.get(name);
      }

      @Override
      public CommandMethods getCommandMethods(String command) {
         return this.commandExecute.get(this.command(command));
      }
   }
}
