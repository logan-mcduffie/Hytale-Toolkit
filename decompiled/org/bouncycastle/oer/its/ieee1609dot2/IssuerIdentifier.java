package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashAlgorithm;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class IssuerIdentifier extends ASN1Object implements ASN1Choice {
   public static final int sha256AndDigest = 0;
   public static final int self = 1;
   public static final int sha384AndDigest = 2;
   private final int choice;
   private final ASN1Encodable issuerIdentifier;

   public static IssuerIdentifier sha256AndDigest(HashedId8 var0) {
      return new IssuerIdentifier(0, var0);
   }

   public static IssuerIdentifier self(HashAlgorithm var0) {
      return new IssuerIdentifier(1, var0);
   }

   public static IssuerIdentifier sha384AndDigest(HashedId8 var0) {
      return new IssuerIdentifier(2, var0);
   }

   public IssuerIdentifier(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.issuerIdentifier = var2;
   }

   private IssuerIdentifier(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      ASN1Object var2 = var1.getExplicitBaseObject();
      switch (this.choice) {
         case 0:
         case 2:
            this.issuerIdentifier = HashedId8.getInstance(var2);
            break;
         case 1:
            this.issuerIdentifier = HashAlgorithm.getInstance(var2);
            break;
         default:
            throw new IllegalArgumentException("invalid choice value " + this.choice);
      }
   }

   public static IssuerIdentifier getInstance(Object var0) {
      if (var0 instanceof IssuerIdentifier) {
         return (IssuerIdentifier)var0;
      } else {
         return var0 != null ? new IssuerIdentifier(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public boolean isSelf() {
      return this.choice == 1;
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getIssuerIdentifier() {
      return this.issuerIdentifier;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.issuerIdentifier);
   }
}
