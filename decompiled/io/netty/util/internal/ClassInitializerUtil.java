package io.netty.util.internal;

public final class ClassInitializerUtil {
   private ClassInitializerUtil() {
   }

   public static void tryLoadClasses(Class<?> loadingClass, Class<?>... classes) {
      ClassLoader loader = PlatformDependent.getClassLoader(loadingClass);

      for (Class<?> clazz : classes) {
         tryLoadClass(loader, clazz.getName());
      }
   }

   private static void tryLoadClass(ClassLoader classLoader, String className) {
      try {
         Class.forName(className, true, classLoader);
      } catch (SecurityException | ClassNotFoundException var3) {
      }
   }
}
