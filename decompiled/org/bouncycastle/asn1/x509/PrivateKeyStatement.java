package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.IssuerAndSerialNumber;

@Deprecated
public class PrivateKeyStatement extends ASN1Object {
   private final IssuerAndSerialNumber signer;
   private final Certificate cert;

   public static PrivateKeyStatement getInstance(Object var0) {
      if (var0 instanceof PrivateKeyStatement) {
         return (PrivateKeyStatement)var0;
      } else {
         return var0 != null ? new PrivateKeyStatement(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private PrivateKeyStatement(ASN1Sequence var1) {
      if (var1.size() == 1) {
         this.signer = IssuerAndSerialNumber.getInstance(var1.getObjectAt(0));
         this.cert = null;
      } else {
         if (var1.size() != 2) {
            throw new IllegalArgumentException("unknown sequence in PrivateKeyStatement");
         }

         this.signer = IssuerAndSerialNumber.getInstance(var1.getObjectAt(0));
         this.cert = Certificate.getInstance(var1.getObjectAt(1));
      }
   }

   public PrivateKeyStatement(IssuerAndSerialNumber var1) {
      this.signer = var1;
      this.cert = null;
   }

   public PrivateKeyStatement(Certificate var1) {
      this.signer = new IssuerAndSerialNumber(var1.getIssuer(), var1.getSerialNumber().getValue());
      this.cert = var1;
   }

   public IssuerAndSerialNumber getSigner() {
      return this.signer;
   }

   public Certificate getCert() {
      return this.cert;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.signer);
      if (this.cert != null) {
         var1.add(this.cert);
      }

      return new DERSequence(var1);
   }
}
