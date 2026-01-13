package org.jline.reader.impl;

import java.util.ListIterator;
import org.jline.reader.Expander;
import org.jline.reader.History;

public class DefaultExpander implements Expander {
   @Override
   public String expandHistory(History history, String line) {
      boolean inQuote = false;
      StringBuilder sb = new StringBuilder();
      boolean escaped = false;
      int unicode = 0;

      for (int i = 0; i < line.length(); i++) {
         char c = line.charAt(i);
         if (unicode > 0) {
            unicode--;
            escaped = unicode >= 0;
            sb.append(c);
         } else if (escaped) {
            if (c == 'u') {
               unicode = 4;
            } else {
               escaped = false;
            }

            sb.append(c);
         } else if (c == '\'') {
            inQuote = !inQuote;
            sb.append(c);
         } else if (inQuote) {
            sb.append(c);
         } else {
            switch (c) {
               case '!':
                  if (i + 1 < line.length()) {
                     c = line.charAt(++i);
                     boolean neg = false;
                     String rep = null;
                     switch (c) {
                        case '\t':
                        case ' ':
                           sb.append('!');
                           sb.append(c);
                           break;
                        case '\n':
                        case '\u000b':
                        case '\f':
                        case '\r':
                        case '\u000e':
                        case '\u000f':
                        case '\u0010':
                        case '\u0011':
                        case '\u0012':
                        case '\u0013':
                        case '\u0014':
                        case '\u0015':
                        case '\u0016':
                        case '\u0017':
                        case '\u0018':
                        case '\u0019':
                        case '\u001a':
                        case '\u001b':
                        case '\u001c':
                        case '\u001d':
                        case '\u001e':
                        case '\u001f':
                        case '"':
                        case '%':
                        case '&':
                        case '\'':
                        case '(':
                        case ')':
                        case '*':
                        case '+':
                        case ',':
                        case '.':
                        case '/':
                        case ':':
                        case ';':
                        case '<':
                        case '=':
                        case '>':
                        default:
                           String ss = line.substring(i);
                           i = line.length();
                           int idxx = this.searchBackwards(history, ss, history.index(), true);
                           if (idxx < 0) {
                              throw new IllegalArgumentException("!" + ss + ": event not found");
                           }

                           rep = history.get(idxx);
                           break;
                        case '!':
                           if (history.size() == 0) {
                              throw new IllegalArgumentException("!!: event not found");
                           }

                           rep = history.get(history.index() - 1);
                           break;
                        case '#':
                           sb.append(sb.toString());
                           break;
                        case '$':
                           if (history.size() == 0) {
                              throw new IllegalArgumentException("!$: event not found");
                           }

                           String previous = history.get(history.index() - 1).trim();
                           int lastSpace = previous.lastIndexOf(32);
                           if (lastSpace != -1) {
                              rep = previous.substring(lastSpace + 1);
                           } else {
                              rep = previous;
                           }
                           break;
                        case '-':
                           neg = true;
                           i++;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                           int i1;
                           for (i1 = i; i < line.length(); i++) {
                              c = line.charAt(i);
                              if (c < '0' || c > '9') {
                                 break;
                              }
                           }

                           int idx;
                           try {
                              idx = Integer.parseInt(line.substring(i1, i));
                           } catch (NumberFormatException var17) {
                              throw new IllegalArgumentException((neg ? "!-" : "!") + line.substring(i1, i) + ": event not found");
                           }

                           if (neg && idx > 0 && idx <= history.size()) {
                              rep = history.get(history.index() - idx);
                           } else {
                              if (neg || idx <= history.index() - history.size() || idx > history.index()) {
                                 throw new IllegalArgumentException((neg ? "!-" : "!") + line.substring(i1, i) + ": event not found");
                              }

                              rep = history.get(idx - 1);
                           }
                           break;
                        case '?':
                           int i1x = line.indexOf(63, i + 1);
                           if (i1x < 0) {
                              i1x = line.length();
                           }

                           String sc = line.substring(i + 1, i1x);
                           i = i1x;
                           int idxx = this.searchBackwards(history, sc, history.index(), false);
                           if (idxx < 0) {
                              throw new IllegalArgumentException("!?" + sc + ": event not found");
                           }

                           rep = history.get(idxx);
                     }

                     if (rep != null) {
                        sb.append(rep);
                     }
                  } else {
                     sb.append(c);
                  }
                  break;
               case '\\':
                  escaped = true;
                  sb.append(c);
                  break;
               case '^':
                  if (i == 0) {
                     int i1xx = line.indexOf(94, i + 1);
                     int i2 = line.indexOf(94, i1xx + 1);
                     if (i2 < 0) {
                        i2 = line.length();
                     }

                     if (i1xx > 0 && i2 > 0) {
                        String s1 = line.substring(i + 1, i1xx);
                        String s2 = line.substring(i1xx + 1, i2);
                        String s = history.get(history.index() - 1).replace(s1, s2);
                        sb.append(s);
                        i = i2 + 1;
                        break;
                     }
                  }

                  sb.append(c);
                  break;
               default:
                  sb.append(c);
            }
         }
      }

      return sb.toString();
   }

   @Override
   public String expandVar(String word) {
      return word;
   }

   protected int searchBackwards(History history, String searchTerm, int startIndex, boolean startsWith) {
      ListIterator<History.Entry> it = history.iterator(startIndex);

      while (it.hasPrevious()) {
         History.Entry e = it.previous();
         if (startsWith) {
            if (e.line().startsWith(searchTerm)) {
               return e.index();
            }
         } else if (e.line().contains(searchTerm)) {
            return e.index();
         }
      }

      return -1;
   }
}
