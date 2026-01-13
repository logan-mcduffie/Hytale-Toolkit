package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.nio.charset.Charset;
import java.util.List;

public class HttpPostRequestDecoder implements InterfaceHttpPostRequestDecoder {
   static final int DEFAULT_DISCARD_THRESHOLD = 10485760;
   static final int DEFAULT_MAX_FIELDS = 128;
   static final int DEFAULT_MAX_BUFFERED_BYTES = 1024;
   private final InterfaceHttpPostRequestDecoder decoder;

   public HttpPostRequestDecoder(HttpRequest request) {
      this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
   }

   public HttpPostRequestDecoder(HttpRequest request, int maxFields, int maxBufferedBytes) {
      this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET, maxFields, maxBufferedBytes);
   }

   public HttpPostRequestDecoder(HttpDataFactory factory, HttpRequest request) {
      this(factory, request, HttpConstants.DEFAULT_CHARSET);
   }

   public HttpPostRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) {
      ObjectUtil.checkNotNull(factory, "factory");
      ObjectUtil.checkNotNull(request, "request");
      ObjectUtil.checkNotNull(charset, "charset");
      if (isMultipart(request)) {
         this.decoder = new HttpPostMultipartRequestDecoder(factory, request, charset);
      } else {
         this.decoder = new HttpPostStandardRequestDecoder(factory, request, charset);
      }
   }

   public HttpPostRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset, int maxFields, int maxBufferedBytes) {
      ObjectUtil.checkNotNull(factory, "factory");
      ObjectUtil.checkNotNull(request, "request");
      ObjectUtil.checkNotNull(charset, "charset");
      if (isMultipart(request)) {
         this.decoder = new HttpPostMultipartRequestDecoder(factory, request, charset, maxFields, maxBufferedBytes);
      } else {
         this.decoder = new HttpPostStandardRequestDecoder(factory, request, charset, maxFields, maxBufferedBytes);
      }
   }

   public static boolean isMultipart(HttpRequest request) {
      String mimeType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
      return mimeType != null && mimeType.startsWith(HttpHeaderValues.MULTIPART_FORM_DATA.toString()) ? getMultipartDataBoundary(mimeType) != null : false;
   }

   protected static String[] getMultipartDataBoundary(String contentType) {
      String[] headerContentType = splitHeaderContentType(contentType);
      String multiPartHeader = HttpHeaderValues.MULTIPART_FORM_DATA.toString();
      if (headerContentType[0].regionMatches(true, 0, multiPartHeader, 0, multiPartHeader.length())) {
         String boundaryHeader = HttpHeaderValues.BOUNDARY.toString();
         int mrank;
         int crank;
         if (headerContentType[1].regionMatches(true, 0, boundaryHeader, 0, boundaryHeader.length())) {
            mrank = 1;
            crank = 2;
         } else {
            if (!headerContentType[2].regionMatches(true, 0, boundaryHeader, 0, boundaryHeader.length())) {
               return null;
            }

            mrank = 2;
            crank = 1;
         }

         String boundary = StringUtil.substringAfter(headerContentType[mrank], '=');
         if (boundary == null) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Needs a boundary value");
         } else {
            if (boundary.charAt(0) == '"') {
               String bound = boundary.trim();
               int index = bound.length() - 1;
               if (bound.charAt(index) == '"') {
                  boundary = bound.substring(1, index);
               }
            }

            String charsetHeader = HttpHeaderValues.CHARSET.toString();
            if (headerContentType[crank].regionMatches(true, 0, charsetHeader, 0, charsetHeader.length())) {
               String charset = StringUtil.substringAfter(headerContentType[crank], '=');
               if (charset != null) {
                  return new String[]{"--" + boundary, charset};
               }
            }

            return new String[]{"--" + boundary};
         }
      } else {
         return null;
      }
   }

   @Override
   public boolean isMultipart() {
      return this.decoder.isMultipart();
   }

   @Override
   public void setDiscardThreshold(int discardThreshold) {
      this.decoder.setDiscardThreshold(discardThreshold);
   }

   @Override
   public int getDiscardThreshold() {
      return this.decoder.getDiscardThreshold();
   }

   @Override
   public List<InterfaceHttpData> getBodyHttpDatas() {
      return this.decoder.getBodyHttpDatas();
   }

   @Override
   public List<InterfaceHttpData> getBodyHttpDatas(String name) {
      return this.decoder.getBodyHttpDatas(name);
   }

   @Override
   public InterfaceHttpData getBodyHttpData(String name) {
      return this.decoder.getBodyHttpData(name);
   }

   @Override
   public InterfaceHttpPostRequestDecoder offer(HttpContent content) {
      return this.decoder.offer(content);
   }

   @Override
   public boolean hasNext() {
      return this.decoder.hasNext();
   }

   @Override
   public InterfaceHttpData next() {
      return this.decoder.next();
   }

   @Override
   public InterfaceHttpData currentPartialHttpData() {
      return this.decoder.currentPartialHttpData();
   }

   @Override
   public void destroy() {
      this.decoder.destroy();
   }

   @Override
   public void cleanFiles() {
      this.decoder.cleanFiles();
   }

   @Override
   public void removeHttpDataFromClean(InterfaceHttpData data) {
      this.decoder.removeHttpDataFromClean(data);
   }

   private static String[] splitHeaderContentType(String sb) {
      int aStart = HttpPostBodyUtil.findNonWhitespace(sb, 0);
      int aEnd = sb.indexOf(59);
      if (aEnd == -1) {
         return new String[]{sb, "", ""};
      } else {
         int bStart = HttpPostBodyUtil.findNonWhitespace(sb, aEnd + 1);
         if (sb.charAt(aEnd - 1) == ' ') {
            aEnd--;
         }

         int bEnd = sb.indexOf(59, bStart);
         if (bEnd == -1) {
            bEnd = HttpPostBodyUtil.findEndOfString(sb);
            return new String[]{sb.substring(aStart, aEnd), sb.substring(bStart, bEnd), ""};
         } else {
            int cStart = HttpPostBodyUtil.findNonWhitespace(sb, bEnd + 1);
            if (sb.charAt(bEnd - 1) == ' ') {
               bEnd--;
            }

            int cEnd = HttpPostBodyUtil.findEndOfString(sb);
            return new String[]{sb.substring(aStart, aEnd), sb.substring(bStart, bEnd), sb.substring(cStart, cEnd)};
         }
      }
   }

   public static class EndOfDataDecoderException extends DecoderException {
      private static final long serialVersionUID = 1336267941020800769L;
   }

   public static class ErrorDataDecoderException extends DecoderException {
      private static final long serialVersionUID = 5020247425493164465L;

      public ErrorDataDecoderException() {
      }

      public ErrorDataDecoderException(String msg) {
         super(msg);
      }

      public ErrorDataDecoderException(Throwable cause) {
         super(cause);
      }

      public ErrorDataDecoderException(String msg, Throwable cause) {
         super(msg, cause);
      }
   }

   protected static enum MultiPartStatus {
      NOTSTARTED,
      PREAMBLE,
      HEADERDELIMITER,
      DISPOSITION,
      FIELD,
      FILEUPLOAD,
      MIXEDPREAMBLE,
      MIXEDDELIMITER,
      MIXEDDISPOSITION,
      MIXEDFILEUPLOAD,
      MIXEDCLOSEDELIMITER,
      CLOSEDELIMITER,
      PREEPILOGUE,
      EPILOGUE;
   }

   public static class NotEnoughDataDecoderException extends DecoderException {
      private static final long serialVersionUID = -7846841864603865638L;

      public NotEnoughDataDecoderException() {
      }

      public NotEnoughDataDecoderException(String msg) {
         super(msg);
      }

      public NotEnoughDataDecoderException(Throwable cause) {
         super(cause);
      }

      public NotEnoughDataDecoderException(String msg, Throwable cause) {
         super(msg, cause);
      }
   }

   public static final class TooLongFormFieldException extends DecoderException {
      private static final long serialVersionUID = 1336267941020800769L;
   }

   public static final class TooManyFormFieldsException extends DecoderException {
      private static final long serialVersionUID = 1336267941020800769L;
   }
}
