package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource.PSpecified;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.ISO9796d1Encoding;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.tls.TlsRsaKeyExchange;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;
import org.bouncycastle.jcajce.provider.util.BadBlockException;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import org.bouncycastle.jcajce.spec.TLSRSAPremasterSecretParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Strings;

public class CipherSpi extends BaseCipherSpi {
   private final JcaJceHelper helper = new BCJcaJceHelper();
   private AsymmetricBlockCipher cipher;
   private AlgorithmParameterSpec paramSpec;
   private AlgorithmParameters engineParams;
   private boolean publicKeyOnly = false;
   private boolean privateKeyOnly = false;
   private BaseCipherSpi.ErasableOutputStream bOut = new BaseCipherSpi.ErasableOutputStream();
   private TLSRSAPremasterSecretParameterSpec tlsRsaSpec = null;
   private CipherParameters param = null;

   public CipherSpi(AsymmetricBlockCipher var1) {
      this.cipher = var1;
   }

   public CipherSpi(OAEPParameterSpec var1) {
      try {
         this.initFromSpec(var1);
      } catch (NoSuchPaddingException var3) {
         throw new IllegalArgumentException(var3.getMessage());
      }
   }

   public CipherSpi(boolean var1, boolean var2, AsymmetricBlockCipher var3) {
      this.publicKeyOnly = var1;
      this.privateKeyOnly = var2;
      this.cipher = var3;
   }

   private void initFromSpec(OAEPParameterSpec var1) throws NoSuchPaddingException {
      MGF1ParameterSpec var2 = (MGF1ParameterSpec)var1.getMGFParameters();
      Digest var3 = DigestFactory.getDigest(var2.getDigestAlgorithm());
      if (var3 == null) {
         throw new NoSuchPaddingException("no match on OAEP constructor for digest algorithm: " + var2.getDigestAlgorithm());
      } else {
         this.cipher = new OAEPEncoding(new RSABlindedEngine(), var3, ((PSpecified)var1.getPSource()).getValue());
         this.paramSpec = var1;
      }
   }

   @Override
   protected int engineGetBlockSize() {
      try {
         return this.cipher.getInputBlockSize();
      } catch (NullPointerException var2) {
         throw new IllegalStateException("RSA Cipher not initialised");
      }
   }

   @Override
   protected int engineGetKeySize(Key var1) {
      if (var1 instanceof RSAPrivateKey) {
         RSAPrivateKey var3 = (RSAPrivateKey)var1;
         return var3.getModulus().bitLength();
      } else if (var1 instanceof RSAPublicKey) {
         RSAPublicKey var2 = (RSAPublicKey)var1;
         return var2.getModulus().bitLength();
      } else {
         throw new IllegalArgumentException("not an RSA key!");
      }
   }

   @Override
   protected int engineGetOutputSize(int var1) {
      if (this.tlsRsaSpec != null) {
         return 48;
      } else {
         try {
            return this.cipher.getOutputBlockSize();
         } catch (NullPointerException var3) {
            throw new IllegalStateException("RSA Cipher not initialised");
         }
      }
   }

   @Override
   protected AlgorithmParameters engineGetParameters() {
      if (this.engineParams == null && this.paramSpec != null) {
         try {
            this.engineParams = this.helper.createAlgorithmParameters("OAEP");
            this.engineParams.init(this.paramSpec);
         } catch (Exception var2) {
            throw new RuntimeException(var2.toString());
         }
      }

      return this.engineParams;
   }

   @Override
   protected void engineSetMode(String var1) throws NoSuchAlgorithmException {
      String var2 = Strings.toUpperCase(var1);
      if (!var2.equals("NONE") && !var2.equals("ECB")) {
         if (var2.equals("1")) {
            this.privateKeyOnly = true;
            this.publicKeyOnly = false;
         } else if (var2.equals("2")) {
            this.privateKeyOnly = false;
            this.publicKeyOnly = true;
         } else {
            throw new NoSuchAlgorithmException("can't support mode " + var1);
         }
      }
   }

   @Override
   protected void engineSetPadding(String var1) throws NoSuchPaddingException {
      String var2 = Strings.toUpperCase(var1);
      if (var2.equals("NOPADDING")) {
         this.cipher = new RSABlindedEngine();
      } else if (var2.equals("PKCS1PADDING")) {
         this.cipher = new CustomPKCS1Encoding(new RSABlindedEngine());
      } else if (var2.equals("ISO9796-1PADDING")) {
         this.cipher = new ISO9796d1Encoding(new RSABlindedEngine());
      } else if (var2.equals("OAEPWITHMD5ANDMGF1PADDING")) {
         this.initFromSpec(new OAEPParameterSpec("MD5", "MGF1", new MGF1ParameterSpec("MD5"), PSpecified.DEFAULT));
      } else if (var2.equals("OAEPPADDING")) {
         this.initFromSpec(OAEPParameterSpec.DEFAULT);
      } else if (var2.equals("OAEPWITHSHA1ANDMGF1PADDING") || var2.equals("OAEPWITHSHA-1ANDMGF1PADDING")) {
         this.initFromSpec(OAEPParameterSpec.DEFAULT);
      } else if (var2.equals("OAEPWITHSHA224ANDMGF1PADDING") || var2.equals("OAEPWITHSHA-224ANDMGF1PADDING")) {
         this.initFromSpec(new OAEPParameterSpec("SHA-224", "MGF1", new MGF1ParameterSpec("SHA-224"), PSpecified.DEFAULT));
      } else if (var2.equals("OAEPWITHSHA256ANDMGF1PADDING") || var2.equals("OAEPWITHSHA-256ANDMGF1PADDING")) {
         this.initFromSpec(new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSpecified.DEFAULT));
      } else if (var2.equals("OAEPWITHSHA384ANDMGF1PADDING") || var2.equals("OAEPWITHSHA-384ANDMGF1PADDING")) {
         this.initFromSpec(new OAEPParameterSpec("SHA-384", "MGF1", MGF1ParameterSpec.SHA384, PSpecified.DEFAULT));
      } else if (var2.equals("OAEPWITHSHA512ANDMGF1PADDING") || var2.equals("OAEPWITHSHA-512ANDMGF1PADDING")) {
         this.initFromSpec(new OAEPParameterSpec("SHA-512", "MGF1", MGF1ParameterSpec.SHA512, PSpecified.DEFAULT));
      } else if (var2.equals("OAEPWITHSHA3-224ANDMGF1PADDING")) {
         this.initFromSpec(new OAEPParameterSpec("SHA3-224", "MGF1", new MGF1ParameterSpec("SHA3-224"), PSpecified.DEFAULT));
      } else if (var2.equals("OAEPWITHSHA3-256ANDMGF1PADDING")) {
         this.initFromSpec(new OAEPParameterSpec("SHA3-256", "MGF1", new MGF1ParameterSpec("SHA3-256"), PSpecified.DEFAULT));
      } else if (var2.equals("OAEPWITHSHA3-384ANDMGF1PADDING")) {
         this.initFromSpec(new OAEPParameterSpec("SHA3-384", "MGF1", new MGF1ParameterSpec("SHA3-384"), PSpecified.DEFAULT));
      } else {
         if (!var2.equals("OAEPWITHSHA3-512ANDMGF1PADDING")) {
            throw new NoSuchPaddingException(var1 + " unavailable with RSA.");
         }

         this.initFromSpec(new OAEPParameterSpec("SHA3-512", "MGF1", new MGF1ParameterSpec("SHA3-512"), PSpecified.DEFAULT));
      }
   }

   @Override
   protected void engineInit(int var1, Key var2, AlgorithmParameterSpec var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      this.tlsRsaSpec = null;
      if (var3 != null && !(var3 instanceof OAEPParameterSpec) && !(var3 instanceof TLSRSAPremasterSecretParameterSpec)) {
         throw new InvalidAlgorithmParameterException("unknown parameter type: " + var3.getClass().getName());
      } else {
         if (var2 instanceof RSAPublicKey) {
            if (this.privateKeyOnly && var1 == 1) {
               throw new InvalidKeyException("mode 1 requires RSAPrivateKey");
            }

            this.param = RSAUtil.generatePublicKeyParameter((RSAPublicKey)var2);
         } else {
            if (!(var2 instanceof RSAPrivateKey)) {
               throw new InvalidKeyException("unknown key type passed to RSA");
            }

            if (this.publicKeyOnly && var1 == 1) {
               throw new InvalidKeyException("mode 2 requires RSAPublicKey");
            }

            this.param = RSAUtil.generatePrivateKeyParameter((RSAPrivateKey)var2);
         }

         if (var3 instanceof OAEPParameterSpec) {
            OAEPParameterSpec var5 = (OAEPParameterSpec)var3;
            this.paramSpec = var3;
            if (!var5.getMGFAlgorithm().equalsIgnoreCase("MGF1") && !var5.getMGFAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1.getId())) {
               throw new InvalidAlgorithmParameterException("unknown mask generation function specified");
            }

            if (!(var5.getMGFParameters() instanceof MGF1ParameterSpec)) {
               throw new InvalidAlgorithmParameterException("unkown MGF parameters");
            }

            Digest var6 = DigestFactory.getDigest(var5.getDigestAlgorithm());
            if (var6 == null) {
               throw new InvalidAlgorithmParameterException("no match on digest algorithm: " + var5.getDigestAlgorithm());
            }

            MGF1ParameterSpec var7 = (MGF1ParameterSpec)var5.getMGFParameters();
            Digest var8 = DigestFactory.getDigest(var7.getDigestAlgorithm());
            if (var8 == null) {
               throw new InvalidAlgorithmParameterException("no match on MGF digest algorithm: " + var7.getDigestAlgorithm());
            }

            this.cipher = new OAEPEncoding(new RSABlindedEngine(), var6, var8, ((PSpecified)var5.getPSource()).getValue());
         } else if (var3 instanceof TLSRSAPremasterSecretParameterSpec) {
            if (!(this.param instanceof RSAKeyParameters) || !((RSAKeyParameters)this.param).isPrivate()) {
               throw new InvalidKeyException("RSA private key required for TLS decryption");
            }

            this.tlsRsaSpec = (TLSRSAPremasterSecretParameterSpec)var3;
         }

         if (var4 != null) {
            this.param = new ParametersWithRandom(this.param, var4);
         } else {
            this.param = new ParametersWithRandom(this.param, CryptoServicesRegistrar.getSecureRandom());
         }

         this.bOut.reset();
         switch (var1) {
            case 1:
            case 3:
               this.cipher.init(true, this.param);
               break;
            case 2:
            case 4:
               this.cipher.init(false, this.param);
               break;
            default:
               throw new InvalidParameterException("unknown opmode " + var1 + " passed to RSA");
         }
      }
   }

   @Override
   protected void engineInit(int var1, Key var2, AlgorithmParameters var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      AlgorithmParameterSpec var5 = null;
      if (var3 != null) {
         try {
            var5 = var3.getParameterSpec(OAEPParameterSpec.class);
         } catch (InvalidParameterSpecException var7) {
            throw new InvalidAlgorithmParameterException("cannot recognise parameters: " + var7.toString(), var7);
         }
      }

      this.engineParams = var3;
      this.engineInit(var1, var2, var5, var4);
   }

   @Override
   protected void engineInit(int var1, Key var2, SecureRandom var3) throws InvalidKeyException {
      try {
         this.engineInit(var1, var2, (AlgorithmParameterSpec)null, var3);
      } catch (InvalidAlgorithmParameterException var5) {
         throw new InvalidKeyException("Eeeek! " + var5.toString(), var5);
      }
   }

   @Override
   protected byte[] engineUpdate(byte[] var1, int var2, int var3) {
      if (var3 > this.getInputLimit() - this.bOut.size()) {
         throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
      } else {
         this.bOut.write(var1, var2, var3);
         return null;
      }
   }

   @Override
   protected int engineUpdate(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      this.engineUpdate(var1, var2, var3);
      return 0;
   }

   @Override
   protected byte[] engineDoFinal(byte[] var1, int var2, int var3) throws IllegalBlockSizeException, BadPaddingException {
      if (var1 != null) {
         this.engineUpdate(var1, var2, var3);
      }

      return this.getOutput();
   }

   @Override
   protected int engineDoFinal(byte[] var1, int var2, int var3, byte[] var4, int var5) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException {
      int var6 = this.engineGetOutputSize(var1 == null ? 0 : var3);
      if (var5 > var4.length - var6) {
         throw new ShortBufferException("output buffer too short for input.");
      } else {
         byte[] var7 = this.engineDoFinal(var1, var2, var3);
         System.arraycopy(var7, 0, var4, var5, var7.length);
         return var7.length;
      }
   }

   private int getInputLimit() {
      if (this.tlsRsaSpec != null) {
         ParametersWithRandom var1 = (ParametersWithRandom)this.param;
         return TlsRsaKeyExchange.getInputLimit((RSAKeyParameters)var1.getParameters());
      } else {
         return this.cipher instanceof RSABlindedEngine ? this.cipher.getInputBlockSize() + 1 : this.cipher.getInputBlockSize();
      }
   }

   private byte[] getOutput() throws BadPaddingException {
      byte[] var2;
      try {
         if (this.tlsRsaSpec == null) {
            byte[] var10;
            try {
               var10 = this.cipher.processBlock(this.bOut.getBuf(), 0, this.bOut.size());
            } catch (InvalidCipherTextException var7) {
               throw new BadBlockException("unable to decrypt block", var7);
            } catch (ArrayIndexOutOfBoundsException var8) {
               throw new BadBlockException("unable to decrypt block", var8);
            }

            if (var10 == null) {
               throw new BadBlockException("unable to decrypt block", null);
            }

            return var10;
         }

         ParametersWithRandom var1 = (ParametersWithRandom)this.param;
         var2 = TlsRsaKeyExchange.decryptPreMasterSecret(
            this.bOut.getBuf(), 0, this.bOut.size(), (RSAKeyParameters)var1.getParameters(), this.tlsRsaSpec.getProtocolVersion(), var1.getRandom()
         );
      } finally {
         this.bOut.erase();
      }

      return var2;
   }

   public static class ISO9796d1Padding extends CipherSpi {
      public ISO9796d1Padding() {
         super(new ISO9796d1Encoding(new RSABlindedEngine()));
      }
   }

   public static class NoPadding extends CipherSpi {
      public NoPadding() {
         super(new RSABlindedEngine());
      }
   }

   public static class OAEPPadding extends CipherSpi {
      public OAEPPadding() {
         super(OAEPParameterSpec.DEFAULT);
      }
   }

   public static class PKCS1v1_5Padding extends CipherSpi {
      public PKCS1v1_5Padding() {
         super(new CustomPKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class PKCS1v1_5Padding_PrivateOnly extends CipherSpi {
      public PKCS1v1_5Padding_PrivateOnly() {
         super(false, true, new CustomPKCS1Encoding(new RSABlindedEngine()));
      }
   }

   public static class PKCS1v1_5Padding_PublicOnly extends CipherSpi {
      public PKCS1v1_5Padding_PublicOnly() {
         super(true, false, new CustomPKCS1Encoding(new RSABlindedEngine()));
      }
   }
}
