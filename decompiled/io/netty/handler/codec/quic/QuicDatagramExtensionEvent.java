package io.netty.handler.codec.quic;

import io.netty.util.internal.ObjectUtil;

public final class QuicDatagramExtensionEvent implements QuicExtensionEvent {
   private final int maxLength;

   QuicDatagramExtensionEvent(int maxLength) {
      this.maxLength = ObjectUtil.checkPositiveOrZero(maxLength, "maxLength");
   }

   public int maxLength() {
      return this.maxLength;
   }

   @Override
   public String toString() {
      return "QuicDatagramExtensionEvent{maxLength=" + this.maxLength + '}';
   }
}
