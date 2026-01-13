package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

public class PKIXRecipientId extends RecipientId {
   protected final X509CertificateHolderSelector baseSelector;

   protected PKIXRecipientId(int var1, X509CertificateHolderSelector var2) {
      super(var1);
      this.baseSelector = var2;
   }

   protected PKIXRecipientId(int var1, X500Name var2, BigInteger var3, byte[] var4) {
      this(var1, new X509CertificateHolderSelector(var2, var3, var4));
   }

   public X500Name getIssuer() {
      return this.baseSelector.getIssuer();
   }

   public BigInteger getSerialNumber() {
      return this.baseSelector.getSerialNumber();
   }

   public byte[] getSubjectKeyIdentifier() {
      return this.baseSelector.getSubjectKeyIdentifier();
   }

   @Override
   public Object clone() {
      return new PKIXRecipientId(this.getType(), this.baseSelector);
   }

   @Override
   public int hashCode() {
      return this.baseSelector.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof PKIXRecipientId)) {
         return false;
      } else {
         PKIXRecipientId var2 = (PKIXRecipientId)var1;
         return this.baseSelector.equals(var2.baseSelector);
      }
   }

   @Override
   public boolean match(Object var1) {
      return this.baseSelector.match(var1);
   }
}
