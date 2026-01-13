package io.netty.channel.unix;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.DefaultAddressedEnvelope;

public final class DomainDatagramPacket extends DefaultAddressedEnvelope<ByteBuf, DomainSocketAddress> implements ByteBufHolder {
   public DomainDatagramPacket(ByteBuf data, DomainSocketAddress recipient) {
      super(data, recipient);
   }

   public DomainDatagramPacket(ByteBuf data, DomainSocketAddress recipient, DomainSocketAddress sender) {
      super(data, recipient, sender);
   }

   public DomainDatagramPacket copy() {
      return this.replace(this.content().copy());
   }

   public DomainDatagramPacket duplicate() {
      return this.replace(this.content().duplicate());
   }

   public DomainDatagramPacket replace(ByteBuf content) {
      return new DomainDatagramPacket(content, this.recipient(), this.sender());
   }

   public DomainDatagramPacket retain() {
      super.retain();
      return this;
   }

   public DomainDatagramPacket retain(int increment) {
      super.retain(increment);
      return this;
   }

   public DomainDatagramPacket retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   public DomainDatagramPacket touch() {
      super.touch();
      return this;
   }

   public DomainDatagramPacket touch(Object hint) {
      super.touch(hint);
      return this;
   }
}
