package org.jline.builtins;

public interface ConsoleOptionGetter {
   Object consoleOption(String var1);

   <T> T consoleOption(String var1, T var2);
}
