package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.X509CertChainUtils;
import com.nimbusds.jwt.util.DateUtils;
import java.net.URI;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class JWKMetadata {
   static KeyType parseKeyType(Map<String, Object> o) throws ParseException {
      try {
         return KeyType.parse(JSONObjectUtils.getString(o, "kty"));
      } catch (IllegalArgumentException var2) {
         throw new ParseException(var2.getMessage(), 0);
      }
   }

   static KeyUse parseKeyUse(Map<String, Object> o) throws ParseException {
      return KeyUse.parse(JSONObjectUtils.getString(o, "use"));
   }

   static Set<KeyOperation> parseKeyOperations(Map<String, Object> o) throws ParseException {
      return KeyOperation.parse(JSONObjectUtils.getStringList(o, "key_ops"));
   }

   static Algorithm parseAlgorithm(Map<String, Object> o) throws ParseException {
      return Algorithm.parse(JSONObjectUtils.getString(o, "alg"));
   }

   static String parseKeyID(Map<String, Object> o) throws ParseException {
      return JSONObjectUtils.getString(o, "kid");
   }

   static URI parseX509CertURL(Map<String, Object> o) throws ParseException {
      return JSONObjectUtils.getURI(o, "x5u");
   }

   static Base64URL parseX509CertThumbprint(Map<String, Object> o) throws ParseException {
      return JSONObjectUtils.getBase64URL(o, "x5t");
   }

   static Base64URL parseX509CertSHA256Thumbprint(Map<String, Object> o) throws ParseException {
      return JSONObjectUtils.getBase64URL(o, "x5t#S256");
   }

   static List<Base64> parseX509CertChain(Map<String, Object> o) throws ParseException {
      List<Base64> chain = X509CertChainUtils.toBase64List(JSONObjectUtils.getJSONArray(o, "x5c"));
      return chain != null && chain.isEmpty() ? null : chain;
   }

   static Date parseExpirationTime(Map<String, Object> o) throws ParseException {
      return o.get("exp") == null ? null : DateUtils.fromSecondsSinceEpoch(JSONObjectUtils.getLong(o, "exp"));
   }

   static Date parseNotBeforeTime(Map<String, Object> o) throws ParseException {
      return o.get("nbf") == null ? null : DateUtils.fromSecondsSinceEpoch(JSONObjectUtils.getLong(o, "nbf"));
   }

   static Date parseIssueTime(Map<String, Object> o) throws ParseException {
      return o.get("iat") == null ? null : DateUtils.fromSecondsSinceEpoch(JSONObjectUtils.getLong(o, "iat"));
   }

   static KeyRevocation parseKeyRevocation(Map<String, Object> o) throws ParseException {
      return o.get("revoked") == null ? null : KeyRevocation.parse(JSONObjectUtils.getJSONObject(o, "revoked"));
   }
}
