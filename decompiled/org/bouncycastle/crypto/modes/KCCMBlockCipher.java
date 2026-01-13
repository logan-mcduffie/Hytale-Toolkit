package org.bouncycastle.crypto.modes;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class KCCMBlockCipher implements AEADBlockCipher {
   private static final int BYTES_IN_INT = 4;
   private static final int BITS_IN_BYTE = 8;
   private static final int MAX_MAC_BIT_LENGTH = 512;
   private static final int MIN_MAC_BIT_LENGTH = 64;
   private BlockCipher engine;
   private int macSize;
   private boolean forEncryption;
   private byte[] initialAssociatedText;
   private byte[] mac;
   private byte[] macBlock;
   private byte[] nonce;
   private byte[] G1;
   private byte[] buffer;
   private byte[] s;
   private byte[] counter;
   private KCCMBlockCipher.ExposedByteArrayOutputStream associatedText = new KCCMBlockCipher.ExposedByteArrayOutputStream();
   private KCCMBlockCipher.ExposedByteArrayOutputStream data = new KCCMBlockCipher.ExposedByteArrayOutputStream();
   private int Nb_ = 4;

   private void setNb(int var1) {
      if (var1 != 4 && var1 != 6 && var1 != 8) {
         throw new IllegalArgumentException("Nb = 4 is recommended by DSTU7624 but can be changed to only 6 or 8 in this implementation");
      } else {
         this.Nb_ = var1;
      }
   }

   public KCCMBlockCipher(BlockCipher var1) {
      this(var1, 4);
   }

   public KCCMBlockCipher(BlockCipher var1, int var2) {
      this.engine = var1;
      this.macSize = var1.getBlockSize();
      this.nonce = new byte[var1.getBlockSize()];
      this.initialAssociatedText = new byte[var1.getBlockSize()];
      this.mac = new byte[var1.getBlockSize()];
      this.macBlock = new byte[var1.getBlockSize()];
      this.G1 = new byte[var1.getBlockSize()];
      this.buffer = new byte[var1.getBlockSize()];
      this.s = new byte[var1.getBlockSize()];
      this.counter = new byte[var1.getBlockSize()];
      this.setNb(var2);
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      Object var3;
      if (var2 instanceof AEADParameters) {
         AEADParameters var4 = (AEADParameters)var2;
         if (var4.getMacSize() > 512 || var4.getMacSize() < 64 || var4.getMacSize() % 8 != 0) {
            throw new IllegalArgumentException("Invalid mac size specified");
         }

         this.nonce = var4.getNonce();
         this.macSize = var4.getMacSize() / 8;
         this.initialAssociatedText = var4.getAssociatedText();
         var3 = var4.getKey();
      } else {
         if (!(var2 instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("Invalid parameters specified");
         }

         this.nonce = ((ParametersWithIV)var2).getIV();
         this.macSize = this.engine.getBlockSize();
         this.initialAssociatedText = null;
         var3 = ((ParametersWithIV)var2).getParameters();
      }

      this.mac = new byte[this.macSize];
      this.forEncryption = var1;
      this.engine.init(true, (CipherParameters)var3);
      this.counter[0] = 1;
      if (this.initialAssociatedText != null) {
         this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
      }
   }

   @Override
   public String getAlgorithmName() {
      return this.engine.getAlgorithmName() + "/KCCM";
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

   private void processAAD(byte[] var1, int var2, int var3, int var4) {
      if (var3 - var2 < this.engine.getBlockSize()) {
         throw new IllegalArgumentException("authText buffer too short");
      } else if (var3 % this.engine.getBlockSize() != 0) {
         throw new IllegalArgumentException("padding not supported");
      } else {
         System.arraycopy(this.nonce, 0, this.G1, 0, this.nonce.length - this.Nb_ - 1);
         this.intToBytes(var4, this.buffer, 0);
         System.arraycopy(this.buffer, 0, this.G1, this.nonce.length - this.Nb_ - 1, 4);
         this.G1[this.G1.length - 1] = this.getFlag(true, this.macSize);
         this.engine.processBlock(this.G1, 0, this.macBlock, 0);
         this.intToBytes(var3, this.buffer, 0);
         if (var3 <= this.engine.getBlockSize() - this.Nb_) {
            for (int var8 = 0; var8 < var3; var8++) {
               this.buffer[var8 + this.Nb_] = (byte)(this.buffer[var8 + this.Nb_] ^ var1[var2 + var8]);
            }

            for (int var9 = 0; var9 < this.engine.getBlockSize(); var9++) {
               this.macBlock[var9] = (byte)(this.macBlock[var9] ^ this.buffer[var9]);
            }

            this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
         } else {
            for (int var5 = 0; var5 < this.engine.getBlockSize(); var5++) {
               this.macBlock[var5] = (byte)(this.macBlock[var5] ^ this.buffer[var5]);
            }

            this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);

            for (int var7 = var3; var7 != 0; var7 -= this.engine.getBlockSize()) {
               for (int var6 = 0; var6 < this.engine.getBlockSize(); var6++) {
                  this.macBlock[var6] = (byte)(this.macBlock[var6] ^ var1[var6 + var2]);
               }

               this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
               var2 += this.engine.getBlockSize();
            }
         }
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

   public int processPacket(byte[] var1, int var2, int var3, byte[] var4, int var5) throws IllegalStateException, InvalidCipherTextException {
      if (var1.length - var2 < var3) {
         throw new DataLengthException("input buffer too short");
      } else if (var4.length - var5 < var3) {
         throw new OutputLengthException("output buffer too short");
      } else {
         if (this.associatedText.size() > 0) {
            if (this.forEncryption) {
               this.processAAD(this.associatedText.getBuffer(), 0, this.associatedText.size(), this.data.size());
            } else {
               this.processAAD(this.associatedText.getBuffer(), 0, this.associatedText.size(), this.data.size() - this.macSize);
            }
         }

         if (this.forEncryption) {
            if (var3 % this.engine.getBlockSize() != 0) {
               throw new DataLengthException("partial blocks not supported");
            } else {
               this.CalculateMac(var1, var2, var3);
               this.engine.processBlock(this.nonce, 0, this.s, 0);

               for (int var8 = var3; var8 > 0; var5 += this.engine.getBlockSize()) {
                  this.ProcessBlock(var1, var2, var3, var4, var5);
                  var8 -= this.engine.getBlockSize();
                  var2 += this.engine.getBlockSize();
               }

               for (int var13 = 0; var13 < this.counter.length; var13++) {
                  this.s[var13] = (byte)(this.s[var13] + this.counter[var13]);
               }

               this.engine.processBlock(this.s, 0, this.buffer, 0);

               for (int var14 = 0; var14 < this.macSize; var14++) {
                  var4[var5 + var14] = (byte)(this.buffer[var14] ^ this.macBlock[var14]);
               }

               System.arraycopy(this.macBlock, 0, this.mac, 0, this.macSize);
               this.reset();
               return var3 + this.macSize;
            }
         } else if ((var3 - this.macSize) % this.engine.getBlockSize() != 0) {
            throw new DataLengthException("partial blocks not supported");
         } else {
            this.engine.processBlock(this.nonce, 0, this.s, 0);
            int var6 = var3 / this.engine.getBlockSize();

            for (int var7 = 0; var7 < var6; var7++) {
               this.ProcessBlock(var1, var2, var3, var4, var5);
               var2 += this.engine.getBlockSize();
               var5 += this.engine.getBlockSize();
            }

            if (var3 > var2) {
               for (int var9 = 0; var9 < this.counter.length; var9++) {
                  this.s[var9] = (byte)(this.s[var9] + this.counter[var9]);
               }

               this.engine.processBlock(this.s, 0, this.buffer, 0);

               for (int var10 = 0; var10 < this.macSize; var10++) {
                  var4[var5 + var10] = (byte)(this.buffer[var10] ^ var1[var2 + var10]);
               }

               var5 += this.macSize;
            }

            for (int var11 = 0; var11 < this.counter.length; var11++) {
               this.s[var11] = (byte)(this.s[var11] + this.counter[var11]);
            }

            this.engine.processBlock(this.s, 0, this.buffer, 0);
            System.arraycopy(var4, var5 - this.macSize, this.buffer, 0, this.macSize);
            this.CalculateMac(var4, 0, var5 - this.macSize);
            System.arraycopy(this.macBlock, 0, this.mac, 0, this.macSize);
            byte[] var12 = new byte[this.macSize];
            System.arraycopy(this.buffer, 0, var12, 0, this.macSize);
            if (!Arrays.constantTimeAreEqual(this.mac, var12)) {
               throw new InvalidCipherTextException("mac check failed");
            } else {
               this.reset();
               return var3 - this.macSize;
            }
         }
      }
   }

   private void ProcessBlock(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      for (int var6 = 0; var6 < this.counter.length; var6++) {
         this.s[var6] = (byte)(this.s[var6] + this.counter[var6]);
      }

      this.engine.processBlock(this.s, 0, this.buffer, 0);

      for (int var7 = 0; var7 < this.engine.getBlockSize(); var7++) {
         var4[var5 + var7] = (byte)(this.buffer[var7] ^ var1[var2 + var7]);
      }
   }

   private void CalculateMac(byte[] var1, int var2, int var3) {
      int var4 = var3;

      while (var4 > 0) {
         for (int var5 = 0; var5 < this.engine.getBlockSize(); var5++) {
            this.macBlock[var5] = (byte)(this.macBlock[var5] ^ var1[var2 + var5]);
         }

         this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
         var4 -= this.engine.getBlockSize();
         var2 += this.engine.getBlockSize();
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws IllegalStateException, InvalidCipherTextException {
      int var3 = this.processPacket(this.data.getBuffer(), 0, this.data.size(), var1, var2);
      this.reset();
      return var3;
   }

   @Override
   public byte[] getMac() {
      return Arrays.clone(this.mac);
   }

   @Override
   public int getUpdateOutputSize(int var1) {
      return var1;
   }

   @Override
   public int getOutputSize(int var1) {
      return var1 + this.macSize;
   }

   @Override
   public void reset() {
      Arrays.fill(this.G1, (byte)0);
      Arrays.fill(this.buffer, (byte)0);
      Arrays.fill(this.counter, (byte)0);
      Arrays.fill(this.macBlock, (byte)0);
      this.counter[0] = 1;
      this.data.reset();
      this.associatedText.reset();
      if (this.initialAssociatedText != null) {
         this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
      }
   }

   private void intToBytes(int var1, byte[] var2, int var3) {
      var2[var3 + 3] = (byte)(var1 >> 24);
      var2[var3 + 2] = (byte)(var1 >> 16);
      var2[var3 + 1] = (byte)(var1 >> 8);
      var2[var3] = (byte)var1;
   }

   private byte getFlag(boolean var1, int var2) {
      StringBuilder var3 = new StringBuilder();
      if (var1) {
         var3.append("1");
      } else {
         var3.append("0");
      }

      switch (var2) {
         case 8:
            var3.append("010");
            break;
         case 16:
            var3.append("011");
            break;
         case 32:
            var3.append("100");
            break;
         case 48:
            var3.append("101");
            break;
         case 64:
            var3.append("110");
      }

      String var4 = Integer.toBinaryString(this.Nb_ - 1);

      while (var4.length() < 4) {
         var4 = new StringBuilder(var4).insert(0, "0").toString();
      }

      var3.append(var4);
      return (byte)Integer.parseInt(var3.toString(), 2);
   }

   private static class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
      public ExposedByteArrayOutputStream() {
      }

      public byte[] getBuffer() {
         return this.buf;
      }
   }
}
