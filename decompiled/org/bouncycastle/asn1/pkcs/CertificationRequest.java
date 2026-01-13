package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CertificationRequest extends ASN1Object {
   protected CertificationRequestInfo reqInfo = null;
   protected AlgorithmIdentifier sigAlgId = null;
   protected ASN1BitString sigBits = null;

   public static CertificationRequest getInstance(Object var0) {
      if (var0 instanceof CertificationRequest) {
         return (CertificationRequest)var0;
      } else {
         return var0 != null ? new CertificationRequest(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   protected CertificationRequest() {
   }

   public CertificationRequest(CertificationRequestInfo var1, AlgorithmIdentifier var2, ASN1BitString var3) {
      this.reqInfo = var1;
      this.sigAlgId = var2;
      this.sigBits = var3;
   }

   /** @deprecated */
   public CertificationRequest(ASN1Sequence var1) {
      this.reqInfo = CertificationRequestInfo.getInstance(var1.getObjectAt(0));
      this.sigAlgId = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
      this.sigBits = (DERBitString)var1.getObjectAt(2);
   }

   public CertificationRequestInfo getCertificationRequestInfo() {
      return this.reqInfo;
   }

   public AlgorithmIdentifier getSignatureAlgorithm() {
      return this.sigAlgId;
   }

   public ASN1BitString getSignature() {
      return this.sigBits;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.reqInfo);
      var1.add(this.sigAlgId);
      var1.add(this.sigBits);
      return new DERSequence(var1);
   }
}
