package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import java.util.Vector;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.NaccacheSternKeyParameters;
import org.bouncycastle.crypto.params.NaccacheSternPrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class NaccacheSternEngine implements AsymmetricBlockCipher {
   private boolean forEncryption;
   private NaccacheSternKeyParameters key;
   private Vector[] lookup = null;
   private boolean debug = false;
   private static BigInteger ZERO = BigInteger.valueOf(0L);
   private static BigInteger ONE = BigInteger.valueOf(1L);

   @Override
   public void init(boolean var1, CipherParameters var2) {
      this.forEncryption = var1;
      if (var2 instanceof ParametersWithRandom) {
         var2 = ((ParametersWithRandom)var2).getParameters();
      }

      this.key = (NaccacheSternKeyParameters)var2;
      if (!this.forEncryption) {
         if (this.debug) {
            System.out.println("Constructing lookup Array");
         }

         NaccacheSternPrivateKeyParameters var3 = (NaccacheSternPrivateKeyParameters)this.key;
         Vector var4 = var3.getSmallPrimes();
         this.lookup = new Vector[var4.size()];

         for (int var5 = 0; var5 < var4.size(); var5++) {
            BigInteger var6 = (BigInteger)var4.elementAt(var5);
            int var7 = var6.intValue();
            this.lookup[var5] = new Vector();
            this.lookup[var5].addElement(ONE);
            if (this.debug) {
               System.out.println("Constructing lookup ArrayList for " + var7);
            }

            BigInteger var8 = ZERO;

            for (int var9 = 1; var9 < var7; var9++) {
               var8 = var8.add(var3.getPhi_n());
               BigInteger var10 = var8.divide(var6);
               this.lookup[var5].addElement(var3.getG().modPow(var10, var3.getModulus()));
            }
         }
      }

      CryptoServicesRegistrar.checkConstraints(
         new DefaultServiceProperties("NaccacheStern", ConstraintUtils.bitsOfSecurityFor(this.key.getModulus()), var2, Utils.getPurpose(var1))
      );
   }

   public void setDebug(boolean var1) {
      this.debug = var1;
   }

   @Override
   public int getInputBlockSize() {
      return this.forEncryption ? (this.key.getLowerSigmaBound() + 7) / 8 - 1 : this.key.getModulus().toByteArray().length;
   }

   @Override
   public int getOutputBlockSize() {
      return this.forEncryption ? this.key.getModulus().toByteArray().length : (this.key.getLowerSigmaBound() + 7) / 8 - 1;
   }

   @Override
   public byte[] processBlock(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      if (this.key == null) {
         throw new IllegalStateException("NaccacheStern engine not initialised");
      } else if (var3 > this.getInputBlockSize() + 1) {
         throw new DataLengthException("input too large for Naccache-Stern cipher.\n");
      } else if (!this.forEncryption && var3 < this.getInputBlockSize()) {
         throw new InvalidCipherTextException("BlockLength does not match modulus for Naccache-Stern cipher.\n");
      } else {
         byte[] var4;
         if (var2 == 0 && var3 == var1.length) {
            var4 = var1;
         } else {
            var4 = new byte[var3];
            System.arraycopy(var1, var2, var4, 0, var3);
         }

         BigInteger var5 = new BigInteger(1, var4);
         if (this.debug) {
            System.out.println("input as BigInteger: " + var5);
         }

         byte[] var6;
         if (this.forEncryption) {
            var6 = this.encrypt(var5);
         } else {
            Vector var7 = new Vector();
            NaccacheSternPrivateKeyParameters var8 = (NaccacheSternPrivateKeyParameters)this.key;
            Vector var9 = var8.getSmallPrimes();

            for (int var10 = 0; var10 < var9.size(); var10++) {
               BigInteger var11 = var5.modPow(var8.getPhi_n().divide((BigInteger)var9.elementAt(var10)), var8.getModulus());
               Vector var12 = this.lookup[var10];
               if (this.lookup[var10].size() != ((BigInteger)var9.elementAt(var10)).intValue()) {
                  if (this.debug) {
                     System.out.println("Prime is " + var9.elementAt(var10) + ", lookup table has size " + var12.size());
                  }

                  throw new InvalidCipherTextException(
                     "Error in lookup Array for "
                        + ((BigInteger)var9.elementAt(var10)).intValue()
                        + ": Size mismatch. Expected ArrayList with length "
                        + ((BigInteger)var9.elementAt(var10)).intValue()
                        + " but found ArrayList of length "
                        + this.lookup[var10].size()
                  );
               }

               int var13 = var12.indexOf(var11);
               if (var13 == -1) {
                  if (this.debug) {
                     System.out.println("Actual prime is " + var9.elementAt(var10));
                     System.out.println("Decrypted value is " + var11);
                     System.out.println("LookupList for " + var9.elementAt(var10) + " with size " + this.lookup[var10].size() + " is: ");

                     for (int var14 = 0; var14 < this.lookup[var10].size(); var14++) {
                        System.out.println(this.lookup[var10].elementAt(var14));
                     }
                  }

                  throw new InvalidCipherTextException("Lookup failed");
               }

               var7.addElement(BigInteger.valueOf(var13));
            }

            BigInteger var15 = chineseRemainder(var7, var9);
            var6 = var15.toByteArray();
         }

         return var6;
      }
   }

   public byte[] encrypt(BigInteger var1) {
      byte[] var2 = this.key.getModulus().toByteArray();
      Arrays.fill(var2, (byte)0);
      byte[] var3 = this.key.getG().modPow(var1, this.key.getModulus()).toByteArray();
      System.arraycopy(var3, 0, var2, var2.length - var3.length, var3.length);
      if (this.debug) {
         System.out.println("Encrypted value is:  " + new BigInteger(var2));
      }

      return var2;
   }

   public byte[] addCryptedBlocks(byte[] var1, byte[] var2) throws InvalidCipherTextException {
      if (this.forEncryption) {
         if (var1.length > this.getOutputBlockSize() || var2.length > this.getOutputBlockSize()) {
            throw new InvalidCipherTextException("BlockLength too large for simple addition.\n");
         }
      } else if (var1.length > this.getInputBlockSize() || var2.length > this.getInputBlockSize()) {
         throw new InvalidCipherTextException("BlockLength too large for simple addition.\n");
      }

      BigInteger var3 = new BigInteger(1, var1);
      BigInteger var4 = new BigInteger(1, var2);
      BigInteger var5 = var3.multiply(var4);
      var5 = var5.mod(this.key.getModulus());
      if (this.debug) {
         System.out.println("c(m1) as BigInteger:....... " + var3);
         System.out.println("c(m2) as BigInteger:....... " + var4);
         System.out.println("c(m1)*c(m2)%n = c(m1+m2)%n: " + var5);
      }

      byte[] var6 = this.key.getModulus().toByteArray();
      Arrays.fill(var6, (byte)0);
      System.arraycopy(var5.toByteArray(), 0, var6, var6.length - var5.toByteArray().length, var5.toByteArray().length);
      return var6;
   }

   public byte[] processData(byte[] var1) throws InvalidCipherTextException {
      if (this.debug) {
         System.out.println();
      }

      if (var1.length > this.getInputBlockSize()) {
         int var2 = this.getInputBlockSize();
         int var3 = this.getOutputBlockSize();
         if (this.debug) {
            System.out.println("Input blocksize is:  " + var2 + " bytes");
            System.out.println("Output blocksize is: " + var3 + " bytes");
            System.out.println("Data has length:.... " + var1.length + " bytes");
         }

         int var4 = 0;
         int var5 = 0;
         byte[] var6 = new byte[(var1.length / var2 + 1) * var3];

         while (var4 < var1.length) {
            byte[] var7;
            if (var4 + var2 < var1.length) {
               var7 = this.processBlock(var1, var4, var2);
               var4 += var2;
            } else {
               var7 = this.processBlock(var1, var4, var1.length - var4);
               var4 += var1.length - var4;
            }

            if (this.debug) {
               System.out.println("new datapos is " + var4);
            }

            if (var7 == null) {
               if (this.debug) {
                  System.out.println("cipher returned null");
               }

               throw new InvalidCipherTextException("cipher returned null");
            }

            System.arraycopy(var7, 0, var6, var5, var7.length);
            var5 += var7.length;
         }

         byte[] var8 = new byte[var5];
         System.arraycopy(var6, 0, var8, 0, var5);
         if (this.debug) {
            System.out.println("returning " + var8.length + " bytes");
         }

         return var8;
      } else {
         if (this.debug) {
            System.out.println("data size is less then input block size, processing directly");
         }

         return this.processBlock(var1, 0, var1.length);
      }
   }

   private static BigInteger chineseRemainder(Vector var0, Vector var1) {
      BigInteger var2 = ZERO;
      BigInteger var3 = ONE;

      for (int var4 = 0; var4 < var1.size(); var4++) {
         var3 = var3.multiply((BigInteger)var1.elementAt(var4));
      }

      for (int var9 = 0; var9 < var1.size(); var9++) {
         BigInteger var5 = (BigInteger)var1.elementAt(var9);
         BigInteger var6 = var3.divide(var5);
         BigInteger var7 = var6.modInverse(var5);
         BigInteger var8 = var6.multiply(var7);
         var8 = var8.multiply((BigInteger)var0.elementAt(var9));
         var2 = var2.add(var8);
      }

      return var2.mod(var3);
   }
}
