package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public abstract class SpdyHeaderBlockDecoder {
   static SpdyHeaderBlockDecoder newInstance(SpdyVersion spdyVersion, int maxHeaderSize) {
      return new SpdyHeaderBlockZlibDecoder(spdyVersion, maxHeaderSize);
   }

   abstract void decode(ByteBufAllocator var1, ByteBuf var2, SpdyHeadersFrame var3) throws Exception;

   abstract void endHeaderBlock(SpdyHeadersFrame var1) throws Exception;

   abstract void end();
}
