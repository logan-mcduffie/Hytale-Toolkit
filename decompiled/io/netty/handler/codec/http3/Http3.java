package io.netty.handler.codec.http3;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicClientCodecBuilder;
import io.netty.handler.codec.quic.QuicCodecBuilder;
import io.netty.handler.codec.quic.QuicServerCodecBuilder;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.handler.codec.quic.QuicStreamChannelBootstrap;
import io.netty.handler.codec.quic.QuicStreamType;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import org.jetbrains.annotations.Nullable;

public final class Http3 {
   private static final String[] H3_PROTOS = new String[]{"h3-29", "h3-30", "h3-31", "h3-32", "h3"};
   private static final AttributeKey<QuicStreamChannel> HTTP3_CONTROL_STREAM_KEY = AttributeKey.valueOf(Http3.class, "HTTP3ControlStream");
   private static final AttributeKey<QpackAttributes> QPACK_ATTRIBUTES_KEY = AttributeKey.valueOf(Http3.class, "QpackAttributes");
   public static final int MIN_INITIAL_MAX_STREAMS_UNIDIRECTIONAL = 3;
   public static final int MIN_INITIAL_MAX_STREAM_DATA_UNIDIRECTIONAL = 1024;

   private Http3() {
   }

   @Nullable
   public static QuicStreamChannel getLocalControlStream(Channel channel) {
      return channel.attr(HTTP3_CONTROL_STREAM_KEY).get();
   }

   static long maxPushIdReceived(QuicChannel channel) {
      Http3ConnectionHandler connectionHandler = Http3CodecUtils.getConnectionHandlerOrClose(channel);
      if (connectionHandler == null) {
         throw new IllegalStateException("Connection handler not found.");
      } else {
         return connectionHandler.localControlStreamHandler.maxPushIdReceived();
      }
   }

   static void setLocalControlStream(Channel channel, QuicStreamChannel controlStreamChannel) {
      channel.attr(HTTP3_CONTROL_STREAM_KEY).set(controlStreamChannel);
   }

   @Nullable
   static QpackAttributes getQpackAttributes(Channel channel) {
      return channel.attr(QPACK_ATTRIBUTES_KEY).get();
   }

   static void setQpackAttributes(Channel channel, QpackAttributes attributes) {
      channel.attr(QPACK_ATTRIBUTES_KEY).set(attributes);
   }

   public static Future<QuicStreamChannel> newRequestStream(QuicChannel channel, ChannelHandler handler) {
      return channel.createStream(QuicStreamType.BIDIRECTIONAL, requestStreamInitializer(handler));
   }

   public static QuicStreamChannelBootstrap newRequestStreamBootstrap(QuicChannel channel, ChannelHandler handler) {
      return channel.newStreamBootstrap().handler(requestStreamInitializer(handler)).type(QuicStreamType.BIDIRECTIONAL);
   }

   public static String[] supportedApplicationProtocols() {
      return (String[])H3_PROTOS.clone();
   }

   public static QuicServerCodecBuilder newQuicServerCodecBuilder() {
      return configure(new QuicServerCodecBuilder());
   }

   public static QuicClientCodecBuilder newQuicClientCodecBuilder() {
      return configure(new QuicClientCodecBuilder());
   }

   private static <T extends QuicCodecBuilder<T>> T configure(T builder) {
      return builder.initialMaxStreamsUnidirectional(3L).initialMaxStreamDataUnidirectional(1024L);
   }

   private static Http3RequestStreamInitializer requestStreamInitializer(final ChannelHandler handler) {
      return handler instanceof Http3RequestStreamInitializer ? (Http3RequestStreamInitializer)handler : new Http3RequestStreamInitializer() {
         @Override
         protected void initRequestStream(QuicStreamChannel ch) {
            ch.pipeline().addLast(handler);
         }
      };
   }
}
