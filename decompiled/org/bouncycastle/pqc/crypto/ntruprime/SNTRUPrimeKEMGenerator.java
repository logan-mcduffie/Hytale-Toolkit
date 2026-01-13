package org.bouncycastle.pqc.crypto.ntruprime;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;
import org.bouncycastle.util.Arrays;

public class SNTRUPrimeKEMGenerator implements EncapsulatedSecretGenerator {
   private final SecureRandom random;

   public SNTRUPrimeKEMGenerator(SecureRandom var1) {
      this.random = var1;
   }

   @Override
   public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter var1) {
      SNTRUPrimePublicKeyParameters var2 = (SNTRUPrimePublicKeyParameters)var1;
      SNTRUPrimeParameters var3 = var2.getParameters();
      int var4 = var3.getP();
      int var5 = var3.getQ();
      int var6 = var3.getW();
      int var7 = var3.getRoundedPolynomialBytes();
      byte[] var8 = new byte[]{4};
      byte[] var9 = Utils.getHashWithPrefix(var8, var2.getEncoded());
      byte[] var10 = new byte[var4];
      Utils.getRandomShortPolynomial(this.random, var10, var4, var6);
      byte[] var11 = new byte[(var4 + 3) / 4];
      Utils.getEncodedSmallPolynomial(var11, var10, var4);
      short[] var12 = new short[var4];
      Utils.getDecodedPolynomial(var12, var2.getEncH(), var4, var5);
      short[] var13 = new short[var4];
      Utils.multiplicationInRQ(var13, var12, var10, var4, var5);
      short[] var14 = new short[var4];
      Utils.roundPolynomial(var14, var13);
      byte[] var15 = new byte[var7];
      Utils.getRoundedEncodedPolynomial(var15, var14, var4, var5);
      byte[] var16 = new byte[]{3};
      byte[] var17 = Utils.getHashWithPrefix(var16, var11);
      byte[] var18 = new byte[var17.length / 2 + var9.length / 2];
      System.arraycopy(var17, 0, var18, 0, var17.length / 2);
      System.arraycopy(var9, 0, var18, var17.length / 2, var9.length / 2);
      byte[] var19 = new byte[]{2};
      byte[] var20 = Utils.getHashWithPrefix(var19, var18);
      byte[] var21 = new byte[var15.length + var20.length / 2];
      System.arraycopy(var15, 0, var21, 0, var15.length);
      System.arraycopy(var20, 0, var21, var15.length, var20.length / 2);
      byte[] var22 = new byte[]{3};
      byte[] var23 = Utils.getHashWithPrefix(var22, var11);
      byte[] var24 = new byte[var23.length / 2 + var21.length];
      System.arraycopy(var23, 0, var24, 0, var23.length / 2);
      System.arraycopy(var21, 0, var24, var23.length / 2, var21.length);
      byte[] var25 = new byte[]{1};
      byte[] var26 = Utils.getHashWithPrefix(var25, var24);
      byte[] var27 = Arrays.copyOfRange(var26, 0, var3.getSessionKeySize() / 8);
      return new SecretWithEncapsulationImpl(var27, var21);
   }
}
