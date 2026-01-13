package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class ParSet extends ASN1Object {
   private int t;
   private int[] h;
   private int[] w;
   private int[] k;

   private static int checkBigIntegerInIntRangeAndPositive(ASN1Encodable var0) {
      ASN1Integer var1 = (ASN1Integer)var0;
      int var2 = var1.intValueExact();
      if (var2 <= 0) {
         throw new IllegalArgumentException("BigInteger not in Range: " + var2);
      } else {
         return var2;
      }
   }

   private ParSet(ASN1Sequence var1) {
      if (var1.size() != 4) {
         throw new IllegalArgumentException("sie of seqOfParams = " + var1.size());
      } else {
         this.t = checkBigIntegerInIntRangeAndPositive(var1.getObjectAt(0));
         ASN1Sequence var2 = (ASN1Sequence)var1.getObjectAt(1);
         ASN1Sequence var3 = (ASN1Sequence)var1.getObjectAt(2);
         ASN1Sequence var4 = (ASN1Sequence)var1.getObjectAt(3);
         if (var2.size() == this.t && var3.size() == this.t && var4.size() == this.t) {
            this.h = new int[var2.size()];
            this.w = new int[var3.size()];
            this.k = new int[var4.size()];

            for (int var5 = 0; var5 < this.t; var5++) {
               this.h[var5] = checkBigIntegerInIntRangeAndPositive(var2.getObjectAt(var5));
               this.w[var5] = checkBigIntegerInIntRangeAndPositive(var3.getObjectAt(var5));
               this.k[var5] = checkBigIntegerInIntRangeAndPositive(var4.getObjectAt(var5));
            }
         } else {
            throw new IllegalArgumentException("invalid size of sequences");
         }
      }
   }

   public ParSet(int var1, int[] var2, int[] var3, int[] var4) {
      this.t = var1;
      this.h = var2;
      this.w = var3;
      this.k = var4;
   }

   public static ParSet getInstance(Object var0) {
      if (var0 instanceof ParSet) {
         return (ParSet)var0;
      } else {
         return var0 != null ? new ParSet(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public int getT() {
      return this.t;
   }

   public int[] getH() {
      return Arrays.clone(this.h);
   }

   public int[] getW() {
      return Arrays.clone(this.w);
   }

   public int[] getK() {
      return Arrays.clone(this.k);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      ASN1EncodableVector var2 = new ASN1EncodableVector();
      ASN1EncodableVector var3 = new ASN1EncodableVector();

      for (int var4 = 0; var4 < this.h.length; var4++) {
         var1.add(new ASN1Integer(this.h[var4]));
         var2.add(new ASN1Integer(this.w[var4]));
         var3.add(new ASN1Integer(this.k[var4]));
      }

      ASN1EncodableVector var5 = new ASN1EncodableVector();
      var5.add(new ASN1Integer(this.t));
      var5.add(new DERSequence(var1));
      var5.add(new DERSequence(var2));
      var5.add(new DERSequence(var3));
      return new DERSequence(var5);
   }
}
