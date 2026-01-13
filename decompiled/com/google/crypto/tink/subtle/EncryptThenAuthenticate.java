package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.Mac;
import com.google.crypto.tink.aead.AesCtrHmacAeadKey;
import com.google.crypto.tink.internal.Util;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.spec.SecretKeySpec;

@AccessesPartialKey
public final class EncryptThenAuthenticate implements Aead {
   private final IndCpaCipher cipher;
   private final Mac mac;
   private final int macLength;
   private final byte[] outputPrefix;

   public EncryptThenAuthenticate(final IndCpaCipher cipher, final Mac mac, int macLength) {
      this(cipher, mac, macLength, new byte[0]);
   }

   private EncryptThenAuthenticate(IndCpaCipher cipher, Mac mac, int macLength, byte[] outputPrefix) {
      this.cipher = cipher;
      this.mac = mac;
      this.macLength = macLength;
      this.outputPrefix = outputPrefix;
   }

   public static Aead create(AesCtrHmacAeadKey key) throws GeneralSecurityException {
      return new EncryptThenAuthenticate(
         new AesCtrJceCipher(key.getAesKeyBytes().toByteArray(InsecureSecretKeyAccess.get()), key.getParameters().getIvSizeBytes()),
         new PrfMac(
            new PrfHmacJce(
               "HMAC" + key.getParameters().getHashType(), new SecretKeySpec(key.getHmacKeyBytes().toByteArray(InsecureSecretKeyAccess.get()), "HMAC")
            ),
            key.getParameters().getTagSizeBytes()
         ),
         key.getParameters().getTagSizeBytes(),
         key.getOutputPrefix().toByteArray()
      );
   }

   public static Aead newAesCtrHmac(final byte[] aesCtrKey, int ivSize, String hmacAlgorithm, final byte[] hmacKey, int tagSize) throws GeneralSecurityException {
      IndCpaCipher cipher = new AesCtrJceCipher(aesCtrKey, ivSize);
      SecretKeySpec hmacKeySpec = new SecretKeySpec(hmacKey, "HMAC");
      Mac hmac = new PrfMac(new PrfHmacJce(hmacAlgorithm, hmacKeySpec), tagSize);
      return new EncryptThenAuthenticate(cipher, hmac, tagSize);
   }

   @Override
   public byte[] encrypt(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
      byte[] ciphertext = this.cipher.encrypt(plaintext);
      byte[] ad = associatedData;
      if (associatedData == null) {
         ad = new byte[0];
      }

      byte[] adLengthInBits = Arrays.copyOf(ByteBuffer.allocate(8).putLong(8L * ad.length).array(), 8);
      byte[] macValue = this.mac.computeMac(Bytes.concat(ad, ciphertext, adLengthInBits));
      return Bytes.concat(this.outputPrefix, ciphertext, macValue);
   }

   @Override
   public byte[] decrypt(final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException {
      if (ciphertext.length < this.macLength + this.outputPrefix.length) {
         throw new GeneralSecurityException("Decryption failed (ciphertext too short).");
      } else if (!Util.isPrefix(this.outputPrefix, ciphertext)) {
         throw new GeneralSecurityException("Decryption failed (OutputPrefix mismatch).");
      } else {
         byte[] rawCiphertext = Arrays.copyOfRange(ciphertext, this.outputPrefix.length, ciphertext.length - this.macLength);
         byte[] macValue = Arrays.copyOfRange(ciphertext, ciphertext.length - this.macLength, ciphertext.length);
         byte[] ad = associatedData;
         if (associatedData == null) {
            ad = new byte[0];
         }

         byte[] adLengthInBits = Arrays.copyOf(ByteBuffer.allocate(8).putLong(8L * ad.length).array(), 8);
         this.mac.verifyMac(macValue, Bytes.concat(ad, rawCiphertext, adLengthInBits));
         return this.cipher.decrypt(rawCiphertext);
      }
   }
}
