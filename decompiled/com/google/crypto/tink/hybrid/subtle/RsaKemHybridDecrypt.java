package com.google.crypto.tink.hybrid.subtle;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.aead.subtle.AeadFactory;
import com.google.crypto.tink.subtle.Hkdf;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;

public final class RsaKemHybridDecrypt implements HybridDecrypt {
   private final PrivateKey recipientPrivateKey;
   private final String hkdfHmacAlgo;
   private final byte[] hkdfSalt;
   private final AeadFactory aeadFactory;
   private final int modSizeInBytes;

   private RsaKemHybridDecrypt(final PrivateKey recipientPrivateKey, String hkdfHmacAlgo, final byte[] hkdfSalt, AeadFactory aeadFactory) throws GeneralSecurityException {
      BigInteger mod = ((RSAKey)recipientPrivateKey).getModulus();
      RsaKem.validateRsaModulus(mod);
      this.recipientPrivateKey = recipientPrivateKey;
      this.hkdfSalt = hkdfSalt;
      this.hkdfHmacAlgo = hkdfHmacAlgo;
      this.aeadFactory = aeadFactory;
      this.modSizeInBytes = RsaKem.bigIntSizeInBytes(mod);
   }

   public RsaKemHybridDecrypt(final RSAPrivateKey recipientPrivateKey, String hkdfHmacAlgo, final byte[] hkdfSalt, AeadFactory aeadFactory) throws GeneralSecurityException {
      this((PrivateKey)recipientPrivateKey, hkdfHmacAlgo, hkdfSalt, aeadFactory);
   }

   public static RsaKemHybridDecrypt create(final PrivateKey recipientPrivateKey, String hkdfHmacAlgo, final byte[] hkdfSalt, AeadFactory aeadFactory) throws GeneralSecurityException {
      if (!(recipientPrivateKey instanceof RSAKey)) {
         throw new InvalidKeyException("Must be an RSA private key");
      } else {
         return new RsaKemHybridDecrypt(recipientPrivateKey, hkdfHmacAlgo, hkdfSalt, aeadFactory);
      }
   }

   @Override
   public byte[] decrypt(final byte[] ciphertext, final byte[] contextInfo) throws GeneralSecurityException {
      if (ciphertext.length < this.modSizeInBytes) {
         throw new GeneralSecurityException(String.format("Ciphertext must be of at least size %d bytes, but got %d", this.modSizeInBytes, ciphertext.length));
      } else {
         ByteBuffer cipherBuffer = ByteBuffer.wrap(ciphertext);
         byte[] token = new byte[this.modSizeInBytes];
         cipherBuffer.get(token);
         byte[] sharedSecret = RsaKem.rsaDecrypt(this.recipientPrivateKey, token);
         byte[] demKey = Hkdf.computeHkdf(this.hkdfHmacAlgo, sharedSecret, this.hkdfSalt, contextInfo, this.aeadFactory.getKeySizeInBytes());
         Aead aead = this.aeadFactory.createAead(demKey);
         byte[] demPayload = new byte[cipherBuffer.remaining()];
         cipherBuffer.get(demPayload);
         return aead.decrypt(demPayload, RsaKem.EMPTY_AAD);
      }
   }
}
