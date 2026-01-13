package org.bouncycastle.crypto.ec;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.math.ec.ECPoint;

public interface ECDecryptor {
   void init(CipherParameters var1);

   ECPoint decrypt(ECPair var1);
}
