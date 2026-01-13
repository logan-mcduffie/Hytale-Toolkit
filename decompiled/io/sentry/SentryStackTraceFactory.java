package io.sentry;

import io.sentry.protocol.SentryStackFrame;
import io.sentry.util.CollectionUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SentryStackTraceFactory {
   private static final int STACKTRACE_FRAME_LIMIT = 100;
   @NotNull
   private final SentryOptions options;

   public SentryStackTraceFactory(@NotNull SentryOptions options) {
      this.options = options;
   }

   @Nullable
   public List<SentryStackFrame> getStackFrames(@Nullable StackTraceElement[] elements, boolean includeSentryFrames) {
      List<SentryStackFrame> sentryStackFrames = null;
      if (elements != null && elements.length > 0) {
         sentryStackFrames = new ArrayList<>();

         for (StackTraceElement item : elements) {
            if (item != null) {
               String className = item.getClassName();
               if (includeSentryFrames
                  || !className.startsWith("io.sentry.")
                  || className.startsWith("io.sentry.samples.")
                  || className.startsWith("io.sentry.mobile.")) {
                  SentryStackFrame sentryStackFrame = new SentryStackFrame();
                  sentryStackFrame.setInApp(this.isInApp(className));
                  sentryStackFrame.setModule(className);
                  sentryStackFrame.setFunction(item.getMethodName());
                  sentryStackFrame.setFilename(item.getFileName());
                  if (item.getLineNumber() >= 0) {
                     sentryStackFrame.setLineno(item.getLineNumber());
                  }

                  sentryStackFrame.setNative(item.isNativeMethod());
                  sentryStackFrames.add(sentryStackFrame);
                  if (sentryStackFrames.size() >= 100) {
                     break;
                  }
               }
            }
         }

         Collections.reverse(sentryStackFrames);
      }

      return sentryStackFrames;
   }

   @Nullable
   public Boolean isInApp(@Nullable String className) {
      if (className != null && !className.isEmpty()) {
         for (String include : this.options.getInAppIncludes()) {
            if (className.startsWith(include)) {
               return true;
            }
         }

         for (String exclude : this.options.getInAppExcludes()) {
            if (className.startsWith(exclude)) {
               return false;
            }
         }

         return null;
      } else {
         return true;
      }
   }

   @NotNull
   List<SentryStackFrame> getInAppCallStack(@NotNull Throwable exception) {
      StackTraceElement[] stacktrace = exception.getStackTrace();
      List<SentryStackFrame> frames = this.getStackFrames(stacktrace, false);
      if (frames == null) {
         return Collections.emptyList();
      } else {
         List<SentryStackFrame> inAppFrames = CollectionUtils.filterListEntries(frames, frame -> Boolean.TRUE.equals(frame.isInApp()));
         return !inAppFrames.isEmpty() ? inAppFrames : CollectionUtils.filterListEntries(frames, frame -> {
            String module = frame.getModule();
            boolean isSystemFrame = false;
            if (module != null) {
               isSystemFrame = module.startsWith("sun.") || module.startsWith("java.") || module.startsWith("android.") || module.startsWith("com.android.");
            }

            return !isSystemFrame;
         });
      }
   }

   @Internal
   @NotNull
   public List<SentryStackFrame> getInAppCallStack() {
      return this.getInAppCallStack(new Exception());
   }
}
