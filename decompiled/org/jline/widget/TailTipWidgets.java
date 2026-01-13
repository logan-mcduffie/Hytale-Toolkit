package org.jline.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.jline.builtins.Options;
import org.jline.console.ArgDesc;
import org.jline.console.CmdDesc;
import org.jline.console.CmdLine;
import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.Buffer;
import org.jline.reader.LineReader;
import org.jline.reader.Reference;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp;
import org.jline.utils.Status;
import org.jline.utils.StyleResolver;

public class TailTipWidgets extends Widgets {
   private boolean enabled = false;
   private final TailTipWidgets.CommandDescriptions cmdDescs;
   private TailTipWidgets.TipType tipType;
   private int descriptionSize;
   private boolean descriptionEnabled = true;
   private boolean descriptionCache = false;
   private Object readerErrors;

   public TailTipWidgets(LineReader reader, Map<String, CmdDesc> tailTips) {
      this(reader, tailTips, 0, TailTipWidgets.TipType.COMBINED);
   }

   public TailTipWidgets(LineReader reader, Map<String, CmdDesc> tailTips, TailTipWidgets.TipType tipType) {
      this(reader, tailTips, 0, tipType);
   }

   public TailTipWidgets(LineReader reader, Map<String, CmdDesc> tailTips, int descriptionSize) {
      this(reader, tailTips, descriptionSize, TailTipWidgets.TipType.COMBINED);
   }

   public TailTipWidgets(LineReader reader, Map<String, CmdDesc> tailTips, int descriptionSize, TailTipWidgets.TipType tipType) {
      this(reader, tailTips, descriptionSize, tipType, null);
   }

   public TailTipWidgets(LineReader reader, Function<CmdLine, CmdDesc> descFun, int descriptionSize, TailTipWidgets.TipType tipType) {
      this(reader, null, descriptionSize, tipType, descFun);
   }

   private TailTipWidgets(
      LineReader reader, Map<String, CmdDesc> tailTips, int descriptionSize, TailTipWidgets.TipType tipType, Function<CmdLine, CmdDesc> descFun
   ) {
      super(reader);
      if (this.existsWidget("_tailtip-accept-line")) {
         throw new IllegalStateException("TailTipWidgets already created!");
      } else {
         this.cmdDescs = tailTips != null ? new TailTipWidgets.CommandDescriptions(tailTips) : new TailTipWidgets.CommandDescriptions(descFun);
         this.descriptionSize = descriptionSize;
         this.tipType = tipType;
         this.addWidget("_tailtip-accept-line", this::tailtipAcceptLine);
         this.addWidget("_tailtip-self-insert", this::tailtipInsert);
         this.addWidget("_tailtip-backward-delete-char", this::tailtipBackwardDelete);
         this.addWidget("_tailtip-delete-char", this::tailtipDelete);
         this.addWidget("_tailtip-expand-or-complete", this::tailtipComplete);
         this.addWidget("_tailtip-redisplay", this::tailtipUpdateStatus);
         this.addWidget("_tailtip-kill-line", this::tailtipKillLine);
         this.addWidget("_tailtip-kill-whole-line", this::tailtipKillWholeLine);
         this.addWidget("tailtip-window", this::toggleWindow);
         this.addWidget("tailtip-toggle", this::toggleKeyBindings);
      }
   }

   public void setTailTips(Map<String, CmdDesc> tailTips) {
      this.cmdDescs.setDescriptions(tailTips);
   }

   public void setDescriptionSize(int descriptionSize) {
      this.descriptionSize = descriptionSize;
      this.initDescription();
   }

   public int getDescriptionSize() {
      return this.descriptionSize;
   }

   public void setTipType(TailTipWidgets.TipType type) {
      this.tipType = type;
      if (this.tipType == TailTipWidgets.TipType.TAIL_TIP) {
         this.setSuggestionType(LineReader.SuggestionType.TAIL_TIP);
      } else {
         this.setSuggestionType(LineReader.SuggestionType.COMPLETER);
      }
   }

   public TailTipWidgets.TipType getTipType() {
      return this.tipType;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void disable() {
      if (this.enabled) {
         this.toggleKeyBindings();
      }
   }

   public void enable() {
      if (!this.enabled) {
         this.toggleKeyBindings();
      }
   }

   public void setDescriptionCache(boolean cache) {
      this.descriptionCache = cache;
   }

   public boolean tailtipComplete() {
      if (this.doTailTip("expand-or-complete")) {
         if ("\t".equals(this.lastBinding())) {
            this.callWidget("backward-char");
            this.reader.runMacro(KeyMap.key(this.reader.getTerminal(), InfoCmp.Capability.key_right));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean tailtipAcceptLine() {
      if (this.tipType != TailTipWidgets.TipType.TAIL_TIP) {
         this.setSuggestionType(LineReader.SuggestionType.COMPLETER);
      }

      this.clearDescription();
      this.setErrorPattern(null);
      this.setErrorIndex(-1);
      this.cmdDescs.clearTemporaryDescs();
      return this.clearTailTip("accept-line");
   }

   public boolean tailtipBackwardDelete() {
      return this.doTailTip(this.autopairEnabled() ? "_autopair-backward-delete-char" : "backward-delete-char");
   }

   private boolean clearTailTip(String widget) {
      this.clearTailTip();
      this.callWidget(widget);
      return true;
   }

   public boolean tailtipDelete() {
      this.clearTailTip();
      return this.doTailTip("delete-char");
   }

   public boolean tailtipKillLine() {
      this.clearTailTip();
      return this.doTailTip("kill-line");
   }

   public boolean tailtipKillWholeLine() {
      this.callWidget("kill-whole-line");
      return this.doTailTip("redisplay");
   }

   public boolean tailtipInsert() {
      return this.doTailTip(this.autopairEnabled() ? "_autopair-insert" : "self-insert");
   }

   public boolean tailtipUpdateStatus() {
      return this.doTailTip("redisplay");
   }

   private boolean doTailTip(String widget) {
      Buffer buffer = this.buffer();
      this.callWidget(widget);
      List<String> args = this.args();
      TailTipWidgets.Pair<String, Boolean> cmdkey = this.cmdDescs.evaluateCommandLine(buffer.toString(), this.args(), buffer.cursor());
      CmdDesc cmdDesc = this.cmdDescs.getDescription(cmdkey.getU());
      if (cmdDesc == null) {
         this.setErrorPattern(null);
         this.setErrorIndex(-1);
         this.clearDescription();
         this.resetTailTip();
      } else if (cmdDesc.isValid()) {
         if (cmdkey.getV()) {
            if (cmdDesc.isCommand() && buffer.length() == buffer.cursor()) {
               this.doCommandTailTip(widget, cmdDesc, args);
            }
         } else {
            this.doDescription(this.compileMainDescription(cmdDesc, this.descriptionSize));
            this.setErrorPattern(cmdDesc.getErrorPattern());
            this.setErrorIndex(cmdDesc.getErrorIndex());
         }
      }

      return true;
   }

   private void doCommandTailTip(String widget, CmdDesc cmdDesc, List<String> args) {
      int argnum = 0;
      String prevArg = "";

      for (String a : args) {
         if (!a.startsWith("-") && (!prevArg.matches("-[a-zA-Z]") || !cmdDesc.optionWithValue(prevArg))) {
            argnum++;
         }

         prevArg = a;
      }

      String lastArg = "";
      prevArg = args.get(args.size() - 1);
      if (!this.prevChar().equals(" ") && args.size() > 1) {
         lastArg = args.get(args.size() - 1);
         prevArg = args.get(args.size() - 2);
      }

      int bpsize = argnum;
      boolean doTailTip = true;
      boolean noCompleters = false;
      if (widget.endsWith("backward-delete-char")) {
         this.setSuggestionType(LineReader.SuggestionType.TAIL_TIP);
         noCompleters = true;
         if (!lastArg.startsWith("-") && (!prevArg.matches("-[a-zA-Z]") || !cmdDesc.optionWithValue(prevArg))) {
            bpsize = argnum - 1;
         }

         if (this.prevChar().equals(" ")) {
            bpsize++;
         }
      } else if (!this.prevChar().equals(" ")) {
         doTailTip = false;
         this.doDescription(this.compileMainDescription(cmdDesc, this.descriptionSize, cmdDesc.isSubcommand() ? lastArg : null));
      } else if (cmdDesc != null) {
         this.doDescription(this.compileMainDescription(cmdDesc, this.descriptionSize));
      }

      if (cmdDesc != null) {
         if (prevArg.startsWith("-") && !prevArg.contains("=") && !prevArg.matches("-[a-zA-Z][\\S]+") && cmdDesc.optionWithValue(prevArg)) {
            this.doDescription(this.compileOptionDescription(cmdDesc, prevArg, this.descriptionSize));
            this.setTipType(this.tipType);
         } else if (lastArg.matches("-[a-zA-Z][\\S]+") && cmdDesc.optionWithValue(lastArg.substring(0, 2))) {
            this.doDescription(this.compileOptionDescription(cmdDesc, lastArg.substring(0, 2), this.descriptionSize));
            this.setTipType(this.tipType);
         } else if (lastArg.startsWith("-")) {
            this.doDescription(this.compileOptionDescription(cmdDesc, lastArg, this.descriptionSize));
            if (!lastArg.contains("=")) {
               this.setSuggestionType(LineReader.SuggestionType.TAIL_TIP);
               noCompleters = true;
            } else {
               this.setTipType(this.tipType);
            }
         } else if (!widget.endsWith("backward-delete-char")) {
            this.setTipType(this.tipType);
         }

         if (bpsize > 0 && doTailTip) {
            List<ArgDesc> params = cmdDesc.getArgsDesc();
            if (!noCompleters) {
               this.setSuggestionType(
                  this.tipType == TailTipWidgets.TipType.COMPLETER ? LineReader.SuggestionType.COMPLETER : LineReader.SuggestionType.TAIL_TIP
               );
            }

            if (bpsize - 1 < params.size()) {
               if (!lastArg.startsWith("-")) {
                  List<AttributedString> d;
                  if (prevArg.startsWith("-") && cmdDesc.optionWithValue(prevArg)) {
                     d = this.compileOptionDescription(cmdDesc, prevArg, this.descriptionSize);
                  } else {
                     d = params.get(bpsize - 1).getDescription();
                  }

                  if (d == null || d.isEmpty()) {
                     d = this.compileMainDescription(cmdDesc, this.descriptionSize, cmdDesc.isSubcommand() ? lastArg : null);
                  }

                  this.doDescription(d);
               }

               StringBuilder tip = new StringBuilder();

               for (int i = bpsize - 1; i < params.size(); i++) {
                  tip.append(params.get(i).getName());
                  tip.append(" ");
               }

               this.setTailTip(tip.toString());
            } else if (!params.isEmpty() && params.get(params.size() - 1).getName().startsWith("[")) {
               this.setTailTip(params.get(params.size() - 1).getName());
               this.doDescription(params.get(params.size() - 1).getDescription());
            }
         } else if (doTailTip) {
            this.resetTailTip();
         }
      } else {
         this.clearDescription();
         this.resetTailTip();
      }
   }

   private void resetTailTip() {
      this.setTailTip("");
      if (this.tipType != TailTipWidgets.TipType.TAIL_TIP) {
         this.setSuggestionType(LineReader.SuggestionType.COMPLETER);
      }
   }

   private void doDescription(List<AttributedString> desc) {
      if (this.descriptionSize != 0 && this.descriptionEnabled) {
         List<AttributedString> list = desc;
         if (desc.size() > this.descriptionSize) {
            AttributedStringBuilder asb = new AttributedStringBuilder();
            asb.append(desc.get(this.descriptionSize - 1)).append("…", new AttributedStyle(AttributedStyle.INVERSE));
            List<AttributedString> mod = new ArrayList<>(desc.subList(0, this.descriptionSize - 1));
            mod.add(asb.toAttributedString());
            list = mod;
         } else if (desc.size() < this.descriptionSize) {
            List<AttributedString> mod = new ArrayList<>(desc);

            while (mod.size() != this.descriptionSize) {
               mod.add(new AttributedString(""));
            }

            list = mod;
         }

         this.setDescription(list);
      }
   }

   public void initDescription() {
      Status.getStatus(this.reader.getTerminal()).setBorder(true);
      this.clearDescription();
   }

   @Override
   public void clearDescription() {
      this.doDescription(Collections.emptyList());
   }

   private boolean autopairEnabled() {
      Binding binding = this.getKeyMap().getBound("(");
      return binding instanceof Reference && ((Reference)binding).name().equals("_autopair-insert");
   }

   public boolean toggleWindow() {
      this.descriptionEnabled = !this.descriptionEnabled;
      if (this.descriptionEnabled) {
         this.initDescription();
      } else {
         this.destroyDescription();
      }

      this.callWidget("redraw-line");
      return true;
   }

   public boolean toggleKeyBindings() {
      if (this.enabled) {
         this.defaultBindings();
         this.destroyDescription();
         this.reader.setVariable("errors", this.readerErrors);
      } else {
         this.customBindings();
         if (this.descriptionEnabled) {
            this.initDescription();
         }

         this.readerErrors = this.reader.getVariable("errors");
         this.reader.setVariable("errors", 0);
      }

      try {
         this.callWidget("redraw-line");
      } catch (Exception var2) {
      }

      return this.enabled;
   }

   private boolean defaultBindings() {
      if (!this.enabled) {
         return false;
      } else {
         this.aliasWidget(".accept-line", "accept-line");
         this.aliasWidget(".backward-delete-char", "backward-delete-char");
         this.aliasWidget(".delete-char", "delete-char");
         this.aliasWidget(".expand-or-complete", "expand-or-complete");
         this.aliasWidget(".self-insert", "self-insert");
         this.aliasWidget(".redisplay", "redisplay");
         this.aliasWidget(".kill-line", "kill-line");
         this.aliasWidget(".kill-whole-line", "kill-whole-line");
         KeyMap<Binding> map = this.getKeyMap();
         map.bind(new Reference("insert-close-paren"), ")");
         this.setSuggestionType(LineReader.SuggestionType.NONE);
         if (this.autopairEnabled()) {
            this.callWidget("autopair-toggle");
            this.callWidget("autopair-toggle");
         }

         this.enabled = false;
         return true;
      }
   }

   private void customBindings() {
      if (!this.enabled) {
         this.aliasWidget("_tailtip-accept-line", "accept-line");
         this.aliasWidget("_tailtip-backward-delete-char", "backward-delete-char");
         this.aliasWidget("_tailtip-delete-char", "delete-char");
         this.aliasWidget("_tailtip-expand-or-complete", "expand-or-complete");
         this.aliasWidget("_tailtip-self-insert", "self-insert");
         this.aliasWidget("_tailtip-redisplay", "redisplay");
         this.aliasWidget("_tailtip-kill-line", "kill-line");
         this.aliasWidget("_tailtip-kill-whole-line", "kill-whole-line");
         KeyMap<Binding> map = this.getKeyMap();
         map.bind(new Reference("_tailtip-self-insert"), ")");
         if (this.tipType != TailTipWidgets.TipType.TAIL_TIP) {
            this.setSuggestionType(LineReader.SuggestionType.COMPLETER);
         } else {
            this.setSuggestionType(LineReader.SuggestionType.TAIL_TIP);
         }

         this.enabled = true;
      }
   }

   private List<AttributedString> compileMainDescription(CmdDesc cmdDesc, int descriptionSize) {
      return this.compileMainDescription(cmdDesc, descriptionSize, null);
   }

   private List<AttributedString> compileMainDescription(CmdDesc cmdDesc, int descriptionSize, String lastArg) {
      if (descriptionSize != 0 && this.descriptionEnabled) {
         List<AttributedString> out = new ArrayList<>();
         List<AttributedString> mainDesc = cmdDesc.getMainDesc();
         if (mainDesc == null) {
            return out;
         } else {
            if (cmdDesc.isCommand() && cmdDesc.isValid() && !cmdDesc.isHighlighted()) {
               mainDesc = new ArrayList<>();
               StyleResolver resolver = Options.HelpException.defaultStyle();

               for (AttributedString as : cmdDesc.getMainDesc()) {
                  mainDesc.add(Options.HelpException.highlightSyntax(as.toString(), resolver));
               }
            }

            if (mainDesc.size() <= descriptionSize && lastArg == null) {
               out.addAll(mainDesc);
            } else {
               int tabs = 0;

               for (AttributedString as : mainDesc) {
                  if (as.columnLength() >= tabs) {
                     tabs = as.columnLength() + 2;
                  }
               }

               int row = 0;
               int col = 0;
               List<AttributedString> descList = new ArrayList<>();

               for (int i = 0; i < descriptionSize; i++) {
                  descList.add(new AttributedString(""));
               }

               for (AttributedString asx : mainDesc) {
                  if (lastArg == null || asx.toString().startsWith(lastArg)) {
                     AttributedStringBuilder asb = new AttributedStringBuilder().tabs(tabs);
                     if (col > 0) {
                        asb.append(descList.get(row));
                        asb.append("\t");
                     }

                     asb.append(asx);
                     descList.remove(row);
                     descList.add(row, asb.toAttributedString());
                     if (++row >= descriptionSize) {
                        row = 0;
                        col++;
                     }
                  }
               }

               out = new ArrayList<>(descList);
            }

            return out;
         }
      } else {
         return new ArrayList<>();
      }
   }

   private List<AttributedString> compileOptionDescription(CmdDesc cmdDesc, String opt, int descriptionSize) {
      if (descriptionSize != 0 && this.descriptionEnabled) {
         List<AttributedString> out = new ArrayList<>();
         Map<String, List<AttributedString>> optsDesc = cmdDesc.getOptsDesc();
         StyleResolver resolver = Options.HelpException.defaultStyle();
         if (!opt.startsWith("-")) {
            return out;
         } else {
            int ind = opt.indexOf("=");
            if (ind > 0) {
               opt = opt.substring(0, ind);
            }

            List<String> matched = new ArrayList<>();
            int tabs = 0;

            for (String key : optsDesc.keySet()) {
               for (String k : key.split("\\s+")) {
                  if (k.trim().startsWith(opt)) {
                     matched.add(key);
                     if (key.length() >= tabs) {
                        tabs = key.length() + 2;
                     }
                     break;
                  }
               }
            }

            if (matched.size() == 1) {
               out.add(Options.HelpException.highlightSyntax(matched.get(0), resolver));

               for (AttributedString as : optsDesc.get(matched.get(0))) {
                  AttributedStringBuilder asb = new AttributedStringBuilder().tabs(8);
                  asb.append("\t");
                  asb.append(as);
                  out.add(asb.toAttributedString());
               }
            } else if (matched.size() <= descriptionSize) {
               for (String key : matched) {
                  AttributedStringBuilder asb = new AttributedStringBuilder().tabs(tabs);
                  asb.append(Options.HelpException.highlightSyntax(key, resolver));
                  asb.append("\t");
                  asb.append(cmdDesc.optionDescription(key));
                  out.add(asb.toAttributedString());
               }
            } else if (matched.size() <= 2 * descriptionSize) {
               List<AttributedString> keyList = new ArrayList<>();
               int row = 0;
               int columnWidth = 2 * tabs;

               while (columnWidth < 50) {
                  columnWidth += tabs;
               }

               for (String key : matched) {
                  AttributedStringBuilder asb = new AttributedStringBuilder().tabs(tabs);
                  if (row >= descriptionSize) {
                     asb.append(keyList.get(row - descriptionSize));
                     asb.append(Options.HelpException.highlightSyntax(key, resolver));
                     asb.append("\t");
                     asb.append(cmdDesc.optionDescription(key));
                     keyList.remove(row - descriptionSize);
                     keyList.add(row - descriptionSize, asb.toAttributedString());
                  } else {
                     asb.append(Options.HelpException.highlightSyntax(key, resolver));
                     asb.append("\t");
                     asb.append(cmdDesc.optionDescription(key));
                     if (asb.columnLength() > columnWidth - 2) {
                        AttributedString trunc = asb.columnSubSequence(0, columnWidth - 5);
                        asb = new AttributedStringBuilder().tabs(tabs);
                        asb.append(trunc);
                        asb.append("...", new AttributedStyle(AttributedStyle.INVERSE));
                        asb.append("  ");
                     } else {
                        for (int i = asb.columnLength(); i < columnWidth; i++) {
                           asb.append(" ");
                        }
                     }

                     keyList.add(asb.toAttributedString().columnSubSequence(0, columnWidth));
                  }

                  row++;
               }

               out = new ArrayList<>(keyList);
            } else {
               List<AttributedString> keyList = new ArrayList<>();

               for (int i = 0; i < descriptionSize; i++) {
                  keyList.add(new AttributedString(""));
               }

               int row = 0;

               for (String key : matched) {
                  AttributedStringBuilder asb = new AttributedStringBuilder().tabs(tabs);
                  asb.append(keyList.get(row));
                  asb.append(Options.HelpException.highlightSyntax(key, resolver));
                  asb.append("\t");
                  keyList.remove(row);
                  keyList.add(row, asb.toAttributedString());
                  if (++row >= descriptionSize) {
                     row = 0;
                  }
               }

               out = new ArrayList<>(keyList);
            }

            return out;
         }
      } else {
         return new ArrayList<>();
      }
   }

   private class CommandDescriptions {
      Map<String, CmdDesc> descriptions = new HashMap<>();
      Map<String, CmdDesc> temporaryDescs = new HashMap<>();
      Map<String, CmdDesc> volatileDescs = new HashMap<>();
      Function<CmdLine, CmdDesc> descFun;

      public CommandDescriptions(Map<String, CmdDesc> descriptions) {
         this.descriptions = new HashMap<>(descriptions);
      }

      public CommandDescriptions(Function<CmdLine, CmdDesc> descFun) {
         this.descFun = descFun;
      }

      public void setDescriptions(Map<String, CmdDesc> descriptions) {
         this.descriptions = new HashMap<>(descriptions);
      }

      public TailTipWidgets.Pair<String, Boolean> evaluateCommandLine(String line, int curPos) {
         return this.evaluateCommandLine(line, TailTipWidgets.this.args(), curPos);
      }

      public TailTipWidgets.Pair<String, Boolean> evaluateCommandLine(String line, List<String> args) {
         return this.evaluateCommandLine(line, args, line.length());
      }

      private TailTipWidgets.Pair<String, Boolean> evaluateCommandLine(String line, List<String> args, int curPos) {
         String cmd = null;
         CmdLine.DescriptionType descType = CmdLine.DescriptionType.METHOD;
         String head = line.substring(0, curPos);
         String tail = line.substring(curPos);
         if (TailTipWidgets.this.prevChar().equals(")")) {
            descType = CmdLine.DescriptionType.SYNTAX;
            cmd = head;
         } else {
            if (line.length() == curPos) {
               cmd = args == null || args.size() <= 1 && (args.size() != 1 || !line.endsWith(" "))
                  ? null
                  : TailTipWidgets.this.parser().getCommand(args.get(0));
               descType = CmdLine.DescriptionType.COMMAND;
            }

            int brackets = 0;

            for (int i = head.length() - 1; i >= 0; i--) {
               if (head.charAt(i) == ')') {
                  brackets++;
               } else if (head.charAt(i) == '(') {
                  brackets--;
               }

               if (brackets < 0) {
                  descType = CmdLine.DescriptionType.METHOD;
                  head = head.substring(0, i);
                  cmd = head;
                  break;
               }
            }

            if (descType == CmdLine.DescriptionType.METHOD) {
               brackets = 0;

               for (int i = 0; i < tail.length(); i++) {
                  if (tail.charAt(i) == ')') {
                     brackets++;
                  } else if (tail.charAt(i) == '(') {
                     brackets--;
                  }

                  if (brackets > 0) {
                     tail = tail.substring(i + 1);
                     break;
                  }
               }
            }
         }

         if (cmd != null && this.descFun != null && !this.descriptions.containsKey(cmd) && !this.temporaryDescs.containsKey(cmd)) {
            CmdDesc c = this.descFun.apply(new CmdLine(line, head, tail, args, descType));
            if (descType == CmdLine.DescriptionType.COMMAND) {
               if (!TailTipWidgets.this.descriptionCache) {
                  this.volatileDescs.put(cmd, c);
               } else if (c != null) {
                  this.descriptions.put(cmd, c);
               } else {
                  this.temporaryDescs.put(cmd, null);
               }
            } else {
               this.temporaryDescs.put(cmd, c);
            }
         }

         return new TailTipWidgets.Pair<>(cmd, descType == CmdLine.DescriptionType.COMMAND);
      }

      public CmdDesc getDescription(String command) {
         CmdDesc out;
         if (this.descriptions.containsKey(command)) {
            out = this.descriptions.get(command);
         } else if (this.temporaryDescs.containsKey(command)) {
            out = this.temporaryDescs.get(command);
         } else {
            out = this.volatileDescs.remove(command);
         }

         return out;
      }

      public void clearTemporaryDescs() {
         this.temporaryDescs.clear();
      }
   }

   static class Pair<U, V> {
      final U u;
      final V v;

      public Pair(U u, V v) {
         this.u = u;
         this.v = v;
      }

      public U getU() {
         return this.u;
      }

      public V getV() {
         return this.v;
      }
   }

   public static enum TipType {
      TAIL_TIP,
      COMPLETER,
      COMBINED;
   }
}
