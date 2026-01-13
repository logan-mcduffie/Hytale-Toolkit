package ch.randelshofer.fastdoubleparser.bte;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;

final class ByteSetOfFew implements ByteSet {
   private final byte[] bytes;

   public ByteSetOfFew(Set<Character> set) {
      byte[] tmp = new byte[set.size() * 4];
      int i = 0;

      for (char ch : set) {
         for (byte b : String.valueOf(ch).getBytes(StandardCharsets.UTF_8)) {
            tmp[i++] = b;
         }
      }

      this.bytes = Arrays.copyOf(tmp, i);
   }

   @Override
   public boolean containsKey(byte b) {
      boolean found = false;

      for (byte aChar : this.bytes) {
         found |= aChar == b;
      }

      return found;
   }
}
