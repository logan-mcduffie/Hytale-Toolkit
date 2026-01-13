package org.bouncycastle.jcajce.provider.asymmetric.edec;

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
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.KeyEncoder;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.agreement.XDHBasicAgreement;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.IESEngine;
import org.bouncycastle.crypto.generators.EphemeralKeyPairGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.X448KeyPairGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.IESWithCipherParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.crypto.parsers.XIESPublicKeyParser;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.interfaces.XDHKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.IESUtil;
import org.bouncycastle.jcajce.provider.util.BadBlockException;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Strings;

public class IESCipher extends BaseCipherSpi {
   private final JcaJceHelper helper = new BCJcaJceHelper();
   private int ivLength;
   private IESEngine engine;
   private int state = -1;
   private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
   private AlgorithmParameters engineParam = null;
   private IESParameterSpec engineSpec = null;
   private AsymmetricKeyParameter key;
   private SecureRandom random;
   private boolean dhaesMode = false;
   private AsymmetricKeyParameter otherKeyParameter = null;

   public IESCipher(IESEngine var1) {
      this.engine = var1;
      this.ivLength = 0;
   }

   public IESCipher(IESEngine var1, int var2) {
      this.engine = var1;
      this.ivLength = var2;
   }

   @Override
   public int engineGetBlockSize() {
      BufferedBlockCipher var1 = this.engine.getCipher();
      return var1 == null ? 0 : var1.getBlockSize();
   }

   @Override
   public int engineGetKeySize(Key var1) {
      if (var1 instanceof XDHKey) {
         String var2 = ((XDHKey)var1).getAlgorithm();
         if ("X25519".equalsIgnoreCase(var2)) {
            return 256;
         } else if ("X448".equalsIgnoreCase(var2)) {
            return 448;
         } else {
            throw new IllegalArgumentException("unknown XDH key algorithm " + var2);
         }
      } else {
         throw new IllegalArgumentException("not an XDH key");
      }
   }

   @Override
   public byte[] engineGetIV() {
      return this.engineSpec != null ? this.engineSpec.getNonce() : null;
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
      String var2 = Strings.toUpperCase(var1);
      if (var2.equals("NONE")) {
         this.dhaesMode = false;
      } else {
         if (!var2.equals("DHAES")) {
            throw new IllegalArgumentException("can't support mode " + var1);
         }

         this.dhaesMode = true;
      }
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
      String var2 = Strings.toUpperCase(var1);
      if (!var2.equals("NOPADDING") && !var2.equals("PKCS5PADDING") && !var2.equals("PKCS7PADDING")) {
         throw new NoSuchPaddingException("padding not available with IESCipher");
      }
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
      if (var3 == null && this.ivLength == 0) {
         this.engineSpec = IESUtil.guessParameterSpec(this.engine.getCipher(), null);
      } else {
         if (!(var3 instanceof IESParameterSpec)) {
            throw new InvalidAlgorithmParameterException("must be passed IES parameters");
         }

         this.engineSpec = (IESParameterSpec)var3;
      }

      byte[] var5 = this.engineSpec.getNonce();
      if (this.ivLength == 0 || var5 != null && var5.length == this.ivLength) {
         if (var1 != 1 && var1 != 3) {
            if (var1 != 2 && var1 != 4) {
               throw new InvalidKeyException("must be passed XDH key");
            }

            if (!(var2 instanceof PrivateKey)) {
               throw new InvalidKeyException("must be passed recipient's private XDH key for decryption");
            }

            this.key = EdECUtil.generatePrivateKeyParameter((PrivateKey)var2);
         } else {
            if (!(var2 instanceof PublicKey)) {
               throw new InvalidKeyException("must be passed recipient's public XDH key for encryption");
            }

            this.key = EdECUtil.generatePublicKeyParameter((PublicKey)var2);
         }

         this.random = var4;
         this.state = var1;
         this.buffer.reset();
      } else {
         throw new InvalidAlgorithmParameterException("NONCE in IES Parameters needs to be " + this.ivLength + " bytes long");
      }
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
      Object var5 = new IESWithCipherParameters(
         this.engineSpec.getDerivationV(), this.engineSpec.getEncodingV(), this.engineSpec.getMacKeySize(), this.engineSpec.getCipherKeySize()
      );
      byte[] var6 = this.engineSpec.getNonce();
      if (var6 != null) {
         var5 = new ParametersWithIV((CipherParameters)var5, var6);
      }

      if (this.otherKeyParameter != null) {
         try {
            if (this.state != 1 && this.state != 3) {
               this.engine.init(false, this.key, this.otherKeyParameter, (CipherParameters)var5);
            } else {
               this.engine.init(true, this.otherKeyParameter, this.key, (CipherParameters)var5);
            }

            return this.engine.processBlock(var4, 0, var4.length);
         } catch (Exception var14) {
            throw new BadBlockException("unable to process block", var14);
         }
      } else {
         final boolean var7 = this.key instanceof X25519PublicKeyParameters || this.key instanceof X25519PrivateKeyParameters;
         int var8 = var7 ? 256 : 448;
         if (this.state != 1 && this.state != 3) {
            if (this.state != 2 && this.state != 4) {
               throw new IllegalStateException("cipher not initialised");
            } else {
               try {
                  this.engine.init(this.key, (CipherParameters)var5, new XIESPublicKeyParser(var7));
                  return this.engine.processBlock(var4, 0, var4.length);
               } catch (InvalidCipherTextException var12) {
                  throw new BadBlockException("unable to process block", var12);
               }
            }
         } else {
            Object var9 = var7 ? new X25519KeyPairGenerator() : new X448KeyPairGenerator();
            ((AsymmetricCipherKeyPairGenerator)var9).init(new KeyGenerationParameters(this.random, var8));
            EphemeralKeyPairGenerator var10 = new EphemeralKeyPairGenerator((AsymmetricCipherKeyPairGenerator)var9, new KeyEncoder() {
               @Override
               public byte[] getEncoded(AsymmetricKeyParameter var1) {
                  return var7 ? ((X25519PublicKeyParameters)var1).getEncoded() : ((X448PublicKeyParameters)var1).getEncoded();
               }
            });

            try {
               this.engine.init(this.key, (CipherParameters)var5, var10);
               return this.engine.processBlock(var4, 0, var4.length);
            } catch (Exception var13) {
               throw new BadBlockException("unable to process block", var13);
            }
         }
      }
   }

   @Override
   public int engineDoFinal(byte[] var1, int var2, int var3, byte[] var4, int var5) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
      byte[] var6 = this.engineDoFinal(var1, var2, var3);
      System.arraycopy(var6, 0, var4, var5, var6.length);
      return var6.length;
   }

   public static class XIES extends IESCipher {
      public XIES() {
         this(DigestFactory.createSHA1(), DigestFactory.createSHA1());
      }

      public XIES(Digest var1, Digest var2) {
         super(new IESEngine(new XDHBasicAgreement(), new KDF2BytesGenerator(var1), new HMac(var2)));
      }
   }

   public static class XIESwithAESCBC extends IESCipher.XIESwithCipher {
      public XIESwithAESCBC() {
         super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 16);
      }
   }

   public static class XIESwithCipher extends IESCipher {
      public XIESwithCipher(BlockCipher var1, int var2) {
         this(var1, var2, DigestFactory.createSHA1(), DigestFactory.createSHA1());
      }

      public XIESwithCipher(BlockCipher var1, int var2, Digest var3, Digest var4) {
         super(new IESEngine(new XDHBasicAgreement(), new KDF2BytesGenerator(var3), new HMac(var4), new PaddedBufferedBlockCipher(var1)), var2);
      }
   }

   public static class XIESwithDESedeCBC extends IESCipher.XIESwithCipher {
      public XIESwithDESedeCBC() {
         super(CBCBlockCipher.newInstance(new DESedeEngine()), 8);
      }
   }

   public static class XIESwithSHA256 extends IESCipher.XIES {
      public XIESwithSHA256() {
         super(DigestFactory.createSHA256(), DigestFactory.createSHA256());
      }
   }

   public static class XIESwithSHA256andAESCBC extends IESCipher.XIESwithCipher {
      public XIESwithSHA256andAESCBC() {
         super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 16, DigestFactory.createSHA256(), DigestFactory.createSHA256());
      }
   }

   public static class XIESwithSHA256andDESedeCBC extends IESCipher.XIESwithCipher {
      public XIESwithSHA256andDESedeCBC() {
         super(CBCBlockCipher.newInstance(new DESedeEngine()), 8, DigestFactory.createSHA256(), DigestFactory.createSHA256());
      }
   }

   public static class XIESwithSHA384 extends IESCipher.XIES {
      public XIESwithSHA384() {
         super(DigestFactory.createSHA384(), DigestFactory.createSHA384());
      }
   }

   public static class XIESwithSHA384andAESCBC extends IESCipher.XIESwithCipher {
      public XIESwithSHA384andAESCBC() {
         super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 16, DigestFactory.createSHA384(), DigestFactory.createSHA384());
      }
   }

   public static class XIESwithSHA384andDESedeCBC extends IESCipher.XIESwithCipher {
      public XIESwithSHA384andDESedeCBC() {
         super(CBCBlockCipher.newInstance(new DESedeEngine()), 8, DigestFactory.createSHA384(), DigestFactory.createSHA384());
      }
   }

   public static class XIESwithSHA512 extends IESCipher.XIES {
      public XIESwithSHA512() {
         super(DigestFactory.createSHA512(), DigestFactory.createSHA512());
      }
   }

   public static class XIESwithSHA512andAESCBC extends IESCipher.XIESwithCipher {
      public XIESwithSHA512andAESCBC() {
         super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 16, DigestFactory.createSHA512(), DigestFactory.createSHA512());
      }
   }

   public static class XIESwithSHA512andDESedeCBC extends IESCipher.XIESwithCipher {
      public XIESwithSHA512andDESedeCBC() {
         super(CBCBlockCipher.newInstance(new DESedeEngine()), 8, DigestFactory.createSHA512(), DigestFactory.createSHA512());
      }
   }
}
