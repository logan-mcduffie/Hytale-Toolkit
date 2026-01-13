package io.netty.handler.codec.quic;

import java.nio.ByteBuffer;

public interface QuicConnectionIdGenerator {
   ByteBuffer newId(int var1);

   ByteBuffer newId(ByteBuffer var1, int var2);

   default ByteBuffer newId(ByteBuffer scid, ByteBuffer dcid, int length) {
      return this.newId(dcid, length);
   }

   int maxConnectionIdLength();

   boolean isIdempotent();

   static QuicConnectionIdGenerator randomGenerator() {
      return SecureRandomQuicConnectionIdGenerator.INSTANCE;
   }

   static QuicConnectionIdGenerator signGenerator() {
      return HmacSignQuicConnectionIdGenerator.INSTANCE;
   }
}
