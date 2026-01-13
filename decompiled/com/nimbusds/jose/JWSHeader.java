package com.nimbusds.jose;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.X509CertChainUtils;
import java.net.URI;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Immutable
public final class JWSHeader extends CommonSEHeader {
   private static final long serialVersionUID = 1L;
   private static final Set<String> REGISTERED_PARAMETER_NAMES;
   private final boolean b64;

   public JWSHeader(JWSAlgorithm alg) {
      this(alg, null, null, null, null, null, null, null, null, null, null, true, null, null);
   }

   @Deprecated
   public JWSHeader(
      JWSAlgorithm alg,
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
      this(alg, typ, cty, crit, jku, jwk, x5u, x5t, x5t256, x5c, kid, true, customParams, parsedBase64URL);
   }

   public JWSHeader(
      JWSAlgorithm alg,
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
      boolean b64,
      Map<String, Object> customParams,
      Base64URL parsedBase64URL
   ) {
      super(alg, typ, cty, crit, jku, jwk, x5u, x5t, x5t256, x5c, kid, customParams, parsedBase64URL);
      if (alg.getName().equals(Algorithm.NONE.getName())) {
         throw new IllegalArgumentException("The JWS algorithm \"alg\" cannot be \"none\"");
      } else {
         this.b64 = b64;
      }
   }

   public JWSHeader(JWSHeader jwsHeader) {
      this(
         jwsHeader.getAlgorithm(),
         jwsHeader.getType(),
         jwsHeader.getContentType(),
         jwsHeader.getCriticalParams(),
         jwsHeader.getJWKURL(),
         jwsHeader.getJWK(),
         jwsHeader.getX509CertURL(),
         jwsHeader.getX509CertThumbprint(),
         jwsHeader.getX509CertSHA256Thumbprint(),
         jwsHeader.getX509CertChain(),
         jwsHeader.getKeyID(),
         jwsHeader.isBase64URLEncodePayload(),
         jwsHeader.getCustomParams(),
         jwsHeader.getParsedBase64URL()
      );
   }

   public static Set<String> getRegisteredParameterNames() {
      return REGISTERED_PARAMETER_NAMES;
   }

   public JWSAlgorithm getAlgorithm() {
      return (JWSAlgorithm)super.getAlgorithm();
   }

   public boolean isBase64URLEncodePayload() {
      return this.b64;
   }

   @Override
   public Set<String> getIncludedParams() {
      Set<String> includedParams = super.getIncludedParams();
      if (!this.isBase64URLEncodePayload()) {
         includedParams.add("b64");
      }

      return includedParams;
   }

   @Override
   public Map<String, Object> toJSONObject() {
      Map<String, Object> o = super.toJSONObject();
      if (!this.isBase64URLEncodePayload()) {
         o.put("b64", false);
      }

      return o;
   }

   public static JWSHeader parse(Map<String, Object> jsonObject) throws ParseException {
      return parse(jsonObject, null);
   }

   public static JWSHeader parse(Map<String, Object> jsonObject, Base64URL parsedBase64URL) throws ParseException {
      Algorithm alg = Header.parseAlgorithm(jsonObject);
      if (!(alg instanceof JWSAlgorithm)) {
         throw new ParseException("Not a JWS header", 0);
      } else {
         JWSHeader.Builder header = new JWSHeader.Builder((JWSAlgorithm)alg).parsedBase64URL(parsedBase64URL);

         for (String name : jsonObject.keySet()) {
            if (!"alg".equals(name)) {
               if ("typ".equals(name)) {
                  String typValue = JSONObjectUtils.getString(jsonObject, name);
                  if (typValue != null) {
                     header = header.type(new JOSEObjectType(typValue));
                  }
               } else if ("cty".equals(name)) {
                  header = header.contentType(JSONObjectUtils.getString(jsonObject, name));
               } else if ("crit".equals(name)) {
                  List<String> critValues = JSONObjectUtils.getStringList(jsonObject, name);
                  if (critValues != null) {
                     header = header.criticalParams(new HashSet<>(critValues));
                  }
               } else if ("jku".equals(name)) {
                  header = header.jwkURL(JSONObjectUtils.getURI(jsonObject, name));
               } else if ("jwk".equals(name)) {
                  header = header.jwk(CommonSEHeader.parsePublicJWK(JSONObjectUtils.getJSONObject(jsonObject, name)));
               } else if ("x5u".equals(name)) {
                  header = header.x509CertURL(JSONObjectUtils.getURI(jsonObject, name));
               } else if ("x5t".equals(name)) {
                  header = header.x509CertThumbprint(Base64URL.from(JSONObjectUtils.getString(jsonObject, name)));
               } else if ("x5t#S256".equals(name)) {
                  header = header.x509CertSHA256Thumbprint(Base64URL.from(JSONObjectUtils.getString(jsonObject, name)));
               } else if ("x5c".equals(name)) {
                  header = header.x509CertChain(X509CertChainUtils.toBase64List(JSONObjectUtils.getJSONArray(jsonObject, name)));
               } else if ("kid".equals(name)) {
                  header = header.keyID(JSONObjectUtils.getString(jsonObject, name));
               } else if ("b64".equals(name)) {
                  header = header.base64URLEncodePayload(JSONObjectUtils.getBoolean(jsonObject, name));
               } else {
                  header = header.customParam(name, jsonObject.get(name));
               }
            }
         }

         return header.build();
      }
   }

   public static JWSHeader parse(String jsonString) throws ParseException {
      return parse(jsonString, null);
   }

   public static JWSHeader parse(String jsonString, Base64URL parsedBase64URL) throws ParseException {
      return parse(JSONObjectUtils.parse(jsonString, 20000), parsedBase64URL);
   }

   public static JWSHeader parse(Base64URL base64URL) throws ParseException {
      return parse(base64URL.decodeToString(), base64URL);
   }

   static {
      Set<String> p = new HashSet<>();
      p.add("alg");
      p.add("jku");
      p.add("jwk");
      p.add("x5u");
      p.add("x5t");
      p.add("x5t#S256");
      p.add("x5c");
      p.add("kid");
      p.add("typ");
      p.add("cty");
      p.add("crit");
      p.add("b64");
      REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
   }

   public static class Builder {
      private final JWSAlgorithm alg;
      private JOSEObjectType typ;
      private String cty;
      private Set<String> crit;
      private URI jku;
      private JWK jwk;
      private URI x5u;
      @Deprecated
      private Base64URL x5t;
      private Base64URL x5t256;
      private List<Base64> x5c;
      private String kid;
      private boolean b64 = true;
      private Map<String, Object> customParams;
      private Base64URL parsedBase64URL;

      public Builder(JWSAlgorithm alg) {
         if (alg.getName().equals(Algorithm.NONE.getName())) {
            throw new IllegalArgumentException("The JWS algorithm \"alg\" cannot be \"none\"");
         } else {
            this.alg = alg;
         }
      }

      public Builder(JWSHeader jwsHeader) {
         this(jwsHeader.getAlgorithm());
         this.typ = jwsHeader.getType();
         this.cty = jwsHeader.getContentType();
         this.crit = jwsHeader.getCriticalParams();
         this.jku = jwsHeader.getJWKURL();
         this.jwk = jwsHeader.getJWK();
         this.x5u = jwsHeader.getX509CertURL();
         this.x5t = jwsHeader.getX509CertThumbprint();
         this.x5t256 = jwsHeader.getX509CertSHA256Thumbprint();
         this.x5c = jwsHeader.getX509CertChain();
         this.kid = jwsHeader.getKeyID();
         this.b64 = jwsHeader.isBase64URLEncodePayload();
         this.customParams = jwsHeader.getCustomParams();
      }

      public JWSHeader.Builder type(JOSEObjectType typ) {
         this.typ = typ;
         return this;
      }

      public JWSHeader.Builder contentType(String cty) {
         this.cty = cty;
         return this;
      }

      public JWSHeader.Builder criticalParams(Set<String> crit) {
         this.crit = crit;
         return this;
      }

      public JWSHeader.Builder jwkURL(URI jku) {
         this.jku = jku;
         return this;
      }

      public JWSHeader.Builder jwk(JWK jwk) {
         if (jwk != null && jwk.isPrivate()) {
            throw new IllegalArgumentException("The JWK must be public");
         } else {
            this.jwk = jwk;
            return this;
         }
      }

      public JWSHeader.Builder x509CertURL(URI x5u) {
         this.x5u = x5u;
         return this;
      }

      @Deprecated
      public JWSHeader.Builder x509CertThumbprint(Base64URL x5t) {
         this.x5t = x5t;
         return this;
      }

      public JWSHeader.Builder x509CertSHA256Thumbprint(Base64URL x5t256) {
         this.x5t256 = x5t256;
         return this;
      }

      public JWSHeader.Builder x509CertChain(List<Base64> x5c) {
         this.x5c = x5c;
         return this;
      }

      public JWSHeader.Builder keyID(String kid) {
         this.kid = kid;
         return this;
      }

      public JWSHeader.Builder base64URLEncodePayload(boolean b64) {
         this.b64 = b64;
         return this;
      }

      public JWSHeader.Builder customParam(String name, Object value) {
         if (JWSHeader.getRegisteredParameterNames().contains(name)) {
            throw new IllegalArgumentException("The parameter name \"" + name + "\" matches a registered name");
         } else {
            if (this.customParams == null) {
               this.customParams = new HashMap<>();
            }

            this.customParams.put(name, value);
            return this;
         }
      }

      public JWSHeader.Builder customParams(Map<String, Object> customParameters) {
         this.customParams = customParameters;
         return this;
      }

      public JWSHeader.Builder parsedBase64URL(Base64URL base64URL) {
         this.parsedBase64URL = base64URL;
         return this;
      }

      public JWSHeader build() {
         return new JWSHeader(
            this.alg,
            this.typ,
            this.cty,
            this.crit,
            this.jku,
            this.jwk,
            this.x5u,
            this.x5t,
            this.x5t256,
            this.x5c,
            this.kid,
            this.b64,
            this.customParams,
            this.parsedBase64URL
         );
      }
   }
}
