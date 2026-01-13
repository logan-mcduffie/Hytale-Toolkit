package org.bouncycastle.jcajce.provider.asymmetric.gost;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.GOST3410Signer;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.GOST3410Util;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.interfaces.GOST3410Key;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class SignatureSpi extends java.security.SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
   private Digest digest = new GOST3411Digest();
   private DSAExt signer = new GOST3410Signer();
   private SecureRandom random;

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      AsymmetricKeyParameter var2;
      if (var1 instanceof ECPublicKey) {
         var2 = ECUtil.generatePublicKeyParameter(var1);
      } else if (var1 instanceof GOST3410Key) {
         var2 = GOST3410Util.generatePublicKeyParameter(var1);
      } else {
         try {
            byte[] var3 = var1.getEncoded();
            var1 = BouncyCastleProvider.getPublicKey(SubjectPublicKeyInfo.getInstance(var3));
            if (!(var1 instanceof ECPublicKey)) {
               throw new InvalidKeyException("can't recognise key type in DSA based signer");
            }

            var2 = ECUtil.generatePublicKeyParameter(var1);
         } catch (Exception var4) {
            throw new InvalidKeyException("can't recognise key type in DSA based signer");
         }
      }

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
      AsymmetricKeyParameter var2;
      if (var1 instanceof ECKey) {
         var2 = ECUtil.generatePrivateKeyParameter(var1);
      } else {
         var2 = GOST3410Util.generatePrivateKeyParameter(var1);
      }

      this.digest.reset();
      if (this.random != null) {
         this.signer.init(true, new ParametersWithRandom(var2, this.random));
      } else {
         this.signer.init(true, var2);
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
         byte[] var2 = new byte[64];
         BigInteger[] var3 = this.signer.generateSignature(var1);
         byte[] var4 = var3[0].toByteArray();
         byte[] var5 = var3[1].toByteArray();
         if (var5[0] != 0) {
            System.arraycopy(var5, 0, var2, 32 - var5.length, var5.length);
         } else {
            System.arraycopy(var5, 1, var2, 32 - (var5.length - 1), var5.length - 1);
         }

         if (var4[0] != 0) {
            System.arraycopy(var4, 0, var2, 64 - var4.length, var4.length);
         } else {
            System.arraycopy(var4, 1, var2, 64 - (var4.length - 1), var4.length - 1);
         }

         return var2;
      } catch (Exception var6) {
         throw new SignatureException(var6.toString());
      }
   }

   @Override
   protected boolean engineVerify(byte[] var1) throws SignatureException {
      byte[] var2 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var2, 0);

      BigInteger[] var3;
      try {
         byte[] var4 = new byte[32];
         byte[] var5 = new byte[32];
         System.arraycopy(var1, 0, var5, 0, 32);
         System.arraycopy(var1, 32, var4, 0, 32);
         var3 = new BigInteger[]{new BigInteger(1, var4), new BigInteger(1, var5)};
      } catch (Exception var6) {
         throw new SignatureException("error decoding signature bytes.");
      }

      return this.signer.verifySignature(var2, var3[0], var3[1]);
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
}
