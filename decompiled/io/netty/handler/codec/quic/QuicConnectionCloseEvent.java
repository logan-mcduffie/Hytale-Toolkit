package io.netty.handler.codec.quic;

import java.util.Arrays;

public final class QuicConnectionCloseEvent implements QuicEvent {
   final boolean applicationClose;
   final int error;
   final byte[] reason;

   QuicConnectionCloseEvent(boolean applicationClose, int error, byte[] reason) {
      this.applicationClose = applicationClose;
      this.error = error;
      this.reason = reason;
   }

   public boolean isApplicationClose() {
      return this.applicationClose;
   }

   public int error() {
      return this.error;
   }

   public boolean isTlsError() {
      return !this.applicationClose && this.error >= 256;
   }

   public byte[] reason() {
      return (byte[])this.reason.clone();
   }

   @Override
   public String toString() {
      return "QuicConnectionCloseEvent{applicationClose=" + this.applicationClose + ", error=" + this.error + ", reason=" + Arrays.toString(this.reason) + '}';
   }

   public static int extractTlsError(int error) {
      int tlsError = error - 256;
      return tlsError < 0 ? -1 : tlsError;
   }
}
