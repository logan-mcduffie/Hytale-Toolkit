package ch.randelshofer.fastdoubleparser.bte;

import java.nio.charset.StandardCharsets;
import java.util.Set;

final class ByteTrieOfOne implements ByteTrie {
   private final byte[] chars;

   public ByteTrieOfOne(Set<String> set) {
      if (set.size() != 1) {
         throw new IllegalArgumentException("set size must be 1, size=" + set.size());
      } else {
         this.chars = set.iterator().next().getBytes(StandardCharsets.UTF_8);
      }
   }

   @Override
   public int match(byte[] str) {
      return this.match(str, 0, str.length);
   }

   @Override
   public int match(byte[] str, int startIndex, int endIndex) {
      int i = 0;
      int limit = Math.min(endIndex - startIndex, this.chars.length);

      while (i < limit && str[i + startIndex] == this.chars[i]) {
         i++;
      }

      return i == this.chars.length ? this.chars.length : 0;
   }
}
