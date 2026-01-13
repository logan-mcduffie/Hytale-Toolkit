package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.BigIntegers;

public class SAKKEPrivateKeyParameters extends AsymmetricKeyParameter {
   private static final BigInteger qMinOne = SAKKEPublicKeyParameters.q.subtract(BigInteger.ONE);
   private final SAKKEPublicKeyParameters publicParams;
   private final BigInteger z;

   public SAKKEPrivateKeyParameters(BigInteger var1, SAKKEPublicKeyParameters var2) {
      super(true);
      this.z = var1;
      this.publicParams = var2;
      ECPoint var3 = var2.getPoint().multiply(var1).normalize();
      if (!var3.equals(var2.getZ())) {
         throw new IllegalStateException("public key and private key of SAKKE do not match");
      }
   }

   public SAKKEPrivateKeyParameters(SecureRandom var1) {
      super(true);
      this.z = BigIntegers.createRandomInRange(BigIntegers.TWO, qMinOne, var1);
      BigInteger var2 = BigIntegers.createRandomInRange(BigIntegers.TWO, qMinOne, var1);
      this.publicParams = new SAKKEPublicKeyParameters(var2, SAKKEPublicKeyParameters.P.multiply(this.z).normalize());
   }

   public SAKKEPublicKeyParameters getPublicParams() {
      return this.publicParams;
   }

   public BigInteger getMasterSecret() {
      return this.z;
   }
}
