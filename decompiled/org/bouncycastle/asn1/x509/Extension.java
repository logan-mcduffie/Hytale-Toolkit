package org.bouncycastle.asn1.x509;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class Extension extends ASN1Object {
   public static final ASN1ObjectIdentifier subjectDirectoryAttributes = new ASN1ObjectIdentifier("2.5.29.9").intern();
   public static final ASN1ObjectIdentifier subjectKeyIdentifier = new ASN1ObjectIdentifier("2.5.29.14").intern();
   public static final ASN1ObjectIdentifier keyUsage = new ASN1ObjectIdentifier("2.5.29.15").intern();
   public static final ASN1ObjectIdentifier privateKeyUsagePeriod = new ASN1ObjectIdentifier("2.5.29.16").intern();
   public static final ASN1ObjectIdentifier subjectAlternativeName = new ASN1ObjectIdentifier("2.5.29.17").intern();
   public static final ASN1ObjectIdentifier issuerAlternativeName = new ASN1ObjectIdentifier("2.5.29.18").intern();
   public static final ASN1ObjectIdentifier basicConstraints = new ASN1ObjectIdentifier("2.5.29.19").intern();
   public static final ASN1ObjectIdentifier cRLNumber = new ASN1ObjectIdentifier("2.5.29.20").intern();
   public static final ASN1ObjectIdentifier reasonCode = new ASN1ObjectIdentifier("2.5.29.21").intern();
   public static final ASN1ObjectIdentifier instructionCode = new ASN1ObjectIdentifier("2.5.29.23").intern();
   public static final ASN1ObjectIdentifier invalidityDate = new ASN1ObjectIdentifier("2.5.29.24").intern();
   public static final ASN1ObjectIdentifier deltaCRLIndicator = new ASN1ObjectIdentifier("2.5.29.27").intern();
   public static final ASN1ObjectIdentifier issuingDistributionPoint = new ASN1ObjectIdentifier("2.5.29.28").intern();
   public static final ASN1ObjectIdentifier certificateIssuer = new ASN1ObjectIdentifier("2.5.29.29").intern();
   public static final ASN1ObjectIdentifier nameConstraints = new ASN1ObjectIdentifier("2.5.29.30").intern();
   public static final ASN1ObjectIdentifier cRLDistributionPoints = new ASN1ObjectIdentifier("2.5.29.31").intern();
   public static final ASN1ObjectIdentifier certificatePolicies = new ASN1ObjectIdentifier("2.5.29.32").intern();
   public static final ASN1ObjectIdentifier policyMappings = new ASN1ObjectIdentifier("2.5.29.33").intern();
   public static final ASN1ObjectIdentifier authorityKeyIdentifier = new ASN1ObjectIdentifier("2.5.29.35").intern();
   public static final ASN1ObjectIdentifier policyConstraints = new ASN1ObjectIdentifier("2.5.29.36").intern();
   public static final ASN1ObjectIdentifier extendedKeyUsage = new ASN1ObjectIdentifier("2.5.29.37").intern();
   public static final ASN1ObjectIdentifier freshestCRL = new ASN1ObjectIdentifier("2.5.29.46").intern();
   public static final ASN1ObjectIdentifier inhibitAnyPolicy = new ASN1ObjectIdentifier("2.5.29.54").intern();
   public static final ASN1ObjectIdentifier authorityInfoAccess = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.1").intern();
   public static final ASN1ObjectIdentifier subjectInfoAccess = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.11").intern();
   public static final ASN1ObjectIdentifier logoType = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.12").intern();
   public static final ASN1ObjectIdentifier biometricInfo = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.2").intern();
   public static final ASN1ObjectIdentifier qCStatements = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.3").intern();
   public static final ASN1ObjectIdentifier auditIdentity = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.4").intern();
   public static final ASN1ObjectIdentifier noRevAvail = new ASN1ObjectIdentifier("2.5.29.56").intern();
   public static final ASN1ObjectIdentifier targetInformation = new ASN1ObjectIdentifier("2.5.29.55").intern();
   public static final ASN1ObjectIdentifier expiredCertsOnCRL = new ASN1ObjectIdentifier("2.5.29.60").intern();
   public static final ASN1ObjectIdentifier subjectAltPublicKeyInfo = new ASN1ObjectIdentifier("2.5.29.72").intern();
   public static final ASN1ObjectIdentifier altSignatureAlgorithm = new ASN1ObjectIdentifier("2.5.29.73").intern();
   public static final ASN1ObjectIdentifier altSignatureValue = new ASN1ObjectIdentifier("2.5.29.74").intern();
   public static final ASN1ObjectIdentifier deltaCertificateDescriptor = new ASN1ObjectIdentifier("2.16.840.1.114027.80.6.1");
   private ASN1ObjectIdentifier extnId;
   private boolean critical;
   private ASN1OctetString value;

   public Extension(ASN1ObjectIdentifier var1, ASN1Boolean var2, ASN1OctetString var3) {
      this(var1, var2.isTrue(), var3);
   }

   public Extension(ASN1ObjectIdentifier var1, boolean var2, byte[] var3) {
      this(var1, var2, new DEROctetString(Arrays.clone(var3)));
   }

   public Extension(ASN1ObjectIdentifier var1, boolean var2, ASN1OctetString var3) {
      this.extnId = var1;
      this.critical = var2;
      this.value = var3;
   }

   public static Extension create(ASN1ObjectIdentifier var0, boolean var1, ASN1Encodable var2) throws IOException {
      return new Extension(var0, var1, new DEROctetString(var2.toASN1Primitive().getEncoded()));
   }

   private Extension(ASN1Sequence var1) {
      if (var1.size() == 2) {
         this.extnId = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
         this.critical = false;
         this.value = ASN1OctetString.getInstance(var1.getObjectAt(1));
      } else {
         if (var1.size() != 3) {
            throw new IllegalArgumentException("Bad sequence size: " + var1.size());
         }

         this.extnId = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
         this.critical = ASN1Boolean.getInstance(var1.getObjectAt(1)).isTrue();
         this.value = ASN1OctetString.getInstance(var1.getObjectAt(2));
      }
   }

   public static Extension getInstance(Object var0) {
      if (var0 instanceof Extension) {
         return (Extension)var0;
      } else {
         return var0 != null ? new Extension(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1ObjectIdentifier getExtnId() {
      return this.extnId;
   }

   public boolean isCritical() {
      return this.critical;
   }

   public ASN1OctetString getExtnValue() {
      return this.value;
   }

   public ASN1Encodable getParsedValue() {
      return convertValueToObject(this);
   }

   @Override
   public int hashCode() {
      return this.isCritical() ? this.getExtnValue().hashCode() ^ this.getExtnId().hashCode() : ~(this.getExtnValue().hashCode() ^ this.getExtnId().hashCode());
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof Extension)) {
         return false;
      } else {
         Extension var2 = (Extension)var1;
         return var2.getExtnId().equals(this.getExtnId()) && var2.getExtnValue().equals(this.getExtnValue()) && var2.isCritical() == this.isCritical();
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.extnId);
      if (this.critical) {
         var1.add(ASN1Boolean.getInstance(true));
      }

      var1.add(this.value);
      return new DERSequence(var1);
   }

   private static ASN1Primitive convertValueToObject(Extension var0) throws IllegalArgumentException {
      try {
         return ASN1Primitive.fromByteArray(var0.getExtnValue().getOctets());
      } catch (IOException var2) {
         throw new IllegalArgumentException("can't convert extension: " + var2);
      }
   }
}
