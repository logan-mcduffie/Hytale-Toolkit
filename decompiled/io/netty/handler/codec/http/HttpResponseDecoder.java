package io.netty.handler.codec.http;

import io.netty.buffer.Unpooled;

public class HttpResponseDecoder extends HttpObjectDecoder {
   private static final HttpResponseStatus UNKNOWN_STATUS = new HttpResponseStatus(999, "Unknown");

   public HttpResponseDecoder() {
   }

   public HttpResponseDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
      super(new HttpDecoderConfig().setMaxInitialLineLength(maxInitialLineLength).setMaxHeaderSize(maxHeaderSize).setMaxChunkSize(maxChunkSize));
   }

   @Deprecated
   public HttpResponseDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
      super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders);
   }

   @Deprecated
   public HttpResponseDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize) {
      super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders, initialBufferSize);
   }

   @Deprecated
   public HttpResponseDecoder(
      int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize, boolean allowDuplicateContentLengths
   ) {
      super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders, initialBufferSize, allowDuplicateContentLengths);
   }

   @Deprecated
   public HttpResponseDecoder(
      int maxInitialLineLength,
      int maxHeaderSize,
      int maxChunkSize,
      boolean validateHeaders,
      int initialBufferSize,
      boolean allowDuplicateContentLengths,
      boolean allowPartialChunks
   ) {
      super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders, initialBufferSize, allowDuplicateContentLengths, allowPartialChunks);
   }

   public HttpResponseDecoder(HttpDecoderConfig config) {
      super(config);
   }

   @Override
   protected HttpMessage createMessage(String[] initialLine) {
      return new DefaultHttpResponse(
         HttpVersion.valueOf(initialLine[0], true), HttpResponseStatus.valueOf(Integer.parseInt(initialLine[1]), initialLine[2]), this.headersFactory
      );
   }

   @Override
   protected HttpMessage createInvalidMessage() {
      return new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, UNKNOWN_STATUS, Unpooled.buffer(0), this.headersFactory, this.trailersFactory);
   }

   @Override
   protected boolean isDecodingRequest() {
      return false;
   }
}
