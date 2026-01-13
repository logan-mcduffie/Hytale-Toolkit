package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CRLException;
import java.security.cert.X509CRLEntry;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.util.Strings;

public class X509CRLEntryObject extends X509CRLEntry {
   private TBSCertList.CRLEntry c;
   private X500Name certificateIssuer;
   private int hashValue;
   private boolean isHashValueSet;

   public X509CRLEntryObject(TBSCertList.CRLEntry var1) {
      this.c = var1;
      this.certificateIssuer = null;
   }

   public X509CRLEntryObject(TBSCertList.CRLEntry var1, boolean var2, X500Name var3) {
      this.c = var1;
      this.certificateIssuer = this.loadCertificateIssuer(var2, var3);
   }

   @Override
   public boolean hasUnsupportedCriticalExtension() {
      Extensions var1 = this.c.getExtensions();
      return var1 != null && var1.hasAnyCriticalExtensions();
   }

   private X500Name loadCertificateIssuer(boolean var1, X500Name var2) {
      if (!var1) {
         return null;
      } else {
         ASN1OctetString var3 = Extensions.getExtensionValue(this.c.getExtensions(), Extension.certificateIssuer);
         if (var3 == null) {
            return var2;
         } else {
            try {
               GeneralName[] var4 = GeneralNames.getInstance(var3.getOctets()).getNames();

               for (int var5 = 0; var5 < var4.length; var5++) {
                  if (var4[var5].getTagNo() == 4) {
                     return X500Name.getInstance(var4[var5].getName());
                  }
               }

               return null;
            } catch (Exception var6) {
               return null;
            }
         }
      }
   }

   @Override
   public X500Principal getCertificateIssuer() {
      if (this.certificateIssuer == null) {
         return null;
      } else {
         try {
            return new X500Principal(this.certificateIssuer.getEncoded());
         } catch (IOException var2) {
            return null;
         }
      }
   }

   private Set getExtensionOIDs(boolean var1) {
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
      } else {
         return null;
      }
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
   public int hashCode() {
      if (!this.isHashValueSet) {
         this.hashValue = super.hashCode();
         this.isHashValueSet = true;
      }

      return this.hashValue;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof X509CRLEntryObject) {
         X509CRLEntryObject var2 = (X509CRLEntryObject)var1;
         return this.c.equals(var2.c);
      } else {
         return super.equals(this);
      }
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
   public BigInteger getSerialNumber() {
      return this.c.getUserCertificate().getValue();
   }

   @Override
   public Date getRevocationDate() {
      return this.c.getRevocationDate().getDate();
   }

   @Override
   public boolean hasExtensions() {
      return this.c.getExtensions() != null;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = Strings.lineSeparator();
      var1.append("      userCertificate: ").append(this.getSerialNumber()).append(var2);
      var1.append("       revocationDate: ").append(this.getRevocationDate()).append(var2);
      var1.append("       certificateIssuer: ").append(this.getCertificateIssuer()).append(var2);
      Extensions var3 = this.c.getExtensions();
      if (var3 != null) {
         Enumeration var4 = var3.oids();
         if (var4.hasMoreElements()) {
            var1.append("   crlEntryExtensions:").append(var2);

            while (var4.hasMoreElements()) {
               ASN1ObjectIdentifier var5 = (ASN1ObjectIdentifier)var4.nextElement();
               Extension var6 = var3.getExtension(var5);
               if (var6.getExtnValue() != null) {
                  byte[] var7 = var6.getExtnValue().getOctets();
                  ASN1InputStream var8 = new ASN1InputStream(var7);
                  var1.append("                       critical(").append(var6.isCritical()).append(") ");

                  try {
                     if (var5.equals(Extension.reasonCode)) {
                        var1.append(CRLReason.getInstance(ASN1Enumerated.getInstance(var8.readObject()))).append(var2);
                     } else if (var5.equals(Extension.certificateIssuer)) {
                        var1.append("Certificate issuer: ").append(GeneralNames.getInstance(var8.readObject())).append(var2);
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
      }

      return var1.toString();
   }
}
