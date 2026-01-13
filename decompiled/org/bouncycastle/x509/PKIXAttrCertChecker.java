package org.bouncycastle.x509;

import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.util.Collection;
import java.util.Set;

public abstract class PKIXAttrCertChecker implements Cloneable {
   public abstract Set getSupportedExtensions();

   public abstract void check(X509AttributeCertificate var1, CertPath var2, CertPath var3, Collection var4) throws CertPathValidatorException;

   @Override
   public abstract Object clone();
}
