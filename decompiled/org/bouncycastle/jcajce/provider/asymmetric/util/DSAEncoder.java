package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.io.IOException;
import java.math.BigInteger;

/** @deprecated */
public interface DSAEncoder {
   byte[] encode(BigInteger var1, BigInteger var2) throws IOException;

   BigInteger[] decode(byte[] var1) throws IOException;
}
