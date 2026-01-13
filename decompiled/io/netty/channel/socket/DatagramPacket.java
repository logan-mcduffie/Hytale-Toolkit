package io.netty.channel.socket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.DefaultAddressedEnvelope;
import java.net.InetSocketAddress;

public class DatagramPacket extends DefaultAddressedEnvelope<ByteBuf, InetSocketAddress> implements ByteBufHolder {
   public DatagramPacket(ByteBuf data, InetSocketAddress recipient) {
      super(data, recipient);
   }

   public DatagramPacket(ByteBuf data, InetSocketAddress recipient, InetSocketAddress sender) {
      super(data, recipient, sender);
   }

   public DatagramPacket copy() {
      return this.replace(this.content().copy());
   }

   public DatagramPacket duplicate() {
      return this.replace(this.content().duplicate());
   }

   public DatagramPacket retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   public DatagramPacket replace(ByteBuf content) {
      return new DatagramPacket(content, this.recipient(), this.sender());
   }

   public DatagramPacket retain() {
      super.retain();
      return this;
   }

   public DatagramPacket retain(int increment) {
      super.retain(increment);
      return this;
   }

   public DatagramPacket touch() {
      super.touch();
      return this;
   }

   public DatagramPacket touch(Object hint) {
      super.touch(hint);
      return this;
   }
}
