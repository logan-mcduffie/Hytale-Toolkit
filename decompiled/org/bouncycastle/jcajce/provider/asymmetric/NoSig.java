package org.bouncycastle.jcajce.provider.asymmetric;

import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.SignatureSpi;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class NoSig {
   private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.NoSig$";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("Signature." + X509ObjectIdentifiers.id_alg_noSignature, "org.bouncycastle.jcajce.provider.asymmetric.NoSig$SigSpi");
         var1.addAlgorithm("Signature." + X509ObjectIdentifiers.id_alg_unsigned, "org.bouncycastle.jcajce.provider.asymmetric.NoSig$SigSpi");
      }
   }

   public static class SigSpi extends SignatureSpi {
      @Override
      protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
         throw new InvalidKeyException("attempt to pass public key to NoSig");
      }

      @Override
      protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
         throw new InvalidKeyException("attempt to pass private key to NoSig");
      }

      @Override
      protected void engineUpdate(byte var1) throws SignatureException {
      }

      @Override
      protected void engineUpdate(byte[] var1, int var2, int var3) throws SignatureException {
      }

      @Override
      protected byte[] engineSign() throws SignatureException {
         return new byte[0];
      }

      @Override
      protected boolean engineVerify(byte[] var1) throws SignatureException {
         return false;
      }

      @Override
      protected void engineSetParameter(String var1, Object var2) throws InvalidParameterException {
      }

      @Override
      protected Object engineGetParameter(String var1) throws InvalidParameterException {
         return null;
      }
   }
}
