package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.ElGamalKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.BigIntegers;

public class ElGamalEngine implements AsymmetricBlockCipher {
   private ElGamalKeyParameters key;
   private SecureRandom random;
   private boolean forEncryption;
   private int bitSize;
   private static final BigInteger ZERO = BigInteger.valueOf(0L);
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private static final BigInteger TWO = BigInteger.valueOf(2L);

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var2 instanceof ParametersWithRandom) {
         ParametersWithRandom var3 = (ParametersWithRandom)var2;
         this.key = (ElGamalKeyParameters)var3.getParameters();
         this.random = var3.getRandom();
      } else {
         this.key = (ElGamalKeyParameters)var2;
         this.random = CryptoServicesRegistrar.getSecureRandom();
      }

      this.forEncryption = var1;
      BigInteger var4 = this.key.getParameters().getP();
      this.bitSize = var4.bitLength();
      if (var1) {
         if (!(this.key instanceof ElGamalPublicKeyParameters)) {
            throw new IllegalArgumentException("ElGamalPublicKeyParameters are required for encryption.");
         }
      } else if (!(this.key instanceof ElGamalPrivateKeyParameters)) {
         throw new IllegalArgumentException("ElGamalPrivateKeyParameters are required for decryption.");
      }

      CryptoServicesRegistrar.checkConstraints(
         new DefaultServiceProperties("RSA", ConstraintUtils.bitsOfSecurityFor(this.key.getParameters().getP()), this.key, Utils.getPurpose(var1))
      );
   }

   @Override
   public int getInputBlockSize() {
      return this.forEncryption ? (this.bitSize - 1) / 8 : 2 * ((this.bitSize + 7) / 8);
   }

   @Override
   public int getOutputBlockSize() {
      return this.forEncryption ? 2 * ((this.bitSize + 7) / 8) : (this.bitSize - 1) / 8;
   }

   @Override
   public byte[] processBlock(byte[] var1, int var2, int var3) {
      if (this.key == null) {
         throw new IllegalStateException("ElGamal engine not initialised");
      } else {
         int var4 = this.forEncryption ? (this.bitSize - 1 + 7) / 8 : this.getInputBlockSize();
         if (var3 > var4) {
            throw new DataLengthException("input too large for ElGamal cipher.\n");
         } else {
            BigInteger var5 = this.key.getParameters().getP();
            if (this.key instanceof ElGamalPrivateKeyParameters) {
               byte[] var17 = new byte[var3 / 2];
               byte[] var18 = new byte[var3 / 2];
               System.arraycopy(var1, var2, var17, 0, var17.length);
               System.arraycopy(var1, var2 + var17.length, var18, 0, var18.length);
               BigInteger var19 = new BigInteger(1, var17);
               BigInteger var20 = new BigInteger(1, var18);
               ElGamalPrivateKeyParameters var21 = (ElGamalPrivateKeyParameters)this.key;
               BigInteger var22 = var19.modPow(var5.subtract(ONE).subtract(var21.getX()), var5).multiply(var20).mod(var5);
               return BigIntegers.asUnsignedByteArray(var22);
            } else {
               byte[] var6;
               if (var2 == 0 && var3 == var1.length) {
                  var6 = var1;
               } else {
                  var6 = new byte[var3];
                  System.arraycopy(var1, var2, var6, 0, var3);
               }

               BigInteger var7 = new BigInteger(1, var6);
               if (var7.compareTo(var5) >= 0) {
                  throw new DataLengthException("input too large for ElGamal cipher.\n");
               } else {
                  ElGamalPublicKeyParameters var8 = (ElGamalPublicKeyParameters)this.key;
                  int var9 = var5.bitLength();
                  BigInteger var10 = BigIntegers.createRandomBigInteger(var9, this.random);

                  while (var10.equals(ZERO) || var10.compareTo(var5.subtract(TWO)) > 0) {
                     var10 = BigIntegers.createRandomBigInteger(var9, this.random);
                  }

                  BigInteger var11 = this.key.getParameters().getG();
                  BigInteger var12 = var11.modPow(var10, var5);
                  BigInteger var13 = var7.multiply(var8.getY().modPow(var10, var5)).mod(var5);
                  byte[] var14 = var12.toByteArray();
                  byte[] var15 = var13.toByteArray();
                  byte[] var16 = new byte[this.getOutputBlockSize()];
                  if (var14.length > var16.length / 2) {
                     System.arraycopy(var14, 1, var16, var16.length / 2 - (var14.length - 1), var14.length - 1);
                  } else {
                     System.arraycopy(var14, 0, var16, var16.length / 2 - var14.length, var14.length);
                  }

                  if (var15.length > var16.length / 2) {
                     System.arraycopy(var15, 1, var16, var16.length - (var15.length - 1), var15.length - 1);
                  } else {
                     System.arraycopy(var15, 0, var16, var16.length - var15.length, var15.length);
                  }

                  return var16;
               }
            }
         }
      }
   }
}
