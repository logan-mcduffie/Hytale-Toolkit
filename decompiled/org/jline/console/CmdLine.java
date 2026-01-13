package org.jline.console;

import java.util.ArrayList;
import java.util.List;

public class CmdLine {
   private final String line;
   private final String head;
   private final String tail;
   private final List<String> args;
   private final CmdLine.DescriptionType descType;

   public CmdLine(String line, String head, String tail, List<String> args, CmdLine.DescriptionType descType) {
      this.line = line;
      this.head = head;
      this.tail = tail;
      this.args = new ArrayList<>(args);
      this.descType = descType;
   }

   public String getLine() {
      return this.line;
   }

   public String getHead() {
      return this.head;
   }

   public String getTail() {
      return this.tail;
   }

   public List<String> getArgs() {
      return this.args;
   }

   public CmdLine.DescriptionType getDescriptionType() {
      return this.descType;
   }

   public static enum DescriptionType {
      COMMAND,
      METHOD,
      SYNTAX;
   }
}
