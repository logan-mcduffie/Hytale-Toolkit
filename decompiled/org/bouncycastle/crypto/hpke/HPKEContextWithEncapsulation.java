package org.bouncycastle.crypto.hpke;

import org.bouncycastle.util.Arrays;

public class HPKEContextWithEncapsulation extends HPKEContext {
   final byte[] encapsulation;

   public HPKEContextWithEncapsulation(HPKEContext var1, byte[] var2) {
      super(var1.aead, var1.hkdf, var1.exporterSecret, var1.suiteId);
      this.encapsulation = var2;
   }

   public byte[] getEncapsulation() {
      return Arrays.clone(this.encapsulation);
   }
}
