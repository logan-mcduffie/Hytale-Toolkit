package org.bouncycastle.crypto.encodings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class OAEPEncoding implements AsymmetricBlockCipher {
   private final AsymmetricBlockCipher engine;
   private final Digest mgf1Hash;
   private final int mgf1NoMemoLimit;
   private final byte[] defHash;
   private SecureRandom random;
   private boolean forEncryption;

   private static int getMGF1NoMemoLimit(Digest var0) {
      return var0 instanceof Memoable && var0 instanceof ExtendedDigest ? ((ExtendedDigest)var0).getByteLength() - 1 : Integer.MAX_VALUE;
   }

   public OAEPEncoding(AsymmetricBlockCipher var1) {
      this(var1, DigestFactory.createSHA1(), null);
   }

   public OAEPEncoding(AsymmetricBlockCipher var1, Digest var2) {
      this(var1, var2, null);
   }

   public OAEPEncoding(AsymmetricBlockCipher var1, Digest var2, byte[] var3) {
      this(var1, var2, var2, var3);
   }

   public OAEPEncoding(AsymmetricBlockCipher var1, Digest var2, Digest var3, byte[] var4) {
      this.engine = var1;
      this.mgf1Hash = var3;
      this.mgf1NoMemoLimit = getMGF1NoMemoLimit(var3);
      this.defHash = new byte[var2.getDigestSize()];
      var2.reset();
      if (var4 != null) {
         var2.update(var4, 0, var4.length);
      }

      var2.doFinal(this.defHash, 0);
   }

   public AsymmetricBlockCipher getUnderlyingCipher() {
      return this.engine;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      SecureRandom var3 = null;
      if (var2 instanceof ParametersWithRandom) {
         ParametersWithRandom var4 = (ParametersWithRandom)var2;
         var3 = var4.getRandom();
      }

      this.random = var1 ? CryptoServicesRegistrar.getSecureRandom(var3) : null;
      this.forEncryption = var1;
      this.engine.init(var1, var2);
   }

   @Override
   public int getInputBlockSize() {
      int var1 = this.engine.getInputBlockSize();
      return this.forEncryption ? var1 - 1 - 2 * this.defHash.length : var1;
   }

   @Override
   public int getOutputBlockSize() {
      int var1 = this.engine.getOutputBlockSize();
      return this.forEncryption ? var1 : var1 - 1 - 2 * this.defHash.length;
   }

   @Override
   public byte[] processBlock(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      return this.forEncryption ? this.encodeBlock(var1, var2, var3) : this.decodeBlock(var1, var2, var3);
   }

   public byte[] encodeBlock(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      int var4 = this.getInputBlockSize();
      if (var3 > var4) {
         throw new DataLengthException("input data too long");
      } else {
         byte[] var5 = new byte[var4 + 1 + 2 * this.defHash.length];
         System.arraycopy(var1, var2, var5, var5.length - var3, var3);
         var5[var5.length - var3 - 1] = 1;
         System.arraycopy(this.defHash, 0, var5, this.defHash.length, this.defHash.length);
         byte[] var6 = new byte[this.defHash.length];
         this.random.nextBytes(var6);
         System.arraycopy(var6, 0, var5, 0, this.defHash.length);
         this.mgf1Hash.reset();
         this.maskGeneratorFunction1(var6, 0, var6.length, var5, this.defHash.length, var5.length - this.defHash.length);
         this.maskGeneratorFunction1(var5, this.defHash.length, var5.length - this.defHash.length, var5, 0, this.defHash.length);
         return this.engine.processBlock(var5, 0, var5.length);
      }
   }

   public byte[] decodeBlock(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      int var4 = this.getOutputBlockSize() >> 31;
      byte[] var5 = new byte[this.engine.getOutputBlockSize()];
      byte[] var6 = this.engine.processBlock(var1, var2, var3);
      var4 |= var5.length - var6.length >> 31;
      int var7 = Math.min(var5.length, var6.length);
      System.arraycopy(var6, 0, var5, var5.length - var7, var7);
      Arrays.fill(var6, (byte)0);
      this.mgf1Hash.reset();
      this.maskGeneratorFunction1(var5, this.defHash.length, var5.length - this.defHash.length, var5, 0, this.defHash.length);
      this.maskGeneratorFunction1(var5, 0, this.defHash.length, var5, this.defHash.length, var5.length - this.defHash.length);

      for (int var13 = 0; var13 != this.defHash.length; var13++) {
         var4 |= this.defHash[var13] ^ var5[this.defHash.length + var13];
      }

      int var14 = -1;

      for (int var17 = 2 * this.defHash.length; var17 != var5.length; var17++) {
         int var8 = var5[var17] & 255;
         int var9 = (-var8 & var14) >> 31;
         var14 += var17 & var9;
      }

      var4 |= var14 >> 31;
      var4 |= var5[++var14] ^ 1;
      if (var4 != 0) {
         Arrays.fill(var5, (byte)0);
         throw new InvalidCipherTextException("data wrong");
      } else {
         var14++;
         byte[] var18 = new byte[var5.length - var14];
         System.arraycopy(var5, var14, var18, 0, var18.length);
         Arrays.fill(var5, (byte)0);
         return var18;
      }
   }

   private void maskGeneratorFunction1(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6) {
      int var7 = this.mgf1Hash.getDigestSize();
      byte[] var8 = new byte[var7];
      byte[] var9 = new byte[4];
      int var10 = 0;
      int var11 = var5 + var6;
      int var12 = var11 - var7;
      int var13 = var5;
      this.mgf1Hash.update(var1, var2, var3);
      if (var3 > this.mgf1NoMemoLimit) {
         Memoable var14 = (Memoable)this.mgf1Hash;

         for (Memoable var15 = var14.copy(); var13 < var12; var13 += var7) {
            Pack.intToBigEndian(var10++, var9, 0);
            this.mgf1Hash.update(var9, 0, var9.length);
            this.mgf1Hash.doFinal(var8, 0);
            var14.reset(var15);
            Bytes.xorTo(var7, var8, 0, var4, var13);
         }
      } else {
         while (var13 < var12) {
            Pack.intToBigEndian(var10++, var9, 0);
            this.mgf1Hash.update(var9, 0, var9.length);
            this.mgf1Hash.doFinal(var8, 0);
            this.mgf1Hash.update(var1, var2, var3);
            Bytes.xorTo(var7, var8, 0, var4, var13);
            var13 += var7;
         }
      }

      Pack.intToBigEndian(var10, var9, 0);
      this.mgf1Hash.update(var9, 0, var9.length);
      this.mgf1Hash.doFinal(var8, 0);
      Bytes.xorTo(var11 - var13, var8, 0, var4, var13);
   }
}
