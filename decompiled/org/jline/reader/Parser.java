package org.jline.reader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Parser {
   String REGEX_VARIABLE = "[a-zA-Z_]+[a-zA-Z0-9_-]*";
   String REGEX_COMMAND = "[:]?[a-zA-Z]+[a-zA-Z0-9_-]*";

   ParsedLine parse(String var1, int var2, Parser.ParseContext var3) throws SyntaxError;

   default ParsedLine parse(String line, int cursor) throws SyntaxError {
      return this.parse(line, cursor, Parser.ParseContext.UNSPECIFIED);
   }

   default boolean isEscapeChar(char ch) {
      return ch == '\\';
   }

   default boolean validCommandName(String name) {
      return name != null && name.matches("[:]?[a-zA-Z]+[a-zA-Z0-9_-]*");
   }

   default boolean validVariableName(String name) {
      return name != null && name.matches("[a-zA-Z_]+[a-zA-Z0-9_-]*");
   }

   default String getCommand(String line) {
      Pattern patternCommand = Pattern.compile("^\\s*[a-zA-Z_]+[a-zA-Z0-9_-]*=([:]?[a-zA-Z]+[a-zA-Z0-9_-]*)(\\s+|$)");
      Matcher matcher = patternCommand.matcher(line);
      String out;
      if (matcher.find()) {
         out = matcher.group(1);
      } else {
         out = line.trim().split("\\s+")[0];
         if (!out.matches("[:]?[a-zA-Z]+[a-zA-Z0-9_-]*")) {
            out = "";
         }
      }

      return out;
   }

   default String getVariable(String line) {
      String out = null;
      Pattern patternCommand = Pattern.compile("^\\s*([a-zA-Z_]+[a-zA-Z0-9_-]*)\\s*=[^=~].*");
      Matcher matcher = patternCommand.matcher(line);
      if (matcher.find()) {
         out = matcher.group(1);
      }

      return out;
   }

   public static enum ParseContext {
      UNSPECIFIED,
      ACCEPT_LINE,
      SPLIT_LINE,
      COMPLETE,
      SECONDARY_PROMPT;
   }
}
