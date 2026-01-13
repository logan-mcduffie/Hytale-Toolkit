package org.jline.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AttributedStringBuilder extends AttributedCharSequence implements Appendable {
   private char[] buffer;
   private long[] style;
   private int length;
   private AttributedStringBuilder.TabStops tabs = new AttributedStringBuilder.TabStops(0);
   private char[] altIn;
   private char[] altOut;
   private boolean inAltCharset;
   private int lastLineLength = 0;
   private AttributedStyle current = AttributedStyle.DEFAULT;

   public static AttributedString append(CharSequence... strings) {
      AttributedStringBuilder sb = new AttributedStringBuilder();

      for (CharSequence s : strings) {
         sb.append(s);
      }

      return sb.toAttributedString();
   }

   public AttributedStringBuilder() {
      this(64);
   }

   public AttributedStringBuilder(int capacity) {
      this.buffer = new char[capacity];
      this.style = new long[capacity];
      this.length = 0;
   }

   @Override
   public int length() {
      return this.length;
   }

   @Override
   public char charAt(int index) {
      return this.buffer[index];
   }

   @Override
   public AttributedStyle styleAt(int index) {
      return new AttributedStyle(this.style[index], this.style[index]);
   }

   @Override
   long styleCodeAt(int index) {
      return this.style[index];
   }

   @Override
   protected char[] buffer() {
      return this.buffer;
   }

   @Override
   protected int offset() {
      return 0;
   }

   @Override
   public AttributedString subSequence(int start, int end) {
      return new AttributedString(Arrays.copyOfRange(this.buffer, start, end), Arrays.copyOfRange(this.style, start, end), 0, end - start);
   }

   public AttributedStringBuilder append(CharSequence csq) {
      if (csq == null) {
         csq = "null";
      }

      return this.append(new AttributedString(csq, this.current));
   }

   public AttributedStringBuilder append(CharSequence csq, int start, int end) {
      if (csq == null) {
         csq = "null";
      }

      return this.append(csq.subSequence(start, end));
   }

   public AttributedStringBuilder append(char c) {
      return this.append(Character.toString(c));
   }

   public AttributedStringBuilder append(char c, int repeat) {
      AttributedString s = new AttributedString(Character.toString(c), this.current);

      while (repeat-- > 0) {
         this.append(s);
      }

      return this;
   }

   public AttributedStringBuilder append(CharSequence csq, AttributedStyle style) {
      return this.append(new AttributedString(csq, style));
   }

   public AttributedStringBuilder style(AttributedStyle style) {
      this.current = style;
      return this;
   }

   public AttributedStringBuilder style(Function<AttributedStyle, AttributedStyle> style) {
      this.current = style.apply(this.current);
      return this;
   }

   public AttributedStringBuilder styled(Function<AttributedStyle, AttributedStyle> style, CharSequence cs) {
      return this.styled(style, sb -> sb.append(cs));
   }

   public AttributedStringBuilder styled(AttributedStyle style, CharSequence cs) {
      return this.styled(s -> style, sb -> sb.append(cs));
   }

   public AttributedStringBuilder styled(Function<AttributedStyle, AttributedStyle> style, Consumer<AttributedStringBuilder> consumer) {
      AttributedStyle prev = this.current;
      this.current = style.apply(prev);
      consumer.accept(this);
      this.current = prev;
      return this;
   }

   public AttributedStyle style() {
      return this.current;
   }

   public AttributedStringBuilder append(AttributedString str) {
      return this.append(str, 0, str.length());
   }

   public AttributedStringBuilder append(AttributedString str, int start, int end) {
      return this.append((AttributedCharSequence)str, start, end);
   }

   public AttributedStringBuilder append(AttributedCharSequence str) {
      return this.append(str, 0, str.length());
   }

   public AttributedStringBuilder append(AttributedCharSequence str, int start, int end) {
      this.ensureCapacity(this.length + end - start);

      for (int i = start; i < end; i++) {
         char c = str.charAt(i);
         long s = str.styleCodeAt(i) & ~this.current.getMask() | this.current.getStyle();
         if (this.tabs.defined() && c == '\t') {
            this.insertTab(new AttributedStyle(s, 0L));
         } else {
            this.ensureCapacity(this.length + 1);
            this.buffer[this.length] = c;
            this.style[this.length] = s;
            if (c == '\n') {
               this.lastLineLength = 0;
            } else {
               this.lastLineLength++;
            }

            this.length++;
         }
      }

      return this;
   }

   protected void ensureCapacity(int nl) {
      if (nl > this.buffer.length) {
         int s = Math.max(this.buffer.length, 1);

         while (s <= nl) {
            s *= 2;
         }

         this.buffer = Arrays.copyOf(this.buffer, s);
         this.style = Arrays.copyOf(this.style, s);
      }
   }

   public void appendAnsi(String ansi) {
      this.ansiAppend(ansi);
   }

   public AttributedStringBuilder ansiAppend(String ansi) {
      int ansiStart = 0;
      int ansiState = 0;
      this.ensureCapacity(this.length + ansi.length());

      for (int i = 0; i < ansi.length(); i++) {
         char c = ansi.charAt(i);
         if (ansiState == 0 && c == 27) {
            ansiState++;
         } else if (ansiState == 1 && c == '[') {
            ansiState++;
            ansiStart = i + 1;
         } else if (ansiState != 2) {
            if (ansiState >= 1) {
               this.ensureCapacity(this.length + 1);
               this.buffer[this.length++] = 27;
               if (ansiState >= 2) {
                  this.ensureCapacity(this.length + 1);
                  this.buffer[this.length++] = '[';
               }

               ansiState = 0;
            }

            if (c == '\t' && this.tabs.defined()) {
               this.insertTab(this.current);
            } else {
               this.ensureCapacity(this.length + 1);
               if (this.inAltCharset) {
                  switch (c) {
                     case 'j':
                        c = 9496;
                        break;
                     case 'k':
                        c = 9488;
                        break;
                     case 'l':
                        c = 9484;
                        break;
                     case 'm':
                        c = 9492;
                        break;
                     case 'n':
                        c = 9532;
                     case 'o':
                     case 'p':
                     case 'r':
                     case 's':
                     default:
                        break;
                     case 'q':
                        c = 9472;
                        break;
                     case 't':
                        c = 9500;
                        break;
                     case 'u':
                        c = 9508;
                        break;
                     case 'v':
                        c = 9524;
                        break;
                     case 'w':
                        c = 9516;
                        break;
                     case 'x':
                        c = 9474;
                  }
               }

               this.buffer[this.length] = c;
               this.style[this.length] = this.current.getStyle();
               if (c == '\n') {
                  this.lastLineLength = 0;
               } else {
                  this.lastLineLength++;
               }

               this.length++;
               if (this.altIn != null && this.altOut != null) {
                  char[] alt = this.inAltCharset ? this.altOut : this.altIn;
                  if (equals(this.buffer, this.length - alt.length, alt, 0, alt.length)) {
                     this.inAltCharset = !this.inAltCharset;
                     this.length -= alt.length;
                  }
               }
            }
         } else if (c != 'm') {
            if ((c < '0' || c > '9') && c != ';') {
               ansiState = 0;
            }
         } else {
            String[] params = ansi.substring(ansiStart, i).split(";");

            for (int j = 0; j < params.length; j++) {
               int ansiParam = params[j].isEmpty() ? 0 : Integer.parseInt(params[j]);
               switch (ansiParam) {
                  case 0:
                     this.current = AttributedStyle.DEFAULT;
                     break;
                  case 1:
                     this.current = this.current.bold();
                     break;
                  case 2:
                     this.current = this.current.faint();
                     break;
                  case 3:
                     this.current = this.current.italic();
                     break;
                  case 4:
                     this.current = this.current.underline();
                     break;
                  case 5:
                     this.current = this.current.blink();
                  case 6:
                  case 10:
                  case 11:
                  case 12:
                  case 13:
                  case 14:
                  case 15:
                  case 16:
                  case 17:
                  case 18:
                  case 19:
                  case 20:
                  case 21:
                  case 26:
                  case 50:
                  case 51:
                  case 52:
                  case 53:
                  case 54:
                  case 55:
                  case 56:
                  case 57:
                  case 58:
                  case 59:
                  case 60:
                  case 61:
                  case 62:
                  case 63:
                  case 64:
                  case 65:
                  case 66:
                  case 67:
                  case 68:
                  case 69:
                  case 70:
                  case 71:
                  case 72:
                  case 73:
                  case 74:
                  case 75:
                  case 76:
                  case 77:
                  case 78:
                  case 79:
                  case 80:
                  case 81:
                  case 82:
                  case 83:
                  case 84:
                  case 85:
                  case 86:
                  case 87:
                  case 88:
                  case 89:
                  case 98:
                  case 99:
                  default:
                     break;
                  case 7:
                     this.current = this.current.inverse();
                     break;
                  case 8:
                     this.current = this.current.conceal();
                     break;
                  case 9:
                     this.current = this.current.crossedOut();
                     break;
                  case 22:
                     this.current = this.current.boldOff().faintOff();
                     break;
                  case 23:
                     this.current = this.current.italicOff();
                     break;
                  case 24:
                     this.current = this.current.underlineOff();
                     break;
                  case 25:
                     this.current = this.current.blinkOff();
                     break;
                  case 27:
                     this.current = this.current.inverseOff();
                     break;
                  case 28:
                     this.current = this.current.concealOff();
                     break;
                  case 29:
                     this.current = this.current.crossedOutOff();
                     break;
                  case 30:
                  case 31:
                  case 32:
                  case 33:
                  case 34:
                  case 35:
                  case 36:
                  case 37:
                     this.current = this.current.foreground(ansiParam - 30);
                     break;
                  case 38:
                  case 48:
                     if (j + 1 < params.length) {
                        int ansiParam2 = Integer.parseInt(params[++j]);
                        if (ansiParam2 == 2) {
                           if (j + 3 < params.length) {
                              int r = Integer.parseInt(params[++j]);
                              int g = Integer.parseInt(params[++j]);
                              int b = Integer.parseInt(params[++j]);
                              if (ansiParam == 38) {
                                 this.current = this.current.foreground(r, g, b);
                              } else {
                                 this.current = this.current.background(r, g, b);
                              }
                           }
                        } else if (ansiParam2 == 5 && j + 1 < params.length) {
                           int col = Integer.parseInt(params[++j]);
                           if (ansiParam == 38) {
                              this.current = this.current.foreground(col);
                           } else {
                              this.current = this.current.background(col);
                           }
                        }
                     }
                     break;
                  case 39:
                     this.current = this.current.foregroundOff();
                     break;
                  case 40:
                  case 41:
                  case 42:
                  case 43:
                  case 44:
                  case 45:
                  case 46:
                  case 47:
                     this.current = this.current.background(ansiParam - 40);
                     break;
                  case 49:
                     this.current = this.current.backgroundOff();
                     break;
                  case 90:
                  case 91:
                  case 92:
                  case 93:
                  case 94:
                  case 95:
                  case 96:
                  case 97:
                     this.current = this.current.foreground(ansiParam - 90 + 8);
                     break;
                  case 100:
                  case 101:
                  case 102:
                  case 103:
                  case 104:
                  case 105:
                  case 106:
                  case 107:
                     this.current = this.current.background(ansiParam - 100 + 8);
               }
            }

            ansiState = 0;
         }
      }

      return this;
   }

   private static boolean equals(char[] a, int aFromIndex, char[] b, int bFromIndex, int length) {
      if (aFromIndex >= 0 && bFromIndex >= 0 && aFromIndex + length <= a.length && bFromIndex + length <= b.length) {
         for (int i = 0; i < length; i++) {
            if (a[aFromIndex + i] != b[bFromIndex + i]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected void insertTab(AttributedStyle s) {
      int nb = this.tabs.spaces(this.lastLineLength);
      this.ensureCapacity(this.length + nb);

      for (int i = 0; i < nb; i++) {
         this.buffer[this.length] = ' ';
         this.style[this.length] = s.getStyle();
         this.length++;
      }

      this.lastLineLength += nb;
   }

   public void setLength(int l) {
      this.length = l;
   }

   public AttributedStringBuilder tabs(int tabsize) {
      if (tabsize < 0) {
         throw new IllegalArgumentException("Tab size must be non negative");
      } else {
         return this.tabs(Arrays.asList(tabsize));
      }
   }

   public AttributedStringBuilder tabs(List<Integer> tabs) {
      if (this.length > 0) {
         throw new IllegalStateException("Cannot change tab size after appending text");
      } else {
         this.tabs = new AttributedStringBuilder.TabStops(tabs);
         return this;
      }
   }

   public AttributedStringBuilder altCharset(String altIn, String altOut) {
      if (this.length > 0) {
         throw new IllegalStateException("Cannot change alternative charset after appending text");
      } else {
         this.altIn = altIn != null ? altIn.toCharArray() : null;
         this.altOut = altOut != null ? altOut.toCharArray() : null;
         return this;
      }
   }

   public AttributedStringBuilder styleMatches(Pattern pattern, AttributedStyle s) {
      Matcher matcher = pattern.matcher(this);

      while (matcher.find()) {
         for (int i = matcher.start(); i < matcher.end(); i++) {
            this.style[i] = this.style[i] & ~s.getMask() | s.getStyle();
         }
      }

      return this;
   }

   public AttributedStringBuilder styleMatches(Pattern pattern, List<AttributedStyle> styles) {
      Matcher matcher = pattern.matcher(this);

      while (matcher.find()) {
         for (int group = 0; group < matcher.groupCount(); group++) {
            AttributedStyle s = styles.get(group);

            for (int i = matcher.start(group + 1); i < matcher.end(group + 1); i++) {
               this.style[i] = this.style[i] & ~s.getMask() | s.getStyle();
            }
         }
      }

      return this;
   }

   private static class TabStops {
      private List<Integer> tabs = new ArrayList<>();
      private int lastStop = 0;
      private int lastSize = 0;

      public TabStops(int tabs) {
         this.lastSize = tabs;
      }

      public TabStops(List<Integer> tabs) {
         this.tabs = tabs;
         int p = 0;

         for (int s : tabs) {
            if (s > p) {
               this.lastStop = s;
               this.lastSize = s - p;
               p = s;
            }
         }
      }

      boolean defined() {
         return this.lastSize > 0;
      }

      int spaces(int lastLineLength) {
         int out = 0;
         if (lastLineLength >= this.lastStop) {
            out = this.lastSize - (lastLineLength - this.lastStop) % this.lastSize;
         } else {
            for (int s : this.tabs) {
               if (s > lastLineLength) {
                  out = s - lastLineLength;
                  break;
               }
            }
         }

         return out;
      }
   }
}
