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
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

/** @deprecated */
public class ECIESKeyEncapsulation implements KeyEncapsulation {
   private DerivationFunction kdf;
   private SecureRandom rnd;
   private ECKeyParameters key;
   private boolean CofactorMode;
   private boolean OldCofactorMode;
   private boolean SingleHashMode;

   public ECIESKeyEncapsulation(DerivationFunction var1, SecureRandom var2) {
      this.kdf = var1;
      this.rnd = var2;
      this.CofactorMode = false;
      this.OldCofactorMode = false;
      this.SingleHashMode = false;
   }

   public ECIESKeyEncapsulation(DerivationFunction var1, SecureRandom var2, boolean var3, boolean var4, boolean var5) {
      this.kdf = var1;
      this.rnd = var2;
      this.CofactorMode = var3;
      if (var3) {
         this.OldCofactorMode = false;
      } else {
         this.OldCofactorMode = var4;
      }

      this.SingleHashMode = var5;
   }

   @Override
   public void init(CipherParameters var1) throws IllegalArgumentException {
      if (!(var1 instanceof ECKeyParameters)) {
         throw new IllegalArgumentException("EC key required");
      } else {
         this.key = (ECKeyParameters)var1;
         CryptoServicesRegistrar.checkConstraints(
            new DefaultServiceProperties("ECIESKem", ConstraintUtils.bitsOfSecurityFor(this.key.getParameters().getCurve()), var1, CryptoServicePurpose.ANY)
         );
      }
   }

   /** @deprecated */
   @Override
   public CipherParameters encrypt(byte[] var1, int var2, int var3) throws IllegalArgumentException {
      if (!(this.key instanceof ECPublicKeyParameters)) {
         throw new IllegalArgumentException("Public key required for encryption");
      } else {
         ECIESKEMGenerator var4 = new ECIESKEMGenerator(var3, this.kdf, this.rnd, this.CofactorMode, this.OldCofactorMode, this.SingleHashMode);
         SecretWithEncapsulation var5 = var4.generateEncapsulated(this.key);
         byte[] var6 = var5.getEncapsulation();
         System.arraycopy(var6, 0, var1, var2, var6.length);
         return new KeyParameter(var5.getSecret());
      }
   }

   /** @deprecated */
   public CipherParameters encrypt(byte[] var1, int var2) {
      return this.encrypt(var1, 0, var2);
   }

   /** @deprecated */
   @Override
   public CipherParameters decrypt(byte[] var1, int var2, int var3, int var4) throws IllegalArgumentException {
      if (!(this.key instanceof ECPrivateKeyParameters)) {
         throw new IllegalArgumentException("Private key required for encryption");
      } else {
         ECPrivateKeyParameters var5 = (ECPrivateKeyParameters)this.key;
         ECIESKEMExtractor var6 = new ECIESKEMExtractor(var5, var4, this.kdf, this.CofactorMode, this.OldCofactorMode, this.SingleHashMode);
         byte[] var7 = var6.extractSecret(Arrays.copyOfRange(var1, var2, var2 + var3));
         return new KeyParameter(var7);
      }
   }

   /** @deprecated */
   public CipherParameters decrypt(byte[] var1, int var2) {
      return this.decrypt(var1, 0, var1.length, var2);
   }
}
