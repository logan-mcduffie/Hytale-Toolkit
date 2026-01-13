package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.util.Arrays;

public class ESSCertID extends ASN1Object {
   private ASN1OctetString certHash;
   private IssuerSerial issuerSerial;

   public static ESSCertID getInstance(Object var0) {
      if (var0 instanceof ESSCertID) {
         return (ESSCertID)var0;
      } else {
         return var0 != null ? new ESSCertID(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private ESSCertID(ASN1Sequence var1) {
      if (var1.size() >= 1 && var1.size() <= 2) {
         this.certHash = ASN1OctetString.getInstance(var1.getObjectAt(0));
         if (var1.size() > 1) {
            this.issuerSerial = IssuerSerial.getInstance(var1.getObjectAt(1));
         }
      } else {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      }
   }

   public ESSCertID(byte[] var1) {
      this.certHash = new DEROctetString(Arrays.clone(var1));
   }

   public ESSCertID(byte[] var1, IssuerSerial var2) {
      this.certHash = new DEROctetString(Arrays.clone(var1));
      this.issuerSerial = var2;
   }

   public ESSCertID(ASN1OctetString var1, IssuerSerial var2) {
      if (var1 == null) {
         throw new NullPointerException("'certHash' cannot be null");
      } else {
         this.certHash = var1;
         this.issuerSerial = var2;
      }
   }

   public ASN1OctetString getCertHashObject() {
      return this.certHash;
   }

   public byte[] getCertHash() {
      return this.certHash.getOctets();
   }

   public IssuerSerial getIssuerSerial() {
      return this.issuerSerial;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.certHash);
      if (this.issuerSerial != null) {
         var1.add(this.issuerSerial);
      }

      return new DERSequence(var1);
   }
}
