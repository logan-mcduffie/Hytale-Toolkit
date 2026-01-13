package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface KeyUnwrapper {
   AlgorithmIdentifier getAlgorithmIdentifier();

   GenericKey generateUnwrappedKey(AlgorithmIdentifier var1, byte[] var2) throws OperatorException;
}
