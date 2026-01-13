package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface DigestCalculatorProvider {
   DigestCalculator get(AlgorithmIdentifier var1) throws OperatorCreationException;
}
