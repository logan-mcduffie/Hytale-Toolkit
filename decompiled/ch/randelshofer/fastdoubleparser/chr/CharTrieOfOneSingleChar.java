package ch.randelshofer.fastdoubleparser.chr;

import java.util.Set;

final class CharTrieOfOneSingleChar implements CharTrie {
   private final char ch;

   public CharTrieOfOneSingleChar(Set<String> set) {
      if (set.size() != 1) {
         throw new IllegalArgumentException("set size must be 1, size=" + set.size());
      } else {
         char[] chars = set.iterator().next().toCharArray();
         if (chars.length != 1) {
            throw new IllegalArgumentException("char size must be 1, size=" + set.size());
         } else {
            this.ch = chars[0];
         }
      }
   }

   public CharTrieOfOneSingleChar(char ch) {
      this.ch = ch;
   }

   @Override
   public int match(CharSequence str, int startIndex, int endIndex) {
      return startIndex < endIndex && str.charAt(startIndex) == this.ch ? 1 : 0;
   }

   @Override
   public int match(char[] str) {
      return this.match(str, 0, str.length);
   }

   @Override
   public int match(char[] str, int startIndex, int endIndex) {
      return startIndex < endIndex && str[startIndex] == this.ch ? 1 : 0;
   }
}
