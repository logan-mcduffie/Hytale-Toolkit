package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.GOST3410Parameters;
import org.bouncycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.bouncycastle.crypto.params.GOST3410PublicKeyParameters;
import org.bouncycastle.jce.interfaces.GOST3410PrivateKey;
import org.bouncycastle.jce.interfaces.GOST3410PublicKey;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;

public class GOST3410Util {
   public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey var0) throws InvalidKeyException {
      if (var0 instanceof GOST3410PublicKey) {
         GOST3410PublicKey var1 = (GOST3410PublicKey)var0;
         GOST3410PublicKeyParameterSetSpec var2 = var1.getParameters().getPublicKeyParameters();
         return new GOST3410PublicKeyParameters(var1.getY(), new GOST3410Parameters(var2.getP(), var2.getQ(), var2.getA()));
      } else {
         throw new InvalidKeyException("can't identify GOST3410 public key: " + var0.getClass().getName());
      }
   }

   public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey var0) throws InvalidKeyException {
      if (var0 instanceof GOST3410PrivateKey) {
         GOST3410PrivateKey var1 = (GOST3410PrivateKey)var0;
         GOST3410PublicKeyParameterSetSpec var2 = var1.getParameters().getPublicKeyParameters();
         return new GOST3410PrivateKeyParameters(var1.getX(), new GOST3410Parameters(var2.getP(), var2.getQ(), var2.getA()));
      } else {
         throw new InvalidKeyException("can't identify GOST3410 private key.");
      }
   }
}
