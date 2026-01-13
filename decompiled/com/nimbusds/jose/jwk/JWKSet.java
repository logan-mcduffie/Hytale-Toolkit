package com.nimbusds.jose.jwk;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.IOUtils;
import com.nimbusds.jose.util.JSONArrayUtils;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.StandardCharset;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

@Immutable
public class JWKSet implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final String MIME_TYPE = "application/jwk-set+json; charset=UTF-8";
   private final List<JWK> keys;
   private final Map<String, Object> customMembers;

   public JWKSet() {
      this(Collections.emptyList());
   }

   public JWKSet(JWK key) {
      this(Collections.singletonList(Objects.requireNonNull(key, "The JWK must not be null")));
   }

   public JWKSet(List<JWK> keys) {
      this(keys, Collections.emptyMap());
   }

   public JWKSet(List<JWK> keys, Map<String, Object> customMembers) {
      this.keys = Collections.unmodifiableList(Objects.requireNonNull(keys, "The JWK list must not be null"));
      this.customMembers = Collections.unmodifiableMap(customMembers);
   }

   public List<JWK> getKeys() {
      return this.keys;
   }

   public boolean isEmpty() {
      return this.keys.isEmpty();
   }

   public int size() {
      return this.keys.size();
   }

   public JWK getKeyByKeyId(String kid) {
      for (JWK key : this.getKeys()) {
         if (key.getKeyID() != null && key.getKeyID().equals(kid)) {
            return key;
         }
      }

      return null;
   }

   public boolean containsJWK(JWK jwk) throws JOSEException {
      Base64URL thumbprint = jwk.computeThumbprint();

      for (JWK k : this.getKeys()) {
         if (thumbprint.equals(k.computeThumbprint())) {
            return true;
         }
      }

      return false;
   }

   public Map<String, Object> getAdditionalMembers() {
      return this.customMembers;
   }

   public JWKSet toPublicJWKSet() {
      List<JWK> publicKeyList = new LinkedList<>();

      for (JWK key : this.keys) {
         JWK publicKey = key.toPublicJWK();
         if (publicKey != null) {
            publicKeyList.add(publicKey);
         }
      }

      return new JWKSet(publicKeyList, this.customMembers);
   }

   public JWKSet filter(JWKMatcher jwkMatcher) {
      List<JWK> matches = new LinkedList<>();

      for (JWK key : this.keys) {
         if (jwkMatcher.matches(key)) {
            matches.add(key);
         }
      }

      return new JWKSet(matches, this.customMembers);
   }

   public boolean containsNonPublicKeys() {
      for (JWK jwk : this.getKeys()) {
         if (jwk.isPrivate()) {
            return true;
         }
      }

      return false;
   }

   public Map<String, Object> toJSONObject() {
      return this.toJSONObject(true);
   }

   public Map<String, Object> toJSONObject(boolean publicKeysOnly) {
      Map<String, Object> o = JSONObjectUtils.newJSONObject();
      o.putAll(this.customMembers);
      List<Object> a = JSONArrayUtils.newJSONArray();

      for (JWK key : this.keys) {
         if (publicKeysOnly) {
            JWK publicKey = key.toPublicJWK();
            if (publicKey != null) {
               a.add(publicKey.toJSONObject());
            }
         } else {
            a.add(key.toJSONObject());
         }
      }

      o.put("keys", a);
      return o;
   }

   public String toString(boolean publicKeysOnly) {
      return JSONObjectUtils.toJSONString(this.toJSONObject(publicKeysOnly));
   }

   @Override
   public String toString() {
      return this.toString(true);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof JWKSet)) {
         return false;
      } else {
         JWKSet jwkSet = (JWKSet)o;
         return this.getKeys().equals(jwkSet.getKeys()) && this.customMembers.equals(jwkSet.customMembers);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.getKeys(), this.customMembers);
   }

   public static JWKSet parse(String s) throws ParseException {
      return parse(JSONObjectUtils.parse(s));
   }

   public static JWKSet parse(Map<String, Object> json) throws ParseException {
      List<Object> keyArray = JSONObjectUtils.getJSONArray(json, "keys");
      if (keyArray == null) {
         throw new ParseException("Missing required \"keys\" member", 0);
      } else {
         List<JWK> keys = new LinkedList<>();

         for (int i = 0; i < keyArray.size(); i++) {
            try {
               Map<String, Object> keyJSONObject = (Map<String, Object>)keyArray.get(i);
               keys.add(JWK.parse(keyJSONObject));
            } catch (ClassCastException var6) {
               throw new ParseException("The \"keys\" JSON array must contain JSON objects only", 0);
            } catch (ParseException var7) {
               if (var7.getMessage() == null || !var7.getMessage().startsWith("Unsupported key type")) {
                  throw new ParseException("Invalid JWK at position " + i + ": " + var7.getMessage(), 0);
               }
            }
         }

         Map<String, Object> additionalMembers = new HashMap<>();

         for (Entry<String, Object> entry : json.entrySet()) {
            if (entry.getKey() != null && !entry.getKey().equals("keys")) {
               additionalMembers.put(entry.getKey(), entry.getValue());
            }
         }

         return new JWKSet(keys, additionalMembers);
      }
   }

   public static JWKSet load(InputStream inputStream) throws IOException, ParseException {
      return parse(IOUtils.readInputStreamToString(inputStream, StandardCharset.UTF_8));
   }

   public static JWKSet load(File file) throws IOException, ParseException {
      return parse(IOUtils.readFileToString(file, StandardCharset.UTF_8));
   }

   public static JWKSet load(URL url, int connectTimeout, int readTimeout, int sizeLimit) throws IOException, ParseException {
      return load(url, connectTimeout, readTimeout, sizeLimit, null);
   }

   public static JWKSet load(URL url, int connectTimeout, int readTimeout, int sizeLimit, Proxy proxy) throws IOException, ParseException {
      DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever(connectTimeout, readTimeout, sizeLimit);
      resourceRetriever.setProxy(proxy);
      Resource resource = resourceRetriever.retrieveResource(url);
      return parse(resource.getContent());
   }

   public static JWKSet load(URL url) throws IOException, ParseException {
      return load(url, 0, 0, 0);
   }

   public static JWKSet load(KeyStore keyStore, PasswordLookup pwLookup) throws KeyStoreException {
      List<JWK> jwks = new LinkedList<>();
      Enumeration<String> keyAliases = keyStore.aliases();

      while (keyAliases.hasMoreElements()) {
         String keyAlias = keyAliases.nextElement();
         char[] keyPassword = pwLookup == null ? "".toCharArray() : pwLookup.lookupPassword(keyAlias);
         Certificate cert = keyStore.getCertificate(keyAlias);
         if (cert != null) {
            if (cert.getPublicKey() instanceof RSAPublicKey) {
               RSAKey rsaJWK;
               try {
                  rsaJWK = RSAKey.load(keyStore, keyAlias, keyPassword);
               } catch (JOSEException var10) {
                  continue;
               }

               if (rsaJWK != null) {
                  jwks.add(rsaJWK);
               }
            } else if (cert.getPublicKey() instanceof ECPublicKey) {
               ECKey ecJWK;
               try {
                  ecJWK = ECKey.load(keyStore, keyAlias, keyPassword);
               } catch (JOSEException var11) {
                  continue;
               }

               if (ecJWK != null) {
                  jwks.add(ecJWK);
               }
            }
         }
      }

      keyAliases = keyStore.aliases();

      while (keyAliases.hasMoreElements()) {
         String keyAlias = keyAliases.nextElement();
         char[] keyPassword = pwLookup == null ? "".toCharArray() : pwLookup.lookupPassword(keyAlias);

         OctetSequenceKey octJWK;
         try {
            octJWK = OctetSequenceKey.load(keyStore, keyAlias, keyPassword);
         } catch (JOSEException var9) {
            continue;
         }

         if (octJWK != null) {
            jwks.add(octJWK);
         }
      }

      return new JWKSet(jwks);
   }
}
