package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.BigIntegers;

public class ECNRSigner implements DSAExt {
   private boolean forSigning;
   private ECKeyParameters key;
   private SecureRandom random;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      this.forSigning = var1;
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

      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECNR", this.key, var1));
   }

   @Override
   public BigInteger getOrder() {
      return this.key.getParameters().getN();
   }

   @Override
   public BigInteger[] generateSignature(byte[] var1) {
      if (!this.forSigning) {
         throw new IllegalStateException("not initialised for signing");
      } else {
         BigInteger var2 = this.getOrder();
         BigInteger var3 = new BigInteger(1, var1);
         ECPrivateKeyParameters var4 = (ECPrivateKeyParameters)this.key;
         if (var3.compareTo(var2) >= 0) {
            throw new DataLengthException("input too large for ECNR key");
         } else {
            Object var5 = null;
            Object var6 = null;

            AsymmetricCipherKeyPair var7;
            do {
               ECKeyPairGenerator var8 = new ECKeyPairGenerator();
               var8.init(new ECKeyGenerationParameters(var4.getParameters(), this.random));
               var7 = var8.generateKeyPair();
               ECPublicKeyParameters var9 = (ECPublicKeyParameters)var7.getPublic();
               BigInteger var10 = var9.getQ().getAffineXCoord().toBigInteger();
               var5 = var10.add(var3).mod(var2);
            } while (var5.equals(ECConstants.ZERO));

            BigInteger var13 = var4.getD();
            BigInteger var14 = ((ECPrivateKeyParameters)var7.getPrivate()).getD();
            var6 = var14.subtract(var5.multiply(var13)).mod(var2);
            return new BigInteger[]{(BigInteger)var5, (BigInteger)var6};
         }
      }
   }

   @Override
   public boolean verifySignature(byte[] var1, BigInteger var2, BigInteger var3) {
      if (this.forSigning) {
         throw new IllegalStateException("not initialised for verifying");
      } else {
         ECPublicKeyParameters var4 = (ECPublicKeyParameters)this.key;
         BigInteger var5 = var4.getParameters().getN();
         int var6 = var5.bitLength();
         BigInteger var7 = new BigInteger(1, var1);
         int var8 = var7.bitLength();
         if (var8 > var6) {
            throw new DataLengthException("input too large for ECNR key.");
         } else {
            BigInteger var9 = this.extractT(var4, var2, var3);
            return var9 != null && var9.equals(var7.mod(var5));
         }
      }
   }

   public byte[] getRecoveredMessage(BigInteger var1, BigInteger var2) {
      if (this.forSigning) {
         throw new IllegalStateException("not initialised for verifying/recovery");
      } else {
         BigInteger var3 = this.extractT((ECPublicKeyParameters)this.key, var1, var2);
         return var3 != null ? BigIntegers.asUnsignedByteArray(var3) : null;
      }
   }

   private BigInteger extractT(ECPublicKeyParameters var1, BigInteger var2, BigInteger var3) {
      BigInteger var4 = var1.getParameters().getN();
      if (var2.compareTo(ECConstants.ONE) < 0 || var2.compareTo(var4) >= 0) {
         return null;
      } else if (var3.compareTo(ECConstants.ZERO) >= 0 && var3.compareTo(var4) < 0) {
         ECPoint var5 = var1.getParameters().getG();
         ECPoint var6 = var1.getQ();
         ECPoint var7 = ECAlgorithms.sumOfTwoMultiplies(var5, var3, var6, var2).normalize();
         if (var7.isInfinity()) {
            return null;
         } else {
            BigInteger var8 = var7.getAffineXCoord().toBigInteger();
            return var2.subtract(var8).mod(var4);
         }
      } else {
         return null;
      }
   }
}
