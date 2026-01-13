package org.bouncycastle.x509;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXParameters;
import java.security.cert.X509CertSelector;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.util.Selector;

/** @deprecated */
public class ExtendedPKIXBuilderParameters extends ExtendedPKIXParameters {
   private int maxPathLength = 5;
   private Set excludedCerts = Collections.EMPTY_SET;

   public Set getExcludedCerts() {
      return Collections.unmodifiableSet(this.excludedCerts);
   }

   public void setExcludedCerts(Set var1) {
      if (var1 == null) {
         var1 = Collections.EMPTY_SET;
      } else {
         this.excludedCerts = new HashSet(var1);
      }
   }

   public ExtendedPKIXBuilderParameters(Set var1, Selector var2) throws InvalidAlgorithmParameterException {
      super(var1);
      this.setTargetConstraints(var2);
   }

   public void setMaxPathLength(int var1) {
      if (var1 < -1) {
         throw new InvalidParameterException("The maximum path length parameter can not be less than -1.");
      } else {
         this.maxPathLength = var1;
      }
   }

   public int getMaxPathLength() {
      return this.maxPathLength;
   }

   @Override
   protected void setParams(PKIXParameters var1) {
      super.setParams(var1);
      if (var1 instanceof ExtendedPKIXBuilderParameters) {
         ExtendedPKIXBuilderParameters var2 = (ExtendedPKIXBuilderParameters)var1;
         this.maxPathLength = var2.maxPathLength;
         this.excludedCerts = new HashSet(var2.excludedCerts);
      }

      if (var1 instanceof PKIXBuilderParameters) {
         PKIXBuilderParameters var3 = (PKIXBuilderParameters)var1;
         this.maxPathLength = var3.getMaxPathLength();
      }
   }

   @Override
   public Object clone() {
      ExtendedPKIXBuilderParameters var1 = null;

      try {
         var1 = new ExtendedPKIXBuilderParameters(this.getTrustAnchors(), this.getTargetConstraints());
      } catch (Exception var3) {
         throw new RuntimeException(var3.getMessage());
      }

      var1.setParams(this);
      return var1;
   }

   public static ExtendedPKIXParameters getInstance(PKIXParameters var0) {
      ExtendedPKIXBuilderParameters var1;
      try {
         var1 = new ExtendedPKIXBuilderParameters(var0.getTrustAnchors(), X509CertStoreSelector.getInstance((X509CertSelector)var0.getTargetCertConstraints()));
      } catch (Exception var3) {
         throw new RuntimeException(var3.getMessage());
      }

      var1.setParams(var0);
      return var1;
   }
}
