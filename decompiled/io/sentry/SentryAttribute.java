package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryAttribute {
   @NotNull
   private final String name;
   @Nullable
   private final SentryAttributeType type;
   @Nullable
   private final Object value;

   private SentryAttribute(@NotNull String name, @Nullable SentryAttributeType type, @Nullable Object value) {
      this.name = name;
      this.type = type;
      this.value = value;
   }

   @NotNull
   public String getName() {
      return this.name;
   }

   @Nullable
   public SentryAttributeType getType() {
      return this.type;
   }

   @Nullable
   public Object getValue() {
      return this.value;
   }

   @NotNull
   public static SentryAttribute named(@NotNull String name, @Nullable Object value) {
      return new SentryAttribute(name, null, value);
   }

   @NotNull
   public static SentryAttribute booleanAttribute(@NotNull String name, @Nullable Boolean value) {
      return new SentryAttribute(name, SentryAttributeType.BOOLEAN, value);
   }

   @NotNull
   public static SentryAttribute integerAttribute(@NotNull String name, @Nullable Integer value) {
      return new SentryAttribute(name, SentryAttributeType.INTEGER, value);
   }

   @NotNull
   public static SentryAttribute doubleAttribute(@NotNull String name, @Nullable Double value) {
      return new SentryAttribute(name, SentryAttributeType.DOUBLE, value);
   }

   @NotNull
   public static SentryAttribute stringAttribute(@NotNull String name, @Nullable String value) {
      return new SentryAttribute(name, SentryAttributeType.STRING, value);
   }
}
