package io.netty.channel;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.WeakHashMap;

final class ChannelHandlerMask {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelHandlerMask.class);
   static final int MASK_EXCEPTION_CAUGHT = 1;
   static final int MASK_CHANNEL_REGISTERED = 2;
   static final int MASK_CHANNEL_UNREGISTERED = 4;
   static final int MASK_CHANNEL_ACTIVE = 8;
   static final int MASK_CHANNEL_INACTIVE = 16;
   static final int MASK_CHANNEL_READ = 32;
   static final int MASK_CHANNEL_READ_COMPLETE = 64;
   static final int MASK_USER_EVENT_TRIGGERED = 128;
   static final int MASK_CHANNEL_WRITABILITY_CHANGED = 256;
   static final int MASK_BIND = 512;
   static final int MASK_CONNECT = 1024;
   static final int MASK_DISCONNECT = 2048;
   static final int MASK_CLOSE = 4096;
   static final int MASK_DEREGISTER = 8192;
   static final int MASK_READ = 16384;
   static final int MASK_WRITE = 32768;
   static final int MASK_FLUSH = 65536;
   static final int MASK_ONLY_INBOUND = 510;
   private static final int MASK_ALL_INBOUND = 511;
   static final int MASK_ONLY_OUTBOUND = 130560;
   private static final int MASK_ALL_OUTBOUND = 130561;
   private static final FastThreadLocal<Map<Class<? extends ChannelHandler>, Integer>> MASKS = new FastThreadLocal<Map<Class<? extends ChannelHandler>, Integer>>() {
      protected Map<Class<? extends ChannelHandler>, Integer> initialValue() {
         return new WeakHashMap<>(32);
      }
   };

   static int mask(Class<? extends ChannelHandler> clazz) {
      Map<Class<? extends ChannelHandler>, Integer> cache = MASKS.get();
      Integer mask = cache.get(clazz);
      if (mask == null) {
         mask = mask0(clazz);
         cache.put(clazz, mask);
      }

      return mask;
   }

   private static int mask0(Class<? extends ChannelHandler> handlerType) {
      int mask = 1;

      try {
         if (ChannelInboundHandler.class.isAssignableFrom(handlerType)) {
            mask |= 511;
            if (isSkippable(handlerType, "channelRegistered", ChannelHandlerContext.class)) {
               mask &= -3;
            }

            if (isSkippable(handlerType, "channelUnregistered", ChannelHandlerContext.class)) {
               mask &= -5;
            }

            if (isSkippable(handlerType, "channelActive", ChannelHandlerContext.class)) {
               mask &= -9;
            }

            if (isSkippable(handlerType, "channelInactive", ChannelHandlerContext.class)) {
               mask &= -17;
            }

            if (isSkippable(handlerType, "channelRead", ChannelHandlerContext.class, Object.class)) {
               mask &= -33;
            }

            if (isSkippable(handlerType, "channelReadComplete", ChannelHandlerContext.class)) {
               mask &= -65;
            }

            if (isSkippable(handlerType, "channelWritabilityChanged", ChannelHandlerContext.class)) {
               mask &= -257;
            }

            if (isSkippable(handlerType, "userEventTriggered", ChannelHandlerContext.class, Object.class)) {
               mask &= -129;
            }
         }

         if (ChannelOutboundHandler.class.isAssignableFrom(handlerType)) {
            mask |= 130561;
            if (isSkippable(handlerType, "bind", ChannelHandlerContext.class, SocketAddress.class, ChannelPromise.class)) {
               mask &= -513;
            }

            if (isSkippable(handlerType, "connect", ChannelHandlerContext.class, SocketAddress.class, SocketAddress.class, ChannelPromise.class)) {
               mask &= -1025;
            }

            if (isSkippable(handlerType, "disconnect", ChannelHandlerContext.class, ChannelPromise.class)) {
               mask &= -2049;
            }

            if (isSkippable(handlerType, "close", ChannelHandlerContext.class, ChannelPromise.class)) {
               mask &= -4097;
            }

            if (isSkippable(handlerType, "deregister", ChannelHandlerContext.class, ChannelPromise.class)) {
               mask &= -8193;
            }

            if (isSkippable(handlerType, "read", ChannelHandlerContext.class)) {
               mask &= -16385;
            }

            if (isSkippable(handlerType, "write", ChannelHandlerContext.class, Object.class, ChannelPromise.class)) {
               mask &= -32769;
            }

            if (isSkippable(handlerType, "flush", ChannelHandlerContext.class)) {
               mask &= -65537;
            }
         }

         if (isSkippable(handlerType, "exceptionCaught", ChannelHandlerContext.class, Throwable.class)) {
            mask &= -2;
         }
      } catch (Exception var3) {
         PlatformDependent.throwException(var3);
      }

      return mask;
   }

   private static boolean isSkippable(final Class<?> handlerType, final String methodName, final Class<?>... paramTypes) throws Exception {
      return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
         public Boolean run() throws Exception {
            Method m;
            try {
               m = handlerType.getMethod(methodName, paramTypes);
            } catch (NoSuchMethodException var3) {
               if (ChannelHandlerMask.logger.isDebugEnabled()) {
                  ChannelHandlerMask.logger.debug("Class {} missing method {}, assume we can not skip execution", handlerType, methodName, var3);
               }

               return false;
            }

            return m.isAnnotationPresent(ChannelHandlerMask.Skip.class);
         }
      });
   }

   private ChannelHandlerMask() {
   }

   @Target(ElementType.METHOD)
   @Retention(RetentionPolicy.RUNTIME)
   @interface Skip {
   }
}
