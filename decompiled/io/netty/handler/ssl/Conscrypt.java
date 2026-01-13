package io.netty.handler.ssl;

import io.netty.util.internal.PlatformDependent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.net.ssl.SSLEngine;

final class Conscrypt {
   private static final Method IS_CONSCRYPT_SSLENGINE;

   static boolean isAvailable() {
      return IS_CONSCRYPT_SSLENGINE != null;
   }

   static boolean isEngineSupported(SSLEngine engine) {
      try {
         return IS_CONSCRYPT_SSLENGINE != null && (Boolean)IS_CONSCRYPT_SSLENGINE.invoke(null, engine);
      } catch (IllegalAccessException var2) {
         return false;
      } catch (InvocationTargetException var3) {
         throw new RuntimeException(var3);
      }
   }

   private Conscrypt() {
   }

   static {
      Method isConscryptSSLEngine = null;
      if (PlatformDependent.javaVersion() < 15 || PlatformDependent.isAndroid()) {
         try {
            Class<?> providerClass = Class.forName("org.conscrypt.OpenSSLProvider", true, PlatformDependent.getClassLoader(ConscryptAlpnSslEngine.class));
            providerClass.newInstance();
            Class<?> conscryptClass = Class.forName("org.conscrypt.Conscrypt", true, PlatformDependent.getClassLoader(ConscryptAlpnSslEngine.class));
            isConscryptSSLEngine = conscryptClass.getMethod("isConscrypt", SSLEngine.class);
         } catch (Throwable var3) {
         }
      }

      IS_CONSCRYPT_SSLENGINE = isConscryptSSLEngine;
   }
}
