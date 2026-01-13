package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class PKIData extends ASN1Object {
   private final TaggedAttribute[] controlSequence;
   private final TaggedRequest[] reqSequence;
   private final TaggedContentInfo[] cmsSequence;
   private final OtherMsg[] otherMsgSequence;

   public PKIData(TaggedAttribute[] var1, TaggedRequest[] var2, TaggedContentInfo[] var3, OtherMsg[] var4) {
      this.controlSequence = this.copy(var1);
      this.reqSequence = this.copy(var2);
      this.cmsSequence = this.copy(var3);
      this.otherMsgSequence = this.copy(var4);
   }

   private PKIData(ASN1Sequence var1) {
      if (var1.size() != 4) {
         throw new IllegalArgumentException("Sequence not 4 elements.");
      } else {
         ASN1Sequence var2 = (ASN1Sequence)var1.getObjectAt(0);
         this.controlSequence = new TaggedAttribute[var2.size()];

         for (int var3 = 0; var3 < this.controlSequence.length; var3++) {
            this.controlSequence[var3] = TaggedAttribute.getInstance(var2.getObjectAt(var3));
         }

         var2 = (ASN1Sequence)var1.getObjectAt(1);
         this.reqSequence = new TaggedRequest[var2.size()];

         for (int var7 = 0; var7 < this.reqSequence.length; var7++) {
            this.reqSequence[var7] = TaggedRequest.getInstance(var2.getObjectAt(var7));
         }

         var2 = (ASN1Sequence)var1.getObjectAt(2);
         this.cmsSequence = new TaggedContentInfo[var2.size()];

         for (int var8 = 0; var8 < this.cmsSequence.length; var8++) {
            this.cmsSequence[var8] = TaggedContentInfo.getInstance(var2.getObjectAt(var8));
         }

         var2 = (ASN1Sequence)var1.getObjectAt(3);
         this.otherMsgSequence = new OtherMsg[var2.size()];

         for (int var9 = 0; var9 < this.otherMsgSequence.length; var9++) {
            this.otherMsgSequence[var9] = OtherMsg.getInstance(var2.getObjectAt(var9));
         }
      }
   }

   public static PKIData getInstance(Object var0) {
      if (var0 instanceof PKIData) {
         return (PKIData)var0;
      } else {
         return var0 != null ? new PKIData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(
         new ASN1Encodable[]{
            new DERSequence(this.controlSequence), new DERSequence(this.reqSequence), new DERSequence(this.cmsSequence), new DERSequence(this.otherMsgSequence)
         }
      );
   }

   public TaggedAttribute[] getControlSequence() {
      return this.copy(this.controlSequence);
   }

   private TaggedAttribute[] copy(TaggedAttribute[] var1) {
      TaggedAttribute[] var2 = new TaggedAttribute[var1.length];
      System.arraycopy(var1, 0, var2, 0, var2.length);
      return var2;
   }

   public TaggedRequest[] getReqSequence() {
      return this.copy(this.reqSequence);
   }

   private TaggedRequest[] copy(TaggedRequest[] var1) {
      TaggedRequest[] var2 = new TaggedRequest[var1.length];
      System.arraycopy(var1, 0, var2, 0, var2.length);
      return var2;
   }

   public TaggedContentInfo[] getCmsSequence() {
      return this.copy(this.cmsSequence);
   }

   private TaggedContentInfo[] copy(TaggedContentInfo[] var1) {
      TaggedContentInfo[] var2 = new TaggedContentInfo[var1.length];
      System.arraycopy(var1, 0, var2, 0, var2.length);
      return var2;
   }

   public OtherMsg[] getOtherMsgSequence() {
      return this.copy(this.otherMsgSequence);
   }

   private OtherMsg[] copy(OtherMsg[] var1) {
      OtherMsg[] var2 = new OtherMsg[var1.length];
      System.arraycopy(var1, 0, var2, 0, var2.length);
      return var2;
   }
}
