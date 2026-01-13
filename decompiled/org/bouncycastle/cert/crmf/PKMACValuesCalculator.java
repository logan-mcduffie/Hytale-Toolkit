package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface PKMACValuesCalculator {
   void setup(AlgorithmIdentifier var1, AlgorithmIdentifier var2) throws CRMFException;

   byte[] calculateDigest(byte[] var1) throws CRMFException;

   byte[] calculateMac(byte[] var1, byte[] var2) throws CRMFException;
}
