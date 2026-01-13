package ch.randelshofer.fastdoubleparser.chr;

import java.util.Set;

public interface CharTrie {
   default int match(CharSequence str) {
      return this.match(str, 0, str.length());
   }

   default int match(char[] str) {
      return this.match(str, 0, str.length);
   }

   int match(CharSequence var1, int var2, int var3);

   int match(char[] var1, int var2, int var3);

   static CharTrie copyOf(Set<String> set, boolean ignoreCase) {
      switch (set.size()) {
         case 0:
            return new CharTrieOfNone();
         case 1:
            if (set.iterator().next().length() == 1) {
               return (CharTrie)(ignoreCase ? new CharTrieOfFewIgnoreCase(set) : new CharTrieOfOneSingleChar(set));
            }

            return (CharTrie)(ignoreCase ? new CharTrieOfFewIgnoreCase(set) : new CharTrieOfOne(set));
         default:
            return (CharTrie)(ignoreCase ? new CharTrieOfFewIgnoreCase(set) : new CharTrieOfFew(set));
      }
   }
}
