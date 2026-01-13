package org.jline.widget;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.Buffer;
import org.jline.reader.LineReader;
import org.jline.reader.Parser;
import org.jline.reader.Reference;
import org.jline.reader.Widget;
import org.jline.utils.AttributedString;
import org.jline.utils.Status;

public abstract class Widgets {
   public static final String TAILTIP_TOGGLE = "tailtip-toggle";
   public static final String TAILTIP_PANE = "tailtip-window";
   public static final String AUTOPAIR_TOGGLE = "autopair-toggle";
   public static final String AUTOSUGGEST_TOGGLE = "autosuggest-toggle";
   protected static final String AP_INSERT = "_autopair-insert";
   protected static final String AP_BACKWARD_DELETE_CHAR = "_autopair-backward-delete-char";
   protected static final String TT_ACCEPT_LINE = "_tailtip-accept-line";
   protected final LineReader reader;

   public Widgets(LineReader reader) {
      this.reader = reader;
   }

   public void addWidget(String name, Widget widget) {
      this.reader.getWidgets().put(name, this.namedWidget(name, widget));
   }

   private Widget namedWidget(final String name, final Widget widget) {
      return new Widget() {
         @Override
         public String toString() {
            return name;
         }

         @Override
         public boolean apply() {
            return widget.apply();
         }
      };
   }

   public void callWidget(String name) {
      if (!name.startsWith("_") && !name.endsWith("-toggle")) {
         name = "." + name;
      }

      this.reader.callWidget(name);
   }

   public void executeWidget(String name) {
      Binding ref = this.getKeyMap().getBoundKeys().get(KeyMap.alt(KeyMap.ctrl('X')));
      this.getKeyMap().bind(new Reference(name), KeyMap.alt(KeyMap.ctrl('X')));
      this.reader.runMacro(KeyMap.alt(KeyMap.ctrl('X')));
      if (ref != null) {
         this.getKeyMap().bind(ref, KeyMap.alt(KeyMap.ctrl('X')));
      }
   }

   public void aliasWidget(String orig, String alias) {
      this.reader.getWidgets().put(alias, this.widget(orig));
   }

   public String getWidget(String name) {
      return this.widget(name).toString();
   }

   public boolean existsWidget(String name) {
      try {
         this.widget(name);
         return true;
      } catch (Exception var3) {
         return false;
      }
   }

   private Widget widget(String name) {
      Widget out;
      if (name.startsWith(".")) {
         out = this.reader.getBuiltinWidgets().get(name.substring(1));
      } else {
         out = this.reader.getWidgets().get(name);
      }

      if (out == null) {
         throw new InvalidParameterException("widget: no such widget " + name);
      } else {
         return out;
      }
   }

   public Parser parser() {
      return this.reader.getParser();
   }

   public KeyMap<Binding> getKeyMap() {
      return this.reader.getKeyMaps().get("main");
   }

   public Buffer buffer() {
      return this.reader.getBuffer();
   }

   public void replaceBuffer(Buffer buffer) {
      this.reader.getBuffer().copyFrom(buffer);
   }

   public List<String> args() {
      return this.reader.getParser().parse(this.buffer().toString(), 0, Parser.ParseContext.COMPLETE).words();
   }

   public String prevChar() {
      return String.valueOf((char)this.reader.getBuffer().prevChar());
   }

   public String currChar() {
      return String.valueOf((char)this.reader.getBuffer().currChar());
   }

   public String lastBinding() {
      return this.reader.getLastBinding();
   }

   public void putString(String string) {
      this.reader.getBuffer().write(string);
   }

   public String tailTip() {
      return this.reader.getTailTip();
   }

   public void setTailTip(String tailTip) {
      this.reader.setTailTip(tailTip);
   }

   public void setErrorPattern(Pattern errorPattern) {
      this.reader.getHighlighter().setErrorPattern(errorPattern);
   }

   public void setErrorIndex(int errorIndex) {
      this.reader.getHighlighter().setErrorIndex(errorIndex);
   }

   public void clearTailTip() {
      this.reader.setTailTip("");
   }

   public void setSuggestionType(LineReader.SuggestionType type) {
      this.reader.setAutosuggestion(type);
   }

   public void setDescription(List<AttributedString> desc) {
      Status.getStatus(this.reader.getTerminal()).update(desc);
   }

   public void clearDescription() {
      this.setDescription(Collections.emptyList());
   }

   public void destroyDescription() {
      Status.getExistingStatus(this.reader.getTerminal()).ifPresent(Status::hide);
   }
}
