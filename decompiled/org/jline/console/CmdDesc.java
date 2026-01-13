package org.jline.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.jline.utils.AttributedString;

public class CmdDesc {
   private List<AttributedString> mainDesc;
   private List<ArgDesc> argsDesc;
   private TreeMap<String, List<AttributedString>> optsDesc;
   private Pattern errorPattern;
   private int errorIndex = -1;
   private boolean valid = true;
   private boolean command = false;
   private boolean subcommand = false;
   private boolean highlighted = true;

   public CmdDesc() {
      this.command = false;
   }

   public CmdDesc(boolean valid) {
      this.valid = valid;
   }

   public CmdDesc(List<ArgDesc> argsDesc) {
      this(new ArrayList<>(), argsDesc, new HashMap<>());
   }

   public CmdDesc(List<ArgDesc> argsDesc, Map<String, List<AttributedString>> optsDesc) {
      this(new ArrayList<>(), argsDesc, optsDesc);
   }

   public CmdDesc(List<AttributedString> mainDesc, List<ArgDesc> argsDesc, Map<String, List<AttributedString>> optsDesc) {
      this.argsDesc = new ArrayList<>(argsDesc);
      this.optsDesc = new TreeMap<>(optsDesc);
      if (mainDesc.isEmpty() && optsDesc.containsKey("main")) {
         this.mainDesc = new ArrayList<>(optsDesc.get("main"));
         this.optsDesc.remove("main");
      } else {
         this.mainDesc = new ArrayList<>(mainDesc);
      }

      this.command = true;
   }

   public boolean isValid() {
      return this.valid;
   }

   public boolean isCommand() {
      return this.command;
   }

   public void setSubcommand(boolean subcommand) {
      this.subcommand = subcommand;
   }

   public boolean isSubcommand() {
      return this.subcommand;
   }

   public void setHighlighted(boolean highlighted) {
      this.highlighted = highlighted;
   }

   public boolean isHighlighted() {
      return this.highlighted;
   }

   public CmdDesc mainDesc(List<AttributedString> mainDesc) {
      this.mainDesc = new ArrayList<>(mainDesc);
      return this;
   }

   public void setMainDesc(List<AttributedString> mainDesc) {
      this.mainDesc = new ArrayList<>(mainDesc);
   }

   public List<AttributedString> getMainDesc() {
      return this.mainDesc;
   }

   public TreeMap<String, List<AttributedString>> getOptsDesc() {
      return this.optsDesc;
   }

   public void setErrorPattern(Pattern errorPattern) {
      this.errorPattern = errorPattern;
   }

   public Pattern getErrorPattern() {
      return this.errorPattern;
   }

   public void setErrorIndex(int errorIndex) {
      this.errorIndex = errorIndex;
   }

   public int getErrorIndex() {
      return this.errorIndex;
   }

   public List<ArgDesc> getArgsDesc() {
      return this.argsDesc;
   }

   public boolean optionWithValue(String option) {
      for (String key : this.optsDesc.keySet()) {
         if (key.matches("(^|.*\\s)" + option + "($|=.*|\\s.*)")) {
            return key.contains("=");
         }
      }

      return false;
   }

   public AttributedString optionDescription(String key) {
      return !this.optsDesc.get(key).isEmpty() ? this.optsDesc.get(key).get(0) : new AttributedString("");
   }
}
