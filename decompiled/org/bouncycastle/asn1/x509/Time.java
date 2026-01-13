package org.bouncycastle.asn1.x509;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.LocaleUtil;

public class Time extends ASN1Object implements ASN1Choice {
   ASN1Primitive time;

   public static Time getInstance(ASN1TaggedObject var0, boolean var1) {
      if (!var1) {
         throw new IllegalArgumentException("choice item must be explicitly tagged");
      } else {
         return getInstance(var0.getExplicitBaseObject());
      }
   }

   public Time(ASN1Primitive var1) {
      if (!(var1 instanceof ASN1UTCTime) && !(var1 instanceof ASN1GeneralizedTime)) {
         throw new IllegalArgumentException("unknown object passed to Time");
      } else {
         this.time = var1;
      }
   }

   public Time(Date var1) {
      SimpleTimeZone var2 = new SimpleTimeZone(0, "Z");
      SimpleDateFormat var3 = new SimpleDateFormat("yyyyMMddHHmmss", LocaleUtil.EN_Locale);
      var3.setTimeZone(var2);
      String var4 = var3.format(var1) + "Z";
      int var5 = Integer.parseInt(var4.substring(0, 4));
      if (var5 >= 1950 && var5 <= 2049) {
         this.time = new DERUTCTime(var4.substring(2));
      } else {
         this.time = new DERGeneralizedTime(var4);
      }
   }

   public Time(Date var1, Locale var2) {
      SimpleTimeZone var3 = new SimpleTimeZone(0, "Z");
      SimpleDateFormat var4 = new SimpleDateFormat("yyyyMMddHHmmss", var2);
      var4.setTimeZone(var3);
      String var5 = var4.format(var1) + "Z";
      int var6 = Integer.parseInt(var5.substring(0, 4));
      if (var6 >= 1950 && var6 <= 2049) {
         this.time = new DERUTCTime(var5.substring(2));
      } else {
         this.time = new DERGeneralizedTime(var5);
      }
   }

   public static Time getInstance(Object var0) {
      if (var0 == null || var0 instanceof Time) {
         return (Time)var0;
      } else if (var0 instanceof ASN1UTCTime) {
         return new Time((ASN1UTCTime)var0);
      } else if (var0 instanceof ASN1GeneralizedTime) {
         return new Time((ASN1GeneralizedTime)var0);
      } else {
         throw new IllegalArgumentException("unknown object in factory: " + var0.getClass().getName());
      }
   }

   public String getTime() {
      return this.time instanceof ASN1UTCTime ? ((ASN1UTCTime)this.time).getAdjustedTime() : ((ASN1GeneralizedTime)this.time).getTime();
   }

   public Date getDate() {
      try {
         return this.time instanceof ASN1UTCTime ? ((ASN1UTCTime)this.time).getAdjustedDate() : ((ASN1GeneralizedTime)this.time).getDate();
      } catch (ParseException var2) {
         throw new IllegalStateException("invalid date string: " + var2.getMessage());
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.time;
   }

   @Override
   public String toString() {
      return this.getTime();
   }
}
