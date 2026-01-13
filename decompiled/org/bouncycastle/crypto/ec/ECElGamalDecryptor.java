package org.bouncycastle.crypto.ec;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class ECElGamalDecryptor implements ECDecryptor {
   private ECPrivateKeyParameters key;

   @Override
   public void init(CipherParameters var1) {
      if (!(var1 instanceof ECPrivateKeyParameters)) {
         throw new IllegalArgumentException("ECPrivateKeyParameters are required for decryption.");
      } else {
         this.key = (ECPrivateKeyParameters)var1;
      }
   }

   @Override
   public ECPoint decrypt(ECPair var1) {
      if (this.key == null) {
         throw new IllegalStateException("ECElGamalDecryptor not initialised");
      } else {
         ECCurve var2 = this.key.getParameters().getCurve();
         ECPoint var3 = ECAlgorithms.cleanPoint(var2, var1.getX()).multiply(this.key.getD());
         return ECAlgorithms.cleanPoint(var2, var1.getY()).subtract(var3).normalize();
      }
   }
}
