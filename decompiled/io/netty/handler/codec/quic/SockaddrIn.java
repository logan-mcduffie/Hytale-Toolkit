package io.netty.handler.codec.quic;

import io.netty.util.internal.PlatformDependent;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.jetbrains.annotations.Nullable;

final class SockaddrIn {
   static final byte[] IPV4_MAPPED_IPV6_PREFIX = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1};
   static final int IPV4_ADDRESS_LENGTH = 4;
   static final int IPV6_ADDRESS_LENGTH = 16;
   static final byte[] SOCKADDR_IN6_EMPTY_ARRAY = new byte[Quiche.SIZEOF_SOCKADDR_IN6];
   static final byte[] SOCKADDR_IN_EMPTY_ARRAY = new byte[Quiche.SIZEOF_SOCKADDR_IN];

   private SockaddrIn() {
   }

   static int cmp(long memory, long memory2) {
      return Quiche.sockaddr_cmp(memory, memory2);
   }

   static int setAddress(ByteBuffer memory, InetSocketAddress address) {
      InetAddress addr = address.getAddress();
      return setAddress(addr instanceof Inet6Address, memory, address);
   }

   static int setAddress(boolean ipv6, ByteBuffer memory, InetSocketAddress address) {
      return ipv6 ? setIPv6(memory, address.getAddress(), address.getPort()) : setIPv4(memory, address.getAddress(), address.getPort());
   }

   static int setIPv4(ByteBuffer memory, InetAddress address, int port) {
      int position = memory.position();

      int var6;
      try {
         memory.put(SOCKADDR_IN_EMPTY_ARRAY);
         memory.putShort(position + Quiche.SOCKADDR_IN_OFFSETOF_SIN_FAMILY, Quiche.AF_INET);
         memory.putShort(position + Quiche.SOCKADDR_IN_OFFSETOF_SIN_PORT, (short)port);
         byte[] bytes = address.getAddress();
         int offset = 0;
         if (bytes.length == 16) {
            offset = IPV4_MAPPED_IPV6_PREFIX.length;
         }

         assert bytes.length == offset + 4;

         ((Buffer)memory).position(position + Quiche.SOCKADDR_IN_OFFSETOF_SIN_ADDR + Quiche.IN_ADDRESS_OFFSETOF_S_ADDR);
         memory.put(bytes, offset, 4);
         var6 = Quiche.SIZEOF_SOCKADDR_IN;
      } finally {
         ((Buffer)memory).position(position);
      }

      return var6;
   }

   static int setIPv6(ByteBuffer memory, InetAddress address, int port) {
      int position = memory.position();

      int var6;
      try {
         memory.put(SOCKADDR_IN6_EMPTY_ARRAY);
         memory.putShort(position + Quiche.SOCKADDR_IN6_OFFSETOF_SIN6_FAMILY, Quiche.AF_INET6);
         memory.putShort(position + Quiche.SOCKADDR_IN6_OFFSETOF_SIN6_PORT, (short)port);
         byte[] bytes = address.getAddress();
         int offset = Quiche.SOCKADDR_IN6_OFFSETOF_SIN6_ADDR + Quiche.IN6_ADDRESS_OFFSETOF_S6_ADDR;
         if (bytes.length == 4) {
            ((Buffer)memory).position(position + offset);
            memory.put(IPV4_MAPPED_IPV6_PREFIX);
            ((Buffer)memory).position(position + offset + IPV4_MAPPED_IPV6_PREFIX.length);
            memory.put(bytes, 0, 4);
         } else {
            ((Buffer)memory).position(position + offset);
            memory.put(bytes, 0, 16);
            memory.putInt(position + Quiche.SOCKADDR_IN6_OFFSETOF_SIN6_SCOPE_ID, ((Inet6Address)address).getScopeId());
         }

         var6 = Quiche.SIZEOF_SOCKADDR_IN6;
      } finally {
         ((Buffer)memory).position(position);
      }

      return var6;
   }

   @Nullable
   static InetSocketAddress getIPv4(ByteBuffer memory, byte[] tmpArray) {
      assert tmpArray.length == 4;

      int position = memory.position();

      Object var5;
      try {
         int port = memory.getShort(position + Quiche.SOCKADDR_IN_OFFSETOF_SIN_PORT) & '\uffff';
         ((Buffer)memory).position(position + Quiche.SOCKADDR_IN_OFFSETOF_SIN_ADDR + Quiche.IN_ADDRESS_OFFSETOF_S_ADDR);
         memory.get(tmpArray);

         try {
            return new InetSocketAddress(InetAddress.getByAddress(tmpArray), port);
         } catch (UnknownHostException var9) {
            var5 = null;
         }
      } finally {
         ((Buffer)memory).position(position);
      }

      return (InetSocketAddress)var5;
   }

   @Nullable
   static InetSocketAddress getIPv6(ByteBuffer memory, byte[] ipv6Array, byte[] ipv4Array) {
      assert ipv6Array.length == 16;

      assert ipv4Array.length == 4;

      int position = memory.position();

      Object ignore;
      try {
         int port = memory.getShort(position + Quiche.SOCKADDR_IN6_OFFSETOF_SIN6_PORT) & '\uffff';
         ((Buffer)memory).position(position + Quiche.SOCKADDR_IN6_OFFSETOF_SIN6_ADDR + Quiche.IN6_ADDRESS_OFFSETOF_S6_ADDR);
         memory.get(ipv6Array);
         if (!PlatformDependent.equals(ipv6Array, 0, IPV4_MAPPED_IPV6_PREFIX, 0, IPV4_MAPPED_IPV6_PREFIX.length)) {
            int scopeId = memory.getInt(position + Quiche.SOCKADDR_IN6_OFFSETOF_SIN6_SCOPE_ID);

            try {
               return new InetSocketAddress(Inet6Address.getByAddress(null, ipv6Array, scopeId), port);
            } catch (UnknownHostException var12) {
               return null;
            }
         }

         System.arraycopy(ipv6Array, IPV4_MAPPED_IPV6_PREFIX.length, ipv4Array, 0, 4);

         try {
            return new InetSocketAddress(Inet4Address.getByAddress(ipv4Array), port);
         } catch (UnknownHostException var13) {
            ignore = null;
         }
      } finally {
         ((Buffer)memory).position(position);
      }

      return (InetSocketAddress)ignore;
   }
}
