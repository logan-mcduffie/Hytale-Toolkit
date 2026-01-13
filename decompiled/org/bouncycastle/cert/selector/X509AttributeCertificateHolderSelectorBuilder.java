package org.bouncycastle.cert.selector;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.X509AttributeCertificateHolder;

public class X509AttributeCertificateHolderSelectorBuilder {
   private AttributeCertificateHolder holder;
   private AttributeCertificateIssuer issuer;
   private BigInteger serialNumber;
   private Date attributeCertificateValid;
   private X509AttributeCertificateHolder attributeCert;
   private Collection targetNames = new HashSet();
   private Collection targetGroups = new HashSet();

   public void setAttributeCert(X509AttributeCertificateHolder var1) {
      this.attributeCert = var1;
   }

   public void setAttributeCertificateValid(Date var1) {
      if (var1 != null) {
         this.attributeCertificateValid = new Date(var1.getTime());
      } else {
         this.attributeCertificateValid = null;
      }
   }

   public void setHolder(AttributeCertificateHolder var1) {
      this.holder = var1;
   }

   public void setIssuer(AttributeCertificateIssuer var1) {
      this.issuer = var1;
   }

   public void setSerialNumber(BigInteger var1) {
      this.serialNumber = var1;
   }

   public void addTargetName(GeneralName var1) {
      this.targetNames.add(var1);
   }

   public void setTargetNames(Collection var1) throws IOException {
      this.targetNames = this.extractGeneralNames(var1);
   }

   public void addTargetGroup(GeneralName var1) {
      this.targetGroups.add(var1);
   }

   public void setTargetGroups(Collection var1) throws IOException {
      this.targetGroups = this.extractGeneralNames(var1);
   }

   private Set extractGeneralNames(Collection var1) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         HashSet var2 = new HashSet();
         Iterator var3 = var1.iterator();

         while (var3.hasNext()) {
            var2.add(GeneralName.getInstance(var3.next()));
         }

         return var2;
      } else {
         return new HashSet();
      }
   }

   public X509AttributeCertificateHolderSelector build() {
      return new X509AttributeCertificateHolderSelector(
         this.holder,
         this.issuer,
         this.serialNumber,
         this.attributeCertificateValid,
         this.attributeCert,
         Collections.unmodifiableCollection(new HashSet(this.targetNames)),
         Collections.unmodifiableCollection(new HashSet(this.targetGroups))
      );
   }
}
