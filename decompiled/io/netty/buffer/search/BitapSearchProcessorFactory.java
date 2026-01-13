package io.netty.buffer.search;

import io.netty.util.internal.PlatformDependent;

public class BitapSearchProcessorFactory extends AbstractSearchProcessorFactory {
   private final long[] bitMasks = new long[256];
   private final long successBit;

   BitapSearchProcessorFactory(byte[] needle) {
      if (needle.length > 64) {
         throw new IllegalArgumentException("Maximum supported search pattern length is 64, got " + needle.length);
      } else {
         long bit = 1L;

         for (byte c : needle) {
            this.bitMasks[c & 255] = this.bitMasks[c & 255] | bit;
            bit <<= 1;
         }

         this.successBit = 1L << needle.length - 1;
      }
   }

   public BitapSearchProcessorFactory.Processor newSearchProcessor() {
      return new BitapSearchProcessorFactory.Processor(this.bitMasks, this.successBit);
   }

   public static class Processor implements SearchProcessor {
      private final long[] bitMasks;
      private final long successBit;
      private long currentMask;

      Processor(long[] bitMasks, long successBit) {
         this.bitMasks = bitMasks;
         this.successBit = successBit;
      }

      @Override
      public boolean process(byte value) {
         this.currentMask = (this.currentMask << 1 | 1L) & PlatformDependent.getLong(this.bitMasks, value & 255L);
         return (this.currentMask & this.successBit) == 0L;
      }

      @Override
      public void reset() {
         this.currentMask = 0L;
      }
   }
}
