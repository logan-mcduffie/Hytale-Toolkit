package com.google.crypto.tink.hybrid.subtle;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.aead.subtle.AeadFactory;
import com.google.crypto.tink.subtle.Hkdf;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;

public final class RsaKemHybridEncrypt implements HybridEncrypt {
   private final RSAPublicKey recipientPublicKey;
   private final String hkdfHmacAlgo;
   private final byte[] hkdfSalt;
   private final AeadFactory aeadFactory;

   public RsaKemHybridEncrypt(final RSAPublicKey recipientPublicKey, String hkdfHmacAlgo, final byte[] hkdfSalt, AeadFactory aeadFactory) throws GeneralSecurityException {
      RsaKem.validateRsaModulus(recipientPublicKey.getModulus());
      this.recipientPublicKey = recipientPublicKey;
      this.hkdfHmacAlgo = hkdfHmacAlgo;
      this.hkdfSalt = hkdfSalt;
      this.aeadFactory = aeadFactory;
   }

   @Override
   public byte[] encrypt(final byte[] plaintext, final byte[] contextInfo) throws GeneralSecurityException {
      BigInteger mod = this.recipientPublicKey.getModulus();
      byte[] sharedSecret = RsaKem.generateSecret(mod);
      byte[] token = RsaKem.rsaEncrypt(this.recipientPublicKey, sharedSecret);
      byte[] demKey = Hkdf.computeHkdf(this.hkdfHmacAlgo, sharedSecret, this.hkdfSalt, contextInfo, this.aeadFactory.getKeySizeInBytes());
      Aead aead = this.aeadFactory.createAead(demKey);
      byte[] ciphertext = aead.encrypt(plaintext, RsaKem.EMPTY_AAD);
      return ByteBuffer.allocate(token.length + ciphertext.length).put(token).put(ciphertext).array();
   }
}
