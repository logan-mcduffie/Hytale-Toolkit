package org.bouncycastle.cert.path;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.util.Integers;

class CertPathValidationResultBuilder {
   private final CertPathValidationContext context;
   private final List<Integer> certIndexes = new ArrayList<>();
   private final List<Integer> ruleIndexes = new ArrayList<>();
   private final List<CertPathValidationException> exceptions = new ArrayList<>();

   CertPathValidationResultBuilder(CertPathValidationContext var1) {
      this.context = var1;
   }

   public CertPathValidationResult build() {
      return this.exceptions.isEmpty()
         ? new CertPathValidationResult(this.context)
         : new CertPathValidationResult(
            this.context,
            this.toInts(this.certIndexes),
            this.toInts(this.ruleIndexes),
            this.exceptions.toArray(new CertPathValidationException[this.exceptions.size()])
         );
   }

   public void addException(int var1, int var2, CertPathValidationException var3) {
      this.certIndexes.add(Integers.valueOf(var1));
      this.ruleIndexes.add(Integers.valueOf(var2));
      this.exceptions.add(var3);
   }

   private int[] toInts(List<Integer> var1) {
      int[] var2 = new int[var1.size()];

      for (int var3 = 0; var3 != var2.length; var3++) {
         var2[var3] = (Integer)var1.get(var3);
      }

      return var2;
   }
}
