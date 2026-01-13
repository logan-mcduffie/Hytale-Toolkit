package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class PlainDSAEncoding implements DSAEncoding {
   public static final PlainDSAEncoding INSTANCE = new PlainDSAEncoding();

   @Override
   public byte[] encode(BigInteger var1, BigInteger var2, BigInteger var3) {
      int var4 = BigIntegers.getUnsignedByteLength(var1);
      byte[] var5 = new byte[var4 * 2];
      this.encodeValue(var1, var2, var5, 0, var4);
      this.encodeValue(var1, var3, var5, var4, var4);
      return var5;
   }

   @Override
   public BigInteger[] decode(BigInteger var1, byte[] var2) {
      int var3 = BigIntegers.getUnsignedByteLength(var1);
      if (var2.length != var3 * 2) {
         throw new IllegalArgumentException("Encoding has incorrect length");
      } else {
         return new BigInteger[]{this.decodeValue(var1, var2, 0, var3), this.decodeValue(var1, var2, var3, var3)};
      }
   }

   protected BigInteger checkValue(BigInteger var1, BigInteger var2) {
      if (var2.signum() >= 0 && var2.compareTo(var1) < 0) {
         return var2;
      } else {
         throw new IllegalArgumentException("Value out of range");
      }
   }

   protected BigInteger decodeValue(BigInteger var1, byte[] var2, int var3, int var4) {
      byte[] var5 = Arrays.copyOfRange(var2, var3, var3 + var4);
      return this.checkValue(var1, new BigInteger(1, var5));
   }

   private void encodeValue(BigInteger var1, BigInteger var2, byte[] var3, int var4, int var5) {
      byte[] var6 = this.checkValue(var1, var2).toByteArray();
      int var7 = Math.max(0, var6.length - var5);
      int var8 = var6.length - var7;
      int var9 = var5 - var8;
      Arrays.fill(var3, var4, var4 + var9, (byte)0);
      System.arraycopy(var6, var7, var3, var4 + var9, var8);
   }
}
