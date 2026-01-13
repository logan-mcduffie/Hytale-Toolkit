package org.bouncycastle.jcajce.provider.asymmetric.dstu;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ua.DSTU4145Params;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSTU4145Signer;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.interfaces.ECKey;

public class SignatureSpi extends java.security.SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
   private Digest digest;
   private DSAExt signer = new DSTU4145Signer();

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      Object var2;
      if (var1 instanceof BCDSTU4145PublicKey) {
         var2 = ((BCDSTU4145PublicKey)var1).engineGetKeyParameters();
         this.digest = new GOST3411Digest(this.expandSbox(((BCDSTU4145PublicKey)var1).getSbox()));
      } else {
         var2 = ECUtil.generatePublicKeyParameter(var1);
         this.digest = new GOST3411Digest(this.expandSbox(DSTU4145Params.getDefaultDKE()));
      }

      this.signer.init(false, (CipherParameters)var2);
   }

   byte[] expandSbox(byte[] var1) {
      byte[] var2 = new byte[128];

      for (int var3 = 0; var3 < var1.length; var3++) {
         var2[var3 * 2] = (byte)(var1[var3] >> 4 & 15);
         var2[var3 * 2 + 1] = (byte)(var1[var3] & 15);
      }

      return var2;
   }

   @Override
   protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      AsymmetricKeyParameter var2 = null;
      if (var1 instanceof BCDSTU4145PrivateKey) {
         var2 = ECUtil.generatePrivateKeyParameter(var1);
         this.digest = new GOST3411Digest(this.expandSbox(DSTU4145Params.getDefaultDKE()));
      } else if (var1 instanceof ECKey) {
         var2 = ECUtil.generatePrivateKeyParameter(var1);
         this.digest = new GOST3411Digest(this.expandSbox(DSTU4145Params.getDefaultDKE()));
      }

      if (this.appRandom != null) {
         this.signer.init(true, new ParametersWithRandom(var2, this.appRandom));
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
         BigInteger[] var2 = this.signer.generateSignature(var1);
         byte[] var3 = var2[0].toByteArray();
         byte[] var4 = var2[1].toByteArray();
         byte[] var5 = new byte[var3.length > var4.length ? var3.length * 2 : var4.length * 2];
         System.arraycopy(var4, 0, var5, var5.length / 2 - var4.length, var4.length);
         System.arraycopy(var3, 0, var5, var5.length - var3.length, var3.length);
         return new DEROctetString(var5).getEncoded();
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
         byte[] var4 = ((ASN1OctetString)ASN1OctetString.fromByteArray(var1)).getOctets();
         byte[] var5 = new byte[var4.length / 2];
         byte[] var6 = new byte[var4.length / 2];
         System.arraycopy(var4, 0, var6, 0, var4.length / 2);
         System.arraycopy(var4, var4.length / 2, var5, 0, var4.length / 2);
         var3 = new BigInteger[]{new BigInteger(1, var5), new BigInteger(1, var6)};
      } catch (Exception var7) {
         throw new SignatureException("error decoding signature bytes.");
      }

      return this.signer.verifySignature(var2, var3[0], var3[1]);
   }

   @Override
   protected void engineSetParameter(AlgorithmParameterSpec var1) {
      throw new UnsupportedOperationException("engineSetParameter unsupported");
   }

   @Override
   protected AlgorithmParameters engineGetParameters() {
      return null;
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
