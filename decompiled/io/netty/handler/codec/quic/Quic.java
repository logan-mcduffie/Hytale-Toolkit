package io.netty.handler.codec.quic;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.util.AttributeKey;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;

public final class Quic {
   static final Entry<ChannelOption<?>, Object>[] EMPTY_OPTION_ARRAY = new Entry[0];
   static final Entry<AttributeKey<?>, Object>[] EMPTY_ATTRIBUTE_ARRAY = new Entry[0];
   static final int MAX_DATAGRAM_SIZE = 1350;
   static final int RESET_TOKEN_LEN = 16;
   private static final Throwable UNAVAILABILITY_CAUSE;
   public static final int MAX_CONN_ID_LEN = 20;

   public static boolean isVersionSupported(int version) {
      return isAvailable() && Quiche.quiche_version_is_supported(version);
   }

   public static boolean isAvailable() {
      return UNAVAILABILITY_CAUSE == null;
   }

   public static void ensureAvailability() {
      if (UNAVAILABILITY_CAUSE != null) {
         throw (Error)new UnsatisfiedLinkError("failed to load the required native library").initCause(UNAVAILABILITY_CAUSE);
      }
   }

   @Nullable
   public static Throwable unavailabilityCause() {
      return UNAVAILABILITY_CAUSE;
   }

   static Entry<ChannelOption<?>, Object>[] toOptionsArray(Map<ChannelOption<?>, Object> opts) {
      return new HashMap<>(opts).entrySet().toArray(EMPTY_OPTION_ARRAY);
   }

   static Entry<AttributeKey<?>, Object>[] toAttributesArray(Map<AttributeKey<?>, Object> attributes) {
      return new LinkedHashMap<>(attributes).entrySet().toArray(EMPTY_ATTRIBUTE_ARRAY);
   }

   private static void setAttributes(Channel channel, Entry<AttributeKey<?>, Object>[] attrs) {
      for (Entry<AttributeKey<?>, Object> e : attrs) {
         AttributeKey<Object> key = (AttributeKey<Object>)e.getKey();
         channel.attr(key).set(e.getValue());
      }
   }

   private static void setChannelOptions(Channel channel, Entry<ChannelOption<?>, Object>[] options, InternalLogger logger) {
      for (Entry<ChannelOption<?>, Object> e : options) {
         setChannelOption(channel, e.getKey(), e.getValue(), logger);
      }
   }

   private static void setChannelOption(Channel channel, ChannelOption<?> option, Object value, InternalLogger logger) {
      try {
         if (!channel.config().setOption(option, value)) {
            logger.warn("Unknown channel option '{}' for channel '{}'", option, channel);
         }
      } catch (Throwable var5) {
         logger.warn("Failed to set channel option '{}' with value '{}' for channel '{}'", option, value, channel, var5);
      }
   }

   static <T> void updateOptions(Map<ChannelOption<?>, Object> options, ChannelOption<T> option, @Nullable T value) {
      ObjectUtil.checkNotNull(option, "option");
      if (value == null) {
         options.remove(option);
      } else {
         options.put(option, value);
      }
   }

   static <T> void updateAttributes(Map<AttributeKey<?>, Object> attributes, AttributeKey<T> key, @Nullable T value) {
      ObjectUtil.checkNotNull(key, "key");
      if (value == null) {
         attributes.remove(key);
      } else {
         attributes.put(key, value);
      }
   }

   static void setupChannel(
      Channel ch, Entry<ChannelOption<?>, Object>[] options, Entry<AttributeKey<?>, Object>[] attrs, @Nullable ChannelHandler handler, InternalLogger logger
   ) {
      setChannelOptions(ch, options, logger);
      setAttributes(ch, attrs);
      if (handler != null) {
         ch.pipeline().addLast(handler);
      }
   }

   private Quic() {
   }

   static {
      Throwable cause = null;

      try {
         String version = Quiche.quiche_version();

         assert version != null;
      } catch (Throwable var2) {
         cause = var2;
      }

      UNAVAILABILITY_CAUSE = cause;
   }
}
