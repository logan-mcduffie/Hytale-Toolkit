package com.nimbusds.jose.util.health;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.events.Event;
import java.util.Objects;

@Immutable
public class HealthReport<S, C extends SecurityContext> implements Event<S, C> {
   private final S source;
   private final HealthStatus status;
   private final Exception exception;
   private final long timestamp;
   private final C context;

   public HealthReport(S source, HealthStatus status, long timestamp, C context) {
      this(source, status, null, timestamp, context);
   }

   public HealthReport(S source, HealthStatus status, Exception exception, long timestamp, C context) {
      Objects.requireNonNull(source);
      this.source = source;
      Objects.requireNonNull(status);
      this.status = status;
      if (exception != null && HealthStatus.HEALTHY.equals(status)) {
         throw new IllegalArgumentException("Exception not accepted for a healthy status");
      } else {
         this.exception = exception;
         this.timestamp = timestamp;
         this.context = context;
      }
   }

   @Override
   public S getSource() {
      return this.source;
   }

   @Override
   public C getContext() {
      return this.context;
   }

   public HealthStatus getHealthStatus() {
      return this.status;
   }

   public Exception getException() {
      return this.exception;
   }

   public long getTimestamp() {
      return this.timestamp;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder("HealthReport{");
      sb.append("source=").append(this.source);
      sb.append(", status=").append(this.status);
      sb.append(", exception=").append(this.exception);
      sb.append(", timestamp=").append(this.timestamp);
      sb.append(", context=").append(this.context);
      sb.append('}');
      return sb.toString();
   }
}
