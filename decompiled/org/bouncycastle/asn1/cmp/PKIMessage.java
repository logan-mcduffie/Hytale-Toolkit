package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class PKIMessage extends ASN1Object {
   private final PKIHeader header;
   private final PKIBody body;
   private final ASN1BitString protection;
   private final ASN1Sequence extraCerts;

   private PKIMessage(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.header = PKIHeader.getInstance(var2.nextElement());
      if (var2.hasMoreElements()) {
         this.body = PKIBody.getInstance(var2.nextElement());
         ASN1BitString var3 = null;
         ASN1Sequence var4 = null;

         while (var2.hasMoreElements()) {
            ASN1TaggedObject var5 = (ASN1TaggedObject)var2.nextElement();
            if (var5.getTagNo() == 0) {
               var3 = ASN1BitString.getInstance(var5, true);
            } else {
               var4 = ASN1Sequence.getInstance(var5, true);
            }
         }

         this.protection = var3;
         this.extraCerts = var4;
      } else {
         throw new IllegalArgumentException("PKIMessage missing PKIBody structure");
      }
   }

   public PKIMessage(PKIHeader var1, PKIBody var2, ASN1BitString var3, CMPCertificate[] var4) {
      this.header = var1;
      this.body = var2;
      this.protection = var3;
      if (var4 != null) {
         this.extraCerts = new DERSequence(var4);
      } else {
         this.extraCerts = null;
      }
   }

   public PKIMessage(PKIHeader var1, PKIBody var2, ASN1BitString var3) {
      this(var1, var2, var3, null);
   }

   public PKIMessage(PKIHeader var1, PKIBody var2) {
      this(var1, var2, null, null);
   }

   public static PKIMessage getInstance(Object var0) {
      if (var0 instanceof PKIMessage) {
         return (PKIMessage)var0;
      } else {
         return var0 != null ? new PKIMessage(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public PKIHeader getHeader() {
      return this.header;
   }

   public PKIBody getBody() {
      return this.body;
   }

   public ASN1BitString getProtection() {
      return this.protection;
   }

   public CMPCertificate[] getExtraCerts() {
      if (this.extraCerts == null) {
         return null;
      } else {
         CMPCertificate[] var1 = new CMPCertificate[this.extraCerts.size()];

         for (int var2 = 0; var2 < var1.length; var2++) {
            var1[var2] = CMPCertificate.getInstance(this.extraCerts.getObjectAt(var2));
         }

         return var1;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(this.header);
      var1.add(this.body);
      this.addOptional(var1, 0, this.protection);
      this.addOptional(var1, 1, this.extraCerts);
      return new DERSequence(var1);
   }

   private void addOptional(ASN1EncodableVector var1, int var2, ASN1Encodable var3) {
      if (var3 != null) {
         var1.add(new DERTaggedObject(true, var2, var3));
      }
   }
}
