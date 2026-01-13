package io.netty.handler.codec.http3;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.Objects;

public final class DefaultHttp3CancelPushFrame implements Http3CancelPushFrame {
   private final long id;

   public DefaultHttp3CancelPushFrame(long id) {
      this.id = ObjectUtil.checkPositiveOrZero(id, "id");
   }

   @Override
   public long id() {
      return this.id;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         DefaultHttp3CancelPushFrame that = (DefaultHttp3CancelPushFrame)o;
         return this.id == that.id;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this) + "(id=" + this.id() + ')';
   }
}
