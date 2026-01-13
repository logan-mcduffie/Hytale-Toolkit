package com.nimbusds.jose;

import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.StandardCharset;
import com.nimbusds.jwt.SignedJWT;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

@Immutable
public final class Payload implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Payload.Origin origin;
   private final Map<String, Object> jsonObject;
   private final String string;
   private final byte[] bytes;
   private final Base64URL base64URL;
   private final JWSObject jwsObject;
   private final SignedJWT signedJWT;

   private static String byteArrayToString(byte[] bytes) {
      return bytes != null ? new String(bytes, StandardCharset.UTF_8) : null;
   }

   private static byte[] stringToByteArray(String string) {
      return string != null ? string.getBytes(StandardCharset.UTF_8) : null;
   }

   public Payload(Map<String, Object> jsonObject) {
      this.jsonObject = JSONObjectUtils.newJSONObject();
      this.jsonObject.putAll(Objects.requireNonNull(jsonObject, "The JSON object must not be null"));
      this.string = null;
      this.bytes = null;
      this.base64URL = null;
      this.jwsObject = null;
      this.signedJWT = null;
      this.origin = Payload.Origin.JSON;
   }

   public Payload(String string) {
      this.jsonObject = null;
      this.string = Objects.requireNonNull(string, "The string must not be null");
      this.bytes = null;
      this.base64URL = null;
      this.jwsObject = null;
      this.signedJWT = null;
      this.origin = Payload.Origin.STRING;
   }

   public Payload(byte[] bytes) {
      this.jsonObject = null;
      this.string = null;
      this.bytes = Objects.requireNonNull(bytes, "The byte array must not be null");
      this.base64URL = null;
      this.jwsObject = null;
      this.signedJWT = null;
      this.origin = Payload.Origin.BYTE_ARRAY;
   }

   public Payload(Base64URL base64URL) {
      this.jsonObject = null;
      this.string = null;
      this.bytes = null;
      this.base64URL = Objects.requireNonNull(base64URL, "The Base64URL-encoded object must not be null");
      this.jwsObject = null;
      this.signedJWT = null;
      this.origin = Payload.Origin.BASE64URL;
   }

   public Payload(JWSObject jwsObject) {
      if (jwsObject.getState() == JWSObject.State.UNSIGNED) {
         throw new IllegalArgumentException("The JWS object must be signed");
      } else {
         this.jsonObject = null;
         this.string = null;
         this.bytes = null;
         this.base64URL = null;
         this.jwsObject = jwsObject;
         this.signedJWT = null;
         this.origin = Payload.Origin.JWS_OBJECT;
      }
   }

   public Payload(SignedJWT signedJWT) {
      if (signedJWT.getState() == JWSObject.State.UNSIGNED) {
         throw new IllegalArgumentException("The JWT must be signed");
      } else {
         this.jsonObject = null;
         this.string = null;
         this.bytes = null;
         this.base64URL = null;
         this.signedJWT = signedJWT;
         this.jwsObject = signedJWT;
         this.origin = Payload.Origin.SIGNED_JWT;
      }
   }

   public Payload.Origin getOrigin() {
      return this.origin;
   }

   public Map<String, Object> toJSONObject() {
      if (this.jsonObject != null) {
         return this.jsonObject;
      } else {
         String s = this.toString();
         if (s == null) {
            return null;
         } else {
            try {
               return JSONObjectUtils.parse(s);
            } catch (ParseException var3) {
               return null;
            }
         }
      }
   }

   @Override
   public String toString() {
      if (this.string != null) {
         return this.string;
      } else if (this.jwsObject != null) {
         return this.jwsObject.getParsedString() != null ? this.jwsObject.getParsedString() : this.jwsObject.serialize();
      } else if (this.jsonObject != null) {
         return JSONObjectUtils.toJSONString(this.jsonObject);
      } else if (this.bytes != null) {
         return byteArrayToString(this.bytes);
      } else {
         return this.base64URL != null ? this.base64URL.decodeToString() : null;
      }
   }

   public byte[] toBytes() {
      if (this.bytes != null) {
         return this.bytes;
      } else {
         return this.base64URL != null ? this.base64URL.decode() : stringToByteArray(this.toString());
      }
   }

   public Base64URL toBase64URL() {
      return this.base64URL != null ? this.base64URL : Base64URL.encode(this.toBytes());
   }

   public JWSObject toJWSObject() {
      if (this.jwsObject != null) {
         return this.jwsObject;
      } else {
         try {
            return JWSObject.parse(this.toString());
         } catch (ParseException var2) {
            return null;
         }
      }
   }

   public SignedJWT toSignedJWT() {
      if (this.signedJWT != null) {
         return this.signedJWT;
      } else {
         try {
            return SignedJWT.parse(this.toString());
         } catch (ParseException var2) {
            return null;
         }
      }
   }

   public <T> T toType(PayloadTransformer<T> transformer) {
      return transformer.transform(this);
   }

   public static enum Origin {
      JSON,
      STRING,
      BYTE_ARRAY,
      BASE64URL,
      JWS_OBJECT,
      SIGNED_JWT;
   }
}
