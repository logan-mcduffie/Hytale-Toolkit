package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

/** @deprecated */
public class X509CRLObject extends X509CRL {
   private CertificateList c;
   private String sigAlgName;
   private byte[] sigAlgParams;
   private boolean isIndirect;
   private boolean isHashCodeSet = false;
   private int hashCodeValue;

   public static boolean isIndirectCRL(X509CRL var0) throws CRLException {
      try {
         byte[] var1 = var0.getExtensionValue(Extension.issuingDistributionPoint.getId());
         return var1 != null && IssuingDistributionPoint.getInstance(ASN1OctetString.getInstance(var1).getOctets()).isIndirectCRL();
      } catch (Exception var2) {
         throw new ExtCRLException("Exception reading IssuingDistributionPoint", var2);
      }
   }

   public X509CRLObject(CertificateList var1) throws CRLException {
      this.c = var1;

      try {
         this.sigAlgName = X509SignatureUtil.getSignatureName(var1.getSignatureAlgorithm());
         if (var1.getSignatureAlgorithm().getParameters() != null) {
            this.sigAlgParams = var1.getSignatureAlgorithm().getParameters().toASN1Primitive().getEncoded("DER");
         } else {
            this.sigAlgParams = null;
         }

         this.isIndirect = isIndirectCRL(this);
      } catch (Exception var3) {
         throw new CRLException("CRL contents invalid: " + var3);
      }
   }

   @Override
   public boolean hasUnsupportedCriticalExtension() {
      if (this.getVersion() == 2) {
         Extensions var1 = this.c.getExtensions();
         if (var1 != null) {
            Enumeration var2 = var1.oids();

            while (var2.hasMoreElements()) {
               ASN1ObjectIdentifier var3 = (ASN1ObjectIdentifier)var2.nextElement();
               if (!Extension.issuingDistributionPoint.equals(var3) && !Extension.deltaCRLIndicator.equals(var3)) {
                  Extension var4 = var1.getExtension(var3);
                  if (var4.isCritical()) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private Set getExtensionOIDs(boolean var1) {
      if (this.getVersion() == 2) {
         Extensions var2 = this.c.getExtensions();
         if (var2 != null) {
            HashSet var3 = new HashSet();
            Enumeration var4 = var2.oids();

            while (var4.hasMoreElements()) {
               ASN1ObjectIdentifier var5 = (ASN1ObjectIdentifier)var4.nextElement();
               Extension var6 = var2.getExtension(var5);
               if (var1 == var6.isCritical()) {
                  var3.add(var5.getId());
               }
            }

            return var3;
         }
      }

      return null;
   }

   @Override
   public Set getCriticalExtensionOIDs() {
      return this.getExtensionOIDs(true);
   }

   @Override
   public Set getNonCriticalExtensionOIDs() {
      return this.getExtensionOIDs(false);
   }

   @Override
   public byte[] getExtensionValue(String var1) {
      return X509SignatureUtil.getExtensionValue(this.c.getExtensions(), var1);
   }

   @Override
   public byte[] getEncoded() throws CRLException {
      try {
         return this.c.getEncoded("DER");
      } catch (IOException var2) {
         throw new CRLException(var2.toString());
      }
   }

   @Override
   public void verify(PublicKey var1) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
      Signature var2;
      try {
         var2 = Signature.getInstance(this.getSigAlgName(), "BC");
      } catch (Exception var4) {
         var2 = Signature.getInstance(this.getSigAlgName());
      }

      this.doVerify(var1, var2);
   }

   @Override
   public void verify(PublicKey var1, String var2) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
      Signature var3;
      if (var2 != null) {
         var3 = Signature.getInstance(this.getSigAlgName(), var2);
      } else {
         var3 = Signature.getInstance(this.getSigAlgName());
      }

      this.doVerify(var1, var3);
   }

   @Override
   public void verify(PublicKey var1, Provider var2) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
      Signature var3;
      if (var2 != null) {
         var3 = Signature.getInstance(this.getSigAlgName(), var2);
      } else {
         var3 = Signature.getInstance(this.getSigAlgName());
      }

      this.doVerify(var1, var3);
   }

   private void doVerify(PublicKey var1, Signature var2) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
      if (!this.c.getSignatureAlgorithm().equals(this.c.getTBSCertList().getSignature())) {
         throw new CRLException("Signature algorithm on CertificateList does not match TBSCertList.");
      } else {
         var2.initVerify(var1);
         var2.update(this.getTBSCertList());
         if (!var2.verify(this.getSignature())) {
            throw new SignatureException("CRL does not verify with supplied public key.");
         }
      }
   }

   @Override
   public int getVersion() {
      return this.c.getVersionNumber();
   }

   @Override
   public Principal getIssuerDN() {
      return new X509Principal(X500Name.getInstance(this.c.getIssuer().toASN1Primitive()));
   }

   @Override
   public X500Principal getIssuerX500Principal() {
      try {
         return new X500Principal(this.c.getIssuer().getEncoded());
      } catch (IOException var2) {
         throw new IllegalStateException("can't encode issuer DN");
      }
   }

   @Override
   public Date getThisUpdate() {
      return this.c.getThisUpdate().getDate();
   }

   @Override
   public Date getNextUpdate() {
      Time var1 = this.c.getNextUpdate();
      return null == var1 ? null : var1.getDate();
   }

   private Set loadCRLEntries() {
      HashSet var1 = new HashSet();
      Enumeration var2 = this.c.getRevokedCertificateEnumeration();
      X500Name var3 = null;

      while (var2.hasMoreElements()) {
         TBSCertList.CRLEntry var4 = (TBSCertList.CRLEntry)var2.nextElement();
         X509CRLEntryObject var5 = new X509CRLEntryObject(var4, this.isIndirect, var3);
         var1.add(var5);
         if (this.isIndirect && var4.hasExtensions()) {
            Extension var6 = var4.getExtensions().getExtension(Extension.certificateIssuer);
            if (var6 != null) {
               var3 = X500Name.getInstance(GeneralNames.getInstance(var6.getParsedValue()).getNames()[0].getName());
            }
         }
      }

      return var1;
   }

   @Override
   public X509CRLEntry getRevokedCertificate(BigInteger var1) {
      Enumeration var2 = this.c.getRevokedCertificateEnumeration();
      X500Name var3 = null;

      while (var2.hasMoreElements()) {
         TBSCertList.CRLEntry var4 = (TBSCertList.CRLEntry)var2.nextElement();
         if (var4.getUserCertificate().hasValue(var1)) {
            return new X509CRLEntryObject(var4, this.isIndirect, var3);
         }

         if (this.isIndirect && var4.hasExtensions()) {
            Extension var5 = var4.getExtensions().getExtension(Extension.certificateIssuer);
            if (var5 != null) {
               var3 = X500Name.getInstance(GeneralNames.getInstance(var5.getParsedValue()).getNames()[0].getName());
            }
         }
      }

      return null;
   }

   @Override
   public Set getRevokedCertificates() {
      Set var1 = this.loadCRLEntries();
      return !var1.isEmpty() ? Collections.unmodifiableSet(var1) : null;
   }

   @Override
   public byte[] getTBSCertList() throws CRLException {
      try {
         return this.c.getTBSCertList().getEncoded("DER");
      } catch (IOException var2) {
         throw new CRLException(var2.toString());
      }
   }

   @Override
   public byte[] getSignature() {
      return this.c.getSignature().getOctets();
   }

   @Override
   public String getSigAlgName() {
      return this.sigAlgName;
   }

   @Override
   public String getSigAlgOID() {
      return this.c.getSignatureAlgorithm().getAlgorithm().getId();
   }

   @Override
   public byte[] getSigAlgParams() {
      return Arrays.clone(this.sigAlgParams);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = Strings.lineSeparator();
      var1.append("              Version: ").append(this.getVersion()).append(var2);
      var1.append("             IssuerDN: ").append(this.getIssuerDN()).append(var2);
      var1.append("          This update: ").append(this.getThisUpdate()).append(var2);
      var1.append("          Next update: ").append(this.getNextUpdate()).append(var2);
      var1.append("  Signature Algorithm: ").append(this.getSigAlgName()).append(var2);
      byte[] var3 = this.getSignature();
      var1.append("            Signature: ").append(new String(Hex.encode(var3, 0, 20))).append(var2);

      for (byte var4 = 20; var4 < var3.length; var4 += 20) {
         if (var4 < var3.length - 20) {
            var1.append("                       ").append(new String(Hex.encode(var3, var4, 20))).append(var2);
         } else {
            var1.append("                       ").append(new String(Hex.encode(var3, var4, var3.length - var4))).append(var2);
         }
      }

      Extensions var12 = this.c.getExtensions();
      if (var12 != null) {
         Enumeration var5 = var12.oids();
         if (var5.hasMoreElements()) {
            var1.append("           Extensions: ").append(var2);
         }

         while (var5.hasMoreElements()) {
            ASN1ObjectIdentifier var6 = (ASN1ObjectIdentifier)var5.nextElement();
            Extension var7 = var12.getExtension(var6);
            if (var7.getExtnValue() != null) {
               byte[] var8 = var7.getExtnValue().getOctets();
               ASN1InputStream var9 = new ASN1InputStream(var8);
               var1.append("                       critical(").append(var7.isCritical()).append(") ");

               try {
                  if (var6.equals(Extension.cRLNumber)) {
                     var1.append(new CRLNumber(ASN1Integer.getInstance(var9.readObject()).getPositiveValue())).append(var2);
                  } else if (var6.equals(Extension.deltaCRLIndicator)) {
                     var1.append("Base CRL: " + new CRLNumber(ASN1Integer.getInstance(var9.readObject()).getPositiveValue())).append(var2);
                  } else if (var6.equals(Extension.issuingDistributionPoint)) {
                     var1.append(IssuingDistributionPoint.getInstance(var9.readObject())).append(var2);
                  } else if (var6.equals(Extension.cRLDistributionPoints)) {
                     var1.append(CRLDistPoint.getInstance(var9.readObject())).append(var2);
                  } else if (var6.equals(Extension.freshestCRL)) {
                     var1.append(CRLDistPoint.getInstance(var9.readObject())).append(var2);
                  } else {
                     var1.append(var6.getId());
                     var1.append(" value = ").append(ASN1Dump.dumpAsString(var9.readObject())).append(var2);
                  }
               } catch (Exception var11) {
                  var1.append(var6.getId());
                  var1.append(" value = ").append("*****").append(var2);
               }
            } else {
               var1.append(var2);
            }
         }
      }

      Set var13 = this.getRevokedCertificates();
      if (var13 != null) {
         Iterator var14 = var13.iterator();

         while (var14.hasNext()) {
            var1.append(var14.next());
            var1.append(var2);
         }
      }

      return var1.toString();
   }

   @Override
   public boolean isRevoked(Certificate var1) {
      if (!var1.getType().equals("X.509")) {
         throw new RuntimeException("X.509 CRL used with non X.509 Cert");
      } else {
         Enumeration var2 = this.c.getRevokedCertificateEnumeration();
         X500Name var3 = this.c.getIssuer();
         if (var2 != null) {
            BigInteger var4 = ((X509Certificate)var1).getSerialNumber();

            while (var2.hasMoreElements()) {
               TBSCertList.CRLEntry var5 = TBSCertList.CRLEntry.getInstance(var2.nextElement());
               if (this.isIndirect && var5.hasExtensions()) {
                  Extension var6 = var5.getExtensions().getExtension(Extension.certificateIssuer);
                  if (var6 != null) {
                     var3 = X500Name.getInstance(GeneralNames.getInstance(var6.getParsedValue()).getNames()[0].getName());
                  }
               }

               if (var5.getUserCertificate().hasValue(var4)) {
                  X500Name var9;
                  if (var1 instanceof X509Certificate) {
                     var9 = X500Name.getInstance(((X509Certificate)var1).getIssuerX500Principal().getEncoded());
                  } else {
                     try {
                        var9 = org.bouncycastle.asn1.x509.Certificate.getInstance(var1.getEncoded()).getIssuer();
                     } catch (CertificateEncodingException var8) {
                        throw new RuntimeException("Cannot process certificate");
                     }
                  }

                  if (!var3.equals(var9)) {
                     return false;
                  }

                  return true;
               }
            }
         }

         return false;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof X509CRL)) {
         return false;
      } else if (var1 instanceof X509CRLObject) {
         X509CRLObject var2 = (X509CRLObject)var1;
         if (this.isHashCodeSet) {
            boolean var3 = var2.isHashCodeSet;
            if (var3 && var2.hashCodeValue != this.hashCodeValue) {
               return false;
            }
         }

         return this.c.equals(var2.c);
      } else {
         return super.equals(var1);
      }
   }

   @Override
   public int hashCode() {
      if (!this.isHashCodeSet) {
         this.isHashCodeSet = true;
         this.hashCodeValue = super.hashCode();
      }

      return this.hashCodeValue;
   }
}
