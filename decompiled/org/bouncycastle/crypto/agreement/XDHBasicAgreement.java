package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.RawAgreement;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;

public class XDHBasicAgreement implements BasicAgreement {
   private AsymmetricKeyParameter key;
   private RawAgreement agreement;
   private int fieldSize = 0;

   @Override
   public void init(CipherParameters var1) {
      if (var1 instanceof X25519PrivateKeyParameters) {
         this.fieldSize = 32;
         this.agreement = new X25519Agreement();
      } else {
         if (!(var1 instanceof X448PrivateKeyParameters)) {
            throw new IllegalArgumentException("key is neither X25519 nor X448");
         }

         this.fieldSize = 56;
         this.agreement = new X448Agreement();
      }

      this.key = (AsymmetricKeyParameter)var1;
      this.agreement.init(var1);
   }

   @Override
   public int getFieldSize() {
      return this.fieldSize;
   }

   @Override
   public BigInteger calculateAgreement(CipherParameters var1) {
      byte[] var2 = new byte[this.fieldSize];
      this.agreement.calculateAgreement(var1, var2, 0);
      return new BigInteger(1, var2);
   }
}
