package org.bouncycastle.jce;

import java.security.cert.CertStoreParameters;
import java.security.cert.LDAPCertStoreParameters;
import org.bouncycastle.x509.X509StoreParameters;

public class X509LDAPCertStoreParameters implements X509StoreParameters, CertStoreParameters {
   private String ldapURL;
   private String baseDN;
   private String userCertificateAttribute;
   private String cACertificateAttribute;
   private String crossCertificateAttribute;
   private String certificateRevocationListAttribute;
   private String deltaRevocationListAttribute;
   private String authorityRevocationListAttribute;
   private String attributeCertificateAttributeAttribute;
   private String aACertificateAttribute;
   private String attributeDescriptorCertificateAttribute;
   private String attributeCertificateRevocationListAttribute;
   private String attributeAuthorityRevocationListAttribute;
   private String ldapUserCertificateAttributeName;
   private String ldapCACertificateAttributeName;
   private String ldapCrossCertificateAttributeName;
   private String ldapCertificateRevocationListAttributeName;
   private String ldapDeltaRevocationListAttributeName;
   private String ldapAuthorityRevocationListAttributeName;
   private String ldapAttributeCertificateAttributeAttributeName;
   private String ldapAACertificateAttributeName;
   private String ldapAttributeDescriptorCertificateAttributeName;
   private String ldapAttributeCertificateRevocationListAttributeName;
   private String ldapAttributeAuthorityRevocationListAttributeName;
   private String userCertificateSubjectAttributeName;
   private String cACertificateSubjectAttributeName;
   private String crossCertificateSubjectAttributeName;
   private String certificateRevocationListIssuerAttributeName;
   private String deltaRevocationListIssuerAttributeName;
   private String authorityRevocationListIssuerAttributeName;
   private String attributeCertificateAttributeSubjectAttributeName;
   private String aACertificateSubjectAttributeName;
   private String attributeDescriptorCertificateSubjectAttributeName;
   private String attributeCertificateRevocationListIssuerAttributeName;
   private String attributeAuthorityRevocationListIssuerAttributeName;
   private String searchForSerialNumberIn;

   private X509LDAPCertStoreParameters(X509LDAPCertStoreParameters.Builder var1) {
      this.ldapURL = var1.ldapURL;
      this.baseDN = var1.baseDN;
      this.userCertificateAttribute = var1.userCertificateAttribute;
      this.cACertificateAttribute = var1.cACertificateAttribute;
      this.crossCertificateAttribute = var1.crossCertificateAttribute;
      this.certificateRevocationListAttribute = var1.certificateRevocationListAttribute;
      this.deltaRevocationListAttribute = var1.deltaRevocationListAttribute;
      this.authorityRevocationListAttribute = var1.authorityRevocationListAttribute;
      this.attributeCertificateAttributeAttribute = var1.attributeCertificateAttributeAttribute;
      this.aACertificateAttribute = var1.aACertificateAttribute;
      this.attributeDescriptorCertificateAttribute = var1.attributeDescriptorCertificateAttribute;
      this.attributeCertificateRevocationListAttribute = var1.attributeCertificateRevocationListAttribute;
      this.attributeAuthorityRevocationListAttribute = var1.attributeAuthorityRevocationListAttribute;
      this.ldapUserCertificateAttributeName = var1.ldapUserCertificateAttributeName;
      this.ldapCACertificateAttributeName = var1.ldapCACertificateAttributeName;
      this.ldapCrossCertificateAttributeName = var1.ldapCrossCertificateAttributeName;
      this.ldapCertificateRevocationListAttributeName = var1.ldapCertificateRevocationListAttributeName;
      this.ldapDeltaRevocationListAttributeName = var1.ldapDeltaRevocationListAttributeName;
      this.ldapAuthorityRevocationListAttributeName = var1.ldapAuthorityRevocationListAttributeName;
      this.ldapAttributeCertificateAttributeAttributeName = var1.ldapAttributeCertificateAttributeAttributeName;
      this.ldapAACertificateAttributeName = var1.ldapAACertificateAttributeName;
      this.ldapAttributeDescriptorCertificateAttributeName = var1.ldapAttributeDescriptorCertificateAttributeName;
      this.ldapAttributeCertificateRevocationListAttributeName = var1.ldapAttributeCertificateRevocationListAttributeName;
      this.ldapAttributeAuthorityRevocationListAttributeName = var1.ldapAttributeAuthorityRevocationListAttributeName;
      this.userCertificateSubjectAttributeName = var1.userCertificateSubjectAttributeName;
      this.cACertificateSubjectAttributeName = var1.cACertificateSubjectAttributeName;
      this.crossCertificateSubjectAttributeName = var1.crossCertificateSubjectAttributeName;
      this.certificateRevocationListIssuerAttributeName = var1.certificateRevocationListIssuerAttributeName;
      this.deltaRevocationListIssuerAttributeName = var1.deltaRevocationListIssuerAttributeName;
      this.authorityRevocationListIssuerAttributeName = var1.authorityRevocationListIssuerAttributeName;
      this.attributeCertificateAttributeSubjectAttributeName = var1.attributeCertificateAttributeSubjectAttributeName;
      this.aACertificateSubjectAttributeName = var1.aACertificateSubjectAttributeName;
      this.attributeDescriptorCertificateSubjectAttributeName = var1.attributeDescriptorCertificateSubjectAttributeName;
      this.attributeCertificateRevocationListIssuerAttributeName = var1.attributeCertificateRevocationListIssuerAttributeName;
      this.attributeAuthorityRevocationListIssuerAttributeName = var1.attributeAuthorityRevocationListIssuerAttributeName;
      this.searchForSerialNumberIn = var1.searchForSerialNumberIn;
   }

   @Override
   public Object clone() {
      return this;
   }

   public boolean equal(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof X509LDAPCertStoreParameters)) {
         return false;
      } else {
         X509LDAPCertStoreParameters var2 = (X509LDAPCertStoreParameters)var1;
         return this.checkField(this.ldapURL, var2.ldapURL)
            && this.checkField(this.baseDN, var2.baseDN)
            && this.checkField(this.userCertificateAttribute, var2.userCertificateAttribute)
            && this.checkField(this.cACertificateAttribute, var2.cACertificateAttribute)
            && this.checkField(this.crossCertificateAttribute, var2.crossCertificateAttribute)
            && this.checkField(this.certificateRevocationListAttribute, var2.certificateRevocationListAttribute)
            && this.checkField(this.deltaRevocationListAttribute, var2.deltaRevocationListAttribute)
            && this.checkField(this.authorityRevocationListAttribute, var2.authorityRevocationListAttribute)
            && this.checkField(this.attributeCertificateAttributeAttribute, var2.attributeCertificateAttributeAttribute)
            && this.checkField(this.aACertificateAttribute, var2.aACertificateAttribute)
            && this.checkField(this.attributeDescriptorCertificateAttribute, var2.attributeDescriptorCertificateAttribute)
            && this.checkField(this.attributeCertificateRevocationListAttribute, var2.attributeCertificateRevocationListAttribute)
            && this.checkField(this.attributeAuthorityRevocationListAttribute, var2.attributeAuthorityRevocationListAttribute)
            && this.checkField(this.ldapUserCertificateAttributeName, var2.ldapUserCertificateAttributeName)
            && this.checkField(this.ldapCACertificateAttributeName, var2.ldapCACertificateAttributeName)
            && this.checkField(this.ldapCrossCertificateAttributeName, var2.ldapCrossCertificateAttributeName)
            && this.checkField(this.ldapCertificateRevocationListAttributeName, var2.ldapCertificateRevocationListAttributeName)
            && this.checkField(this.ldapDeltaRevocationListAttributeName, var2.ldapDeltaRevocationListAttributeName)
            && this.checkField(this.ldapAuthorityRevocationListAttributeName, var2.ldapAuthorityRevocationListAttributeName)
            && this.checkField(this.ldapAttributeCertificateAttributeAttributeName, var2.ldapAttributeCertificateAttributeAttributeName)
            && this.checkField(this.ldapAACertificateAttributeName, var2.ldapAACertificateAttributeName)
            && this.checkField(this.ldapAttributeDescriptorCertificateAttributeName, var2.ldapAttributeDescriptorCertificateAttributeName)
            && this.checkField(this.ldapAttributeCertificateRevocationListAttributeName, var2.ldapAttributeCertificateRevocationListAttributeName)
            && this.checkField(this.ldapAttributeAuthorityRevocationListAttributeName, var2.ldapAttributeAuthorityRevocationListAttributeName)
            && this.checkField(this.userCertificateSubjectAttributeName, var2.userCertificateSubjectAttributeName)
            && this.checkField(this.cACertificateSubjectAttributeName, var2.cACertificateSubjectAttributeName)
            && this.checkField(this.crossCertificateSubjectAttributeName, var2.crossCertificateSubjectAttributeName)
            && this.checkField(this.certificateRevocationListIssuerAttributeName, var2.certificateRevocationListIssuerAttributeName)
            && this.checkField(this.deltaRevocationListIssuerAttributeName, var2.deltaRevocationListIssuerAttributeName)
            && this.checkField(this.authorityRevocationListIssuerAttributeName, var2.authorityRevocationListIssuerAttributeName)
            && this.checkField(this.attributeCertificateAttributeSubjectAttributeName, var2.attributeCertificateAttributeSubjectAttributeName)
            && this.checkField(this.aACertificateSubjectAttributeName, var2.aACertificateSubjectAttributeName)
            && this.checkField(this.attributeDescriptorCertificateSubjectAttributeName, var2.attributeDescriptorCertificateSubjectAttributeName)
            && this.checkField(this.attributeCertificateRevocationListIssuerAttributeName, var2.attributeCertificateRevocationListIssuerAttributeName)
            && this.checkField(this.attributeAuthorityRevocationListIssuerAttributeName, var2.attributeAuthorityRevocationListIssuerAttributeName)
            && this.checkField(this.searchForSerialNumberIn, var2.searchForSerialNumberIn);
      }
   }

   private boolean checkField(Object var1, Object var2) {
      if (var1 == var2) {
         return true;
      } else {
         return var1 == null ? false : var1.equals(var2);
      }
   }

   @Override
   public int hashCode() {
      int var1 = 0;
      var1 = this.addHashCode(var1, this.userCertificateAttribute);
      var1 = this.addHashCode(var1, this.cACertificateAttribute);
      var1 = this.addHashCode(var1, this.crossCertificateAttribute);
      var1 = this.addHashCode(var1, this.certificateRevocationListAttribute);
      var1 = this.addHashCode(var1, this.deltaRevocationListAttribute);
      var1 = this.addHashCode(var1, this.authorityRevocationListAttribute);
      var1 = this.addHashCode(var1, this.attributeCertificateAttributeAttribute);
      var1 = this.addHashCode(var1, this.aACertificateAttribute);
      var1 = this.addHashCode(var1, this.attributeDescriptorCertificateAttribute);
      var1 = this.addHashCode(var1, this.attributeCertificateRevocationListAttribute);
      var1 = this.addHashCode(var1, this.attributeAuthorityRevocationListAttribute);
      var1 = this.addHashCode(var1, this.ldapUserCertificateAttributeName);
      var1 = this.addHashCode(var1, this.ldapCACertificateAttributeName);
      var1 = this.addHashCode(var1, this.ldapCrossCertificateAttributeName);
      var1 = this.addHashCode(var1, this.ldapCertificateRevocationListAttributeName);
      var1 = this.addHashCode(var1, this.ldapDeltaRevocationListAttributeName);
      var1 = this.addHashCode(var1, this.ldapAuthorityRevocationListAttributeName);
      var1 = this.addHashCode(var1, this.ldapAttributeCertificateAttributeAttributeName);
      var1 = this.addHashCode(var1, this.ldapAACertificateAttributeName);
      var1 = this.addHashCode(var1, this.ldapAttributeDescriptorCertificateAttributeName);
      var1 = this.addHashCode(var1, this.ldapAttributeCertificateRevocationListAttributeName);
      var1 = this.addHashCode(var1, this.ldapAttributeAuthorityRevocationListAttributeName);
      var1 = this.addHashCode(var1, this.userCertificateSubjectAttributeName);
      var1 = this.addHashCode(var1, this.cACertificateSubjectAttributeName);
      var1 = this.addHashCode(var1, this.crossCertificateSubjectAttributeName);
      var1 = this.addHashCode(var1, this.certificateRevocationListIssuerAttributeName);
      var1 = this.addHashCode(var1, this.deltaRevocationListIssuerAttributeName);
      var1 = this.addHashCode(var1, this.authorityRevocationListIssuerAttributeName);
      var1 = this.addHashCode(var1, this.attributeCertificateAttributeSubjectAttributeName);
      var1 = this.addHashCode(var1, this.aACertificateSubjectAttributeName);
      var1 = this.addHashCode(var1, this.attributeDescriptorCertificateSubjectAttributeName);
      var1 = this.addHashCode(var1, this.attributeCertificateRevocationListIssuerAttributeName);
      var1 = this.addHashCode(var1, this.attributeAuthorityRevocationListIssuerAttributeName);
      return this.addHashCode(var1, this.searchForSerialNumberIn);
   }

   private int addHashCode(int var1, Object var2) {
      return var1 * 29 + (var2 == null ? 0 : var2.hashCode());
   }

   public String getAACertificateAttribute() {
      return this.aACertificateAttribute;
   }

   public String getAACertificateSubjectAttributeName() {
      return this.aACertificateSubjectAttributeName;
   }

   public String getAttributeAuthorityRevocationListAttribute() {
      return this.attributeAuthorityRevocationListAttribute;
   }

   public String getAttributeAuthorityRevocationListIssuerAttributeName() {
      return this.attributeAuthorityRevocationListIssuerAttributeName;
   }

   public String getAttributeCertificateAttributeAttribute() {
      return this.attributeCertificateAttributeAttribute;
   }

   public String getAttributeCertificateAttributeSubjectAttributeName() {
      return this.attributeCertificateAttributeSubjectAttributeName;
   }

   public String getAttributeCertificateRevocationListAttribute() {
      return this.attributeCertificateRevocationListAttribute;
   }

   public String getAttributeCertificateRevocationListIssuerAttributeName() {
      return this.attributeCertificateRevocationListIssuerAttributeName;
   }

   public String getAttributeDescriptorCertificateAttribute() {
      return this.attributeDescriptorCertificateAttribute;
   }

   public String getAttributeDescriptorCertificateSubjectAttributeName() {
      return this.attributeDescriptorCertificateSubjectAttributeName;
   }

   public String getAuthorityRevocationListAttribute() {
      return this.authorityRevocationListAttribute;
   }

   public String getAuthorityRevocationListIssuerAttributeName() {
      return this.authorityRevocationListIssuerAttributeName;
   }

   public String getBaseDN() {
      return this.baseDN;
   }

   public String getCACertificateAttribute() {
      return this.cACertificateAttribute;
   }

   public String getCACertificateSubjectAttributeName() {
      return this.cACertificateSubjectAttributeName;
   }

   public String getCertificateRevocationListAttribute() {
      return this.certificateRevocationListAttribute;
   }

   public String getCertificateRevocationListIssuerAttributeName() {
      return this.certificateRevocationListIssuerAttributeName;
   }

   public String getCrossCertificateAttribute() {
      return this.crossCertificateAttribute;
   }

   public String getCrossCertificateSubjectAttributeName() {
      return this.crossCertificateSubjectAttributeName;
   }

   public String getDeltaRevocationListAttribute() {
      return this.deltaRevocationListAttribute;
   }

   public String getDeltaRevocationListIssuerAttributeName() {
      return this.deltaRevocationListIssuerAttributeName;
   }

   public String getLdapAACertificateAttributeName() {
      return this.ldapAACertificateAttributeName;
   }

   public String getLdapAttributeAuthorityRevocationListAttributeName() {
      return this.ldapAttributeAuthorityRevocationListAttributeName;
   }

   public String getLdapAttributeCertificateAttributeAttributeName() {
      return this.ldapAttributeCertificateAttributeAttributeName;
   }

   public String getLdapAttributeCertificateRevocationListAttributeName() {
      return this.ldapAttributeCertificateRevocationListAttributeName;
   }

   public String getLdapAttributeDescriptorCertificateAttributeName() {
      return this.ldapAttributeDescriptorCertificateAttributeName;
   }

   public String getLdapAuthorityRevocationListAttributeName() {
      return this.ldapAuthorityRevocationListAttributeName;
   }

   public String getLdapCACertificateAttributeName() {
      return this.ldapCACertificateAttributeName;
   }

   public String getLdapCertificateRevocationListAttributeName() {
      return this.ldapCertificateRevocationListAttributeName;
   }

   public String getLdapCrossCertificateAttributeName() {
      return this.ldapCrossCertificateAttributeName;
   }

   public String getLdapDeltaRevocationListAttributeName() {
      return this.ldapDeltaRevocationListAttributeName;
   }

   public String getLdapURL() {
      return this.ldapURL;
   }

   public String getLdapUserCertificateAttributeName() {
      return this.ldapUserCertificateAttributeName;
   }

   public String getSearchForSerialNumberIn() {
      return this.searchForSerialNumberIn;
   }

   public String getUserCertificateAttribute() {
      return this.userCertificateAttribute;
   }

   public String getUserCertificateSubjectAttributeName() {
      return this.userCertificateSubjectAttributeName;
   }

   public static X509LDAPCertStoreParameters getInstance(LDAPCertStoreParameters var0) {
      String var1 = "ldap://" + var0.getServerName() + ":" + var0.getPort();
      return new X509LDAPCertStoreParameters.Builder(var1, "").build();
   }

   public static class Builder {
      private String ldapURL;
      private String baseDN;
      private String userCertificateAttribute;
      private String cACertificateAttribute;
      private String crossCertificateAttribute;
      private String certificateRevocationListAttribute;
      private String deltaRevocationListAttribute;
      private String authorityRevocationListAttribute;
      private String attributeCertificateAttributeAttribute;
      private String aACertificateAttribute;
      private String attributeDescriptorCertificateAttribute;
      private String attributeCertificateRevocationListAttribute;
      private String attributeAuthorityRevocationListAttribute;
      private String ldapUserCertificateAttributeName;
      private String ldapCACertificateAttributeName;
      private String ldapCrossCertificateAttributeName;
      private String ldapCertificateRevocationListAttributeName;
      private String ldapDeltaRevocationListAttributeName;
      private String ldapAuthorityRevocationListAttributeName;
      private String ldapAttributeCertificateAttributeAttributeName;
      private String ldapAACertificateAttributeName;
      private String ldapAttributeDescriptorCertificateAttributeName;
      private String ldapAttributeCertificateRevocationListAttributeName;
      private String ldapAttributeAuthorityRevocationListAttributeName;
      private String userCertificateSubjectAttributeName;
      private String cACertificateSubjectAttributeName;
      private String crossCertificateSubjectAttributeName;
      private String certificateRevocationListIssuerAttributeName;
      private String deltaRevocationListIssuerAttributeName;
      private String authorityRevocationListIssuerAttributeName;
      private String attributeCertificateAttributeSubjectAttributeName;
      private String aACertificateSubjectAttributeName;
      private String attributeDescriptorCertificateSubjectAttributeName;
      private String attributeCertificateRevocationListIssuerAttributeName;
      private String attributeAuthorityRevocationListIssuerAttributeName;
      private String searchForSerialNumberIn;

      public Builder() {
         this("ldap://localhost:389", "");
      }

      public Builder(String var1, String var2) {
         this.ldapURL = var1;
         if (var2 == null) {
            this.baseDN = "";
         } else {
            this.baseDN = var2;
         }

         this.userCertificateAttribute = "userCertificate";
         this.cACertificateAttribute = "cACertificate";
         this.crossCertificateAttribute = "crossCertificatePair";
         this.certificateRevocationListAttribute = "certificateRevocationList";
         this.deltaRevocationListAttribute = "deltaRevocationList";
         this.authorityRevocationListAttribute = "authorityRevocationList";
         this.attributeCertificateAttributeAttribute = "attributeCertificateAttribute";
         this.aACertificateAttribute = "aACertificate";
         this.attributeDescriptorCertificateAttribute = "attributeDescriptorCertificate";
         this.attributeCertificateRevocationListAttribute = "attributeCertificateRevocationList";
         this.attributeAuthorityRevocationListAttribute = "attributeAuthorityRevocationList";
         this.ldapUserCertificateAttributeName = "cn";
         this.ldapCACertificateAttributeName = "cn ou o";
         this.ldapCrossCertificateAttributeName = "cn ou o";
         this.ldapCertificateRevocationListAttributeName = "cn ou o";
         this.ldapDeltaRevocationListAttributeName = "cn ou o";
         this.ldapAuthorityRevocationListAttributeName = "cn ou o";
         this.ldapAttributeCertificateAttributeAttributeName = "cn";
         this.ldapAACertificateAttributeName = "cn o ou";
         this.ldapAttributeDescriptorCertificateAttributeName = "cn o ou";
         this.ldapAttributeCertificateRevocationListAttributeName = "cn o ou";
         this.ldapAttributeAuthorityRevocationListAttributeName = "cn o ou";
         this.userCertificateSubjectAttributeName = "cn";
         this.cACertificateSubjectAttributeName = "o ou";
         this.crossCertificateSubjectAttributeName = "o ou";
         this.certificateRevocationListIssuerAttributeName = "o ou";
         this.deltaRevocationListIssuerAttributeName = "o ou";
         this.authorityRevocationListIssuerAttributeName = "o ou";
         this.attributeCertificateAttributeSubjectAttributeName = "cn";
         this.aACertificateSubjectAttributeName = "o ou";
         this.attributeDescriptorCertificateSubjectAttributeName = "o ou";
         this.attributeCertificateRevocationListIssuerAttributeName = "o ou";
         this.attributeAuthorityRevocationListIssuerAttributeName = "o ou";
         this.searchForSerialNumberIn = "uid serialNumber cn";
      }

      public X509LDAPCertStoreParameters.Builder setUserCertificateAttribute(String var1) {
         this.userCertificateAttribute = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setCACertificateAttribute(String var1) {
         this.cACertificateAttribute = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setCrossCertificateAttribute(String var1) {
         this.crossCertificateAttribute = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setCertificateRevocationListAttribute(String var1) {
         this.certificateRevocationListAttribute = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setDeltaRevocationListAttribute(String var1) {
         this.deltaRevocationListAttribute = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setAuthorityRevocationListAttribute(String var1) {
         this.authorityRevocationListAttribute = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setAttributeCertificateAttributeAttribute(String var1) {
         this.attributeCertificateAttributeAttribute = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setAACertificateAttribute(String var1) {
         this.aACertificateAttribute = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setAttributeDescriptorCertificateAttribute(String var1) {
         this.attributeDescriptorCertificateAttribute = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setAttributeCertificateRevocationListAttribute(String var1) {
         this.attributeCertificateRevocationListAttribute = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setAttributeAuthorityRevocationListAttribute(String var1) {
         this.attributeAuthorityRevocationListAttribute = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setLdapUserCertificateAttributeName(String var1) {
         this.ldapUserCertificateAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setLdapCACertificateAttributeName(String var1) {
         this.ldapCACertificateAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setLdapCrossCertificateAttributeName(String var1) {
         this.ldapCrossCertificateAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setLdapCertificateRevocationListAttributeName(String var1) {
         this.ldapCertificateRevocationListAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setLdapDeltaRevocationListAttributeName(String var1) {
         this.ldapDeltaRevocationListAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setLdapAuthorityRevocationListAttributeName(String var1) {
         this.ldapAuthorityRevocationListAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setLdapAttributeCertificateAttributeAttributeName(String var1) {
         this.ldapAttributeCertificateAttributeAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setLdapAACertificateAttributeName(String var1) {
         this.ldapAACertificateAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setLdapAttributeDescriptorCertificateAttributeName(String var1) {
         this.ldapAttributeDescriptorCertificateAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setLdapAttributeCertificateRevocationListAttributeName(String var1) {
         this.ldapAttributeCertificateRevocationListAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setLdapAttributeAuthorityRevocationListAttributeName(String var1) {
         this.ldapAttributeAuthorityRevocationListAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setUserCertificateSubjectAttributeName(String var1) {
         this.userCertificateSubjectAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setCACertificateSubjectAttributeName(String var1) {
         this.cACertificateSubjectAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setCrossCertificateSubjectAttributeName(String var1) {
         this.crossCertificateSubjectAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setCertificateRevocationListIssuerAttributeName(String var1) {
         this.certificateRevocationListIssuerAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setDeltaRevocationListIssuerAttributeName(String var1) {
         this.deltaRevocationListIssuerAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setAuthorityRevocationListIssuerAttributeName(String var1) {
         this.authorityRevocationListIssuerAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setAttributeCertificateAttributeSubjectAttributeName(String var1) {
         this.attributeCertificateAttributeSubjectAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setAACertificateSubjectAttributeName(String var1) {
         this.aACertificateSubjectAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setAttributeDescriptorCertificateSubjectAttributeName(String var1) {
         this.attributeDescriptorCertificateSubjectAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setAttributeCertificateRevocationListIssuerAttributeName(String var1) {
         this.attributeCertificateRevocationListIssuerAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setAttributeAuthorityRevocationListIssuerAttributeName(String var1) {
         this.attributeAuthorityRevocationListIssuerAttributeName = var1;
         return this;
      }

      public X509LDAPCertStoreParameters.Builder setSearchForSerialNumberIn(String var1) {
         this.searchForSerialNumberIn = var1;
         return this;
      }

      public X509LDAPCertStoreParameters build() {
         if (this.ldapUserCertificateAttributeName != null
            && this.ldapCACertificateAttributeName != null
            && this.ldapCrossCertificateAttributeName != null
            && this.ldapCertificateRevocationListAttributeName != null
            && this.ldapDeltaRevocationListAttributeName != null
            && this.ldapAuthorityRevocationListAttributeName != null
            && this.ldapAttributeCertificateAttributeAttributeName != null
            && this.ldapAACertificateAttributeName != null
            && this.ldapAttributeDescriptorCertificateAttributeName != null
            && this.ldapAttributeCertificateRevocationListAttributeName != null
            && this.ldapAttributeAuthorityRevocationListAttributeName != null
            && this.userCertificateSubjectAttributeName != null
            && this.cACertificateSubjectAttributeName != null
            && this.crossCertificateSubjectAttributeName != null
            && this.certificateRevocationListIssuerAttributeName != null
            && this.deltaRevocationListIssuerAttributeName != null
            && this.authorityRevocationListIssuerAttributeName != null
            && this.attributeCertificateAttributeSubjectAttributeName != null
            && this.aACertificateSubjectAttributeName != null
            && this.attributeDescriptorCertificateSubjectAttributeName != null
            && this.attributeCertificateRevocationListIssuerAttributeName != null
            && this.attributeAuthorityRevocationListIssuerAttributeName != null) {
            return new X509LDAPCertStoreParameters(this);
         } else {
            throw new IllegalArgumentException("Necessary parameters not specified.");
         }
      }
   }
}
