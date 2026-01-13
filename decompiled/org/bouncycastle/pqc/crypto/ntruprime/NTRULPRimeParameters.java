package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.pqc.crypto.KEMParameters;

public class NTRULPRimeParameters implements KEMParameters {
   public static final NTRULPRimeParameters ntrulpr653 = new NTRULPRimeParameters("ntrulpr653", 653, 4621, 252, 289, 2175, 113, 2031, 290, 865, 897, 1125, 32);
   public static final NTRULPRimeParameters ntrulpr761 = new NTRULPRimeParameters("ntrulpr761", 761, 4591, 250, 292, 2156, 114, 2007, 287, 1007, 1039, 1294, 32);
   public static final NTRULPRimeParameters ntrulpr857 = new NTRULPRimeParameters("ntrulpr857", 857, 5167, 281, 329, 2433, 101, 2265, 324, 1152, 1184, 1463, 32);
   public static final NTRULPRimeParameters ntrulpr953 = new NTRULPRimeParameters("ntrulpr953", 953, 6343, 345, 404, 2997, 82, 2798, 400, 1317, 1349, 1652, 32);
   public static final NTRULPRimeParameters ntrulpr1013 = new NTRULPRimeParameters(
      "ntrulpr1013", 1013, 7177, 392, 450, 3367, 73, 3143, 449, 1423, 1455, 1773, 32
   );
   public static final NTRULPRimeParameters ntrulpr1277 = new NTRULPRimeParameters(
      "ntrulpr1277", 1277, 7879, 429, 502, 3724, 66, 3469, 496, 1815, 1847, 2231, 32
   );
   private final String name;
   private final int p;
   private final int q;
   private final int w;
   private final int delta;
   private final int tau0;
   private final int tau1;
   private final int tau2;
   private final int tau3;
   private final int roundedPolynomialBytes;
   private final int publicKeyBytes;
   private final int privateKeyBytes;
   private final int sharedKeyBytes;

   private NTRULPRimeParameters(
      String var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13
   ) {
      this.name = var1;
      this.p = var2;
      this.q = var3;
      this.w = var4;
      this.delta = var5;
      this.tau0 = var6;
      this.tau1 = var7;
      this.tau2 = var8;
      this.tau3 = var9;
      this.roundedPolynomialBytes = var10;
      this.publicKeyBytes = var11;
      this.privateKeyBytes = var12;
      this.sharedKeyBytes = var13;
   }

   public String getName() {
      return this.name;
   }

   public int getP() {
      return this.p;
   }

   public int getQ() {
      return this.q;
   }

   public int getW() {
      return this.w;
   }

   public int getDelta() {
      return this.delta;
   }

   public int getTau0() {
      return this.tau0;
   }

   public int getTau1() {
      return this.tau1;
   }

   public int getTau2() {
      return this.tau2;
   }

   public int getTau3() {
      return this.tau3;
   }

   public int getPublicKeyBytes() {
      return this.publicKeyBytes;
   }

   public int getPrivateKeyBytes() {
      return this.privateKeyBytes;
   }

   public int getRoundedPolynomialBytes() {
      return this.roundedPolynomialBytes;
   }

   public int getSessionKeySize() {
      return this.sharedKeyBytes * 8;
   }
}
