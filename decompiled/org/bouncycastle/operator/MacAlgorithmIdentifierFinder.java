package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface MacAlgorithmIdentifierFinder {
   AlgorithmIdentifier find(String var1);
}
