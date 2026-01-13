package io.netty.handler.codec.quic;

import io.netty.util.internal.ObjectUtil;
import java.nio.ByteBuffer;

final class HmacSignQuicConnectionIdGenerator implements QuicConnectionIdGenerator {
   static final QuicConnectionIdGenerator INSTANCE = new HmacSignQuicConnectionIdGenerator();

   private HmacSignQuicConnectionIdGenerator() {
   }

   @Override
   public ByteBuffer newId(int length) {
      throw new UnsupportedOperationException("HmacSignQuicConnectionIdGenerator should always have an input to sign with");
   }

   @Override
   public ByteBuffer newId(ByteBuffer buffer, int length) {
      ObjectUtil.checkNotNull(buffer, "buffer");
      ObjectUtil.checkPositive(buffer.remaining(), "buffer");
      ObjectUtil.checkInRange(length, 0, this.maxConnectionIdLength(), "length");
      return Hmac.sign(buffer, length);
   }

   @Override
   public int maxConnectionIdLength() {
      return Quiche.QUICHE_MAX_CONN_ID_LEN;
   }

   @Override
   public boolean isIdempotent() {
      return true;
   }
}
