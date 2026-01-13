package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class ECGOST3410Signer implements DSAExt {
   ECKeyParameters key;
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

         this.key = (ECPrivateKeyParameters)var2;
         this.random = CryptoServicesRegistrar.getSecureRandom(var3);
      } else {
         this.key = (ECPublicKeyParameters)var2;
         this.random = null;
      }

      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECGOST3410", this.key, var1));
   }

   @Override
   public BigInteger getOrder() {
      return this.key.getParameters().getN();
   }

   @Override
   public BigInteger[] generateSignature(byte[] var1) {
      byte[] var2 = Arrays.reverse(var1);
      BigInteger var3 = new BigInteger(1, var2);
      ECDomainParameters var4 = this.key.getParameters();
      BigInteger var5 = var4.getN();
      BigInteger var6 = ((ECPrivateKeyParameters)this.key).getD();
      ECMultiplier var9 = this.createBasePointMultiplier();

      while (true) {
         BigInteger var10 = BigIntegers.createRandomBigInteger(var5.bitLength(), this.random);
         if (!var10.equals(ECConstants.ZERO)) {
            ECPoint var11 = var9.multiply(var4.getG(), var10).normalize();
            BigInteger var7 = var11.getAffineXCoord().toBigInteger().mod(var5);
            if (!var7.equals(ECConstants.ZERO)) {
               BigInteger var8 = var10.multiply(var3).add(var6.multiply(var7)).mod(var5);
               if (!var8.equals(ECConstants.ZERO)) {
                  return new BigInteger[]{var7, var8};
               }
            }
         }
      }
   }

   @Override
   public boolean verifySignature(byte[] var1, BigInteger var2, BigInteger var3) {
      byte[] var4 = Arrays.reverse(var1);
      BigInteger var5 = new BigInteger(1, var4);
      BigInteger var6 = this.key.getParameters().getN();
      if (var2.compareTo(ECConstants.ONE) < 0 || var2.compareTo(var6) >= 0) {
         return false;
      } else if (var3.compareTo(ECConstants.ONE) >= 0 && var3.compareTo(var6) < 0) {
         BigInteger var7 = BigIntegers.modOddInverseVar(var6, var5);
         BigInteger var8 = var3.multiply(var7).mod(var6);
         BigInteger var9 = var6.subtract(var2).multiply(var7).mod(var6);
         ECPoint var10 = this.key.getParameters().getG();
         ECPoint var11 = ((ECPublicKeyParameters)this.key).getQ();
         ECPoint var12 = ECAlgorithms.sumOfTwoMultiplies(var10, var8, var11, var9).normalize();
         if (var12.isInfinity()) {
            return false;
         } else {
            BigInteger var13 = var12.getAffineXCoord().toBigInteger().mod(var6);
            return var13.equals(var2);
         }
      } else {
         return false;
      }
   }

   protected ECMultiplier createBasePointMultiplier() {
      return new FixedPointCombMultiplier();
   }
}
