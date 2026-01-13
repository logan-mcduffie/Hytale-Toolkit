package org.bouncycastle.asn1;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class ASN1UTCTime extends ASN1Primitive {
   static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1UTCTime.class, 23) {
      @Override
      ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
         return ASN1UTCTime.createPrimitive(var1.getOctets());
      }
   };
   final byte[] contents;

   public static ASN1UTCTime getInstance(Object var0) {
      if (var0 != null && !(var0 instanceof ASN1UTCTime)) {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1UTCTime) {
               return (ASN1UTCTime)var1;
            }
         }

         if (var0 instanceof byte[]) {
            try {
               return (ASN1UTCTime)TYPE.fromByteArray((byte[])var0);
            } catch (Exception var2) {
               throw new IllegalArgumentException("encoding error in getInstance: " + var2.toString());
            }
         } else {
            throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
         }
      } else {
         return (ASN1UTCTime)var0;
      }
   }

   public static ASN1UTCTime getInstance(ASN1TaggedObject var0, boolean var1) {
      return (ASN1UTCTime)TYPE.getContextTagged(var0, var1);
   }

   public static ASN1UTCTime getTagged(ASN1TaggedObject var0, boolean var1) {
      return (ASN1UTCTime)TYPE.getTagged(var0, var1);
   }

   public ASN1UTCTime(String var1) {
      this.contents = Strings.toByteArray(var1);

      try {
         this.getDate();
      } catch (ParseException var3) {
         throw new IllegalArgumentException("invalid date string: " + var3.getMessage());
      }
   }

   public ASN1UTCTime(Date var1) {
      SimpleDateFormat var2 = new SimpleDateFormat("yyMMddHHmmss'Z'", LocaleUtil.EN_Locale);
      var2.setTimeZone(new SimpleTimeZone(0, "Z"));
      this.contents = Strings.toByteArray(var2.format(var1));
   }

   public ASN1UTCTime(Date var1, Locale var2) {
      SimpleDateFormat var3 = new SimpleDateFormat("yyMMddHHmmss'Z'", var2);
      var3.setTimeZone(new SimpleTimeZone(0, "Z"));
      this.contents = Strings.toByteArray(var3.format(var1));
   }

   ASN1UTCTime(byte[] var1) {
      if (var1.length < 2) {
         throw new IllegalArgumentException("UTCTime string too short");
      } else {
         this.contents = var1;
         if (!this.isDigit(0) || !this.isDigit(1)) {
            throw new IllegalArgumentException("illegal characters in UTCTime string");
         }
      }
   }

   public Date getDate() throws ParseException {
      SimpleDateFormat var1 = new SimpleDateFormat("yyMMddHHmmssz", LocaleUtil.EN_Locale);
      return var1.parse(this.getTime());
   }

   public Date getAdjustedDate() throws ParseException {
      SimpleDateFormat var1 = new SimpleDateFormat("yyyyMMddHHmmssz", LocaleUtil.EN_Locale);
      var1.setTimeZone(new SimpleTimeZone(0, "Z"));
      return var1.parse(this.getAdjustedTime());
   }

   public String getTime() {
      String var1 = Strings.fromByteArray(this.contents);
      if (var1.indexOf(45) < 0 && var1.indexOf(43) < 0) {
         return var1.length() == 11 ? var1.substring(0, 10) + "00GMT+00:00" : var1.substring(0, 12) + "GMT+00:00";
      } else {
         int var2 = var1.indexOf(45);
         if (var2 < 0) {
            var2 = var1.indexOf(43);
         }

         String var3 = var1;
         if (var2 == var1.length() - 3) {
            var3 = var1 + "00";
         }

         return var2 == 10
            ? var3.substring(0, 10) + "00GMT" + var3.substring(10, 13) + ":" + var3.substring(13, 15)
            : var3.substring(0, 12) + "GMT" + var3.substring(12, 15) + ":" + var3.substring(15, 17);
      }
   }

   public String getAdjustedTime() {
      String var1 = this.getTime();
      return var1.charAt(0) < '5' ? "20" + var1 : "19" + var1;
   }

   private boolean isDigit(int var1) {
      return this.contents.length > var1 && this.contents[var1] >= 48 && this.contents[var1] <= 57;
   }

   @Override
   final boolean encodeConstructed() {
      return false;
   }

   @Override
   int encodedLength(boolean var1) {
      return ASN1OutputStream.getLengthOfEncodingDL(var1, this.contents.length);
   }

   @Override
   void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      var1.writeEncodingDL(var2, 23, this.contents);
   }

   @Override
   boolean asn1Equals(ASN1Primitive var1) {
      return !(var1 instanceof ASN1UTCTime) ? false : Arrays.areEqual(this.contents, ((ASN1UTCTime)var1).contents);
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.contents);
   }

   @Override
   public String toString() {
      return Strings.fromByteArray(this.contents);
   }

   static ASN1UTCTime createPrimitive(byte[] var0) {
      return new ASN1UTCTime(var0);
   }
}
