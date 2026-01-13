package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.x509.CertificateList;

public class RevocationValues extends ASN1Object {
   private ASN1Sequence crlVals;
   private ASN1Sequence ocspVals;
   private OtherRevVals otherRevVals;

   public static RevocationValues getInstance(Object var0) {
      if (var0 instanceof RevocationValues) {
         return (RevocationValues)var0;
      } else {
         return var0 != null ? new RevocationValues(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private RevocationValues(ASN1Sequence var1) {
      if (var1.size() > 3) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         Enumeration var2 = var1.getObjects();

         while (var2.hasMoreElements()) {
            ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var2.nextElement(), 128);
            switch (var3.getTagNo()) {
               case 0:
                  ASN1Sequence var4 = (ASN1Sequence)var3.getExplicitBaseObject();
                  Enumeration var5 = var4.getObjects();

                  while (var5.hasMoreElements()) {
                     CertificateList.getInstance(var5.nextElement());
                  }

                  this.crlVals = var4;
                  break;
               case 1:
                  ASN1Sequence var6 = (ASN1Sequence)var3.getExplicitBaseObject();
                  Enumeration var7 = var6.getObjects();

                  while (var7.hasMoreElements()) {
                     BasicOCSPResponse.getInstance(var7.nextElement());
                  }

                  this.ocspVals = var6;
                  break;
               case 2:
                  this.otherRevVals = OtherRevVals.getInstance(var3.getExplicitBaseObject());
                  break;
               default:
                  throw new IllegalArgumentException("invalid tag: " + var3.getTagNo());
            }
         }
      }
   }

   public RevocationValues(CertificateList[] var1, BasicOCSPResponse[] var2, OtherRevVals var3) {
      if (null != var1) {
         this.crlVals = new DERSequence(var1);
      }

      if (null != var2) {
         this.ocspVals = new DERSequence(var2);
      }

      this.otherRevVals = var3;
   }

   public CertificateList[] getCrlVals() {
      if (null == this.crlVals) {
         return new CertificateList[0];
      } else {
         CertificateList[] var1 = new CertificateList[this.crlVals.size()];

         for (int var2 = 0; var2 < var1.length; var2++) {
            var1[var2] = CertificateList.getInstance(this.crlVals.getObjectAt(var2));
         }

         return var1;
      }
   }

   public BasicOCSPResponse[] getOcspVals() {
      if (null == this.ocspVals) {
         return new BasicOCSPResponse[0];
      } else {
         BasicOCSPResponse[] var1 = new BasicOCSPResponse[this.ocspVals.size()];

         for (int var2 = 0; var2 < var1.length; var2++) {
            var1[var2] = BasicOCSPResponse.getInstance(this.ocspVals.getObjectAt(var2));
         }

         return var1;
      }
   }

   public OtherRevVals getOtherRevVals() {
      return this.otherRevVals;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      if (null != this.crlVals) {
         var1.add(new DERTaggedObject(true, 0, this.crlVals));
      }

      if (null != this.ocspVals) {
         var1.add(new DERTaggedObject(true, 1, this.ocspVals));
      }

      if (null != this.otherRevVals) {
         var1.add(new DERTaggedObject(true, 2, this.otherRevVals.toASN1Primitive()));
      }

      return new DERSequence(var1);
   }
}
