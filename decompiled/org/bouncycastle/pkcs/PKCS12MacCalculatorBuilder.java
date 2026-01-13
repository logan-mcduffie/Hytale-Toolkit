package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;

public interface PKCS12MacCalculatorBuilder {
   MacCalculator build(char[] var1) throws OperatorCreationException;

   AlgorithmIdentifier getDigestAlgorithmIdentifier();
}
