package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class X931Signer implements Signer {
   /** @deprecated */
   public static final int TRAILER_IMPLICIT = 188;
   /** @deprecated */
   public static final int TRAILER_RIPEMD160 = 12748;
   /** @deprecated */
   public static final int TRAILER_RIPEMD128 = 13004;
   /** @deprecated */
   public static final int TRAILER_SHA1 = 13260;
   /** @deprecated */
   public static final int TRAILER_SHA256 = 13516;
   /** @deprecated */
   public static final int TRAILER_SHA512 = 13772;
   /** @deprecated */
   public static final int TRAILER_SHA384 = 14028;
   /** @deprecated */
   public static final int TRAILER_WHIRLPOOL = 14284;
   /** @deprecated */
   public static final int TRAILER_SHA224 = 14540;
   private Digest digest;
   private AsymmetricBlockCipher cipher;
   private RSAKeyParameters kParam;
   private int trailer;
   private int keyBits;
   private byte[] block;

   public X931Signer(AsymmetricBlockCipher var1, Digest var2, boolean var3) {
      this.cipher = var1;
      this.digest = var2;
      if (var3) {
         this.trailer = 188;
      } else {
         Integer var4 = ISOTrailers.getTrailer(var2);
         if (var4 == null) {
            throw new IllegalArgumentException("no valid trailer for digest: " + var2.getAlgorithmName());
         }

         this.trailer = var4;
      }
   }

   public X931Signer(AsymmetricBlockCipher var1, Digest var2) {
      this(var1, var2, false);
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      this.kParam = (RSAKeyParameters)var2;
      this.cipher.init(var1, this.kParam);
      this.keyBits = this.kParam.getModulus().bitLength();
      this.block = new byte[(this.keyBits + 7) / 8];
      this.reset();
   }

   private void clearBlock(byte[] var1) {
      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = 0;
      }
   }

   @Override
   public void update(byte var1) {
      this.digest.update(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.digest.update(var1, var2, var3);
   }

   @Override
   public void reset() {
      this.digest.reset();
   }

   @Override
   public byte[] generateSignature() throws CryptoException {
      this.createSignatureBlock(this.trailer);
      BigInteger var1 = new BigInteger(1, this.cipher.processBlock(this.block, 0, this.block.length));
      this.clearBlock(this.block);
      var1 = var1.min(this.kParam.getModulus().subtract(var1));
      int var2 = BigIntegers.getUnsignedByteLength(this.kParam.getModulus());
      return BigIntegers.asUnsignedByteArray(var2, var1);
   }

   private void createSignatureBlock(int var1) {
      int var2 = this.digest.getDigestSize();
      int var3;
      if (var1 == 188) {
         var3 = this.block.length - var2 - 1;
         this.digest.doFinal(this.block, var3);
         this.block[this.block.length - 1] = -68;
      } else {
         var3 = this.block.length - var2 - 2;
         this.digest.doFinal(this.block, var3);
         this.block[this.block.length - 2] = (byte)(var1 >>> 8);
         this.block[this.block.length - 1] = (byte)var1;
      }

      this.block[0] = 107;

      for (int var4 = var3 - 2; var4 != 0; var4--) {
         this.block[var4] = -69;
      }

      this.block[var3 - 1] = -70;
   }

   @Override
   public boolean verifySignature(byte[] var1) {
      try {
         this.block = this.cipher.processBlock(var1, 0, var1.length);
      } catch (Exception var6) {
         return false;
      }

      BigInteger var2 = new BigInteger(1, this.block);
      BigInteger var3;
      if ((var2.intValue() & 15) == 12) {
         var3 = var2;
      } else {
         var2 = this.kParam.getModulus().subtract(var2);
         if ((var2.intValue() & 15) != 12) {
            return false;
         }

         var3 = var2;
      }

      this.createSignatureBlock(this.trailer);
      byte[] var4 = BigIntegers.asUnsignedByteArray(this.block.length, var3);
      boolean var5 = Arrays.constantTimeAreEqual(this.block, var4);
      if (this.trailer == 15052 && !var5) {
         this.block[this.block.length - 2] = 64;
         var5 = Arrays.constantTimeAreEqual(this.block, var4);
      }

      this.clearBlock(this.block);
      this.clearBlock(var4);
      return var5;
   }
}
