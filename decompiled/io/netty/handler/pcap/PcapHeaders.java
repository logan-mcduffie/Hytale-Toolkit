package io.netty.handler.pcap;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.io.OutputStream;

final class PcapHeaders {
   private static final byte[] GLOBAL_HEADER = new byte[]{-95, -78, -61, -44, 0, 2, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 1};

   private PcapHeaders() {
   }

   static void writeGlobalHeader(OutputStream outputStream) throws IOException {
      outputStream.write(GLOBAL_HEADER);
   }

   static void writePacketHeader(ByteBuf byteBuf, int ts_sec, int ts_usec, int incl_len, int orig_len) {
      byteBuf.writeInt(ts_sec);
      byteBuf.writeInt(ts_usec);
      byteBuf.writeInt(incl_len);
      byteBuf.writeInt(orig_len);
   }
}
