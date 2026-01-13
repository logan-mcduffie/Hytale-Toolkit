package ch.randelshofer.fastdoubleparser.chr;

import java.util.Set;

final class CharTrieOfOne implements CharTrie {
   private final char[] chars;

   public CharTrieOfOne(Set<String> set) {
      if (set.size() != 1) {
         throw new IllegalArgumentException("set size must be 1, size=" + set.size());
      } else {
         this.chars = set.iterator().next().toCharArray();
      }
   }

   public CharTrieOfOne(char[] chars) {
      this.chars = chars;
   }

   @Override
   public int match(CharSequence str) {
      return this.match(str, 0, str.length());
   }

   @Override
   public int match(CharSequence str, int startIndex, int endIndex) {
      int i = 0;
      int limit = Math.min(endIndex - startIndex, this.chars.length);

      while (i < limit && str.charAt(i + startIndex) == this.chars[i]) {
         i++;
      }

      return i == this.chars.length ? this.chars.length : 0;
   }

   @Override
   public int match(char[] str) {
      return this.match(str, 0, str.length);
   }

   @Override
   public int match(char[] str, int startIndex, int endIndex) {
      int i = 0;
      int limit = Math.min(endIndex - startIndex, this.chars.length);

      while (i < limit && str[i + startIndex] == this.chars[i]) {
         i++;
      }

      return i == this.chars.length ? this.chars.length : 0;
   }
}
