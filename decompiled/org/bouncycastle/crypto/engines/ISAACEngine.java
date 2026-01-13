package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

public class ISAACEngine implements StreamCipher {
   private final int sizeL = 8;
   private final int stateArraySize = 256;
   private int[] engineState = null;
   private int[] results = null;
   private int a = 0;
   private int b = 0;
   private int c = 0;
   private int index = 0;
   private byte[] keyStream = new byte[1024];
   private byte[] workingKey = null;
   private boolean initialised = false;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (!(var2 instanceof KeyParameter)) {
         throw new IllegalArgumentException("invalid parameter passed to ISAAC init - " + var2.getClass().getName());
      } else {
         KeyParameter var3 = (KeyParameter)var2;
         byte[] var4 = var3.getKey();
         this.setKey(var4);
         CryptoServicesRegistrar.checkConstraints(
            new DefaultServiceProperties(this.getAlgorithmName(), var4.length < 32 ? var4.length * 8 : 256, var2, Utils.getPurpose(var1))
         );
      }
   }

   @Override
   public byte returnByte(byte var1) {
      if (this.index == 0) {
         this.isaac();
         this.keyStream = Pack.intToBigEndian(this.results);
      }

      byte var2 = (byte)(this.keyStream[this.index] ^ var1);
      this.index = this.index + 1 & 1023;
      return var2;
   }

   @Override
   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      if (!this.initialised) {
         throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
      } else if (var2 + var3 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var5 + var3 > var4.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         for (int var6 = 0; var6 < var3; var6++) {
            if (this.index == 0) {
               this.isaac();
               this.keyStream = Pack.intToBigEndian(this.results);
            }

            var4[var6 + var5] = (byte)(this.keyStream[this.index] ^ var1[var6 + var2]);
            this.index = this.index + 1 & 1023;
         }

         return var3;
      }
   }

   @Override
   public String getAlgorithmName() {
      return "ISAAC";
   }

   @Override
   public void reset() {
      this.setKey(this.workingKey);
   }

   private void setKey(byte[] var1) {
      this.workingKey = var1;
      if (this.engineState == null) {
         this.engineState = new int[256];
      }

      if (this.results == null) {
         this.results = new int[256];
      }

      for (int var2 = 0; var2 < 256; var2++) {
         this.engineState[var2] = this.results[var2] = 0;
      }

      this.a = this.b = this.c = 0;
      this.index = 0;
      byte[] var5 = new byte[var1.length + (var1.length & 3)];
      System.arraycopy(var1, 0, var5, 0, var1.length);

      for (byte var7 = 0; var7 < var5.length; var7 += 4) {
         this.results[var7 >>> 2] = Pack.littleEndianToInt(var5, var7);
      }

      int[] var6 = new int[8];

      for (int var8 = 0; var8 < 8; var8++) {
         var6[var8] = -1640531527;
      }

      for (int var9 = 0; var9 < 4; var9++) {
         this.mix(var6);
      }

      for (int var10 = 0; var10 < 2; var10++) {
         for (byte var3 = 0; var3 < 256; var3 += 8) {
            for (int var4 = 0; var4 < 8; var4++) {
               var6[var4] += var10 < 1 ? this.results[var3 + var4] : this.engineState[var3 + var4];
            }

            this.mix(var6);

            for (int var11 = 0; var11 < 8; var11++) {
               this.engineState[var3 + var11] = var6[var11];
            }
         }
      }

      this.isaac();
      this.initialised = true;
   }

   private void isaac() {
      this.b = this.b + ++this.c;

      for (int var1 = 0; var1 < 256; var1++) {
         int var2 = this.engineState[var1];
         switch (var1 & 3) {
            case 0:
               this.a = this.a ^ this.a << 13;
               break;
            case 1:
               this.a = this.a ^ this.a >>> 6;
               break;
            case 2:
               this.a = this.a ^ this.a << 2;
               break;
            case 3:
               this.a = this.a ^ this.a >>> 16;
         }

         this.a = this.a + this.engineState[var1 + 128 & 0xFF];
         int var3;
         this.engineState[var1] = var3 = this.engineState[var2 >>> 2 & 0xFF] + this.a + this.b;
         this.results[var1] = this.b = this.engineState[var3 >>> 10 & 0xFF] + var2;
      }
   }

   private void mix(int[] var1) {
      var1[0] ^= var1[1] << 11;
      var1[3] += var1[0];
      var1[1] += var1[2];
      var1[1] ^= var1[2] >>> 2;
      var1[4] += var1[1];
      var1[2] += var1[3];
      var1[2] ^= var1[3] << 8;
      var1[5] += var1[2];
      var1[3] += var1[4];
      var1[3] ^= var1[4] >>> 16;
      var1[6] += var1[3];
      var1[4] += var1[5];
      var1[4] ^= var1[5] << 10;
      var1[7] += var1[4];
      var1[5] += var1[6];
      var1[5] ^= var1[6] >>> 4;
      var1[0] += var1[5];
      var1[6] += var1[7];
      var1[6] ^= var1[7] << 8;
      var1[1] += var1[6];
      var1[7] += var1[0];
      var1[7] ^= var1[0] >>> 9;
      var1[2] += var1[7];
      var1[0] += var1[1];
   }
}
