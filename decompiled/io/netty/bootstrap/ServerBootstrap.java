package io.netty.bootstrap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.util.AttributeKey;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ServerBootstrap extends AbstractBootstrap<ServerBootstrap, ServerChannel> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ServerBootstrap.class);
   private final Map<ChannelOption<?>, Object> childOptions = new LinkedHashMap<>();
   private final Map<AttributeKey<?>, Object> childAttrs = new ConcurrentHashMap<>();
   private final ServerBootstrapConfig config = new ServerBootstrapConfig(this);
   private volatile EventLoopGroup childGroup;
   private volatile ChannelHandler childHandler;

   public ServerBootstrap() {
   }

   private ServerBootstrap(ServerBootstrap bootstrap) {
      super(bootstrap);
      this.childGroup = bootstrap.childGroup;
      this.childHandler = bootstrap.childHandler;
      synchronized (bootstrap.childOptions) {
         this.childOptions.putAll(bootstrap.childOptions);
      }

      this.childAttrs.putAll(bootstrap.childAttrs);
   }

   public ServerBootstrap group(EventLoopGroup group) {
      return this.group(group, group);
   }

   public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) {
      super.group(parentGroup);
      if (this.childGroup != null) {
         throw new IllegalStateException("childGroup set already");
      } else {
         this.childGroup = ObjectUtil.checkNotNull(childGroup, "childGroup");
         return this;
      }
   }

   public <T> ServerBootstrap childOption(ChannelOption<T> childOption, T value) {
      ObjectUtil.checkNotNull(childOption, "childOption");
      synchronized (this.childOptions) {
         if (value == null) {
            this.childOptions.remove(childOption);
         } else {
            this.childOptions.put(childOption, value);
         }

         return this;
      }
   }

   public <T> ServerBootstrap childAttr(AttributeKey<T> childKey, T value) {
      ObjectUtil.checkNotNull(childKey, "childKey");
      if (value == null) {
         this.childAttrs.remove(childKey);
      } else {
         this.childAttrs.put(childKey, value);
      }

      return this;
   }

   public ServerBootstrap childHandler(ChannelHandler childHandler) {
      this.childHandler = ObjectUtil.checkNotNull(childHandler, "childHandler");
      return this;
   }

   @Override
   void init(Channel channel) throws Throwable {
      setChannelOptions(channel, this.newOptionsArray(), logger);
      setAttributes(channel, this.newAttributesArray());
      ChannelPipeline p = channel.pipeline();
      final EventLoopGroup currentChildGroup = this.childGroup;
      final ChannelHandler currentChildHandler = this.childHandler;
      final Entry<ChannelOption<?>, Object>[] currentChildOptions = newOptionsArray(this.childOptions);
      final Entry<AttributeKey<?>, Object>[] currentChildAttrs = newAttributesArray(this.childAttrs);
      final Collection<ChannelInitializerExtension> extensions = this.getInitializerExtensions();
      p.addLast(
         new ChannelInitializer<Channel>() {
            @Override
            public void initChannel(final Channel ch) {
               final ChannelPipeline pipeline = ch.pipeline();
               ChannelHandler handler = ServerBootstrap.this.config.handler();
               if (handler != null) {
                  pipeline.addLast(handler);
               }

               ch.eventLoop()
                  .execute(
                     new Runnable() {
                        @Override
                        public void run() {
                           pipeline.addLast(
                              new ServerBootstrap.ServerBootstrapAcceptor(
                                 ch, currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs, extensions
                              )
                           );
                        }
                     }
                  );
            }
         }
      );
      if (!extensions.isEmpty() && channel instanceof ServerChannel) {
         ServerChannel serverChannel = (ServerChannel)channel;

         for (ChannelInitializerExtension extension : extensions) {
            try {
               extension.postInitializeServerListenerChannel(serverChannel);
            } catch (Exception var12) {
               logger.warn("Exception thrown from postInitializeServerListenerChannel", (Throwable)var12);
            }
         }
      }
   }

   public ServerBootstrap validate() {
      super.validate();
      if (this.childHandler == null) {
         throw new IllegalStateException("childHandler not set");
      } else {
         if (this.childGroup == null) {
            logger.warn("childGroup is not set. Using parentGroup instead.");
            this.childGroup = this.config.group();
         }

         return this;
      }
   }

   public ServerBootstrap clone() {
      return new ServerBootstrap(this);
   }

   @Deprecated
   public EventLoopGroup childGroup() {
      return this.childGroup;
   }

   final ChannelHandler childHandler() {
      return this.childHandler;
   }

   final Map<ChannelOption<?>, Object> childOptions() {
      synchronized (this.childOptions) {
         return copiedMap(this.childOptions);
      }
   }

   final Map<AttributeKey<?>, Object> childAttrs() {
      return copiedMap(this.childAttrs);
   }

   public final ServerBootstrapConfig config() {
      return this.config;
   }

   private static class ServerBootstrapAcceptor extends ChannelInboundHandlerAdapter {
      private final EventLoopGroup childGroup;
      private final ChannelHandler childHandler;
      private final Entry<ChannelOption<?>, Object>[] childOptions;
      private final Entry<AttributeKey<?>, Object>[] childAttrs;
      private final Runnable enableAutoReadTask;
      private final Collection<ChannelInitializerExtension> extensions;

      ServerBootstrapAcceptor(
         final Channel channel,
         EventLoopGroup childGroup,
         ChannelHandler childHandler,
         Entry<ChannelOption<?>, Object>[] childOptions,
         Entry<AttributeKey<?>, Object>[] childAttrs,
         Collection<ChannelInitializerExtension> extensions
      ) {
         this.childGroup = childGroup;
         this.childHandler = childHandler;
         this.childOptions = childOptions;
         this.childAttrs = childAttrs;
         this.extensions = extensions;
         this.enableAutoReadTask = new Runnable() {
            @Override
            public void run() {
               channel.config().setAutoRead(true);
            }
         };
      }

      @Override
      public void channelRead(ChannelHandlerContext ctx, Object msg) {
         final Channel child = (Channel)msg;
         child.pipeline().addLast(this.childHandler);

         try {
            AbstractBootstrap.setChannelOptions(child, this.childOptions, ServerBootstrap.logger);
         } catch (Throwable var9) {
            forceClose(child, var9);
            return;
         }

         AbstractBootstrap.setAttributes(child, this.childAttrs);
         if (!this.extensions.isEmpty()) {
            for (ChannelInitializerExtension extension : this.extensions) {
               try {
                  extension.postInitializeServerChildChannel(child);
               } catch (Exception var8) {
                  ServerBootstrap.logger.warn("Exception thrown from postInitializeServerChildChannel", (Throwable)var8);
               }
            }
         }

         try {
            this.childGroup.register(child).addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture future) throws Exception {
                  if (!future.isSuccess()) {
                     ServerBootstrap.ServerBootstrapAcceptor.forceClose(child, future.cause());
                  }
               }
            });
         } catch (Throwable var7) {
            forceClose(child, var7);
         }
      }

      private static void forceClose(Channel child, Throwable t) {
         child.unsafe().closeForcibly();
         ServerBootstrap.logger.warn("Failed to register an accepted channel: {}", child, t);
      }

      @Override
      public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
         ChannelConfig config = ctx.channel().config();
         if (config.isAutoRead()) {
            config.setAutoRead(false);
            ctx.channel().eventLoop().schedule(this.enableAutoReadTask, 1L, TimeUnit.SECONDS);
         }

         ctx.fireExceptionCaught(cause);
      }
   }
}
