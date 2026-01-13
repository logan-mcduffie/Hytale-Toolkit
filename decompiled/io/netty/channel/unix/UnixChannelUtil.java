package io.netty.channel.unix;

import io.netty.buffer.ByteBuf;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public final class UnixChannelUtil {
   private UnixChannelUtil() {
   }

   public static boolean isBufferCopyNeededForWrite(ByteBuf byteBuf) {
      return isBufferCopyNeededForWrite(byteBuf, Limits.IOV_MAX);
   }

   static boolean isBufferCopyNeededForWrite(ByteBuf byteBuf, int iovMax) {
      return !byteBuf.hasMemoryAddress() && (!byteBuf.isDirect() || byteBuf.nioBufferCount() > iovMax);
   }

   public static InetSocketAddress computeRemoteAddr(InetSocketAddress remoteAddr, InetSocketAddress osRemoteAddr) {
      if (osRemoteAddr != null) {
         try {
            return new InetSocketAddress(InetAddress.getByAddress(remoteAddr.getHostString(), osRemoteAddr.getAddress().getAddress()), osRemoteAddr.getPort());
         } catch (UnknownHostException var3) {
            return osRemoteAddr;
         }
      } else {
         return remoteAddr;
      }
   }
}
