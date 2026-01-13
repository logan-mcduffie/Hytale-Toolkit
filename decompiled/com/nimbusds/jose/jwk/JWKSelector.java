package com.nimbusds.jose.jwk;

import com.nimbusds.jose.shaded.jcip.Immutable;
import java.util.LinkedList;
import java.util.List;

@Immutable
public final class JWKSelector {
   private final JWKMatcher matcher;

   public JWKSelector(JWKMatcher matcher) {
      if (matcher == null) {
         throw new IllegalArgumentException("The JWK matcher must not be null");
      } else {
         this.matcher = matcher;
      }
   }

   public JWKMatcher getMatcher() {
      return this.matcher;
   }

   public List<JWK> select(JWKSet jwkSet) {
      List<JWK> selectedKeys = new LinkedList<>();
      if (jwkSet == null) {
         return selectedKeys;
      } else {
         for (JWK key : jwkSet.getKeys()) {
            if (this.matcher.matches(key)) {
               selectedKeys.add(key);
            }
         }

         return selectedKeys;
      }
   }
}
