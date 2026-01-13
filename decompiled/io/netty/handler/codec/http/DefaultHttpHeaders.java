package io.netty.handler.codec.http;

import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.DefaultHeadersImpl;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.HeadersUtils;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.AsciiString;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class DefaultHttpHeaders extends HttpHeaders {
   private final DefaultHeaders<CharSequence, CharSequence, ?> headers;

   public DefaultHttpHeaders() {
      this(nameValidator(true), valueValidator(true));
   }

   @Deprecated
   public DefaultHttpHeaders(boolean validate) {
      this(nameValidator(validate), valueValidator(validate));
   }

   protected DefaultHttpHeaders(boolean validateValues, DefaultHeaders.NameValidator<CharSequence> nameValidator) {
      this(nameValidator, valueValidator(validateValues));
   }

   protected DefaultHttpHeaders(DefaultHeaders.NameValidator<CharSequence> nameValidator, DefaultHeaders.ValueValidator<CharSequence> valueValidator) {
      this(nameValidator, valueValidator, 16);
   }

   protected DefaultHttpHeaders(
      DefaultHeaders.NameValidator<CharSequence> nameValidator, DefaultHeaders.ValueValidator<CharSequence> valueValidator, int sizeHint
   ) {
      this(
         new DefaultHeadersImpl<>(
            AsciiString.CASE_INSENSITIVE_HASHER, DefaultHttpHeaders.HeaderValueConverter.INSTANCE, nameValidator, sizeHint, valueValidator
         )
      );
   }

   protected DefaultHttpHeaders(DefaultHeaders<CharSequence, CharSequence, ?> headers) {
      this.headers = headers;
   }

   public Headers<CharSequence, CharSequence, ?> unwrap() {
      return this.headers;
   }

   @Override
   public HttpHeaders add(HttpHeaders headers) {
      if (headers instanceof DefaultHttpHeaders) {
         this.headers.add(((DefaultHttpHeaders)headers).headers);
         return this;
      } else {
         return super.add(headers);
      }
   }

   @Override
   public HttpHeaders set(HttpHeaders headers) {
      if (headers instanceof DefaultHttpHeaders) {
         this.headers.set(((DefaultHttpHeaders)headers).headers);
         return this;
      } else {
         return super.set(headers);
      }
   }

   @Override
   public HttpHeaders add(String name, Object value) {
      this.headers.addObject(name, value);
      return this;
   }

   @Override
   public HttpHeaders add(CharSequence name, Object value) {
      this.headers.addObject(name, value);
      return this;
   }

   @Override
   public HttpHeaders add(String name, Iterable<?> values) {
      this.headers.addObject(name, values);
      return this;
   }

   @Override
   public HttpHeaders add(CharSequence name, Iterable<?> values) {
      this.headers.addObject(name, values);
      return this;
   }

   @Override
   public HttpHeaders addInt(CharSequence name, int value) {
      this.headers.addInt(name, value);
      return this;
   }

   @Override
   public HttpHeaders addShort(CharSequence name, short value) {
      this.headers.addShort(name, value);
      return this;
   }

   @Override
   public HttpHeaders remove(String name) {
      this.headers.remove(name);
      return this;
   }

   @Override
   public HttpHeaders remove(CharSequence name) {
      this.headers.remove(name);
      return this;
   }

   @Override
   public HttpHeaders set(String name, Object value) {
      this.headers.setObject(name, value);
      return this;
   }

   @Override
   public HttpHeaders set(CharSequence name, Object value) {
      this.headers.setObject(name, value);
      return this;
   }

   @Override
   public HttpHeaders set(String name, Iterable<?> values) {
      this.headers.setObject(name, values);
      return this;
   }

   @Override
   public HttpHeaders set(CharSequence name, Iterable<?> values) {
      this.headers.setObject(name, values);
      return this;
   }

   @Override
   public HttpHeaders setInt(CharSequence name, int value) {
      this.headers.setInt(name, value);
      return this;
   }

   @Override
   public HttpHeaders setShort(CharSequence name, short value) {
      this.headers.setShort(name, value);
      return this;
   }

   @Override
   public HttpHeaders clear() {
      this.headers.clear();
      return this;
   }

   @Override
   public String get(String name) {
      return this.get((CharSequence)name);
   }

   @Override
   public String get(CharSequence name) {
      return HeadersUtils.getAsString(this.headers, name);
   }

   @Override
   public Integer getInt(CharSequence name) {
      return this.headers.getInt(name);
   }

   @Override
   public int getInt(CharSequence name, int defaultValue) {
      return this.headers.getInt(name, defaultValue);
   }

   @Override
   public Short getShort(CharSequence name) {
      return this.headers.getShort(name);
   }

   @Override
   public short getShort(CharSequence name, short defaultValue) {
      return this.headers.getShort(name, defaultValue);
   }

   @Override
   public Long getTimeMillis(CharSequence name) {
      return this.headers.getTimeMillis(name);
   }

   @Override
   public long getTimeMillis(CharSequence name, long defaultValue) {
      return this.headers.getTimeMillis(name, defaultValue);
   }

   @Override
   public List<String> getAll(String name) {
      return this.getAll((CharSequence)name);
   }

   @Override
   public List<String> getAll(CharSequence name) {
      return HeadersUtils.getAllAsString(this.headers, name);
   }

   @Override
   public List<Entry<String, String>> entries() {
      if (this.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<Entry<String, String>> entriesConverted = new ArrayList<>(this.headers.size());

         for (Entry<String, String> entry : this) {
            entriesConverted.add(entry);
         }

         return entriesConverted;
      }
   }

   @Deprecated
   @Override
   public Iterator<Entry<String, String>> iterator() {
      return HeadersUtils.iteratorAsString(this.headers);
   }

   @Override
   public Iterator<Entry<CharSequence, CharSequence>> iteratorCharSequence() {
      return this.headers.iterator();
   }

   @Override
   public Iterator<String> valueStringIterator(CharSequence name) {
      final Iterator<CharSequence> itr = this.valueCharSequenceIterator(name);
      return new Iterator<String>() {
         @Override
         public boolean hasNext() {
            return itr.hasNext();
         }

         public String next() {
            return itr.next().toString();
         }

         @Override
         public void remove() {
            itr.remove();
         }
      };
   }

   @Override
   public Iterator<CharSequence> valueCharSequenceIterator(CharSequence name) {
      return this.headers.valueIterator(name);
   }

   @Override
   public boolean contains(String name) {
      return this.contains((CharSequence)name);
   }

   @Override
   public boolean contains(CharSequence name) {
      return this.headers.contains(name);
   }

   @Override
   public boolean isEmpty() {
      return this.headers.isEmpty();
   }

   @Override
   public int size() {
      return this.headers.size();
   }

   @Override
   public boolean contains(String name, String value, boolean ignoreCase) {
      return this.contains((CharSequence)name, (CharSequence)value, ignoreCase);
   }

   @Override
   public boolean contains(CharSequence name, CharSequence value, boolean ignoreCase) {
      return this.headers.contains(name, value, ignoreCase ? AsciiString.CASE_INSENSITIVE_HASHER : AsciiString.CASE_SENSITIVE_HASHER);
   }

   @Override
   public Set<String> names() {
      return HeadersUtils.namesAsString(this.headers);
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof DefaultHttpHeaders && this.headers.equals(((DefaultHttpHeaders)o).headers, AsciiString.CASE_SENSITIVE_HASHER);
   }

   @Override
   public int hashCode() {
      return this.headers.hashCode(AsciiString.CASE_SENSITIVE_HASHER);
   }

   @Override
   public HttpHeaders copy() {
      return new DefaultHttpHeaders(this.headers.copy());
   }

   static ValueConverter<CharSequence> valueConverter() {
      return DefaultHttpHeaders.HeaderValueConverter.INSTANCE;
   }

   static DefaultHeaders.ValueValidator<CharSequence> valueValidator(boolean validate) {
      return validate
         ? DefaultHttpHeadersFactory.headersFactory().getValueValidator()
         : DefaultHttpHeadersFactory.headersFactory().withValidation(false).getValueValidator();
   }

   static DefaultHeaders.NameValidator<CharSequence> nameValidator(boolean validate) {
      return validate
         ? DefaultHttpHeadersFactory.headersFactory().getNameValidator()
         : DefaultHttpHeadersFactory.headersFactory().withNameValidation(false).getNameValidator();
   }

   private static class HeaderValueConverter extends CharSequenceValueConverter {
      static final DefaultHttpHeaders.HeaderValueConverter INSTANCE = new DefaultHttpHeaders.HeaderValueConverter();

      @Override
      public CharSequence convertObject(Object value) {
         if (value instanceof CharSequence) {
            return (CharSequence)value;
         } else if (value instanceof Date) {
            return DateFormatter.format((Date)value);
         } else {
            return value instanceof Calendar ? DateFormatter.format(((Calendar)value).getTime()) : value.toString();
         }
      }
   }
}
