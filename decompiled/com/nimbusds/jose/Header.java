package com.nimbusds.jose;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Header implements Serializable {
   public static final int MAX_HEADER_STRING_LENGTH = 20000;
   private static final long serialVersionUID = 1L;
   private final Algorithm alg;
   private final JOSEObjectType typ;
   private final String cty;
   private final Set<String> crit;
   private final Map<String, Object> customParams;
   private static final Map<String, Object> EMPTY_CUSTOM_PARAMS = Collections.unmodifiableMap(new HashMap<>());
   private final Base64URL parsedBase64URL;

   protected Header(Algorithm alg, JOSEObjectType typ, String cty, Set<String> crit, Map<String, Object> customParams, Base64URL parsedBase64URL) {
      this.alg = alg;
      this.typ = typ;
      this.cty = cty;
      if (crit != null) {
         this.crit = Collections.unmodifiableSet(new HashSet<>(crit));
      } else {
         this.crit = null;
      }

      if (customParams != null) {
         this.customParams = Collections.unmodifiableMap(new HashMap<>(customParams));
      } else {
         this.customParams = EMPTY_CUSTOM_PARAMS;
      }

      this.parsedBase64URL = parsedBase64URL;
   }

   protected Header(Header header) {
      this(header.getAlgorithm(), header.getType(), header.getContentType(), header.getCriticalParams(), header.getCustomParams(), header.getParsedBase64URL());
   }

   public Algorithm getAlgorithm() {
      return this.alg;
   }

   public JOSEObjectType getType() {
      return this.typ;
   }

   public String getContentType() {
      return this.cty;
   }

   public Set<String> getCriticalParams() {
      return this.crit;
   }

   public Object getCustomParam(String name) {
      return this.customParams.get(name);
   }

   public Map<String, Object> getCustomParams() {
      return this.customParams;
   }

   public Base64URL getParsedBase64URL() {
      return this.parsedBase64URL;
   }

   public Set<String> getIncludedParams() {
      Set<String> includedParameters = new HashSet<>(this.getCustomParams().keySet());
      if (this.getAlgorithm() != null) {
         includedParameters.add("alg");
      }

      if (this.getType() != null) {
         includedParameters.add("typ");
      }

      if (this.getContentType() != null) {
         includedParameters.add("cty");
      }

      if (this.getCriticalParams() != null && !this.getCriticalParams().isEmpty()) {
         includedParameters.add("crit");
      }

      return includedParameters;
   }

   public Map<String, Object> toJSONObject() {
      Map<String, Object> o = JSONObjectUtils.newJSONObject();
      o.putAll(this.customParams);
      if (this.alg != null) {
         o.put("alg", this.alg.toString());
      }

      if (this.typ != null) {
         o.put("typ", this.typ.toString());
      }

      if (this.cty != null) {
         o.put("cty", this.cty);
      }

      if (this.crit != null && !this.crit.isEmpty()) {
         o.put("crit", new ArrayList<>(this.crit));
      }

      return o;
   }

   @Override
   public String toString() {
      return JSONObjectUtils.toJSONString(this.toJSONObject());
   }

   public Base64URL toBase64URL() {
      return this.parsedBase64URL == null ? Base64URL.encode(this.toString()) : this.parsedBase64URL;
   }

   public static Algorithm parseAlgorithm(Map<String, Object> json) throws ParseException {
      String algName = JSONObjectUtils.getString(json, "alg");
      if (algName == null) {
         throw new ParseException("Missing \"alg\" in header JSON object", 0);
      } else if (algName.equals(Algorithm.NONE.getName())) {
         return Algorithm.NONE;
      } else {
         return (Algorithm)(json.containsKey("enc") ? JWEAlgorithm.parse(algName) : JWSAlgorithm.parse(algName));
      }
   }

   public Header join(UnprotectedHeader unprotected) throws ParseException {
      Map<String, Object> jsonObject = this.toJSONObject();

      try {
         HeaderValidation.ensureDisjoint(this, unprotected);
      } catch (IllegalHeaderException var4) {
         throw new ParseException(var4.getMessage(), 0);
      }

      if (unprotected != null) {
         jsonObject.putAll(unprotected.toJSONObject());
      }

      return parse(jsonObject, null);
   }

   public static Header parse(Map<String, Object> jsonObject) throws ParseException {
      return parse(jsonObject, null);
   }

   public static Header parse(Map<String, Object> jsonObject, Base64URL parsedBase64URL) throws ParseException {
      String algName = JSONObjectUtils.getString(jsonObject, "alg");
      if (jsonObject.containsKey("enc")) {
         return JWEHeader.parse(jsonObject, parsedBase64URL);
      } else if (Algorithm.NONE.getName().equals(algName)) {
         return PlainHeader.parse(jsonObject, parsedBase64URL);
      } else if (jsonObject.containsKey("alg")) {
         return JWSHeader.parse(jsonObject, parsedBase64URL);
      } else {
         throw new ParseException("Missing \"alg\" in header JSON object", 0);
      }
   }

   public static Header parse(String jsonString) throws ParseException {
      return parse(jsonString, null);
   }

   public static Header parse(String jsonString, Base64URL parsedBase64URL) throws ParseException {
      Map<String, Object> jsonObject = JSONObjectUtils.parse(jsonString, 20000);
      return parse(jsonObject, parsedBase64URL);
   }

   public static Header parse(Base64URL base64URL) throws ParseException {
      return parse(base64URL.decodeToString(), base64URL);
   }
}
