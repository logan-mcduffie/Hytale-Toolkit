package org.jline.utils;

import java.util.ArrayList;
import java.util.List;
import org.jline.terminal.Terminal;

public abstract class AttributedCharSequence implements CharSequence {
   public static final int TRUE_COLORS = 16777216;
   private static final int HIGH_COLORS = 32767;
   static final boolean DISABLE_ALTERNATE_CHARSET = Boolean.getBoolean("org.jline.utils.disableAlternateCharset");

   public void print(Terminal terminal) {
      terminal.writer().print(this.toAnsi(terminal));
   }

   public void println(Terminal terminal) {
      terminal.writer().println(this.toAnsi(terminal));
   }

   public String toAnsi() {
      return this.toAnsi(null);
   }

   public String toAnsi(Terminal terminal) {
      if (terminal != null && "dumb".equals(terminal.getType())) {
         return this.toString();
      } else {
         int colors = 256;
         AttributedCharSequence.ForceMode forceMode = AttributedCharSequence.ForceMode.None;
         ColorPalette palette = null;
         String alternateIn = null;
         String alternateOut = null;
         if (terminal != null) {
            Integer max_colors = terminal.getNumericCapability(InfoCmp.Capability.max_colors);
            if (max_colors != null) {
               colors = max_colors;
            }

            if ("windows-256color".equals(terminal.getType()) || "windows-conemu".equals(terminal.getType())) {
               forceMode = AttributedCharSequence.ForceMode.Force256Colors;
            }

            palette = terminal.getPalette();
            if (!DISABLE_ALTERNATE_CHARSET) {
               alternateIn = Curses.tputs(terminal.getStringCapability(InfoCmp.Capability.enter_alt_charset_mode));
               alternateOut = Curses.tputs(terminal.getStringCapability(InfoCmp.Capability.exit_alt_charset_mode));
            }
         }

         return this.toAnsi(colors, forceMode, palette, alternateIn, alternateOut);
      }
   }

   @Deprecated
   public String toAnsi(int colors, boolean force256colors) {
      return this.toAnsi(colors, force256colors, null, null);
   }

   @Deprecated
   public String toAnsi(int colors, boolean force256colors, String altIn, String altOut) {
      return this.toAnsi(colors, force256colors ? AttributedCharSequence.ForceMode.Force256Colors : AttributedCharSequence.ForceMode.None, null, altIn, altOut);
   }

   public String toAnsi(int colors, AttributedCharSequence.ForceMode force) {
      return this.toAnsi(colors, force, null, null, null);
   }

   public String toAnsi(int colors, AttributedCharSequence.ForceMode force, ColorPalette palette) {
      return this.toAnsi(colors, force, palette, null, null);
   }

   public String toAnsi(int colors, AttributedCharSequence.ForceMode force, ColorPalette palette, String altIn, String altOut) {
      StringBuilder sb = new StringBuilder();
      long style = 0L;
      long foreground = 0L;
      long background = 0L;
      boolean alt = false;
      if (palette == null) {
         palette = ColorPalette.DEFAULT;
      }

      for (int i = 0; i < this.length(); i++) {
         char c = this.charAt(i);
         if (altIn != null && altOut != null) {
            char pc = c;
            switch (c) {
               case '─':
                  c = 'q';
                  break;
               case '│':
                  c = 'x';
                  break;
               case '┌':
                  c = 'l';
                  break;
               case '┐':
                  c = 'k';
                  break;
               case '└':
                  c = 'm';
                  break;
               case '┘':
                  c = 'j';
                  break;
               case '├':
                  c = 't';
                  break;
               case '┤':
                  c = 'u';
                  break;
               case '┬':
                  c = 'w';
                  break;
               case '┴':
                  c = 'v';
                  break;
               case '┼':
                  c = 'n';
            }

            boolean oldalt = alt;
            alt = c != pc;
            if (oldalt ^ alt) {
               sb.append(alt ? altIn : altOut);
            }
         }

         long s = this.styleCodeAt(i) & -4097L;
         if (style != s) {
            long d = (style ^ s) & 8191L;
            long fg = (s & 768L) != 0L ? s & 549755781888L : 0L;
            long bg = (s & 3072L) != 0L ? s & 9223371487098964992L : 0L;
            if (s == 0L) {
               sb.append("\u001b[0m");
               background = 0L;
               foreground = 0L;
            } else {
               sb.append("\u001b[");
               boolean first = true;
               if ((d & 4L) != 0L) {
                  first = attr(sb, (s & 4L) != 0L ? "3" : "23", first);
               }

               if ((d & 8L) != 0L) {
                  first = attr(sb, (s & 8L) != 0L ? "4" : "24", first);
               }

               if ((d & 16L) != 0L) {
                  first = attr(sb, (s & 16L) != 0L ? "5" : "25", first);
               }

               if ((d & 32L) != 0L) {
                  first = attr(sb, (s & 32L) != 0L ? "7" : "27", first);
               }

               if ((d & 64L) != 0L) {
                  first = attr(sb, (s & 64L) != 0L ? "8" : "28", first);
               }

               if ((d & 128L) != 0L) {
                  first = attr(sb, (s & 128L) != 0L ? "9" : "29", first);
               }

               if (foreground != fg) {
                  if (fg > 0L) {
                     int rounded = -1;
                     if ((fg & 512L) != 0L) {
                        int r = (int)(fg >> 31) & 0xFF;
                        int g = (int)(fg >> 23) & 0xFF;
                        int b = (int)(fg >> 15) & 0xFF;
                        if (colors >= 32767) {
                           first = attr(sb, "38;2;" + r + ";" + g + ";" + b, first);
                        } else {
                           rounded = palette.round(r, g, b);
                        }
                     } else if ((fg & 256L) != 0L) {
                        rounded = palette.round((int)(fg >> 15) & 0xFF);
                     }

                     if (rounded >= 0) {
                        if (colors >= 32767 && force == AttributedCharSequence.ForceMode.ForceTrueColors) {
                           int col = palette.getColor(rounded);
                           int r = col >> 16 & 0xFF;
                           int g = col >> 8 & 0xFF;
                           int b = col & 0xFF;
                           first = attr(sb, "38;2;" + r + ";" + g + ";" + b, first);
                        } else if (force == AttributedCharSequence.ForceMode.Force256Colors || rounded >= 16) {
                           first = attr(sb, "38;5;" + rounded, first);
                        } else if (rounded >= 8) {
                           first = attr(sb, "9" + (rounded - 8), first);
                           d |= s & 1L;
                        } else {
                           first = attr(sb, "3" + rounded, first);
                           d |= s & 1L;
                        }
                     }
                  } else {
                     first = attr(sb, "39", first);
                  }

                  foreground = fg;
               }

               if (background != bg) {
                  if (bg > 0L) {
                     int roundedx = -1;
                     if ((bg & 2048L) != 0L) {
                        int r = (int)(bg >> 55) & 0xFF;
                        int g = (int)(bg >> 47) & 0xFF;
                        int b = (int)(bg >> 39) & 0xFF;
                        if (colors >= 32767) {
                           first = attr(sb, "48;2;" + r + ";" + g + ";" + b, first);
                        } else {
                           roundedx = palette.round(r, g, b);
                        }
                     } else if ((bg & 1024L) != 0L) {
                        roundedx = palette.round((int)(bg >> 39) & 0xFF);
                     }

                     if (roundedx >= 0) {
                        if (colors >= 32767 && force == AttributedCharSequence.ForceMode.ForceTrueColors) {
                           int col = palette.getColor(roundedx);
                           int r = col >> 16 & 0xFF;
                           int g = col >> 8 & 0xFF;
                           int b = col & 0xFF;
                           first = attr(sb, "48;2;" + r + ";" + g + ";" + b, first);
                        } else if (force == AttributedCharSequence.ForceMode.Force256Colors || roundedx >= 16) {
                           first = attr(sb, "48;5;" + roundedx, first);
                        } else if (roundedx >= 8) {
                           first = attr(sb, "10" + (roundedx - 8), first);
                        } else {
                           first = attr(sb, "4" + roundedx, first);
                        }
                     }
                  } else {
                     first = attr(sb, "49", first);
                  }

                  background = bg;
               }

               if ((d & 3L) != 0L) {
                  if ((d & 1L) != 0L && (s & 1L) == 0L || (d & 2L) != 0L && (s & 2L) == 0L) {
                     first = attr(sb, "22", first);
                  }

                  if ((d & 1L) != 0L && (s & 1L) != 0L) {
                     first = attr(sb, "1", first);
                  }

                  if ((d & 2L) != 0L && (s & 2L) != 0L) {
                     first = attr(sb, "2", first);
                  }
               }

               sb.append("m");
            }

            style = s;
         }

         sb.append(c);
      }

      if (alt) {
         sb.append(altOut);
      }

      if (style != 0L) {
         sb.append("\u001b[0m");
      }

      return sb.toString();
   }

   @Deprecated
   public static int rgbColor(int col) {
      return Colors.rgbColor(col);
   }

   @Deprecated
   public static int roundColor(int col, int max) {
      return Colors.roundColor(col, max);
   }

   @Deprecated
   public static int roundRgbColor(int r, int g, int b, int max) {
      return Colors.roundRgbColor(r, g, b, max);
   }

   private static boolean attr(StringBuilder sb, String s, boolean first) {
      if (!first) {
         sb.append(";");
      }

      sb.append(s);
      return false;
   }

   public abstract AttributedStyle styleAt(int var1);

   long styleCodeAt(int index) {
      return this.styleAt(index).getStyle();
   }

   public boolean isHidden(int index) {
      return (this.styleCodeAt(index) & 4096L) != 0L;
   }

   public int runStart(int index) {
      AttributedStyle style = this.styleAt(index);

      while (index > 0 && this.styleAt(index - 1).equals(style)) {
         index--;
      }

      return index;
   }

   public int runLimit(int index) {
      AttributedStyle style = this.styleAt(index);

      while (index < this.length() - 1 && this.styleAt(index + 1).equals(style)) {
         index++;
      }

      return index + 1;
   }

   public abstract AttributedString subSequence(int var1, int var2);

   public AttributedString substring(int start, int end) {
      return this.subSequence(start, end);
   }

   protected abstract char[] buffer();

   protected abstract int offset();

   @Override
   public char charAt(int index) {
      return this.buffer()[this.offset() + index];
   }

   public int codePointAt(int index) {
      return Character.codePointAt(this.buffer(), index + this.offset());
   }

   public boolean contains(char c) {
      for (int i = 0; i < this.length(); i++) {
         if (this.charAt(i) == c) {
            return true;
         }
      }

      return false;
   }

   public int codePointBefore(int index) {
      return Character.codePointBefore(this.buffer(), index + this.offset());
   }

   public int codePointCount(int index, int length) {
      return Character.codePointCount(this.buffer(), index + this.offset(), length);
   }

   public int columnLength() {
      int cols = 0;
      int len = this.length();
      int cur = 0;

      while (cur < len) {
         int cp = this.codePointAt(cur);
         if (!this.isHidden(cur)) {
            cols += WCWidth.wcwidth(cp);
         }

         cur += Character.charCount(cp);
      }

      return cols;
   }

   public AttributedString columnSubSequence(int start, int stop) {
      int begin = 0;
      int col = 0;

      while (begin < this.length()) {
         int cp = this.codePointAt(begin);
         int w = this.isHidden(begin) ? 0 : WCWidth.wcwidth(cp);
         if (col + w > start) {
            break;
         }

         begin += Character.charCount(cp);
         col += w;
      }

      int end = begin;

      while (end < this.length()) {
         int cp = this.codePointAt(end);
         if (cp == 10) {
            break;
         }

         int w = this.isHidden(end) ? 0 : WCWidth.wcwidth(cp);
         if (col + w > stop) {
            break;
         }

         end += Character.charCount(cp);
         col += w;
      }

      return this.subSequence(begin, end);
   }

   public List<AttributedString> columnSplitLength(int columns) {
      return this.columnSplitLength(columns, false, true);
   }

   public List<AttributedString> columnSplitLength(int columns, boolean includeNewlines, boolean delayLineWrap) {
      List<AttributedString> strings = new ArrayList<>();
      int cur = 0;
      int beg = cur;
      int col = 0;

      while (cur < this.length()) {
         int cp = this.codePointAt(cur);
         int w = this.isHidden(cur) ? 0 : WCWidth.wcwidth(cp);
         if (cp == 10) {
            strings.add(this.subSequence(beg, includeNewlines ? cur + 1 : cur));
            beg = cur + 1;
            col = 0;
         } else if ((col += w) > columns) {
            strings.add(this.subSequence(beg, cur));
            beg = cur;
            col = w;
         }

         cur += Character.charCount(cp);
      }

      strings.add(this.subSequence(beg, cur));
      return strings;
   }

   @Override
   public String toString() {
      return new String(this.buffer(), this.offset(), this.length());
   }

   public AttributedString toAttributedString() {
      return this.substring(0, this.length());
   }

   public static enum ForceMode {
      None,
      Force256Colors,
      ForceTrueColors;
   }
}
