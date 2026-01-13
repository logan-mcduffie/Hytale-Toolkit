package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

public class KEMRecipientId extends PKIXRecipientId {
   private KEMRecipientId(X509CertificateHolderSelector var1) {
      super(4, var1);
   }

   public KEMRecipientId(byte[] var1) {
      super(4, null, null, var1);
   }

   public KEMRecipientId(X500Name var1, BigInteger var2) {
      super(4, var1, var2, null);
   }

   public KEMRecipientId(X500Name var1, BigInteger var2, byte[] var3) {
      super(4, var1, var2, var3);
   }

   @Override
   public Object clone() {
      return new KEMRecipientId(this.baseSelector);
   }

   @Override
   public boolean match(Object var1) {
      return var1 instanceof KEMRecipientInformation ? ((KEMRecipientInformation)var1).getRID().equals(this) : super.match(var1);
   }
}
