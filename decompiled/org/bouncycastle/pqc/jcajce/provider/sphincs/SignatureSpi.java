package org.bouncycastle.pqc.jcajce.provider.sphincs;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA512tDigest;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256Signer;

public class SignatureSpi extends java.security.SignatureSpi {
   private final ASN1ObjectIdentifier treeDigest;
   private Digest digest;
   private SPHINCS256Signer signer;
   private SecureRandom random;

   protected SignatureSpi(Digest var1, ASN1ObjectIdentifier var2, SPHINCS256Signer var3) {
      this.digest = var1;
      this.treeDigest = var2;
      this.signer = var3;
   }

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      if (var1 instanceof BCSphincs256PublicKey) {
         BCSphincs256PublicKey var2 = (BCSphincs256PublicKey)var1;
         if (!this.treeDigest.equals(var2.getTreeDigest())) {
            throw new InvalidKeyException("SPHINCS-256 signature for tree digest: " + var2.getTreeDigest());
         } else {
            CipherParameters var3 = var2.getKeyParams();
            this.digest.reset();
            this.signer.init(false, var3);
         }
      } else {
         throw new InvalidKeyException("unknown public key passed to SPHINCS-256");
      }
   }

   @Override
   protected void engineInitSign(PrivateKey var1, SecureRandom var2) throws InvalidKeyException {
      this.random = var2;
      this.engineInitSign(var1);
   }

   @Override
   protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      if (var1 instanceof BCSphincs256PrivateKey) {
         BCSphincs256PrivateKey var2 = (BCSphincs256PrivateKey)var1;
         if (!this.treeDigest.equals(var2.getTreeDigest())) {
            throw new InvalidKeyException("SPHINCS-256 signature for tree digest: " + var2.getTreeDigest());
         } else {
            CipherParameters var3 = var2.getKeyParams();
            this.digest.reset();
            this.signer.init(true, var3);
         }
      } else {
         throw new InvalidKeyException("unknown private key passed to SPHINCS-256");
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
      byte[] var1 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var1, 0);

      try {
         return this.signer.generateSignature(var1);
      } catch (Exception var3) {
         throw new SignatureException(var3.toString());
      }
   }

   @Override
   protected boolean engineVerify(byte[] var1) throws SignatureException {
      byte[] var2 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var2, 0);
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

   public static class withSha3_512 extends SignatureSpi {
      public withSha3_512() {
         super(new SHA3Digest(512), NISTObjectIdentifiers.id_sha3_256, new SPHINCS256Signer(new SHA3Digest(256), new SHA3Digest(512)));
      }
   }

   public static class withSha512 extends SignatureSpi {
      public withSha512() {
         super(new SHA512Digest(), NISTObjectIdentifiers.id_sha512_256, new SPHINCS256Signer(new SHA512tDigest(256), new SHA512Digest()));
      }
   }
}
