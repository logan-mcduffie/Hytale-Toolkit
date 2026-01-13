package io.netty.handler.codec.quic;

import io.netty.util.internal.StringUtil;

final class QuicheQuicConnectionStats implements QuicConnectionStats {
   private final long[] values;

   QuicheQuicConnectionStats(long[] values) {
      this.values = values;
   }

   @Override
   public long recv() {
      return this.values[0];
   }

   @Override
   public long sent() {
      return this.values[1];
   }

   @Override
   public long lost() {
      return this.values[2];
   }

   @Override
   public long retrans() {
      return this.values[3];
   }

   @Override
   public long sentBytes() {
      return this.values[4];
   }

   @Override
   public long recvBytes() {
      return this.values[5];
   }

   @Override
   public long lostBytes() {
      return this.values[6];
   }

   @Override
   public long streamRetransBytes() {
      return this.values[7];
   }

   @Override
   public long pathsCount() {
      return this.values[8];
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this)
         + "[recv="
         + this.recv()
         + ", sent="
         + this.sent()
         + ", lost="
         + this.lost()
         + ", retrans="
         + this.retrans()
         + ", sentBytes="
         + this.sentBytes()
         + ", recvBytes="
         + this.recvBytes()
         + ", lostBytes="
         + this.lostBytes()
         + ", streamRetransBytes="
         + this.streamRetransBytes()
         + ", pathsCount="
         + this.pathsCount()
         + "]";
   }
}
