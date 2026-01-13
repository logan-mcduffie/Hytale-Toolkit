package com.nimbusds.jose;

import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONArrayUtils;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ThreadSafe
public class JWEObjectJSON extends JOSEObjectJSON {
   private static final long serialVersionUID = 1L;
   private final JWEHeader header;
   private UnprotectedHeader unprotectedHeader;
   private final List<JWEObjectJSON.Recipient> recipients = new LinkedList<>();
   private Base64URL iv;
   private Base64URL cipherText;
   private Base64URL authTag;
   private final byte[] aad;
   private JWEObject.State state;

   public JWEObjectJSON(JWEObject jweObject) {
      super(jweObject.getPayload());
      this.header = jweObject.getHeader();
      this.aad = null;
      this.iv = jweObject.getIV();
      this.cipherText = jweObject.getCipherText();
      this.authTag = jweObject.getAuthTag();
      if (jweObject.getState() == JWEObject.State.ENCRYPTED) {
         this.recipients.add(new JWEObjectJSON.Recipient(null, jweObject.getEncryptedKey()));
         this.state = JWEObject.State.ENCRYPTED;
      } else if (jweObject.getState() == JWEObject.State.DECRYPTED) {
         this.recipients.add(new JWEObjectJSON.Recipient(null, jweObject.getEncryptedKey()));
         this.state = JWEObject.State.DECRYPTED;
      } else {
         this.state = JWEObject.State.UNENCRYPTED;
      }
   }

   public JWEObjectJSON(JWEHeader header, Payload payload) {
      this(header, payload, null, null);
   }

   public JWEObjectJSON(JWEHeader header, Payload payload, UnprotectedHeader unprotectedHeader, byte[] aad) {
      super(payload);
      this.header = Objects.requireNonNull(header);
      this.setPayload(Objects.requireNonNull(payload));
      this.unprotectedHeader = unprotectedHeader;
      this.aad = aad;
      this.cipherText = null;
      this.state = JWEObject.State.UNENCRYPTED;
   }

   public JWEObjectJSON(
      JWEHeader header,
      Base64URL cipherText,
      Base64URL iv,
      Base64URL authTag,
      List<JWEObjectJSON.Recipient> recipients,
      UnprotectedHeader unprotectedHeader,
      byte[] aad
   ) {
      super(null);
      this.header = Objects.requireNonNull(header);
      this.recipients.addAll(recipients);
      this.unprotectedHeader = unprotectedHeader;
      this.aad = aad;
      this.iv = iv;
      this.cipherText = Objects.requireNonNull(cipherText);
      this.authTag = authTag;
      this.state = JWEObject.State.ENCRYPTED;
   }

   public JWEHeader getHeader() {
      return this.header;
   }

   public UnprotectedHeader getUnprotectedHeader() {
      return this.unprotectedHeader;
   }

   public Base64URL getEncryptedKey() {
      if (this.recipients.isEmpty()) {
         return null;
      } else if (this.recipients.size() == 1) {
         return this.recipients.get(0).getEncryptedKey();
      } else {
         List<Object> recipientsList = JSONArrayUtils.newJSONArray();

         for (JWEObjectJSON.Recipient recipient : this.recipients) {
            recipientsList.add(recipient.toJSONObject());
         }

         Map<String, Object> recipientsMap = JSONObjectUtils.newJSONObject();
         recipientsMap.put("recipients", recipientsList);
         return Base64URL.encode(JSONObjectUtils.toJSONString(recipientsMap));
      }
   }

   public Base64URL getIV() {
      return this.iv;
   }

   public Base64URL getCipherText() {
      return this.cipherText;
   }

   public Base64URL getAuthTag() {
      return this.authTag;
   }

   public byte[] getAAD() {
      StringBuilder aadSB = new StringBuilder(this.header.toBase64URL().toString());
      if (this.aad != null && this.aad.length > 0) {
         aadSB.append(".").append(new String(this.aad, StandardCharsets.US_ASCII));
      }

      return aadSB.toString().getBytes(StandardCharsets.US_ASCII);
   }

   public List<JWEObjectJSON.Recipient> getRecipients() {
      return Collections.unmodifiableList(this.recipients);
   }

   public JWEObject.State getState() {
      return this.state;
   }

   private void ensureUnencryptedState() {
      if (this.state != JWEObject.State.UNENCRYPTED) {
         throw new IllegalStateException("The JWE object must be in an unencrypted state");
      }
   }

   private void ensureEncryptedState() {
      if (this.state != JWEObject.State.ENCRYPTED) {
         throw new IllegalStateException("The JWE object must be in an encrypted state");
      }
   }

   private void ensureEncryptedOrDecryptedState() {
      if (this.state != JWEObject.State.ENCRYPTED && this.state != JWEObject.State.DECRYPTED) {
         throw new IllegalStateException("The JWE object must be in an encrypted or decrypted state");
      }
   }

   private void ensureJWEEncrypterSupport(JWEEncrypter encrypter) throws JOSEException {
      if (!encrypter.supportedJWEAlgorithms().contains(this.getHeader().getAlgorithm())) {
         throw new JOSEException(
            "The "
               + this.getHeader().getAlgorithm()
               + " algorithm is not supported by the JWE encrypter: Supported algorithms: "
               + encrypter.supportedJWEAlgorithms()
         );
      } else if (!encrypter.supportedEncryptionMethods().contains(this.getHeader().getEncryptionMethod())) {
         throw new JOSEException(
            "The "
               + this.getHeader().getEncryptionMethod()
               + " encryption method or key size is not supported by the JWE encrypter: Supported methods: "
               + encrypter.supportedEncryptionMethods()
         );
      }
   }

   public synchronized void encrypt(JWEEncrypter encrypter) throws JOSEException {
      this.ensureUnencryptedState();
      this.ensureJWEEncrypterSupport(encrypter);
      JWEHeader jweJoinedHeader = this.getHeader();

      JWECryptoParts parts;
      try {
         jweJoinedHeader = (JWEHeader)this.getHeader().join(this.unprotectedHeader);
         parts = encrypter.encrypt(jweJoinedHeader, this.getPayload().toBytes(), this.getAAD());
      } catch (JOSEException var10) {
         throw var10;
      } catch (Exception var11) {
         throw new JOSEException(var11.getMessage(), var11);
      }

      Base64URL encryptedKey = parts.getEncryptedKey();

      try {
         for (Map<String, Object> recipientMap : JSONObjectUtils.getJSONObjectArray(JSONObjectUtils.parse(encryptedKey.decodeToString()), "recipients")) {
            this.recipients.add(JWEObjectJSON.Recipient.parse(recipientMap));
         }
      } catch (Exception var12) {
         Map<String, Object> recipientHeader = parts.getHeader().toJSONObject();

         for (String param : jweJoinedHeader.getIncludedParams()) {
            if (recipientHeader.containsKey(param)) {
               recipientHeader.remove(param);
            }
         }

         try {
            this.recipients.add(new JWEObjectJSON.Recipient(UnprotectedHeader.parse(recipientHeader), encryptedKey));
         } catch (Exception var9) {
            throw new JOSEException(var9.getMessage(), var9);
         }
      }

      this.iv = parts.getInitializationVector();
      this.cipherText = parts.getCipherText();
      this.authTag = parts.getAuthenticationTag();
      this.state = JWEObject.State.ENCRYPTED;
   }

   public synchronized void decrypt(JWEDecrypter decrypter) throws JOSEException {
      this.ensureEncryptedState();

      try {
         this.setPayload(
            new Payload(decrypter.decrypt(this.getHeader(), this.getEncryptedKey(), this.getIV(), this.getCipherText(), this.getAuthTag(), this.getAAD()))
         );
      } catch (JOSEException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new JOSEException(var4.getMessage(), var4);
      }

      this.state = JWEObject.State.DECRYPTED;
   }

   private Map<String, Object> toBaseJSONObject() {
      Map<String, Object> jsonObject = JSONObjectUtils.newJSONObject();
      jsonObject.put("protected", this.header.toBase64URL().toString());
      if (this.aad != null) {
         jsonObject.put("aad", new String(this.aad, StandardCharsets.US_ASCII));
      }

      jsonObject.put("ciphertext", this.cipherText.toString());
      jsonObject.put("iv", this.iv.toString());
      jsonObject.put("tag", this.authTag.toString());
      return jsonObject;
   }

   @Override
   public Map<String, Object> toGeneralJSONObject() {
      this.ensureEncryptedOrDecryptedState();
      if (!this.recipients.isEmpty() && (this.recipients.get(0).getUnprotectedHeader() != null || this.recipients.get(0).getEncryptedKey() != null)) {
         Map<String, Object> jsonObject = this.toBaseJSONObject();
         if (this.unprotectedHeader != null) {
            jsonObject.put("unprotected", this.unprotectedHeader.toJSONObject());
         }

         List<Object> recipientsJSONArray = JSONArrayUtils.newJSONArray();

         for (JWEObjectJSON.Recipient recipient : this.recipients) {
            Map<String, Object> recipientJSONObject = recipient.toJSONObject();
            recipientsJSONArray.add(recipientJSONObject);
         }

         jsonObject.put("recipients", recipientsJSONArray);
         return jsonObject;
      } else {
         throw new IllegalStateException("The general JWE JSON serialization requires at least one recipient");
      }
   }

   @Override
   public Map<String, Object> toFlattenedJSONObject() {
      this.ensureEncryptedOrDecryptedState();
      if (this.recipients.size() != 1) {
         throw new IllegalStateException("The flattened JWE JSON serialization requires exactly one recipient");
      } else {
         Map<String, Object> jsonObject = this.toBaseJSONObject();
         Map<String, Object> recipientHeader = JSONObjectUtils.newJSONObject();
         if (this.recipients.get(0).getUnprotectedHeader() != null) {
            recipientHeader.putAll(this.recipients.get(0).getUnprotectedHeader().toJSONObject());
         }

         if (this.unprotectedHeader != null) {
            recipientHeader.putAll(this.unprotectedHeader.toJSONObject());
         }

         if (recipientHeader.size() > 0) {
            jsonObject.put("unprotected", recipientHeader);
         }

         if (this.recipients.get(0).getEncryptedKey() != null) {
            jsonObject.put("encrypted_key", this.recipients.get(0).getEncryptedKey().toString());
         }

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

   public static JWEObjectJSON parse(Map<String, Object> jsonObject) throws ParseException {
      if (!jsonObject.containsKey("protected")) {
         throw new ParseException("The JWE protected header mast be present", 0);
      } else {
         List<JWEObjectJSON.Recipient> recipientList = new LinkedList<>();
         JWEHeader jweHeader = JWEHeader.parse(JSONObjectUtils.getBase64URL(jsonObject, "protected"));
         UnprotectedHeader unprotected = UnprotectedHeader.parse(JSONObjectUtils.getJSONObject(jsonObject, "unprotected"));
         Base64URL cipherText = JSONObjectUtils.getBase64URL(jsonObject, "ciphertext");
         Base64URL iv = JSONObjectUtils.getBase64URL(jsonObject, "iv");
         Base64URL authTag = JSONObjectUtils.getBase64URL(jsonObject, "tag");
         Base64URL aad = JSONObjectUtils.getBase64URL(jsonObject, "aad");
         JWEHeader jweJoinedHeader = (JWEHeader)jweHeader.join(unprotected);
         if (jsonObject.containsKey("recipients")) {
            Map<String, Object>[] recipients = JSONObjectUtils.getJSONObjectArray(jsonObject, "recipients");
            if (recipients == null || recipients.length == 0) {
               throw new ParseException("The \"recipients\" member must be present in general JSON Serialization", 0);
            }

            for (Map<String, Object> recipientJSONObject : recipients) {
               JWEObjectJSON.Recipient recipient = JWEObjectJSON.Recipient.parse(recipientJSONObject);

               try {
                  HeaderValidation.ensureDisjoint(jweJoinedHeader, recipient.getUnprotectedHeader());
               } catch (IllegalHeaderException var16) {
                  throw new ParseException(var16.getMessage(), 0);
               }

               recipientList.add(recipient);
            }
         } else {
            Base64URL encryptedKey = JSONObjectUtils.getBase64URL(jsonObject, "encrypted_key");
            recipientList.add(new JWEObjectJSON.Recipient(null, encryptedKey));
         }

         return new JWEObjectJSON(
            jweHeader, cipherText, iv, authTag, recipientList, unprotected, aad == null ? null : aad.toString().getBytes(StandardCharsets.US_ASCII)
         );
      }
   }

   public static JWEObjectJSON parse(String json) throws ParseException {
      return parse(JSONObjectUtils.parse(Objects.requireNonNull(json)));
   }

   @Immutable
   public static final class Recipient {
      private final UnprotectedHeader unprotectedHeader;
      private final Base64URL encryptedKey;

      public Recipient(UnprotectedHeader unprotectedHeader, Base64URL encryptedKey) {
         this.unprotectedHeader = unprotectedHeader;
         this.encryptedKey = encryptedKey;
      }

      public UnprotectedHeader getUnprotectedHeader() {
         return this.unprotectedHeader;
      }

      public Base64URL getEncryptedKey() {
         return this.encryptedKey;
      }

      public Map<String, Object> toJSONObject() {
         Map<String, Object> jsonObject = JSONObjectUtils.newJSONObject();
         if (this.unprotectedHeader != null && !this.unprotectedHeader.getIncludedParams().isEmpty()) {
            jsonObject.put("header", this.unprotectedHeader.toJSONObject());
         }

         if (this.encryptedKey != null) {
            jsonObject.put("encrypted_key", this.encryptedKey.toString());
         }

         return jsonObject;
      }

      public static JWEObjectJSON.Recipient parse(Map<String, Object> jsonObject) throws ParseException {
         UnprotectedHeader header = UnprotectedHeader.parse(JSONObjectUtils.getJSONObject(jsonObject, "header"));
         Base64URL encryptedKey = JSONObjectUtils.getBase64URL(jsonObject, "encrypted_key");
         return new JWEObjectJSON.Recipient(header, encryptedKey);
      }
   }
}
