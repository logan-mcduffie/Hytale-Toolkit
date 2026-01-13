package org.bouncycastle.pkix.jcajce;

import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CRL;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Iterable;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

public class X509RevocationChecker extends PKIXCertPathChecker {
   public static final int PKIX_VALIDITY_MODEL = 0;
   public static final int CHAIN_VALIDITY_MODEL = 1;
   private static Logger LOG = Logger.getLogger(X509RevocationChecker.class.getName());
   private final Map<X500Principal, Long> failures = new HashMap<>();
   private final Set<TrustAnchor> trustAnchors;
   private final boolean isCheckEEOnly;
   private final int validityModel;
   private final List<Store<CRL>> crls;
   private final List<CertStore> crlCertStores;
   private final JcaJceHelper helper;
   private final boolean canSoftFail;
   private final long failLogMaxTime;
   private final long failHardMaxTime;
   private final Date validationDate;
   private Date currentDate;
   private X500Principal workingIssuerName;
   private PublicKey workingPublicKey;
   private X509Certificate signingCert;
   protected static final String[] crlReasons = new String[]{
      "unspecified",
      "keyCompromise",
      "cACompromise",
      "affiliationChanged",
      "superseded",
      "cessationOfOperation",
      "certificateHold",
      "unknown",
      "removeFromCRL",
      "privilegeWithdrawn",
      "aACompromise"
   };

   private X509RevocationChecker(X509RevocationChecker.Builder var1) {
      this.crls = new ArrayList<>(var1.crls);
      this.crlCertStores = new ArrayList<>(var1.crlCertStores);
      this.isCheckEEOnly = var1.isCheckEEOnly;
      this.validityModel = var1.validityModel;
      this.trustAnchors = var1.trustAnchors;
      this.canSoftFail = var1.canSoftFail;
      this.failLogMaxTime = var1.failLogMaxTime;
      this.failHardMaxTime = var1.failHardMaxTime;
      this.validationDate = var1.validityDate;
      if (var1.provider != null) {
         this.helper = new ProviderJcaJceHelper(var1.provider);
      } else if (var1.providerName != null) {
         this.helper = new NamedJcaJceHelper(var1.providerName);
      } else {
         this.helper = new DefaultJcaJceHelper();
      }
   }

   @Override
   public void init(boolean var1) throws CertPathValidatorException {
      if (var1) {
         throw new IllegalArgumentException("forward processing not supported");
      } else {
         this.currentDate = new Date();
         this.workingIssuerName = null;
      }
   }

   @Override
   public boolean isForwardCheckingSupported() {
      return false;
   }

   @Override
   public Set<String> getSupportedExtensions() {
      return null;
   }

   @Override
   public void check(Certificate var1, Collection<String> var2) throws CertPathValidatorException {
      X509Certificate var3 = (X509Certificate)var1;
      if (this.isCheckEEOnly && var3.getBasicConstraints() != -1) {
         this.workingIssuerName = var3.getSubjectX500Principal();
         this.workingPublicKey = var3.getPublicKey();
         this.signingCert = var3;
      } else {
         TrustAnchor var4 = null;
         if (this.workingIssuerName == null) {
            this.workingIssuerName = var3.getIssuerX500Principal();

            for (TrustAnchor var6 : this.trustAnchors) {
               if (this.workingIssuerName.equals(var6.getCA()) || this.workingIssuerName.equals(var6.getTrustedCert().getSubjectX500Principal())) {
                  var4 = var6;
               }
            }

            if (var4 == null) {
               throw new CertPathValidatorException("no trust anchor found for " + this.workingIssuerName);
            }

            this.signingCert = var4.getTrustedCert();
            this.workingPublicKey = this.signingCert.getPublicKey();
         }

         ArrayList var20 = new ArrayList();

         PKIXExtendedParameters.Builder var21;
         try {
            PKIXParameters var7 = new PKIXParameters(this.trustAnchors);
            var7.setRevocationEnabled(false);
            var7.setDate(this.validationDate);

            for (int var8 = 0; var8 != this.crlCertStores.size(); var8++) {
               if (LOG.isLoggable(Level.INFO)) {
                  this.addIssuers(var20, this.crlCertStores.get(var8));
               }

               var7.addCertStore(this.crlCertStores.get(var8));
            }

            var21 = new PKIXExtendedParameters.Builder(var7);
            var21.setValidityModel(this.validityModel);
         } catch (GeneralSecurityException var19) {
            throw new RuntimeException("error setting up baseParams: " + var19.getMessage());
         }

         for (int var22 = 0; var22 != this.crls.size(); var22++) {
            if (LOG.isLoggable(Level.INFO)) {
               this.addIssuers(var20, this.crls.get(var22));
            }

            var21.addCRLStore(new X509RevocationChecker.LocalCRLStore(this.crls.get(var22)));
         }

         if (var20.isEmpty()) {
            LOG.log(Level.INFO, "configured with 0 pre-loaded CRLs");
         } else if (LOG.isLoggable(Level.FINE)) {
            for (int var23 = 0; var23 != var20.size(); var23++) {
               LOG.log(Level.FINE, "configuring with CRL for issuer \"" + var20.get(var23) + "\"");
            }
         } else {
            LOG.log(Level.INFO, "configured with " + var20.size() + " pre-loaded CRLs");
         }

         PKIXExtendedParameters var24 = var21.build();
         Date var26 = RevocationUtilities.getValidityDate(var24, this.validationDate);

         try {
            this.checkCRLs(var24, this.currentDate, var26, var3, this.signingCert, this.workingPublicKey, new ArrayList(), this.helper);
         } catch (AnnotatedException var17) {
            throw new CertPathValidatorException(var17.getMessage(), var17.getCause());
         } catch (CRLNotFoundException var18) {
            if (null == var3.getExtensionValue(Extension.cRLDistributionPoints.getId())) {
               throw var18;
            }

            Set var10;
            try {
               var10 = this.downloadCRLs(
                  var3.getIssuerX500Principal(), var26, RevocationUtilities.getExtensionValue(var3, Extension.cRLDistributionPoints), this.helper
               );
            } catch (AnnotatedException var16) {
               throw new CertPathValidatorException(var16.getMessage(), var16.getCause());
            }

            if (!var10.isEmpty()) {
               try {
                  var21.addCRLStore(new X509RevocationChecker.LocalCRLStore(new CollectionStore<>(var10)));
                  var24 = var21.build();
                  var26 = RevocationUtilities.getValidityDate(var24, this.validationDate);
                  this.checkCRLs(var24, this.currentDate, var26, var3, this.signingCert, this.workingPublicKey, new ArrayList(), this.helper);
               } catch (AnnotatedException var15) {
                  throw new CertPathValidatorException(var15.getMessage(), var15.getCause());
               }
            } else {
               if (!this.canSoftFail) {
                  throw var18;
               }

               X500Principal var11 = var3.getIssuerX500Principal();
               Long var12 = this.failures.get(var11);
               if (var12 != null) {
                  long var13 = System.currentTimeMillis() - var12;
                  if (this.failHardMaxTime != -1L && this.failHardMaxTime < var13) {
                     throw var18;
                  }

                  if (var13 < this.failLogMaxTime) {
                     LOG.log(Level.WARNING, "soft failing for issuer: \"" + var11 + "\"");
                  } else {
                     LOG.log(Level.SEVERE, "soft failing for issuer: \"" + var11 + "\"");
                  }
               } else {
                  this.failures.put(var11, System.currentTimeMillis());
               }
            }
         }

         this.signingCert = var3;
         this.workingPublicKey = var3.getPublicKey();
         this.workingIssuerName = var3.getSubjectX500Principal();
      }
   }

   private void addIssuers(final List<X500Principal> var1, CertStore var2) throws CertStoreException {
      var2.getCRLs(new X509CRLSelector() {
         @Override
         public boolean match(CRL var1x) {
            if (!(var1x instanceof X509CRL)) {
               return false;
            } else {
               var1.add(((X509CRL)var1x).getIssuerX500Principal());
               return false;
            }
         }
      });
   }

   private void addIssuers(final List<X500Principal> var1, Store<CRL> var2) {
      var2.getMatches(new Selector<CRL>() {
         public boolean match(CRL var1x) {
            if (!(var1x instanceof X509CRL)) {
               return false;
            } else {
               var1.add(((X509CRL)var1x).getIssuerX500Principal());
               return false;
            }
         }

         @Override
         public Object clone() {
            return this;
         }
      });
   }

   private Set<CRL> downloadCRLs(X500Principal var1, Date var2, ASN1Primitive var3, JcaJceHelper var4) {
      CRLDistPoint var5 = CRLDistPoint.getInstance(var3);
      DistributionPoint[] var6 = var5.getDistributionPoints();

      CertificateFactory var7;
      try {
         var7 = var4.createCertificateFactory("X.509");
      } catch (Exception var19) {
         if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "could not create certFact: " + var19.getMessage(), (Throwable)var19);
         } else {
            LOG.log(Level.INFO, "could not create certFact: " + var19.getMessage());
         }

         return null;
      }

      X509CRLSelector var8 = new X509CRLSelector();
      var8.addIssuer(var1);
      PKIXCRLStoreSelector var9 = new PKIXCRLStoreSelector.Builder(var8).build();
      HashSet var10 = new HashSet();

      for (int var11 = 0; var11 != var6.length; var11++) {
         DistributionPoint var12 = var6[var11];
         DistributionPointName var13 = var12.getDistributionPoint();
         if (var13 != null && var13.getType() == 0) {
            GeneralName[] var14 = GeneralNames.getInstance(var13.getName()).getNames();

            for (int var15 = 0; var15 != var14.length; var15++) {
               GeneralName var16 = var14[var15];
               if (var16.getTagNo() == 6) {
                  URI var17 = null;

                  try {
                     var17 = new URI(((ASN1String)var16.getName()).getString());
                     PKIXCRLStore var18 = CrlCache.getCrl(var7, this.validationDate, var17);
                     if (var18 != null) {
                        var10.addAll(PKIXCRLUtil.findCRLs(var9, var2, Collections.EMPTY_LIST, Collections.singletonList(var18)));
                     }
                  } catch (Exception var20) {
                     if (LOG.isLoggable(Level.FINE)) {
                        LOG.log(Level.FINE, "CrlDP " + var17 + " ignored: " + var20.getMessage(), (Throwable)var20);
                     } else {
                        LOG.log(Level.INFO, "CrlDP " + var17 + " ignored: " + var20.getMessage());
                     }
                  }
               }
            }
         }
      }

      return var10;
   }

   static List<PKIXCRLStore> getAdditionalStoresFromCRLDistributionPoint(CRLDistPoint var0, Map<GeneralName, PKIXCRLStore> var1) throws AnnotatedException {
      if (var0 == null) {
         return Collections.emptyList();
      } else {
         DistributionPoint[] var2;
         try {
            var2 = var0.getDistributionPoints();
         } catch (Exception var9) {
            throw new AnnotatedException("could not read distribution points could not be read", var9);
         }

         ArrayList var3 = new ArrayList();

         for (int var4 = 0; var4 < var2.length; var4++) {
            DistributionPointName var5 = var2[var4].getDistributionPoint();
            if (var5 != null && var5.getType() == 0) {
               GeneralName[] var6 = GeneralNames.getInstance(var5.getName()).getNames();

               for (int var7 = 0; var7 < var6.length; var7++) {
                  PKIXCRLStore var8 = (PKIXCRLStore)var1.get(var6[var7]);
                  if (var8 != null) {
                     var3.add(var8);
                  }
               }
            }
         }

         return var3;
      }
   }

   protected void checkCRLs(
      PKIXExtendedParameters var1, Date var2, Date var3, X509Certificate var4, X509Certificate var5, PublicKey var6, List var7, JcaJceHelper var8
   ) throws AnnotatedException, CertPathValidatorException {
      CRLDistPoint var9;
      try {
         var9 = CRLDistPoint.getInstance(RevocationUtilities.getExtensionValue(var4, Extension.cRLDistributionPoints));
      } catch (Exception var23) {
         throw new AnnotatedException("cannot read CRL distribution point extension", var23);
      }

      CertStatus var10 = new CertStatus();
      ReasonsMask var11 = new ReasonsMask();
      AnnotatedException var12 = null;
      boolean var13 = false;
      if (var9 != null) {
         DistributionPoint[] var14;
         try {
            var14 = var9.getDistributionPoints();
         } catch (Exception var22) {
            throw new AnnotatedException("cannot read distribution points", var22);
         }

         if (var14 != null) {
            PKIXExtendedParameters.Builder var15 = new PKIXExtendedParameters.Builder(var1);

            try {
               List var16 = getAdditionalStoresFromCRLDistributionPoint(var9, var1.getNamedCRLStoreMap());
               Iterator var17 = var16.iterator();

               while (var17.hasNext()) {
                  var15.addCRLStore((PKIXCRLStore)var17.next());
               }
            } catch (AnnotatedException var24) {
               throw new AnnotatedException("no additional CRL locations could be decoded from CRL distribution point extension", var24);
            }

            PKIXExtendedParameters var30 = var15.build();
            Date var32 = RevocationUtilities.getValidityDate(var30, var2);

            for (int var18 = 0; var18 < var14.length && var10.getCertStatus() == 11 && !var11.isAllReasons(); var18++) {
               try {
                  RFC3280CertPathUtilities.checkCRL(var14[var18], var30, var2, var32, var4, var5, var6, var10, var11, var7, var8);
                  var13 = true;
               } catch (AnnotatedException var21) {
                  var12 = var21;
               }
            }
         }
      }

      if (var10.getCertStatus() == 11 && !var11.isAllReasons()) {
         try {
            X500Principal var25 = var4.getIssuerX500Principal();
            DistributionPoint var27 = new DistributionPoint(
               new DistributionPointName(0, new GeneralNames(new GeneralName(4, X500Name.getInstance(var25.getEncoded())))), null, null
            );
            PKIXExtendedParameters var31 = (PKIXExtendedParameters)var1.clone();
            RFC3280CertPathUtilities.checkCRL(var27, var31, var2, var3, var4, var5, var6, var10, var11, var7, var8);
            var13 = true;
         } catch (AnnotatedException var20) {
            var12 = var20;
         }
      }

      if (!var13) {
         if (var12 instanceof AnnotatedException) {
            throw new CRLNotFoundException("no valid CRL found", var12);
         } else {
            throw new CRLNotFoundException("no valid CRL found");
         }
      } else if (var10.getCertStatus() != 11) {
         SimpleDateFormat var26 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
         var26.setTimeZone(TimeZone.getTimeZone("UTC"));
         String var28 = "certificate [issuer=\""
            + var4.getIssuerX500Principal()
            + "\",serialNumber="
            + var4.getSerialNumber()
            + ",subject=\""
            + var4.getSubjectX500Principal()
            + "\"] revoked after "
            + var26.format(var10.getRevocationDate());
         var28 = var28 + ", reason: " + crlReasons[var10.getCertStatus()];
         throw new AnnotatedException(var28);
      } else {
         if (!var11.isAllReasons() && var10.getCertStatus() == 11) {
            var10.setCertStatus(12);
         }

         if (var10.getCertStatus() == 12) {
            throw new AnnotatedException("certificate status could not be determined");
         }
      }
   }

   @Override
   public Object clone() {
      return this;
   }

   public static class Builder {
      private Set<TrustAnchor> trustAnchors;
      private List<CertStore> crlCertStores = new ArrayList<>();
      private List<Store<CRL>> crls = new ArrayList<>();
      private boolean isCheckEEOnly;
      private int validityModel = 0;
      private Provider provider;
      private String providerName;
      private boolean canSoftFail;
      private long failLogMaxTime;
      private long failHardMaxTime;
      private Date validityDate = new Date();

      public Builder(TrustAnchor var1) {
         this.trustAnchors = Collections.singleton(var1);
      }

      public Builder(Set<TrustAnchor> var1) {
         this.trustAnchors = new HashSet<>(var1);
      }

      public Builder(KeyStore var1) throws KeyStoreException {
         this.trustAnchors = new HashSet<>();
         Enumeration var2 = var1.aliases();

         while (var2.hasMoreElements()) {
            String var3 = (String)var2.nextElement();
            if (var1.isCertificateEntry(var3)) {
               this.trustAnchors.add(new TrustAnchor((X509Certificate)var1.getCertificate(var3), null));
            }
         }
      }

      public X509RevocationChecker.Builder addCrls(CertStore var1) {
         this.crlCertStores.add(var1);
         return this;
      }

      public X509RevocationChecker.Builder addCrls(Store<CRL> var1) {
         this.crls.add(var1);
         return this;
      }

      public X509RevocationChecker.Builder setDate(Date var1) {
         this.validityDate = new Date(var1.getTime());
         return this;
      }

      public X509RevocationChecker.Builder setCheckEndEntityOnly(boolean var1) {
         this.isCheckEEOnly = var1;
         return this;
      }

      public X509RevocationChecker.Builder setSoftFail(boolean var1, long var2) {
         this.canSoftFail = var1;
         this.failLogMaxTime = var2;
         this.failHardMaxTime = -1L;
         return this;
      }

      public X509RevocationChecker.Builder setSoftFailHardLimit(boolean var1, long var2) {
         this.canSoftFail = var1;
         this.failLogMaxTime = var2 * 3L / 4L;
         this.failHardMaxTime = var2;
         return this;
      }

      public X509RevocationChecker.Builder setValidityModel(int var1) {
         this.validityModel = var1;
         return this;
      }

      public X509RevocationChecker.Builder usingProvider(Provider var1) {
         this.provider = var1;
         return this;
      }

      public X509RevocationChecker.Builder usingProvider(String var1) {
         this.providerName = var1;
         return this;
      }

      public X509RevocationChecker build() {
         return new X509RevocationChecker(this);
      }
   }

   private static class LocalCRLStore implements PKIXCRLStore<CRL>, Iterable<CRL> {
      private Collection<CRL> _local;

      public LocalCRLStore(Store<CRL> var1) {
         this._local = new ArrayList<>(var1.getMatches(null));
      }

      @Override
      public Collection<CRL> getMatches(Selector<CRL> var1) {
         if (var1 == null) {
            return new ArrayList<>(this._local);
         } else {
            ArrayList var2 = new ArrayList();

            for (CRL var4 : this._local) {
               if (var1.match(var4)) {
                  var2.add(var4);
               }
            }

            return var2;
         }
      }

      @Override
      public Iterator<CRL> iterator() {
         return this.getMatches(null).iterator();
      }
   }
}
