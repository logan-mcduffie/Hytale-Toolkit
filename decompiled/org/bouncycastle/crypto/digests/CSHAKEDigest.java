package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;

public class CSHAKEDigest extends SHAKEDigest {
   private static final byte[] padding = new byte[100];
   private byte[] diff;

   public CSHAKEDigest(int var1, byte[] var2, byte[] var3) {
      this(var1, CryptoServicePurpose.ANY, var2, var3);
   }

   public CSHAKEDigest(int var1, CryptoServicePurpose var2, byte[] var3, byte[] var4) {
      super(var1, var2);
      if (var3 != null && var3.length != 0 || var4 != null && var4.length != 0) {
         this.diff = Arrays.concatenate(XofUtils.leftEncode(this.rate / 8), this.encodeString(var3), this.encodeString(var4));
         this.diffPadAndAbsorb();
      } else {
         this.diff = null;
      }
   }

   public CSHAKEDigest(CSHAKEDigest var1) {
      super(var1);
      this.diff = Arrays.clone(var1.diff);
   }

   public CSHAKEDigest(byte[] var1) {
      super(var1);
      int var2 = this.state.length * 8 + this.dataQueue.length + 12 + 2;
      if (var1.length != var2) {
         this.diff = new byte[var1.length - var2];
         System.arraycopy(var1, var2, this.diff, 0, this.diff.length);
      } else {
         this.diff = null;
      }
   }

   private void copyIn(CSHAKEDigest var1) {
      super.copyIn(var1);
      this.diff = Arrays.clone(var1.diff);
   }

   private void diffPadAndAbsorb() {
      int var1 = this.rate / 8;
      this.absorb(this.diff, 0, this.diff.length);
      int var2 = this.diff.length % var1;
      if (var2 != 0) {
         int var3;
         for (var3 = var1 - var2; var3 > padding.length; var3 -= padding.length) {
            this.absorb(padding, 0, padding.length);
         }

         this.absorb(padding, 0, var3);
      }
   }

   private byte[] encodeString(byte[] var1) {
      return var1 != null && var1.length != 0 ? Arrays.concatenate(XofUtils.leftEncode(var1.length * 8L), var1) : XofUtils.leftEncode(0L);
   }

   @Override
   public String getAlgorithmName() {
      return "CSHAKE" + this.fixedOutputLength;
   }

   @Override
   public int doOutput(byte[] var1, int var2, int var3) {
      if (this.diff != null) {
         if (!this.squeezing) {
            this.absorbBits(0, 2);
         }

         this.squeeze(var1, var2, var3 * 8L);
         return var3;
      } else {
         return super.doOutput(var1, var2, var3);
      }
   }

   @Override
   public void reset() {
      super.reset();
      if (this.diff != null) {
         this.diffPadAndAbsorb();
      }
   }

   @Override
   public byte[] getEncodedState() {
      int var1 = this.state.length * 8 + this.dataQueue.length + 12 + 2;
      byte[] var2;
      if (this.diff == null) {
         var2 = new byte[var1];
         super.getEncodedState(var2);
      } else {
         var2 = new byte[var1 + this.diff.length];
         super.getEncodedState(var2);
         System.arraycopy(this.diff, 0, var2, var1, this.diff.length);
      }

      return var2;
   }

   @Override
   public Memoable copy() {
      return new CSHAKEDigest(this);
   }

   @Override
   public void reset(Memoable var1) {
      CSHAKEDigest var2 = (CSHAKEDigest)var1;
      this.copyIn(var2);
   }
}
