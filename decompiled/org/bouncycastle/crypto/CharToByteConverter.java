package org.bouncycastle.crypto;

public interface CharToByteConverter {
   String getType();

   byte[] convert(char[] var1);
}
