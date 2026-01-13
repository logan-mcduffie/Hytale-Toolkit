package com.nimbusds.jose;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class CommonSEHeader extends Header {
   private static final long serialVersionUID = 1L;
   private final URI jku;
   private final JWK jwk;
   private final URI x5u;
   private final Base64URL x5t;
   private final Base64URL x5t256;
   private final List<Base64> x5c;
   private final String kid;

   protected CommonSEHeader(
      Algorithm alg,
      JOSEObjectType typ,
      String cty,
      Set<String> crit,
      URI jku,
      JWK jwk,
      URI x5u,
      Base64URL x5t,
      Base64URL x5t256,
      List<Base64> x5c,
      String kid,
      Map<String, Object> customParams,
      Base64URL parsedBase64URL
   ) {
      super(alg, typ, cty, crit, customParams, parsedBase64URL);
      this.jku = jku;
      this.jwk = jwk;
      this.x5u = x5u;
      this.x5t = x5t;
      this.x5t256 = x5t256;
      if (x5c != null) {
         this.x5c = Collections.unmodifiableList(new ArrayList<>(x5c));
      } else {
         this.x5c = null;
      }

      this.kid = kid;
   }

   public URI getJWKURL() {
      return this.jku;
   }

   public JWK getJWK() {
      return this.jwk;
   }

   public URI getX509CertURL() {
      return this.x5u;
   }

   @Deprecated
   public Base64URL getX509CertThumbprint() {
      return this.x5t;
   }

   public Base64URL getX509CertSHA256Thumbprint() {
      return this.x5t256;
   }

   public List<Base64> getX509CertChain() {
      return this.x5c;
   }

   public String getKeyID() {
      return this.kid;
   }

   @Override
   public Set<String> getIncludedParams() {
      Set<String> includedParameters = super.getIncludedParams();
      if (this.jku != null) {
         includedParameters.add("jku");
      }

      if (this.jwk != null) {
         includedParameters.add("jwk");
      }

      if (this.x5u != null) {
         includedParameters.add("x5u");
      }

      if (this.x5t != null) {
         includedParameters.add("x5t");
      }

      if (this.x5t256 != null) {
         includedParameters.add("x5t#S256");
      }

      if (this.x5c != null && !this.x5c.isEmpty()) {
         includedParameters.add("x5c");
      }

      if (this.kid != null) {
         includedParameters.add("kid");
      }

      return includedParameters;
   }

   @Override
   public Map<String, Object> toJSONObject() {
      Map<String, Object> o = super.toJSONObject();
      if (this.jku != null) {
         o.put("jku", this.jku.toString());
      }

      if (this.jwk != null) {
         o.put("jwk", this.jwk.toJSONObject());
      }

      if (this.x5u != null) {
         o.put("x5u", this.x5u.toString());
      }

      if (this.x5t != null) {
         o.put("x5t", this.x5t.toString());
      }

      if (this.x5t256 != null) {
         o.put("x5t#S256", this.x5t256.toString());
      }

      if (this.x5c != null && !this.x5c.isEmpty()) {
         List<String> x5cJson = new ArrayList<>(this.x5c.size());

         for (Base64 item : this.x5c) {
            x5cJson.add(item.toString());
         }

         o.put("x5c", x5cJson);
      }

      if (this.kid != null) {
         o.put("kid", this.kid);
      }

      return o;
   }

   static JWK parsePublicJWK(Map<String, Object> jwkObject) throws ParseException {
      if (jwkObject == null) {
         return null;
      } else {
         JWK jwk = JWK.parse(jwkObject);
         if (jwk.isPrivate()) {
            throw new ParseException("Non-public key in jwk header parameter", 0);
         } else {
            return jwk;
         }
      }
   }
}
