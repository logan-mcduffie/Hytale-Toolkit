package com.nimbusds.jose.jwk;

import com.nimbusds.jose.JOSEException;
import java.security.Key;
import java.security.KeyPair;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class KeyConverter {
   public static List<Key> toJavaKeys(List<JWK> jwkList) {
      if (jwkList == null) {
         return Collections.emptyList();
      } else {
         List<Key> out = new LinkedList<>();

         for (JWK jwk : jwkList) {
            try {
               if (jwk instanceof AsymmetricJWK) {
                  KeyPair keyPair = ((AsymmetricJWK)jwk).toKeyPair();
                  out.add(keyPair.getPublic());
                  if (keyPair.getPrivate() != null) {
                     out.add(keyPair.getPrivate());
                  }
               } else if (jwk instanceof SecretJWK) {
                  out.add(((SecretJWK)jwk).toSecretKey());
               }
            } catch (JOSEException var5) {
            }
         }

         return out;
      }
   }
}
