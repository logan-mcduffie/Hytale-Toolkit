package io.netty.handler.codec.http3;

import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import org.jetbrains.annotations.Nullable;

public final class DefaultHttp3Headers extends DefaultHeaders<CharSequence, CharSequence, Http3Headers> implements Http3Headers {
   private static final ByteProcessor HTTP3_NAME_VALIDATOR_PROCESSOR = new ByteProcessor() {
      @Override
      public boolean process(byte value) {
         return !AsciiString.isUpperCase(value);
      }
   };
   static final DefaultHeaders.NameValidator<CharSequence> HTTP3_NAME_VALIDATOR = new DefaultHeaders.NameValidator<CharSequence>() {
      public void validateName(@Nullable CharSequence name) {
         if (name != null && name.length() != 0) {
            if (name instanceof AsciiString) {
               int index;
               try {
                  index = ((AsciiString)name).forEachByte(DefaultHttp3Headers.HTTP3_NAME_VALIDATOR_PROCESSOR);
               } catch (Http3HeadersValidationException var4) {
                  throw var4;
               } catch (Throwable var5) {
                  throw new Http3HeadersValidationException(String.format("unexpected error. invalid header name [%s]", name), var5);
               }

               if (index != -1) {
                  throw new Http3HeadersValidationException(String.format("invalid header name [%s]", name));
               }
            } else {
               for (int i = 0; i < name.length(); i++) {
                  if (AsciiString.isUpperCase(name.charAt(i))) {
                     throw new Http3HeadersValidationException(String.format("invalid header name [%s]", name));
                  }
               }
            }
         } else {
            throw new Http3HeadersValidationException(String.format("empty headers are not allowed [%s]", name));
         }
      }
   };
   private DefaultHeaders.HeaderEntry<CharSequence, CharSequence> firstNonPseudo = this.head;

   public DefaultHttp3Headers() {
      this(true);
   }

   public DefaultHttp3Headers(boolean validate) {
      super(AsciiString.CASE_SENSITIVE_HASHER, CharSequenceValueConverter.INSTANCE, validate ? HTTP3_NAME_VALIDATOR : DefaultHeaders.NameValidator.NOT_NULL);
   }

   public DefaultHttp3Headers(boolean validate, int arraySizeHint) {
      super(
         AsciiString.CASE_SENSITIVE_HASHER,
         CharSequenceValueConverter.INSTANCE,
         validate ? HTTP3_NAME_VALIDATOR : DefaultHeaders.NameValidator.NOT_NULL,
         arraySizeHint
      );
   }

   public Http3Headers clear() {
      this.firstNonPseudo = this.head;
      return (Http3Headers)super.clear();
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof Http3Headers && this.equals((Http3Headers)o, AsciiString.CASE_SENSITIVE_HASHER);
   }

   @Override
   public int hashCode() {
      return this.hashCode(AsciiString.CASE_SENSITIVE_HASHER);
   }

   @Override
   public Http3Headers method(CharSequence value) {
      this.set(Http3Headers.PseudoHeaderName.METHOD.value(), value);
      return this;
   }

   @Override
   public Http3Headers scheme(CharSequence value) {
      this.set(Http3Headers.PseudoHeaderName.SCHEME.value(), value);
      return this;
   }

   @Override
   public Http3Headers authority(CharSequence value) {
      this.set(Http3Headers.PseudoHeaderName.AUTHORITY.value(), value);
      return this;
   }

   @Override
   public Http3Headers path(CharSequence value) {
      this.set(Http3Headers.PseudoHeaderName.PATH.value(), value);
      return this;
   }

   @Override
   public Http3Headers status(CharSequence value) {
      this.set(Http3Headers.PseudoHeaderName.STATUS.value(), value);
      return this;
   }

   @Override
   public CharSequence method() {
      return this.get(Http3Headers.PseudoHeaderName.METHOD.value());
   }

   @Override
   public CharSequence scheme() {
      return this.get(Http3Headers.PseudoHeaderName.SCHEME.value());
   }

   @Override
   public CharSequence authority() {
      return this.get(Http3Headers.PseudoHeaderName.AUTHORITY.value());
   }

   @Override
   public CharSequence path() {
      return this.get(Http3Headers.PseudoHeaderName.PATH.value());
   }

   @Override
   public CharSequence status() {
      return this.get(Http3Headers.PseudoHeaderName.STATUS.value());
   }

   public boolean contains(CharSequence name, CharSequence value) {
      return this.contains(name, value, false);
   }

   @Override
   public boolean contains(CharSequence name, CharSequence value, boolean caseInsensitive) {
      return this.contains(name, value, caseInsensitive ? AsciiString.CASE_INSENSITIVE_HASHER : AsciiString.CASE_SENSITIVE_HASHER);
   }

   protected DefaultHeaders.HeaderEntry<CharSequence, CharSequence> newHeaderEntry(
      int h, CharSequence name, CharSequence value, DefaultHeaders.HeaderEntry<CharSequence, CharSequence> next
   ) {
      return new DefaultHttp3Headers.Http3HeaderEntry(h, name, value, next);
   }

   private final class Http3HeaderEntry extends DefaultHeaders.HeaderEntry<CharSequence, CharSequence> {
      protected Http3HeaderEntry(int hash, CharSequence key, CharSequence value, DefaultHeaders.HeaderEntry<CharSequence, CharSequence> next) {
         super(hash, key);
         this.value = value;
         this.next = next;
         if (Http3Headers.PseudoHeaderName.hasPseudoHeaderFormat(key)) {
            this.after = DefaultHttp3Headers.this.firstNonPseudo;
            this.before = DefaultHttp3Headers.this.firstNonPseudo.before();
         } else {
            this.after = DefaultHttp3Headers.this.head;
            this.before = DefaultHttp3Headers.this.head.before();
            if (DefaultHttp3Headers.this.firstNonPseudo == DefaultHttp3Headers.this.head) {
               DefaultHttp3Headers.this.firstNonPseudo = this;
            }
         }

         this.pointNeighborsToThis();
      }

      @Override
      protected void remove() {
         if (this == DefaultHttp3Headers.this.firstNonPseudo) {
            DefaultHttp3Headers.this.firstNonPseudo = DefaultHttp3Headers.this.firstNonPseudo.after();
         }

         super.remove();
      }
   }
}
