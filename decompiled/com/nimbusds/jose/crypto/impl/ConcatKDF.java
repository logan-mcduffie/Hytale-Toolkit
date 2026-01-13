package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jca.JCAAware;
import com.nimbusds.jose.jca.JCAContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.IntegerUtils;
import com.nimbusds.jose.util.StandardCharset;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@ThreadSafe
public class ConcatKDF implements JCAAware<JCAContext> {
   private final String jcaHashAlg;
   private final JCAContext jcaContext = new JCAContext();

   public ConcatKDF(String jcaHashAlg) {
      if (jcaHashAlg == null) {
         throw new IllegalArgumentException("The JCA hash algorithm must not be null");
      } else {
         this.jcaHashAlg = jcaHashAlg;
      }
   }

   public String getHashAlgorithm() {
      return this.jcaHashAlg;
   }

   @Override
   public JCAContext getJCAContext() {
      return this.jcaContext;
   }

   public SecretKey deriveKey(SecretKey sharedSecret, int keyLengthBits, byte[] otherInfo) throws JOSEException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      MessageDigest md = this.getMessageDigest();

      for (int i = 1; i <= computeDigestCycles(ByteUtils.safeBitLength(md.getDigestLength()), keyLengthBits); i++) {
         byte[] counterBytes = IntegerUtils.toBytes(i);
         md.update(counterBytes);
         md.update(sharedSecret.getEncoded());
         if (otherInfo != null) {
            md.update(otherInfo);
         }

         try {
            baos.write(md.digest());
         } catch (IOException var9) {
            throw new JOSEException("Couldn't write derived key: " + var9.getMessage(), var9);
         }
      }

      byte[] derivedKeyMaterial = baos.toByteArray();
      int keyLengthBytes = ByteUtils.byteLength(keyLengthBits);
      return derivedKeyMaterial.length == keyLengthBytes
         ? new SecretKeySpec(derivedKeyMaterial, "AES")
         : new SecretKeySpec(ByteUtils.subArray(derivedKeyMaterial, 0, keyLengthBytes), "AES");
   }

   public SecretKey deriveKey(
      SecretKey sharedSecret, int keyLength, byte[] algID, byte[] partyUInfo, byte[] partyVInfo, byte[] suppPubInfo, byte[] suppPrivInfo
   ) throws JOSEException {
      byte[] otherInfo = composeOtherInfo(algID, partyUInfo, partyVInfo, suppPubInfo, suppPrivInfo);
      return this.deriveKey(sharedSecret, keyLength, otherInfo);
   }

   public SecretKey deriveKey(
      SecretKey sharedSecret, int keyLength, byte[] algID, byte[] partyUInfo, byte[] partyVInfo, byte[] suppPubInfo, byte[] suppPrivInfo, byte[] tag
   ) throws JOSEException {
      byte[] otherInfo = composeOtherInfo(algID, partyUInfo, partyVInfo, suppPubInfo, suppPrivInfo, tag);
      return this.deriveKey(sharedSecret, keyLength, otherInfo);
   }

   public static byte[] composeOtherInfo(byte[] algID, byte[] partyUInfo, byte[] partyVInfo, byte[] suppPubInfo, byte[] suppPrivInfo) {
      return ByteUtils.concat(algID, partyUInfo, partyVInfo, suppPubInfo, suppPrivInfo);
   }

   public static byte[] composeOtherInfo(byte[] algID, byte[] partyUInfo, byte[] partyVInfo, byte[] suppPubInfo, byte[] suppPrivInfo, byte[] tag) {
      return ByteUtils.concat(algID, partyUInfo, partyVInfo, suppPubInfo, suppPrivInfo, tag);
   }

   private MessageDigest getMessageDigest() throws JOSEException {
      Provider provider = this.getJCAContext().getProvider();

      try {
         return provider == null ? MessageDigest.getInstance(this.jcaHashAlg) : MessageDigest.getInstance(this.jcaHashAlg, provider);
      } catch (NoSuchAlgorithmException var3) {
         throw new JOSEException("Couldn't get message digest for KDF: " + var3.getMessage(), var3);
      }
   }

   public static int computeDigestCycles(int digestLengthBits, int keyLengthBits) {
      return (keyLengthBits + digestLengthBits - 1) / digestLengthBits;
   }

   public static byte[] encodeNoData() {
      return new byte[0];
   }

   public static byte[] encodeIntData(int data) {
      return IntegerUtils.toBytes(data);
   }

   public static byte[] encodeStringData(String data) {
      byte[] bytes = data != null ? data.getBytes(StandardCharset.UTF_8) : null;
      return encodeDataWithLength(bytes);
   }

   public static byte[] encodeDataWithLength(byte[] data) {
      byte[] bytes = data != null ? data : new byte[0];
      byte[] length = IntegerUtils.toBytes(bytes.length);
      return ByteUtils.concat(length, bytes);
   }

   public static byte[] encodeDataWithLength(Base64URL data) {
      byte[] bytes = data != null ? data.decode() : null;
      return encodeDataWithLength(bytes);
   }
}
