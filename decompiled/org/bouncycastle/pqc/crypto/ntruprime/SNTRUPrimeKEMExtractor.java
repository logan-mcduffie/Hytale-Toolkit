package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.util.Arrays;

public class SNTRUPrimeKEMExtractor implements EncapsulatedSecretExtractor {
   private final SNTRUPrimePrivateKeyParameters privateKey;

   public SNTRUPrimeKEMExtractor(SNTRUPrimePrivateKeyParameters var1) {
      this.privateKey = var1;
   }

   @Override
   public byte[] extractSecret(byte[] var1) {
      SNTRUPrimeParameters var2 = this.privateKey.getParameters();
      int var3 = var2.getP();
      int var4 = var2.getQ();
      int var5 = var2.getW();
      int var6 = var2.getRoundedPolynomialBytes();
      byte[] var7 = new byte[var3];
      Utils.getDecodedSmallPolynomial(var7, this.privateKey.getF(), var3);
      byte[] var8 = new byte[var3];
      Utils.getDecodedSmallPolynomial(var8, this.privateKey.getGinv(), var3);
      short[] var9 = new short[var3];
      Utils.getRoundedDecodedPolynomial(var9, var1, var3, var4);
      short[] var10 = new short[var3];
      Utils.multiplicationInRQ(var10, var9, var7, var3, var4);
      short[] var11 = new short[var3];
      Utils.scalarMultiplicationInRQ(var11, var10, 3, var4);
      byte[] var12 = new byte[var3];
      Utils.transformRQToR3(var12, var11);
      byte[] var13 = new byte[var3];
      Utils.multiplicationInR3(var13, var12, var8, var3);
      byte[] var14 = new byte[var3];
      Utils.checkForSmallPolynomial(var14, var13, var3, var5);
      byte[] var15 = new byte[(var3 + 3) / 4];
      Utils.getEncodedSmallPolynomial(var15, var14, var3);
      short[] var16 = new short[var3];
      Utils.getDecodedPolynomial(var16, this.privateKey.getPk(), var3, var4);
      short[] var17 = new short[var3];
      Utils.multiplicationInRQ(var17, var16, var14, var3, var4);
      short[] var18 = new short[var3];
      Utils.roundPolynomial(var18, var17);
      byte[] var19 = new byte[var6];
      Utils.getRoundedEncodedPolynomial(var19, var18, var3, var4);
      byte[] var20 = new byte[]{3};
      byte[] var21 = Utils.getHashWithPrefix(var20, var15);
      byte[] var22 = new byte[var21.length / 2 + this.privateKey.getHash().length];
      System.arraycopy(var21, 0, var22, 0, var21.length / 2);
      System.arraycopy(this.privateKey.getHash(), 0, var22, var21.length / 2, this.privateKey.getHash().length);
      byte[] var23 = new byte[]{2};
      byte[] var24 = Utils.getHashWithPrefix(var23, var22);
      byte[] var25 = new byte[var19.length + var24.length / 2];
      System.arraycopy(var19, 0, var25, 0, var19.length);
      System.arraycopy(var24, 0, var25, var19.length, var24.length / 2);
      int var26 = Arrays.areEqual(var1, var25) ? 0 : -1;
      Utils.updateDiffMask(var15, this.privateKey.getRho(), var26);
      byte[] var27 = new byte[]{3};
      byte[] var28 = Utils.getHashWithPrefix(var27, var15);
      byte[] var29 = new byte[var28.length / 2 + var25.length];
      System.arraycopy(var28, 0, var29, 0, var28.length / 2);
      System.arraycopy(var25, 0, var29, var28.length / 2, var25.length);
      byte[] var30 = new byte[]{(byte)(var26 + 1)};
      byte[] var31 = Utils.getHashWithPrefix(var30, var29);
      return Arrays.copyOfRange(var31, 0, var2.getSessionKeySize() / 8);
   }

   @Override
   public int getEncapsulationLength() {
      return this.privateKey.getParameters().getRoundedPolynomialBytes() + 32;
   }
}
