package com.nimbusds.jwt;

import com.nimbusds.jose.Payload;
import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.JSONArrayUtils;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.util.DateUtils;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;

@Immutable
public final class JWTClaimsSet implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final Set<String> REGISTERED_CLAIM_NAMES;
   private final Map<String, Object> claims = new LinkedHashMap<>();
   private final boolean serializeNullClaims;

   private JWTClaimsSet(Map<String, Object> claims, boolean serializeNullClaims) {
      this.claims.putAll(claims);
      this.serializeNullClaims = serializeNullClaims;
   }

   public static Set<String> getRegisteredNames() {
      return REGISTERED_CLAIM_NAMES;
   }

   public String getIssuer() {
      try {
         return this.getStringClaim("iss");
      } catch (ParseException var2) {
         return null;
      }
   }

   public String getSubject() {
      try {
         return this.getStringClaim("sub");
      } catch (ParseException var2) {
         return null;
      }
   }

   public List<String> getAudience() {
      Object audValue = this.getClaim("aud");
      if (audValue instanceof String) {
         return Collections.singletonList((String)audValue);
      } else {
         List<String> aud;
         try {
            aud = this.getStringListClaim("aud");
         } catch (ParseException var4) {
            return Collections.emptyList();
         }

         return aud != null ? aud : Collections.emptyList();
      }
   }

   public Date getExpirationTime() {
      try {
         return this.getDateClaim("exp");
      } catch (ParseException var2) {
         return null;
      }
   }

   public Date getNotBeforeTime() {
      try {
         return this.getDateClaim("nbf");
      } catch (ParseException var2) {
         return null;
      }
   }

   public Date getIssueTime() {
      try {
         return this.getDateClaim("iat");
      } catch (ParseException var2) {
         return null;
      }
   }

   public String getJWTID() {
      try {
         return this.getStringClaim("jti");
      } catch (ParseException var2) {
         return null;
      }
   }

   public Object getClaim(String name) {
      return this.claims.get(name);
   }

   public String getStringClaim(String name) throws ParseException {
      Object value = this.getClaim(name);
      if (value != null && !(value instanceof String)) {
         throw new ParseException("The " + name + " claim is not a String", 0);
      } else {
         return (String)value;
      }
   }

   public String getClaimAsString(String name) throws ParseException {
      Object value = this.getClaim(name);
      if (value != null && !(value instanceof String)) {
         Class<?> clazz;
         if (!(clazz = value.getClass()).isPrimitive() && !isWrapper(clazz)) {
            throw new ParseException("The " + name + " claim is not and cannot be converted to a String", 0);
         } else {
            return String.valueOf(value);
         }
      } else {
         return (String)value;
      }
   }

   private static boolean isWrapper(Class<?> clazz) {
      return clazz == Integer.class
         || clazz == Double.class
         || clazz == Float.class
         || clazz == Long.class
         || clazz == Short.class
         || clazz == Byte.class
         || clazz == Character.class
         || clazz == Boolean.class;
   }

   public List<Object> getListClaim(String name) throws ParseException {
      Object value = this.getClaim(name);
      if (value == null) {
         return null;
      } else {
         try {
            return (List<Object>)this.getClaim(name);
         } catch (ClassCastException var4) {
            throw new ParseException("The " + name + " claim is not a list / JSON array", 0);
         }
      }
   }

   public String[] getStringArrayClaim(String name) throws ParseException {
      List<?> list = this.getListClaim(name);
      if (list == null) {
         return null;
      } else {
         String[] stringArray = new String[list.size()];

         for (int i = 0; i < stringArray.length; i++) {
            try {
               stringArray[i] = (String)list.get(i);
            } catch (ClassCastException var6) {
               throw new ParseException("The " + name + " claim is not a list / JSON array of strings", 0);
            }
         }

         return stringArray;
      }
   }

   public List<String> getStringListClaim(String name) throws ParseException {
      String[] stringArray = this.getStringArrayClaim(name);
      return stringArray == null ? null : Collections.unmodifiableList(Arrays.asList(stringArray));
   }

   public URI getURIClaim(String name) throws ParseException {
      String uriString = this.getStringClaim(name);
      if (uriString == null) {
         return null;
      } else {
         try {
            return new URI(uriString);
         } catch (URISyntaxException var4) {
            throw new ParseException("The \"" + name + "\" claim is not a URI: " + var4.getMessage(), 0);
         }
      }
   }

   public Boolean getBooleanClaim(String name) throws ParseException {
      Object value = this.getClaim(name);
      if (value != null && !(value instanceof Boolean)) {
         throw new ParseException("The \"" + name + "\" claim is not a Boolean", 0);
      } else {
         return (Boolean)value;
      }
   }

   public Integer getIntegerClaim(String name) throws ParseException {
      Object value = this.getClaim(name);
      if (value == null) {
         return null;
      } else if (value instanceof Number) {
         return ((Number)value).intValue();
      } else {
         throw new ParseException("The \"" + name + "\" claim is not an Integer", 0);
      }
   }

   public Long getLongClaim(String name) throws ParseException {
      Object value = this.getClaim(name);
      if (value == null) {
         return null;
      } else if (value instanceof Number) {
         return ((Number)value).longValue();
      } else {
         throw new ParseException("The \"" + name + "\" claim is not a Number", 0);
      }
   }

   public Date getDateClaim(String name) throws ParseException {
      Object value = this.getClaim(name);
      if (value == null) {
         return null;
      } else if (value instanceof Date) {
         return (Date)value;
      } else if (value instanceof Number) {
         return DateUtils.fromSecondsSinceEpoch(((Number)value).longValue());
      } else {
         throw new ParseException("The \"" + name + "\" claim is not a Date", 0);
      }
   }

   public Float getFloatClaim(String name) throws ParseException {
      Object value = this.getClaim(name);
      if (value == null) {
         return null;
      } else if (value instanceof Number) {
         return ((Number)value).floatValue();
      } else {
         throw new ParseException("The \"" + name + "\" claim is not a Float", 0);
      }
   }

   public Double getDoubleClaim(String name) throws ParseException {
      Object value = this.getClaim(name);
      if (value == null) {
         return null;
      } else if (value instanceof Number) {
         return ((Number)value).doubleValue();
      } else {
         throw new ParseException("The \"" + name + "\" claim is not a Double", 0);
      }
   }

   public Map<String, Object> getJSONObjectClaim(String name) throws ParseException {
      Object value = this.getClaim(name);
      if (value == null) {
         return null;
      } else if (value instanceof Map) {
         Map<String, Object> jsonObject = JSONObjectUtils.newJSONObject();
         Map<?, ?> map = (Map<?, ?>)value;

         for (Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() instanceof String) {
               jsonObject.put((String)entry.getKey(), entry.getValue());
            }
         }

         return jsonObject;
      } else {
         throw new ParseException("The \"" + name + "\" claim is not a JSON object or Map", 0);
      }
   }

   public Map<String, Object> getClaims() {
      return Collections.unmodifiableMap(this.claims);
   }

   public Payload toPayload() {
      return new Payload(this.toJSONObject(this.serializeNullClaims));
   }

   public Payload toPayload(boolean serializeNullClaims) {
      return new Payload(this.toJSONObject(serializeNullClaims));
   }

   public Map<String, Object> toJSONObject() {
      return this.toJSONObject(this.serializeNullClaims);
   }

   public Map<String, Object> toJSONObject(boolean serializeNullClaims) {
      Map<String, Object> o = JSONObjectUtils.newJSONObject();

      for (Entry<String, Object> claim : this.claims.entrySet()) {
         if (claim.getValue() instanceof Date) {
            Date dateValue = (Date)claim.getValue();
            o.put(claim.getKey(), DateUtils.toSecondsSinceEpoch(dateValue));
         } else if ("aud".equals(claim.getKey())) {
            List<String> audList = this.getAudience();
            if (audList != null && !audList.isEmpty()) {
               if (audList.size() == 1) {
                  o.put("aud", audList.get(0));
               } else {
                  List<Object> audArray = JSONArrayUtils.newJSONArray();
                  audArray.addAll(audList);
                  o.put("aud", audArray);
               }
            } else if (serializeNullClaims) {
               o.put("aud", null);
            }
         } else if (claim.getValue() != null) {
            o.put(claim.getKey(), claim.getValue());
         } else if (serializeNullClaims) {
            o.put(claim.getKey(), null);
         }
      }

      return o;
   }

   @Override
   public String toString() {
      return JSONObjectUtils.toJSONString(this.toJSONObject());
   }

   public String toString(boolean serializeNullClaims) {
      return JSONObjectUtils.toJSONString(this.toJSONObject(serializeNullClaims));
   }

   public <T> T toType(JWTClaimsSetTransformer<T> transformer) {
      return transformer.transform(this);
   }

   public static JWTClaimsSet parse(Map<String, Object> json) throws ParseException {
      JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

      for (String name : json.keySet()) {
         switch (name) {
            case "iss":
               builder.issuer(JSONObjectUtils.getString(json, "iss"));
               break;
            case "sub":
               Object subValue = json.get("sub");
               if (subValue instanceof String) {
                  builder.subject(JSONObjectUtils.getString(json, "sub"));
               } else if (subValue instanceof Number) {
                  builder.subject(String.valueOf(subValue));
               } else {
                  if (subValue != null) {
                     throw new ParseException("Illegal sub claim", 0);
                  }

                  builder.subject(null);
               }
               break;
            case "aud":
               Object audValue = json.get("aud");
               if (audValue instanceof String) {
                  List<String> singleAud = new ArrayList<>();
                  singleAud.add(JSONObjectUtils.getString(json, "aud"));
                  builder.audience(singleAud);
               } else if (audValue instanceof List) {
                  builder.audience(JSONObjectUtils.getStringList(json, "aud"));
               } else {
                  if (audValue != null) {
                     throw new ParseException("Illegal aud claim", 0);
                  }

                  builder.audience((String)null);
               }
               break;
            case "exp":
               builder.expirationTime(JSONObjectUtils.getEpochSecondAsDate(json, "exp"));
               break;
            case "nbf":
               builder.notBeforeTime(JSONObjectUtils.getEpochSecondAsDate(json, "nbf"));
               break;
            case "iat":
               builder.issueTime(JSONObjectUtils.getEpochSecondAsDate(json, "iat"));
               break;
            case "jti":
               builder.jwtID(JSONObjectUtils.getString(json, "jti"));
               break;
            default:
               builder.claim(name, json.get(name));
         }
      }

      return builder.build();
   }

   public static JWTClaimsSet parse(String s) throws ParseException {
      return parse(JSONObjectUtils.parse(s));
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof JWTClaimsSet)) {
         return false;
      } else {
         JWTClaimsSet that = (JWTClaimsSet)o;
         return Objects.equals(this.claims, that.claims);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.claims);
   }

   static {
      Set<String> n = new HashSet<>();
      n.add("iss");
      n.add("sub");
      n.add("aud");
      n.add("exp");
      n.add("nbf");
      n.add("iat");
      n.add("jti");
      REGISTERED_CLAIM_NAMES = Collections.unmodifiableSet(n);
   }

   public static class Builder {
      private final Map<String, Object> claims = new LinkedHashMap<>();
      private boolean serializeNullClaims = false;

      public Builder() {
      }

      public Builder(JWTClaimsSet jwtClaimsSet) {
         this.claims.putAll(jwtClaimsSet.claims);
      }

      public JWTClaimsSet.Builder serializeNullClaims(boolean enable) {
         this.serializeNullClaims = enable;
         return this;
      }

      public JWTClaimsSet.Builder issuer(String iss) {
         this.claims.put("iss", iss);
         return this;
      }

      public JWTClaimsSet.Builder subject(String sub) {
         this.claims.put("sub", sub);
         return this;
      }

      public JWTClaimsSet.Builder audience(List<String> aud) {
         this.claims.put("aud", aud);
         return this;
      }

      public JWTClaimsSet.Builder audience(String aud) {
         if (aud == null) {
            this.claims.put("aud", null);
         } else {
            this.claims.put("aud", Collections.singletonList(aud));
         }

         return this;
      }

      public JWTClaimsSet.Builder expirationTime(Date exp) {
         this.claims.put("exp", exp);
         return this;
      }

      public JWTClaimsSet.Builder notBeforeTime(Date nbf) {
         this.claims.put("nbf", nbf);
         return this;
      }

      public JWTClaimsSet.Builder issueTime(Date iat) {
         this.claims.put("iat", iat);
         return this;
      }

      public JWTClaimsSet.Builder jwtID(String jti) {
         this.claims.put("jti", jti);
         return this;
      }

      public JWTClaimsSet.Builder claim(String name, Object value) {
         this.claims.put(name, value);
         return this;
      }

      public Map<String, Object> getClaims() {
         return Collections.unmodifiableMap(this.claims);
      }

      public JWTClaimsSet build() {
         return new JWTClaimsSet(this.claims, this.serializeNullClaims);
      }
   }
}
