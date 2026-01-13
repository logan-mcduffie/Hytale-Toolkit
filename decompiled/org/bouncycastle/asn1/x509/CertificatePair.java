package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class CertificatePair extends ASN1Object {
   private Certificate forward;
   private Certificate reverse;

   public static CertificatePair getInstance(Object var0) {
      if (var0 == null || var0 instanceof CertificatePair) {
         return (CertificatePair)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new CertificatePair((ASN1Sequence)var0);
      } else {
         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      }
   }

   private CertificatePair(ASN1Sequence var1) {
      if (var1.size() != 1 && var1.size() != 2) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         Enumeration var2 = var1.getObjects();

         while (var2.hasMoreElements()) {
            ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var2.nextElement());
            if (var3.getTagNo() == 0) {
               this.forward = Certificate.getInstance(var3, true);
            } else {
               if (var3.getTagNo() != 1) {
                  throw new IllegalArgumentException("Bad tag number: " + var3.getTagNo());
               }

               this.reverse = Certificate.getInstance(var3, true);
            }
         }
      }
   }

   public CertificatePair(Certificate var1, Certificate var2) {
      this.forward = var1;
      this.reverse = var2;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      if (this.forward != null) {
         var1.add(new DERTaggedObject(0, this.forward));
      }

      if (this.reverse != null) {
         var1.add(new DERTaggedObject(1, this.reverse));
      }

      return new DERSequence(var1);
   }

   public Certificate getForward() {
      return this.forward;
   }

   public Certificate getReverse() {
      return this.reverse;
   }
}
