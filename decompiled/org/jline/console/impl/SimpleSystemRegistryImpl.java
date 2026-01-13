package org.jline.console.impl;

import java.nio.file.Path;
import java.util.function.Supplier;
import org.jline.builtins.ConfigurationPath;
import org.jline.reader.LineReader;
import org.jline.reader.Parser;
import org.jline.terminal.Terminal;

public class SimpleSystemRegistryImpl extends SystemRegistryImpl {
   private LineReader lineReader;

   public SimpleSystemRegistryImpl(Parser parser, Terminal terminal, Supplier<Path> workDir, ConfigurationPath configPath) {
      super(parser, terminal, workDir, configPath);
   }

   public void setLineReader(LineReader lineReader) {
      this.lineReader = lineReader;
   }

   @Override
   public <T> T consoleOption(String name, T defVal) {
      return (T)this.lineReader.getVariables().getOrDefault(name, defVal);
   }

   @Override
   public void setConsoleOption(String name, Object value) {
      this.lineReader.setVariable(name, value);
   }
}
