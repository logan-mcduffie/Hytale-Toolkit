package org.bouncycastle.jce.provider;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.bouncycastle.jce.X509LDAPCertStoreParameters;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.StoreException;
import org.bouncycastle.x509.X509AttributeCertStoreSelector;
import org.bouncycastle.x509.X509StoreParameters;
import org.bouncycastle.x509.X509StoreSpi;
import org.bouncycastle.x509.util.LDAPStoreHelper;

public class X509StoreLDAPAttrCerts extends X509StoreSpi {
   private LDAPStoreHelper helper;

   @Override
   public void engineInit(X509StoreParameters var1) {
      if (!(var1 instanceof X509LDAPCertStoreParameters)) {
         throw new IllegalArgumentException("Initialization parameters must be an instance of " + X509LDAPCertStoreParameters.class.getName() + ".");
      } else {
         this.helper = new LDAPStoreHelper((X509LDAPCertStoreParameters)var1);
      }
   }

   @Override
   public Collection engineGetMatches(Selector var1) throws StoreException {
      if (!(var1 instanceof X509AttributeCertStoreSelector)) {
         return Collections.EMPTY_SET;
      } else {
         X509AttributeCertStoreSelector var2 = (X509AttributeCertStoreSelector)var1;
         HashSet var3 = new HashSet();
         var3.addAll(this.helper.getAACertificates(var2));
         var3.addAll(this.helper.getAttributeCertificateAttributes(var2));
         var3.addAll(this.helper.getAttributeDescriptorCertificates(var2));
         return var3;
      }
   }
}
