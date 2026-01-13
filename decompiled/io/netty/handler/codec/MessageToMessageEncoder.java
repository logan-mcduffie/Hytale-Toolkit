package io.netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageEncoder<I> extends ChannelOutboundHandlerAdapter {
   private final TypeParameterMatcher matcher;

   protected MessageToMessageEncoder() {
      this.matcher = TypeParameterMatcher.find(this, MessageToMessageEncoder.class, "I");
   }

   protected MessageToMessageEncoder(Class<? extends I> outboundMessageType) {
      this.matcher = TypeParameterMatcher.get(outboundMessageType);
   }

   public boolean acceptOutboundMessage(Object msg) throws Exception {
      return this.matcher.match(msg);
   }

   @Override
   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      CodecOutputList out = null;

      try {
         if (this.acceptOutboundMessage(msg)) {
            out = CodecOutputList.newInstance();
            I cast = (I)msg;

            try {
               this.encode(ctx, cast, out);
            } catch (Throwable var30) {
               ReferenceCountUtil.safeRelease(msg);
               PlatformDependent.throwException(var30);
            }

            ReferenceCountUtil.release(msg);
            if (out.isEmpty()) {
               throw new EncoderException(StringUtil.simpleClassName(this) + " must produce at least one message.");
            }
         } else {
            ctx.write(msg, promise);
         }
      } catch (EncoderException var31) {
         throw var31;
      } catch (Throwable var32) {
         throw new EncoderException(var32);
      } finally {
         if (out != null) {
            try {
               int sizeMinusOne = out.size() - 1;
               if (sizeMinusOne == 0) {
                  ctx.write(out.getUnsafe(0), promise);
               } else if (sizeMinusOne > 0) {
                  if (promise == ctx.voidPromise()) {
                     writeVoidPromise(ctx, out);
                  } else {
                     writePromiseCombiner(ctx, out, promise);
                  }
               }
            } finally {
               out.recycle();
            }
         }
      }
   }

   private static void writeVoidPromise(ChannelHandlerContext ctx, CodecOutputList out) {
      ChannelPromise voidPromise = ctx.voidPromise();

      for (int i = 0; i < out.size(); i++) {
         ctx.write(out.getUnsafe(i), voidPromise);
      }
   }

   private static void writePromiseCombiner(ChannelHandlerContext ctx, CodecOutputList out, ChannelPromise promise) {
      PromiseCombiner combiner = new PromiseCombiner(ctx.executor());

      for (int i = 0; i < out.size(); i++) {
         combiner.add(ctx.write(out.getUnsafe(i)));
      }

      combiner.finish(promise);
   }

   protected abstract void encode(ChannelHandlerContext var1, I var2, List<Object> var3) throws Exception;
}
