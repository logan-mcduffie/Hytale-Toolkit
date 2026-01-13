package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class Duration extends ASN1Object implements ASN1Choice {
   public static final int microseconds = 0;
   public static final int milliseconds = 1;
   public static final int seconds = 2;
   public static final int minutes = 3;
   public static final int hours = 4;
   public static final int sixtyHours = 5;
   public static final int years = 6;
   private final int choice;
   private final UINT16 duration;

   public Duration(int var1, UINT16 var2) {
      this.choice = var1;
      this.duration = var2;
   }

   private Duration(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
            try {
               this.duration = UINT16.getInstance(var1.getExplicitBaseObject());
               return;
            } catch (Exception var3) {
               throw new IllegalStateException(var3.getMessage(), var3);
            }
         default:
            throw new IllegalArgumentException("invalid choice value " + this.choice);
      }
   }

   public static Duration getInstance(Object var0) {
      if (var0 instanceof Duration) {
         return (Duration)var0;
      } else {
         return var0 != null ? new Duration(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public static Duration years(UINT16 var0) {
      return new Duration(6, var0);
   }

   public static Duration sixtyHours(UINT16 var0) {
      return new Duration(5, var0);
   }

   public static Duration hours(UINT16 var0) {
      return new Duration(4, var0);
   }

   public static Duration minutes(UINT16 var0) {
      return new Duration(3, var0);
   }

   public static Duration seconds(UINT16 var0) {
      return new Duration(2, var0);
   }

   public static Duration milliseconds(UINT16 var0) {
      return new Duration(1, var0);
   }

   public static Duration microseconds(UINT16 var0) {
      return new Duration(0, var0);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.duration);
   }

   public int getChoice() {
      return this.choice;
   }

   public UINT16 getDuration() {
      return this.duration;
   }

   @Override
   public String toString() {
      switch (this.choice) {
         case 0:
            return this.duration.value + "uS";
         case 1:
            return this.duration.value + "mS";
         case 2:
            return this.duration.value + " seconds";
         case 3:
            return this.duration.value + " minute";
         case 4:
            return this.duration.value + " hours";
         case 5:
            return this.duration.value + " sixty hours";
         case 6:
            return this.duration.value + " years";
         default:
            return this.duration.value + " unknown choice";
      }
   }
}
