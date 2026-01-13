package org.bouncycastle.x509;

import java.io.IOException;
import java.security.Principal;
import java.security.cert.CertSelector;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AttCertIssuer;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.V2Form;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.util.Selector;

/** @deprecated */
public class AttributeCertificateIssuer implements CertSelector, Selector {
   final ASN1Encodable form;

   public AttributeCertificateIssuer(AttCertIssuer var1) {
      this.form = var1.getIssuer();
   }

   public AttributeCertificateIssuer(X500Principal var1) throws IOException {
      this(new X509Principal(var1.getEncoded()));
   }

   public AttributeCertificateIssuer(X509Principal var1) {
      this.form = new V2Form(GeneralNames.getInstance(new DERSequence(new GeneralName(var1))));
   }

   private Object[] getNames() {
      GeneralNames var1;
      if (this.form instanceof V2Form) {
         var1 = ((V2Form)this.form).getIssuerName();
      } else {
         var1 = (GeneralNames)this.form;
      }

      GeneralName[] var2 = var1.getNames();
      ArrayList var3 = new ArrayList(var2.length);

      for (int var4 = 0; var4 != var2.length; var4++) {
         if (var2[var4].getTagNo() == 4) {
            try {
               var3.add(new X500Principal(var2[var4].getName().toASN1Primitive().getEncoded()));
            } catch (IOException var6) {
               throw new RuntimeException("badly formed Name object");
            }
         }
      }

      return var3.toArray(new Object[var3.size()]);
   }

   public Principal[] getPrincipals() {
      Object[] var1 = this.getNames();
      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 != var1.length; var3++) {
         if (var1[var3] instanceof Principal) {
            var2.add(var1[var3]);
         }
      }

      return var2.toArray(new Principal[var2.size()]);
   }

   private boolean matchesDN(X500Principal var1, GeneralNames var2) {
      GeneralName[] var3 = var2.getNames();

      for (int var4 = 0; var4 != var3.length; var4++) {
         GeneralName var5 = var3[var4];
         if (var5.getTagNo() == 4) {
            try {
               if (new X500Principal(var5.getName().toASN1Primitive().getEncoded()).equals(var1)) {
                  return true;
               }
            } catch (IOException var7) {
            }
         }
      }

      return false;
   }

   @Override
   public Object clone() {
      return new AttributeCertificateIssuer(AttCertIssuer.getInstance(this.form));
   }

   @Override
   public boolean match(Certificate var1) {
      if (!(var1 instanceof X509Certificate)) {
         return false;
      } else {
         X509Certificate var2 = (X509Certificate)var1;
         if (this.form instanceof V2Form) {
            V2Form var3 = (V2Form)this.form;
            if (var3.getBaseCertificateID() != null) {
               return var3.getBaseCertificateID().getSerial().hasValue(var2.getSerialNumber())
                  && this.matchesDN(var2.getIssuerX500Principal(), var3.getBaseCertificateID().getIssuer());
            }

            GeneralNames var4 = var3.getIssuerName();
            if (this.matchesDN(var2.getSubjectX500Principal(), var4)) {
               return true;
            }
         } else {
            GeneralNames var5 = (GeneralNames)this.form;
            if (this.matchesDN(var2.getSubjectX500Principal(), var5)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof AttributeCertificateIssuer)) {
         return false;
      } else {
         AttributeCertificateIssuer var2 = (AttributeCertificateIssuer)var1;
         return this.form.equals(var2.form);
      }
   }

   @Override
   public int hashCode() {
      return this.form.hashCode();
   }

   @Override
   public boolean match(Object var1) {
      return !(var1 instanceof X509Certificate) ? false : this.match((Certificate)var1);
   }
}
