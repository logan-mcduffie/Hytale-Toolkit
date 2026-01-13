package org.bouncycastle.crypto.engines;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;

public class DESedeWrapEngine implements Wrapper {
   private CBCBlockCipher engine;
   private KeyParameter param;
   private ParametersWithIV paramPlusIV;
   private byte[] iv;
   private boolean forWrapping;
   private static final byte[] IV2 = new byte[]{74, -35, -94, 44, 121, -24, 33, 5};
   Digest sha1 = DigestFactory.createSHA1();
   byte[] digest = new byte[20];

   @Override
   public void init(boolean var1, CipherParameters var2) {
      this.forWrapping = var1;
      this.engine = new CBCBlockCipher(new DESedeEngine());
      SecureRandom var3;
      if (var2 instanceof ParametersWithRandom) {
         ParametersWithRandom var4 = (ParametersWithRandom)var2;
         var2 = var4.getParameters();
         var3 = var4.getRandom();
      } else {
         var3 = CryptoServicesRegistrar.getSecureRandom();
      }

      if (var2 instanceof KeyParameter) {
         this.param = (KeyParameter)var2;
         if (this.forWrapping) {
            this.iv = new byte[8];
            var3.nextBytes(this.iv);
            this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
         }
      } else if (var2 instanceof ParametersWithIV) {
         this.paramPlusIV = (ParametersWithIV)var2;
         this.iv = this.paramPlusIV.getIV();
         this.param = (KeyParameter)this.paramPlusIV.getParameters();
         if (!this.forWrapping) {
            throw new IllegalArgumentException("You should not supply an IV for unwrapping");
         }

         if (this.iv == null || this.iv.length != 8) {
            throw new IllegalArgumentException("IV is not 8 octets");
         }
      }
   }

   @Override
   public String getAlgorithmName() {
      return "DESede";
   }

   @Override
   public byte[] wrap(byte[] var1, int var2, int var3) {
      if (!this.forWrapping) {
         throw new IllegalStateException("Not initialized for wrapping");
      } else {
         byte[] var4 = new byte[var3];
         System.arraycopy(var1, var2, var4, 0, var3);
         byte[] var5 = this.calculateCMSKeyChecksum(var4);
         byte[] var6 = new byte[var4.length + var5.length];
         System.arraycopy(var4, 0, var6, 0, var4.length);
         System.arraycopy(var5, 0, var6, var4.length, var5.length);
         int var7 = this.engine.getBlockSize();
         if (var6.length % var7 != 0) {
            throw new IllegalStateException("Not multiple of block length");
         } else {
            this.engine.init(true, this.paramPlusIV);
            byte[] var8 = new byte[var6.length];

            for (int var9 = 0; var9 != var6.length; var9 += var7) {
               this.engine.processBlock(var6, var9, var8, var9);
            }

            byte[] var12 = new byte[this.iv.length + var8.length];
            System.arraycopy(this.iv, 0, var12, 0, this.iv.length);
            System.arraycopy(var8, 0, var12, this.iv.length, var8.length);
            Arrays.reverseInPlace(var12);
            ParametersWithIV var10 = new ParametersWithIV(this.param, IV2);
            this.engine.init(true, var10);

            for (int var11 = 0; var11 != var12.length; var11 += var7) {
               this.engine.processBlock(var12, var11, var12, var11);
            }

            return var12;
         }
      }
   }

   @Override
   public byte[] unwrap(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      if (this.forWrapping) {
         throw new IllegalStateException("Not set for unwrapping");
      } else if (var1 == null) {
         throw new InvalidCipherTextException("Null pointer as ciphertext");
      } else {
         int var4 = this.engine.getBlockSize();
         if (var3 % var4 != 0) {
            throw new InvalidCipherTextException("Ciphertext not multiple of " + var4);
         } else {
            ParametersWithIV var5 = new ParametersWithIV(this.param, IV2);
            this.engine.init(false, var5);
            byte[] var6 = new byte[var3];

            for (int var7 = 0; var7 != var3; var7 += var4) {
               this.engine.processBlock(var1, var2 + var7, var6, var7);
            }

            Arrays.reverseInPlace(var6);
            this.iv = new byte[8];
            byte[] var11 = new byte[var6.length - 8];
            System.arraycopy(var6, 0, this.iv, 0, 8);
            System.arraycopy(var6, 8, var11, 0, var6.length - 8);
            this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
            this.engine.init(false, this.paramPlusIV);
            byte[] var8 = new byte[var11.length];

            for (int var9 = 0; var9 != var8.length; var9 += var4) {
               this.engine.processBlock(var11, var9, var8, var9);
            }

            byte[] var12 = new byte[var8.length - 8];
            byte[] var10 = new byte[8];
            System.arraycopy(var8, 0, var12, 0, var8.length - 8);
            System.arraycopy(var8, var8.length - 8, var10, 0, 8);
            if (!this.checkCMSKeyChecksum(var12, var10)) {
               throw new InvalidCipherTextException("Checksum inside ciphertext is corrupted");
            } else {
               return var12;
            }
         }
      }
   }

   private byte[] calculateCMSKeyChecksum(byte[] var1) {
      byte[] var2 = new byte[8];
      this.sha1.update(var1, 0, var1.length);
      this.sha1.doFinal(this.digest, 0);
      System.arraycopy(this.digest, 0, var2, 0, 8);
      return var2;
   }

   private boolean checkCMSKeyChecksum(byte[] var1, byte[] var2) {
      return Arrays.constantTimeAreEqual(this.calculateCMSKeyChecksum(var1), var2);
   }
}
