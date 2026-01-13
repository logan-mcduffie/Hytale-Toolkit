package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.util.Arrays;

public class Blake2xsDigest implements Xof {
   public static final int UNKNOWN_DIGEST_LENGTH = 65535;
   private static final int DIGEST_LENGTH = 32;
   private static final long MAX_NUMBER_BLOCKS = 4294967296L;
   private int digestLength;
   private Blake2sDigest hash;
   private byte[] h0 = null;
   private byte[] buf = new byte[32];
   private int bufPos = 32;
   private int digestPos = 0;
   private long blockPos = 0L;
   private long nodeOffset;
   private final CryptoServicePurpose purpose;

   public Blake2xsDigest() {
      this(65535, CryptoServicePurpose.ANY);
   }

   public Blake2xsDigest(int var1, CryptoServicePurpose var2) {
      this(var1, null, null, null, var2);
   }

   public Blake2xsDigest(int var1) {
      this(var1, CryptoServicePurpose.ANY);
   }

   public Blake2xsDigest(int var1, byte[] var2) {
      this(var1, var2, null, null, CryptoServicePurpose.ANY);
   }

   public Blake2xsDigest(int var1, byte[] var2, byte[] var3, byte[] var4, CryptoServicePurpose var5) {
      if (var1 >= 1 && var1 <= 65535) {
         this.digestLength = var1;
         this.nodeOffset = this.computeNodeOffset();
         this.purpose = var5;
         this.hash = new Blake2sDigest(32, var2, var3, var4, this.nodeOffset, var5);
      } else {
         throw new IllegalArgumentException("BLAKE2xs digest length must be between 1 and 2^16-1");
      }
   }

   public Blake2xsDigest(Blake2xsDigest var1) {
      this.digestLength = var1.digestLength;
      this.hash = new Blake2sDigest(var1.hash);
      this.h0 = Arrays.clone(var1.h0);
      this.buf = Arrays.clone(var1.buf);
      this.bufPos = var1.bufPos;
      this.digestPos = var1.digestPos;
      this.blockPos = var1.blockPos;
      this.nodeOffset = var1.nodeOffset;
      this.purpose = var1.purpose;
   }

   @Override
   public String getAlgorithmName() {
      return "BLAKE2xs";
   }

   @Override
   public int getDigestSize() {
      return this.digestLength;
   }

   @Override
   public int getByteLength() {
      return this.hash.getByteLength();
   }

   public long getUnknownMaxLength() {
      return 137438953472L;
   }

   @Override
   public void update(byte var1) {
      this.hash.update(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.hash.update(var1, var2, var3);
   }

   @Override
   public void reset() {
      this.hash.reset();
      this.h0 = null;
      this.bufPos = 32;
      this.digestPos = 0;
      this.blockPos = 0L;
      this.nodeOffset = this.computeNodeOffset();
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      return this.doFinal(var1, var2, this.digestLength);
   }

   @Override
   public int doFinal(byte[] var1, int var2, int var3) {
      int var4 = this.doOutput(var1, var2, var3);
      this.reset();
      return var4;
   }

   @Override
   public int doOutput(byte[] var1, int var2, int var3) {
      if (var2 > var1.length - var3) {
         throw new OutputLengthException("output buffer too short");
      } else {
         if (this.h0 == null) {
            this.h0 = new byte[this.hash.getDigestSize()];
            this.hash.doFinal(this.h0, 0);
         }

         if (this.digestLength != 65535) {
            if (this.digestPos + var3 > this.digestLength) {
               throw new IllegalArgumentException("Output length is above the digest length");
            }
         } else if (this.blockPos << 5 >= this.getUnknownMaxLength()) {
            throw new IllegalArgumentException("Maximum length is 2^32 blocks of 32 bytes");
         }

         for (int var4 = 0; var4 < var3; var4++) {
            if (this.bufPos >= 32) {
               Blake2sDigest var5 = new Blake2sDigest(this.computeStepLength(), 32, this.nodeOffset);
               var5.update(this.h0, 0, this.h0.length);
               Arrays.fill(this.buf, (byte)0);
               var5.doFinal(this.buf, 0);
               this.bufPos = 0;
               this.nodeOffset++;
               this.blockPos++;
            }

            var1[var2 + var4] = this.buf[this.bufPos];
            this.bufPos++;
            this.digestPos++;
         }

         return var3;
      }
   }

   private int computeStepLength() {
      return this.digestLength == 65535 ? 32 : Math.min(32, this.digestLength - this.digestPos);
   }

   private long computeNodeOffset() {
      return this.digestLength * 4294967296L;
   }
}
