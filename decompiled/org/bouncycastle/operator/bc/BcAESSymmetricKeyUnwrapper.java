package org.bouncycastle.operator.bc;

import org.bouncycastle.crypto.engines.AESWrapEngine;
import org.bouncycastle.crypto.params.KeyParameter;

public class BcAESSymmetricKeyUnwrapper extends BcSymmetricKeyUnwrapper {
   public BcAESSymmetricKeyUnwrapper(KeyParameter var1) {
      super(AESUtil.determineKeyEncAlg(var1), new AESWrapEngine(), var1);
   }
}
