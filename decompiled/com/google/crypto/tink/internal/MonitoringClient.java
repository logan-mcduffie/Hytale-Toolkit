package com.google.crypto.tink.internal;

import com.google.crypto.tink.annotations.Alpha;

@Alpha
public interface MonitoringClient {
   MonitoringClient.Logger createLogger(KeysetHandleInterface keysetInfo, MonitoringAnnotations annotations, String primitive, String api);

   public interface Logger {
      default void log(int keyId, long numBytesAsInput) {
      }

      default void logFailure() {
      }

      default void logKeyExport(int keyId) {
      }
   }
}
