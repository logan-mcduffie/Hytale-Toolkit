package org.bouncycastle.asn1.cmp;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CertStatus extends ASN1Object {
   private final ASN1OctetString certHash;
   private final ASN1Integer certReqId;
   private final PKIStatusInfo statusInfo;
   private final AlgorithmIdentifier hashAlg;

   private CertStatus(ASN1Sequence var1) {
      this.certHash = ASN1OctetString.getInstance(var1.getObjectAt(0));
      this.certReqId = ASN1Integer.getInstance(var1.getObjectAt(1));
      PKIStatusInfo var2 = null;
      AlgorithmIdentifier var3 = null;
      if (var1.size() > 2) {
         for (int var4 = 2; var4 < var1.size(); var4++) {
            ASN1Primitive var5 = var1.getObjectAt(var4).toASN1Primitive();
            if (var5 instanceof ASN1Sequence) {
               var2 = PKIStatusInfo.getInstance(var5);
            }

            if (var5 instanceof ASN1TaggedObject) {
               ASN1TaggedObject var6 = (ASN1TaggedObject)var5;
               if (!var6.hasContextTag(0)) {
                  throw new IllegalArgumentException("unknown tag " + ASN1Util.getTagText(var6));
               }

               var3 = AlgorithmIdentifier.getInstance(var6, true);
            }
         }
      }

      this.statusInfo = var2;
      this.hashAlg = var3;
   }

   public CertStatus(byte[] var1, BigInteger var2) {
      this(var1, new ASN1Integer(var2));
   }

   public CertStatus(byte[] var1, ASN1Integer var2) {
      this.certHash = new DEROctetString(var1);
      this.certReqId = var2;
      this.statusInfo = null;
      this.hashAlg = null;
   }

   public CertStatus(byte[] var1, BigInteger var2, PKIStatusInfo var3) {
      this.certHash = new DEROctetString(var1);
      this.certReqId = new ASN1Integer(var2);
      this.statusInfo = var3;
      this.hashAlg = null;
   }

   public CertStatus(byte[] var1, BigInteger var2, PKIStatusInfo var3, AlgorithmIdentifier var4) {
      this.certHash = new DEROctetString(var1);
      this.certReqId = new ASN1Integer(var2);
      this.statusInfo = var3;
      this.hashAlg = var4;
   }

   public static CertStatus getInstance(Object var0) {
      if (var0 instanceof CertStatus) {
         return (CertStatus)var0;
      } else {
         return var0 != null ? new CertStatus(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1OctetString getCertHash() {
      return this.certHash;
   }

   public ASN1Integer getCertReqId() {
      return this.certReqId;
   }

   public PKIStatusInfo getStatusInfo() {
      return this.statusInfo;
   }

   public AlgorithmIdentifier getHashAlg() {
      return this.hashAlg;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(this.certHash);
      var1.add(this.certReqId);
      if (this.statusInfo != null) {
         var1.add(this.statusInfo);
      }

      if (this.hashAlg != null) {
         var1.add(new DERTaggedObject(true, 0, this.hashAlg));
      }

      return new DERSequence(var1);
   }
}
