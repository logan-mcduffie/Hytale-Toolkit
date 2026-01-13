package org.bouncycastle.jcajce;

import java.math.BigInteger;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509Certificate;
import java.util.Collection;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

public class PKIXCRLStoreSelector<T extends CRL> implements Selector<T> {
   private final CRLSelector baseSelector;
   private final boolean deltaCRLIndicator;
   private final boolean completeCRLEnabled;
   private final BigInteger maxBaseCRLNumber;
   private final byte[] issuingDistributionPoint;
   private final boolean issuingDistributionPointEnabled;

   private PKIXCRLStoreSelector(PKIXCRLStoreSelector.Builder var1) {
      this.baseSelector = var1.baseSelector;
      this.deltaCRLIndicator = var1.deltaCRLIndicator;
      this.completeCRLEnabled = var1.completeCRLEnabled;
      this.maxBaseCRLNumber = var1.maxBaseCRLNumber;
      this.issuingDistributionPoint = var1.issuingDistributionPoint;
      this.issuingDistributionPointEnabled = var1.issuingDistributionPointEnabled;
   }

   public boolean isIssuingDistributionPointEnabled() {
      return this.issuingDistributionPointEnabled;
   }

   public boolean match(CRL var1) {
      if (!(var1 instanceof X509CRL)) {
         return this.baseSelector.match(var1);
      } else {
         X509CRL var2 = (X509CRL)var1;
         ASN1Integer var3 = null;

         try {
            byte[] var4 = var2.getExtensionValue(Extension.deltaCRLIndicator.getId());
            if (var4 != null) {
               var3 = ASN1Integer.getInstance(ASN1OctetString.getInstance(var4).getOctets());
            }
         } catch (Exception var5) {
            return false;
         }

         if (var3 == null) {
            if (this.isDeltaCRLIndicatorEnabled()) {
               return false;
            }
         } else {
            if (this.isCompleteCRLEnabled()) {
               return false;
            }

            if (this.maxBaseCRLNumber != null && var3.getPositiveValue().compareTo(this.maxBaseCRLNumber) == 1) {
               return false;
            }
         }

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

         return this.baseSelector.match(var1);
      }
   }

   public boolean isDeltaCRLIndicatorEnabled() {
      return this.deltaCRLIndicator;
   }

   @Override
   public Object clone() {
      return this;
   }

   public boolean isCompleteCRLEnabled() {
      return this.completeCRLEnabled;
   }

   public BigInteger getMaxBaseCRLNumber() {
      return this.maxBaseCRLNumber;
   }

   public byte[] getIssuingDistributionPoint() {
      return Arrays.clone(this.issuingDistributionPoint);
   }

   public X509Certificate getCertificateChecking() {
      return this.baseSelector instanceof X509CRLSelector ? ((X509CRLSelector)this.baseSelector).getCertificateChecking() : null;
   }

   public static Collection<? extends CRL> getCRLs(PKIXCRLStoreSelector var0, CertStore var1) throws CertStoreException {
      return var1.getCRLs(new PKIXCRLStoreSelector.SelectorClone(var0));
   }

   public static class Builder {
      private final CRLSelector baseSelector;
      private boolean deltaCRLIndicator = false;
      private boolean completeCRLEnabled = false;
      private BigInteger maxBaseCRLNumber = null;
      private byte[] issuingDistributionPoint = null;
      private boolean issuingDistributionPointEnabled = false;

      public Builder(CRLSelector var1) {
         this.baseSelector = (CRLSelector)var1.clone();
      }

      public PKIXCRLStoreSelector.Builder setCompleteCRLEnabled(boolean var1) {
         this.completeCRLEnabled = var1;
         return this;
      }

      public PKIXCRLStoreSelector.Builder setDeltaCRLIndicatorEnabled(boolean var1) {
         this.deltaCRLIndicator = var1;
         return this;
      }

      public void setMaxBaseCRLNumber(BigInteger var1) {
         this.maxBaseCRLNumber = var1;
      }

      public void setIssuingDistributionPointEnabled(boolean var1) {
         this.issuingDistributionPointEnabled = var1;
      }

      public void setIssuingDistributionPoint(byte[] var1) {
         this.issuingDistributionPoint = Arrays.clone(var1);
      }

      public PKIXCRLStoreSelector<? extends CRL> build() {
         return new PKIXCRLStoreSelector<>(this);
      }
   }

   private static class SelectorClone extends X509CRLSelector {
      private final PKIXCRLStoreSelector selector;

      SelectorClone(PKIXCRLStoreSelector var1) {
         this.selector = var1;
         if (var1.baseSelector instanceof X509CRLSelector) {
            X509CRLSelector var2 = (X509CRLSelector)var1.baseSelector;
            this.setCertificateChecking(var2.getCertificateChecking());
            this.setDateAndTime(var2.getDateAndTime());
            this.setIssuers(var2.getIssuers());
            this.setMinCRLNumber(var2.getMinCRL());
            this.setMaxCRLNumber(var2.getMaxCRL());
         }
      }

      @Override
      public boolean match(CRL var1) {
         return this.selector == null ? var1 != null : this.selector.match(var1);
      }
   }
}
