package org.bouncycastle.pqc.jcajce.provider.xmss;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTSigner;
import org.bouncycastle.pqc.jcajce.interfaces.StateAwareSignature;

public class XMSSMTSignatureSpi extends Signature implements StateAwareSignature {
   private Digest digest;
   private XMSSMTSigner signer;
   private ASN1ObjectIdentifier treeDigest;
   private SecureRandom random;

   protected XMSSMTSignatureSpi(String var1) {
      super(var1);
   }

   protected XMSSMTSignatureSpi(String var1, Digest var2, XMSSMTSigner var3) {
      super(var1);
      this.digest = var2;
      this.signer = var3;
   }

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      if (var1 instanceof BCXMSSMTPublicKey) {
         CipherParameters var2 = ((BCXMSSMTPublicKey)var1).getKeyParams();
         this.treeDigest = null;
         this.digest.reset();
         this.signer.init(false, var2);
      } else {
         throw new InvalidKeyException("unknown public key passed to XMSSMT");
      }
   }

   @Override
   protected void engineInitSign(PrivateKey var1, SecureRandom var2) throws InvalidKeyException {
      this.random = var2;
      this.engineInitSign(var1);
   }

   @Override
   protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      if (var1 instanceof BCXMSSMTPrivateKey) {
         Object var2 = ((BCXMSSMTPrivateKey)var1).getKeyParams();
         this.treeDigest = ((BCXMSSMTPrivateKey)var1).getTreeDigestOID();
         if (this.random != null) {
            var2 = new ParametersWithRandom((CipherParameters)var2, this.random);
         }

         this.digest.reset();
         this.signer.init(true, (CipherParameters)var2);
      } else {
         throw new InvalidKeyException("unknown private key passed to XMSSMT");
      }
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
      byte[] var1 = DigestUtil.getDigestResult(this.digest);

      try {
         return this.signer.generateSignature(var1);
      } catch (Exception var3) {
         if (var3 instanceof IllegalStateException) {
            throw new SignatureException(var3.getMessage(), var3);
         } else {
            throw new SignatureException(var3.toString());
         }
      }
   }

   @Override
   protected boolean engineVerify(byte[] var1) throws SignatureException {
      byte[] var2 = DigestUtil.getDigestResult(this.digest);
      return this.signer.verifySignature(var2, var1);
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
      throw new UnsupportedOperationException("engineSetParameter unsupported");
   }

   @Override
   public boolean isSigningCapable() {
      return this.treeDigest != null && this.signer.getUsagesRemaining() != 0L;
   }

   @Override
   public PrivateKey getUpdatedPrivateKey() {
      if (this.treeDigest == null) {
         throw new IllegalStateException("signature object not in a signing state");
      } else {
         BCXMSSMTPrivateKey var1 = new BCXMSSMTPrivateKey(this.treeDigest, (XMSSMTPrivateKeyParameters)this.signer.getUpdatedPrivateKey());
         this.treeDigest = null;
         return var1;
      }
   }

   public static class generic extends XMSSMTSignatureSpi {
      public generic() {
         super("XMSSMT", new NullDigest(), new XMSSMTSigner());
      }
   }

   public static class withSha256 extends XMSSMTSignatureSpi {
      public withSha256() {
         super("XMSSMT-SHA256", new NullDigest(), new XMSSMTSigner());
      }
   }

   public static class withSha256andPrehash extends XMSSMTSignatureSpi {
      public withSha256andPrehash() {
         super("SHA256withXMSSMT-SHA256", new SHA256Digest(), new XMSSMTSigner());
      }
   }

   public static class withSha512 extends XMSSMTSignatureSpi {
      public withSha512() {
         super("XMSSMT-SHA512", new NullDigest(), new XMSSMTSigner());
      }
   }

   public static class withSha512andPrehash extends XMSSMTSignatureSpi {
      public withSha512andPrehash() {
         super("SHA512withXMSSMT-SHA512", new SHA512Digest(), new XMSSMTSigner());
      }
   }

   public static class withShake128 extends XMSSMTSignatureSpi {
      public withShake128() {
         super("XMSSMT-SHAKE128", new NullDigest(), new XMSSMTSigner());
      }
   }

   public static class withShake128_512andPrehash extends XMSSMTSignatureSpi {
      public withShake128_512andPrehash() {
         super("SHAKE128(512)withXMSSMT-SHAKE128", new DigestUtil.DoubleDigest(new SHAKEDigest(128)), new XMSSMTSigner());
      }
   }

   public static class withShake128andPrehash extends XMSSMTSignatureSpi {
      public withShake128andPrehash() {
         super("SHAKE128withXMSSMT-SHAKE128", new SHAKEDigest(128), new XMSSMTSigner());
      }
   }

   public static class withShake256 extends XMSSMTSignatureSpi {
      public withShake256() {
         super("XMSSMT-SHAKE256", new NullDigest(), new XMSSMTSigner());
      }
   }

   public static class withShake256_1024andPrehash extends XMSSMTSignatureSpi {
      public withShake256_1024andPrehash() {
         super("SHAKE256(1024)withXMSSMT-SHAKE256", new DigestUtil.DoubleDigest(new SHAKEDigest(256)), new XMSSMTSigner());
      }
   }

   public static class withShake256andPrehash extends XMSSMTSignatureSpi {
      public withShake256andPrehash() {
         super("SHAKE256withXMSSMT-SHAKE256", new SHAKEDigest(256), new XMSSMTSigner());
      }
   }
}
