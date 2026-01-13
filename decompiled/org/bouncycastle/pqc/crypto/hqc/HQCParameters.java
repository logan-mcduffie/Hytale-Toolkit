package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.pqc.crypto.KEMParameters;

public class HQCParameters implements KEMParameters {
   public static final HQCParameters hqc128 = new HQCParameters(
      "hqc-128",
      17669,
      46,
      384,
      16,
      31,
      15,
      66,
      75,
      4,
      243079,
      2241,
      2321,
      new int[]{89, 69, 153, 116, 176, 117, 111, 75, 73, 233, 242, 233, 65, 210, 21, 139, 103, 173, 67, 118, 105, 210, 174, 110, 74, 69, 228, 82, 255, 181, 1}
   );
   public static final HQCParameters hqc192 = new HQCParameters(
      "hqc-192",
      35851,
      56,
      640,
      24,
      33,
      16,
      100,
      114,
      5,
      119800,
      4514,
      4602,
      new int[]{
         45, 216, 239, 24, 253, 104, 27, 40, 107, 50, 163, 210, 227, 134, 224, 158, 119, 13, 158, 1, 238, 164, 82, 43, 15, 232, 246, 142, 50, 189, 29, 232, 1
      }
   );
   public static final HQCParameters hqc256 = new HQCParameters(
      "hqc-256",
      57637,
      90,
      640,
      32,
      59,
      29,
      131,
      149,
      5,
      74517,
      7237,
      7333,
      new int[]{
         49,
         167,
         49,
         39,
         200,
         121,
         124,
         91,
         240,
         63,
         148,
         71,
         150,
         123,
         87,
         101,
         32,
         215,
         159,
         71,
         201,
         115,
         97,
         210,
         186,
         183,
         141,
         217,
         123,
         12,
         31,
         243,
         180,
         219,
         152,
         239,
         99,
         141,
         4,
         246,
         191,
         144,
         8,
         232,
         47,
         27,
         141,
         178,
         130,
         64,
         124,
         47,
         39,
         188,
         216,
         48,
         199,
         187,
         1
      }
   );
   private final String name;
   private final int n;
   private final int n1;
   private final int n2;
   private final int publicKeyBytes;
   private final int secretKeyBytes;
   static final int PARAM_M = 8;
   static final int GF_MUL_ORDER = 255;
   private final HQCEngine hqcEngine;

   private HQCParameters(
      String var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int[] var14
   ) {
      this.name = var1;
      this.n = var2;
      this.n1 = var3;
      this.n2 = var4;
      this.publicKeyBytes = var12;
      this.secretKeyBytes = var13;
      this.hqcEngine = new HQCEngine(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var14);
   }

   int getSHA512_BYTES() {
      return 64;
   }

   int getSALT_SIZE_BYTES() {
      return 16;
   }

   int getN_BYTES() {
      return (this.n + 7) / 8;
   }

   int getN1N2_BYTES() {
      return (this.n1 * this.n2 + 7) / 8;
   }

   HQCEngine getEngine() {
      return this.hqcEngine;
   }

   public int getSessionKeySize() {
      return 256;
   }

   public String getName() {
      return this.name;
   }

   public int getPublicKeyBytes() {
      return this.publicKeyBytes;
   }

   public int getSecretKeyBytes() {
      return this.secretKeyBytes;
   }
}
