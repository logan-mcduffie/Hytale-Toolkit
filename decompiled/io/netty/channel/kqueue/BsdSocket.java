package io.netty.channel.kqueue;

import io.netty.channel.DefaultFileRegion;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.SocketProtocolFamily;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.NativeInetAddress;
import io.netty.channel.unix.PeerCredentials;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

final class BsdSocket extends Socket {
   private static final int APPLE_SND_LOW_AT_MAX = 131072;
   private static final int FREEBSD_SND_LOW_AT_MAX = 32768;
   static final int BSD_SND_LOW_AT_MAX = Math.min(131072, 32768);
   private static final int UNSPECIFIED_SOURCE_INTERFACE = 0;

   BsdSocket(int fd) {
      super(fd);
   }

   void setAcceptFilter(AcceptFilter acceptFilter) throws IOException {
      setAcceptFilter(this.intValue(), acceptFilter.filterName(), acceptFilter.filterArgs());
   }

   void setTcpNoPush(boolean tcpNoPush) throws IOException {
      setTcpNoPush(this.intValue(), tcpNoPush ? 1 : 0);
   }

   void setSndLowAt(int lowAt) throws IOException {
      setSndLowAt(this.intValue(), lowAt);
   }

   public void setTcpFastOpen(boolean enableTcpFastOpen) throws IOException {
      setTcpFastOpen(this.intValue(), enableTcpFastOpen ? 1 : 0);
   }

   boolean isTcpNoPush() throws IOException {
      return getTcpNoPush(this.intValue()) != 0;
   }

   int getSndLowAt() throws IOException {
      return getSndLowAt(this.intValue());
   }

   AcceptFilter getAcceptFilter() throws IOException {
      String[] result = getAcceptFilter(this.intValue());
      return result == null ? AcceptFilter.PLATFORM_UNSUPPORTED : new AcceptFilter(result[0], result[1]);
   }

   public boolean isTcpFastOpen() throws IOException {
      return isTcpFastOpen(this.intValue()) != 0;
   }

   PeerCredentials getPeerCredentials() throws IOException {
      return getPeerCredentials(this.intValue());
   }

   long sendFile(DefaultFileRegion src, long baseOffset, long offset, long length) throws IOException {
      src.open();
      long res = sendFile(this.intValue(), src, baseOffset, offset, length);
      return res >= 0L ? res : Errors.ioResult("sendfile", (int)res);
   }

   int connectx(InetSocketAddress source, InetSocketAddress destination, IovArray data, boolean tcpFastOpen) throws IOException {
      ObjectUtil.checkNotNull(destination, "Destination InetSocketAddress cannot be null.");
      int flags = tcpFastOpen ? Native.CONNECT_TCP_FASTOPEN : 0;
      boolean sourceIPv6;
      byte[] sourceAddress;
      int sourceScopeId;
      int sourcePort;
      if (source == null) {
         sourceIPv6 = false;
         sourceAddress = null;
         sourceScopeId = 0;
         sourcePort = 0;
      } else {
         InetAddress sourceInetAddress = source.getAddress();
         sourceIPv6 = useIpv6(this, sourceInetAddress);
         if (sourceInetAddress instanceof Inet6Address) {
            sourceAddress = sourceInetAddress.getAddress();
            sourceScopeId = ((Inet6Address)sourceInetAddress).getScopeId();
         } else {
            sourceScopeId = 0;
            sourceAddress = NativeInetAddress.ipv4MappedIpv6Address(sourceInetAddress.getAddress());
         }

         sourcePort = source.getPort();
      }

      InetAddress destinationInetAddress = destination.getAddress();
      boolean destinationIPv6 = useIpv6(this, destinationInetAddress);
      byte[] destinationAddress;
      int destinationScopeId;
      if (destinationInetAddress instanceof Inet6Address) {
         destinationAddress = destinationInetAddress.getAddress();
         destinationScopeId = ((Inet6Address)destinationInetAddress).getScopeId();
      } else {
         destinationScopeId = 0;
         destinationAddress = NativeInetAddress.ipv4MappedIpv6Address(destinationInetAddress.getAddress());
      }

      int destinationPort = destination.getPort();
      long iovAddress;
      int iovCount;
      int iovDataLength;
      if (data != null && data.count() != 0) {
         iovAddress = data.memoryAddress(0);
         iovCount = data.count();
         long size = data.size();
         if (size > 2147483647L) {
            throw new IOException("IovArray.size() too big: " + size + " bytes.");
         }

         iovDataLength = (int)size;
      } else {
         iovAddress = 0L;
         iovCount = 0;
         iovDataLength = 0;
      }

      int result = connectx(
         this.intValue(),
         0,
         sourceIPv6,
         sourceAddress,
         sourceScopeId,
         sourcePort,
         destinationIPv6,
         destinationAddress,
         destinationScopeId,
         destinationPort,
         flags,
         iovAddress,
         iovCount,
         iovDataLength
      );
      if (result == Errors.ERRNO_EINPROGRESS_NEGATIVE) {
         return -iovDataLength;
      } else {
         return result < 0 ? Errors.ioResult("connectx", result) : result;
      }
   }

   public static BsdSocket newSocketStream() {
      return new BsdSocket(newSocketStream0());
   }

   /** @deprecated */
   public static BsdSocket newSocketStream(InternetProtocolFamily protocol) {
      return new BsdSocket(newSocketStream0(protocol));
   }

   public static BsdSocket newSocketStream(SocketProtocolFamily protocol) {
      return new BsdSocket(newSocketStream0(protocol));
   }

   public static BsdSocket newSocketDgram() {
      return new BsdSocket(newSocketDgram0());
   }

   /** @deprecated */
   public static BsdSocket newSocketDgram(InternetProtocolFamily protocol) {
      return new BsdSocket(newSocketDgram0(protocol));
   }

   public static BsdSocket newSocketDgram(SocketProtocolFamily protocol) {
      return new BsdSocket(newSocketDgram0(protocol));
   }

   public static BsdSocket newSocketDomain() {
      return new BsdSocket(newSocketDomain0());
   }

   public static BsdSocket newSocketDomainDgram() {
      return new BsdSocket(newSocketDomainDgram0());
   }

   private static native long sendFile(int var0, DefaultFileRegion var1, long var2, long var4, long var6) throws IOException;

   private static native int connectx(
      int var0,
      int var1,
      boolean var2,
      byte[] var3,
      int var4,
      int var5,
      boolean var6,
      byte[] var7,
      int var8,
      int var9,
      int var10,
      long var11,
      int var13,
      int var14
   );

   private static native String[] getAcceptFilter(int var0) throws IOException;

   private static native int getTcpNoPush(int var0) throws IOException;

   private static native int getSndLowAt(int var0) throws IOException;

   private static native int isTcpFastOpen(int var0) throws IOException;

   private static native PeerCredentials getPeerCredentials(int var0) throws IOException;

   private static native void setAcceptFilter(int var0, String var1, String var2) throws IOException;

   private static native void setTcpNoPush(int var0, int var1) throws IOException;

   private static native void setSndLowAt(int var0, int var1) throws IOException;

   private static native void setTcpFastOpen(int var0, int var1) throws IOException;
}
