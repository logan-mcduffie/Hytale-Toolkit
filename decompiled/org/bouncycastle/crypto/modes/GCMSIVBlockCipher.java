package org.bouncycastle.crypto.modes;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.gcm.GCMMultiplier;
import org.bouncycastle.crypto.modes.gcm.Tables4kGCMMultiplier;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class GCMSIVBlockCipher implements AEADBlockCipher {
   private static final int BUFLEN = 16;
   private static final int HALFBUFLEN = 8;
   private static final int NONCELEN = 12;
   private static final int MAX_DATALEN = 2147483623;
   private static final byte MASK = -128;
   private static final byte ADD = -31;
   private static final int INIT = 1;
   private static final int AEAD_COMPLETE = 2;
   private final BlockCipher theCipher;
   private final GCMMultiplier theMultiplier;
   private final byte[] theGHash = new byte[16];
   private final byte[] theReverse = new byte[16];
   private final GCMSIVBlockCipher.GCMSIVHasher theAEADHasher;
   private final GCMSIVBlockCipher.GCMSIVHasher theDataHasher;
   private GCMSIVBlockCipher.GCMSIVCache thePlain;
   private GCMSIVBlockCipher.GCMSIVCache theEncData;
   private boolean forEncryption;
   private byte[] theInitialAEAD;
   private byte[] theNonce;
   private int theFlags;
   private byte[] macBlock = new byte[16];

   public GCMSIVBlockCipher() {
      this(AESEngine.newInstance());
   }

   public GCMSIVBlockCipher(BlockCipher var1) {
      this(var1, new Tables4kGCMMultiplier());
   }

   public GCMSIVBlockCipher(BlockCipher var1, GCMMultiplier var2) {
      if (var1.getBlockSize() != 16) {
         throw new IllegalArgumentException("Cipher required with a block size of 16.");
      } else {
         this.theCipher = var1;
         this.theMultiplier = var2;
         this.theAEADHasher = new GCMSIVBlockCipher.GCMSIVHasher();
         this.theDataHasher = new GCMSIVBlockCipher.GCMSIVHasher();
      }
   }

   @Override
   public BlockCipher getUnderlyingCipher() {
      return this.theCipher;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      byte[] var3 = null;
      Object var4 = null;
      Object var5 = null;
      if (var2 instanceof AEADParameters) {
         AEADParameters var6 = (AEADParameters)var2;
         var3 = var6.getAssociatedText();
         var4 = var6.getNonce();
         var5 = var6.getKey();
      } else {
         if (!(var2 instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("invalid parameters passed to GCM-SIV");
         }

         ParametersWithIV var9 = (ParametersWithIV)var2;
         var4 = var9.getIV();
         var5 = (KeyParameter)var9.getParameters();
      }

      if (var4 != null && ((Object[])var4).length == 12) {
         if (var5 != null && (((KeyParameter)var5).getKeyLength() == 16 || ((KeyParameter)var5).getKeyLength() == 32)) {
            this.forEncryption = var1;
            this.theInitialAEAD = var3;
            this.theNonce = (byte[])var4;
            this.deriveKeys((KeyParameter)var5);
            this.resetStreams();
         } else {
            throw new IllegalArgumentException("Invalid key");
         }
      } else {
         throw new IllegalArgumentException("Invalid nonce");
      }
   }

   @Override
   public String getAlgorithmName() {
      return this.theCipher.getAlgorithmName() + "-GCM-SIV";
   }

   private void checkAEADStatus(int var1) {
      if ((this.theFlags & 1) == 0) {
         throw new IllegalStateException("Cipher is not initialised");
      } else if ((this.theFlags & 2) != 0) {
         throw new IllegalStateException("AEAD data cannot be processed after ordinary data");
      } else if (this.theAEADHasher.getBytesProcessed() + Long.MIN_VALUE > 2147483623 - var1 + Long.MIN_VALUE) {
         throw new IllegalStateException("AEAD byte count exceeded");
      }
   }

   private void checkStatus(int var1) {
      if ((this.theFlags & 1) == 0) {
         throw new IllegalStateException("Cipher is not initialised");
      } else {
         if ((this.theFlags & 2) == 0) {
            this.theAEADHasher.completeHash();
            this.theFlags |= 2;
         }

         long var2 = 2147483623L;
         long var4 = this.thePlain.size();
         if (!this.forEncryption) {
            var2 += 16L;
            var4 = this.theEncData.size();
         }

         if (var4 + Long.MIN_VALUE > var2 - var1 + Long.MIN_VALUE) {
            throw new IllegalStateException("byte count exceeded");
         }
      }
   }

   @Override
   public void processAADByte(byte var1) {
      this.checkAEADStatus(1);
      this.theAEADHasher.updateHash(var1);
   }

   @Override
   public void processAADBytes(byte[] var1, int var2, int var3) {
      this.checkAEADStatus(var3);
      checkBuffer(var1, var2, var3, false);
      this.theAEADHasher.updateHash(var1, var2, var3);
   }

   @Override
   public int processByte(byte var1, byte[] var2, int var3) throws DataLengthException {
      this.checkStatus(1);
      if (this.forEncryption) {
         this.thePlain.write(var1);
         this.theDataHasher.updateHash(var1);
      } else {
         this.theEncData.write(var1);
      }

      return 0;
   }

   @Override
   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException {
      this.checkStatus(var3);
      checkBuffer(var1, var2, var3, false);
      if (this.forEncryption) {
         this.thePlain.write(var1, var2, var3);
         this.theDataHasher.updateHash(var1, var2, var3);
      } else {
         this.theEncData.write(var1, var2, var3);
      }

      return 0;
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws IllegalStateException, InvalidCipherTextException {
      this.checkStatus(0);
      checkBuffer(var1, var2, this.getOutputSize(0), true);
      if (this.forEncryption) {
         byte[] var5 = this.calculateTag();
         int var6 = 16 + this.encryptPlain(var5, var1, var2);
         System.arraycopy(var5, 0, var1, var2 + this.thePlain.size(), 16);
         System.arraycopy(var5, 0, this.macBlock, 0, this.macBlock.length);
         this.resetStreams();
         return var6;
      } else {
         this.decryptPlain();
         int var3 = this.thePlain.size();
         byte[] var4 = this.thePlain.getBuffer();
         System.arraycopy(var4, 0, var1, var2, var3);
         this.resetStreams();
         return var3;
      }
   }

   @Override
   public byte[] getMac() {
      return Arrays.clone(this.macBlock);
   }

   @Override
   public int getUpdateOutputSize(int var1) {
      return 0;
   }

   @Override
   public int getOutputSize(int var1) {
      if (this.forEncryption) {
         return var1 + this.thePlain.size() + 16;
      } else {
         int var2 = var1 + this.theEncData.size();
         return var2 > 16 ? var2 - 16 : 0;
      }
   }

   @Override
   public void reset() {
      this.resetStreams();
   }

   private void resetStreams() {
      if (this.thePlain != null) {
         this.thePlain.clearBuffer();
      }

      this.theAEADHasher.reset();
      this.theDataHasher.reset();
      this.thePlain = new GCMSIVBlockCipher.GCMSIVCache();
      this.theEncData = this.forEncryption ? null : new GCMSIVBlockCipher.GCMSIVCache();
      this.theFlags &= -3;
      Arrays.fill(this.theGHash, (byte)0);
      if (this.theInitialAEAD != null) {
         this.theAEADHasher.updateHash(this.theInitialAEAD, 0, this.theInitialAEAD.length);
      }
   }

   private static int bufLength(byte[] var0) {
      return var0 == null ? 0 : var0.length;
   }

   private static void checkBuffer(byte[] var0, int var1, int var2, boolean var3) {
      int var4 = bufLength(var0);
      int var5 = var1 + var2;
      boolean var6 = var2 < 0 || var1 < 0 || var5 < 0;
      if (var6 || var5 > var4) {
         throw var3 ? new OutputLengthException("Output buffer too short.") : new DataLengthException("Input buffer too short.");
      }
   }

   private int encryptPlain(byte[] var1, byte[] var2, int var3) {
      byte[] var4 = this.thePlain.getBuffer();
      byte[] var5 = Arrays.clone(var1);
      var5[15] |= -128;
      byte[] var6 = new byte[16];
      int var7 = this.thePlain.size();
      int var8 = 0;

      while (var7 > 0) {
         this.theCipher.processBlock(var5, 0, var6, 0);
         int var9 = Math.min(16, var7);
         xorBlock(var6, var4, var8, var9);
         System.arraycopy(var6, 0, var2, var3 + var8, var9);
         var7 -= var9;
         var8 += var9;
         incrementCounter(var5);
      }

      return this.thePlain.size();
   }

   private void decryptPlain() throws InvalidCipherTextException {
      byte[] var1 = this.theEncData.getBuffer();
      int var2 = this.theEncData.size() - 16;
      if (var2 < 0) {
         throw new InvalidCipherTextException("Data too short");
      } else {
         byte[] var3 = Arrays.copyOfRange(var1, var2, var2 + 16);
         byte[] var4 = Arrays.clone(var3);
         var4[15] |= -128;
         byte[] var5 = new byte[16];
         int var6 = 0;

         while (var2 > 0) {
            this.theCipher.processBlock(var4, 0, var5, 0);
            int var7 = Math.min(16, var2);
            xorBlock(var5, var1, var6, var7);
            this.thePlain.write(var5, 0, var7);
            this.theDataHasher.updateHash(var5, 0, var7);
            var2 -= var7;
            var6 += var7;
            incrementCounter(var4);
         }

         byte[] var8 = this.calculateTag();
         if (!Arrays.constantTimeAreEqual(var8, var3)) {
            this.reset();
            throw new InvalidCipherTextException("mac check failed");
         } else {
            System.arraycopy(var8, 0, this.macBlock, 0, this.macBlock.length);
         }
      }
   }

   private byte[] calculateTag() {
      this.theDataHasher.completeHash();
      byte[] var1 = this.completePolyVal();
      byte[] var2 = new byte[16];

      for (int var3 = 0; var3 < 12; var3++) {
         var1[var3] ^= this.theNonce[var3];
      }

      var1[15] = (byte)(var1[15] & -129);
      this.theCipher.processBlock(var1, 0, var2, 0);
      return var2;
   }

   private byte[] completePolyVal() {
      byte[] var1 = new byte[16];
      this.gHashLengths();
      fillReverse(this.theGHash, 0, 16, var1);
      return var1;
   }

   private void gHashLengths() {
      byte[] var1 = new byte[16];
      Pack.longToBigEndian(8L * this.theDataHasher.getBytesProcessed(), var1, 0);
      Pack.longToBigEndian(8L * this.theAEADHasher.getBytesProcessed(), var1, 8);
      this.gHASH(var1);
   }

   private void gHASH(byte[] var1) {
      xorBlock(this.theGHash, var1);
      this.theMultiplier.multiplyH(this.theGHash);
   }

   private static void fillReverse(byte[] var0, int var1, int var2, byte[] var3) {
      int var4 = 0;

      for (int var5 = 15; var4 < var2; var5--) {
         var3[var5] = var0[var1 + var4];
         var4++;
      }
   }

   private static void xorBlock(byte[] var0, byte[] var1) {
      for (int var2 = 0; var2 < 16; var2++) {
         var0[var2] ^= var1[var2];
      }
   }

   private static void xorBlock(byte[] var0, byte[] var1, int var2, int var3) {
      for (int var4 = 0; var4 < var3; var4++) {
         var0[var4] ^= var1[var4 + var2];
      }
   }

   private static void incrementCounter(byte[] var0) {
      int var1 = 0;

      while (var1 < 4 && ++var0[var1] == 0) {
         var1++;
      }
   }

   private static void mulX(byte[] var0) {
      int var1 = 0;

      for (int var2 = 0; var2 < 16; var2++) {
         byte var3 = var0[var2];
         var0[var2] = (byte)(var3 >> 1 & 127 | var1);
         var1 = (var3 & 1) == 0 ? 0 : -128;
      }

      if (var1 != 0) {
         var0[0] ^= -31;
      }
   }

   private void deriveKeys(KeyParameter var1) {
      byte[] var2 = new byte[16];
      byte[] var3 = new byte[16];
      byte[] var4 = new byte[16];
      byte[] var5 = new byte[var1.getKeyLength()];
      System.arraycopy(this.theNonce, 0, var2, 4, 12);
      this.theCipher.init(true, var1);
      int var6 = 0;
      this.theCipher.processBlock(var2, 0, var3, 0);
      System.arraycopy(var3, 0, var4, var6, 8);
      var2[0]++;
      var6 += 8;
      this.theCipher.processBlock(var2, 0, var3, 0);
      System.arraycopy(var3, 0, var4, var6, 8);
      var2[0]++;
      int var8 = 0;
      this.theCipher.processBlock(var2, 0, var3, 0);
      System.arraycopy(var3, 0, var5, var8, 8);
      var2[0]++;
      var8 += 8;
      this.theCipher.processBlock(var2, 0, var3, 0);
      System.arraycopy(var3, 0, var5, var8, 8);
      if (var5.length == 32) {
         var2[0]++;
         var8 += 8;
         this.theCipher.processBlock(var2, 0, var3, 0);
         System.arraycopy(var3, 0, var5, var8, 8);
         var2[0]++;
         var8 += 8;
         this.theCipher.processBlock(var2, 0, var3, 0);
         System.arraycopy(var3, 0, var5, var8, 8);
      }

      this.theCipher.init(true, new KeyParameter(var5));
      fillReverse(var4, 0, 16, var3);
      mulX(var3);
      this.theMultiplier.init(var3);
      this.theFlags |= 1;
   }

   private static class GCMSIVCache extends ByteArrayOutputStream {
      GCMSIVCache() {
      }

      byte[] getBuffer() {
         return this.buf;
      }

      void clearBuffer() {
         Arrays.fill(this.getBuffer(), (byte)0);
      }
   }

   private class GCMSIVHasher {
      private final byte[] theBuffer = new byte[16];
      private final byte[] theByte = new byte[1];
      private int numActive;
      private long numHashed;

      private GCMSIVHasher() {
      }

      long getBytesProcessed() {
         return this.numHashed;
      }

      void reset() {
         this.numActive = 0;
         this.numHashed = 0L;
      }

      void updateHash(byte var1) {
         this.theByte[0] = var1;
         this.updateHash(this.theByte, 0, 1);
      }

      void updateHash(byte[] var1, int var2, int var3) {
         int var4 = 16 - this.numActive;
         int var5 = 0;
         int var6 = var3;
         if (this.numActive > 0 && var3 >= var4) {
            System.arraycopy(var1, var2, this.theBuffer, this.numActive, var4);
            GCMSIVBlockCipher.fillReverse(this.theBuffer, 0, 16, GCMSIVBlockCipher.this.theReverse);
            GCMSIVBlockCipher.this.gHASH(GCMSIVBlockCipher.this.theReverse);
            var5 += var4;
            var6 = var3 - var4;
            this.numActive = 0;
         }

         while (var6 >= 16) {
            GCMSIVBlockCipher.fillReverse(var1, var2 + var5, 16, GCMSIVBlockCipher.this.theReverse);
            GCMSIVBlockCipher.this.gHASH(GCMSIVBlockCipher.this.theReverse);
            var5 += 16;
            var6 -= 16;
         }

         if (var6 > 0) {
            System.arraycopy(var1, var2 + var5, this.theBuffer, this.numActive, var6);
            this.numActive += var6;
         }

         this.numHashed += var3;
      }

      void completeHash() {
         if (this.numActive > 0) {
            Arrays.fill(GCMSIVBlockCipher.this.theReverse, (byte)0);
            GCMSIVBlockCipher.fillReverse(this.theBuffer, 0, this.numActive, GCMSIVBlockCipher.this.theReverse);
            GCMSIVBlockCipher.this.gHASH(GCMSIVBlockCipher.this.theReverse);
         }
      }
   }
}
