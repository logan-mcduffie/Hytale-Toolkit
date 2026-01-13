package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.EphemeralKeyPair;
import org.bouncycastle.crypto.KeyEncoder;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
import org.bouncycastle.crypto.engines.IESEngine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.EphemeralKeyPairGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;
import org.bouncycastle.jcajce.spec.IESKEMParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

public class IESKEMCipher extends BaseCipherSpi {
   private static final X9IntegerConverter converter = new X9IntegerConverter();
   private final JcaJceHelper helper = new BCJcaJceHelper();
   private final ECDHCBasicAgreement agreement;
   private final KDF2BytesGenerator kdf;
   private final Mac hMac;
   private final int macKeyLength;
   private final int macLength;
   private int ivLength;
   private IESEngine engine;
   private int state = -1;
   private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
   private AlgorithmParameters engineParam = null;
   private IESKEMParameterSpec engineSpec = null;
   private AsymmetricKeyParameter key;
   private SecureRandom random;
   private boolean dhaesMode = false;
   private AsymmetricKeyParameter otherKeyParameter = null;

   public IESKEMCipher(ECDHCBasicAgreement var1, KDF2BytesGenerator var2, Mac var3, int var4, int var5) {
      this.agreement = var1;
      this.kdf = var2;
      this.hMac = var3;
      this.macKeyLength = var4;
      this.macLength = var5;
   }

   @Override
   public int engineGetBlockSize() {
      return 0;
   }

   @Override
   public int engineGetKeySize(Key var1) {
      if (var1 instanceof ECKey) {
         return ((ECKey)var1).getParameters().getCurve().getFieldSize();
      } else {
         throw new IllegalArgumentException("not an EC key");
      }
   }

   @Override
   public byte[] engineGetIV() {
      return null;
   }

   @Override
   public AlgorithmParameters engineGetParameters() {
      if (this.engineParam == null && this.engineSpec != null) {
         try {
            this.engineParam = this.helper.createAlgorithmParameters("IES");
            this.engineParam.init(this.engineSpec);
         } catch (Exception var2) {
            throw new RuntimeException(var2.toString());
         }
      }

      return this.engineParam;
   }

   @Override
   public void engineSetMode(String var1) throws NoSuchAlgorithmException {
      throw new NoSuchAlgorithmException("can't support mode " + var1);
   }

   @Override
   public int engineGetOutputSize(int var1) {
      if (this.key == null) {
         throw new IllegalStateException("cipher not initialised");
      } else {
         int var2 = this.engine.getMac().getMacSize();
         int var3;
         if (this.otherKeyParameter == null) {
            ECCurve var5 = ((ECKeyParameters)this.key).getParameters().getCurve();
            int var6 = (var5.getFieldSize() + 7) / 8;
            var3 = 2 * var6;
         } else {
            var3 = 0;
         }

         int var7 = this.buffer.size() + var1;
         int var4;
         if (this.engine.getCipher() == null) {
            var4 = var7;
         } else if (this.state != 1 && this.state != 3) {
            if (this.state != 2 && this.state != 4) {
               throw new IllegalStateException("cipher not initialised");
            }

            var4 = this.engine.getCipher().getOutputSize(var7 - var2 - var3);
         } else {
            var4 = this.engine.getCipher().getOutputSize(var7);
         }

         if (this.state == 1 || this.state == 3) {
            return var2 + var3 + var4;
         } else if (this.state != 2 && this.state != 4) {
            throw new IllegalStateException("cipher not initialised");
         } else {
            return var4;
         }
      }
   }

   @Override
   public void engineSetPadding(String var1) throws NoSuchPaddingException {
      throw new NoSuchPaddingException("padding not available with IESCipher");
   }

   @Override
   public void engineInit(int var1, Key var2, AlgorithmParameters var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      AlgorithmParameterSpec var5 = null;
      if (var3 != null) {
         try {
            var5 = var3.getParameterSpec(IESParameterSpec.class);
         } catch (Exception var7) {
            throw new InvalidAlgorithmParameterException("cannot recognise parameters: " + var7.toString());
         }
      }

      this.engineParam = var3;
      this.engineInit(var1, var2, var5, var4);
   }

   @Override
   public void engineInit(int var1, Key var2, AlgorithmParameterSpec var3, SecureRandom var4) throws InvalidAlgorithmParameterException, InvalidKeyException {
      this.otherKeyParameter = null;
      this.engineSpec = (IESKEMParameterSpec)var3;
      if (var1 != 1 && var1 != 3) {
         if (var1 != 2 && var1 != 4) {
            throw new InvalidKeyException("must be passed EC key");
         }

         if (!(var2 instanceof PrivateKey)) {
            throw new InvalidKeyException("must be passed recipient's private EC key for decryption");
         }

         this.key = ECUtils.generatePrivateKeyParameter((PrivateKey)var2);
      } else {
         if (!(var2 instanceof PublicKey)) {
            throw new InvalidKeyException("must be passed recipient's public EC key for encryption");
         }

         this.key = ECUtils.generatePublicKeyParameter((PublicKey)var2);
      }

      this.random = var4;
      this.state = var1;
      this.buffer.reset();
   }

   @Override
   public void engineInit(int var1, Key var2, SecureRandom var3) throws InvalidKeyException {
      try {
         this.engineInit(var1, var2, (AlgorithmParameterSpec)null, var3);
      } catch (InvalidAlgorithmParameterException var5) {
         throw new IllegalArgumentException("cannot handle supplied parameter spec: " + var5.getMessage());
      }
   }

   @Override
   public byte[] engineUpdate(byte[] var1, int var2, int var3) {
      this.buffer.write(var1, var2, var3);
      return null;
   }

   @Override
   public int engineUpdate(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      this.buffer.write(var1, var2, var3);
      return 0;
   }

   @Override
   public byte[] engineDoFinal(byte[] var1, int var2, int var3) throws IllegalBlockSizeException, BadPaddingException {
      if (var3 != 0) {
         this.buffer.write(var1, var2, var3);
      }

      byte[] var4 = this.buffer.toByteArray();
      this.buffer.reset();
      ECDomainParameters var5 = ((ECKeyParameters)this.key).getParameters();
      if (this.state == 1 || this.state == 3) {
         ECKeyPairGenerator var16 = new ECKeyPairGenerator();
         var16.init(new ECKeyGenerationParameters(var5, this.random));
         final boolean var17 = this.engineSpec.hasUsePointCompression();
         EphemeralKeyPairGenerator var19 = new EphemeralKeyPairGenerator(var16, new KeyEncoder() {
            @Override
            public byte[] getEncoded(AsymmetricKeyParameter var1) {
               return ((ECPublicKeyParameters)var1).getQ().getEncoded(var17);
            }
         });
         EphemeralKeyPair var20 = var19.generate();
         this.agreement.init(var20.getKeyPair().getPrivate());
         byte[] var21 = converter.integerToBytes(this.agreement.calculateAgreement(this.key), converter.getByteLength(var5.getCurve()));
         byte[] var22 = new byte[var3 + this.macKeyLength];
         this.kdf.init(new KDFParameters(var21, this.engineSpec.getRecipientInfo()));
         this.kdf.generateBytes(var22, 0, var22.length);
         byte[] var23 = new byte[var3 + this.macLength];

         for (int var24 = 0; var24 != var3; var24++) {
            var23[var24] = (byte)(var1[var2 + var24] ^ var22[var24]);
         }

         KeyParameter var25 = new KeyParameter(var22, var3, var22.length - var3);
         this.hMac.init(var25);
         this.hMac.update(var23, 0, var3);
         byte[] var27 = new byte[this.hMac.getMacSize()];
         this.hMac.doFinal(var27, 0);
         Arrays.clear(var25.getKey());
         Arrays.clear(var22);
         System.arraycopy(var27, 0, var23, var3, this.macLength);
         return Arrays.concatenate(var20.getEncodedPublicKey(), var23);
      } else if (this.state != 2 && this.state != 4) {
         throw new IllegalStateException("cipher not initialised");
      } else {
         ECPrivateKeyParameters var6 = (ECPrivateKeyParameters)this.key;
         ECCurve var7 = var6.getParameters().getCurve();
         int var8 = (var7.getFieldSize() + 7) / 8;
         if (var1[var2] == 4) {
            var8 = 1 + 2 * var8;
         } else {
            var8++;
         }

         int var9 = var3 - (var8 + this.macLength);
         ECPoint var10 = var7.decodePoint(Arrays.copyOfRange(var1, var2, var2 + var8));
         this.agreement.init(this.key);
         byte[] var11 = converter.integerToBytes(
            this.agreement.calculateAgreement(new ECPublicKeyParameters(var10, var6.getParameters())), converter.getByteLength(var5.getCurve())
         );
         byte[] var12 = new byte[var9 + this.macKeyLength];
         this.kdf.init(new KDFParameters(var11, this.engineSpec.getRecipientInfo()));
         this.kdf.generateBytes(var12, 0, var12.length);
         byte[] var13 = new byte[var9];

         for (int var14 = 0; var14 != var13.length; var14++) {
            var13[var14] = (byte)(var1[var2 + var8 + var14] ^ var12[var14]);
         }

         KeyParameter var26 = new KeyParameter(var12, var9, var12.length - var9);
         this.hMac.init(var26);
         this.hMac.update(var1, var2 + var8, var13.length);
         byte[] var15 = new byte[this.hMac.getMacSize()];
         this.hMac.doFinal(var15, 0);
         Arrays.clear(var26.getKey());
         Arrays.clear(var12);
         if (!Arrays.constantTimeAreEqual(this.macLength, var15, 0, var1, var2 + (var3 - this.macLength))) {
            throw new BadPaddingException("mac field");
         } else {
            return var13;
         }
      }
   }

   @Override
   public int engineDoFinal(byte[] var1, int var2, int var3, byte[] var4, int var5) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
      byte[] var6 = this.engineDoFinal(var1, var2, var3);
      System.arraycopy(var6, 0, var4, var5, var6.length);
      return var6.length;
   }

   public static class KEM extends IESKEMCipher {
      public KEM(Digest var1, Digest var2, int var3, int var4) {
         super(new ECDHCBasicAgreement(), new KDF2BytesGenerator(var1), new HMac(var2), var3, var4);
      }
   }

   public static class KEMwithSHA256 extends IESKEMCipher.KEM {
      public KEMwithSHA256() {
         super(DigestFactory.createSHA256(), DigestFactory.createSHA256(), 32, 16);
      }
   }
}
