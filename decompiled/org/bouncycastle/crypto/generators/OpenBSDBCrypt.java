package org.bouncycastle.crypto.generators;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class OpenBSDBCrypt {
   private static final byte[] encodingTable = new byte[]{
      46,
      47,
      65,
      66,
      67,
      68,
      69,
      70,
      71,
      72,
      73,
      74,
      75,
      76,
      77,
      78,
      79,
      80,
      81,
      82,
      83,
      84,
      85,
      86,
      87,
      88,
      89,
      90,
      97,
      98,
      99,
      100,
      101,
      102,
      103,
      104,
      105,
      106,
      107,
      108,
      109,
      110,
      111,
      112,
      113,
      114,
      115,
      116,
      117,
      118,
      119,
      120,
      121,
      122,
      48,
      49,
      50,
      51,
      52,
      53,
      54,
      55,
      56,
      57
   };
   private static final byte[] decodingTable = new byte[128];
   private static final String defaultVersion = "2y";
   private static final Set<String> allowedVersions = new HashSet<>();

   private OpenBSDBCrypt() {
   }

   public static String generate(char[] var0, byte[] var1, int var2) {
      return generate("2y", var0, var1, var2);
   }

   public static String generate(byte[] var0, byte[] var1, int var2) {
      return generate("2y", var0, var1, var2);
   }

   public static String generate(String var0, char[] var1, byte[] var2, int var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Password required.");
      } else {
         return doGenerate(var0, Strings.toUTF8ByteArray(var1), var2, var3);
      }
   }

   public static String generate(String var0, byte[] var1, byte[] var2, int var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Password required.");
      } else {
         return doGenerate(var0, Arrays.clone(var1), var2, var3);
      }
   }

   private static String doGenerate(String var0, byte[] var1, byte[] var2, int var3) {
      if (!allowedVersions.contains(var0)) {
         throw new IllegalArgumentException("Version " + var0 + " is not accepted by this implementation.");
      } else if (var2 == null) {
         throw new IllegalArgumentException("Salt required.");
      } else if (var2.length != 16) {
         throw new DataLengthException("16 byte salt required: " + var2.length);
      } else if (var3 >= 4 && var3 <= 31) {
         byte[] var4 = new byte[var1.length >= 72 ? 72 : var1.length + 1];
         if (var4.length > var1.length) {
            System.arraycopy(var1, 0, var4, 0, var1.length);
         } else {
            System.arraycopy(var1, 0, var4, 0, var4.length);
         }

         Arrays.fill(var1, (byte)0);
         String var5 = createBcryptString(var0, var4, var2, var3);
         Arrays.fill(var4, (byte)0);
         return var5;
      } else {
         throw new IllegalArgumentException("Invalid cost factor.");
      }
   }

   public static boolean checkPassword(String var0, char[] var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Missing password.");
      } else {
         return doCheckPassword(var0, Strings.toUTF8ByteArray(var1));
      }
   }

   public static boolean checkPassword(String var0, byte[] var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Missing password.");
      } else {
         return doCheckPassword(var0, Arrays.clone(var1));
      }
   }

   private static boolean doCheckPassword(String var0, byte[] var1) {
      if (var0 == null) {
         throw new IllegalArgumentException("Missing bcryptString.");
      } else if (var0.charAt(1) != '2') {
         throw new IllegalArgumentException("not a Bcrypt string");
      } else {
         int var2 = var0.length();
         if (var2 == 60 || var2 == 59 && var0.charAt(2) == '$') {
            if (var0.charAt(2) == '$') {
               if (var0.charAt(0) != '$' || var0.charAt(5) != '$') {
                  throw new IllegalArgumentException("Invalid Bcrypt String format.");
               }
            } else if (var0.charAt(0) != '$' || var0.charAt(3) != '$' || var0.charAt(6) != '$') {
               throw new IllegalArgumentException("Invalid Bcrypt String format.");
            }

            String var3;
            byte var4;
            if (var0.charAt(2) == '$') {
               var3 = var0.substring(1, 2);
               var4 = 3;
            } else {
               var3 = var0.substring(1, 3);
               var4 = 4;
            }

            if (!allowedVersions.contains(var3)) {
               throw new IllegalArgumentException("Bcrypt version '" + var3 + "' is not supported by this implementation");
            } else {
               int var5 = 0;
               String var6 = var0.substring(var4, var4 + 2);

               try {
                  var5 = Integer.parseInt(var6);
               } catch (NumberFormatException var9) {
                  throw new IllegalArgumentException("Invalid cost factor: " + var6);
               }

               if (var5 >= 4 && var5 <= 31) {
                  byte[] var7 = decodeSaltString(var0.substring(var0.lastIndexOf(36) + 1, var2 - 31));
                  String var8 = doGenerate(var3, var1, var7, var5);
                  return Strings.constantTimeAreEqual(var0, var8);
               } else {
                  throw new IllegalArgumentException("Invalid cost factor: " + var5 + ", 4 < cost < 31 expected.");
               }
            }
         } else {
            throw new DataLengthException("Bcrypt String length: " + var2 + ", 60 required.");
         }
      }
   }

   private static String createBcryptString(String var0, byte[] var1, byte[] var2, int var3) {
      if (!allowedVersions.contains(var0)) {
         throw new IllegalArgumentException("Version " + var0 + " is not accepted by this implementation.");
      } else {
         StringBuilder var4 = new StringBuilder(60);
         var4.append('$');
         var4.append(var0);
         var4.append('$');
         var4.append(var3 < 10 ? "0" + var3 : Integer.toString(var3));
         var4.append('$');
         encodeData(var4, var2);
         byte[] var5 = BCrypt.generate(var1, var2, var3);
         encodeData(var4, var5);
         return var4.toString();
      }
   }

   private static void encodeData(StringBuilder var0, byte[] var1) {
      if (var1.length != 24 && var1.length != 16) {
         throw new DataLengthException("Invalid length: " + var1.length + ", 24 for key or 16 for salt expected");
      } else {
         boolean var2 = false;
         if (var1.length == 16) {
            var2 = true;
            byte[] var3 = new byte[18];
            System.arraycopy(var1, 0, var3, 0, var1.length);
            var1 = var3;
         } else {
            var1[var1.length - 1] = 0;
         }

         int var8 = var1.length;

         for (byte var7 = 0; var7 < var8; var7 += 3) {
            int var4 = var1[var7] & 255;
            int var5 = var1[var7 + 1] & 255;
            int var6 = var1[var7 + 2] & 255;
            var0.append((char)encodingTable[var4 >>> 2 & 63]);
            var0.append((char)encodingTable[(var4 << 4 | var5 >>> 4) & 63]);
            var0.append((char)encodingTable[(var5 << 2 | var6 >>> 6) & 63]);
            var0.append((char)encodingTable[var6 & 63]);
         }

         if (var2) {
            var0.setLength(var0.length() - 2);
         } else {
            var0.setLength(var0.length() - 1);
         }
      }
   }

   private static byte[] decodeSaltString(String var0) {
      char[] var1 = var0.toCharArray();
      ByteArrayOutputStream var2 = new ByteArrayOutputStream(16);
      if (var1.length != 22) {
         throw new DataLengthException("Invalid base64 salt length: " + var1.length + " , 22 required.");
      } else {
         for (int var7 = 0; var7 < var1.length; var7++) {
            char var8 = var1[var7];
            if (var8 > 'z' || var8 < '.' || var8 > '9' && var8 < 'A') {
               throw new IllegalArgumentException("Salt string contains invalid character: " + var8);
            }
         }

         char[] var12 = new char[24];
         System.arraycopy(var1, 0, var12, 0, var1.length);
         var1 = var12;
         int var13 = var12.length;

         for (byte var9 = 0; var9 < var13; var9 += 4) {
            byte var3 = decodingTable[var1[var9]];
            byte var4 = decodingTable[var1[var9 + 1]];
            byte var5 = decodingTable[var1[var9 + 2]];
            byte var6 = decodingTable[var1[var9 + 3]];
            var2.write(var3 << 2 | var4 >> 4);
            var2.write(var4 << 4 | var5 >> 2);
            var2.write(var5 << 6 | var6);
         }

         byte[] var14 = var2.toByteArray();
         byte[] var10 = new byte[16];
         System.arraycopy(var14, 0, var10, 0, var10.length);
         return var10;
      }
   }

   static {
      allowedVersions.add("2");
      allowedVersions.add("2x");
      allowedVersions.add("2a");
      allowedVersions.add("2y");
      allowedVersions.add("2b");

      for (int var0 = 0; var0 < decodingTable.length; var0++) {
         decodingTable[var0] = -1;
      }

      for (int var1 = 0; var1 < encodingTable.length; var1++) {
         decodingTable[encodingTable[var1]] = (byte)var1;
      }
   }
}
