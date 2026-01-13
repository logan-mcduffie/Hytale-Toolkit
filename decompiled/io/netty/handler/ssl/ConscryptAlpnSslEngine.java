package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SystemPropertyUtil;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import org.conscrypt.AllocatedBuffer;
import org.conscrypt.BufferAllocator;
import org.conscrypt.HandshakeListener;

abstract class ConscryptAlpnSslEngine extends JdkSslEngine {
   private static final boolean USE_BUFFER_ALLOCATOR = SystemPropertyUtil.getBoolean("io.netty.handler.ssl.conscrypt.useBufferAllocator", true);

   static ConscryptAlpnSslEngine newClientEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator) {
      return new ConscryptAlpnSslEngine.ClientEngine(engine, alloc, applicationNegotiator);
   }

   static ConscryptAlpnSslEngine newServerEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator) {
      return new ConscryptAlpnSslEngine.ServerEngine(engine, alloc, applicationNegotiator);
   }

   private ConscryptAlpnSslEngine(SSLEngine engine, ByteBufAllocator alloc, List<String> protocols) {
      super(engine);
      if (USE_BUFFER_ALLOCATOR) {
         org.conscrypt.Conscrypt.setBufferAllocator(engine, new ConscryptAlpnSslEngine.BufferAllocatorAdapter(alloc));
      }

      org.conscrypt.Conscrypt.setApplicationProtocols(engine, protocols.toArray(EmptyArrays.EMPTY_STRINGS));
   }

   final int calculateOutNetBufSize(int plaintextBytes, int numBuffers) {
      return this.calculateSpace(plaintextBytes, numBuffers, 2147483647L);
   }

   final int calculateRequiredOutBufSpace(int plaintextBytes, int numBuffers) {
      return this.calculateSpace(plaintextBytes, numBuffers, org.conscrypt.Conscrypt.maxEncryptedPacketLength());
   }

   private int calculateSpace(int plaintextBytes, int numBuffers, long maxPacketLength) {
      long maxOverhead = (long)org.conscrypt.Conscrypt.maxSealOverhead(this.getWrappedEngine()) * numBuffers;
      return (int)Math.min(maxPacketLength, plaintextBytes + maxOverhead);
   }

   final SSLEngineResult unwrap(ByteBuffer[] srcs, ByteBuffer[] dests) throws SSLException {
      return org.conscrypt.Conscrypt.unwrap(this.getWrappedEngine(), srcs, dests);
   }

   private static final class BufferAdapter extends AllocatedBuffer {
      private final ByteBuf nettyBuffer;
      private final ByteBuffer buffer;

      BufferAdapter(ByteBuf nettyBuffer) {
         this.nettyBuffer = nettyBuffer;
         this.buffer = nettyBuffer.nioBuffer(0, nettyBuffer.capacity());
      }

      public ByteBuffer nioBuffer() {
         return this.buffer;
      }

      public AllocatedBuffer retain() {
         this.nettyBuffer.retain();
         return this;
      }

      public AllocatedBuffer release() {
         this.nettyBuffer.release();
         return this;
      }
   }

   private static final class BufferAllocatorAdapter extends BufferAllocator {
      private final ByteBufAllocator alloc;

      BufferAllocatorAdapter(ByteBufAllocator alloc) {
         this.alloc = alloc;
      }

      public AllocatedBuffer allocateDirectBuffer(int capacity) {
         return new ConscryptAlpnSslEngine.BufferAdapter(this.alloc.directBuffer(capacity));
      }
   }

   private static final class ClientEngine extends ConscryptAlpnSslEngine {
      private final JdkApplicationProtocolNegotiator.ProtocolSelectionListener protocolListener;

      ClientEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator) {
         super(engine, alloc, applicationNegotiator.protocols());
         org.conscrypt.Conscrypt.setHandshakeListener(engine, new HandshakeListener() {
            public void onHandshakeFinished() throws SSLException {
               ClientEngine.this.selectProtocol();
            }
         });
         this.protocolListener = ObjectUtil.checkNotNull(
            applicationNegotiator.protocolListenerFactory().newListener(this, applicationNegotiator.protocols()), "protocolListener"
         );
      }

      private void selectProtocol() throws SSLException {
         String protocol = org.conscrypt.Conscrypt.getApplicationProtocol(this.getWrappedEngine());

         try {
            this.protocolListener.selected(protocol);
         } catch (Throwable var3) {
            throw SslUtils.toSSLHandshakeException(var3);
         }
      }
   }

   private static final class ServerEngine extends ConscryptAlpnSslEngine {
      private final JdkApplicationProtocolNegotiator.ProtocolSelector protocolSelector;

      ServerEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator) {
         super(engine, alloc, applicationNegotiator.protocols());
         org.conscrypt.Conscrypt.setHandshakeListener(engine, new HandshakeListener() {
            public void onHandshakeFinished() throws SSLException {
               ServerEngine.this.selectProtocol();
            }
         });
         this.protocolSelector = ObjectUtil.checkNotNull(
            applicationNegotiator.protocolSelectorFactory().newSelector(this, new LinkedHashSet<>(applicationNegotiator.protocols())), "protocolSelector"
         );
      }

      private void selectProtocol() throws SSLException {
         try {
            String protocol = org.conscrypt.Conscrypt.getApplicationProtocol(this.getWrappedEngine());
            this.protocolSelector.select(protocol != null ? Collections.singletonList(protocol) : Collections.emptyList());
         } catch (Throwable var2) {
            throw SslUtils.toSSLHandshakeException(var2);
         }
      }
   }
}
