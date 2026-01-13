package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class FalconPublicKey extends ASN1Object {
   private byte[] h;

   public FalconPublicKey(byte[] var1) {
      this.h = var1;
   }

   public byte[] getH() {
      return this.h;
   }

   @Deprecated
   public FalconPublicKey(ASN1Sequence var1) {
      this.h = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(0)).getOctets());
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      var1.add(new DEROctetString(this.h));
      return new DERSequence(var1);
   }

   public static FalconPublicKey getInstance(Object var0) {
      if (var0 instanceof FalconPublicKey) {
         return (FalconPublicKey)var0;
      } else {
         return var0 != null ? new FalconPublicKey(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
