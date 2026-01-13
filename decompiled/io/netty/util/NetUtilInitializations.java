package io.netty.util;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

final class NetUtilInitializations {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NetUtilInitializations.class);

   private NetUtilInitializations() {
   }

   static Inet4Address createLocalhost4() {
      byte[] LOCALHOST4_BYTES = new byte[]{127, 0, 0, 1};
      Inet4Address localhost4 = null;

      try {
         localhost4 = (Inet4Address)InetAddress.getByAddress("localhost", LOCALHOST4_BYTES);
      } catch (Exception var3) {
         PlatformDependent.throwException(var3);
      }

      return localhost4;
   }

   static Inet6Address createLocalhost6() {
      byte[] LOCALHOST6_BYTES = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
      Inet6Address localhost6 = null;

      try {
         localhost6 = (Inet6Address)InetAddress.getByAddress("localhost", LOCALHOST6_BYTES);
      } catch (Exception var3) {
         PlatformDependent.throwException(var3);
      }

      return localhost6;
   }

   static Collection<NetworkInterface> networkInterfaces() {
      List<NetworkInterface> networkInterfaces = new ArrayList<>();

      try {
         Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
         if (interfaces != null) {
            while (interfaces.hasMoreElements()) {
               networkInterfaces.add(interfaces.nextElement());
            }
         }
      } catch (SocketException var2) {
         logger.warn("Failed to retrieve the list of available network interfaces", (Throwable)var2);
      } catch (NullPointerException var3) {
         if (!PlatformDependent.isAndroid()) {
            throw var3;
         }
      }

      return Collections.unmodifiableList(networkInterfaces);
   }

   static NetUtilInitializations.NetworkIfaceAndInetAddress determineLoopback(
      Collection<NetworkInterface> networkInterfaces, Inet4Address localhost4, Inet6Address localhost6
   ) {
      List<NetworkInterface> ifaces = new ArrayList<>();

      for (NetworkInterface iface : networkInterfaces) {
         if (SocketUtils.addressesFromNetworkInterface(iface).hasMoreElements()) {
            ifaces.add(iface);
         }
      }

      NetworkInterface loopbackIface = null;
      InetAddress loopbackAddr = null;

      label134:
      for (NetworkInterface ifacex : ifaces) {
         Enumeration<InetAddress> i = SocketUtils.addressesFromNetworkInterface(ifacex);

         while (i.hasMoreElements()) {
            InetAddress addr = i.nextElement();
            if (addr.isLoopbackAddress()) {
               loopbackIface = ifacex;
               loopbackAddr = addr;
               break label134;
            }
         }
      }

      if (loopbackIface == null) {
         try {
            for (NetworkInterface ifacex : ifaces) {
               if (ifacex.isLoopback()) {
                  Enumeration<InetAddress> i = SocketUtils.addressesFromNetworkInterface(ifacex);
                  if (i.hasMoreElements()) {
                     loopbackIface = ifacex;
                     loopbackAddr = i.nextElement();
                     break;
                  }
               }
            }

            if (loopbackIface == null) {
               logger.warn("Failed to find the loopback interface");
            }
         } catch (SocketException var16) {
            logger.warn("Failed to find the loopback interface", (Throwable)var16);
         }
      }

      if (loopbackIface != null) {
         logger.debug("Loopback interface: {} ({}, {})", loopbackIface.getName(), loopbackIface.getDisplayName(), loopbackAddr.getHostAddress());
      } else if (loopbackAddr == null) {
         try {
            if (NetworkInterface.getByInetAddress(localhost6) != null) {
               logger.debug("Using hard-coded IPv6 localhost address: {}", localhost6);
               loopbackAddr = localhost6;
            }
         } catch (Exception var14) {
         } finally {
            if (loopbackAddr == null) {
               logger.debug("Using hard-coded IPv4 localhost address: {}", localhost4);
            }
         }
      }

      return new NetUtilInitializations.NetworkIfaceAndInetAddress(loopbackIface, loopbackAddr);
   }

   static final class NetworkIfaceAndInetAddress {
      private final NetworkInterface iface;
      private final InetAddress address;

      NetworkIfaceAndInetAddress(NetworkInterface iface, InetAddress address) {
         this.iface = iface;
         this.address = address;
      }

      public NetworkInterface iface() {
         return this.iface;
      }

      public InetAddress address() {
         return this.address;
      }
   }
}
