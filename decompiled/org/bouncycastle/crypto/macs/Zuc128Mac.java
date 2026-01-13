package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.Zuc128CoreEngine;

public final class Zuc128Mac implements Mac {
   private static final int TOPBIT = 128;
   private final Zuc128Mac.InternalZuc128Engine theEngine = new Zuc128Mac.InternalZuc128Engine();
   private int theMac;
   private final int[] theKeyStream = new int[2];
   private Zuc128CoreEngine theState;
   private int theWordIndex;
   private int theByteIndex;

   @Override
   public String getAlgorithmName() {
      return "Zuc128Mac";
   }

   @Override
   public int getMacSize() {
      return 4;
   }

   @Override
   public void init(CipherParameters var1) {
      this.theEngine.init(true, var1);
      this.theState = (Zuc128CoreEngine)this.theEngine.copy();
      this.initKeyStream();
   }

   private void initKeyStream() {
      this.theMac = 0;

      for (int var1 = 0; var1 < this.theKeyStream.length - 1; var1++) {
         this.theKeyStream[var1] = this.theEngine.createKeyStreamWord();
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

   private void updateMac(int var1) {
      this.theMac = this.theMac ^ this.getKeyStreamWord(var1);
   }

   private int getKeyStreamWord(int var1) {
      int var2 = this.theKeyStream[this.theWordIndex];
      if (var1 == 0) {
         return var2;
      } else {
         int var3 = this.theKeyStream[(this.theWordIndex + 1) % this.theKeyStream.length];
         return var2 << var1 | var3 >>> 32 - var1;
      }
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      for (int var4 = 0; var4 < var3; var4++) {
         this.update(var1[var2 + var4]);
      }
   }

   private int getFinalWord() {
      if (this.theByteIndex != 0) {
         return this.theEngine.createKeyStreamWord();
      } else {
         this.theWordIndex = (this.theWordIndex + 1) % this.theKeyStream.length;
         return this.theKeyStream[this.theWordIndex];
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      this.shift4NextByte();
      this.theMac = this.theMac ^ this.getKeyStreamWord(this.theByteIndex * 8);
      this.theMac = this.theMac ^ this.getFinalWord();
      Zuc128CoreEngine.encode32be(this.theMac, var1, var2);
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

   private static class InternalZuc128Engine extends Zuc128CoreEngine {
      private InternalZuc128Engine() {
      }

      int createKeyStreamWord() {
         return super.makeKeyStreamWord();
      }
   }
}
