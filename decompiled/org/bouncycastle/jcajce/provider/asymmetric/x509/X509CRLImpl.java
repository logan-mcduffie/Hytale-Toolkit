package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.BufferedOutputStream;
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
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.jcajce.CompositePublicKey;
import org.bouncycastle.jcajce.io.OutputStreamFactory;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

abstract class X509CRLImpl extends X509CRL {
   protected JcaJceHelper bcHelper;
   protected CertificateList c;
   protected String sigAlgName;
   protected byte[] sigAlgParams;
   protected boolean isIndirect;

   X509CRLImpl(JcaJceHelper var1, CertificateList var2, String var3, byte[] var4, boolean var5) {
      this.bcHelper = var1;
      this.c = var2;
      this.sigAlgName = var3;
      this.sigAlgParams = var4;
      this.isIndirect = var5;
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
   public void verify(PublicKey var1) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
      this.doVerify(var1, new SignatureCreator() {
         @Override
         public Signature createSignature(String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
            try {
               return X509CRLImpl.this.bcHelper.createSignature(var1);
            } catch (Exception var3) {
               return Signature.getInstance(var1);
            }
         }
      });
   }

   @Override
   public void verify(PublicKey var1, final String var2) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
      this.doVerify(var1, new SignatureCreator() {
         @Override
         public Signature createSignature(String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
            return var2 != null ? Signature.getInstance(var1, var2) : Signature.getInstance(var1);
         }
      });
   }

   @Override
   public void verify(PublicKey var1, final Provider var2) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
      try {
         this.doVerify(var1, new SignatureCreator() {
            @Override
            public Signature createSignature(String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
               return var2 != null ? Signature.getInstance(X509CRLImpl.this.getSigAlgName(), var2) : Signature.getInstance(X509CRLImpl.this.getSigAlgName());
            }
         });
      } catch (NoSuchProviderException var4) {
         throw new NoSuchAlgorithmException("provider issue: " + var4.getMessage());
      }
   }

   private void doVerify(PublicKey var1, SignatureCreator var2) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
      if (!this.c.getSignatureAlgorithm().equals(this.c.getTBSCertList().getSignature())) {
         throw new CRLException("Signature algorithm on CertificateList does not match TBSCertList.");
      } else {
         if (var1 instanceof CompositePublicKey && X509SignatureUtil.isCompositeAlgorithm(this.c.getSignatureAlgorithm())) {
            List var19 = ((CompositePublicKey)var1).getPublicKeys();
            ASN1Sequence var20 = ASN1Sequence.getInstance(this.c.getSignatureAlgorithm().getParameters());
            ASN1Sequence var21 = ASN1Sequence.getInstance(this.c.getSignature().getOctets());
            boolean var22 = false;

            for (int var23 = 0; var23 != var19.size(); var23++) {
               if (var19.get(var23) != null) {
                  AlgorithmIdentifier var24 = AlgorithmIdentifier.getInstance(var20.getObjectAt(var23));
                  String var25 = X509SignatureUtil.getSignatureName(var24);
                  Signature var26 = var2.createSignature(var25);
                  SignatureException var11 = null;

                  try {
                     this.checkSignature(
                        (PublicKey)var19.get(var23), var26, var24.getParameters(), ASN1BitString.getInstance(var21.getObjectAt(var23)).getOctets()
                     );
                     var22 = true;
                  } catch (SignatureException var17) {
                     var11 = var17;
                  }

                  if (var11 != null) {
                     throw var11;
                  }
               }
            }

            if (!var22) {
               throw new InvalidKeyException("no matching key found");
            }
         } else if (X509SignatureUtil.isCompositeAlgorithm(this.c.getSignatureAlgorithm())) {
            ASN1Sequence var3 = ASN1Sequence.getInstance(this.c.getSignatureAlgorithm().getParameters());
            ASN1Sequence var4 = ASN1Sequence.getInstance(this.c.getSignature().getOctets());
            boolean var5 = false;

            for (int var6 = 0; var6 != var4.size(); var6++) {
               AlgorithmIdentifier var7 = AlgorithmIdentifier.getInstance(var3.getObjectAt(var6));
               String var8 = X509SignatureUtil.getSignatureName(var7);
               SignatureException var9 = null;

               try {
                  Signature var10 = var2.createSignature(var8);
                  this.checkSignature(var1, var10, var7.getParameters(), ASN1BitString.getInstance(var4.getObjectAt(var6)).getOctets());
                  var5 = true;
               } catch (InvalidKeyException var14) {
               } catch (NoSuchAlgorithmException var15) {
               } catch (SignatureException var16) {
                  var9 = var16;
               }

               if (var9 != null) {
                  throw var9;
               }
            }

            if (!var5) {
               throw new InvalidKeyException("no matching key found");
            }
         } else {
            Signature var18 = var2.createSignature(this.getSigAlgName());
            if (this.sigAlgParams == null) {
               this.checkSignature(var1, var18, null, this.getSignature());
            } else {
               try {
                  this.checkSignature(var1, var18, ASN1Primitive.fromByteArray(this.sigAlgParams), this.getSignature());
               } catch (IOException var13) {
                  throw new SignatureException("cannot decode signature parameters: " + var13.getMessage());
               }
            }
         }
      }
   }

   private void checkSignature(PublicKey var1, Signature var2, ASN1Encodable var3, byte[] var4) throws CRLException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
      if (!X509SignatureUtil.areEquivalentAlgorithms(this.c.getSignatureAlgorithm(), this.c.getTBSCertList().getSignature())) {
         throw new CRLException("Signature algorithm on CertificateList does not match TbsCertList.");
      } else {
         X509SignatureUtil.setSignatureParameters(var2, var3);
         var2.initVerify(var1);

         try {
            BufferedOutputStream var5 = new BufferedOutputStream(OutputStreamFactory.createStream(var2), 512);
            this.c.getTBSCertList().encodeTo(var5, "DER");
            var5.close();
         } catch (IOException var6) {
            throw new CRLException(var6.toString());
         }

         if (!var2.verify(var4)) {
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
      X509SignatureUtil.prettyPrintSignature(this.getSignature(), var1, var2);
      Extensions var3 = this.c.getExtensions();
      if (var3 != null) {
         Enumeration var4 = var3.oids();
         if (var4.hasMoreElements()) {
            var1.append("           Extensions: ").append(var2);
         }

         while (var4.hasMoreElements()) {
            ASN1ObjectIdentifier var5 = (ASN1ObjectIdentifier)var4.nextElement();
            Extension var6 = var3.getExtension(var5);
            if (var6.getExtnValue() != null) {
               byte[] var7 = var6.getExtnValue().getOctets();
               ASN1InputStream var8 = new ASN1InputStream(var7);
               var1.append("                       critical(").append(var6.isCritical()).append(") ");

               try {
                  if (var5.equals(Extension.cRLNumber)) {
                     var1.append(new CRLNumber(ASN1Integer.getInstance(var8.readObject()).getPositiveValue())).append(var2);
                  } else if (var5.equals(Extension.deltaCRLIndicator)) {
                     var1.append("Base CRL: " + new CRLNumber(ASN1Integer.getInstance(var8.readObject()).getPositiveValue())).append(var2);
                  } else if (var5.equals(Extension.issuingDistributionPoint)) {
                     var1.append(IssuingDistributionPoint.getInstance(var8.readObject())).append(var2);
                  } else if (var5.equals(Extension.cRLDistributionPoints)) {
                     var1.append(CRLDistPoint.getInstance(var8.readObject())).append(var2);
                  } else if (var5.equals(Extension.freshestCRL)) {
                     var1.append(CRLDistPoint.getInstance(var8.readObject())).append(var2);
                  } else {
                     var1.append(var5.getId());
                     var1.append(" value = ").append(ASN1Dump.dumpAsString(var8.readObject())).append(var2);
                  }
               } catch (Exception var10) {
                  var1.append(var5.getId());
                  var1.append(" value = ").append("*****").append(var2);
               }
            } else {
               var1.append(var2);
            }
         }
      }

      Set var11 = this.getRevokedCertificates();
      if (var11 != null) {
         Iterator var12 = var11.iterator();

         while (var12.hasNext()) {
            var1.append(var12.next());
            var1.append(var2);
         }
      }

      return var1.toString();
   }

   @Override
   public boolean isRevoked(Certificate var1) {
      if (!var1.getType().equals("X.509")) {
         throw new IllegalArgumentException("X.509 CRL used with non X.509 Cert");
      } else {
         Enumeration var2 = this.c.getRevokedCertificateEnumeration();
         X500Name var3 = this.c.getIssuer();
         if (var2.hasMoreElements()) {
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
                        throw new IllegalArgumentException("Cannot process certificate: " + var8.getMessage());
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

   static byte[] getExtensionOctets(CertificateList var0, ASN1ObjectIdentifier var1) {
      ASN1OctetString var2 = Extensions.getExtensionValue(var0.getExtensions(), var1);
      return var2 == null ? null : var2.getOctets();
   }
}
