package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.interfaces.DHKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource.PSpecified;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.ISO9796d1Encoding;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.ElGamalEngine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;
import org.bouncycastle.jcajce.provider.util.BadBlockException;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import org.bouncycastle.jce.interfaces.ElGamalKey;
import org.bouncycastle.util.Strings;

public class CipherSpi extends BaseCipherSpi {
   private AsymmetricBlockCipher cipher;
   private AlgorithmParameterSpec paramSpec;
   private AlgorithmParameters engineParams;
   private BaseCipherSpi.ErasableOutputStream bOut = new BaseCipherSpi.ErasableOutputStream();

   public CipherSpi(AsymmetricBlockCipher var1) {
      this.cipher = var1;
   }

   private void initFromSpec(OAEPParameterSpec var1) throws NoSuchPaddingException {
      MGF1ParameterSpec var2 = (MGF1ParameterSpec)var1.getMGFParameters();
      Digest var3 = DigestFactory.getDigest(var2.getDigestAlgorithm());
      if (var3 == null) {
         throw new NoSuchPaddingException("no match on OAEP constructor for digest algorithm: " + var2.getDigestAlgorithm());
      } else {
         this.cipher = new OAEPEncoding(new ElGamalEngine(), var3, ((PSpecified)var1.getPSource()).getValue());
         this.paramSpec = var1;
      }
   }

   @Override
   protected int engineGetBlockSize() {
      return this.cipher.getInputBlockSize();
   }

   @Override
   protected int engineGetKeySize(Key var1) {
      if (var1 instanceof ElGamalKey) {
         ElGamalKey var3 = (ElGamalKey)var1;
         return var3.getParameters().getP().bitLength();
      } else if (var1 instanceof DHKey) {
         DHKey var2 = (DHKey)var1;
         return var2.getParams().getP().bitLength();
      } else {
         throw new IllegalArgumentException("not an ElGamal key!");
      }
   }

   @Override
   protected int engineGetOutputSize(int var1) {
      return this.cipher.getOutputBlockSize();
   }

   @Override
   protected AlgorithmParameters engineGetParameters() {
      if (this.engineParams == null && this.paramSpec != null) {
         try {
            this.engineParams = this.createParametersInstance("OAEP");
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
         throw new NoSuchAlgorithmException("can't support mode " + var1);
      }
   }

   @Override
   protected void engineSetPadding(String var1) throws NoSuchPaddingException {
      String var2 = Strings.toUpperCase(var1);
      if (var2.equals("NOPADDING")) {
         this.cipher = new ElGamalEngine();
      } else if (var2.equals("PKCS1PADDING")) {
         this.cipher = new PKCS1Encoding(new ElGamalEngine());
      } else if (var2.equals("ISO9796-1PADDING")) {
         this.cipher = new ISO9796d1Encoding(new ElGamalEngine());
      } else if (var2.equals("OAEPPADDING")) {
         this.initFromSpec(OAEPParameterSpec.DEFAULT);
      } else if (var2.equals("OAEPWITHMD5ANDMGF1PADDING")) {
         this.initFromSpec(new OAEPParameterSpec("MD5", "MGF1", new MGF1ParameterSpec("MD5"), PSpecified.DEFAULT));
      } else if (var2.equals("OAEPWITHSHA1ANDMGF1PADDING")) {
         this.initFromSpec(OAEPParameterSpec.DEFAULT);
      } else if (var2.equals("OAEPWITHSHA224ANDMGF1PADDING")) {
         this.initFromSpec(new OAEPParameterSpec("SHA-224", "MGF1", new MGF1ParameterSpec("SHA-224"), PSpecified.DEFAULT));
      } else if (var2.equals("OAEPWITHSHA256ANDMGF1PADDING")) {
         this.initFromSpec(new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSpecified.DEFAULT));
      } else if (var2.equals("OAEPWITHSHA384ANDMGF1PADDING")) {
         this.initFromSpec(new OAEPParameterSpec("SHA-384", "MGF1", MGF1ParameterSpec.SHA384, PSpecified.DEFAULT));
      } else if (var2.equals("OAEPWITHSHA512ANDMGF1PADDING")) {
         this.initFromSpec(new OAEPParameterSpec("SHA-512", "MGF1", MGF1ParameterSpec.SHA512, PSpecified.DEFAULT));
      } else if (var2.equals("OAEPWITHSHA3-224ANDMGF1PADDING")) {
         this.initFromSpec(new OAEPParameterSpec("SHA3-224", "MGF1", new MGF1ParameterSpec("SHA3-224"), PSpecified.DEFAULT));
      } else if (var2.equals("OAEPWITHSHA3-256ANDMGF1PADDING")) {
         this.initFromSpec(new OAEPParameterSpec("SHA3-256", "MGF1", new MGF1ParameterSpec("SHA3-256"), PSpecified.DEFAULT));
      } else if (var2.equals("OAEPWITHSHA3-384ANDMGF1PADDING")) {
         this.initFromSpec(new OAEPParameterSpec("SHA3-384", "MGF1", new MGF1ParameterSpec("SHA3-384"), PSpecified.DEFAULT));
      } else {
         if (!var2.equals("OAEPWITHSHA3-512ANDMGF1PADDING")) {
            throw new NoSuchPaddingException(var1 + " unavailable with ElGamal.");
         }

         this.initFromSpec(new OAEPParameterSpec("SHA3-512", "MGF1", new MGF1ParameterSpec("SHA3-512"), PSpecified.DEFAULT));
      }
   }

   @Override
   protected void engineInit(int var1, Key var2, AlgorithmParameterSpec var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      Object var5;
      if (var2 instanceof DHPublicKey) {
         var5 = ElGamalUtil.generatePublicKeyParameter((PublicKey)var2);
      } else {
         if (!(var2 instanceof DHPrivateKey)) {
            throw new InvalidKeyException("unknown key type passed to ElGamal");
         }

         var5 = ElGamalUtil.generatePrivateKeyParameter((PrivateKey)var2);
      }

      if (var3 instanceof OAEPParameterSpec) {
         OAEPParameterSpec var6 = (OAEPParameterSpec)var3;
         this.paramSpec = var3;
         if (!var6.getMGFAlgorithm().equalsIgnoreCase("MGF1") && !var6.getMGFAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1.getId())) {
            throw new InvalidAlgorithmParameterException("unknown mask generation function specified");
         }

         if (!(var6.getMGFParameters() instanceof MGF1ParameterSpec)) {
            throw new InvalidAlgorithmParameterException("unkown MGF parameters");
         }

         Digest var7 = DigestFactory.getDigest(var6.getDigestAlgorithm());
         if (var7 == null) {
            throw new InvalidAlgorithmParameterException("no match on digest algorithm: " + var6.getDigestAlgorithm());
         }

         MGF1ParameterSpec var8 = (MGF1ParameterSpec)var6.getMGFParameters();
         Digest var9 = DigestFactory.getDigest(var8.getDigestAlgorithm());
         if (var9 == null) {
            throw new InvalidAlgorithmParameterException("no match on MGF digest algorithm: " + var8.getDigestAlgorithm());
         }

         this.cipher = new OAEPEncoding(new ElGamalEngine(), var7, var9, ((PSpecified)var6.getPSource()).getValue());
      } else if (var3 != null) {
         throw new InvalidAlgorithmParameterException("unknown parameter type.");
      }

      if (var4 != null) {
         var5 = new ParametersWithRandom((CipherParameters)var5, var4);
      }

      switch (var1) {
         case 1:
         case 3:
            this.cipher.init(true, (CipherParameters)var5);
            break;
         case 2:
         case 4:
            this.cipher.init(false, (CipherParameters)var5);
            break;
         default:
            throw new InvalidParameterException("unknown opmode " + var1 + " passed to ElGamal");
      }
   }

   @Override
   protected void engineInit(int var1, Key var2, AlgorithmParameters var3, SecureRandom var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException("can't handle parameters in ElGamal");
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
      this.bOut.write(var1, var2, var3);
      return null;
   }

   @Override
   protected int engineUpdate(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      this.bOut.write(var1, var2, var3);
      return 0;
   }

   @Override
   protected byte[] engineDoFinal(byte[] var1, int var2, int var3) throws IllegalBlockSizeException, BadPaddingException {
      if (var1 != null) {
         this.bOut.write(var1, var2, var3);
      }

      if (this.cipher instanceof ElGamalEngine) {
         if (this.bOut.size() > this.cipher.getInputBlockSize() + 1) {
            throw new ArrayIndexOutOfBoundsException("too much data for ElGamal block");
         }
      } else if (this.bOut.size() > this.cipher.getInputBlockSize()) {
         throw new ArrayIndexOutOfBoundsException("too much data for ElGamal block");
      }

      return this.getOutput();
   }

   @Override
   protected int engineDoFinal(byte[] var1, int var2, int var3, byte[] var4, int var5) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException {
      if (var5 + this.engineGetOutputSize(var3) > var4.length) {
         throw new ShortBufferException("output buffer too short for input.");
      } else {
         if (var1 != null) {
            this.bOut.write(var1, var2, var3);
         }

         if (this.cipher instanceof ElGamalEngine) {
            if (this.bOut.size() > this.cipher.getInputBlockSize() + 1) {
               throw new ArrayIndexOutOfBoundsException("too much data for ElGamal block");
            }
         } else if (this.bOut.size() > this.cipher.getInputBlockSize()) {
            throw new ArrayIndexOutOfBoundsException("too much data for ElGamal block");
         }

         byte[] var6 = this.getOutput();

         for (int var7 = 0; var7 != var6.length; var7++) {
            var4[var5 + var7] = var6[var7];
         }

         return var6.length;
      }
   }

   private byte[] getOutput() throws BadPaddingException {
      byte[] var1;
      try {
         var1 = this.cipher.processBlock(this.bOut.getBuf(), 0, this.bOut.size());
      } catch (InvalidCipherTextException var6) {
         throw new BadBlockException("unable to decrypt block", var6);
      } catch (ArrayIndexOutOfBoundsException var7) {
         throw new BadBlockException("unable to decrypt block", var7);
      } finally {
         this.bOut.erase();
      }

      return var1;
   }

   public static class NoPadding extends CipherSpi {
      public NoPadding() {
         super(new ElGamalEngine());
      }
   }

   public static class PKCS1v1_5Padding extends CipherSpi {
      public PKCS1v1_5Padding() {
         super(new PKCS1Encoding(new ElGamalEngine()));
      }
   }
}
