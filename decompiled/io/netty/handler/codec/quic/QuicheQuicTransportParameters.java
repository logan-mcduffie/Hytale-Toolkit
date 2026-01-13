package io.netty.handler.codec.quic;

import io.netty.util.internal.StringUtil;

final class QuicheQuicTransportParameters implements QuicTransportParameters {
   private final long[] values;

   QuicheQuicTransportParameters(long[] values) {
      this.values = values;
   }

   @Override
   public long maxIdleTimeout() {
      return this.values[0];
   }

   @Override
   public long maxUdpPayloadSize() {
      return this.values[1];
   }

   @Override
   public long initialMaxData() {
      return this.values[2];
   }

   @Override
   public long initialMaxStreamDataBidiLocal() {
      return this.values[3];
   }

   @Override
   public long initialMaxStreamDataBidiRemote() {
      return this.values[4];
   }

   @Override
   public long initialMaxStreamDataUni() {
      return this.values[5];
   }

   @Override
   public long initialMaxStreamsBidi() {
      return this.values[6];
   }

   @Override
   public long initialMaxStreamsUni() {
      return this.values[7];
   }

   @Override
   public long ackDelayExponent() {
      return this.values[8];
   }

   @Override
   public long maxAckDelay() {
      return this.values[9];
   }

   @Override
   public boolean disableActiveMigration() {
      return this.values[10] == 1L;
   }

   @Override
   public long activeConnIdLimit() {
      return this.values[11];
   }

   @Override
   public long maxDatagramFrameSize() {
      return this.values[12];
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this)
         + "[maxIdleTimeout="
         + this.maxIdleTimeout()
         + ", maxUdpPayloadSize="
         + this.maxUdpPayloadSize()
         + ", initialMaxData="
         + this.initialMaxData()
         + ", initialMaxStreamDataBidiLocal="
         + this.initialMaxStreamDataBidiLocal()
         + ", initialMaxStreamDataBidiRemote="
         + this.initialMaxStreamDataBidiRemote()
         + ", initialMaxStreamDataUni="
         + this.initialMaxStreamDataUni()
         + ", initialMaxStreamsBidi="
         + this.initialMaxStreamsBidi()
         + ", initialMaxStreamsUni="
         + this.initialMaxStreamsUni()
         + ", ackDelayExponent="
         + this.ackDelayExponent()
         + ", maxAckDelay="
         + this.maxAckDelay()
         + ", disableActiveMigration="
         + this.disableActiveMigration()
         + ", activeConnIdLimit="
         + this.activeConnIdLimit()
         + ", maxDatagramFrameSize="
         + this.maxDatagramFrameSize()
         + "]";
   }
}
