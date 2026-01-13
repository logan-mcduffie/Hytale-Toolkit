package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class RSAKEMGenerator implements EncapsulatedSecretGenerator {
   private static final BigInteger ZERO = BigInteger.valueOf(0L);
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private final int keyLen;
   private DerivationFunction kdf;
   private SecureRandom rnd;

   public RSAKEMGenerator(int var1, DerivationFunction var2, SecureRandom var3) {
      this.keyLen = var1;
      this.kdf = var2;
      this.rnd = var3;
   }

   @Override
   public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter var1) {
      RSAKeyParameters var2 = (RSAKeyParameters)var1;
      if (var2.isPrivate()) {
         throw new IllegalArgumentException("public key required for encryption");
      } else {
         CryptoServicesRegistrar.checkConstraints(
            new DefaultServiceProperties("RSAKem", ConstraintUtils.bitsOfSecurityFor(var2.getModulus()), var2, CryptoServicePurpose.ENCRYPTION)
         );
         BigInteger var3 = var2.getModulus();
         BigInteger var4 = var2.getExponent();
         BigInteger var5 = BigIntegers.createRandomInRange(ZERO, var3.subtract(ONE), this.rnd);
         BigInteger var6 = var5.modPow(var4, var3);
         byte[] var7 = BigIntegers.asUnsignedByteArray((var3.bitLength() + 7) / 8, var6);
         return new SecretWithEncapsulationImpl(generateKey(this.kdf, var3, var5, this.keyLen), var7);
      }
   }

   static byte[] generateKey(DerivationFunction var0, BigInteger var1, BigInteger var2, int var3) {
      byte[] var4 = BigIntegers.asUnsignedByteArray((var1.bitLength() + 7) / 8, var2);
      var0.init(new KDFParameters(var4, null));
      byte[] var5 = new byte[var3];
      var0.generateBytes(var5, 0, var5.length);
      return var5;
   }
}
