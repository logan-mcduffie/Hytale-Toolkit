package org.bouncycastle.crypto.signers;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.SignerWithRecovery;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.Arrays;

public class ISO9796d2Signer implements SignerWithRecovery {
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
   private Digest digest;
   private AsymmetricBlockCipher cipher;
   private int trailer;
   private int keyBits;
   private byte[] block;
   private byte[] mBuf;
   private int messageLength;
   private boolean fullMessage;
   private byte[] recoveredMessage;
   private byte[] preSig;
   private byte[] preBlock;

   public ISO9796d2Signer(AsymmetricBlockCipher var1, Digest var2, boolean var3) {
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

   public ISO9796d2Signer(AsymmetricBlockCipher var1, Digest var2) {
      this(var1, var2, false);
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      RSAKeyParameters var3 = (RSAKeyParameters)var2;
      this.cipher.init(var1, var3);
      this.keyBits = var3.getModulus().bitLength();
      this.block = new byte[(this.keyBits + 7) / 8];
      if (this.trailer == 188) {
         this.mBuf = new byte[this.block.length - this.digest.getDigestSize() - 2];
      } else {
         this.mBuf = new byte[this.block.length - this.digest.getDigestSize() - 3];
      }

      this.reset();
   }

   private boolean isSameAs(byte[] var1, byte[] var2) {
      boolean var3 = true;
      if (this.messageLength > this.mBuf.length) {
         if (this.mBuf.length > var2.length) {
            var3 = false;
         }

         for (int var4 = 0; var4 != this.mBuf.length; var4++) {
            if (var1[var4] != var2[var4]) {
               var3 = false;
            }
         }
      } else {
         if (this.messageLength != var2.length) {
            var3 = false;
         }

         for (int var5 = 0; var5 != var2.length; var5++) {
            if (var1[var5] != var2[var5]) {
               var3 = false;
            }
         }
      }

      return var3;
   }

   private void clearBlock(byte[] var1) {
      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = 0;
      }
   }

   @Override
   public void updateWithRecoveredMessage(byte[] var1) throws InvalidCipherTextException {
      byte[] var2 = this.cipher.processBlock(var1, 0, var1.length);
      if ((var2[0] & 192 ^ 64) != 0) {
         throw new InvalidCipherTextException("malformed signature");
      } else if ((var2[var2.length - 1] & 15 ^ 12) != 0) {
         throw new InvalidCipherTextException("malformed signature");
      } else {
         byte var3 = 0;
         if ((var2[var2.length - 1] & 255 ^ 188) == 0) {
            var3 = 1;
         } else {
            int var4 = (var2[var2.length - 2] & 255) << 8 | var2[var2.length - 1] & 255;
            Integer var5 = ISOTrailers.getTrailer(this.digest);
            if (var5 == null) {
               throw new IllegalArgumentException("unrecognised hash in signature");
            }

            int var6 = var5;
            if (var4 != var6 && (var6 != 15052 || var4 != 16588)) {
               throw new IllegalStateException("signer initialised with wrong digest for trailer " + var4);
            }

            var3 = 2;
         }

         int var8 = 0;
         var8 = 0;

         while (var8 != var2.length && (var2[var8] & 15 ^ 10) != 0) {
            var8++;
         }

         var8++;
         int var11 = var2.length - var3 - this.digest.getDigestSize();
         if (var11 - var8 <= 0) {
            throw new InvalidCipherTextException("malformed block");
         } else {
            if ((var2[0] & 32) == 0) {
               this.fullMessage = true;
               this.recoveredMessage = new byte[var11 - var8];
               System.arraycopy(var2, var8, this.recoveredMessage, 0, this.recoveredMessage.length);
            } else {
               this.fullMessage = false;
               this.recoveredMessage = new byte[var11 - var8];
               System.arraycopy(var2, var8, this.recoveredMessage, 0, this.recoveredMessage.length);
            }

            this.preSig = var1;
            this.preBlock = var2;
            this.digest.update(this.recoveredMessage, 0, this.recoveredMessage.length);
            this.messageLength = this.recoveredMessage.length;
            System.arraycopy(this.recoveredMessage, 0, this.mBuf, 0, this.recoveredMessage.length);
         }
      }
   }

   @Override
   public void update(byte var1) {
      this.digest.update(var1);
      if (this.messageLength < this.mBuf.length) {
         this.mBuf[this.messageLength] = var1;
      }

      this.messageLength++;
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      while (var3 > 0 && this.messageLength < this.mBuf.length) {
         this.update(var1[var2]);
         var2++;
         var3--;
      }

      this.digest.update(var1, var2, var3);
      this.messageLength += var3;
   }

   @Override
   public void reset() {
      this.digest.reset();
      this.messageLength = 0;
      this.clearBlock(this.mBuf);
      if (this.recoveredMessage != null) {
         this.clearBlock(this.recoveredMessage);
      }

      this.recoveredMessage = null;
      this.fullMessage = false;
      if (this.preSig != null) {
         this.preSig = null;
         this.clearBlock(this.preBlock);
         this.preBlock = null;
      }
   }

   @Override
   public byte[] generateSignature() throws CryptoException {
      int var1 = this.digest.getDigestSize();
      byte var2 = 0;
      int var3 = 0;
      if (this.trailer == 188) {
         var2 = 8;
         var3 = this.block.length - var1 - 1;
         this.digest.doFinal(this.block, var3);
         this.block[this.block.length - 1] = -68;
      } else {
         var2 = 16;
         var3 = this.block.length - var1 - 2;
         this.digest.doFinal(this.block, var3);
         this.block[this.block.length - 2] = (byte)(this.trailer >>> 8);
         this.block[this.block.length - 1] = (byte)this.trailer;
      }

      byte var4 = 0;
      int var5 = (var1 + this.messageLength) * 8 + var2 + 4 - this.keyBits;
      if (var5 > 0) {
         int var6 = this.messageLength - (var5 + 7) / 8;
         var4 = 96;
         var3 -= var6;
         System.arraycopy(this.mBuf, 0, this.block, var3, var6);
         this.recoveredMessage = new byte[var6];
      } else {
         var4 = 64;
         var3 -= this.messageLength;
         System.arraycopy(this.mBuf, 0, this.block, var3, this.messageLength);
         this.recoveredMessage = new byte[this.messageLength];
      }

      if (var3 - 1 > 0) {
         for (int var11 = var3 - 1; var11 != 0; var11--) {
            this.block[var11] = -69;
         }

         this.block[var3 - 1] = (byte)(this.block[var3 - 1] ^ 1);
         this.block[0] = 11;
         this.block[0] = (byte)(this.block[0] | var4);
      } else {
         this.block[0] = 10;
         this.block[0] = (byte)(this.block[0] | var4);
      }

      byte[] var12 = this.cipher.processBlock(this.block, 0, this.block.length);
      this.fullMessage = (var4 & 32) == 0;
      System.arraycopy(this.mBuf, 0, this.recoveredMessage, 0, this.recoveredMessage.length);
      this.messageLength = 0;
      this.clearBlock(this.mBuf);
      this.clearBlock(this.block);
      return var12;
   }

   @Override
   public boolean verifySignature(byte[] var1) {
      Object var2 = null;
      if (this.preSig == null) {
         try {
            var2 = this.cipher.processBlock(var1, 0, var1.length);
         } catch (Exception var9) {
            return false;
         }
      } else {
         if (!Arrays.areEqual(this.preSig, var1)) {
            throw new IllegalStateException("updateWithRecoveredMessage called on different signature");
         }

         var2 = this.preBlock;
         this.preSig = null;
         this.preBlock = null;
      }

      if ((((Object[])var2)[0] & 192 ^ 64) != 0) {
         return this.returnFalse((byte[])var2);
      } else if ((((Object[])var2)[((Object[])var2).length - 1] & 15 ^ 12) != 0) {
         return this.returnFalse((byte[])var2);
      } else {
         byte var3 = 0;
         if ((((Object[])var2)[((Object[])var2).length - 1] & 255 ^ 188) == 0) {
            var3 = 1;
         } else {
            int var4 = (((Object[])var2)[((Object[])var2).length - 2] & 255) << 8 | ((Object[])var2)[((Object[])var2).length - 1] & 255;
            Integer var5 = ISOTrailers.getTrailer(this.digest);
            if (var5 == null) {
               throw new IllegalArgumentException("unrecognised hash in signature");
            }

            int var6 = var5;
            if (var4 != var6 && (var6 != 15052 || var4 != 16588)) {
               throw new IllegalStateException("signer initialised with wrong digest for trailer " + var4);
            }

            var3 = 2;
         }

         int var12 = 0;
         var12 = 0;

         while (var12 != ((Object[])var2).length && (((Object[])var2)[var12] & 15 ^ 10) != 0) {
            var12++;
         }

         var12++;
         byte[] var15 = new byte[this.digest.getDigestSize()];
         int var16 = ((Object[])var2).length - var3 - var15.length;
         if (var16 - var12 <= 0) {
            return this.returnFalse((byte[])var2);
         } else {
            if ((((Object[])var2)[0] & 32) == 0) {
               this.fullMessage = true;
               if (this.messageLength > var16 - var12) {
                  return this.returnFalse((byte[])var2);
               }

               this.digest.reset();
               this.digest.update((byte[])var2, var12, var16 - var12);
               this.digest.doFinal(var15, 0);
               boolean var7 = true;

               for (int var8 = 0; var8 != var15.length; var8++) {
                  ((Object[])var2)[var16 + var8] = (byte)(((Object[])var2)[var16 + var8] ^ var15[var8]);
                  if (((Object[])var2)[var16 + var8] != 0) {
                     var7 = false;
                  }
               }

               if (!var7) {
                  return this.returnFalse((byte[])var2);
               }

               this.recoveredMessage = new byte[var16 - var12];
               System.arraycopy(var2, var12, this.recoveredMessage, 0, this.recoveredMessage.length);
            } else {
               this.fullMessage = false;
               this.digest.doFinal(var15, 0);
               boolean var17 = true;

               for (int var18 = 0; var18 != var15.length; var18++) {
                  ((Object[])var2)[var16 + var18] = (byte)(((Object[])var2)[var16 + var18] ^ var15[var18]);
                  if (((Object[])var2)[var16 + var18] != 0) {
                     var17 = false;
                  }
               }

               if (!var17) {
                  return this.returnFalse((byte[])var2);
               }

               this.recoveredMessage = new byte[var16 - var12];
               System.arraycopy(var2, var12, this.recoveredMessage, 0, this.recoveredMessage.length);
            }

            if (this.messageLength != 0 && !this.isSameAs(this.mBuf, this.recoveredMessage)) {
               return this.returnFalse((byte[])var2);
            } else {
               this.clearBlock(this.mBuf);
               this.clearBlock((byte[])var2);
               this.messageLength = 0;
               return true;
            }
         }
      }
   }

   private boolean returnFalse(byte[] var1) {
      this.messageLength = 0;
      this.clearBlock(this.mBuf);
      this.clearBlock(var1);
      return false;
   }

   @Override
   public boolean hasFullMessage() {
      return this.fullMessage;
   }

   @Override
   public byte[] getRecoveredMessage() {
      return this.recoveredMessage;
   }
}
