package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.List;

public abstract class SslClientHelloHandler<T> extends ByteToMessageDecoder implements ChannelOutboundHandler {
   public static final int MAX_CLIENT_HELLO_LENGTH = 16777215;
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SslClientHelloHandler.class);
   private final int maxClientHelloLength;
   private boolean handshakeFailed;
   private boolean suppressRead;
   private boolean readPending;
   private ByteBuf handshakeBuffer;

   public SslClientHelloHandler() {
      this(16777215);
   }

   protected SslClientHelloHandler(int maxClientHelloLength) {
      this.maxClientHelloLength = ObjectUtil.checkInRange(maxClientHelloLength, 0, 16777215, "maxClientHelloLength");
   }

   @Override
   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      if (!this.suppressRead && !this.handshakeFailed) {
         try {
            int readerIndex = in.readerIndex();
            int readableBytes = in.readableBytes();
            int handshakeLength = -1;

            while (readableBytes >= 5) {
               int contentType = in.getUnsignedByte(readerIndex);
               switch (contentType) {
                  case 20:
                  case 21:
                     int len = SslUtils.getEncryptedPacketLength(in, readerIndex, true);
                     if (len == -2) {
                        this.handshakeFailed = true;
                        NotSslRecordException e = new NotSslRecordException("not an SSL/TLS record: " + ByteBufUtil.hexDump(in));
                        in.skipBytes(in.readableBytes());
                        ctx.fireUserEventTriggered(new SniCompletionEvent(e));
                        SslUtils.handleHandshakeFailure(ctx, e, true);
                        throw e;
                     }

                     if (len == -1) {
                        return;
                     }

                     this.select(ctx, null);
                     return;
                  case 22:
                     int majorVersion = in.getUnsignedByte(readerIndex + 1);
                     if (majorVersion == 3) {
                        int packetLength = in.getUnsignedShort(readerIndex + 3) + 5;
                        if (readableBytes < packetLength) {
                           return;
                        }

                        if (packetLength == 5) {
                           this.select(ctx, null);
                           return;
                        }

                        int endOffset = readerIndex + packetLength;
                        if (handshakeLength == -1) {
                           if (readerIndex + 4 > endOffset) {
                              return;
                           }

                           int handshakeType = in.getUnsignedByte(readerIndex + 5);
                           if (handshakeType != 1) {
                              this.select(ctx, null);
                              return;
                           }

                           handshakeLength = in.getUnsignedMedium(readerIndex + 5 + 1);
                           if (handshakeLength > this.maxClientHelloLength && this.maxClientHelloLength != 0) {
                              TooLongFrameException e = new TooLongFrameException(
                                 "ClientHello length exceeds " + this.maxClientHelloLength + ": " + handshakeLength
                              );
                              in.skipBytes(in.readableBytes());
                              ctx.fireUserEventTriggered(new SniCompletionEvent(e));
                              SslUtils.handleHandshakeFailure(ctx, e, true);
                              throw e;
                           }

                           readerIndex += 4;
                           packetLength -= 4;
                           if (handshakeLength + 4 + 5 <= packetLength) {
                              readerIndex += 5;
                              this.select(ctx, in.retainedSlice(readerIndex, handshakeLength));
                              return;
                           }

                           if (this.handshakeBuffer == null) {
                              this.handshakeBuffer = ctx.alloc().buffer(handshakeLength);
                           } else {
                              this.handshakeBuffer.clear();
                           }
                        }

                        this.handshakeBuffer.writeBytes(in, readerIndex + 5, packetLength - 5);
                        readerIndex += packetLength;
                        readableBytes -= packetLength;
                        if (handshakeLength <= this.handshakeBuffer.readableBytes()) {
                           ByteBuf clientHello = this.handshakeBuffer.setIndex(0, handshakeLength);
                           this.handshakeBuffer = null;
                           this.select(ctx, clientHello);
                           return;
                        }
                        break;
                     }
                  default:
                     this.select(ctx, null);
                     return;
               }
            }
         } catch (NotSslRecordException var14) {
            throw var14;
         } catch (TooLongFrameException var15) {
            throw var15;
         } catch (Exception var16) {
            if (logger.isDebugEnabled()) {
               logger.debug("Unexpected client hello packet: " + ByteBufUtil.hexDump(in), (Throwable)var16);
            }

            this.select(ctx, null);
         }
      }
   }

   private void releaseHandshakeBuffer() {
      releaseIfNotNull(this.handshakeBuffer);
      this.handshakeBuffer = null;
   }

   private static void releaseIfNotNull(ByteBuf buffer) {
      if (buffer != null) {
         buffer.release();
      }
   }

   private void select(final ChannelHandlerContext ctx, ByteBuf clientHello) throws Exception {
      try {
         Future<T> future = this.lookup(ctx, clientHello);
         if (future.isDone()) {
            this.onLookupComplete(ctx, future);
         } else {
            this.suppressRead = true;
            final ByteBuf finalClientHello = clientHello;
            future.addListener(new FutureListener<T>() {
               @Override
               public void operationComplete(Future<T> future) {
                  SslClientHelloHandler.releaseIfNotNull(finalClientHello);

                  try {
                     SslClientHelloHandler.this.suppressRead = false;

                     try {
                        SslClientHelloHandler.this.onLookupComplete(ctx, future);
                     } catch (DecoderException var8x) {
                        ctx.fireExceptionCaught(var8x);
                     } catch (Exception var9) {
                        ctx.fireExceptionCaught(new DecoderException(var9));
                     } catch (Throwable var10) {
                        ctx.fireExceptionCaught(var10);
                     }
                  } finally {
                     if (SslClientHelloHandler.this.readPending) {
                        SslClientHelloHandler.this.readPending = false;
                        ctx.read();
                     }
                  }
               }
            });
            clientHello = null;
         }
      } catch (Throwable var8) {
         PlatformDependent.throwException(var8);
      } finally {
         releaseIfNotNull(clientHello);
      }
   }

   @Override
   protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
      this.releaseHandshakeBuffer();
      super.handlerRemoved0(ctx);
   }

   protected abstract Future<T> lookup(ChannelHandlerContext var1, ByteBuf var2) throws Exception;

   protected abstract void onLookupComplete(ChannelHandlerContext var1, Future<T> var2) throws Exception;

   @Override
   public void read(ChannelHandlerContext ctx) throws Exception {
      if (this.suppressRead) {
         this.readPending = true;
      } else {
         ctx.read();
      }
   }

   @Override
   public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
      ctx.bind(localAddress, promise);
   }

   @Override
   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
      ctx.connect(remoteAddress, localAddress, promise);
   }

   @Override
   public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      ctx.disconnect(promise);
   }

   @Override
   public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      ctx.close(promise);
   }

   @Override
   public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      ctx.deregister(promise);
   }

   @Override
   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      ctx.write(msg, promise);
   }

   @Override
   public void flush(ChannelHandlerContext ctx) throws Exception {
      ctx.flush();
   }
}
