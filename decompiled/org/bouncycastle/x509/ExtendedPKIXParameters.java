package org.bouncycastle.x509;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

/** @deprecated */
public class ExtendedPKIXParameters extends PKIXParameters {
   private List stores;
   private Selector selector;
   private boolean additionalLocationsEnabled;
   private List additionalStores;
   private Set trustedACIssuers;
   private Set necessaryACAttributes;
   private Set prohibitedACAttributes;
   private Set attrCertCheckers;
   public static final int PKIX_VALIDITY_MODEL = 0;
   public static final int CHAIN_VALIDITY_MODEL = 1;
   private int validityModel = 0;
   private boolean useDeltas = false;

   public ExtendedPKIXParameters(Set var1) throws InvalidAlgorithmParameterException {
      super(var1);
      this.stores = new ArrayList();
      this.additionalStores = new ArrayList();
      this.trustedACIssuers = new HashSet();
      this.necessaryACAttributes = new HashSet();
      this.prohibitedACAttributes = new HashSet();
      this.attrCertCheckers = new HashSet();
   }

   public static ExtendedPKIXParameters getInstance(PKIXParameters var0) {
      ExtendedPKIXParameters var1;
      try {
         var1 = new ExtendedPKIXParameters(var0.getTrustAnchors());
      } catch (Exception var3) {
         throw new RuntimeException(var3.getMessage());
      }

      var1.setParams(var0);
      return var1;
   }

   protected void setParams(PKIXParameters var1) {
      this.setDate(var1.getDate());
      this.setCertPathCheckers(var1.getCertPathCheckers());
      this.setCertStores(var1.getCertStores());
      this.setAnyPolicyInhibited(var1.isAnyPolicyInhibited());
      this.setExplicitPolicyRequired(var1.isExplicitPolicyRequired());
      this.setPolicyMappingInhibited(var1.isPolicyMappingInhibited());
      this.setRevocationEnabled(var1.isRevocationEnabled());
      this.setInitialPolicies(var1.getInitialPolicies());
      this.setPolicyQualifiersRejected(var1.getPolicyQualifiersRejected());
      this.setSigProvider(var1.getSigProvider());
      this.setTargetCertConstraints(var1.getTargetCertConstraints());

      try {
         this.setTrustAnchors(var1.getTrustAnchors());
      } catch (Exception var3) {
         throw new RuntimeException(var3.getMessage());
      }

      if (var1 instanceof ExtendedPKIXParameters) {
         ExtendedPKIXParameters var2 = (ExtendedPKIXParameters)var1;
         this.validityModel = var2.validityModel;
         this.useDeltas = var2.useDeltas;
         this.additionalLocationsEnabled = var2.additionalLocationsEnabled;
         this.selector = var2.selector == null ? null : (Selector)var2.selector.clone();
         this.stores = new ArrayList(var2.stores);
         this.additionalStores = new ArrayList(var2.additionalStores);
         this.trustedACIssuers = new HashSet(var2.trustedACIssuers);
         this.prohibitedACAttributes = new HashSet(var2.prohibitedACAttributes);
         this.necessaryACAttributes = new HashSet(var2.necessaryACAttributes);
         this.attrCertCheckers = new HashSet(var2.attrCertCheckers);
      }
   }

   public boolean isUseDeltasEnabled() {
      return this.useDeltas;
   }

   public void setUseDeltasEnabled(boolean var1) {
      this.useDeltas = var1;
   }

   public int getValidityModel() {
      return this.validityModel;
   }

   @Override
   public void setCertStores(List var1) {
      if (var1 != null) {
         Iterator var2 = var1.iterator();

         while (var2.hasNext()) {
            this.addCertStore((CertStore)var2.next());
         }
      }
   }

   public void setStores(List var1) {
      if (var1 == null) {
         this.stores = new ArrayList();
      } else {
         Iterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!(var2.next() instanceof Store)) {
               throw new ClassCastException("All elements of list must be of type org.bouncycastle.util.Store.");
            }
         }

         this.stores = new ArrayList(var1);
      }
   }

   public void addStore(Store var1) {
      if (var1 != null) {
         this.stores.add(var1);
      }
   }

   /** @deprecated */
   public void addAdditionalStore(Store var1) {
      if (var1 != null) {
         this.additionalStores.add(var1);
      }
   }

   /** @deprecated */
   public void addAddionalStore(Store var1) {
      this.addAdditionalStore(var1);
   }

   public List getAdditionalStores() {
      return Collections.unmodifiableList(this.additionalStores);
   }

   public List getStores() {
      return Collections.unmodifiableList(new ArrayList(this.stores));
   }

   public void setValidityModel(int var1) {
      this.validityModel = var1;
   }

   @Override
   public Object clone() {
      ExtendedPKIXParameters var1;
      try {
         var1 = new ExtendedPKIXParameters(this.getTrustAnchors());
      } catch (Exception var3) {
         throw new RuntimeException(var3.getMessage());
      }

      var1.setParams(this);
      return var1;
   }

   public boolean isAdditionalLocationsEnabled() {
      return this.additionalLocationsEnabled;
   }

   public void setAdditionalLocationsEnabled(boolean var1) {
      this.additionalLocationsEnabled = var1;
   }

   public Selector getTargetConstraints() {
      return this.selector != null ? (Selector)this.selector.clone() : null;
   }

   public void setTargetConstraints(Selector var1) {
      if (var1 != null) {
         this.selector = (Selector)var1.clone();
      } else {
         this.selector = null;
      }
   }

   @Override
   public void setTargetCertConstraints(CertSelector var1) {
      super.setTargetCertConstraints(var1);
      if (var1 != null) {
         this.selector = X509CertStoreSelector.getInstance((X509CertSelector)var1);
      } else {
         this.selector = null;
      }
   }

   public Set getTrustedACIssuers() {
      return Collections.unmodifiableSet(this.trustedACIssuers);
   }

   public void setTrustedACIssuers(Set var1) {
      if (var1 == null) {
         this.trustedACIssuers.clear();
      } else {
         Iterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!(var2.next() instanceof TrustAnchor)) {
               throw new ClassCastException("All elements of set must be of type " + TrustAnchor.class.getName() + ".");
            }
         }

         this.trustedACIssuers.clear();
         this.trustedACIssuers.addAll(var1);
      }
   }

   public Set getNecessaryACAttributes() {
      return Collections.unmodifiableSet(this.necessaryACAttributes);
   }

   public void setNecessaryACAttributes(Set var1) {
      if (var1 == null) {
         this.necessaryACAttributes.clear();
      } else {
         Iterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!(var2.next() instanceof String)) {
               throw new ClassCastException("All elements of set must be of type String.");
            }
         }

         this.necessaryACAttributes.clear();
         this.necessaryACAttributes.addAll(var1);
      }
   }

   public Set getProhibitedACAttributes() {
      return Collections.unmodifiableSet(this.prohibitedACAttributes);
   }

   public void setProhibitedACAttributes(Set var1) {
      if (var1 == null) {
         this.prohibitedACAttributes.clear();
      } else {
         Iterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!(var2.next() instanceof String)) {
               throw new ClassCastException("All elements of set must be of type String.");
            }
         }

         this.prohibitedACAttributes.clear();
         this.prohibitedACAttributes.addAll(var1);
      }
   }

   public Set getAttrCertCheckers() {
      return Collections.unmodifiableSet(this.attrCertCheckers);
   }

   public void setAttrCertCheckers(Set var1) {
      if (var1 == null) {
         this.attrCertCheckers.clear();
      } else {
         Iterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!(var2.next() instanceof PKIXAttrCertChecker)) {
               throw new ClassCastException("All elements of set must be of type " + PKIXAttrCertChecker.class.getName() + ".");
            }
         }

         this.attrCertCheckers.clear();
         this.attrCertCheckers.addAll(var1);
      }
   }
}
