package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Signal;
import io.netty.util.internal.StringUtil;
import java.util.List;

public abstract class ReplayingDecoder<S> extends ByteToMessageDecoder {
   static final Signal REPLAY = Signal.valueOf(ReplayingDecoder.class, "REPLAY");
   private final ReplayingDecoderByteBuf replayable = new ReplayingDecoderByteBuf();
   private S state;
   private int checkpoint = -1;

   protected ReplayingDecoder() {
      this(null);
   }

   protected ReplayingDecoder(S initialState) {
      this.state = initialState;
   }

   protected void checkpoint() {
      this.checkpoint = this.internalBuffer().readerIndex();
   }

   protected void checkpoint(S state) {
      this.checkpoint();
      this.state(state);
   }

   protected S state() {
      return this.state;
   }

   protected S state(S newState) {
      S oldState = this.state;
      this.state = newState;
      return oldState;
   }

   @Override
   final void channelInputClosed(ChannelHandlerContext ctx, List<Object> out) throws Exception {
      try {
         this.replayable.terminate();
         if (this.cumulation != null) {
            this.callDecode(ctx, this.internalBuffer(), out);
         } else {
            this.replayable.setCumulation(Unpooled.EMPTY_BUFFER);
         }

         this.decodeLast(ctx, this.replayable, out);
      } catch (Signal var4) {
         var4.expect(REPLAY);
      }
   }

   @Override
   protected void callDecode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
      this.replayable.setCumulation(in);

      try {
         while (in.isReadable()) {
            int oldReaderIndex = this.checkpoint = in.readerIndex();
            int outSize = out.size();
            if (outSize > 0) {
               fireChannelRead(ctx, out, outSize);
               out.clear();
               if (ctx.isRemoved()) {
                  break;
               }

               outSize = 0;
            }

            S oldState = this.state;
            int oldInputLength = in.readableBytes();

            try {
               this.decodeRemovalReentryProtection(ctx, this.replayable, out);
               if (ctx.isRemoved()) {
                  break;
               }

               if (outSize == out.size()) {
                  if (oldInputLength == in.readableBytes() && oldState == this.state) {
                     throw new DecoderException(
                        StringUtil.simpleClassName(this.getClass())
                           + ".decode() must consume the inbound data or change its state if it did not decode anything."
                     );
                  }
                  continue;
               }
            } catch (Signal var10) {
               var10.expect(REPLAY);
               if (!ctx.isRemoved()) {
                  int checkpoint = this.checkpoint;
                  if (checkpoint >= 0) {
                     in.readerIndex(checkpoint);
                  }
               }
               break;
            }

            if (oldReaderIndex == in.readerIndex() && oldState == this.state) {
               throw new DecoderException(
                  StringUtil.simpleClassName(this.getClass()) + ".decode() method must consume the inbound data or change its state if it decoded something."
               );
            }

            if (this.isSingleDecode()) {
               break;
            }
         }
      } catch (DecoderException var11) {
         throw var11;
      } catch (Exception var12) {
         throw new DecoderException(var12);
      }
   }
}
