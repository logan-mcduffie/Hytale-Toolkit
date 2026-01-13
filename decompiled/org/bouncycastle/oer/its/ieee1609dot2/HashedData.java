package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Arrays;

public class HashedData extends ASN1Object implements ASN1Choice {
   public static final int sha256HashedData = 0;
   public static final int sha384HashedData = 1;
   public static final int reserved = 2;
   private final int choice;
   private final ASN1Encodable hashedData;

   public HashedData(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.hashedData = var2;
   }

   private HashedData(ASN1TaggedObject var1) {
      switch (var1.getTagNo()) {
         case 0:
         case 1:
         case 2:
            this.choice = var1.getTagNo();
            this.hashedData = DEROctetString.getInstance(var1.getExplicitBaseObject());
            return;
         default:
            throw new IllegalArgumentException("invalid choice value " + var1.getTagNo());
      }
   }

   public static HashedData sha256HashedData(ASN1OctetString var0) {
      return new HashedData(0, var0);
   }

   public static HashedData sha256HashedData(byte[] var0) {
      return new HashedData(0, new DEROctetString(Arrays.clone(var0)));
   }

   public static HashedData sha384HashedData(ASN1OctetString var0) {
      return new HashedData(1, var0);
   }

   public static HashedData sha384HashedData(byte[] var0) {
      return new HashedData(1, new DEROctetString(Arrays.clone(var0)));
   }

   public static HashedData reserved(ASN1OctetString var0) {
      return new HashedData(2, var0);
   }

   public static HashedData reserved(byte[] var0) {
      return new HashedData(2, new DEROctetString(Arrays.clone(var0)));
   }

   public static HashedData getInstance(Object var0) {
      if (var0 instanceof HashedData) {
         return (HashedData)var0;
      } else {
         return var0 != null ? new HashedData(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getHashedData() {
      return this.hashedData;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.hashedData);
   }
}
