package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.util.Arrays;

class MLKEMIndCpa {
   private final MLKEMEngine engine;
   private final int kyberK;
   private final int indCpaPublicKeyBytes;
   private final int polyVecBytes;
   private final int indCpaBytes;
   private final int polyVecCompressedBytes;
   private final int polyCompressedBytes;
   private Symmetric symmetric;
   public final int KyberGenerateMatrixNBlocks;

   public MLKEMIndCpa(MLKEMEngine var1) {
      this.engine = var1;
      this.kyberK = var1.getKyberK();
      this.indCpaPublicKeyBytes = var1.getKyberPublicKeyBytes();
      this.polyVecBytes = var1.getKyberPolyVecBytes();
      this.indCpaBytes = var1.getKyberIndCpaBytes();
      this.polyVecCompressedBytes = var1.getKyberPolyVecCompressedBytes();
      this.polyCompressedBytes = var1.getKyberPolyCompressedBytes();
      this.symmetric = var1.getSymmetric();
      this.KyberGenerateMatrixNBlocks = (472 + this.symmetric.xofBlockBytes) / this.symmetric.xofBlockBytes;
   }

   byte[][] generateKeyPair(byte[] var1) {
      PolyVec var2 = new PolyVec(this.engine);
      PolyVec var3 = new PolyVec(this.engine);
      PolyVec var4 = new PolyVec(this.engine);
      byte[] var5 = new byte[64];
      this.symmetric.hash_g(var5, Arrays.append(var1, (byte)this.kyberK));
      byte[] var6 = new byte[32];
      byte[] var7 = new byte[32];
      System.arraycopy(var5, 0, var6, 0, 32);
      System.arraycopy(var5, 32, var7, 0, 32);
      byte var8 = 0;
      PolyVec[] var9 = new PolyVec[this.kyberK];

      for (int var10 = 0; var10 < this.kyberK; var10++) {
         var9[var10] = new PolyVec(this.engine);
      }

      this.generateMatrix(var9, var6, false);

      for (int var11 = 0; var11 < this.kyberK; var11++) {
         var2.getVectorIndex(var11).getEta1Noise(var7, var8);
         var8++;
      }

      for (int var12 = 0; var12 < this.kyberK; var12++) {
         var4.getVectorIndex(var12).getEta1Noise(var7, var8);
         var8++;
      }

      var2.polyVecNtt();
      var4.polyVecNtt();

      for (int var13 = 0; var13 < this.kyberK; var13++) {
         PolyVec.pointwiseAccountMontgomery(var3.getVectorIndex(var13), var9[var13], var2, this.engine);
         var3.getVectorIndex(var13).convertToMont();
      }

      var3.addPoly(var4);
      var3.reducePoly();
      return new byte[][]{this.packPublicKey(var3, var6), this.packSecretKey(var2)};
   }

   public byte[] encrypt(byte[] var1, byte[] var2, byte[] var3) {
      byte var6 = 0;
      PolyVec var7 = new PolyVec(this.engine);
      PolyVec var8 = new PolyVec(this.engine);
      PolyVec var9 = new PolyVec(this.engine);
      PolyVec var10 = new PolyVec(this.engine);
      PolyVec[] var11 = new PolyVec[this.engine.getKyberK()];
      Poly var12 = new Poly(this.engine);
      Poly var13 = new Poly(this.engine);
      Poly var14 = new Poly(this.engine);
      byte[] var5 = this.unpackPublicKey(var8, var1);
      var14.fromMsg(var2);

      for (int var4 = 0; var4 < this.kyberK; var4++) {
         var11[var4] = new PolyVec(this.engine);
      }

      this.generateMatrix(var11, var5, true);

      for (int var16 = 0; var16 < this.kyberK; var16++) {
         var7.getVectorIndex(var16).getEta1Noise(var3, var6);
         var6++;
      }

      for (int var17 = 0; var17 < this.kyberK; var17++) {
         var9.getVectorIndex(var17).getEta2Noise(var3, var6);
         var6++;
      }

      var12.getEta2Noise(var3, var6);
      var7.polyVecNtt();

      for (int var18 = 0; var18 < this.kyberK; var18++) {
         PolyVec.pointwiseAccountMontgomery(var10.getVectorIndex(var18), var11[var18], var7, this.engine);
      }

      PolyVec.pointwiseAccountMontgomery(var13, var8, var7, this.engine);
      var10.polyVecInverseNttToMont();
      var13.polyInverseNttToMont();
      var10.addPoly(var9);
      var13.addCoeffs(var12);
      var13.addCoeffs(var14);
      var10.reducePoly();
      var13.reduce();
      return this.packCipherText(var10, var13);
   }

   private byte[] packCipherText(PolyVec var1, Poly var2) {
      byte[] var3 = new byte[this.indCpaBytes];
      System.arraycopy(var1.compressPolyVec(), 0, var3, 0, this.polyVecCompressedBytes);
      System.arraycopy(var2.compressPoly(), 0, var3, this.polyVecCompressedBytes, this.polyCompressedBytes);
      return var3;
   }

   private void unpackCipherText(PolyVec var1, Poly var2, byte[] var3) {
      byte[] var4 = Arrays.copyOfRange(var3, 0, this.engine.getKyberPolyVecCompressedBytes());
      var1.decompressPolyVec(var4);
      byte[] var5 = Arrays.copyOfRange(var3, this.engine.getKyberPolyVecCompressedBytes(), var3.length);
      var2.decompressPoly(var5);
   }

   public byte[] packPublicKey(PolyVec var1, byte[] var2) {
      byte[] var3 = new byte[this.indCpaPublicKeyBytes];
      System.arraycopy(var1.toBytes(), 0, var3, 0, this.polyVecBytes);
      System.arraycopy(var2, 0, var3, this.polyVecBytes, 32);
      return var3;
   }

   public byte[] unpackPublicKey(PolyVec var1, byte[] var2) {
      byte[] var3 = new byte[32];
      var1.fromBytes(var2);
      System.arraycopy(var2, this.polyVecBytes, var3, 0, 32);
      return var3;
   }

   public byte[] packSecretKey(PolyVec var1) {
      return var1.toBytes();
   }

   public void unpackSecretKey(PolyVec var1, byte[] var2) {
      var1.fromBytes(var2);
   }

   public void generateMatrix(PolyVec[] var1, byte[] var2, boolean var3) {
      byte[] var9 = new byte[this.KyberGenerateMatrixNBlocks * this.symmetric.xofBlockBytes + 2];

      for (int var4 = 0; var4 < this.kyberK; var4++) {
         for (int var5 = 0; var5 < this.kyberK; var5++) {
            if (var3) {
               this.symmetric.xofAbsorb(var2, (byte)var4, (byte)var5);
            } else {
               this.symmetric.xofAbsorb(var2, (byte)var5, (byte)var4);
            }

            this.symmetric.xofSqueezeBlocks(var9, 0, this.symmetric.xofBlockBytes * this.KyberGenerateMatrixNBlocks);
            int var10 = this.KyberGenerateMatrixNBlocks * this.symmetric.xofBlockBytes;

            for (int var7 = rejectionSampling(var1[var4].getVectorIndex(var5), 0, 256, var9, var10);
               var7 < 256;
               var7 += rejectionSampling(var1[var4].getVectorIndex(var5), var7, 256 - var7, var9, var10)
            ) {
               int var8 = var10 % 3;

               for (int var6 = 0; var6 < var8; var6++) {
                  var9[var6] = var9[var10 - var8 + var6];
               }

               this.symmetric.xofSqueezeBlocks(var9, var8, this.symmetric.xofBlockBytes * 2);
               var10 = var8 + this.symmetric.xofBlockBytes;
            }
         }
      }
   }

   private static int rejectionSampling(Poly var0, int var1, int var2, byte[] var3, int var4) {
      byte var6 = 0;
      int var5 = 0;

      while (var5 < var2 && var6 + 3 <= var4) {
         short var7 = (short)(((short)(var3[var6] & 255) >> 0 | (short)(var3[var6 + 1] & 255) << 8) & 4095);
         short var8 = (short)(((short)(var3[var6 + 1] & 255) >> 4 | (short)(var3[var6 + 2] & 255) << 4) & 4095);
         var6 += 3;
         if (var7 < 3329) {
            var0.setCoeffIndex(var1 + var5, var7);
            var5++;
         }

         if (var5 < var2 && var8 < 3329) {
            var0.setCoeffIndex(var1 + var5, var8);
            var5++;
         }
      }

      return var5;
   }

   public byte[] decrypt(byte[] var1, byte[] var2) {
      byte[] var3 = new byte[MLKEMEngine.getKyberIndCpaMsgBytes()];
      PolyVec var4 = new PolyVec(this.engine);
      PolyVec var5 = new PolyVec(this.engine);
      Poly var6 = new Poly(this.engine);
      Poly var7 = new Poly(this.engine);
      this.unpackCipherText(var4, var6, var2);
      this.unpackSecretKey(var5, var1);
      var4.polyVecNtt();
      PolyVec.pointwiseAccountMontgomery(var7, var5, var4, this.engine);
      var7.polyInverseNttToMont();
      var7.polySubtract(var6);
      var7.reduce();
      return var7.toMsg();
   }
}
