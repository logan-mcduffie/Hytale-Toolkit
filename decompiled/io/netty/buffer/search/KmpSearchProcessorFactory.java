package io.netty.buffer.search;

import io.netty.util.internal.PlatformDependent;

public class KmpSearchProcessorFactory extends AbstractSearchProcessorFactory {
   private final int[] jumpTable;
   private final byte[] needle;

   KmpSearchProcessorFactory(byte[] needle) {
      this.needle = (byte[])needle.clone();
      this.jumpTable = new int[needle.length + 1];
      int j = 0;

      for (int i = 1; i < needle.length; i++) {
         while (j > 0 && needle[j] != needle[i]) {
            j = this.jumpTable[j];
         }

         if (needle[j] == needle[i]) {
            j++;
         }

         this.jumpTable[i + 1] = j;
      }
   }

   public KmpSearchProcessorFactory.Processor newSearchProcessor() {
      return new KmpSearchProcessorFactory.Processor(this.needle, this.jumpTable);
   }

   public static class Processor implements SearchProcessor {
      private final byte[] needle;
      private final int[] jumpTable;
      private long currentPosition;

      Processor(byte[] needle, int[] jumpTable) {
         this.needle = needle;
         this.jumpTable = jumpTable;
      }

      @Override
      public boolean process(byte value) {
         while (this.currentPosition > 0L && PlatformDependent.getByte(this.needle, this.currentPosition) != value) {
            this.currentPosition = PlatformDependent.getInt(this.jumpTable, this.currentPosition);
         }

         if (PlatformDependent.getByte(this.needle, this.currentPosition) == value) {
            this.currentPosition++;
         }

         if (this.currentPosition == this.needle.length) {
            this.currentPosition = PlatformDependent.getInt(this.jumpTable, this.currentPosition);
            return false;
         } else {
            return true;
         }
      }

      @Override
      public void reset() {
         this.currentPosition = 0L;
      }
   }
}
