package org.bouncycastle.asn1.isismtt.x509;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class DeclarationOfMajority extends ASN1Object implements ASN1Choice {
   public static final int notYoungerThan = 0;
   public static final int fullAgeAtCountry = 1;
   public static final int dateOfBirth = 2;
   private ASN1TaggedObject declaration;

   public DeclarationOfMajority(int var1) {
      this.declaration = new DERTaggedObject(false, 0, new ASN1Integer(var1));
   }

   public DeclarationOfMajority(boolean var1, String var2) {
      if (var2.length() > 2) {
         throw new IllegalArgumentException("country can only be 2 characters");
      } else {
         if (var1) {
            this.declaration = new DERTaggedObject(false, 1, new DERSequence(new DERPrintableString(var2, true)));
         } else {
            this.declaration = new DERTaggedObject(false, 1, new DERSequence(ASN1Boolean.FALSE, new DERPrintableString(var2, true)));
         }
      }
   }

   public DeclarationOfMajority(ASN1GeneralizedTime var1) {
      this.declaration = new DERTaggedObject(false, 2, var1);
   }

   public static DeclarationOfMajority getInstance(Object var0) {
      if (var0 == null || var0 instanceof DeclarationOfMajority) {
         return (DeclarationOfMajority)var0;
      } else if (var0 instanceof ASN1TaggedObject) {
         return new DeclarationOfMajority(ASN1TaggedObject.getInstance(var0, 128));
      } else {
         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      }
   }

   private DeclarationOfMajority(ASN1TaggedObject var1) {
      if (var1.getTagNo() > 2) {
         throw new IllegalArgumentException("Bad tag number: " + var1.getTagNo());
      } else {
         this.declaration = var1;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.declaration;
   }

   public int getType() {
      return this.declaration.getTagNo();
   }

   public int notYoungerThan() {
      return this.declaration.getTagNo() != 0 ? -1 : ASN1Integer.getInstance(this.declaration, false).intValueExact();
   }

   public ASN1Sequence fullAgeAtCountry() {
      return this.declaration.getTagNo() != 1 ? null : ASN1Sequence.getInstance(this.declaration, false);
   }

   public ASN1GeneralizedTime getDateOfBirth() {
      return this.declaration.getTagNo() != 2 ? null : ASN1GeneralizedTime.getInstance(this.declaration, false);
   }
}
