package io.netty.util;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class CharsetUtil {
   public static final Charset UTF_16 = StandardCharsets.UTF_16;
   public static final Charset UTF_16BE = StandardCharsets.UTF_16BE;
   public static final Charset UTF_16LE = StandardCharsets.UTF_16LE;
   public static final Charset UTF_8 = StandardCharsets.UTF_8;
   public static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;
   public static final Charset US_ASCII = StandardCharsets.US_ASCII;
   private static final Charset[] CHARSETS = new Charset[]{UTF_16, UTF_16BE, UTF_16LE, UTF_8, ISO_8859_1, US_ASCII};

   public static Charset[] values() {
      return CHARSETS;
   }

   @Deprecated
   public static CharsetEncoder getEncoder(Charset charset) {
      return encoder(charset);
   }

   public static CharsetEncoder encoder(Charset charset, CodingErrorAction malformedInputAction, CodingErrorAction unmappableCharacterAction) {
      ObjectUtil.checkNotNull(charset, "charset");
      CharsetEncoder e = charset.newEncoder();
      e.onMalformedInput(malformedInputAction).onUnmappableCharacter(unmappableCharacterAction);
      return e;
   }

   public static CharsetEncoder encoder(Charset charset, CodingErrorAction codingErrorAction) {
      return encoder(charset, codingErrorAction, codingErrorAction);
   }

   public static CharsetEncoder encoder(Charset charset) {
      ObjectUtil.checkNotNull(charset, "charset");
      Map<Charset, CharsetEncoder> map = InternalThreadLocalMap.get().charsetEncoderCache();
      CharsetEncoder e = map.get(charset);
      if (e != null) {
         e.reset().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
         return e;
      } else {
         e = encoder(charset, CodingErrorAction.REPLACE, CodingErrorAction.REPLACE);
         map.put(charset, e);
         return e;
      }
   }

   @Deprecated
   public static CharsetDecoder getDecoder(Charset charset) {
      return decoder(charset);
   }

   public static CharsetDecoder decoder(Charset charset, CodingErrorAction malformedInputAction, CodingErrorAction unmappableCharacterAction) {
      ObjectUtil.checkNotNull(charset, "charset");
      CharsetDecoder d = charset.newDecoder();
      d.onMalformedInput(malformedInputAction).onUnmappableCharacter(unmappableCharacterAction);
      return d;
   }

   public static CharsetDecoder decoder(Charset charset, CodingErrorAction codingErrorAction) {
      return decoder(charset, codingErrorAction, codingErrorAction);
   }

   public static CharsetDecoder decoder(Charset charset) {
      ObjectUtil.checkNotNull(charset, "charset");
      Map<Charset, CharsetDecoder> map = InternalThreadLocalMap.get().charsetDecoderCache();
      CharsetDecoder d = map.get(charset);
      if (d != null) {
         d.reset().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
         return d;
      } else {
         d = decoder(charset, CodingErrorAction.REPLACE, CodingErrorAction.REPLACE);
         map.put(charset, d);
         return d;
      }
   }

   private CharsetUtil() {
   }
}
