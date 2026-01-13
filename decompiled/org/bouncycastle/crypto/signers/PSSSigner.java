package org.bouncycastle.crypto.signers;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.Prehash;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.Arrays;

public class PSSSigner implements Signer {
   public static final byte TRAILER_IMPLICIT = -68;
   private Digest contentDigest1;
   private Digest contentDigest2;
   private Digest mgfDigest;
   private AsymmetricBlockCipher cipher;
   private SecureRandom random;
   private int hLen;
   private int mgfhLen;
   private boolean sSet;
   private int sLen;
   private int emBits;
   private byte[] salt;
   private byte[] mDash;
   private byte[] block;
   private byte trailer;

   public static PSSSigner createRawSigner(AsymmetricBlockCipher var0, Digest var1) {
      return new PSSSigner(var0, Prehash.forDigest(var1), var1, var1, var1.getDigestSize(), (byte)-68);
   }

   public static PSSSigner createRawSigner(AsymmetricBlockCipher var0, Digest var1, Digest var2, int var3, byte var4) {
      return new PSSSigner(var0, Prehash.forDigest(var1), var1, var2, var3, var4);
   }

   public static PSSSigner createRawSigner(AsymmetricBlockCipher var0, Digest var1, Digest var2, byte[] var3, byte var4) {
      return new PSSSigner(var0, Prehash.forDigest(var1), var1, var2, var3, var4);
   }

   public PSSSigner(AsymmetricBlockCipher var1, Digest var2, int var3) {
      this(var1, var2, var3, (byte)-68);
   }

   public PSSSigner(AsymmetricBlockCipher var1, Digest var2, Digest var3, int var4) {
      this(var1, var2, var3, var4, (byte)-68);
   }

   public PSSSigner(AsymmetricBlockCipher var1, Digest var2, int var3, byte var4) {
      this(var1, var2, var2, var3, var4);
   }

   public PSSSigner(AsymmetricBlockCipher var1, Digest var2, Digest var3, int var4, byte var5) {
      this(var1, var2, var2, var3, var4, var5);
   }

   private PSSSigner(AsymmetricBlockCipher var1, Digest var2, Digest var3, Digest var4, int var5, byte var6) {
      this.cipher = var1;
      this.contentDigest1 = var2;
      this.contentDigest2 = var3;
      this.mgfDigest = var4;
      this.hLen = var3.getDigestSize();
      this.mgfhLen = var4.getDigestSize();
      this.sSet = false;
      this.sLen = var5;
      this.salt = new byte[var5];
      this.mDash = new byte[8 + var5 + this.hLen];
      this.trailer = var6;
   }

   public PSSSigner(AsymmetricBlockCipher var1, Digest var2, byte[] var3) {
      this(var1, var2, var2, var3, (byte)-68);
   }

   public PSSSigner(AsymmetricBlockCipher var1, Digest var2, Digest var3, byte[] var4) {
      this(var1, var2, var3, var4, (byte)-68);
   }

   public PSSSigner(AsymmetricBlockCipher var1, Digest var2, Digest var3, byte[] var4, byte var5) {
      this(var1, var2, var2, var3, var4, var5);
   }

   private PSSSigner(AsymmetricBlockCipher var1, Digest var2, Digest var3, Digest var4, byte[] var5, byte var6) {
      this.cipher = var1;
      this.contentDigest1 = var2;
      this.contentDigest2 = var3;
      this.mgfDigest = var4;
      this.hLen = var3.getDigestSize();
      this.mgfhLen = var4.getDigestSize();
      this.sSet = true;
      this.sLen = var5.length;
      this.salt = var5;
      this.mDash = new byte[8 + this.sLen + this.hLen];
      this.trailer = var6;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      CipherParameters var3;
      if (var2 instanceof ParametersWithRandom) {
         ParametersWithRandom var4 = (ParametersWithRandom)var2;
         var3 = var4.getParameters();
         this.random = var4.getRandom();
      } else {
         var3 = var2;
         if (var1) {
            this.random = CryptoServicesRegistrar.getSecureRandom();
         }
      }

      RSAKeyParameters var5;
      if (var3 instanceof RSABlindingParameters) {
         var5 = ((RSABlindingParameters)var3).getPublicKey();
         this.cipher.init(var1, var2);
      } else {
         var5 = (RSAKeyParameters)var3;
         this.cipher.init(var1, var3);
      }

      this.emBits = var5.getModulus().bitLength() - 1;
      if (this.emBits < 8 * this.hLen + 8 * this.sLen + 9) {
         throw new IllegalArgumentException("key too small for specified hash and salt lengths");
      } else {
         this.block = new byte[(this.emBits + 7) / 8];
         this.reset();
      }
   }

   private void clearBlock(byte[] var1) {
      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = 0;
      }
   }

   @Override
   public void update(byte var1) {
      this.contentDigest1.update(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.contentDigest1.update(var1, var2, var3);
   }

   @Override
   public void reset() {
      this.contentDigest1.reset();
   }

   @Override
   public byte[] generateSignature() throws CryptoException, DataLengthException {
      if (this.contentDigest1.getDigestSize() != this.hLen) {
         throw new IllegalStateException();
      } else {
         this.contentDigest1.doFinal(this.mDash, this.mDash.length - this.hLen - this.sLen);
         if (this.sLen != 0) {
            if (!this.sSet) {
               this.random.nextBytes(this.salt);
            }

            System.arraycopy(this.salt, 0, this.mDash, this.mDash.length - this.sLen, this.sLen);
         }

         byte[] var1 = new byte[this.hLen];
         this.contentDigest2.update(this.mDash, 0, this.mDash.length);
         this.contentDigest2.doFinal(var1, 0);
         this.block[this.block.length - this.sLen - 1 - this.hLen - 1] = 1;
         System.arraycopy(this.salt, 0, this.block, this.block.length - this.sLen - this.hLen - 1, this.sLen);
         byte[] var2 = this.maskGenerator(var1, 0, var1.length, this.block.length - this.hLen - 1);

         for (int var3 = 0; var3 != var2.length; var3++) {
            this.block[var3] = (byte)(this.block[var3] ^ var2[var3]);
         }

         System.arraycopy(var1, 0, this.block, this.block.length - this.hLen - 1, this.hLen);
         int var5 = 255 >>> this.block.length * 8 - this.emBits;
         this.block[0] = (byte)(this.block[0] & var5);
         this.block[this.block.length - 1] = this.trailer;
         byte[] var4 = this.cipher.processBlock(this.block, 0, this.block.length);
         this.clearBlock(this.block);
         return var4;
      }
   }

   @Override
   public boolean verifySignature(byte[] var1) {
      if (this.contentDigest1.getDigestSize() != this.hLen) {
         throw new IllegalStateException();
      } else {
         this.contentDigest1.doFinal(this.mDash, this.mDash.length - this.hLen - this.sLen);

         try {
            byte[] var2 = this.cipher.processBlock(var1, 0, var1.length);
            Arrays.fill(this.block, 0, this.block.length - var2.length, (byte)0);
            System.arraycopy(var2, 0, this.block, this.block.length - var2.length, var2.length);
         } catch (Exception var6) {
            return false;
         }

         int var7 = 255 >>> this.block.length * 8 - this.emBits;
         if ((this.block[0] & 255) == (this.block[0] & var7) && this.block[this.block.length - 1] == this.trailer) {
            byte[] var3 = this.maskGenerator(this.block, this.block.length - this.hLen - 1, this.hLen, this.block.length - this.hLen - 1);

            for (int var4 = 0; var4 != var3.length; var4++) {
               this.block[var4] = (byte)(this.block[var4] ^ var3[var4]);
            }

            this.block[0] = (byte)(this.block[0] & var7);

            for (int var8 = 0; var8 != this.block.length - this.hLen - this.sLen - 2; var8++) {
               if (this.block[var8] != 0) {
                  this.clearBlock(this.block);
                  return false;
               }
            }

            if (this.block[this.block.length - this.hLen - this.sLen - 2] != 1) {
               this.clearBlock(this.block);
               return false;
            } else {
               if (this.sSet) {
                  System.arraycopy(this.salt, 0, this.mDash, this.mDash.length - this.sLen, this.sLen);
               } else {
                  System.arraycopy(this.block, this.block.length - this.sLen - this.hLen - 1, this.mDash, this.mDash.length - this.sLen, this.sLen);
               }

               this.contentDigest2.update(this.mDash, 0, this.mDash.length);
               this.contentDigest2.doFinal(this.mDash, this.mDash.length - this.hLen);
               int var9 = this.block.length - this.hLen - 1;

               for (int var5 = this.mDash.length - this.hLen; var5 != this.mDash.length; var5++) {
                  if ((this.block[var9] ^ this.mDash[var5]) != 0) {
                     this.clearBlock(this.mDash);
                     this.clearBlock(this.block);
                     return false;
                  }

                  var9++;
               }

               this.clearBlock(this.mDash);
               this.clearBlock(this.block);
               return true;
            }
         } else {
            this.clearBlock(this.block);
            return false;
         }
      }
   }

   private void ItoOSP(int var1, byte[] var2) {
      var2[0] = (byte)(var1 >>> 24);
      var2[1] = (byte)(var1 >>> 16);
      var2[2] = (byte)(var1 >>> 8);
      var2[3] = (byte)(var1 >>> 0);
   }

   private byte[] maskGenerator(byte[] var1, int var2, int var3, int var4) {
      if (this.mgfDigest instanceof Xof) {
         byte[] var5 = new byte[var4];
         this.mgfDigest.update(var1, var2, var3);
         ((Xof)this.mgfDigest).doFinal(var5, 0, var5.length);
         return var5;
      } else {
         return this.maskGeneratorFunction1(var1, var2, var3, var4);
      }
   }

   private byte[] maskGeneratorFunction1(byte[] var1, int var2, int var3, int var4) {
      byte[] var5 = new byte[var4];
      byte[] var6 = new byte[this.mgfhLen];
      byte[] var7 = new byte[4];
      int var8 = 0;
      this.mgfDigest.reset();

      while (var8 < var4 / this.mgfhLen) {
         this.ItoOSP(var8, var7);
         this.mgfDigest.update(var1, var2, var3);
         this.mgfDigest.update(var7, 0, var7.length);
         this.mgfDigest.doFinal(var6, 0);
         System.arraycopy(var6, 0, var5, var8 * this.mgfhLen, this.mgfhLen);
         var8++;
      }

      if (var8 * this.mgfhLen < var4) {
         this.ItoOSP(var8, var7);
         this.mgfDigest.update(var1, var2, var3);
         this.mgfDigest.update(var7, 0, var7.length);
         this.mgfDigest.doFinal(var6, 0);
         System.arraycopy(var6, 0, var5, var8 * this.mgfhLen, var5.length - var8 * this.mgfhLen);
      }

      return var5;
   }
}
