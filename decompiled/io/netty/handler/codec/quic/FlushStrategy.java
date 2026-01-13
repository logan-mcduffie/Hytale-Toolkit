package io.netty.handler.codec.quic;

import io.netty.util.internal.ObjectUtil;

public interface FlushStrategy {
   FlushStrategy DEFAULT = afterNumBytes(27000);

   boolean shouldFlushNow(int var1, int var2);

   static FlushStrategy afterNumBytes(int bytes) {
      ObjectUtil.checkPositive(bytes, "bytes");
      return (numPackets, numBytes) -> numBytes > bytes;
   }

   static FlushStrategy afterNumPackets(int packets) {
      ObjectUtil.checkPositive(packets, "packets");
      return (numPackets, numBytes) -> numPackets > packets;
   }
}
