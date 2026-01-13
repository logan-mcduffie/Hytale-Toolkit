package org.bouncycastle.crypto;

public class BufferedAsymmetricBlockCipher {
   protected byte[] buf;
   protected int bufOff;
   private final AsymmetricBlockCipher cipher;

   public BufferedAsymmetricBlockCipher(AsymmetricBlockCipher var1) {
      this.cipher = var1;
   }

   public AsymmetricBlockCipher getUnderlyingCipher() {
      return this.cipher;
   }

   public int getBufferPosition() {
      return this.bufOff;
   }

   public void init(boolean var1, CipherParameters var2) {
      this.reset();
      this.cipher.init(var1, var2);
      this.buf = new byte[this.cipher.getInputBlockSize() + (var1 ? 1 : 0)];
      this.bufOff = 0;
   }

   public int getInputBlockSize() {
      return this.cipher.getInputBlockSize();
   }

   public int getOutputBlockSize() {
      return this.cipher.getOutputBlockSize();
   }

   public void processByte(byte var1) {
      if (this.bufOff >= this.buf.length) {
         throw new DataLengthException("attempt to process message too long for cipher");
      } else {
         this.buf[this.bufOff++] = var1;
      }
   }

   public void processBytes(byte[] var1, int var2, int var3) {
      if (var3 != 0) {
         if (var3 < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
         } else if (this.bufOff + var3 > this.buf.length) {
            throw new DataLengthException("attempt to process message too long for cipher");
         } else {
            System.arraycopy(var1, var2, this.buf, this.bufOff, var3);
            this.bufOff += var3;
         }
      }
   }

   public byte[] doFinal() throws InvalidCipherTextException {
      byte[] var1 = this.cipher.processBlock(this.buf, 0, this.bufOff);
      this.reset();
      return var1;
   }

   public void reset() {
      if (this.buf != null) {
         for (int var1 = 0; var1 < this.buf.length; var1++) {
            this.buf[var1] = 0;
         }
      }

      this.bufOff = 0;
   }
}
