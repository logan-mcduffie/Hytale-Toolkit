package org.bouncycastle.cert.path;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Memoable;

public interface CertPathValidation extends Memoable {
   void validate(CertPathValidationContext var1, X509CertificateHolder var2) throws CertPathValidationException;
}
