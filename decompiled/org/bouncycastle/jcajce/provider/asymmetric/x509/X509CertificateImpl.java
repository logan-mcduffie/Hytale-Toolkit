package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.BufferedOutputStream;
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
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
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
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.NetscapeCertType;
import org.bouncycastle.internal.asn1.misc.NetscapeRevocationURL;
import org.bouncycastle.internal.asn1.misc.VerisignCzagExtension;
import org.bouncycastle.jcajce.CompositePublicKey;
import org.bouncycastle.jcajce.interfaces.BCX509Certificate;
import org.bouncycastle.jcajce.io.OutputStreamFactory;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Strings;

abstract class X509CertificateImpl extends X509Certificate implements BCX509Certificate {
   protected JcaJceHelper bcHelper;
   protected Certificate c;
   protected BasicConstraints basicConstraints;
   protected boolean[] keyUsage;
   protected String sigAlgName;
   protected byte[] sigAlgParams;

   X509CertificateImpl(JcaJceHelper var1, Certificate var2, BasicConstraints var3, boolean[] var4, String var5, byte[] var6) {
      this.bcHelper = var1;
      this.c = var2;
      this.basicConstraints = var3;
      this.keyUsage = var4;
      this.sigAlgName = var5;
      this.sigAlgParams = var6;
   }

   @Override
   public X500Name getIssuerX500Name() {
      return this.c.getIssuer();
   }

   @Override
   public TBSCertificate getTBSCertificateNative() {
      return this.c.getTBSCertificate();
   }

   @Override
   public X500Name getSubjectX500Name() {
      return this.c.getSubject();
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
         byte[] var1 = this.c.getIssuer().getEncoded("DER");
         return new X500Principal(var1);
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
         byte[] var1 = this.c.getSubject().getEncoded("DER");
         return new X500Principal(var1);
      } catch (IOException var2) {
         throw new IllegalStateException("can't encode subject DN");
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
      return Arrays.clone(this.keyUsage);
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
         throw Exceptions.illegalStateException("failed to recover public key: " + var2.getMessage(), var2);
      }
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
      X509SignatureUtil.prettyPrintSignature(this.getSignature(), var1, var2);
      Extensions var3 = this.c.getExtensions();
      if (var3 != null) {
         Enumeration var4 = var3.oids();
         if (var4.hasMoreElements()) {
            var1.append("       Extensions: \n");
         }

         while (var4.hasMoreElements()) {
            ASN1ObjectIdentifier var5 = (ASN1ObjectIdentifier)var4.nextElement();
            Extension var6 = var3.getExtension(var5);
            if (var6.getExtnValue() != null) {
               byte[] var7 = var6.getExtnValue().getOctets();
               ASN1InputStream var8 = new ASN1InputStream(var7);
               var1.append("                       critical(").append(var6.isCritical()).append(") ");

               try {
                  if (var5.equals(Extension.basicConstraints)) {
                     var1.append(BasicConstraints.getInstance(var8.readObject())).append(var2);
                  } else if (var5.equals(Extension.keyUsage)) {
                     var1.append(KeyUsage.getInstance(var8.readObject())).append(var2);
                  } else if (var5.equals(MiscObjectIdentifiers.netscapeCertType)) {
                     var1.append(new NetscapeCertType(ASN1BitString.getInstance(var8.readObject()))).append(var2);
                  } else if (var5.equals(MiscObjectIdentifiers.netscapeRevocationURL)) {
                     var1.append(new NetscapeRevocationURL(ASN1IA5String.getInstance(var8.readObject()))).append(var2);
                  } else if (var5.equals(MiscObjectIdentifiers.verisignCzagExtension)) {
                     var1.append(new VerisignCzagExtension(ASN1IA5String.getInstance(var8.readObject()))).append(var2);
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

      return var1.toString();
   }

   @Override
   public final void verify(PublicKey var1) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
      this.doVerify(var1, new SignatureCreator() {
         @Override
         public Signature createSignature(String var1) throws NoSuchAlgorithmException {
            try {
               return X509CertificateImpl.this.bcHelper.createSignature(var1);
            } catch (Exception var3) {
               return Signature.getInstance(var1);
            }
         }
      });
   }

   @Override
   public final void verify(PublicKey var1, final String var2) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
      this.doVerify(var1, new SignatureCreator() {
         @Override
         public Signature createSignature(String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
            return var2 != null ? Signature.getInstance(var1, var2) : Signature.getInstance(var1);
         }
      });
   }

   @Override
   public final void verify(PublicKey var1, final Provider var2) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
      try {
         this.doVerify(var1, new SignatureCreator() {
            @Override
            public Signature createSignature(String var1) throws NoSuchAlgorithmException {
               return var2 != null ? Signature.getInstance(var1, var2) : Signature.getInstance(var1);
            }
         });
      } catch (NoSuchProviderException var4) {
         throw new NoSuchAlgorithmException("provider issue: " + var4.getMessage());
      }
   }

   private void doVerify(PublicKey var1, SignatureCreator var2) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
      if (var1 instanceof CompositePublicKey && X509SignatureUtil.isCompositeAlgorithm(this.c.getSignatureAlgorithm())) {
         List var19 = ((CompositePublicKey)var1).getPublicKeys();
         ASN1Sequence var21 = ASN1Sequence.getInstance(this.c.getSignatureAlgorithm().getParameters());
         ASN1Sequence var23 = ASN1Sequence.getInstance(this.c.getSignature().getOctets());
         boolean var24 = false;

         for (int var25 = 0; var25 != var19.size(); var25++) {
            if (var19.get(var25) != null) {
               AlgorithmIdentifier var26 = AlgorithmIdentifier.getInstance(var21.getObjectAt(var25));
               String var27 = X509SignatureUtil.getSignatureName(var26);
               Signature var28 = var2.createSignature(var27);
               SignatureException var11 = null;

               try {
                  this.checkSignature(
                     (PublicKey)var19.get(var25), var28, var26.getParameters(), ASN1BitString.getInstance(var23.getObjectAt(var25)).getOctets()
                  );
                  var24 = true;
               } catch (SignatureException var16) {
                  var11 = var16;
               }

               if (var11 != null) {
                  throw var11;
               }
            }
         }

         if (!var24) {
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
            } catch (InvalidKeyException var13) {
            } catch (NoSuchAlgorithmException var14) {
            } catch (SignatureException var15) {
               var9 = var15;
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
         if (var1 instanceof CompositePublicKey
            && MiscObjectIdentifiers.id_composite_key.equals(((CompositePublicKey)var1).getAlgorithmIdentifier().getAlgorithm())) {
            List var20 = ((CompositePublicKey)var1).getPublicKeys();

            for (int var22 = 0; var22 != var20.size(); var22++) {
               try {
                  this.checkSignature((PublicKey)var20.get(var22), var18, this.c.getSignatureAlgorithm().getParameters(), this.getSignature());
                  return;
               } catch (InvalidKeyException var17) {
               }
            }

            throw new InvalidKeyException("no matching signature found");
         }

         this.checkSignature(var1, var18, this.c.getSignatureAlgorithm().getParameters(), this.getSignature());
      }
   }

   private void checkSignature(PublicKey var1, Signature var2, ASN1Encodable var3, byte[] var4) throws CertificateException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
      if (!X509SignatureUtil.areEquivalentAlgorithms(this.c.getSignatureAlgorithm(), this.c.getTBSCertificate().getSignature())) {
         throw new CertificateException("signature algorithm in TBS cert not same as outer cert");
      } else {
         X509SignatureUtil.setSignatureParameters(var2, var3);
         var2.initVerify(var1);

         try {
            BufferedOutputStream var5 = new BufferedOutputStream(OutputStreamFactory.createStream(var2), 512);
            this.c.getTBSCertificate().encodeTo(var5, "DER");
            var5.close();
         } catch (IOException var6) {
            throw new CertificateEncodingException(var6.toString());
         }

         if (!var2.verify(var4)) {
            throw new SignatureException("certificate does not verify with supplied key");
         }
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

   static byte[] getExtensionOctets(Certificate var0, ASN1ObjectIdentifier var1) {
      ASN1OctetString var2 = Extensions.getExtensionValue(var0.getExtensions(), var1);
      return var2 == null ? null : var2.getOctets();
   }
}
