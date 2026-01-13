package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.hybrid.HpkePublicKey;
import com.google.crypto.tink.internal.BigIntegerEncoding;
import com.google.crypto.tink.subtle.Bytes;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class HpkeContext {
   private static final byte[] EMPTY_IKM = new byte[0];
   private final HpkeAead aead;
   private final BigInteger maxSequenceNumber;
   private final byte[] key;
   private final byte[] baseNonce;
   private final byte[] encapsulatedKey;
   @GuardedBy("this")
   private BigInteger sequenceNumber;

   private HpkeContext(byte[] encapsulatedKey, byte[] key, byte[] baseNonce, BigInteger maxSequenceNumber, HpkeAead aead) {
      this.encapsulatedKey = encapsulatedKey;
      this.key = key;
      this.baseNonce = baseNonce;
      this.sequenceNumber = BigInteger.ZERO;
      this.maxSequenceNumber = maxSequenceNumber;
      this.aead = aead;
   }

   static HpkeContext createContext(byte[] mode, byte[] encapsulatedKey, byte[] sharedSecret, HpkeKem kem, HpkeKdf kdf, HpkeAead aead, byte[] info) throws GeneralSecurityException {
      byte[] suiteId = HpkeUtil.hpkeSuiteId(kem.getKemId(), kdf.getKdfId(), aead.getAeadId());
      byte[] pskIdHash = kdf.labeledExtract(HpkeUtil.EMPTY_SALT, EMPTY_IKM, "psk_id_hash", suiteId);
      byte[] infoHash = kdf.labeledExtract(HpkeUtil.EMPTY_SALT, info, "info_hash", suiteId);
      byte[] keyScheduleContext = Bytes.concat(mode, pskIdHash, infoHash);
      byte[] secret = kdf.labeledExtract(sharedSecret, EMPTY_IKM, "secret", suiteId);
      byte[] key = kdf.labeledExpand(secret, keyScheduleContext, "key", suiteId, aead.getKeyLength());
      byte[] baseNonce = kdf.labeledExpand(secret, keyScheduleContext, "base_nonce", suiteId, aead.getNonceLength());
      BigInteger maxSeqNo = maxSequenceNumber(aead.getNonceLength());
      return new HpkeContext(encapsulatedKey, key, baseNonce, maxSeqNo, aead);
   }

   static HpkeContext createSenderContext(byte[] recipientPublicKey, HpkeKem kem, HpkeKdf kdf, HpkeAead aead, byte[] info) throws GeneralSecurityException {
      HpkeKemEncapOutput encapOutput = kem.encapsulate(recipientPublicKey);
      byte[] encapsulatedKey = encapOutput.getEncapsulatedKey();
      byte[] sharedSecret = encapOutput.getSharedSecret();
      return createContext(HpkeUtil.BASE_MODE, encapsulatedKey, sharedSecret, kem, kdf, aead, info);
   }

   @AccessesPartialKey
   public static HpkeContext createAuthSenderContext(
      HpkePublicKey recipientPublicKey, HpkeKem kem, HpkeKdf kdf, HpkeAead aead, byte[] info, HpkeKemPrivateKey senderPrivateKey
   ) throws GeneralSecurityException {
      HpkeKemEncapOutput encapOutput = kem.authEncapsulate(recipientPublicKey.getPublicKeyBytes().toByteArray(), senderPrivateKey);
      byte[] encapsulatedKey = encapOutput.getEncapsulatedKey();
      byte[] sharedSecret = encapOutput.getSharedSecret();
      return createContext(HpkeUtil.AUTH_MODE, encapsulatedKey, sharedSecret, kem, kdf, aead, info);
   }

   public static HpkeContext createRecipientContext(
      byte[] encapsulatedKey, HpkeKemPrivateKey recipientPrivateKey, HpkeKem kem, HpkeKdf kdf, HpkeAead aead, byte[] info
   ) throws GeneralSecurityException {
      byte[] sharedSecret = kem.decapsulate(encapsulatedKey, recipientPrivateKey);
      return createContext(HpkeUtil.BASE_MODE, encapsulatedKey, sharedSecret, kem, kdf, aead, info);
   }

   @AccessesPartialKey
   public static HpkeContext createAuthRecipientContext(
      byte[] encapsulatedKey, HpkeKemPrivateKey recipientPrivateKey, HpkeKem kem, HpkeKdf kdf, HpkeAead aead, byte[] info, HpkePublicKey senderPublicKey
   ) throws GeneralSecurityException {
      byte[] sharedSecret = kem.authDecapsulate(encapsulatedKey, recipientPrivateKey, senderPublicKey.getPublicKeyBytes().toByteArray());
      return createContext(HpkeUtil.AUTH_MODE, encapsulatedKey, sharedSecret, kem, kdf, aead, info);
   }

   private static BigInteger maxSequenceNumber(int nonceLength) {
      return BigInteger.ONE.shiftLeft(8 * nonceLength).subtract(BigInteger.ONE);
   }

   @GuardedBy("this")
   private void incrementSequenceNumber() throws GeneralSecurityException {
      if (this.sequenceNumber.compareTo(this.maxSequenceNumber) >= 0) {
         throw new GeneralSecurityException("message limit reached");
      } else {
         this.sequenceNumber = this.sequenceNumber.add(BigInteger.ONE);
      }
   }

   @GuardedBy("this")
   private byte[] computeNonce() throws GeneralSecurityException {
      return Bytes.xor(this.baseNonce, BigIntegerEncoding.toBigEndianBytesOfFixedLength(this.sequenceNumber, this.aead.getNonceLength()));
   }

   private synchronized byte[] computeNonceAndIncrementSequenceNumber() throws GeneralSecurityException {
      byte[] nonce = this.computeNonce();
      this.incrementSequenceNumber();
      return nonce;
   }

   byte[] getKey() {
      return this.key;
   }

   byte[] getBaseNonce() {
      return this.baseNonce;
   }

   public byte[] getEncapsulatedKey() {
      return this.encapsulatedKey;
   }

   public byte[] seal(byte[] plaintext, byte[] associatedData) throws GeneralSecurityException {
      byte[] nonce = this.computeNonceAndIncrementSequenceNumber();
      return this.aead.seal(this.key, nonce, plaintext, associatedData);
   }

   byte[] seal(byte[] plaintext, int ciphertextOffset, byte[] associatedData) throws GeneralSecurityException {
      byte[] nonce = this.computeNonceAndIncrementSequenceNumber();
      return this.aead.seal(this.key, nonce, plaintext, ciphertextOffset, associatedData);
   }

   public byte[] open(byte[] ciphertext, byte[] associatedData) throws GeneralSecurityException {
      return this.open(ciphertext, 0, associatedData);
   }

   byte[] open(byte[] ciphertext, int ciphertextOffset, byte[] associatedData) throws GeneralSecurityException {
      byte[] nonce = this.computeNonceAndIncrementSequenceNumber();
      return this.aead.open(this.key, nonce, ciphertext, ciphertextOffset, associatedData);
   }
}
