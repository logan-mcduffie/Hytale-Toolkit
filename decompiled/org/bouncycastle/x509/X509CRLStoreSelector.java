package org.bouncycastle.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CRL;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

public class X509CRLStoreSelector extends X509CRLSelector implements Selector {
   private boolean deltaCRLIndicator = false;
   private boolean completeCRLEnabled = false;
   private BigInteger maxBaseCRLNumber = null;
   private byte[] issuingDistributionPoint = null;
   private boolean issuingDistributionPointEnabled = false;
   private X509AttributeCertificate attrCertChecking;

   public boolean isIssuingDistributionPointEnabled() {
      return this.issuingDistributionPointEnabled;
   }

   public void setIssuingDistributionPointEnabled(boolean var1) {
      this.issuingDistributionPointEnabled = var1;
   }

   public void setAttrCertificateChecking(X509AttributeCertificate var1) {
      this.attrCertChecking = var1;
   }

   public X509AttributeCertificate getAttrCertificateChecking() {
      return this.attrCertChecking;
   }

   @Override
   public boolean match(Object var1) {
      if (!(var1 instanceof X509CRL)) {
         return false;
      } else {
         X509CRL var2 = (X509CRL)var1;
         ASN1Integer var3 = null;

         try {
            byte[] var4 = var2.getExtensionValue(Extension.deltaCRLIndicator.getId());
            if (var4 != null) {
               var3 = ASN1Integer.getInstance(X509ExtensionUtil.fromExtensionValue(var4));
            }
         } catch (Exception var5) {
            return false;
         }

         if (this.isDeltaCRLIndicatorEnabled() && var3 == null) {
            return false;
         } else if (this.isCompleteCRLEnabled() && var3 != null) {
            return false;
         } else if (var3 != null && this.maxBaseCRLNumber != null && var3.getPositiveValue().compareTo(this.maxBaseCRLNumber) == 1) {
            return false;
         } else {
            if (this.issuingDistributionPointEnabled) {
               byte[] var6 = var2.getExtensionValue(Extension.issuingDistributionPoint.getId());
               if (this.issuingDistributionPoint == null) {
                  if (var6 != null) {
                     return false;
                  }
               } else if (!Arrays.areEqual(var6, this.issuingDistributionPoint)) {
                  return false;
               }
            }

            return super.match((X509CRL)var1);
         }
      }
   }

   @Override
   public boolean match(CRL var1) {
      return this.match((Object)var1);
   }

   public boolean isDeltaCRLIndicatorEnabled() {
      return this.deltaCRLIndicator;
   }

   public void setDeltaCRLIndicatorEnabled(boolean var1) {
      this.deltaCRLIndicator = var1;
   }

   public static X509CRLStoreSelector getInstance(X509CRLSelector var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("cannot create from null selector");
      } else {
         X509CRLStoreSelector var1 = new X509CRLStoreSelector();
         var1.setCertificateChecking(var0.getCertificateChecking());
         var1.setDateAndTime(var0.getDateAndTime());

         try {
            var1.setIssuerNames(var0.getIssuerNames());
         } catch (IOException var3) {
            throw new IllegalArgumentException(var3.getMessage());
         }

         var1.setIssuers(var0.getIssuers());
         var1.setMaxCRLNumber(var0.getMaxCRL());
         var1.setMinCRLNumber(var0.getMinCRL());
         return var1;
      }
   }

   @Override
   public Object clone() {
      X509CRLStoreSelector var1 = getInstance(this);
      var1.deltaCRLIndicator = this.deltaCRLIndicator;
      var1.completeCRLEnabled = this.completeCRLEnabled;
      var1.maxBaseCRLNumber = this.maxBaseCRLNumber;
      var1.attrCertChecking = this.attrCertChecking;
      var1.issuingDistributionPointEnabled = this.issuingDistributionPointEnabled;
      var1.issuingDistributionPoint = Arrays.clone(this.issuingDistributionPoint);
      return var1;
   }

   public boolean isCompleteCRLEnabled() {
      return this.completeCRLEnabled;
   }

   public void setCompleteCRLEnabled(boolean var1) {
      this.completeCRLEnabled = var1;
   }

   public BigInteger getMaxBaseCRLNumber() {
      return this.maxBaseCRLNumber;
   }

   public void setMaxBaseCRLNumber(BigInteger var1) {
      this.maxBaseCRLNumber = var1;
   }

   public byte[] getIssuingDistributionPoint() {
      return Arrays.clone(this.issuingDistributionPoint);
   }

   public void setIssuingDistributionPoint(byte[] var1) {
      this.issuingDistributionPoint = Arrays.clone(var1);
   }
}
