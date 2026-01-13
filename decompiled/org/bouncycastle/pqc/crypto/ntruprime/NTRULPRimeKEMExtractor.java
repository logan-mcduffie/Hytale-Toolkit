package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.util.Arrays;

public class NTRULPRimeKEMExtractor implements EncapsulatedSecretExtractor {
   private final NTRULPRimePrivateKeyParameters privateKey;

   public NTRULPRimeKEMExtractor(NTRULPRimePrivateKeyParameters var1) {
      this.privateKey = var1;
   }

   @Override
   public byte[] extractSecret(byte[] var1) {
      NTRULPRimeParameters var2 = this.privateKey.getParameters();
      int var3 = var2.getP();
      int var4 = var2.getQ();
      int var5 = var2.getW();
      int var6 = var2.getRoundedPolynomialBytes();
      int var7 = var2.getTau0();
      int var8 = var2.getTau1();
      int var9 = var2.getTau2();
      int var10 = var2.getTau3();
      byte[] var11 = new byte[var3];
      Utils.getDecodedSmallPolynomial(var11, this.privateKey.getEncoded(), var3);
      byte[] var12 = new byte[var6];
      System.arraycopy(var1, 0, var12, 0, var6);
      short[] var13 = new short[var3];
      Utils.getRoundedDecodedPolynomial(var13, var12, var3, var4);
      byte[] var14 = new byte[128];
      System.arraycopy(var1, var6, var14, 0, var14.length);
      byte[] var15 = new byte[256];
      Utils.getTopDecodedPolynomial(var15, var14);
      short[] var16 = new short[var3];
      Utils.multiplicationInRQ(var16, var13, var11, var3, var4);
      byte[] var17 = new byte[256];
      Utils.right(var17, var16, var15, var4, var5, var9, var10);
      byte[] var18 = new byte[32];
      Utils.getEncodedInputs(var18, var17);
      byte[] var19 = new byte[var2.getPublicKeyBytes() - 32];
      System.arraycopy(this.privateKey.getPk(), 32, var19, 0, var19.length);
      short[] var20 = new short[var3];
      Utils.getRoundedDecodedPolynomial(var20, var19, var3, var4);
      byte[] var21 = new byte[32];
      System.arraycopy(this.privateKey.getPk(), 0, var21, 0, var21.length);
      short[] var22 = new short[var3];
      Utils.generatePolynomialInRQFromSeed(var22, var21, var3, var4);
      byte[] var23 = new byte[]{5};
      byte[] var24 = Utils.getHashWithPrefix(var23, var18);
      byte[] var25 = Arrays.copyOfRange(var24, 0, var24.length / 2);
      int[] var26 = new int[var3];
      Utils.expand(var26, var25);
      byte[] var27 = new byte[var3];
      Utils.sortGenerateShortPolynomial(var27, var26, var3, var5);
      short[] var28 = new short[var3];
      Utils.multiplicationInRQ(var28, var22, var27, var3, var4);
      short[] var29 = new short[var3];
      Utils.roundPolynomial(var29, var28);
      byte[] var30 = new byte[var6];
      Utils.getRoundedEncodedPolynomial(var30, var29, var3, var4);
      short[] var31 = new short[var3];
      Utils.multiplicationInRQ(var31, var20, var27, var3, var4);
      byte[] var32 = new byte[256];
      Utils.top(var32, var31, var17, var4, var7, var8);
      byte[] var33 = new byte[128];
      Utils.getTopEncodedPolynomial(var33, var15);
      byte[] var34 = new byte[var18.length + this.privateKey.getHash().length];
      System.arraycopy(var18, 0, var34, 0, var18.length);
      System.arraycopy(this.privateKey.getHash(), 0, var34, var18.length, this.privateKey.getHash().length);
      byte[] var35 = new byte[]{2};
      byte[] var36 = Utils.getHashWithPrefix(var35, var34);
      byte[] var37 = new byte[var12.length + var14.length + var36.length / 2];
      System.arraycopy(var12, 0, var37, 0, var12.length);
      System.arraycopy(var14, 0, var37, var12.length, var14.length);
      System.arraycopy(var36, 0, var37, var12.length + var14.length, var36.length / 2);
      int var38 = Arrays.areEqual(var1, var37) ? 0 : -1;
      Utils.updateDiffMask(var18, this.privateKey.getRho(), var38);
      byte[] var39 = new byte[var18.length + var37.length];
      System.arraycopy(var18, 0, var39, 0, var18.length);
      System.arraycopy(var37, 0, var39, var18.length, var37.length);
      byte[] var40 = new byte[]{1};
      byte[] var41 = Utils.getHashWithPrefix(var40, var39);
      return Arrays.copyOfRange(var41, 0, var2.getSessionKeySize() / 8);
   }

   @Override
   public int getEncapsulationLength() {
      return this.privateKey.getParameters().getRoundedPolynomialBytes() + 128 + 32;
   }
}
