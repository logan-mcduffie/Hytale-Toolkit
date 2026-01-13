package com.google.crypto.tink.jwt;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.jwt.internal.JsonUtil;
import com.google.crypto.tink.signature.RsaSsaPkcs1PublicKey;
import com.google.crypto.tink.subtle.RsaSsaPkcs1VerifyJce;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

final class JwtRsaSsaPkcs1VerifyKeyManager {
   static final PrimitiveConstructor<JwtRsaSsaPkcs1PublicKey, JwtPublicKeyVerify> PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      JwtRsaSsaPkcs1VerifyKeyManager::createFullPrimitive, JwtRsaSsaPkcs1PublicKey.class, JwtPublicKeyVerify.class
   );

   @AccessesPartialKey
   static RsaSsaPkcs1PublicKey toRsaSsaPkcs1PublicKey(JwtRsaSsaPkcs1PublicKey publicKey) {
      return publicKey.getRsaSsaPkcs1PublicKey();
   }

   static JwtPublicKeyVerify createFullPrimitive(JwtRsaSsaPkcs1PublicKey publicKey) throws GeneralSecurityException {
      RsaSsaPkcs1PublicKey rsaSsaPkcs1PublicKey = toRsaSsaPkcs1PublicKey(publicKey);
      final PublicKeyVerify verifier = RsaSsaPkcs1VerifyJce.create(rsaSsaPkcs1PublicKey);
      return new JwtPublicKeyVerify() {
         @Override
         public VerifiedJwt verifyAndDecode(String compact, JwtValidator validator) throws GeneralSecurityException {
            JwtFormat.Parts parts = JwtFormat.splitSignedCompact(compact);
            verifier.verify(parts.signatureOrMac, parts.unsignedCompact.getBytes(StandardCharsets.US_ASCII));
            JsonObject parsedHeader = JsonUtil.parseJson(parts.header);
            JwtFormat.validateHeader(
               parsedHeader, publicKey.getParameters().getAlgorithm().getStandardName(), publicKey.getKid(), publicKey.getParameters().allowKidAbsent()
            );
            RawJwt token = RawJwt.fromJsonPayload(JwtFormat.getTypeHeader(parsedHeader), parts.payload);
            return validator.validate(token);
         }
      };
   }

   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.JwtRsaSsaPkcs1PublicKey";
   }

   private JwtRsaSsaPkcs1VerifyKeyManager() {
   }
}
