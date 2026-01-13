package org.jline.reader.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.jline.reader.LineReader;
import org.jline.reader.Macro;
import org.jline.reader.Reference;
import org.jline.terminal.Terminal;
import org.jline.utils.Log;

public final class InputRC {
   private final LineReader reader;

   public static void configure(LineReader reader, URL url) throws IOException {
      InputStream is = url.openStream();

      try {
         configure(reader, is);
      } catch (Throwable var6) {
         if (is != null) {
            try {
               is.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (is != null) {
         is.close();
      }
   }

   public static void configure(LineReader reader, InputStream is) throws IOException {
      InputStreamReader r = new InputStreamReader(is);

      try {
         configure(reader, r);
      } catch (Throwable var6) {
         try {
            r.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      r.close();
   }

   public static void configure(LineReader reader, Reader r) throws IOException {
      BufferedReader br;
      if (r instanceof BufferedReader) {
         br = (BufferedReader)r;
      } else {
         br = new BufferedReader(r);
      }

      Terminal terminal = reader.getTerminal();
      if (!"dumb".equals(terminal.getType()) && !"dumb-color".equals(terminal.getType())) {
         reader.getVariables().putIfAbsent("editing-mode", "emacs");
      } else {
         reader.getVariables().putIfAbsent("editing-mode", "dumb");
      }

      reader.setKeyMap("main");
      new InputRC(reader).parse(br);
      if ("vi".equals(reader.getVariable("editing-mode"))) {
         reader.getKeyMaps().put("main", reader.getKeyMaps().get("viins"));
      } else if ("emacs".equals(reader.getVariable("editing-mode"))) {
         reader.getKeyMaps().put("main", reader.getKeyMaps().get("emacs"));
      } else if ("dumb".equals(reader.getVariable("editing-mode"))) {
         reader.getKeyMaps().put("main", reader.getKeyMaps().get("dumb"));
      }
   }

   private InputRC(LineReader reader) {
      this.reader = reader;
   }

   private void parse(BufferedReader br) throws IOException, IllegalArgumentException {
      boolean parsing = true;
      List<Boolean> ifsStack = new ArrayList<>();

      String line;
      while ((line = br.readLine()) != null) {
         try {
            line = line.trim();
            if (line.length() != 0 && line.charAt(0) != '#') {
               int i = 0;
               if (line.charAt(i) == '$') {
                  i++;

                  while (i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t')) {
                     i++;
                  }

                  int s = i;

                  while (i < line.length() && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
                     i++;
                  }

                  String cmd = line.substring(s, i);

                  while (i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t')) {
                     i++;
                  }

                  s = i;

                  while (i < line.length() && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
                     i++;
                  }

                  String args = line.substring(s, i);
                  if ("if".equalsIgnoreCase(cmd)) {
                     ifsStack.add(parsing);
                     if (parsing && !args.startsWith("term=")) {
                        if (args.startsWith("mode=")) {
                           String mode = (String)this.reader.getVariable("editing-mode");
                           parsing = args.substring("mode=".length()).equalsIgnoreCase(mode);
                        } else {
                           parsing = args.equalsIgnoreCase(this.reader.getAppName());
                        }
                     }
                  } else if (!"else".equalsIgnoreCase(cmd)) {
                     if ("endif".equalsIgnoreCase(cmd)) {
                        if (ifsStack.isEmpty()) {
                           throw new IllegalArgumentException("endif found without matching $if");
                        }

                        parsing = ifsStack.remove(ifsStack.size() - 1);
                     } else if ("include".equalsIgnoreCase(cmd)) {
                     }
                  } else {
                     if (ifsStack.isEmpty()) {
                        throw new IllegalArgumentException("$else found without matching $if");
                     }

                     boolean invert = true;
                     Iterator var33 = ifsStack.iterator();

                     while (true) {
                        if (var33.hasNext()) {
                           boolean b = (Boolean)var33.next();
                           if (b) {
                              continue;
                           }

                           invert = false;
                        }

                        if (invert) {
                           parsing = !parsing;
                        }
                        break;
                     }
                  }
               } else if (parsing) {
                  if (line.charAt(i++) == '"') {
                     boolean esc = false;

                     while (true) {
                        if (i >= line.length()) {
                           throw new IllegalArgumentException("Missing closing quote on line '" + line + "'");
                        }

                        if (esc) {
                           esc = false;
                        } else if (line.charAt(i) == '\\') {
                           esc = true;
                        } else if (line.charAt(i) == '"') {
                           break;
                        }

                        i++;
                     }
                  }

                  while (i < line.length() && line.charAt(i) != ':' && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
                     i++;
                  }

                  String keySeq = line.substring(0, i);
                  boolean equivalency = i + 1 < line.length() && line.charAt(i) == ':' && line.charAt(i + 1) == '=';
                  i++;
                  if (equivalency) {
                     i++;
                  }

                  if (keySeq.equalsIgnoreCase("set")) {
                     while (i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t')) {
                        i++;
                     }

                     int s = i;

                     while (i < line.length() && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
                        i++;
                     }

                     String key = line.substring(s, i);

                     while (i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t')) {
                        i++;
                     }

                     s = i;

                     while (i < line.length() && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
                        i++;
                     }

                     String val = line.substring(s, i);
                     setVar(this.reader, key, val);
                  } else {
                     while (i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t')) {
                        i++;
                     }

                     int start = i;
                     if (i < line.length() && (line.charAt(i) == '\'' || line.charAt(i) == '"')) {
                        char delim = line.charAt(i++);

                        for (boolean esc = false; i < line.length(); i++) {
                           if (esc) {
                              esc = false;
                           } else if (line.charAt(i) == '\\') {
                              esc = true;
                           } else if (line.charAt(i) == delim) {
                              break;
                           }
                        }
                     }

                     while (i < line.length() && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
                        i++;
                     }

                     String val = line.substring(Math.min(start, line.length()), Math.min(i, line.length()));
                     if (keySeq.charAt(0) == '"') {
                        keySeq = translateQuoted(keySeq);
                     } else {
                        String keyName = keySeq.lastIndexOf(45) > 0 ? keySeq.substring(keySeq.lastIndexOf(45) + 1) : keySeq;
                        char key = getKeyFromName(keyName);
                        keyName = keySeq.toLowerCase();
                        keySeq = "";
                        if (keyName.contains("meta-") || keyName.contains("m-")) {
                           keySeq = keySeq + "\u001b";
                        }

                        if (keyName.contains("control-") || keyName.contains("c-") || keyName.contains("ctrl-")) {
                           key = (char)(Character.toUpperCase(key) & 31);
                        }

                        keySeq = keySeq + key;
                     }

                     if (val.length() <= 0 || val.charAt(0) != '\'' && val.charAt(0) != '"') {
                        this.reader.getKeys().bind(new Reference(val), keySeq);
                     } else {
                        this.reader.getKeys().bind(new Macro(translateQuoted(val)), keySeq);
                     }
                  }
               }
            }
         } catch (IllegalArgumentException var12) {
            Log.warn("Unable to parse user configuration: ", var12);
         }
      }
   }

   private static String translateQuoted(String keySeq) {
      String str = keySeq.substring(1, keySeq.length() - 1);
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < str.length(); i++) {
         char c = str.charAt(i);
         if (c == '\\') {
            boolean ctrl = str.regionMatches(i, "\\C-", 0, 3) || str.regionMatches(i, "\\M-\\C-", 0, 6);
            boolean meta = str.regionMatches(i, "\\M-", 0, 3) || str.regionMatches(i, "\\C-\\M-", 0, 6);
            i += (meta ? 3 : 0) + (ctrl ? 3 : 0) + (!meta && !ctrl ? 1 : 0);
            if (i >= str.length()) {
               break;
            }

            c = str.charAt(i);
            if (meta) {
               sb.append("\u001b");
            }

            if (ctrl) {
               c = c == '?' ? 127 : (char)(Character.toUpperCase(c) & 31);
            }

            if (!meta && !ctrl) {
               label105:
               switch (c) {
                  case '0':
                  case '1':
                  case '2':
                  case '3':
                  case '4':
                  case '5':
                  case '6':
                  case '7':
                     c = '\u0000';

                     for (int j = 0; j < 3 && i < str.length(); i++) {
                        int k = Character.digit(str.charAt(i), 8);
                        if (k < 0) {
                           break;
                        }

                        c = (char)(c * '\b' + k);
                        j++;
                     }

                     c = (char)(c & 255);
                  case '8':
                  case '9':
                  case ':':
                  case ';':
                  case '<':
                  case '=':
                  case '>':
                  case '?':
                  case '@':
                  case 'A':
                  case 'B':
                  case 'C':
                  case 'D':
                  case 'E':
                  case 'F':
                  case 'G':
                  case 'H':
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
                  case ']':
                  case '^':
                  case '_':
                  case '`':
                  case 'c':
                  case 'g':
                  case 'h':
                  case 'i':
                  case 'j':
                  case 'k':
                  case 'l':
                  case 'm':
                  case 'o':
                  case 'p':
                  case 'q':
                  case 's':
                  case 'w':
                  default:
                     break;
                  case '\\':
                     c = '\\';
                     break;
                  case 'a':
                     c = '\u0007';
                     break;
                  case 'b':
                     c = '\b';
                     break;
                  case 'd':
                     c = '\u007f';
                     break;
                  case 'e':
                     c = '\u001b';
                     break;
                  case 'f':
                     c = '\f';
                     break;
                  case 'n':
                     c = '\n';
                     break;
                  case 'r':
                     c = '\r';
                     break;
                  case 't':
                     c = '\t';
                     break;
                  case 'u':
                     i++;
                     c = '\u0000';

                     for (int j = 0; j < 4 && i < str.length(); i++) {
                        int k = Character.digit(str.charAt(i), 16);
                        if (k < 0) {
                           break label105;
                        }

                        c = (char)(c * 16 + k);
                        j++;
                     }
                     break;
                  case 'v':
                     c = '\u000b';
                     break;
                  case 'x':
                     i++;
                     c = '\u0000';

                     for (int j = 0; j < 2 && i < str.length(); i++) {
                        int k = Character.digit(str.charAt(i), 16);
                        if (k < 0) {
                           break;
                        }

                        c = (char)(c * 16 + k);
                        j++;
                     }

                     c = (char)(c & 255);
               }
            }

            sb.append(c);
         } else {
            sb.append(c);
         }
      }

      return sb.toString();
   }

   private static char getKeyFromName(String name) {
      if ("DEL".equalsIgnoreCase(name) || "Rubout".equalsIgnoreCase(name)) {
         return '\u007f';
      } else if ("ESC".equalsIgnoreCase(name) || "Escape".equalsIgnoreCase(name)) {
         return '\u001b';
      } else if ("LFD".equalsIgnoreCase(name) || "NewLine".equalsIgnoreCase(name)) {
         return '\n';
      } else if ("RET".equalsIgnoreCase(name) || "Return".equalsIgnoreCase(name)) {
         return '\r';
      } else if ("SPC".equalsIgnoreCase(name) || "Space".equalsIgnoreCase(name)) {
         return ' ';
      } else {
         return "Tab".equalsIgnoreCase(name) ? '\t' : name.charAt(0);
      }
   }

   static void setVar(LineReader reader, String key, String val) {
      if ("keymap".equalsIgnoreCase(key)) {
         reader.setKeyMap(val);
      } else {
         for (LineReader.Option option : LineReader.Option.values()) {
            if (option.name().toLowerCase(Locale.ENGLISH).replace('_', '-').equals(val)) {
               if ("on".equalsIgnoreCase(val)) {
                  reader.setOpt(option);
               } else if ("off".equalsIgnoreCase(val)) {
                  reader.unsetOpt(option);
               }

               return;
            }
         }

         reader.setVariable(key, val);
      }
   }
}
