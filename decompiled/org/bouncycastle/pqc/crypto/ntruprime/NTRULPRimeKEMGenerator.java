package org.bouncycastle.pqc.crypto.ntruprime;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;
import org.bouncycastle.util.Arrays;

public class NTRULPRimeKEMGenerator implements EncapsulatedSecretGenerator {
   private final SecureRandom random;

   public NTRULPRimeKEMGenerator(SecureRandom var1) {
      this.random = var1;
   }

   @Override
   public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter var1) {
      NTRULPRimePublicKeyParameters var2 = (NTRULPRimePublicKeyParameters)var1;
      NTRULPRimeParameters var3 = var2.getParameters();
      int var4 = var3.getP();
      int var5 = var3.getQ();
      int var6 = var3.getW();
      int var7 = var3.getRoundedPolynomialBytes();
      int var8 = var3.getTau0();
      int var9 = var3.getTau1();
      byte[] var10 = new byte[]{4};
      byte[] var11 = Utils.getHashWithPrefix(var10, var2.getEncoded());
      byte[] var12 = new byte[256];
      Utils.getRandomInputs(this.random, var12);
      byte[] var13 = new byte[32];
      Utils.getEncodedInputs(var13, var12);
      short[] var14 = new short[var4];
      Utils.getRoundedDecodedPolynomial(var14, var2.getRoundEncA(), var4, var5);
      short[] var15 = new short[var4];
      Utils.generatePolynomialInRQFromSeed(var15, var2.getSeed(), var4, var5);
      byte[] var16 = new byte[]{5};
      byte[] var17 = Utils.getHashWithPrefix(var16, var13);
      byte[] var18 = Arrays.copyOfRange(var17, 0, var17.length / 2);
      int[] var19 = new int[var4];
      Utils.expand(var19, var18);
      byte[] var20 = new byte[var4];
      Utils.sortGenerateShortPolynomial(var20, var19, var4, var6);
      short[] var21 = new short[var4];
      Utils.multiplicationInRQ(var21, var15, var20, var4, var5);
      short[] var22 = new short[var4];
      Utils.roundPolynomial(var22, var21);
      byte[] var23 = new byte[var7];
      Utils.getRoundedEncodedPolynomial(var23, var22, var4, var5);
      short[] var24 = new short[var4];
      Utils.multiplicationInRQ(var24, var14, var20, var4, var5);
      byte[] var25 = new byte[256];
      Utils.top(var25, var24, var12, var5, var8, var9);
      byte[] var26 = new byte[128];
      Utils.getTopEncodedPolynomial(var26, var25);
      byte[] var27 = new byte[var13.length + var11.length / 2];
      System.arraycopy(var13, 0, var27, 0, var13.length);
      System.arraycopy(var11, 0, var27, var13.length, var11.length / 2);
      byte[] var28 = new byte[]{2};
      byte[] var29 = Utils.getHashWithPrefix(var28, var27);
      byte[] var30 = new byte[var23.length + var26.length + var29.length / 2];
      System.arraycopy(var23, 0, var30, 0, var23.length);
      System.arraycopy(var26, 0, var30, var23.length, var26.length);
      System.arraycopy(var29, 0, var30, var23.length + var26.length, var29.length / 2);
      byte[] var31 = new byte[var13.length + var30.length];
      System.arraycopy(var13, 0, var31, 0, var13.length);
      System.arraycopy(var30, 0, var31, var13.length, var30.length);
      byte[] var32 = new byte[]{1};
      byte[] var33 = Utils.getHashWithPrefix(var32, var31);
      byte[] var34 = Arrays.copyOfRange(var33, 0, var3.getSessionKeySize() / 8);
      return new SecretWithEncapsulationImpl(var34, var30);
   }
}
