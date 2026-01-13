package io.netty.handler.codec.quic;

import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;

final class QuicheRecvInfo {
   private QuicheRecvInfo() {
   }

   static void setRecvInfo(ByteBuffer memory, InetSocketAddress from, InetSocketAddress to) {
      int position = memory.position();

      try {
         setAddress(memory, Quiche.SIZEOF_QUICHE_RECV_INFO, Quiche.QUICHE_RECV_INFO_OFFSETOF_FROM, Quiche.QUICHE_RECV_INFO_OFFSETOF_FROM_LEN, from);
         setAddress(
            memory,
            Quiche.SIZEOF_QUICHE_RECV_INFO + Quiche.SIZEOF_SOCKADDR_STORAGE,
            Quiche.QUICHE_RECV_INFO_OFFSETOF_TO,
            Quiche.QUICHE_RECV_INFO_OFFSETOF_TO_LEN,
            to
         );
      } finally {
         ((Buffer)memory).position(position);
      }
   }

   private static void setAddress(ByteBuffer memory, int socketAddressOffset, int addrOffset, int lenOffset, InetSocketAddress address) {
      int position = memory.position();

      try {
         int sockaddrPosition = position + socketAddressOffset;
         ((Buffer)memory).position(sockaddrPosition);
         long sockaddrMemoryAddress = Quiche.memoryAddressWithPosition(memory);
         int len = SockaddrIn.setAddress(memory, address);
         if (Quiche.SIZEOF_SIZE_T == 4) {
            memory.putInt(position + addrOffset, (int)sockaddrMemoryAddress);
         } else {
            memory.putLong(position + addrOffset, sockaddrMemoryAddress);
         }

         Quiche.setPrimitiveValue(memory, position + lenOffset, Quiche.SIZEOF_SOCKLEN_T, len);
      } finally {
         ((Buffer)memory).position(position);
      }
   }
}
