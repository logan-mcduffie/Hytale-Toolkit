package io.netty.handler.codec.quic;

import io.netty.util.concurrent.FastThreadLocal;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.Nullable;

final class QuicheSendInfo {
   private static final FastThreadLocal<byte[]> IPV4_ARRAYS = new FastThreadLocal<byte[]>() {
      protected byte[] initialValue() {
         return new byte[4];
      }
   };
   private static final FastThreadLocal<byte[]> IPV6_ARRAYS = new FastThreadLocal<byte[]>() {
      protected byte[] initialValue() {
         return new byte[16];
      }
   };
   private static final byte[] TIMESPEC_ZEROOUT = new byte[Quiche.SIZEOF_TIMESPEC];

   private QuicheSendInfo() {
   }

   @Nullable
   static InetSocketAddress getToAddress(ByteBuffer memory) {
      return getAddress(memory, Quiche.QUICHE_SEND_INFO_OFFSETOF_TO_LEN, Quiche.QUICHE_SEND_INFO_OFFSETOF_TO);
   }

   @Nullable
   static InetSocketAddress getFromAddress(ByteBuffer memory) {
      return getAddress(memory, Quiche.QUICHE_SEND_INFO_OFFSETOF_FROM_LEN, Quiche.QUICHE_SEND_INFO_OFFSETOF_FROM);
   }

   @Nullable
   private static InetSocketAddress getAddress(ByteBuffer memory, int lenOffset, int addressOffset) {
      int position = memory.position();

      InetSocketAddress var6;
      try {
         long len = getLen(memory, position + lenOffset);
         ((Buffer)memory).position(position + addressOffset);
         if (len != Quiche.SIZEOF_SOCKADDR_IN) {
            assert len == (long)Quiche.SIZEOF_SOCKADDR_IN6;

            return SockaddrIn.getIPv6(memory, IPV6_ARRAYS.get(), IPV4_ARRAYS.get());
         }

         var6 = SockaddrIn.getIPv4(memory, IPV4_ARRAYS.get());
      } finally {
         ((Buffer)memory).position(position);
      }

      return var6;
   }

   private static long getLen(ByteBuffer memory, int index) {
      return Quiche.getPrimitiveValue(memory, index, Quiche.SIZEOF_SOCKLEN_T);
   }

   static void setSendInfo(ByteBuffer memory, InetSocketAddress from, InetSocketAddress to) {
      int position = memory.position();

      try {
         setAddress(memory, Quiche.QUICHE_SEND_INFO_OFFSETOF_FROM, Quiche.QUICHE_SEND_INFO_OFFSETOF_FROM_LEN, from);
         setAddress(memory, Quiche.QUICHE_SEND_INFO_OFFSETOF_TO, Quiche.QUICHE_SEND_INFO_OFFSETOF_TO_LEN, to);
         ((Buffer)memory).position(position + Quiche.QUICHE_SEND_INFO_OFFSETOF_AT);
         memory.put(TIMESPEC_ZEROOUT);
      } finally {
         ((Buffer)memory).position(position);
      }
   }

   private static void setAddress(ByteBuffer memory, int addrOffset, int lenOffset, InetSocketAddress addr) {
      int position = memory.position();

      try {
         ((Buffer)memory).position(position + addrOffset);
         int len = SockaddrIn.setAddress(memory, addr);
         Quiche.setPrimitiveValue(memory, position + lenOffset, Quiche.SIZEOF_SOCKLEN_T, len);
      } finally {
         ((Buffer)memory).position(position);
      }
   }

   static long getAtNanos(ByteBuffer memory) {
      long sec = Quiche.getPrimitiveValue(memory, Quiche.QUICHE_SEND_INFO_OFFSETOF_AT + Quiche.TIMESPEC_OFFSETOF_TV_SEC, Quiche.SIZEOF_TIME_T);
      long nsec = Quiche.getPrimitiveValue(memory, Quiche.QUICHE_SEND_INFO_OFFSETOF_AT + Quiche.TIMESPEC_OFFSETOF_TV_SEC, Quiche.SIZEOF_LONG);
      return TimeUnit.SECONDS.toNanos(sec) + nsec;
   }

   static boolean isSameAddress(ByteBuffer memory, ByteBuffer memory2) {
      return Quiche.isSameAddress(memory, memory2, Quiche.QUICHE_SEND_INFO_OFFSETOF_TO);
   }
}
