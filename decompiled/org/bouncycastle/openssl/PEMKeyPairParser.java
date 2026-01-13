package org.bouncycastle.openssl;

import java.io.IOException;

interface PEMKeyPairParser {
   PEMKeyPair parse(byte[] var1) throws IOException;
}
