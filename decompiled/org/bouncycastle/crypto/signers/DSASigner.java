package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.params.DSAKeyParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.BigIntegers;

public class DSASigner implements DSAExt {
   private final DSAKCalculator kCalculator;
   private DSAKeyParameters key;
   private SecureRandom random;

   public DSASigner() {
      this.kCalculator = new RandomDSAKCalculator();
   }

   public DSASigner(DSAKCalculator var1) {
      this.kCalculator = var1;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      SecureRandom var3 = null;
      if (var1) {
         if (var2 instanceof ParametersWithRandom) {
            ParametersWithRandom var4 = (ParametersWithRandom)var2;
            this.key = (DSAPrivateKeyParameters)var4.getParameters();
            var3 = var4.getRandom();
         } else {
            this.key = (DSAPrivateKeyParameters)var2;
         }
      } else {
         this.key = (DSAPublicKeyParameters)var2;
      }

      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("DSA", this.key, var1));
      this.random = this.initSecureRandom(var1 && !this.kCalculator.isDeterministic(), var3);
   }

   @Override
   public BigInteger getOrder() {
      return this.key.getParameters().getQ();
   }

   @Override
   public BigInteger[] generateSignature(byte[] var1) {
      DSAParameters var2 = this.key.getParameters();
      BigInteger var3 = var2.getQ();
      BigInteger var4 = this.calculateE(var3, var1);
      BigInteger var5 = ((DSAPrivateKeyParameters)this.key).getX();
      if (this.kCalculator.isDeterministic()) {
         this.kCalculator.init(var3, var5, var1);
      } else {
         this.kCalculator.init(var3, this.random);
      }

      BigInteger var6 = this.kCalculator.nextK();
      BigInteger var7 = var2.getG().modPow(var6.add(this.getRandomizer(var3, this.random)), var2.getP()).mod(var3);
      var6 = BigIntegers.modOddInverse(var3, var6).multiply(var4.add(var5.multiply(var7)));
      BigInteger var8 = var6.mod(var3);
      return new BigInteger[]{var7, var8};
   }

   @Override
   public boolean verifySignature(byte[] var1, BigInteger var2, BigInteger var3) {
      DSAParameters var4 = this.key.getParameters();
      BigInteger var5 = var4.getQ();
      BigInteger var6 = this.calculateE(var5, var1);
      BigInteger var7 = BigInteger.valueOf(0L);
      if (var7.compareTo(var2) >= 0 || var5.compareTo(var2) <= 0) {
         return false;
      } else if (var7.compareTo(var3) < 0 && var5.compareTo(var3) > 0) {
         BigInteger var8 = BigIntegers.modOddInverseVar(var5, var3);
         BigInteger var9 = var6.multiply(var8).mod(var5);
         BigInteger var10 = var2.multiply(var8).mod(var5);
         BigInteger var11 = var4.getP();
         var9 = var4.getG().modPow(var9, var11);
         var10 = ((DSAPublicKeyParameters)this.key).getY().modPow(var10, var11);
         BigInteger var12 = var9.multiply(var10).mod(var11).mod(var5);
         return var12.equals(var2);
      } else {
         return false;
      }
   }

   private BigInteger calculateE(BigInteger var1, byte[] var2) {
      if (var1.bitLength() >= var2.length * 8) {
         return new BigInteger(1, var2);
      } else {
         byte[] var3 = new byte[var1.bitLength() / 8];
         System.arraycopy(var2, 0, var3, 0, var3.length);
         return new BigInteger(1, var3);
      }
   }

   protected SecureRandom initSecureRandom(boolean var1, SecureRandom var2) {
      return var1 ? CryptoServicesRegistrar.getSecureRandom(var2) : null;
   }

   private BigInteger getRandomizer(BigInteger var1, SecureRandom var2) {
      byte var3 = 7;
      return BigIntegers.createRandomBigInteger(var3, CryptoServicesRegistrar.getSecureRandom(var2)).add(BigInteger.valueOf(128L)).multiply(var1);
   }
}
