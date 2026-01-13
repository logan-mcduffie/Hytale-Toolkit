package org.bouncycastle.cert;

import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.Holder;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.asn1.x509.ObjectDigestInfo;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

public class AttributeCertificateHolder implements Selector {
   private static DigestCalculatorProvider digestCalculatorProvider;
   final Holder holder;

   AttributeCertificateHolder(ASN1Sequence var1) {
      this.holder = Holder.getInstance(var1);
   }

   public AttributeCertificateHolder(X500Name var1, BigInteger var2) {
      this.holder = new Holder(new IssuerSerial(this.generateGeneralNames(var1), new ASN1Integer(var2)));
   }

   public AttributeCertificateHolder(X509CertificateHolder var1) {
      this.holder = new Holder(new IssuerSerial(this.generateGeneralNames(var1.getIssuer()), new ASN1Integer(var1.getSerialNumber())));
   }

   public AttributeCertificateHolder(X500Name var1) {
      this.holder = new Holder(this.generateGeneralNames(var1));
   }

   public AttributeCertificateHolder(int var1, ASN1ObjectIdentifier var2, ASN1ObjectIdentifier var3, byte[] var4) {
      this.holder = new Holder(new ObjectDigestInfo(var1, var3, new AlgorithmIdentifier(var2), Arrays.clone(var4)));
   }

   public int getDigestedObjectType() {
      return this.holder.getObjectDigestInfo() != null ? this.holder.getObjectDigestInfo().getDigestedObjectType().intValueExact() : -1;
   }

   public AlgorithmIdentifier getDigestAlgorithm() {
      return this.holder.getObjectDigestInfo() != null ? this.holder.getObjectDigestInfo().getDigestAlgorithm() : null;
   }

   public byte[] getObjectDigest() {
      return this.holder.getObjectDigestInfo() != null ? this.holder.getObjectDigestInfo().getObjectDigest().getBytes() : null;
   }

   public ASN1ObjectIdentifier getOtherObjectTypeID() {
      if (this.holder.getObjectDigestInfo() != null) {
         new ASN1ObjectIdentifier(this.holder.getObjectDigestInfo().getOtherObjectTypeID().getId());
      }

      return null;
   }

   private GeneralNames generateGeneralNames(X500Name var1) {
      return new GeneralNames(new GeneralName(var1));
   }

   private boolean matchesDN(X500Name var1, GeneralNames var2) {
      GeneralName[] var3 = var2.getNames();

      for (int var4 = 0; var4 != var3.length; var4++) {
         GeneralName var5 = var3[var4];
         if (var5.getTagNo() == 4 && X500Name.getInstance(var5.getName()).equals(var1)) {
            return true;
         }
      }

      return false;
   }

   private X500Name[] getPrincipals(GeneralName[] var1) {
      ArrayList var2 = new ArrayList(var1.length);

      for (int var3 = 0; var3 != var1.length; var3++) {
         if (var1[var3].getTagNo() == 4) {
            var2.add(X500Name.getInstance(var1[var3].getName()));
         }
      }

      return var2.toArray(new X500Name[var2.size()]);
   }

   public X500Name[] getEntityNames() {
      return this.holder.getEntityName() != null ? this.getPrincipals(this.holder.getEntityName().getNames()) : null;
   }

   public X500Name[] getIssuer() {
      return this.holder.getBaseCertificateID() != null ? this.getPrincipals(this.holder.getBaseCertificateID().getIssuer().getNames()) : null;
   }

   public BigInteger getSerialNumber() {
      return this.holder.getBaseCertificateID() != null ? this.holder.getBaseCertificateID().getSerial().getValue() : null;
   }

   @Override
   public Object clone() {
      return new AttributeCertificateHolder((ASN1Sequence)this.holder.toASN1Primitive());
   }

   @Override
   public boolean match(Object var1) {
      if (!(var1 instanceof X509CertificateHolder)) {
         return false;
      } else {
         X509CertificateHolder var2 = (X509CertificateHolder)var1;
         if (this.holder.getBaseCertificateID() == null) {
            if (this.holder.getEntityName() != null && this.matchesDN(var2.getSubject(), this.holder.getEntityName())) {
               return true;
            } else {
               if (this.holder.getObjectDigestInfo() != null) {
                  try {
                     DigestCalculator var3 = digestCalculatorProvider.get(this.holder.getObjectDigestInfo().getDigestAlgorithm());
                     OutputStream var4 = var3.getOutputStream();
                     switch (this.getDigestedObjectType()) {
                        case 0:
                           var4.write(var2.getSubjectPublicKeyInfo().getEncoded());
                           break;
                        case 1:
                           var4.write(var2.getEncoded());
                     }

                     var4.close();
                     if (Arrays.areEqual(var3.getDigest(), this.getObjectDigest())) {
                        return true;
                     }
                  } catch (Exception var5) {
                     return false;
                  }
               }

               return false;
            }
         } else {
            return this.holder.getBaseCertificateID().getSerial().hasValue(var2.getSerialNumber())
               && this.matchesDN(var2.getIssuer(), this.holder.getBaseCertificateID().getIssuer());
         }
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof AttributeCertificateHolder)) {
         return false;
      } else {
         AttributeCertificateHolder var2 = (AttributeCertificateHolder)var1;
         return this.holder.equals(var2.holder);
      }
   }

   @Override
   public int hashCode() {
      return this.holder.hashCode();
   }

   public static void setDigestCalculatorProvider(DigestCalculatorProvider var0) {
      digestCalculatorProvider = var0;
   }
}
