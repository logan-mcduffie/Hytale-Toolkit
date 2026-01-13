package io.netty.handler.codec.http.cookie;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.CharBuffer;

public abstract class CookieDecoder {
   private final InternalLogger logger = InternalLoggerFactory.getInstance(this.getClass());
   private final boolean strict;

   protected CookieDecoder(boolean strict) {
      this.strict = strict;
   }

   protected DefaultCookie initCookie(String header, int nameBegin, int nameEnd, int valueBegin, int valueEnd) {
      if (nameBegin == -1 || nameBegin == nameEnd) {
         this.logger.debug("Skipping cookie with null name");
         return null;
      } else if (valueBegin == -1) {
         this.logger.debug("Skipping cookie with null value");
         return null;
      } else {
         CharSequence wrappedValue = CharBuffer.wrap(header, valueBegin, valueEnd);
         CharSequence unwrappedValue = CookieUtil.unwrapValue(wrappedValue);
         if (unwrappedValue == null) {
            this.logger.debug("Skipping cookie because starting quotes are not properly balanced in '{}'", wrappedValue);
            return null;
         } else {
            String name = header.substring(nameBegin, nameEnd);
            int invalidOctetPos;
            if (this.strict && (invalidOctetPos = CookieUtil.firstInvalidCookieNameOctet(name)) >= 0) {
               if (this.logger.isDebugEnabled()) {
                  this.logger.debug("Skipping cookie because name '{}' contains invalid char '{}'", name, name.charAt(invalidOctetPos));
               }

               return null;
            } else {
               boolean wrap = unwrappedValue.length() != valueEnd - valueBegin;
               if (this.strict && (invalidOctetPos = CookieUtil.firstInvalidCookieValueOctet(unwrappedValue)) >= 0) {
                  if (this.logger.isDebugEnabled()) {
                     this.logger.debug("Skipping cookie because value '{}' contains invalid char '{}'", unwrappedValue, unwrappedValue.charAt(invalidOctetPos));
                  }

                  return null;
               } else {
                  DefaultCookie cookie = new DefaultCookie(name, unwrappedValue.toString());
                  cookie.setWrap(wrap);
                  return cookie;
               }
            }
         }
      }
   }
}
