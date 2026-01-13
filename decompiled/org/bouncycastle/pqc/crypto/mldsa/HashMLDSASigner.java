package org.bouncycastle.pqc.crypto.mldsa;

import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.DigestUtils;

public class HashMLDSASigner implements Signer {
   private static final byte[] EMPTY_CONTEXT = new byte[0];
   private MLDSAPublicKeyParameters pubKey;
   private MLDSAPrivateKeyParameters privKey;
   private SecureRandom random;
   private MLDSAEngine engine;
   private Digest digest;
   private byte[] digestOIDEncoding;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      byte[] var3 = EMPTY_CONTEXT;
      if (var2 instanceof ParametersWithContext) {
         ParametersWithContext var4 = (ParametersWithContext)var2;
         var3 = var4.getContext();
         var2 = var4.getParameters();
         if (var3.length > 255) {
            throw new IllegalArgumentException("context too long");
         }
      }

      MLDSAParameters var6;
      if (var1) {
         this.pubKey = null;
         if (var2 instanceof ParametersWithRandom) {
            ParametersWithRandom var5 = (ParametersWithRandom)var2;
            this.privKey = (MLDSAPrivateKeyParameters)var5.getParameters();
            this.random = var5.getRandom();
         } else {
            this.privKey = (MLDSAPrivateKeyParameters)var2;
            this.random = null;
         }

         var6 = this.privKey.getParameters();
         this.engine = var6.getEngine(this.random);
         this.engine.initSign(this.privKey.tr, true, var3);
      } else {
         this.pubKey = (MLDSAPublicKeyParameters)var2;
         this.privKey = null;
         this.random = null;
         var6 = this.pubKey.getParameters();
         this.engine = var6.getEngine(null);
         this.engine.initVerify(this.pubKey.rho, this.pubKey.t1, true, var3);
      }

      this.initDigest(var6);
   }

   private void initDigest(MLDSAParameters var1) {
      this.digest = createDigest(var1);
      ASN1ObjectIdentifier var2 = DigestUtils.getDigestOid(this.digest.getAlgorithmName());

      try {
         this.digestOIDEncoding = var2.getEncoded("DER");
      } catch (IOException var4) {
         throw new IllegalStateException("oid encoding failed: " + var4.getMessage());
      }
   }

   @Override
   public void update(byte var1) {
      this.digest.update(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.digest.update(var1, var2, var3);
   }

   @Override
   public byte[] generateSignature() throws CryptoException, DataLengthException {
      SHAKEDigest var1 = this.finishPreHash();
      byte[] var2 = new byte[32];
      if (this.random != null) {
         this.random.nextBytes(var2);
      }

      byte[] var3 = this.engine.generateMu(var1);
      return this.engine.generateSignature(var3, var1, this.privKey.rho, this.privKey.k, this.privKey.t0, this.privKey.s1, this.privKey.s2, var2);
   }

   @Override
   public boolean verifySignature(byte[] var1) {
      SHAKEDigest var2 = this.finishPreHash();
      return this.engine.verifyInternal(var1, var1.length, var2, this.pubKey.rho, this.pubKey.t1);
   }

   @Override
   public void reset() {
      this.digest.reset();
   }

   private SHAKEDigest finishPreHash() {
      byte[] var1 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var1, 0);
      SHAKEDigest var2 = this.engine.getShake256Digest();
      var2.update(this.digestOIDEncoding, 0, this.digestOIDEncoding.length);
      var2.update(var1, 0, var1.length);
      return var2;
   }

   private static Digest createDigest(MLDSAParameters var0) {
      switch (var0.getType()) {
         case 0:
         case 1:
            return new SHA512Digest();
         default:
            throw new IllegalArgumentException("unknown parameters type");
      }
   }
}
