package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class HttpObjectDecoder extends ByteToMessageDecoder {
   public static final int DEFAULT_MAX_INITIAL_LINE_LENGTH = 4096;
   public static final int DEFAULT_MAX_HEADER_SIZE = 8192;
   public static final boolean DEFAULT_CHUNKED_SUPPORTED = true;
   public static final boolean DEFAULT_ALLOW_PARTIAL_CHUNKS = true;
   public static final int DEFAULT_MAX_CHUNK_SIZE = 8192;
   public static final boolean DEFAULT_VALIDATE_HEADERS = true;
   public static final int DEFAULT_INITIAL_BUFFER_SIZE = 128;
   public static final boolean DEFAULT_ALLOW_DUPLICATE_CONTENT_LENGTHS = false;
   public static final boolean DEFAULT_STRICT_LINE_PARSING = SystemPropertyUtil.getBoolean("io.netty.handler.codec.http.defaultStrictLineParsing", true);
   private static final Runnable THROW_INVALID_CHUNK_EXTENSION = new Runnable() {
      @Override
      public void run() {
         throw new InvalidChunkExtensionException();
      }
   };
   private static final Runnable THROW_INVALID_LINE_SEPARATOR = new Runnable() {
      @Override
      public void run() {
         throw new InvalidLineSeparatorException();
      }
   };
   private final int maxChunkSize;
   private final boolean chunkedSupported;
   private final boolean allowPartialChunks;
   @Deprecated
   protected final boolean validateHeaders;
   protected final HttpHeadersFactory headersFactory;
   protected final HttpHeadersFactory trailersFactory;
   private final boolean allowDuplicateContentLengths;
   private final ByteBuf parserScratchBuffer;
   private final Runnable defaultStrictCRLFCheck;
   private final HttpObjectDecoder.HeaderParser headerParser;
   private final HttpObjectDecoder.LineParser lineParser;
   private HttpMessage message;
   private long chunkSize;
   private long contentLength = Long.MIN_VALUE;
   private boolean chunked;
   private boolean isSwitchingToNonHttp1Protocol;
   private final AtomicBoolean resetRequested = new AtomicBoolean();
   private AsciiString name;
   private String value;
   private LastHttpContent trailer;
   private HttpObjectDecoder.State currentState = HttpObjectDecoder.State.SKIP_CONTROL_CHARS;
   private static final boolean[] SP_LENIENT_BYTES = new boolean[256];
   private static final boolean[] LATIN_WHITESPACE = new boolean[256];
   private static final boolean[] ISO_CONTROL_OR_WHITESPACE;
   private static final ByteProcessor SKIP_CONTROL_CHARS_BYTES;

   @Override
   protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
      try {
         this.parserScratchBuffer.release();
      } finally {
         super.handlerRemoved0(ctx);
      }
   }

   protected HttpObjectDecoder() {
      this(new HttpDecoderConfig());
   }

   @Deprecated
   protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported) {
      this(
         new HttpDecoderConfig()
            .setMaxInitialLineLength(maxInitialLineLength)
            .setMaxHeaderSize(maxHeaderSize)
            .setMaxChunkSize(maxChunkSize)
            .setChunkedSupported(chunkedSupported)
      );
   }

   @Deprecated
   protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported, boolean validateHeaders) {
      this(
         new HttpDecoderConfig()
            .setMaxInitialLineLength(maxInitialLineLength)
            .setMaxHeaderSize(maxHeaderSize)
            .setMaxChunkSize(maxChunkSize)
            .setChunkedSupported(chunkedSupported)
            .setValidateHeaders(validateHeaders)
      );
   }

   @Deprecated
   protected HttpObjectDecoder(
      int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported, boolean validateHeaders, int initialBufferSize
   ) {
      this(
         new HttpDecoderConfig()
            .setMaxInitialLineLength(maxInitialLineLength)
            .setMaxHeaderSize(maxHeaderSize)
            .setMaxChunkSize(maxChunkSize)
            .setChunkedSupported(chunkedSupported)
            .setValidateHeaders(validateHeaders)
            .setInitialBufferSize(initialBufferSize)
      );
   }

   @Deprecated
   protected HttpObjectDecoder(
      int maxInitialLineLength,
      int maxHeaderSize,
      int maxChunkSize,
      boolean chunkedSupported,
      boolean validateHeaders,
      int initialBufferSize,
      boolean allowDuplicateContentLengths
   ) {
      this(
         new HttpDecoderConfig()
            .setMaxInitialLineLength(maxInitialLineLength)
            .setMaxHeaderSize(maxHeaderSize)
            .setMaxChunkSize(maxChunkSize)
            .setChunkedSupported(chunkedSupported)
            .setValidateHeaders(validateHeaders)
            .setInitialBufferSize(initialBufferSize)
            .setAllowDuplicateContentLengths(allowDuplicateContentLengths)
      );
   }

   @Deprecated
   protected HttpObjectDecoder(
      int maxInitialLineLength,
      int maxHeaderSize,
      int maxChunkSize,
      boolean chunkedSupported,
      boolean validateHeaders,
      int initialBufferSize,
      boolean allowDuplicateContentLengths,
      boolean allowPartialChunks
   ) {
      this(
         new HttpDecoderConfig()
            .setMaxInitialLineLength(maxInitialLineLength)
            .setMaxHeaderSize(maxHeaderSize)
            .setMaxChunkSize(maxChunkSize)
            .setChunkedSupported(chunkedSupported)
            .setValidateHeaders(validateHeaders)
            .setInitialBufferSize(initialBufferSize)
            .setAllowDuplicateContentLengths(allowDuplicateContentLengths)
            .setAllowPartialChunks(allowPartialChunks)
      );
   }

   protected HttpObjectDecoder(HttpDecoderConfig config) {
      ObjectUtil.checkNotNull(config, "config");
      this.parserScratchBuffer = Unpooled.buffer(config.getInitialBufferSize());
      this.defaultStrictCRLFCheck = config.isStrictLineParsing() ? THROW_INVALID_LINE_SEPARATOR : null;
      this.lineParser = new HttpObjectDecoder.LineParser(this.parserScratchBuffer, config.getMaxInitialLineLength());
      this.headerParser = new HttpObjectDecoder.HeaderParser(this.parserScratchBuffer, config.getMaxHeaderSize());
      this.maxChunkSize = config.getMaxChunkSize();
      this.chunkedSupported = config.isChunkedSupported();
      this.headersFactory = config.getHeadersFactory();
      this.trailersFactory = config.getTrailersFactory();
      this.validateHeaders = this.isValidating(this.headersFactory);
      this.allowDuplicateContentLengths = config.isAllowDuplicateContentLengths();
      this.allowPartialChunks = config.isAllowPartialChunks();
   }

   protected boolean isValidating(HttpHeadersFactory headersFactory) {
      if (!(headersFactory instanceof DefaultHttpHeadersFactory)) {
         return true;
      } else {
         DefaultHttpHeadersFactory builder = (DefaultHttpHeadersFactory)headersFactory;
         return builder.isValidatingHeaderNames() || builder.isValidatingHeaderValues();
      }
   }

   @Override
   protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
      if (this.resetRequested.get()) {
         this.resetNow();
      }

      switch (this.currentState) {
         case SKIP_CONTROL_CHARS:
         case READ_INITIAL:
            try {
               ByteBuf linex = this.lineParser.parse(buffer, this.defaultStrictCRLFCheck);
               if (linex == null) {
                  return;
               }

               String[] initialLine = this.splitInitialLine(linex);

               assert initialLine.length == 3 : "initialLine::length must be 3";

               this.message = this.createMessage(initialLine);
               this.currentState = HttpObjectDecoder.State.READ_HEADER;
            } catch (Exception var9) {
               out.add(this.invalidMessage(this.message, buffer, var9));
               return;
            }
         case READ_HEADER:
            try {
               HttpObjectDecoder.State nextState = this.readHeaders(buffer);
               if (nextState == null) {
                  return;
               }

               this.currentState = nextState;
               switch (nextState) {
                  case SKIP_CONTROL_CHARS:
                     this.addCurrentMessage(out);
                     out.add(LastHttpContent.EMPTY_LAST_CONTENT);
                     this.resetNow();
                     return;
                  case READ_CHUNK_SIZE:
                     if (!this.chunkedSupported) {
                        throw new IllegalArgumentException("Chunked messages not supported");
                     }

                     this.addCurrentMessage(out);
                     return;
                  default:
                     if (this.contentLength != 0L && (this.contentLength != -1L || !this.isDecodingRequest())) {
                        assert nextState == HttpObjectDecoder.State.READ_FIXED_LENGTH_CONTENT
                           || nextState == HttpObjectDecoder.State.READ_VARIABLE_LENGTH_CONTENT;

                        this.addCurrentMessage(out);
                        if (nextState == HttpObjectDecoder.State.READ_FIXED_LENGTH_CONTENT) {
                           this.chunkSize = this.contentLength;
                        }

                        return;
                     }

                     this.addCurrentMessage(out);
                     out.add(LastHttpContent.EMPTY_LAST_CONTENT);
                     this.resetNow();
                     return;
               }
            } catch (Exception var10) {
               out.add(this.invalidMessage(this.message, buffer, var10));
               return;
            }
         case READ_CHUNK_SIZE:
            try {
               ByteBuf line = this.lineParser.parse(buffer, THROW_INVALID_CHUNK_EXTENSION);
               if (line == null) {
                  return;
               }

               int chunkSize = getChunkSize(line.array(), line.arrayOffset() + line.readerIndex(), line.readableBytes());
               this.chunkSize = chunkSize;
               if (chunkSize == 0) {
                  this.currentState = HttpObjectDecoder.State.READ_CHUNK_FOOTER;
                  return;
               }

               this.currentState = HttpObjectDecoder.State.READ_CHUNKED_CONTENT;
            } catch (Exception var8) {
               out.add(this.invalidChunk(buffer, var8));
               return;
            }
         case READ_CHUNKED_CONTENT:
            assert this.chunkSize <= 2147483647L;

            int toReadx = Math.min((int)this.chunkSize, this.maxChunkSize);
            if (!this.allowPartialChunks && buffer.readableBytes() < toReadx) {
               return;
            }

            toReadx = Math.min(toReadx, buffer.readableBytes());
            if (toReadx == 0) {
               return;
            }

            HttpContent chunk = new DefaultHttpContent(buffer.readRetainedSlice(toReadx));
            this.chunkSize -= toReadx;
            out.add(chunk);
            if (this.chunkSize != 0L) {
               return;
            }

            this.currentState = HttpObjectDecoder.State.READ_CHUNK_DELIMITER;
         case READ_CHUNK_DELIMITER:
            if (buffer.readableBytes() >= 2) {
               int rIdx = buffer.readerIndex();
               if (buffer.getByte(rIdx) == 13 && buffer.getByte(rIdx + 1) == 10) {
                  buffer.skipBytes(2);
                  this.currentState = HttpObjectDecoder.State.READ_CHUNK_SIZE;
               } else {
                  out.add(this.invalidChunk(buffer, new InvalidChunkTerminationException()));
               }
            }

            return;
         case READ_VARIABLE_LENGTH_CONTENT:
            int toReadx = Math.min(buffer.readableBytes(), this.maxChunkSize);
            if (toReadx > 0) {
               ByteBuf content = buffer.readRetainedSlice(toReadx);
               out.add(new DefaultHttpContent(content));
            }

            return;
         case READ_FIXED_LENGTH_CONTENT:
            int readLimit = buffer.readableBytes();
            if (readLimit == 0) {
               return;
            }

            int toRead = Math.min(readLimit, this.maxChunkSize);
            if (toRead > this.chunkSize) {
               toRead = (int)this.chunkSize;
            }

            ByteBuf content = buffer.readRetainedSlice(toRead);
            this.chunkSize -= toRead;
            if (this.chunkSize == 0L) {
               out.add(new DefaultLastHttpContent(content, this.trailersFactory));
               this.resetNow();
            } else {
               out.add(new DefaultHttpContent(content));
            }

            return;
         case READ_CHUNK_FOOTER:
            try {
               LastHttpContent trailer = this.readTrailingHeaders(buffer);
               if (trailer == null) {
                  return;
               }

               out.add(trailer);
               this.resetNow();
               return;
            } catch (Exception var7) {
               out.add(this.invalidChunk(buffer, var7));
               return;
            }
         case BAD_MESSAGE:
            buffer.skipBytes(buffer.readableBytes());
            break;
         case UPGRADED:
            int readableBytes = buffer.readableBytes();
            if (readableBytes > 0) {
               out.add(buffer.readBytes(readableBytes));
            }
      }
   }

   @Override
   protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      super.decodeLast(ctx, in, out);
      if (this.resetRequested.get()) {
         this.resetNow();
      }

      switch (this.currentState) {
         case SKIP_CONTROL_CHARS:
         case READ_INITIAL:
         case BAD_MESSAGE:
         case UPGRADED:
            return;
         case READ_CHUNK_SIZE:
         case READ_FIXED_LENGTH_CONTENT:
         case READ_CHUNKED_CONTENT:
         case READ_CHUNK_DELIMITER:
         case READ_CHUNK_FOOTER:
            boolean prematureClosure;
            if (!this.isDecodingRequest() && !this.chunked) {
               prematureClosure = this.contentLength > 0L;
            } else {
               prematureClosure = true;
            }

            if (!prematureClosure) {
               out.add(LastHttpContent.EMPTY_LAST_CONTENT);
            }

            this.resetNow();
            return;
         case READ_HEADER:
            out.add(this.invalidMessage(this.message, Unpooled.EMPTY_BUFFER, new PrematureChannelClosureException("Connection closed before received headers")));
            this.resetNow();
            return;
         case READ_VARIABLE_LENGTH_CONTENT:
            if (!this.chunked && !in.isReadable()) {
               out.add(LastHttpContent.EMPTY_LAST_CONTENT);
               this.resetNow();
            }

            return;
         default:
            throw new IllegalStateException("Unhandled state " + this.currentState);
      }
   }

   @Override
   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
      if (evt instanceof HttpExpectationFailedEvent) {
         switch (this.currentState) {
            case READ_CHUNK_SIZE:
            case READ_VARIABLE_LENGTH_CONTENT:
            case READ_FIXED_LENGTH_CONTENT:
               this.reset();
            case READ_INITIAL:
            case READ_HEADER:
         }
      }

      super.userEventTriggered(ctx, evt);
   }

   private void addCurrentMessage(List<Object> out) {
      HttpMessage message = this.message;

      assert message != null;

      this.message = null;
      out.add(message);
   }

   protected boolean isContentAlwaysEmpty(HttpMessage msg) {
      if (msg instanceof HttpResponse) {
         HttpResponse res = (HttpResponse)msg;
         HttpResponseStatus status = res.status();
         int code = status.code();
         HttpStatusClass statusClass = status.codeClass();
         if (statusClass != HttpStatusClass.INFORMATIONAL) {
            switch (code) {
               case 204:
               case 304:
                  return true;
               default:
                  return false;
            }
         } else {
            return code != 101
               || res.headers().contains(HttpHeaderNames.SEC_WEBSOCKET_ACCEPT)
               || !res.headers().contains(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET, true);
         }
      } else {
         return false;
      }
   }

   protected boolean isSwitchingToNonHttp1Protocol(HttpResponse msg) {
      if (msg.status().code() != HttpResponseStatus.SWITCHING_PROTOCOLS.code()) {
         return false;
      } else {
         String newProtocol = msg.headers().get(HttpHeaderNames.UPGRADE);
         return newProtocol == null || !newProtocol.contains(HttpVersion.HTTP_1_0.text()) && !newProtocol.contains(HttpVersion.HTTP_1_1.text());
      }
   }

   public void reset() {
      this.resetRequested.lazySet(true);
   }

   private void resetNow() {
      this.message = null;
      this.name = null;
      this.value = null;
      this.contentLength = Long.MIN_VALUE;
      this.chunked = false;
      this.lineParser.reset();
      this.headerParser.reset();
      this.trailer = null;
      if (this.isSwitchingToNonHttp1Protocol) {
         this.isSwitchingToNonHttp1Protocol = false;
         this.currentState = HttpObjectDecoder.State.UPGRADED;
      } else {
         this.resetRequested.lazySet(false);
         this.currentState = HttpObjectDecoder.State.SKIP_CONTROL_CHARS;
      }
   }

   private HttpMessage invalidMessage(HttpMessage current, ByteBuf in, Exception cause) {
      this.currentState = HttpObjectDecoder.State.BAD_MESSAGE;
      this.message = null;
      this.trailer = null;
      in.skipBytes(in.readableBytes());
      if (current == null) {
         current = this.createInvalidMessage();
      }

      current.setDecoderResult(DecoderResult.failure(cause));
      return current;
   }

   private HttpContent invalidChunk(ByteBuf in, Exception cause) {
      this.currentState = HttpObjectDecoder.State.BAD_MESSAGE;
      this.message = null;
      this.trailer = null;
      in.skipBytes(in.readableBytes());
      HttpContent chunk = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);
      chunk.setDecoderResult(DecoderResult.failure(cause));
      return chunk;
   }

   private HttpObjectDecoder.State readHeaders(ByteBuf buffer) {
      HttpMessage message = this.message;
      HttpHeaders headers = message.headers();
      HttpObjectDecoder.HeaderParser headerParser = this.headerParser;
      ByteBuf line = headerParser.parse(buffer, this.defaultStrictCRLFCheck);
      if (line == null) {
         return null;
      } else {
         for (int lineLength = line.readableBytes(); lineLength > 0; lineLength = line.readableBytes()) {
            byte[] lineContent = line.array();
            int startLine = line.arrayOffset() + line.readerIndex();
            byte firstChar = lineContent[startLine];
            if (this.name == null || firstChar != 32 && firstChar != 9) {
               if (this.name != null) {
                  headers.add(this.name, this.value);
               }

               this.splitHeader(lineContent, startLine, lineLength);
            } else {
               String trimmedLine = langAsciiString(lineContent, startLine, lineLength).trim();
               String valueStr = this.value;
               this.value = valueStr + ' ' + trimmedLine;
            }

            line = headerParser.parse(buffer, this.defaultStrictCRLFCheck);
            if (line == null) {
               return null;
            }
         }

         if (this.name != null) {
            headers.add(this.name, this.value);
         }

         this.name = null;
         this.value = null;
         HttpMessageDecoderResult decoderResult = new HttpMessageDecoderResult(this.lineParser.size, headerParser.size);
         message.setDecoderResult(decoderResult);
         List<String> contentLengthFields = headers.getAll(HttpHeaderNames.CONTENT_LENGTH);
         if (!contentLengthFields.isEmpty()) {
            HttpVersion version = message.protocolVersion();
            boolean isHttp10OrEarlier = version.majorVersion() < 1 || version.majorVersion() == 1 && version.minorVersion() == 0;
            this.contentLength = HttpUtil.normalizeAndGetContentLength(contentLengthFields, isHttp10OrEarlier, this.allowDuplicateContentLengths);
            if (this.contentLength != -1L) {
               String lengthValue = contentLengthFields.get(0).trim();
               if (contentLengthFields.size() > 1 || !lengthValue.equals(Long.toString(this.contentLength))) {
                  headers.set(HttpHeaderNames.CONTENT_LENGTH, this.contentLength);
               }
            }
         } else {
            this.contentLength = HttpUtil.getWebSocketContentLength(message);
         }

         if (!this.isDecodingRequest() && message instanceof HttpResponse) {
            HttpResponse res = (HttpResponse)message;
            this.isSwitchingToNonHttp1Protocol = this.isSwitchingToNonHttp1Protocol(res);
         }

         if (this.isContentAlwaysEmpty(message)) {
            HttpUtil.setTransferEncodingChunked(message, false);
            return HttpObjectDecoder.State.SKIP_CONTROL_CHARS;
         } else if (HttpUtil.isTransferEncodingChunked(message)) {
            this.chunked = true;
            if (!contentLengthFields.isEmpty() && message.protocolVersion() == HttpVersion.HTTP_1_1) {
               this.handleTransferEncodingChunkedWithContentLength(message);
            }

            return HttpObjectDecoder.State.READ_CHUNK_SIZE;
         } else {
            return this.contentLength >= 0L ? HttpObjectDecoder.State.READ_FIXED_LENGTH_CONTENT : HttpObjectDecoder.State.READ_VARIABLE_LENGTH_CONTENT;
         }
      }
   }

   protected void handleTransferEncodingChunkedWithContentLength(HttpMessage message) {
      message.headers().remove(HttpHeaderNames.CONTENT_LENGTH);
      this.contentLength = Long.MIN_VALUE;
   }

   private LastHttpContent readTrailingHeaders(ByteBuf buffer) {
      HttpObjectDecoder.HeaderParser headerParser = this.headerParser;
      ByteBuf line = headerParser.parse(buffer, this.defaultStrictCRLFCheck);
      if (line == null) {
         return null;
      } else {
         LastHttpContent trailer = this.trailer;
         int lineLength = line.readableBytes();
         if (lineLength == 0 && trailer == null) {
            return LastHttpContent.EMPTY_LAST_CONTENT;
         } else {
            CharSequence lastHeader = null;
            if (trailer == null) {
               trailer = this.trailer = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER, this.trailersFactory);
            }

            while (lineLength > 0) {
               byte[] lineContent = line.array();
               int startLine = line.arrayOffset() + line.readerIndex();
               byte firstChar = lineContent[startLine];
               if (lastHeader != null && (firstChar == 32 || firstChar == 9)) {
                  List<String> current = trailer.trailingHeaders().getAll(lastHeader);
                  if (!current.isEmpty()) {
                     int lastPos = current.size() - 1;
                     String lineTrimmed = langAsciiString(lineContent, startLine, line.readableBytes()).trim();
                     String currentLastPos = current.get(lastPos);
                     current.set(lastPos, currentLastPos + lineTrimmed);
                  }
               } else {
                  this.splitHeader(lineContent, startLine, lineLength);
                  AsciiString headerName = this.name;
                  if (!HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(headerName)
                     && !HttpHeaderNames.TRANSFER_ENCODING.contentEqualsIgnoreCase(headerName)
                     && !HttpHeaderNames.TRAILER.contentEqualsIgnoreCase(headerName)) {
                     trailer.trailingHeaders().add(headerName, this.value);
                  }

                  lastHeader = this.name;
                  this.name = null;
                  this.value = null;
               }

               line = headerParser.parse(buffer, this.defaultStrictCRLFCheck);
               if (line == null) {
                  return null;
               }

               lineLength = line.readableBytes();
            }

            this.trailer = null;
            return trailer;
         }
      }
   }

   protected abstract boolean isDecodingRequest();

   protected abstract HttpMessage createMessage(String[] var1) throws Exception;

   protected abstract HttpMessage createInvalidMessage();

   private static int skipWhiteSpaces(byte[] hex, int start, int length) {
      for (int i = 0; i < length; i++) {
         if (!isWhitespace(hex[start + i])) {
            return i;
         }
      }

      return length;
   }

   private static int getChunkSize(byte[] hex, int start, int length) {
      int skipped = skipWhiteSpaces(hex, start, length);
      if (skipped == length) {
         throw new NumberFormatException();
      } else {
         start += skipped;
         length -= skipped;
         int result = 0;

         for (int i = 0; i < length; i++) {
            int digit = StringUtil.decodeHexNibble(hex[start + i]);
            if (digit == -1) {
               byte b = hex[start + i];
               if (b != 59 && !isControlOrWhitespaceAsciiChar(b)) {
                  throw new NumberFormatException("Invalid character in chunk size");
               }

               if (i == 0) {
                  throw new NumberFormatException("Empty chunk size");
               }

               return result;
            }

            result *= 16;
            result += digit;
            if (result < 0) {
               throw new NumberFormatException("Chunk size overflow: " + result);
            }
         }

         return result;
      }
   }

   private String[] splitInitialLine(ByteBuf asciiBuffer) {
      byte[] asciiBytes = asciiBuffer.array();
      int arrayOffset = asciiBuffer.arrayOffset();
      int startContent = arrayOffset + asciiBuffer.readerIndex();
      int end = startContent + asciiBuffer.readableBytes();
      byte lastByte = asciiBytes[end - 1];
      if (!isControlOrWhitespaceAsciiChar(lastByte) || !this.isDecodingRequest() && isOWS(lastByte)) {
         int aStart = findNonSPLenient(asciiBytes, startContent, end);
         int aEnd = findSPLenient(asciiBytes, aStart, end);
         int bStart = findNonSPLenient(asciiBytes, aEnd, end);
         int bEnd = findSPLenient(asciiBytes, bStart, end);
         int cStart = findNonSPLenient(asciiBytes, bEnd, end);
         int cEnd = findEndOfString(asciiBytes, Math.max(cStart - 1, startContent), end);
         return new String[]{
            this.splitFirstWordInitialLine(asciiBytes, aStart, aEnd - aStart),
            this.splitSecondWordInitialLine(asciiBytes, bStart, bEnd - bStart),
            cStart < cEnd ? this.splitThirdWordInitialLine(asciiBytes, cStart, cEnd - cStart) : ""
         };
      } else {
         throw new IllegalArgumentException("Illegal character in request line: 0x" + Integer.toHexString(lastByte));
      }
   }

   protected String splitFirstWordInitialLine(byte[] asciiContent, int start, int length) {
      return langAsciiString(asciiContent, start, length);
   }

   protected String splitSecondWordInitialLine(byte[] asciiContent, int start, int length) {
      return langAsciiString(asciiContent, start, length);
   }

   protected String splitThirdWordInitialLine(byte[] asciiContent, int start, int length) {
      return langAsciiString(asciiContent, start, length);
   }

   private static String langAsciiString(byte[] asciiContent, int start, int length) {
      if (length == 0) {
         return "";
      } else if (start == 0) {
         return length == asciiContent.length ? new String(asciiContent, 0, 0, asciiContent.length) : new String(asciiContent, 0, 0, length);
      } else {
         return new String(asciiContent, 0, start, length);
      }
   }

   private void splitHeader(byte[] line, int start, int length) {
      int end = start + length;
      boolean isDecodingRequest = this.isDecodingRequest();

      int nameEnd;
      for (nameEnd = start; nameEnd < end; nameEnd++) {
         byte ch = line[nameEnd];
         if (ch == 58 || !isDecodingRequest && isOWS(ch)) {
            break;
         }
      }

      if (nameEnd == end) {
         throw new IllegalArgumentException("No colon found");
      } else {
         int colonEnd;
         for (colonEnd = nameEnd; colonEnd < end; colonEnd++) {
            if (line[colonEnd] == 58) {
               colonEnd++;
               break;
            }
         }

         this.name = this.splitHeaderName(line, start, nameEnd - start);
         int valueStart = findNonWhitespace(line, colonEnd, end);
         if (valueStart == end) {
            this.value = "";
         } else {
            int valueEnd = findEndOfString(line, start, end);
            this.value = langAsciiString(line, valueStart, valueEnd - valueStart);
         }
      }
   }

   protected AsciiString splitHeaderName(byte[] sb, int start, int length) {
      return new AsciiString(sb, start, length, true);
   }

   private static int findNonSPLenient(byte[] sb, int offset, int end) {
      for (int result = offset; result < end; result++) {
         byte c = sb[result];
         if (!isSPLenient(c)) {
            if (isWhitespace(c)) {
               throw new IllegalArgumentException("Invalid separator");
            }

            return result;
         }
      }

      return end;
   }

   private static int findSPLenient(byte[] sb, int offset, int end) {
      for (int result = offset; result < end; result++) {
         if (isSPLenient(sb[result])) {
            return result;
         }
      }

      return end;
   }

   private static boolean isSPLenient(byte c) {
      return SP_LENIENT_BYTES[c + 128];
   }

   private static boolean isWhitespace(byte b) {
      return LATIN_WHITESPACE[b + 128];
   }

   private static int findNonWhitespace(byte[] sb, int offset, int end) {
      for (int result = offset; result < end; result++) {
         byte c = sb[result];
         if (!isWhitespace(c)) {
            return result;
         }

         if (!isOWS(c)) {
            throw new IllegalArgumentException(
               "Invalid separator, only a single space or horizontal tab allowed, but received a '" + c + "' (0x" + Integer.toHexString(c) + ")"
            );
         }
      }

      return end;
   }

   private static int findEndOfString(byte[] sb, int start, int end) {
      for (int result = end - 1; result > start; result--) {
         if (!isOWS(sb[result])) {
            return result + 1;
         }
      }

      return 0;
   }

   private static boolean isOWS(byte ch) {
      return ch == 32 || ch == 9;
   }

   private static boolean isControlOrWhitespaceAsciiChar(byte b) {
      return ISO_CONTROL_OR_WHITESPACE[128 + b];
   }

   static {
      SP_LENIENT_BYTES[160] = true;
      SP_LENIENT_BYTES[137] = true;
      SP_LENIENT_BYTES[139] = true;
      SP_LENIENT_BYTES[140] = true;
      SP_LENIENT_BYTES[141] = true;

      for (byte b = -128; b < 127; b++) {
         LATIN_WHITESPACE[128 + b] = Character.isWhitespace((int)b);
      }

      ISO_CONTROL_OR_WHITESPACE = new boolean[256];

      for (byte b = -128; b < 127; b++) {
         ISO_CONTROL_OR_WHITESPACE[128 + b] = Character.isISOControl((int)b) || isWhitespace(b);
      }

      SKIP_CONTROL_CHARS_BYTES = new ByteProcessor() {
         @Override
         public boolean process(byte value) {
            return HttpObjectDecoder.ISO_CONTROL_OR_WHITESPACE[128 + value];
         }
      };
   }

   private static class HeaderParser {
      protected final ByteBuf seq;
      protected final int maxLength;
      int size;

      HeaderParser(ByteBuf seq, int maxLength) {
         this.seq = seq;
         this.maxLength = maxLength;
      }

      public ByteBuf parse(ByteBuf buffer, Runnable strictCRLFCheck) {
         int readableBytes = buffer.readableBytes();
         int readerIndex = buffer.readerIndex();
         int maxBodySize = this.maxLength - this.size;

         assert maxBodySize >= 0;

         long maxBodySizeWithCRLF = maxBodySize + 2L;
         int toProcess = (int)Math.min(maxBodySizeWithCRLF, (long)readableBytes);
         int toIndexExclusive = readerIndex + toProcess;

         assert toIndexExclusive >= readerIndex;

         int indexOfLf = buffer.indexOf(readerIndex, toIndexExclusive, (byte)10);
         if (indexOfLf == -1) {
            if (readableBytes > maxBodySize) {
               throw this.newException(this.maxLength);
            } else {
               return null;
            }
         } else {
            int endOfSeqIncluded;
            if (indexOfLf > readerIndex && buffer.getByte(indexOfLf - 1) == 13) {
               endOfSeqIncluded = indexOfLf - 1;
            } else {
               if (strictCRLFCheck != null) {
                  strictCRLFCheck.run();
               }

               endOfSeqIncluded = indexOfLf;
            }

            int newSize = endOfSeqIncluded - readerIndex;
            if (newSize == 0) {
               this.seq.clear();
               buffer.readerIndex(indexOfLf + 1);
               return this.seq;
            } else {
               int size = this.size + newSize;
               if (size > this.maxLength) {
                  throw this.newException(this.maxLength);
               } else {
                  this.size = size;
                  this.seq.clear();
                  this.seq.writeBytes(buffer, readerIndex, newSize);
                  buffer.readerIndex(indexOfLf + 1);
                  return this.seq;
               }
            }
         }
      }

      public void reset() {
         this.size = 0;
      }

      protected TooLongFrameException newException(int maxLength) {
         return new TooLongHttpHeaderException("HTTP header is larger than " + maxLength + " bytes.");
      }
   }

   private final class LineParser extends HttpObjectDecoder.HeaderParser {
      LineParser(ByteBuf seq, int maxLength) {
         super(seq, maxLength);
      }

      @Override
      public ByteBuf parse(ByteBuf buffer, Runnable strictCRLFCheck) {
         this.reset();
         int readableBytes = buffer.readableBytes();
         if (readableBytes == 0) {
            return null;
         } else {
            return HttpObjectDecoder.this.currentState == HttpObjectDecoder.State.SKIP_CONTROL_CHARS
                  && this.skipControlChars(buffer, readableBytes, buffer.readerIndex())
               ? null
               : super.parse(buffer, strictCRLFCheck);
         }
      }

      private boolean skipControlChars(ByteBuf buffer, int readableBytes, int readerIndex) {
         assert HttpObjectDecoder.this.currentState == HttpObjectDecoder.State.SKIP_CONTROL_CHARS;

         int maxToSkip = Math.min(this.maxLength, readableBytes);
         int firstNonControlIndex = buffer.forEachByte(readerIndex, maxToSkip, HttpObjectDecoder.SKIP_CONTROL_CHARS_BYTES);
         if (firstNonControlIndex == -1) {
            buffer.skipBytes(maxToSkip);
            if (readableBytes > this.maxLength) {
               throw this.newException(this.maxLength);
            } else {
               return true;
            }
         } else {
            buffer.readerIndex(firstNonControlIndex);
            HttpObjectDecoder.this.currentState = HttpObjectDecoder.State.READ_INITIAL;
            return false;
         }
      }

      @Override
      protected TooLongFrameException newException(int maxLength) {
         return new TooLongHttpLineException("An HTTP line is larger than " + maxLength + " bytes.");
      }
   }

   private static enum State {
      SKIP_CONTROL_CHARS,
      READ_INITIAL,
      READ_HEADER,
      READ_VARIABLE_LENGTH_CONTENT,
      READ_FIXED_LENGTH_CONTENT,
      READ_CHUNK_SIZE,
      READ_CHUNKED_CONTENT,
      READ_CHUNK_DELIMITER,
      READ_CHUNK_FOOTER,
      BAD_MESSAGE,
      UPGRADED;
   }
}
