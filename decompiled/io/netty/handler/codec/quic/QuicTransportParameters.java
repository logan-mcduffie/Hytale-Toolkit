package io.netty.handler.codec.quic;

public interface QuicTransportParameters {
   long maxIdleTimeout();

   long maxUdpPayloadSize();

   long initialMaxData();

   long initialMaxStreamDataBidiLocal();

   long initialMaxStreamDataBidiRemote();

   long initialMaxStreamDataUni();

   long initialMaxStreamsBidi();

   long initialMaxStreamsUni();

   long ackDelayExponent();

   long maxAckDelay();

   boolean disableActiveMigration();

   long activeConnIdLimit();

   long maxDatagramFrameSize();
}
