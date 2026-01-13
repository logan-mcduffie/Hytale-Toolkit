package io.netty.handler.codec.http3;

import io.netty.util.internal.ThrowableUtil;
import org.jetbrains.annotations.Nullable;

public final class QpackException extends Exception {
   private QpackException(String message, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

   static QpackException newStatic(Class<?> clazz, String method, String message) {
      return ThrowableUtil.unknownStackTrace(new QpackException(message, null, false, false), clazz, method);
   }
}
