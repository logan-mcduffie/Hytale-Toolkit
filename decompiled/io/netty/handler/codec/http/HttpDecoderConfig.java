package io.netty.handler.codec.http;

import io.netty.util.internal.ObjectUtil;

public final class HttpDecoderConfig implements Cloneable {
   private int maxChunkSize = 8192;
   private boolean chunkedSupported = true;
   private boolean allowPartialChunks = true;
   private HttpHeadersFactory headersFactory = DefaultHttpHeadersFactory.headersFactory();
   private HttpHeadersFactory trailersFactory = DefaultHttpHeadersFactory.trailersFactory();
   private boolean allowDuplicateContentLengths = false;
   private int maxInitialLineLength = 4096;
   private int maxHeaderSize = 8192;
   private int initialBufferSize = 128;
   private boolean strictLineParsing = HttpObjectDecoder.DEFAULT_STRICT_LINE_PARSING;

   public int getInitialBufferSize() {
      return this.initialBufferSize;
   }

   public HttpDecoderConfig setInitialBufferSize(int initialBufferSize) {
      ObjectUtil.checkPositive(initialBufferSize, "initialBufferSize");
      this.initialBufferSize = initialBufferSize;
      return this;
   }

   public int getMaxInitialLineLength() {
      return this.maxInitialLineLength;
   }

   public HttpDecoderConfig setMaxInitialLineLength(int maxInitialLineLength) {
      ObjectUtil.checkPositive(maxInitialLineLength, "maxInitialLineLength");
      this.maxInitialLineLength = maxInitialLineLength;
      return this;
   }

   public int getMaxHeaderSize() {
      return this.maxHeaderSize;
   }

   public HttpDecoderConfig setMaxHeaderSize(int maxHeaderSize) {
      ObjectUtil.checkPositive(maxHeaderSize, "maxHeaderSize");
      this.maxHeaderSize = maxHeaderSize;
      return this;
   }

   public int getMaxChunkSize() {
      return this.maxChunkSize;
   }

   public HttpDecoderConfig setMaxChunkSize(int maxChunkSize) {
      ObjectUtil.checkPositive(maxChunkSize, "maxChunkSize");
      this.maxChunkSize = maxChunkSize;
      return this;
   }

   public boolean isChunkedSupported() {
      return this.chunkedSupported;
   }

   public HttpDecoderConfig setChunkedSupported(boolean chunkedSupported) {
      this.chunkedSupported = chunkedSupported;
      return this;
   }

   public boolean isAllowPartialChunks() {
      return this.allowPartialChunks;
   }

   public HttpDecoderConfig setAllowPartialChunks(boolean allowPartialChunks) {
      this.allowPartialChunks = allowPartialChunks;
      return this;
   }

   public HttpHeadersFactory getHeadersFactory() {
      return this.headersFactory;
   }

   public HttpDecoderConfig setHeadersFactory(HttpHeadersFactory headersFactory) {
      ObjectUtil.checkNotNull(headersFactory, "headersFactory");
      this.headersFactory = headersFactory;
      return this;
   }

   public boolean isAllowDuplicateContentLengths() {
      return this.allowDuplicateContentLengths;
   }

   public HttpDecoderConfig setAllowDuplicateContentLengths(boolean allowDuplicateContentLengths) {
      this.allowDuplicateContentLengths = allowDuplicateContentLengths;
      return this;
   }

   public HttpDecoderConfig setValidateHeaders(boolean validateHeaders) {
      DefaultHttpHeadersFactory noValidation = DefaultHttpHeadersFactory.headersFactory().withValidation(false);
      this.headersFactory = validateHeaders ? DefaultHttpHeadersFactory.headersFactory() : noValidation;
      this.trailersFactory = validateHeaders ? DefaultHttpHeadersFactory.trailersFactory() : noValidation;
      return this;
   }

   public HttpHeadersFactory getTrailersFactory() {
      return this.trailersFactory;
   }

   public HttpDecoderConfig setTrailersFactory(HttpHeadersFactory trailersFactory) {
      ObjectUtil.checkNotNull(trailersFactory, "trailersFactory");
      this.trailersFactory = trailersFactory;
      return this;
   }

   public boolean isStrictLineParsing() {
      return this.strictLineParsing;
   }

   public HttpDecoderConfig setStrictLineParsing(boolean strictLineParsing) {
      this.strictLineParsing = strictLineParsing;
      return this;
   }

   public HttpDecoderConfig clone() {
      try {
         return (HttpDecoderConfig)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new AssertionError();
      }
   }
}
