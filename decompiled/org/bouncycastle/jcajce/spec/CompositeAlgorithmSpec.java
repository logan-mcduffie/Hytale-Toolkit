package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeAlgorithmSpec implements AlgorithmParameterSpec {
   private final List<String> algorithmNames;
   private final List<AlgorithmParameterSpec> parameterSpecs;

   public CompositeAlgorithmSpec(CompositeAlgorithmSpec.Builder var1) {
      this.algorithmNames = Collections.unmodifiableList(new ArrayList<>(var1.algorithmNames));
      this.parameterSpecs = Collections.unmodifiableList(new ArrayList<>(var1.parameterSpecs));
   }

   public List<String> getAlgorithmNames() {
      return this.algorithmNames;
   }

   public List<AlgorithmParameterSpec> getParameterSpecs() {
      return this.parameterSpecs;
   }

   public static class Builder {
      private List<String> algorithmNames = new ArrayList<>();
      private List<AlgorithmParameterSpec> parameterSpecs = new ArrayList<>();

      public CompositeAlgorithmSpec.Builder add(String var1) {
         return this.add(var1, null);
      }

      public CompositeAlgorithmSpec.Builder add(String var1, AlgorithmParameterSpec var2) {
         if (!this.algorithmNames.contains(var1)) {
            this.algorithmNames.add(var1);
            this.parameterSpecs.add(var2);
            return this;
         } else {
            throw new IllegalStateException("cannot build with the same algorithm name added");
         }
      }

      public CompositeAlgorithmSpec build() {
         if (this.algorithmNames.isEmpty()) {
            throw new IllegalStateException("cannot call build with no algorithm names added");
         } else {
            return new CompositeAlgorithmSpec(this);
         }
      }
   }
}
