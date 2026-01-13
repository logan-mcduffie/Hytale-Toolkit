package com.google.crypto.tink.jwt;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.jwt.internal.JsonUtil;
import com.google.crypto.tink.signature.RsaSsaPssPublicKey;
import com.google.crypto.tink.subtle.RsaSsaPssVerifyJce;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

final class JwtRsaSsaPssVerifyKeyManager {
   static final PrimitiveConstructor<JwtRsaSsaPssPublicKey, JwtPublicKeyVerify> PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      JwtRsaSsaPssVerifyKeyManager::createFullPrimitive, JwtRsaSsaPssPublicKey.class, JwtPublicKeyVerify.class
   );

   @AccessesPartialKey
   static RsaSsaPssPublicKey toRsaSsaPssPublicKey(JwtRsaSsaPssPublicKey publicKey) {
      return publicKey.getRsaSsaPssPublicKey();
   }

   static JwtPublicKeyVerify createFullPrimitive(JwtRsaSsaPssPublicKey publicKey) throws GeneralSecurityException {
      RsaSsaPssPublicKey rsaSsaPssPublicKey = toRsaSsaPssPublicKey(publicKey);
      final PublicKeyVerify verifier = RsaSsaPssVerifyJce.create(rsaSsaPssPublicKey);
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
      return "type.googleapis.com/google.crypto.tink.JwtRsaSsaPssPublicKey";
   }

   private JwtRsaSsaPssVerifyKeyManager() {
   }
}
