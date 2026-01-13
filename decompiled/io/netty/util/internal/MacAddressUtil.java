package io.netty.util.internal;

import io.netty.util.NetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class MacAddressUtil {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(MacAddressUtil.class);
   private static final int EUI64_MAC_ADDRESS_LENGTH = 8;
   private static final int EUI48_MAC_ADDRESS_LENGTH = 6;

   public static byte[] bestAvailableMac() {
      byte[] bestMacAddr = EmptyArrays.EMPTY_BYTES;
      InetAddress bestInetAddr = NetUtil.LOCALHOST4;
      Map<NetworkInterface, InetAddress> ifaces = new LinkedHashMap<>();

      for (NetworkInterface iface : NetUtil.NETWORK_INTERFACES) {
         Enumeration<InetAddress> addrs = SocketUtils.addressesFromNetworkInterface(iface);
         if (addrs.hasMoreElements()) {
            InetAddress a = addrs.nextElement();
            if (!a.isLoopbackAddress()) {
               ifaces.put(iface, a);
            }
         }
      }

      for (Entry<NetworkInterface, InetAddress> entry : ifaces.entrySet()) {
         NetworkInterface ifacex = entry.getKey();
         InetAddress inetAddr = entry.getValue();
         if (!ifacex.isVirtual()) {
            byte[] macAddr;
            try {
               macAddr = SocketUtils.hardwareAddressFromNetworkInterface(ifacex);
            } catch (SocketException var10) {
               logger.debug("Failed to get the hardware address of a network interface: {}", ifacex, var10);
               continue;
            }

            boolean replace = false;
            int res = compareAddresses(bestMacAddr, macAddr);
            if (res < 0) {
               replace = true;
            } else if (res == 0) {
               res = compareAddresses(bestInetAddr, inetAddr);
               if (res < 0) {
                  replace = true;
               } else if (res == 0 && bestMacAddr.length < macAddr.length) {
                  replace = true;
               }
            }

            if (replace) {
               bestMacAddr = macAddr;
               bestInetAddr = inetAddr;
            }
         }
      }

      if (bestMacAddr == EmptyArrays.EMPTY_BYTES) {
         return null;
      } else {
         if (bestMacAddr.length == 6) {
            byte[] newAddr = new byte[8];
            System.arraycopy(bestMacAddr, 0, newAddr, 0, 3);
            newAddr[3] = -1;
            newAddr[4] = -2;
            System.arraycopy(bestMacAddr, 3, newAddr, 5, 3);
            bestMacAddr = newAddr;
         } else {
            bestMacAddr = Arrays.copyOf(bestMacAddr, 8);
         }

         return bestMacAddr;
      }
   }

   public static byte[] defaultMachineId() {
      byte[] bestMacAddr = bestAvailableMac();
      if (bestMacAddr == null) {
         bestMacAddr = new byte[8];
         java.util.concurrent.ThreadLocalRandom.current().nextBytes(bestMacAddr);
         logger.warn("Failed to find a usable hardware address from the network interfaces; using random bytes: {}", formatAddress(bestMacAddr));
      }

      return bestMacAddr;
   }

   public static byte[] parseMAC(String value) {
      byte[] machineId;
      char separator;
      switch (value.length()) {
         case 17:
            separator = value.charAt(2);
            validateMacSeparator(separator);
            machineId = new byte[6];
            break;
         case 23:
            separator = value.charAt(2);
            validateMacSeparator(separator);
            machineId = new byte[8];
            break;
         default:
            throw new IllegalArgumentException("value is not supported [MAC-48, EUI-48, EUI-64]");
      }

      int end = machineId.length - 1;
      int j = 0;

      for (int i = 0; i < end; j += 3) {
         int sIndex = j + 2;
         machineId[i] = StringUtil.decodeHexByte(value, j);
         if (value.charAt(sIndex) != separator) {
            throw new IllegalArgumentException("expected separator '" + separator + " but got '" + value.charAt(sIndex) + "' at index: " + sIndex);
         }

         i++;
      }

      machineId[end] = StringUtil.decodeHexByte(value, j);
      return machineId;
   }

   private static void validateMacSeparator(char separator) {
      if (separator != ':' && separator != '-') {
         throw new IllegalArgumentException("unsupported separator: " + separator + " (expected: [:-])");
      }
   }

   public static String formatAddress(byte[] addr) {
      StringBuilder buf = new StringBuilder(24);

      for (byte b : addr) {
         buf.append(String.format("%02x:", b & 255));
      }

      return buf.substring(0, buf.length() - 1);
   }

   static int compareAddresses(byte[] current, byte[] candidate) {
      if (candidate != null && candidate.length >= 6) {
         boolean onlyZeroAndOne = true;

         for (byte b : candidate) {
            if (b != 0 && b != 1) {
               onlyZeroAndOne = false;
               break;
            }
         }

         if (onlyZeroAndOne) {
            return 1;
         } else if ((candidate[0] & 1) != 0) {
            return 1;
         } else if ((candidate[0] & 2) == 0) {
            return current.length != 0 && (current[0] & 2) == 0 ? 0 : -1;
         } else {
            return current.length != 0 && (current[0] & 2) == 0 ? 1 : 0;
         }
      } else {
         return 1;
      }
   }

   private static int compareAddresses(InetAddress current, InetAddress candidate) {
      return scoreAddress(current) - scoreAddress(candidate);
   }

   private static int scoreAddress(InetAddress addr) {
      if (addr.isAnyLocalAddress() || addr.isLoopbackAddress()) {
         return 0;
      } else if (addr.isMulticastAddress()) {
         return 1;
      } else if (addr.isLinkLocalAddress()) {
         return 2;
      } else {
         return addr.isSiteLocalAddress() ? 3 : 4;
      }
   }

   private MacAddressUtil() {
   }
}
