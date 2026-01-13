package io.netty.handler.codec.quic;

public enum QuicPacketType {
   INITIAL,
   RETRY,
   HANDSHAKE,
   ZERO_RTT,
   SHORT,
   VERSION_NEGOTIATION;

   static QuicPacketType of(byte type) {
      switch (type) {
         case 1:
            return INITIAL;
         case 2:
            return RETRY;
         case 3:
            return HANDSHAKE;
         case 4:
            return ZERO_RTT;
         case 5:
            return SHORT;
         case 6:
            return VERSION_NEGOTIATION;
         default:
            throw new IllegalArgumentException("Unknown QUIC packet type: " + type);
      }
   }
}
