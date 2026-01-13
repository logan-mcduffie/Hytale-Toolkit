package org.bouncycastle.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.Principal;
import java.security.cert.CertSelector;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.Holder;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.asn1.x509.ObjectDigestInfo;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

/** @deprecated */
public class AttributeCertificateHolder implements CertSelector, Selector {
   final Holder holder;

   AttributeCertificateHolder(ASN1Sequence var1) {
      this.holder = Holder.getInstance(var1);
   }

   public AttributeCertificateHolder(X509Principal var1, BigInteger var2) {
      this.holder = new Holder(new IssuerSerial(GeneralNames.getInstance(new DERSequence(new GeneralName(var1))), new ASN1Integer(var2)));
   }

   public AttributeCertificateHolder(X500Principal var1, BigInteger var2) {
      this(X509Util.convertPrincipal(var1), var2);
   }

   public AttributeCertificateHolder(X509Certificate var1) throws CertificateParsingException {
      X509Principal var2;
      try {
         var2 = PrincipalUtil.getIssuerX509Principal(var1);
      } catch (Exception var4) {
         throw new CertificateParsingException(var4.getMessage());
      }

      this.holder = new Holder(new IssuerSerial(this.generateGeneralNames(var2), new ASN1Integer(var1.getSerialNumber())));
   }

   public AttributeCertificateHolder(X509Principal var1) {
      this.holder = new Holder(this.generateGeneralNames(var1));
   }

   public AttributeCertificateHolder(X500Principal var1) {
      this(X509Util.convertPrincipal(var1));
   }

   public AttributeCertificateHolder(int var1, String var2, String var3, byte[] var4) {
      this.holder = new Holder(
         new ObjectDigestInfo(var1, new ASN1ObjectIdentifier(var3), new AlgorithmIdentifier(new ASN1ObjectIdentifier(var2)), Arrays.clone(var4))
      );
   }

   public int getDigestedObjectType() {
      return this.holder.getObjectDigestInfo() != null ? this.holder.getObjectDigestInfo().getDigestedObjectType().intValueExact() : -1;
   }

   public String getDigestAlgorithm() {
      return this.holder.getObjectDigestInfo() != null ? this.holder.getObjectDigestInfo().getDigestAlgorithm().getAlgorithm().getId() : null;
   }

   public byte[] getObjectDigest() {
      return this.holder.getObjectDigestInfo() != null ? this.holder.getObjectDigestInfo().getObjectDigest().getBytes() : null;
   }

   public String getOtherObjectTypeID() {
      if (this.holder.getObjectDigestInfo() != null) {
         this.holder.getObjectDigestInfo().getOtherObjectTypeID().getId();
      }

      return null;
   }

   private GeneralNames generateGeneralNames(X509Principal var1) {
      return GeneralNames.getInstance(new DERSequence(new GeneralName(var1)));
   }

   private boolean matchesDN(X509Principal var1, GeneralNames var2) {
      GeneralName[] var3 = var2.getNames();

      for (int var4 = 0; var4 != var3.length; var4++) {
         GeneralName var5 = var3[var4];
         if (var5.getTagNo() == 4) {
            try {
               if (new X509Principal(var5.getName().toASN1Primitive().getEncoded()).equals(var1)) {
                  return true;
               }
            } catch (IOException var7) {
            }
         }
      }

      return false;
   }

   private Object[] getNames(GeneralName[] var1) {
      ArrayList var2 = new ArrayList(var1.length);

      for (int var3 = 0; var3 != var1.length; var3++) {
         if (var1[var3].getTagNo() == 4) {
            try {
               var2.add(new X500Principal(var1[var3].getName().toASN1Primitive().getEncoded()));
            } catch (IOException var5) {
               throw new RuntimeException("badly formed Name object");
            }
         }
      }

      return var2.toArray(new Object[var2.size()]);
   }

   private Principal[] getPrincipals(GeneralNames var1) {
      Object[] var2 = this.getNames(var1.getNames());
      ArrayList var3 = new ArrayList();

      for (int var4 = 0; var4 != var2.length; var4++) {
         if (var2[var4] instanceof Principal) {
            var3.add(var2[var4]);
         }
      }

      return var3.toArray(new Principal[var3.size()]);
   }

   public Principal[] getEntityNames() {
      return this.holder.getEntityName() != null ? this.getPrincipals(this.holder.getEntityName()) : null;
   }

   public Principal[] getIssuer() {
      return this.holder.getBaseCertificateID() != null ? this.getPrincipals(this.holder.getBaseCertificateID().getIssuer()) : null;
   }

   public BigInteger getSerialNumber() {
      return this.holder.getBaseCertificateID() != null ? this.holder.getBaseCertificateID().getSerial().getValue() : null;
   }

   @Override
   public Object clone() {
      return new AttributeCertificateHolder((ASN1Sequence)this.holder.toASN1Primitive());
   }

   @Override
   public boolean match(Certificate var1) {
      if (!(var1 instanceof X509Certificate)) {
         return false;
      } else {
         X509Certificate var2 = (X509Certificate)var1;

         try {
            if (this.holder.getBaseCertificateID() == null) {
               if (this.holder.getEntityName() != null && this.matchesDN(PrincipalUtil.getSubjectX509Principal(var2), this.holder.getEntityName())) {
                  return true;
               } else {
                  if (this.holder.getObjectDigestInfo() != null) {
                     MessageDigest var3 = null;

                     try {
                        var3 = MessageDigest.getInstance(this.getDigestAlgorithm(), "BC");
                     } catch (Exception var5) {
                        return false;
                     }

                     switch (this.getDigestedObjectType()) {
                        case 0:
                           var3.update(var1.getPublicKey().getEncoded());
                           break;
                        case 1:
                           var3.update(var1.getEncoded());
                     }

                     if (!Arrays.areEqual(var3.digest(), this.getObjectDigest())) {
                        return false;
                     }
                  }

                  return false;
               }
            } else {
               return this.holder.getBaseCertificateID().getSerial().hasValue(var2.getSerialNumber())
                  && this.matchesDN(PrincipalUtil.getIssuerX509Principal(var2), this.holder.getBaseCertificateID().getIssuer());
            }
         } catch (CertificateEncodingException var6) {
            return false;
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

   @Override
   public boolean match(Object var1) {
      return !(var1 instanceof X509Certificate) ? false : this.match((Certificate)var1);
   }
}
