package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.util.internal.StringUtil;
import java.util.Objects;

public final class DefaultHttp3UnknownFrame extends DefaultByteBufHolder implements Http3UnknownFrame {
   private final long type;

   public DefaultHttp3UnknownFrame(long type, ByteBuf payload) {
      super(payload);
      this.type = Http3CodecUtils.checkIsReservedFrameType(type);
   }

   @Override
   public long type() {
      return this.type;
   }

   @Override
   public Http3UnknownFrame copy() {
      return new DefaultHttp3UnknownFrame(this.type, this.content().copy());
   }

   @Override
   public Http3UnknownFrame duplicate() {
      return new DefaultHttp3UnknownFrame(this.type, this.content().duplicate());
   }

   @Override
   public Http3UnknownFrame retainedDuplicate() {
      return new DefaultHttp3UnknownFrame(this.type, this.content().retainedDuplicate());
   }

   @Override
   public Http3UnknownFrame replace(ByteBuf content) {
      return new DefaultHttp3UnknownFrame(this.type, content);
   }

   @Override
   public Http3UnknownFrame retain() {
      super.retain();
      return this;
   }

   @Override
   public Http3UnknownFrame retain(int increment) {
      super.retain(increment);
      return this;
   }

   @Override
   public Http3UnknownFrame touch() {
      super.touch();
      return this;
   }

   @Override
   public Http3UnknownFrame touch(Object hint) {
      super.touch(hint);
      return this;
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this) + "(type=" + this.type() + ", content=" + this.content() + ')';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         DefaultHttp3UnknownFrame that = (DefaultHttp3UnknownFrame)o;
         return this.type != that.type ? false : super.equals(o);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), this.type);
   }
}
