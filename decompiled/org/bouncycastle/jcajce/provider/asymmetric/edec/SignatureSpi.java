package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.crypto.signers.Ed448Signer;

public class SignatureSpi extends java.security.SignatureSpi {
   private static final byte[] EMPTY_CONTEXT = new byte[0];
   private final String algorithm;
   private Signer signer;

   SignatureSpi(String var1) {
      this.algorithm = var1;
   }

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      AsymmetricKeyParameter var2 = getLwEdDSAKeyPublic(var1);
      if (var2 instanceof Ed25519PublicKeyParameters) {
         this.signer = this.getSigner("Ed25519");
      } else {
         if (!(var2 instanceof Ed448PublicKeyParameters)) {
            throw new InvalidKeyException("unsupported public key type");
         }

         this.signer = this.getSigner("Ed448");
      }

      this.signer.init(false, var2);
   }

   @Override
   protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      AsymmetricKeyParameter var2 = getLwEdDSAKeyPrivate(var1);
      if (var2 instanceof Ed25519PrivateKeyParameters) {
         this.signer = this.getSigner("Ed25519");
      } else {
         if (!(var2 instanceof Ed448PrivateKeyParameters)) {
            throw new InvalidKeyException("unsupported private key type");
         }

         this.signer = this.getSigner("Ed448");
      }

      this.signer.init(true, var2);
   }

   private static AsymmetricKeyParameter getLwEdDSAKeyPrivate(PrivateKey var0) throws InvalidKeyException {
      return EdECUtil.generatePrivateKeyParameter(var0);
   }

   private static AsymmetricKeyParameter getLwEdDSAKeyPublic(PublicKey var0) throws InvalidKeyException {
      return EdECUtil.generatePublicKeyParameter(var0);
   }

   private Signer getSigner(String var1) throws InvalidKeyException {
      if (this.algorithm != null && !var1.equals(this.algorithm)) {
         throw new InvalidKeyException("inappropriate key for " + this.algorithm);
      } else {
         return (Signer)(var1.equals("Ed448") ? new Ed448Signer(EMPTY_CONTEXT) : new Ed25519Signer());
      }
   }

   @Override
   protected void engineUpdate(byte var1) throws SignatureException {
      this.signer.update(var1);
   }

   @Override
   protected void engineUpdate(byte[] var1, int var2, int var3) throws SignatureException {
      this.signer.update(var1, var2, var3);
   }

   @Override
   protected byte[] engineSign() throws SignatureException {
      try {
         return this.signer.generateSignature();
      } catch (CryptoException var2) {
         throw new SignatureException(var2.getMessage());
      }
   }

   @Override
   protected boolean engineVerify(byte[] var1) throws SignatureException {
      return this.signer.verifySignature(var1);
   }

   @Override
   protected void engineSetParameter(String var1, Object var2) throws InvalidParameterException {
      throw new UnsupportedOperationException("engineSetParameter unsupported");
   }

   @Override
   protected Object engineGetParameter(String var1) throws InvalidParameterException {
      throw new UnsupportedOperationException("engineGetParameter unsupported");
   }

   @Override
   protected AlgorithmParameters engineGetParameters() {
      return null;
   }

   public static final class Ed25519 extends SignatureSpi {
      public Ed25519() {
         super("Ed25519");
      }
   }

   public static final class Ed448 extends SignatureSpi {
      public Ed448() {
         super("Ed448");
      }
   }

   public static final class EdDSA extends SignatureSpi {
      public EdDSA() {
         super(null);
      }
   }
}
