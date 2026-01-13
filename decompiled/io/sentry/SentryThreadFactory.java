package io.sentry;

import io.sentry.protocol.SentryStackFrame;
import io.sentry.protocol.SentryStackTrace;
import io.sentry.protocol.SentryThread;
import io.sentry.util.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SentryThreadFactory {
   @NotNull
   private final SentryStackTraceFactory sentryStackTraceFactory;

   public SentryThreadFactory(@NotNull SentryStackTraceFactory sentryStackTraceFactory) {
      this.sentryStackTraceFactory = Objects.requireNonNull(sentryStackTraceFactory, "The SentryStackTraceFactory is required.");
   }

   @Nullable
   List<SentryThread> getCurrentThread(boolean attachStackTrace) {
      Map<Thread, StackTraceElement[]> threads = new HashMap<>();
      Thread currentThread = Thread.currentThread();
      threads.put(currentThread, currentThread.getStackTrace());
      return this.getCurrentThreads(threads, null, false, attachStackTrace);
   }

   @Nullable
   List<SentryThread> getCurrentThreads(@Nullable List<Long> mechanismThreadIds, boolean ignoreCurrentThread, boolean attachStackTrace) {
      return this.getCurrentThreads(Thread.getAllStackTraces(), mechanismThreadIds, ignoreCurrentThread, attachStackTrace);
   }

   @Nullable
   List<SentryThread> getCurrentThreads(@Nullable List<Long> mechanismThreadIds, boolean attachStackTrace) {
      return this.getCurrentThreads(Thread.getAllStackTraces(), mechanismThreadIds, false, attachStackTrace);
   }

   @TestOnly
   @Nullable
   List<SentryThread> getCurrentThreads(
      @NotNull Map<Thread, StackTraceElement[]> threads, @Nullable List<Long> mechanismThreadIds, boolean ignoreCurrentThread, boolean attachStackTrace
   ) {
      List<SentryThread> result = null;
      Thread currentThread = Thread.currentThread();
      if (!threads.isEmpty()) {
         result = new ArrayList<>();
         if (!threads.containsKey(currentThread)) {
            threads.put(currentThread, currentThread.getStackTrace());
         }

         for (Entry<Thread, StackTraceElement[]> item : threads.entrySet()) {
            Thread thread = item.getKey();
            boolean crashed = thread == currentThread && !ignoreCurrentThread
               || mechanismThreadIds != null && mechanismThreadIds.contains(thread.getId()) && !ignoreCurrentThread;
            result.add(this.getSentryThread(crashed, item.getValue(), item.getKey(), attachStackTrace));
         }
      }

      return result;
   }

   @NotNull
   private SentryThread getSentryThread(boolean crashed, @NotNull StackTraceElement[] stackFramesElements, @NotNull Thread thread, boolean attachStacktrace) {
      SentryThread sentryThread = new SentryThread();
      sentryThread.setName(thread.getName());
      sentryThread.setPriority(thread.getPriority());
      sentryThread.setId(thread.getId());
      sentryThread.setDaemon(thread.isDaemon());
      sentryThread.setState(thread.getState().name());
      sentryThread.setCrashed(crashed);
      if (attachStacktrace) {
         List<SentryStackFrame> frames = this.sentryStackTraceFactory.getStackFrames(stackFramesElements, false);
         if (frames != null && !frames.isEmpty()) {
            SentryStackTrace sentryStackTrace = new SentryStackTrace(frames);
            sentryStackTrace.setSnapshot(true);
            sentryThread.setStacktrace(sentryStackTrace);
         }
      }

      return sentryThread;
   }
}
