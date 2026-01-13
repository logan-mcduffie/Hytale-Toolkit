package org.bouncycastle.crypto.ec;

import org.bouncycastle.crypto.CipherParameters;

public interface ECPairTransform {
   void init(CipherParameters var1);

   ECPair transform(ECPair var1);
}
