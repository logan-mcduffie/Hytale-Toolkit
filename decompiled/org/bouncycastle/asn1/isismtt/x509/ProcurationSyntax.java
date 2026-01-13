package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.DirectoryString;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.IssuerSerial;

public class ProcurationSyntax extends ASN1Object {
   private String country;
   private DirectoryString typeOfSubstitution;
   private GeneralName thirdPerson;
   private IssuerSerial certRef;

   public static ProcurationSyntax getInstance(Object var0) {
      if (var0 == null || var0 instanceof ProcurationSyntax) {
         return (ProcurationSyntax)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new ProcurationSyntax((ASN1Sequence)var0);
      } else {
         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      }
   }

   private ProcurationSyntax(ASN1Sequence var1) {
      if (var1.size() >= 1 && var1.size() <= 3) {
         Enumeration var2 = var1.getObjects();

         while (var2.hasMoreElements()) {
            ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var2.nextElement(), 128);
            switch (var3.getTagNo()) {
               case 1:
                  this.country = ASN1PrintableString.getInstance(var3, true).getString();
                  break;
               case 2:
                  this.typeOfSubstitution = DirectoryString.getInstance(var3, true);
                  break;
               case 3:
                  ASN1Object var4 = var3.getExplicitBaseObject();
                  if (var4 instanceof ASN1TaggedObject) {
                     this.thirdPerson = GeneralName.getInstance(var4);
                  } else {
                     this.certRef = IssuerSerial.getInstance(var4);
                  }
                  break;
               default:
                  throw new IllegalArgumentException("Bad tag number: " + var3.getTagNo());
            }
         }
      } else {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      }
   }

   public ProcurationSyntax(String var1, DirectoryString var2, IssuerSerial var3) {
      this.country = var1;
      this.typeOfSubstitution = var2;
      this.thirdPerson = null;
      this.certRef = var3;
   }

   public ProcurationSyntax(String var1, DirectoryString var2, GeneralName var3) {
      this.country = var1;
      this.typeOfSubstitution = var2;
      this.thirdPerson = var3;
      this.certRef = null;
   }

   public String getCountry() {
      return this.country;
   }

   public DirectoryString getTypeOfSubstitution() {
      return this.typeOfSubstitution;
   }

   public GeneralName getThirdPerson() {
      return this.thirdPerson;
   }

   public IssuerSerial getCertRef() {
      return this.certRef;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      if (this.country != null) {
         var1.add(new DERTaggedObject(true, 1, new DERPrintableString(this.country, true)));
      }

      if (this.typeOfSubstitution != null) {
         var1.add(new DERTaggedObject(true, 2, this.typeOfSubstitution));
      }

      if (this.thirdPerson != null) {
         var1.add(new DERTaggedObject(true, 3, this.thirdPerson));
      } else {
         var1.add(new DERTaggedObject(true, 3, this.certRef));
      }

      return new DERSequence(var1);
   }
}
