package org.jline.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.Buffer;
import org.jline.reader.LineReader;
import org.jline.reader.Reference;

public class AutopairWidgets extends Widgets {
   private static final Map<String, String> LBOUNDS = new HashMap<>();
   private static final Map<String, String> RBOUNDS = new HashMap<>();
   private final Map<String, String> pairs;
   private final Map<String, Binding> defaultBindings = new HashMap<>();
   private boolean enabled;

   public AutopairWidgets(LineReader reader) {
      this(reader, false);
   }

   public AutopairWidgets(LineReader reader, boolean addCurlyBrackets) {
      super(reader);
      this.pairs = new HashMap<>();
      this.pairs.put("`", "`");
      this.pairs.put("'", "'");
      this.pairs.put("\"", "\"");
      this.pairs.put("[", "]");
      this.pairs.put("(", ")");
      this.pairs.put(" ", " ");
      if (this.existsWidget("_autopair-insert")) {
         throw new IllegalStateException("AutopairWidgets already created!");
      } else {
         if (addCurlyBrackets) {
            this.pairs.put("{", "}");
         }

         this.addWidget("_autopair-insert", this::autopairInsert);
         this.addWidget("_autopair-close", this::autopairClose);
         this.addWidget("_autopair-backward-delete-char", this::autopairDelete);
         this.addWidget("autopair-toggle", this::toggleKeyBindings);
         KeyMap<Binding> map = this.getKeyMap();

         for (Entry<String, String> p : this.pairs.entrySet()) {
            this.defaultBindings.put(p.getKey(), map.getBound(p.getKey()));
            if (!p.getKey().equals(p.getValue())) {
               this.defaultBindings.put(p.getValue(), map.getBound(p.getValue()));
            }
         }
      }
   }

   public void enable() {
      if (!this.enabled) {
         this.toggle();
      }
   }

   public void disable() {
      if (this.enabled) {
         this.toggle();
      }
   }

   public boolean toggle() {
      boolean before = this.enabled;
      this.toggleKeyBindings();
      return !before;
   }

   public boolean autopairInsert() {
      if (this.pairs.containsKey(this.lastBinding())) {
         if (this.canSkip(this.lastBinding())) {
            this.callWidget("forward-char");
         } else if (this.canPair(this.lastBinding())) {
            this.callWidget("self-insert");
            this.putString(this.pairs.get(this.lastBinding()));
            this.callWidget("backward-char");
         } else {
            this.callWidget("self-insert");
         }
      } else {
         this.callWidget("self-insert");
      }

      return true;
   }

   public boolean autopairClose() {
      if (this.pairs.containsValue(this.lastBinding()) && this.currChar().equals(this.lastBinding())) {
         this.callWidget("forward-char");
      } else {
         this.callWidget("self-insert");
      }

      return true;
   }

   public boolean autopairDelete() {
      if (this.pairs.containsKey(this.prevChar()) && this.pairs.get(this.prevChar()).equals(this.currChar()) && this.canDelete(this.prevChar())) {
         this.callWidget("delete-char");
      }

      this.callWidget("backward-delete-char");
      return true;
   }

   public boolean toggleKeyBindings() {
      if (this.enabled) {
         this.defaultBindings();
      } else {
         this.customBindings();
      }

      return this.enabled;
   }

   private void customBindings() {
      boolean ttActive = this.tailtipEnabled();
      if (ttActive) {
         this.callWidget("tailtip-toggle");
      }

      KeyMap<Binding> map = this.getKeyMap();

      for (Entry<String, String> p : this.pairs.entrySet()) {
         map.bind(new Reference("_autopair-insert"), p.getKey());
         if (!p.getKey().equals(p.getValue())) {
            map.bind(new Reference("_autopair-close"), p.getValue());
         }
      }

      this.aliasWidget("_autopair-backward-delete-char", "backward-delete-char");
      if (ttActive) {
         this.callWidget("tailtip-toggle");
      }

      this.enabled = true;
   }

   private void defaultBindings() {
      KeyMap<Binding> map = this.getKeyMap();

      for (Entry<String, String> p : this.pairs.entrySet()) {
         map.bind(this.defaultBindings.get(p.getKey()), p.getKey());
         if (!p.getKey().equals(p.getValue())) {
            map.bind(this.defaultBindings.get(p.getValue()), p.getValue());
         }
      }

      this.aliasWidget(".backward-delete-char", "backward-delete-char");
      if (this.tailtipEnabled()) {
         this.callWidget("tailtip-toggle");
         this.callWidget("tailtip-toggle");
      }

      this.enabled = false;
   }

   private boolean tailtipEnabled() {
      return this.getWidget("accept-line").equals("_tailtip-accept-line");
   }

   private boolean canPair(String d) {
      return this.balanced(d) && !this.nexToBoundary(d) ? !d.equals(" ") || !this.prevChar().equals(" ") && !this.currChar().equals(" ") : false;
   }

   private boolean canSkip(String d) {
      return this.pairs.get(d).equals(d) && d.charAt(0) != ' ' && this.currChar().equals(d) && this.balanced(d);
   }

   private boolean canDelete(String d) {
      return this.balanced(d);
   }

   private boolean balanced(String d) {
      boolean out = false;
      Buffer buf = this.buffer();
      String lbuf = buf.upToCursor();
      String rbuf = buf.substring(lbuf.length());
      String regx1 = this.pairs.get(d).equals(d) ? d : "\\" + d;
      String regx2 = this.pairs.get(d).equals(d) ? this.pairs.get(d) : "\\" + this.pairs.get(d);
      int llen = lbuf.length() - lbuf.replaceAll(regx1, "").length();
      int rlen = rbuf.length() - rbuf.replaceAll(regx2, "").length();
      if (llen == 0 && rlen == 0) {
         out = true;
      } else if (d.charAt(0) == ' ') {
         out = true;
      } else if (this.pairs.get(d).equals(d)) {
         if (llen == rlen || (llen + rlen) % 2 == 0) {
            out = true;
         }
      } else {
         int l2len = lbuf.length() - lbuf.replaceAll(regx2, "").length();
         int r2len = rbuf.length() - rbuf.replaceAll(regx1, "").length();
         int ltotal = llen - l2len;
         int rtotal = rlen - r2len;
         if (ltotal < 0) {
            ltotal = 0;
         }

         if (ltotal >= rtotal) {
            out = true;
         }
      }

      return out;
   }

   private boolean boundary(String lb, String rb) {
      return lb.length() > 0 && this.prevChar().matches(lb) || rb.length() > 0 && this.currChar().matches(rb);
   }

   private boolean nexToBoundary(String d) {
      List<String> bk = new ArrayList<>();
      bk.add("all");
      if (d.matches("['\"`]")) {
         bk.add("quotes");
      } else if (d.matches("[{\\[(<]")) {
         bk.add("braces");
      } else if (d.charAt(0) == ' ') {
         bk.add("spaces");
      }

      if (LBOUNDS.containsKey(d) && RBOUNDS.containsKey(d)) {
         bk.add(d);
      }

      for (String k : bk) {
         if (this.boundary(LBOUNDS.get(k), RBOUNDS.get(k))) {
            return true;
         }
      }

      return false;
   }

   static {
      LBOUNDS.put("all", "[.:/\\!]");
      LBOUNDS.put("quotes", "[\\]})a-zA-Z0-9]");
      LBOUNDS.put("spaces", "[^{(\\[]");
      LBOUNDS.put("braces", "");
      LBOUNDS.put("`", "`");
      LBOUNDS.put("\"", "\"");
      LBOUNDS.put("'", "'");
      RBOUNDS.put("all", "[\\[{(<,.:?/%$!a-zA-Z0-9]");
      RBOUNDS.put("quotes", "[a-zA-Z0-9]");
      RBOUNDS.put("spaces", "[^\\]})]");
      RBOUNDS.put("braces", "");
      RBOUNDS.put("`", "");
      RBOUNDS.put("\"", "");
      RBOUNDS.put("'", "");
   }
}
