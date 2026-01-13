package org.bouncycastle.jcajce.provider.asymmetric.ecgost12;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.ECGOST3410Signer;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ECGOST2012SignatureSpi512 extends SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
   private Digest digest;
   private DSAExt signer;
   private int size = 128;
   private int halfSize = 64;

   public ECGOST2012SignatureSpi512() {
      this.digest = new GOST3411_2012_512Digest();
      this.signer = new ECGOST3410Signer();
   }

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      ECKeyParameters var2;
      if (var1 instanceof ECPublicKey) {
         var2 = (ECKeyParameters)generatePublicKeyParameter(var1);
      } else {
         try {
            byte[] var3 = var1.getEncoded();
            var1 = BouncyCastleProvider.getPublicKey(SubjectPublicKeyInfo.getInstance(var3));
            var2 = (ECKeyParameters)ECUtil.generatePublicKeyParameter(var1);
         } catch (Exception var4) {
            throw new InvalidKeyException("cannot recognise key type in ECGOST-2012-512 signer");
         }
      }

      if (var2.getParameters().getN().bitLength() < 505) {
         throw new InvalidKeyException("key too weak for ECGOST-2012-512");
      } else {
         this.digest.reset();
         this.signer.init(false, var2);
      }
   }

   @Override
   protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      if (var1 instanceof ECKey) {
         ECKeyParameters var2 = (ECKeyParameters)ECUtil.generatePrivateKeyParameter(var1);
         if (var2.getParameters().getN().bitLength() < 505) {
            throw new InvalidKeyException("key too weak for ECGOST-2012-512");
         } else {
            this.digest.reset();
            if (this.appRandom != null) {
               this.signer.init(true, new ParametersWithRandom(var2, this.appRandom));
            } else {
               this.signer.init(true, var2);
            }
         }
      } else {
         throw new InvalidKeyException("cannot recognise key type in ECGOST-2012-512 signer");
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
         byte[] var2 = new byte[this.size];
         BigInteger[] var3 = this.signer.generateSignature(var1);
         byte[] var4 = var3[0].toByteArray();
         byte[] var5 = var3[1].toByteArray();
         if (var5[0] != 0) {
            System.arraycopy(var5, 0, var2, this.halfSize - var5.length, var5.length);
         } else {
            System.arraycopy(var5, 1, var2, this.halfSize - (var5.length - 1), var5.length - 1);
         }

         if (var4[0] != 0) {
            System.arraycopy(var4, 0, var2, this.size - var4.length, var4.length);
         } else {
            System.arraycopy(var4, 1, var2, this.size - (var4.length - 1), var4.length - 1);
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
         byte[] var4 = new byte[this.halfSize];
         byte[] var5 = new byte[this.halfSize];
         System.arraycopy(var1, 0, var5, 0, this.halfSize);
         System.arraycopy(var1, this.halfSize, var4, 0, this.halfSize);
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

   static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey var0) throws InvalidKeyException {
      return (AsymmetricKeyParameter)(var0 instanceof BCECGOST3410_2012PublicKey
         ? ((BCECGOST3410_2012PublicKey)var0).engineGetKeyParameters()
         : ECUtil.generatePublicKeyParameter(var0));
   }
}
