package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public abstract class SpdyHeaderBlockEncoder {
   static SpdyHeaderBlockEncoder newInstance(SpdyVersion version, int compressionLevel, int windowBits, int memLevel) {
      return new SpdyHeaderBlockZlibEncoder(version, compressionLevel);
   }

   abstract ByteBuf encode(ByteBufAllocator var1, SpdyHeadersFrame var2) throws Exception;

   abstract void end();
}
