package ch.randelshofer.fastdoubleparser.bte;

import java.util.List;

public interface ByteDigitSet {
   int toDigit(byte var1);

   static ByteDigitSet copyOf(List<Character> digits) {
      boolean consecutive = true;
      char zeroDigit = digits.get(0);

      for (int i = 1; i < 10; i++) {
         char current = digits.get(i);
         consecutive &= current == zeroDigit + i;
      }

      return (ByteDigitSet)(consecutive ? new ConsecutiveByteDigitSet(digits.get(0)) : new ByteToIntMap(digits));
   }
}
