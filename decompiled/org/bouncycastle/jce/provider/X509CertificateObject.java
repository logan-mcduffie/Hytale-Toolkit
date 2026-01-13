package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.NetscapeCertType;
import org.bouncycastle.internal.asn1.misc.NetscapeRevocationURL;
import org.bouncycastle.internal.asn1.misc.VerisignCzagExtension;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

/** @deprecated */
public class X509CertificateObject extends X509Certificate implements PKCS12BagAttributeCarrier {
   private Certificate c;
   private BasicConstraints basicConstraints;
   private boolean[] keyUsage;
   private boolean hashValueSet;
   private int hashValue;
   private PKCS12BagAttributeCarrier attrCarrier = new PKCS12BagAttributeCarrierImpl();

   public X509CertificateObject(Certificate var1) throws CertificateParsingException {
      this.c = var1;

      try {
         byte[] var2 = getExtensionOctets(var1, Extension.basicConstraints);
         if (var2 != null) {
            this.basicConstraints = BasicConstraints.getInstance(ASN1Primitive.fromByteArray(var2));
         }
      } catch (Exception var6) {
         throw new CertificateParsingException("cannot construct BasicConstraints: " + var6);
      }

      try {
         byte[] var8 = getExtensionOctets(var1, Extension.keyUsage);
         if (var8 != null) {
            ASN1BitString var3 = ASN1BitString.getInstance(ASN1Primitive.fromByteArray(var8));
            var8 = var3.getBytes();
            int var4 = var8.length * 8 - var3.getPadBits();
            this.keyUsage = new boolean[var4 < 9 ? 9 : var4];

            for (int var5 = 0; var5 != var4; var5++) {
               this.keyUsage[var5] = (var8[var5 / 8] & 128 >>> var5 % 8) != 0;
            }
         } else {
            this.keyUsage = null;
         }
      } catch (Exception var7) {
         throw new CertificateParsingException("cannot construct KeyUsage: " + var7);
      }
   }

   @Override
   public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
      this.checkValidity(new Date());
   }

   @Override
   public void checkValidity(Date var1) throws CertificateExpiredException, CertificateNotYetValidException {
      if (var1.getTime() > this.getNotAfter().getTime()) {
         throw new CertificateExpiredException("certificate expired on " + this.c.getEndDate().getTime());
      } else if (var1.getTime() < this.getNotBefore().getTime()) {
         throw new CertificateNotYetValidException("certificate not valid till " + this.c.getStartDate().getTime());
      }
   }

   @Override
   public int getVersion() {
      return this.c.getVersionNumber();
   }

   @Override
   public BigInteger getSerialNumber() {
      return this.c.getSerialNumber().getValue();
   }

   @Override
   public Principal getIssuerDN() {
      return new X509Principal(this.c.getIssuer());
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
   public Principal getSubjectDN() {
      return new X509Principal(this.c.getSubject());
   }

   @Override
   public X500Principal getSubjectX500Principal() {
      try {
         return new X500Principal(this.c.getSubject().getEncoded());
      } catch (IOException var2) {
         throw new IllegalStateException("can't encode issuer DN");
      }
   }

   @Override
   public Date getNotBefore() {
      return this.c.getStartDate().getDate();
   }

   @Override
   public Date getNotAfter() {
      return this.c.getEndDate().getDate();
   }

   @Override
   public byte[] getTBSCertificate() throws CertificateEncodingException {
      try {
         return this.c.getTBSCertificate().getEncoded("DER");
      } catch (IOException var2) {
         throw new CertificateEncodingException(var2.toString());
      }
   }

   @Override
   public byte[] getSignature() {
      return this.c.getSignature().getOctets();
   }

   @Override
   public String getSigAlgName() {
      Provider var1 = Security.getProvider("BC");
      if (var1 != null) {
         String var2 = var1.getProperty("Alg.Alias.Signature." + this.getSigAlgOID());
         if (var2 != null) {
            return var2;
         }
      }

      Provider[] var5 = Security.getProviders();

      for (int var3 = 0; var3 != var5.length; var3++) {
         String var4 = var5[var3].getProperty("Alg.Alias.Signature." + this.getSigAlgOID());
         if (var4 != null) {
            return var4;
         }
      }

      return this.getSigAlgOID();
   }

   @Override
   public String getSigAlgOID() {
      return this.c.getSignatureAlgorithm().getAlgorithm().getId();
   }

   @Override
   public byte[] getSigAlgParams() {
      if (this.c.getSignatureAlgorithm().getParameters() != null) {
         try {
            return this.c.getSignatureAlgorithm().getParameters().toASN1Primitive().getEncoded("DER");
         } catch (IOException var2) {
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public boolean[] getIssuerUniqueID() {
      ASN1BitString var1 = this.c.getTBSCertificate().getIssuerUniqueId();
      if (var1 != null) {
         byte[] var2 = var1.getBytes();
         boolean[] var3 = new boolean[var2.length * 8 - var1.getPadBits()];

         for (int var4 = 0; var4 != var3.length; var4++) {
            var3[var4] = (var2[var4 / 8] & 128 >>> var4 % 8) != 0;
         }

         return var3;
      } else {
         return null;
      }
   }

   @Override
   public boolean[] getSubjectUniqueID() {
      ASN1BitString var1 = this.c.getTBSCertificate().getSubjectUniqueId();
      if (var1 != null) {
         byte[] var2 = var1.getBytes();
         boolean[] var3 = new boolean[var2.length * 8 - var1.getPadBits()];

         for (int var4 = 0; var4 != var3.length; var4++) {
            var3[var4] = (var2[var4 / 8] & 128 >>> var4 % 8) != 0;
         }

         return var3;
      } else {
         return null;
      }
   }

   @Override
   public boolean[] getKeyUsage() {
      return this.keyUsage;
   }

   @Override
   public List getExtendedKeyUsage() throws CertificateParsingException {
      byte[] var1 = getExtensionOctets(this.c, Extension.extendedKeyUsage);
      if (null == var1) {
         return null;
      } else {
         try {
            ASN1Sequence var2 = ASN1Sequence.getInstance(var1);
            ArrayList var3 = new ArrayList();

            for (int var4 = 0; var4 != var2.size(); var4++) {
               var3.add(((ASN1ObjectIdentifier)var2.getObjectAt(var4)).getId());
            }

            return Collections.unmodifiableList(var3);
         } catch (Exception var5) {
            throw new CertificateParsingException("error processing extended key usage extension");
         }
      }
   }

   @Override
   public int getBasicConstraints() {
      if (this.basicConstraints != null && this.basicConstraints.isCA()) {
         ASN1Integer var1 = this.basicConstraints.getPathLenConstraintInteger();
         return var1 == null ? Integer.MAX_VALUE : var1.intPositiveValueExact();
      } else {
         return -1;
      }
   }

   @Override
   public Collection getSubjectAlternativeNames() throws CertificateParsingException {
      return getAlternativeNames(this.c, Extension.subjectAlternativeName);
   }

   @Override
   public Collection getIssuerAlternativeNames() throws CertificateParsingException {
      return getAlternativeNames(this.c, Extension.issuerAlternativeName);
   }

   @Override
   public Set getCriticalExtensionOIDs() {
      if (this.getVersion() == 3) {
         HashSet var1 = new HashSet();
         Extensions var2 = this.c.getExtensions();
         if (var2 != null) {
            Enumeration var3 = var2.oids();

            while (var3.hasMoreElements()) {
               ASN1ObjectIdentifier var4 = (ASN1ObjectIdentifier)var3.nextElement();
               Extension var5 = var2.getExtension(var4);
               if (var5.isCritical()) {
                  var1.add(var4.getId());
               }
            }

            return var1;
         }
      }

      return null;
   }

   @Override
   public byte[] getExtensionValue(String var1) {
      return X509SignatureUtil.getExtensionValue(this.c.getExtensions(), var1);
   }

   @Override
   public Set getNonCriticalExtensionOIDs() {
      if (this.getVersion() == 3) {
         HashSet var1 = new HashSet();
         Extensions var2 = this.c.getExtensions();
         if (var2 != null) {
            Enumeration var3 = var2.oids();

            while (var3.hasMoreElements()) {
               ASN1ObjectIdentifier var4 = (ASN1ObjectIdentifier)var3.nextElement();
               Extension var5 = var2.getExtension(var4);
               if (!var5.isCritical()) {
                  var1.add(var4.getId());
               }
            }

            return var1;
         }
      }

      return null;
   }

   @Override
   public boolean hasUnsupportedCriticalExtension() {
      if (this.getVersion() == 3) {
         Extensions var1 = this.c.getExtensions();
         if (var1 != null) {
            Enumeration var2 = var1.oids();

            while (var2.hasMoreElements()) {
               ASN1ObjectIdentifier var3 = (ASN1ObjectIdentifier)var2.nextElement();
               if (!Extension.keyUsage.equals(var3)
                  && !Extension.certificatePolicies.equals(var3)
                  && !Extension.policyMappings.equals(var3)
                  && !Extension.inhibitAnyPolicy.equals(var3)
                  && !Extension.cRLDistributionPoints.equals(var3)
                  && !Extension.issuingDistributionPoint.equals(var3)
                  && !Extension.deltaCRLIndicator.equals(var3)
                  && !Extension.policyConstraints.equals(var3)
                  && !Extension.basicConstraints.equals(var3)
                  && !Extension.subjectAlternativeName.equals(var3)
                  && !Extension.nameConstraints.equals(var3)) {
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

   @Override
   public PublicKey getPublicKey() {
      try {
         return BouncyCastleProvider.getPublicKey(this.c.getSubjectPublicKeyInfo());
      } catch (IOException var2) {
         return null;
      }
   }

   @Override
   public byte[] getEncoded() throws CertificateEncodingException {
      try {
         return this.c.getEncoded("DER");
      } catch (IOException var2) {
         throw new CertificateEncodingException(var2.toString());
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof java.security.cert.Certificate)) {
         return false;
      } else {
         java.security.cert.Certificate var2 = (java.security.cert.Certificate)var1;

         try {
            byte[] var3 = this.getEncoded();
            byte[] var4 = var2.getEncoded();
            return Arrays.areEqual(var3, var4);
         } catch (CertificateEncodingException var5) {
            return false;
         }
      }
   }

   @Override
   public synchronized int hashCode() {
      if (!this.hashValueSet) {
         this.hashValue = this.calculateHashCode();
         this.hashValueSet = true;
      }

      return this.hashValue;
   }

   private int calculateHashCode() {
      try {
         int var1 = 0;
         byte[] var2 = this.getEncoded();

         for (int var3 = 1; var3 < var2.length; var3++) {
            var1 += var2[var3] * var3;
         }

         return var1;
      } catch (CertificateEncodingException var4) {
         return 0;
      }
   }

   @Override
   public void setBagAttribute(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.attrCarrier.setBagAttribute(var1, var2);
   }

   @Override
   public ASN1Encodable getBagAttribute(ASN1ObjectIdentifier var1) {
      return this.attrCarrier.getBagAttribute(var1);
   }

   @Override
   public Enumeration getBagAttributeKeys() {
      return this.attrCarrier.getBagAttributeKeys();
   }

   @Override
   public boolean hasFriendlyName() {
      return this.attrCarrier.hasFriendlyName();
   }

   @Override
   public void setFriendlyName(String var1) {
      this.attrCarrier.setFriendlyName(var1);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = Strings.lineSeparator();
      var1.append("  [0]         Version: ").append(this.getVersion()).append(var2);
      var1.append("         SerialNumber: ").append(this.getSerialNumber()).append(var2);
      var1.append("             IssuerDN: ").append(this.getIssuerDN()).append(var2);
      var1.append("           Start Date: ").append(this.getNotBefore()).append(var2);
      var1.append("           Final Date: ").append(this.getNotAfter()).append(var2);
      var1.append("            SubjectDN: ").append(this.getSubjectDN()).append(var2);
      var1.append("           Public Key: ").append(this.getPublicKey()).append(var2);
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
            var1.append("       Extensions: \n");
         }

         while (var5.hasMoreElements()) {
            ASN1ObjectIdentifier var6 = (ASN1ObjectIdentifier)var5.nextElement();
            Extension var7 = var12.getExtension(var6);
            if (var7.getExtnValue() != null) {
               byte[] var8 = var7.getExtnValue().getOctets();
               ASN1InputStream var9 = new ASN1InputStream(var8);
               var1.append("                       critical(").append(var7.isCritical()).append(") ");

               try {
                  if (var6.equals(Extension.basicConstraints)) {
                     var1.append(BasicConstraints.getInstance(var9.readObject())).append(var2);
                  } else if (var6.equals(Extension.keyUsage)) {
                     var1.append(KeyUsage.getInstance(var9.readObject())).append(var2);
                  } else if (var6.equals(MiscObjectIdentifiers.netscapeCertType)) {
                     var1.append(new NetscapeCertType((ASN1BitString)var9.readObject())).append(var2);
                  } else if (var6.equals(MiscObjectIdentifiers.netscapeRevocationURL)) {
                     var1.append(new NetscapeRevocationURL((ASN1IA5String)var9.readObject())).append(var2);
                  } else if (var6.equals(MiscObjectIdentifiers.verisignCzagExtension)) {
                     var1.append(new VerisignCzagExtension((ASN1IA5String)var9.readObject())).append(var2);
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

      return var1.toString();
   }

   @Override
   public final void verify(PublicKey var1) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
      String var3 = X509SignatureUtil.getSignatureName(this.c.getSignatureAlgorithm());

      Signature var2;
      try {
         var2 = Signature.getInstance(var3, "BC");
      } catch (Exception var5) {
         var2 = Signature.getInstance(var3);
      }

      this.checkSignature(var1, var2);
   }

   @Override
   public final void verify(PublicKey var1, String var2) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
      String var3 = X509SignatureUtil.getSignatureName(this.c.getSignatureAlgorithm());
      Signature var4;
      if (var2 != null) {
         var4 = Signature.getInstance(var3, var2);
      } else {
         var4 = Signature.getInstance(var3);
      }

      this.checkSignature(var1, var4);
   }

   @Override
   public final void verify(PublicKey var1, Provider var2) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
      String var3 = X509SignatureUtil.getSignatureName(this.c.getSignatureAlgorithm());
      Signature var4;
      if (var2 != null) {
         var4 = Signature.getInstance(var3, var2);
      } else {
         var4 = Signature.getInstance(var3);
      }

      this.checkSignature(var1, var4);
   }

   private void checkSignature(PublicKey var1, Signature var2) throws CertificateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
      if (!this.isAlgIdEqual(this.c.getSignatureAlgorithm(), this.c.getTBSCertificate().getSignature())) {
         throw new CertificateException("signature algorithm in TBS cert not same as outer cert");
      } else {
         ASN1Encodable var3 = this.c.getSignatureAlgorithm().getParameters();
         X509SignatureUtil.setSignatureParameters(var2, var3);
         var2.initVerify(var1);
         var2.update(this.getTBSCertificate());
         if (!var2.verify(this.getSignature())) {
            throw new SignatureException("certificate does not verify with supplied key");
         }
      }
   }

   private boolean isAlgIdEqual(AlgorithmIdentifier var1, AlgorithmIdentifier var2) {
      if (!var1.getAlgorithm().equals(var2.getAlgorithm())) {
         return false;
      } else if (var1.getParameters() == null) {
         return var2.getParameters() == null || var2.getParameters().equals(DERNull.INSTANCE);
      } else {
         return var2.getParameters() == null
            ? var1.getParameters() == null || var1.getParameters().equals(DERNull.INSTANCE)
            : var1.getParameters().equals(var2.getParameters());
      }
   }

   private static Collection getAlternativeNames(Certificate var0, ASN1ObjectIdentifier var1) throws CertificateParsingException {
      byte[] var2 = getExtensionOctets(var0, var1);
      if (var2 == null) {
         return null;
      } else {
         try {
            ArrayList var3 = new ArrayList();
            Enumeration var4 = ASN1Sequence.getInstance(var2).getObjects();

            while (var4.hasMoreElements()) {
               GeneralName var5 = GeneralName.getInstance(var4.nextElement());
               ArrayList var6 = new ArrayList();
               var6.add(Integers.valueOf(var5.getTagNo()));
               switch (var5.getTagNo()) {
                  case 0:
                  case 3:
                  case 5:
                     var6.add(var5.getEncoded());
                     break;
                  case 1:
                  case 2:
                  case 6:
                     var6.add(((ASN1String)var5.getName()).getString());
                     break;
                  case 4:
                     var6.add(X500Name.getInstance(RFC4519Style.INSTANCE, var5.getName()).toString());
                     break;
                  case 7:
                     byte[] var7 = DEROctetString.getInstance(var5.getName()).getOctets();

                     String var8;
                     try {
                        var8 = InetAddress.getByAddress(var7).getHostAddress();
                     } catch (UnknownHostException var10) {
                        continue;
                     }

                     var6.add(var8);
                     break;
                  case 8:
                     var6.add(ASN1ObjectIdentifier.getInstance(var5.getName()).getId());
                     break;
                  default:
                     throw new IOException("Bad tag number: " + var5.getTagNo());
               }

               var3.add(Collections.unmodifiableList(var6));
            }

            return var3.size() == 0 ? null : Collections.unmodifiableCollection(var3);
         } catch (Exception var11) {
            throw new CertificateParsingException(var11.getMessage());
         }
      }
   }

   private static byte[] getExtensionOctets(Certificate var0, ASN1ObjectIdentifier var1) {
      ASN1OctetString var2 = Extensions.getExtensionValue(var0.getExtensions(), var1);
      return var2 == null ? null : var2.getOctets();
   }
}
