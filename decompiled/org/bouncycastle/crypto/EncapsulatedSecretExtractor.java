package org.bouncycastle.crypto;

public interface EncapsulatedSecretExtractor {
   byte[] extractSecret(byte[] var1);

   int getEncapsulationLength();
}
