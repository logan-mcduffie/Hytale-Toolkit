package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.Container;
import com.nimbusds.jose.util.KeyUtils;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

@ThreadSafe
public class AESGCM {
   public static final int IV_BIT_LENGTH = 96;
   public static final int AUTH_TAG_BIT_LENGTH = 128;

   public static byte[] generateIV(SecureRandom randomGen) {
      byte[] bytes = new byte[12];
      randomGen.nextBytes(bytes);
      return bytes;
   }

   public static AuthenticatedCipherText encrypt(SecretKey secretKey, Container<byte[]> ivContainer, byte[] plainText, byte[] authData, Provider provider) throws JOSEException {
      SecretKey aesKey = KeyUtils.toAESKey(secretKey);
      byte[] iv = ivContainer.get();

      Cipher cipher;
      try {
         if (provider != null) {
            cipher = Cipher.getInstance("AES/GCM/NoPadding", provider);
         } else {
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
         }

         GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
         cipher.init(1, aesKey, gcmSpec);
      } catch (NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException var13) {
         throw new JOSEException("Couldn't create AES/GCM/NoPadding cipher: " + var13.getMessage(), var13);
      }

      cipher.updateAAD(authData);

      byte[] cipherOutput;
      try {
         cipherOutput = cipher.doFinal(plainText);
      } catch (BadPaddingException | IllegalBlockSizeException var12) {
         throw new JOSEException("Couldn't encrypt with AES/GCM/NoPadding: " + var12.getMessage(), var12);
      }

      int tagPos = cipherOutput.length - ByteUtils.byteLength(128);
      byte[] cipherText = ByteUtils.subArray(cipherOutput, 0, tagPos);
      byte[] authTag = ByteUtils.subArray(cipherOutput, tagPos, ByteUtils.byteLength(128));
      ivContainer.set(actualIVOf(cipher));
      return new AuthenticatedCipherText(cipherText, authTag);
   }

   private static byte[] actualIVOf(Cipher cipher) throws JOSEException {
      GCMParameterSpec actualParams = actualParamsOf(cipher);
      byte[] iv = actualParams.getIV();
      int tLen = actualParams.getTLen();
      validate(iv, tLen);
      return iv;
   }

   private static void validate(byte[] iv, int authTagLength) throws JOSEException {
      if (ByteUtils.safeBitLength(iv) != 96) {
         throw new JOSEException(String.format("IV length of %d bits is required, got %d", 96, ByteUtils.safeBitLength(iv)));
      } else if (authTagLength != 128) {
         throw new JOSEException(String.format("Authentication tag length of %d bits is required, got %d", 128, authTagLength));
      }
   }

   private static GCMParameterSpec actualParamsOf(Cipher cipher) throws JOSEException {
      AlgorithmParameters algorithmParameters = cipher.getParameters();
      if (algorithmParameters == null) {
         throw new JOSEException("AES GCM ciphers are expected to make use of algorithm parameters");
      } else {
         try {
            return algorithmParameters.getParameterSpec(GCMParameterSpec.class);
         } catch (InvalidParameterSpecException var3) {
            throw new JOSEException(var3.getMessage(), var3);
         }
      }
   }

   public static byte[] decrypt(SecretKey secretKey, byte[] iv, byte[] cipherText, byte[] authData, byte[] authTag, Provider provider) throws JOSEException {
      SecretKey aesKey = KeyUtils.toAESKey(secretKey);

      Cipher cipher;
      try {
         if (provider != null) {
            cipher = Cipher.getInstance("AES/GCM/NoPadding", provider);
         } else {
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
         }

         GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
         cipher.init(2, aesKey, gcmSpec);
      } catch (NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException var10) {
         throw new JOSEException("Couldn't create AES/GCM/NoPadding cipher: " + var10.getMessage(), var10);
      }

      cipher.updateAAD(authData);

      try {
         return cipher.doFinal(ByteUtils.concat(cipherText, authTag));
      } catch (BadPaddingException | IllegalBlockSizeException var9) {
         throw new JOSEException("AES/GCM/NoPadding decryption failed: " + var9.getMessage(), var9);
      }
   }

   private AESGCM() {
   }
}
