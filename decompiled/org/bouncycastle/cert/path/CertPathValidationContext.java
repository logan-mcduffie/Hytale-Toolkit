package org.bouncycastle.cert.path;

import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.util.Memoable;

public class CertPathValidationContext implements Memoable {
   private Set criticalExtensions;
   private Set handledExtensions = new HashSet();
   private boolean endEntity;
   private int index;

   public CertPathValidationContext(Set var1) {
      this.criticalExtensions = var1;
   }

   public void addHandledExtension(ASN1ObjectIdentifier var1) {
      this.handledExtensions.add(var1);
   }

   public void setIsEndEntity(boolean var1) {
      this.endEntity = var1;
   }

   public Set getUnhandledCriticalExtensionOIDs() {
      HashSet var1 = new HashSet(this.criticalExtensions);
      var1.removeAll(this.handledExtensions);
      return var1;
   }

   public boolean isEndEntity() {
      return this.endEntity;
   }

   @Override
   public Memoable copy() {
      CertPathValidationContext var1 = new CertPathValidationContext(new HashSet(this.criticalExtensions));
      var1.handledExtensions = new HashSet(this.handledExtensions);
      var1.endEntity = this.endEntity;
      var1.index = this.index;
      return var1;
   }

   @Override
   public void reset(Memoable var1) {
      CertPathValidationContext var2 = (CertPathValidationContext)var1;
      this.criticalExtensions = new HashSet(var2.criticalExtensions);
      this.handledExtensions = new HashSet(var2.handledExtensions);
      this.endEntity = var2.endEntity;
      this.index = var2.index;
   }
}
