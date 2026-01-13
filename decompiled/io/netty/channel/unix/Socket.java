package io.netty.channel.unix;

import io.netty.channel.ChannelException;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.SocketProtocolFamily;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

public class Socket extends FileDescriptor {
   private static volatile boolean isIpv6Preferred;
   @Deprecated
   public static final int UDS_SUN_PATH_SIZE = 100;
   protected final boolean ipv6;

   public Socket(int fd) {
      super(fd);
      this.ipv6 = isIPv6(fd);
   }

   private boolean useIpv6(InetAddress address) {
      return useIpv6(this, address);
   }

   protected static boolean useIpv6(Socket socket, InetAddress address) {
      return socket.ipv6 || address instanceof Inet6Address;
   }

   public final void shutdown() throws IOException {
      this.shutdown(true, true);
   }

   public final void shutdown(boolean read, boolean write) throws IOException {
      int oldState;
      int newState;
      do {
         oldState = this.state;
         if (isClosed(oldState)) {
            throw new ClosedChannelException();
         }

         newState = oldState;
         if (read && !isInputShutdown(oldState)) {
            newState = inputShutdown(oldState);
         }

         if (write && !isOutputShutdown(newState)) {
            newState = outputShutdown(newState);
         }

         if (newState == oldState) {
            return;
         }
      } while (!this.casState(oldState, newState));

      oldState = shutdown(this.fd, read, write);
      if (oldState < 0) {
         Errors.ioResult("shutdown", oldState);
      }
   }

   public final boolean isShutdown() {
      int state = this.state;
      return isInputShutdown(state) && isOutputShutdown(state);
   }

   public final boolean isInputShutdown() {
      return isInputShutdown(this.state);
   }

   public final boolean isOutputShutdown() {
      return isOutputShutdown(this.state);
   }

   public final int sendTo(ByteBuffer buf, int pos, int limit, InetAddress addr, int port) throws IOException {
      return this.sendTo(buf, pos, limit, addr, port, false);
   }

   public final int sendTo(ByteBuffer buf, int pos, int limit, InetAddress addr, int port, boolean fastOpen) throws IOException {
      byte[] address;
      int scopeId;
      if (addr instanceof Inet6Address) {
         address = addr.getAddress();
         scopeId = ((Inet6Address)addr).getScopeId();
      } else {
         scopeId = 0;
         address = NativeInetAddress.ipv4MappedIpv6Address(addr.getAddress());
      }

      int flags = fastOpen ? msgFastopen() : 0;
      int res = sendTo(this.fd, this.useIpv6(addr), buf, pos, limit, address, scopeId, port, flags);
      if (res >= 0) {
         return res;
      } else if (res == Errors.ERRNO_EINPROGRESS_NEGATIVE && fastOpen) {
         return 0;
      } else if (res == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
         throw new PortUnreachableException("sendTo failed");
      } else {
         return Errors.ioResult("sendTo", res);
      }
   }

   public final int sendToDomainSocket(ByteBuffer buf, int pos, int limit, byte[] path) throws IOException {
      int res = sendToDomainSocket(this.fd, buf, pos, limit, path);
      return res >= 0 ? res : Errors.ioResult("sendToDomainSocket", res);
   }

   public final int sendToAddress(long memoryAddress, int pos, int limit, InetAddress addr, int port) throws IOException {
      return this.sendToAddress(memoryAddress, pos, limit, addr, port, false);
   }

   public final int sendToAddress(long memoryAddress, int pos, int limit, InetAddress addr, int port, boolean fastOpen) throws IOException {
      byte[] address;
      int scopeId;
      if (addr instanceof Inet6Address) {
         address = addr.getAddress();
         scopeId = ((Inet6Address)addr).getScopeId();
      } else {
         scopeId = 0;
         address = NativeInetAddress.ipv4MappedIpv6Address(addr.getAddress());
      }

      int flags = fastOpen ? msgFastopen() : 0;
      int res = sendToAddress(this.fd, this.useIpv6(addr), memoryAddress, pos, limit, address, scopeId, port, flags);
      if (res >= 0) {
         return res;
      } else if (res == Errors.ERRNO_EINPROGRESS_NEGATIVE && fastOpen) {
         return 0;
      } else if (res == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
         throw new PortUnreachableException("sendToAddress failed");
      } else {
         return Errors.ioResult("sendToAddress", res);
      }
   }

   public final int sendToAddressDomainSocket(long memoryAddress, int pos, int limit, byte[] path) throws IOException {
      int res = sendToAddressDomainSocket(this.fd, memoryAddress, pos, limit, path);
      return res >= 0 ? res : Errors.ioResult("sendToAddressDomainSocket", res);
   }

   public final int sendToAddresses(long memoryAddress, int length, InetAddress addr, int port) throws IOException {
      return this.sendToAddresses(memoryAddress, length, addr, port, false);
   }

   public final int sendToAddresses(long memoryAddress, int length, InetAddress addr, int port, boolean fastOpen) throws IOException {
      byte[] address;
      int scopeId;
      if (addr instanceof Inet6Address) {
         address = addr.getAddress();
         scopeId = ((Inet6Address)addr).getScopeId();
      } else {
         scopeId = 0;
         address = NativeInetAddress.ipv4MappedIpv6Address(addr.getAddress());
      }

      int flags = fastOpen ? msgFastopen() : 0;
      int res = sendToAddresses(this.fd, this.useIpv6(addr), memoryAddress, length, address, scopeId, port, flags);
      if (res >= 0) {
         return res;
      } else if (res == Errors.ERRNO_EINPROGRESS_NEGATIVE && fastOpen) {
         return 0;
      } else if (res == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
         throw new PortUnreachableException("sendToAddresses failed");
      } else {
         return Errors.ioResult("sendToAddresses", res);
      }
   }

   public final int sendToAddressesDomainSocket(long memoryAddress, int length, byte[] path) throws IOException {
      int res = sendToAddressesDomainSocket(this.fd, memoryAddress, length, path);
      return res >= 0 ? res : Errors.ioResult("sendToAddressesDomainSocket", res);
   }

   public final DatagramSocketAddress recvFrom(ByteBuffer buf, int pos, int limit) throws IOException {
      return recvFrom(this.fd, buf, pos, limit);
   }

   public final DatagramSocketAddress recvFromAddress(long memoryAddress, int pos, int limit) throws IOException {
      return recvFromAddress(this.fd, memoryAddress, pos, limit);
   }

   public final DomainDatagramSocketAddress recvFromDomainSocket(ByteBuffer buf, int pos, int limit) throws IOException {
      return recvFromDomainSocket(this.fd, buf, pos, limit);
   }

   public final DomainDatagramSocketAddress recvFromAddressDomainSocket(long memoryAddress, int pos, int limit) throws IOException {
      return recvFromAddressDomainSocket(this.fd, memoryAddress, pos, limit);
   }

   public int recv(ByteBuffer buf, int pos, int limit) throws IOException {
      int res = recv(this.intValue(), buf, pos, limit);
      if (res > 0) {
         return res;
      } else {
         return res == 0 ? -1 : Errors.ioResult("recv", res);
      }
   }

   public int recvAddress(long address, int pos, int limit) throws IOException {
      int res = recvAddress(this.intValue(), address, pos, limit);
      if (res > 0) {
         return res;
      } else {
         return res == 0 ? -1 : Errors.ioResult("recvAddress", res);
      }
   }

   public int send(ByteBuffer buf, int pos, int limit) throws IOException {
      int res = send(this.intValue(), buf, pos, limit);
      return res >= 0 ? res : Errors.ioResult("send", res);
   }

   public int sendAddress(long address, int pos, int limit) throws IOException {
      int res = sendAddress(this.intValue(), address, pos, limit);
      return res >= 0 ? res : Errors.ioResult("sendAddress", res);
   }

   public final int recvFd() throws IOException {
      int res = recvFd(this.fd);
      if (res > 0) {
         return res;
      } else if (res == 0) {
         return -1;
      } else if (res != Errors.ERRNO_EAGAIN_NEGATIVE && res != Errors.ERRNO_EWOULDBLOCK_NEGATIVE) {
         throw Errors.newIOException("recvFd", res);
      } else {
         return 0;
      }
   }

   public final int sendFd(int fdToSend) throws IOException {
      int res = sendFd(this.fd, fdToSend);
      if (res >= 0) {
         return res;
      } else if (res != Errors.ERRNO_EAGAIN_NEGATIVE && res != Errors.ERRNO_EWOULDBLOCK_NEGATIVE) {
         throw Errors.newIOException("sendFd", res);
      } else {
         return -1;
      }
   }

   public final boolean connect(SocketAddress socketAddress) throws IOException {
      int res;
      if (socketAddress instanceof InetSocketAddress) {
         InetSocketAddress inetSocketAddress = (InetSocketAddress)socketAddress;
         InetAddress inetAddress = inetSocketAddress.getAddress();
         NativeInetAddress address = NativeInetAddress.newInstance(inetAddress);
         res = connect(this.fd, this.useIpv6(inetAddress), address.address, address.scopeId, inetSocketAddress.getPort());
      } else {
         if (!(socketAddress instanceof DomainSocketAddress)) {
            throw new Error("Unexpected SocketAddress implementation: " + StringUtil.className(socketAddress));
         }

         DomainSocketAddress unixDomainSocketAddress = (DomainSocketAddress)socketAddress;
         res = connectDomainSocket(this.fd, unixDomainSocketAddress.path().getBytes(CharsetUtil.UTF_8));
      }

      return res < 0 ? Errors.handleConnectErrno("connect", res) : true;
   }

   public final boolean finishConnect() throws IOException {
      int res = finishConnect(this.fd);
      return res < 0 ? Errors.handleConnectErrno("finishConnect", res) : true;
   }

   public final void disconnect() throws IOException {
      int res = disconnect(this.fd, this.ipv6);
      if (res < 0) {
         Errors.handleConnectErrno("disconnect", res);
      }
   }

   public final void bind(SocketAddress socketAddress) throws IOException {
      if (socketAddress instanceof InetSocketAddress) {
         InetSocketAddress addr = (InetSocketAddress)socketAddress;
         InetAddress inetAddress = addr.getAddress();
         NativeInetAddress address = NativeInetAddress.newInstance(inetAddress);
         int res = bind(this.fd, this.useIpv6(inetAddress), address.address, address.scopeId, addr.getPort());
         if (res < 0) {
            throw Errors.newIOException("bind", res);
         }
      } else {
         if (!(socketAddress instanceof DomainSocketAddress)) {
            throw new Error("Unexpected SocketAddress implementation: " + StringUtil.className(socketAddress));
         }

         DomainSocketAddress addr = (DomainSocketAddress)socketAddress;
         int res = bindDomainSocket(this.fd, addr.path().getBytes(CharsetUtil.UTF_8));
         if (res < 0) {
            throw Errors.newIOException("bind", res);
         }
      }
   }

   public final void listen(int backlog) throws IOException {
      int res = listen(this.fd, backlog);
      if (res < 0) {
         throw Errors.newIOException("listen", res);
      }
   }

   public final int accept(byte[] addr) throws IOException {
      int res = accept(this.fd, addr);
      if (res >= 0) {
         return res;
      } else if (res != Errors.ERRNO_EAGAIN_NEGATIVE && res != Errors.ERRNO_EWOULDBLOCK_NEGATIVE) {
         throw Errors.newIOException("accept", res);
      } else {
         return -1;
      }
   }

   public final InetSocketAddress remoteAddress() {
      byte[] addr = remoteAddress(this.fd);
      return addr == null ? null : NativeInetAddress.address(addr, 0, addr.length);
   }

   public final DomainSocketAddress remoteDomainSocketAddress() {
      byte[] addr = remoteDomainSocketAddress(this.fd);
      return addr == null ? null : new DomainSocketAddress(new String(addr));
   }

   public final InetSocketAddress localAddress() {
      byte[] addr = localAddress(this.fd);
      return addr == null ? null : NativeInetAddress.address(addr, 0, addr.length);
   }

   public final DomainSocketAddress localDomainSocketAddress() {
      byte[] addr = localDomainSocketAddress(this.fd);
      return addr == null ? null : new DomainSocketAddress(new String(addr));
   }

   public final int getReceiveBufferSize() throws IOException {
      return getReceiveBufferSize(this.fd);
   }

   public final int getSendBufferSize() throws IOException {
      return getSendBufferSize(this.fd);
   }

   public final boolean isKeepAlive() throws IOException {
      return isKeepAlive(this.fd) != 0;
   }

   public final boolean isTcpNoDelay() throws IOException {
      return isTcpNoDelay(this.fd) != 0;
   }

   public final boolean isReuseAddress() throws IOException {
      return isReuseAddress(this.fd) != 0;
   }

   public final boolean isReusePort() throws IOException {
      return isReusePort(this.fd) != 0;
   }

   public final boolean isBroadcast() throws IOException {
      return isBroadcast(this.fd) != 0;
   }

   public final int getSoLinger() throws IOException {
      return getSoLinger(this.fd);
   }

   public final int getSoError() throws IOException {
      return getSoError(this.fd);
   }

   public final int getTrafficClass() throws IOException {
      return getTrafficClass(this.fd, this.ipv6);
   }

   public final void setKeepAlive(boolean keepAlive) throws IOException {
      setKeepAlive(this.fd, keepAlive ? 1 : 0);
   }

   public final void setReceiveBufferSize(int receiveBufferSize) throws IOException {
      setReceiveBufferSize(this.fd, receiveBufferSize);
   }

   public final void setSendBufferSize(int sendBufferSize) throws IOException {
      setSendBufferSize(this.fd, sendBufferSize);
   }

   public final void setTcpNoDelay(boolean tcpNoDelay) throws IOException {
      setTcpNoDelay(this.fd, tcpNoDelay ? 1 : 0);
   }

   public final void setSoLinger(int soLinger) throws IOException {
      setSoLinger(this.fd, soLinger);
   }

   public final void setReuseAddress(boolean reuseAddress) throws IOException {
      setReuseAddress(this.fd, reuseAddress ? 1 : 0);
   }

   public final void setReusePort(boolean reusePort) throws IOException {
      setReusePort(this.fd, reusePort ? 1 : 0);
   }

   public final void setBroadcast(boolean broadcast) throws IOException {
      setBroadcast(this.fd, broadcast ? 1 : 0);
   }

   public final void setTrafficClass(int trafficClass) throws IOException {
      setTrafficClass(this.fd, this.ipv6, trafficClass);
   }

   public void setIntOpt(int level, int optname, int optvalue) throws IOException {
      setIntOpt(this.fd, level, optname, optvalue);
   }

   public void setRawOpt(int level, int optname, ByteBuffer optvalue) throws IOException {
      int limit = optvalue.limit();
      if (optvalue.isDirect()) {
         setRawOptAddress(this.fd, level, optname, Buffer.memoryAddress(optvalue) + optvalue.position(), optvalue.remaining());
      } else if (optvalue.hasArray()) {
         setRawOptArray(this.fd, level, optname, optvalue.array(), optvalue.arrayOffset() + optvalue.position(), optvalue.remaining());
      } else {
         byte[] bytes = new byte[optvalue.remaining()];
         optvalue.duplicate().get(bytes);
         setRawOptArray(this.fd, level, optname, bytes, 0, bytes.length);
      }

      ((java.nio.Buffer)optvalue).position(limit);
   }

   public int getIntOpt(int level, int optname) throws IOException {
      return getIntOpt(this.fd, level, optname);
   }

   public void getRawOpt(int level, int optname, ByteBuffer out) throws IOException {
      if (out.isDirect()) {
         getRawOptAddress(this.fd, level, optname, Buffer.memoryAddress(out) + out.position(), out.remaining());
      } else if (out.hasArray()) {
         getRawOptArray(this.fd, level, optname, out.array(), out.position() + out.arrayOffset(), out.remaining());
      } else {
         byte[] outArray = new byte[out.remaining()];
         getRawOptArray(this.fd, level, optname, outArray, 0, outArray.length);
         out.put(outArray);
      }

      ((java.nio.Buffer)out).position(out.limit());
   }

   public static boolean isIPv6Preferred() {
      return isIpv6Preferred;
   }

   @Deprecated
   public static boolean shouldUseIpv6(InternetProtocolFamily family) {
      return family == null ? isIPv6Preferred() : family == InternetProtocolFamily.IPv6;
   }

   public static boolean shouldUseIpv6(SocketProtocolFamily family) {
      return family == null ? isIPv6Preferred() : family == SocketProtocolFamily.INET6;
   }

   private static native boolean isIPv6Preferred0(boolean var0);

   private static native boolean isIPv6(int var0);

   @Override
   public String toString() {
      return "Socket{fd=" + this.fd + '}';
   }

   public static Socket newSocketStream() {
      return new Socket(newSocketStream0());
   }

   public static Socket newSocketDgram() {
      return new Socket(newSocketDgram0());
   }

   public static Socket newSocketDomain() {
      return new Socket(newSocketDomain0());
   }

   public static Socket newSocketDomainDgram() {
      return new Socket(newSocketDomainDgram0());
   }

   public static void initialize() {
      isIpv6Preferred = isIPv6Preferred0(NetUtil.isIpV4StackPreferred());
   }

   protected static int newSocketStream0() {
      return newSocketStream0(isIPv6Preferred());
   }

   @Deprecated
   protected static int newSocketStream0(InternetProtocolFamily protocol) {
      return newSocketStream0(shouldUseIpv6(protocol));
   }

   protected static int newSocketStream0(SocketProtocolFamily protocol) {
      return newSocketStream0(shouldUseIpv6(protocol));
   }

   protected static int newSocketStream0(boolean ipv6) {
      int res = newSocketStreamFd(ipv6);
      if (res < 0) {
         throw new ChannelException(Errors.newIOException("newSocketStream", res));
      } else {
         return res;
      }
   }

   protected static int newSocketDgram0() {
      return newSocketDgram0(isIPv6Preferred());
   }

   @Deprecated
   protected static int newSocketDgram0(InternetProtocolFamily family) {
      return newSocketDgram0(shouldUseIpv6(family));
   }

   protected static int newSocketDgram0(SocketProtocolFamily family) {
      if (family != null && family != SocketProtocolFamily.INET && family != SocketProtocolFamily.INET6) {
         throw new IllegalArgumentException("SocketProtocolFamily must be either INET or INET6: " + family);
      } else {
         return newSocketDgram0(shouldUseIpv6(family));
      }
   }

   protected static int newSocketDgram0(boolean ipv6) {
      int res = newSocketDgramFd(ipv6);
      if (res < 0) {
         throw new ChannelException(Errors.newIOException("newSocketDgram", res));
      } else {
         return res;
      }
   }

   protected static int newSocketDomain0() {
      int res = newSocketDomainFd();
      if (res < 0) {
         throw new ChannelException(Errors.newIOException("newSocketDomain", res));
      } else {
         return res;
      }
   }

   protected static int newSocketDomainDgram0() {
      int res = newSocketDomainDgramFd();
      if (res < 0) {
         throw new ChannelException(Errors.newIOException("newSocketDomainDgram", res));
      } else {
         return res;
      }
   }

   private static native int shutdown(int var0, boolean var1, boolean var2);

   private static native int connect(int var0, boolean var1, byte[] var2, int var3, int var4);

   private static native int connectDomainSocket(int var0, byte[] var1);

   private static native int finishConnect(int var0);

   private static native int disconnect(int var0, boolean var1);

   private static native int bind(int var0, boolean var1, byte[] var2, int var3, int var4);

   private static native int bindDomainSocket(int var0, byte[] var1);

   private static native int listen(int var0, int var1);

   private static native int accept(int var0, byte[] var1);

   private static native byte[] remoteAddress(int var0);

   private static native byte[] remoteDomainSocketAddress(int var0);

   private static native byte[] localAddress(int var0);

   private static native byte[] localDomainSocketAddress(int var0);

   private static native int send(int var0, ByteBuffer var1, int var2, int var3);

   private static native int sendAddress(int var0, long var1, int var3, int var4);

   private static native int recv(int var0, ByteBuffer var1, int var2, int var3);

   private static native int recvAddress(int var0, long var1, int var3, int var4);

   private static native int sendTo(int var0, boolean var1, ByteBuffer var2, int var3, int var4, byte[] var5, int var6, int var7, int var8);

   private static native int sendToAddress(int var0, boolean var1, long var2, int var4, int var5, byte[] var6, int var7, int var8, int var9);

   private static native int sendToAddresses(int var0, boolean var1, long var2, int var4, byte[] var5, int var6, int var7, int var8);

   private static native int sendToDomainSocket(int var0, ByteBuffer var1, int var2, int var3, byte[] var4);

   private static native int sendToAddressDomainSocket(int var0, long var1, int var3, int var4, byte[] var5);

   private static native int sendToAddressesDomainSocket(int var0, long var1, int var3, byte[] var4);

   private static native DatagramSocketAddress recvFrom(int var0, ByteBuffer var1, int var2, int var3) throws IOException;

   private static native DatagramSocketAddress recvFromAddress(int var0, long var1, int var3, int var4) throws IOException;

   private static native DomainDatagramSocketAddress recvFromDomainSocket(int var0, ByteBuffer var1, int var2, int var3) throws IOException;

   private static native DomainDatagramSocketAddress recvFromAddressDomainSocket(int var0, long var1, int var3, int var4) throws IOException;

   private static native int recvFd(int var0);

   private static native int sendFd(int var0, int var1);

   private static native int msgFastopen();

   private static native int newSocketStreamFd(boolean var0);

   private static native int newSocketDgramFd(boolean var0);

   private static native int newSocketDomainFd();

   private static native int newSocketDomainDgramFd();

   private static native int isReuseAddress(int var0) throws IOException;

   private static native int isReusePort(int var0) throws IOException;

   private static native int getReceiveBufferSize(int var0) throws IOException;

   private static native int getSendBufferSize(int var0) throws IOException;

   private static native int isKeepAlive(int var0) throws IOException;

   private static native int isTcpNoDelay(int var0) throws IOException;

   private static native int isBroadcast(int var0) throws IOException;

   private static native int getSoLinger(int var0) throws IOException;

   private static native int getSoError(int var0) throws IOException;

   private static native int getTrafficClass(int var0, boolean var1) throws IOException;

   private static native void setReuseAddress(int var0, int var1) throws IOException;

   private static native void setReusePort(int var0, int var1) throws IOException;

   private static native void setKeepAlive(int var0, int var1) throws IOException;

   private static native void setReceiveBufferSize(int var0, int var1) throws IOException;

   private static native void setSendBufferSize(int var0, int var1) throws IOException;

   private static native void setTcpNoDelay(int var0, int var1) throws IOException;

   private static native void setSoLinger(int var0, int var1) throws IOException;

   private static native void setBroadcast(int var0, int var1) throws IOException;

   private static native void setTrafficClass(int var0, boolean var1, int var2) throws IOException;

   private static native void setIntOpt(int var0, int var1, int var2, int var3) throws IOException;

   private static native void setRawOptArray(int var0, int var1, int var2, byte[] var3, int var4, int var5) throws IOException;

   private static native void setRawOptAddress(int var0, int var1, int var2, long var3, int var5) throws IOException;

   private static native int getIntOpt(int var0, int var1, int var2) throws IOException;

   private static native void getRawOptArray(int var0, int var1, int var2, byte[] var3, int var4, int var5) throws IOException;

   private static native void getRawOptAddress(int var0, int var1, int var2, long var3, int var5) throws IOException;
}
