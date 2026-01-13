package io.sentry;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface MeasurementUnit {
   @Internal
   String NONE = "none";

   @NotNull
   String name();

   @Internal
   @NotNull
   String apiName();

   public static final class Custom implements MeasurementUnit {
      @NotNull
      private final String name;

      public Custom(@NotNull String name) {
         this.name = name;
      }

      @NotNull
      @Override
      public String name() {
         return this.name;
      }

      @NotNull
      @Override
      public String apiName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }

   public static enum Duration implements MeasurementUnit {
      NANOSECOND,
      MICROSECOND,
      MILLISECOND,
      SECOND,
      MINUTE,
      HOUR,
      DAY,
      WEEK;

      @NotNull
      @Override
      public String apiName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }

   public static enum Fraction implements MeasurementUnit {
      RATIO,
      PERCENT;

      @NotNull
      @Override
      public String apiName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }

   public static enum Information implements MeasurementUnit {
      BIT,
      BYTE,
      KILOBYTE,
      KIBIBYTE,
      MEGABYTE,
      MEBIBYTE,
      GIGABYTE,
      GIBIBYTE,
      TERABYTE,
      TEBIBYTE,
      PETABYTE,
      PEBIBYTE,
      EXABYTE,
      EXBIBYTE;

      @NotNull
      @Override
      public String apiName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
