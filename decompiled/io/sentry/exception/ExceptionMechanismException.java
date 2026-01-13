package io.sentry.exception;

import io.sentry.protocol.Mechanism;
import io.sentry.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ExceptionMechanismException extends RuntimeException {
   private static final long serialVersionUID = 142345454265713915L;
   @NotNull
   private final Mechanism exceptionMechanism;
   @NotNull
   private final Throwable throwable;
   @NotNull
   private final Thread thread;
   private final boolean snapshot;

   public ExceptionMechanismException(@NotNull Mechanism mechanism, @NotNull Throwable throwable, @NotNull Thread thread, boolean snapshot) {
      this.exceptionMechanism = Objects.requireNonNull(mechanism, "Mechanism is required.");
      this.throwable = Objects.requireNonNull(throwable, "Throwable is required.");
      this.thread = Objects.requireNonNull(thread, "Thread is required.");
      this.snapshot = snapshot;
   }

   public ExceptionMechanismException(@NotNull Mechanism mechanism, @NotNull Throwable throwable, @NotNull Thread thread) {
      this(mechanism, throwable, thread, false);
   }

   @NotNull
   public Mechanism getExceptionMechanism() {
      return this.exceptionMechanism;
   }

   @NotNull
   public Throwable getThrowable() {
      return this.throwable;
   }

   @NotNull
   public Thread getThread() {
      return this.thread;
   }

   public boolean isSnapshot() {
      return this.snapshot;
   }
}
