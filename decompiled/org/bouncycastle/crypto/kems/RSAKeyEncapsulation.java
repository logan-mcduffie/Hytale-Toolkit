package org.bouncycastle.crypto.kems;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.KeyEncapsulation;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.Arrays;

/** @deprecated */
public class RSAKeyEncapsulation implements KeyEncapsulation {
   private DerivationFunction kdf;
   private SecureRandom rnd;
   private RSAKeyParameters key;

   public RSAKeyEncapsulation(DerivationFunction var1, SecureRandom var2) {
      this.kdf = var1;
      this.rnd = var2;
   }

   @Override
   public void init(CipherParameters var1) throws IllegalArgumentException {
      if (!(var1 instanceof RSAKeyParameters)) {
         throw new IllegalArgumentException("RSA key required");
      } else {
         this.key = (RSAKeyParameters)var1;
         CryptoServicesRegistrar.checkConstraints(
            new DefaultServiceProperties(
               "RSAKem",
               ConstraintUtils.bitsOfSecurityFor(this.key.getModulus()),
               var1,
               this.key.isPrivate() ? CryptoServicePurpose.DECRYPTION : CryptoServicePurpose.ENCRYPTION
            )
         );
      }
   }

   @Override
   public CipherParameters encrypt(byte[] var1, int var2, int var3) throws IllegalArgumentException {
      if (this.key.isPrivate()) {
         throw new IllegalArgumentException("Public key required for encryption");
      } else {
         RSAKEMGenerator var4 = new RSAKEMGenerator(var3, this.kdf, this.rnd);
         SecretWithEncapsulation var5 = var4.generateEncapsulated(this.key);
         byte[] var6 = var5.getEncapsulation();
         System.arraycopy(var6, 0, var1, var2, var6.length);
         return new KeyParameter(var5.getSecret());
      }
   }

   public CipherParameters encrypt(byte[] var1, int var2) {
      return this.encrypt(var1, 0, var2);
   }

   @Override
   public CipherParameters decrypt(byte[] var1, int var2, int var3, int var4) throws IllegalArgumentException {
      if (!this.key.isPrivate()) {
         throw new IllegalArgumentException("Private key required for decryption");
      } else {
         RSAKEMExtractor var5 = new RSAKEMExtractor(this.key, var4, this.kdf);
         byte[] var6 = var5.extractSecret(Arrays.copyOfRange(var1, var2, var2 + var3));
         return new KeyParameter(var6);
      }
   }

   public CipherParameters decrypt(byte[] var1, int var2) {
      return this.decrypt(var1, 0, var1.length, var2);
   }
}
