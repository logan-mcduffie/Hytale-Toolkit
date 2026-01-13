package org.bouncycastle.asn1.ua;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class DSTU4145BinaryField extends ASN1Object {
   private int m;
   private int k;
   private int j;
   private int l;

   private DSTU4145BinaryField(ASN1Sequence var1) {
      this.m = ASN1Integer.getInstance(var1.getObjectAt(0)).intPositiveValueExact();
      if (var1.getObjectAt(1) instanceof ASN1Integer) {
         this.k = ((ASN1Integer)var1.getObjectAt(1)).intPositiveValueExact();
      } else {
         if (!(var1.getObjectAt(1) instanceof ASN1Sequence)) {
            throw new IllegalArgumentException("object parse error");
         }

         ASN1Sequence var2 = ASN1Sequence.getInstance(var1.getObjectAt(1));
         this.k = ASN1Integer.getInstance(var2.getObjectAt(0)).intPositiveValueExact();
         this.j = ASN1Integer.getInstance(var2.getObjectAt(1)).intPositiveValueExact();
         this.l = ASN1Integer.getInstance(var2.getObjectAt(2)).intPositiveValueExact();
      }
   }

   public static DSTU4145BinaryField getInstance(Object var0) {
      if (var0 instanceof DSTU4145BinaryField) {
         return (DSTU4145BinaryField)var0;
      } else {
         return var0 != null ? new DSTU4145BinaryField(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public DSTU4145BinaryField(int var1, int var2, int var3, int var4) {
      this.m = var1;
      this.k = var2;
      this.j = var3;
      this.l = var4;
   }

   public int getM() {
      return this.m;
   }

   public int getK1() {
      return this.k;
   }

   public int getK2() {
      return this.j;
   }

   public int getK3() {
      return this.l;
   }

   public DSTU4145BinaryField(int var1, int var2) {
      this(var1, var2, 0, 0);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(new ASN1Integer(this.m));
      if (this.j == 0) {
         var1.add(new ASN1Integer(this.k));
      } else {
         ASN1EncodableVector var2 = new ASN1EncodableVector(3);
         var2.add(new ASN1Integer(this.k));
         var2.add(new ASN1Integer(this.j));
         var2.add(new ASN1Integer(this.l));
         var1.add(new DERSequence(var2));
      }

      return new DERSequence(var1);
   }
}
