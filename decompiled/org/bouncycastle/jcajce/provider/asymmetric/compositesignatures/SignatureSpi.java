package org.bouncycastle.jcajce.provider.asymmetric.compositesignatures;

import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.internal.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.jcajce.CompositePrivateKey;
import org.bouncycastle.jcajce.CompositePublicKey;
import org.bouncycastle.jcajce.interfaces.BCKey;
import org.bouncycastle.jcajce.spec.CompositeSignatureSpec;
import org.bouncycastle.jcajce.spec.ContextParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;
import org.bouncycastle.util.encoders.Hex;

public class SignatureSpi extends java.security.SignatureSpi {
   private static final byte[] prefix = Hex.decode("436f6d706f73697465416c676f726974686d5369676e61747572657332303235");
   private static final Map<String, String> canonicalNames = new HashMap<>();
   private static final HashMap<ASN1ObjectIdentifier, byte[]> domainSeparators = new LinkedHashMap<>();
   private static final HashMap<ASN1ObjectIdentifier, AlgorithmParameterSpec> algorithmsParameterSpecs = new HashMap<>();
   private static final String ML_DSA_44 = "ML-DSA-44";
   private static final String ML_DSA_65 = "ML-DSA-65";
   private static final String ML_DSA_87 = "ML-DSA-87";
   private final SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   private Key compositeKey;
   private final boolean isPrehash;
   private ASN1ObjectIdentifier algorithm;
   private String[] algs;
   private Signature[] componentSignatures;
   private byte[] domain;
   private Digest baseDigest;
   private JcaJceHelper helper = new BCJcaJceHelper();
   private Digest preHashDigest;
   private ContextParameterSpec contextSpec;
   private AlgorithmParameters engineParams = null;
   private boolean unprimed = true;

   SignatureSpi(ASN1ObjectIdentifier var1, Digest var2) {
      this(var1, var2, false);
   }

   SignatureSpi(ASN1ObjectIdentifier var1, Digest var2, boolean var3) {
      this.algorithm = var1;
      this.isPrehash = var3;
      if (var1 != null) {
         this.baseDigest = var2;
         this.preHashDigest = (Digest)(var3 ? new SignatureSpi.NullDigest(var2.getDigestSize()) : var2);
         this.domain = domainSeparators.get(var1);
         this.algs = CompositeIndex.getPairing(var1);
         this.componentSignatures = new Signature[this.algs.length];
      }
   }

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      if (!(var1 instanceof CompositePublicKey)) {
         throw new InvalidKeyException("public key is not composite");
      } else {
         this.compositeKey = var1;
         CompositePublicKey var2 = (CompositePublicKey)this.compositeKey;
         if (this.algorithm != null) {
            if (!var2.getAlgorithmIdentifier().getAlgorithm().equals(this.algorithm)) {
               throw new InvalidKeyException("provided composite public key cannot be used with the composite signature algorithm");
            }
         } else {
            ASN1ObjectIdentifier var3 = SubjectPublicKeyInfo.getInstance(var1.getEncoded()).getAlgorithm().getAlgorithm();
            this.algorithm = var3;
            this.baseDigest = CompositeIndex.getDigest(var3);
            this.preHashDigest = (Digest)(this.isPrehash ? new SignatureSpi.NullDigest(this.baseDigest.getDigestSize()) : this.baseDigest);
            this.domain = domainSeparators.get(var3);
            this.algs = CompositeIndex.getPairing(var3);
            this.componentSignatures = new Signature[this.algs.length];
         }

         this.createComponentSignatures(var2.getPublicKeys(), var2.getProviders());
         this.sigInitVerify();
      }
   }

   private void sigInitVerify() throws InvalidKeyException {
      CompositePublicKey var1 = (CompositePublicKey)this.compositeKey;

      for (int var2 = 0; var2 < this.componentSignatures.length; var2++) {
         this.componentSignatures[var2].initVerify(var1.getPublicKeys().get(var2));
      }

      this.unprimed = true;
   }

   @Override
   protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      if (!(var1 instanceof CompositePrivateKey)) {
         throw new InvalidKeyException("Private key is not composite.");
      } else {
         this.compositeKey = var1;
         CompositePrivateKey var2 = (CompositePrivateKey)var1;
         if (this.algorithm != null) {
            if (!var2.getAlgorithmIdentifier().getAlgorithm().equals(this.algorithm)) {
               throw new InvalidKeyException("provided composite public key cannot be used with the composite signature algorithm");
            }
         } else {
            ASN1ObjectIdentifier var3 = var2.getAlgorithmIdentifier().getAlgorithm();
            this.algorithm = var3;
            this.baseDigest = CompositeIndex.getDigest(var3);
            this.preHashDigest = (Digest)(this.isPrehash ? new SignatureSpi.NullDigest(this.baseDigest.getDigestSize()) : this.baseDigest);
            this.domain = domainSeparators.get(var3);
            this.algs = CompositeIndex.getPairing(var3);
            this.componentSignatures = new Signature[this.algs.length];
         }

         this.createComponentSignatures(var2.getPrivateKeys(), var2.getProviders());
         this.sigInitSign();
      }
   }

   private void createComponentSignatures(List var1, List<Provider> var2) {
      try {
         if (var2 == null) {
            for (int var3 = 0; var3 != this.componentSignatures.length; var3++) {
               this.componentSignatures[var3] = this.getDefaultSignature(this.algs[var3], var1.get(var3));
            }
         } else {
            for (int var6 = 0; var6 != this.componentSignatures.length; var6++) {
               Provider var4 = (Provider)var2.get(var6);
               if (var4 == null) {
                  this.componentSignatures[var6] = this.getDefaultSignature(this.algs[var6], var1.get(var6));
               } else {
                  this.componentSignatures[var6] = Signature.getInstance(this.algs[var6], (Provider)var2.get(var6));
               }
            }
         }
      } catch (GeneralSecurityException var5) {
         throw Exceptions.illegalStateException(var5.getMessage(), var5);
      }
   }

   private Signature getDefaultSignature(String var1, Object var2) throws NoSuchAlgorithmException, NoSuchProviderException {
      return var2 instanceof BCKey ? this.helper.createSignature(var1) : Signature.getInstance(var1);
   }

   private void sigInitSign() throws InvalidKeyException {
      CompositePrivateKey var1 = (CompositePrivateKey)this.compositeKey;

      for (int var2 = 0; var2 < this.componentSignatures.length; var2++) {
         this.componentSignatures[var2].initSign(var1.getPrivateKeys().get(var2));
      }

      this.unprimed = true;
   }

   private void baseSigInit() throws SignatureException {
      try {
         this.componentSignatures[0].setParameter(new ContextParameterSpec(this.domain));
         AlgorithmParameterSpec var1 = algorithmsParameterSpecs.get(this.algorithm);
         if (var1 != null) {
            this.componentSignatures[1].setParameter(var1);
         }
      } catch (InvalidAlgorithmParameterException var2) {
         throw new IllegalStateException("unable to set context on ML-DSA");
      }

      this.unprimed = false;
   }

   @Override
   protected void engineUpdate(byte var1) throws SignatureException {
      if (this.unprimed) {
         this.baseSigInit();
      }

      if (this.preHashDigest != null) {
         this.preHashDigest.update(var1);
      } else {
         for (int var2 = 0; var2 < this.componentSignatures.length; var2++) {
            Signature var3 = this.componentSignatures[var2];
            var3.update(var1);
         }
      }
   }

   @Override
   protected void engineUpdate(byte[] var1, int var2, int var3) throws SignatureException {
      if (this.unprimed) {
         this.baseSigInit();
      }

      if (this.preHashDigest != null) {
         this.preHashDigest.update(var1, var2, var3);
      } else {
         for (int var4 = 0; var4 < this.componentSignatures.length; var4++) {
            Signature var5 = this.componentSignatures[var4];
            var5.update(var1, var2, var3);
         }
      }
   }

   @Override
   protected byte[] engineSign() throws SignatureException {
      byte[] var1 = new byte[32];
      this.random.nextBytes(var1);
      if (this.preHashDigest != null) {
         this.processPreHashedMessage(null);
      }

      byte[] var2 = this.componentSignatures[0].sign();
      byte[] var3 = this.componentSignatures[1].sign();
      byte[] var4 = new byte[var2.length + var3.length];
      System.arraycopy(var2, 0, var4, 0, var2.length);
      System.arraycopy(var3, 0, var4, var2.length, var3.length);
      return var4;
   }

   private void processPreHashedMessage(byte[] var1) throws SignatureException {
      byte[] var2 = new byte[this.baseDigest.getDigestSize()];

      try {
         this.preHashDigest.doFinal(var2, 0);
      } catch (IllegalStateException var6) {
         throw new SignatureException(var6.getMessage());
      }

      for (int var3 = 0; var3 < this.componentSignatures.length; var3++) {
         Signature var4 = this.componentSignatures[var3];
         var4.update(prefix);
         var4.update(this.domain);
         if (this.contextSpec == null) {
            var4.update((byte)0);
         } else {
            byte[] var5 = this.contextSpec.getContext();
            var4.update((byte)var5.length);
            var4.update(var5);
         }

         if (var1 != null) {
            var4.update(var1, 0, var1.length);
         }

         var4.update(var2, 0, var2.length);
      }
   }

   public static byte[][] splitCompositeSignature(byte[] var0, int var1) {
      byte[] var2 = new byte[var1];
      byte[] var3 = new byte[var0.length - var1];
      System.arraycopy(var0, 0, var2, 0, var1);
      System.arraycopy(var0, var1, var3, 0, var3.length);
      return new byte[][]{var2, var3};
   }

   @Override
   protected boolean engineVerify(byte[] var1) throws SignatureException {
      short var2 = 0;
      if (this.algs[0].indexOf("44") > 0) {
         var2 = 2420;
      } else if (this.algs[0].indexOf("65") > 0) {
         var2 = 3309;
      } else if (this.algs[0].indexOf("87") > 0) {
         var2 = 4627;
      }

      byte[][] var3 = splitCompositeSignature(var1, var2);
      if (this.preHashDigest != null) {
         this.processPreHashedMessage(null);
      }

      boolean var4 = false;

      for (int var5 = 0; var5 < this.componentSignatures.length; var5++) {
         if (!this.componentSignatures[var5].verify(var3[var5])) {
            var4 = true;
         }
      }

      return !var4;
   }

   @Override
   protected void engineSetParameter(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
      if (!this.unprimed) {
         throw new InvalidAlgorithmParameterException("attempt to set parameter after update");
      } else {
         if (var1 instanceof ContextParameterSpec) {
            this.contextSpec = (ContextParameterSpec)var1;

            try {
               if (this.compositeKey instanceof PublicKey) {
                  this.sigInitVerify();
               } else {
                  this.sigInitSign();
               }
            } catch (InvalidKeyException var5) {
               throw new InvalidAlgorithmParameterException("keys invalid on reset: " + var5.getMessage(), var5);
            }
         } else {
            if (!(var1 instanceof CompositeSignatureSpec)) {
               byte[] var7 = SpecUtil.getContextFrom(var1);
               if (var7 != null) {
                  this.contextSpec = new ContextParameterSpec(var7);

                  try {
                     if (this.compositeKey instanceof PublicKey) {
                        this.sigInitVerify();
                     } else {
                        this.sigInitSign();
                     }
                  } catch (InvalidKeyException var6) {
                     throw new InvalidAlgorithmParameterException("keys invalid on reset: " + var6.getMessage(), var6);
                  }
               }

               throw new InvalidAlgorithmParameterException("unknown parameterSpec passed to composite signature");
            }

            CompositeSignatureSpec var2 = (CompositeSignatureSpec)var1;
            if (var2.isPrehashMode()) {
               this.preHashDigest = new SignatureSpi.NullDigest(this.baseDigest.getDigestSize());
            } else {
               this.preHashDigest = this.baseDigest;
            }

            AlgorithmParameterSpec var3 = var2.getSecondarySpec();
            if (var3 != null && !(var3 instanceof ContextParameterSpec)) {
               byte[] var4 = SpecUtil.getContextFrom(var3);
               if (var4 == null) {
                  throw new InvalidAlgorithmParameterException("unknown parameterSpec passed to composite signature");
               }

               this.contextSpec = new ContextParameterSpec(var4);
            } else {
               this.contextSpec = (ContextParameterSpec)var2.getSecondarySpec();
            }
         }
      }
   }

   private String getCanonicalName(String var1) {
      String var2 = canonicalNames.get(var1);
      return var2 != null ? var2 : var1;
   }

   @Override
   protected void engineSetParameter(String var1, Object var2) throws InvalidParameterException {
      throw new UnsupportedOperationException("engineSetParameter unsupported");
   }

   @Override
   protected Object engineGetParameter(String var1) throws InvalidParameterException {
      throw new UnsupportedOperationException("engineGetParameter unsupported");
   }

   @Override
   protected final AlgorithmParameters engineGetParameters() {
      if (this.engineParams == null && this.contextSpec != null) {
         try {
            this.engineParams = this.helper.createAlgorithmParameters("CONTEXT");
            this.engineParams.init(this.contextSpec);
         } catch (Exception var2) {
            throw Exceptions.illegalStateException(var2.toString(), var2);
         }
      }

      return this.engineParams;
   }

   static {
      canonicalNames.put("MLDSA44", "ML-DSA-44");
      canonicalNames.put("MLDSA65", "ML-DSA-65");
      canonicalNames.put("MLDSA87", "ML-DSA-87");
      canonicalNames.put(NISTObjectIdentifiers.id_ml_dsa_44.getId(), "ML-DSA-44");
      canonicalNames.put(NISTObjectIdentifiers.id_ml_dsa_65.getId(), "ML-DSA-65");
      canonicalNames.put(NISTObjectIdentifiers.id_ml_dsa_87.getId(), "ML-DSA-87");
      domainSeparators.put(
         IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256, Hex.decode("434f4d505349472d4d4c44534134342d525341323034382d5053532d534841323536")
      );
      domainSeparators.put(
         IANAObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256, Hex.decode("434f4d505349472d4d4c44534134342d525341323034382d504b435331352d534841323536")
      );
      domainSeparators.put(IANAObjectIdentifiers.id_MLDSA44_Ed25519_SHA512, Hex.decode("434f4d505349472d4d4c44534134342d456432353531392d534841353132"));
      domainSeparators.put(IANAObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256, Hex.decode("434f4d505349472d4d4c44534134342d45434453412d503235362d534841323536"));
      domainSeparators.put(
         IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512, Hex.decode("434f4d505349472d4d4c44534136352d525341333037322d5053532d534841353132")
      );
      domainSeparators.put(
         IANAObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA512, Hex.decode("434f4d505349472d4d4c44534136352d525341333037322d504b435331352d534841353132")
      );
      domainSeparators.put(
         IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512, Hex.decode("434f4d505349472d4d4c44534136352d525341343039362d5053532d534841353132")
      );
      domainSeparators.put(
         IANAObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA512, Hex.decode("434f4d505349472d4d4c44534136352d525341343039362d504b435331352d534841353132")
      );
      domainSeparators.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P256_SHA512, Hex.decode("434f4d505349472d4d4c44534136352d45434453412d503235362d534841353132"));
      domainSeparators.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA512, Hex.decode("434f4d505349472d4d4c44534136352d45434453412d503338342d534841353132"));
      domainSeparators.put(
         IANAObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA512, Hex.decode("434f4d505349472d4d4c44534136352d45434453412d42503235362d534841353132")
      );
      domainSeparators.put(IANAObjectIdentifiers.id_MLDSA65_Ed25519_SHA512, Hex.decode("434f4d505349472d4d4c44534136352d456432353531392d534841353132"));
      domainSeparators.put(
         IANAObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA512, Hex.decode("434f4d505349472d4d4c44534138372d45434453412d42503338342d534841353132")
      );
      domainSeparators.put(IANAObjectIdentifiers.id_MLDSA87_Ed448_SHAKE256, Hex.decode("434f4d505349472d4d4c44534138372d45643434382d5348414b45323536"));
      domainSeparators.put(
         IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512, Hex.decode("434f4d505349472d4d4c44534138372d525341333037322d5053532d534841353132")
      );
      domainSeparators.put(
         IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512, Hex.decode("434f4d505349472d4d4c44534138372d525341343039362d5053532d534841353132")
      );
      domainSeparators.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA512, Hex.decode("434f4d505349472d4d4c44534138372d45434453412d503338342d534841353132"));
      domainSeparators.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P521_SHA512, Hex.decode("434f4d505349472d4d4c44534138372d45434453412d503532312d534841353132"));
      algorithmsParameterSpecs.put(
         IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256, new PSSParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), 32, 1)
      );
      algorithmsParameterSpecs.put(
         IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512, new PSSParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), 32, 1)
      );
      algorithmsParameterSpecs.put(
         IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512, new PSSParameterSpec("SHA-384", "MGF1", new MGF1ParameterSpec("SHA-384"), 48, 1)
      );
      algorithmsParameterSpecs.put(
         IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512, new PSSParameterSpec("SHA-384", "MGF1", new MGF1ParameterSpec("SHA-384"), 48, 1)
      );
      algorithmsParameterSpecs.put(
         IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512, new PSSParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), 32, 1)
      );
   }

   public static final class COMPOSITE extends SignatureSpi {
      public COMPOSITE() {
         super(null, null, false);
      }
   }

   private static final class ErasableOutputStream extends ByteArrayOutputStream {
      public ErasableOutputStream() {
      }

      public byte[] getBuf() {
         return this.buf;
      }
   }

   public static final class MLDSA44_ECDSA_P256_SHA256 extends SignatureSpi {
      public MLDSA44_ECDSA_P256_SHA256() {
         super(IANAObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256, new SHA256Digest());
      }
   }

   public static final class MLDSA44_ECDSA_P256_SHA256_PREHASH extends SignatureSpi {
      public MLDSA44_ECDSA_P256_SHA256_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256, new SHA256Digest(), true);
      }
   }

   public static final class MLDSA44_Ed25519_SHA512 extends SignatureSpi {
      public MLDSA44_Ed25519_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA44_Ed25519_SHA512, new SHA512Digest());
      }
   }

   public static final class MLDSA44_Ed25519_SHA512_PREHASH extends SignatureSpi {
      public MLDSA44_Ed25519_SHA512_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA44_Ed25519_SHA512, new SHA512Digest(), true);
      }
   }

   public static final class MLDSA44_RSA2048_PKCS15_SHA256 extends SignatureSpi {
      public MLDSA44_RSA2048_PKCS15_SHA256() {
         super(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256, new SHA256Digest());
      }
   }

   public static final class MLDSA44_RSA2048_PKCS15_SHA256_PREHASH extends SignatureSpi {
      public MLDSA44_RSA2048_PKCS15_SHA256_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256, new SHA256Digest(), true);
      }
   }

   public static final class MLDSA44_RSA2048_PSS_SHA256 extends SignatureSpi {
      public MLDSA44_RSA2048_PSS_SHA256() {
         super(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256, new SHA256Digest());
      }
   }

   public static final class MLDSA44_RSA2048_PSS_SHA256_PREHASH extends SignatureSpi {
      public MLDSA44_RSA2048_PSS_SHA256_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256, new SHA256Digest(), true);
      }
   }

   public static final class MLDSA65_ECDSA_P256_SHA512 extends SignatureSpi {
      public MLDSA65_ECDSA_P256_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P256_SHA512, new SHA512Digest());
      }
   }

   public static final class MLDSA65_ECDSA_P256_SHA512_PREHASH extends SignatureSpi {
      public MLDSA65_ECDSA_P256_SHA512_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P256_SHA512, new SHA512Digest(), true);
      }
   }

   public static final class MLDSA65_ECDSA_P384_SHA512 extends SignatureSpi {
      public MLDSA65_ECDSA_P384_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA512, new SHA512Digest());
      }
   }

   public static final class MLDSA65_ECDSA_P384_SHA512_PREHASH extends SignatureSpi {
      public MLDSA65_ECDSA_P384_SHA512_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA512, new SHA512Digest(), true);
      }
   }

   public static final class MLDSA65_ECDSA_brainpoolP256r1_SHA512 extends SignatureSpi {
      public MLDSA65_ECDSA_brainpoolP256r1_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA512, new SHA512Digest());
      }
   }

   public static final class MLDSA65_ECDSA_brainpoolP256r1_SHA512_PREHASH extends SignatureSpi {
      public MLDSA65_ECDSA_brainpoolP256r1_SHA512_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA512, new SHA512Digest(), true);
      }
   }

   public static final class MLDSA65_Ed25519_SHA512 extends SignatureSpi {
      public MLDSA65_Ed25519_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA65_Ed25519_SHA512, new SHA512Digest());
      }
   }

   public static final class MLDSA65_Ed25519_SHA512_PREHASH extends SignatureSpi {
      public MLDSA65_Ed25519_SHA512_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA65_Ed25519_SHA512, new SHA512Digest(), true);
      }
   }

   public static final class MLDSA65_RSA3072_PKCS15_SHA512 extends SignatureSpi {
      public MLDSA65_RSA3072_PKCS15_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA512, new SHA512Digest());
      }
   }

   public static final class MLDSA65_RSA3072_PKCS15_SHA512_PREHASH extends SignatureSpi {
      public MLDSA65_RSA3072_PKCS15_SHA512_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA512, new SHA512Digest(), true);
      }
   }

   public static final class MLDSA65_RSA3072_PSS_SHA512 extends SignatureSpi {
      public MLDSA65_RSA3072_PSS_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512, new SHA512Digest());
      }
   }

   public static final class MLDSA65_RSA3072_PSS_SHA512_PREHASH extends SignatureSpi {
      public MLDSA65_RSA3072_PSS_SHA512_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512, new SHA512Digest(), true);
      }
   }

   public static final class MLDSA65_RSA4096_PKCS15_SHA512 extends SignatureSpi {
      public MLDSA65_RSA4096_PKCS15_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA512, new SHA512Digest());
      }
   }

   public static final class MLDSA65_RSA4096_PKCS15_SHA512_PREHASH extends SignatureSpi {
      public MLDSA65_RSA4096_PKCS15_SHA512_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA512, new SHA512Digest(), true);
      }
   }

   public static final class MLDSA65_RSA4096_PSS_SHA512 extends SignatureSpi {
      public MLDSA65_RSA4096_PSS_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512, new SHA512Digest());
      }
   }

   public static final class MLDSA65_RSA4096_PSS_SHA512_PREHASH extends SignatureSpi {
      public MLDSA65_RSA4096_PSS_SHA512_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512, new SHA512Digest(), true);
      }
   }

   public static final class MLDSA87_ECDSA_P384_SHA512 extends SignatureSpi {
      public MLDSA87_ECDSA_P384_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA512, new SHA512Digest());
      }
   }

   public static final class MLDSA87_ECDSA_P384_SHA512_PREHASH extends SignatureSpi {
      public MLDSA87_ECDSA_P384_SHA512_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA512, new SHA512Digest(), true);
      }
   }

   public static final class MLDSA87_ECDSA_P521_SHA512 extends SignatureSpi {
      public MLDSA87_ECDSA_P521_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P521_SHA512, new SHA512Digest());
      }
   }

   public static final class MLDSA87_ECDSA_P521_SHA512_PREHASH extends SignatureSpi {
      public MLDSA87_ECDSA_P521_SHA512_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P521_SHA512, new SHA512Digest(), true);
      }
   }

   public static final class MLDSA87_ECDSA_brainpoolP384r1_SHA512 extends SignatureSpi {
      public MLDSA87_ECDSA_brainpoolP384r1_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA512, new SHA512Digest());
      }
   }

   public static final class MLDSA87_ECDSA_brainpoolP384r1_SHA512_PREHASH extends SignatureSpi {
      public MLDSA87_ECDSA_brainpoolP384r1_SHA512_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA512, new SHA512Digest(), true);
      }
   }

   public static final class MLDSA87_Ed448_SHAKE256 extends SignatureSpi {
      public MLDSA87_Ed448_SHAKE256() {
         super(IANAObjectIdentifiers.id_MLDSA87_Ed448_SHAKE256, new SHAKEDigest(256));
      }
   }

   public static final class MLDSA87_Ed448_SHAKE256_PREHASH extends SignatureSpi {
      public MLDSA87_Ed448_SHAKE256_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA87_Ed448_SHAKE256, new SHAKEDigest(256), true);
      }
   }

   public static final class MLDSA87_RSA3072_PSS_SHA512 extends SignatureSpi {
      public MLDSA87_RSA3072_PSS_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512, new SHA512Digest());
      }
   }

   public static final class MLDSA87_RSA3072_PSS_SHA512_PREHASH extends SignatureSpi {
      public MLDSA87_RSA3072_PSS_SHA512_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512, new SHA512Digest(), true);
      }
   }

   public static final class MLDSA87_RSA4096_PSS_SHA512 extends SignatureSpi {
      public MLDSA87_RSA4096_PSS_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512, new SHA512Digest());
      }
   }

   public static final class MLDSA87_RSA4096_PSS_SHA512_PREHASH extends SignatureSpi {
      public MLDSA87_RSA4096_PSS_SHA512_PREHASH() {
         super(IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512, new SHA512Digest(), true);
      }
   }

   private static class NullDigest implements Digest {
      private final int expectedSize;
      private final SignatureSpi.NullDigest.OpenByteArrayOutputStream bOut = new SignatureSpi.NullDigest.OpenByteArrayOutputStream();

      NullDigest(int var1) {
         this.expectedSize = var1;
      }

      @Override
      public String getAlgorithmName() {
         return "NULL";
      }

      @Override
      public int getDigestSize() {
         return this.bOut.size();
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
         int var3 = this.bOut.size();
         if (var3 != this.expectedSize) {
            throw new IllegalStateException("provided pre-hash digest is the wrong length");
         } else {
            this.bOut.copy(var1, var2);
            this.reset();
            return var3;
         }
      }

      @Override
      public void reset() {
         this.bOut.reset();
      }

      private static class OpenByteArrayOutputStream extends ByteArrayOutputStream {
         private OpenByteArrayOutputStream() {
         }

         @Override
         public void reset() {
            super.reset();
            Arrays.clear(this.buf);
         }

         void copy(byte[] var1, int var2) {
            System.arraycopy(this.buf, 0, var1, var2, this.size());
         }
      }
   }
}
