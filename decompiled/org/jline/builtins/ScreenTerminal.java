package org.jline.builtins;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jline.utils.Colors;
import org.jline.utils.WCWidth;

public class ScreenTerminal {
   private int width;
   private int height;
   private long attr;
   private boolean eol;
   private int cx;
   private int cy;
   private long[][] screen;
   private long[][] screen2;
   private ScreenTerminal.State vt100_parse_state = ScreenTerminal.State.None;
   private int vt100_parse_len;
   private int vt100_lastchar;
   private int vt100_parse_func;
   private String vt100_parse_param;
   private boolean vt100_mode_autowrap;
   private boolean vt100_mode_insert;
   private boolean vt100_charset_is_single_shift;
   private boolean vt100_charset_is_graphical;
   private boolean vt100_mode_lfnewline;
   private boolean vt100_mode_origin;
   private boolean vt100_mode_inverse;
   private boolean vt100_mode_cursorkey;
   private boolean vt100_mode_cursor;
   private boolean vt100_mode_alt_screen;
   private boolean vt100_mode_backspace;
   private boolean vt100_mode_column_switch;
   private boolean vt100_keyfilter_escape;
   private int[] vt100_charset_graph = new int[]{
      9674, 8230, 8226, 63, 182, 63, 176, 177, 63, 63, 43, 43, 43, 43, 43, 175, 8212, 8212, 8212, 95, 43, 43, 43, 43, 124, 8804, 8805, 182, 8800, 163, 183, 127
   };
   private int vt100_charset_g_sel;
   private int[] vt100_charset_g = new int[]{0, 0};
   private Map<String, Object> vt100_saved;
   private Map<String, Object> vt100_saved2;
   private int vt100_alternate_saved_cx;
   private int vt100_alternate_saved_cy;
   private int vt100_saved_cx;
   private int vt100_saved_cy;
   private String vt100_out;
   private int scroll_area_y0;
   private int scroll_area_y1;
   private List<Integer> tab_stops;
   private final List<long[]> history = new ArrayList<>();
   private AtomicBoolean dirty = new AtomicBoolean(true);

   public ScreenTerminal() {
      this(80, 24);
   }

   public ScreenTerminal(int width, int height) {
      this.width = width;
      this.height = height;
      this.reset_hard();
   }

   private void reset_hard() {
      this.attr = 0L;
      this.vt100_keyfilter_escape = false;
      this.vt100_lastchar = 0;
      this.vt100_parse_len = 0;
      this.vt100_parse_state = ScreenTerminal.State.None;
      this.vt100_parse_func = 0;
      this.vt100_parse_param = "";
      this.vt100_out = "";
      this.reset_screen();
      this.reset_soft();
   }

   private void reset_soft() {
      this.attr = 0L;
      this.scroll_area_y0 = 0;
      this.scroll_area_y1 = this.height;
      this.vt100_charset_is_single_shift = false;
      this.vt100_charset_is_graphical = false;
      this.vt100_charset_g_sel = 0;
      this.vt100_charset_g = new int[]{0, 0};
      this.vt100_mode_insert = false;
      this.vt100_mode_lfnewline = false;
      this.vt100_mode_cursorkey = false;
      this.vt100_mode_column_switch = false;
      this.vt100_mode_inverse = false;
      this.vt100_mode_origin = false;
      this.vt100_mode_autowrap = true;
      this.vt100_mode_cursor = true;
      this.vt100_mode_alt_screen = false;
      this.vt100_mode_backspace = false;
      this.esc_DECSC();
      this.vt100_saved2 = this.vt100_saved;
      this.esc_DECSC();
   }

   private void reset_screen() {
      this.screen = (long[][])Array.newInstance(long.class, this.height, this.width);
      this.screen2 = (long[][])Array.newInstance(long.class, this.height, this.width);

      for (int i = 0; i < this.height; i++) {
         Arrays.fill(this.screen[i], this.attr | 32L);
         Arrays.fill(this.screen2[i], this.attr | 32L);
      }

      this.scroll_area_y0 = 0;
      this.scroll_area_y1 = this.height;
      this.cx = 0;
      this.cy = 0;
      this.tab_stops = new ArrayList<>();

      for (int i = 7; i < this.width; i += 8) {
         this.tab_stops.add(i);
      }
   }

   private int utf8_charwidth(int c) {
      return WCWidth.wcwidth(c);
   }

   private long[] peek(int y0, int x0, int y1, int x1) {
      int from = this.width * y0 + x0;
      int to = this.width * (y1 - 1) + x1;
      int newLength = to - from;
      if (newLength < 0) {
         throw new IllegalArgumentException(from + " > " + to);
      } else {
         long[] copy = new long[newLength];
         int cur = from;

         while (cur < to) {
            int y = cur / this.width;
            int x = cur % this.width;
            int nb = Math.min(this.width - x, to - cur);
            System.arraycopy(this.screen[y], x, copy, cur - from, nb);
            cur += nb;
         }

         return copy;
      }
   }

   private void poke(int y, int x, long[] s) {
      int cur = 0;
      int max = s.length;

      while (cur < max) {
         int nb = Math.min(this.width - x, max - cur);
         System.arraycopy(s, cur, this.screen[y++], x, nb);
         x = 0;
         cur += nb;
      }

      this.setDirty();
   }

   private void fill(int y0, int x0, int y1, int x1, long c) {
      if (y0 == y1 - 1) {
         if (x0 < x1 - 1) {
            Arrays.fill(this.screen[y0], x0, x1, c);
            this.setDirty();
         }
      } else if (y0 < y1 - 1) {
         Arrays.fill(this.screen[y0], x0, this.width, c);

         for (int i = y0; i < y1 - 1; i++) {
            Arrays.fill(this.screen[i], c);
         }

         Arrays.fill(this.screen[y1 - 1], 0, x1, c);
         this.setDirty();
      }
   }

   private void clear(int y0, int x0, int y1, int x1) {
      this.fill(y0, x0, y1, x1, this.attr | 32L);
   }

   private void scroll_area_up(int y0, int y1) {
      this.scroll_area_up(y0, y1, 1);
   }

   private void scroll_area_up(int y0, int y1, int n) {
      n = Math.min(y1 - y0, n);
      if (y0 == 0 && y1 == this.height) {
         for (int i = 0; i < n; i++) {
            this.history.add(this.screen[i]);
         }

         System.arraycopy(this.screen, n, this.screen, 0, this.height - n);

         for (int i = 1; i <= n; i++) {
            this.screen[y1 - i] = new long[this.width];
            Arrays.fill(this.screen[y1 - 1], this.attr | 32L);
         }
      } else {
         this.poke(y0, 0, this.peek(y0 + n, 0, y1, this.width));
         this.clear(y1 - n, 0, y1, this.width);
      }
   }

   private void scroll_area_down(int y0, int y1) {
      this.scroll_area_down(y0, y1, 1);
   }

   private void scroll_area_down(int y0, int y1, int n) {
      n = Math.min(y1 - y0, n);
      this.poke(y0 + n, 0, this.peek(y0, 0, y1 - n, this.width));
      this.clear(y0, 0, y0 + n, this.width);
   }

   private void scroll_area_set(int y0, int y1) {
      y0 = Math.max(0, Math.min(this.height - 1, y0));
      y1 = Math.max(1, Math.min(this.height, y1));
      if (y1 > y0) {
         this.scroll_area_y0 = y0;
         this.scroll_area_y1 = y1;
      }
   }

   private void scroll_line_right(int y, int x) {
      this.scroll_line_right(y, x, 1);
   }

   private void scroll_line_right(int y, int x, int n) {
      if (x < this.width) {
         n = Math.min(this.width - this.cx, n);
         this.poke(y, x + n, this.peek(y, x, y + 1, this.width - n));
         this.clear(y, x, y + 1, x + n);
      }
   }

   private void scroll_line_left(int y, int x) {
      this.scroll_line_left(y, x, 1);
   }

   private void scroll_line_left(int y, int x, int n) {
      if (x < this.width) {
         n = Math.min(this.width - this.cx, n);
         this.poke(y, x, this.peek(y, x + n, y + 1, this.width));
         this.clear(y, this.width - n, y + 1, this.width);
      }
   }

   private int[] cursor_line_width(int next_char) {
      int wx = this.utf8_charwidth(next_char);
      int lx = 0;

      for (int x = 0; x < Math.min(this.cx, this.width); x++) {
         int c = (int)(this.peek(this.cy, x, this.cy + 1, x + 1)[0] & 4294967295L);
         wx += this.utf8_charwidth(c);
         lx++;
      }

      return new int[]{wx, lx};
   }

   private void cursor_up() {
      this.cursor_up(1);
   }

   private void cursor_up(int n) {
      this.cy = Math.max(this.scroll_area_y0, this.cy - n);
      this.setDirty();
   }

   private void cursor_down() {
      this.cursor_down(1);
   }

   private void cursor_down(int n) {
      this.cy = Math.min(this.scroll_area_y1 - 1, this.cy + n);
      this.setDirty();
   }

   private void cursor_left() {
      this.cursor_left(1);
   }

   private void cursor_left(int n) {
      this.eol = false;
      this.cx = Math.max(0, this.cx - n);
      this.setDirty();
   }

   private void cursor_right() {
      this.cursor_right(1);
   }

   private void cursor_right(int n) {
      this.eol = this.cx + n >= this.width;
      this.cx = Math.min(this.width - 1, this.cx + n);
      this.setDirty();
   }

   private void cursor_set_x(int x) {
      this.eol = false;
      this.cx = Math.max(0, x);
      this.setDirty();
   }

   private void cursor_set_y(int y) {
      this.cy = Math.max(0, Math.min(this.height - 1, y));
      this.setDirty();
   }

   private void cursor_set(int y, int x) {
      this.cursor_set_x(x);
      this.cursor_set_y(y);
   }

   private void ctrl_BS() {
      int dy = (this.cx - 1) / this.width;
      this.cursor_set(Math.max(this.scroll_area_y0, this.cy + dy), (this.cx - 1) % this.width);
   }

   private void ctrl_HT() {
      this.ctrl_HT(1);
   }

   private void ctrl_HT(int n) {
      if (n <= 0 || this.cx < this.width) {
         if (n > 0 || this.cx != 0) {
            int ts = -1;

            for (int i = 0; i < this.tab_stops.size(); i++) {
               if (this.cx >= this.tab_stops.get(i)) {
                  ts = i;
               }
            }

            ts += n;
            if (ts < this.tab_stops.size() && ts >= 0) {
               this.cursor_set_x(this.tab_stops.get(ts));
            } else {
               this.cursor_set_x(this.width - 1);
            }
         }
      }
   }

   private void ctrl_LF() {
      if (this.vt100_mode_lfnewline) {
         this.ctrl_CR();
      }

      if (this.cy == this.scroll_area_y1 - 1) {
         this.scroll_area_up(this.scroll_area_y0, this.scroll_area_y1);
      } else {
         this.cursor_down();
      }
   }

   private void ctrl_CR() {
      this.cursor_set_x(0);
   }

   private boolean dumb_write(int c) {
      if (c >= 32) {
         return false;
      } else {
         if (c == 8) {
            this.ctrl_BS();
         } else if (c == 9) {
            this.ctrl_HT();
         } else if (c >= 10 && c <= 12) {
            this.ctrl_LF();
         } else if (c == 13) {
            this.ctrl_CR();
         }

         return true;
      }
   }

   private void dumb_echo(int c) {
      if (this.eol) {
         if (this.vt100_mode_autowrap) {
            this.ctrl_CR();
            this.ctrl_LF();
         } else {
            this.cx = this.cursor_line_width(c)[1] - 1;
         }
      }

      if (this.vt100_mode_insert) {
         this.scroll_line_right(this.cy, this.cx);
      }

      if (this.vt100_charset_is_single_shift) {
         this.vt100_charset_is_single_shift = false;
      } else if (this.vt100_charset_is_graphical && (c & 65504) == 96) {
         c = this.vt100_charset_graph[c - 96];
      }

      this.poke(this.cy, this.cx, new long[]{this.attr | c});
      this.cursor_right();
   }

   private void vt100_charset_update() {
      this.vt100_charset_is_graphical = this.vt100_charset_g[this.vt100_charset_g_sel] == 2;
   }

   private void vt100_charset_set(int g) {
      this.vt100_charset_g_sel = g;
      this.vt100_charset_update();
   }

   private void vt100_charset_select(int g, int charset) {
      this.vt100_charset_g[g] = charset;
      this.vt100_charset_update();
   }

   private void vt100_setmode(String p, boolean state) {
      String[] ps = this.vt100_parse_params(p, new String[0]);

      for (String m : ps) {
         switch (m) {
            case "4":
               this.vt100_mode_insert = state;
               break;
            case "20":
               this.vt100_mode_lfnewline = state;
               break;
            case "?1":
               this.vt100_mode_cursorkey = state;
               break;
            case "?3":
               if (this.vt100_mode_column_switch) {
                  if (state) {
                     this.width = 132;
                  } else {
                     this.width = 80;
                  }

                  this.reset_screen();
               }
               break;
            case "?5":
               this.vt100_mode_inverse = state;
               break;
            case "?6":
               this.vt100_mode_origin = state;
               if (state) {
                  this.cursor_set(this.scroll_area_y0, 0);
               } else {
                  this.cursor_set(0, 0);
               }
               break;
            case "?7":
               this.vt100_mode_autowrap = state;
               break;
            case "?25":
               this.vt100_mode_cursor = state;
               break;
            case "?40":
               this.vt100_mode_column_switch = state;
               break;
            case "?1049":
               if (state && !this.vt100_mode_alt_screen || !state && this.vt100_mode_alt_screen) {
                  long[][] s = this.screen;
                  this.screen = this.screen2;
                  this.screen2 = s;
                  Map<String, Object> map = this.vt100_saved;
                  this.vt100_saved = this.vt100_saved2;
                  this.vt100_saved2 = map;
                  int c = this.vt100_alternate_saved_cx;
                  this.vt100_alternate_saved_cx = this.cx;
                  this.cx = Math.min(c, this.width - 1);
                  c = this.vt100_alternate_saved_cy;
                  this.vt100_alternate_saved_cy = this.cy;
                  this.cy = Math.min(c, this.height - 1);
               }

               this.vt100_mode_alt_screen = state;
               break;
            case "?67":
               this.vt100_mode_backspace = state;
         }
      }
   }

   private void ctrl_SO() {
      this.vt100_charset_set(1);
   }

   private void ctrl_SI() {
      this.vt100_charset_set(0);
   }

   private void esc_CSI() {
      this.vt100_parse_reset(ScreenTerminal.State.Csi);
   }

   private void esc_DECALN() {
      this.fill(0, 0, this.height, this.width, 16711749L);
   }

   private void esc_G0_0() {
      this.vt100_charset_select(0, 0);
   }

   private void esc_G0_1() {
      this.vt100_charset_select(0, 1);
   }

   private void esc_G0_2() {
      this.vt100_charset_select(0, 2);
   }

   private void esc_G0_3() {
      this.vt100_charset_select(0, 3);
   }

   private void esc_G0_4() {
      this.vt100_charset_select(0, 4);
   }

   private void esc_G1_0() {
      this.vt100_charset_select(1, 0);
   }

   private void esc_G1_1() {
      this.vt100_charset_select(1, 1);
   }

   private void esc_G1_2() {
      this.vt100_charset_select(1, 2);
   }

   private void esc_G1_3() {
      this.vt100_charset_select(1, 3);
   }

   private void esc_G1_4() {
      this.vt100_charset_select(1, 4);
   }

   private void esc_DECSC() {
      this.vt100_saved = new HashMap<>();
      this.vt100_saved.put("cx", this.cx);
      this.vt100_saved.put("cy", this.cy);
      this.vt100_saved.put("attr", this.attr);
      this.vt100_saved.put("vt100_charset_g_sel", this.vt100_charset_g_sel);
      this.vt100_saved.put("vt100_charset_g", this.vt100_charset_g);
      this.vt100_saved.put("vt100_mode_autowrap", this.vt100_mode_autowrap);
      this.vt100_saved.put("vt100_mode_origin", this.vt100_mode_origin);
   }

   private void esc_DECRC() {
      this.cx = (Integer)this.vt100_saved.get("cx");
      this.cy = (Integer)this.vt100_saved.get("cy");
      this.attr = (Long)this.vt100_saved.get("attr");
      this.vt100_charset_g_sel = (Integer)this.vt100_saved.get("vt100_charset_g_sel");
      this.vt100_charset_g = (int[])this.vt100_saved.get("vt100_charset_g");
      this.vt100_charset_update();
      this.vt100_mode_autowrap = (Boolean)this.vt100_saved.get("vt100_mode_autowrap");
      this.vt100_mode_origin = (Boolean)this.vt100_saved.get("vt100_mode_origin");
   }

   private void esc_IND() {
      this.ctrl_LF();
   }

   private void esc_NEL() {
      this.ctrl_CR();
      this.ctrl_LF();
   }

   private void esc_HTS() {
      this.csi_CTC("0");
   }

   private void esc_RI() {
      if (this.cy == this.scroll_area_y0) {
         this.scroll_area_down(this.scroll_area_y0, this.scroll_area_y1);
      } else {
         this.cursor_up();
      }
   }

   private void esc_SS2() {
      this.vt100_charset_is_single_shift = true;
   }

   private void esc_SS3() {
      this.vt100_charset_is_single_shift = true;
   }

   private void esc_DCS() {
      this.vt100_parse_reset(ScreenTerminal.State.Str);
   }

   private void esc_SOS() {
      this.vt100_parse_reset(ScreenTerminal.State.Str);
   }

   private void esc_DECID() {
      this.csi_DA("0");
   }

   private void esc_ST() {
   }

   private void esc_OSC() {
      this.vt100_parse_reset(ScreenTerminal.State.Str);
   }

   private void esc_PM() {
      this.vt100_parse_reset(ScreenTerminal.State.Str);
   }

   private void esc_APC() {
      this.vt100_parse_reset(ScreenTerminal.State.Str);
   }

   private void esc_RIS() {
      this.reset_hard();
   }

   private void csi_ICH(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1});
      this.scroll_line_right(this.cy, this.cx, ps[0]);
   }

   private void csi_CUU(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1});
      this.cursor_up(Math.max(1, ps[0]));
   }

   private void csi_CUD(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1});
      this.cursor_down(Math.max(1, ps[0]));
   }

   private void csi_CUF(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1});
      this.cursor_right(Math.max(1, ps[0]));
   }

   private void csi_CUB(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1});
      this.cursor_left(Math.max(1, ps[0]));
   }

   private void csi_CNL(String p) {
      this.csi_CUD(p);
      this.ctrl_CR();
   }

   private void csi_CPL(String p) {
      this.csi_CUU(p);
      this.ctrl_CR();
   }

   private void csi_CHA(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1});
      this.cursor_set_x(ps[0] - 1);
   }

   private void csi_CUP(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1, 1});
      if (this.vt100_mode_origin) {
         this.cursor_set(this.scroll_area_y0 + ps[0] - 1, ps[1] - 1);
      } else {
         this.cursor_set(ps[0] - 1, ps[1] - 1);
      }
   }

   private void csi_CHT(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1});
      this.ctrl_HT(Math.max(1, ps[0]));
   }

   private void csi_ED(String p) {
      String[] ps = this.vt100_parse_params(p, new String[]{"0"});
      if ("0".equals(ps[0])) {
         this.clear(this.cy, this.cx, this.height, this.width);
      } else if ("1".equals(ps[0])) {
         this.clear(0, 0, this.cy + 1, this.cx + 1);
      } else if ("2".equals(ps[0])) {
         this.clear(0, 0, this.height, this.width);
      }
   }

   private void csi_EL(String p) {
      String[] ps = this.vt100_parse_params(p, new String[]{"0"});
      if ("0".equals(ps[0])) {
         this.clear(this.cy, this.cx, this.cy + 1, this.width);
      } else if ("1".equals(ps[0])) {
         this.clear(this.cy, 0, this.cy + 1, this.cx + 1);
      } else if ("2".equals(ps[0])) {
         this.clear(this.cy, 0, this.cy + 1, this.width);
      }
   }

   private void csi_IL(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1});
      if (this.cy >= this.scroll_area_y0 && this.cy < this.scroll_area_y1) {
         this.scroll_area_down(this.cy, this.scroll_area_y1, Math.max(1, ps[0]));
      }
   }

   private void csi_DL(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1});
      if (this.cy >= this.scroll_area_y0 && this.cy < this.scroll_area_y1) {
         this.scroll_area_up(this.cy, this.scroll_area_y1, Math.max(1, ps[0]));
      }
   }

   private void csi_DCH(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1});
      this.scroll_line_left(this.cy, this.cx, Math.max(1, ps[0]));
   }

   private void csi_SU(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1});
      this.scroll_area_up(this.scroll_area_y0, this.scroll_area_y1, Math.max(1, ps[0]));
   }

   private void csi_SD(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1});
      this.scroll_area_down(this.scroll_area_y0, this.scroll_area_y1, Math.max(1, ps[0]));
   }

   private void csi_CTC(String p) {
      String[] ps = this.vt100_parse_params(p, new String[]{"0"});

      for (String m : ps) {
         if ("0".equals(m)) {
            if (this.tab_stops.indexOf(this.cx) < 0) {
               this.tab_stops.add(this.cx);
               Collections.sort(this.tab_stops);
            }
         } else if ("2".equals(m)) {
            this.tab_stops.remove(Integer.valueOf(this.cx));
         } else if ("5".equals(m)) {
            this.tab_stops = new ArrayList<>();
         }
      }
   }

   private void csi_ECH(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1});
      int n = Math.min(this.width - this.cx, Math.max(1, ps[0]));
      this.clear(this.cy, this.cx, this.cy + 1, this.cx + n);
   }

   private void csi_CBT(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1});
      this.ctrl_HT(1 - Math.max(1, ps[0]));
   }

   private void csi_HPA(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1});
      this.cursor_set_x(ps[0] - 1);
   }

   private void csi_HPR(String p) {
      this.csi_CUF(p);
   }

   private void csi_REP(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1});
      if (this.vt100_lastchar >= 32) {
         int n = Math.min(2000, Math.max(1, ps[0]));

         while (n-- > 0) {
            this.dumb_echo(this.vt100_lastchar);
         }

         this.vt100_lastchar = 0;
      }
   }

   private void csi_DA(String p) {
      String[] ps = this.vt100_parse_params(p, new String[]{"0"});
      if ("0".equals(ps[0])) {
         this.vt100_out = "\u001b[?1;2c";
      } else if (">0".equals(ps[0]) || ">".equals(ps[0])) {
         this.vt100_out = "\u001b[>0;184;0c";
      }
   }

   private void csi_VPA(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1});
      this.cursor_set_y(ps[0] - 1);
   }

   private void csi_VPR(String p) {
      this.csi_CUD(p);
   }

   private void csi_HVP(String p) {
      this.csi_CUP(p);
   }

   private void csi_TBC(String p) {
      String[] ps = this.vt100_parse_params(p, new String[]{"0"});
      if ("0".equals(ps[0])) {
         this.csi_CTC("2");
      } else if ("3".equals(ps[0])) {
         this.csi_CTC("5");
      }
   }

   private void csi_SM(String p) {
      this.vt100_setmode(p, true);
   }

   private void csi_RM(String p) {
      this.vt100_setmode(p, false);
   }

   private void csi_SGR(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{0});

      for (int i = 0; i < ps.length; i++) {
         int m = ps[i];
         if (m == 0) {
            this.attr = 0L;
         } else if (m == 1) {
            this.attr |= 576460752303423488L;
         } else if (m == 4) {
            this.attr |= 72057594037927936L;
         } else if (m == 7) {
            this.attr |= 144115188075855872L;
         } else if (m == 8) {
            this.attr |= 288230376151711744L;
         } else if (m == 21) {
            this.attr &= -576460756598390784L;
         } else if (m == 24) {
            this.attr &= -72057598332895232L;
         } else if (m == 27) {
            this.attr &= -144115192370823168L;
         } else if (m == 28) {
            this.attr &= -288230380446679040L;
         } else if (m >= 30 && m <= 37) {
            this.attr = this.attr & -1224961510753697792L | 1152921504606846976L | this.col24(m - 30) << 44;
         } else if (m == 38) {
            i++;
            m = i < ps.length ? ps[i] : 0;
            if (m == 5) {
               i++;
               m = i < ps.length ? ps[i] : 0;
               this.attr = this.attr & -1224961510753697792L | 1152921504606846976L | this.col24(m) << 44;
            }
         } else if (m == 39) {
            this.attr &= -1224961510753697792L;
         } else if (m >= 40 && m <= 47) {
            this.attr = this.attr & -2305860601399738368L | 2305843009213693952L | this.col24(m - 40) << 32;
         } else if (m == 48) {
            i++;
            m = i < ps.length ? ps[i] : 0;
            if (m == 5) {
               i++;
               m = i < ps.length ? ps[i] : 0;
               this.attr = this.attr & -2305860601399738368L | 2305843009213693952L | this.col24(m) << 32;
            }
         } else if (m == 49) {
            this.attr &= -2377883015360544768L;
         } else if (m >= 90 && m <= 97) {
            this.attr = this.attr & -1224961510753697792L | 1152921504606846976L | this.col24(m - 90 + 8) << 44;
         } else if (m >= 100 && m <= 107) {
            this.attr = this.attr & -2305860601399738368L | 2305843009213693952L | this.col24(m - 100 + 8) << 32;
         }
      }
   }

   private long col24(int col) {
      int c = Colors.rgbColor(col);
      int r = c >> 16 & 0xFF;
      int g = c >> 8 & 0xFF;
      int b = c >> 0 & 0xFF;
      return r >> 4 << 8 | g >> 4 << 4 | b >> 4 << 0;
   }

   private void csi_DSR(String p) {
      String[] ps = this.vt100_parse_params(p, new String[]{"0"});
      if ("5".equals(ps[0])) {
         this.vt100_out = "\u001b[0n";
      } else if ("6".equals(ps[0])) {
         this.vt100_out = "\u001b[" + (this.cy + 1) + ";" + (this.cx + 1) + "R";
      } else if ("7".equals(ps[0])) {
         this.vt100_out = "gogo-term";
      } else if ("8".equals(ps[0])) {
         this.vt100_out = "1.0-SNAPSHOT";
      } else if ("?6".equals(ps[0])) {
         this.vt100_out = "\u001b[" + (this.cy + 1) + ";" + (this.cx + 1) + ";0R";
      } else if ("?15".equals(ps[0])) {
         this.vt100_out = "\u001b[?13n";
      } else if ("?25".equals(ps[0])) {
         this.vt100_out = "\u001b[?20n";
      } else if ("?26".equals(ps[0])) {
         this.vt100_out = "\u001b[?27;1n";
      } else if ("?53".equals(ps[0])) {
         this.vt100_out = "\u001b[?53n";
      }
   }

   private void csi_DECSTBM(String p) {
      int[] ps = this.vt100_parse_params(p, new int[]{1, this.height});
      this.scroll_area_set(ps[0] - 1, ps[1]);
      if (this.vt100_mode_origin) {
         this.cursor_set(this.scroll_area_y0, 0);
      } else {
         this.cursor_set(0, 0);
      }
   }

   private void csi_SCP(String p) {
      this.vt100_saved_cx = this.cx;
      this.vt100_saved_cy = this.cy;
   }

   private void csi_RCP(String p) {
      this.cx = this.vt100_saved_cx;
      this.cy = this.vt100_saved_cy;
   }

   private void csi_DECREQTPARM(String p) {
      String[] ps = this.vt100_parse_params(p, new String[0]);
      if ("0".equals(ps[0])) {
         this.vt100_out = "\u001b[2;1;1;112;112;1;0x";
      } else if ("1".equals(ps[0])) {
         this.vt100_out = "\u001b[3;1;1;112;112;1;0x";
      }
   }

   private void csi_DECSTR(String p) {
      this.reset_soft();
   }

   private String[] vt100_parse_params(String p, String[] defaults) {
      String prefix = "";
      if (!p.isEmpty() && p.charAt(0) >= '<' && p.charAt(0) <= '?') {
         prefix = "" + p.charAt(0);
         p = p.substring(1);
      }

      String[] ps = p.split(";");
      int n = Math.max(ps.length, defaults.length);
      String[] values = new String[n];

      for (int i = 0; i < n; i++) {
         String value = null;
         if (i < ps.length && !ps[i].isEmpty()) {
            value = prefix + ps[i];
         }

         if (value == null && i < defaults.length) {
            value = defaults[i];
         }

         if (value == null) {
            value = "";
         }

         values[i] = value;
      }

      return values;
   }

   private int[] vt100_parse_params(String p, int[] defaults) {
      String prefix = "";
      p = p == null ? "" : p;
      if (!p.isEmpty() && p.charAt(0) >= '<' && p.charAt(0) <= '?') {
         prefix = p.substring(0, 1);
         p = p.substring(1);
      }

      String[] ps = p.split(";");
      int n = Math.max(ps.length, defaults.length);
      int[] values = new int[n];

      for (int i = 0; i < n; i++) {
         Integer value = null;
         if (i < ps.length) {
            String v = prefix + ps[i];

            try {
               value = Integer.parseInt(v);
            } catch (NumberFormatException var11) {
            }
         }

         if (value == null && i < defaults.length) {
            value = defaults[i];
         }

         if (value == null) {
            value = 0;
         }

         values[i] = value;
      }

      return values;
   }

   private void vt100_parse_reset() {
      this.vt100_parse_reset(ScreenTerminal.State.None);
   }

   private void vt100_parse_reset(ScreenTerminal.State state) {
      this.vt100_parse_state = state;
      this.vt100_parse_len = 0;
      this.vt100_parse_func = 0;
      this.vt100_parse_param = "";
   }

   private void vt100_parse_process() {
      if (this.vt100_parse_state == ScreenTerminal.State.Esc) {
         switch (this.vt100_parse_func) {
            case 54:
            case 66:
            case 67:
            case 73:
            case 74:
            case 75:
            case 76:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 90:
            case 96:
            case 97:
            case 98:
            case 100:
            case 108:
            case 110:
            case 111:
            case 124:
            case 125:
            case 126:
            default:
               break;
            case 55:
               this.esc_DECSC();
               break;
            case 56:
               this.esc_DECRC();
               break;
            case 68:
               this.esc_IND();
               break;
            case 69:
               this.esc_NEL();
               break;
            case 70:
               this.esc_NEL();
               break;
            case 72:
               this.esc_HTS();
               break;
            case 77:
               this.esc_RI();
               break;
            case 78:
               this.esc_SS2();
               break;
            case 79:
               this.esc_SS3();
               break;
            case 80:
               this.esc_DCS();
               break;
            case 88:
               this.esc_SOS();
               break;
            case 91:
               this.esc_CSI();
               break;
            case 92:
               this.esc_ST();
               break;
            case 93:
               this.esc_OSC();
               break;
            case 94:
               this.esc_PM();
               break;
            case 95:
               this.esc_APC();
               break;
            case 99:
               this.esc_RIS();
               break;
            case 9016:
               this.esc_DECALN();
               break;
            case 10288:
               this.esc_G0_2();
               break;
            case 10289:
               this.esc_G0_3();
               break;
            case 10290:
               this.esc_G0_4();
               break;
            case 10305:
               this.esc_G0_0();
               break;
            case 10306:
               this.esc_G0_1();
               break;
            case 10544:
               this.esc_G1_2();
               break;
            case 10545:
               this.esc_G1_3();
               break;
            case 10546:
               this.esc_G1_4();
               break;
            case 10561:
               this.esc_G1_0();
               break;
            case 10562:
               this.esc_G1_1();
         }

         if (this.vt100_parse_state == ScreenTerminal.State.Esc) {
            this.vt100_parse_reset();
         }
      } else {
         switch (this.vt100_parse_func) {
            case 64:
               this.csi_ICH(this.vt100_parse_param);
               break;
            case 65:
               this.csi_CUU(this.vt100_parse_param);
               break;
            case 66:
               this.csi_CUD(this.vt100_parse_param);
               break;
            case 67:
               this.csi_CUF(this.vt100_parse_param);
               break;
            case 68:
               this.csi_CUB(this.vt100_parse_param);
               break;
            case 69:
               this.csi_CNL(this.vt100_parse_param);
               break;
            case 70:
               this.csi_CPL(this.vt100_parse_param);
               break;
            case 71:
               this.csi_CHA(this.vt100_parse_param);
               break;
            case 72:
               this.csi_CUP(this.vt100_parse_param);
               break;
            case 73:
               this.csi_CHT(this.vt100_parse_param);
               break;
            case 74:
               this.csi_ED(this.vt100_parse_param);
               break;
            case 75:
               this.csi_EL(this.vt100_parse_param);
               break;
            case 76:
               this.csi_IL(this.vt100_parse_param);
               break;
            case 77:
               this.csi_DL(this.vt100_parse_param);
            case 78:
            case 79:
            case 81:
            case 82:
            case 85:
            case 86:
            case 89:
            case 91:
            case 92:
            case 93:
            case 94:
            case 105:
            case 106:
            case 107:
            case 111:
            case 8256:
            case 8257:
            case 8258:
            case 8259:
            case 8260:
            case 8261:
            case 8262:
            case 8263:
            case 8264:
            case 8265:
            case 8266:
            case 8267:
            case 8268:
            case 8269:
            case 8270:
            case 8271:
            case 8272:
            case 8273:
            case 8274:
            case 8275:
            case 8276:
            case 8277:
            case 8278:
            case 8279:
            case 8280:
            case 8281:
            case 8282:
            case 8283:
            case 8284:
            case 8285:
            case 8286:
            case 8287:
            case 8288:
            case 8289:
            case 8290:
            case 8291:
            case 8292:
            case 8293:
            case 8294:
            case 8295:
            case 8296:
            case 8297:
            case 8298:
            case 8299:
            case 9330:
            case 9335:
            default:
               break;
            case 80:
               this.csi_DCH(this.vt100_parse_param);
               break;
            case 83:
               this.csi_SU(this.vt100_parse_param);
               break;
            case 84:
               this.csi_SD(this.vt100_parse_param);
               break;
            case 87:
               this.csi_CTC(this.vt100_parse_param);
               break;
            case 88:
               this.csi_ECH(this.vt100_parse_param);
               break;
            case 90:
               this.csi_CBT(this.vt100_parse_param);
               break;
            case 96:
               this.csi_HPA(this.vt100_parse_param);
               break;
            case 97:
               this.csi_HPR(this.vt100_parse_param);
               break;
            case 98:
               this.csi_REP(this.vt100_parse_param);
               break;
            case 99:
               this.csi_DA(this.vt100_parse_param);
               break;
            case 100:
               this.csi_VPA(this.vt100_parse_param);
               break;
            case 101:
               this.csi_VPR(this.vt100_parse_param);
               break;
            case 102:
               this.csi_HVP(this.vt100_parse_param);
               break;
            case 103:
               this.csi_TBC(this.vt100_parse_param);
               break;
            case 104:
               this.csi_SM(this.vt100_parse_param);
               break;
            case 108:
               this.csi_RM(this.vt100_parse_param);
               break;
            case 109:
               this.csi_SGR(this.vt100_parse_param);
               break;
            case 110:
               this.csi_DSR(this.vt100_parse_param);
               break;
            case 114:
               this.csi_DECSTBM(this.vt100_parse_param);
               break;
            case 115:
               this.csi_SCP(this.vt100_parse_param);
               break;
            case 117:
               this.csi_RCP(this.vt100_parse_param);
               break;
            case 120:
               this.csi_DECREQTPARM(this.vt100_parse_param);
               break;
            case 8560:
               this.csi_DECSTR(this.vt100_parse_param);
         }

         if (this.vt100_parse_state == ScreenTerminal.State.Csi) {
            this.vt100_parse_reset();
         }
      }
   }

   private boolean vt100_write(int c) {
      if (c < 32) {
         if (c == 27) {
            this.vt100_parse_reset(ScreenTerminal.State.Esc);
            return true;
         }

         if (c == 14) {
            this.ctrl_SO();
         } else if (c == 15) {
            this.ctrl_SI();
         }
      } else if ((c & 65504) == 128) {
         this.vt100_parse_reset(ScreenTerminal.State.Esc);
         this.vt100_parse_func = (char)(c - 64);
         this.vt100_parse_process();
         return true;
      }

      if (this.vt100_parse_state != ScreenTerminal.State.None) {
         if (this.vt100_parse_state == ScreenTerminal.State.Str) {
            if (c >= 32) {
               return true;
            }

            this.vt100_parse_reset();
         } else if (c < 32) {
            if (c == 24 || c == 26) {
               this.vt100_parse_reset();
               return true;
            }
         } else {
            this.vt100_parse_len++;
            if (this.vt100_parse_len <= 32) {
               int msb = c & 240;
               if (msb == 32) {
                  this.vt100_parse_func <<= 8;
                  this.vt100_parse_func += (char)c;
               } else if (msb == 48 && this.vt100_parse_state == ScreenTerminal.State.Csi) {
                  this.vt100_parse_param = this.vt100_parse_param + String.valueOf((char)c);
               } else {
                  this.vt100_parse_func <<= 8;
                  this.vt100_parse_func += (char)c;
                  this.vt100_parse_process();
               }

               return true;
            }

            this.vt100_parse_reset();
         }
      }

      this.vt100_lastchar = c;
      return false;
   }

   public boolean isDirty() {
      return this.dirty.compareAndSet(true, false);
   }

   public synchronized void waitDirty() throws InterruptedException {
      while (!this.dirty.compareAndSet(true, false)) {
         this.wait();
      }
   }

   protected synchronized void setDirty() {
      this.dirty.set(true);
      this.notifyAll();
   }

   public synchronized boolean setSize(int w, int h) {
      if (w >= 2 && w <= 256 && h >= 2 && h <= 256) {
         for (int i = 0; i < this.height; i++) {
            if (this.screen[i].length < w) {
               int oldLength = this.screen[i].length;
               this.screen[i] = Arrays.copyOf(this.screen[i], w);

               for (int j = oldLength; j < w; j++) {
                  this.screen[i][j] = this.attr | 32L;
               }
            }

            if (this.screen2[i].length < w) {
               int oldLength = this.screen2[i].length;
               this.screen2[i] = Arrays.copyOf(this.screen2[i], w);

               for (int j = oldLength; j < w; j++) {
                  this.screen2[i][j] = this.attr | 32L;
               }
            }
         }

         if (this.cx >= w) {
            this.cx = w - 1;
         }

         if (h < this.height) {
            int needed = this.height - h;
            int avail = this.height - 1 - this.cy;
            if (avail > 0) {
               if (avail > needed) {
                  avail = needed;
               }

               this.screen = Arrays.copyOfRange(this.screen, 0, this.height - avail);
            }

            needed -= avail;

            for (int i = 0; i < needed; i++) {
               this.history.add(this.screen[i]);
            }

            this.screen = Arrays.copyOfRange(this.screen, needed, this.screen.length);
            this.cy -= needed;
         } else if (h > this.height) {
            int needed = h - this.height;
            int avail = this.history.size();
            if (avail > needed) {
               avail = needed;
            }

            long[][] sc = new long[h][];
            if (avail > 0) {
               for (int i = 0; i < avail; i++) {
                  long[] historyLine = this.history.remove(this.history.size() - avail + i);
                  if (historyLine.length < w) {
                     int oldLength = historyLine.length;
                     historyLine = Arrays.copyOf(historyLine, w);

                     for (int j = oldLength; j < w; j++) {
                        historyLine[j] = this.attr | 32L;
                     }
                  }

                  sc[i] = historyLine;
               }

               this.cy += avail;
            }

            System.arraycopy(this.screen, 0, sc, avail, this.screen.length);

            for (int i = avail + this.screen.length; i < sc.length; i++) {
               sc[i] = new long[w];
               Arrays.fill(sc[i], this.attr | 32L);
            }

            this.screen = sc;
         }

         this.screen2 = (long[][])Array.newInstance(long.class, h, w);

         for (int i = 0; i < h; i++) {
            Arrays.fill(this.screen2[i], this.attr | 32L);
         }

         this.scroll_area_y0 = Math.min(h, this.scroll_area_y0);
         this.scroll_area_y1 = this.scroll_area_y1 == this.height ? h : Math.min(h, this.scroll_area_y1);
         this.cx = Math.min(w - 1, this.cx);
         this.cy = Math.min(h - 1, this.cy);
         this.width = w;
         this.height = h;
         this.setDirty();
         return true;
      } else {
         return false;
      }
   }

   public synchronized String read() {
      String d = this.vt100_out;
      this.vt100_out = "";
      return d;
   }

   public synchronized String pipe(String d) {
      String o = "";

      for (char c : d.toCharArray()) {
         if (this.vt100_keyfilter_escape) {
            this.vt100_keyfilter_escape = false;
            if (this.vt100_mode_cursorkey) {
               switch (c) {
                  case '1':
                     o = o + "\u001b[5~";
                     break;
                  case '2':
                     o = o + "\u001b[6~";
                     break;
                  case '3':
                     o = o + "\u001b[2~";
                     break;
                  case '4':
                     o = o + "\u001b[3~";
                  case '5':
                  case '6':
                  case '7':
                  case '8':
                  case '9':
                  case ':':
                  case ';':
                  case '<':
                  case '=':
                  case '>':
                  case '?':
                  case '@':
                  case 'E':
                  case 'G':
                  case 'I':
                  case 'J':
                  case 'K':
                  case 'L':
                  case 'M':
                  case 'N':
                  case 'O':
                  case 'P':
                  case 'Q':
                  case 'R':
                  case 'S':
                  case 'T':
                  case 'U':
                  case 'V':
                  case 'W':
                  case 'X':
                  case 'Y':
                  case 'Z':
                  case '[':
                  case '\\':
                  case ']':
                  case '^':
                  case '_':
                  case '`':
                  case 'm':
                  case 'n':
                  case 'o':
                  case 'p':
                  case 'q':
                  case 'r':
                  case 's':
                  case 't':
                  case 'u':
                  case 'v':
                  case 'w':
                  case 'x':
                  case 'y':
                  case 'z':
                  case '{':
                  case '|':
                  case '}':
                  default:
                     break;
                  case 'A':
                     o = o + "\u001bOA";
                     break;
                  case 'B':
                     o = o + "\u001bOB";
                     break;
                  case 'C':
                     o = o + "\u001bOC";
                     break;
                  case 'D':
                     o = o + "\u001bOD";
                     break;
                  case 'F':
                     o = o + "\u001bOF";
                     break;
                  case 'H':
                     o = o + "\u001bOH";
                     break;
                  case 'a':
                     o = o + "\u001bOP";
                     break;
                  case 'b':
                     o = o + "\u001bOQ";
                     break;
                  case 'c':
                     o = o + "\u001bOR";
                     break;
                  case 'd':
                     o = o + "\u001bOS";
                     break;
                  case 'e':
                     o = o + "\u001b[15~";
                     break;
                  case 'f':
                     o = o + "\u001b[17~";
                     break;
                  case 'g':
                     o = o + "\u001b[18~";
                     break;
                  case 'h':
                     o = o + "\u001b[19~";
                     break;
                  case 'i':
                     o = o + "\u001b[20~";
                     break;
                  case 'j':
                     o = o + "\u001b[21~";
                     break;
                  case 'k':
                     o = o + "\u001b[23~";
                     break;
                  case 'l':
                     o = o + "\u001b[24~";
                     break;
                  case '~':
                     o = o + "~";
               }
            } else {
               switch (c) {
                  case '1':
                     o = o + "\u001b[5~";
                     break;
                  case '2':
                     o = o + "\u001b[6~";
                     break;
                  case '3':
                     o = o + "\u001b[2~";
                     break;
                  case '4':
                     o = o + "\u001b[3~";
                  case '5':
                  case '6':
                  case '7':
                  case '8':
                  case '9':
                  case ':':
                  case ';':
                  case '<':
                  case '=':
                  case '>':
                  case '?':
                  case '@':
                  case 'E':
                  case 'G':
                  case 'I':
                  case 'J':
                  case 'K':
                  case 'L':
                  case 'M':
                  case 'N':
                  case 'O':
                  case 'P':
                  case 'Q':
                  case 'R':
                  case 'S':
                  case 'T':
                  case 'U':
                  case 'V':
                  case 'W':
                  case 'X':
                  case 'Y':
                  case 'Z':
                  case '[':
                  case '\\':
                  case ']':
                  case '^':
                  case '_':
                  case '`':
                  case 'm':
                  case 'n':
                  case 'o':
                  case 'p':
                  case 'q':
                  case 'r':
                  case 's':
                  case 't':
                  case 'u':
                  case 'v':
                  case 'w':
                  case 'x':
                  case 'y':
                  case 'z':
                  case '{':
                  case '|':
                  case '}':
                  default:
                     break;
                  case 'A':
                     o = o + "\u001b[A";
                     break;
                  case 'B':
                     o = o + "\u001b[B";
                     break;
                  case 'C':
                     o = o + "\u001b[C";
                     break;
                  case 'D':
                     o = o + "\u001b[D";
                     break;
                  case 'F':
                     o = o + "\u001b[F";
                     break;
                  case 'H':
                     o = o + "\u001b[H";
                     break;
                  case 'a':
                     o = o + "\u001bOP";
                     break;
                  case 'b':
                     o = o + "\u001bOQ";
                     break;
                  case 'c':
                     o = o + "\u001bOR";
                     break;
                  case 'd':
                     o = o + "\u001bOS";
                     break;
                  case 'e':
                     o = o + "\u001b[15~";
                     break;
                  case 'f':
                     o = o + "\u001b[17~";
                     break;
                  case 'g':
                     o = o + "\u001b[18~";
                     break;
                  case 'h':
                     o = o + "\u001b[19~";
                     break;
                  case 'i':
                     o = o + "\u001b[20~";
                     break;
                  case 'j':
                     o = o + "\u001b[21~";
                     break;
                  case 'k':
                     o = o + "\u001b[23~";
                     break;
                  case 'l':
                     o = o + "\u001b[24~";
                     break;
                  case '~':
                     o = o + "~";
               }
            }
         } else if (c == '~') {
            this.vt100_keyfilter_escape = true;
         } else if (c == 127) {
            if (this.vt100_mode_backspace) {
               o = o + '\b';
            } else {
               o = o + '\u007f';
            }
         } else {
            o = o + c;
            if (this.vt100_mode_lfnewline && c == '\r') {
               o = o + '\n';
            }
         }
      }

      return o;
   }

   public synchronized boolean write(CharSequence d) {
      d.codePoints().forEachOrdered(c -> {
         if (!this.vt100_write(c) && !this.dumb_write(c) && c <= 65535) {
            this.dumb_echo(c);
         }
      });
      return true;
   }

   public synchronized void dump(long[] fullscreen, int ftop, int fleft, int fheight, int fwidth, int[] cursor) {
      int cx = Math.min(this.cx, this.width - 1);
      int cy = this.cy;

      for (int y = 0; y < Math.min(this.height, fheight - ftop); y++) {
         System.arraycopy(this.screen[y], 0, fullscreen, (y + ftop) * fwidth + fleft, this.width);
      }

      if (cursor != null) {
         cursor[0] = cx + fleft;
         cursor[1] = cy + ftop;
      }
   }

   public synchronized String dump(long timeout, boolean forceDump) throws InterruptedException {
      if (!this.dirty.get() && timeout > 0L) {
         this.wait(timeout);
      }

      if (!this.dirty.compareAndSet(true, false) && !forceDump) {
         return null;
      } else {
         StringBuilder sb = new StringBuilder();
         int prev_attr = -1;
         int cx = Math.min(this.cx, this.width - 1);
         int cy = this.cy;
         sb.append("<div><pre class='term'>");

         for (int y = 0; y < this.height; y++) {
            int wx = 0;

            for (int x = 0; x < this.width; x++) {
               long d = this.screen[y][x];
               int c = (int)(d & -1L);
               int a = (int)(d >> 32);
               if (cy == y && cx == x && this.vt100_mode_cursor) {
                  a = a & 65520 | 12;
               }

               if (a != prev_attr) {
                  if (prev_attr != -1) {
                     sb.append("</span>");
                  }

                  int bg = a & 0xFF;
                  int fg = (a & 0xFF00) >> 8;
                  boolean inv = (a & 131072) != 0;
                  boolean inv2 = this.vt100_mode_inverse;
                  if (inv && !inv2 || inv2 && !inv) {
                     int i = fg;
                     fg = bg;
                     bg = i;
                  }

                  if ((a & 262144) != 0) {
                     fg = 12;
                  }

                  String ul;
                  if ((a & 65536) != 0) {
                     ul = " ul";
                  } else {
                     ul = "";
                  }

                  String b;
                  if ((a & 524288) != 0) {
                     b = " b";
                  } else {
                     b = "";
                  }

                  sb.append("<span class='f").append(fg).append(" b").append(bg).append(ul).append(b).append("'>");
                  prev_attr = a;
               }

               switch (c) {
                  case 38:
                     sb.append("&amp;");
                     break;
                  case 60:
                     sb.append("&lt;");
                     break;
                  case 62:
                     sb.append("&gt;");
                     break;
                  default:
                     wx += this.utf8_charwidth(c);
                     if (wx <= this.width) {
                        sb.append((char)c);
                     }
               }
            }

            sb.append("\n");
         }

         sb.append("</span></pre></div>");
         return sb.toString();
      }
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();

      for (int y = 0; y < this.height; y++) {
         for (int x = 0; x < this.width; x++) {
            sb.appendCodePoint((int)(this.screen[y][x] & 4294967295L));
         }

         sb.append("\n");
      }

      return sb.toString();
   }

   static enum State {
      None,
      Esc,
      Str,
      Csi;
   }
}
