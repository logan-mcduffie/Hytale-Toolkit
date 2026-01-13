package org.jline.utils;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jline.terminal.Terminal;

public class AttributedString extends AttributedCharSequence {
   final char[] buffer;
   final long[] style;
   final int start;
   final int end;
   public static final AttributedString EMPTY = new AttributedString("");
   public static final AttributedString NEWLINE = new AttributedString("\n");

   public AttributedString(CharSequence str) {
      this(str, 0, str.length(), null);
   }

   public AttributedString(CharSequence str, int start, int end) {
      this(str, start, end, null);
   }

   public AttributedString(CharSequence str, AttributedStyle s) {
      this(str, 0, str.length(), s);
   }

   public AttributedString(CharSequence str, int start, int end, AttributedStyle s) {
      if (end < start) {
         throw new InvalidParameterException();
      } else {
         if (str instanceof AttributedString) {
            AttributedString as = (AttributedString)str;
            this.buffer = as.buffer;
            if (s != null) {
               this.style = (long[])as.style.clone();

               for (int i = 0; i < this.style.length; i++) {
                  this.style[i] = this.style[i] & ~s.getMask() | s.getStyle();
               }
            } else {
               this.style = as.style;
            }

            this.start = as.start + start;
            this.end = as.start + end;
         } else if (str instanceof AttributedStringBuilder) {
            AttributedStringBuilder asb = (AttributedStringBuilder)str;
            AttributedString as = asb.subSequence(start, end);
            this.buffer = as.buffer;
            this.style = as.style;
            if (s != null) {
               for (int i = 0; i < this.style.length; i++) {
                  this.style[i] = this.style[i] & ~s.getMask() | s.getStyle();
               }
            }

            this.start = as.start;
            this.end = as.end;
         } else {
            int l = end - start;
            this.buffer = new char[l];

            for (int i = 0; i < l; i++) {
               this.buffer[i] = str.charAt(start + i);
            }

            this.style = new long[l];
            if (s != null) {
               Arrays.fill(this.style, s.getStyle());
            }

            this.start = 0;
            this.end = l;
         }
      }
   }

   AttributedString(char[] buffer, long[] style, int start, int end) {
      this.buffer = buffer;
      this.style = style;
      this.start = start;
      this.end = end;
   }

   public static AttributedString fromAnsi(String ansi) {
      return fromAnsi(ansi, 0);
   }

   public static AttributedString fromAnsi(String ansi, int tabs) {
      return fromAnsi(ansi, Arrays.asList(tabs));
   }

   public static AttributedString fromAnsi(String ansi, List<Integer> tabs) {
      return fromAnsi(ansi, tabs, null, null);
   }

   public static AttributedString fromAnsi(String ansi, Terminal terminal) {
      String alternateIn;
      String alternateOut;
      if (!DISABLE_ALTERNATE_CHARSET) {
         alternateIn = Curses.tputs(terminal.getStringCapability(InfoCmp.Capability.enter_alt_charset_mode));
         alternateOut = Curses.tputs(terminal.getStringCapability(InfoCmp.Capability.exit_alt_charset_mode));
      } else {
         alternateIn = null;
         alternateOut = null;
      }

      return fromAnsi(ansi, Arrays.asList(0), alternateIn, alternateOut);
   }

   public static AttributedString fromAnsi(String ansi, List<Integer> tabs, String altIn, String altOut) {
      return ansi == null ? null : new AttributedStringBuilder(ansi.length()).tabs(tabs).altCharset(altIn, altOut).ansiAppend(ansi).toAttributedString();
   }

   public static String stripAnsi(String ansi) {
      return ansi == null ? null : new AttributedStringBuilder(ansi.length()).ansiAppend(ansi).toString();
   }

   @Override
   protected char[] buffer() {
      return this.buffer;
   }

   @Override
   protected int offset() {
      return this.start;
   }

   @Override
   public int length() {
      return this.end - this.start;
   }

   @Override
   public AttributedStyle styleAt(int index) {
      return new AttributedStyle(this.style[this.start + index], this.style[this.start + index]);
   }

   @Override
   long styleCodeAt(int index) {
      return this.style[this.start + index];
   }

   @Override
   public AttributedString subSequence(int start, int end) {
      return new AttributedString(this, start, end);
   }

   public AttributedString styleMatches(Pattern pattern, AttributedStyle style) {
      Matcher matcher = pattern.matcher(this);
      boolean result = matcher.find();
      if (!result) {
         return this;
      } else {
         long[] newstyle = (long[])this.style.clone();

         do {
            for (int i = matcher.start(); i < matcher.end(); i++) {
               newstyle[this.start + i] = newstyle[this.start + i] & ~style.getMask() | style.getStyle();
            }

            result = matcher.find();
         } while (result);

         return new AttributedString(this.buffer, newstyle, this.start, this.end);
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         AttributedString that = (AttributedString)o;
         return this.end - this.start == that.end - that.start
            && this.arrEq(this.buffer, that.buffer, this.start, that.start, this.end - this.start)
            && this.arrEq(this.style, that.style, this.start, that.start, this.end - this.start);
      } else {
         return false;
      }
   }

   private boolean arrEq(char[] a1, char[] a2, int s1, int s2, int l) {
      for (int i = 0; i < l; i++) {
         if (a1[s1 + i] != a2[s2 + i]) {
            return false;
         }
      }

      return true;
   }

   private boolean arrEq(long[] a1, long[] a2, int s1, int s2, int l) {
      for (int i = 0; i < l; i++) {
         if (a1[s1 + i] != a2[s2 + i]) {
            return false;
         }
      }

      return true;
   }

   @Override
   public int hashCode() {
      int result = Arrays.hashCode(this.buffer);
      result = 31 * result + Arrays.hashCode(this.style);
      result = 31 * result + this.start;
      return 31 * result + this.end;
   }

   public static AttributedString join(AttributedString delimiter, AttributedString... elements) {
      Objects.requireNonNull(delimiter);
      Objects.requireNonNull(elements);
      return join(delimiter, Arrays.asList(elements));
   }

   public static AttributedString join(AttributedString delimiter, Iterable<AttributedString> elements) {
      Objects.requireNonNull(elements);
      AttributedStringBuilder sb = new AttributedStringBuilder();
      int i = 0;

      for (AttributedString str : elements) {
         if (i++ > 0 && delimiter != null) {
            sb.append(delimiter);
         }

         sb.append(str);
      }

      return sb.toAttributedString();
   }
}
