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

public class KeyRecRepContent extends ASN1Object {
   private final PKIStatusInfo status;
   private CMPCertificate newSigCert;
   private ASN1Sequence caCerts;
   private ASN1Sequence keyPairHist;

   private KeyRecRepContent(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.status = PKIStatusInfo.getInstance(var2.nextElement());

      while (var2.hasMoreElements()) {
         ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var2.nextElement(), 128);
         switch (var3.getTagNo()) {
            case 0:
               this.newSigCert = CMPCertificate.getInstance(var3.getExplicitBaseObject());
               break;
            case 1:
               this.caCerts = ASN1Sequence.getInstance(var3.getExplicitBaseObject());
               break;
            case 2:
               this.keyPairHist = ASN1Sequence.getInstance(var3.getExplicitBaseObject());
               break;
            default:
               throw new IllegalArgumentException("unknown tag number: " + var3.getTagNo());
         }
      }
   }

   public static KeyRecRepContent getInstance(Object var0) {
      if (var0 instanceof KeyRecRepContent) {
         return (KeyRecRepContent)var0;
      } else {
         return var0 != null ? new KeyRecRepContent(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public PKIStatusInfo getStatus() {
      return this.status;
   }

   public CMPCertificate getNewSigCert() {
      return this.newSigCert;
   }

   public CMPCertificate[] getCaCerts() {
      if (this.caCerts == null) {
         return null;
      } else {
         CMPCertificate[] var1 = new CMPCertificate[this.caCerts.size()];

         for (int var2 = 0; var2 != var1.length; var2++) {
            var1[var2] = CMPCertificate.getInstance(this.caCerts.getObjectAt(var2));
         }

         return var1;
      }
   }

   public CertifiedKeyPair[] getKeyPairHist() {
      if (this.keyPairHist == null) {
         return null;
      } else {
         CertifiedKeyPair[] var1 = new CertifiedKeyPair[this.keyPairHist.size()];

         for (int var2 = 0; var2 != var1.length; var2++) {
            var1[var2] = CertifiedKeyPair.getInstance(this.keyPairHist.getObjectAt(var2));
         }

         return var1;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(this.status);
      this.addOptional(var1, 0, this.newSigCert);
      this.addOptional(var1, 1, this.caCerts);
      this.addOptional(var1, 2, this.keyPairHist);
      return new DERSequence(var1);
   }

   private void addOptional(ASN1EncodableVector var1, int var2, ASN1Encodable var3) {
      if (var3 != null) {
         var1.add(new DERTaggedObject(true, var2, var3));
      }
   }
}
