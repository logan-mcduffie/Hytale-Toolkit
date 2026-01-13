package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;

public final class DefaultQuicStreamFrame extends DefaultByteBufHolder implements QuicStreamFrame {
   private final boolean fin;

   public DefaultQuicStreamFrame(ByteBuf data, boolean fin) {
      super(data);
      this.fin = fin;
   }

   @Override
   public boolean hasFin() {
      return this.fin;
   }

   @Override
   public QuicStreamFrame copy() {
      return new DefaultQuicStreamFrame(this.content().copy(), this.fin);
   }

   @Override
   public QuicStreamFrame duplicate() {
      return new DefaultQuicStreamFrame(this.content().duplicate(), this.fin);
   }

   @Override
   public QuicStreamFrame retainedDuplicate() {
      return new DefaultQuicStreamFrame(this.content().retainedDuplicate(), this.fin);
   }

   @Override
   public QuicStreamFrame replace(ByteBuf content) {
      return new DefaultQuicStreamFrame(content, this.fin);
   }

   @Override
   public QuicStreamFrame retain() {
      super.retain();
      return this;
   }

   @Override
   public QuicStreamFrame retain(int increment) {
      super.retain(increment);
      return this;
   }

   @Override
   public QuicStreamFrame touch() {
      super.touch();
      return this;
   }

   @Override
   public QuicStreamFrame touch(Object hint) {
      super.touch(hint);
      return this;
   }

   @Override
   public String toString() {
      return "DefaultQuicStreamFrame{fin=" + this.fin + ", content=" + this.contentToString() + '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         DefaultQuicStreamFrame that = (DefaultQuicStreamFrame)o;
         return this.fin != that.fin ? false : super.equals(o);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      return 31 * result + (this.fin ? 1 : 0);
   }
}
