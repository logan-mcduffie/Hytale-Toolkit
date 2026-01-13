package io.netty.handler.codec.http3;

import io.netty.util.internal.ObjectUtil;
import org.jetbrains.annotations.Nullable;

public final class Http3Exception extends Exception {
   private final Http3ErrorCode errorCode;

   public Http3Exception(Http3ErrorCode errorCode, @Nullable String message) {
      super(message);
      this.errorCode = ObjectUtil.checkNotNull(errorCode, "errorCode");
   }

   public Http3Exception(Http3ErrorCode errorCode, String message, @Nullable Throwable cause) {
      super(message, cause);
      this.errorCode = ObjectUtil.checkNotNull(errorCode, "errorCode");
   }

   public Http3ErrorCode errorCode() {
      return this.errorCode;
   }
}
