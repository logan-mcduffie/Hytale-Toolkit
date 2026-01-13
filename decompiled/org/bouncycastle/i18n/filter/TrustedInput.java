package org.bouncycastle.i18n.filter;

public class TrustedInput {
   protected Object input;

   public TrustedInput(Object var1) {
      this.input = var1;
   }

   public Object getInput() {
      return this.input;
   }

   @Override
   public String toString() {
      return this.input.toString();
   }
}
