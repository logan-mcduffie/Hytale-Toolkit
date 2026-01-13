package io.netty.handler.codec.spdy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class SpdyHttpEncoder extends MessageToMessageEncoder<HttpObject> {
   private int currentStreamId;
   private final boolean validateHeaders;
   private final boolean headersToLowerCase;

   public SpdyHttpEncoder(SpdyVersion version) {
      this(version, true, true);
   }

   public SpdyHttpEncoder(SpdyVersion version, boolean headersToLowerCase, boolean validateHeaders) {
      super(HttpObject.class);
      ObjectUtil.checkNotNull(version, "version");
      this.headersToLowerCase = headersToLowerCase;
      this.validateHeaders = validateHeaders;
   }

   protected void encode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
      boolean valid = false;
      boolean last = false;
      if (msg instanceof HttpRequest) {
         HttpRequest httpRequest = (HttpRequest)msg;
         SpdySynStreamFrame spdySynStreamFrame = this.createSynStreamFrame(httpRequest);
         out.add(spdySynStreamFrame);
         last = spdySynStreamFrame.isLast() || spdySynStreamFrame.isUnidirectional();
         valid = true;
      }

      if (msg instanceof HttpResponse) {
         HttpResponse httpResponse = (HttpResponse)msg;
         SpdyHeadersFrame spdyHeadersFrame = this.createHeadersFrame(httpResponse);
         out.add(spdyHeadersFrame);
         last = spdyHeadersFrame.isLast();
         valid = true;
      }

      if (msg instanceof HttpContent && !last) {
         HttpContent chunk = (HttpContent)msg;
         chunk.content().retain();
         SpdyDataFrame spdyDataFrame = new DefaultSpdyDataFrame(this.currentStreamId, chunk.content());
         if (!(chunk instanceof LastHttpContent)) {
            out.add(spdyDataFrame);
         } else {
            LastHttpContent trailer = (LastHttpContent)chunk;
            HttpHeaders trailers = trailer.trailingHeaders();
            if (trailers.isEmpty()) {
               spdyDataFrame.setLast(true);
               out.add(spdyDataFrame);
            } else {
               SpdyHeadersFrame spdyHeadersFrame = new DefaultSpdyHeadersFrame(this.currentStreamId, this.validateHeaders);
               spdyHeadersFrame.setLast(true);
               Iterator<Entry<CharSequence, CharSequence>> itr = trailers.iteratorCharSequence();

               while (itr.hasNext()) {
                  Entry<CharSequence, CharSequence> entry = itr.next();
                  CharSequence headerName = (CharSequence)(this.headersToLowerCase ? AsciiString.of(entry.getKey()).toLowerCase() : entry.getKey());
                  spdyHeadersFrame.headers().add(headerName, entry.getValue());
               }

               out.add(spdyDataFrame);
               out.add(spdyHeadersFrame);
            }
         }

         valid = true;
      }

      if (!valid) {
         throw new UnsupportedMessageTypeException(msg);
      }
   }

   private SpdySynStreamFrame createSynStreamFrame(HttpRequest httpRequest) throws Exception {
      HttpHeaders httpHeaders = httpRequest.headers();
      int streamId = httpHeaders.getInt(SpdyHttpHeaders.Names.STREAM_ID);
      int associatedToStreamId = httpHeaders.getInt(SpdyHttpHeaders.Names.ASSOCIATED_TO_STREAM_ID, 0);
      byte priority = (byte)httpHeaders.getInt(SpdyHttpHeaders.Names.PRIORITY, 0);
      CharSequence scheme = httpHeaders.get(SpdyHttpHeaders.Names.SCHEME);
      httpHeaders.remove(SpdyHttpHeaders.Names.STREAM_ID);
      httpHeaders.remove(SpdyHttpHeaders.Names.ASSOCIATED_TO_STREAM_ID);
      httpHeaders.remove(SpdyHttpHeaders.Names.PRIORITY);
      httpHeaders.remove(SpdyHttpHeaders.Names.SCHEME);
      httpHeaders.remove(HttpHeaderNames.CONNECTION);
      httpHeaders.remove("Keep-Alive");
      httpHeaders.remove("Proxy-Connection");
      httpHeaders.remove(HttpHeaderNames.TRANSFER_ENCODING);
      SpdySynStreamFrame spdySynStreamFrame = new DefaultSpdySynStreamFrame(streamId, associatedToStreamId, priority, this.validateHeaders);
      SpdyHeaders frameHeaders = spdySynStreamFrame.headers();
      frameHeaders.set(SpdyHeaders.HttpNames.METHOD, httpRequest.method().name());
      frameHeaders.set(SpdyHeaders.HttpNames.PATH, httpRequest.uri());
      frameHeaders.set(SpdyHeaders.HttpNames.VERSION, httpRequest.protocolVersion().text());
      CharSequence host = httpHeaders.get(HttpHeaderNames.HOST);
      httpHeaders.remove(HttpHeaderNames.HOST);
      frameHeaders.set(SpdyHeaders.HttpNames.HOST, host);
      if (scheme == null) {
         scheme = "https";
      }

      frameHeaders.set(SpdyHeaders.HttpNames.SCHEME, scheme);
      Iterator<Entry<CharSequence, CharSequence>> itr = httpHeaders.iteratorCharSequence();

      while (itr.hasNext()) {
         Entry<CharSequence, CharSequence> entry = itr.next();
         CharSequence headerName = (CharSequence)(this.headersToLowerCase ? AsciiString.of(entry.getKey()).toLowerCase() : entry.getKey());
         frameHeaders.add(headerName, entry.getValue());
      }

      this.currentStreamId = spdySynStreamFrame.streamId();
      if (associatedToStreamId == 0) {
         spdySynStreamFrame.setLast(isLast(httpRequest));
      } else {
         spdySynStreamFrame.setUnidirectional(true);
      }

      return spdySynStreamFrame;
   }

   private SpdyHeadersFrame createHeadersFrame(HttpResponse httpResponse) throws Exception {
      HttpHeaders httpHeaders = httpResponse.headers();
      int streamId = httpHeaders.getInt(SpdyHttpHeaders.Names.STREAM_ID);
      httpHeaders.remove(SpdyHttpHeaders.Names.STREAM_ID);
      httpHeaders.remove(HttpHeaderNames.CONNECTION);
      httpHeaders.remove("Keep-Alive");
      httpHeaders.remove("Proxy-Connection");
      httpHeaders.remove(HttpHeaderNames.TRANSFER_ENCODING);
      SpdyHeadersFrame spdyHeadersFrame;
      if (SpdyCodecUtil.isServerId(streamId)) {
         spdyHeadersFrame = new DefaultSpdyHeadersFrame(streamId, this.validateHeaders);
      } else {
         spdyHeadersFrame = new DefaultSpdySynReplyFrame(streamId, this.validateHeaders);
      }

      SpdyHeaders frameHeaders = spdyHeadersFrame.headers();
      frameHeaders.set(SpdyHeaders.HttpNames.STATUS, httpResponse.status().codeAsText());
      frameHeaders.set(SpdyHeaders.HttpNames.VERSION, httpResponse.protocolVersion().text());
      Iterator<Entry<CharSequence, CharSequence>> itr = httpHeaders.iteratorCharSequence();

      while (itr.hasNext()) {
         Entry<CharSequence, CharSequence> entry = itr.next();
         CharSequence headerName = (CharSequence)(this.headersToLowerCase ? AsciiString.of(entry.getKey()).toLowerCase() : entry.getKey());
         spdyHeadersFrame.headers().add(headerName, entry.getValue());
      }

      this.currentStreamId = streamId;
      spdyHeadersFrame.setLast(isLast(httpResponse));
      return spdyHeadersFrame;
   }

   private static boolean isLast(HttpMessage httpMessage) {
      if (httpMessage instanceof FullHttpMessage) {
         FullHttpMessage fullMessage = (FullHttpMessage)httpMessage;
         if (fullMessage.trailingHeaders().isEmpty() && !fullMessage.content().isReadable()) {
            return true;
         }
      }

      return false;
   }
}
