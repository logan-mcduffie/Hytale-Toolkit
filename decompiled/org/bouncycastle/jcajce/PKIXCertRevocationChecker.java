package org.bouncycastle.jcajce;

import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;

public interface PKIXCertRevocationChecker {
   void setParameter(String var1, Object var2);

   void initialize(PKIXCertRevocationCheckerParameters var1) throws CertPathValidatorException;

   void check(Certificate var1) throws CertPathValidatorException;
}
