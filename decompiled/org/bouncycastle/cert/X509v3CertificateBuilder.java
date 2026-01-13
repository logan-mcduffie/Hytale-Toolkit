package org.bouncycastle.cert;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.DeltaCertificateDescriptor;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.util.Exceptions;

public class X509v3CertificateBuilder {
   private V3TBSCertificateGenerator tbsGen = new V3TBSCertificateGenerator();
   private ExtensionsGenerator extGenerator;

   public X509v3CertificateBuilder(X500Name var1, BigInteger var2, Date var3, Date var4, X500Name var5, SubjectPublicKeyInfo var6) {
      this(var1, var2, new Time(var3), new Time(var4), var5, var6);
   }

   public X509v3CertificateBuilder(X500Name var1, BigInteger var2, Date var3, Date var4, Locale var5, X500Name var6, SubjectPublicKeyInfo var7) {
      this(var1, var2, new Time(var3, var5), new Time(var4, var5), var6, var7);
   }

   public X509v3CertificateBuilder(X500Name var1, BigInteger var2, Time var3, Time var4, X500Name var5, SubjectPublicKeyInfo var6) {
      this.tbsGen.setSerialNumber(new ASN1Integer(var2));
      this.tbsGen.setIssuer(var1);
      this.tbsGen.setStartDate(var3);
      this.tbsGen.setEndDate(var4);
      this.tbsGen.setSubject(var5);
      this.tbsGen.setSubjectPublicKeyInfo(var6);
      this.extGenerator = new ExtensionsGenerator();
   }

   public X509v3CertificateBuilder(X509CertificateHolder var1) {
      this.tbsGen.setSerialNumber(new ASN1Integer(var1.getSerialNumber()));
      this.tbsGen.setIssuer(var1.getIssuer());
      this.tbsGen.setStartDate(new Time(var1.getNotBefore()));
      this.tbsGen.setEndDate(new Time(var1.getNotAfter()));
      this.tbsGen.setSubject(var1.getSubject());
      this.tbsGen.setSubjectPublicKeyInfo(var1.getSubjectPublicKeyInfo());
      this.extGenerator = new ExtensionsGenerator();
      Extensions var2 = var1.getExtensions();
      Enumeration var3 = var2.oids();

      while (var3.hasMoreElements()) {
         ASN1ObjectIdentifier var4 = (ASN1ObjectIdentifier)var3.nextElement();
         if (!Extension.subjectAltPublicKeyInfo.equals(var4) && !Extension.altSignatureAlgorithm.equals(var4) && !Extension.altSignatureValue.equals(var4)) {
            this.extGenerator.addExtension(var2.getExtension(var4));
         }
      }
   }

   public boolean hasExtension(ASN1ObjectIdentifier var1) {
      return this.doGetExtension(var1) != null;
   }

   public Extension getExtension(ASN1ObjectIdentifier var1) {
      return this.doGetExtension(var1);
   }

   private Extension doGetExtension(ASN1ObjectIdentifier var1) {
      if (this.extGenerator.isEmpty()) {
         return null;
      } else {
         Extensions var2 = this.extGenerator.generate();
         return var2.getExtension(var1);
      }
   }

   public X509v3CertificateBuilder setSubjectUniqueID(boolean[] var1) {
      this.tbsGen.setSubjectUniqueID(booleanToBitString(var1));
      return this;
   }

   public X509v3CertificateBuilder setIssuerUniqueID(boolean[] var1) {
      this.tbsGen.setIssuerUniqueID(booleanToBitString(var1));
      return this;
   }

   public X509v3CertificateBuilder addExtension(ASN1ObjectIdentifier var1, boolean var2, ASN1Encodable var3) throws CertIOException {
      try {
         this.extGenerator.addExtension(var1, var2, var3);
         return this;
      } catch (IOException var5) {
         throw new CertIOException("cannot encode extension: " + var5.getMessage(), var5);
      }
   }

   public X509v3CertificateBuilder addExtension(Extension var1) throws CertIOException {
      this.extGenerator.addExtension(var1);
      return this;
   }

   public X509v3CertificateBuilder addExtension(ASN1ObjectIdentifier var1, boolean var2, byte[] var3) throws CertIOException {
      this.extGenerator.addExtension(var1, var2, var3);
      return this;
   }

   public X509v3CertificateBuilder replaceExtension(ASN1ObjectIdentifier var1, boolean var2, ASN1Encodable var3) throws CertIOException {
      try {
         this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(var1, var2, new DEROctetString(var3)));
         return this;
      } catch (IOException var5) {
         throw new CertIOException("cannot encode extension: " + var5.getMessage(), var5);
      }
   }

   public X509v3CertificateBuilder replaceExtension(Extension var1) throws CertIOException {
      this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, var1);
      return this;
   }

   public X509v3CertificateBuilder replaceExtension(ASN1ObjectIdentifier var1, boolean var2, byte[] var3) throws CertIOException {
      this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(var1, var2, var3));
      return this;
   }

   public X509v3CertificateBuilder removeExtension(ASN1ObjectIdentifier var1) {
      this.extGenerator = CertUtils.doRemoveExtension(this.extGenerator, var1);
      return this;
   }

   public X509v3CertificateBuilder copyAndAddExtension(ASN1ObjectIdentifier var1, boolean var2, X509CertificateHolder var3) {
      Certificate var4 = var3.toASN1Structure();
      Extension var5 = var4.getTBSCertificate().getExtensions().getExtension(var1);
      if (var5 == null) {
         throw new NullPointerException("extension " + var1 + " not present");
      } else {
         this.extGenerator.addExtension(var1, var2, var5.getExtnValue().getOctets());
         return this;
      }
   }

   public X509CertificateHolder build(ContentSigner var1) {
      AlgorithmIdentifier var2 = var1.getAlgorithmIdentifier();
      this.tbsGen.setSignature(var2);
      if (!this.extGenerator.isEmpty()) {
         Extension var3 = this.extGenerator.getExtension(Extension.deltaCertificateDescriptor);
         if (var3 != null) {
            DeltaCertificateDescriptor var4 = DeltaCertificateTool.trimDeltaCertificateDescriptor(
               DeltaCertificateDescriptor.getInstance(var3.getParsedValue()), this.tbsGen.generateTBSCertificate(), this.extGenerator.generate()
            );

            try {
               this.extGenerator.replaceExtension(Extension.deltaCertificateDescriptor, var3.isCritical(), var4);
            } catch (IOException var7) {
               throw new IllegalStateException("unable to replace deltaCertificateDescriptor: " + var7.getMessage());
            }
         }

         this.tbsGen.setExtensions(this.extGenerator.generate());
      }

      try {
         TBSCertificate var8 = this.tbsGen.generateTBSCertificate();
         byte[] var9 = generateSig(var1, var8);
         return new X509CertificateHolder(generateStructure(var8, var2, var9));
      } catch (IOException var6) {
         throw Exceptions.illegalArgumentException("cannot produce certificate signature", var6);
      }
   }

   public X509CertificateHolder build(ContentSigner var1, boolean var2, ContentSigner var3) {
      AlgorithmIdentifier var4 = var1.getAlgorithmIdentifier();
      AlgorithmIdentifier var5 = var3.getAlgorithmIdentifier();

      try {
         this.extGenerator.addExtension(Extension.altSignatureAlgorithm, var2, var5);
      } catch (IOException var12) {
         throw Exceptions.illegalStateException("cannot add altSignatureAlgorithm extension", var12);
      }

      Extension var6 = this.extGenerator.getExtension(Extension.deltaCertificateDescriptor);
      if (var6 != null) {
         this.tbsGen.setSignature(var4);

         try {
            ExtensionsGenerator var7 = new ExtensionsGenerator();
            var7.addExtensions(this.extGenerator.generate());
            var7.addExtension(Extension.altSignatureValue, false, DERNull.INSTANCE);
            DeltaCertificateDescriptor var8 = DeltaCertificateTool.trimDeltaCertificateDescriptor(
               DeltaCertificateDescriptor.getInstance(var6.getParsedValue()), this.tbsGen.generateTBSCertificate(), var7.generate()
            );
            this.extGenerator.replaceExtension(Extension.deltaCertificateDescriptor, var6.isCritical(), var8);
         } catch (IOException var11) {
            throw new IllegalStateException("unable to replace deltaCertificateDescriptor: " + var11.getMessage());
         }
      }

      this.tbsGen.setSignature(null);
      this.tbsGen.setExtensions(this.extGenerator.generate());

      try {
         byte[] var13 = generateSig(var3, this.tbsGen.generatePreTBSCertificate());
         this.extGenerator.addExtension(Extension.altSignatureValue, var2, new DERBitString(var13));
         this.tbsGen.setSignature(var4);
         this.tbsGen.setExtensions(this.extGenerator.generate());
         TBSCertificate var14 = this.tbsGen.generateTBSCertificate();
         byte[] var9 = generateSig(var1, var14);
         return new X509CertificateHolder(generateStructure(var14, var4, var9));
      } catch (IOException var10) {
         throw Exceptions.illegalArgumentException("cannot produce certificate signature", var10);
      }
   }

   private static byte[] generateSig(ContentSigner var0, ASN1Object var1) throws IOException {
      OutputStream var2 = var0.getOutputStream();
      var1.encodeTo(var2, "DER");
      var2.close();
      return var0.getSignature();
   }

   private static Certificate generateStructure(TBSCertificate var0, AlgorithmIdentifier var1, byte[] var2) {
      ASN1EncodableVector var3 = new ASN1EncodableVector(3);
      var3.add(var0);
      var3.add(var1);
      var3.add(new DERBitString(var2));
      return Certificate.getInstance(new DERSequence(var3));
   }

   static DERBitString booleanToBitString(boolean[] var0) {
      byte[] var1 = new byte[(var0.length + 7) / 8];

      for (int var2 = 0; var2 != var0.length; var2++) {
         var1[var2 >>> 3] = (byte)(var1[var2 >>> 3] | (var0[var2] ? (byte)(128 >> (var2 & 7)) : 0));
      }

      return new DERBitString(var1, 8 - var0.length & 7);
   }
}
