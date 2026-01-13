package com.nimbusds.jwt;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.Header;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.text.ParseException;
import java.util.Map;

public final class JWTParser {
   public static JWT parse(String s) throws ParseException {
      int firstDotPos = s.indexOf(".");
      if (firstDotPos == -1) {
         throw new ParseException("Invalid JWT serialization: Missing dot delimiter(s)", 0);
      } else {
         Base64URL header = new Base64URL(s.substring(0, firstDotPos));

         Map<String, Object> jsonObject;
         try {
            jsonObject = JSONObjectUtils.parse(header.decodeToString());
         } catch (ParseException var5) {
            throw new ParseException("Invalid unsecured/JWS/JWE header: " + var5.getMessage(), 0);
         }

         Algorithm alg = Header.parseAlgorithm(jsonObject);
         if (alg.equals(Algorithm.NONE)) {
            return PlainJWT.parse(s);
         } else if (alg instanceof JWSAlgorithm) {
            return SignedJWT.parse(s);
         } else if (alg instanceof JWEAlgorithm) {
            return EncryptedJWT.parse(s);
         } else {
            throw new AssertionError("Unexpected algorithm type: " + alg);
         }
      }
   }

   private JWTParser() {
   }
}
