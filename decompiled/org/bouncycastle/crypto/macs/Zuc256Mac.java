package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.Zuc256CoreEngine;

public final class Zuc256Mac implements Mac {
   private static final int TOPBIT = 128;
   private final Zuc256Mac.InternalZuc256Engine theEngine;
   private final int theMacLength;
   private final int[] theMac;
   private final int[] theKeyStream;
   private Zuc256CoreEngine theState;
   private int theWordIndex;
   private int theByteIndex;

   public Zuc256Mac(int var1) {
      this.theEngine = new Zuc256Mac.InternalZuc256Engine(var1);
      this.theMacLength = var1;
      int var2 = var1 / 32;
      this.theMac = new int[var2];
      this.theKeyStream = new int[var2 + 1];
   }

   @Override
   public String getAlgorithmName() {
      return "Zuc256Mac-" + this.theMacLength;
   }

   @Override
   public int getMacSize() {
      return this.theMacLength / 8;
   }

   @Override
   public void init(CipherParameters var1) {
      this.theEngine.init(true, var1);
      this.theState = (Zuc256CoreEngine)this.theEngine.copy();
      this.initKeyStream();
   }

   private void initKeyStream() {
      for (int var1 = 0; var1 < this.theMac.length; var1++) {
         this.theMac[var1] = this.theEngine.createKeyStreamWord();
      }

      for (int var2 = 0; var2 < this.theKeyStream.length - 1; var2++) {
         this.theKeyStream[var2] = this.theEngine.createKeyStreamWord();
      }

      this.theWordIndex = this.theKeyStream.length - 1;
      this.theByteIndex = 3;
   }

   @Override
   public void update(byte var1) {
      this.shift4NextByte();
      int var2 = this.theByteIndex * 8;
      short var3 = 128;

      for (int var4 = 0; var3 > 0; var4++) {
         if ((var1 & var3) != 0) {
            this.updateMac(var2 + var4);
         }

         var3 >>= 1;
      }
   }

   private void shift4NextByte() {
      this.theByteIndex = (this.theByteIndex + 1) % 4;
      if (this.theByteIndex == 0) {
         this.theKeyStream[this.theWordIndex] = this.theEngine.createKeyStreamWord();
         this.theWordIndex = (this.theWordIndex + 1) % this.theKeyStream.length;
      }
   }

   private void shift4Final() {
      this.theByteIndex = (this.theByteIndex + 1) % 4;
      if (this.theByteIndex == 0) {
         this.theWordIndex = (this.theWordIndex + 1) % this.theKeyStream.length;
      }
   }

   private void updateMac(int var1) {
      for (int var2 = 0; var2 < this.theMac.length; var2++) {
         this.theMac[var2] = this.theMac[var2] ^ this.getKeyStreamWord(var2, var1);
      }
   }

   private int getKeyStreamWord(int var1, int var2) {
      int var3 = this.theKeyStream[(this.theWordIndex + var1) % this.theKeyStream.length];
      if (var2 == 0) {
         return var3;
      } else {
         int var4 = this.theKeyStream[(this.theWordIndex + var1 + 1) % this.theKeyStream.length];
         return var3 << var2 | var4 >>> 32 - var2;
      }
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      for (int var4 = 0; var4 < var3; var4++) {
         this.update(var1[var2 + var4]);
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      this.shift4Final();
      this.updateMac(this.theByteIndex * 8);

      for (int var3 = 0; var3 < this.theMac.length; var3++) {
         Zuc256CoreEngine.encode32be(this.theMac[var3], var1, var2 + var3 * 4);
      }

      this.reset();
      return this.getMacSize();
   }

   @Override
   public void reset() {
      if (this.theState != null) {
         this.theEngine.reset(this.theState);
      }

      this.initKeyStream();
   }

   private static class InternalZuc256Engine extends Zuc256CoreEngine {
      public InternalZuc256Engine(int var1) {
         super(var1);
      }

      int createKeyStreamWord() {
         return super.makeKeyStreamWord();
      }
   }
}
