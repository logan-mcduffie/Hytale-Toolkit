package org.jline.reader;

import java.io.IOError;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.Log;

public final class LineReaderBuilder {
   Terminal terminal;
   String appName;
   Map<String, Object> variables = new HashMap<>();
   Map<LineReader.Option, Boolean> options = new HashMap<>();
   History history;
   Completer completer;
   History memoryHistory;
   Highlighter highlighter;
   Parser parser;
   Expander expander;
   CompletionMatcher completionMatcher;

   public static LineReaderBuilder builder() {
      return new LineReaderBuilder();
   }

   private LineReaderBuilder() {
   }

   public LineReaderBuilder terminal(Terminal terminal) {
      this.terminal = terminal;
      return this;
   }

   public LineReaderBuilder appName(String appName) {
      this.appName = appName;
      return this;
   }

   public LineReaderBuilder variables(Map<String, Object> variables) {
      Map<String, Object> old = this.variables;
      this.variables = Objects.requireNonNull(variables);
      this.variables.putAll(old);
      return this;
   }

   public LineReaderBuilder variable(String name, Object value) {
      this.variables.put(name, value);
      return this;
   }

   public LineReaderBuilder option(LineReader.Option option, boolean value) {
      this.options.put(option, value);
      return this;
   }

   public LineReaderBuilder history(History history) {
      this.history = history;
      return this;
   }

   public LineReaderBuilder completer(Completer completer) {
      this.completer = completer;
      return this;
   }

   public LineReaderBuilder highlighter(Highlighter highlighter) {
      this.highlighter = highlighter;
      return this;
   }

   public LineReaderBuilder parser(Parser parser) {
      if (parser != null) {
         try {
            if (!Boolean.getBoolean("org.jline.reader.support.parsedline") && !(parser.parse("", 0) instanceof CompletingParsedLine)) {
               Log.warn(
                  "The Parser of class "
                     + parser.getClass().getName()
                     + " does not support the CompletingParsedLine interface. Completion with escaped or quoted words won't work correctly."
               );
            }
         } catch (Throwable var3) {
         }
      }

      this.parser = parser;
      return this;
   }

   public LineReaderBuilder expander(Expander expander) {
      this.expander = expander;
      return this;
   }

   public LineReaderBuilder completionMatcher(CompletionMatcher completionMatcher) {
      this.completionMatcher = completionMatcher;
      return this;
   }

   public LineReader build() {
      Terminal terminal = this.terminal;
      if (terminal == null) {
         try {
            terminal = TerminalBuilder.terminal();
         } catch (IOException var6) {
            throw new IOError(var6);
         }
      }

      String appName = this.appName;
      if (null == appName) {
         appName = terminal.getName();
      }

      LineReaderImpl reader = new LineReaderImpl(terminal, appName, this.variables);
      if (this.history != null) {
         reader.setHistory(this.history);
      } else {
         if (this.memoryHistory == null) {
            this.memoryHistory = new DefaultHistory();
         }

         reader.setHistory(this.memoryHistory);
      }

      if (this.completer != null) {
         reader.setCompleter(this.completer);
      }

      if (this.highlighter != null) {
         reader.setHighlighter(this.highlighter);
      }

      if (this.parser != null) {
         reader.setParser(this.parser);
      }

      if (this.expander != null) {
         reader.setExpander(this.expander);
      }

      if (this.completionMatcher != null) {
         reader.setCompletionMatcher(this.completionMatcher);
      }

      for (Entry<LineReader.Option, Boolean> e : this.options.entrySet()) {
         reader.option(e.getKey(), e.getValue());
      }

      return reader;
   }
}
