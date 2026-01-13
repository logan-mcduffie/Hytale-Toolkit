package io.netty.channel.epoll;

import io.netty.channel.ChannelException;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.SocketProtocolFamily;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.NativeInetAddress;
import io.netty.channel.unix.PeerCredentials;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.SocketUtils;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public final class LinuxSocket extends Socket {
   private static final long MAX_UINT32_T = 4294967295L;

   LinuxSocket(int fd) {
      super(fd);
   }

   SocketProtocolFamily family() {
      return this.ipv6 ? SocketProtocolFamily.INET6 : SocketProtocolFamily.INET;
   }

   int sendmmsg(NativeDatagramPacketArray.NativeDatagramPacket[] msgs, int offset, int len) throws IOException {
      return Native.sendmmsg(this.intValue(), this.ipv6, msgs, offset, len);
   }

   int recvmmsg(NativeDatagramPacketArray.NativeDatagramPacket[] msgs, int offset, int len) throws IOException {
      return Native.recvmmsg(this.intValue(), this.ipv6, msgs, offset, len);
   }

   int recvmsg(NativeDatagramPacketArray.NativeDatagramPacket msg) throws IOException {
      return Native.recvmsg(this.intValue(), this.ipv6, msg);
   }

   void setTimeToLive(int ttl) throws IOException {
      setTimeToLive(this.intValue(), ttl);
   }

   void setInterface(InetAddress address) throws IOException {
      NativeInetAddress a = NativeInetAddress.newInstance(address);
      setInterface(this.intValue(), this.ipv6, a.address(), a.scopeId(), interfaceIndex(address));
   }

   void setNetworkInterface(NetworkInterface netInterface) throws IOException {
      InetAddress address = deriveInetAddress(netInterface, this.family() == SocketProtocolFamily.INET6);
      if (address.equals(this.family() == SocketProtocolFamily.INET ? Native.INET_ANY : Native.INET6_ANY)) {
         throw new IOException("NetworkInterface does not support " + this.family());
      } else {
         NativeInetAddress nativeAddress = NativeInetAddress.newInstance(address);
         setInterface(this.intValue(), this.ipv6, nativeAddress.address(), nativeAddress.scopeId(), interfaceIndex(netInterface));
      }
   }

   InetAddress getInterface() throws IOException {
      NetworkInterface inf = this.getNetworkInterface();
      if (inf != null) {
         Enumeration<InetAddress> addresses = SocketUtils.addressesFromNetworkInterface(inf);
         if (addresses.hasMoreElements()) {
            return addresses.nextElement();
         }
      }

      return null;
   }

   NetworkInterface getNetworkInterface() throws IOException {
      int ret = getInterface(this.intValue(), this.ipv6);
      if (this.ipv6) {
         return NetworkInterface.getByIndex(ret);
      } else {
         InetAddress address = inetAddress(ret);
         return address != null ? NetworkInterface.getByInetAddress(address) : null;
      }
   }

   private static InetAddress inetAddress(int value) {
      byte[] var1 = new byte[]{(byte)(value >>> 24 & 0xFF), (byte)(value >>> 16 & 0xFF), (byte)(value >>> 8 & 0xFF), (byte)(value & 0xFF)};

      try {
         return InetAddress.getByAddress(var1);
      } catch (UnknownHostException var3) {
         return null;
      }
   }

   void joinGroup(InetAddress group, NetworkInterface netInterface, InetAddress source) throws IOException {
      NativeInetAddress g = NativeInetAddress.newInstance(group);
      boolean isIpv6 = group instanceof Inet6Address;
      NativeInetAddress i = NativeInetAddress.newInstance(deriveInetAddress(netInterface, isIpv6));
      if (source != null) {
         if (source.getClass() != group.getClass()) {
            throw new IllegalArgumentException("Source address is different type to group");
         }

         NativeInetAddress s = NativeInetAddress.newInstance(source);
         joinSsmGroup(this.intValue(), this.ipv6 && isIpv6, g.address(), i.address(), g.scopeId(), interfaceIndex(netInterface), s.address());
      } else {
         joinGroup(this.intValue(), this.ipv6 && isIpv6, g.address(), i.address(), g.scopeId(), interfaceIndex(netInterface));
      }
   }

   void leaveGroup(InetAddress group, NetworkInterface netInterface, InetAddress source) throws IOException {
      NativeInetAddress g = NativeInetAddress.newInstance(group);
      boolean isIpv6 = group instanceof Inet6Address;
      NativeInetAddress i = NativeInetAddress.newInstance(deriveInetAddress(netInterface, isIpv6));
      if (source != null) {
         if (source.getClass() != group.getClass()) {
            throw new IllegalArgumentException("Source address is different type to group");
         }

         NativeInetAddress s = NativeInetAddress.newInstance(source);
         leaveSsmGroup(this.intValue(), this.ipv6 && isIpv6, g.address(), i.address(), g.scopeId(), interfaceIndex(netInterface), s.address());
      } else {
         leaveGroup(this.intValue(), this.ipv6 && isIpv6, g.address(), i.address(), g.scopeId(), interfaceIndex(netInterface));
      }
   }

   private static int interfaceIndex(NetworkInterface networkInterface) {
      return networkInterface.getIndex();
   }

   private static int interfaceIndex(InetAddress address) throws IOException {
      NetworkInterface iface = NetworkInterface.getByInetAddress(address);
      return iface != null ? iface.getIndex() : -1;
   }

   void setTcpDeferAccept(int deferAccept) throws IOException {
      setTcpDeferAccept(this.intValue(), deferAccept);
   }

   void setTcpQuickAck(boolean quickAck) throws IOException {
      setTcpQuickAck(this.intValue(), quickAck ? 1 : 0);
   }

   void setTcpCork(boolean tcpCork) throws IOException {
      setTcpCork(this.intValue(), tcpCork ? 1 : 0);
   }

   void setSoBusyPoll(int loopMicros) throws IOException {
      setSoBusyPoll(this.intValue(), loopMicros);
   }

   void setTcpNotSentLowAt(long tcpNotSentLowAt) throws IOException {
      if (tcpNotSentLowAt >= 0L && tcpNotSentLowAt <= 4294967295L) {
         setTcpNotSentLowAt(this.intValue(), (int)tcpNotSentLowAt);
      } else {
         throw new IllegalArgumentException("tcpNotSentLowAt must be a uint32_t");
      }
   }

   void setTcpFastOpen(int tcpFastopenBacklog) throws IOException {
      setTcpFastOpen(this.intValue(), tcpFastopenBacklog);
   }

   void setTcpKeepIdle(int seconds) throws IOException {
      setTcpKeepIdle(this.intValue(), seconds);
   }

   void setTcpKeepIntvl(int seconds) throws IOException {
      setTcpKeepIntvl(this.intValue(), seconds);
   }

   void setTcpKeepCnt(int probes) throws IOException {
      setTcpKeepCnt(this.intValue(), probes);
   }

   void setTcpUserTimeout(int milliseconds) throws IOException {
      setTcpUserTimeout(this.intValue(), milliseconds);
   }

   void setIpBindAddressNoPort(boolean enabled) throws IOException {
      setIpBindAddressNoPort(this.intValue(), enabled ? 1 : 0);
   }

   void setIpMulticastAll(boolean enabled) throws IOException {
      setIpMulticastAll(this.intValue(), this.ipv6, enabled ? 1 : 0);
   }

   void setIpFreeBind(boolean enabled) throws IOException {
      setIpFreeBind(this.intValue(), enabled ? 1 : 0);
   }

   void setIpTransparent(boolean enabled) throws IOException {
      setIpTransparent(this.intValue(), enabled ? 1 : 0);
   }

   void setIpRecvOrigDestAddr(boolean enabled) throws IOException {
      setIpRecvOrigDestAddr(this.intValue(), enabled ? 1 : 0);
   }

   int getTimeToLive() throws IOException {
      return getTimeToLive(this.intValue());
   }

   void getTcpInfo(EpollTcpInfo info) throws IOException {
      getTcpInfo(this.intValue(), info.info);
   }

   void setTcpMd5Sig(InetAddress address, byte[] key) throws IOException {
      NativeInetAddress a = NativeInetAddress.newInstance(address);
      setTcpMd5Sig(this.intValue(), this.ipv6, a.address(), a.scopeId(), key);
   }

   boolean isTcpCork() throws IOException {
      return isTcpCork(this.intValue()) != 0;
   }

   int getSoBusyPoll() throws IOException {
      return getSoBusyPoll(this.intValue());
   }

   int getTcpDeferAccept() throws IOException {
      return getTcpDeferAccept(this.intValue());
   }

   boolean isTcpQuickAck() throws IOException {
      return isTcpQuickAck(this.intValue()) != 0;
   }

   long getTcpNotSentLowAt() throws IOException {
      return getTcpNotSentLowAt(this.intValue()) & 4294967295L;
   }

   int getTcpKeepIdle() throws IOException {
      return getTcpKeepIdle(this.intValue());
   }

   int getTcpKeepIntvl() throws IOException {
      return getTcpKeepIntvl(this.intValue());
   }

   int getTcpKeepCnt() throws IOException {
      return getTcpKeepCnt(this.intValue());
   }

   int getTcpUserTimeout() throws IOException {
      return getTcpUserTimeout(this.intValue());
   }

   boolean isIpBindAddressNoPort() throws IOException {
      return isIpBindAddressNoPort(this.intValue()) != 0;
   }

   boolean isIpMulticastAll() throws IOException {
      return isIpMulticastAll(this.intValue(), this.ipv6) != 0;
   }

   boolean isIpFreeBind() throws IOException {
      return isIpFreeBind(this.intValue()) != 0;
   }

   boolean isIpTransparent() throws IOException {
      return isIpTransparent(this.intValue()) != 0;
   }

   boolean isIpRecvOrigDestAddr() throws IOException {
      return isIpRecvOrigDestAddr(this.intValue()) != 0;
   }

   PeerCredentials getPeerCredentials() throws IOException {
      return getPeerCredentials(this.intValue());
   }

   boolean isLoopbackModeDisabled() throws IOException {
      return getIpMulticastLoop(this.intValue(), this.ipv6) == 0;
   }

   void setLoopbackModeDisabled(boolean loopbackModeDisabled) throws IOException {
      setIpMulticastLoop(this.intValue(), this.ipv6, loopbackModeDisabled ? 0 : 1);
   }

   boolean isUdpGro() throws IOException {
      return isUdpGro(this.intValue()) != 0;
   }

   void setUdpGro(boolean gro) throws IOException {
      setUdpGro(this.intValue(), gro ? 1 : 0);
   }

   long sendFile(DefaultFileRegion src, long baseOffset, long offset, long length) throws IOException {
      src.open();
      long res = sendFile(this.intValue(), src, baseOffset, offset, length);
      return res >= 0L ? res : Errors.ioResult("sendfile", (int)res);
   }

   public void bindVSock(VSockAddress address) throws IOException {
      int res = bindVSock(this.intValue(), address.getCid(), address.getPort());
      if (res < 0) {
         throw Errors.newIOException("bindVSock", res);
      }
   }

   public boolean connectVSock(VSockAddress address) throws IOException {
      int res = connectVSock(this.intValue(), address.getCid(), address.getPort());
      return res < 0 ? Errors.handleConnectErrno("connectVSock", res) : true;
   }

   public VSockAddress remoteVSockAddress() {
      byte[] addr = remoteVSockAddress(this.intValue());
      if (addr == null) {
         return null;
      } else {
         int cid = getIntAt(addr, 0);
         int port = getIntAt(addr, 4);
         return new VSockAddress(cid, port);
      }
   }

   public VSockAddress localVSockAddress() {
      byte[] addr = localVSockAddress(this.intValue());
      if (addr == null) {
         return null;
      } else {
         int cid = getIntAt(addr, 0);
         int port = getIntAt(addr, 4);
         return new VSockAddress(cid, port);
      }
   }

   private static int getIntAt(byte[] array, int startIndex) {
      return array[startIndex] << 24 | (array[startIndex + 1] & 0xFF) << 16 | (array[startIndex + 2] & 0xFF) << 8 | array[startIndex + 3] & 0xFF;
   }

   private static InetAddress deriveInetAddress(NetworkInterface netInterface, boolean ipv6) {
      InetAddress ipAny = ipv6 ? Native.INET6_ANY : Native.INET_ANY;
      if (netInterface != null) {
         Enumeration<InetAddress> ias = netInterface.getInetAddresses();

         while (ias.hasMoreElements()) {
            InetAddress ia = ias.nextElement();
            boolean isV6 = ia instanceof Inet6Address;
            if (isV6 == ipv6) {
               return ia;
            }
         }
      }

      return ipAny;
   }

   public static LinuxSocket newSocket(int fd) {
      return new LinuxSocket(fd);
   }

   public static LinuxSocket newVSockStream() {
      return new LinuxSocket(newVSockStream0());
   }

   static int newVSockStream0() {
      int res = newVSockStreamFd();
      if (res < 0) {
         throw new ChannelException(Errors.newIOException("newVSockStream", res));
      } else {
         return res;
      }
   }

   public static LinuxSocket newSocketStream(boolean ipv6) {
      return new LinuxSocket(newSocketStream0(ipv6));
   }

   @Deprecated
   public static LinuxSocket newSocketStream(InternetProtocolFamily protocol) {
      return new LinuxSocket(newSocketStream0(protocol));
   }

   public static LinuxSocket newSocketStream(SocketProtocolFamily protocol) {
      return new LinuxSocket(newSocketStream0(protocol));
   }

   public static LinuxSocket newSocketStream() {
      return newSocketStream(isIPv6Preferred());
   }

   public static LinuxSocket newSocketDgram(boolean ipv6) {
      return new LinuxSocket(newSocketDgram0(ipv6));
   }

   @Deprecated
   public static LinuxSocket newSocketDgram(InternetProtocolFamily family) {
      return new LinuxSocket(newSocketDgram0(family));
   }

   public static LinuxSocket newSocketDgram(SocketProtocolFamily family) {
      return new LinuxSocket(newSocketDgram0(family));
   }

   public static LinuxSocket newSocketDgram() {
      return newSocketDgram(isIPv6Preferred());
   }

   public static LinuxSocket newSocketDomain() {
      return new LinuxSocket(newSocketDomain0());
   }

   public static LinuxSocket newSocketDomainDgram() {
      return new LinuxSocket(newSocketDomainDgram0());
   }

   private static native int newVSockStreamFd();

   private static native int bindVSock(int var0, int var1, int var2);

   private static native int connectVSock(int var0, int var1, int var2);

   private static native byte[] remoteVSockAddress(int var0);

   private static native byte[] localVSockAddress(int var0);

   private static native void joinGroup(int var0, boolean var1, byte[] var2, byte[] var3, int var4, int var5) throws IOException;

   private static native void joinSsmGroup(int var0, boolean var1, byte[] var2, byte[] var3, int var4, int var5, byte[] var6) throws IOException;

   private static native void leaveGroup(int var0, boolean var1, byte[] var2, byte[] var3, int var4, int var5) throws IOException;

   private static native void leaveSsmGroup(int var0, boolean var1, byte[] var2, byte[] var3, int var4, int var5, byte[] var6) throws IOException;

   private static native long sendFile(int var0, DefaultFileRegion var1, long var2, long var4, long var6) throws IOException;

   private static native int getTcpDeferAccept(int var0) throws IOException;

   private static native int isTcpQuickAck(int var0) throws IOException;

   private static native int isTcpCork(int var0) throws IOException;

   private static native int getSoBusyPoll(int var0) throws IOException;

   private static native int getTcpNotSentLowAt(int var0) throws IOException;

   private static native int getTcpKeepIdle(int var0) throws IOException;

   private static native int getTcpKeepIntvl(int var0) throws IOException;

   private static native int getTcpKeepCnt(int var0) throws IOException;

   private static native int getTcpUserTimeout(int var0) throws IOException;

   private static native int getTimeToLive(int var0) throws IOException;

   private static native int isIpBindAddressNoPort(int var0) throws IOException;

   private static native int isIpMulticastAll(int var0, boolean var1) throws IOException;

   private static native int isIpFreeBind(int var0) throws IOException;

   private static native int isIpTransparent(int var0) throws IOException;

   private static native int isIpRecvOrigDestAddr(int var0) throws IOException;

   private static native void getTcpInfo(int var0, long[] var1) throws IOException;

   private static native PeerCredentials getPeerCredentials(int var0) throws IOException;

   private static native void setTcpDeferAccept(int var0, int var1) throws IOException;

   private static native void setTcpQuickAck(int var0, int var1) throws IOException;

   private static native void setTcpCork(int var0, int var1) throws IOException;

   private static native void setSoBusyPoll(int var0, int var1) throws IOException;

   private static native void setTcpNotSentLowAt(int var0, int var1) throws IOException;

   private static native void setTcpFastOpen(int var0, int var1) throws IOException;

   private static native void setTcpKeepIdle(int var0, int var1) throws IOException;

   private static native void setTcpKeepIntvl(int var0, int var1) throws IOException;

   private static native void setTcpKeepCnt(int var0, int var1) throws IOException;

   private static native void setTcpUserTimeout(int var0, int var1) throws IOException;

   private static native void setIpBindAddressNoPort(int var0, int var1) throws IOException;

   private static native void setIpMulticastAll(int var0, boolean var1, int var2) throws IOException;

   private static native void setIpFreeBind(int var0, int var1) throws IOException;

   private static native void setIpTransparent(int var0, int var1) throws IOException;

   private static native void setIpRecvOrigDestAddr(int var0, int var1) throws IOException;

   private static native void setTcpMd5Sig(int var0, boolean var1, byte[] var2, int var3, byte[] var4) throws IOException;

   private static native void setInterface(int var0, boolean var1, byte[] var2, int var3, int var4) throws IOException;

   private static native int getInterface(int var0, boolean var1);

   private static native int getIpMulticastLoop(int var0, boolean var1) throws IOException;

   private static native void setIpMulticastLoop(int var0, boolean var1, int var2) throws IOException;

   private static native void setTimeToLive(int var0, int var1) throws IOException;

   private static native int isUdpGro(int var0) throws IOException;

   private static native void setUdpGro(int var0, int var1) throws IOException;
}
