package org.bouncycastle.cert.selector;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.Target;
import org.bouncycastle.asn1.x509.TargetInformation;
import org.bouncycastle.asn1.x509.Targets;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.util.Selector;

public class X509AttributeCertificateHolderSelector implements Selector {
   private final AttributeCertificateHolder holder;
   private final AttributeCertificateIssuer issuer;
   private final BigInteger serialNumber;
   private final Date attributeCertificateValid;
   private final X509AttributeCertificateHolder attributeCert;
   private final Collection targetNames;
   private final Collection targetGroups;

   X509AttributeCertificateHolderSelector(
      AttributeCertificateHolder var1,
      AttributeCertificateIssuer var2,
      BigInteger var3,
      Date var4,
      X509AttributeCertificateHolder var5,
      Collection var6,
      Collection var7
   ) {
      this.holder = var1;
      this.issuer = var2;
      this.serialNumber = var3;
      this.attributeCertificateValid = var4;
      this.attributeCert = var5;
      this.targetNames = var6;
      this.targetGroups = var7;
   }

   @Override
   public boolean match(Object var1) {
      if (!(var1 instanceof X509AttributeCertificateHolder)) {
         return false;
      } else {
         X509AttributeCertificateHolder var2 = (X509AttributeCertificateHolder)var1;
         if (this.attributeCert != null && !this.attributeCert.equals(var2)) {
            return false;
         } else if (this.serialNumber != null && !var2.getSerialNumber().equals(this.serialNumber)) {
            return false;
         } else if (this.holder != null && !var2.getHolder().equals(this.holder)) {
            return false;
         } else if (this.issuer != null && !var2.getIssuer().equals(this.issuer)) {
            return false;
         } else if (this.attributeCertificateValid != null && !var2.isValidOn(this.attributeCertificateValid)) {
            return false;
         } else {
            if (!this.targetNames.isEmpty() || !this.targetGroups.isEmpty()) {
               Extension var3 = var2.getExtension(Extension.targetInformation);
               if (var3 != null) {
                  TargetInformation var4;
                  try {
                     var4 = TargetInformation.getInstance(var3.getParsedValue());
                  } catch (IllegalArgumentException var11) {
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
                     boolean var12 = false;

                     for (int var13 = 0; var13 < var5.length; var13++) {
                        Targets var14 = var5[var13];
                        Target[] var15 = var14.getTargets();

                        for (int var16 = 0; var16 < var15.length; var16++) {
                           if (this.targetGroups.contains(GeneralName.getInstance(var15[var16].getTargetGroup()))) {
                              var12 = true;
                              break;
                           }
                        }
                     }

                     if (!var12) {
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
      return new X509AttributeCertificateHolderSelector(
         this.holder, this.issuer, this.serialNumber, this.attributeCertificateValid, this.attributeCert, this.targetNames, this.targetGroups
      );
   }

   public X509AttributeCertificateHolder getAttributeCert() {
      return this.attributeCert;
   }

   public Date getAttributeCertificateValid() {
      return this.attributeCertificateValid != null ? new Date(this.attributeCertificateValid.getTime()) : null;
   }

   public AttributeCertificateHolder getHolder() {
      return this.holder;
   }

   public AttributeCertificateIssuer getIssuer() {
      return this.issuer;
   }

   public BigInteger getSerialNumber() {
      return this.serialNumber;
   }

   public Collection getTargetNames() {
      return this.targetNames;
   }

   public Collection getTargetGroups() {
      return this.targetGroups;
   }
}
