package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class EAXBlockCipher implements AEADBlockCipher {
   private static final byte nTAG = 0;
   private static final byte hTAG = 1;
   private static final byte cTAG = 2;
   private CTRModeCipher cipher;
   private boolean forEncryption;
   private int blockSize;
   private Mac mac;
   private byte[] nonceMac;
   private byte[] associatedTextMac;
   private byte[] macBlock;
   private int macSize;
   private byte[] bufBlock;
   private int bufOff;
   private boolean cipherInitialized;
   private byte[] initialAssociatedText;

   public EAXBlockCipher(BlockCipher var1) {
      this.blockSize = var1.getBlockSize();
      this.mac = new CMac(var1);
      this.macBlock = new byte[this.blockSize];
      this.associatedTextMac = new byte[this.mac.getMacSize()];
      this.nonceMac = new byte[this.mac.getMacSize()];
      this.cipher = SICBlockCipher.newInstance(var1);
   }

   @Override
   public String getAlgorithmName() {
      return this.cipher.getUnderlyingCipher().getAlgorithmName() + "/EAX";
   }

   @Override
   public BlockCipher getUnderlyingCipher() {
      return this.cipher.getUnderlyingCipher();
   }

   public int getBlockSize() {
      return this.cipher.getBlockSize();
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      this.forEncryption = var1;
      byte[] var3;
      Object var4;
      if (var2 instanceof AEADParameters) {
         AEADParameters var5 = (AEADParameters)var2;
         var3 = var5.getNonce();
         this.initialAssociatedText = var5.getAssociatedText();
         this.macSize = var5.getMacSize() / 8;
         var4 = var5.getKey();
      } else {
         if (!(var2 instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("invalid parameters passed to EAX");
         }

         ParametersWithIV var6 = (ParametersWithIV)var2;
         var3 = var6.getIV();
         this.initialAssociatedText = null;
         this.macSize = this.mac.getMacSize() / 2;
         var4 = var6.getParameters();
      }

      this.bufBlock = new byte[var1 ? this.blockSize : this.blockSize + this.macSize];
      byte[] var7 = new byte[this.blockSize];
      this.mac.init((CipherParameters)var4);
      var7[this.blockSize - 1] = 0;
      this.mac.update(var7, 0, this.blockSize);
      this.mac.update(var3, 0, var3.length);
      this.mac.doFinal(this.nonceMac, 0);
      this.cipher.init(true, new ParametersWithIV((CipherParameters)var4, this.nonceMac));
      this.reset();
   }

   private void initCipher() {
      if (!this.cipherInitialized) {
         this.cipherInitialized = true;
         this.mac.doFinal(this.associatedTextMac, 0);
         byte[] var1 = new byte[this.blockSize];
         var1[this.blockSize - 1] = 2;
         this.mac.update(var1, 0, this.blockSize);
      }
   }

   private void calculateMac() {
      byte[] var1 = new byte[this.blockSize];
      this.mac.doFinal(var1, 0);

      for (int var2 = 0; var2 < this.macBlock.length; var2++) {
         this.macBlock[var2] = (byte)(this.nonceMac[var2] ^ this.associatedTextMac[var2] ^ var1[var2]);
      }
   }

   @Override
   public void reset() {
      this.reset(true);
   }

   private void reset(boolean var1) {
      this.cipher.reset();
      this.mac.reset();
      this.bufOff = 0;
      Arrays.fill(this.bufBlock, (byte)0);
      if (var1) {
         Arrays.fill(this.macBlock, (byte)0);
      }

      byte[] var2 = new byte[this.blockSize];
      var2[this.blockSize - 1] = 1;
      this.mac.update(var2, 0, this.blockSize);
      this.cipherInitialized = false;
      if (this.initialAssociatedText != null) {
         this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
      }
   }

   @Override
   public void processAADByte(byte var1) {
      if (this.cipherInitialized) {
         throw new IllegalStateException("AAD data cannot be added after encryption/decryption processing has begun.");
      } else {
         this.mac.update(var1);
      }
   }

   @Override
   public void processAADBytes(byte[] var1, int var2, int var3) {
      if (this.cipherInitialized) {
         throw new IllegalStateException("AAD data cannot be added after encryption/decryption processing has begun.");
      } else {
         this.mac.update(var1, var2, var3);
      }
   }

   @Override
   public int processByte(byte var1, byte[] var2, int var3) throws DataLengthException {
      this.initCipher();
      return this.process(var1, var2, var3);
   }

   @Override
   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException {
      this.initCipher();
      if (var1.length < var2 + var3) {
         throw new DataLengthException("Input buffer too short");
      } else {
         if (var1 == var4 && Arrays.segmentsOverlap(var2, var3, var5, this.getUpdateOutputSize(var3))) {
            var1 = new byte[var3];
            System.arraycopy(var4, var2, var1, 0, var3);
            var2 = 0;
         }

         int var6 = 0;

         for (int var7 = 0; var7 != var3; var7++) {
            var6 += this.process(var1[var2 + var7], var4, var5 + var6);
         }

         return var6;
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws IllegalStateException, InvalidCipherTextException {
      this.initCipher();
      int var3 = this.bufOff;
      byte[] var4 = new byte[this.bufBlock.length];
      this.bufOff = 0;
      if (this.forEncryption) {
         if (var1.length < var2 + var3 + this.macSize) {
            throw new OutputLengthException("Output buffer too short");
         } else {
            this.cipher.processBlock(this.bufBlock, 0, var4, 0);
            System.arraycopy(var4, 0, var1, var2, var3);
            this.mac.update(var4, 0, var3);
            this.calculateMac();
            System.arraycopy(this.macBlock, 0, var1, var2 + var3, this.macSize);
            this.reset(false);
            return var3 + this.macSize;
         }
      } else if (var3 < this.macSize) {
         throw new InvalidCipherTextException("data too short");
      } else if (var1.length < var2 + var3 - this.macSize) {
         throw new OutputLengthException("Output buffer too short");
      } else {
         if (var3 > this.macSize) {
            this.mac.update(this.bufBlock, 0, var3 - this.macSize);
            this.cipher.processBlock(this.bufBlock, 0, var4, 0);
            System.arraycopy(var4, 0, var1, var2, var3 - this.macSize);
         }

         this.calculateMac();
         if (!this.verifyMac(this.bufBlock, var3 - this.macSize)) {
            throw new InvalidCipherTextException("mac check in EAX failed");
         } else {
            this.reset(false);
            return var3 - this.macSize;
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
      int var2 = var1 + this.bufOff;
      if (!this.forEncryption) {
         if (var2 < this.macSize) {
            return 0;
         }

         var2 -= this.macSize;
      }

      return var2 - var2 % this.blockSize;
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

   private int process(byte var1, byte[] var2, int var3) {
      this.bufBlock[this.bufOff++] = var1;
      if (this.bufOff == this.bufBlock.length) {
         if (var2.length < var3 + this.blockSize) {
            throw new OutputLengthException("Output buffer is too short");
         } else {
            int var4;
            if (this.forEncryption) {
               var4 = this.cipher.processBlock(this.bufBlock, 0, var2, var3);
               this.mac.update(var2, var3, this.blockSize);
            } else {
               this.mac.update(this.bufBlock, 0, this.blockSize);
               var4 = this.cipher.processBlock(this.bufBlock, 0, var2, var3);
            }

            this.bufOff = 0;
            if (!this.forEncryption) {
               System.arraycopy(this.bufBlock, this.blockSize, this.bufBlock, 0, this.macSize);
               this.bufOff = this.macSize;
            }

            return var4;
         }
      } else {
         return 0;
      }
   }

   private boolean verifyMac(byte[] var1, int var2) {
      int var3 = 0;

      for (int var4 = 0; var4 < this.macSize; var4++) {
         var3 |= this.macBlock[var4] ^ var1[var2 + var4];
      }

      return var3 == 0;
   }
}
