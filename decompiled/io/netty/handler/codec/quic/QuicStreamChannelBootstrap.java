package io.netty.handler.codec.quic;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;

public final class QuicStreamChannelBootstrap {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(QuicStreamChannelBootstrap.class);
   private final QuicChannel parent;
   private final Map<ChannelOption<?>, Object> options = new LinkedHashMap<>();
   private final Map<AttributeKey<?>, Object> attrs = new HashMap<>();
   private ChannelHandler handler;
   private QuicStreamType type = QuicStreamType.BIDIRECTIONAL;

   QuicStreamChannelBootstrap(QuicChannel parent) {
      this.parent = ObjectUtil.checkNotNull(parent, "parent");
   }

   public <T> QuicStreamChannelBootstrap option(ChannelOption<T> option, @Nullable T value) {
      Quic.updateOptions(this.options, option, value);
      return this;
   }

   public <T> QuicStreamChannelBootstrap attr(AttributeKey<T> key, @Nullable T value) {
      Quic.updateAttributes(this.attrs, key, value);
      return this;
   }

   public QuicStreamChannelBootstrap handler(ChannelHandler streamHandler) {
      this.handler = ObjectUtil.checkNotNull(streamHandler, "streamHandler");
      return this;
   }

   public QuicStreamChannelBootstrap type(QuicStreamType type) {
      this.type = ObjectUtil.checkNotNull(type, "type");
      return this;
   }

   public Future<QuicStreamChannel> create() {
      return this.create(this.parent.eventLoop().newPromise());
   }

   public Future<QuicStreamChannel> create(Promise<QuicStreamChannel> promise) {
      if (this.handler == null) {
         throw new IllegalStateException("streamHandler not set");
      } else {
         return this.parent
            .createStream(
               this.type,
               new QuicStreamChannelBootstrap.QuicStreamChannelBootstrapHandler(
                  this.handler, Quic.toOptionsArray(this.options), Quic.toAttributesArray(this.attrs)
               ),
               promise
            );
      }
   }

   private static final class QuicStreamChannelBootstrapHandler extends ChannelInitializer<QuicStreamChannel> {
      private final ChannelHandler streamHandler;
      private final Entry<ChannelOption<?>, Object>[] streamOptions;
      private final Entry<AttributeKey<?>, Object>[] streamAttrs;

      QuicStreamChannelBootstrapHandler(
         ChannelHandler streamHandler, Entry<ChannelOption<?>, Object>[] streamOptions, Entry<AttributeKey<?>, Object>[] streamAttrs
      ) {
         this.streamHandler = streamHandler;
         this.streamOptions = streamOptions;
         this.streamAttrs = streamAttrs;
      }

      protected void initChannel(QuicStreamChannel ch) {
         Quic.setupChannel(ch, this.streamOptions, this.streamAttrs, this.streamHandler, QuicStreamChannelBootstrap.logger);
      }
   }
}
