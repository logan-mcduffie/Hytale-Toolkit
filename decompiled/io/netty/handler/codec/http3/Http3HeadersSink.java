package io.netty.handler.codec.http3;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import java.util.function.BiConsumer;

final class Http3HeadersSink implements BiConsumer<CharSequence, CharSequence> {
   private final Http3Headers headers;
   private final long maxHeaderListSize;
   private final boolean validate;
   private final boolean trailer;
   private long headersLength;
   private boolean exceededMaxLength;
   private Http3HeadersValidationException validationException;
   private Http3HeadersSink.HeaderType previousType;
   private boolean request;
   private int receivedPseudoHeaders;

   Http3HeadersSink(Http3Headers headers, long maxHeaderListSize, boolean validate, boolean trailer) {
      this.headers = headers;
      this.maxHeaderListSize = maxHeaderListSize;
      this.validate = validate;
      this.trailer = trailer;
   }

   void finish() throws Http3HeadersValidationException, Http3Exception {
      if (this.exceededMaxLength) {
         throw new Http3Exception(Http3ErrorCode.H3_EXCESSIVE_LOAD, String.format("Header size exceeded max allowed size (%d)", this.maxHeaderListSize));
      } else if (this.validationException != null) {
         throw this.validationException;
      } else {
         if (this.validate) {
            if (this.trailer) {
               if (this.receivedPseudoHeaders != 0) {
                  throw new Http3HeadersValidationException("Pseudo-header(s) included in trailers.");
               }

               return;
            }

            if (this.request) {
               CharSequence method = this.headers.method();
               if (HttpMethod.CONNECT.asciiName().contentEqualsIgnoreCase(method)) {
                  if ((this.receivedPseudoHeaders & Http3Headers.PseudoHeaderName.PROTOCOL.getFlag()) != 0) {
                     int requiredPseudoHeaders = Http3Headers.PseudoHeaderName.METHOD.getFlag()
                        | Http3Headers.PseudoHeaderName.SCHEME.getFlag()
                        | Http3Headers.PseudoHeaderName.AUTHORITY.getFlag()
                        | Http3Headers.PseudoHeaderName.PATH.getFlag()
                        | Http3Headers.PseudoHeaderName.PROTOCOL.getFlag();
                     if (this.receivedPseudoHeaders != requiredPseudoHeaders) {
                        throw new Http3HeadersValidationException("Not all mandatory pseudo-headers included for Extended CONNECT.");
                     }
                  } else {
                     int requiredPseudoHeaders = Http3Headers.PseudoHeaderName.METHOD.getFlag() | Http3Headers.PseudoHeaderName.AUTHORITY.getFlag();
                     if (this.receivedPseudoHeaders != requiredPseudoHeaders) {
                        throw new Http3HeadersValidationException("Not all mandatory pseudo-headers included.");
                     }
                  }
               } else if (HttpMethod.OPTIONS.asciiName().contentEqualsIgnoreCase(method)) {
                  int requiredPseudoHeaders = Http3Headers.PseudoHeaderName.METHOD.getFlag()
                     | Http3Headers.PseudoHeaderName.SCHEME.getFlag()
                     | Http3Headers.PseudoHeaderName.PATH.getFlag();
                  if ((this.receivedPseudoHeaders & requiredPseudoHeaders) != requiredPseudoHeaders
                     || !this.authorityOrHostHeaderReceived() && !"*".contentEquals(this.headers.path())) {
                     throw new Http3HeadersValidationException("Not all mandatory pseudo-headers included.");
                  }
               } else {
                  int requiredPseudoHeaders = Http3Headers.PseudoHeaderName.METHOD.getFlag()
                     | Http3Headers.PseudoHeaderName.SCHEME.getFlag()
                     | Http3Headers.PseudoHeaderName.PATH.getFlag();
                  if ((this.receivedPseudoHeaders & requiredPseudoHeaders) != requiredPseudoHeaders || !this.authorityOrHostHeaderReceived()) {
                     throw new Http3HeadersValidationException("Not all mandatory pseudo-headers included.");
                  }
               }
            } else if (this.receivedPseudoHeaders != Http3Headers.PseudoHeaderName.STATUS.getFlag()) {
               throw new Http3HeadersValidationException("Not all mandatory pseudo-headers included.");
            }
         }
      }
   }

   private boolean authorityOrHostHeaderReceived() {
      return (this.receivedPseudoHeaders & Http3Headers.PseudoHeaderName.AUTHORITY.getFlag()) == Http3Headers.PseudoHeaderName.AUTHORITY.getFlag()
         || this.headers.contains(HttpHeaderNames.HOST);
   }

   public void accept(CharSequence name, CharSequence value) {
      this.headersLength = this.headersLength + QpackHeaderField.sizeOf(name, value);
      this.exceededMaxLength = this.exceededMaxLength | this.headersLength > this.maxHeaderListSize;
      if (!this.exceededMaxLength && this.validationException == null) {
         if (this.validate) {
            try {
               this.validate(this.headers, name);
            } catch (Http3HeadersValidationException var4) {
               this.validationException = var4;
               return;
            }
         }

         this.headers.add(name, value);
      }
   }

   private void validate(Http3Headers headers, CharSequence name) {
      if (Http3Headers.PseudoHeaderName.hasPseudoHeaderFormat(name)) {
         if (this.previousType == Http3HeadersSink.HeaderType.REGULAR_HEADER) {
            throw new Http3HeadersValidationException(String.format("Pseudo-header field '%s' found after regular header.", name));
         }

         Http3Headers.PseudoHeaderName pseudoHeader = Http3Headers.PseudoHeaderName.getPseudoHeader(name);
         if (pseudoHeader == null) {
            throw new Http3HeadersValidationException(String.format("Invalid HTTP/3 pseudo-header '%s' encountered.", name));
         }

         if ((this.receivedPseudoHeaders & pseudoHeader.getFlag()) != 0) {
            throw new Http3HeadersValidationException(String.format("Pseudo-header field '%s' exists already.", name));
         }

         this.receivedPseudoHeaders = this.receivedPseudoHeaders | pseudoHeader.getFlag();
         Http3HeadersSink.HeaderType currentHeaderType = pseudoHeader.isRequestOnly()
            ? Http3HeadersSink.HeaderType.REQUEST_PSEUDO_HEADER
            : Http3HeadersSink.HeaderType.RESPONSE_PSEUDO_HEADER;
         this.request = pseudoHeader.isRequestOnly();
         this.previousType = currentHeaderType;
      } else {
         this.previousType = Http3HeadersSink.HeaderType.REGULAR_HEADER;
      }
   }

   private static enum HeaderType {
      REGULAR_HEADER,
      REQUEST_PSEUDO_HEADER,
      RESPONSE_PSEUDO_HEADER;
   }
}
