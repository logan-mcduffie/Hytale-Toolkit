package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class PendInfo extends ASN1Object {
   private final byte[] pendToken;
   private final ASN1GeneralizedTime pendTime;

   public PendInfo(byte[] var1, ASN1GeneralizedTime var2) {
      this.pendToken = Arrays.clone(var1);
      this.pendTime = var2;
   }

   private PendInfo(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("incorrect sequence size");
      } else {
         this.pendToken = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(0)).getOctets());
         this.pendTime = ASN1GeneralizedTime.getInstance(var1.getObjectAt(1));
      }
   }

   public static PendInfo getInstance(Object var0) {
      if (var0 instanceof PendInfo) {
         return (PendInfo)var0;
      } else {
         return var0 != null ? new PendInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new DEROctetString(this.pendToken), this.pendTime);
   }

   public byte[] getPendToken() {
      return Arrays.clone(this.pendToken);
   }

   public ASN1GeneralizedTime getPendTime() {
      return this.pendTime;
   }
}
