package com.nimbusds.jose;

import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONArrayUtils;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@ThreadSafe
public class JWSObjectJSON extends JOSEObjectJSON {
   private static final long serialVersionUID = 1L;
   private final List<JWSObjectJSON.Signature> signatures = new LinkedList<>();

   public JWSObjectJSON(Payload payload) {
      super(payload);
      Objects.requireNonNull(payload, "The payload must not be null");
   }

   private JWSObjectJSON(Payload payload, List<JWSObjectJSON.Signature> signatures) {
      super(Objects.requireNonNull(payload, "The payload must not be null"));
      if (signatures.isEmpty()) {
         throw new IllegalArgumentException("At least one signature required");
      } else {
         this.signatures.addAll(signatures);
      }
   }

   public List<JWSObjectJSON.Signature> getSignatures() {
      return Collections.unmodifiableList(this.signatures);
   }

   public synchronized void sign(JWSHeader jwsHeader, JWSSigner signer) throws JOSEException {
      this.sign(jwsHeader, null, signer);
   }

   public synchronized void sign(JWSHeader jwsHeader, UnprotectedHeader unprotectedHeader, JWSSigner signer) throws JOSEException {
      try {
         HeaderValidation.ensureDisjoint(jwsHeader, unprotectedHeader);
      } catch (IllegalHeaderException var5) {
         throw new IllegalArgumentException(var5.getMessage(), var5);
      }

      JWSObject jwsObject = new JWSObject(jwsHeader, this.getPayload());
      jwsObject.sign(signer);
      this.signatures.add(new JWSObjectJSON.Signature(this.getPayload(), jwsHeader, unprotectedHeader, jwsObject.getSignature()));
   }

   public JWSObjectJSON.State getState() {
      if (this.getSignatures().isEmpty()) {
         return JWSObjectJSON.State.UNSIGNED;
      } else {
         for (JWSObjectJSON.Signature sig : this.getSignatures()) {
            if (!sig.isVerified()) {
               return JWSObjectJSON.State.SIGNED;
            }
         }

         return JWSObjectJSON.State.VERIFIED;
      }
   }

   @Override
   public Map<String, Object> toGeneralJSONObject() {
      if (this.signatures.size() < 1) {
         throw new IllegalStateException("The general JWS JSON serialization requires at least one signature");
      } else {
         Map<String, Object> jsonObject = JSONObjectUtils.newJSONObject();
         jsonObject.put("payload", this.getPayload().toBase64URL().toString());
         List<Object> signaturesJSONArray = JSONArrayUtils.newJSONArray();

         for (JWSObjectJSON.Signature signature : this.getSignatures()) {
            Map<String, Object> signatureJSONObject = signature.toJSONObject();
            signaturesJSONArray.add(signatureJSONObject);
         }

         jsonObject.put("signatures", signaturesJSONArray);
         return jsonObject;
      }
   }

   @Override
   public Map<String, Object> toFlattenedJSONObject() {
      if (this.signatures.size() != 1) {
         throw new IllegalStateException("The flattened JWS JSON serialization requires exactly one signature");
      } else {
         Map<String, Object> jsonObject = JSONObjectUtils.newJSONObject();
         jsonObject.put("payload", this.getPayload().toBase64URL().toString());
         jsonObject.putAll(this.getSignatures().get(0).toJSONObject());
         return jsonObject;
      }
   }

   @Override
   public String serializeGeneral() {
      return JSONObjectUtils.toJSONString(this.toGeneralJSONObject());
   }

   @Override
   public String serializeFlattened() {
      return JSONObjectUtils.toJSONString(this.toFlattenedJSONObject());
   }

   private static JWSHeader parseJWSHeader(Map<String, Object> jsonObject) throws ParseException {
      Base64URL protectedHeader = JSONObjectUtils.getBase64URL(jsonObject, "protected");
      if (protectedHeader == null) {
         throw new ParseException("Missing protected header (required by this library)", 0);
      } else {
         try {
            return JWSHeader.parse(protectedHeader);
         } catch (ParseException var3) {
            if ("Not a JWS header".equals(var3.getMessage())) {
               throw new ParseException("Missing JWS \"alg\" parameter in protected header (required by this library)", 0);
            } else {
               throw var3;
            }
         }
      }
   }

   public static JWSObjectJSON parse(Map<String, Object> jsonObject) throws ParseException {
      Base64URL payloadB64URL = JSONObjectUtils.getBase64URL(jsonObject, "payload");
      if (payloadB64URL == null) {
         throw new ParseException("Missing payload", 0);
      } else {
         Payload payload = new Payload(payloadB64URL);
         Base64URL topLevelSignatureB64 = JSONObjectUtils.getBase64URL(jsonObject, "signature");
         boolean flattened = topLevelSignatureB64 != null;
         List<JWSObjectJSON.Signature> signatureList = new LinkedList<>();
         if (flattened) {
            JWSHeader jwsHeader = parseJWSHeader(jsonObject);
            UnprotectedHeader unprotectedHeader = UnprotectedHeader.parse(JSONObjectUtils.getJSONObject(jsonObject, "header"));
            if (jsonObject.get("signatures") != null) {
               throw new ParseException("The \"signatures\" member must not be present in flattened JWS JSON serialization", 0);
            }

            try {
               HeaderValidation.ensureDisjoint(jwsHeader, unprotectedHeader);
            } catch (IllegalHeaderException var15) {
               throw new ParseException(var15.getMessage(), 0);
            }

            signatureList.add(new JWSObjectJSON.Signature(payload, jwsHeader, unprotectedHeader, topLevelSignatureB64));
         } else {
            Map<String, Object>[] signatures = JSONObjectUtils.getJSONObjectArray(jsonObject, "signatures");
            if (signatures == null || signatures.length == 0) {
               throw new ParseException("The \"signatures\" member must be present in general JSON Serialization", 0);
            }

            for (Map<String, Object> signatureJSONObject : signatures) {
               JWSHeader jwsHeader = parseJWSHeader(signatureJSONObject);
               UnprotectedHeader unprotectedHeader = UnprotectedHeader.parse(JSONObjectUtils.getJSONObject(signatureJSONObject, "header"));

               try {
                  HeaderValidation.ensureDisjoint(jwsHeader, unprotectedHeader);
               } catch (IllegalHeaderException var14) {
                  throw new ParseException(var14.getMessage(), 0);
               }

               Base64URL signatureB64 = JSONObjectUtils.getBase64URL(signatureJSONObject, "signature");
               if (signatureB64 == null) {
                  throw new ParseException("Missing \"signature\" member", 0);
               }

               signatureList.add(new JWSObjectJSON.Signature(payload, jwsHeader, unprotectedHeader, signatureB64));
            }
         }

         return new JWSObjectJSON(payload, signatureList);
      }
   }

   public static JWSObjectJSON parse(String json) throws ParseException {
      return parse(JSONObjectUtils.parse(json));
   }

   @Immutable
   public static final class Signature {
      private final Payload payload;
      private final JWSHeader header;
      private final UnprotectedHeader unprotectedHeader;
      private final Base64URL signature;
      private final AtomicBoolean verified = new AtomicBoolean(false);

      private Signature(Payload payload, JWSHeader header, UnprotectedHeader unprotectedHeader, Base64URL signature) {
         Objects.requireNonNull(payload);
         this.payload = payload;
         this.header = header;
         this.unprotectedHeader = unprotectedHeader;
         Objects.requireNonNull(signature);
         this.signature = signature;
      }

      public JWSHeader getHeader() {
         return this.header;
      }

      public UnprotectedHeader getUnprotectedHeader() {
         return this.unprotectedHeader;
      }

      public Base64URL getSignature() {
         return this.signature;
      }

      private Map<String, Object> toJSONObject() {
         Map<String, Object> jsonObject = JSONObjectUtils.newJSONObject();
         if (this.header != null) {
            jsonObject.put("protected", this.header.toBase64URL().toString());
         }

         if (this.unprotectedHeader != null && !this.unprotectedHeader.getIncludedParams().isEmpty()) {
            jsonObject.put("header", this.unprotectedHeader.toJSONObject());
         }

         jsonObject.put("signature", this.signature.toString());
         return jsonObject;
      }

      public JWSObject toJWSObject() {
         try {
            return new JWSObject(this.header.toBase64URL(), this.payload.toBase64URL(), this.signature);
         } catch (ParseException var2) {
            throw new IllegalStateException();
         }
      }

      public boolean isVerified() {
         return this.verified.get();
      }

      public synchronized boolean verify(JWSVerifier verifier) throws JOSEException {
         try {
            this.verified.set(this.toJWSObject().verify(verifier));
         } catch (JOSEException var3) {
            throw var3;
         } catch (Exception var4) {
            throw new JOSEException(var4.getMessage(), var4);
         }

         return this.verified.get();
      }
   }

   public static enum State {
      UNSIGNED,
      SIGNED,
      VERIFIED;
   }
}
