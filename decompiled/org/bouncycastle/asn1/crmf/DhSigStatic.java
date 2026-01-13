package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.util.Arrays;

public class DhSigStatic extends ASN1Object {
   private final IssuerAndSerialNumber issuerAndSerial;
   private final ASN1OctetString hashValue;

   public DhSigStatic(byte[] var1) {
      this(null, var1);
   }

   public DhSigStatic(IssuerAndSerialNumber var1, byte[] var2) {
      this.issuerAndSerial = var1;
      this.hashValue = new DEROctetString(Arrays.clone(var2));
   }

   public static DhSigStatic getInstance(Object var0) {
      if (var0 instanceof DhSigStatic) {
         return (DhSigStatic)var0;
      } else {
         return var0 != null ? new DhSigStatic(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private DhSigStatic(ASN1Sequence var1) {
      if (var1.size() == 1) {
         this.issuerAndSerial = null;
         this.hashValue = ASN1OctetString.getInstance(var1.getObjectAt(0));
      } else {
         if (var1.size() != 2) {
            throw new IllegalArgumentException("sequence wrong length for DhSigStatic");
         }

         this.issuerAndSerial = IssuerAndSerialNumber.getInstance(var1.getObjectAt(0));
         this.hashValue = ASN1OctetString.getInstance(var1.getObjectAt(1));
      }
   }

   public IssuerAndSerialNumber getIssuerAndSerial() {
      return this.issuerAndSerial;
   }

   public byte[] getHashValue() {
      return Arrays.clone(this.hashValue.getOctets());
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      if (this.issuerAndSerial != null) {
         var1.add(this.issuerAndSerial);
      }

      var1.add(this.hashValue);
      return new DERSequence(var1);
   }
}
