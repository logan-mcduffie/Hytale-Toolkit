package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.Digest;

public class KDF2BytesGenerator extends BaseKDFBytesGenerator {
   public KDF2BytesGenerator(Digest var1) {
      super(1, var1);
   }
}
