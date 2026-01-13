package org.bouncycastle.util.encoders;

public class UTF8 {
   private static final byte C_ILL = 0;
   private static final byte C_CR1 = 1;
   private static final byte C_CR2 = 2;
   private static final byte C_CR3 = 3;
   private static final byte C_L2A = 4;
   private static final byte C_L3A = 5;
   private static final byte C_L3B = 6;
   private static final byte C_L3C = 7;
   private static final byte C_L4A = 8;
   private static final byte C_L4B = 9;
   private static final byte C_L4C = 10;
   private static final byte S_ERR = -2;
   private static final byte S_END = -1;
   private static final byte S_CS1 = 0;
   private static final byte S_CS2 = 16;
   private static final byte S_CS3 = 32;
   private static final byte S_P3A = 48;
   private static final byte S_P3B = 64;
   private static final byte S_P4A = 80;
   private static final byte S_P4B = 96;
   private static final short[] firstUnitTable = new short[128];
   private static final byte[] transitionTable = new byte[112];

   private static void fill(byte[] var0, int var1, int var2, byte var3) {
      for (int var4 = var1; var4 <= var2; var4++) {
         var0[var4] = var3;
      }
   }

   public static int transcodeToUTF16(byte[] var0, char[] var1) {
      return transcodeToUTF16(var0, 0, var0.length, var1);
   }

   public static int transcodeToUTF16(byte[] var0, int var1, int var2, char[] var3) {
      int var4 = var1;
      int var5 = 0;
      int var6 = var1 + var2;

      while (var4 < var6) {
         byte var7 = var0[var4++];
         if (var7 >= 0) {
            if (var5 >= var3.length) {
               return -1;
            }

            var3[var5++] = (char)var7;
         } else {
            short var8 = firstUnitTable[var7 & 127];
            int var9 = var8 >>> 8;
            byte var10 = (byte)var8;

            while (var10 >= 0) {
               if (var4 >= var6) {
                  return -1;
               }

               var7 = var0[var4++];
               var9 = var9 << 6 | var7 & 63;
               var10 = transitionTable[var10 + ((var7 & 255) >>> 4)];
            }

            if (var10 == -2) {
               return -1;
            }

            if (var9 <= 65535) {
               if (var5 >= var3.length) {
                  return -1;
               }

               var3[var5++] = (char)var9;
            } else {
               if (var5 >= var3.length - 1) {
                  return -1;
               }

               var3[var5++] = (char)(55232 + (var9 >>> 10));
               var3[var5++] = (char)(56320 | var9 & 1023);
            }
         }
      }

      return var5;
   }

   static {
      byte[] var0 = new byte[128];
      fill(var0, 0, 15, (byte)1);
      fill(var0, 16, 31, (byte)2);
      fill(var0, 32, 63, (byte)3);
      fill(var0, 64, 65, (byte)0);
      fill(var0, 66, 95, (byte)4);
      fill(var0, 96, 96, (byte)5);
      fill(var0, 97, 108, (byte)6);
      fill(var0, 109, 109, (byte)7);
      fill(var0, 110, 111, (byte)6);
      fill(var0, 112, 112, (byte)8);
      fill(var0, 113, 115, (byte)9);
      fill(var0, 116, 116, (byte)10);
      fill(var0, 117, 127, (byte)0);
      fill(transitionTable, 0, transitionTable.length - 1, (byte)-2);
      fill(transitionTable, 8, 11, (byte)-1);
      fill(transitionTable, 24, 27, (byte)0);
      fill(transitionTable, 40, 43, (byte)16);
      fill(transitionTable, 58, 59, (byte)0);
      fill(transitionTable, 72, 73, (byte)0);
      fill(transitionTable, 89, 91, (byte)16);
      fill(transitionTable, 104, 104, (byte)16);
      byte[] var1 = new byte[]{0, 0, 0, 0, 31, 15, 15, 15, 7, 7, 7};
      byte[] var2 = new byte[]{-2, -2, -2, -2, 0, 48, 16, 64, 80, 32, 96};

      for (int var3 = 0; var3 < 128; var3++) {
         byte var4 = var0[var3];
         int var5 = var3 & var1[var4];
         byte var6 = var2[var4];
         firstUnitTable[var3] = (short)(var5 << 8 | var6);
      }
   }
}
