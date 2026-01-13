package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.util.Date;
import java.util.Enumeration;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.util.Arrays;

class X509CertificateObject extends X509CertificateImpl implements PKCS12BagAttributeCarrier {
   private final Object cacheLock = new Object();
   private X509CertificateInternal internalCertificateValue;
   private X500Principal issuerValue;
   private PublicKey publicKeyValue;
   private X500Principal subjectValue;
   private long[] validityValues;
   private volatile boolean hashValueSet;
   private volatile int hashValue;
   private PKCS12BagAttributeCarrier attrCarrier = new PKCS12BagAttributeCarrierImpl();

   X509CertificateObject(JcaJceHelper var1, Certificate var2) throws CertificateParsingException {
      super(var1, var2, createBasicConstraints(var2), createKeyUsage(var2), createSigAlgName(var2), createSigAlgParams(var2));
   }

   @Override
   public void checkValidity(Date var1) throws CertificateExpiredException, CertificateNotYetValidException {
      long var2 = var1.getTime();
      long[] var4 = this.getValidityValues();
      if (var2 > var4[1]) {
         throw new CertificateExpiredException("certificate expired on " + this.c.getEndDate().getTime());
      } else if (var2 < var4[0]) {
         throw new CertificateNotYetValidException("certificate not valid till " + this.c.getStartDate().getTime());
      }
   }

   @Override
   public X500Principal getIssuerX500Principal() {
      synchronized (this.cacheLock) {
         if (null != this.issuerValue) {
            return this.issuerValue;
         }
      }

      X500Principal var6 = super.getIssuerX500Principal();
      synchronized (this.cacheLock) {
         if (null == this.issuerValue) {
            this.issuerValue = var6;
         }

         return this.issuerValue;
      }
   }

   @Override
   public PublicKey getPublicKey() {
      synchronized (this.cacheLock) {
         if (null != this.publicKeyValue) {
            return this.publicKeyValue;
         }
      }

      PublicKey var6 = super.getPublicKey();
      if (null == var6) {
         return null;
      } else {
         synchronized (this.cacheLock) {
            if (null == this.publicKeyValue) {
               this.publicKeyValue = var6;
            }

            return this.publicKeyValue;
         }
      }
   }

   @Override
   public X500Principal getSubjectX500Principal() {
      synchronized (this.cacheLock) {
         if (null != this.subjectValue) {
            return this.subjectValue;
         }
      }

      X500Principal var6 = super.getSubjectX500Principal();
      synchronized (this.cacheLock) {
         if (null == this.subjectValue) {
            this.subjectValue = var6;
         }

         return this.subjectValue;
      }
   }

   public long[] getValidityValues() {
      synchronized (this.cacheLock) {
         if (null != this.validityValues) {
            return this.validityValues;
         }
      }

      long[] var6 = new long[]{super.getNotBefore().getTime(), super.getNotAfter().getTime()};
      synchronized (this.cacheLock) {
         if (null == this.validityValues) {
            this.validityValues = var6;
         }

         return this.validityValues;
      }
   }

   @Override
   public byte[] getEncoded() throws CertificateEncodingException {
      return Arrays.clone(this.getInternalCertificate().getEncoded());
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof X509CertificateObject)) {
         return this.getInternalCertificate().equals(var1);
      } else {
         X509CertificateObject var2 = (X509CertificateObject)var1;
         if (this.hashValueSet && var2.hashValueSet) {
            if (this.hashValue != var2.hashValue) {
               return false;
            }
         } else if (null == this.internalCertificateValue || null == var2.internalCertificateValue) {
            ASN1BitString var3 = this.c.getSignature();
            if (null != var3 && !var3.equals(var2.c.getSignature())) {
               return false;
            }
         }

         return this.getInternalCertificate().equals(var2.getInternalCertificate());
      }
   }

   @Override
   public int hashCode() {
      if (!this.hashValueSet) {
         this.hashValue = this.getInternalCertificate().hashCode();
         this.hashValueSet = true;
      }

      return this.hashValue;
   }

   public int originalHashCode() {
      try {
         int var1 = 0;
         byte[] var2 = this.getInternalCertificate().getEncoded();

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

   private X509CertificateInternal getInternalCertificate() {
      synchronized (this.cacheLock) {
         if (null != this.internalCertificateValue) {
            return this.internalCertificateValue;
         }
      }

      byte[] var9 = null;
      X509CertificateObject.X509CertificateEncodingException var2 = null;

      try {
         var9 = this.c.getEncoded("DER");
      } catch (IOException var7) {
         var2 = new X509CertificateObject.X509CertificateEncodingException(var7);
      }

      X509CertificateInternal var3 = new X509CertificateInternal(
         this.bcHelper, this.c, this.basicConstraints, this.keyUsage, this.sigAlgName, this.sigAlgParams, var9, var2
      );
      synchronized (this.cacheLock) {
         if (null == this.internalCertificateValue) {
            this.internalCertificateValue = var3;
         }

         return this.internalCertificateValue;
      }
   }

   private static BasicConstraints createBasicConstraints(Certificate var0) throws CertificateParsingException {
      try {
         byte[] var1 = getExtensionOctets(var0, Extension.basicConstraints);
         return null == var1 ? null : BasicConstraints.getInstance(var1);
      } catch (Exception var2) {
         throw new CertificateParsingException("cannot construct BasicConstraints: " + var2);
      }
   }

   private static boolean[] createKeyUsage(Certificate var0) throws CertificateParsingException {
      try {
         byte[] var1 = getExtensionOctets(var0, Extension.keyUsage);
         if (null == var1) {
            return null;
         } else {
            ASN1BitString var2 = ASN1BitString.getInstance(var1);
            byte[] var3 = var2.getBytes();
            int var4 = var3.length * 8 - var2.getPadBits();
            boolean[] var5 = new boolean[var4 < 9 ? 9 : var4];

            for (int var6 = 0; var6 != var4; var6++) {
               var5[var6] = (var3[var6 / 8] & 128 >>> var6 % 8) != 0;
            }

            return var5;
         }
      } catch (Exception var7) {
         throw new CertificateParsingException("cannot construct KeyUsage: " + var7);
      }
   }

   private static String createSigAlgName(Certificate var0) throws CertificateParsingException {
      try {
         return X509SignatureUtil.getSignatureName(var0.getSignatureAlgorithm());
      } catch (Exception var2) {
         throw new CertificateParsingException("cannot construct SigAlgName: " + var2);
      }
   }

   private static byte[] createSigAlgParams(Certificate var0) throws CertificateParsingException {
      try {
         ASN1Encodable var1 = var0.getSignatureAlgorithm().getParameters();
         return null == var1 ? null : var1.toASN1Primitive().getEncoded("DER");
      } catch (Exception var2) {
         throw new CertificateParsingException("cannot construct SigAlgParams: " + var2);
      }
   }

   private static class X509CertificateEncodingException extends CertificateEncodingException {
      private final Throwable cause;

      X509CertificateEncodingException(Throwable var1) {
         this.cause = var1;
      }

      @Override
      public Throwable getCause() {
         return this.cause;
      }
   }
}
