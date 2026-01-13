package org.bouncycastle.jcajce;

import java.security.cert.CertPathParameters;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.x509.GeneralName;

public class PKIXExtendedParameters implements CertPathParameters {
   public static final int PKIX_VALIDITY_MODEL = 0;
   public static final int CHAIN_VALIDITY_MODEL = 1;
   private final PKIXParameters baseParameters;
   private final PKIXCertStoreSelector targetConstraints;
   private final Date validityDate;
   private final Date date;
   private final List<PKIXCertStore> extraCertStores;
   private final Map<GeneralName, PKIXCertStore> namedCertificateStoreMap;
   private final List<PKIXCRLStore> extraCRLStores;
   private final Map<GeneralName, PKIXCRLStore> namedCRLStoreMap;
   private final boolean revocationEnabled;
   private final boolean useDeltas;
   private final int validityModel;
   private final Set<TrustAnchor> trustAnchors;

   private PKIXExtendedParameters(PKIXExtendedParameters.Builder var1) {
      this.baseParameters = var1.baseParameters;
      this.validityDate = var1.validityDate;
      this.date = var1.date;
      this.extraCertStores = Collections.unmodifiableList(var1.extraCertStores);
      this.namedCertificateStoreMap = Collections.unmodifiableMap(new HashMap<>(var1.namedCertificateStoreMap));
      this.extraCRLStores = Collections.unmodifiableList(var1.extraCRLStores);
      this.namedCRLStoreMap = Collections.unmodifiableMap(new HashMap<>(var1.namedCRLStoreMap));
      this.targetConstraints = var1.targetConstraints;
      this.revocationEnabled = var1.revocationEnabled;
      this.useDeltas = var1.useDeltas;
      this.validityModel = var1.validityModel;
      this.trustAnchors = Collections.unmodifiableSet(var1.trustAnchors);
   }

   public List<PKIXCertStore> getCertificateStores() {
      return this.extraCertStores;
   }

   public Map<GeneralName, PKIXCertStore> getNamedCertificateStoreMap() {
      return this.namedCertificateStoreMap;
   }

   public List<PKIXCRLStore> getCRLStores() {
      return this.extraCRLStores;
   }

   public Map<GeneralName, PKIXCRLStore> getNamedCRLStoreMap() {
      return this.namedCRLStoreMap;
   }

   public Date getValidityDate() {
      return null == this.validityDate ? null : new Date(this.validityDate.getTime());
   }

   /** @deprecated */
   public Date getDate() {
      return new Date(this.date.getTime());
   }

   public boolean isUseDeltasEnabled() {
      return this.useDeltas;
   }

   public int getValidityModel() {
      return this.validityModel;
   }

   @Override
   public Object clone() {
      return this;
   }

   public PKIXCertStoreSelector getTargetConstraints() {
      return this.targetConstraints;
   }

   public Set getTrustAnchors() {
      return this.trustAnchors;
   }

   public Set getInitialPolicies() {
      return this.baseParameters.getInitialPolicies();
   }

   public String getSigProvider() {
      return this.baseParameters.getSigProvider();
   }

   public boolean isExplicitPolicyRequired() {
      return this.baseParameters.isExplicitPolicyRequired();
   }

   public boolean isAnyPolicyInhibited() {
      return this.baseParameters.isAnyPolicyInhibited();
   }

   public boolean isPolicyMappingInhibited() {
      return this.baseParameters.isPolicyMappingInhibited();
   }

   public List getCertPathCheckers() {
      return this.baseParameters.getCertPathCheckers();
   }

   public List<CertStore> getCertStores() {
      return this.baseParameters.getCertStores();
   }

   public boolean isRevocationEnabled() {
      return this.revocationEnabled;
   }

   public boolean getPolicyQualifiersRejected() {
      return this.baseParameters.getPolicyQualifiersRejected();
   }

   public static class Builder {
      private final PKIXParameters baseParameters;
      private final Date validityDate;
      private final Date date;
      private PKIXCertStoreSelector targetConstraints;
      private List<PKIXCertStore> extraCertStores = new ArrayList<>();
      private Map<GeneralName, PKIXCertStore> namedCertificateStoreMap = new HashMap<>();
      private List<PKIXCRLStore> extraCRLStores = new ArrayList<>();
      private Map<GeneralName, PKIXCRLStore> namedCRLStoreMap = new HashMap<>();
      private boolean revocationEnabled;
      private int validityModel = 0;
      private boolean useDeltas = false;
      private Set<TrustAnchor> trustAnchors;

      public Builder(PKIXParameters var1) {
         this.baseParameters = (PKIXParameters)var1.clone();
         CertSelector var2 = var1.getTargetCertConstraints();
         if (var2 != null) {
            this.targetConstraints = new PKIXCertStoreSelector.Builder(var2).build();
         }

         this.validityDate = var1.getDate();
         this.date = this.validityDate == null ? new Date() : this.validityDate;
         this.revocationEnabled = var1.isRevocationEnabled();
         this.trustAnchors = var1.getTrustAnchors();
      }

      public Builder(PKIXExtendedParameters var1) {
         this.baseParameters = var1.baseParameters;
         this.validityDate = var1.validityDate;
         this.date = var1.date;
         this.targetConstraints = var1.targetConstraints;
         this.extraCertStores = new ArrayList<>(var1.extraCertStores);
         this.namedCertificateStoreMap = new HashMap<>(var1.namedCertificateStoreMap);
         this.extraCRLStores = new ArrayList<>(var1.extraCRLStores);
         this.namedCRLStoreMap = new HashMap<>(var1.namedCRLStoreMap);
         this.useDeltas = var1.useDeltas;
         this.validityModel = var1.validityModel;
         this.revocationEnabled = var1.isRevocationEnabled();
         this.trustAnchors = var1.getTrustAnchors();
      }

      public PKIXExtendedParameters.Builder addCertificateStore(PKIXCertStore var1) {
         this.extraCertStores.add(var1);
         return this;
      }

      public PKIXExtendedParameters.Builder addNamedCertificateStore(GeneralName var1, PKIXCertStore var2) {
         this.namedCertificateStoreMap.put(var1, var2);
         return this;
      }

      public PKIXExtendedParameters.Builder addCRLStore(PKIXCRLStore var1) {
         this.extraCRLStores.add(var1);
         return this;
      }

      public PKIXExtendedParameters.Builder addNamedCRLStore(GeneralName var1, PKIXCRLStore var2) {
         this.namedCRLStoreMap.put(var1, var2);
         return this;
      }

      public PKIXExtendedParameters.Builder setTargetConstraints(PKIXCertStoreSelector var1) {
         this.targetConstraints = var1;
         return this;
      }

      public PKIXExtendedParameters.Builder setUseDeltasEnabled(boolean var1) {
         this.useDeltas = var1;
         return this;
      }

      public PKIXExtendedParameters.Builder setValidityModel(int var1) {
         this.validityModel = var1;
         return this;
      }

      public PKIXExtendedParameters.Builder setTrustAnchor(TrustAnchor var1) {
         this.trustAnchors = Collections.singleton(var1);
         return this;
      }

      public PKIXExtendedParameters.Builder setTrustAnchors(Set<TrustAnchor> var1) {
         this.trustAnchors = var1;
         return this;
      }

      public void setRevocationEnabled(boolean var1) {
         this.revocationEnabled = var1;
      }

      public PKIXExtendedParameters build() {
         return new PKIXExtendedParameters(this);
      }
   }
}
