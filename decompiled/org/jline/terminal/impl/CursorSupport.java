package org.jline.terminal.impl;

import java.io.IOError;
import java.io.IOException;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jline.terminal.Cursor;
import org.jline.terminal.Terminal;
import org.jline.utils.Curses;
import org.jline.utils.InfoCmp;

public class CursorSupport {
   public static Cursor getCursorPosition(Terminal terminal, IntConsumer discarded) {
      try {
         String u6 = terminal.getStringCapability(InfoCmp.Capability.user6);
         String u7 = terminal.getStringCapability(InfoCmp.Capability.user7);
         if (u6 != null && u7 != null) {
            boolean inc1 = false;
            StringBuilder patb = new StringBuilder();
            int index = 0;

            while (index < u6.length()) {
               char ch;
               switch (ch = u6.charAt(index++)) {
                  case '%':
                     ch = u6.charAt(index++);
                     switch (ch) {
                        case '%':
                           patb.append('%');
                           continue;
                        case 'd':
                           patb.append("([0-9]+)");
                           continue;
                        case 'i':
                           inc1 = true;
                           continue;
                        default:
                           throw new IllegalArgumentException();
                     }
                  case '\\':
                     switch (u6.charAt(index++)) {
                        case 'E':
                        case 'e':
                           patb.append("\\x1b");
                           continue;
                        default:
                           throw new IllegalArgumentException();
                     }
                  default:
                     switch (ch) {
                        case '[':
                           patb.append('\\');
                        default:
                           patb.append(ch);
                     }
               }
            }

            Pattern pattern = Pattern.compile(patb.toString());
            Curses.tputs(terminal.writer(), u7);
            terminal.flush();
            StringBuilder sb = new StringBuilder();
            int start = 0;

            while (true) {
               int c = terminal.reader().read();
               if (c < 0) {
                  return null;
               }

               sb.append((char)c);
               Matcher matcher = pattern.matcher(sb.substring(start));
               if (matcher.matches()) {
                  int y = Integer.parseInt(matcher.group(1));
                  int x = Integer.parseInt(matcher.group(2));
                  if (inc1) {
                     x--;
                     y--;
                  }

                  if (discarded != null) {
                     for (int i = 0; i < start; i++) {
                        discarded.accept(sb.charAt(i));
                     }
                  }

                  return new Cursor(x, y);
               }

               if (!matcher.hitEnd()) {
                  start++;
               }
            }
         } else {
            return null;
         }
      } catch (IOException var15) {
         throw new IOError(var15);
      }
   }
}
