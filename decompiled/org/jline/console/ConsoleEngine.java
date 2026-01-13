package org.jline.console;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.Widget;

public interface ConsoleEngine extends CommandRegistry {
   String VAR_NANORC = "NANORC";

   static String plainCommand(String command) {
      return command.startsWith(":") ? command.substring(1) : command;
   }

   void setLineReader(LineReader var1);

   void setSystemRegistry(SystemRegistry var1);

   Object[] expandParameters(String[] var1) throws Exception;

   String expandCommandLine(String var1);

   String expandToList(List<String> var1);

   Map<String, Boolean> scripts();

   void setScriptExtension(String var1);

   boolean hasAlias(String var1);

   String getAlias(String var1);

   Map<String, List<String>> getPipes();

   List<String> getNamedPipes();

   List<Completer> scriptCompleters();

   void persist(Path var1, Object var2);

   Object slurp(Path var1) throws IOException;

   <T> T consoleOption(String var1, T var2);

   void setConsoleOption(String var1, Object var2);

   Object execute(String var1, String var2, String[] var3) throws Exception;

   default Object execute(File script) throws Exception {
      return this.execute(script, "", new String[0]);
   }

   default Object execute(File script, String rawLine, String[] args) throws Exception {
      return this.execute(script != null ? script.toPath() : null, rawLine, args);
   }

   Object execute(Path var1, String var2, String[] var3) throws Exception;

   ConsoleEngine.ExecutionResult postProcess(String var1, Object var2, String var3);

   ConsoleEngine.ExecutionResult postProcess(Object var1);

   void trace(Object var1);

   void println(Object var1);

   void putVariable(String var1, Object var2);

   Object getVariable(String var1);

   boolean hasVariable(String var1);

   void purge();

   boolean executeWidget(Object var1);

   boolean isExecuting();

   public static class ExecutionResult {
      final int status;
      final Object result;

      public ExecutionResult(int status, Object result) {
         this.status = status;
         this.result = result;
      }

      public int status() {
         return this.status;
      }

      public Object result() {
         return this.result;
      }
   }

   public static class WidgetCreator implements Widget {
      private final ConsoleEngine consoleEngine;
      private final Object function;
      private final String name;

      public WidgetCreator(ConsoleEngine consoleEngine, String function) {
         this.consoleEngine = consoleEngine;
         this.name = function;
         this.function = consoleEngine.getVariable(function);
      }

      @Override
      public boolean apply() {
         return this.consoleEngine.executeWidget(this.function);
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
