package io.netty.util;

import io.netty.util.internal.BoundedInputStream;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collection;

public final class NetUtil {
   public static final Inet4Address LOCALHOST4;
   public static final Inet6Address LOCALHOST6;
   public static final InetAddress LOCALHOST;
   public static final NetworkInterface LOOPBACK_IF;
   public static final Collection<NetworkInterface> NETWORK_INTERFACES;
   public static final int SOMAXCONN;
   private static final int IPV6_WORD_COUNT = 8;
   private static final int IPV6_MAX_CHAR_COUNT = 39;
   private static final int IPV6_BYTE_COUNT = 16;
   private static final int IPV6_MAX_CHAR_BETWEEN_SEPARATOR = 4;
   private static final int IPV6_MIN_SEPARATORS = 2;
   private static final int IPV6_MAX_SEPARATORS = 8;
   private static final int IPV4_MAX_CHAR_BETWEEN_SEPARATOR = 3;
   private static final int IPV4_SEPARATORS = 3;
   private static final boolean IPV4_PREFERRED = SystemPropertyUtil.getBoolean("java.net.preferIPv4Stack", false);
   private static final boolean IPV6_ADDRESSES_PREFERRED;
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NetUtil.class);

   private static Integer sysctlGetInt(String sysctlKey) throws IOException {
      Process process = new ProcessBuilder("sysctl", sysctlKey).start();

      Integer var7;
      try {
         InputStream is = process.getInputStream();
         InputStreamReader isr = new InputStreamReader(new BoundedInputStream(is));
         BufferedReader br = new BufferedReader(isr);

         label105: {
            Object var15;
            try {
               String line = br.readLine();
               if (line != null && line.startsWith(sysctlKey)) {
                  for (int i = line.length() - 1; i > sysctlKey.length(); i--) {
                     if (!Character.isDigit(line.charAt(i))) {
                        var7 = Integer.valueOf(line.substring(i + 1));
                        break label105;
                     }
                  }
               }

               var15 = null;
            } catch (Throwable var13) {
               try {
                  br.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }

               throw var13;
            }

            br.close();
            return (Integer)var15;
         }

         br.close();
      } finally {
         process.destroy();
      }

      return var7;
   }

   public static boolean isIpV4StackPreferred() {
      return IPV4_PREFERRED;
   }

   public static boolean isIpV6AddressesPreferred() {
      return IPV6_ADDRESSES_PREFERRED;
   }

   public static byte[] createByteArrayFromIpAddressString(String ipAddressString) {
      if (isValidIpV4Address(ipAddressString)) {
         return validIpV4ToBytes(ipAddressString);
      } else if (isValidIpV6Address(ipAddressString)) {
         if (ipAddressString.charAt(0) == '[') {
            ipAddressString = ipAddressString.substring(1, ipAddressString.length() - 1);
         }

         int percentPos = ipAddressString.indexOf(37);
         if (percentPos >= 0) {
            ipAddressString = ipAddressString.substring(0, percentPos);
         }

         return getIPv6ByName(ipAddressString, true);
      } else {
         return null;
      }
   }

   public static InetAddress createInetAddressFromIpAddressString(String ipAddressString) {
      if (isValidIpV4Address(ipAddressString)) {
         byte[] bytes = validIpV4ToBytes(ipAddressString);

         try {
            return InetAddress.getByAddress(bytes);
         } catch (UnknownHostException var5) {
            throw new IllegalStateException(var5);
         }
      } else if (isValidIpV6Address(ipAddressString)) {
         if (ipAddressString.charAt(0) == '[') {
            ipAddressString = ipAddressString.substring(1, ipAddressString.length() - 1);
         }

         int percentPos = ipAddressString.indexOf(37);
         if (percentPos >= 0) {
            try {
               int scopeId = Integer.parseInt(ipAddressString.substring(percentPos + 1));
               ipAddressString = ipAddressString.substring(0, percentPos);
               byte[] bytes = getIPv6ByName(ipAddressString, true);
               if (bytes == null) {
                  return null;
               } else {
                  try {
                     return Inet6Address.getByAddress(null, bytes, scopeId);
                  } catch (UnknownHostException var6) {
                     throw new IllegalStateException(var6);
                  }
               }
            } catch (NumberFormatException var7) {
               return null;
            }
         } else {
            byte[] bytes = getIPv6ByName(ipAddressString, true);
            if (bytes == null) {
               return null;
            } else {
               try {
                  return InetAddress.getByAddress(bytes);
               } catch (UnknownHostException var8) {
                  throw new IllegalStateException(var8);
               }
            }
         }
      } else {
         return null;
      }
   }

   private static int decimalDigit(String str, int pos) {
      return str.charAt(pos) - 48;
   }

   private static byte ipv4WordToByte(String ip, int from, int toExclusive) {
      int ret = decimalDigit(ip, from);
      if (++from == toExclusive) {
         return (byte)ret;
      } else {
         ret = ret * 10 + decimalDigit(ip, from);
         from++;
         return from == toExclusive ? (byte)ret : (byte)(ret * 10 + decimalDigit(ip, from));
      }
   }

   static byte[] validIpV4ToBytes(String ip) {
      int i;
      int var2;
      int var3;
      return new byte[]{
         ipv4WordToByte(ip, 0, i = ip.indexOf(46, 1)),
         ipv4WordToByte(ip, i + 1, var2 = ip.indexOf(46, i + 2)),
         ipv4WordToByte(ip, var2 + 1, var3 = ip.indexOf(46, var2 + 2)),
         ipv4WordToByte(ip, var3 + 1, ip.length())
      };
   }

   public static int ipv4AddressToInt(Inet4Address ipAddress) {
      byte[] octets = ipAddress.getAddress();
      return (octets[0] & 0xFF) << 24 | (octets[1] & 0xFF) << 16 | (octets[2] & 0xFF) << 8 | octets[3] & 0xFF;
   }

   public static String intToIpAddress(int i) {
      StringBuilder buf = new StringBuilder(15);
      buf.append(i >> 24 & 0xFF);
      buf.append('.');
      buf.append(i >> 16 & 0xFF);
      buf.append('.');
      buf.append(i >> 8 & 0xFF);
      buf.append('.');
      buf.append(i & 0xFF);
      return buf.toString();
   }

   public static String bytesToIpAddress(byte[] bytes) {
      return bytesToIpAddress(bytes, 0, bytes.length);
   }

   public static String bytesToIpAddress(byte[] bytes, int offset, int length) {
      switch (length) {
         case 4:
            return new StringBuilder(15)
               .append(bytes[offset] & 255)
               .append('.')
               .append(bytes[offset + 1] & 255)
               .append('.')
               .append(bytes[offset + 2] & 255)
               .append('.')
               .append(bytes[offset + 3] & 255)
               .toString();
         case 16:
            return toAddressString(bytes, offset, false);
         default:
            throw new IllegalArgumentException("length: " + length + " (expected: 4 or 16)");
      }
   }

   public static boolean isValidIpV6Address(String ip) {
      return isValidIpV6Address((CharSequence)ip);
   }

   public static boolean isValidIpV6Address(CharSequence ip) {
      int end = ip.length();
      if (end < 2) {
         return false;
      } else {
         char c = ip.charAt(0);
         int start;
         if (c == '[') {
            if (ip.charAt(--end) != ']') {
               return false;
            }

            start = 1;
            c = ip.charAt(1);
         } else {
            start = 0;
         }

         int colons;
         int compressBegin;
         if (c == ':') {
            if (ip.charAt(start + 1) != ':') {
               return false;
            }

            colons = 2;
            compressBegin = start;
            start += 2;
         } else {
            colons = 0;
            compressBegin = -1;
         }

         int wordLen = 0;
         int i = start;

         while (true) {
            label160: {
               if (i < end) {
                  c = ip.charAt(i);
                  if (isValidHexChar(c)) {
                     if (wordLen >= 4) {
                        return false;
                     }

                     wordLen++;
                     break label160;
                  }

                  switch (c) {
                     case '%':
                        end = i;
                        break;
                     case '.':
                        if ((compressBegin >= 0 || colons == 6) && (colons != 7 || compressBegin < start) && colons <= 7) {
                           int ipv4Start = i - wordLen;
                           int j = ipv4Start - 2;
                           if (isValidIPv4MappedChar(ip.charAt(j))) {
                              if (!isValidIPv4MappedChar(ip.charAt(j - 1))
                                 || !isValidIPv4MappedChar(ip.charAt(j - 2))
                                 || !isValidIPv4MappedChar(ip.charAt(j - 3))) {
                                 return false;
                              }

                              j -= 5;
                           }

                           while (j >= start) {
                              char tmpChar = ip.charAt(j);
                              if (tmpChar != '0' && tmpChar != ':') {
                                 return false;
                              }

                              j--;
                           }

                           int ipv4End = AsciiString.indexOf(ip, '%', ipv4Start + 7);
                           if (ipv4End < 0) {
                              ipv4End = end;
                           }

                           return isValidIpV4Address(ip, ipv4Start, ipv4End);
                        }

                        return false;
                     case ':':
                        if (colons > 7) {
                           return false;
                        }

                        if (ip.charAt(i - 1) == ':') {
                           if (compressBegin >= 0) {
                              return false;
                           }

                           compressBegin = i - 1;
                        } else {
                           wordLen = 0;
                        }

                        colons++;
                        break label160;
                     default:
                        return false;
                  }
               }

               if (compressBegin < 0) {
                  return colons == 7 && wordLen > 0;
               }

               return compressBegin + 2 == end || wordLen > 0 && (colons < 8 || compressBegin <= start);
            }

            i++;
         }
      }
   }

   private static boolean isValidIpV4Word(CharSequence word, int from, int toExclusive) {
      int len = toExclusive - from;
      char c0;
      if (len < 1 || len > 3 || (c0 = word.charAt(from)) < '0') {
         return false;
      } else {
         char c1;
         char c2;
         return len == 3
            ? (c1 = word.charAt(from + 1)) >= '0'
               && (c2 = word.charAt(from + 2)) >= '0'
               && (c0 <= '1' && c1 <= '9' && c2 <= '9' || c0 == '2' && c1 <= '5' && (c2 <= '5' || c1 < '5' && c2 <= '9'))
            : c0 <= '9' && (len == 1 || isValidNumericChar(word.charAt(from + 1)));
      }
   }

   private static boolean isValidHexChar(char c) {
      return c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f';
   }

   private static boolean isValidNumericChar(char c) {
      return c >= '0' && c <= '9';
   }

   private static boolean isValidIPv4MappedChar(char c) {
      return c == 'f' || c == 'F';
   }

   private static boolean isValidIPv4MappedSeparators(byte b0, byte b1, boolean mustBeZero) {
      return b0 == b1 && (b0 == 0 || !mustBeZero && b1 == -1);
   }

   private static boolean isValidIPv4Mapped(byte[] bytes, int currentIndex, int compressBegin, int compressLength) {
      boolean mustBeZero = compressBegin + compressLength >= 14;
      return currentIndex <= 12
         && currentIndex >= 2
         && (!mustBeZero || compressBegin < 12)
         && isValidIPv4MappedSeparators(bytes[currentIndex - 1], bytes[currentIndex - 2], mustBeZero)
         && PlatformDependent.isZero(bytes, 0, currentIndex - 3);
   }

   public static boolean isValidIpV4Address(CharSequence ip) {
      return isValidIpV4Address(ip, 0, ip.length());
   }

   public static boolean isValidIpV4Address(String ip) {
      return isValidIpV4Address(ip, 0, ip.length());
   }

   private static boolean isValidIpV4Address(CharSequence ip, int from, int toExcluded) {
      return ip instanceof String
         ? isValidIpV4Address((String)ip, from, toExcluded)
         : (ip instanceof AsciiString ? isValidIpV4Address((AsciiString)ip, from, toExcluded) : isValidIpV4Address0(ip, from, toExcluded));
   }

   private static boolean isValidIpV4Address(String ip, int from, int toExcluded) {
      int len = toExcluded - from;
      int i;
      int var5;
      int var6;
      int var7;
      int var8;
      return len <= 15
         && len >= 7
         && (i = ip.indexOf(46, from + 1)) > 0
         && isValidIpV4Word(ip, from, i)
         && (var7 = ip.indexOf(46, var5 = i + 2)) > 0
         && isValidIpV4Word(ip, var5 - 1, var7)
         && (var8 = ip.indexOf(46, var6 = var7 + 2)) > 0
         && isValidIpV4Word(ip, var6 - 1, var8)
         && isValidIpV4Word(ip, var8 + 1, toExcluded);
   }

   private static boolean isValidIpV4Address(AsciiString ip, int from, int toExcluded) {
      int len = toExcluded - from;
      int i;
      int var5;
      int var6;
      int var7;
      int var8;
      return len <= 15
         && len >= 7
         && (i = ip.indexOf('.', from + 1)) > 0
         && isValidIpV4Word(ip, from, i)
         && (var7 = ip.indexOf('.', var5 = i + 2)) > 0
         && isValidIpV4Word(ip, var5 - 1, var7)
         && (var8 = ip.indexOf('.', var6 = var7 + 2)) > 0
         && isValidIpV4Word(ip, var6 - 1, var8)
         && isValidIpV4Word(ip, var8 + 1, toExcluded);
   }

   private static boolean isValidIpV4Address0(CharSequence ip, int from, int toExcluded) {
      int len = toExcluded - from;
      int i;
      int var5;
      int var6;
      int var7;
      int var8;
      return len <= 15
         && len >= 7
         && (i = AsciiString.indexOf(ip, '.', from + 1)) > 0
         && isValidIpV4Word(ip, from, i)
         && (var7 = AsciiString.indexOf(ip, '.', var5 = i + 2)) > 0
         && isValidIpV4Word(ip, var5 - 1, var7)
         && (var8 = AsciiString.indexOf(ip, '.', var6 = var7 + 2)) > 0
         && isValidIpV4Word(ip, var6 - 1, var8)
         && isValidIpV4Word(ip, var8 + 1, toExcluded);
   }

   public static Inet6Address getByName(CharSequence ip) {
      return getByName(ip, true);
   }

   public static Inet6Address getByName(CharSequence ip, boolean ipv4Mapped) {
      byte[] bytes = getIPv6ByName(ip, ipv4Mapped);
      if (bytes == null) {
         return null;
      } else {
         try {
            return Inet6Address.getByAddress(null, bytes, -1);
         } catch (UnknownHostException var4) {
            throw new RuntimeException(var4);
         }
      }
   }

   static byte[] getIPv6ByName(CharSequence ip, boolean ipv4Mapped) {
      byte[] bytes = new byte[16];
      int ipLength = ip.length();
      int compressBegin = 0;
      int compressLength = 0;
      int currentIndex = 0;
      int value = 0;
      int begin = -1;
      int i = 0;
      int ipv6Separators = 0;

      int ipv4Separators;
      for (ipv4Separators = 0; i < ipLength; i++) {
         char c = ip.charAt(i);
         switch (c) {
            case '.':
               ipv4Separators++;
               int tmp = i - begin;
               if (tmp > 3
                  || begin < 0
                  || ipv4Separators > 3
                  || ipv6Separators > 0 && currentIndex + compressLength < 12
                  || i + 1 >= ipLength
                  || currentIndex >= bytes.length
                  || ipv4Separators == 1
                     && (
                        !ipv4Mapped
                           || currentIndex != 0 && !isValidIPv4Mapped(bytes, currentIndex, compressBegin, compressLength)
                           || tmp == 3
                              && (!isValidNumericChar(ip.charAt(i - 1)) || !isValidNumericChar(ip.charAt(i - 2)) || !isValidNumericChar(ip.charAt(i - 3)))
                           || tmp == 2 && (!isValidNumericChar(ip.charAt(i - 1)) || !isValidNumericChar(ip.charAt(i - 2)))
                           || tmp == 1 && !isValidNumericChar(ip.charAt(i - 1))
                     )) {
                  return null;
               }

               value <<= 3 - tmp << 2;
               begin = (value & 15) * 100 + (value >> 4 & 15) * 10 + (value >> 8 & 15);
               if (begin > 255) {
                  return null;
               }

               bytes[currentIndex++] = (byte)begin;
               value = 0;
               begin = -1;
               break;
            case ':':
               ipv6Separators++;
               if (i - begin > 4 || ipv4Separators > 0 || ipv6Separators > 8 || currentIndex + 1 >= bytes.length) {
                  return null;
               }

               value <<= 4 - (i - begin) << 2;
               if (compressLength > 0) {
                  compressLength -= 2;
               }

               bytes[currentIndex++] = (byte)((value & 15) << 4 | value >> 4 & 15);
               bytes[currentIndex++] = (byte)((value >> 8 & 15) << 4 | value >> 12 & 15);
               int tmp = i + 1;
               if (tmp < ipLength && ip.charAt(tmp) == ':') {
                  tmp++;
                  if (compressBegin != 0 || tmp < ipLength && ip.charAt(tmp) == ':') {
                     return null;
                  }

                  ipv6Separators++;
                  compressBegin = currentIndex;
                  compressLength = bytes.length - currentIndex - 2;
                  i++;
               }

               value = 0;
               begin = -1;
               break;
            default:
               if (!isValidHexChar(c) || ipv4Separators > 0 && !isValidNumericChar(c)) {
                  return null;
               }

               if (begin < 0) {
                  begin = i;
               } else if (i - begin > 4) {
                  return null;
               }

               value += StringUtil.decodeHexNibble(c) << (i - begin << 2);
         }
      }

      boolean isCompressed = compressBegin > 0;
      if (ipv4Separators <= 0) {
         int tmpx = ipLength - 1;
         if (begin > 0 && i - begin > 4
            || ipv6Separators < 2
            || !isCompressed && (ipv6Separators + 1 != 8 || ip.charAt(0) == ':' || ip.charAt(tmpx) == ':')
            || isCompressed
               && (ipv6Separators > 8 || ipv6Separators == 8 && (compressBegin <= 2 && ip.charAt(0) != ':' || compressBegin >= 14 && ip.charAt(tmpx) != ':'))
            || currentIndex + 1 >= bytes.length
            || begin < 0 && ip.charAt(tmpx - 1) != ':'
            || compressBegin > 2 && ip.charAt(0) == ':') {
            return null;
         }

         if (begin >= 0 && i - begin <= 4) {
            value <<= 4 - (i - begin) << 2;
         }

         bytes[currentIndex++] = (byte)((value & 15) << 4 | value >> 4 & 15);
         bytes[currentIndex++] = (byte)((value >> 8 & 15) << 4 | value >> 12 & 15);
      } else {
         if (begin > 0 && i - begin > 3 || ipv4Separators != 3 || currentIndex >= bytes.length) {
            return null;
         }

         if (ipv6Separators != 0
            && (
               ipv6Separators < 2
                  || (isCompressed || ipv6Separators != 6 || ip.charAt(0) == ':')
                     && (!isCompressed || ipv6Separators >= 8 || ip.charAt(0) == ':' && compressBegin > 2)
            )) {
            return null;
         }

         value <<= 3 - (i - begin) << 2;
         begin = (value & 15) * 100 + (value >> 4 & 15) * 10 + (value >> 8 & 15);
         if (begin > 255) {
            return null;
         }

         bytes[currentIndex++] = (byte)begin;
      }

      if (currentIndex < bytes.length) {
         int toBeCopiedLength = currentIndex - compressBegin;
         int targetIndex = bytes.length - toBeCopiedLength;
         System.arraycopy(bytes, compressBegin, bytes, targetIndex, toBeCopiedLength);
         Arrays.fill(bytes, compressBegin, targetIndex, (byte)0);
      }

      if (ipv4Separators > 0) {
         bytes[10] = bytes[11] = -1;
      }

      return bytes;
   }

   public static String toSocketAddressString(InetSocketAddress addr) {
      String port = String.valueOf(addr.getPort());
      StringBuilder sb;
      if (addr.isUnresolved()) {
         String hostname = getHostname(addr);
         sb = newSocketAddressStringBuilder(hostname, port, !isValidIpV6Address(hostname));
      } else {
         InetAddress address = addr.getAddress();
         String hostString = toAddressString(address);
         sb = newSocketAddressStringBuilder(hostString, port, address instanceof Inet4Address);
      }

      return sb.append(':').append(port).toString();
   }

   public static String toSocketAddressString(String host, int port) {
      String portStr = String.valueOf(port);
      return newSocketAddressStringBuilder(host, portStr, !isValidIpV6Address(host)).append(':').append(portStr).toString();
   }

   private static StringBuilder newSocketAddressStringBuilder(String host, String port, boolean ipv4) {
      int hostLen = host.length();
      if (ipv4) {
         return new StringBuilder(hostLen + 1 + port.length()).append(host);
      } else {
         StringBuilder stringBuilder = new StringBuilder(hostLen + 3 + port.length());
         return hostLen > 1 && host.charAt(0) == '[' && host.charAt(hostLen - 1) == ']'
            ? stringBuilder.append(host)
            : stringBuilder.append('[').append(host).append(']');
      }
   }

   public static String toAddressString(InetAddress ip) {
      return toAddressString(ip, false);
   }

   public static String toAddressString(InetAddress ip, boolean ipv4Mapped) {
      if (ip instanceof Inet4Address) {
         return ip.getHostAddress();
      } else if (!(ip instanceof Inet6Address)) {
         throw new IllegalArgumentException("Unhandled type: " + ip);
      } else {
         return toAddressString(ip.getAddress(), 0, ipv4Mapped);
      }
   }

   private static String toAddressString(byte[] bytes, int offset, boolean ipv4Mapped) {
      int[] words = new int[8];

      for (int i = 0; i < words.length; i++) {
         int idx = (i << 1) + offset;
         words[i] = (bytes[idx] & 255) << 8 | bytes[idx + 1] & 255;
      }

      int currentStart = -1;
      int shortestStart = -1;
      int shortestLength = 0;

      for (int i = 0; i < words.length; i++) {
         if (words[i] == 0) {
            if (currentStart < 0) {
               currentStart = i;
            }
         } else if (currentStart >= 0) {
            int currentLength = i - currentStart;
            if (currentLength > shortestLength) {
               shortestStart = currentStart;
               shortestLength = currentLength;
            }

            currentStart = -1;
         }
      }

      if (currentStart >= 0) {
         int currentLength = words.length - currentStart;
         if (currentLength > shortestLength) {
            shortestStart = currentStart;
            shortestLength = currentLength;
         }
      }

      if (shortestLength == 1) {
         shortestLength = 0;
         shortestStart = -1;
      }

      int shortestEnd = shortestStart + shortestLength;
      StringBuilder b = new StringBuilder(39);
      if (shortestEnd < 0) {
         b.append(Integer.toHexString(words[0]));

         for (int ix = 1; ix < words.length; ix++) {
            b.append(':');
            b.append(Integer.toHexString(words[ix]));
         }
      } else {
         boolean isIpv4Mapped;
         if (!inRangeEndExclusive(0, shortestStart, shortestEnd)) {
            b.append(Integer.toHexString(words[0]));
            isIpv4Mapped = false;
         } else {
            b.append("::");
            isIpv4Mapped = ipv4Mapped && shortestEnd == 5 && words[5] == 65535;
         }

         for (int ix = 1; ix < words.length; ix++) {
            if (!inRangeEndExclusive(ix, shortestStart, shortestEnd)) {
               if (!inRangeEndExclusive(ix - 1, shortestStart, shortestEnd)) {
                  if (isIpv4Mapped && ix != 6) {
                     b.append('.');
                  } else {
                     b.append(':');
                  }
               }

               if (isIpv4Mapped && ix > 5) {
                  b.append(words[ix] >> 8);
                  b.append('.');
                  b.append(words[ix] & 0xFF);
               } else {
                  b.append(Integer.toHexString(words[ix]));
               }
            } else if (!inRangeEndExclusive(ix - 1, shortestStart, shortestEnd)) {
               b.append("::");
            }
         }
      }

      return b.toString();
   }

   public static String getHostname(InetSocketAddress addr) {
      return addr.getHostString();
   }

   private static boolean inRangeEndExclusive(int value, int start, int end) {
      return value >= start && value < end;
   }

   private NetUtil() {
   }

   static {
      String prefer = SystemPropertyUtil.get("java.net.preferIPv6Addresses", "false");
      if ("true".equalsIgnoreCase(prefer.trim())) {
         IPV6_ADDRESSES_PREFERRED = true;
      } else {
         IPV6_ADDRESSES_PREFERRED = false;
      }

      logger.debug("-Djava.net.preferIPv4Stack: {}", IPV4_PREFERRED);
      logger.debug("-Djava.net.preferIPv6Addresses: {}", prefer);
      NETWORK_INTERFACES = NetUtilInitializations.networkInterfaces();
      LOCALHOST4 = NetUtilInitializations.createLocalhost4();
      LOCALHOST6 = NetUtilInitializations.createLocalhost6();
      NetUtilInitializations.NetworkIfaceAndInetAddress loopback = NetUtilInitializations.determineLoopback(NETWORK_INTERFACES, LOCALHOST4, LOCALHOST6);
      LOOPBACK_IF = loopback.iface();
      LOCALHOST = loopback.address();
      SOMAXCONN = AccessController.doPrivileged(new NetUtil.SoMaxConnAction());
   }

   private static final class SoMaxConnAction implements PrivilegedAction<Integer> {
      private SoMaxConnAction() {
      }

      public Integer run() {
         int somaxconn;
         if (PlatformDependent.isWindows()) {
            somaxconn = 200;
         } else if (PlatformDependent.isOsx()) {
            somaxconn = 128;
         } else {
            somaxconn = 4096;
         }

         File file = new File("/proc/sys/net/core/somaxconn");

         try {
            if (file.exists()) {
               BufferedReader in = new BufferedReader(new InputStreamReader(new BoundedInputStream(new FileInputStream(file))));

               try {
                  somaxconn = Integer.parseInt(in.readLine());
                  if (NetUtil.logger.isDebugEnabled()) {
                     NetUtil.logger.debug("{}: {}", file, somaxconn);
                  }
               } catch (Throwable var7) {
                  try {
                     in.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }

                  throw var7;
               }

               in.close();
            } else {
               Integer tmp = null;
               if (SystemPropertyUtil.getBoolean("io.netty.net.somaxconn.trySysctl", false)) {
                  tmp = NetUtil.sysctlGetInt("kern.ipc.somaxconn");
                  if (tmp == null) {
                     tmp = NetUtil.sysctlGetInt("kern.ipc.soacceptqueue");
                     if (tmp != null) {
                        somaxconn = tmp;
                     }
                  } else {
                     somaxconn = tmp;
                  }
               }

               if (tmp == null) {
                  NetUtil.logger.debug("Failed to get SOMAXCONN from sysctl and file {}. Default: {}", file, somaxconn);
               }
            }
         } catch (Exception var8) {
            if (NetUtil.logger.isDebugEnabled()) {
               NetUtil.logger.debug("Failed to get SOMAXCONN from sysctl and file {}. Default: {}", file, somaxconn, var8);
            }
         }

         return somaxconn;
      }
   }
}
