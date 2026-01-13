package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public abstract class GeneralDigest implements ExtendedDigest, Memoable {
   private static final int BYTE_LENGTH = 64;
   protected final CryptoServicePurpose purpose;
   private final byte[] xBuf = new byte[4];
   private int xBufOff;
   private long byteCount;

   protected GeneralDigest() {
      this(CryptoServicePurpose.ANY);
   }

   protected GeneralDigest(CryptoServicePurpose var1) {
      this.purpose = var1;
      this.xBufOff = 0;
   }

   protected GeneralDigest(GeneralDigest var1) {
      this.purpose = var1.purpose;
      this.copyIn(var1);
   }

   protected GeneralDigest(byte[] var1) {
      CryptoServicePurpose[] var2 = CryptoServicePurpose.values();
      this.purpose = var2[var1[var1.length - 1]];
      System.arraycopy(var1, 0, this.xBuf, 0, this.xBuf.length);
      this.xBufOff = Pack.bigEndianToInt(var1, 4);
      this.byteCount = Pack.bigEndianToLong(var1, 8);
   }

   protected void copyIn(GeneralDigest var1) {
      System.arraycopy(var1.xBuf, 0, this.xBuf, 0, var1.xBuf.length);
      this.xBufOff = var1.xBufOff;
      this.byteCount = var1.byteCount;
   }

   @Override
   public void update(byte var1) {
      this.xBuf[this.xBufOff++] = var1;
      if (this.xBufOff == this.xBuf.length) {
         this.processWord(this.xBuf, 0);
         this.xBufOff = 0;
      }

      this.byteCount++;
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      var3 = Math.max(0, var3);
      int var4 = 0;
      if (this.xBufOff != 0) {
         while (var4 < var3) {
            this.xBuf[this.xBufOff++] = var1[var2 + var4++];
            if (this.xBufOff == 4) {
               this.processWord(this.xBuf, 0);
               this.xBufOff = 0;
               break;
            }
         }
      }

      for (int var5 = var3 - 3; var4 < var5; var4 += 4) {
         this.processWord(var1, var2 + var4);
      }

      while (var4 < var3) {
         this.xBuf[this.xBufOff++] = var1[var2 + var4++];
      }

      this.byteCount += var3;
   }

   public void finish() {
      long var1 = this.byteCount << 3;
      this.update((byte)-128);

      while (this.xBufOff != 0) {
         this.update((byte)0);
      }

      this.processLength(var1);
      this.processBlock();
   }

   @Override
   public void reset() {
      this.byteCount = 0L;
      this.xBufOff = 0;

      for (int var1 = 0; var1 < this.xBuf.length; var1++) {
         this.xBuf[var1] = 0;
      }
   }

   protected void populateState(byte[] var1) {
      System.arraycopy(this.xBuf, 0, var1, 0, this.xBufOff);
      Pack.intToBigEndian(this.xBufOff, var1, 4);
      Pack.longToBigEndian(this.byteCount, var1, 8);
   }

   @Override
   public int getByteLength() {
      return 64;
   }

   protected abstract void processWord(byte[] var1, int var2);

   protected abstract void processLength(long var1);

   protected abstract void processBlock();

   protected abstract CryptoServiceProperties cryptoServiceProperties();
}
