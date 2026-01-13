package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.params.GOST3410KeyParameters;
import org.bouncycastle.crypto.params.GOST3410Parameters;
import org.bouncycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.bouncycastle.crypto.params.GOST3410PublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class GOST3410Signer implements DSAExt {
   GOST3410KeyParameters key;
   SecureRandom random;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var1) {
         SecureRandom var3 = null;
         if (var2 instanceof ParametersWithRandom) {
            ParametersWithRandom var4 = (ParametersWithRandom)var2;
            var3 = var4.getRandom();
            var2 = var4.getParameters();
         }

         this.key = (GOST3410PrivateKeyParameters)var2;
         this.random = CryptoServicesRegistrar.getSecureRandom(var3);
      } else {
         this.key = (GOST3410PublicKeyParameters)var2;
         this.random = null;
      }

      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("GOST3410", this.key, var1));
   }

   @Override
   public BigInteger getOrder() {
      return this.key.getParameters().getQ();
   }

   @Override
   public BigInteger[] generateSignature(byte[] var1) {
      byte[] var2 = Arrays.reverse(var1);
      BigInteger var3 = new BigInteger(1, var2);
      GOST3410Parameters var4 = this.key.getParameters();

      BigInteger var5;
      do {
         var5 = BigIntegers.createRandomBigInteger(var4.getQ().bitLength(), this.random);
      } while (var5.compareTo(var4.getQ()) >= 0);

      BigInteger var6 = var4.getA().modPow(var5, var4.getP()).mod(var4.getQ());
      BigInteger var7 = var5.multiply(var3).add(((GOST3410PrivateKeyParameters)this.key).getX().multiply(var6)).mod(var4.getQ());
      return new BigInteger[]{var6, var7};
   }

   @Override
   public boolean verifySignature(byte[] var1, BigInteger var2, BigInteger var3) {
      byte[] var4 = Arrays.reverse(var1);
      BigInteger var5 = new BigInteger(1, var4);
      GOST3410Parameters var6 = this.key.getParameters();
      BigInteger var7 = BigInteger.valueOf(0L);
      if (var7.compareTo(var2) >= 0 || var6.getQ().compareTo(var2) <= 0) {
         return false;
      } else if (var7.compareTo(var3) < 0 && var6.getQ().compareTo(var3) > 0) {
         BigInteger var8 = var5.modPow(var6.getQ().subtract(new BigInteger("2")), var6.getQ());
         BigInteger var9 = var3.multiply(var8).mod(var6.getQ());
         BigInteger var10 = var6.getQ().subtract(var2).multiply(var8).mod(var6.getQ());
         var9 = var6.getA().modPow(var9, var6.getP());
         var10 = ((GOST3410PublicKeyParameters)this.key).getY().modPow(var10, var6.getP());
         BigInteger var11 = var9.multiply(var10).mod(var6.getP()).mod(var6.getQ());
         return var11.equals(var2);
      } else {
         return false;
      }
   }
}
