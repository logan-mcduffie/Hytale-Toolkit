package ch.randelshofer.fastdoubleparser.chr;

import java.util.List;

public interface CharDigitSet {
   int toDigit(char var1);

   static CharDigitSet copyOf(List<Character> digits) {
      boolean consecutive = true;
      char zeroDigit = digits.get(0);

      for (int i = 1; i < 10; i++) {
         char current = digits.get(i);
         consecutive &= current == zeroDigit + i;
      }

      return (CharDigitSet)(consecutive ? new ConsecutiveCharDigitSet(digits.get(0)) : new CharToIntMap(digits));
   }
}
