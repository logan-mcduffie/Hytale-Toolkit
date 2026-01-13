package org.bouncycastle.asn1.x509.sigi;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.DirectoryString;

public class PersonalData extends ASN1Object {
   private NameOrPseudonym nameOrPseudonym;
   private BigInteger nameDistinguisher;
   private ASN1GeneralizedTime dateOfBirth;
   private DirectoryString placeOfBirth;
   private String gender;
   private DirectoryString postalAddress;

   public static PersonalData getInstance(Object var0) {
      if (var0 == null || var0 instanceof PersonalData) {
         return (PersonalData)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new PersonalData((ASN1Sequence)var0);
      } else {
         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      }
   }

   private PersonalData(ASN1Sequence var1) {
      if (var1.size() < 1) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         Enumeration var2 = var1.getObjects();
         this.nameOrPseudonym = NameOrPseudonym.getInstance(var2.nextElement());

         while (var2.hasMoreElements()) {
            ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var2.nextElement());
            int var4 = var3.getTagNo();
            switch (var4) {
               case 0:
                  this.nameDistinguisher = ASN1Integer.getInstance(var3, false).getValue();
                  break;
               case 1:
                  this.dateOfBirth = ASN1GeneralizedTime.getInstance(var3, false);
                  break;
               case 2:
                  this.placeOfBirth = DirectoryString.getInstance(var3, true);
                  break;
               case 3:
                  this.gender = ASN1PrintableString.getInstance(var3, false).getString();
                  break;
               case 4:
                  this.postalAddress = DirectoryString.getInstance(var3, true);
                  break;
               default:
                  throw new IllegalArgumentException("Bad tag number: " + var3.getTagNo());
            }
         }
      }
   }

   public PersonalData(NameOrPseudonym var1, BigInteger var2, ASN1GeneralizedTime var3, DirectoryString var4, String var5, DirectoryString var6) {
      this.nameOrPseudonym = var1;
      this.dateOfBirth = var3;
      this.gender = var5;
      this.nameDistinguisher = var2;
      this.postalAddress = var6;
      this.placeOfBirth = var4;
   }

   public NameOrPseudonym getNameOrPseudonym() {
      return this.nameOrPseudonym;
   }

   public BigInteger getNameDistinguisher() {
      return this.nameDistinguisher;
   }

   public ASN1GeneralizedTime getDateOfBirth() {
      return this.dateOfBirth;
   }

   public DirectoryString getPlaceOfBirth() {
      return this.placeOfBirth;
   }

   public String getGender() {
      return this.gender;
   }

   public DirectoryString getPostalAddress() {
      return this.postalAddress;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(6);
      var1.add(this.nameOrPseudonym);
      if (this.nameDistinguisher != null) {
         var1.add(new DERTaggedObject(false, 0, new ASN1Integer(this.nameDistinguisher)));
      }

      if (this.dateOfBirth != null) {
         var1.add(new DERTaggedObject(false, 1, this.dateOfBirth));
      }

      if (this.placeOfBirth != null) {
         var1.add(new DERTaggedObject(true, 2, this.placeOfBirth));
      }

      if (this.gender != null) {
         var1.add(new DERTaggedObject(false, 3, new DERPrintableString(this.gender, true)));
      }

      if (this.postalAddress != null) {
         var1.add(new DERTaggedObject(true, 4, this.postalAddress));
      }

      return new DERSequence(var1);
   }
}
