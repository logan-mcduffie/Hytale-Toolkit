package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.util.internal.StringUtil;

public final class DefaultHttp3DataFrame extends DefaultByteBufHolder implements Http3DataFrame {
   public DefaultHttp3DataFrame(ByteBuf data) {
      super(data);
   }

   @Override
   public Http3DataFrame copy() {
      return new DefaultHttp3DataFrame(this.content().copy());
   }

   @Override
   public Http3DataFrame duplicate() {
      return new DefaultHttp3DataFrame(this.content().duplicate());
   }

   @Override
   public Http3DataFrame retainedDuplicate() {
      return new DefaultHttp3DataFrame(this.content().retainedDuplicate());
   }

   @Override
   public Http3DataFrame replace(ByteBuf content) {
      return new DefaultHttp3DataFrame(content);
   }

   @Override
   public Http3DataFrame retain() {
      super.retain();
      return this;
   }

   @Override
   public Http3DataFrame retain(int increment) {
      super.retain(increment);
      return this;
   }

   @Override
   public Http3DataFrame touch() {
      super.touch();
      return this;
   }

   @Override
   public Http3DataFrame touch(Object hint) {
      super.touch(hint);
      return this;
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this) + "(content=" + this.content() + ')';
   }
}
