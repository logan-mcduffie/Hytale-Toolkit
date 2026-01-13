package org.bouncycastle.jcajce;

import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.X509Certificate;
import java.util.Date;

public class PKIXCertRevocationCheckerParameters {
   private final PKIXExtendedParameters paramsPKIX;
   private final Date validDate;
   private final CertPath certPath;
   private final int index;
   private final X509Certificate signingCert;
   private final PublicKey workingPublicKey;

   public PKIXCertRevocationCheckerParameters(PKIXExtendedParameters var1, Date var2, CertPath var3, int var4, X509Certificate var5, PublicKey var6) {
      this.paramsPKIX = var1;
      this.validDate = var2;
      this.certPath = var3;
      this.index = var4;
      this.signingCert = var5;
      this.workingPublicKey = var6;
   }

   public PKIXExtendedParameters getParamsPKIX() {
      return this.paramsPKIX;
   }

   public Date getValidDate() {
      return new Date(this.validDate.getTime());
   }

   public CertPath getCertPath() {
      return this.certPath;
   }

   public int getIndex() {
      return this.index;
   }

   public X509Certificate getSigningCert() {
      return this.signingCert;
   }

   public PublicKey getWorkingPublicKey() {
      return this.workingPublicKey;
   }
}
