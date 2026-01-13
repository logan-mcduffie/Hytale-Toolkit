package org.bouncycastle.asn1.eac;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import org.bouncycastle.util.Arrays;

public class PackedDate {
   private byte[] time;

   public PackedDate(String var1) {
      this.time = this.convert(var1);
   }

   public PackedDate(Date var1) {
      SimpleDateFormat var2 = new SimpleDateFormat("yyMMdd'Z'");
      var2.setTimeZone(new SimpleTimeZone(0, "Z"));
      this.time = this.convert(var2.format(var1));
   }

   public PackedDate(Date var1, Locale var2) {
      SimpleDateFormat var3 = new SimpleDateFormat("yyMMdd'Z'", var2);
      var3.setTimeZone(new SimpleTimeZone(0, "Z"));
      this.time = this.convert(var3.format(var1));
   }

   private byte[] convert(String var1) {
      char[] var2 = var1.toCharArray();
      byte[] var3 = new byte[6];

      for (int var4 = 0; var4 != 6; var4++) {
         var3[var4] = (byte)(var2[var4] - '0');
      }

      return var3;
   }

   PackedDate(byte[] var1) {
      this.time = var1;
   }

   public Date getDate() throws ParseException {
      SimpleDateFormat var1 = new SimpleDateFormat("yyyyMMdd");
      return var1.parse("20" + this.toString());
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.time);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof PackedDate)) {
         return false;
      } else {
         PackedDate var2 = (PackedDate)var1;
         return Arrays.areEqual(this.time, var2.time);
      }
   }

   @Override
   public String toString() {
      char[] var1 = new char[this.time.length];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = (char)((this.time[var2] & 255) + 48);
      }

      return new String(var1);
   }

   public byte[] getEncoding() {
      return Arrays.clone(this.time);
   }
}
