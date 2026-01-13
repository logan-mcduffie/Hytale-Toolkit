package com.google.crypto.tink.jwt;

import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.subtle.Base64;
import com.google.gson.JsonObject;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Optional;

final class JwtFormat {
   private JwtFormat() {
   }

   static boolean isValidUrlsafeBase64Char(char c) {
      return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '-' || c == '_';
   }

   static void validateUtf8(byte[] data) throws JwtInvalidException {
      CharsetDecoder decoder = Util.UTF_8.newDecoder();

      try {
         decoder.decode(ByteBuffer.wrap(data));
      } catch (CharacterCodingException var3) {
         throw new JwtInvalidException(var3.getMessage());
      }
   }

   static byte[] strictUrlSafeDecode(String encodedData) throws JwtInvalidException {
      for (int i = 0; i < encodedData.length(); i++) {
         char c = encodedData.charAt(i);
         if (!isValidUrlsafeBase64Char(c)) {
            throw new JwtInvalidException("invalid encoding");
         }
      }

      try {
         return Base64.urlSafeDecode(encodedData);
      } catch (IllegalArgumentException var3) {
         throw new JwtInvalidException("invalid encoding: " + var3);
      }
   }

   private static void validateAlgorithm(String algo) throws InvalidAlgorithmParameterException {
      switch (algo) {
         case "HS256":
         case "HS384":
         case "HS512":
         case "ES256":
         case "ES384":
         case "ES512":
         case "RS256":
         case "RS384":
         case "RS512":
         case "PS256":
         case "PS384":
         case "PS512":
            return;
         default:
            throw new InvalidAlgorithmParameterException("invalid algorithm: " + algo);
      }
   }

   static String createHeader(String algorithm, Optional<String> typeHeader, Optional<String> kid) throws InvalidAlgorithmParameterException {
      validateAlgorithm(algorithm);
      JsonObject header = new JsonObject();
      if (kid.isPresent()) {
         header.addProperty("kid", kid.get());
      }

      header.addProperty("alg", algorithm);
      if (typeHeader.isPresent()) {
         header.addProperty("typ", typeHeader.get());
      }

      return Base64.urlSafeEncode(header.toString().getBytes(Util.UTF_8));
   }

   private static void validateKidInHeader(String expectedKid, JsonObject parsedHeader) throws JwtInvalidException {
      String kid = getStringHeader(parsedHeader, "kid");
      if (!kid.equals(expectedKid)) {
         throw new JwtInvalidException("invalid kid in header");
      }
   }

   static void validateHeader(JsonObject parsedHeader, String algorithmFromKey, Optional<String> kidFromKey, boolean allowKidAbsent) throws GeneralSecurityException {
      String receivedAlgorithm = getStringHeader(parsedHeader, "alg");
      if (!receivedAlgorithm.equals(algorithmFromKey)) {
         throw new InvalidAlgorithmParameterException(String.format("invalid algorithm; expected %s, got %s", algorithmFromKey, receivedAlgorithm));
      } else if (parsedHeader.has("crit")) {
         throw new JwtInvalidException("all tokens with crit headers are rejected");
      } else {
         boolean headerHasKid = parsedHeader.has("kid");
         if (headerHasKid || !allowKidAbsent) {
            if (!headerHasKid && !allowKidAbsent) {
               throw new JwtInvalidException("missing kid in header");
            } else if (kidFromKey.isPresent()) {
               String kid = getStringHeader(parsedHeader, "kid");
               if (!kid.equals(kidFromKey.get())) {
                  throw new JwtInvalidException("invalid kid in header");
               }
            }
         }
      }
   }

   static void validateHeader(String expectedAlgorithm, Optional<String> tinkKid, Optional<String> customKid, JsonObject parsedHeader) throws InvalidAlgorithmParameterException, JwtInvalidException {
      validateAlgorithm(expectedAlgorithm);
      String algorithm = getStringHeader(parsedHeader, "alg");
      if (!algorithm.equals(expectedAlgorithm)) {
         throw new InvalidAlgorithmParameterException(String.format("invalid algorithm; expected %s, got %s", expectedAlgorithm, algorithm));
      } else if (parsedHeader.has("crit")) {
         throw new JwtInvalidException("all tokens with crit headers are rejected");
      } else if (tinkKid.isPresent() && customKid.isPresent()) {
         throw new JwtInvalidException("custom_kid can only be set for RAW keys.");
      } else {
         boolean headerHasKid = parsedHeader.has("kid");
         if (tinkKid.isPresent()) {
            if (!headerHasKid) {
               throw new JwtInvalidException("missing kid in header");
            }

            validateKidInHeader(tinkKid.get(), parsedHeader);
         }

         if (customKid.isPresent() && headerHasKid) {
            validateKidInHeader(customKid.get(), parsedHeader);
         }
      }
   }

   static Optional<String> getTypeHeader(JsonObject header) throws JwtInvalidException {
      return header.has("typ") ? Optional.of(getStringHeader(header, "typ")) : Optional.empty();
   }

   static String getStringHeader(JsonObject header, String name) throws JwtInvalidException {
      if (!header.has(name)) {
         throw new JwtInvalidException("header " + name + " does not exist");
      } else if (header.get(name).isJsonPrimitive() && header.get(name).getAsJsonPrimitive().isString()) {
         return header.get(name).getAsString();
      } else {
         throw new JwtInvalidException("header " + name + " is not a string");
      }
   }

   static String decodeHeader(String headerStr) throws JwtInvalidException {
      byte[] data = strictUrlSafeDecode(headerStr);
      validateUtf8(data);
      return new String(data, Util.UTF_8);
   }

   static String encodePayload(String jsonPayload) {
      return Base64.urlSafeEncode(jsonPayload.getBytes(Util.UTF_8));
   }

   static String decodePayload(String payloadStr) throws JwtInvalidException {
      byte[] data = strictUrlSafeDecode(payloadStr);
      validateUtf8(data);
      return new String(data, Util.UTF_8);
   }

   static String encodeSignature(byte[] signature) {
      return Base64.urlSafeEncode(signature);
   }

   static byte[] decodeSignature(String signatureStr) throws JwtInvalidException {
      return strictUrlSafeDecode(signatureStr);
   }

   static Optional<String> getKid(int keyId, OutputPrefixType prefix) throws JwtInvalidException {
      if (prefix == OutputPrefixType.RAW) {
         return Optional.empty();
      } else if (prefix == OutputPrefixType.TINK) {
         byte[] bigEndianKeyId = ByteBuffer.allocate(4).putInt(keyId).array();
         return Optional.of(Base64.urlSafeEncode(bigEndianKeyId));
      } else {
         throw new JwtInvalidException("unsupported output prefix type");
      }
   }

   static Optional<Integer> getKeyId(String kid) {
      byte[] encodedKeyId = Base64.urlSafeDecode(kid);
      return encodedKeyId.length != 4 ? Optional.empty() : Optional.of(ByteBuffer.wrap(encodedKeyId).getInt());
   }

   static JwtFormat.Parts splitSignedCompact(String signedCompact) throws JwtInvalidException {
      validateASCII(signedCompact);
      int sigPos = signedCompact.lastIndexOf(46);
      if (sigPos < 0) {
         throw new JwtInvalidException("only tokens in JWS compact serialization format are supported");
      } else {
         String unsignedCompact = signedCompact.substring(0, sigPos);
         String encodedMac = signedCompact.substring(sigPos + 1);
         byte[] mac = decodeSignature(encodedMac);
         int payloadPos = unsignedCompact.indexOf(46);
         if (payloadPos < 0) {
            throw new JwtInvalidException("only tokens in JWS compact serialization format are supported");
         } else {
            String encodedHeader = unsignedCompact.substring(0, payloadPos);
            String encodedPayload = unsignedCompact.substring(payloadPos + 1);
            if (encodedPayload.indexOf(46) > 0) {
               throw new JwtInvalidException("only tokens in JWS compact serialization format are supported");
            } else {
               String header = decodeHeader(encodedHeader);
               String payload = decodePayload(encodedPayload);
               return new JwtFormat.Parts(unsignedCompact, mac, header, payload);
            }
         }
      }
   }

   static String createUnsignedCompact(String algorithm, Optional<String> kid, RawJwt rawJwt) throws InvalidAlgorithmParameterException, JwtInvalidException {
      String jsonPayload = rawJwt.getJsonPayload();
      Optional<String> typeHeader = rawJwt.hasTypeHeader() ? Optional.of(rawJwt.getTypeHeader()) : Optional.empty();
      return createHeader(algorithm, typeHeader, kid) + "." + encodePayload(jsonPayload);
   }

   static String createSignedCompact(String unsignedCompact, byte[] signature) {
      return unsignedCompact + "." + encodeSignature(signature);
   }

   static void validateASCII(String data) throws JwtInvalidException {
      for (int i = 0; i < data.length(); i++) {
         char c = data.charAt(i);
         if ((c & 128) > 0) {
            throw new JwtInvalidException("Non ascii character");
         }
      }
   }

   static class Parts {
      String unsignedCompact;
      byte[] signatureOrMac;
      String header;
      String payload;

      Parts(String unsignedCompact, byte[] signatureOrMac, String header, String payload) {
         this.unsignedCompact = unsignedCompact;
         this.signatureOrMac = signatureOrMac;
         this.header = header;
         this.payload = payload;
      }
   }
}
