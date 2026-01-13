package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.handler.codec.quic.QuicStreamType;
import java.util.function.LongFunction;
import org.jetbrains.annotations.Nullable;

public abstract class Http3ConnectionHandler extends ChannelInboundHandlerAdapter {
   final Http3FrameCodec.Http3FrameCodecFactory codecFactory;
   final LongFunction<ChannelHandler> unknownInboundStreamHandlerFactory;
   final boolean disableQpackDynamicTable;
   final Http3ControlStreamInboundHandler localControlStreamHandler;
   final Http3ControlStreamOutboundHandler remoteControlStreamHandler;
   final QpackDecoder qpackDecoder;
   final QpackEncoder qpackEncoder;
   private boolean controlStreamCreationInProgress;
   final long maxTableCapacity;

   Http3ConnectionHandler(
      boolean server,
      @Nullable ChannelHandler inboundControlStreamHandler,
      @Nullable LongFunction<ChannelHandler> unknownInboundStreamHandlerFactory,
      @Nullable Http3SettingsFrame localSettings,
      boolean disableQpackDynamicTable
   ) {
      this.unknownInboundStreamHandlerFactory = unknownInboundStreamHandlerFactory;
      this.disableQpackDynamicTable = disableQpackDynamicTable;
      DefaultHttp3SettingsFrame var8;
      if (localSettings == null) {
         var8 = new DefaultHttp3SettingsFrame();
      } else {
         var8 = DefaultHttp3SettingsFrame.copyOf(localSettings);
      }

      Long maxFieldSectionSize = var8.get(Http3SettingsFrame.HTTP3_SETTINGS_MAX_FIELD_SECTION_SIZE);
      if (maxFieldSectionSize == null) {
         maxFieldSectionSize = Long.MAX_VALUE;
      }

      this.maxTableCapacity = var8.getOrDefault(Http3SettingsFrame.HTTP3_SETTINGS_QPACK_MAX_TABLE_CAPACITY, 0L);
      int maxBlockedStreams = Math.toIntExact(var8.getOrDefault(Http3SettingsFrame.HTTP3_SETTINGS_QPACK_BLOCKED_STREAMS, 0L));
      this.qpackDecoder = new QpackDecoder(this.maxTableCapacity, maxBlockedStreams);
      this.qpackEncoder = new QpackEncoder();
      this.codecFactory = Http3FrameCodec.newFactory(this.qpackDecoder, maxFieldSectionSize, this.qpackEncoder);
      this.remoteControlStreamHandler = new Http3ControlStreamOutboundHandler(
         server,
         var8,
         this.codecFactory.newCodec(Http3FrameTypeValidator.NO_VALIDATION, Http3RequestStreamCodecState.NO_STATE, Http3RequestStreamCodecState.NO_STATE)
      );
      this.localControlStreamHandler = new Http3ControlStreamInboundHandler(
         server, inboundControlStreamHandler, this.qpackEncoder, this.remoteControlStreamHandler
      );
   }

   private void createControlStreamIfNeeded(ChannelHandlerContext ctx) {
      if (!this.controlStreamCreationInProgress && Http3.getLocalControlStream(ctx.channel()) == null) {
         this.controlStreamCreationInProgress = true;
         QuicChannel channel = (QuicChannel)ctx.channel();
         channel.createStream(QuicStreamType.UNIDIRECTIONAL, this.remoteControlStreamHandler).addListener(f -> {
            if (!f.isSuccess()) {
               ctx.fireExceptionCaught(new Http3Exception(Http3ErrorCode.H3_STREAM_CREATION_ERROR, "Unable to open control stream", f.cause()));
               ctx.close();
            } else {
               Http3.setLocalControlStream(channel, f.getNow());
            }
         });
      }
   }

   public final boolean isGoAwayReceived() {
      return this.localControlStreamHandler.isGoAwayReceived();
   }

   final ChannelHandler newCodec(Http3RequestStreamCodecState encodeState, Http3RequestStreamCodecState decodeState) {
      return this.codecFactory.newCodec(Http3RequestStreamFrameTypeValidator.INSTANCE, encodeState, decodeState);
   }

   final ChannelHandler newRequestStreamValidationHandler(
      QuicStreamChannel forStream, Http3RequestStreamCodecState encodeState, Http3RequestStreamCodecState decodeState
   ) {
      QpackAttributes qpackAttributes = Http3.getQpackAttributes(forStream.parent());

      assert qpackAttributes != null;

      return this.localControlStreamHandler.isServer()
         ? Http3RequestStreamValidationHandler.newServerValidator(qpackAttributes, this.qpackDecoder, encodeState, decodeState)
         : Http3RequestStreamValidationHandler.newClientValidator(
            this.localControlStreamHandler::isGoAwayReceived, qpackAttributes, this.qpackDecoder, encodeState, decodeState
         );
   }

   final ChannelHandler newPushStreamValidationHandler(QuicStreamChannel forStream, Http3RequestStreamCodecState decodeState) {
      if (this.localControlStreamHandler.isServer()) {
         return Http3PushStreamServerValidationHandler.INSTANCE;
      } else {
         QpackAttributes qpackAttributes = Http3.getQpackAttributes(forStream.parent());

         assert qpackAttributes != null;

         return new Http3PushStreamClientValidationHandler(qpackAttributes, this.qpackDecoder, decodeState);
      }
   }

   @Override
   public void handlerAdded(ChannelHandlerContext ctx) {
      QuicChannel channel = (QuicChannel)ctx.channel();
      Http3.setQpackAttributes(channel, new QpackAttributes(channel, this.disableQpackDynamicTable));
      if (ctx.channel().isActive()) {
         this.createControlStreamIfNeeded(ctx);
      }
   }

   @Override
   public void channelActive(ChannelHandlerContext ctx) {
      this.createControlStreamIfNeeded(ctx);
      ctx.fireChannelActive();
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) {
      if (msg instanceof QuicStreamChannel) {
         QuicStreamChannel channel = (QuicStreamChannel)msg;
         switch (channel.type()) {
            case BIDIRECTIONAL:
               this.initBidirectionalStream(ctx, channel);
               break;
            case UNIDIRECTIONAL:
               this.initUnidirectionalStream(ctx, channel);
               break;
            default:
               throw new Error("Unexpected channel type: " + channel.type());
         }
      }

      ctx.fireChannelRead(msg);
   }

   abstract void initBidirectionalStream(ChannelHandlerContext var1, QuicStreamChannel var2);

   abstract void initUnidirectionalStream(ChannelHandlerContext var1, QuicStreamChannel var2);

   long maxTableCapacity() {
      return this.maxTableCapacity;
   }

   @Override
   public boolean isSharable() {
      return false;
   }
}
