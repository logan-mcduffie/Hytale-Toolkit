package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.PendingWriteQueue;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.handler.codec.quic.QuicStreamFrame;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import org.jetbrains.annotations.Nullable;

final class Http3FrameCodec extends ByteToMessageDecoder implements ChannelOutboundHandler {
   private final Http3FrameTypeValidator validator;
   private final long maxHeaderListSize;
   private final QpackDecoder qpackDecoder;
   private final QpackEncoder qpackEncoder;
   private final Http3RequestStreamCodecState encodeState;
   private final Http3RequestStreamCodecState decodeState;
   private boolean firstFrame = true;
   private boolean error;
   private long type = -1L;
   private int payLoadLength = -1;
   private QpackAttributes qpackAttributes;
   private Http3FrameCodec.ReadResumptionListener readResumptionListener;
   private Http3FrameCodec.WriteResumptionListener writeResumptionListener;

   static Http3FrameCodec.Http3FrameCodecFactory newFactory(QpackDecoder qpackDecoder, long maxHeaderListSize, QpackEncoder qpackEncoder) {
      ObjectUtil.checkNotNull(qpackEncoder, "qpackEncoder");
      ObjectUtil.checkNotNull(qpackDecoder, "qpackDecoder");
      return (validator, encodeState, decodeState) -> new Http3FrameCodec(validator, qpackDecoder, maxHeaderListSize, qpackEncoder, encodeState, decodeState);
   }

   Http3FrameCodec(
      Http3FrameTypeValidator validator,
      QpackDecoder qpackDecoder,
      long maxHeaderListSize,
      QpackEncoder qpackEncoder,
      Http3RequestStreamCodecState encodeState,
      Http3RequestStreamCodecState decodeState
   ) {
      this.validator = ObjectUtil.checkNotNull(validator, "validator");
      this.qpackDecoder = ObjectUtil.checkNotNull(qpackDecoder, "qpackDecoder");
      this.maxHeaderListSize = ObjectUtil.checkPositive(maxHeaderListSize, "maxHeaderListSize");
      this.qpackEncoder = ObjectUtil.checkNotNull(qpackEncoder, "qpackEncoder");
      this.encodeState = ObjectUtil.checkNotNull(encodeState, "encodeState");
      this.decodeState = ObjectUtil.checkNotNull(decodeState, "decodeState");
   }

   @Override
   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      this.qpackAttributes = Http3.getQpackAttributes(ctx.channel().parent());

      assert this.qpackAttributes != null;

      this.initReadResumptionListenerIfRequired(ctx);
      super.handlerAdded(ctx);
   }

   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      if (this.writeResumptionListener != null) {
         this.writeResumptionListener.drain();
      }

      super.channelInactive(ctx);
   }

   @Override
   protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
      if (this.writeResumptionListener != null) {
         this.writeResumptionListener.drain();
      }

      super.handlerRemoved0(ctx);
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      ByteBuf buffer;
      if (msg instanceof QuicStreamFrame) {
         QuicStreamFrame streamFrame = (QuicStreamFrame)msg;
         buffer = streamFrame.content().retain();
         streamFrame.release();
      } else {
         buffer = (ByteBuf)msg;
      }

      super.channelRead(ctx, buffer);
   }

   @Override
   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
      assert this.readResumptionListener != null;

      if (this.readResumptionListener.readCompleted()) {
         super.channelReadComplete(ctx);
      }
   }

   private void connectionError(ChannelHandlerContext ctx, Http3ErrorCode code, String msg, boolean fireException) {
      this.error = true;
      Http3CodecUtils.connectionError(ctx, code, msg, fireException);
   }

   private void connectionError(ChannelHandlerContext ctx, Http3Exception exception, boolean fireException) {
      this.error = true;
      Http3CodecUtils.connectionError(ctx, exception, fireException);
   }

   @Override
   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
      assert this.readResumptionListener != null;

      if (in.isReadable() && !this.readResumptionListener.isSuspended()) {
         if (this.error) {
            in.skipBytes(in.readableBytes());
         } else {
            if (this.type == -1L) {
               int typeLen = Http3CodecUtils.numBytesForVariableLengthInteger(in.getByte(in.readerIndex()));
               if (in.readableBytes() < typeLen) {
                  return;
               }

               long localType = Http3CodecUtils.readVariableLengthInteger(in, typeLen);
               if (Http3CodecUtils.isReservedHttp2FrameType(localType)) {
                  this.connectionError(ctx, Http3ErrorCode.H3_FRAME_UNEXPECTED, "Reserved type for HTTP/2 received.", true);
                  return;
               }

               try {
                  this.validator.validate(localType, this.firstFrame);
               } catch (Http3Exception var8) {
                  this.connectionError(ctx, var8, true);
                  return;
               }

               this.type = localType;
               this.firstFrame = false;
               if (!in.isReadable()) {
                  return;
               }
            }

            if (this.payLoadLength == -1) {
               int payloadLen = Http3CodecUtils.numBytesForVariableLengthInteger(in.getByte(in.readerIndex()));

               assert payloadLen <= 8;

               if (in.readableBytes() < payloadLen) {
                  return;
               }

               long len = Http3CodecUtils.readVariableLengthInteger(in, payloadLen);
               if (len > 2147483647L) {
                  this.connectionError(ctx, Http3ErrorCode.H3_EXCESSIVE_LOAD, "Received an invalid frame len.", true);
                  return;
               }

               this.payLoadLength = (int)len;
            }

            int read = this.decodeFrame(ctx, this.type, this.payLoadLength, in, out);
            if (read >= 0) {
               if (read == this.payLoadLength) {
                  this.type = -1L;
                  this.payLoadLength = -1;
               } else {
                  this.payLoadLength -= read;
               }
            }
         }
      }
   }

   private static int skipBytes(ByteBuf in, int payLoadLength) {
      in.skipBytes(payLoadLength);
      return payLoadLength;
   }

   private int decodeFrame(ChannelHandlerContext ctx, long longType, int payLoadLength, ByteBuf in, List<Object> out) {
      if (longType > 2147483647L && !Http3CodecUtils.isReservedFrameType(longType)) {
         return skipBytes(in, payLoadLength);
      } else {
         int type = (int)longType;
         switch (type) {
            case 0:
               int readable = in.readableBytes();
               if (readable == 0 && payLoadLength > 0) {
                  return 0;
               }

               int length = Math.min(readable, payLoadLength);
               out.add(new DefaultHttp3DataFrame(in.readRetainedSlice(length)));
               return length;
            case 1:
               if (!this.enforceMaxPayloadLength(ctx, in, type, payLoadLength, this.maxHeaderListSize, Http3ErrorCode.H3_EXCESSIVE_LOAD)) {
                  return 0;
               } else {
                  assert this.qpackAttributes != null;

                  if (!this.qpackAttributes.dynamicTableDisabled() && !this.qpackAttributes.decoderStreamAvailable()) {
                     assert this.readResumptionListener != null;

                     this.readResumptionListener.suspended();
                     return 0;
                  } else {
                     Http3HeadersFrame headersFrame = new DefaultHttp3HeadersFrame();
                     if (this.decodeHeaders(ctx, headersFrame.headers(), in, payLoadLength, this.decodeState.receivedFinalHeaders())) {
                        out.add(headersFrame);
                        return payLoadLength;
                     }

                     return -1;
                  }
               }
            case 2:
            case 6:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            default:
               if (!Http3CodecUtils.isReservedFrameType(longType)) {
                  return skipBytes(in, payLoadLength);
               } else {
                  if (in.readableBytes() < payLoadLength) {
                     return 0;
                  }

                  out.add(new DefaultHttp3UnknownFrame(longType, in.readRetainedSlice(payLoadLength)));
                  return payLoadLength;
               }
            case 3:
               if (!this.enforceMaxPayloadLength(ctx, in, type, payLoadLength, 8L, Http3ErrorCode.H3_FRAME_ERROR)) {
                  return 0;
               }

               int pushIdLen = Http3CodecUtils.numBytesForVariableLengthInteger(in.getByte(in.readerIndex()));
               out.add(new DefaultHttp3CancelPushFrame(Http3CodecUtils.readVariableLengthInteger(in, pushIdLen)));
               return payLoadLength;
            case 4:
               if (!this.enforceMaxPayloadLength(ctx, in, type, payLoadLength, 256L, Http3ErrorCode.H3_EXCESSIVE_LOAD)) {
                  return 0;
               }

               Http3SettingsFrame settingsFrame = this.decodeSettings(ctx, in, payLoadLength);
               if (settingsFrame != null) {
                  out.add(settingsFrame);
               }

               return payLoadLength;
            case 5:
               if (!this.enforceMaxPayloadLength(
                  ctx, in, type, payLoadLength, Math.max(this.maxHeaderListSize, this.maxHeaderListSize + 8L), Http3ErrorCode.H3_EXCESSIVE_LOAD
               )) {
                  return 0;
               } else {
                  assert this.qpackAttributes != null;

                  if (!this.qpackAttributes.dynamicTableDisabled() && !this.qpackAttributes.decoderStreamAvailable()) {
                     assert this.readResumptionListener != null;

                     this.readResumptionListener.suspended();
                     return 0;
                  } else {
                     int readerIdx = in.readerIndex();
                     int pushPromiseIdLen = Http3CodecUtils.numBytesForVariableLengthInteger(in.getByte(in.readerIndex()));
                     Http3PushPromiseFrame pushPromiseFrame = new DefaultHttp3PushPromiseFrame(Http3CodecUtils.readVariableLengthInteger(in, pushPromiseIdLen));
                     if (this.decodeHeaders(ctx, pushPromiseFrame.headers(), in, payLoadLength - pushPromiseIdLen, false)) {
                        out.add(pushPromiseFrame);
                        return payLoadLength;
                     }

                     in.readerIndex(readerIdx);
                     return -1;
                  }
               }
            case 7:
               if (!this.enforceMaxPayloadLength(ctx, in, type, payLoadLength, 8L, Http3ErrorCode.H3_FRAME_ERROR)) {
                  return 0;
               }

               int idLen = Http3CodecUtils.numBytesForVariableLengthInteger(in.getByte(in.readerIndex()));
               out.add(new DefaultHttp3GoAwayFrame(Http3CodecUtils.readVariableLengthInteger(in, idLen)));
               return payLoadLength;
            case 13:
               if (!this.enforceMaxPayloadLength(ctx, in, type, payLoadLength, 8L, Http3ErrorCode.H3_FRAME_ERROR)) {
                  return 0;
               } else {
                  int pidLen = Http3CodecUtils.numBytesForVariableLengthInteger(in.getByte(in.readerIndex()));
                  out.add(new DefaultHttp3MaxPushIdFrame(Http3CodecUtils.readVariableLengthInteger(in, pidLen)));
                  return payLoadLength;
               }
         }
      }
   }

   private boolean enforceMaxPayloadLength(ChannelHandlerContext ctx, ByteBuf in, int type, int payLoadLength, long maxPayLoadLength, Http3ErrorCode error) {
      if (payLoadLength > maxPayLoadLength) {
         this.connectionError(ctx, error, "Received an invalid frame len " + payLoadLength + " for frame of type " + type + '.', true);
         return false;
      } else {
         return in.readableBytes() >= payLoadLength;
      }
   }

   @Nullable
   private Http3SettingsFrame decodeSettings(ChannelHandlerContext ctx, ByteBuf in, int payLoadLength) {
      Http3SettingsFrame settingsFrame = new DefaultHttp3SettingsFrame();

      while (payLoadLength > 0) {
         int keyLen = Http3CodecUtils.numBytesForVariableLengthInteger(in.getByte(in.readerIndex()));
         long key = Http3CodecUtils.readVariableLengthInteger(in, keyLen);
         if (Http3CodecUtils.isReservedHttp2Setting(key)) {
            this.connectionError(ctx, Http3ErrorCode.H3_SETTINGS_ERROR, "Received a settings key that is reserved for HTTP/2.", true);
            return null;
         }

         payLoadLength -= keyLen;
         int valueLen = Http3CodecUtils.numBytesForVariableLengthInteger(in.getByte(in.readerIndex()));
         long value = Http3CodecUtils.readVariableLengthInteger(in, valueLen);
         payLoadLength -= valueLen;
         if (settingsFrame.put(key, value) != null) {
            this.connectionError(ctx, Http3ErrorCode.H3_SETTINGS_ERROR, "Received a duplicate settings key.", true);
            return null;
         }
      }

      return settingsFrame;
   }

   private boolean decodeHeaders(ChannelHandlerContext ctx, Http3Headers headers, ByteBuf in, int length, boolean trailer) {
      try {
         Http3HeadersSink sink = new Http3HeadersSink(headers, this.maxHeaderListSize, true, trailer);

         assert this.qpackAttributes != null;

         assert this.readResumptionListener != null;

         if (this.qpackDecoder.decode(this.qpackAttributes, ((QuicStreamChannel)ctx.channel()).streamId(), in, length, sink, this.readResumptionListener)) {
            sink.finish();
            return true;
         }

         this.readResumptionListener.suspended();
      } catch (Http3Exception var7) {
         this.connectionError(ctx, var7.errorCode(), var7.getMessage(), true);
      } catch (QpackException var8) {
         this.connectionError(ctx, Http3ErrorCode.QPACK_DECOMPRESSION_FAILED, "Decompression of header block failed.", true);
      } catch (Http3HeadersValidationException var9) {
         this.error = true;
         ctx.fireExceptionCaught(var9);
         Http3CodecUtils.streamError(ctx, Http3ErrorCode.H3_MESSAGE_ERROR);
      }

      return false;
   }

   @Override
   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
      assert this.qpackAttributes != null;

      if (this.writeResumptionListener != null) {
         this.writeResumptionListener.enqueue(msg, promise);
      } else if ((msg instanceof Http3HeadersFrame || msg instanceof Http3PushPromiseFrame)
         && !this.qpackAttributes.dynamicTableDisabled()
         && !this.qpackAttributes.encoderStreamAvailable()) {
         this.writeResumptionListener = Http3FrameCodec.WriteResumptionListener.newListener(ctx, this);
         this.writeResumptionListener.enqueue(msg, promise);
      } else {
         this.write0(ctx, msg, promise);
      }
   }

   private void write0(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
      try {
         if (msg instanceof Http3DataFrame) {
            writeDataFrame(ctx, (Http3DataFrame)msg, promise);
         } else if (msg instanceof Http3HeadersFrame) {
            this.writeHeadersFrame(ctx, (Http3HeadersFrame)msg, promise);
         } else if (msg instanceof Http3CancelPushFrame) {
            writeCancelPushFrame(ctx, (Http3CancelPushFrame)msg, promise);
         } else if (msg instanceof Http3SettingsFrame) {
            writeSettingsFrame(ctx, (Http3SettingsFrame)msg, promise);
         } else if (msg instanceof Http3PushPromiseFrame) {
            this.writePushPromiseFrame(ctx, (Http3PushPromiseFrame)msg, promise);
         } else if (msg instanceof Http3GoAwayFrame) {
            writeGoAwayFrame(ctx, (Http3GoAwayFrame)msg, promise);
         } else if (msg instanceof Http3MaxPushIdFrame) {
            writeMaxPushIdFrame(ctx, (Http3MaxPushIdFrame)msg, promise);
         } else if (msg instanceof Http3UnknownFrame) {
            this.writeUnknownFrame(ctx, (Http3UnknownFrame)msg, promise);
         } else {
            unsupported(promise);
         }
      } finally {
         ReferenceCountUtil.release(msg);
      }
   }

   private static void writeDataFrame(ChannelHandlerContext ctx, Http3DataFrame frame, ChannelPromise promise) {
      ByteBuf out = ctx.alloc().directBuffer(16);
      Http3CodecUtils.writeVariableLengthInteger(out, frame.type());
      Http3CodecUtils.writeVariableLengthInteger(out, frame.content().readableBytes());
      ByteBuf content = frame.content().retain();
      ctx.write(Unpooled.wrappedUnmodifiableBuffer(out, content), promise);
   }

   private void writeHeadersFrame(ChannelHandlerContext ctx, Http3HeadersFrame frame, ChannelPromise promise) {
      assert this.qpackAttributes != null;

      QuicStreamChannel channel = (QuicStreamChannel)ctx.channel();
      writeDynamicFrame(ctx, frame.type(), frame, (f, out) -> {
         this.qpackEncoder.encodeHeaders(this.qpackAttributes, out, ctx.alloc(), channel.streamId(), f.headers());
         return true;
      }, promise);
   }

   private static void writeCancelPushFrame(ChannelHandlerContext ctx, Http3CancelPushFrame frame, ChannelPromise promise) {
      writeFrameWithId(ctx, frame.type(), frame.id(), promise);
   }

   private static void writeSettingsFrame(ChannelHandlerContext ctx, Http3SettingsFrame frame, ChannelPromise promise) {
      writeDynamicFrame(ctx, frame.type(), frame, (f, out) -> {
         for (Entry<Long, Long> e : f) {
            Long key = e.getKey();
            if (Http3CodecUtils.isReservedHttp2Setting(key)) {
               Http3Exception exception = new Http3Exception(Http3ErrorCode.H3_SETTINGS_ERROR, "Received a settings key that is reserved for HTTP/2.");
               promise.setFailure(exception);
               Http3CodecUtils.connectionError(ctx, exception, false);
               return false;
            }

            Long value = e.getValue();
            int keyLen = Http3CodecUtils.numBytesForVariableLengthInteger(key);
            int valueLen = Http3CodecUtils.numBytesForVariableLengthInteger(value);
            Http3CodecUtils.writeVariableLengthInteger(out, key, keyLen);
            Http3CodecUtils.writeVariableLengthInteger(out, value, valueLen);
         }

         return true;
      }, promise);
   }

   private static <T extends Http3Frame> void writeDynamicFrame(
      ChannelHandlerContext ctx, long type, T frame, BiFunction<T, ByteBuf, Boolean> writer, ChannelPromise promise
   ) {
      ByteBuf out = ctx.alloc().directBuffer();
      int initialWriterIndex = out.writerIndex();
      int payloadStartIndex = initialWriterIndex + 16;
      out.writerIndex(payloadStartIndex);
      if (writer.apply(frame, out)) {
         int finalWriterIndex = out.writerIndex();
         int payloadLength = finalWriterIndex - payloadStartIndex;
         int len = Http3CodecUtils.numBytesForVariableLengthInteger((long)payloadLength);
         out.writerIndex(payloadStartIndex - len);
         Http3CodecUtils.writeVariableLengthInteger(out, payloadLength, len);
         int typeLength = Http3CodecUtils.numBytesForVariableLengthInteger(type);
         int startIndex = payloadStartIndex - len - typeLength;
         out.writerIndex(startIndex);
         Http3CodecUtils.writeVariableLengthInteger(out, type, typeLength);
         out.setIndex(startIndex, finalWriterIndex);
         ctx.write(out, promise);
      } else {
         out.release();
      }
   }

   private void writePushPromiseFrame(ChannelHandlerContext ctx, Http3PushPromiseFrame frame, ChannelPromise promise) {
      assert this.qpackAttributes != null;

      QuicStreamChannel channel = (QuicStreamChannel)ctx.channel();
      writeDynamicFrame(ctx, frame.type(), frame, (f, out) -> {
         long id = f.id();
         Http3CodecUtils.writeVariableLengthInteger(out, id);
         this.qpackEncoder.encodeHeaders(this.qpackAttributes, out, ctx.alloc(), channel.streamId(), f.headers());
         return true;
      }, promise);
   }

   private static void writeGoAwayFrame(ChannelHandlerContext ctx, Http3GoAwayFrame frame, ChannelPromise promise) {
      writeFrameWithId(ctx, frame.type(), frame.id(), promise);
   }

   private static void writeMaxPushIdFrame(ChannelHandlerContext ctx, Http3MaxPushIdFrame frame, ChannelPromise promise) {
      writeFrameWithId(ctx, frame.type(), frame.id(), promise);
   }

   private static void writeFrameWithId(ChannelHandlerContext ctx, long type, long id, ChannelPromise promise) {
      ByteBuf out = ctx.alloc().directBuffer(24);
      Http3CodecUtils.writeVariableLengthInteger(out, type);
      Http3CodecUtils.writeVariableLengthInteger(out, Http3CodecUtils.numBytesForVariableLengthInteger(id));
      Http3CodecUtils.writeVariableLengthInteger(out, id);
      ctx.write(out, promise);
   }

   private void writeUnknownFrame(ChannelHandlerContext ctx, Http3UnknownFrame frame, ChannelPromise promise) {
      long type = frame.type();
      if (Http3CodecUtils.isReservedHttp2FrameType(type)) {
         Http3Exception exception = new Http3Exception(Http3ErrorCode.H3_FRAME_UNEXPECTED, "Reserved type for HTTP/2 send.");
         promise.setFailure(exception);
         this.connectionError(ctx, exception.errorCode(), exception.getMessage(), false);
      } else if (!Http3CodecUtils.isReservedFrameType(type)) {
         Http3Exception exception = new Http3Exception(Http3ErrorCode.H3_FRAME_UNEXPECTED, "Non reserved type for HTTP/3 send.");
         promise.setFailure(exception);
      } else {
         ByteBuf out = ctx.alloc().directBuffer();
         Http3CodecUtils.writeVariableLengthInteger(out, type);
         Http3CodecUtils.writeVariableLengthInteger(out, frame.content().readableBytes());
         ByteBuf content = frame.content().retain();
         ctx.write(Unpooled.wrappedUnmodifiableBuffer(out, content), promise);
      }
   }

   private void initReadResumptionListenerIfRequired(ChannelHandlerContext ctx) {
      if (this.readResumptionListener == null) {
         this.readResumptionListener = new Http3FrameCodec.ReadResumptionListener(ctx, this);
      }
   }

   private static void unsupported(ChannelPromise promise) {
      promise.setFailure(new UnsupportedOperationException());
   }

   @Override
   public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) {
      ctx.bind(localAddress, promise);
   }

   @Override
   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
      ctx.connect(remoteAddress, localAddress, promise);
   }

   @Override
   public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) {
      ctx.disconnect(promise);
   }

   @Override
   public void close(ChannelHandlerContext ctx, ChannelPromise promise) {
      ctx.close(promise);
   }

   @Override
   public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) {
      ctx.deregister(promise);
   }

   @Override
   public void read(ChannelHandlerContext ctx) {
      assert this.readResumptionListener != null;

      if (this.readResumptionListener.readRequested()) {
         ctx.read();
      }
   }

   @Override
   public void flush(ChannelHandlerContext ctx) {
      if (this.writeResumptionListener != null) {
         this.writeResumptionListener.enqueueFlush();
      } else {
         ctx.flush();
      }
   }

   @FunctionalInterface
   interface Http3FrameCodecFactory {
      ChannelHandler newCodec(Http3FrameTypeValidator var1, Http3RequestStreamCodecState var2, Http3RequestStreamCodecState var3);
   }

   private static final class ReadResumptionListener implements Runnable, GenericFutureListener<Future<? super QuicStreamChannel>> {
      private static final int STATE_SUSPENDED = 128;
      private static final int STATE_READ_PENDING = 64;
      private static final int STATE_READ_COMPLETE_PENDING = 32;
      private final ChannelHandlerContext ctx;
      private final Http3FrameCodec codec;
      private byte state;

      ReadResumptionListener(ChannelHandlerContext ctx, Http3FrameCodec codec) {
         this.ctx = ctx;
         this.codec = codec;

         assert codec.qpackAttributes != null;

         if (!codec.qpackAttributes.dynamicTableDisabled() && !codec.qpackAttributes.decoderStreamAvailable()) {
            codec.qpackAttributes.whenDecoderStreamAvailable(this);
         }
      }

      void suspended() {
         assert !this.codec.qpackAttributes.dynamicTableDisabled();

         this.setState(128);
      }

      boolean readCompleted() {
         if (this.hasState(128)) {
            this.setState(32);
            return false;
         } else {
            return true;
         }
      }

      boolean readRequested() {
         if (this.hasState(128)) {
            this.setState(64);
            return false;
         } else {
            return true;
         }
      }

      boolean isSuspended() {
         return this.hasState(128);
      }

      @Override
      public void operationComplete(Future<? super QuicStreamChannel> future) {
         if (future.isSuccess()) {
            this.resume();
         } else {
            this.ctx.fireExceptionCaught(future.cause());
         }
      }

      @Override
      public void run() {
         this.resume();
      }

      private void resume() {
         this.unsetState(128);

         try {
            this.codec.channelRead(this.ctx, Unpooled.EMPTY_BUFFER);
            if (this.hasState(32)) {
               this.unsetState(32);
               this.codec.channelReadComplete(this.ctx);
            }

            if (this.hasState(64)) {
               this.unsetState(64);
               this.codec.read(this.ctx);
            }
         } catch (Exception var2) {
            this.ctx.fireExceptionCaught(var2);
         }
      }

      private void setState(int toSet) {
         this.state = (byte)(this.state | toSet);
      }

      private boolean hasState(int toCheck) {
         return (this.state & toCheck) == toCheck;
      }

      private void unsetState(int toUnset) {
         this.state = (byte)(this.state & ~toUnset);
      }
   }

   private static final class WriteResumptionListener implements GenericFutureListener<Future<? super QuicStreamChannel>> {
      private static final Object FLUSH = new Object();
      private final PendingWriteQueue queue;
      private final ChannelHandlerContext ctx;
      private final Http3FrameCodec codec;

      private WriteResumptionListener(ChannelHandlerContext ctx, Http3FrameCodec codec) {
         this.ctx = ctx;
         this.codec = codec;
         this.queue = new PendingWriteQueue(ctx);
      }

      @Override
      public void operationComplete(Future<? super QuicStreamChannel> future) {
         this.drain();
      }

      void enqueue(Object msg, ChannelPromise promise) {
         assert this.ctx.channel().eventLoop().inEventLoop();

         ReferenceCountUtil.touch(msg);
         this.queue.add(msg, promise);
      }

      void enqueueFlush() {
         assert this.ctx.channel().eventLoop().inEventLoop();

         this.queue.add(FLUSH, this.ctx.voidPromise());
      }

      void drain() {
         assert this.ctx.channel().eventLoop().inEventLoop();

         boolean flushSeen = false;

         try {
            while (true) {
               Object entry = this.queue.current();
               if (entry == null) {
                  this.codec.writeResumptionListener = null;
                  return;
               }

               if (entry == FLUSH) {
                  flushSeen = true;
                  this.queue.remove().trySuccess();
               } else {
                  this.codec.write0(this.ctx, ReferenceCountUtil.retain(entry), this.queue.remove());
               }
            }
         } finally {
            if (flushSeen) {
               this.codec.flush(this.ctx);
            }
         }
      }

      static Http3FrameCodec.WriteResumptionListener newListener(ChannelHandlerContext ctx, Http3FrameCodec codec) {
         Http3FrameCodec.WriteResumptionListener listener = new Http3FrameCodec.WriteResumptionListener(ctx, codec);

         assert codec.qpackAttributes != null;

         codec.qpackAttributes.whenEncoderStreamAvailable(listener);
         return listener;
      }
   }
}
