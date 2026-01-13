package org.bouncycastle.pqc.crypto.bike;

import org.bouncycastle.pqc.crypto.KEMParameters;

public class BIKEParameters implements KEMParameters {
   public static final BIKEParameters bike128 = new BIKEParameters("bike128", 12323, 142, 134, 256, 5, 3, 128);
   public static final BIKEParameters bike192 = new BIKEParameters("bike192", 24659, 206, 199, 256, 5, 3, 192);
   public static final BIKEParameters bike256 = new BIKEParameters("bike256", 40973, 274, 264, 256, 5, 3, 256);
   private String name;
   private int r;
   private int w;
   private int t;
   private int l;
   private int nbIter;
   private int tau;
   private final int defaultKeySize;
   private BIKEEngine bikeEngine;

   private BIKEParameters(String var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      this.name = var1;
      this.r = var2;
      this.w = var3;
      this.t = var4;
      this.l = var5;
      this.nbIter = var6;
      this.tau = var7;
      this.defaultKeySize = var8;
      this.bikeEngine = new BIKEEngine(var2, var3, var4, var5, var6, var7);
   }

   public int getR() {
      return this.r;
   }

   public int getRByte() {
      return (this.r + 7) / 8;
   }

   public int getLByte() {
      return this.l / 8;
   }

   public int getW() {
      return this.w;
   }

   public int getT() {
      return this.t;
   }

   public int getL() {
      return this.l;
   }

   public int getNbIter() {
      return this.nbIter;
   }

   public int getTau() {
      return this.tau;
   }

   public String getName() {
      return this.name;
   }

   public int getSessionKeySize() {
      return this.defaultKeySize;
   }

   BIKEEngine getEngine() {
      return this.bikeEngine;
   }
}
