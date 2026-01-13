package io.netty.handler.codec.string;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.ObjectUtil;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

@ChannelHandler.Sharable
public class StringEncoder extends MessageToMessageEncoder<CharSequence> {
   private final Charset charset;

   public StringEncoder() {
      this(Charset.defaultCharset());
   }

   public StringEncoder(Charset charset) {
      super(CharSequence.class);
      this.charset = ObjectUtil.checkNotNull(charset, "charset");
   }

   protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
      if (msg.length() != 0) {
         out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg), this.charset));
      }
   }
}
