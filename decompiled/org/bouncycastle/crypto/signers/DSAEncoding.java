package org.bouncycastle.crypto.signers;

import java.io.IOException;
import java.math.BigInteger;

public interface DSAEncoding {
   BigInteger[] decode(BigInteger var1, byte[] var2) throws IOException;

   byte[] encode(BigInteger var1, BigInteger var2, BigInteger var3) throws IOException;
}
