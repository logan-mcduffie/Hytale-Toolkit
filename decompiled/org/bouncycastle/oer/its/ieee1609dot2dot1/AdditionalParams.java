package org.bouncycastle.oer.its.ieee1609dot2dot1;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;

public class AdditionalParams extends ASN1Object implements ASN1Choice {
   public static final int original = 0;
   public static final int unified = 1;
   public static final int compactUnified = 2;
   public static final int encryptionKey = 3;
   protected final int choice;
   protected final ASN1Encodable additionalParams;

   private AdditionalParams(int var1, ASN1Encodable var2) {
      switch (var1) {
         case 0:
            this.additionalParams = ButterflyParamsOriginal.getInstance(var2);
            break;
         case 1:
         case 2:
            this.additionalParams = ButterflyExpansion.getInstance(var2);
            break;
         case 3:
            this.additionalParams = PublicEncryptionKey.getInstance(var2);
            break;
         default:
            throw new IllegalArgumentException("invalid choice value " + var1);
      }

      this.choice = var1;
   }

   private AdditionalParams(ASN1TaggedObject var1) {
      this(var1.getTagNo(), var1.getExplicitBaseObject());
   }

   public static AdditionalParams getInstance(Object var0) {
      if (var0 instanceof AdditionalParams) {
         return (AdditionalParams)var0;
      } else {
         return var0 != null ? new AdditionalParams(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public static AdditionalParams original(ButterflyParamsOriginal var0) {
      return new AdditionalParams(0, var0);
   }

   public static AdditionalParams unified(ButterflyExpansion var0) {
      return new AdditionalParams(1, var0);
   }

   public static AdditionalParams compactUnified(ButterflyExpansion var0) {
      return new AdditionalParams(2, var0);
   }

   public static AdditionalParams encryptionKey(PublicEncryptionKey var0) {
      return new AdditionalParams(3, var0);
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getAdditionalParams() {
      return this.additionalParams;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.additionalParams);
   }
}
