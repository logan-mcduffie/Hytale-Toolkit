package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

class ChaCha20 {
   static void process(byte[] var0, byte[] var1, byte[] var2, int var3, int var4) {
      ChaChaEngine var5 = new ChaChaEngine(20);
      var5.init(true, new ParametersWithIV(new KeyParameter(var0), var1));
      var5.processBytes(var2, var3, var4, var2, var3);
   }
}
