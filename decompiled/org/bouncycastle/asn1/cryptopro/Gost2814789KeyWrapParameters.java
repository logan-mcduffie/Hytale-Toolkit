package org.bouncycastle.asn1.cryptopro;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class Gost2814789KeyWrapParameters extends ASN1Object {
   private final ASN1ObjectIdentifier encryptionParamSet;
   private final byte[] ukm;

   private Gost2814789KeyWrapParameters(ASN1Sequence var1) {
      if (var1.size() == 2) {
         this.encryptionParamSet = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
         this.ukm = ASN1OctetString.getInstance(var1.getObjectAt(1)).getOctets();
      } else {
         if (var1.size() != 1) {
            throw new IllegalArgumentException("unknown sequence length: " + var1.size());
         }

         this.encryptionParamSet = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
         this.ukm = null;
      }
   }

   public static Gost2814789KeyWrapParameters getInstance(Object var0) {
      if (var0 instanceof Gost2814789KeyWrapParameters) {
         return (Gost2814789KeyWrapParameters)var0;
      } else {
         return var0 != null ? new Gost2814789KeyWrapParameters(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public Gost2814789KeyWrapParameters(ASN1ObjectIdentifier var1) {
      this(var1, null);
   }

   public Gost2814789KeyWrapParameters(ASN1ObjectIdentifier var1, byte[] var2) {
      this.encryptionParamSet = var1;
      this.ukm = Arrays.clone(var2);
   }

   public ASN1ObjectIdentifier getEncryptionParamSet() {
      return this.encryptionParamSet;
   }

   public byte[] getUkm() {
      return Arrays.clone(this.ukm);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.encryptionParamSet);
      if (this.ukm != null) {
         var1.add(new DEROctetString(this.ukm));
      }

      return new DERSequence(var1);
   }
}
