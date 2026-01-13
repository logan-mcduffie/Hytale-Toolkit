package io.netty.handler.codec.bytes;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

@ChannelHandler.Sharable
public class ByteArrayEncoder extends MessageToMessageEncoder<byte[]> {
   public ByteArrayEncoder() {
      super(byte[].class);
   }

   protected void encode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
      out.add(Unpooled.wrappedBuffer(msg));
   }
}
