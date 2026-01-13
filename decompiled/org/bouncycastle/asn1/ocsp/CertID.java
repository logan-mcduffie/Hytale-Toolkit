package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CertID extends ASN1Object {
   AlgorithmIdentifier hashAlgorithm;
   ASN1OctetString issuerNameHash;
   ASN1OctetString issuerKeyHash;
   ASN1Integer serialNumber;

   public CertID(AlgorithmIdentifier var1, ASN1OctetString var2, ASN1OctetString var3, ASN1Integer var4) {
      this.hashAlgorithm = var1;
      this.issuerNameHash = var2;
      this.issuerKeyHash = var3;
      this.serialNumber = var4;
   }

   private CertID(ASN1Sequence var1) {
      this.hashAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
      this.issuerNameHash = (ASN1OctetString)var1.getObjectAt(1);
      this.issuerKeyHash = (ASN1OctetString)var1.getObjectAt(2);
      this.serialNumber = (ASN1Integer)var1.getObjectAt(3);
   }

   public static CertID getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static CertID getInstance(Object var0) {
      if (var0 instanceof CertID) {
         return (CertID)var0;
      } else {
         return var0 != null ? new CertID(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AlgorithmIdentifier getHashAlgorithm() {
      return this.hashAlgorithm;
   }

   public ASN1OctetString getIssuerNameHash() {
      return this.issuerNameHash;
   }

   public ASN1OctetString getIssuerKeyHash() {
      return this.issuerKeyHash;
   }

   public ASN1Integer getSerialNumber() {
      return this.serialNumber;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ASN1Encodable)) {
         return false;
      } else {
         try {
            CertID var2 = getInstance(var1);
            if (!this.hashAlgorithm.getAlgorithm().equals(var2.hashAlgorithm.getAlgorithm())) {
               return false;
            } else {
               return !this.isEqual(this.hashAlgorithm.getParameters(), var2.hashAlgorithm.getParameters())
                  ? false
                  : this.issuerNameHash.equals(var2.issuerNameHash)
                     && this.issuerKeyHash.equals(var2.issuerKeyHash)
                     && this.serialNumber.equals(var2.serialNumber);
            }
         } catch (Exception var3) {
            return false;
         }
      }
   }

   @Override
   public int hashCode() {
      ASN1Encodable var1 = this.hashAlgorithm.getParameters();
      int var2 = var1 != null && !DERNull.INSTANCE.equals(var1) ? var1.hashCode() : 0;
      return var2
         + 7
            * (
               this.hashAlgorithm.getAlgorithm().hashCode()
                  + 7 * (this.issuerNameHash.hashCode() + 7 * (this.issuerKeyHash.hashCode() + 7 * this.serialNumber.hashCode()))
            );
   }

   private boolean isEqual(ASN1Encodable var1, ASN1Encodable var2) {
      if (var1 == var2) {
         return true;
      } else if (var1 == null) {
         return DERNull.INSTANCE.equals(var2);
      } else {
         return DERNull.INSTANCE.equals(var1) && var2 == null ? true : var1.equals(var2);
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(this.hashAlgorithm);
      var1.add(this.issuerNameHash);
      var1.add(this.issuerKeyHash);
      var1.add(this.serialNumber);
      return new DERSequence(var1);
   }
}
