package org.bouncycastle.crypto;

public interface AlphabetMapper {
   int getRadix();

   byte[] convertToIndexes(char[] var1);

   char[] convertToChars(byte[] var1);
}
