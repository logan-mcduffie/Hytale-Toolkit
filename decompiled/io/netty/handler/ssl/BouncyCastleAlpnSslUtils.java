package io.netty.handler.ssl;

import io.netty.handler.ssl.util.BouncyCastleUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.security.SecureRandom;
import java.util.List;
import java.util.function.BiFunction;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

final class BouncyCastleAlpnSslUtils {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(BouncyCastleAlpnSslUtils.class);
   private static final Method SET_APPLICATION_PROTOCOLS;
   private static final Method GET_APPLICATION_PROTOCOL;
   private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL;
   private static final Method SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
   private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
   private static final Class<?> BC_APPLICATION_PROTOCOL_SELECTOR;
   private static final Method BC_APPLICATION_PROTOCOL_SELECTOR_SELECT;
   private static final boolean SUPPORTED;

   private BouncyCastleAlpnSslUtils() {
   }

   static String getApplicationProtocol(SSLEngine sslEngine) {
      try {
         return (String)GET_APPLICATION_PROTOCOL.invoke(sslEngine);
      } catch (UnsupportedOperationException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new IllegalStateException(var3);
      }
   }

   static void setApplicationProtocols(SSLEngine engine, List<String> supportedProtocols) {
      String[] protocolArray = supportedProtocols.toArray(EmptyArrays.EMPTY_STRINGS);

      try {
         SSLParameters bcSslParameters = engine.getSSLParameters();
         SET_APPLICATION_PROTOCOLS.invoke(bcSslParameters, protocolArray);
         engine.setSSLParameters(bcSslParameters);
      } catch (UnsupportedOperationException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new IllegalStateException(var5);
      }

      if (PlatformDependent.javaVersion() >= 9) {
         JdkAlpnSslUtils.setApplicationProtocols(engine, supportedProtocols);
      }
   }

   static String getHandshakeApplicationProtocol(SSLEngine sslEngine) {
      try {
         return (String)GET_HANDSHAKE_APPLICATION_PROTOCOL.invoke(sslEngine);
      } catch (UnsupportedOperationException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new IllegalStateException(var3);
      }
   }

   static void setHandshakeApplicationProtocolSelector(SSLEngine engine, final BiFunction<SSLEngine, List<String>, String> selector) {
      try {
         Object selectorProxyInstance = Proxy.newProxyInstance(
            BouncyCastleAlpnSslUtils.class.getClassLoader(), new Class[]{BC_APPLICATION_PROTOCOL_SELECTOR}, new InvocationHandler() {
               @Override
               public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                  if (method.getName().equals("select")) {
                     try {
                        return selector.apply((SSLEngine)args[0], (List<String>)args[1]);
                     } catch (ClassCastException var5) {
                        throw new RuntimeException("BCApplicationProtocolSelector select method parameter of invalid type.", var5);
                     }
                  } else {
                     throw new UnsupportedOperationException(String.format("Method '%s' not supported.", method.getName()));
                  }
               }
            }
         );
         SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(engine, selectorProxyInstance);
      } catch (UnsupportedOperationException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new IllegalStateException(var4);
      }
   }

   static BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector(SSLEngine engine) {
      try {
         Object selector = GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(engine);
         return (sslEngine, strings) -> {
            try {
               return (String)BC_APPLICATION_PROTOCOL_SELECTOR_SELECT.invoke(selector, sslEngine, strings);
            } catch (Exception var4) {
               throw new RuntimeException("Could not call getHandshakeApplicationProtocolSelector", var4);
            }
         };
      } catch (UnsupportedOperationException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new IllegalStateException(var3);
      }
   }

   static boolean isAlpnSupported() {
      return SUPPORTED;
   }

   static {
      Method setApplicationProtocols;
      Method getApplicationProtocol;
      Method getHandshakeApplicationProtocol;
      Method setHandshakeApplicationProtocolSelector;
      Method getHandshakeApplicationProtocolSelector;
      Method bcApplicationProtocolSelectorSelect;
      Class<?> bcApplicationProtocolSelector;
      boolean supported;
      try {
         if (!BouncyCastleUtil.isBcTlsAvailable()) {
            throw new IllegalStateException(BouncyCastleUtil.unavailabilityCauseBcTls());
         }

         SSLContext context = SslUtils.getSSLContext(BouncyCastleUtil.getBcProviderJsse(), new SecureRandom());
         SSLEngine engine = context.createSSLEngine();
         Class<? extends SSLEngine> engineClass = (Class<? extends SSLEngine>)engine.getClass();
         final Class<? extends SSLEngine> bcEngineClass = BouncyCastleUtil.getBcSSLEngineClass();
         if (bcEngineClass == null || !bcEngineClass.isAssignableFrom(engineClass)) {
            throw new IllegalStateException("Unexpected engine class: " + engineClass);
         }

         SSLParameters bcSslParameters = engine.getSSLParameters();
         final Class<?> bCSslParametersClass = bcSslParameters.getClass();
         setApplicationProtocols = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return bCSslParametersClass.getMethod("setApplicationProtocols", String[].class);
            }
         });
         setApplicationProtocols.invoke(bcSslParameters, EmptyArrays.EMPTY_STRINGS);
         getApplicationProtocol = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return bcEngineClass.getMethod("getApplicationProtocol");
            }
         });
         getApplicationProtocol.invoke(engine);
         getHandshakeApplicationProtocol = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return bcEngineClass.getMethod("getHandshakeApplicationProtocol");
            }
         });
         getHandshakeApplicationProtocol.invoke(engine);
         final Class<?> testBCApplicationProtocolSelector = Class.forName(
            "org.bouncycastle.jsse.BCApplicationProtocolSelector", true, engineClass.getClassLoader()
         );
         bcApplicationProtocolSelector = testBCApplicationProtocolSelector;
         bcApplicationProtocolSelectorSelect = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return testBCApplicationProtocolSelector.getMethod("select", Object.class, List.class);
            }
         });
         setHandshakeApplicationProtocolSelector = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return bcEngineClass.getMethod("setBCHandshakeApplicationProtocolSelector", testBCApplicationProtocolSelector);
            }
         });
         getHandshakeApplicationProtocolSelector = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return bcEngineClass.getMethod("getBCHandshakeApplicationProtocolSelector");
            }
         });
         getHandshakeApplicationProtocolSelector.invoke(engine);
         supported = true;
      } catch (Throwable var15) {
         logger.error("Unable to initialize BouncyCastleAlpnSslUtils.", var15);
         setApplicationProtocols = null;
         getApplicationProtocol = null;
         getHandshakeApplicationProtocol = null;
         setHandshakeApplicationProtocolSelector = null;
         getHandshakeApplicationProtocolSelector = null;
         bcApplicationProtocolSelectorSelect = null;
         bcApplicationProtocolSelector = null;
         supported = false;
      }

      SET_APPLICATION_PROTOCOLS = setApplicationProtocols;
      GET_APPLICATION_PROTOCOL = getApplicationProtocol;
      GET_HANDSHAKE_APPLICATION_PROTOCOL = getHandshakeApplicationProtocol;
      SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = setHandshakeApplicationProtocolSelector;
      GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = getHandshakeApplicationProtocolSelector;
      BC_APPLICATION_PROTOCOL_SELECTOR_SELECT = bcApplicationProtocolSelectorSelect;
      BC_APPLICATION_PROTOCOL_SELECTOR = bcApplicationProtocolSelector;
      SUPPORTED = supported;
   }
}
