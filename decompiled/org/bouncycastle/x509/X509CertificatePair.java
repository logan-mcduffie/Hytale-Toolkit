package org.bouncycastle.x509;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificatePair;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.provider.X509CertificateObject;

public class X509CertificatePair {
   private final JcaJceHelper bcHelper = new BCJcaJceHelper();
   private X509Certificate forward;
   private X509Certificate reverse;

   public X509CertificatePair(X509Certificate var1, X509Certificate var2) {
      this.forward = var1;
      this.reverse = var2;
   }

   public X509CertificatePair(CertificatePair var1) throws CertificateParsingException {
      if (var1.getForward() != null) {
         this.forward = new X509CertificateObject(var1.getForward());
      }

      if (var1.getReverse() != null) {
         this.reverse = new X509CertificateObject(var1.getReverse());
      }
   }

   public byte[] getEncoded() throws CertificateEncodingException {
      Certificate var1 = null;
      Certificate var2 = null;

      try {
         if (this.forward != null) {
            var1 = Certificate.getInstance(new ASN1InputStream(this.forward.getEncoded()).readObject());
            if (var1 == null) {
               throw new CertificateEncodingException("unable to get encoding for forward");
            }
         }

         if (this.reverse != null) {
            var2 = Certificate.getInstance(new ASN1InputStream(this.reverse.getEncoded()).readObject());
            if (var2 == null) {
               throw new CertificateEncodingException("unable to get encoding for reverse");
            }
         }

         return new CertificatePair(var1, var2).getEncoded("DER");
      } catch (IllegalArgumentException var4) {
         throw new ExtCertificateEncodingException(var4.toString(), var4);
      } catch (IOException var5) {
         throw new ExtCertificateEncodingException(var5.toString(), var5);
      }
   }

   public X509Certificate getForward() {
      return this.forward;
   }

   public X509Certificate getReverse() {
      return this.reverse;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (!(var1 instanceof X509CertificatePair)) {
         return false;
      } else {
         X509CertificatePair var2 = (X509CertificatePair)var1;
         boolean var3 = true;
         boolean var4 = true;
         if (this.forward != null) {
            var4 = this.forward.equals(var2.forward);
         } else if (var2.forward != null) {
            var4 = false;
         }

         if (this.reverse != null) {
            var3 = this.reverse.equals(var2.reverse);
         } else if (var2.reverse != null) {
            var3 = false;
         }

         return var4 && var3;
      }
   }

   @Override
   public int hashCode() {
      int var1 = -1;
      if (this.forward != null) {
         var1 ^= this.forward.hashCode();
      }

      if (this.reverse != null) {
         var1 *= 17;
         var1 ^= this.reverse.hashCode();
      }

      return var1;
   }
}
