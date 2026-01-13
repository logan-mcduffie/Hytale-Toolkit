package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

class RSACoreEngine {
   static final String NO_LENSTRA_CHECK = "org.bouncycastle.rsa.no_lenstra_check";
   private RSAKeyParameters key;
   private boolean forEncryption;

   public void init(boolean var1, CipherParameters var2) {
      if (var2 instanceof ParametersWithRandom) {
         ParametersWithRandom var3 = (ParametersWithRandom)var2;
         var2 = var3.getParameters();
      }

      this.forEncryption = var1;
      this.key = (RSAKeyParameters)var2;
      int var5 = ConstraintUtils.bitsOfSecurityFor(this.key.getModulus());
      CryptoServicePurpose var4 = this.getPurpose(this.key.isPrivate(), var1);
      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("RSA", var5, this.key, var4));
   }

   public int getInputBlockSize() {
      int var1 = this.key.getModulus().bitLength();
      return this.forEncryption ? (var1 + 7) / 8 - 1 : (var1 + 7) / 8;
   }

   public int getOutputBlockSize() {
      int var1 = this.key.getModulus().bitLength();
      return this.forEncryption ? (var1 + 7) / 8 : (var1 + 7) / 8 - 1;
   }

   public BigInteger convertInput(byte[] var1, int var2, int var3) {
      if (var3 > this.getInputBlockSize() + 1) {
         throw new DataLengthException("input too large for RSA cipher.");
      } else if (var3 == this.getInputBlockSize() + 1 && !this.forEncryption) {
         throw new DataLengthException("input too large for RSA cipher.");
      } else {
         byte[] var4;
         if (var2 == 0 && var3 == var1.length) {
            var4 = var1;
         } else {
            var4 = new byte[var3];
            System.arraycopy(var1, var2, var4, 0, var3);
         }

         BigInteger var5 = new BigInteger(1, var4);
         if (var5.compareTo(this.key.getModulus()) >= 0) {
            throw new DataLengthException("input too large for RSA cipher.");
         } else {
            return var5;
         }
      }
   }

   public byte[] convertOutput(BigInteger var1) {
      byte[] var2 = var1.toByteArray();
      if (this.forEncryption) {
         if (var2[0] == 0 && var2.length > this.getOutputBlockSize()) {
            byte[] var5 = new byte[var2.length - 1];
            System.arraycopy(var2, 1, var5, 0, var5.length);
            return var5;
         } else if (var2.length < this.getOutputBlockSize()) {
            byte[] var4 = new byte[this.getOutputBlockSize()];
            System.arraycopy(var2, 0, var4, var4.length - var2.length, var2.length);
            return var4;
         } else {
            return var2;
         }
      } else {
         byte[] var3;
         if (var2[0] == 0) {
            var3 = new byte[var2.length - 1];
            System.arraycopy(var2, 1, var3, 0, var3.length);
         } else {
            var3 = new byte[var2.length];
            System.arraycopy(var2, 0, var3, 0, var3.length);
         }

         Arrays.fill(var2, (byte)0);
         return var3;
      }
   }

   public BigInteger processBlock(BigInteger var1) {
      if (this.key instanceof RSAPrivateCrtKeyParameters) {
         RSAPrivateCrtKeyParameters var2 = (RSAPrivateCrtKeyParameters)this.key;
         BigInteger var3 = var2.getPublicExponent();
         if (var3 != null || Properties.isOverrideSet("org.bouncycastle.rsa.no_lenstra_check")) {
            BigInteger var4 = var2.getP();
            BigInteger var5 = var2.getQ();
            BigInteger var6 = var2.getDP();
            BigInteger var7 = var2.getDQ();
            BigInteger var8 = var2.getQInv();
            BigInteger var9 = var1.remainder(var4).modPow(var6, var4);
            BigInteger var10 = var1.remainder(var5).modPow(var7, var5);
            BigInteger var11 = var9.subtract(var10);
            var11 = var11.multiply(var8);
            var11 = var11.mod(var4);
            BigInteger var12 = var11.multiply(var5).add(var10);
            if (var3 != null) {
               BigInteger var13 = var12.modPow(var3, var2.getModulus());
               if (!var13.equals(var1)) {
                  throw new IllegalStateException("RSA engine faulty decryption/signing detected");
               }
            }

            return var12;
         }
      }

      return var1.modPow(this.key.getExponent(), this.key.getModulus());
   }

   private CryptoServicePurpose getPurpose(boolean var1, boolean var2) {
      boolean var3 = var1 && var2;
      boolean var4 = !var1 && var2;
      boolean var5 = !var1 && !var2;
      if (var3) {
         return CryptoServicePurpose.SIGNING;
      } else if (var4) {
         return CryptoServicePurpose.ENCRYPTION;
      } else {
         return var5 ? CryptoServicePurpose.VERIFYING : CryptoServicePurpose.DECRYPTION;
      }
   }
}
