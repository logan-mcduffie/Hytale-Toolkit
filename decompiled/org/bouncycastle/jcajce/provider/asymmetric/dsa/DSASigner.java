package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSAEncoding;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.crypto.signers.StandardDSAEncoding;
import org.bouncycastle.crypto.util.DigestFactory;

public class DSASigner extends SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
   private Digest digest;
   private DSAExt signer;
   private DSAEncoding encoding = StandardDSAEncoding.INSTANCE;
   private SecureRandom random;

   protected DSASigner(Digest var1, DSAExt var2) {
      this.digest = var1;
      this.signer = var2;
   }

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      AsymmetricKeyParameter var2 = DSAUtil.generatePublicKeyParameter(var1);
      this.digest.reset();
      this.signer.init(false, var2);
   }

   @Override
   protected void engineInitSign(PrivateKey var1, SecureRandom var2) throws InvalidKeyException {
      this.random = var2;
      this.engineInitSign(var1);
   }

   @Override
   protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      Object var2 = DSAUtil.generatePrivateKeyParameter(var1);
      if (this.random != null) {
         var2 = new ParametersWithRandom((CipherParameters)var2, this.random);
      }

      this.digest.reset();
      this.signer.init(true, (CipherParameters)var2);
   }

   @Override
   protected void engineUpdate(byte var1) throws SignatureException {
      this.digest.update(var1);
   }

   @Override
   protected void engineUpdate(byte[] var1, int var2, int var3) throws SignatureException {
      this.digest.update(var1, var2, var3);
   }

   @Override
   protected byte[] engineSign() throws SignatureException {
      byte[] var1 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var1, 0);

      try {
         BigInteger[] var2 = this.signer.generateSignature(var1);
         return this.encoding.encode(this.signer.getOrder(), var2[0], var2[1]);
      } catch (Exception var3) {
         throw new SignatureException(var3.toString());
      }
   }

   @Override
   protected boolean engineVerify(byte[] var1) throws SignatureException {
      byte[] var2 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var2, 0);

      BigInteger[] var3;
      try {
         var3 = this.encoding.decode(this.signer.getOrder(), var1);
      } catch (Exception var5) {
         throw new SignatureException("error decoding signature bytes.");
      }

      return this.signer.verifySignature(var2, var3[0], var3[1]);
   }

   @Override
   protected AlgorithmParameters engineGetParameters() {
      return null;
   }

   @Override
   protected void engineSetParameter(AlgorithmParameterSpec var1) {
      throw new UnsupportedOperationException("engineSetParameter unsupported");
   }

   /** @deprecated */
   @Override
   protected void engineSetParameter(String var1, Object var2) {
      throw new UnsupportedOperationException("engineSetParameter unsupported");
   }

   /** @deprecated */
   @Override
   protected Object engineGetParameter(String var1) {
      throw new UnsupportedOperationException("engineGetParameter unsupported");
   }

   public static class detDSA extends DSASigner {
      public detDSA() {
         super(DigestFactory.createSHA1(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA1())));
      }
   }

   public static class detDSA224 extends DSASigner {
      public detDSA224() {
         super(DigestFactory.createSHA224(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA224())));
      }
   }

   public static class detDSA256 extends DSASigner {
      public detDSA256() {
         super(DigestFactory.createSHA256(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA256())));
      }
   }

   public static class detDSA384 extends DSASigner {
      public detDSA384() {
         super(DigestFactory.createSHA384(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA384())));
      }
   }

   public static class detDSA512 extends DSASigner {
      public detDSA512() {
         super(DigestFactory.createSHA512(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA512())));
      }
   }

   public static class detDSASha3_224 extends DSASigner {
      public detDSASha3_224() {
         super(DigestFactory.createSHA3_224(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_224())));
      }
   }

   public static class detDSASha3_256 extends DSASigner {
      public detDSASha3_256() {
         super(DigestFactory.createSHA3_256(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_256())));
      }
   }

   public static class detDSASha3_384 extends DSASigner {
      public detDSASha3_384() {
         super(DigestFactory.createSHA3_384(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_384())));
      }
   }

   public static class detDSASha3_512 extends DSASigner {
      public detDSASha3_512() {
         super(DigestFactory.createSHA3_512(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_512())));
      }
   }

   public static class dsa224 extends DSASigner {
      public dsa224() {
         super(DigestFactory.createSHA224(), new org.bouncycastle.crypto.signers.DSASigner());
      }
   }

   public static class dsa256 extends DSASigner {
      public dsa256() {
         super(DigestFactory.createSHA256(), new org.bouncycastle.crypto.signers.DSASigner());
      }
   }

   public static class dsa384 extends DSASigner {
      public dsa384() {
         super(DigestFactory.createSHA384(), new org.bouncycastle.crypto.signers.DSASigner());
      }
   }

   public static class dsa512 extends DSASigner {
      public dsa512() {
         super(DigestFactory.createSHA512(), new org.bouncycastle.crypto.signers.DSASigner());
      }
   }

   public static class dsaRMD160 extends DSASigner {
      public dsaRMD160() {
         super(new RIPEMD160Digest(), new org.bouncycastle.crypto.signers.DSASigner());
      }
   }

   public static class dsaSha3_224 extends DSASigner {
      public dsaSha3_224() {
         super(DigestFactory.createSHA3_224(), new org.bouncycastle.crypto.signers.DSASigner());
      }
   }

   public static class dsaSha3_256 extends DSASigner {
      public dsaSha3_256() {
         super(DigestFactory.createSHA3_256(), new org.bouncycastle.crypto.signers.DSASigner());
      }
   }

   public static class dsaSha3_384 extends DSASigner {
      public dsaSha3_384() {
         super(DigestFactory.createSHA3_384(), new org.bouncycastle.crypto.signers.DSASigner());
      }
   }

   public static class dsaSha3_512 extends DSASigner {
      public dsaSha3_512() {
         super(DigestFactory.createSHA3_512(), new org.bouncycastle.crypto.signers.DSASigner());
      }
   }

   public static class noneDSA extends DSASigner {
      public noneDSA() {
         super(new NullDigest(), new org.bouncycastle.crypto.signers.DSASigner());
      }
   }

   public static class stdDSA extends DSASigner {
      public stdDSA() {
         super(DigestFactory.createSHA1(), new org.bouncycastle.crypto.signers.DSASigner());
      }
   }
}
