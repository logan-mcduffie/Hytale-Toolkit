package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.InputDecryptor;

public interface ValueDecryptorGenerator {
   InputDecryptor getValueDecryptor(AlgorithmIdentifier var1, AlgorithmIdentifier var2, byte[] var3) throws CRMFException;
}
