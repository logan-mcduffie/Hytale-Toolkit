package org.bouncycastle.util;

public class IPAddress {
   public static boolean isValid(String var0) {
      return isValidIPv4(var0) || isValidIPv6(var0);
   }

   public static boolean isValidWithNetMask(String var0) {
      return isValidIPv4WithNetmask(var0) || isValidIPv6WithNetmask(var0);
   }

   public static boolean isValidIPv4(String var0) {
      int var1 = var0.length();
      if (var1 >= 7 && var1 <= 15) {
         int var2 = 0;

         for (int var3 = 0; var3 < 3; var3++) {
            int var4 = var0.indexOf(46, var2);
            if (!isParseableIPv4Octet(var0, var2, var4)) {
               return false;
            }

            var2 = var4 + 1;
         }

         return isParseableIPv4Octet(var0, var2, var1);
      } else {
         return false;
      }
   }

   public static boolean isValidIPv4WithNetmask(String var0) {
      int var1 = var0.indexOf("/");
      if (var1 < 1) {
         return false;
      } else {
         String var2 = var0.substring(0, var1);
         String var3 = var0.substring(var1 + 1);
         return isValidIPv4(var2) && (isValidIPv4(var3) || isParseableIPv4Mask(var3));
      }
   }

   public static boolean isValidIPv6(String var0) {
      if (var0.length() == 0) {
         return false;
      } else {
         char var1 = var0.charAt(0);
         if (var1 != ':' && Character.digit(var1, 16) < 0) {
            return false;
         } else {
            int var2 = 0;
            String var3 = var0 + ":";
            boolean var4 = false;

            int var6;
            for (int var5 = 0; var5 < var3.length() && (var6 = var3.indexOf(58, var5)) >= var5; var2++) {
               if (var2 == 8) {
                  return false;
               }

               if (var5 != var6) {
                  String var7 = var3.substring(var5, var6);
                  if (var6 == var3.length() - 1 && var7.indexOf(46) > 0) {
                     if (++var2 == 8) {
                        return false;
                     }

                     if (!isValidIPv4(var7)) {
                        return false;
                     }
                  } else if (!isParseableIPv6Segment(var3, var5, var6)) {
                     return false;
                  }
               } else {
                  if (var6 != 1 && var6 != var3.length() - 1 && var4) {
                     return false;
                  }

                  var4 = true;
               }

               var5 = var6 + 1;
            }

            return var2 == 8 || var4;
         }
      }
   }

   public static boolean isValidIPv6WithNetmask(String var0) {
      int var1 = var0.indexOf("/");
      if (var1 < 1) {
         return false;
      } else {
         String var2 = var0.substring(0, var1);
         String var3 = var0.substring(var1 + 1);
         return isValidIPv6(var2) && (isValidIPv6(var3) || isParseableIPv6Mask(var3));
      }
   }

   private static boolean isParseableIPv4Mask(String var0) {
      return isParseable(var0, 0, var0.length(), 10, 2, false, 0, 32);
   }

   private static boolean isParseableIPv4Octet(String var0, int var1, int var2) {
      return isParseable(var0, var1, var2, 10, 3, true, 0, 255);
   }

   private static boolean isParseableIPv6Mask(String var0) {
      return isParseable(var0, 0, var0.length(), 10, 3, false, 1, 128);
   }

   private static boolean isParseableIPv6Segment(String var0, int var1, int var2) {
      return isParseable(var0, var1, var2, 16, 4, true, 0, 65535);
   }

   private static boolean isParseable(String var0, int var1, int var2, int var3, int var4, boolean var5, int var6, int var7) {
      int var8 = var2 - var1;
      if (var8 < 1 | var8 > var4) {
         return false;
      } else {
         boolean var9 = var8 > 1 & !var5;
         if (var9 && Character.digit(var0.charAt(var1), var3) <= 0) {
            return false;
         } else {
            int var10 = 0;

            while (var1 < var2) {
               char var11 = var0.charAt(var1++);
               int var12 = Character.digit(var11, var3);
               if (var12 < 0) {
                  return false;
               }

               var10 *= var3;
               var10 += var12;
            }

            return var10 >= var6 & var10 <= var7;
         }
      }
   }
}
