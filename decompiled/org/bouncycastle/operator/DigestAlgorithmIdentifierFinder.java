package org.bouncycastle.operator;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface DigestAlgorithmIdentifierFinder {
   AlgorithmIdentifier find(AlgorithmIdentifier var1);

   AlgorithmIdentifier find(ASN1ObjectIdentifier var1);

   AlgorithmIdentifier find(String var1);
}
