package org.bouncycastle.operator;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface SecretKeySizeProvider {
   int getKeySize(AlgorithmIdentifier var1);

   int getKeySize(ASN1ObjectIdentifier var1);
}
