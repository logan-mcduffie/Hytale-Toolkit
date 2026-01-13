package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

final class QuicheQuicConnection {
   private static final int TOTAL_RECV_INFO_SIZE = Quiche.SIZEOF_QUICHE_RECV_INFO + Quiche.SIZEOF_SOCKADDR_STORAGE + Quiche.SIZEOF_SOCKADDR_STORAGE;
   private static final ResourceLeakDetector<QuicheQuicConnection> leakDetector = ResourceLeakDetectorFactory.instance()
      .newResourceLeakDetector(QuicheQuicConnection.class);
   private final QuicheQuicSslEngine engine;
   private final ResourceLeakTracker<QuicheQuicConnection> leakTracker;
   final long ssl;
   private ReferenceCounted refCnt;
   private final ByteBuf recvInfoBuffer;
   private final ByteBuf sendInfoBuffer;
   private boolean sendInfoFirst = true;
   private final ByteBuffer recvInfoBuffer1;
   private final ByteBuffer sendInfoBuffer1;
   private final ByteBuffer sendInfoBuffer2;
   private long connection;

   QuicheQuicConnection(long connection, long ssl, QuicheQuicSslEngine engine, ReferenceCounted refCnt) {
      assert connection != -1L;

      this.connection = connection;
      this.ssl = ssl;
      this.engine = engine;
      this.refCnt = refCnt;
      this.recvInfoBuffer = Quiche.allocateNativeOrder(TOTAL_RECV_INFO_SIZE);
      this.sendInfoBuffer = Quiche.allocateNativeOrder(2 * Quiche.SIZEOF_QUICHE_SEND_INFO);
      this.recvInfoBuffer.setZero(0, this.recvInfoBuffer.capacity());
      this.sendInfoBuffer.setZero(0, this.sendInfoBuffer.capacity());
      this.recvInfoBuffer1 = this.recvInfoBuffer.nioBuffer(0, TOTAL_RECV_INFO_SIZE);
      this.sendInfoBuffer1 = this.sendInfoBuffer.nioBuffer(0, Quiche.SIZEOF_QUICHE_SEND_INFO);
      this.sendInfoBuffer2 = this.sendInfoBuffer.nioBuffer(Quiche.SIZEOF_QUICHE_SEND_INFO, Quiche.SIZEOF_QUICHE_SEND_INFO);
      this.engine.connection = this;
      this.leakTracker = leakDetector.track(this);
   }

   synchronized void reattach(ReferenceCounted refCnt) {
      this.refCnt.release();
      this.refCnt = refCnt;
   }

   void free() {
      this.free(true);
   }

   boolean isFreed() {
      return this.connection == -1L;
   }

   private void free(boolean closeLeakTracker) {
      boolean release = false;
      synchronized (this) {
         if (this.connection != -1L) {
            try {
               BoringSSL.SSL_cleanup(this.ssl);
               Quiche.quiche_conn_free(this.connection);
               this.engine.ctx.remove(this.engine);
               release = true;
               this.refCnt.release();
            } finally {
               this.connection = -1L;
            }
         }
      }

      if (release) {
         this.recvInfoBuffer.release();
         this.sendInfoBuffer.release();
         if (closeLeakTracker && this.leakTracker != null) {
            this.leakTracker.close(this);
         }
      }
   }

   @Nullable
   Runnable sslTask() {
      Runnable task;
      synchronized (this) {
         if (this.connection != -1L) {
            task = BoringSSL.SSL_getTask(this.ssl);
         } else {
            task = null;
         }
      }

      return task == null ? null : () -> {
         if (this.connection != -1L) {
            task.run();
         }
      };
   }

   @Nullable
   QuicConnectionAddress sourceId() {
      return this.connectionId(() -> Quiche.quiche_conn_source_id(this.connection));
   }

   @Nullable
   QuicConnectionAddress destinationId() {
      return this.connectionId(() -> Quiche.quiche_conn_destination_id(this.connection));
   }

   @Nullable
   QuicConnectionAddress connectionId(Supplier<byte[]> idSupplier) {
      byte[] id;
      synchronized (this) {
         if (this.connection == -1L) {
            return null;
         }

         id = idSupplier.get();
      }

      return id == null ? QuicConnectionAddress.NULL_LEN : new QuicConnectionAddress(id);
   }

   @Nullable
   QuicheQuicTransportParameters peerParameters() {
      long[] ret;
      synchronized (this) {
         if (this.connection == -1L) {
            return null;
         }

         ret = Quiche.quiche_conn_peer_transport_params(this.connection);
      }

      return ret == null ? null : new QuicheQuicTransportParameters(ret);
   }

   QuicheQuicSslEngine engine() {
      return this.engine;
   }

   long address() {
      assert this.connection != -1L;

      return this.connection;
   }

   void init(InetSocketAddress local, InetSocketAddress remote, Consumer<String> sniSelectedCallback) {
      assert this.connection != -1L;

      assert this.recvInfoBuffer.refCnt() != 0;

      assert this.sendInfoBuffer.refCnt() != 0;

      QuicheRecvInfo.setRecvInfo(this.recvInfoBuffer1, remote, local);
      QuicheSendInfo.setSendInfo(this.sendInfoBuffer1, local, remote);
      QuicheSendInfo.setSendInfo(this.sendInfoBuffer2, local, remote);
      this.engine.sniSelectedCallback = sniSelectedCallback;
   }

   ByteBuffer nextRecvInfo() {
      assert this.recvInfoBuffer.refCnt() != 0;

      return this.recvInfoBuffer1;
   }

   ByteBuffer nextSendInfo() {
      assert this.sendInfoBuffer.refCnt() != 0;

      this.sendInfoFirst = !this.sendInfoFirst;
      return this.sendInfoFirst ? this.sendInfoBuffer1 : this.sendInfoBuffer2;
   }

   boolean isSendInfoChanged() {
      assert this.sendInfoBuffer.refCnt() != 0;

      return !QuicheSendInfo.isSameAddress(this.sendInfoBuffer1, this.sendInfoBuffer2);
   }

   boolean isClosed() {
      return this.isFreed() || Quiche.quiche_conn_is_closed(this.connection);
   }

   @Override
   protected void finalize() throws Throwable {
      try {
         this.free(false);
      } finally {
         super.finalize();
      }
   }
}
