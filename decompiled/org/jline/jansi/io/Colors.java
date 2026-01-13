package org.jline.jansi.io;

public class Colors {
   public static final int[] DEFAULT_COLORS_256 = org.jline.utils.Colors.DEFAULT_COLORS_256;

   public static int roundColor(int col, int max) {
      return org.jline.utils.Colors.roundColor(col, max);
   }

   public static int roundRgbColor(int r, int g, int b, int max) {
      return org.jline.utils.Colors.roundRgbColor(r, g, b, max);
   }
}
