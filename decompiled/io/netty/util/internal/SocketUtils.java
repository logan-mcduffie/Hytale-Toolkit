package io.netty.util.internal;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.Enumeration;

public final class SocketUtils {
   private static final Enumeration<Object> EMPTY = Collections.enumeration(Collections.emptyList());

   private SocketUtils() {
   }

   private static <T> Enumeration<T> empty() {
      return (Enumeration<T>)EMPTY;
   }

   public static void connect(final Socket socket, final SocketAddress remoteAddress, final int timeout) throws IOException {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws IOException {
               socket.connect(remoteAddress, timeout);
               return null;
            }
         });
      } catch (PrivilegedActionException var4) {
         throw (IOException)var4.getCause();
      }
   }

   public static void bind(final Socket socket, final SocketAddress bindpoint) throws IOException {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws IOException {
               socket.bind(bindpoint);
               return null;
            }
         });
      } catch (PrivilegedActionException var3) {
         throw (IOException)var3.getCause();
      }
   }

   public static boolean connect(final SocketChannel socketChannel, final SocketAddress remoteAddress) throws IOException {
      try {
         return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
            public Boolean run() throws IOException {
               return socketChannel.connect(remoteAddress);
            }
         });
      } catch (PrivilegedActionException var3) {
         throw (IOException)var3.getCause();
      }
   }

   public static void bind(final SocketChannel socketChannel, final SocketAddress address) throws IOException {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws IOException {
               socketChannel.bind(address);
               return null;
            }
         });
      } catch (PrivilegedActionException var3) {
         throw (IOException)var3.getCause();
      }
   }

   public static SocketChannel accept(final ServerSocketChannel serverSocketChannel) throws IOException {
      try {
         return AccessController.doPrivileged(new PrivilegedExceptionAction<SocketChannel>() {
            public SocketChannel run() throws IOException {
               return serverSocketChannel.accept();
            }
         });
      } catch (PrivilegedActionException var2) {
         throw (IOException)var2.getCause();
      }
   }

   public static void bind(final DatagramChannel networkChannel, final SocketAddress address) throws IOException {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws IOException {
               networkChannel.bind(address);
               return null;
            }
         });
      } catch (PrivilegedActionException var3) {
         throw (IOException)var3.getCause();
      }
   }

   public static SocketAddress localSocketAddress(final ServerSocket socket) {
      return AccessController.doPrivileged(new PrivilegedAction<SocketAddress>() {
         public SocketAddress run() {
            return socket.getLocalSocketAddress();
         }
      });
   }

   public static InetAddress addressByName(final String hostname) throws UnknownHostException {
      try {
         return AccessController.doPrivileged(new PrivilegedExceptionAction<InetAddress>() {
            public InetAddress run() throws UnknownHostException {
               return InetAddress.getByName(hostname);
            }
         });
      } catch (PrivilegedActionException var2) {
         throw (UnknownHostException)var2.getCause();
      }
   }

   public static InetAddress[] allAddressesByName(final String hostname) throws UnknownHostException {
      try {
         return AccessController.doPrivileged(new PrivilegedExceptionAction<InetAddress[]>() {
            public InetAddress[] run() throws UnknownHostException {
               return InetAddress.getAllByName(hostname);
            }
         });
      } catch (PrivilegedActionException var2) {
         throw (UnknownHostException)var2.getCause();
      }
   }

   public static InetSocketAddress socketAddress(final String hostname, final int port) {
      return AccessController.doPrivileged(new PrivilegedAction<InetSocketAddress>() {
         public InetSocketAddress run() {
            return new InetSocketAddress(hostname, port);
         }
      });
   }

   public static Enumeration<InetAddress> addressesFromNetworkInterface(final NetworkInterface intf) {
      Enumeration<InetAddress> addresses = AccessController.doPrivileged(new PrivilegedAction<Enumeration<InetAddress>>() {
         public Enumeration<InetAddress> run() {
            return intf.getInetAddresses();
         }
      });
      return addresses == null ? empty() : addresses;
   }

   public static InetAddress loopbackAddress() {
      return AccessController.doPrivileged(new PrivilegedAction<InetAddress>() {
         public InetAddress run() {
            return InetAddress.getLoopbackAddress();
         }
      });
   }

   public static byte[] hardwareAddressFromNetworkInterface(final NetworkInterface intf) throws SocketException {
      try {
         return AccessController.doPrivileged(new PrivilegedExceptionAction<byte[]>() {
            public byte[] run() throws SocketException {
               return intf.getHardwareAddress();
            }
         });
      } catch (PrivilegedActionException var2) {
         throw (SocketException)var2.getCause();
      }
   }
}
