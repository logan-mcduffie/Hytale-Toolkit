package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Memoable;

public class MD2Digest implements ExtendedDigest, Memoable {
   private static final int DIGEST_LENGTH = 16;
   private final CryptoServicePurpose purpose;
   private byte[] X = new byte[48];
   private int xOff;
   private byte[] M = new byte[16];
   private int mOff;
   private byte[] C = new byte[16];
   private int COff;
   private static final byte[] S = new byte[]{
      41,
      46,
      67,
      -55,
      -94,
      -40,
      124,
      1,
      61,
      54,
      84,
      -95,
      -20,
      -16,
      6,
      19,
      98,
      -89,
      5,
      -13,
      -64,
      -57,
      115,
      -116,
      -104,
      -109,
      43,
      -39,
      -68,
      76,
      -126,
      -54,
      30,
      -101,
      87,
      60,
      -3,
      -44,
      -32,
      22,
      103,
      66,
      111,
      24,
      -118,
      23,
      -27,
      18,
      -66,
      78,
      -60,
      -42,
      -38,
      -98,
      -34,
      73,
      -96,
      -5,
      -11,
      -114,
      -69,
      47,
      -18,
      122,
      -87,
      104,
      121,
      -111,
      21,
      -78,
      7,
      63,
      -108,
      -62,
      16,
      -119,
      11,
      34,
      95,
      33,
      -128,
      127,
      93,
      -102,
      90,
      -112,
      50,
      39,
      53,
      62,
      -52,
      -25,
      -65,
      -9,
      -105,
      3,
      -1,
      25,
      48,
      -77,
      72,
      -91,
      -75,
      -47,
      -41,
      94,
      -110,
      42,
      -84,
      86,
      -86,
      -58,
      79,
      -72,
      56,
      -46,
      -106,
      -92,
      125,
      -74,
      118,
      -4,
      107,
      -30,
      -100,
      116,
      4,
      -15,
      69,
      -99,
      112,
      89,
      100,
      113,
      -121,
      32,
      -122,
      91,
      -49,
      101,
      -26,
      45,
      -88,
      2,
      27,
      96,
      37,
      -83,
      -82,
      -80,
      -71,
      -10,
      28,
      70,
      97,
      105,
      52,
      64,
      126,
      15,
      85,
      71,
      -93,
      35,
      -35,
      81,
      -81,
      58,
      -61,
      92,
      -7,
      -50,
      -70,
      -59,
      -22,
      38,
      44,
      83,
      13,
      110,
      -123,
      40,
      -124,
      9,
      -45,
      -33,
      -51,
      -12,
      65,
      -127,
      77,
      82,
      106,
      -36,
      55,
      -56,
      108,
      -63,
      -85,
      -6,
      36,
      -31,
      123,
      8,
      12,
      -67,
      -79,
      74,
      120,
      -120,
      -107,
      -117,
      -29,
      99,
      -24,
      109,
      -23,
      -53,
      -43,
      -2,
      59,
      0,
      29,
      57,
      -14,
      -17,
      -73,
      14,
      102,
      88,
      -48,
      -28,
      -90,
      119,
      114,
      -8,
      -21,
      117,
      75,
      10,
      49,
      68,
      80,
      -76,
      -113,
      -19,
      31,
      26,
      -37,
      -103,
      -115,
      51,
      -97,
      17,
      -125,
      20
   };

   public MD2Digest() {
      this(CryptoServicePurpose.ANY);
   }

   public MD2Digest(CryptoServicePurpose var1) {
      this.purpose = var1;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, 64, var1));
      this.reset();
   }

   public MD2Digest(MD2Digest var1) {
      this.purpose = var1.purpose;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, 64, this.purpose));
      this.copyIn(var1);
   }

   private void copyIn(MD2Digest var1) {
      System.arraycopy(var1.X, 0, this.X, 0, var1.X.length);
      this.xOff = var1.xOff;
      System.arraycopy(var1.M, 0, this.M, 0, var1.M.length);
      this.mOff = var1.mOff;
      System.arraycopy(var1.C, 0, this.C, 0, var1.C.length);
      this.COff = var1.COff;
   }

   @Override
   public String getAlgorithmName() {
      return "MD2";
   }

   @Override
   public int getDigestSize() {
      return 16;
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      byte var3 = (byte)(this.M.length - this.mOff);

      for (int var4 = this.mOff; var4 < this.M.length; var4++) {
         this.M[var4] = var3;
      }

      this.processCheckSum(this.M);
      this.processBlock(this.M);
      this.processBlock(this.C);
      System.arraycopy(this.X, this.xOff, var1, var2, 16);
      this.reset();
      return 16;
   }

   @Override
   public void reset() {
      this.xOff = 0;

      for (int var1 = 0; var1 != this.X.length; var1++) {
         this.X[var1] = 0;
      }

      this.mOff = 0;

      for (int var2 = 0; var2 != this.M.length; var2++) {
         this.M[var2] = 0;
      }

      this.COff = 0;

      for (int var3 = 0; var3 != this.C.length; var3++) {
         this.C[var3] = 0;
      }
   }

   @Override
   public void update(byte var1) {
      this.M[this.mOff++] = var1;
      if (this.mOff == 16) {
         this.processCheckSum(this.M);
         this.processBlock(this.M);
         this.mOff = 0;
      }
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      while (this.mOff != 0 && var3 > 0) {
         this.update(var1[var2]);
         var2++;
         var3--;
      }

      while (var3 >= 16) {
         System.arraycopy(var1, var2, this.M, 0, 16);
         this.processCheckSum(this.M);
         this.processBlock(this.M);
         var3 -= 16;
         var2 += 16;
      }

      while (var3 > 0) {
         this.update(var1[var2]);
         var2++;
         var3--;
      }
   }

   protected void processCheckSum(byte[] var1) {
      byte var2 = this.C[15];

      for (int var3 = 0; var3 < 16; var3++) {
         this.C[var3] = (byte)(this.C[var3] ^ S[(var1[var3] ^ var2) & 0xFF]);
         var2 = this.C[var3];
      }
   }

   protected void processBlock(byte[] var1) {
      for (int var2 = 0; var2 < 16; var2++) {
         this.X[var2 + 16] = var1[var2];
         this.X[var2 + 32] = (byte)(var1[var2] ^ this.X[var2]);
      }

      int var5 = 0;

      for (int var3 = 0; var3 < 18; var3++) {
         for (int var4 = 0; var4 < 48; var4++) {
            byte var6 = this.X[var4] = (byte)(this.X[var4] ^ S[var5]);
            var5 = var6 & 255;
         }

         var5 = (var5 + var3) % 256;
      }
   }

   @Override
   public int getByteLength() {
      return 16;
   }

   @Override
   public Memoable copy() {
      return new MD2Digest(this);
   }

   @Override
   public void reset(Memoable var1) {
      MD2Digest var2 = (MD2Digest)var1;
      this.copyIn(var2);
   }
}
