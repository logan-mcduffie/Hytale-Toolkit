package ch.randelshofer.fastdoubleparser.bte;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public interface ByteTrie {
   default int match(byte[] str) {
      return this.match(str, 0, str.length);
   }

   int match(byte[] var1, int var2, int var3);

   static ByteTrie copyOf(Set<String> set, boolean ignoreCase) {
      switch (set.size()) {
         case 0:
            return new ByteTrieOfNone();
         case 1:
            String str = set.iterator().next();
            if (ignoreCase) {
               switch (str.length()) {
                  case 0:
                     return new ByteTrieOfNone();
                  case 1:
                     LinkedHashSet<String> newSet = new LinkedHashSet<>();
                     newSet.add(str.toLowerCase());
                     newSet.add(str.toUpperCase());
                     if (newSet.size() == 1) {
                        if (newSet.iterator().next().getBytes(StandardCharsets.UTF_8).length == 1) {
                           return new ByteTrieOfOneSingleByte(newSet);
                        }

                        return new ByteTrieOfOne(newSet);
                     }

                     return new ByteTrieOfFew(newSet);
                  default:
                     return new ByteTrieOfFewIgnoreCase(set);
               }
            } else {
               if (set.iterator().next().getBytes(StandardCharsets.UTF_8).length == 1) {
                  return new ByteTrieOfOneSingleByte(set);
               }

               return new ByteTrieOfOne(set);
            }
         default:
            return (ByteTrie)(ignoreCase ? new ByteTrieOfFewIgnoreCase(set) : new ByteTrieOfFew(set));
      }
   }

   static ByteTrie copyOfChars(Set<Character> set, boolean ignoreCase) {
      Set<String> strSet = new HashSet<>(set.size() * 2);
      if (ignoreCase) {
         for (char ch : set) {
            String string = new String(new char[]{ch});
            strSet.add(string.toLowerCase());
            strSet.add(string.toUpperCase());
         }

         return copyOf(strSet, false);
      } else {
         for (char ch : set) {
            strSet.add(new String(new char[]{ch}));
         }

         return copyOf(strSet, ignoreCase);
      }
   }
}
