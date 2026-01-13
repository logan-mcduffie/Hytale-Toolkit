package org.jline.jansi;

import java.io.IOException;
import java.util.Locale;

public class AnsiRenderer {
   public static final String BEGIN_TOKEN = "@|";
   public static final String END_TOKEN = "|@";
   public static final String CODE_TEXT_SEPARATOR = " ";
   public static final String CODE_LIST_SEPARATOR = ",";
   private static final int BEGIN_TOKEN_LEN = 2;
   private static final int END_TOKEN_LEN = 2;

   public static String render(String input) throws IllegalArgumentException {
      try {
         return render(input, new StringBuilder()).toString();
      } catch (IOException var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   public static Appendable render(String input, Appendable target) throws IOException {
      int i = 0;

      while (true) {
         int j = input.indexOf("@|", i);
         if (j == -1) {
            if (i == 0) {
               target.append(input);
               return target;
            } else {
               target.append(input.substring(i));
               return target;
            }
         }

         target.append(input.substring(i, j));
         int k = input.indexOf("|@", j);
         if (k == -1) {
            target.append(input);
            return target;
         }

         j += 2;
         if (k < j) {
            throw new IllegalArgumentException("Invalid input string found.");
         }

         String spec = input.substring(j, k);
         String[] items = spec.split(" ", 2);
         if (items.length == 1) {
            target.append(input);
            return target;
         }

         String replacement = render(items[1], items[0].split(","));
         target.append(replacement);
         i = k + 2;
      }
   }

   public static String render(String text, String... codes) {
      return render(Ansi.ansi(), codes).a(text).reset().toString();
   }

   public static String renderCodes(String... codes) {
      return render(Ansi.ansi(), codes).toString();
   }

   public static String renderCodes(String codes) {
      return renderCodes(codes.split("\\s"));
   }

   private static Ansi render(Ansi ansi, String... names) {
      for (String name : names) {
         AnsiRenderer.Code code = AnsiRenderer.Code.valueOf(name.toUpperCase(Locale.ENGLISH));
         if (code.isColor()) {
            if (code.isBackground()) {
               ansi.bg(code.getColor());
            } else {
               ansi.fg(code.getColor());
            }
         } else if (code.isAttribute()) {
            ansi.a(code.getAttribute());
         }
      }

      return ansi;
   }

   public static boolean test(String text) {
      return text != null && text.contains("@|");
   }

   private AnsiRenderer() {
   }

   public static enum Code {
      BLACK(Ansi.Color.BLACK),
      RED(Ansi.Color.RED),
      GREEN(Ansi.Color.GREEN),
      YELLOW(Ansi.Color.YELLOW),
      BLUE(Ansi.Color.BLUE),
      MAGENTA(Ansi.Color.MAGENTA),
      CYAN(Ansi.Color.CYAN),
      WHITE(Ansi.Color.WHITE),
      DEFAULT(Ansi.Color.DEFAULT),
      FG_BLACK(Ansi.Color.BLACK, false),
      FG_RED(Ansi.Color.RED, false),
      FG_GREEN(Ansi.Color.GREEN, false),
      FG_YELLOW(Ansi.Color.YELLOW, false),
      FG_BLUE(Ansi.Color.BLUE, false),
      FG_MAGENTA(Ansi.Color.MAGENTA, false),
      FG_CYAN(Ansi.Color.CYAN, false),
      FG_WHITE(Ansi.Color.WHITE, false),
      FG_DEFAULT(Ansi.Color.DEFAULT, false),
      BG_BLACK(Ansi.Color.BLACK, true),
      BG_RED(Ansi.Color.RED, true),
      BG_GREEN(Ansi.Color.GREEN, true),
      BG_YELLOW(Ansi.Color.YELLOW, true),
      BG_BLUE(Ansi.Color.BLUE, true),
      BG_MAGENTA(Ansi.Color.MAGENTA, true),
      BG_CYAN(Ansi.Color.CYAN, true),
      BG_WHITE(Ansi.Color.WHITE, true),
      BG_DEFAULT(Ansi.Color.DEFAULT, true),
      RESET(Ansi.Attribute.RESET),
      INTENSITY_BOLD(Ansi.Attribute.INTENSITY_BOLD),
      INTENSITY_FAINT(Ansi.Attribute.INTENSITY_FAINT),
      ITALIC(Ansi.Attribute.ITALIC),
      UNDERLINE(Ansi.Attribute.UNDERLINE),
      BLINK_SLOW(Ansi.Attribute.BLINK_SLOW),
      BLINK_FAST(Ansi.Attribute.BLINK_FAST),
      BLINK_OFF(Ansi.Attribute.BLINK_OFF),
      NEGATIVE_ON(Ansi.Attribute.NEGATIVE_ON),
      NEGATIVE_OFF(Ansi.Attribute.NEGATIVE_OFF),
      CONCEAL_ON(Ansi.Attribute.CONCEAL_ON),
      CONCEAL_OFF(Ansi.Attribute.CONCEAL_OFF),
      UNDERLINE_DOUBLE(Ansi.Attribute.UNDERLINE_DOUBLE),
      UNDERLINE_OFF(Ansi.Attribute.UNDERLINE_OFF),
      BOLD(Ansi.Attribute.INTENSITY_BOLD),
      FAINT(Ansi.Attribute.INTENSITY_FAINT);

      private final Enum<?> n;
      private final boolean background;

      private Code(final Enum<?> n, boolean background) {
         this.n = n;
         this.background = background;
      }

      private Code(final Enum<?> n) {
         this(n, false);
      }

      public boolean isColor() {
         return this.n instanceof Ansi.Color;
      }

      public Ansi.Color getColor() {
         return (Ansi.Color)this.n;
      }

      public boolean isAttribute() {
         return this.n instanceof Ansi.Attribute;
      }

      public Ansi.Attribute getAttribute() {
         return (Ansi.Attribute)this.n;
      }

      public boolean isBackground() {
         return this.background;
      }
   }
}
