package org.jline.utils;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StyleResolver {
   private static final Logger log = Logger.getLogger(StyleResolver.class.getName());
   private final Function<String, String> source;

   public StyleResolver(Function<String, String> source) {
      this.source = Objects.requireNonNull(source);
   }

   private static Integer colorRgb(String name) {
      name = name.toLowerCase(Locale.US);
      if (name.charAt(0) != 'x' && name.charAt(0) != '#') {
         Integer color = color(name);
         if (color != null && color != -1) {
            color = Colors.DEFAULT_COLORS_256[color];
         }

         return color;
      } else {
         try {
            return Integer.parseInt(name.substring(1), 16);
         } catch (NumberFormatException var2) {
            log.warning("Invalid hexadecimal color: " + name);
            return null;
         }
      }
   }

   private static Integer color(String name) {
      int flags = 0;
      if (name.equals("default")) {
         return -1;
      } else {
         if (name.charAt(0) == '!') {
            name = name.substring(1);
            flags = 8;
         } else if (name.startsWith("bright-")) {
            name = name.substring(7);
            flags = 8;
         } else if (name.charAt(0) == '~') {
            name = name.substring(1);

            try {
               return Colors.rgbColor(name);
            } catch (IllegalArgumentException var4) {
               log.warning("Invalid style-color name: " + name);
               return null;
            }
         }

         switch (name) {
            case "black":
            case "k":
               return flags + 0;
            case "red":
            case "r":
               return flags + 1;
            case "green":
            case "g":
               return flags + 2;
            case "yellow":
            case "y":
               return flags + 3;
            case "blue":
            case "b":
               return flags + 4;
            case "magenta":
            case "m":
               return flags + 5;
            case "cyan":
            case "c":
               return flags + 6;
            case "white":
            case "w":
               return flags + 7;
            default:
               return null;
         }
      }
   }

   public AttributedStyle resolve(String spec) {
      Objects.requireNonNull(spec);
      if (log.isLoggable(Level.FINEST)) {
         log.finest("Resolve: " + spec);
      }

      int i = spec.indexOf(":-");
      if (i != -1) {
         String[] parts = spec.split(":-");
         return this.resolve(parts[0].trim(), parts[1].trim());
      } else {
         return this.apply(AttributedStyle.DEFAULT, spec);
      }
   }

   public AttributedStyle resolve(String spec, String defaultSpec) {
      Objects.requireNonNull(spec);
      if (log.isLoggable(Level.FINEST)) {
         log.finest(String.format("Resolve: %s; default: %s", spec, defaultSpec));
      }

      AttributedStyle style = this.apply(AttributedStyle.DEFAULT, spec);
      if (style == AttributedStyle.DEFAULT && defaultSpec != null) {
         style = this.apply(style, defaultSpec);
      }

      return style;
   }

   private AttributedStyle apply(AttributedStyle style, String spec) {
      if (log.isLoggable(Level.FINEST)) {
         log.finest("Apply: " + spec);
      }

      for (String item : spec.split(",")) {
         item = item.trim();
         if (!item.isEmpty()) {
            if (item.startsWith(".")) {
               style = this.applyReference(style, item);
            } else if (item.contains(":")) {
               style = this.applyColor(style, item);
            } else if (item.matches("[0-9]+(;[0-9]+)*")) {
               style = this.applyAnsi(style, item);
            } else {
               style = this.applyNamed(style, item);
            }
         }
      }

      return style;
   }

   private AttributedStyle applyAnsi(AttributedStyle style, String spec) {
      if (log.isLoggable(Level.FINEST)) {
         log.finest("Apply-ansi: " + spec);
      }

      return new AttributedStringBuilder().style(style).ansiAppend("\u001b[" + spec + "m").style();
   }

   private AttributedStyle applyReference(AttributedStyle style, String spec) {
      if (log.isLoggable(Level.FINEST)) {
         log.finest("Apply-reference: " + spec);
      }

      if (spec.length() == 1) {
         log.warning("Invalid style-reference; missing discriminator: " + spec);
      } else {
         String name = spec.substring(1);
         String resolvedSpec = this.source.apply(name);
         if (resolvedSpec != null) {
            return this.apply(style, resolvedSpec);
         }
      }

      return style;
   }

   private AttributedStyle applyNamed(AttributedStyle style, String name) {
      if (log.isLoggable(Level.FINEST)) {
         log.finest("Apply-named: " + name);
      }

      String var3 = name.toLowerCase(Locale.US);
      switch (var3) {
         case "default":
            return AttributedStyle.DEFAULT;
         case "bold":
            return style.bold();
         case "faint":
            return style.faint();
         case "italic":
            return style.italic();
         case "underline":
            return style.underline();
         case "blink":
            return style.blink();
         case "inverse":
            return style.inverse();
         case "inverse-neg":
         case "inverseneg":
            return style.inverseNeg();
         case "conceal":
            return style.conceal();
         case "crossed-out":
         case "crossedout":
            return style.crossedOut();
         case "hidden":
            return style.hidden();
         default:
            log.warning("Unknown style: " + name);
            return style;
      }
   }

   private AttributedStyle applyColor(AttributedStyle style, String spec) {
      if (log.isLoggable(Level.FINEST)) {
         log.finest("Apply-color: " + spec);
      }

      String[] parts = spec.split(":", 2);
      String colorMode = parts[0].trim();
      String colorName = parts[1].trim();
      String var7 = colorMode.toLowerCase(Locale.US);
      switch (var7) {
         case "foreground":
         case "fg":
         case "f":
            Integer color = color(colorName);
            if (color != null) {
               return color >= 0 ? style.foreground(color) : style.foregroundDefault();
            }

            log.warning("Invalid color-name: " + colorName);
            break;
         case "background":
         case "bg":
         case "b":
            Integer color = color(colorName);
            if (color != null) {
               return color >= 0 ? style.background(color) : style.backgroundDefault();
            }

            log.warning("Invalid color-name: " + colorName);
            break;
         case "foreground-rgb":
         case "fg-rgb":
         case "f-rgb":
            Integer color = colorRgb(colorName);
            if (color != null) {
               return color >= 0 ? style.foregroundRgb(color) : style.foregroundDefault();
            }

            log.warning("Invalid color-name: " + colorName);
            break;
         case "background-rgb":
         case "bg-rgb":
         case "b-rgb":
            Integer color = colorRgb(colorName);
            if (color != null) {
               return color >= 0 ? style.backgroundRgb(color) : style.backgroundDefault();
            }

            log.warning("Invalid color-name: " + colorName);
            break;
         default:
            log.warning("Invalid color-mode: " + colorMode);
      }

      return style;
   }
}
