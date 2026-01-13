package org.bouncycastle.x509;

import org.bouncycastle.util.Selector;

public class X509CertPairStoreSelector implements Selector {
   private X509CertStoreSelector forwardSelector;
   private X509CertStoreSelector reverseSelector;
   private X509CertificatePair certPair;

   public X509CertificatePair getCertPair() {
      return this.certPair;
   }

   public void setCertPair(X509CertificatePair var1) {
      this.certPair = var1;
   }

   public void setForwardSelector(X509CertStoreSelector var1) {
      this.forwardSelector = var1;
   }

   public void setReverseSelector(X509CertStoreSelector var1) {
      this.reverseSelector = var1;
   }

   @Override
   public Object clone() {
      X509CertPairStoreSelector var1 = new X509CertPairStoreSelector();
      var1.certPair = this.certPair;
      if (this.forwardSelector != null) {
         var1.setForwardSelector((X509CertStoreSelector)this.forwardSelector.clone());
      }

      if (this.reverseSelector != null) {
         var1.setReverseSelector((X509CertStoreSelector)this.reverseSelector.clone());
      }

      return var1;
   }

   @Override
   public boolean match(Object var1) {
      try {
         if (!(var1 instanceof X509CertificatePair)) {
            return false;
         } else {
            X509CertificatePair var2 = (X509CertificatePair)var1;
            if (this.forwardSelector != null && !this.forwardSelector.match(var2.getForward())) {
               return false;
            } else if (this.reverseSelector != null && !this.reverseSelector.match(var2.getReverse())) {
               return false;
            } else {
               return this.certPair != null ? this.certPair.equals(var1) : true;
            }
         }
      } catch (Exception var3) {
         return false;
      }
   }

   public X509CertStoreSelector getForwardSelector() {
      return this.forwardSelector;
   }

   public X509CertStoreSelector getReverseSelector() {
      return this.reverseSelector;
   }
}
