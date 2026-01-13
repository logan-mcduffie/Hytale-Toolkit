package org.jline.console;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.jline.builtins.ConsoleOptionGetter;
import org.jline.reader.Completer;
import org.jline.reader.ParsedLine;
import org.jline.terminal.Terminal;

public interface SystemRegistry extends CommandRegistry, ConsoleOptionGetter {
   void setCommandRegistries(CommandRegistry... var1);

   void register(String var1, CommandRegistry var2);

   void initialize(File var1);

   Collection<String> getPipeNames();

   Completer completer();

   CmdDesc commandDescription(CmdLine var1);

   Object execute(String var1) throws Exception;

   void cleanUp();

   void trace(Throwable var1);

   void trace(boolean var1, Throwable var2);

   @Override
   Object consoleOption(String var1);

   @Override
   <T> T consoleOption(String var1, T var2);

   void setConsoleOption(String var1, Object var2);

   Terminal terminal();

   Object invoke(String var1, Object... var2) throws Exception;

   boolean isCommandOrScript(ParsedLine var1);

   boolean isCommandOrScript(String var1);

   boolean isCommandAlias(String var1);

   void close();

   static SystemRegistry get() {
      return SystemRegistry.Registeries.getInstance().getSystemRegistry();
   }

   static void add(SystemRegistry systemRegistry) {
      SystemRegistry.Registeries.getInstance().addRegistry(systemRegistry);
   }

   static void remove() {
      SystemRegistry.Registeries.getInstance().removeRegistry();
   }

   public static class Registeries {
      private static final SystemRegistry.Registeries instance = new SystemRegistry.Registeries();
      private final Map<Long, SystemRegistry> systemRegisteries = new HashMap<>();

      private Registeries() {
      }

      protected static SystemRegistry.Registeries getInstance() {
         return instance;
      }

      protected void addRegistry(SystemRegistry systemRegistry) {
         this.systemRegisteries.put(getThreadId(), systemRegistry);
      }

      protected SystemRegistry getSystemRegistry() {
         return this.systemRegisteries.getOrDefault(getThreadId(), null);
      }

      protected void removeRegistry() {
         this.systemRegisteries.remove(getThreadId());
      }

      private static long getThreadId() {
         return Thread.currentThread().getId();
      }
   }
}
