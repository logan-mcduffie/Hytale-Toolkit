package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.modes.gcm.BasicGCMExponentiator;
import org.bouncycastle.crypto.modes.gcm.GCMExponentiator;
import org.bouncycastle.crypto.modes.gcm.GCMMultiplier;
import org.bouncycastle.crypto.modes.gcm.GCMUtil;
import org.bouncycastle.crypto.modes.gcm.Tables4kGCMMultiplier;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class GCMBlockCipher implements GCMModeCipher {
   private static final int BLOCK_SIZE = 16;
   private BlockCipher cipher;
   private GCMMultiplier multiplier;
   private GCMExponentiator exp;
   private boolean forEncryption;
   private boolean initialised;
   private int macSize;
   private byte[] lastKey;
   private byte[] nonce;
   private byte[] initialAssociatedText;
   private byte[] H;
   private byte[] J0;
   private byte[] bufBlock;
   private byte[] macBlock;
   private byte[] S;
   private byte[] S_at;
   private byte[] S_atPre;
   private byte[] counter;
   private int blocksRemaining;
   private int bufOff;
   private long totalLength;
   private byte[] atBlock;
   private int atBlockPos;
   private long atLength;
   private long atLengthPre;

   public static GCMModeCipher newInstance(BlockCipher var0) {
      return new GCMBlockCipher(var0);
   }

   public static GCMModeCipher newInstance(BlockCipher var0, GCMMultiplier var1) {
      return new GCMBlockCipher(var0, var1);
   }

   /** @deprecated */
   public GCMBlockCipher(BlockCipher var1) {
      this(var1, null);
   }

   /** @deprecated */
   public GCMBlockCipher(BlockCipher var1, GCMMultiplier var2) {
      if (var1.getBlockSize() != 16) {
         throw new IllegalArgumentException("cipher required with a block size of 16.");
      } else {
         if (var2 == null) {
            var2 = new Tables4kGCMMultiplier();
         }

         this.cipher = var1;
         this.multiplier = (GCMMultiplier)var2;
      }
   }

   @Override
   public BlockCipher getUnderlyingCipher() {
      return this.cipher;
   }

   @Override
   public String getAlgorithmName() {
      return this.cipher.getAlgorithmName() + "/GCM";
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      this.forEncryption = var1;
      this.macBlock = null;
      this.initialised = true;
      Object var4 = null;
      KeyParameter var3;
      if (var2 instanceof AEADParameters) {
         AEADParameters var5 = (AEADParameters)var2;
         var4 = var5.getNonce();
         this.initialAssociatedText = var5.getAssociatedText();
         int var6 = var5.getMacSize();
         if (var6 < 32 || var6 > 128 || var6 % 8 != 0) {
            throw new IllegalArgumentException("Invalid value for MAC size: " + var6);
         }

         this.macSize = var6 / 8;
         var3 = var5.getKey();
      } else {
         if (!(var2 instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("invalid parameters passed to GCM");
         }

         ParametersWithIV var8 = (ParametersWithIV)var2;
         var4 = var8.getIV();
         this.initialAssociatedText = null;
         this.macSize = 16;
         var3 = (KeyParameter)var8.getParameters();
      }

      int var9 = var1 ? 16 : 16 + this.macSize;
      this.bufBlock = new byte[var9];
      if (var4 != null && ((Object[])var4).length >= 1) {
         if (var1 && this.nonce != null && Arrays.areEqual(this.nonce, (byte[])var4)) {
            if (var3 == null) {
               throw new IllegalArgumentException("cannot reuse nonce for GCM encryption");
            }

            if (this.lastKey != null && Arrays.areEqual(this.lastKey, var3.getKey())) {
               throw new IllegalArgumentException("cannot reuse nonce for GCM encryption");
            }
         }

         this.nonce = (byte[])var4;
         if (var3 != null) {
            this.lastKey = var3.getKey();
         }

         if (var3 != null) {
            this.cipher.init(true, var3);
            this.H = new byte[16];
            this.cipher.processBlock(this.H, 0, this.H, 0);
            this.multiplier.init(this.H);
            this.exp = null;
         } else if (this.H == null) {
            throw new IllegalArgumentException("Key must be specified in initial init");
         }

         this.J0 = new byte[16];
         if (this.nonce.length == 12) {
            System.arraycopy(this.nonce, 0, this.J0, 0, this.nonce.length);
            this.J0[15] = 1;
         } else {
            this.gHASH(this.J0, this.nonce, this.nonce.length);
            byte[] var10 = new byte[16];
            Pack.longToBigEndian(this.nonce.length * 8L, var10, 8);
            this.gHASHBlock(this.J0, var10);
         }

         this.S = new byte[16];
         this.S_at = new byte[16];
         this.S_atPre = new byte[16];
         this.atBlock = new byte[16];
         this.atBlockPos = 0;
         this.atLength = 0L;
         this.atLengthPre = 0L;
         this.counter = Arrays.clone(this.J0);
         this.blocksRemaining = -2;
         this.bufOff = 0;
         this.totalLength = 0L;
         if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
         }
      } else {
         throw new IllegalArgumentException("IV must be at least 1 byte");
      }
   }

   @Override
   public byte[] getMac() {
      return this.macBlock == null ? new byte[this.macSize] : Arrays.clone(this.macBlock);
   }

   @Override
   public int getOutputSize(int var1) {
      int var2 = var1 + this.bufOff;
      if (this.forEncryption) {
         return var2 + this.macSize;
      } else {
         return var2 < this.macSize ? 0 : var2 - this.macSize;
      }
   }

   @Override
   public int getUpdateOutputSize(int var1) {
      int var2 = var1 + this.bufOff;
      if (!this.forEncryption) {
         if (var2 < this.macSize) {
            return 0;
         }

         var2 -= this.macSize;
      }

      return var2 - var2 % 16;
   }

   @Override
   public void processAADByte(byte var1) {
      this.checkStatus();
      this.atBlock[this.atBlockPos] = var1;
      if (++this.atBlockPos == 16) {
         this.gHASHBlock(this.S_at, this.atBlock);
         this.atBlockPos = 0;
         this.atLength += 16L;
      }
   }

   @Override
   public void processAADBytes(byte[] var1, int var2, int var3) {
      this.checkStatus();
      if (this.atBlockPos > 0) {
         int var4 = 16 - this.atBlockPos;
         if (var3 < var4) {
            System.arraycopy(var1, var2, this.atBlock, this.atBlockPos, var3);
            this.atBlockPos += var3;
            return;
         }

         System.arraycopy(var1, var2, this.atBlock, this.atBlockPos, var4);
         this.gHASHBlock(this.S_at, this.atBlock);
         this.atLength += 16L;
         var2 += var4;
         var3 -= var4;
      }

      int var5;
      for (var5 = var2 + var3 - 16; var2 <= var5; var2 += 16) {
         this.gHASHBlock(this.S_at, var1, var2);
         this.atLength += 16L;
      }

      this.atBlockPos = 16 + var5 - var2;
      System.arraycopy(var1, var2, this.atBlock, 0, this.atBlockPos);
   }

   private void initCipher() {
      if (this.atLength > 0L) {
         System.arraycopy(this.S_at, 0, this.S_atPre, 0, 16);
         this.atLengthPre = this.atLength;
      }

      if (this.atBlockPos > 0) {
         this.gHASHPartial(this.S_atPre, this.atBlock, 0, this.atBlockPos);
         this.atLengthPre = this.atLengthPre + this.atBlockPos;
      }

      if (this.atLengthPre > 0L) {
         System.arraycopy(this.S_atPre, 0, this.S, 0, 16);
      }
   }

   @Override
   public int processByte(byte var1, byte[] var2, int var3) throws DataLengthException {
      this.checkStatus();
      this.bufBlock[this.bufOff] = var1;
      if (++this.bufOff == this.bufBlock.length) {
         if (this.forEncryption) {
            this.encryptBlock(this.bufBlock, 0, var2, var3);
            this.bufOff = 0;
         } else {
            this.decryptBlock(this.bufBlock, 0, var2, var3);
            System.arraycopy(this.bufBlock, 16, this.bufBlock, 0, this.macSize);
            this.bufOff = this.macSize;
         }

         return 16;
      } else {
         return 0;
      }
   }

   @Override
   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException {
      this.checkStatus();
      if (var1.length - var2 < var3) {
         throw new DataLengthException("Input buffer too short");
      } else {
         if (var1 == var4 && Arrays.segmentsOverlap(var2, var3, var5, this.getUpdateOutputSize(var3))) {
            var1 = new byte[var3];
            System.arraycopy(var4, var2, var1, 0, var3);
            var2 = 0;
         }

         byte var6 = 0;
         if (this.forEncryption) {
            if (this.bufOff > 0) {
               int var7 = 16 - this.bufOff;
               if (var3 < var7) {
                  System.arraycopy(var1, var2, this.bufBlock, this.bufOff, var3);
                  this.bufOff += var3;
                  return 0;
               }

               System.arraycopy(var1, var2, this.bufBlock, this.bufOff, var7);
               this.encryptBlock(this.bufBlock, 0, var4, var5);
               var2 += var7;
               var3 -= var7;
               var6 = 16;
            }

            int var10;
            for (var10 = var2 + var3 - 16; var2 <= var10; var6 += 16) {
               this.encryptBlock(var1, var2, var4, var5 + var6);
               var2 += 16;
            }

            this.bufOff = 16 + var10 - var2;
            System.arraycopy(var1, var2, this.bufBlock, 0, this.bufOff);
         } else {
            int var11 = this.bufBlock.length - this.bufOff;
            if (var3 < var11) {
               System.arraycopy(var1, var2, this.bufBlock, this.bufOff, var3);
               this.bufOff += var3;
               return 0;
            }

            if (this.bufOff >= 16) {
               this.decryptBlock(this.bufBlock, 0, var4, var5);
               System.arraycopy(this.bufBlock, 16, this.bufBlock, 0, this.bufOff -= 16);
               var6 = 16;
               var11 += 16;
               if (var3 < var11) {
                  System.arraycopy(var1, var2, this.bufBlock, this.bufOff, var3);
                  this.bufOff += var3;
                  return var6;
               }
            }

            int var8 = var2 + var3 - this.bufBlock.length;
            var11 = 16 - this.bufOff;
            System.arraycopy(var1, var2, this.bufBlock, this.bufOff, var11);
            this.decryptBlock(this.bufBlock, 0, var4, var5 + var6);
            var2 += var11;

            for (var6 += 16; var2 <= var8; var6 += 16) {
               this.decryptBlock(var1, var2, var4, var5 + var6);
               var2 += 16;
            }

            this.bufOff = this.bufBlock.length + var8 - var2;
            System.arraycopy(var1, var2, this.bufBlock, 0, this.bufOff);
         }

         return var6;
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws IllegalStateException, InvalidCipherTextException {
      this.checkStatus();
      if (this.totalLength == 0L) {
         this.initCipher();
      }

      int var3 = this.bufOff;
      if (this.forEncryption) {
         if (var1.length - var2 < var3 + this.macSize) {
            throw new OutputLengthException("Output buffer too short");
         }
      } else {
         if (var3 < this.macSize) {
            throw new InvalidCipherTextException("data too short");
         }

         var3 -= this.macSize;
         if (var1.length - var2 < var3) {
            throw new OutputLengthException("Output buffer too short");
         }
      }

      if (var3 > 0) {
         this.processPartial(this.bufBlock, 0, var3, var1, var2);
      }

      this.atLength = this.atLength + this.atBlockPos;
      if (this.atLength > this.atLengthPre) {
         if (this.atBlockPos > 0) {
            this.gHASHPartial(this.S_at, this.atBlock, 0, this.atBlockPos);
         }

         if (this.atLengthPre > 0L) {
            GCMUtil.xor(this.S_at, this.S_atPre);
         }

         long var4 = this.totalLength * 8L + 127L >>> 7;
         byte[] var6 = new byte[16];
         if (this.exp == null) {
            this.exp = new BasicGCMExponentiator();
            this.exp.init(this.H);
         }

         this.exp.exponentiateX(var4, var6);
         GCMUtil.multiply(this.S_at, var6);
         GCMUtil.xor(this.S, this.S_at);
      }

      byte[] var8 = new byte[16];
      Pack.longToBigEndian(this.atLength * 8L, var8, 0);
      Pack.longToBigEndian(this.totalLength * 8L, var8, 8);
      this.gHASHBlock(this.S, var8);
      byte[] var5 = new byte[16];
      this.cipher.processBlock(this.J0, 0, var5, 0);
      GCMUtil.xor(var5, this.S);
      int var9 = var3;
      this.macBlock = new byte[this.macSize];
      System.arraycopy(var5, 0, this.macBlock, 0, this.macSize);
      if (this.forEncryption) {
         System.arraycopy(this.macBlock, 0, var1, var2 + this.bufOff, this.macSize);
         var9 = var3 + this.macSize;
      } else {
         byte[] var7 = new byte[this.macSize];
         System.arraycopy(this.bufBlock, var3, var7, 0, this.macSize);
         if (!Arrays.constantTimeAreEqual(this.macBlock, var7)) {
            throw new InvalidCipherTextException("mac check in GCM failed");
         }
      }

      this.reset(false);
      return var9;
   }

   @Override
   public void reset() {
      this.reset(true);
   }

   private void reset(boolean var1) {
      this.cipher.reset();
      this.S = new byte[16];
      this.S_at = new byte[16];
      this.S_atPre = new byte[16];
      this.atBlock = new byte[16];
      this.atBlockPos = 0;
      this.atLength = 0L;
      this.atLengthPre = 0L;
      this.counter = Arrays.clone(this.J0);
      this.blocksRemaining = -2;
      this.bufOff = 0;
      this.totalLength = 0L;
      if (this.bufBlock != null) {
         Arrays.fill(this.bufBlock, (byte)0);
      }

      if (var1) {
         this.macBlock = null;
      }

      if (this.forEncryption) {
         this.initialised = false;
      } else if (this.initialAssociatedText != null) {
         this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
      }
   }

   private void decryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      if (var3.length - var4 < 16) {
         throw new OutputLengthException("Output buffer too short");
      } else {
         if (this.totalLength == 0L) {
            this.initCipher();
         }

         byte[] var5 = new byte[16];
         this.getNextCTRBlock(var5);
         this.gHASHBlock(this.S, var1, var2);
         GCMUtil.xor(var5, 0, var1, var2, var3, var4);
         this.totalLength += 16L;
      }
   }

   private void encryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      if (var3.length - var4 < 16) {
         throw new OutputLengthException("Output buffer too short");
      } else {
         if (this.totalLength == 0L) {
            this.initCipher();
         }

         byte[] var5 = new byte[16];
         this.getNextCTRBlock(var5);
         GCMUtil.xor(var5, var1, var2);
         this.gHASHBlock(this.S, var5);
         System.arraycopy(var5, 0, var3, var4, 16);
         this.totalLength += 16L;
      }
   }

   private void processPartial(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      byte[] var6 = new byte[16];
      this.getNextCTRBlock(var6);
      if (this.forEncryption) {
         GCMUtil.xor(var1, var2, var6, 0, var3);
         this.gHASHPartial(this.S, var1, var2, var3);
      } else {
         this.gHASHPartial(this.S, var1, var2, var3);
         GCMUtil.xor(var1, var2, var6, 0, var3);
      }

      System.arraycopy(var1, var2, var4, var5, var3);
      this.totalLength += var3;
   }

   private void gHASH(byte[] var1, byte[] var2, int var3) {
      for (byte var4 = 0; var4 < var3; var4 += 16) {
         int var5 = Math.min(var3 - var4, 16);
         this.gHASHPartial(var1, var2, var4, var5);
      }
   }

   private void gHASHBlock(byte[] var1, byte[] var2) {
      GCMUtil.xor(var1, var2);
      this.multiplier.multiplyH(var1);
   }

   private void gHASHBlock(byte[] var1, byte[] var2, int var3) {
      GCMUtil.xor(var1, var2, var3);
      this.multiplier.multiplyH(var1);
   }

   private void gHASHPartial(byte[] var1, byte[] var2, int var3, int var4) {
      GCMUtil.xor(var1, var2, var3, var4);
      this.multiplier.multiplyH(var1);
   }

   private void getNextCTRBlock(byte[] var1) {
      if (this.blocksRemaining == 0) {
         throw new IllegalStateException("Attempt to process too many blocks");
      } else {
         this.blocksRemaining--;
         int var2 = 1;
         var2 += this.counter[15] & 255;
         this.counter[15] = (byte)var2;
         var2 >>>= 8;
         var2 += this.counter[14] & 255;
         this.counter[14] = (byte)var2;
         var2 >>>= 8;
         var2 += this.counter[13] & 255;
         this.counter[13] = (byte)var2;
         var2 >>>= 8;
         var2 += this.counter[12] & 255;
         this.counter[12] = (byte)var2;
         this.cipher.processBlock(this.counter, 0, var1, 0);
      }
   }

   private void checkStatus() {
      if (!this.initialised) {
         if (this.forEncryption) {
            throw new IllegalStateException("GCM cipher cannot be reused for encryption");
         } else {
            throw new IllegalStateException("GCM cipher needs to be initialised");
         }
      }
   }
}
