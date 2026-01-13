package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;

public abstract class SerpentEngineBase implements BlockCipher {
   protected static final int BLOCK_SIZE = 16;
   static final int ROUNDS = 32;
   static final int PHI = -1640531527;
   protected boolean encrypting;
   protected int[] wKey;
   protected int keyBits;

   SerpentEngineBase() {
      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 256));
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var2 instanceof KeyParameter) {
         this.encrypting = var1;
         byte[] var3 = ((KeyParameter)var2).getKey();
         this.wKey = this.makeWorkingKey(var3);
         CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), var3.length * 8, var2, this.getPurpose()));
      } else {
         throw new IllegalArgumentException("invalid parameter passed to " + this.getAlgorithmName() + " init - " + var2.getClass().getName());
      }
   }

   @Override
   public String getAlgorithmName() {
      return "Serpent";
   }

   @Override
   public int getBlockSize() {
      return 16;
   }

   @Override
   public final int processBlock(byte[] var1, int var2, byte[] var3, int var4) {
      if (this.wKey == null) {
         throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
      } else if (var2 + 16 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 + 16 > var3.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         if (this.encrypting) {
            this.encryptBlock(var1, var2, var3, var4);
         } else {
            this.decryptBlock(var1, var2, var3, var4);
         }

         return 16;
      }
   }

   @Override
   public void reset() {
   }

   protected static int rotateLeft(int var0, int var1) {
      return var0 << var1 | var0 >>> -var1;
   }

   protected static int rotateRight(int var0, int var1) {
      return var0 >>> var1 | var0 << -var1;
   }

   protected final void sb0(int[] var1, int var2, int var3, int var4, int var5) {
      int var6 = var2 ^ var5;
      int var7 = var4 ^ var6;
      int var8 = var3 ^ var7;
      var1[3] = var2 & var5 ^ var8;
      int var9 = var2 ^ var3 & var6;
      var1[2] = var8 ^ (var4 | var9);
      int var10 = var1[3] & (var7 ^ var9);
      var1[1] = ~var7 ^ var10;
      var1[0] = var10 ^ ~var9;
   }

   protected final void ib0(int[] var1, int var2, int var3, int var4, int var5) {
      int var6 = ~var2;
      int var7 = var2 ^ var3;
      int var8 = var5 ^ (var6 | var7);
      int var9 = var4 ^ var8;
      var1[2] = var7 ^ var9;
      int var10 = var6 ^ var5 & var7;
      var1[1] = var8 ^ var1[2] & var10;
      var1[3] = var2 & var8 ^ (var9 | var1[1]);
      var1[0] = var1[3] ^ var9 ^ var10;
   }

   protected final void sb1(int[] var1, int var2, int var3, int var4, int var5) {
      int var6 = var3 ^ ~var2;
      int var7 = var4 ^ (var2 | var6);
      var1[2] = var5 ^ var7;
      int var8 = var3 ^ (var5 | var6);
      int var9 = var6 ^ var1[2];
      var1[3] = var9 ^ var7 & var8;
      int var10 = var7 ^ var8;
      var1[1] = var1[3] ^ var10;
      var1[0] = var7 ^ var9 & var10;
   }

   protected final void ib1(int[] var1, int var2, int var3, int var4, int var5) {
      int var6 = var3 ^ var5;
      int var7 = var2 ^ var3 & var6;
      int var8 = var6 ^ var7;
      var1[3] = var4 ^ var8;
      int var9 = var3 ^ var6 & var7;
      int var10 = var1[3] | var9;
      var1[1] = var7 ^ var10;
      int var11 = ~var1[1];
      int var12 = var1[3] ^ var9;
      var1[0] = var11 ^ var12;
      var1[2] = var8 ^ (var11 | var12);
   }

   protected final void sb2(int[] var1, int var2, int var3, int var4, int var5) {
      int var6 = ~var2;
      int var7 = var3 ^ var5;
      int var8 = var4 & var6;
      var1[0] = var7 ^ var8;
      int var9 = var4 ^ var6;
      int var10 = var4 ^ var1[0];
      int var11 = var3 & var10;
      var1[3] = var9 ^ var11;
      var1[2] = var2 ^ (var5 | var11) & (var1[0] | var9);
      var1[1] = var7 ^ var1[3] ^ var1[2] ^ (var5 | var6);
   }

   protected final void ib2(int[] var1, int var2, int var3, int var4, int var5) {
      int var6 = var3 ^ var5;
      int var7 = ~var6;
      int var8 = var2 ^ var4;
      int var9 = var4 ^ var6;
      int var10 = var3 & var9;
      var1[0] = var8 ^ var10;
      int var11 = var2 | var7;
      int var12 = var5 ^ var11;
      int var13 = var8 | var12;
      var1[3] = var6 ^ var13;
      int var14 = ~var9;
      int var15 = var1[0] | var1[3];
      var1[1] = var14 ^ var15;
      var1[2] = var5 & var14 ^ var8 ^ var15;
   }

   protected final void sb3(int[] var1, int var2, int var3, int var4, int var5) {
      int var6 = var2 ^ var3;
      int var7 = var2 & var4;
      int var8 = var2 | var5;
      int var9 = var4 ^ var5;
      int var10 = var6 & var8;
      int var11 = var7 | var10;
      var1[2] = var9 ^ var11;
      int var12 = var3 ^ var8;
      int var13 = var11 ^ var12;
      int var14 = var9 & var13;
      var1[0] = var6 ^ var14;
      int var15 = var1[2] & var1[0];
      var1[1] = var13 ^ var15;
      var1[3] = (var3 | var5) ^ var9 ^ var15;
   }

   protected final void ib3(int[] var1, int var2, int var3, int var4, int var5) {
      int var6 = var2 | var3;
      int var7 = var3 ^ var4;
      int var8 = var3 & var7;
      int var9 = var2 ^ var8;
      int var10 = var4 ^ var9;
      int var11 = var5 | var9;
      var1[0] = var7 ^ var11;
      int var12 = var7 | var11;
      int var13 = var5 ^ var12;
      var1[2] = var10 ^ var13;
      int var14 = var6 ^ var13;
      int var15 = var1[0] & var14;
      var1[3] = var9 ^ var15;
      var1[1] = var1[3] ^ var1[0] ^ var14;
   }

   protected final void sb4(int[] var1, int var2, int var3, int var4, int var5) {
      int var6 = var2 ^ var5;
      int var7 = var5 & var6;
      int var8 = var4 ^ var7;
      int var9 = var3 | var8;
      var1[3] = var6 ^ var9;
      int var10 = ~var3;
      int var11 = var6 | var10;
      var1[0] = var8 ^ var11;
      int var12 = var2 & var1[0];
      int var13 = var6 ^ var10;
      int var14 = var9 & var13;
      var1[2] = var12 ^ var14;
      var1[1] = var2 ^ var8 ^ var13 & var1[2];
   }

   protected final void ib4(int[] var1, int var2, int var3, int var4, int var5) {
      int var6 = var4 | var5;
      int var7 = var2 & var6;
      int var8 = var3 ^ var7;
      int var9 = var2 & var8;
      int var10 = var4 ^ var9;
      var1[1] = var5 ^ var10;
      int var11 = ~var2;
      int var12 = var10 & var1[1];
      var1[3] = var8 ^ var12;
      int var13 = var1[1] | var11;
      int var14 = var5 ^ var13;
      var1[0] = var1[3] ^ var14;
      var1[2] = var8 & var14 ^ var1[1] ^ var11;
   }

   protected final void sb5(int[] var1, int var2, int var3, int var4, int var5) {
      int var6 = ~var2;
      int var7 = var2 ^ var3;
      int var8 = var2 ^ var5;
      int var9 = var4 ^ var6;
      int var10 = var7 | var8;
      var1[0] = var9 ^ var10;
      int var11 = var5 & var1[0];
      int var12 = var7 ^ var1[0];
      var1[1] = var11 ^ var12;
      int var13 = var6 | var1[0];
      int var14 = var7 | var11;
      int var15 = var8 ^ var13;
      var1[2] = var14 ^ var15;
      var1[3] = var3 ^ var11 ^ var1[1] & var15;
   }

   protected final void ib5(int[] var1, int var2, int var3, int var4, int var5) {
      int var6 = ~var4;
      int var7 = var3 & var6;
      int var8 = var5 ^ var7;
      int var9 = var2 & var8;
      int var10 = var3 ^ var6;
      var1[3] = var9 ^ var10;
      int var11 = var3 | var1[3];
      int var12 = var2 & var11;
      var1[1] = var8 ^ var12;
      int var13 = var2 | var5;
      int var14 = var6 ^ var11;
      var1[0] = var13 ^ var14;
      var1[2] = var3 & var13 ^ (var9 | var2 ^ var4);
   }

   protected final void sb6(int[] var1, int var2, int var3, int var4, int var5) {
      int var6 = ~var2;
      int var7 = var2 ^ var5;
      int var8 = var3 ^ var7;
      int var9 = var6 | var7;
      int var10 = var4 ^ var9;
      var1[1] = var3 ^ var10;
      int var11 = var7 | var1[1];
      int var12 = var5 ^ var11;
      int var13 = var10 & var12;
      var1[2] = var8 ^ var13;
      int var14 = var10 ^ var12;
      var1[0] = var1[2] ^ var14;
      var1[3] = ~var10 ^ var8 & var14;
   }

   protected final void ib6(int[] var1, int var2, int var3, int var4, int var5) {
      int var6 = ~var2;
      int var7 = var2 ^ var3;
      int var8 = var4 ^ var7;
      int var9 = var4 | var6;
      int var10 = var5 ^ var9;
      var1[1] = var8 ^ var10;
      int var11 = var8 & var10;
      int var12 = var7 ^ var11;
      int var13 = var3 | var12;
      var1[3] = var10 ^ var13;
      int var14 = var3 | var1[3];
      var1[0] = var12 ^ var14;
      var1[2] = var5 & var6 ^ var8 ^ var14;
   }

   protected final void sb7(int[] var1, int var2, int var3, int var4, int var5) {
      int var6 = var3 ^ var4;
      int var7 = var4 & var6;
      int var8 = var5 ^ var7;
      int var9 = var2 ^ var8;
      int var10 = var5 | var6;
      int var11 = var9 & var10;
      var1[1] = var3 ^ var11;
      int var12 = var8 | var1[1];
      int var13 = var2 & var9;
      var1[3] = var6 ^ var13;
      int var14 = var9 ^ var12;
      int var15 = var1[3] & var14;
      var1[2] = var8 ^ var15;
      var1[0] = ~var14 ^ var1[3] & var1[2];
   }

   protected final void ib7(int[] var1, int var2, int var3, int var4, int var5) {
      int var6 = var4 | var2 & var3;
      int var7 = var5 & (var2 | var3);
      var1[3] = var6 ^ var7;
      int var8 = ~var5;
      int var9 = var3 ^ var7;
      int var10 = var9 | var1[3] ^ var8;
      var1[1] = var2 ^ var10;
      var1[0] = var4 ^ var9 ^ (var5 | var1[1]);
      var1[2] = var6 ^ var1[1] ^ var1[0] ^ var2 & var1[3];
   }

   protected final void LT(int[] var1) {
      int var2 = rotateLeft(var1[0], 13);
      int var3 = rotateLeft(var1[2], 3);
      int var4 = var1[1] ^ var2 ^ var3;
      int var5 = var1[3] ^ var3 ^ var2 << 3;
      var1[1] = rotateLeft(var4, 1);
      var1[3] = rotateLeft(var5, 7);
      var1[0] = rotateLeft(var2 ^ var1[1] ^ var1[3], 5);
      var1[2] = rotateLeft(var3 ^ var1[3] ^ var1[1] << 7, 22);
   }

   protected final void inverseLT(int[] var1) {
      int var2 = rotateRight(var1[2], 22) ^ var1[3] ^ var1[1] << 7;
      int var3 = rotateRight(var1[0], 5) ^ var1[1] ^ var1[3];
      int var4 = rotateRight(var1[3], 7);
      int var5 = rotateRight(var1[1], 1);
      var1[3] = var4 ^ var2 ^ var3 << 3;
      var1[1] = var5 ^ var3 ^ var2;
      var1[2] = rotateRight(var2, 3);
      var1[0] = rotateRight(var3, 13);
   }

   protected abstract int[] makeWorkingKey(byte[] var1);

   protected abstract void encryptBlock(byte[] var1, int var2, byte[] var3, int var4);

   protected abstract void decryptBlock(byte[] var1, int var2, byte[] var3, int var4);

   private CryptoServicePurpose getPurpose() {
      if (this.wKey == null) {
         return CryptoServicePurpose.ANY;
      } else {
         return this.encrypting ? CryptoServicePurpose.ENCRYPTION : CryptoServicePurpose.DECRYPTION;
      }
   }
}
