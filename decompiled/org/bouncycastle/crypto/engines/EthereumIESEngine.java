package org.bouncycastle.crypto.engines;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DigestDerivationFunction;
import org.bouncycastle.crypto.EphemeralKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.KeyParser;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.SavableDigest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.EphemeralKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.IESParameters;
import org.bouncycastle.crypto.params.IESWithCipherParameters;
import org.bouncycastle.crypto.params.ISO18033KDFParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Pack;

public class EthereumIESEngine {
   BasicAgreement agree;
   DerivationFunction kdf;
   Mac mac;
   BufferedBlockCipher cipher;
   byte[] macBuf;
   byte[] commonMac;
   boolean forEncryption;
   CipherParameters privParam;
   CipherParameters pubParam;
   IESParameters param;
   byte[] V;
   private EphemeralKeyPairGenerator keyPairGenerator;
   private KeyParser keyParser;
   private byte[] IV;

   public EthereumIESEngine(BasicAgreement var1, DerivationFunction var2, Mac var3, byte[] var4) {
      this.agree = var1;
      this.kdf = var2;
      this.mac = var3;
      this.macBuf = new byte[var3.getMacSize()];
      this.commonMac = var4;
      this.cipher = null;
   }

   public EthereumIESEngine(BasicAgreement var1, DerivationFunction var2, Mac var3, byte[] var4, BufferedBlockCipher var5) {
      this.agree = var1;
      this.kdf = var2;
      this.mac = var3;
      this.macBuf = new byte[var3.getMacSize()];
      this.commonMac = var4;
      this.cipher = var5;
   }

   public void init(boolean var1, CipherParameters var2, CipherParameters var3, CipherParameters var4) {
      this.forEncryption = var1;
      this.privParam = var2;
      this.pubParam = var3;
      this.V = new byte[0];
      this.extractParams(var4);
   }

   public void init(AsymmetricKeyParameter var1, CipherParameters var2, EphemeralKeyPairGenerator var3) {
      this.forEncryption = true;
      this.pubParam = var1;
      this.keyPairGenerator = var3;
      this.extractParams(var2);
   }

   public void init(AsymmetricKeyParameter var1, CipherParameters var2, KeyParser var3) {
      this.forEncryption = false;
      this.privParam = var1;
      this.keyParser = var3;
      this.extractParams(var2);
   }

   private void extractParams(CipherParameters var1) {
      if (var1 instanceof ParametersWithIV) {
         this.IV = ((ParametersWithIV)var1).getIV();
         this.param = (IESParameters)((ParametersWithIV)var1).getParameters();
      } else {
         this.IV = null;
         this.param = (IESParameters)var1;
      }
   }

   public BufferedBlockCipher getCipher() {
      return this.cipher;
   }

   public Mac getMac() {
      return this.mac;
   }

   private byte[] encryptBlock(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      Object var4 = null;
      Object var5 = null;
      Object var6 = null;
      Object var7 = null;
      int var8;
      if (this.cipher == null) {
         var6 = new byte[var3];
         var7 = new byte[this.param.getMacKeySize() / 8];
         var5 = new byte[((Object[])var6).length + ((Object[])var7).length];
         this.kdf.generateBytes((byte[])var5, 0, ((Object[])var5).length);
         if (this.V.length != 0) {
            System.arraycopy(var5, 0, var7, 0, ((Object[])var7).length);
            System.arraycopy(var5, ((Object[])var7).length, var6, 0, ((Object[])var6).length);
         } else {
            System.arraycopy(var5, 0, var6, 0, ((Object[])var6).length);
            System.arraycopy(var5, var3, var7, 0, ((Object[])var7).length);
         }

         var4 = new byte[var3];

         for (int var9 = 0; var9 != var3; var9++) {
            ((Object[])var4)[var9] = (byte)(var1[var2 + var9] ^ ((Object[])var6)[var9]);
         }

         var8 = var3;
      } else {
         var6 = new byte[((IESWithCipherParameters)this.param).getCipherKeySize() / 8];
         var7 = new byte[this.param.getMacKeySize() / 8];
         var5 = new byte[((Object[])var6).length + ((Object[])var7).length];
         this.kdf.generateBytes((byte[])var5, 0, ((Object[])var5).length);
         System.arraycopy(var5, 0, var6, 0, ((Object[])var6).length);
         System.arraycopy(var5, ((Object[])var6).length, var7, 0, ((Object[])var7).length);
         if (this.IV != null) {
            this.cipher.init(true, new ParametersWithIV(new KeyParameter((byte[])var6), this.IV));
         } else {
            this.cipher.init(true, new KeyParameter((byte[])var6));
         }

         var4 = new byte[this.cipher.getOutputSize(var3)];
         var8 = this.cipher.processBytes(var1, var2, var3, (byte[])var4, 0);
         var8 += this.cipher.doFinal((byte[])var4, var8);
      }

      byte[] var22 = this.param.getEncodingV();
      byte[] var10 = null;
      if (this.V.length != 0) {
         var10 = this.getLengthTag(var22);
      }

      byte[] var11 = new byte[this.mac.getMacSize()];
      SavableDigest var12 = SHA256Digest.newInstance();
      byte[] var13 = new byte[var12.getDigestSize()];
      var12.reset();
      var12.update((byte[])var7, 0, ((Object[])var7).length);
      var12.doFinal(var13, 0);
      this.mac.init(new KeyParameter(var13));
      this.mac.update(this.IV, 0, this.IV.length);
      this.mac.update((byte[])var4, 0, ((Object[])var4).length);
      if (var22 != null) {
         this.mac.update(var22, 0, var22.length);
      }

      if (this.V.length != 0) {
         this.mac.update(var10, 0, var10.length);
      }

      this.mac.update(this.commonMac, 0, this.commonMac.length);
      this.mac.doFinal(var11, 0);
      byte[] var14 = new byte[this.V.length + var8 + var11.length];
      System.arraycopy(this.V, 0, var14, 0, this.V.length);
      System.arraycopy(var4, 0, var14, this.V.length, var8);
      System.arraycopy(var11, 0, var14, this.V.length + var8, var11.length);
      return var14;
   }

   private byte[] decryptBlock(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      int var8 = 0;
      if (var3 < this.V.length + this.mac.getMacSize()) {
         throw new InvalidCipherTextException("length of input must be greater than the MAC and V combined");
      } else {
         byte[] var4;
         byte[] var7;
         if (this.cipher == null) {
            byte[] var6 = new byte[var3 - this.V.length - this.mac.getMacSize()];
            var7 = new byte[this.param.getMacKeySize() / 8];
            byte[] var5 = new byte[var6.length + var7.length];
            this.kdf.generateBytes(var5, 0, var5.length);
            if (this.V.length != 0) {
               System.arraycopy(var5, 0, var7, 0, var7.length);
               System.arraycopy(var5, var7.length, var6, 0, var6.length);
            } else {
               System.arraycopy(var5, 0, var6, 0, var6.length);
               System.arraycopy(var5, var6.length, var7, 0, var7.length);
            }

            var4 = new byte[var6.length];

            for (int var9 = 0; var9 != var6.length; var9++) {
               var4[var9] = (byte)(var1[var2 + this.V.length + var9] ^ var6[var9]);
            }
         } else {
            byte[] var17 = new byte[((IESWithCipherParameters)this.param).getCipherKeySize() / 8];
            var7 = new byte[this.param.getMacKeySize() / 8];
            byte[] var16 = new byte[var17.length + var7.length];
            this.kdf.generateBytes(var16, 0, var16.length);
            System.arraycopy(var16, 0, var17, 0, var17.length);
            System.arraycopy(var16, var17.length, var7, 0, var7.length);
            Object var19 = new KeyParameter(var17);
            if (this.IV != null) {
               var19 = new ParametersWithIV((CipherParameters)var19, this.IV);
            }

            this.cipher.init(false, (CipherParameters)var19);
            var4 = new byte[this.cipher.getOutputSize(var3 - this.V.length - this.mac.getMacSize())];
            var8 = this.cipher.processBytes(var1, var2 + this.V.length, var3 - this.V.length - this.mac.getMacSize(), var4, 0);
         }

         byte[] var20 = this.param.getEncodingV();
         byte[] var10 = null;
         if (this.V.length != 0) {
            var10 = this.getLengthTag(var20);
         }

         int var11 = var2 + var3;
         byte[] var12 = Arrays.copyOfRange(var1, var11 - this.mac.getMacSize(), var11);
         byte[] var13 = new byte[var12.length];
         SavableDigest var14 = SHA256Digest.newInstance();
         byte[] var15 = new byte[var14.getDigestSize()];
         var14.reset();
         var14.update(var7, 0, var7.length);
         var14.doFinal(var15, 0);
         this.mac.init(new KeyParameter(var15));
         this.mac.update(this.IV, 0, this.IV.length);
         this.mac.update(var1, var2 + this.V.length, var3 - this.V.length - var13.length);
         if (var20 != null) {
            this.mac.update(var20, 0, var20.length);
         }

         if (this.V.length != 0) {
            this.mac.update(var10, 0, var10.length);
         }

         this.mac.update(this.commonMac, 0, this.commonMac.length);
         this.mac.doFinal(var13, 0);
         if (!Arrays.constantTimeAreEqual(var12, var13)) {
            throw new InvalidCipherTextException("invalid MAC");
         } else if (this.cipher == null) {
            return var4;
         } else {
            var8 += this.cipher.doFinal(var4, var8);
            return Arrays.copyOfRange(var4, 0, var8);
         }
      }
   }

   public byte[] processBlock(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      if (this.forEncryption) {
         if (this.keyPairGenerator != null) {
            EphemeralKeyPair var4 = this.keyPairGenerator.generate();
            this.privParam = var4.getKeyPair().getPrivate();
            this.V = var4.getEncodedPublicKey();
         }
      } else if (this.keyParser != null) {
         ByteArrayInputStream var15 = new ByteArrayInputStream(var1, var2, var3);

         try {
            this.pubParam = this.keyParser.readKey(var15);
         } catch (IOException var13) {
            throw new InvalidCipherTextException("unable to recover ephemeral public key: " + var13.getMessage(), var13);
         } catch (IllegalArgumentException var14) {
            throw new InvalidCipherTextException("unable to recover ephemeral public key: " + var14.getMessage(), var14);
         }

         int var5 = var3 - var15.available();
         this.V = Arrays.copyOfRange(var1, var2, var2 + var5);
      }

      this.agree.init(this.privParam);
      BigInteger var16 = this.agree.calculateAgreement(this.pubParam);
      byte[] var17 = BigIntegers.asUnsignedByteArray(this.agree.getFieldSize(), var16);
      if (this.V.length != 0) {
         byte[] var6 = Arrays.concatenate(this.V, var17);
         Arrays.fill(var17, (byte)0);
         var17 = var6;
      }

      byte[] var7;
      try {
         KDFParameters var18 = new KDFParameters(var17, this.param.getDerivationV());
         this.kdf.init(var18);
         var7 = this.forEncryption ? this.encryptBlock(var1, var2, var3) : this.decryptBlock(var1, var2, var3);
      } finally {
         Arrays.fill(var17, (byte)0);
      }

      return var7;
   }

   protected byte[] getLengthTag(byte[] var1) {
      byte[] var2 = new byte[8];
      if (var1 != null) {
         Pack.longToBigEndian(var1.length * 8L, var2, 0);
      }

      return var2;
   }

   public static class HandshakeKDFFunction implements DigestDerivationFunction {
      private int counterStart;
      private Digest digest;
      private byte[] shared;
      private byte[] iv;

      public HandshakeKDFFunction(int var1, Digest var2) {
         this.counterStart = var1;
         this.digest = var2;
      }

      @Override
      public void init(DerivationParameters var1) {
         if (var1 instanceof KDFParameters) {
            KDFParameters var2 = (KDFParameters)var1;
            this.shared = var2.getSharedSecret();
            this.iv = var2.getIV();
         } else {
            if (!(var1 instanceof ISO18033KDFParameters)) {
               throw new IllegalArgumentException("KDF parameters required for generator");
            }

            ISO18033KDFParameters var3 = (ISO18033KDFParameters)var1;
            this.shared = var3.getSeed();
            this.iv = null;
         }
      }

      @Override
      public Digest getDigest() {
         return this.digest;
      }

      @Override
      public int generateBytes(byte[] var1, int var2, int var3) throws DataLengthException, IllegalArgumentException {
         if (var1.length - var3 < var2) {
            throw new OutputLengthException("output buffer too small");
         } else {
            long var4 = var3;
            int var6 = this.digest.getDigestSize();
            if (var4 > 8589934591L) {
               throw new IllegalArgumentException("output length too large");
            } else {
               int var7 = (int)((var4 + var6 - 1L) / var6);
               byte[] var8 = new byte[this.digest.getDigestSize()];
               byte[] var9 = new byte[4];
               Pack.intToBigEndian(this.counterStart, var9, 0);
               int var10 = this.counterStart & -256;

               for (int var11 = 0; var11 < var7; var11++) {
                  this.digest.update(var9, 0, var9.length);
                  this.digest.update(this.shared, 0, this.shared.length);
                  if (this.iv != null) {
                     this.digest.update(this.iv, 0, this.iv.length);
                  }

                  this.digest.doFinal(var8, 0);
                  if (var3 > var6) {
                     System.arraycopy(var8, 0, var1, var2, var6);
                     var2 += var6;
                     var3 -= var6;
                  } else {
                     System.arraycopy(var8, 0, var1, var2, var3);
                  }

                  if (++var9[3] == 0) {
                     var10 += 256;
                     Pack.intToBigEndian(var10, var9, 0);
                  }
               }

               this.digest.reset();
               return (int)var4;
            }
         }
      }
   }
}
