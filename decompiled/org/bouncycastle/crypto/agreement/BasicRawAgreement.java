package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.RawAgreement;
import org.bouncycastle.util.BigIntegers;

public final class BasicRawAgreement implements RawAgreement {
   public final BasicAgreement basicAgreement;

   public BasicRawAgreement(BasicAgreement var1) {
      if (var1 == null) {
         throw new NullPointerException("'basicAgreement' cannot be null");
      } else {
         this.basicAgreement = var1;
      }
   }

   @Override
   public void init(CipherParameters var1) {
      this.basicAgreement.init(var1);
   }

   @Override
   public int getAgreementSize() {
      return this.basicAgreement.getFieldSize();
   }

   @Override
   public void calculateAgreement(CipherParameters var1, byte[] var2, int var3) {
      BigInteger var4 = this.basicAgreement.calculateAgreement(var1);
      BigIntegers.asUnsignedByteArray(var4, var2, var3, this.getAgreementSize());
   }
}
