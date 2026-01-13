package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class HMacDSAKCalculator implements DSAKCalculator {
   private final HMac hMac;
   private final byte[] K;
   private final byte[] V;
   private BigInteger n;

   public HMacDSAKCalculator(Digest var1) {
      this.hMac = new HMac(var1);
      int var2 = this.hMac.getMacSize();
      this.V = new byte[var2];
      this.K = new byte[var2];
   }

   @Override
   public boolean isDeterministic() {
      return true;
   }

   @Override
   public void init(BigInteger var1, SecureRandom var2) {
      throw new IllegalStateException("Operation not supported");
   }

   @Override
   public void init(BigInteger var1, BigInteger var2, byte[] var3) {
      this.n = var1;
      BigInteger var4 = this.bitsToInt(var3);
      if (var4.compareTo(var1) >= 0) {
         var4 = var4.subtract(var1);
      }

      int var5 = BigIntegers.getUnsignedByteLength(var1);
      byte[] var6 = BigIntegers.asUnsignedByteArray(var5, var2);
      byte[] var7 = BigIntegers.asUnsignedByteArray(var5, var4);
      Arrays.fill(this.K, (byte)0);
      Arrays.fill(this.V, (byte)1);
      this.hMac.init(new KeyParameter(this.K));
      this.hMac.update(this.V, 0, this.V.length);
      this.hMac.update((byte)0);
      this.hMac.update(var6, 0, var6.length);
      this.hMac.update(var7, 0, var7.length);
      this.initAdditionalInput0(this.hMac);
      this.hMac.doFinal(this.K, 0);
      this.hMac.init(new KeyParameter(this.K));
      this.hMac.update(this.V, 0, this.V.length);
      this.hMac.doFinal(this.V, 0);
      this.hMac.update(this.V, 0, this.V.length);
      this.hMac.update((byte)1);
      this.hMac.update(var6, 0, var6.length);
      this.hMac.update(var7, 0, var7.length);
      this.initAdditionalInput1(this.hMac);
      this.hMac.doFinal(this.K, 0);
      this.hMac.init(new KeyParameter(this.K));
      this.hMac.update(this.V, 0, this.V.length);
      this.hMac.doFinal(this.V, 0);
   }

   @Override
   public BigInteger nextK() {
      byte[] var1 = new byte[BigIntegers.getUnsignedByteLength(this.n)];

      while (true) {
         int var2 = 0;

         while (var2 < var1.length) {
            this.hMac.update(this.V, 0, this.V.length);
            this.hMac.doFinal(this.V, 0);
            int var3 = Math.min(var1.length - var2, this.V.length);
            System.arraycopy(this.V, 0, var1, var2, var3);
            var2 += var3;
         }

         BigInteger var4 = this.bitsToInt(var1);
         if (var4.signum() > 0 && var4.compareTo(this.n) < 0) {
            return var4;
         }

         this.hMac.update(this.V, 0, this.V.length);
         this.hMac.update((byte)0);
         this.hMac.doFinal(this.K, 0);
         this.hMac.init(new KeyParameter(this.K));
         this.hMac.update(this.V, 0, this.V.length);
         this.hMac.doFinal(this.V, 0);
      }
   }

   protected void initAdditionalInput0(HMac var1) {
   }

   protected void initAdditionalInput1(HMac var1) {
   }

   private BigInteger bitsToInt(byte[] var1) {
      int var2 = var1.length * 8;
      int var3 = this.n.bitLength();
      BigInteger var4 = BigIntegers.fromUnsignedByteArray(var1);
      if (var2 > var3) {
         var4 = var4.shiftRight(var2 - var3);
      }

      return var4;
   }
}
