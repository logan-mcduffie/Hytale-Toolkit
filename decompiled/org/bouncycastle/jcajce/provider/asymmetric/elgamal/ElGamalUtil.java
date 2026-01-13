package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.jce.interfaces.ElGamalPrivateKey;
import org.bouncycastle.jce.interfaces.ElGamalPublicKey;

public class ElGamalUtil {
   public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey var0) throws InvalidKeyException {
      if (var0 instanceof ElGamalPublicKey) {
         ElGamalPublicKey var2 = (ElGamalPublicKey)var0;
         return new ElGamalPublicKeyParameters(var2.getY(), new ElGamalParameters(var2.getParameters().getP(), var2.getParameters().getG()));
      } else if (var0 instanceof DHPublicKey) {
         DHPublicKey var1 = (DHPublicKey)var0;
         return new ElGamalPublicKeyParameters(var1.getY(), new ElGamalParameters(var1.getParams().getP(), var1.getParams().getG()));
      } else {
         throw new InvalidKeyException("can't identify public key for El Gamal.");
      }
   }

   public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey var0) throws InvalidKeyException {
      if (var0 instanceof ElGamalPrivateKey) {
         ElGamalPrivateKey var2 = (ElGamalPrivateKey)var0;
         return new ElGamalPrivateKeyParameters(var2.getX(), new ElGamalParameters(var2.getParameters().getP(), var2.getParameters().getG()));
      } else if (var0 instanceof DHPrivateKey) {
         DHPrivateKey var1 = (DHPrivateKey)var0;
         return new ElGamalPrivateKeyParameters(var1.getX(), new ElGamalParameters(var1.getParams().getP(), var1.getParams().getG()));
      } else {
         throw new InvalidKeyException("can't identify private key for El Gamal.");
      }
   }
}
