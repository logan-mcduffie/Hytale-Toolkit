package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class FixedLengthFrameDecoder extends ByteToMessageDecoder {
   private final int frameLength;

   public FixedLengthFrameDecoder(int frameLength) {
      ObjectUtil.checkPositive(frameLength, "frameLength");
      this.frameLength = frameLength;
   }

   @Override
   protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      Object decoded = this.decode(ctx, in);
      if (decoded != null) {
         out.add(decoded);
      }
   }

   protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
      return in.readableBytes() < this.frameLength ? null : in.readRetainedSlice(this.frameLength);
   }
}
