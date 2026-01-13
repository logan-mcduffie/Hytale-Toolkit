package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class SABERPublicKey extends ASN1Object {
   private byte[] seed_A;
   private byte[] b;

   public SABERPublicKey(byte[] var1, byte[] var2) {
      this.seed_A = var1;
      this.b = var2;
   }

   private SABERPublicKey(ASN1Sequence var1) {
      this.seed_A = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(0)).getOctets());
      this.b = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(1)).getOctets());
   }

   public byte[] getSeed_A() {
      return this.seed_A;
   }

   public byte[] getB() {
      return this.b;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      var1.add(new DEROctetString(this.seed_A));
      var1.add(new DEROctetString(this.b));
      return new DERSequence(var1);
   }

   public static SABERPublicKey getInstance(Object var0) {
      if (var0 instanceof SABERPublicKey) {
         return (SABERPublicKey)var0;
      } else {
         return var0 != null ? new SABERPublicKey(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
