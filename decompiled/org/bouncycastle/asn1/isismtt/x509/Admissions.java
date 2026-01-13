package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.GeneralName;

public class Admissions extends ASN1Object {
   private GeneralName admissionAuthority;
   private NamingAuthority namingAuthority;
   private ASN1Sequence professionInfos;

   public static Admissions getInstance(Object var0) {
      if (var0 == null || var0 instanceof Admissions) {
         return (Admissions)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new Admissions((ASN1Sequence)var0);
      } else {
         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      }
   }

   private Admissions(ASN1Sequence var1) {
      if (var1.size() > 3) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         Enumeration var2 = var1.getObjects();
         ASN1Encodable var3 = (ASN1Encodable)var2.nextElement();
         if (var3 instanceof ASN1TaggedObject) {
            switch (((ASN1TaggedObject)var3).getTagNo()) {
               case 0:
                  this.admissionAuthority = GeneralName.getInstance((ASN1TaggedObject)var3, true);
                  break;
               case 1:
                  this.namingAuthority = NamingAuthority.getInstance((ASN1TaggedObject)var3, true);
                  break;
               default:
                  throw new IllegalArgumentException("Bad tag number: " + ((ASN1TaggedObject)var3).getTagNo());
            }

            var3 = (ASN1Encodable)var2.nextElement();
         }

         if (var3 instanceof ASN1TaggedObject) {
            switch (((ASN1TaggedObject)var3).getTagNo()) {
               case 1:
                  this.namingAuthority = NamingAuthority.getInstance((ASN1TaggedObject)var3, true);
                  var3 = (ASN1Encodable)var2.nextElement();
                  break;
               default:
                  throw new IllegalArgumentException("Bad tag number: " + ((ASN1TaggedObject)var3).getTagNo());
            }
         }

         this.professionInfos = ASN1Sequence.getInstance(var3);
         if (var2.hasMoreElements()) {
            throw new IllegalArgumentException("Bad object encountered: " + var2.nextElement().getClass());
         }
      }
   }

   public Admissions(GeneralName var1, NamingAuthority var2, ProfessionInfo[] var3) {
      this.admissionAuthority = var1;
      this.namingAuthority = var2;
      this.professionInfos = new DERSequence(var3);
   }

   public GeneralName getAdmissionAuthority() {
      return this.admissionAuthority;
   }

   public NamingAuthority getNamingAuthority() {
      return this.namingAuthority;
   }

   public ProfessionInfo[] getProfessionInfos() {
      ProfessionInfo[] var1 = new ProfessionInfo[this.professionInfos.size()];
      int var2 = 0;
      Enumeration var3 = this.professionInfos.getObjects();

      while (var3.hasMoreElements()) {
         var1[var2++] = ProfessionInfo.getInstance(var3.nextElement());
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      if (this.admissionAuthority != null) {
         var1.add(new DERTaggedObject(true, 0, this.admissionAuthority));
      }

      if (this.namingAuthority != null) {
         var1.add(new DERTaggedObject(true, 1, this.namingAuthority));
      }

      var1.add(this.professionInfos);
      return new DERSequence(var1);
   }
}
