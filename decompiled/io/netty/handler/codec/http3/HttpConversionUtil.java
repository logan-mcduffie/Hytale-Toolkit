package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.UnsupportedValueConverter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.URI;
import java.util.Iterator;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;

public final class HttpConversionUtil {
   private static final CharSequenceMap<AsciiString> HTTP_TO_HTTP3_HEADER_BLACKLIST = new CharSequenceMap<>();
   private static final AsciiString EMPTY_REQUEST_PATH = AsciiString.cached("/");

   private HttpConversionUtil() {
   }

   private static HttpResponseStatus parseStatus(long streamId, @Nullable CharSequence status) throws Http3Exception {
      try {
         HttpResponseStatus result = HttpResponseStatus.parseLine(status);
         if (result == HttpResponseStatus.SWITCHING_PROTOCOLS) {
            throw streamError(streamId, Http3ErrorCode.H3_MESSAGE_ERROR, "Invalid HTTP/3 status code '" + status + "'", null);
         } else {
            return result;
         }
      } catch (Http3Exception var5) {
         throw var5;
      } catch (Throwable var6) {
         throw streamError(
            streamId, Http3ErrorCode.H3_MESSAGE_ERROR, "Unrecognized HTTP status code '" + status + "' encountered in translation to HTTP/1.x" + status, null
         );
      }
   }

   static FullHttpResponse toFullHttpResponse(long streamId, Http3Headers http3Headers, ByteBufAllocator alloc, boolean validateHttpHeaders) throws Http3Exception {
      ByteBuf content = alloc.buffer();
      HttpResponseStatus status = parseStatus(streamId, http3Headers.status());
      FullHttpResponse msg = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content, validateHttpHeaders);

      try {
         addHttp3ToHttpHeaders(streamId, http3Headers, msg, false);
         return msg;
      } catch (Http3Exception var9) {
         msg.release();
         throw var9;
      } catch (Throwable var10) {
         msg.release();
         throw streamError(streamId, Http3ErrorCode.H3_MESSAGE_ERROR, "HTTP/3 to HTTP/1.x headers conversion error", var10);
      }
   }

   private static CharSequence extractPath(CharSequence method, Http3Headers headers) {
      return HttpMethod.CONNECT.asciiName().contentEqualsIgnoreCase(method)
         ? ObjectUtil.checkNotNull(headers.authority(), "authority header cannot be null in the conversion to HTTP/1.x")
         : ObjectUtil.checkNotNull(headers.path(), "path header cannot be null in conversion to HTTP/1.x");
   }

   static FullHttpRequest toFullHttpRequest(long streamId, Http3Headers http3Headers, ByteBufAllocator alloc, boolean validateHttpHeaders) throws Http3Exception {
      ByteBuf content = alloc.buffer();
      CharSequence method = ObjectUtil.checkNotNull(http3Headers.method(), "method header cannot be null in conversion to HTTP/1.x");
      CharSequence path = extractPath(method, http3Headers);
      FullHttpRequest msg = new DefaultFullHttpRequest(
         HttpVersion.HTTP_1_1, HttpMethod.valueOf(method.toString()), path.toString(), content, validateHttpHeaders
      );

      try {
         addHttp3ToHttpHeaders(streamId, http3Headers, msg, false);
         return msg;
      } catch (Http3Exception var10) {
         msg.release();
         throw var10;
      } catch (Throwable var11) {
         msg.release();
         throw streamError(streamId, Http3ErrorCode.H3_MESSAGE_ERROR, "HTTP/3 to HTTP/1.x headers conversion error", var11);
      }
   }

   static HttpRequest toHttpRequest(long streamId, Http3Headers http3Headers, boolean validateHttpHeaders) throws Http3Exception {
      CharSequence method = ObjectUtil.checkNotNull(http3Headers.method(), "method header cannot be null in conversion to HTTP/1.x");
      CharSequence path = extractPath(method, http3Headers);
      HttpRequest msg = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.valueOf(method.toString()), path.toString(), validateHttpHeaders);

      try {
         addHttp3ToHttpHeaders(streamId, http3Headers, msg.headers(), msg.protocolVersion(), false, true);
         return msg;
      } catch (Http3Exception var8) {
         throw var8;
      } catch (Throwable var9) {
         throw streamError(streamId, Http3ErrorCode.H3_MESSAGE_ERROR, "HTTP/3 to HTTP/1.x headers conversion error", var9);
      }
   }

   static HttpResponse toHttpResponse(long streamId, Http3Headers http3Headers, boolean validateHttpHeaders) throws Http3Exception {
      HttpResponseStatus status = parseStatus(streamId, http3Headers.status());
      HttpResponse msg = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status, validateHttpHeaders);

      try {
         addHttp3ToHttpHeaders(streamId, http3Headers, msg.headers(), msg.protocolVersion(), false, false);
         return msg;
      } catch (Http3Exception var7) {
         throw var7;
      } catch (Throwable var8) {
         throw streamError(streamId, Http3ErrorCode.H3_MESSAGE_ERROR, "HTTP/3 to HTTP/1.x headers conversion error", var8);
      }
   }

   private static void addHttp3ToHttpHeaders(long streamId, Http3Headers inputHeaders, FullHttpMessage destinationMessage, boolean addToTrailer) throws Http3Exception {
      addHttp3ToHttpHeaders(
         streamId,
         inputHeaders,
         addToTrailer ? destinationMessage.trailingHeaders() : destinationMessage.headers(),
         destinationMessage.protocolVersion(),
         addToTrailer,
         destinationMessage instanceof HttpRequest
      );
   }

   static void addHttp3ToHttpHeaders(
      long streamId, Http3Headers inputHeaders, HttpHeaders outputHeaders, HttpVersion httpVersion, boolean isTrailer, boolean isRequest
   ) throws Http3Exception {
      HttpConversionUtil.Http3ToHttpHeaderTranslator translator = new HttpConversionUtil.Http3ToHttpHeaderTranslator(streamId, outputHeaders, isRequest);

      try {
         translator.translateHeaders(inputHeaders);
      } catch (Http3Exception var9) {
         throw var9;
      } catch (Throwable var10) {
         throw streamError(streamId, Http3ErrorCode.H3_MESSAGE_ERROR, "HTTP/3 to HTTP/1.x headers conversion error", var10);
      }

      outputHeaders.remove(HttpHeaderNames.TRANSFER_ENCODING);
      outputHeaders.remove(HttpHeaderNames.TRAILER);
      if (!isTrailer) {
         outputHeaders.set(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(), streamId);
         HttpUtil.setKeepAlive(outputHeaders, httpVersion, true);
      }
   }

   static Http3Headers toHttp3Headers(HttpMessage in, boolean validateHeaders) {
      HttpHeaders inHeaders = in.headers();
      Http3Headers out = new DefaultHttp3Headers(validateHeaders, inHeaders.size());
      if (in instanceof HttpRequest) {
         HttpRequest request = (HttpRequest)in;
         URI requestTargetUri = URI.create(request.uri());
         out.path(toHttp3Path(requestTargetUri));
         out.method(request.method().asciiName());
         setHttp3Scheme(inHeaders, requestTargetUri, out);
         String host = inHeaders.getAsString(HttpHeaderNames.HOST);
         if (host != null && !host.isEmpty()) {
            setHttp3Authority(host, out);
         } else if (!HttpUtil.isOriginForm(request.uri()) && !HttpUtil.isAsteriskForm(request.uri())) {
            setHttp3Authority(requestTargetUri.getAuthority(), out);
         }
      } else if (in instanceof HttpResponse) {
         HttpResponse response = (HttpResponse)in;
         out.status(response.status().codeAsText());
      }

      toHttp3Headers(inHeaders, out);
      return out;
   }

   static Http3Headers toHttp3Headers(HttpHeaders inHeaders, boolean validateHeaders) {
      if (inHeaders.isEmpty()) {
         return new DefaultHttp3Headers();
      } else {
         Http3Headers out = new DefaultHttp3Headers(validateHeaders, inHeaders.size());
         toHttp3Headers(inHeaders, out);
         return out;
      }
   }

   private static CharSequenceMap<AsciiString> toLowercaseMap(Iterator<? extends CharSequence> valuesIter, int arraySizeHint) {
      UnsupportedValueConverter<AsciiString> valueConverter = UnsupportedValueConverter.instance();
      CharSequenceMap<AsciiString> result = new CharSequenceMap<>(true, valueConverter, arraySizeHint);

      while (valuesIter.hasNext()) {
         AsciiString lowerCased = AsciiString.of(valuesIter.next()).toLowerCase();

         try {
            int index = lowerCased.forEachByte(ByteProcessor.FIND_COMMA);
            if (index == -1) {
               result.add(lowerCased.trim(), AsciiString.EMPTY_STRING);
            } else {
               int start = 0;

               do {
                  result.add(lowerCased.subSequence(start, index, false).trim(), AsciiString.EMPTY_STRING);
                  start = index + 1;
               } while (start < lowerCased.length() && (index = lowerCased.forEachByte(start, lowerCased.length() - start, ByteProcessor.FIND_COMMA)) != -1);

               result.add(lowerCased.subSequence(start, lowerCased.length(), false).trim(), AsciiString.EMPTY_STRING);
            }
         } catch (Exception var7) {
            throw new IllegalStateException(var7);
         }
      }

      return result;
   }

   private static void toHttp3HeadersFilterTE(Entry<CharSequence, CharSequence> entry, Http3Headers out) {
      if (AsciiString.indexOf(entry.getValue(), ',', 0) == -1) {
         if (AsciiString.contentEqualsIgnoreCase(AsciiString.trim(entry.getValue()), HttpHeaderValues.TRAILERS)) {
            out.add(HttpHeaderNames.TE, HttpHeaderValues.TRAILERS);
         }
      } else {
         for (CharSequence teValue : StringUtil.unescapeCsvFields(entry.getValue())) {
            if (AsciiString.contentEqualsIgnoreCase(AsciiString.trim(teValue), HttpHeaderValues.TRAILERS)) {
               out.add(HttpHeaderNames.TE, HttpHeaderValues.TRAILERS);
               break;
            }
         }
      }
   }

   static void toHttp3Headers(HttpHeaders inHeaders, Http3Headers out) {
      Iterator<Entry<CharSequence, CharSequence>> iter = inHeaders.iteratorCharSequence();
      CharSequenceMap<AsciiString> connectionBlacklist = toLowercaseMap(inHeaders.valueCharSequenceIterator(HttpHeaderNames.CONNECTION), 8);

      while (iter.hasNext()) {
         Entry<CharSequence, CharSequence> entry = iter.next();
         AsciiString aName = AsciiString.of(entry.getKey()).toLowerCase();
         if (!HTTP_TO_HTTP3_HEADER_BLACKLIST.contains(aName) && !connectionBlacklist.contains(aName)) {
            if (aName.contentEqualsIgnoreCase(HttpHeaderNames.TE)) {
               toHttp3HeadersFilterTE(entry, out);
            } else if (aName.contentEqualsIgnoreCase(HttpHeaderNames.COOKIE)) {
               AsciiString value = AsciiString.of(entry.getValue());

               try {
                  int index = value.forEachByte(ByteProcessor.FIND_SEMI_COLON);
                  if (index == -1) {
                     out.add(HttpHeaderNames.COOKIE, value);
                  } else {
                     int start = 0;

                     do {
                        out.add(HttpHeaderNames.COOKIE, value.subSequence(start, index, false));
                        start = index + 2;
                     } while (start < value.length() && (index = value.forEachByte(start, value.length() - start, ByteProcessor.FIND_SEMI_COLON)) != -1);

                     if (start >= value.length()) {
                        throw new IllegalArgumentException("cookie value is of unexpected format: " + value);
                     }

                     out.add(HttpHeaderNames.COOKIE, value.subSequence(start, value.length(), false));
                  }
               } catch (Exception var9) {
                  throw new IllegalStateException(var9);
               }
            } else {
               out.add(aName, entry.getValue());
            }
         }
      }
   }

   private static AsciiString toHttp3Path(URI uri) {
      StringBuilder pathBuilder = new StringBuilder(
         StringUtil.length(uri.getRawPath()) + StringUtil.length(uri.getRawQuery()) + StringUtil.length(uri.getRawFragment()) + 2
      );
      if (!StringUtil.isNullOrEmpty(uri.getRawPath())) {
         pathBuilder.append(uri.getRawPath());
      }

      if (!StringUtil.isNullOrEmpty(uri.getRawQuery())) {
         pathBuilder.append('?');
         pathBuilder.append(uri.getRawQuery());
      }

      if (!StringUtil.isNullOrEmpty(uri.getRawFragment())) {
         pathBuilder.append('#');
         pathBuilder.append(uri.getRawFragment());
      }

      String path = pathBuilder.toString();
      return path.isEmpty() ? EMPTY_REQUEST_PATH : new AsciiString(path);
   }

   static void setHttp3Authority(@Nullable String authority, Http3Headers out) {
      if (authority != null) {
         if (authority.isEmpty()) {
            out.authority(AsciiString.EMPTY_STRING);
         } else {
            int start = authority.indexOf(64) + 1;
            int length = authority.length() - start;
            if (length == 0) {
               throw new IllegalArgumentException("authority: " + authority);
            }

            out.authority(new AsciiString(authority, start, length));
         }
      }
   }

   private static void setHttp3Scheme(HttpHeaders in, URI uri, Http3Headers out) {
      String value = uri.getScheme();
      if (value != null) {
         out.scheme(new AsciiString(value));
      } else {
         CharSequence cValue = in.get(HttpConversionUtil.ExtensionHeaderNames.SCHEME.text());
         if (cValue != null) {
            out.scheme(AsciiString.of(cValue));
         } else {
            if (uri.getPort() == HttpScheme.HTTPS.port()) {
               out.scheme(HttpScheme.HTTPS.name());
            } else {
               if (uri.getPort() != HttpScheme.HTTP.port()) {
                  throw new IllegalArgumentException(":scheme must be specified. see https://quicwg.org/base-drafts/draft-ietf-quic-http.html#section-4.1.1.1");
               }

               out.scheme(HttpScheme.HTTP.name());
            }
         }
      }
   }

   private static Http3Exception streamError(long streamId, Http3ErrorCode error, String msg, @Nullable Throwable cause) {
      return new Http3Exception(error, streamId + ": " + msg, cause);
   }

   static {
      HTTP_TO_HTTP3_HEADER_BLACKLIST.add(HttpHeaderNames.CONNECTION, AsciiString.EMPTY_STRING);
      AsciiString keepAlive = HttpHeaderNames.KEEP_ALIVE;
      HTTP_TO_HTTP3_HEADER_BLACKLIST.add(keepAlive, AsciiString.EMPTY_STRING);
      AsciiString proxyConnection = HttpHeaderNames.PROXY_CONNECTION;
      HTTP_TO_HTTP3_HEADER_BLACKLIST.add(proxyConnection, AsciiString.EMPTY_STRING);
      HTTP_TO_HTTP3_HEADER_BLACKLIST.add(HttpHeaderNames.TRANSFER_ENCODING, AsciiString.EMPTY_STRING);
      HTTP_TO_HTTP3_HEADER_BLACKLIST.add(HttpHeaderNames.HOST, AsciiString.EMPTY_STRING);
      HTTP_TO_HTTP3_HEADER_BLACKLIST.add(HttpHeaderNames.UPGRADE, AsciiString.EMPTY_STRING);
      HTTP_TO_HTTP3_HEADER_BLACKLIST.add(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(), AsciiString.EMPTY_STRING);
      HTTP_TO_HTTP3_HEADER_BLACKLIST.add(HttpConversionUtil.ExtensionHeaderNames.SCHEME.text(), AsciiString.EMPTY_STRING);
      HTTP_TO_HTTP3_HEADER_BLACKLIST.add(HttpConversionUtil.ExtensionHeaderNames.PATH.text(), AsciiString.EMPTY_STRING);
   }

   public static enum ExtensionHeaderNames {
      STREAM_ID("x-http3-stream-id"),
      SCHEME("x-http3-scheme"),
      PATH("x-http3-path"),
      STREAM_PROMISE_ID("x-http3-stream-promise-id");

      private final AsciiString text;

      private ExtensionHeaderNames(String text) {
         this.text = AsciiString.cached(text);
      }

      public AsciiString text() {
         return this.text;
      }
   }

   private static final class Http3ToHttpHeaderTranslator {
      private static final CharSequenceMap<AsciiString> REQUEST_HEADER_TRANSLATIONS = new CharSequenceMap<>();
      private static final CharSequenceMap<AsciiString> RESPONSE_HEADER_TRANSLATIONS = new CharSequenceMap<>();
      private final long streamId;
      private final HttpHeaders output;
      private final CharSequenceMap<AsciiString> translations;

      Http3ToHttpHeaderTranslator(long streamId, HttpHeaders output, boolean request) {
         this.streamId = streamId;
         this.output = output;
         this.translations = request ? REQUEST_HEADER_TRANSLATIONS : RESPONSE_HEADER_TRANSLATIONS;
      }

      void translateHeaders(Iterable<Entry<CharSequence, CharSequence>> inputHeaders) throws Http3Exception {
         StringBuilder cookies = null;

         for (Entry<CharSequence, CharSequence> entry : inputHeaders) {
            CharSequence name = entry.getKey();
            CharSequence value = entry.getValue();
            AsciiString translatedName = this.translations.get(name);
            if (translatedName != null) {
               this.output.add(translatedName, AsciiString.of(value));
            } else if (!Http3Headers.PseudoHeaderName.isPseudoHeader(name)) {
               if (name.length() == 0 || name.charAt(0) == ':') {
                  throw HttpConversionUtil.streamError(
                     this.streamId, Http3ErrorCode.H3_MESSAGE_ERROR, "Invalid HTTP/3 header '" + name + "' encountered in translation to HTTP/1.x", null
                  );
               }

               if (HttpHeaderNames.COOKIE.equals(name)) {
                  if (cookies == null) {
                     cookies = InternalThreadLocalMap.get().stringBuilder();
                  } else if (cookies.length() > 0) {
                     cookies.append("; ");
                  }

                  cookies.append(value);
               } else {
                  this.output.add(name, value);
               }
            }
         }

         if (cookies != null) {
            this.output.add(HttpHeaderNames.COOKIE, cookies.toString());
         }
      }

      static {
         RESPONSE_HEADER_TRANSLATIONS.add(Http3Headers.PseudoHeaderName.AUTHORITY.value(), HttpHeaderNames.HOST);
         RESPONSE_HEADER_TRANSLATIONS.add(Http3Headers.PseudoHeaderName.SCHEME.value(), HttpConversionUtil.ExtensionHeaderNames.SCHEME.text());
         REQUEST_HEADER_TRANSLATIONS.add(RESPONSE_HEADER_TRANSLATIONS);
         RESPONSE_HEADER_TRANSLATIONS.add(Http3Headers.PseudoHeaderName.PATH.value(), HttpConversionUtil.ExtensionHeaderNames.PATH.text());
      }
   }
}
