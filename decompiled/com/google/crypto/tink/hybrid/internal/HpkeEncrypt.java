package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.hybrid.HpkeParameters;
import com.google.crypto.tink.hybrid.HpkePublicKey;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public final class HpkeEncrypt implements HybridEncrypt {
   private static final byte[] EMPTY_ASSOCIATED_DATA = new byte[0];
   private final byte[] recipientPublicKey;
   private final HpkeKem kem;
   private final HpkeKdf kdf;
   private final HpkeAead aead;
   private final byte[] outputPrefix;

   private HpkeEncrypt(Bytes recipientPublicKey, HpkeKem kem, HpkeKdf kdf, HpkeAead aead, Bytes outputPrefix) {
      this.recipientPublicKey = recipientPublicKey.toByteArray();
      this.kem = kem;
      this.kdf = kdf;
      this.aead = aead;
      this.outputPrefix = outputPrefix.toByteArray();
   }

   @AccessesPartialKey
   public static HybridEncrypt create(HpkePublicKey key) throws GeneralSecurityException {
      HpkeParameters parameters = key.getParameters();
      return new HpkeEncrypt(
         key.getPublicKeyBytes(),
         HpkePrimitiveFactory.createKem(parameters.getKemId()),
         HpkePrimitiveFactory.createKdf(parameters.getKdfId()),
         HpkePrimitiveFactory.createAead(parameters.getAeadId()),
         key.getOutputPrefix()
      );
   }

   @Override
   public byte[] encrypt(final byte[] plaintext, final byte[] contextInfo) throws GeneralSecurityException {
      byte[] info = contextInfo;
      if (contextInfo == null) {
         info = new byte[0];
      }

      HpkeContext context = HpkeContext.createSenderContext(this.recipientPublicKey, this.kem, this.kdf, this.aead, info);
      byte[] encapsulatedKey = context.getEncapsulatedKey();
      int ciphertextOffset = this.outputPrefix.length + encapsulatedKey.length;
      byte[] ciphertextWithPrefix = context.seal(plaintext, ciphertextOffset, EMPTY_ASSOCIATED_DATA);
      System.arraycopy(this.outputPrefix, 0, ciphertextWithPrefix, 0, this.outputPrefix.length);
      System.arraycopy(encapsulatedKey, 0, ciphertextWithPrefix, this.outputPrefix.length, encapsulatedKey.length);
      return ciphertextWithPrefix;
   }
}
