package org.bouncycastle.asn1.cmp;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class PollReqContent extends ASN1Object {
   private final ASN1Sequence content;

   private PollReqContent(ASN1Sequence var1) {
      this.content = var1;
   }

   public PollReqContent(ASN1Integer var1) {
      this(new DERSequence(new DERSequence(var1)));
   }

   public PollReqContent(ASN1Integer[] var1) {
      this(new DERSequence(intsToSequence(var1)));
   }

   public PollReqContent(BigInteger var1) {
      this(new ASN1Integer(var1));
   }

   public PollReqContent(BigInteger[] var1) {
      this(intsToASN1(var1));
   }

   public static PollReqContent getInstance(Object var0) {
      if (var0 instanceof PollReqContent) {
         return (PollReqContent)var0;
      } else {
         return var0 != null ? new PollReqContent(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private static ASN1Integer[] sequenceToASN1IntegerArray(ASN1Sequence var0) {
      ASN1Integer[] var1 = new ASN1Integer[var0.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = ASN1Integer.getInstance(var0.getObjectAt(var2));
      }

      return var1;
   }

   private static DERSequence[] intsToSequence(ASN1Integer[] var0) {
      DERSequence[] var1 = new DERSequence[var0.length];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = new DERSequence(var0[var2]);
      }

      return var1;
   }

   private static ASN1Integer[] intsToASN1(BigInteger[] var0) {
      ASN1Integer[] var1 = new ASN1Integer[var0.length];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = new ASN1Integer(var0[var2]);
      }

      return var1;
   }

   public ASN1Integer[][] getCertReqIds() {
      ASN1Integer[][] var1 = new ASN1Integer[this.content.size()][];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = sequenceToASN1IntegerArray((ASN1Sequence)this.content.getObjectAt(var2));
      }

      return var1;
   }

   public BigInteger[] getCertReqIdValues() {
      BigInteger[] var1 = new BigInteger[this.content.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = ASN1Integer.getInstance(ASN1Sequence.getInstance(this.content.getObjectAt(var2)).getObjectAt(0)).getValue();
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.content;
   }
}
