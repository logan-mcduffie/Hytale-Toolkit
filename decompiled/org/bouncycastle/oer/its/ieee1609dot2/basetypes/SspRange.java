package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERTaggedObject;

public class SspRange extends ASN1Object implements ASN1Choice {
   public static final int opaque = 0;
   public static final int all = 1;
   public static final int bitmapSspRange = 2;
   private final int choice;
   private final ASN1Encodable sspRange;

   public static SspRange opaque(SequenceOfOctetString var0) {
      return new SspRange(0, var0);
   }

   public static SspRange all() {
      return new SspRange(1, DERNull.INSTANCE);
   }

   public static SspRange bitmapSspRange(BitmapSspRange var0) {
      return new SspRange(2, var0);
   }

   public SspRange(int var1, ASN1Encodable var2) {
      switch (var1) {
         case 0:
         case 1:
         case 2:
            this.choice = var1;
            this.sspRange = var2;
            return;
         default:
            throw new IllegalArgumentException("invalid choice value " + var1);
      }
   }

   private SspRange(ASN1TaggedObject var1) {
      this(var1.getTagNo(), var1.getExplicitBaseObject());
   }

   public static SspRange getInstance(Object var0) {
      if (var0 instanceof SspRange) {
         return (SspRange)var0;
      } else {
         return var0 != null ? new SspRange(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getSspRange() {
      return this.sspRange;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.sspRange);
   }
}
