package org.bouncycastle.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface DigestCalculator {
   AlgorithmIdentifier SHA_256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
   AlgorithmIdentifier SHA_512 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512);

   AlgorithmIdentifier getAlgorithmIdentifier();

   OutputStream getOutputStream();

   byte[] getDigest();
}
