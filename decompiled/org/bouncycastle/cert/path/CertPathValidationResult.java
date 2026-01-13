package org.bouncycastle.cert.path;

import java.util.Collections;
import java.util.Set;
import org.bouncycastle.util.Arrays;

public class CertPathValidationResult {
   private final boolean isValid;
   private final CertPathValidationException cause;
   private final Set unhandledCriticalExtensionOIDs;
   private final int certIndex;
   private final int ruleIndex;
   private CertPathValidationException[] causes;
   private int[] certIndexes;
   private int[] ruleIndexes;

   public CertPathValidationResult(CertPathValidationContext var1) {
      this.unhandledCriticalExtensionOIDs = Collections.unmodifiableSet(var1.getUnhandledCriticalExtensionOIDs());
      this.isValid = this.unhandledCriticalExtensionOIDs.isEmpty();
      this.certIndex = -1;
      this.ruleIndex = -1;
      this.cause = null;
   }

   public CertPathValidationResult(CertPathValidationContext var1, int var2, int var3, CertPathValidationException var4) {
      this.unhandledCriticalExtensionOIDs = Collections.unmodifiableSet(var1.getUnhandledCriticalExtensionOIDs());
      this.isValid = false;
      this.certIndex = var2;
      this.ruleIndex = var3;
      this.cause = var4;
   }

   public CertPathValidationResult(CertPathValidationContext var1, int[] var2, int[] var3, CertPathValidationException[] var4) {
      this.unhandledCriticalExtensionOIDs = Collections.unmodifiableSet(var1.getUnhandledCriticalExtensionOIDs());
      this.isValid = false;
      this.cause = var4[0];
      this.certIndex = var2[0];
      this.ruleIndex = var3[0];
      this.causes = var4;
      this.certIndexes = var2;
      this.ruleIndexes = var3;
   }

   public boolean isValid() {
      return this.isValid;
   }

   public CertPathValidationException getCause() {
      if (this.cause != null) {
         return this.cause;
      } else {
         return !this.unhandledCriticalExtensionOIDs.isEmpty() ? new CertPathValidationException("Unhandled Critical Extensions") : null;
      }
   }

   public int getFailingCertIndex() {
      return this.certIndex;
   }

   public int getFailingRuleIndex() {
      return this.ruleIndex;
   }

   public Set getUnhandledCriticalExtensionOIDs() {
      return this.unhandledCriticalExtensionOIDs;
   }

   public boolean isDetailed() {
      return this.certIndexes != null;
   }

   public CertPathValidationException[] getCauses() {
      if (this.causes != null) {
         CertPathValidationException[] var1 = new CertPathValidationException[this.causes.length];
         System.arraycopy(this.causes, 0, var1, 0, this.causes.length);
         return var1;
      } else {
         return !this.unhandledCriticalExtensionOIDs.isEmpty()
            ? new CertPathValidationException[]{new CertPathValidationException("Unhandled Critical Extensions")}
            : null;
      }
   }

   public int[] getFailingCertIndexes() {
      return Arrays.clone(this.certIndexes);
   }

   public int[] getFailingRuleIndexes() {
      return Arrays.clone(this.ruleIndexes);
   }
}
