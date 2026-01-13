package org.bouncycastle.oer.its.ieee1609dot2dot1;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;

public class ButterflyExpansion extends ASN1Object implements ASN1Choice {
   public static final int aes128 = 0;
   protected final int choice;
   protected final ASN1Encodable butterflyExpansion;

   ButterflyExpansion(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.butterflyExpansion = var2;
   }

   private ButterflyExpansion(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
            this.butterflyExpansion = DEROctetString.getInstance(var1.getExplicitBaseObject());
            return;
         default:
            throw new IllegalArgumentException("invalid choice value " + this.choice);
      }
   }

   public static ButterflyExpansion getInstance(Object var0) {
      if (var0 instanceof ButterflyExpansion) {
         return (ButterflyExpansion)var0;
      } else {
         return var0 != null ? new ButterflyExpansion(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public static ButterflyExpansion aes128(byte[] var0) {
      if (var0.length != 16) {
         throw new IllegalArgumentException("length must be 16");
      } else {
         return new ButterflyExpansion(0, new DEROctetString(var0));
      }
   }

   public static ButterflyExpansion aes128(ASN1OctetString var0) {
      return aes128(var0.getOctets());
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.butterflyExpansion);
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getButterflyExpansion() {
      return this.butterflyExpansion;
   }
}
