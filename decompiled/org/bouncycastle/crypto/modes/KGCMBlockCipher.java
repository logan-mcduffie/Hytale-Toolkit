package org.bouncycastle.crypto.modes;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.modes.kgcm.KGCMMultiplier;
import org.bouncycastle.crypto.modes.kgcm.Tables16kKGCMMultiplier_512;
import org.bouncycastle.crypto.modes.kgcm.Tables4kKGCMMultiplier_128;
import org.bouncycastle.crypto.modes.kgcm.Tables8kKGCMMultiplier_256;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class KGCMBlockCipher implements AEADBlockCipher {
   private static final int MIN_MAC_BITS = 64;
   private BlockCipher engine;
   private BufferedBlockCipher ctrEngine;
   private int macSize;
   private boolean forEncryption;
   private byte[] initialAssociatedText;
   private byte[] macBlock;
   private byte[] iv;
   private KGCMMultiplier multiplier;
   private long[] b;
   private final int blockSize;
   private KGCMBlockCipher.ExposedByteArrayOutputStream associatedText = new KGCMBlockCipher.ExposedByteArrayOutputStream();
   private KGCMBlockCipher.ExposedByteArrayOutputStream data = new KGCMBlockCipher.ExposedByteArrayOutputStream();

   private static KGCMMultiplier createDefaultMultiplier(int var0) {
      switch (var0) {
         case 16:
            return new Tables4kKGCMMultiplier_128();
         case 32:
            return new Tables8kKGCMMultiplier_256();
         case 64:
            return new Tables16kKGCMMultiplier_512();
         default:
            throw new IllegalArgumentException("Only 128, 256, and 512 -bit block sizes supported");
      }
   }

   public KGCMBlockCipher(BlockCipher var1) {
      this.engine = var1;
      this.ctrEngine = new BufferedBlockCipher(new KCTRBlockCipher(this.engine));
      this.macSize = -1;
      this.blockSize = this.engine.getBlockSize();
      this.initialAssociatedText = new byte[this.blockSize];
      this.iv = new byte[this.blockSize];
      this.multiplier = createDefaultMultiplier(this.blockSize);
      this.b = new long[this.blockSize >>> 3];
      this.macBlock = null;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      this.forEncryption = var1;
      KeyParameter var3;
      if (var2 instanceof AEADParameters) {
         AEADParameters var4 = (AEADParameters)var2;
         byte[] var5 = var4.getNonce();
         int var6 = this.iv.length - var5.length;
         Arrays.fill(this.iv, (byte)0);
         System.arraycopy(var5, 0, this.iv, var6, var5.length);
         this.initialAssociatedText = var4.getAssociatedText();
         int var7 = var4.getMacSize();
         if (var7 < 64 || var7 > this.blockSize << 3 || (var7 & 7) != 0) {
            throw new IllegalArgumentException("Invalid value for MAC size: " + var7);
         }

         this.macSize = var7 >>> 3;
         var3 = var4.getKey();
         if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
         }
      } else {
         if (!(var2 instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("Invalid parameter passed");
         }

         ParametersWithIV var8 = (ParametersWithIV)var2;
         byte[] var9 = var8.getIV();
         int var10 = this.iv.length - var9.length;
         Arrays.fill(this.iv, (byte)0);
         System.arraycopy(var9, 0, this.iv, var10, var9.length);
         this.initialAssociatedText = null;
         this.macSize = this.blockSize;
         var3 = (KeyParameter)var8.getParameters();
      }

      this.macBlock = new byte[this.blockSize];
      this.ctrEngine.init(true, new ParametersWithIV(var3, this.iv));
      this.engine.init(true, var3);
   }

   @Override
   public String getAlgorithmName() {
      return this.engine.getAlgorithmName() + "/KGCM";
   }

   @Override
   public BlockCipher getUnderlyingCipher() {
      return this.engine;
   }

   @Override
   public void processAADByte(byte var1) {
      this.associatedText.write(var1);
   }

   @Override
   public void processAADBytes(byte[] var1, int var2, int var3) {
      this.associatedText.write(var1, var2, var3);
   }

   private void processAAD(byte[] var1, int var2, int var3) {
      int var4 = var2;
      int var5 = var2 + var3;

      while (var4 < var5) {
         xorWithInput(this.b, var1, var4);
         this.multiplier.multiplyH(this.b);
         var4 += this.blockSize;
      }
   }

   @Override
   public int processByte(byte var1, byte[] var2, int var3) throws DataLengthException, IllegalStateException {
      this.data.write(var1);
      return 0;
   }

   @Override
   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException, IllegalStateException {
      if (var1.length < var2 + var3) {
         throw new DataLengthException("input buffer too short");
      } else {
         this.data.write(var1, var2, var3);
         return 0;
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws IllegalStateException, InvalidCipherTextException {
      int var3 = this.data.size();
      if (!this.forEncryption && var3 < this.macSize) {
         throw new InvalidCipherTextException("data too short");
      } else {
         byte[] var4 = new byte[this.blockSize];
         this.engine.processBlock(var4, 0, var4, 0);
         long[] var5 = new long[this.blockSize >>> 3];
         Pack.littleEndianToLong(var4, 0, var5);
         this.multiplier.init(var5);
         Arrays.fill(var4, (byte)0);
         Arrays.fill(var5, 0L);
         int var8 = this.associatedText.size();
         if (var8 > 0) {
            this.processAAD(this.associatedText.getBuffer(), 0, var8);
         }

         int var10;
         if (this.forEncryption) {
            if (var1.length - var2 - this.macSize < var3) {
               throw new OutputLengthException("Output buffer too short");
            }

            int var9 = this.ctrEngine.processBytes(this.data.getBuffer(), 0, var3, var1, var2);
            var10 = var9 + this.ctrEngine.doFinal(var1, var2 + var9);
            this.calculateMac(var1, var2, var3, var8);
         } else {
            int var6 = var3 - this.macSize;
            if (var1.length - var2 < var6) {
               throw new OutputLengthException("Output buffer too short");
            }

            this.calculateMac(this.data.getBuffer(), 0, var6, var8);
            int var11 = this.ctrEngine.processBytes(this.data.getBuffer(), 0, var6, var1, var2);
            var10 = var11 + this.ctrEngine.doFinal(var1, var2 + var11);
         }

         if (this.macBlock == null) {
            throw new IllegalStateException("mac is not calculated");
         } else if (this.forEncryption) {
            System.arraycopy(this.macBlock, 0, var1, var2 + var10, this.macSize);
            this.reset();
            return var10 + this.macSize;
         } else {
            byte[] var12 = new byte[this.macSize];
            System.arraycopy(this.data.getBuffer(), var3 - this.macSize, var12, 0, this.macSize);
            byte[] var7 = new byte[this.macSize];
            System.arraycopy(this.macBlock, 0, var7, 0, this.macSize);
            if (!Arrays.constantTimeAreEqual(var12, var7)) {
               throw new InvalidCipherTextException("mac verification failed");
            } else {
               this.reset();
               return var10;
            }
         }
      }
   }

   @Override
   public byte[] getMac() {
      byte[] var1 = new byte[this.macSize];
      System.arraycopy(this.macBlock, 0, var1, 0, this.macSize);
      return var1;
   }

   @Override
   public int getUpdateOutputSize(int var1) {
      return 0;
   }

   @Override
   public int getOutputSize(int var1) {
      int var2 = var1 + this.data.size();
      if (this.forEncryption) {
         return var2 + this.macSize;
      } else {
         return var2 < this.macSize ? 0 : var2 - this.macSize;
      }
   }

   @Override
   public void reset() {
      Arrays.fill(this.b, 0L);
      this.engine.reset();
      this.data.reset();
      this.associatedText.reset();
      if (this.initialAssociatedText != null) {
         this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
      }
   }

   private void calculateMac(byte[] var1, int var2, int var3, int var4) {
      int var5 = var2;
      int var6 = var2 + var3;

      while (var5 < var6) {
         xorWithInput(this.b, var1, var5);
         this.multiplier.multiplyH(this.b);
         var5 += this.blockSize;
      }

      long var7 = (var4 & 4294967295L) << 3;
      long var9 = (var3 & 4294967295L) << 3;
      this.b[0] = this.b[0] ^ var7;
      this.b[this.blockSize >>> 4] = this.b[this.blockSize >>> 4] ^ var9;
      this.macBlock = Pack.longToLittleEndian(this.b);
      this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
   }

   private static void xorWithInput(long[] var0, byte[] var1, int var2) {
      for (int var3 = 0; var3 < var0.length; var3++) {
         var0[var3] ^= Pack.littleEndianToLong(var1, var2);
         var2 += 8;
      }
   }

   private static class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
      public ExposedByteArrayOutputStream() {
      }

      public byte[] getBuffer() {
         return this.buf;
      }
   }
}
