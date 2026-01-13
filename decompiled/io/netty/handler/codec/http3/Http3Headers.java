package io.netty.handler.codec.http3;

import io.netty.handler.codec.Headers;
import io.netty.util.AsciiString;
import java.util.Iterator;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;

public interface Http3Headers extends Headers<CharSequence, CharSequence, Http3Headers> {
   @Override
   Iterator<Entry<CharSequence, CharSequence>> iterator();

   Iterator<CharSequence> valueIterator(CharSequence var1);

   Http3Headers method(CharSequence var1);

   Http3Headers scheme(CharSequence var1);

   Http3Headers authority(CharSequence var1);

   Http3Headers path(CharSequence var1);

   Http3Headers status(CharSequence var1);

   default Http3Headers protocol(CharSequence value) {
      this.set(Http3Headers.PseudoHeaderName.PROTOCOL.value(), value);
      return this;
   }

   @Nullable
   CharSequence method();

   @Nullable
   CharSequence scheme();

   @Nullable
   CharSequence authority();

   @Nullable
   CharSequence path();

   @Nullable
   CharSequence status();

   @Nullable
   default CharSequence protocol() {
      return this.get(Http3Headers.PseudoHeaderName.PROTOCOL.value());
   }

   boolean contains(CharSequence var1, CharSequence var2, boolean var3);

   public static enum PseudoHeaderName {
      METHOD(":method", true, 1),
      SCHEME(":scheme", true, 2),
      AUTHORITY(":authority", true, 4),
      PATH(":path", true, 8),
      STATUS(":status", false, 16),
      PROTOCOL(":protocol", true, 32);

      private static final char PSEUDO_HEADER_PREFIX = ':';
      private static final byte PSEUDO_HEADER_PREFIX_BYTE = 58;
      private final AsciiString value;
      private final boolean requestOnly;
      private final int flag;
      private static final CharSequenceMap<Http3Headers.PseudoHeaderName> PSEUDO_HEADERS = new CharSequenceMap<>();

      private PseudoHeaderName(String value, boolean requestOnly, int flag) {
         this.value = AsciiString.cached(value);
         this.requestOnly = requestOnly;
         this.flag = flag;
      }

      public AsciiString value() {
         return this.value;
      }

      public static boolean hasPseudoHeaderFormat(CharSequence headerName) {
         if (headerName instanceof AsciiString) {
            AsciiString asciiHeaderName = (AsciiString)headerName;
            return asciiHeaderName.length() > 0 && asciiHeaderName.byteAt(0) == 58;
         } else {
            return headerName.length() > 0 && headerName.charAt(0) == ':';
         }
      }

      public static boolean isPseudoHeader(CharSequence name) {
         return PSEUDO_HEADERS.contains(name);
      }

      @Nullable
      public static Http3Headers.PseudoHeaderName getPseudoHeader(CharSequence name) {
         return PSEUDO_HEADERS.get(name);
      }

      public boolean isRequestOnly() {
         return this.requestOnly;
      }

      public int getFlag() {
         return this.flag;
      }

      static {
         for (Http3Headers.PseudoHeaderName pseudoHeader : values()) {
            PSEUDO_HEADERS.add(pseudoHeader.value(), pseudoHeader);
         }
      }
   }
}
