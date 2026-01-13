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
import java.util.Objects;
import java.util.Set;

@Immutable
public final class JWEHeader extends CommonSEHeader {
   private static final long serialVersionUID = 1L;
   private static final Set<String> REGISTERED_PARAMETER_NAMES;
   private final EncryptionMethod enc;
   private final JWK epk;
   private final CompressionAlgorithm zip;
   private final Base64URL apu;
   private final Base64URL apv;
   private final Base64URL p2s;
   private final int p2c;
   private final Base64URL iv;
   private final Base64URL tag;
   private final String skid;
   private final String iss;
   private final String sub;
   private final List<String> aud;

   public JWEHeader(EncryptionMethod enc) {
      this(
         null, enc, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, null, null, null, null, null, null, null, null
      );
   }

   public JWEHeader(JWEAlgorithm alg, EncryptionMethod enc) {
      this(
         alg, enc, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, null, null, null, null, null, null, null, null
      );
   }

   @Deprecated
   public JWEHeader(
      Algorithm alg,
      EncryptionMethod enc,
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
      JWK epk,
      CompressionAlgorithm zip,
      Base64URL apu,
      Base64URL apv,
      Base64URL p2s,
      int p2c,
      Base64URL iv,
      Base64URL tag,
      String skid,
      Map<String, Object> customParams,
      Base64URL parsedBase64URL
   ) {
      this(
         alg,
         enc,
         typ,
         cty,
         crit,
         jku,
         jwk,
         x5u,
         x5t,
         x5t256,
         x5c,
         kid,
         epk,
         zip,
         apu,
         apv,
         p2s,
         p2c,
         iv,
         tag,
         skid,
         null,
         null,
         null,
         customParams,
         parsedBase64URL
      );
   }

   public JWEHeader(
      Algorithm alg,
      EncryptionMethod enc,
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
      JWK epk,
      CompressionAlgorithm zip,
      Base64URL apu,
      Base64URL apv,
      Base64URL p2s,
      int p2c,
      Base64URL iv,
      Base64URL tag,
      String skid,
      String iss,
      String sub,
      List<String> aud,
      Map<String, Object> customParams,
      Base64URL parsedBase64URL
   ) {
      super(alg, typ, cty, crit, jku, jwk, x5u, x5t, x5t256, x5c, kid, customParams, parsedBase64URL);
      if (alg != null && alg.getName().equals(Algorithm.NONE.getName())) {
         throw new IllegalArgumentException("The JWE algorithm cannot be \"none\"");
      } else if (epk != null && epk.isPrivate()) {
         throw new IllegalArgumentException("Ephemeral public key should not be a private key");
      } else {
         this.enc = Objects.requireNonNull(enc);
         this.epk = epk;
         this.zip = zip;
         this.apu = apu;
         this.apv = apv;
         this.p2s = p2s;
         this.p2c = p2c;
         this.iv = iv;
         this.tag = tag;
         this.skid = skid;
         this.iss = iss;
         this.sub = sub;
         this.aud = aud;
      }
   }

   public JWEHeader(JWEHeader jweHeader) {
      this(
         jweHeader.getAlgorithm(),
         jweHeader.getEncryptionMethod(),
         jweHeader.getType(),
         jweHeader.getContentType(),
         jweHeader.getCriticalParams(),
         jweHeader.getJWKURL(),
         jweHeader.getJWK(),
         jweHeader.getX509CertURL(),
         jweHeader.getX509CertThumbprint(),
         jweHeader.getX509CertSHA256Thumbprint(),
         jweHeader.getX509CertChain(),
         jweHeader.getKeyID(),
         jweHeader.getEphemeralPublicKey(),
         jweHeader.getCompressionAlgorithm(),
         jweHeader.getAgreementPartyUInfo(),
         jweHeader.getAgreementPartyVInfo(),
         jweHeader.getPBES2Salt(),
         jweHeader.getPBES2Count(),
         jweHeader.getIV(),
         jweHeader.getAuthTag(),
         jweHeader.getSenderKeyID(),
         jweHeader.getIssuer(),
         jweHeader.getSubject(),
         jweHeader.getAudience(),
         jweHeader.getCustomParams(),
         jweHeader.getParsedBase64URL()
      );
   }

   public static Set<String> getRegisteredParameterNames() {
      return REGISTERED_PARAMETER_NAMES;
   }

   public JWEAlgorithm getAlgorithm() {
      return (JWEAlgorithm)super.getAlgorithm();
   }

   public EncryptionMethod getEncryptionMethod() {
      return this.enc;
   }

   public JWK getEphemeralPublicKey() {
      return this.epk;
   }

   public CompressionAlgorithm getCompressionAlgorithm() {
      return this.zip;
   }

   public Base64URL getAgreementPartyUInfo() {
      return this.apu;
   }

   public Base64URL getAgreementPartyVInfo() {
      return this.apv;
   }

   public Base64URL getPBES2Salt() {
      return this.p2s;
   }

   public int getPBES2Count() {
      return this.p2c;
   }

   public Base64URL getIV() {
      return this.iv;
   }

   public Base64URL getAuthTag() {
      return this.tag;
   }

   public String getSenderKeyID() {
      return this.skid;
   }

   public String getIssuer() {
      return this.iss;
   }

   public String getSubject() {
      return this.sub;
   }

   public List<String> getAudience() {
      return this.aud == null ? Collections.emptyList() : this.aud;
   }

   @Override
   public Set<String> getIncludedParams() {
      Set<String> includedParameters = super.getIncludedParams();
      if (this.enc != null) {
         includedParameters.add("enc");
      }

      if (this.epk != null) {
         includedParameters.add("epk");
      }

      if (this.zip != null) {
         includedParameters.add("zip");
      }

      if (this.apu != null) {
         includedParameters.add("apu");
      }

      if (this.apv != null) {
         includedParameters.add("apv");
      }

      if (this.p2s != null) {
         includedParameters.add("p2s");
      }

      if (this.p2c > 0) {
         includedParameters.add("p2c");
      }

      if (this.iv != null) {
         includedParameters.add("iv");
      }

      if (this.tag != null) {
         includedParameters.add("tag");
      }

      if (this.skid != null) {
         includedParameters.add("skid");
      }

      if (this.iss != null) {
         includedParameters.add("iss");
      }

      if (this.sub != null) {
         includedParameters.add("sub");
      }

      if (this.aud != null) {
         includedParameters.add("aud");
      }

      return includedParameters;
   }

   @Override
   public Map<String, Object> toJSONObject() {
      Map<String, Object> o = super.toJSONObject();
      if (this.enc != null) {
         o.put("enc", this.enc.toString());
      }

      if (this.epk != null) {
         o.put("epk", this.epk.toJSONObject());
      }

      if (this.zip != null) {
         o.put("zip", this.zip.toString());
      }

      if (this.apu != null) {
         o.put("apu", this.apu.toString());
      }

      if (this.apv != null) {
         o.put("apv", this.apv.toString());
      }

      if (this.p2s != null) {
         o.put("p2s", this.p2s.toString());
      }

      if (this.p2c > 0) {
         o.put("p2c", this.p2c);
      }

      if (this.iv != null) {
         o.put("iv", this.iv.toString());
      }

      if (this.tag != null) {
         o.put("tag", this.tag.toString());
      }

      if (this.skid != null) {
         o.put("skid", this.skid);
      }

      if (this.iss != null) {
         o.put("iss", this.iss);
      }

      if (this.sub != null) {
         o.put("sub", this.sub);
      }

      if (this.aud != null) {
         if (this.aud.size() == 1) {
            o.put("aud", this.aud.get(0));
         } else if (!this.aud.isEmpty()) {
            o.put("aud", this.aud);
         }
      }

      return o;
   }

   private static EncryptionMethod parseEncryptionMethod(Map<String, Object> json) throws ParseException {
      return EncryptionMethod.parse(JSONObjectUtils.getString(json, "enc"));
   }

   public static JWEHeader parse(Map<String, Object> jsonObject) throws ParseException {
      return parse(jsonObject, null);
   }

   public static JWEHeader parse(Map<String, Object> jsonObject, Base64URL parsedBase64URL) throws ParseException {
      EncryptionMethod enc = parseEncryptionMethod(jsonObject);
      JWEHeader.Builder header = new JWEHeader.Builder(enc).parsedBase64URL(parsedBase64URL);

      for (String name : jsonObject.keySet()) {
         if ("alg".equals(name)) {
            header = header.alg(JWEAlgorithm.parse(JSONObjectUtils.getString(jsonObject, name)));
         } else if (!"enc".equals(name)) {
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
            } else if ("epk".equals(name)) {
               header = header.ephemeralPublicKey(JWK.parse(JSONObjectUtils.getJSONObject(jsonObject, name)));
            } else if ("zip".equals(name)) {
               String zipValue = JSONObjectUtils.getString(jsonObject, name);
               if (zipValue != null) {
                  header = header.compressionAlgorithm(new CompressionAlgorithm(zipValue));
               }
            } else if ("apu".equals(name)) {
               header = header.agreementPartyUInfo(Base64URL.from(JSONObjectUtils.getString(jsonObject, name)));
            } else if ("apv".equals(name)) {
               header = header.agreementPartyVInfo(Base64URL.from(JSONObjectUtils.getString(jsonObject, name)));
            } else if ("p2s".equals(name)) {
               header = header.pbes2Salt(Base64URL.from(JSONObjectUtils.getString(jsonObject, name)));
            } else if ("p2c".equals(name)) {
               header = header.pbes2Count(JSONObjectUtils.getInt(jsonObject, name));
            } else if ("iv".equals(name)) {
               header = header.iv(Base64URL.from(JSONObjectUtils.getString(jsonObject, name)));
            } else if ("tag".equals(name)) {
               header = header.authTag(Base64URL.from(JSONObjectUtils.getString(jsonObject, name)));
            } else if ("skid".equals(name)) {
               header = header.senderKeyID(JSONObjectUtils.getString(jsonObject, name));
            } else if ("iss".equals(name)) {
               header = header.issuer(JSONObjectUtils.getString(jsonObject, name));
            } else if ("sub".equals(name)) {
               header = header.subject(JSONObjectUtils.getString(jsonObject, name));
            } else if ("aud".equals(name)) {
               if (jsonObject.get(name) instanceof String) {
                  header = header.audience(Collections.singletonList(JSONObjectUtils.getString(jsonObject, name)));
               } else {
                  header = header.audience(JSONObjectUtils.getStringList(jsonObject, name));
               }
            } else {
               header = header.customParam(name, jsonObject.get(name));
            }
         }
      }

      return header.build();
   }

   public static JWEHeader parse(String jsonString) throws ParseException {
      return parse(JSONObjectUtils.parse(jsonString), null);
   }

   public static JWEHeader parse(String jsonString, Base64URL parsedBase64URL) throws ParseException {
      return parse(JSONObjectUtils.parse(jsonString, 20000), parsedBase64URL);
   }

   public static JWEHeader parse(Base64URL base64URL) throws ParseException {
      return parse(base64URL.decodeToString(), base64URL);
   }

   static {
      Set<String> p = new HashSet<>();
      p.add("alg");
      p.add("enc");
      p.add("epk");
      p.add("zip");
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
      p.add("apu");
      p.add("apv");
      p.add("p2s");
      p.add("p2c");
      p.add("iv");
      p.add("tag");
      p.add("skid");
      p.add("iss");
      p.add("sub");
      p.add("aud");
      p.add("authTag");
      REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
   }

   public static class Builder {
      private final EncryptionMethod enc;
      private JWEAlgorithm alg;
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
      private JWK epk;
      private CompressionAlgorithm zip;
      private Base64URL apu;
      private Base64URL apv;
      private Base64URL p2s;
      private int p2c;
      private Base64URL iv;
      private Base64URL tag;
      private String skid;
      private String iss;
      private String sub;
      private List<String> aud;
      private Map<String, Object> customParams;
      private Base64URL parsedBase64URL;

      public Builder(JWEAlgorithm alg, EncryptionMethod enc) {
         if (alg.getName().equals(Algorithm.NONE.getName())) {
            throw new IllegalArgumentException("The JWE algorithm \"alg\" cannot be \"none\"");
         } else {
            this.alg = alg;
            this.enc = Objects.requireNonNull(enc);
         }
      }

      public Builder(EncryptionMethod enc) {
         this.enc = Objects.requireNonNull(enc);
      }

      public Builder(JWEHeader jweHeader) {
         this(jweHeader.getEncryptionMethod());
         this.alg = jweHeader.getAlgorithm();
         this.typ = jweHeader.getType();
         this.cty = jweHeader.getContentType();
         this.crit = jweHeader.getCriticalParams();
         this.customParams = jweHeader.getCustomParams();
         this.jku = jweHeader.getJWKURL();
         this.jwk = jweHeader.getJWK();
         this.x5u = jweHeader.getX509CertURL();
         this.x5t = jweHeader.getX509CertThumbprint();
         this.x5t256 = jweHeader.getX509CertSHA256Thumbprint();
         this.x5c = jweHeader.getX509CertChain();
         this.kid = jweHeader.getKeyID();
         this.epk = jweHeader.getEphemeralPublicKey();
         this.zip = jweHeader.getCompressionAlgorithm();
         this.apu = jweHeader.getAgreementPartyUInfo();
         this.apv = jweHeader.getAgreementPartyVInfo();
         this.p2s = jweHeader.getPBES2Salt();
         this.p2c = jweHeader.getPBES2Count();
         this.iv = jweHeader.getIV();
         this.tag = jweHeader.getAuthTag();
         this.skid = jweHeader.getSenderKeyID();
         this.iss = jweHeader.getIssuer();
         this.sub = jweHeader.getSubject();
         this.aud = jweHeader.getAudience();
         this.customParams = jweHeader.getCustomParams();
      }

      public JWEHeader.Builder alg(JWEAlgorithm alg) {
         this.alg = alg;
         return this;
      }

      public JWEHeader.Builder type(JOSEObjectType typ) {
         this.typ = typ;
         return this;
      }

      public JWEHeader.Builder contentType(String cty) {
         this.cty = cty;
         return this;
      }

      public JWEHeader.Builder criticalParams(Set<String> crit) {
         this.crit = crit;
         return this;
      }

      public JWEHeader.Builder jwkURL(URI jku) {
         this.jku = jku;
         return this;
      }

      public JWEHeader.Builder jwk(JWK jwk) {
         if (jwk != null && jwk.isPrivate()) {
            throw new IllegalArgumentException("The JWK must be public");
         } else {
            this.jwk = jwk;
            return this;
         }
      }

      public JWEHeader.Builder x509CertURL(URI x5u) {
         this.x5u = x5u;
         return this;
      }

      @Deprecated
      public JWEHeader.Builder x509CertThumbprint(Base64URL x5t) {
         this.x5t = x5t;
         return this;
      }

      public JWEHeader.Builder x509CertSHA256Thumbprint(Base64URL x5t256) {
         this.x5t256 = x5t256;
         return this;
      }

      public JWEHeader.Builder x509CertChain(List<Base64> x5c) {
         this.x5c = x5c;
         return this;
      }

      public JWEHeader.Builder keyID(String kid) {
         this.kid = kid;
         return this;
      }

      public JWEHeader.Builder ephemeralPublicKey(JWK epk) {
         this.epk = epk;
         return this;
      }

      public JWEHeader.Builder compressionAlgorithm(CompressionAlgorithm zip) {
         this.zip = zip;
         return this;
      }

      public JWEHeader.Builder agreementPartyUInfo(Base64URL apu) {
         this.apu = apu;
         return this;
      }

      public JWEHeader.Builder agreementPartyVInfo(Base64URL apv) {
         this.apv = apv;
         return this;
      }

      public JWEHeader.Builder pbes2Salt(Base64URL p2s) {
         this.p2s = p2s;
         return this;
      }

      public JWEHeader.Builder pbes2Count(int p2c) {
         if (p2c < 0) {
            throw new IllegalArgumentException("The PBES2 count parameter must not be negative");
         } else {
            this.p2c = p2c;
            return this;
         }
      }

      public JWEHeader.Builder iv(Base64URL iv) {
         this.iv = iv;
         return this;
      }

      public JWEHeader.Builder senderKeyID(String skid) {
         this.skid = skid;
         return this;
      }

      public JWEHeader.Builder issuer(String iss) {
         this.iss = iss;
         return this;
      }

      public JWEHeader.Builder subject(String sub) {
         this.sub = sub;
         return this;
      }

      public JWEHeader.Builder audience(List<String> aud) {
         this.aud = aud;
         return this;
      }

      public JWEHeader.Builder authTag(Base64URL tag) {
         this.tag = tag;
         return this;
      }

      public JWEHeader.Builder customParam(String name, Object value) {
         if (JWEHeader.getRegisteredParameterNames().contains(name)) {
            throw new IllegalArgumentException("The parameter name \"" + name + "\" matches a registered name");
         } else {
            if (this.customParams == null) {
               this.customParams = new HashMap<>();
            }

            this.customParams.put(name, value);
            return this;
         }
      }

      public JWEHeader.Builder customParams(Map<String, Object> customParameters) {
         this.customParams = customParameters;
         return this;
      }

      public JWEHeader.Builder parsedBase64URL(Base64URL base64URL) {
         this.parsedBase64URL = base64URL;
         return this;
      }

      public JWEHeader build() {
         return new JWEHeader(
            this.alg,
            this.enc,
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
            this.epk,
            this.zip,
            this.apu,
            this.apv,
            this.p2s,
            this.p2c,
            this.iv,
            this.tag,
            this.skid,
            this.iss,
            this.sub,
            this.aud,
            this.customParams,
            this.parsedBase64URL
         );
      }
   }
}
