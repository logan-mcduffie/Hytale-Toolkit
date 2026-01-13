package org.bouncycastle.asn1.x500.style;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameStyle;

public class BCStrictStyle extends BCStyle {
   public static final X500NameStyle INSTANCE = new BCStrictStyle();

   @Override
   public boolean areEqual(X500Name var1, X500Name var2) {
      if (var1.size() != var2.size()) {
         return false;
      } else {
         RDN[] var3 = var1.getRDNs();
         RDN[] var4 = var2.getRDNs();

         for (int var5 = 0; var5 != var3.length; var5++) {
            if (!this.rdnAreEqual(var3[var5], var4[var5])) {
               return false;
            }
         }

         return true;
      }
   }
}
