package org.jline.widget;

import org.jline.reader.Buffer;
import org.jline.reader.LineReader;
import org.jline.reader.impl.BufferImpl;

public class AutosuggestionWidgets extends Widgets {
   private boolean enabled = false;

   public AutosuggestionWidgets(LineReader reader) {
      super(reader);
      if (this.existsWidget("_autosuggest-forward-char")) {
         throw new IllegalStateException("AutosuggestionWidgets already created!");
      } else {
         this.addWidget("_autosuggest-forward-char", this::autosuggestForwardChar);
         this.addWidget("_autosuggest-end-of-line", this::autosuggestEndOfLine);
         this.addWidget("_autosuggest-forward-word", this::partialAccept);
         this.addWidget("autosuggest-toggle", this::toggleKeyBindings);
      }
   }

   public void disable() {
      this.defaultBindings();
   }

   public void enable() {
      this.customBindings();
   }

   public boolean partialAccept() {
      Buffer buffer = this.buffer();
      if (buffer.cursor() == buffer.length()) {
         int curPos = buffer.cursor();
         buffer.write(this.tailTip());
         buffer.cursor(curPos);
         this.replaceBuffer(buffer);
         this.callWidget("forward-word");
         Buffer newBuf = new BufferImpl();
         newBuf.write(this.buffer().substring(0, this.buffer().cursor()));
         this.replaceBuffer(newBuf);
      } else {
         this.callWidget("forward-word");
      }

      return true;
   }

   public boolean autosuggestForwardChar() {
      return this.accept("forward-char");
   }

   public boolean autosuggestEndOfLine() {
      return this.accept("end-of-line");
   }

   public boolean toggleKeyBindings() {
      if (this.enabled) {
         this.defaultBindings();
      } else {
         this.customBindings();
      }

      return this.enabled;
   }

   private boolean accept(String widget) {
      Buffer buffer = this.buffer();
      if (buffer.cursor() == buffer.length()) {
         this.putString(this.tailTip());
      } else {
         this.callWidget(widget);
      }

      return true;
   }

   private void customBindings() {
      if (!this.enabled) {
         this.aliasWidget("_autosuggest-forward-char", "forward-char");
         this.aliasWidget("_autosuggest-end-of-line", "end-of-line");
         this.aliasWidget("_autosuggest-forward-word", "forward-word");
         this.enabled = true;
         this.setSuggestionType(LineReader.SuggestionType.HISTORY);
      }
   }

   private void defaultBindings() {
      if (this.enabled) {
         this.aliasWidget(".forward-char", "forward-char");
         this.aliasWidget(".end-of-line", "end-of-line");
         this.aliasWidget(".forward-word", "forward-word");
         this.enabled = false;
         this.setSuggestionType(LineReader.SuggestionType.NONE);
      }
   }
}
