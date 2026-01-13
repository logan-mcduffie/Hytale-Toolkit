package io.netty.handler.codec.quic;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.util.AttributeKey;
import io.netty.util.internal.ObjectUtil;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

public final class QuicServerCodecBuilder extends QuicCodecBuilder<QuicServerCodecBuilder> {
   private final Map<ChannelOption<?>, Object> options = new LinkedHashMap<>();
   private final Map<AttributeKey<?>, Object> attrs = new HashMap<>();
   private final Map<ChannelOption<?>, Object> streamOptions = new LinkedHashMap<>();
   private final Map<AttributeKey<?>, Object> streamAttrs = new HashMap<>();
   private ChannelHandler handler;
   private ChannelHandler streamHandler;
   private QuicConnectionIdGenerator connectionIdAddressGenerator;
   private QuicTokenHandler tokenHandler;
   private QuicResetTokenGenerator resetTokenGenerator;

   public QuicServerCodecBuilder() {
      super(true);
   }

   private QuicServerCodecBuilder(QuicServerCodecBuilder builder) {
      super(builder);
      this.options.putAll(builder.options);
      this.attrs.putAll(builder.attrs);
      this.streamOptions.putAll(builder.streamOptions);
      this.streamAttrs.putAll(builder.streamAttrs);
      this.handler = builder.handler;
      this.streamHandler = builder.streamHandler;
      this.connectionIdAddressGenerator = builder.connectionIdAddressGenerator;
      this.tokenHandler = builder.tokenHandler;
      this.resetTokenGenerator = builder.resetTokenGenerator;
   }

   public QuicServerCodecBuilder clone() {
      return new QuicServerCodecBuilder(this);
   }

   public <T> QuicServerCodecBuilder option(ChannelOption<T> option, @Nullable T value) {
      Quic.updateOptions(this.options, option, value);
      return this.self();
   }

   public <T> QuicServerCodecBuilder attr(AttributeKey<T> key, @Nullable T value) {
      Quic.updateAttributes(this.attrs, key, value);
      return this.self();
   }

   public QuicServerCodecBuilder handler(ChannelHandler handler) {
      this.handler = ObjectUtil.checkNotNull(handler, "handler");
      return this.self();
   }

   public <T> QuicServerCodecBuilder streamOption(ChannelOption<T> option, @Nullable T value) {
      Quic.updateOptions(this.streamOptions, option, value);
      return this.self();
   }

   public <T> QuicServerCodecBuilder streamAttr(AttributeKey<T> key, @Nullable T value) {
      Quic.updateAttributes(this.streamAttrs, key, value);
      return this.self();
   }

   public QuicServerCodecBuilder streamHandler(ChannelHandler streamHandler) {
      this.streamHandler = ObjectUtil.checkNotNull(streamHandler, "streamHandler");
      return this.self();
   }

   public QuicServerCodecBuilder connectionIdAddressGenerator(QuicConnectionIdGenerator connectionIdAddressGenerator) {
      this.connectionIdAddressGenerator = connectionIdAddressGenerator;
      return this;
   }

   public QuicServerCodecBuilder tokenHandler(@Nullable QuicTokenHandler tokenHandler) {
      this.tokenHandler = tokenHandler;
      return this.self();
   }

   public QuicServerCodecBuilder resetTokenGenerator(@Nullable QuicResetTokenGenerator resetTokenGenerator) {
      this.resetTokenGenerator = resetTokenGenerator;
      return this.self();
   }

   @Override
   protected void validate() {
      super.validate();
      if (this.handler == null && this.streamHandler == null) {
         throw new IllegalStateException("handler and streamHandler not set");
      }
   }

   @Override
   ChannelHandler build(
      QuicheConfig config,
      Function<QuicChannel, ? extends QuicSslEngine> sslEngineProvider,
      Executor sslTaskExecutor,
      int localConnIdLength,
      FlushStrategy flushStrategy
   ) {
      this.validate();
      QuicTokenHandler tokenHandler = this.tokenHandler;
      if (tokenHandler == null) {
         tokenHandler = NoQuicTokenHandler.INSTANCE;
      }

      QuicConnectionIdGenerator generator = this.connectionIdAddressGenerator;
      if (generator == null) {
         generator = QuicConnectionIdGenerator.signGenerator();
      }

      QuicResetTokenGenerator resetTokenGenerator = this.resetTokenGenerator;
      if (resetTokenGenerator == null) {
         resetTokenGenerator = QuicResetTokenGenerator.signGenerator();
      }

      ChannelHandler handler = this.handler;
      ChannelHandler streamHandler = this.streamHandler;
      return new QuicheQuicServerCodec(
         config,
         localConnIdLength,
         tokenHandler,
         generator,
         resetTokenGenerator,
         flushStrategy,
         sslEngineProvider,
         sslTaskExecutor,
         handler,
         Quic.toOptionsArray(this.options),
         Quic.toAttributesArray(this.attrs),
         streamHandler,
         Quic.toOptionsArray(this.streamOptions),
         Quic.toAttributesArray(this.streamAttrs)
      );
   }
}
