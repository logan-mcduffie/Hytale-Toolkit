package io.sentry.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PropertiesProvider {
   @Nullable
   String getProperty(@NotNull String var1);

   @NotNull
   Map<String, String> getMap(@NotNull String var1);

   @NotNull
   default List<String> getList(@NotNull String property) {
      String value = this.getProperty(property);
      return value != null ? Arrays.asList(value.split(",")) : Collections.emptyList();
   }

   @Nullable
   default List<String> getListOrNull(@NotNull String property) {
      String value = this.getProperty(property);
      return value != null ? Arrays.asList(value.split(",")) : null;
   }

   @NotNull
   default String getProperty(@NotNull String property, @NotNull String defaultValue) {
      String result = this.getProperty(property);
      return result != null ? result : defaultValue;
   }

   @Nullable
   default Boolean getBooleanProperty(@NotNull String property) {
      String result = this.getProperty(property);
      return result != null ? Boolean.valueOf(result) : null;
   }

   @Nullable
   default Double getDoubleProperty(@NotNull String property) {
      String prop = this.getProperty(property);
      Double result = null;
      if (prop != null) {
         try {
            result = Double.valueOf(prop);
         } catch (NumberFormatException var5) {
         }
      }

      return result;
   }

   @Nullable
   default Long getLongProperty(@NotNull String property) {
      String prop = this.getProperty(property);
      Long result = null;
      if (prop != null) {
         try {
            result = Long.valueOf(prop);
         } catch (NumberFormatException var5) {
         }
      }

      return result;
   }
}
