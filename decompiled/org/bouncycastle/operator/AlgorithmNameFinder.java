package org.bouncycastle.operator;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface AlgorithmNameFinder {
   boolean hasAlgorithmName(ASN1ObjectIdentifier var1);

   String getAlgorithmName(ASN1ObjectIdentifier var1);

   String getAlgorithmName(AlgorithmIdentifier var1);
}
