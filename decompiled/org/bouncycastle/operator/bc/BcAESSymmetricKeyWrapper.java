package org.bouncycastle.operator.bc;

import org.bouncycastle.crypto.engines.AESWrapEngine;
import org.bouncycastle.crypto.params.KeyParameter;

public class BcAESSymmetricKeyWrapper extends BcSymmetricKeyWrapper {
   public BcAESSymmetricKeyWrapper(KeyParameter var1) {
      super(AESUtil.determineKeyEncAlg(var1), new AESWrapEngine(), var1);
   }
}
