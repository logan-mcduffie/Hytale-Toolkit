package com.nimbusds.jose;

import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import java.text.ParseException;
import java.util.Objects;

@ThreadSafe
public class JWEObject extends JOSEObject {
   private static final long serialVersionUID = 1L;
   public static final int MAX_COMPRESSED_CIPHER_TEXT_LENGTH = 100000;
   private JWEHeader header;
   private Base64URL encryptedKey;
   private Base64URL iv;
   private Base64URL cipherText;
   private Base64URL authTag;
   private JWEObject.State state;

   public JWEObject(JWEHeader header, Payload payload) {
      this.header = Objects.requireNonNull(header);
      this.setPayload(Objects.requireNonNull(payload));
      this.encryptedKey = null;
      this.cipherText = null;
      this.state = JWEObject.State.UNENCRYPTED;
   }

   public JWEObject(Base64URL firstPart, Base64URL secondPart, Base64URL thirdPart, Base64URL fourthPart, Base64URL fifthPart) throws ParseException {
      try {
         this.header = JWEHeader.parse(Objects.requireNonNull(firstPart));
      } catch (ParseException var7) {
         throw new ParseException("Invalid JWE header: " + var7.getMessage(), 0);
      }

      if (secondPart != null && !secondPart.toString().isEmpty()) {
         this.encryptedKey = secondPart;
      } else {
         this.encryptedKey = null;
      }

      if (thirdPart != null && !thirdPart.toString().isEmpty()) {
         this.iv = thirdPart;
      } else {
         this.iv = null;
      }

      this.cipherText = Objects.requireNonNull(fourthPart);
      if (fifthPart != null && !fifthPart.toString().isEmpty()) {
         this.authTag = fifthPart;
      } else {
         this.authTag = null;
      }

      this.state = JWEObject.State.ENCRYPTED;
      this.setParsedParts(firstPart, secondPart, thirdPart, fourthPart, fifthPart);
   }

   public JWEHeader getHeader() {
      return this.header;
   }

   public Base64URL getEncryptedKey() {
      return this.encryptedKey;
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

      JWECryptoParts parts;
      try {
         parts = encrypter.encrypt(this.getHeader(), this.getPayload().toBytes(), AAD.compute(this.getHeader()));
      } catch (JOSEException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new JOSEException(var5.getMessage(), var5);
      }

      if (parts.getHeader() != null) {
         this.header = parts.getHeader();
      }

      this.encryptedKey = parts.getEncryptedKey();
      this.iv = parts.getInitializationVector();
      this.cipherText = parts.getCipherText();
      this.authTag = parts.getAuthenticationTag();
      this.state = JWEObject.State.ENCRYPTED;
   }

   public synchronized void decrypt(JWEDecrypter decrypter) throws JOSEException {
      this.ensureEncryptedState();
      if (this.getHeader().getCompressionAlgorithm() != null && this.getCipherText().toString().length() > 100000) {
         throw new JOSEException("The JWE compressed cipher text exceeds the maximum allowed length of 100000 characters");
      } else {
         try {
            this.setPayload(
               new Payload(
                  decrypter.decrypt(
                     this.getHeader(), this.getEncryptedKey(), this.getIV(), this.getCipherText(), this.getAuthTag(), AAD.compute(this.getHeader())
                  )
               )
            );
         } catch (JOSEException var3) {
            throw var3;
         } catch (Exception var4) {
            throw new JOSEException(var4.getMessage(), var4);
         }

         this.state = JWEObject.State.DECRYPTED;
      }
   }

   @Override
   public String serialize() {
      this.ensureEncryptedOrDecryptedState();
      StringBuilder sb = new StringBuilder(this.header.toBase64URL().toString());
      sb.append('.');
      if (this.encryptedKey != null) {
         sb.append(this.encryptedKey);
      }

      sb.append('.');
      if (this.iv != null) {
         sb.append(this.iv);
      }

      sb.append('.');
      sb.append(this.cipherText);
      sb.append('.');
      if (this.authTag != null) {
         sb.append(this.authTag);
      }

      return sb.toString();
   }

   public static JWEObject parse(String s) throws ParseException {
      Base64URL[] parts = JOSEObject.split(s);
      if (parts.length != 5) {
         throw new ParseException("Unexpected number of Base64URL parts, must be five", 0);
      } else {
         return new JWEObject(parts[0], parts[1], parts[2], parts[3], parts[4]);
      }
   }

   public static enum State {
      UNENCRYPTED,
      ENCRYPTED,
      DECRYPTED;
   }
}
