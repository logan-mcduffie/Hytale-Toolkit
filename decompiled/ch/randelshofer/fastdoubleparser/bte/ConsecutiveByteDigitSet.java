package ch.randelshofer.fastdoubleparser.bte;

final class ConsecutiveByteDigitSet implements ByteDigitSet {
   private final byte zeroDigit;

   public ConsecutiveByteDigitSet(char zeroDigit) {
      if (zeroDigit > 127) {
         throw new IllegalArgumentException("can not map to a single byte. zeroDigit=" + zeroDigit + "' 0x" + Integer.toHexString(zeroDigit));
      } else {
         this.zeroDigit = (byte)zeroDigit;
      }
   }

   @Override
   public int toDigit(byte ch) {
      return (char)(ch - this.zeroDigit);
   }
}
