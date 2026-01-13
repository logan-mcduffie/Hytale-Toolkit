package io.netty.handler.codec.compression;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.Inflater;
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.JZlib.WrapperType;

final class ZlibUtil {
   static void fail(Inflater z, String message, int resultCode) {
      throw inflaterException(z, message, resultCode);
   }

   static void fail(Deflater z, String message, int resultCode) {
      throw deflaterException(z, message, resultCode);
   }

   static DecompressionException inflaterException(Inflater z, String message, int resultCode) {
      return new DecompressionException(message + " (" + resultCode + ')' + (z.msg != null ? ": " + z.msg : ""));
   }

   static CompressionException deflaterException(Deflater z, String message, int resultCode) {
      return new CompressionException(message + " (" + resultCode + ')' + (z.msg != null ? ": " + z.msg : ""));
   }

   static WrapperType convertWrapperType(ZlibWrapper wrapper) {
      switch (wrapper) {
         case NONE:
            return JZlib.W_NONE;
         case ZLIB:
            return JZlib.W_ZLIB;
         case GZIP:
            return JZlib.W_GZIP;
         case ZLIB_OR_NONE:
            return JZlib.W_ANY;
         default:
            throw new Error("Unexpected wrapper type: " + wrapper);
      }
   }

   static int wrapperOverhead(ZlibWrapper wrapper) {
      switch (wrapper) {
         case NONE:
            return 0;
         case ZLIB:
         case ZLIB_OR_NONE:
            return 2;
         case GZIP:
            return 10;
         default:
            throw new Error("Unexpected wrapper type: " + wrapper);
      }
   }

   private ZlibUtil() {
   }
}
