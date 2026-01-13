package io.netty.handler.codec.http3;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.Objects;

public final class DefaultHttp3HeadersFrame implements Http3HeadersFrame {
   private final Http3Headers headers;

   public DefaultHttp3HeadersFrame() {
      this(new DefaultHttp3Headers());
   }

   public DefaultHttp3HeadersFrame(Http3Headers headers) {
      this.headers = ObjectUtil.checkNotNull(headers, "headers");
   }

   @Override
   public Http3Headers headers() {
      return this.headers;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         DefaultHttp3HeadersFrame that = (DefaultHttp3HeadersFrame)o;
         return Objects.equals(this.headers, that.headers);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.headers);
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this) + "(headers=" + this.headers() + ')';
   }
}
