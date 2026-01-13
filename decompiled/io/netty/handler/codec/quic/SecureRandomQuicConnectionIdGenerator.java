package io.netty.handler.codec.quic;

import io.netty.util.internal.ObjectUtil;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

final class SecureRandomQuicConnectionIdGenerator implements QuicConnectionIdGenerator {
   private static final SecureRandom RANDOM = new SecureRandom();
   static final QuicConnectionIdGenerator INSTANCE = new SecureRandomQuicConnectionIdGenerator();

   private SecureRandomQuicConnectionIdGenerator() {
   }

   @Override
   public ByteBuffer newId(int length) {
      ObjectUtil.checkInRange(length, 0, this.maxConnectionIdLength(), "length");
      byte[] bytes = new byte[length];
      RANDOM.nextBytes(bytes);
      return ByteBuffer.wrap(bytes);
   }

   @Override
   public ByteBuffer newId(ByteBuffer buffer, int length) {
      return this.newId(length);
   }

   @Override
   public int maxConnectionIdLength() {
      return Quiche.QUICHE_MAX_CONN_ID_LEN;
   }

   @Override
   public boolean isIdempotent() {
      return false;
   }
}
