package org.bouncycastle.asn1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.bouncycastle.util.Longs;

public class LocaleUtil {
   private static final Map localeCache = new HashMap();
   public static Locale EN_Locale = forEN();

   private static Locale forEN() {
      if ("en".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
         return Locale.getDefault();
      } else {
         Locale[] var0 = Locale.getAvailableLocales();

         for (int var1 = 0; var1 != var0.length; var1++) {
            if ("en".equalsIgnoreCase(var0[var1].getLanguage())) {
               return var0[var1];
            }
         }

         return Locale.getDefault();
      }
   }

   static Date epochAdjust(Date var0) throws ParseException {
      Locale var1 = Locale.getDefault();
      if (var1 == null) {
         return var0;
      } else {
         synchronized (localeCache) {
            Long var3 = (Long)localeCache.get(var1);
            if (var3 == null) {
               SimpleDateFormat var4 = new SimpleDateFormat("yyyyMMddHHmmssz");
               long var5 = var4.parse("19700101000000GMT+00:00").getTime();
               var3 = longValueOf(var5);
               localeCache.put(var1, var3);
            }

            return var3 != 0L ? new Date(var0.getTime() - var3) : var0;
         }
      }
   }

   private static Long longValueOf(long var0) {
      return Longs.valueOf(var0);
   }
}
