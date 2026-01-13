package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.CertId;
import org.bouncycastle.asn1.x509.CertificateList;

public class RevRepContent extends ASN1Object {
   private final ASN1Sequence status;
   private ASN1Sequence revCerts;
   private ASN1Sequence crls;

   private RevRepContent(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.status = ASN1Sequence.getInstance(var2.nextElement());

      while (var2.hasMoreElements()) {
         ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var2.nextElement());
         if (var3.hasContextTag(0)) {
            this.revCerts = ASN1Sequence.getInstance(var3, true);
         } else if (var3.hasContextTag(1)) {
            this.crls = ASN1Sequence.getInstance(var3, true);
         }
      }
   }

   public static RevRepContent getInstance(Object var0) {
      if (var0 instanceof RevRepContent) {
         return (RevRepContent)var0;
      } else {
         return var0 != null ? new RevRepContent(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public PKIStatusInfo[] getStatus() {
      PKIStatusInfo[] var1 = new PKIStatusInfo[this.status.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = PKIStatusInfo.getInstance(this.status.getObjectAt(var2));
      }

      return var1;
   }

   public CertId[] getRevCerts() {
      if (this.revCerts == null) {
         return null;
      } else {
         CertId[] var1 = new CertId[this.revCerts.size()];

         for (int var2 = 0; var2 != var1.length; var2++) {
            var1[var2] = CertId.getInstance(this.revCerts.getObjectAt(var2));
         }

         return var1;
      }
   }

   public CertificateList[] getCrls() {
      if (this.crls == null) {
         return null;
      } else {
         CertificateList[] var1 = new CertificateList[this.crls.size()];

         for (int var2 = 0; var2 != var1.length; var2++) {
            var1[var2] = CertificateList.getInstance(this.crls.getObjectAt(var2));
         }

         return var1;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.status);
      this.addOptional(var1, 0, this.revCerts);
      this.addOptional(var1, 1, this.crls);
      return new DERSequence(var1);
   }

   private void addOptional(ASN1EncodableVector var1, int var2, ASN1Encodable var3) {
      if (var3 != null) {
         var1.add(new DERTaggedObject(true, var2, var3));
      }
   }
}
