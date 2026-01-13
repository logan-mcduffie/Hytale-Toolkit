package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.security.cert.CRLException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Arrays;

class X509CRLObject extends X509CRLImpl {
   private final Object cacheLock = new Object();
   private X509CRLInternal internalCRLValue;
   private volatile boolean hashValueSet;
   private volatile int hashValue;

   X509CRLObject(JcaJceHelper var1, CertificateList var2) throws CRLException {
      super(var1, var2, createSigAlgName(var2), createSigAlgParams(var2), isIndirectCRL(var2));
   }

   @Override
   public byte[] getEncoded() throws CRLException {
      return Arrays.clone(this.getInternalCRL().getEncoded());
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof X509CRLObject)) {
         return this.getInternalCRL().equals(var1);
      } else {
         X509CRLObject var2 = (X509CRLObject)var1;
         if (this.hashValueSet && var2.hashValueSet) {
            if (this.hashValue != var2.hashValue) {
               return false;
            }
         } else if (null == this.internalCRLValue || null == var2.internalCRLValue) {
            ASN1BitString var3 = this.c.getSignature();
            if (null != var3 && !var3.equals(var2.c.getSignature())) {
               return false;
            }
         }

         return this.getInternalCRL().equals(var2.getInternalCRL());
      }
   }

   @Override
   public int hashCode() {
      if (!this.hashValueSet) {
         this.hashValue = this.getInternalCRL().hashCode();
         this.hashValueSet = true;
      }

      return this.hashValue;
   }

   private X509CRLInternal getInternalCRL() {
      synchronized (this.cacheLock) {
         if (null != this.internalCRLValue) {
            return this.internalCRLValue;
         }
      }

      byte[] var9 = null;
      X509CRLObject.X509CRLException var2 = null;

      try {
         var9 = this.c.getEncoded("DER");
      } catch (IOException var7) {
         var2 = new X509CRLObject.X509CRLException(var7);
      }

      X509CRLInternal var3 = new X509CRLInternal(this.bcHelper, this.c, this.sigAlgName, this.sigAlgParams, this.isIndirect, var9, var2);
      synchronized (this.cacheLock) {
         if (null == this.internalCRLValue) {
            this.internalCRLValue = var3;
         }

         return this.internalCRLValue;
      }
   }

   private static String createSigAlgName(CertificateList var0) throws CRLException {
      try {
         return X509SignatureUtil.getSignatureName(var0.getSignatureAlgorithm());
      } catch (Exception var2) {
         throw new X509CRLObject.X509CRLException("CRL contents invalid: " + var2.getMessage(), var2);
      }
   }

   private static byte[] createSigAlgParams(CertificateList var0) throws CRLException {
      try {
         ASN1Encodable var1 = var0.getSignatureAlgorithm().getParameters();
         return null == var1 ? null : var1.toASN1Primitive().getEncoded("DER");
      } catch (Exception var2) {
         throw new CRLException("CRL contents invalid: " + var2);
      }
   }

   private static boolean isIndirectCRL(CertificateList var0) throws CRLException {
      try {
         byte[] var1 = getExtensionOctets(var0, Extension.issuingDistributionPoint);
         return null == var1 ? false : IssuingDistributionPoint.getInstance(var1).isIndirectCRL();
      } catch (Exception var2) {
         throw new ExtCRLException("Exception reading IssuingDistributionPoint", var2);
      }
   }

   private static class X509CRLException extends CRLException {
      private final Throwable cause;

      X509CRLException(String var1, Throwable var2) {
         super(var1);
         this.cause = var2;
      }

      X509CRLException(Throwable var1) {
         this.cause = var1;
      }

      @Override
      public Throwable getCause() {
         return this.cause;
      }
   }
}
