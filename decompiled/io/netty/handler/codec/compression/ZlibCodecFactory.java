package io.netty.handler.codec.compression;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class ZlibCodecFactory {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ZlibCodecFactory.class);
   private static final int DEFAULT_JDK_WINDOW_SIZE = 15;
   private static final int DEFAULT_JDK_MEM_LEVEL = 8;
   private static final boolean noJdkZlibDecoder = SystemPropertyUtil.getBoolean("io.netty.noJdkZlibDecoder", false);
   private static final boolean noJdkZlibEncoder = SystemPropertyUtil.getBoolean("io.netty.noJdkZlibEncoder", false);
   private static final boolean JZLIB_AVAILABLE;

   public static boolean isSupportingWindowSizeAndMemLevel() {
      return JZLIB_AVAILABLE;
   }

   public static ZlibEncoder newZlibEncoder(int compressionLevel) {
      return (ZlibEncoder)(noJdkZlibEncoder ? new JZlibEncoder(compressionLevel) : new JdkZlibEncoder(compressionLevel));
   }

   public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper) {
      return (ZlibEncoder)(noJdkZlibEncoder ? new JZlibEncoder(wrapper) : new JdkZlibEncoder(wrapper));
   }

   public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
      return (ZlibEncoder)(noJdkZlibEncoder ? new JZlibEncoder(wrapper, compressionLevel) : new JdkZlibEncoder(wrapper, compressionLevel));
   }

   public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper, int compressionLevel, int windowBits, int memLevel) {
      return (ZlibEncoder)(!noJdkZlibEncoder && windowBits == 15 && memLevel == 8
         ? new JdkZlibEncoder(wrapper, compressionLevel)
         : new JZlibEncoder(wrapper, compressionLevel, windowBits, memLevel));
   }

   public static ZlibEncoder newZlibEncoder(byte[] dictionary) {
      return (ZlibEncoder)(noJdkZlibEncoder ? new JZlibEncoder(dictionary) : new JdkZlibEncoder(dictionary));
   }

   public static ZlibEncoder newZlibEncoder(int compressionLevel, byte[] dictionary) {
      return (ZlibEncoder)(noJdkZlibEncoder ? new JZlibEncoder(compressionLevel, dictionary) : new JdkZlibEncoder(compressionLevel, dictionary));
   }

   public static ZlibEncoder newZlibEncoder(int compressionLevel, int windowBits, int memLevel, byte[] dictionary) {
      return (ZlibEncoder)(!noJdkZlibEncoder && windowBits == 15 && memLevel == 8
         ? new JdkZlibEncoder(compressionLevel, dictionary)
         : new JZlibEncoder(compressionLevel, windowBits, memLevel, dictionary));
   }

   @Deprecated
   public static ZlibDecoder newZlibDecoder() {
      return newZlibDecoder(0);
   }

   public static ZlibDecoder newZlibDecoder(int maxAllocation) {
      return (ZlibDecoder)(noJdkZlibDecoder ? new JZlibDecoder(maxAllocation) : new JdkZlibDecoder(true, maxAllocation));
   }

   @Deprecated
   public static ZlibDecoder newZlibDecoder(ZlibWrapper wrapper) {
      return newZlibDecoder(wrapper, 0);
   }

   public static ZlibDecoder newZlibDecoder(ZlibWrapper wrapper, int maxAllocation) {
      return (ZlibDecoder)(noJdkZlibDecoder ? new JZlibDecoder(wrapper, maxAllocation) : new JdkZlibDecoder(wrapper, true, maxAllocation));
   }

   @Deprecated
   public static ZlibDecoder newZlibDecoder(byte[] dictionary) {
      return newZlibDecoder(dictionary, 0);
   }

   public static ZlibDecoder newZlibDecoder(byte[] dictionary, int maxAllocation) {
      return (ZlibDecoder)(noJdkZlibDecoder ? new JZlibDecoder(dictionary, maxAllocation) : new JdkZlibDecoder(dictionary, maxAllocation));
   }

   private ZlibCodecFactory() {
   }

   static {
      logger.debug("-Dio.netty.noJdkZlibDecoder: {}", noJdkZlibDecoder);
      logger.debug("-Dio.netty.noJdkZlibEncoder: {}", noJdkZlibEncoder);

      boolean jzlibAvailable;
      try {
         Class.forName("com.jcraft.jzlib.JZlib", false, PlatformDependent.getClassLoader(ZlibCodecFactory.class));
         jzlibAvailable = true;
      } catch (ClassNotFoundException var2) {
         jzlibAvailable = false;
         logger.debug("JZlib not in the classpath; the only window bits supported value will be 15");
      }

      JZLIB_AVAILABLE = jzlibAvailable;
   }
}
