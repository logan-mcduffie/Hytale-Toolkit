package com.nimbusds.jose.util;

import com.nimbusds.jose.shaded.jcip.Immutable;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Immutable
public class Base64 implements Serializable {
   private static final long serialVersionUID = 1L;
   private final String value;

   public Base64(String base64) {
      this.value = Objects.requireNonNull(base64);
   }

   public byte[] decode() {
      return Base64Codec.decode(this.value);
   }

   public BigInteger decodeToBigInteger() {
      return new BigInteger(1, this.decode());
   }

   public String decodeToString() {
      return new String(this.decode(), StandardCharset.UTF_8);
   }

   public String toJSONString() {
      return JSONStringUtils.toJSONString(this.value);
   }

   @Override
   public String toString() {
      return this.value;
   }

   @Override
   public int hashCode() {
      return this.value.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      return object instanceof Base64 && this.toString().equals(object.toString());
   }

   public static Base64 from(String base64) {
      return base64 == null ? null : new Base64(base64);
   }

   public static Base64 encode(byte[] bytes) {
      return new Base64(Base64Codec.encodeToString(bytes, false));
   }

   public static Base64 encode(BigInteger bigInt) {
      return encode(BigIntegerUtils.toBytesUnsigned(bigInt));
   }

   public static Base64 encode(String text) {
      return encode(text.getBytes(StandardCharset.UTF_8));
   }
}
