package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;

public class PublicEncryptionKey extends ASN1Object {
   private final SymmAlgorithm supportedSymmAlg;
   private final BasePublicEncryptionKey publicKey;

   public PublicEncryptionKey(SymmAlgorithm var1, BasePublicEncryptionKey var2) {
      this.supportedSymmAlg = var1;
      this.publicKey = var2;
   }

   private PublicEncryptionKey(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.supportedSymmAlg = SymmAlgorithm.getInstance(var1.getObjectAt(0));
         this.publicKey = BasePublicEncryptionKey.getInstance(var1.getObjectAt(1));
      }
   }

   public static PublicEncryptionKey getInstance(Object var0) {
      if (var0 instanceof PublicEncryptionKey) {
         return (PublicEncryptionKey)var0;
      } else {
         return var0 != null ? new PublicEncryptionKey(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public SymmAlgorithm getSupportedSymmAlg() {
      return this.supportedSymmAlg;
   }

   public BasePublicEncryptionKey getPublicKey() {
      return this.publicKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.supportedSymmAlg, this.publicKey);
   }

   public static PublicEncryptionKey.Builder builder() {
      return new PublicEncryptionKey.Builder();
   }

   public static class Builder {
      private SymmAlgorithm supportedSymmAlg;
      private BasePublicEncryptionKey publicKey;

      public PublicEncryptionKey.Builder setSupportedSymmAlg(SymmAlgorithm var1) {
         this.supportedSymmAlg = var1;
         return this;
      }

      public PublicEncryptionKey.Builder setPublicKey(BasePublicEncryptionKey var1) {
         this.publicKey = var1;
         return this;
      }

      public PublicEncryptionKey createPublicEncryptionKey() {
         return new PublicEncryptionKey(this.supportedSymmAlg, this.publicKey);
      }
   }
}
