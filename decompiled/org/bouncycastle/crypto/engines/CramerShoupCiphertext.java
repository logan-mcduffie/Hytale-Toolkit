package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class CramerShoupCiphertext {
   BigInteger u1;
   BigInteger u2;
   BigInteger e;
   BigInteger v;

   public CramerShoupCiphertext() {
   }

   public CramerShoupCiphertext(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4) {
      this.u1 = var1;
      this.u2 = var2;
      this.e = var3;
      this.v = var4;
   }

   public CramerShoupCiphertext(byte[] var1) {
      int var2 = 0;
      int var3 = Pack.bigEndianToInt(var1, var2);
      var2 += 4;
      byte[] var4 = Arrays.copyOfRange(var1, var2, var2 + var3);
      var2 += var3;
      this.u1 = new BigInteger(var4);
      var3 = Pack.bigEndianToInt(var1, var2);
      var2 += 4;
      var4 = Arrays.copyOfRange(var1, var2, var2 + var3);
      var2 += var3;
      this.u2 = new BigInteger(var4);
      var3 = Pack.bigEndianToInt(var1, var2);
      var2 += 4;
      var4 = Arrays.copyOfRange(var1, var2, var2 + var3);
      var2 += var3;
      this.e = new BigInteger(var4);
      var3 = Pack.bigEndianToInt(var1, var2);
      var2 += 4;
      var4 = Arrays.copyOfRange(var1, var2, var2 + var3);
      var2 += var3;
      this.v = new BigInteger(var4);
   }

   public BigInteger getU1() {
      return this.u1;
   }

   public void setU1(BigInteger var1) {
      this.u1 = var1;
   }

   public BigInteger getU2() {
      return this.u2;
   }

   public void setU2(BigInteger var1) {
      this.u2 = var1;
   }

   public BigInteger getE() {
      return this.e;
   }

   public void setE(BigInteger var1) {
      this.e = var1;
   }

   public BigInteger getV() {
      return this.v;
   }

   public void setV(BigInteger var1) {
      this.v = var1;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("u1: " + this.u1.toString());
      var1.append("\nu2: " + this.u2.toString());
      var1.append("\ne: " + this.e.toString());
      var1.append("\nv: " + this.v.toString());
      return var1.toString();
   }

   public byte[] toByteArray() {
      byte[] var1 = this.u1.toByteArray();
      int var2 = var1.length;
      byte[] var3 = this.u2.toByteArray();
      int var4 = var3.length;
      byte[] var5 = this.e.toByteArray();
      int var6 = var5.length;
      byte[] var7 = this.v.toByteArray();
      int var8 = var7.length;
      int var9 = 0;
      byte[] var10 = new byte[var2 + var4 + var6 + var8 + 16];
      Pack.intToBigEndian(var2, var10, var9);
      var9 += 4;
      System.arraycopy(var1, 0, var10, var9, var2);
      var9 += var2;
      Pack.intToBigEndian(var4, var10, var9);
      var9 += 4;
      System.arraycopy(var3, 0, var10, var9, var4);
      var9 += var4;
      Pack.intToBigEndian(var6, var10, var9);
      var9 += 4;
      System.arraycopy(var5, 0, var10, var9, var6);
      var9 += var6;
      Pack.intToBigEndian(var8, var10, var9);
      var9 += 4;
      System.arraycopy(var7, 0, var10, var9, var8);
      var9 += var8;
      return var10;
   }
}
