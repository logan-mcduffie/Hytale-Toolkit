package io.netty.handler.codec.http;

import io.netty.buffer.Unpooled;
import io.netty.util.AsciiString;

public class HttpRequestDecoder extends HttpObjectDecoder {
   private static final AsciiString Accept = AsciiString.cached("Accept");
   private static final AsciiString Host = AsciiString.cached("Host");
   private static final AsciiString Connection = AsciiString.cached("Connection");
   private static final AsciiString ContentType = AsciiString.cached("Content-Type");
   private static final AsciiString ContentLength = AsciiString.cached("Content-Length");
   private static final int GET_AS_INT = 5522759;
   private static final int POST_AS_INT = 1414745936;
   private static final long HTTP_1_1_AS_LONG = 3543824036068086856L;
   private static final long HTTP_1_0_AS_LONG = 3471766442030158920L;
   private static final int HOST_AS_INT = 1953722184;
   private static final long CONNECTION_AS_LONG_0 = 7598807758576447299L;
   private static final short CONNECTION_AS_SHORT_1 = 28271;
   private static final long CONTENT_AS_LONG = 3275364211029339971L;
   private static final int TYPE_AS_INT = 1701869908;
   private static final long LENGTH_AS_LONG = 114849160783180L;
   private static final long ACCEPT_AS_LONG = 128026086171457L;

   public HttpRequestDecoder() {
   }

   public HttpRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
      this(new HttpDecoderConfig().setMaxInitialLineLength(maxInitialLineLength).setMaxHeaderSize(maxHeaderSize).setMaxChunkSize(maxChunkSize));
   }

   @Deprecated
   public HttpRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
      super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders);
   }

   @Deprecated
   public HttpRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize) {
      super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders, initialBufferSize);
   }

   @Deprecated
   public HttpRequestDecoder(
      int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize, boolean allowDuplicateContentLengths
   ) {
      super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders, initialBufferSize, allowDuplicateContentLengths);
   }

   @Deprecated
   public HttpRequestDecoder(
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

   public HttpRequestDecoder(HttpDecoderConfig config) {
      super(config);
   }

   @Override
   protected HttpMessage createMessage(String[] initialLine) throws Exception {
      return new DefaultHttpRequest(HttpVersion.valueOf(initialLine[2], true), HttpMethod.valueOf(initialLine[0]), initialLine[1], this.headersFactory);
   }

   @Override
   protected AsciiString splitHeaderName(byte[] sb, int start, int length) {
      byte firstChar = sb[start];
      if (firstChar == 72) {
         if (length == 4 && isHost(sb, start)) {
            return Host;
         }
      } else if (firstChar == 65) {
         if (length == 6 && isAccept(sb, start)) {
            return Accept;
         }
      } else if (firstChar == 67) {
         if (length == 10) {
            if (isConnection(sb, start)) {
               return Connection;
            }
         } else if (length == 12) {
            if (isContentType(sb, start)) {
               return ContentType;
            }
         } else if (length == 14 && isContentLength(sb, start)) {
            return ContentLength;
         }
      }

      return super.splitHeaderName(sb, start, length);
   }

   private static boolean isAccept(byte[] sb, int start) {
      long maybeAccept = sb[start] | sb[start + 1] << 8 | sb[start + 2] << 16 | sb[start + 3] << 24 | (long)sb[start + 4] << 32 | (long)sb[start + 5] << 40;
      return maybeAccept == 128026086171457L;
   }

   private static boolean isHost(byte[] sb, int start) {
      int maybeHost = sb[start] | sb[start + 1] << 8 | sb[start + 2] << 16 | sb[start + 3] << 24;
      return maybeHost == 1953722184;
   }

   private static boolean isConnection(byte[] sb, int start) {
      long maybeConnecti = sb[start] | sb[start + 1] << 8 | sb[start + 2] << 16 | sb[start + 3] << 24
         | (long)sb[start + 4] << 32
         | (long)sb[start + 5] << 40
         | (long)sb[start + 6] << 48
         | (long)sb[start + 7] << 56;
      if (maybeConnecti != 7598807758576447299L) {
         return false;
      } else {
         short maybeOn = (short)(sb[start + 8] | sb[start + 9] << 8);
         return maybeOn == 28271;
      }
   }

   private static boolean isContentType(byte[] sb, int start) {
      long maybeContent = sb[start] | sb[start + 1] << 8 | sb[start + 2] << 16 | sb[start + 3] << 24
         | (long)sb[start + 4] << 32
         | (long)sb[start + 5] << 40
         | (long)sb[start + 6] << 48
         | (long)sb[start + 7] << 56;
      if (maybeContent != 3275364211029339971L) {
         return false;
      } else {
         int maybeType = sb[start + 8] | sb[start + 9] << 8 | sb[start + 10] << 16 | sb[start + 11] << 24;
         return maybeType == 1701869908;
      }
   }

   private static boolean isContentLength(byte[] sb, int start) {
      long maybeContent = sb[start] | sb[start + 1] << 8 | sb[start + 2] << 16 | sb[start + 3] << 24
         | (long)sb[start + 4] << 32
         | (long)sb[start + 5] << 40
         | (long)sb[start + 6] << 48
         | (long)sb[start + 7] << 56;
      if (maybeContent != 3275364211029339971L) {
         return false;
      } else {
         long maybeLength = sb[start + 8] | sb[start + 9] << 8 | sb[start + 10] << 16 | sb[start + 11] << 24
            | (long)sb[start + 12] << 32
            | (long)sb[start + 13] << 40;
         return maybeLength == 114849160783180L;
      }
   }

   private static boolean isGetMethod(byte[] sb, int start) {
      int maybeGet = sb[start] | sb[start + 1] << 8 | sb[start + 2] << 16;
      return maybeGet == 5522759;
   }

   private static boolean isPostMethod(byte[] sb, int start) {
      int maybePost = sb[start] | sb[start + 1] << 8 | sb[start + 2] << 16 | sb[start + 3] << 24;
      return maybePost == 1414745936;
   }

   @Override
   protected String splitFirstWordInitialLine(byte[] sb, int start, int length) {
      if (length == 3) {
         if (isGetMethod(sb, start)) {
            return HttpMethod.GET.name();
         }
      } else if (length == 4 && isPostMethod(sb, start)) {
         return HttpMethod.POST.name();
      }

      return super.splitFirstWordInitialLine(sb, start, length);
   }

   @Override
   protected String splitThirdWordInitialLine(byte[] sb, int start, int length) {
      if (length == 8) {
         long maybeHttp1_x = sb[start] | sb[start + 1] << 8 | sb[start + 2] << 16 | sb[start + 3] << 24
            | (long)sb[start + 4] << 32
            | (long)sb[start + 5] << 40
            | (long)sb[start + 6] << 48
            | (long)sb[start + 7] << 56;
         if (maybeHttp1_x == 3543824036068086856L) {
            return "HTTP/1.1";
         }

         if (maybeHttp1_x == 3471766442030158920L) {
            return "HTTP/1.0";
         }
      }

      return super.splitThirdWordInitialLine(sb, start, length);
   }

   @Override
   protected HttpMessage createInvalidMessage() {
      return new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "/bad-request", Unpooled.buffer(0), this.headersFactory, this.trailersFactory);
   }

   @Override
   protected boolean isDecodingRequest() {
      return true;
   }

   @Override
   protected boolean isContentAlwaysEmpty(HttpMessage msg) {
      return msg.getClass() == DefaultHttpRequest.class ? false : super.isContentAlwaysEmpty(msg);
   }
}
