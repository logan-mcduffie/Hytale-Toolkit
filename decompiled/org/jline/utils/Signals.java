package org.jline.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Objects;

public final class Signals {
   private Signals() {
   }

   public static Object register(String name, Runnable handler) {
      Objects.requireNonNull(handler);
      return register(name, handler, handler.getClass().getClassLoader());
   }

   public static Object register(String name, Runnable handler, ClassLoader loader) {
      try {
         Class<?> signalHandlerClass = Class.forName("sun.misc.SignalHandler");
         Object signalHandler = Proxy.newProxyInstance(loader, new Class[]{signalHandlerClass}, (proxy, method, args) -> {
            if (method.getDeclaringClass() == Object.class) {
               if ("toString".equals(method.getName())) {
                  return handler.toString();
               }
            } else if (method.getDeclaringClass() == signalHandlerClass) {
               Log.trace(() -> "Calling handler " + toString(handler) + " for signal " + name);
               handler.run();
            }

            return null;
         });
         return doRegister(name, signalHandler);
      } catch (Exception var5) {
         Log.debug("Error registering handler for signal ", name, var5);
         return null;
      }
   }

   public static Object registerDefault(String name) {
      try {
         Class<?> signalHandlerClass = Class.forName("sun.misc.SignalHandler");
         return doRegister(name, signalHandlerClass.getField("SIG_DFL").get(null));
      } catch (Exception var2) {
         Log.debug("Error registering default handler for signal ", name, var2);
         return null;
      }
   }

   public static void unregister(String name, Object previous) {
      try {
         if (previous != null) {
            doRegister(name, previous);
         }
      } catch (Exception var3) {
         Log.debug("Error unregistering handler for signal ", name, var3);
      }
   }

   private static Object doRegister(String name, Object handler) throws Exception {
      Log.trace(() -> "Registering signal " + name + " with handler " + toString(handler));
      Class<?> signalClass = Class.forName("sun.misc.Signal");
      Constructor<?> constructor = signalClass.getConstructor(String.class);

      Object signal;
      try {
         signal = constructor.newInstance(name);
      } catch (InvocationTargetException var6) {
         if (var6.getCause() instanceof IllegalArgumentException) {
            Log.trace(() -> "Ignoring unsupported signal " + name);
         } else {
            Log.debug("Error registering handler for signal ", name, var6);
         }

         return null;
      }

      Class<?> signalHandlerClass = Class.forName("sun.misc.SignalHandler");
      return signalClass.getMethod("handle", signalClass, signalHandlerClass).invoke(null, signal, handler);
   }

   private static String toString(Object handler) {
      try {
         Class<?> signalHandlerClass = Class.forName("sun.misc.SignalHandler");
         if (handler == signalHandlerClass.getField("SIG_DFL").get(null)) {
            return "SIG_DFL";
         }

         if (handler == signalHandlerClass.getField("SIG_IGN").get(null)) {
            return "SIG_IGN";
         }
      } catch (Throwable var2) {
      }

      return handler != null ? handler.toString() : "null";
   }
}
