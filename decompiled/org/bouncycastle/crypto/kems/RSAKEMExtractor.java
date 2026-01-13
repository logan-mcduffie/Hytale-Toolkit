package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class RSAKEMExtractor implements EncapsulatedSecretExtractor {
   private final RSAKeyParameters privKey;
   private final int keyLen;
   private DerivationFunction kdf;

   public RSAKEMExtractor(RSAKeyParameters var1, int var2, DerivationFunction var3) {
      if (!var1.isPrivate()) {
         throw new IllegalArgumentException("private key required for encryption");
      } else {
         this.privKey = var1;
         this.keyLen = var2;
         this.kdf = var3;
         CryptoServicesRegistrar.checkConstraints(
            new DefaultServiceProperties("RSAKem", ConstraintUtils.bitsOfSecurityFor(this.privKey.getModulus()), var1, CryptoServicePurpose.DECRYPTION)
         );
      }
   }

   @Override
   public byte[] extractSecret(byte[] var1) {
      BigInteger var2 = this.privKey.getModulus();
      BigInteger var3 = this.privKey.getExponent();
      BigInteger var4 = new BigInteger(1, var1);
      BigInteger var5 = var4.modPow(var3, var2);
      return RSAKEMGenerator.generateKey(this.kdf, var2, var5, this.keyLen);
   }

   @Override
   public int getEncapsulationLength() {
      return (this.privKey.getModulus().bitLength() + 7) / 8;
   }
}
