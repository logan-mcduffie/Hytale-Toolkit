package io.netty.handler.codec.quic;

import java.nio.ByteBuffer;

public interface QuicResetTokenGenerator {
   ByteBuffer newResetToken(ByteBuffer var1);

   static QuicResetTokenGenerator signGenerator() {
      return HmacSignQuicResetTokenGenerator.INSTANCE;
   }
}
