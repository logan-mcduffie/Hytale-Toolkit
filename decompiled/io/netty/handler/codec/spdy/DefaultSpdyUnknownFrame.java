package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.util.internal.StringUtil;

public final class DefaultSpdyUnknownFrame extends DefaultByteBufHolder implements SpdyUnknownFrame {
   private final int frameType;
   private final byte flags;

   public DefaultSpdyUnknownFrame(int frameType, byte flags, ByteBuf data) {
      super(data);
      this.frameType = frameType;
      this.flags = flags;
   }

   @Override
   public int frameType() {
      return this.frameType;
   }

   @Override
   public byte flags() {
      return this.flags;
   }

   public DefaultSpdyUnknownFrame copy() {
      return this.replace(this.content().copy());
   }

   public DefaultSpdyUnknownFrame duplicate() {
      return this.replace(this.content().duplicate());
   }

   public DefaultSpdyUnknownFrame retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   public DefaultSpdyUnknownFrame replace(ByteBuf content) {
      return new DefaultSpdyUnknownFrame(this.frameType, this.flags, content);
   }

   public DefaultSpdyUnknownFrame retain() {
      super.retain();
      return this;
   }

   public DefaultSpdyUnknownFrame retain(int increment) {
      super.retain(increment);
      return this;
   }

   public DefaultSpdyUnknownFrame touch() {
      super.touch();
      return this;
   }

   public DefaultSpdyUnknownFrame touch(Object hint) {
      super.touch(hint);
      return this;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof DefaultSpdyUnknownFrame)) {
         return false;
      } else {
         DefaultSpdyUnknownFrame that = (DefaultSpdyUnknownFrame)o;
         return this.frameType == that.frameType && this.flags == that.flags && super.equals(that);
      }
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + this.frameType;
      return 31 * result + this.flags;
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this) + "(frameType=" + this.frameType + ", flags=" + this.flags + ", content=" + this.contentToString() + ')';
   }
}
