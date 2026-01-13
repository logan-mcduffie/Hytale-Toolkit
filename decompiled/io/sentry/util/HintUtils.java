package io.sentry.util;

import io.sentry.Hint;
import io.sentry.ILogger;
import io.sentry.hints.ApplyScopeData;
import io.sentry.hints.Backfillable;
import io.sentry.hints.Cached;
import io.sentry.hints.EventDropReason;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class HintUtils {
   private HintUtils() {
   }

   public static void setIsFromHybridSdk(@NotNull Hint hint, @NotNull String sdkName) {
      if (sdkName.startsWith("sentry.javascript") || sdkName.startsWith("sentry.dart") || sdkName.startsWith("sentry.dotnet")) {
         hint.set("sentry:isFromHybridSdk", true);
      }
   }

   public static boolean isFromHybridSdk(@NotNull Hint hint) {
      return Boolean.TRUE.equals(hint.getAs("sentry:isFromHybridSdk", Boolean.class));
   }

   public static void setEventDropReason(@NotNull Hint hint, @NotNull EventDropReason eventDropReason) {
      hint.set("sentry:eventDropReason", eventDropReason);
   }

   @Nullable
   public static EventDropReason getEventDropReason(@NotNull Hint hint) {
      return hint.getAs("sentry:eventDropReason", EventDropReason.class);
   }

   public static Hint createWithTypeCheckHint(Object typeCheckHint) {
      Hint hint = new Hint();
      setTypeCheckHint(hint, typeCheckHint);
      return hint;
   }

   public static void setTypeCheckHint(@NotNull Hint hint, Object typeCheckHint) {
      hint.set("sentry:typeCheckHint", typeCheckHint);
   }

   @Nullable
   public static Object getSentrySdkHint(@NotNull Hint hint) {
      return hint.get("sentry:typeCheckHint");
   }

   public static boolean hasType(@NotNull Hint hint, @NotNull Class<?> clazz) {
      Object sentrySdkHint = getSentrySdkHint(hint);
      return clazz.isInstance(sentrySdkHint);
   }

   public static <T> void runIfDoesNotHaveType(@NotNull Hint hint, @NotNull Class<T> clazz, HintUtils.SentryNullableConsumer<Object> lambda) {
      runIfHasType(hint, clazz, ignored -> {}, (value, clazz2) -> lambda.accept(value));
   }

   public static <T> void runIfHasType(@NotNull Hint hint, @NotNull Class<T> clazz, HintUtils.SentryConsumer<T> lambda) {
      runIfHasType(hint, clazz, lambda, (value, clazz2) -> {});
   }

   public static <T> void runIfHasTypeLogIfNot(@NotNull Hint hint, @NotNull Class<T> clazz, ILogger logger, HintUtils.SentryConsumer<T> lambda) {
      runIfHasType(hint, clazz, lambda, (sentrySdkHint, expectedClass) -> LogUtils.logNotInstanceOf(expectedClass, sentrySdkHint, logger));
   }

   public static <T> void runIfHasType(
      @NotNull Hint hint, @NotNull Class<T> clazz, HintUtils.SentryConsumer<T> lambda, HintUtils.SentryHintFallback fallbackLambda
   ) {
      Object sentrySdkHint = getSentrySdkHint(hint);
      if (hasType(hint, clazz) && sentrySdkHint != null) {
         lambda.accept((T)sentrySdkHint);
      } else {
         fallbackLambda.accept(sentrySdkHint, clazz);
      }
   }

   public static boolean shouldApplyScopeData(@NotNull Hint hint) {
      return !hasType(hint, Cached.class) && !hasType(hint, Backfillable.class) || hasType(hint, ApplyScopeData.class);
   }

   @FunctionalInterface
   public interface SentryConsumer<T> {
      void accept(@NotNull T var1);
   }

   @FunctionalInterface
   public interface SentryHintFallback {
      void accept(@Nullable Object var1, @NotNull Class<?> var2);
   }

   @FunctionalInterface
   public interface SentryNullableConsumer<T> {
      void accept(@Nullable T var1);
   }
}
