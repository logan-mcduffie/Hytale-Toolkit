package io.netty.buffer;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultByteBufHolder implements ByteBufHolder {
   private final ByteBuf data;

   public DefaultByteBufHolder(ByteBuf data) {
      this.data = ObjectUtil.checkNotNull(data, "data");
   }

   @Override
   public ByteBuf content() {
      return ByteBufUtil.ensureAccessible(this.data);
   }

   @Override
   public ByteBufHolder copy() {
      return this.replace(this.data.copy());
   }

   @Override
   public ByteBufHolder duplicate() {
      return this.replace(this.data.duplicate());
   }

   @Override
   public ByteBufHolder retainedDuplicate() {
      return this.replace(this.data.retainedDuplicate());
   }

   @Override
   public ByteBufHolder replace(ByteBuf content) {
      return new DefaultByteBufHolder(content);
   }

   @Override
   public int refCnt() {
      return this.data.refCnt();
   }

   @Override
   public ByteBufHolder retain() {
      this.data.retain();
      return this;
   }

   @Override
   public ByteBufHolder retain(int increment) {
      this.data.retain(increment);
      return this;
   }

   @Override
   public ByteBufHolder touch() {
      this.data.touch();
      return this;
   }

   @Override
   public ByteBufHolder touch(Object hint) {
      this.data.touch(hint);
      return this;
   }

   @Override
   public boolean release() {
      return this.data.release();
   }

   @Override
   public boolean release(int decrement) {
      return this.data.release(decrement);
   }

   protected final String contentToString() {
      return this.data.toString();
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this) + '(' + this.contentToString() + ')';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return o != null && this.getClass() == o.getClass() ? this.data.equals(((DefaultByteBufHolder)o).data) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.data.hashCode();
   }
}
