package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class SPHINCSPLUSPublicKey extends ASN1Object {
   private byte[] pkseed;
   private byte[] pkroot;

   public SPHINCSPLUSPublicKey(byte[] var1, byte[] var2) {
      this.pkseed = var1;
      this.pkroot = var2;
   }

   @Deprecated
   public SPHINCSPLUSPublicKey(ASN1Sequence var1) {
      this.pkseed = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(0)).getOctets());
      this.pkroot = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(1)).getOctets());
   }

   public byte[] getPkseed() {
      return Arrays.clone(this.pkseed);
   }

   public byte[] getPkroot() {
      return Arrays.clone(this.pkroot);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      var1.add(new DEROctetString(this.pkseed));
      var1.add(new DEROctetString(this.pkroot));
      return new DERSequence(var1);
   }

   public static SPHINCSPLUSPublicKey getInstance(Object var0) {
      if (var0 instanceof SPHINCSPLUSPublicKey) {
         return (SPHINCSPLUSPublicKey)var0;
      } else {
         return var0 != null ? new SPHINCSPLUSPublicKey(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
