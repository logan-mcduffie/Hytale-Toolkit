package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeadersFactory;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeadersFactory;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SpdyHttpDecoder extends MessageToMessageDecoder<SpdyFrame> {
   private final int spdyVersion;
   private final int maxContentLength;
   private final Map<Integer, FullHttpMessage> messageMap;
   private final HttpHeadersFactory headersFactory;
   private final HttpHeadersFactory trailersFactory;

   public SpdyHttpDecoder(SpdyVersion version, int maxContentLength) {
      this(version, maxContentLength, new HashMap<>(), DefaultHttpHeadersFactory.headersFactory(), DefaultHttpHeadersFactory.trailersFactory());
   }

   @Deprecated
   public SpdyHttpDecoder(SpdyVersion version, int maxContentLength, boolean validateHeaders) {
      this(version, maxContentLength, new HashMap<>(), validateHeaders);
   }

   protected SpdyHttpDecoder(SpdyVersion version, int maxContentLength, Map<Integer, FullHttpMessage> messageMap) {
      this(version, maxContentLength, messageMap, DefaultHttpHeadersFactory.headersFactory(), DefaultHttpHeadersFactory.trailersFactory());
   }

   @Deprecated
   protected SpdyHttpDecoder(SpdyVersion version, int maxContentLength, Map<Integer, FullHttpMessage> messageMap, boolean validateHeaders) {
      this(
         version,
         maxContentLength,
         messageMap,
         DefaultHttpHeadersFactory.headersFactory().withValidation(validateHeaders),
         DefaultHttpHeadersFactory.trailersFactory().withValidation(validateHeaders)
      );
   }

   protected SpdyHttpDecoder(
      SpdyVersion version,
      int maxContentLength,
      Map<Integer, FullHttpMessage> messageMap,
      HttpHeadersFactory headersFactory,
      HttpHeadersFactory trailersFactory
   ) {
      super(SpdyFrame.class);
      this.spdyVersion = ObjectUtil.checkNotNull(version, "version").version();
      this.maxContentLength = ObjectUtil.checkPositive(maxContentLength, "maxContentLength");
      this.messageMap = messageMap;
      this.headersFactory = headersFactory;
      this.trailersFactory = trailersFactory;
   }

   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      for (Entry<Integer, FullHttpMessage> entry : this.messageMap.entrySet()) {
         ReferenceCountUtil.safeRelease(entry.getValue());
      }

      this.messageMap.clear();
      super.channelInactive(ctx);
   }

   protected FullHttpMessage putMessage(int streamId, FullHttpMessage message) {
      return this.messageMap.put(streamId, message);
   }

   protected FullHttpMessage getMessage(int streamId) {
      return this.messageMap.get(streamId);
   }

   protected FullHttpMessage removeMessage(int streamId) {
      return this.messageMap.remove(streamId);
   }

   protected void decode(ChannelHandlerContext ctx, SpdyFrame msg, List<Object> out) throws Exception {
      if (msg instanceof SpdySynStreamFrame) {
         SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame)msg;
         int streamId = spdySynStreamFrame.streamId();
         if (SpdyCodecUtil.isServerId(streamId)) {
            int associatedToStreamId = spdySynStreamFrame.associatedStreamId();
            if (associatedToStreamId == 0) {
               SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INVALID_STREAM);
               ctx.writeAndFlush(spdyRstStreamFrame);
               return;
            }

            if (spdySynStreamFrame.isLast()) {
               SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR);
               ctx.writeAndFlush(spdyRstStreamFrame);
               return;
            }

            if (spdySynStreamFrame.isTruncated()) {
               SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INTERNAL_ERROR);
               ctx.writeAndFlush(spdyRstStreamFrame);
               return;
            }

            try {
               FullHttpRequest httpRequestWithEntity = createHttpRequest(spdySynStreamFrame, ctx.alloc());
               httpRequestWithEntity.headers().setInt(SpdyHttpHeaders.Names.STREAM_ID, streamId);
               httpRequestWithEntity.headers().setInt(SpdyHttpHeaders.Names.ASSOCIATED_TO_STREAM_ID, associatedToStreamId);
               httpRequestWithEntity.headers().setInt(SpdyHttpHeaders.Names.PRIORITY, spdySynStreamFrame.priority());
               out.add(httpRequestWithEntity);
            } catch (Throwable var13) {
               SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR);
               ctx.writeAndFlush(spdyRstStreamFrame);
            }
         } else {
            if (spdySynStreamFrame.isTruncated()) {
               SpdySynReplyFrame spdySynReplyFrame = new DefaultSpdySynReplyFrame(streamId);
               spdySynReplyFrame.setLast(true);
               SpdyHeaders frameHeaders = spdySynReplyFrame.headers();
               frameHeaders.setInt(SpdyHeaders.HttpNames.STATUS, HttpResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE.code());
               frameHeaders.setObject(SpdyHeaders.HttpNames.VERSION, HttpVersion.HTTP_1_0);
               ctx.writeAndFlush(spdySynReplyFrame);
               return;
            }

            try {
               FullHttpRequest httpRequestWithEntity = createHttpRequest(spdySynStreamFrame, ctx.alloc());
               httpRequestWithEntity.headers().setInt(SpdyHttpHeaders.Names.STREAM_ID, streamId);
               if (spdySynStreamFrame.isLast()) {
                  out.add(httpRequestWithEntity);
               } else {
                  this.putMessage(streamId, httpRequestWithEntity);
               }
            } catch (Throwable var12) {
               SpdySynReplyFrame spdySynReplyFrame = new DefaultSpdySynReplyFrame(streamId);
               spdySynReplyFrame.setLast(true);
               SpdyHeaders frameHeaders = spdySynReplyFrame.headers();
               frameHeaders.setInt(SpdyHeaders.HttpNames.STATUS, HttpResponseStatus.BAD_REQUEST.code());
               frameHeaders.setObject(SpdyHeaders.HttpNames.VERSION, HttpVersion.HTTP_1_0);
               ctx.writeAndFlush(spdySynReplyFrame);
            }
         }
      } else if (msg instanceof SpdySynReplyFrame) {
         SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame)msg;
         int streamId = spdySynReplyFrame.streamId();
         if (spdySynReplyFrame.isTruncated()) {
            SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INTERNAL_ERROR);
            ctx.writeAndFlush(spdyRstStreamFrame);
            return;
         }

         try {
            FullHttpResponse httpResponseWithEntity = this.createHttpResponse(spdySynReplyFrame, ctx.alloc());
            httpResponseWithEntity.headers().setInt(SpdyHttpHeaders.Names.STREAM_ID, streamId);
            if (spdySynReplyFrame.isLast()) {
               HttpUtil.setContentLength(httpResponseWithEntity, 0L);
               out.add(httpResponseWithEntity);
            } else {
               this.putMessage(streamId, httpResponseWithEntity);
            }
         } catch (Throwable var11) {
            SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR);
            ctx.writeAndFlush(spdyRstStreamFrame);
         }
      } else if (msg instanceof SpdyHeadersFrame) {
         SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame)msg;
         int streamId = spdyHeadersFrame.streamId();
         FullHttpMessage fullHttpMessage = this.getMessage(streamId);
         if (fullHttpMessage == null) {
            if (SpdyCodecUtil.isServerId(streamId)) {
               if (spdyHeadersFrame.isTruncated()) {
                  SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.INTERNAL_ERROR);
                  ctx.writeAndFlush(spdyRstStreamFrame);
                  return;
               }

               try {
                  FullHttpMessage var27 = this.createHttpResponse(spdyHeadersFrame, ctx.alloc());
                  var27.headers().setInt(SpdyHttpHeaders.Names.STREAM_ID, streamId);
                  if (spdyHeadersFrame.isLast()) {
                     HttpUtil.setContentLength(var27, 0L);
                     out.add(var27);
                  } else {
                     this.putMessage(streamId, var27);
                  }
               } catch (Throwable var10) {
                  SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, SpdyStreamStatus.PROTOCOL_ERROR);
                  ctx.writeAndFlush(spdyRstStreamFrame);
               }
            }

            return;
         }

         if (!spdyHeadersFrame.isTruncated()) {
            for (Entry<CharSequence, CharSequence> e : spdyHeadersFrame.headers()) {
               fullHttpMessage.headers().add(e.getKey(), e.getValue());
            }
         }

         if (spdyHeadersFrame.isLast()) {
            HttpUtil.setContentLength(fullHttpMessage, fullHttpMessage.content().readableBytes());
            this.removeMessage(streamId);
            out.add(fullHttpMessage);
         }
      } else if (msg instanceof SpdyDataFrame) {
         SpdyDataFrame spdyDataFrame = (SpdyDataFrame)msg;
         int streamIdx = spdyDataFrame.streamId();
         FullHttpMessage fullHttpMessagex = this.getMessage(streamIdx);
         if (fullHttpMessagex == null) {
            return;
         }

         ByteBuf content = fullHttpMessagex.content();
         if (content.readableBytes() > this.maxContentLength - spdyDataFrame.content().readableBytes()) {
            this.removeMessage(streamIdx);
            throw new TooLongFrameException("HTTP content length exceeded " + this.maxContentLength + " bytes: " + spdyDataFrame.content().readableBytes());
         }

         ByteBuf spdyDataFrameData = spdyDataFrame.content();
         int spdyDataFrameDataLen = spdyDataFrameData.readableBytes();
         content.writeBytes(spdyDataFrameData, spdyDataFrameData.readerIndex(), spdyDataFrameDataLen);
         if (spdyDataFrame.isLast()) {
            HttpUtil.setContentLength(fullHttpMessagex, content.readableBytes());
            this.removeMessage(streamIdx);
            out.add(fullHttpMessagex);
         }
      } else if (msg instanceof SpdyRstStreamFrame) {
         SpdyRstStreamFrame spdyRstStreamFrame = (SpdyRstStreamFrame)msg;
         int streamIdxx = spdyRstStreamFrame.streamId();
         this.removeMessage(streamIdxx);
      }
   }

   private static FullHttpRequest createHttpRequest(SpdyHeadersFrame requestFrame, ByteBufAllocator alloc) throws Exception {
      SpdyHeaders headers = requestFrame.headers();
      HttpMethod method = HttpMethod.valueOf(headers.getAsString(SpdyHeaders.HttpNames.METHOD));
      String url = headers.getAsString(SpdyHeaders.HttpNames.PATH);
      HttpVersion httpVersion = HttpVersion.valueOf(headers.getAsString(SpdyHeaders.HttpNames.VERSION));
      headers.remove(SpdyHeaders.HttpNames.METHOD);
      headers.remove(SpdyHeaders.HttpNames.PATH);
      headers.remove(SpdyHeaders.HttpNames.VERSION);
      boolean release = true;
      ByteBuf buffer = alloc.buffer();

      Object var15;
      try {
         FullHttpRequest req = new DefaultFullHttpRequest(httpVersion, method, url, buffer);
         headers.remove(SpdyHeaders.HttpNames.SCHEME);
         CharSequence host = headers.get(SpdyHeaders.HttpNames.HOST);
         headers.remove(SpdyHeaders.HttpNames.HOST);
         req.headers().set(HttpHeaderNames.HOST, host);

         for (Entry<CharSequence, CharSequence> e : requestFrame.headers()) {
            req.headers().add(e.getKey(), e.getValue());
         }

         HttpUtil.setKeepAlive(req, true);
         req.headers().remove(HttpHeaderNames.TRANSFER_ENCODING);
         release = false;
         var15 = req;
      } finally {
         if (release) {
            buffer.release();
         }
      }

      return (FullHttpRequest)var15;
   }

   private FullHttpResponse createHttpResponse(SpdyHeadersFrame responseFrame, ByteBufAllocator alloc) throws Exception {
      SpdyHeaders headers = responseFrame.headers();
      HttpResponseStatus status = HttpResponseStatus.parseLine(headers.get(SpdyHeaders.HttpNames.STATUS));
      HttpVersion version = HttpVersion.valueOf(headers.getAsString(SpdyHeaders.HttpNames.VERSION));
      headers.remove(SpdyHeaders.HttpNames.STATUS);
      headers.remove(SpdyHeaders.HttpNames.VERSION);
      boolean release = true;
      ByteBuf buffer = alloc.buffer();

      Object var14;
      try {
         FullHttpResponse res = new DefaultFullHttpResponse(version, status, buffer, this.headersFactory, this.trailersFactory);

         for (Entry<CharSequence, CharSequence> e : responseFrame.headers()) {
            res.headers().add(e.getKey(), e.getValue());
         }

         HttpUtil.setKeepAlive(res, true);
         res.headers().remove(HttpHeaderNames.TRANSFER_ENCODING);
         res.headers().remove(HttpHeaderNames.TRAILER);
         release = false;
         var14 = res;
      } finally {
         if (release) {
            buffer.release();
         }
      }

      return (FullHttpResponse)var14;
   }
}
