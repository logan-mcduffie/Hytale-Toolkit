package io.sentry;

import java.lang.reflect.InvocationTargetException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class OptionsContainer<T> {
   @NotNull
   private final Class<T> clazz;

   @NotNull
   public static <T> OptionsContainer<T> create(@NotNull Class<T> clazz) {
      return new OptionsContainer<>(clazz);
   }

   private OptionsContainer(@NotNull Class<T> clazz) {
      this.clazz = clazz;
   }

   @NotNull
   public T createInstance() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
      return this.clazz.getDeclaredConstructor().newInstance();
   }
}
