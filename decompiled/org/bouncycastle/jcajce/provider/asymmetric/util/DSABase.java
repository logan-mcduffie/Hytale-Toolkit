package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.math.BigInteger;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.signers.DSAEncoding;

public abstract class DSABase extends SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
   protected Digest digest;
   protected DSAExt signer;
   protected DSAEncoding encoding;

   protected DSABase(Digest var1, DSAExt var2, DSAEncoding var3) {
      this.digest = var1;
      this.signer = var2;
      this.encoding = var3;
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
