package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.chr.CharSet;
import ch.randelshofer.fastdoubleparser.chr.FormatCharSet;
import java.util.Collection;
import java.util.Set;

class NumberFormatSymbolsInfo {
   static boolean isAscii(NumberFormatSymbols symbols) {
      return isAsciiCharCollection(symbols.decimalSeparator())
         && isAsciiCharCollection(symbols.groupingSeparator())
         && isAsciiStringCollection(symbols.exponentSeparator())
         && isAsciiCharCollection(symbols.minusSign())
         && isAsciiCharCollection(symbols.plusSign())
         && isAsciiStringCollection(symbols.infinity())
         && isAsciiStringCollection(symbols.nan())
         && isAsciiCharCollection(symbols.digits());
   }

   static boolean isMostlyAscii(NumberFormatSymbols symbols) {
      return isAsciiCharCollection(symbols.decimalSeparator())
         && isAsciiCharCollection(symbols.groupingSeparator())
         && isAsciiCharCollection(symbols.minusSign())
         && isAsciiCharCollection(symbols.plusSign())
         && isAsciiCharCollection(symbols.digits());
   }

   static boolean isDigitsTokensAscii(NumberFormatSymbols symbols) {
      return isAsciiCharCollection(symbols.digits());
   }

   static boolean isAsciiStringCollection(Collection<String> collection) {
      for (String str : collection) {
         for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch > 127) {
               return false;
            }
         }
      }

      return true;
   }

   static boolean isAsciiCharCollection(Collection<Character> collection) {
      for (char ch : collection) {
         if (ch > 127) {
            return false;
         }
      }

      return true;
   }

   static boolean containsFormatChars(NumberFormatSymbols symbols) {
      FormatCharSet formatCharSet = new FormatCharSet();
      return containsChars(symbols.decimalSeparator(), (CharSet)formatCharSet)
         || containsChars(symbols.groupingSeparator(), (CharSet)formatCharSet)
         || containsChars(symbols.exponentSeparator(), formatCharSet)
         || containsChars(symbols.minusSign(), (CharSet)formatCharSet)
         || containsChars(symbols.plusSign(), (CharSet)formatCharSet)
         || containsChars(symbols.infinity(), formatCharSet)
         || containsChars(symbols.nan(), formatCharSet)
         || containsChars(symbols.digits(), formatCharSet);
   }

   private static boolean containsChars(Set<String> strings, FormatCharSet set) {
      for (String str : strings) {
         int i = 0;

         for (int n = str.length(); i < n; i++) {
            if (set.containsKey(str.charAt(i))) {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean containsChars(Collection<Character> characters, CharSet set) {
      for (char ch : characters) {
         if (set.containsKey(ch)) {
            return true;
         }
      }

      return false;
   }
}
