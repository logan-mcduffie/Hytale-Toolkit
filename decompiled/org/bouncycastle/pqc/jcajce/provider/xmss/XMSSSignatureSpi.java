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
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSSigner;
import org.bouncycastle.pqc.jcajce.interfaces.StateAwareSignature;

public class XMSSSignatureSpi extends Signature implements StateAwareSignature {
   private Digest digest;
   private XMSSSigner signer;
   private SecureRandom random;
   private ASN1ObjectIdentifier treeDigest;

   protected XMSSSignatureSpi(String var1) {
      super(var1);
   }

   protected XMSSSignatureSpi(String var1, Digest var2, XMSSSigner var3) {
      super(var1);
      this.digest = var2;
      this.signer = var3;
   }

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      if (var1 instanceof BCXMSSPublicKey) {
         CipherParameters var2 = ((BCXMSSPublicKey)var1).getKeyParams();
         this.treeDigest = null;
         this.digest.reset();
         this.signer.init(false, var2);
      } else {
         throw new InvalidKeyException("unknown public key passed to XMSS");
      }
   }

   @Override
   protected void engineInitSign(PrivateKey var1, SecureRandom var2) throws InvalidKeyException {
      this.random = var2;
      this.engineInitSign(var1);
   }

   @Override
   protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      if (var1 instanceof BCXMSSPrivateKey) {
         Object var2 = ((BCXMSSPrivateKey)var1).getKeyParams();
         this.treeDigest = ((BCXMSSPrivateKey)var1).getTreeDigestOID();
         if (this.random != null) {
            var2 = new ParametersWithRandom((CipherParameters)var2, this.random);
         }

         this.digest.reset();
         this.signer.init(true, (CipherParameters)var2);
      } else {
         throw new InvalidKeyException("unknown private key passed to XMSS");
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
            throw new SignatureException(var3.toString(), var3);
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
         BCXMSSPrivateKey var1 = new BCXMSSPrivateKey(this.treeDigest, (XMSSPrivateKeyParameters)this.signer.getUpdatedPrivateKey());
         this.treeDigest = null;
         return var1;
      }
   }

   public static class generic extends XMSSSignatureSpi {
      public generic() {
         super("XMSS", new NullDigest(), new XMSSSigner());
      }
   }

   public static class withSha256 extends XMSSSignatureSpi {
      public withSha256() {
         super("XMSS-SHA256", new NullDigest(), new XMSSSigner());
      }
   }

   public static class withSha256andPrehash extends XMSSSignatureSpi {
      public withSha256andPrehash() {
         super("SHA256withXMSS-SHA256", new SHA256Digest(), new XMSSSigner());
      }
   }

   public static class withSha512 extends XMSSSignatureSpi {
      public withSha512() {
         super("XMSS-SHA512", new NullDigest(), new XMSSSigner());
      }
   }

   public static class withSha512andPrehash extends XMSSSignatureSpi {
      public withSha512andPrehash() {
         super("SHA512withXMSS-SHA512", new SHA512Digest(), new XMSSSigner());
      }
   }

   public static class withShake128 extends XMSSSignatureSpi {
      public withShake128() {
         super("XMSS-SHAKE128", new NullDigest(), new XMSSSigner());
      }
   }

   public static class withShake128_512andPrehash extends XMSSSignatureSpi {
      public withShake128_512andPrehash() {
         super("SHAKE128(512)withXMSS-SHAKE128", new DigestUtil.DoubleDigest(new SHAKEDigest(128)), new XMSSSigner());
      }
   }

   public static class withShake128andPrehash extends XMSSSignatureSpi {
      public withShake128andPrehash() {
         super("SHAKE128withXMSS-SHAKE128", new SHAKEDigest(128), new XMSSSigner());
      }
   }

   public static class withShake256 extends XMSSSignatureSpi {
      public withShake256() {
         super("XMSS-SHAKE256", new NullDigest(), new XMSSSigner());
      }
   }

   public static class withShake256_1024andPrehash extends XMSSSignatureSpi {
      public withShake256_1024andPrehash() {
         super("SHAKE256(1024)withXMSS-SHAKE256", new DigestUtil.DoubleDigest(new SHAKEDigest(256)), new XMSSSigner());
      }
   }

   public static class withShake256andPrehash extends XMSSSignatureSpi {
      public withShake256andPrehash() {
         super("SHAKE256withXMSS-SHAKE256", new SHAKEDigest(256), new XMSSSigner());
      }
   }
}
