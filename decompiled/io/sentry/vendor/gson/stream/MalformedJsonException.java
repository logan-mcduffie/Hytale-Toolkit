package io.sentry.vendor.gson.stream;

import java.io.IOException;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class MalformedJsonException extends IOException {
   private static final long serialVersionUID = 1L;

   public MalformedJsonException(String msg) {
      super(msg);
   }

   public MalformedJsonException(String msg, Throwable throwable) {
      super(msg);
      this.initCause(throwable);
   }

   public MalformedJsonException(Throwable throwable) {
      this.initCause(throwable);
   }
}
