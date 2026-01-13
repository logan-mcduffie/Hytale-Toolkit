package org.jline.utils;

public class AttributedStyle {
   public static final int BLACK = 0;
   public static final int RED = 1;
   public static final int GREEN = 2;
   public static final int YELLOW = 3;
   public static final int BLUE = 4;
   public static final int MAGENTA = 5;
   public static final int CYAN = 6;
   public static final int WHITE = 7;
   public static final int BRIGHT = 8;
   static final long F_BOLD = 1L;
   static final long F_FAINT = 2L;
   static final long F_ITALIC = 4L;
   static final long F_UNDERLINE = 8L;
   static final long F_BLINK = 16L;
   static final long F_INVERSE = 32L;
   static final long F_CONCEAL = 64L;
   static final long F_CROSSED_OUT = 128L;
   static final long F_FOREGROUND_IND = 256L;
   static final long F_FOREGROUND_RGB = 512L;
   static final long F_FOREGROUND = 768L;
   static final long F_BACKGROUND_IND = 1024L;
   static final long F_BACKGROUND_RGB = 2048L;
   static final long F_BACKGROUND = 3072L;
   static final long F_HIDDEN = 4096L;
   static final long MASK = 8191L;
   static final int FG_COLOR_EXP = 15;
   static final int BG_COLOR_EXP = 39;
   static final long FG_COLOR = 549755781120L;
   static final long BG_COLOR = 9223371487098961920L;
   public static final AttributedStyle DEFAULT = new AttributedStyle();
   public static final AttributedStyle BOLD = DEFAULT.bold();
   public static final AttributedStyle BOLD_OFF = DEFAULT.boldOff();
   public static final AttributedStyle INVERSE = DEFAULT.inverse();
   public static final AttributedStyle INVERSE_OFF = DEFAULT.inverseOff();
   public static final AttributedStyle HIDDEN = DEFAULT.hidden();
   public static final AttributedStyle HIDDEN_OFF = DEFAULT.hiddenOff();
   final long style;
   final long mask;

   public AttributedStyle() {
      this(0L, 0L);
   }

   public AttributedStyle(AttributedStyle s) {
      this(s.style, s.mask);
   }

   public AttributedStyle(long style, long mask) {
      this.style = style;
      this.mask = mask & 8191L | ((style & 768L) != 0L ? 549755781120L : 0L) | ((style & 3072L) != 0L ? 9223371487098961920L : 0L);
   }

   public AttributedStyle bold() {
      return new AttributedStyle(this.style | 1L, this.mask | 1L);
   }

   public AttributedStyle boldOff() {
      return new AttributedStyle(this.style & -2L, this.mask | 1L);
   }

   public AttributedStyle boldDefault() {
      return new AttributedStyle(this.style & -2L, this.mask & -2L);
   }

   public AttributedStyle faint() {
      return new AttributedStyle(this.style | 2L, this.mask | 2L);
   }

   public AttributedStyle faintOff() {
      return new AttributedStyle(this.style & -3L, this.mask | 2L);
   }

   public AttributedStyle faintDefault() {
      return new AttributedStyle(this.style & -3L, this.mask & -3L);
   }

   public AttributedStyle italic() {
      return new AttributedStyle(this.style | 4L, this.mask | 4L);
   }

   public AttributedStyle italicOff() {
      return new AttributedStyle(this.style & -5L, this.mask | 4L);
   }

   public AttributedStyle italicDefault() {
      return new AttributedStyle(this.style & -5L, this.mask & -5L);
   }

   public AttributedStyle underline() {
      return new AttributedStyle(this.style | 8L, this.mask | 8L);
   }

   public AttributedStyle underlineOff() {
      return new AttributedStyle(this.style & -9L, this.mask | 8L);
   }

   public AttributedStyle underlineDefault() {
      return new AttributedStyle(this.style & -9L, this.mask & -9L);
   }

   public AttributedStyle blink() {
      return new AttributedStyle(this.style | 16L, this.mask | 16L);
   }

   public AttributedStyle blinkOff() {
      return new AttributedStyle(this.style & -17L, this.mask | 16L);
   }

   public AttributedStyle blinkDefault() {
      return new AttributedStyle(this.style & -17L, this.mask & -17L);
   }

   public AttributedStyle inverse() {
      return new AttributedStyle(this.style | 32L, this.mask | 32L);
   }

   public AttributedStyle inverseNeg() {
      long s = (this.style & 32L) != 0L ? this.style & -33L : this.style | 32L;
      return new AttributedStyle(s, this.mask | 32L);
   }

   public AttributedStyle inverseOff() {
      return new AttributedStyle(this.style & -33L, this.mask | 32L);
   }

   public AttributedStyle inverseDefault() {
      return new AttributedStyle(this.style & -33L, this.mask & -33L);
   }

   public AttributedStyle conceal() {
      return new AttributedStyle(this.style | 64L, this.mask | 64L);
   }

   public AttributedStyle concealOff() {
      return new AttributedStyle(this.style & -65L, this.mask | 64L);
   }

   public AttributedStyle concealDefault() {
      return new AttributedStyle(this.style & -65L, this.mask & -65L);
   }

   public AttributedStyle crossedOut() {
      return new AttributedStyle(this.style | 128L, this.mask | 128L);
   }

   public AttributedStyle crossedOutOff() {
      return new AttributedStyle(this.style & -129L, this.mask | 128L);
   }

   public AttributedStyle crossedOutDefault() {
      return new AttributedStyle(this.style & -129L, this.mask & -129L);
   }

   public AttributedStyle foreground(int color) {
      return new AttributedStyle(this.style & -549755781121L | 256L | (long)color << 15 & 549755781120L, this.mask | 256L);
   }

   public AttributedStyle foreground(int r, int g, int b) {
      return this.foregroundRgb(r << 16 | g << 8 | b);
   }

   public AttributedStyle foregroundRgb(int color) {
      return new AttributedStyle(this.style & -549755781121L | 512L | (color & 16777215L) << 15 & 549755781120L, this.mask | 512L);
   }

   public AttributedStyle foregroundOff() {
      return new AttributedStyle(this.style & -549755781121L & -769L, this.mask | 768L);
   }

   public AttributedStyle foregroundDefault() {
      return new AttributedStyle(this.style & -549755781121L & -769L, this.mask & -549755781889L);
   }

   public AttributedStyle background(int color) {
      return new AttributedStyle(this.style & -9223371487098961921L | 1024L | (long)color << 39 & 9223371487098961920L, this.mask | 1024L);
   }

   public AttributedStyle background(int r, int g, int b) {
      return this.backgroundRgb(r << 16 | g << 8 | b);
   }

   public AttributedStyle backgroundRgb(int color) {
      return new AttributedStyle(this.style & -9223371487098961921L | 2048L | (color & 16777215L) << 39 & 9223371487098961920L, this.mask | 2048L);
   }

   public AttributedStyle backgroundOff() {
      return new AttributedStyle(this.style & -9223371487098961921L & -3073L, this.mask | 3072L);
   }

   public AttributedStyle backgroundDefault() {
      return new AttributedStyle(this.style & -9223371487098961921L & -3073L, this.mask & -9223371487098964993L);
   }

   public AttributedStyle hidden() {
      return new AttributedStyle(this.style | 4096L, this.mask | 4096L);
   }

   public AttributedStyle hiddenOff() {
      return new AttributedStyle(this.style & -4097L, this.mask | 4096L);
   }

   public AttributedStyle hiddenDefault() {
      return new AttributedStyle(this.style & -4097L, this.mask & -4097L);
   }

   public long getStyle() {
      return this.style;
   }

   public long getMask() {
      return this.mask;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         AttributedStyle that = (AttributedStyle)o;
         return this.style != that.style ? false : this.mask == that.mask;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return 31 * Long.hashCode(this.style) + Long.hashCode(this.mask);
   }

   public String toAnsi() {
      AttributedStringBuilder sb = new AttributedStringBuilder();
      sb.styled(this, " ");
      String s = sb.toAnsi(16777216, AttributedCharSequence.ForceMode.None);
      return s.length() > 1 ? s.substring(2, s.indexOf(109)) : s;
   }

   @Override
   public String toString() {
      return "AttributedStyle{style=" + this.style + ", mask=" + this.mask + ", ansi=" + this.toAnsi() + '}';
   }
}
