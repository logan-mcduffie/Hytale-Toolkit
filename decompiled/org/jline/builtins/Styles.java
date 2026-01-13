package org.jline.builtins;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jline.utils.StyleResolver;

public class Styles {
   public static final String NANORC_THEME = "NANORC_THEME";
   protected static final List<String> ANSI_STYLES = Arrays.asList(
      "blink", "bold", "conceal", "crossed-out", "crossedout", "faint", "hidden", "inverse", "inverse-neg", "inverseneg", "italic", "underline"
   );
   private static final String DEFAULT_LS_COLORS = "di=1;91:ex=1;92:ln=1;96:fi=";
   private static final String DEFAULT_HELP_COLORS = "ti=1;34:co=1:ar=3:op=33:de=";
   private static final String DEFAULT_PRNT_COLORS = "th=1;34:rn=1;34:rs=,~grey15:mk=1;34:em=31:vs=32";
   private static final String LS_COLORS = "LS_COLORS";
   private static final String HELP_COLORS = "HELP_COLORS";
   private static final String PRNT_COLORS = "PRNT_COLORS";
   private static final String KEY = "([a-z]{2}|\\*\\.[a-zA-Z0-9]+)";
   private static final String VALUE = "(([!~#]?[a-zA-Z0-9]+[a-z0-9-;]*)?|[A-Z_]+)";
   private static final String VALUES = "(([!~#]?[a-zA-Z0-9]+[a-z0-9-;]*)?|[A-Z_]+)(,(([!~#]?[a-zA-Z0-9]+[a-z0-9-;]*)?|[A-Z_]+))*";
   private static final Pattern STYLE_ELEMENT_SEPARATOR = Pattern.compile(":");
   private static final Pattern STYLE_ELEMENT_PATTERN = Pattern.compile(
      "([a-z]{2}|\\*\\.[a-zA-Z0-9]+)=(([!~#]?[a-zA-Z0-9]+[a-z0-9-;]*)?|[A-Z_]+)(,(([!~#]?[a-zA-Z0-9]+[a-z0-9-;]*)?|[A-Z_]+))*"
   );

   public static StyleResolver lsStyle() {
      return style("LS_COLORS", "di=1;91:ex=1;92:ln=1;96:fi=");
   }

   public static StyleResolver helpStyle() {
      return style("HELP_COLORS", "ti=1;34:co=1:ar=3:op=33:de=");
   }

   public static StyleResolver prntStyle() {
      return style("PRNT_COLORS", "th=1;34:rn=1;34:rs=,~grey15:mk=1;34:em=31:vs=32");
   }

   public static boolean isStylePattern(String style) {
      String[] styleElements = STYLE_ELEMENT_SEPARATOR.split(style);
      return Arrays.stream(styleElements).allMatch(element -> element.isEmpty() || STYLE_ELEMENT_PATTERN.matcher(element).matches());
   }

   public static StyleResolver style(String name, String defStyle) {
      String style = consoleOption(name);
      if (style == null) {
         style = defStyle;
      }

      return style(style);
   }

   private static ConsoleOptionGetter optionGetter() {
      try {
         return (ConsoleOptionGetter)Class.forName("org.jline.console.SystemRegistry").getDeclaredMethod("get").invoke(null);
      } catch (Exception var1) {
         return null;
      }
   }

   private static <T> T consoleOption(String name, T defVal) {
      T out = defVal;
      ConsoleOptionGetter cog = optionGetter();
      if (cog != null) {
         out = cog.consoleOption(name, defVal);
      }

      return out;
   }

   private static String consoleOption(String name) {
      String out = null;
      ConsoleOptionGetter cog = optionGetter();
      if (cog != null) {
         out = (String)cog.consoleOption(name);
         if (out != null && !isStylePattern(out)) {
            out = null;
         }
      }

      if (out == null) {
         out = System.getenv(name);
         if (out != null && !isStylePattern(out)) {
            out = null;
         }
      }

      return out;
   }

   public static StyleResolver style(String style) {
      Map<String, String> colors = Arrays.stream(style.split(":"))
         .collect(Collectors.toMap(s -> s.substring(0, s.indexOf(61)), s -> s.substring(s.indexOf(61) + 1)));
      return new StyleResolver(new Styles.StyleCompiler(colors)::getStyle);
   }

   public static class StyleCompiler {
      private static final String ANSI_VALUE = "[0-9]*(;[0-9]+){0,2}";
      private static final String COLORS_24BIT = "[#x][0-9a-fA-F]{6}";
      private static final List<String> COLORS_8 = Arrays.asList("white", "black", "red", "blue", "green", "yellow", "magenta", "cyan");
      private static final Map<String, Integer> COLORS_NANO = new HashMap<>();
      private final Map<String, String> colors;
      private final Map<String, String> tokenColors;
      private final boolean nanoStyle;

      public StyleCompiler(Map<String, String> colors) {
         this(colors, false);
      }

      public StyleCompiler(Map<String, String> colors, boolean nanoStyle) {
         this.colors = colors;
         this.nanoStyle = nanoStyle;
         this.tokenColors = Styles.consoleOption("NANORC_THEME", new HashMap<>());
      }

      public String getStyle(String reference) {
         String rawStyle = this.colors.get(reference);
         if (rawStyle == null) {
            return null;
         } else {
            if (rawStyle.matches("[A-Z_]+")) {
               rawStyle = this.tokenColors.getOrDefault(rawStyle, "normal");
            } else if (!this.nanoStyle && rawStyle.matches("[0-9]*(;[0-9]+){0,2}")) {
               return rawStyle;
            }

            StringBuilder out = new StringBuilder();
            boolean first = true;
            boolean fg = true;

            for (String s : rawStyle.split(",")) {
               if (s.trim().isEmpty()) {
                  fg = false;
               } else {
                  if (!first) {
                     out.append(",");
                  }

                  if (Styles.ANSI_STYLES.contains(s)) {
                     out.append(s);
                  } else if (COLORS_8.contains(s)
                     || COLORS_NANO.containsKey(s)
                     || s.startsWith("light")
                     || s.startsWith("bright")
                     || s.startsWith("~")
                     || s.startsWith("!")
                     || s.matches("\\d+")
                     || s.matches("[#x][0-9a-fA-F]{6}")
                     || s.equals("normal")
                     || s.equals("default")) {
                     if (s.matches("[#x][0-9a-fA-F]{6}")) {
                        if (fg) {
                           out.append("fg-rgb:");
                        } else {
                           out.append("bg-rgb:");
                        }

                        out.append(s);
                     } else if (!s.matches("\\d+") && !COLORS_NANO.containsKey(s)) {
                        if (fg) {
                           out.append("fg:");
                        } else {
                           out.append("bg:");
                        }

                        if (COLORS_8.contains(s) || s.startsWith("~") || s.startsWith("!") || s.startsWith("bright-")) {
                           out.append(s);
                        } else if (s.startsWith("light")) {
                           out.append("!").append(s.substring(5));
                        } else if (s.startsWith("bright")) {
                           out.append("!").append(s.substring(6));
                        } else {
                           out.append("default");
                        }
                     } else {
                        if (fg) {
                           out.append("38;5;");
                        } else {
                           out.append("48;5;");
                        }

                        out.append(s.matches("\\d+") ? s : COLORS_NANO.get(s).toString());
                     }

                     fg = false;
                  }

                  first = false;
               }
            }

            return out.toString();
         }
      }

      static {
         COLORS_NANO.put("pink", 204);
         COLORS_NANO.put("purple", 163);
         COLORS_NANO.put("mauve", 134);
         COLORS_NANO.put("lagoon", 38);
         COLORS_NANO.put("mint", 48);
         COLORS_NANO.put("lime", 148);
         COLORS_NANO.put("peach", 215);
         COLORS_NANO.put("orange", 208);
         COLORS_NANO.put("latte", 137);
      }
   }
}
