package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;

public class PSSSignatureSpi extends SignatureSpi {
   private final JcaJceHelper helper = new BCJcaJceHelper();
   private AlgorithmParameters engineParams;
   private PSSParameterSpec paramSpec;
   private PSSParameterSpec originalSpec;
   private AsymmetricBlockCipher signer;
   private Digest contentDigest;
   private Digest mgfDigest;
   private int saltLength;
   private byte trailer;
   private boolean isRaw;
   private RSAKeyParameters key;
   private SecureRandom random;
   private PSSSigner pss;
   private boolean isInitState = true;

   private byte getTrailer(int var1) {
      if (var1 == 1) {
         return -68;
      } else {
         throw new IllegalArgumentException("unknown trailer field");
      }
   }

   private void setupContentDigest() {
      this.contentDigest = DigestFactory.getDigest(this.paramSpec.getDigestAlgorithm());
      if (this.isRaw) {
         this.contentDigest = new PSSSignatureSpi.NullPssDigest(this.contentDigest);
      }
   }

   protected PSSSignatureSpi(AsymmetricBlockCipher var1, PSSParameterSpec var2) {
      this(var1, var2, false);
   }

   protected PSSSignatureSpi(AsymmetricBlockCipher var1, PSSParameterSpec var2, boolean var3) {
      this.signer = var1;
      this.originalSpec = var2;
      if (var2 == null) {
         this.paramSpec = PSSParameterSpec.DEFAULT;
      } else {
         this.paramSpec = var2;
      }

      if ("MGF1".equals(this.paramSpec.getMGFAlgorithm())) {
         this.mgfDigest = DigestFactory.getDigest(this.paramSpec.getDigestAlgorithm());
      } else {
         this.mgfDigest = DigestFactory.getDigest(this.paramSpec.getMGFAlgorithm());
      }

      this.saltLength = this.paramSpec.getSaltLength();
      this.trailer = this.getTrailer(this.paramSpec.getTrailerField());
      this.isRaw = var3;
      this.setupContentDigest();
   }

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      if (!(var1 instanceof RSAPublicKey)) {
         throw new InvalidKeyException("Supplied key is not a RSAPublicKey instance");
      } else {
         this.key = RSAUtil.generatePublicKeyParameter((RSAPublicKey)var1);
         this.pss = new PSSSigner(this.signer, this.contentDigest, this.mgfDigest, this.saltLength, this.trailer);
         this.pss.init(false, this.key);
         this.isInitState = true;
      }
   }

   @Override
   protected void engineInitSign(PrivateKey var1, SecureRandom var2) throws InvalidKeyException {
      this.random = var2;
      this.engineInitSign(var1);
   }

   @Override
   protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      if (!(var1 instanceof RSAPrivateKey)) {
         throw new InvalidKeyException("Supplied key is not a RSAPrivateKey instance");
      } else {
         this.key = RSAUtil.generatePrivateKeyParameter((RSAPrivateKey)var1);
         this.pss = new PSSSigner(this.signer, this.contentDigest, this.mgfDigest, this.saltLength, this.trailer);
         if (this.random != null) {
            this.pss.init(true, new ParametersWithRandom(this.key, this.random));
         } else {
            this.pss.init(true, this.key);
         }

         this.isInitState = true;
      }
   }

   @Override
   protected void engineUpdate(byte var1) throws SignatureException {
      this.pss.update(var1);
      this.isInitState = false;
   }

   @Override
   protected void engineUpdate(byte[] var1, int var2, int var3) throws SignatureException {
      this.pss.update(var1, var2, var3);
      this.isInitState = false;
   }

   @Override
   protected byte[] engineSign() throws SignatureException {
      this.isInitState = true;

      try {
         return this.pss.generateSignature();
      } catch (CryptoException var2) {
         throw new SignatureException(var2.getMessage());
      }
   }

   @Override
   protected boolean engineVerify(byte[] var1) throws SignatureException {
      this.isInitState = true;
      return this.pss.verifySignature(var1);
   }

   @Override
   protected void engineSetParameter(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
      if (var1 == null) {
         if (this.originalSpec == null) {
            return;
         }

         var1 = this.originalSpec;
      }

      if (!this.isInitState) {
         throw new ProviderException("cannot call setParameter in the middle of update");
      } else if (var1 instanceof PSSParameterSpec) {
         PSSParameterSpec var2 = (PSSParameterSpec)var1;
         if (this.originalSpec != null && !DigestFactory.isSameDigest(this.originalSpec.getDigestAlgorithm(), var2.getDigestAlgorithm())) {
            throw new InvalidAlgorithmParameterException("parameter must be using " + this.originalSpec.getDigestAlgorithm());
         } else {
            Digest var3;
            if (!var2.getMGFAlgorithm().equalsIgnoreCase("MGF1") && !var2.getMGFAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1.getId())) {
               if (!var2.getMGFAlgorithm().equals("SHAKE128") && !var2.getMGFAlgorithm().equals("SHAKE256")) {
                  throw new InvalidAlgorithmParameterException("unknown mask generation function specified");
               }

               var3 = DigestFactory.getDigest(var2.getMGFAlgorithm());
            } else {
               if (!(var2.getMGFParameters() instanceof MGF1ParameterSpec)) {
                  throw new InvalidAlgorithmParameterException("unknown MGF parameters");
               }

               MGF1ParameterSpec var4 = (MGF1ParameterSpec)var2.getMGFParameters();
               if (!DigestFactory.isSameDigest(var4.getDigestAlgorithm(), var2.getDigestAlgorithm())) {
                  throw new InvalidAlgorithmParameterException("digest algorithm for MGF should be the same as for PSS parameters.");
               }

               var3 = DigestFactory.getDigest(var4.getDigestAlgorithm());
            }

            if (var3 == null) {
               throw new InvalidAlgorithmParameterException("no match on MGF algorithm: " + var2.getMGFAlgorithm());
            } else {
               this.engineParams = null;
               this.paramSpec = var2;
               this.mgfDigest = var3;
               this.saltLength = this.paramSpec.getSaltLength();
               this.trailer = this.getTrailer(this.paramSpec.getTrailerField());
               this.setupContentDigest();
               if (this.key != null) {
                  this.pss = new PSSSigner(this.signer, this.contentDigest, var3, this.saltLength, this.trailer);
                  if (this.key.isPrivate()) {
                     this.pss.init(true, this.key);
                  } else {
                     this.pss.init(false, this.key);
                  }
               }
            }
         }
      } else {
         throw new InvalidAlgorithmParameterException("Only PSSParameterSpec supported");
      }
   }

   @Override
   protected AlgorithmParameters engineGetParameters() {
      if (this.engineParams == null && this.paramSpec != null) {
         try {
            this.engineParams = this.helper.createAlgorithmParameters("PSS");
            this.engineParams.init(this.paramSpec);
         } catch (Exception var2) {
            throw new RuntimeException(var2.toString());
         }
      }

      return this.engineParams;
   }

   /** @deprecated */
   @Override
   protected void engineSetParameter(String var1, Object var2) {
      throw new UnsupportedOperationException("engineSetParameter unsupported");
   }

   @Override
   protected Object engineGetParameter(String var1) {
      throw new UnsupportedOperationException("engineGetParameter unsupported");
   }

   private static class NullPssDigest implements Digest {
      private ByteArrayOutputStream bOut = new ByteArrayOutputStream();
      private Digest baseDigest;
      private boolean oddTime = true;

      public NullPssDigest(Digest var1) {
         this.baseDigest = var1;
      }

      @Override
      public String getAlgorithmName() {
         return "NULL";
      }

      @Override
      public int getDigestSize() {
         return this.baseDigest.getDigestSize();
      }

      @Override
      public void update(byte var1) {
         this.bOut.write(var1);
      }

      @Override
      public void update(byte[] var1, int var2, int var3) {
         this.bOut.write(var1, var2, var3);
      }

      @Override
      public int doFinal(byte[] var1, int var2) {
         byte[] var3 = this.bOut.toByteArray();
         if (this.oddTime) {
            System.arraycopy(var3, 0, var1, var2, var3.length);
         } else {
            this.baseDigest.update(var3, 0, var3.length);
            this.baseDigest.doFinal(var1, var2);
         }

         this.reset();
         this.oddTime = !this.oddTime;
         return var3.length;
      }

      @Override
      public void reset() {
         this.bOut.reset();
         this.baseDigest.reset();
      }

      public int getByteLength() {
         return 0;
      }
   }

   public static class PSSwithRSA extends PSSSignatureSpi {
      public PSSwithRSA() {
         super(new RSABlindedEngine(), null);
      }
   }

   public static class SHA1withRSA extends PSSSignatureSpi {
      public SHA1withRSA() {
         super(new RSABlindedEngine(), PSSParameterSpec.DEFAULT);
      }
   }

   public static class SHA1withRSAandSHAKE128 extends PSSSignatureSpi {
      public SHA1withRSAandSHAKE128() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA1", "SHAKE128", null, 20, 1));
      }
   }

   public static class SHA1withRSAandSHAKE256 extends PSSSignatureSpi {
      public SHA1withRSAandSHAKE256() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA1", "SHAKE256", null, 20, 1));
      }
   }

   public static class SHA224withRSA extends PSSSignatureSpi {
      public SHA224withRSA() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-224", "MGF1", new MGF1ParameterSpec("SHA-224"), 28, 1));
      }
   }

   public static class SHA224withRSAandSHAKE128 extends PSSSignatureSpi {
      public SHA224withRSAandSHAKE128() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-224", "SHAKE128", null, 28, 1));
      }
   }

   public static class SHA224withRSAandSHAKE256 extends PSSSignatureSpi {
      public SHA224withRSAandSHAKE256() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-224", "SHAKE256", null, 28, 1));
      }
   }

   public static class SHA256withRSA extends PSSSignatureSpi {
      public SHA256withRSA() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), 32, 1));
      }
   }

   public static class SHA256withRSAandSHAKE128 extends PSSSignatureSpi {
      public SHA256withRSAandSHAKE128() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-256", "SHAKE128", null, 32, 1));
      }
   }

   public static class SHA256withRSAandSHAKE256 extends PSSSignatureSpi {
      public SHA256withRSAandSHAKE256() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-256", "SHAKE256", null, 32, 1));
      }
   }

   public static class SHA384withRSA extends PSSSignatureSpi {
      public SHA384withRSA() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-384", "MGF1", new MGF1ParameterSpec("SHA-384"), 48, 1));
      }
   }

   public static class SHA384withRSAandSHAKE128 extends PSSSignatureSpi {
      public SHA384withRSAandSHAKE128() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-384", "SHAKE128", null, 48, 1));
      }
   }

   public static class SHA384withRSAandSHAKE256 extends PSSSignatureSpi {
      public SHA384withRSAandSHAKE256() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-384", "SHAKE256", null, 48, 1));
      }
   }

   public static class SHA3_224withRSA extends PSSSignatureSpi {
      public SHA3_224withRSA() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-224", "MGF1", new MGF1ParameterSpec("SHA3-224"), 28, 1));
      }
   }

   public static class SHA3_224withRSAandSHAKE128 extends PSSSignatureSpi {
      public SHA3_224withRSAandSHAKE128() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-224", "SHAKE128", null, 28, 1));
      }
   }

   public static class SHA3_224withRSAandSHAKE256 extends PSSSignatureSpi {
      public SHA3_224withRSAandSHAKE256() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-224", "SHAKE256", null, 28, 1));
      }
   }

   public static class SHA3_256withRSA extends PSSSignatureSpi {
      public SHA3_256withRSA() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-256", "MGF1", new MGF1ParameterSpec("SHA3-256"), 32, 1));
      }
   }

   public static class SHA3_256withRSAandSHAKE128 extends PSSSignatureSpi {
      public SHA3_256withRSAandSHAKE128() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-256", "SHAKE128", null, 32, 1));
      }
   }

   public static class SHA3_256withRSAandSHAKE256 extends PSSSignatureSpi {
      public SHA3_256withRSAandSHAKE256() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-256", "SHAKE256", null, 32, 1));
      }
   }

   public static class SHA3_384withRSA extends PSSSignatureSpi {
      public SHA3_384withRSA() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-384", "MGF1", new MGF1ParameterSpec("SHA3-384"), 48, 1));
      }
   }

   public static class SHA3_384withRSAandSHAKE128 extends PSSSignatureSpi {
      public SHA3_384withRSAandSHAKE128() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-384", "SHAKE128", null, 48, 1));
      }
   }

   public static class SHA3_384withRSAandSHAKE256 extends PSSSignatureSpi {
      public SHA3_384withRSAandSHAKE256() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-384", "SHAKE256", null, 48, 1));
      }
   }

   public static class SHA3_512withRSA extends PSSSignatureSpi {
      public SHA3_512withRSA() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-512", "MGF1", new MGF1ParameterSpec("SHA3-512"), 64, 1));
      }
   }

   public static class SHA3_512withRSAandSHAKE128 extends PSSSignatureSpi {
      public SHA3_512withRSAandSHAKE128() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-512", "SHAKE128", null, 64, 1));
      }
   }

   public static class SHA3_512withRSAandSHAKE256 extends PSSSignatureSpi {
      public SHA3_512withRSAandSHAKE256() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-512", "SHAKE256", null, 64, 1));
      }
   }

   public static class SHA512_224withRSA extends PSSSignatureSpi {
      public SHA512_224withRSA() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512(224)", "MGF1", new MGF1ParameterSpec("SHA-512(224)"), 28, 1));
      }
   }

   public static class SHA512_224withRSAandSHAKE128 extends PSSSignatureSpi {
      public SHA512_224withRSAandSHAKE128() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512(224)", "SHAKE128", null, 28, 1));
      }
   }

   public static class SHA512_224withRSAandSHAKE256 extends PSSSignatureSpi {
      public SHA512_224withRSAandSHAKE256() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512(224)", "SHAKE256", null, 28, 1));
      }
   }

   public static class SHA512_256withRSA extends PSSSignatureSpi {
      public SHA512_256withRSA() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512(256)", "MGF1", new MGF1ParameterSpec("SHA-512(256)"), 32, 1));
      }
   }

   public static class SHA512_256withRSAandSHAKE128 extends PSSSignatureSpi {
      public SHA512_256withRSAandSHAKE128() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512(256)", "SHAKE128", null, 32, 1));
      }
   }

   public static class SHA512_256withRSAandSHAKE256 extends PSSSignatureSpi {
      public SHA512_256withRSAandSHAKE256() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512(256)", "SHAKE256", null, 32, 1));
      }
   }

   public static class SHA512withRSA extends PSSSignatureSpi {
      public SHA512withRSA() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512", "MGF1", new MGF1ParameterSpec("SHA-512"), 64, 1));
      }
   }

   public static class SHA512withRSAandSHAKE128 extends PSSSignatureSpi {
      public SHA512withRSAandSHAKE128() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512", "SHAKE128", null, 64, 1));
      }
   }

   public static class SHA512withRSAandSHAKE256 extends PSSSignatureSpi {
      public SHA512withRSAandSHAKE256() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512", "SHAKE256", null, 64, 1));
      }
   }

   public static class SHAKE128WithRSAPSS extends PSSSignatureSpi {
      public SHAKE128WithRSAPSS() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHAKE128", "SHAKE128", null, 32, 1));
      }
   }

   public static class SHAKE256WithRSAPSS extends PSSSignatureSpi {
      public SHAKE256WithRSAPSS() {
         super(new RSABlindedEngine(), new PSSParameterSpec("SHAKE256", "SHAKE256", null, 64, 1));
      }
   }

   public static class nonePSS extends PSSSignatureSpi {
      public nonePSS() {
         super(new RSABlindedEngine(), null, true);
      }
   }
}
