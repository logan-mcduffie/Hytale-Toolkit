package org.jline.keymap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import org.jline.terminal.Terminal;
import org.jline.utils.Curses;
import org.jline.utils.InfoCmp;

public class KeyMap<T> {
   public static final int KEYMAP_LENGTH = 128;
   public static final long DEFAULT_AMBIGUOUS_TIMEOUT = 1000L;
   private Object[] mapping = new Object[128];
   private T anotherKey = (T)null;
   private T unicode;
   private T nomatch;
   private long ambiguousTimeout = 1000L;
   public static final Comparator<String> KEYSEQ_COMPARATOR = (s1, s2) -> {
      int len1 = s1.length();
      int len2 = s2.length();
      int lim = Math.min(len1, len2);

      for (int k = 0; k < lim; k++) {
         char c1 = s1.charAt(k);
         char c2 = s2.charAt(k);
         if (c1 != c2) {
            int l = len1 - len2;
            return l != 0 ? l : c1 - c2;
         }
      }

      return len1 - len2;
   };

   public static String display(String key) {
      StringBuilder sb = new StringBuilder();
      sb.append("\"");

      for (int i = 0; i < key.length(); i++) {
         char c = key.charAt(i);
         if (c < ' ') {
            sb.append('^');
            sb.append((char)(c + 'A' - 1));
         } else if (c == 127) {
            sb.append("^?");
         } else if (c == '^' || c == '\\') {
            sb.append('\\').append(c);
         } else if (c >= 128) {
            sb.append(String.format("\\u%04x", Integer.valueOf(c)));
         } else {
            sb.append(c);
         }
      }

      sb.append("\"");
      return sb.toString();
   }

   public static String translate(String str) {
      if (!str.isEmpty()) {
         char c = str.charAt(0);
         if ((c == '\'' || c == '"') && str.charAt(str.length() - 1) == c) {
            str = str.substring(1, str.length() - 1);
         }
      }

      StringBuilder keySeq = new StringBuilder();

      for (int i = 0; i < str.length(); i++) {
         char c = str.charAt(i);
         if (c == '\\') {
            if (++i >= str.length()) {
               break;
            }

            c = str.charAt(i);
            label117:
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

                  i--;
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
               case 'D':
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
               case 'C':
                  label87:
                  if (++i < str.length()) {
                     c = str.charAt(i);
                     if (c == '-') {
                        if (++i >= str.length()) {
                           break label87;
                        }

                        c = str.charAt(i);
                     }

                     c = c == '?' ? 127 : (char)(Character.toUpperCase(c) & 31);
                  }
                  break;
               case 'E':
               case 'e':
                  c = '\u001b';
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
                        break label117;
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

                  i--;
                  c = (char)(c & 255);
            }
         } else if (c == '^') {
            if (++i >= str.length()) {
               break;
            }

            c = str.charAt(i);
            if (c != '^') {
               c = c == '?' ? 127 : (char)(Character.toUpperCase(c) & 31);
            }
         }

         keySeq.append(c);
      }

      return keySeq.toString();
   }

   public static Collection<String> range(String range) {
      String[] keys = range.split("-");
      if (keys.length != 2) {
         return null;
      } else {
         keys[0] = translate(keys[0]);
         keys[1] = translate(keys[1]);
         if (keys[0].length() != keys[1].length()) {
            return null;
         } else {
            String pfx;
            if (keys[0].length() > 1) {
               pfx = keys[0].substring(0, keys[0].length() - 1);
               if (!keys[1].startsWith(pfx)) {
                  return null;
               }
            } else {
               pfx = "";
            }

            char c0 = keys[0].charAt(keys[0].length() - 1);
            char c1 = keys[1].charAt(keys[1].length() - 1);
            if (c0 > c1) {
               return null;
            } else {
               Collection<String> seqs = new ArrayList<>();

               for (char c = c0; c <= c1; c++) {
                  seqs.add(pfx + c);
               }

               return seqs;
            }
         }
      }
   }

   public static String esc() {
      return "\u001b";
   }

   public static String alt(char c) {
      return "\u001b" + c;
   }

   public static String alt(String c) {
      return "\u001b" + c;
   }

   public static String del() {
      return "\u007f";
   }

   public static String ctrl(char key) {
      return key == '?' ? del() : Character.toString((char)(Character.toUpperCase(key) & 31));
   }

   public static String key(Terminal terminal, InfoCmp.Capability capability) {
      return Curses.tputs(terminal.getStringCapability(capability));
   }

   public T getUnicode() {
      return this.unicode;
   }

   public void setUnicode(T unicode) {
      this.unicode = unicode;
   }

   public T getNomatch() {
      return this.nomatch;
   }

   public void setNomatch(T nomatch) {
      this.nomatch = nomatch;
   }

   public long getAmbiguousTimeout() {
      return this.ambiguousTimeout;
   }

   public void setAmbiguousTimeout(long ambiguousTimeout) {
      this.ambiguousTimeout = ambiguousTimeout;
   }

   public T getAnotherKey() {
      return this.anotherKey;
   }

   public Map<String, T> getBoundKeys() {
      Map<String, T> bound = new TreeMap<>(KEYSEQ_COMPARATOR);
      doGetBoundKeys(this, "", bound);
      return bound;
   }

   private static <T> void doGetBoundKeys(KeyMap<T> keyMap, String prefix, Map<String, T> bound) {
      if (keyMap.anotherKey != null) {
         bound.put(prefix, keyMap.anotherKey);
      }

      for (int c = 0; c < keyMap.mapping.length; c++) {
         if (keyMap.mapping[c] instanceof KeyMap) {
            doGetBoundKeys((KeyMap<T>)keyMap.mapping[c], prefix + (char)c, bound);
         } else if (keyMap.mapping[c] != null) {
            bound.put(prefix + (char)c, (T)keyMap.mapping[c]);
         }
      }
   }

   public T getBound(CharSequence keySeq, int[] remaining) {
      remaining[0] = -1;
      if (keySeq != null && keySeq.length() > 0) {
         char c = keySeq.charAt(0);
         if (c >= this.mapping.length) {
            remaining[0] = Character.codePointCount(keySeq, 0, keySeq.length());
            return null;
         } else if (this.mapping[c] instanceof KeyMap) {
            CharSequence sub = keySeq.subSequence(1, keySeq.length());
            return (T)((KeyMap)this.mapping[c]).getBound(sub, remaining);
         } else if (this.mapping[c] != null) {
            remaining[0] = keySeq.length() - 1;
            return (T)this.mapping[c];
         } else {
            remaining[0] = keySeq.length();
            return this.anotherKey;
         }
      } else {
         return this.anotherKey;
      }
   }

   public T getBound(CharSequence keySeq) {
      int[] remaining = new int[1];
      T res = this.getBound(keySeq, remaining);
      return remaining[0] <= 0 ? res : null;
   }

   public void bindIfNotBound(T function, CharSequence keySeq) {
      if (function != null && keySeq != null) {
         bind(this, keySeq, function, true);
      }
   }

   public void bind(T function, CharSequence... keySeqs) {
      for (CharSequence keySeq : keySeqs) {
         this.bind(function, keySeq);
      }
   }

   public void bind(T function, Iterable<? extends CharSequence> keySeqs) {
      for (CharSequence keySeq : keySeqs) {
         this.bind(function, keySeq);
      }
   }

   public void bind(T function, CharSequence keySeq) {
      if (keySeq != null) {
         if (function == null) {
            this.unbind(keySeq);
         } else {
            bind(this, keySeq, function, false);
         }
      }
   }

   public void unbind(CharSequence... keySeqs) {
      for (CharSequence keySeq : keySeqs) {
         this.unbind(keySeq);
      }
   }

   public void unbind(CharSequence keySeq) {
      if (keySeq != null) {
         unbind(this, keySeq);
      }
   }

   private static <T> T unbind(KeyMap<T> map, CharSequence keySeq) {
      KeyMap<T> prev = null;
      if (keySeq != null && keySeq.length() > 0) {
         for (int i = 0; i < keySeq.length() - 1; i++) {
            char c = keySeq.charAt(i);
            if (c > map.mapping.length) {
               return null;
            }

            if (!(map.mapping[c] instanceof KeyMap)) {
               return null;
            }

            prev = map;
            map = (KeyMap<T>)map.mapping[c];
         }

         char cx = keySeq.charAt(keySeq.length() - 1);
         if (cx > map.mapping.length) {
            return null;
         } else if (map.mapping[cx] instanceof KeyMap) {
            KeyMap<?> sub = (KeyMap<?>)map.mapping[cx];
            Object res = sub.anotherKey;
            sub.anotherKey = null;
            return (T)res;
         } else {
            Object res = map.mapping[cx];
            map.mapping[cx] = null;
            int nb = 0;

            for (int i = 0; i < map.mapping.length; i++) {
               if (map.mapping[i] != null) {
                  nb++;
               }
            }

            if (nb == 0 && prev != null) {
               prev.mapping[keySeq.charAt(keySeq.length() - 2)] = map.anotherKey;
            }

            return (T)res;
         }
      } else {
         return null;
      }
   }

   private static <T> void bind(KeyMap<T> map, CharSequence keySeq, T function, boolean onlyIfNotBound) {
      if (keySeq != null && keySeq.length() > 0) {
         for (int i = 0; i < keySeq.length(); i++) {
            char c = keySeq.charAt(i);
            if (c >= map.mapping.length) {
               return;
            }

            if (i < keySeq.length() - 1) {
               if (!(map.mapping[c] instanceof KeyMap)) {
                  KeyMap<T> m = new KeyMap<>();
                  m.anotherKey = (T)map.mapping[c];
                  map.mapping[c] = m;
               }

               map = (KeyMap<T>)map.mapping[c];
            } else if (map.mapping[c] instanceof KeyMap) {
               ((KeyMap)map.mapping[c]).anotherKey = function;
            } else {
               Object op = map.mapping[c];
               if (!onlyIfNotBound || op == null) {
                  map.mapping[c] = function;
               }
            }
         }
      }
   }
}
