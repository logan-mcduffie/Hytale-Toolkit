package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.CertId;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class OOBCertHash extends ASN1Object {
   private final AlgorithmIdentifier hashAlg;
   private final CertId certId;
   private final ASN1BitString hashVal;

   private OOBCertHash(ASN1Sequence var1) {
      int var2 = var1.size() - 1;
      this.hashVal = ASN1BitString.getInstance(var1.getObjectAt(var2--));
      AlgorithmIdentifier var3 = null;
      CertId var4 = null;

      for (int var5 = var2; var5 >= 0; var5--) {
         ASN1TaggedObject var6 = (ASN1TaggedObject)var1.getObjectAt(var5);
         if (var6.hasContextTag(0)) {
            var3 = AlgorithmIdentifier.getInstance(var6, true);
         } else {
            if (!var6.hasContextTag(1)) {
               throw new IllegalArgumentException("unknown tag " + ASN1Util.getTagText(var6));
            }

            var4 = CertId.getInstance(var6, true);
         }
      }

      this.hashAlg = var3;
      this.certId = var4;
   }

   public OOBCertHash(AlgorithmIdentifier var1, CertId var2, byte[] var3) {
      this(var1, var2, new DERBitString(var3));
   }

   public OOBCertHash(AlgorithmIdentifier var1, CertId var2, DERBitString var3) {
      this.hashAlg = var1;
      this.certId = var2;
      this.hashVal = var3;
   }

   public static OOBCertHash getInstance(Object var0) {
      if (var0 instanceof OOBCertHash) {
         return (OOBCertHash)var0;
      } else {
         return var0 != null ? new OOBCertHash(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AlgorithmIdentifier getHashAlg() {
      return this.hashAlg;
   }

   public CertId getCertId() {
      return this.certId;
   }

   public ASN1BitString getHashVal() {
      return this.hashVal;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      this.addOptional(var1, 0, this.hashAlg);
      this.addOptional(var1, 1, this.certId);
      var1.add(this.hashVal);
      return new DERSequence(var1);
   }

   private void addOptional(ASN1EncodableVector var1, int var2, ASN1Encodable var3) {
      if (var3 != null) {
         var1.add(new DERTaggedObject(true, var2, var3));
      }
   }
}
