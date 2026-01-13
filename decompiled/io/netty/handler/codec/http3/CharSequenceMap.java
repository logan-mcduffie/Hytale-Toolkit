package io.netty.handler.codec.http3;

import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.UnsupportedValueConverter;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.AsciiString;

final class CharSequenceMap<V> extends DefaultHeaders<CharSequence, V, CharSequenceMap<V>> {
   CharSequenceMap() {
      this(true);
   }

   CharSequenceMap(boolean caseSensitive) {
      this(caseSensitive, UnsupportedValueConverter.instance());
   }

   CharSequenceMap(boolean caseSensitive, ValueConverter<V> valueConverter) {
      super(caseSensitive ? AsciiString.CASE_SENSITIVE_HASHER : AsciiString.CASE_INSENSITIVE_HASHER, valueConverter);
   }

   CharSequenceMap(boolean caseSensitive, ValueConverter<V> valueConverter, int arraySizeHint) {
      super(
         caseSensitive ? AsciiString.CASE_SENSITIVE_HASHER : AsciiString.CASE_INSENSITIVE_HASHER,
         valueConverter,
         DefaultHeaders.NameValidator.NOT_NULL,
         arraySizeHint
      );
   }
}
