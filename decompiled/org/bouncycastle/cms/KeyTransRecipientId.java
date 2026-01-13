package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

public class KeyTransRecipientId extends PKIXRecipientId {
   private KeyTransRecipientId(X509CertificateHolderSelector var1) {
      super(0, var1);
   }

   public KeyTransRecipientId(byte[] var1) {
      super(0, null, null, var1);
   }

   public KeyTransRecipientId(X500Name var1, BigInteger var2) {
      super(0, var1, var2, null);
   }

   public KeyTransRecipientId(X500Name var1, BigInteger var2, byte[] var3) {
      super(0, var1, var2, var3);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof KeyTransRecipientId)) {
         return false;
      } else {
         KeyTransRecipientId var2 = (KeyTransRecipientId)var1;
         return this.baseSelector.equals(var2.baseSelector);
      }
   }

   @Override
   public Object clone() {
      return new KeyTransRecipientId(this.baseSelector);
   }

   @Override
   public boolean match(Object var1) {
      return var1 instanceof KeyTransRecipientInformation ? ((KeyTransRecipientInformation)var1).getRID().equals(this) : super.match(var1);
   }
}
