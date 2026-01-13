package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.util.Arrays;

public class BitmapSspRange extends ASN1Object {
   private final ASN1OctetString sspValue;
   private final ASN1OctetString sspBitMask;

   public BitmapSspRange(ASN1OctetString var1, ASN1OctetString var2) {
      this.sspValue = var1;
      this.sspBitMask = var2;
   }

   private BitmapSspRange(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         Iterator var2 = var1.iterator();
         this.sspValue = ASN1OctetString.getInstance(var2.next());
         this.sspBitMask = ASN1OctetString.getInstance(var2.next());
      }
   }

   public static BitmapSspRange getInstance(Object var0) {
      if (var0 instanceof BitmapSspRange) {
         return (BitmapSspRange)var0;
      } else {
         return var0 != null ? new BitmapSspRange(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1OctetString getSspValue() {
      return this.sspValue;
   }

   public ASN1OctetString getSspBitMask() {
      return this.sspBitMask;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.sspValue, this.sspBitMask);
   }

   public static BitmapSspRange.Builder builder() {
      return new BitmapSspRange.Builder();
   }

   public static class Builder {
      private ASN1OctetString sspValue;
      private ASN1OctetString sspBitMask;

      public BitmapSspRange.Builder setSspValue(ASN1OctetString var1) {
         this.sspValue = var1;
         return this;
      }

      public BitmapSspRange.Builder setSspBitMask(ASN1OctetString var1) {
         this.sspBitMask = var1;
         return this;
      }

      public BitmapSspRange.Builder setSspValue(byte[] var1) {
         this.sspValue = new DEROctetString(Arrays.clone(var1));
         return this;
      }

      public BitmapSspRange.Builder setSspBitMask(byte[] var1) {
         this.sspBitMask = new DEROctetString(Arrays.clone(var1));
         return this;
      }

      public BitmapSspRange createBitmapSspRange() {
         return new BitmapSspRange(this.sspValue, this.sspBitMask);
      }
   }
}
