package org.bouncycastle.pkix.util.filter;

public class UntrustedInput {
   protected Object input;

   public UntrustedInput(Object var1) {
      this.input = var1;
   }

   public Object getInput() {
      return this.input;
   }

   public String getString() {
      return this.input.toString();
   }

   @Override
   public String toString() {
      return this.input.toString();
   }
}
