package io.netty.buffer.search;

public abstract class AbstractMultiSearchProcessorFactory implements MultiSearchProcessorFactory {
   public static AhoCorasicSearchProcessorFactory newAhoCorasicSearchProcessorFactory(byte[]... needles) {
      return new AhoCorasicSearchProcessorFactory(needles);
   }
}
