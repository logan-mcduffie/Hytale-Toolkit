package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

public class KeyAgreeRecipientId extends PKIXRecipientId {
   private KeyAgreeRecipientId(X509CertificateHolderSelector var1) {
      super(2, var1);
   }

   public KeyAgreeRecipientId(byte[] var1) {
      super(2, null, null, var1);
   }

   public KeyAgreeRecipientId(X500Name var1, BigInteger var2) {
      super(2, var1, var2, null);
   }

   public KeyAgreeRecipientId(X500Name var1, BigInteger var2, byte[] var3) {
      super(2, var1, var2, var3);
   }

   @Override
   public X500Name getIssuer() {
      return this.baseSelector.getIssuer();
   }

   @Override
   public BigInteger getSerialNumber() {
      return this.baseSelector.getSerialNumber();
   }

   @Override
   public byte[] getSubjectKeyIdentifier() {
      return this.baseSelector.getSubjectKeyIdentifier();
   }

   @Override
   public int hashCode() {
      return this.baseSelector.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof KeyAgreeRecipientId)) {
         return false;
      } else {
         KeyAgreeRecipientId var2 = (KeyAgreeRecipientId)var1;
         return this.baseSelector.equals(var2.baseSelector);
      }
   }

   @Override
   public Object clone() {
      return new KeyAgreeRecipientId(this.baseSelector);
   }

   @Override
   public boolean match(Object var1) {
      return var1 instanceof KeyAgreeRecipientInformation ? ((KeyAgreeRecipientInformation)var1).getRID().equals(this) : this.baseSelector.match(var1);
   }
}
