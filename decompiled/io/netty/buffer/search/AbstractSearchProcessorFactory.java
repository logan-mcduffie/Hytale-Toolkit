package io.netty.buffer.search;

public abstract class AbstractSearchProcessorFactory implements SearchProcessorFactory {
   public static KmpSearchProcessorFactory newKmpSearchProcessorFactory(byte[] needle) {
      return new KmpSearchProcessorFactory(needle);
   }

   public static BitapSearchProcessorFactory newBitapSearchProcessorFactory(byte[] needle) {
      return new BitapSearchProcessorFactory(needle);
   }
}
