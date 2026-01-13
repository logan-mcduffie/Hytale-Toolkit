package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.function.BiFunction;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

final class JdkAlpnSslUtils {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(JdkAlpnSslUtils.class);
   private static final Method SET_APPLICATION_PROTOCOLS;
   private static final Method GET_APPLICATION_PROTOCOL;
   private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL;
   private static final Method SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
   private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;

   private JdkAlpnSslUtils() {
   }

   static boolean supportsAlpn() {
      return GET_APPLICATION_PROTOCOL != null;
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

   static String getHandshakeApplicationProtocol(SSLEngine sslEngine) {
      try {
         return (String)GET_HANDSHAKE_APPLICATION_PROTOCOL.invoke(sslEngine);
      } catch (UnsupportedOperationException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new IllegalStateException(var3);
      }
   }

   static void setApplicationProtocols(SSLEngine engine, List<String> supportedProtocols) {
      SSLParameters parameters = engine.getSSLParameters();
      String[] protocolArray = supportedProtocols.toArray(EmptyArrays.EMPTY_STRINGS);

      try {
         SET_APPLICATION_PROTOCOLS.invoke(parameters, protocolArray);
      } catch (UnsupportedOperationException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new IllegalStateException(var6);
      }

      engine.setSSLParameters(parameters);
   }

   static void setHandshakeApplicationProtocolSelector(SSLEngine engine, BiFunction<SSLEngine, List<String>, String> selector) {
      try {
         SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(engine, selector);
      } catch (UnsupportedOperationException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new IllegalStateException(var4);
      }
   }

   static BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector(SSLEngine engine) {
      try {
         return (BiFunction<SSLEngine, List<String>, String>)GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(engine);
      } catch (UnsupportedOperationException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new IllegalStateException(var3);
      }
   }

   static {
      Method getHandshakeApplicationProtocol;
      Method getApplicationProtocol;
      Method setApplicationProtocols;
      Method setHandshakeApplicationProtocolSelector;
      Method getHandshakeApplicationProtocolSelector;
      try {
         SSLContext context = SSLContext.getInstance("TLS");
         context.init(null, null, null);
         SSLEngine engine = context.createSSLEngine();
         getHandshakeApplicationProtocol = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return SSLEngine.class.getMethod("getHandshakeApplicationProtocol");
            }
         });
         getHandshakeApplicationProtocol.invoke(engine);
         getApplicationProtocol = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return SSLEngine.class.getMethod("getApplicationProtocol");
            }
         });
         getApplicationProtocol.invoke(engine);
         setApplicationProtocols = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return SSLParameters.class.getMethod("setApplicationProtocols", String[].class);
            }
         });
         setApplicationProtocols.invoke(engine.getSSLParameters(), EmptyArrays.EMPTY_STRINGS);
         setHandshakeApplicationProtocolSelector = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return SSLEngine.class.getMethod("setHandshakeApplicationProtocolSelector", BiFunction.class);
            }
         });
         setHandshakeApplicationProtocolSelector.invoke(engine, new BiFunction<SSLEngine, List<String>, String>() {
            public String apply(SSLEngine sslEngine, List<String> strings) {
               return null;
            }
         });
         getHandshakeApplicationProtocolSelector = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return SSLEngine.class.getMethod("getHandshakeApplicationProtocolSelector");
            }
         });
         getHandshakeApplicationProtocolSelector.invoke(engine);
      } catch (Throwable var7) {
         int version = PlatformDependent.javaVersion();
         if (version >= 9) {
            logger.error("Unable to initialize JdkAlpnSslUtils, but the detected java version was: {}", version, var7);
         }

         getHandshakeApplicationProtocol = null;
         getApplicationProtocol = null;
         setApplicationProtocols = null;
         setHandshakeApplicationProtocolSelector = null;
         getHandshakeApplicationProtocolSelector = null;
      }

      GET_HANDSHAKE_APPLICATION_PROTOCOL = getHandshakeApplicationProtocol;
      GET_APPLICATION_PROTOCOL = getApplicationProtocol;
      SET_APPLICATION_PROTOCOLS = setApplicationProtocols;
      SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = setHandshakeApplicationProtocolSelector;
      GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = getHandshakeApplicationProtocolSelector;
   }
}
