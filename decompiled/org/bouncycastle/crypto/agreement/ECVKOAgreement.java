package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

public class ECVKOAgreement {
   private final Digest digest;
   private ECPrivateKeyParameters key;
   private BigInteger ukm;

   public ECVKOAgreement(Digest var1) {
      this.digest = var1;
   }

   public void init(CipherParameters var1) {
      ParametersWithUKM var2 = (ParametersWithUKM)var1;
      this.key = (ECPrivateKeyParameters)var2.getParameters();
      this.ukm = new BigInteger(1, Arrays.reverse(var2.getUKM()));
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECVKO", this.key));
   }

   public int getAgreementSize() {
      return this.digest.getDigestSize();
   }

   /** @deprecated */
   public int getFieldSize() {
      return this.key.getParameters().getCurve().getFieldElementEncodingLength();
   }

   public byte[] calculateAgreement(CipherParameters var1) {
      ECPublicKeyParameters var2 = (ECPublicKeyParameters)var1;
      ECDomainParameters var3 = this.key.getParameters();
      if (!var3.equals(var2.getParameters())) {
         throw new IllegalStateException("ECVKO public key has wrong domain parameters");
      } else {
         BigInteger var4 = var3.getH().multiply(this.ukm).multiply(this.key.getD()).mod(var3.getN());
         ECPoint var5 = ECAlgorithms.cleanPoint(var3.getCurve(), var2.getQ());
         if (var5.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid public key for ECVKO");
         } else {
            ECPoint var6 = var5.multiply(var4).normalize();
            if (var6.isInfinity()) {
               throw new IllegalStateException("Infinity is not a valid agreement value for ECVKO");
            } else {
               byte[] var7 = var6.getEncoded(false);
               int var8 = var7.length;
               int var9 = var8 / 2;
               Arrays.reverseInPlace(var7, var8 - var9 * 2, var9);
               Arrays.reverseInPlace(var7, var8 - var9, var9);
               byte[] var10 = new byte[this.digest.getDigestSize()];
               this.digest.update(var7, var8 - var9 * 2, var9 * 2);
               this.digest.doFinal(var10, 0);
               return var10;
            }
         }
      }
   }
}
