package io.netty.channel.socket.nio;

import io.netty.channel.socket.SocketProtocolFamily;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ProtocolFamily;
import java.net.StandardProtocolFamily;
import java.nio.channels.Channel;
import java.nio.channels.spi.SelectorProvider;

final class SelectorProviderUtil {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SelectorProviderUtil.class);

   static Method findOpenMethod(String methodName) {
      if (PlatformDependent.javaVersion() >= 15) {
         try {
            return SelectorProvider.class.getMethod(methodName, ProtocolFamily.class);
         } catch (Throwable var2) {
            logger.debug("SelectorProvider.{}(ProtocolFamily) not available, will use default", methodName, var2);
         }
      }

      return null;
   }

   private static <C extends Channel> C newChannel(Method method, SelectorProvider provider, Object family) throws IOException {
      if (family != null && method != null) {
         try {
            return (C)method.invoke(provider, family);
         } catch (IllegalAccessException | InvocationTargetException var4) {
            throw new IOException(var4);
         }
      } else {
         return null;
      }
   }

   static <C extends Channel> C newChannel(Method method, SelectorProvider provider, SocketProtocolFamily family) throws IOException {
      return family != null ? newChannel(method, provider, family.toJdkFamily()) : null;
   }

   static <C extends Channel> C newDomainSocketChannel(Method method, SelectorProvider provider) throws IOException {
      return newChannel(method, provider, StandardProtocolFamily.valueOf("UNIX"));
   }

   private SelectorProviderUtil() {
   }
}
