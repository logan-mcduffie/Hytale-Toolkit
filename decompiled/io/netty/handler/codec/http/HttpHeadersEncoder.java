package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

final class HttpHeadersEncoder {
   private static final int COLON_AND_SPACE_SHORT = 14880;

   private HttpHeadersEncoder() {
   }

   static void encoderHeader(CharSequence name, CharSequence value, ByteBuf buf) {
      int nameLen = name.length();
      int valueLen = value.length();
      int entryLen = nameLen + valueLen + 4;
      buf.ensureWritable(entryLen);
      int offset = buf.writerIndex();
      writeAscii(buf, offset, name);
      offset += nameLen;
      ByteBufUtil.setShortBE(buf, offset, 14880);
      offset += 2;
      writeAscii(buf, offset, value);
      offset += valueLen;
      ByteBufUtil.setShortBE(buf, offset, 3338);
      offset += 2;
      buf.writerIndex(offset);
   }

   private static void writeAscii(ByteBuf buf, int offset, CharSequence value) {
      if (value instanceof AsciiString) {
         ByteBufUtil.copy((AsciiString)value, 0, buf, offset, value.length());
      } else {
         buf.setCharSequence(offset, value, CharsetUtil.US_ASCII);
      }
   }
}
