package com.google.common.flogger.backend;

import com.google.common.flogger.backend.system.DefaultPlatform;
import java.lang.reflect.InvocationTargetException;

public final class PlatformProvider {
   private PlatformProvider() {
   }

   public static Platform getPlatform() {
      try {
         return DefaultPlatform.class.getDeclaredConstructor().newInstance();
      } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException | NoClassDefFoundError var1) {
         return null;
      }
   }
}
