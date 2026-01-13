package io.netty.handler.codec.rtsp;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpDecoderConfig;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.regex.Pattern;

public class RtspDecoder extends HttpObjectDecoder {
   private static final HttpResponseStatus UNKNOWN_STATUS = new HttpResponseStatus(999, "Unknown");
   private boolean isDecodingRequest;
   private static final Pattern versionPattern = Pattern.compile("RTSP/\\d\\.\\d");
   public static final int DEFAULT_MAX_CONTENT_LENGTH = 8192;

   public RtspDecoder() {
      this(4096, 8192, 8192);
   }

   public RtspDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength) {
      super(
         new HttpDecoderConfig()
            .setMaxInitialLineLength(maxInitialLineLength)
            .setMaxHeaderSize(maxHeaderSize)
            .setMaxChunkSize(maxContentLength * 2)
            .setChunkedSupported(false)
      );
   }

   @Deprecated
   public RtspDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength, boolean validateHeaders) {
      super(
         new HttpDecoderConfig()
            .setMaxInitialLineLength(maxInitialLineLength)
            .setMaxHeaderSize(maxHeaderSize)
            .setMaxChunkSize(maxContentLength * 2)
            .setChunkedSupported(false)
            .setValidateHeaders(validateHeaders)
      );
   }

   public RtspDecoder(HttpDecoderConfig config) {
      super(config.clone().setMaxChunkSize(2 * config.getMaxChunkSize()).setChunkedSupported(false));
   }

   @Override
   protected HttpMessage createMessage(String[] initialLine) throws Exception {
      if (versionPattern.matcher(initialLine[0]).matches()) {
         this.isDecodingRequest = false;
         return new DefaultHttpResponse(
            RtspVersions.valueOf(initialLine[0]), new HttpResponseStatus(Integer.parseInt(initialLine[1]), initialLine[2]), this.headersFactory
         );
      } else {
         this.isDecodingRequest = true;
         return new DefaultHttpRequest(RtspVersions.valueOf(initialLine[2]), RtspMethods.valueOf(initialLine[0]), initialLine[1], this.headersFactory);
      }
   }

   @Override
   protected boolean isContentAlwaysEmpty(HttpMessage msg) {
      return super.isContentAlwaysEmpty(msg) || !msg.headers().contains(RtspHeaderNames.CONTENT_LENGTH);
   }

   @Override
   protected HttpMessage createInvalidMessage() {
      return (HttpMessage)(this.isDecodingRequest
         ? new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.OPTIONS, "/bad-request", Unpooled.buffer(0), this.headersFactory, this.trailersFactory)
         : new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, UNKNOWN_STATUS, Unpooled.buffer(0), this.headersFactory, this.trailersFactory));
   }

   @Override
   protected boolean isDecodingRequest() {
      return this.isDecodingRequest;
   }
}
