package com.nimbusds.jose.jwk;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.StandardCharset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;

public final class ThumbprintUtils {
   public static Base64URL compute(JWK jwk) throws JOSEException {
      return compute("SHA-256", jwk);
   }

   public static Base64URL compute(String hashAlg, JWK jwk) throws JOSEException {
      LinkedHashMap<String, ?> orderedParams = jwk.getRequiredParams();
      return compute(hashAlg, orderedParams);
   }

   public static Base64URL compute(String hashAlg, LinkedHashMap<String, ?> params) throws JOSEException {
      String json = JSONObjectUtils.toJSONString(params);

      MessageDigest md;
      try {
         md = MessageDigest.getInstance(hashAlg);
      } catch (NoSuchAlgorithmException var5) {
         throw new JOSEException("Couldn't compute JWK thumbprint: Unsupported hash algorithm: " + var5.getMessage(), var5);
      }

      md.update(json.getBytes(StandardCharset.UTF_8));
      return Base64URL.encode(md.digest());
   }
}
