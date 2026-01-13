package org.bouncycastle.cert.dane;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DigestCalculator;

public class DANEEntryFactory {
   private final DANEEntrySelectorFactory selectorFactory;

   public DANEEntryFactory(DigestCalculator var1) {
      this.selectorFactory = new DANEEntrySelectorFactory(var1);
   }

   public DANEEntry createEntry(String var1, X509CertificateHolder var2) throws DANEException {
      return this.createEntry(var1, 3, var2);
   }

   public DANEEntry createEntry(String var1, int var2, X509CertificateHolder var3) throws DANEException {
      if (var2 >= 0 && var2 <= 3) {
         DANEEntrySelector var4 = this.selectorFactory.createSelector(var1);
         byte[] var5 = new byte[]{(byte)var2, 0, 0};
         return new DANEEntry(var4.getDomainName(), var5, var3);
      } else {
         throw new DANEException("unknown certificate usage: " + var2);
      }
   }
}
