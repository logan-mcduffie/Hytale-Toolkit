package org.bouncycastle.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.Target;
import org.bouncycastle.asn1.x509.TargetInformation;
import org.bouncycastle.asn1.x509.Targets;
import org.bouncycastle.util.Selector;

/** @deprecated */
public class X509AttributeCertStoreSelector implements Selector {
   private AttributeCertificateHolder holder;
   private AttributeCertificateIssuer issuer;
   private BigInteger serialNumber;
   private Date attributeCertificateValid;
   private X509AttributeCertificate attributeCert;
   private Collection targetNames = new HashSet();
   private Collection targetGroups = new HashSet();

   @Override
   public boolean match(Object var1) {
      if (!(var1 instanceof X509AttributeCertificate)) {
         return false;
      } else {
         X509AttributeCertificate var2 = (X509AttributeCertificate)var1;
         if (this.attributeCert != null && !this.attributeCert.equals(var2)) {
            return false;
         } else if (this.serialNumber != null && !var2.getSerialNumber().equals(this.serialNumber)) {
            return false;
         } else if (this.holder != null && !var2.getHolder().equals(this.holder)) {
            return false;
         } else if (this.issuer != null && !var2.getIssuer().equals(this.issuer)) {
            return false;
         } else {
            if (this.attributeCertificateValid != null) {
               try {
                  var2.checkValidity(this.attributeCertificateValid);
               } catch (CertificateExpiredException var13) {
                  return false;
               } catch (CertificateNotYetValidException var14) {
                  return false;
               }
            }

            if (!this.targetNames.isEmpty() || !this.targetGroups.isEmpty()) {
               byte[] var3 = var2.getExtensionValue(Extension.targetInformation.getId());
               if (var3 != null) {
                  TargetInformation var4;
                  try {
                     var4 = TargetInformation.getInstance(new ASN1InputStream(((DEROctetString)DEROctetString.fromByteArray(var3)).getOctets()).readObject());
                  } catch (IOException var11) {
                     return false;
                  } catch (IllegalArgumentException var12) {
                     return false;
                  }

                  Targets[] var5 = var4.getTargetsObjects();
                  if (!this.targetNames.isEmpty()) {
                     boolean var6 = false;

                     for (int var7 = 0; var7 < var5.length; var7++) {
                        Targets var8 = var5[var7];
                        Target[] var9 = var8.getTargets();

                        for (int var10 = 0; var10 < var9.length; var10++) {
                           if (this.targetNames.contains(GeneralName.getInstance(var9[var10].getTargetName()))) {
                              var6 = true;
                              break;
                           }
                        }
                     }

                     if (!var6) {
                        return false;
                     }
                  }

                  if (!this.targetGroups.isEmpty()) {
                     boolean var15 = false;

                     for (int var16 = 0; var16 < var5.length; var16++) {
                        Targets var17 = var5[var16];
                        Target[] var18 = var17.getTargets();

                        for (int var19 = 0; var19 < var18.length; var19++) {
                           if (this.targetGroups.contains(GeneralName.getInstance(var18[var19].getTargetGroup()))) {
                              var15 = true;
                              break;
                           }
                        }
                     }

                     if (!var15) {
                        return false;
                     }
                  }
               }
            }

            return true;
         }
      }
   }

   @Override
   public Object clone() {
      X509AttributeCertStoreSelector var1 = new X509AttributeCertStoreSelector();
      var1.attributeCert = this.attributeCert;
      var1.attributeCertificateValid = this.getAttributeCertificateValid();
      var1.holder = this.holder;
      var1.issuer = this.issuer;
      var1.serialNumber = this.serialNumber;
      var1.targetGroups = this.getTargetGroups();
      var1.targetNames = this.getTargetNames();
      return var1;
   }

   public X509AttributeCertificate getAttributeCert() {
      return this.attributeCert;
   }

   public void setAttributeCert(X509AttributeCertificate var1) {
      this.attributeCert = var1;
   }

   public Date getAttributeCertificateValid() {
      return this.attributeCertificateValid != null ? new Date(this.attributeCertificateValid.getTime()) : null;
   }

   public void setAttributeCertificateValid(Date var1) {
      if (var1 != null) {
         this.attributeCertificateValid = new Date(var1.getTime());
      } else {
         this.attributeCertificateValid = null;
      }
   }

   public AttributeCertificateHolder getHolder() {
      return this.holder;
   }

   public void setHolder(AttributeCertificateHolder var1) {
      this.holder = var1;
   }

   public AttributeCertificateIssuer getIssuer() {
      return this.issuer;
   }

   public void setIssuer(AttributeCertificateIssuer var1) {
      this.issuer = var1;
   }

   public BigInteger getSerialNumber() {
      return this.serialNumber;
   }

   public void setSerialNumber(BigInteger var1) {
      this.serialNumber = var1;
   }

   public void addTargetName(GeneralName var1) {
      this.targetNames.add(var1);
   }

   public void addTargetName(byte[] var1) throws IOException {
      this.addTargetName(GeneralName.getInstance(ASN1Primitive.fromByteArray(var1)));
   }

   public void setTargetNames(Collection var1) throws IOException {
      this.targetNames = this.extractGeneralNames(var1);
   }

   public Collection getTargetNames() {
      return Collections.unmodifiableCollection(this.targetNames);
   }

   public void addTargetGroup(GeneralName var1) {
      this.targetGroups.add(var1);
   }

   public void addTargetGroup(byte[] var1) throws IOException {
      this.addTargetGroup(GeneralName.getInstance(ASN1Primitive.fromByteArray(var1)));
   }

   public void setTargetGroups(Collection var1) throws IOException {
      this.targetGroups = this.extractGeneralNames(var1);
   }

   public Collection getTargetGroups() {
      return Collections.unmodifiableCollection(this.targetGroups);
   }

   private Set extractGeneralNames(Collection var1) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         HashSet var2 = new HashSet();

         for (Object var4 : var1) {
            if (var4 instanceof GeneralName) {
               var2.add(var4);
            } else {
               var2.add(GeneralName.getInstance(ASN1Primitive.fromByteArray((byte[])var4)));
            }
         }

         return var2;
      } else {
         return new HashSet();
      }
   }
}
