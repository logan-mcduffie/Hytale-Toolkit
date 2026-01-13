package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.util.AsyncMapping;
import io.netty.util.DomainNameMapping;
import io.netty.util.Mapping;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;

public class SniHandler extends AbstractSniHandler<SslContext> {
   private static final SniHandler.Selection EMPTY_SELECTION = new SniHandler.Selection(null, null);
   protected final AsyncMapping<String, SslContext> mapping;
   private volatile SniHandler.Selection selection = EMPTY_SELECTION;

   public SniHandler(Mapping<? super String, ? extends SslContext> mapping) {
      this(new SniHandler.AsyncMappingAdapter(mapping));
   }

   public SniHandler(Mapping<? super String, ? extends SslContext> mapping, int maxClientHelloLength, long handshakeTimeoutMillis) {
      this(new SniHandler.AsyncMappingAdapter(mapping), maxClientHelloLength, handshakeTimeoutMillis);
   }

   public SniHandler(DomainNameMapping<? extends SslContext> mapping) {
      this(mapping);
   }

   public SniHandler(AsyncMapping<? super String, ? extends SslContext> mapping) {
      this(mapping, 0, 0L);
   }

   public SniHandler(AsyncMapping<? super String, ? extends SslContext> mapping, int maxClientHelloLength, long handshakeTimeoutMillis) {
      super(maxClientHelloLength, handshakeTimeoutMillis);
      this.mapping = ObjectUtil.checkNotNull(mapping, "mapping");
   }

   public SniHandler(Mapping<? super String, ? extends SslContext> mapping, long handshakeTimeoutMillis) {
      this(new SniHandler.AsyncMappingAdapter(mapping), handshakeTimeoutMillis);
   }

   public SniHandler(AsyncMapping<? super String, ? extends SslContext> mapping, long handshakeTimeoutMillis) {
      this(mapping, 0, handshakeTimeoutMillis);
   }

   public String hostname() {
      return this.selection.hostname;
   }

   public SslContext sslContext() {
      return this.selection.context;
   }

   @Override
   protected Future<SslContext> lookup(ChannelHandlerContext ctx, String hostname) throws Exception {
      return this.mapping.map(hostname, ctx.executor().newPromise());
   }

   @Override
   protected final void onLookupComplete(ChannelHandlerContext ctx, String hostname, Future<SslContext> future) throws Exception {
      if (!future.isSuccess()) {
         Throwable cause = future.cause();
         if (cause instanceof Error) {
            throw (Error)cause;
         } else {
            throw new DecoderException("failed to get the SslContext for " + hostname, cause);
         }
      } else {
         SslContext sslContext = future.getNow();
         this.selection = new SniHandler.Selection(sslContext, hostname);

         try {
            this.replaceHandler(ctx, hostname, sslContext);
         } catch (Throwable var6) {
            this.selection = EMPTY_SELECTION;
            PlatformDependent.throwException(var6);
         }
      }
   }

   protected void replaceHandler(ChannelHandlerContext ctx, String hostname, SslContext sslContext) throws Exception {
      SslHandler sslHandler = null;

      try {
         sslHandler = this.newSslHandler(sslContext, ctx.alloc());
         ctx.pipeline().replace(this, SslHandler.class.getName(), sslHandler);
         sslHandler = null;
      } finally {
         if (sslHandler != null) {
            ReferenceCountUtil.safeRelease(sslHandler.engine());
         }
      }
   }

   protected SslHandler newSslHandler(SslContext context, ByteBufAllocator allocator) {
      SslHandler sslHandler = context.newHandler(allocator);
      sslHandler.setHandshakeTimeoutMillis(this.handshakeTimeoutMillis);
      return sslHandler;
   }

   private static final class AsyncMappingAdapter implements AsyncMapping<String, SslContext> {
      private final Mapping<? super String, ? extends SslContext> mapping;

      private AsyncMappingAdapter(Mapping<? super String, ? extends SslContext> mapping) {
         this.mapping = ObjectUtil.checkNotNull(mapping, "mapping");
      }

      public Future<SslContext> map(String input, Promise<SslContext> promise) {
         SslContext context;
         try {
            context = this.mapping.map(input);
         } catch (Throwable var5) {
            return promise.setFailure(var5);
         }

         return promise.setSuccess(context);
      }
   }

   private static final class Selection {
      final SslContext context;
      final String hostname;

      Selection(SslContext context, String hostname) {
         this.context = context;
         this.hostname = hostname;
      }
   }
}
