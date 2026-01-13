package ch.randelshofer.fastdoubleparser.chr;

import java.util.Set;

final class CharSetOfFew implements CharSet {
   private final char[] chars;

   public CharSetOfFew(Set<Character> set) {
      this.chars = new char[set.size()];
      int i = 0;

      for (Character ch : set) {
         this.chars[i++] = ch;
      }
   }

   @Override
   public boolean containsKey(char ch) {
      boolean found = false;

      for (char aChar : this.chars) {
         found |= aChar == ch;
      }

      return found;
   }
}
