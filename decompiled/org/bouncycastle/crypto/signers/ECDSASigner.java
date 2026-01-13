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
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.BigIntegers;

public class ECDSASigner implements ECConstants, DSAExt {
   private final DSAKCalculator kCalculator;
   private ECKeyParameters key;
   private SecureRandom random;

   public ECDSASigner() {
      this.kCalculator = new RandomDSAKCalculator();
   }

   public ECDSASigner(DSAKCalculator var1) {
      this.kCalculator = var1;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      SecureRandom var3 = null;
      if (var1) {
         if (var2 instanceof ParametersWithRandom) {
            ParametersWithRandom var4 = (ParametersWithRandom)var2;
            this.key = (ECPrivateKeyParameters)var4.getParameters();
            var3 = var4.getRandom();
         } else {
            this.key = (ECPrivateKeyParameters)var2;
         }
      } else {
         this.key = (ECPublicKeyParameters)var2;
      }

      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECDSA", this.key, var1));
      this.random = this.initSecureRandom(var1 && !this.kCalculator.isDeterministic(), var3);
   }

   @Override
   public BigInteger getOrder() {
      return this.key.getParameters().getN();
   }

   @Override
   public BigInteger[] generateSignature(byte[] var1) {
      ECDomainParameters var2 = this.key.getParameters();
      BigInteger var3 = var2.getN();
      BigInteger var4 = this.calculateE(var3, var1);
      BigInteger var5 = ((ECPrivateKeyParameters)this.key).getD();
      if (this.kCalculator.isDeterministic()) {
         this.kCalculator.init(var3, var5, var1);
      } else {
         this.kCalculator.init(var3, this.random);
      }

      ECMultiplier var8 = this.createBasePointMultiplier();

      while (true) {
         BigInteger var9 = this.kCalculator.nextK();
         ECPoint var10 = var8.multiply(var2.getG(), var9).normalize();
         BigInteger var6 = var10.getAffineXCoord().toBigInteger().mod(var3);
         if (!var6.equals(ZERO)) {
            BigInteger var7 = BigIntegers.modOddInverse(var3, var9).multiply(var4.add(var5.multiply(var6))).mod(var3);
            if (!var7.equals(ZERO)) {
               return new BigInteger[]{var6, var7};
            }
         }
      }
   }

   @Override
   public boolean verifySignature(byte[] var1, BigInteger var2, BigInteger var3) {
      ECDomainParameters var4 = this.key.getParameters();
      BigInteger var5 = var4.getN();
      BigInteger var6 = this.calculateE(var5, var1);
      if (var2.compareTo(ONE) < 0 || var2.compareTo(var5) >= 0) {
         return false;
      } else if (var3.compareTo(ONE) >= 0 && var3.compareTo(var5) < 0) {
         BigInteger var7 = BigIntegers.modOddInverseVar(var5, var3);
         BigInteger var8 = var6.multiply(var7).mod(var5);
         BigInteger var9 = var2.multiply(var7).mod(var5);
         ECPoint var10 = var4.getG();
         ECPoint var11 = ((ECPublicKeyParameters)this.key).getQ();
         ECPoint var12 = ECAlgorithms.sumOfTwoMultiplies(var10, var8, var11, var9);
         if (var12.isInfinity()) {
            return false;
         } else {
            ECCurve var13 = var12.getCurve();
            if (var13 != null) {
               BigInteger var14 = var13.getCofactor();
               if (var14 != null && var14.compareTo(EIGHT) <= 0) {
                  ECFieldElement var15 = this.getDenominator(var13.getCoordinateSystem(), var12);
                  if (var15 != null && !var15.isZero()) {
                     for (ECFieldElement var16 = var12.getXCoord(); var13.isValidFieldElement(var2); var2 = var2.add(var5)) {
                        ECFieldElement var17 = var13.fromBigInteger(var2).multiply(var15);
                        if (var17.equals(var16)) {
                           return true;
                        }
                     }

                     return false;
                  }
               }
            }

            BigInteger var18 = var12.normalize().getAffineXCoord().toBigInteger().mod(var5);
            return var18.equals(var2);
         }
      } else {
         return false;
      }
   }

   protected BigInteger calculateE(BigInteger var1, byte[] var2) {
      int var3 = var1.bitLength();
      int var4 = var2.length * 8;
      BigInteger var5 = new BigInteger(1, var2);
      if (var3 < var4) {
         var5 = var5.shiftRight(var4 - var3);
      }

      return var5;
   }

   protected ECMultiplier createBasePointMultiplier() {
      return new FixedPointCombMultiplier();
   }

   protected ECFieldElement getDenominator(int var1, ECPoint var2) {
      switch (var1) {
         case 1:
         case 6:
         case 7:
            return var2.getZCoord(0);
         case 2:
         case 3:
         case 4:
            return var2.getZCoord(0).square();
         case 5:
         default:
            return null;
      }
   }

   protected SecureRandom initSecureRandom(boolean var1, SecureRandom var2) {
      return var1 ? CryptoServicesRegistrar.getSecureRandom(var2) : null;
   }
}
