package org.bouncycastle.its.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.its.ITSCertificate;

public interface ITSContentSigner {
   OutputStream getOutputStream();

   byte[] getSignature();

   ITSCertificate getAssociatedCertificate();

   byte[] getAssociatedCertificateDigest();

   AlgorithmIdentifier getDigestAlgorithm();

   ASN1ObjectIdentifier getCurveID();

   boolean isForSelfSigning();
}
