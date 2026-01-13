package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Arrays;

public class ParametersWithContext implements CipherParameters {
   private CipherParameters parameters;
   private byte[] context;

   public ParametersWithContext(CipherParameters var1, byte[] var2) {
      if (var2 == null) {
         throw new NullPointerException("'context' cannot be null");
      } else {
         this.parameters = var1;
         this.context = Arrays.clone(var2);
      }
   }

   public void copyContextTo(byte[] var1, int var2, int var3) {
      if (this.context.length != var3) {
         throw new IllegalArgumentException("len");
      } else {
         System.arraycopy(this.context, 0, var1, var2, var3);
      }
   }

   public byte[] getContext() {
      return Arrays.clone(this.context);
   }

   public int getContextLength() {
      return this.context.length;
   }

   public CipherParameters getParameters() {
      return this.parameters;
   }
}
