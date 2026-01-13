package ch.randelshofer.fastdoubleparser.chr;

import java.util.Set;

final class CharSetOfOne implements CharSet {
   private final char ch;

   CharSetOfOne(Set<Character> set) {
      if (set.size() != 1) {
         throw new IllegalArgumentException("set size must be 1, size=" + set.size());
      } else {
         this.ch = set.iterator().next();
      }
   }

   @Override
   public boolean containsKey(char ch) {
      return this.ch == ch;
   }
}
