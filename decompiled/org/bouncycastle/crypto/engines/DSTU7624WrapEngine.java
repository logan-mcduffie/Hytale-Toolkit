package org.bouncycastle.crypto.engines;

import java.util.ArrayList;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class DSTU7624WrapEngine implements Wrapper {
   private static final int BYTES_IN_INTEGER = 4;
   private boolean forWrapping;
   private DSTU7624Engine engine;
   private byte[] B;
   private byte[] intArray;
   private byte[] checkSumArray;
   private byte[] zeroArray;
   private ArrayList<byte[]> Btemp;

   public DSTU7624WrapEngine(int var1) {
      this.engine = new DSTU7624Engine(var1);
      this.B = new byte[this.engine.getBlockSize() / 2];
      this.checkSumArray = new byte[this.engine.getBlockSize()];
      this.zeroArray = new byte[this.engine.getBlockSize()];
      this.Btemp = new ArrayList<>();
      this.intArray = new byte[4];
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var2 instanceof ParametersWithRandom) {
         var2 = ((ParametersWithRandom)var2).getParameters();
      }

      this.forWrapping = var1;
      if (var2 instanceof KeyParameter) {
         this.engine.init(var1, var2);
      } else {
         throw new IllegalArgumentException("invalid parameters passed to DSTU7624WrapEngine");
      }
   }

   @Override
   public String getAlgorithmName() {
      return "DSTU7624WrapEngine";
   }

   @Override
   public byte[] wrap(byte[] var1, int var2, int var3) {
      if (!this.forWrapping) {
         throw new IllegalStateException("not set for wrapping");
      } else if (var3 % this.engine.getBlockSize() != 0) {
         throw new DataLengthException("wrap data must be a multiple of " + this.engine.getBlockSize() + " bytes");
      } else if (var2 + var3 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else {
         int var4 = 2 * (1 + var3 / this.engine.getBlockSize());
         int var5 = (var4 - 1) * 6;
         byte[] var6 = new byte[var3 + this.engine.getBlockSize()];
         System.arraycopy(var1, var2, var6, 0, var3);
         System.arraycopy(var6, 0, this.B, 0, this.engine.getBlockSize() / 2);
         this.Btemp.clear();
         int var7 = var6.length - this.engine.getBlockSize() / 2;

         for (int var8 = this.engine.getBlockSize() / 2; var7 != 0; var8 += this.engine.getBlockSize() / 2) {
            byte[] var9 = new byte[this.engine.getBlockSize() / 2];
            System.arraycopy(var6, var8, var9, 0, this.engine.getBlockSize() / 2);
            this.Btemp.add(var9);
            var7 -= this.engine.getBlockSize() / 2;
         }

         for (int var12 = 0; var12 < var5; var12++) {
            System.arraycopy(this.B, 0, var6, 0, this.engine.getBlockSize() / 2);
            System.arraycopy(this.Btemp.get(0), 0, var6, this.engine.getBlockSize() / 2, this.engine.getBlockSize() / 2);
            this.engine.processBlock(var6, 0, var6, 0);
            this.intToBytes(var12 + 1, this.intArray, 0);

            for (int var10 = 0; var10 < 4; var10++) {
               var6[var10 + this.engine.getBlockSize() / 2] ^= this.intArray[var10];
            }

            System.arraycopy(var6, this.engine.getBlockSize() / 2, this.B, 0, this.engine.getBlockSize() / 2);

            for (int var14 = 2; var14 < var4; var14++) {
               System.arraycopy(this.Btemp.get(var14 - 1), 0, this.Btemp.get(var14 - 2), 0, this.engine.getBlockSize() / 2);
            }

            System.arraycopy(var6, 0, this.Btemp.get(var4 - 2), 0, this.engine.getBlockSize() / 2);
         }

         System.arraycopy(this.B, 0, var6, 0, this.engine.getBlockSize() / 2);
         int var11 = this.engine.getBlockSize() / 2;

         for (int var13 = 0; var13 < var4 - 1; var13++) {
            System.arraycopy(this.Btemp.get(var13), 0, var6, var11, this.engine.getBlockSize() / 2);
            var11 += this.engine.getBlockSize() / 2;
         }

         return var6;
      }
   }

   @Override
   public byte[] unwrap(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      if (this.forWrapping) {
         throw new IllegalStateException("not set for unwrapping");
      } else if (var3 % this.engine.getBlockSize() != 0) {
         throw new DataLengthException("unwrap data must be a multiple of " + this.engine.getBlockSize() + " bytes");
      } else {
         int var4 = 2 * var3 / this.engine.getBlockSize();
         int var5 = (var4 - 1) * 6;
         byte[] var6 = new byte[var3];
         System.arraycopy(var1, var2, var6, 0, var3);
         byte[] var7 = new byte[this.engine.getBlockSize() / 2];
         System.arraycopy(var6, 0, var7, 0, this.engine.getBlockSize() / 2);
         this.Btemp.clear();
         int var8 = var6.length - this.engine.getBlockSize() / 2;

         for (int var9 = this.engine.getBlockSize() / 2; var8 != 0; var9 += this.engine.getBlockSize() / 2) {
            byte[] var10 = new byte[this.engine.getBlockSize() / 2];
            System.arraycopy(var6, var9, var10, 0, this.engine.getBlockSize() / 2);
            this.Btemp.add(var10);
            var8 -= this.engine.getBlockSize() / 2;
         }

         for (int var13 = 0; var13 < var5; var13++) {
            System.arraycopy(this.Btemp.get(var4 - 2), 0, var6, 0, this.engine.getBlockSize() / 2);
            System.arraycopy(var7, 0, var6, this.engine.getBlockSize() / 2, this.engine.getBlockSize() / 2);
            this.intToBytes(var5 - var13, this.intArray, 0);

            for (int var11 = 0; var11 < 4; var11++) {
               var6[var11 + this.engine.getBlockSize() / 2] ^= this.intArray[var11];
            }

            this.engine.processBlock(var6, 0, var6, 0);
            System.arraycopy(var6, 0, var7, 0, this.engine.getBlockSize() / 2);

            for (int var16 = 2; var16 < var4; var16++) {
               System.arraycopy(this.Btemp.get(var4 - var16 - 1), 0, this.Btemp.get(var4 - var16), 0, this.engine.getBlockSize() / 2);
            }

            System.arraycopy(var6, this.engine.getBlockSize() / 2, this.Btemp.get(0), 0, this.engine.getBlockSize() / 2);
         }

         System.arraycopy(var7, 0, var6, 0, this.engine.getBlockSize() / 2);
         int var12 = this.engine.getBlockSize() / 2;

         for (int var14 = 0; var14 < var4 - 1; var14++) {
            System.arraycopy(this.Btemp.get(var14), 0, var6, var12, this.engine.getBlockSize() / 2);
            var12 += this.engine.getBlockSize() / 2;
         }

         System.arraycopy(var6, var6.length - this.engine.getBlockSize(), this.checkSumArray, 0, this.engine.getBlockSize());
         byte[] var15 = new byte[var6.length - this.engine.getBlockSize()];
         if (!Arrays.areEqual(this.checkSumArray, this.zeroArray)) {
            throw new InvalidCipherTextException("checksum failed");
         } else {
            System.arraycopy(var6, 0, var15, 0, var6.length - this.engine.getBlockSize());
            return var15;
         }
      }
   }

   private void intToBytes(int var1, byte[] var2, int var3) {
      var2[var3 + 3] = (byte)(var1 >> 24);
      var2[var3 + 2] = (byte)(var1 >> 16);
      var2[var3 + 1] = (byte)(var1 >> 8);
      var2[var3] = (byte)var1;
   }
}
