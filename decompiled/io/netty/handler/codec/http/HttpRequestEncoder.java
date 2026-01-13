package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.CharsetUtil;

public class HttpRequestEncoder extends HttpObjectEncoder<HttpRequest> {
   private static final char SLASH = '/';
   private static final char QUESTION_MARK = '?';
   private static final int SLASH_AND_SPACE_SHORT = 12064;
   private static final int SPACE_SLASH_AND_SPACE_MEDIUM = 2109216;

   @Override
   public boolean acceptOutboundMessage(Object msg) throws Exception {
      return super.acceptOutboundMessage(msg) && !(msg instanceof HttpResponse);
   }

   protected void encodeInitialLine(ByteBuf buf, HttpRequest request) throws Exception {
      ByteBufUtil.copy(request.method().asciiName(), buf);
      String uri = request.uri();
      if (uri.isEmpty()) {
         ByteBufUtil.writeMediumBE(buf, 2109216);
      } else {
         CharSequence uriCharSequence = uri;
         boolean needSlash = false;
         int start = uri.indexOf("://");
         if (start != -1 && uri.charAt(0) != '/') {
            start += 3;
            int index = uri.indexOf(63, start);
            if (index == -1) {
               if (uri.lastIndexOf(47) < start) {
                  needSlash = true;
               }
            } else if (uri.lastIndexOf(47, index) < start) {
               uriCharSequence = new StringBuilder(uri).insert(index, '/');
            }
         }

         buf.writeByte(32).writeCharSequence(uriCharSequence, CharsetUtil.UTF_8);
         if (needSlash) {
            ByteBufUtil.writeShortBE(buf, 12064);
         } else {
            buf.writeByte(32);
         }
      }

      request.protocolVersion().encode(buf);
      ByteBufUtil.writeShortBE(buf, 3338);
   }
}
