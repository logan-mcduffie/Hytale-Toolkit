package org.bouncycastle.oer.its.ieee1609dot2dot1;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;

public class ButterflyParamsOriginal extends ASN1Object {
   private final ButterflyExpansion signingExpansion;
   private final PublicEncryptionKey encryptionKey;
   private final ButterflyExpansion encryptionExpansion;

   public ButterflyParamsOriginal(ButterflyExpansion var1, PublicEncryptionKey var2, ButterflyExpansion var3) {
      this.signingExpansion = var1;
      this.encryptionKey = var2;
      this.encryptionExpansion = var3;
   }

   private ButterflyParamsOriginal(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("expected sequence size of 3");
      } else {
         this.signingExpansion = ButterflyExpansion.getInstance(var1.getObjectAt(0));
         this.encryptionKey = PublicEncryptionKey.getInstance(var1.getObjectAt(1));
         this.encryptionExpansion = ButterflyExpansion.getInstance(var1.getObjectAt(2));
      }
   }

   public static ButterflyParamsOriginal getInstance(Object var0) {
      if (var0 instanceof ButterflyParamsOriginal) {
         return (ButterflyParamsOriginal)var0;
      } else {
         return var0 != null ? new ButterflyParamsOriginal(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static ButterflyParamsOriginal.Builder builder() {
      return new ButterflyParamsOriginal.Builder();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.signingExpansion, this.encryptionKey, this.encryptionExpansion});
   }

   public ButterflyExpansion getSigningExpansion() {
      return this.signingExpansion;
   }

   public PublicEncryptionKey getEncryptionKey() {
      return this.encryptionKey;
   }

   public ButterflyExpansion getEncryptionExpansion() {
      return this.encryptionExpansion;
   }

   public static class Builder {
      private ButterflyExpansion signingExpansion;
      private PublicEncryptionKey encryptionKey;
      private ButterflyExpansion encryptionExpansion;

      public ButterflyParamsOriginal.Builder setSigningExpansion(ButterflyExpansion var1) {
         this.signingExpansion = var1;
         return this;
      }

      public ButterflyParamsOriginal.Builder setEncryptionKey(PublicEncryptionKey var1) {
         this.encryptionKey = var1;
         return this;
      }

      public ButterflyParamsOriginal.Builder setEncryptionExpansion(ButterflyExpansion var1) {
         this.encryptionExpansion = var1;
         return this;
      }

      public ButterflyParamsOriginal createButterflyParamsOriginal() {
         return new ButterflyParamsOriginal(this.signingExpansion, this.encryptionKey, this.encryptionExpansion);
      }
   }
}
