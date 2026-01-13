package io.netty.util;

public interface ByteProcessor {
   ByteProcessor FIND_NUL = new ByteProcessor.IndexOfProcessor((byte)0);
   ByteProcessor FIND_NON_NUL = new ByteProcessor.IndexNotOfProcessor((byte)0);
   ByteProcessor FIND_CR = new ByteProcessor.IndexOfProcessor((byte)13);
   ByteProcessor FIND_NON_CR = new ByteProcessor.IndexNotOfProcessor((byte)13);
   ByteProcessor FIND_LF = new ByteProcessor.IndexOfProcessor((byte)10);
   ByteProcessor FIND_NON_LF = new ByteProcessor.IndexNotOfProcessor((byte)10);
   ByteProcessor FIND_SEMI_COLON = new ByteProcessor.IndexOfProcessor((byte)59);
   ByteProcessor FIND_COMMA = new ByteProcessor.IndexOfProcessor((byte)44);
   ByteProcessor FIND_ASCII_SPACE = new ByteProcessor.IndexOfProcessor((byte)32);
   ByteProcessor FIND_CRLF = new ByteProcessor() {
      @Override
      public boolean process(byte value) {
         return value != 13 && value != 10;
      }
   };
   ByteProcessor FIND_NON_CRLF = new ByteProcessor() {
      @Override
      public boolean process(byte value) {
         return value == 13 || value == 10;
      }
   };
   ByteProcessor FIND_LINEAR_WHITESPACE = new ByteProcessor() {
      @Override
      public boolean process(byte value) {
         return value != 32 && value != 9;
      }
   };
   ByteProcessor FIND_NON_LINEAR_WHITESPACE = new ByteProcessor() {
      @Override
      public boolean process(byte value) {
         return value == 32 || value == 9;
      }
   };

   boolean process(byte var1) throws Exception;

   public static class IndexNotOfProcessor implements ByteProcessor {
      private final byte byteToNotFind;

      public IndexNotOfProcessor(byte byteToNotFind) {
         this.byteToNotFind = byteToNotFind;
      }

      @Override
      public boolean process(byte value) {
         return value == this.byteToNotFind;
      }
   }

   public static class IndexOfProcessor implements ByteProcessor {
      private final byte byteToFind;

      public IndexOfProcessor(byte byteToFind) {
         this.byteToFind = byteToFind;
      }

      @Override
      public boolean process(byte value) {
         return value != this.byteToFind;
      }
   }
}
