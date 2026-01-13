package org.bouncycastle.asn1.ua;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class DSTU4145Params extends ASN1Object {
   private static final byte[] DEFAULT_DKE = new byte[]{
      -87,
      -42,
      -21,
      69,
      -15,
      60,
      112,
      -126,
      -128,
      -60,
      -106,
      123,
      35,
      31,
      94,
      -83,
      -10,
      88,
      -21,
      -92,
      -64,
      55,
      41,
      29,
      56,
      -39,
      107,
      -16,
      37,
      -54,
      78,
      23,
      -8,
      -23,
      114,
      13,
      -58,
      21,
      -76,
      58,
      40,
      -105,
      95,
      11,
      -63,
      -34,
      -93,
      100,
      56,
      -75,
      100,
      -22,
      44,
      23,
      -97,
      -48,
      18,
      62,
      109,
      -72,
      -6,
      -59,
      121,
      4
   };
   private ASN1ObjectIdentifier namedCurve;
   private DSTU4145ECBinary ecbinary;
   private byte[] dke = DEFAULT_DKE;

   public DSTU4145Params(ASN1ObjectIdentifier var1) {
      this.namedCurve = var1;
   }

   public DSTU4145Params(ASN1ObjectIdentifier var1, byte[] var2) {
      this.namedCurve = var1;
      this.dke = Arrays.clone(var2);
   }

   public DSTU4145Params(DSTU4145ECBinary var1) {
      this.ecbinary = var1;
   }

   public boolean isNamedCurve() {
      return this.namedCurve != null;
   }

   public DSTU4145ECBinary getECBinary() {
      return this.ecbinary;
   }

   public byte[] getDKE() {
      return Arrays.clone(this.dke);
   }

   public static byte[] getDefaultDKE() {
      return Arrays.clone(DEFAULT_DKE);
   }

   public ASN1ObjectIdentifier getNamedCurve() {
      return this.namedCurve;
   }

   public static DSTU4145Params getInstance(Object var0) {
      if (var0 instanceof DSTU4145Params) {
         return (DSTU4145Params)var0;
      } else if (var0 != null) {
         ASN1Sequence var1 = ASN1Sequence.getInstance(var0);
         DSTU4145Params var2;
         if (var1.getObjectAt(0) instanceof ASN1ObjectIdentifier) {
            var2 = new DSTU4145Params(ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0)));
         } else {
            var2 = new DSTU4145Params(DSTU4145ECBinary.getInstance(var1.getObjectAt(0)));
         }

         if (var1.size() == 2) {
            var2.dke = ASN1OctetString.getInstance(var1.getObjectAt(1)).getOctets();
            if (var2.dke.length != DEFAULT_DKE.length) {
               throw new IllegalArgumentException("object parse error");
            }
         }

         return var2;
      } else {
         throw new IllegalArgumentException("object parse error");
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      if (this.namedCurve != null) {
         var1.add(this.namedCurve);
      } else {
         var1.add(this.ecbinary);
      }

      if (!Arrays.areEqual(this.dke, DEFAULT_DKE)) {
         var1.add(new DEROctetString(this.dke));
      }

      return new DERSequence(var1);
   }
}
