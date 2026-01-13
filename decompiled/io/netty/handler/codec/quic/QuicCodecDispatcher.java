package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.internal.ObjectUtil;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.Nullable;

public abstract class QuicCodecDispatcher extends ChannelInboundHandlerAdapter {
   private static final int MAX_LOCAL_CONNECTION_ID_LENGTH = 20;
   private final List<QuicCodecDispatcher.ChannelHandlerContextDispatcher> contextList = new CopyOnWriteArrayList<>();
   private final int localConnectionIdLength;

   protected QuicCodecDispatcher() {
      this(20);
   }

   protected QuicCodecDispatcher(int localConnectionIdLength) {
      this.localConnectionIdLength = ObjectUtil.checkInRange(localConnectionIdLength, 10, 20, "localConnectionIdLength");
   }

   @Override
   public final boolean isSharable() {
      return true;
   }

   @Override
   public final void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      super.handlerAdded(ctx);
      QuicCodecDispatcher.ChannelHandlerContextDispatcher ctxDispatcher = new QuicCodecDispatcher.ChannelHandlerContextDispatcher(ctx);
      this.contextList.add(ctxDispatcher);
      int idx = this.contextList.indexOf(ctxDispatcher);

      try {
         QuicConnectionIdGenerator idGenerator = this.newIdGenerator((short)idx);
         this.initChannel(ctx.channel(), this.localConnectionIdLength, idGenerator);
      } catch (Exception var5) {
         this.contextList.set(idx, null);
         throw var5;
      }
   }

   @Override
   public final void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      super.handlerRemoved(ctx);

      for (int idx = 0; idx < this.contextList.size(); idx++) {
         QuicCodecDispatcher.ChannelHandlerContextDispatcher ctxDispatcher = this.contextList.get(idx);
         if (ctxDispatcher != null && ctxDispatcher.ctx.equals(ctx)) {
            this.contextList.set(idx, null);
            break;
         }
      }
   }

   @Override
   public final void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      DatagramPacket packet = (DatagramPacket)msg;
      ByteBuf connectionId = getDestinationConnectionId(packet.content(), this.localConnectionIdLength);
      if (connectionId != null) {
         int idx = this.decodeIndex(connectionId);
         if (this.contextList.size() > idx) {
            QuicCodecDispatcher.ChannelHandlerContextDispatcher selectedCtx = this.contextList.get(idx);
            if (selectedCtx != null) {
               selectedCtx.fireChannelRead(msg);
               return;
            }
         }
      }

      ctx.fireChannelRead(msg);
   }

   @Override
   public final void channelReadComplete(ChannelHandlerContext ctx) {
      boolean dispatchForOwnContextAlready = false;

      for (int i = 0; i < this.contextList.size(); i++) {
         QuicCodecDispatcher.ChannelHandlerContextDispatcher ctxDispatcher = this.contextList.get(i);
         if (ctxDispatcher != null) {
            boolean fired = ctxDispatcher.fireChannelReadCompleteIfNeeded();
            if (fired && !dispatchForOwnContextAlready) {
               dispatchForOwnContextAlready = ctx.equals(ctxDispatcher.ctx);
            }
         }
      }

      if (!dispatchForOwnContextAlready) {
         ctx.fireChannelReadComplete();
      }
   }

   protected abstract void initChannel(Channel var1, int var2, QuicConnectionIdGenerator var3) throws Exception;

   protected int decodeIndex(ByteBuf connectionId) {
      return decodeIdx(connectionId);
   }

   @Nullable
   static ByteBuf getDestinationConnectionId(ByteBuf buffer, int localConnectionIdLength) throws QuicException {
      if (buffer.readableBytes() > 1) {
         int offset = buffer.readerIndex();
         boolean shortHeader = hasShortHeader(buffer);
         offset++;
         if (shortHeader) {
            return QuicHeaderParser.sliceCid(buffer, offset, localConnectionIdLength);
         }
      }

      return null;
   }

   static boolean hasShortHeader(ByteBuf buffer) {
      return QuicHeaderParser.hasShortHeader(buffer.getByte(buffer.readerIndex()));
   }

   static int decodeIdx(ByteBuf connectionId) {
      return connectionId.readableBytes() >= 2 ? connectionId.getUnsignedShort(connectionId.readerIndex()) : -1;
   }

   static ByteBuffer encodeIdx(ByteBuffer buffer, int idx) {
      ByteBuffer b = ByteBuffer.allocate(buffer.capacity() + 2);
      ((Buffer)b.putShort((short)idx).put(buffer)).flip();
      return b;
   }

   protected QuicConnectionIdGenerator newIdGenerator(int idx) {
      return new QuicCodecDispatcher.IndexAwareQuicConnectionIdGenerator(idx, SecureRandomQuicConnectionIdGenerator.INSTANCE);
   }

   private static final class ChannelHandlerContextDispatcher extends AtomicBoolean {
      private final ChannelHandlerContext ctx;

      ChannelHandlerContextDispatcher(ChannelHandlerContext ctx) {
         this.ctx = ctx;
      }

      void fireChannelRead(Object msg) {
         this.ctx.fireChannelRead(msg);
         this.set(true);
      }

      boolean fireChannelReadCompleteIfNeeded() {
         if (this.getAndSet(false)) {
            this.ctx.fireChannelReadComplete();
            return true;
         } else {
            return false;
         }
      }
   }

   private static final class IndexAwareQuicConnectionIdGenerator implements QuicConnectionIdGenerator {
      private final int idx;
      private final QuicConnectionIdGenerator idGenerator;

      IndexAwareQuicConnectionIdGenerator(int idx, QuicConnectionIdGenerator idGenerator) {
         this.idx = idx;
         this.idGenerator = idGenerator;
      }

      @Override
      public ByteBuffer newId(int length) {
         return length > 2 ? QuicCodecDispatcher.encodeIdx(this.idGenerator.newId(length - 2), this.idx) : this.idGenerator.newId(length);
      }

      @Override
      public ByteBuffer newId(ByteBuffer input, int length) {
         return length > 2 ? QuicCodecDispatcher.encodeIdx(this.idGenerator.newId(input, length - 2), this.idx) : this.idGenerator.newId(input, length);
      }

      @Override
      public ByteBuffer newId(ByteBuffer scid, ByteBuffer dcid, int length) {
         return length > 2
            ? QuicCodecDispatcher.encodeIdx(this.idGenerator.newId(scid, dcid, length - 2), this.idx)
            : this.idGenerator.newId(scid, dcid, length);
      }

      @Override
      public int maxConnectionIdLength() {
         return this.idGenerator.maxConnectionIdLength();
      }

      @Override
      public boolean isIdempotent() {
         return false;
      }
   }
}
