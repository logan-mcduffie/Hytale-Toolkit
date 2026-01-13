package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class ParallelHash implements Xof, Digest {
   private static final byte[] N_PARALLEL_HASH = Strings.toByteArray("ParallelHash");
   private final CSHAKEDigest cshake;
   private final CSHAKEDigest compressor;
   private final int bitLength;
   private final int outputLength;
   private final int B;
   private final byte[] buffer;
   private final byte[] compressorBuffer;
   private boolean firstOutput;
   private int nCount;
   private int bufOff;
   private final CryptoServicePurpose purpose;

   public ParallelHash(int var1, byte[] var2, int var3) {
      this(var1, var2, var3, var1 * 2, CryptoServicePurpose.ANY);
   }

   public ParallelHash(int var1, byte[] var2, int var3, int var4) {
      this(var1, var2, var3, var4, CryptoServicePurpose.ANY);
   }

   public ParallelHash(int var1, byte[] var2, int var3, int var4, CryptoServicePurpose var5) {
      if (var3 <= 0) {
         throw new IllegalArgumentException("block size should be greater than 0");
      } else {
         this.cshake = new CSHAKEDigest(var1, N_PARALLEL_HASH, var2);
         this.compressor = new CSHAKEDigest(var1, new byte[0], new byte[0]);
         this.bitLength = var1;
         this.B = var3;
         this.outputLength = (var4 + 7) / 8;
         this.buffer = new byte[var3];
         this.compressorBuffer = new byte[var1 * 2 / 8];
         this.purpose = var5;
         CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, var1, var5));
         this.reset();
      }
   }

   public ParallelHash(ParallelHash var1) {
      this.cshake = new CSHAKEDigest(var1.cshake);
      this.compressor = new CSHAKEDigest(var1.compressor);
      this.bitLength = var1.bitLength;
      this.B = var1.B;
      this.outputLength = var1.outputLength;
      this.buffer = Arrays.clone(var1.buffer);
      this.compressorBuffer = Arrays.clone(var1.compressorBuffer);
      this.purpose = var1.purpose;
      this.firstOutput = var1.firstOutput;
      this.nCount = var1.nCount;
      this.bufOff = var1.bufOff;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.bitLength, this.purpose));
   }

   @Override
   public String getAlgorithmName() {
      return "ParallelHash" + this.cshake.getAlgorithmName().substring(6);
   }

   @Override
   public int getByteLength() {
      return this.cshake.getByteLength();
   }

   @Override
   public int getDigestSize() {
      return this.outputLength;
   }

   @Override
   public void update(byte var1) throws IllegalStateException {
      this.buffer[this.bufOff++] = var1;
      if (this.bufOff == this.buffer.length) {
         this.compress();
      }
   }

   @Override
   public void update(byte[] var1, int var2, int var3) throws DataLengthException, IllegalStateException {
      var3 = Math.max(0, var3);
      int var4 = 0;
      if (this.bufOff != 0) {
         while (var4 < var3 && this.bufOff != this.buffer.length) {
            this.buffer[this.bufOff++] = var1[var2 + var4++];
         }

         if (this.bufOff == this.buffer.length) {
            this.compress();
         }
      }

      if (var4 < var3) {
         while (var3 - var4 >= this.B) {
            this.compress(var1, var2 + var4, this.B);
            var4 += this.B;
         }
      }

      while (var4 < var3) {
         this.update(var1[var2 + var4++]);
      }
   }

   private void compress() {
      this.compress(this.buffer, 0, this.bufOff);
      this.bufOff = 0;
   }

   private void compress(byte[] var1, int var2, int var3) {
      this.compressor.update(var1, var2, var3);
      this.compressor.doFinal(this.compressorBuffer, 0, this.compressorBuffer.length);
      this.cshake.update(this.compressorBuffer, 0, this.compressorBuffer.length);
      this.nCount++;
   }

   private void wrapUp(int var1) {
      if (this.bufOff != 0) {
         this.compress();
      }

      byte[] var2 = XofUtils.rightEncode(this.nCount);
      byte[] var3 = XofUtils.rightEncode(var1 * 8);
      this.cshake.update(var2, 0, var2.length);
      this.cshake.update(var3, 0, var3.length);
      this.firstOutput = false;
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws DataLengthException, IllegalStateException {
      if (this.firstOutput) {
         this.wrapUp(this.outputLength);
      }

      int var3 = this.cshake.doFinal(var1, var2, this.getDigestSize());
      this.reset();
      return var3;
   }

   @Override
   public int doFinal(byte[] var1, int var2, int var3) {
      if (this.firstOutput) {
         this.wrapUp(this.outputLength);
      }

      int var4 = this.cshake.doFinal(var1, var2, var3);
      this.reset();
      return var4;
   }

   @Override
   public int doOutput(byte[] var1, int var2, int var3) {
      if (this.firstOutput) {
         this.wrapUp(0);
      }

      return this.cshake.doOutput(var1, var2, var3);
   }

   @Override
   public void reset() {
      this.cshake.reset();
      Arrays.clear(this.buffer);
      byte[] var1 = XofUtils.leftEncode(this.B);
      this.cshake.update(var1, 0, var1.length);
      this.nCount = 0;
      this.bufOff = 0;
      this.firstOutput = true;
   }
}
