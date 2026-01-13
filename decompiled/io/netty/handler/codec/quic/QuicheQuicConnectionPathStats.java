package io.netty.handler.codec.quic;

import io.netty.util.internal.StringUtil;
import java.net.InetSocketAddress;

final class QuicheQuicConnectionPathStats implements QuicConnectionPathStats {
   private final Object[] values;

   QuicheQuicConnectionPathStats(Object[] values) {
      this.values = values;
   }

   @Override
   public InetSocketAddress localAddress() {
      return (InetSocketAddress)this.values[0];
   }

   @Override
   public InetSocketAddress peerAddress() {
      return (InetSocketAddress)this.values[1];
   }

   @Override
   public long validationState() {
      return (Long)this.values[2];
   }

   @Override
   public boolean active() {
      return (Boolean)this.values[3];
   }

   @Override
   public long recv() {
      return (Long)this.values[4];
   }

   @Override
   public long sent() {
      return (Long)this.values[5];
   }

   @Override
   public long lost() {
      return (Long)this.values[6];
   }

   @Override
   public long retrans() {
      return (Long)this.values[7];
   }

   @Override
   public long rtt() {
      return (Long)this.values[8];
   }

   @Override
   public long cwnd() {
      return (Long)this.values[9];
   }

   @Override
   public long sentBytes() {
      return (Long)this.values[10];
   }

   @Override
   public long recvBytes() {
      return (Long)this.values[11];
   }

   @Override
   public long lostBytes() {
      return (Long)this.values[12];
   }

   @Override
   public long streamRetransBytes() {
      return (Long)this.values[13];
   }

   @Override
   public long pmtu() {
      return (Long)this.values[14];
   }

   @Override
   public long deliveryRate() {
      return (Long)this.values[15];
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this)
         + "[local="
         + this.localAddress()
         + ", peer="
         + this.peerAddress()
         + ", validationState="
         + this.validationState()
         + ", active="
         + this.active()
         + ", recv="
         + this.recv()
         + ", sent="
         + this.sent()
         + ", lost="
         + this.lost()
         + ", retrans="
         + this.retrans()
         + ", rtt="
         + this.rtt()
         + ", cwnd="
         + this.cwnd()
         + ", sentBytes="
         + this.sentBytes()
         + ", recvBytes="
         + this.recvBytes()
         + ", lostBytes="
         + this.lostBytes()
         + ", streamRetransBytes="
         + this.streamRetransBytes()
         + ", pmtu="
         + this.pmtu()
         + ", deliveryRate="
         + this.deliveryRate()
         + ']';
   }
}
