package org.bouncycastle.asn1.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class SignerInfo extends ASN1Object {
   private ASN1Integer version;
   private IssuerAndSerialNumber issuerAndSerialNumber;
   private AlgorithmIdentifier digAlgorithm;
   private ASN1Set authenticatedAttributes;
   private AlgorithmIdentifier digEncryptionAlgorithm;
   private ASN1OctetString encryptedDigest;
   private ASN1Set unauthenticatedAttributes;

   public static SignerInfo getInstance(Object var0) {
      if (var0 instanceof SignerInfo) {
         return (SignerInfo)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new SignerInfo((ASN1Sequence)var0);
      } else {
         throw new IllegalArgumentException("unknown object in factory: " + var0.getClass().getName());
      }
   }

   public SignerInfo(
      ASN1Integer var1, IssuerAndSerialNumber var2, AlgorithmIdentifier var3, ASN1Set var4, AlgorithmIdentifier var5, ASN1OctetString var6, ASN1Set var7
   ) {
      this.version = var1;
      this.issuerAndSerialNumber = var2;
      this.digAlgorithm = var3;
      this.authenticatedAttributes = var4;
      this.digEncryptionAlgorithm = var5;
      this.encryptedDigest = var6;
      this.unauthenticatedAttributes = var7;
   }

   public SignerInfo(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.version = (ASN1Integer)var2.nextElement();
      this.issuerAndSerialNumber = IssuerAndSerialNumber.getInstance(var2.nextElement());
      this.digAlgorithm = AlgorithmIdentifier.getInstance(var2.nextElement());
      Object var3 = var2.nextElement();
      if (var3 instanceof ASN1TaggedObject) {
         this.authenticatedAttributes = ASN1Set.getInstance((ASN1TaggedObject)var3, false);
         this.digEncryptionAlgorithm = AlgorithmIdentifier.getInstance(var2.nextElement());
      } else {
         this.authenticatedAttributes = null;
         this.digEncryptionAlgorithm = AlgorithmIdentifier.getInstance(var3);
      }

      this.encryptedDigest = DEROctetString.getInstance(var2.nextElement());
      if (var2.hasMoreElements()) {
         this.unauthenticatedAttributes = ASN1Set.getInstance((ASN1TaggedObject)var2.nextElement(), false);
      } else {
         this.unauthenticatedAttributes = null;
      }
   }

   public ASN1Integer getVersion() {
      return this.version;
   }

   public IssuerAndSerialNumber getIssuerAndSerialNumber() {
      return this.issuerAndSerialNumber;
   }

   public ASN1Set getAuthenticatedAttributes() {
      return this.authenticatedAttributes;
   }

   public AlgorithmIdentifier getDigestAlgorithm() {
      return this.digAlgorithm;
   }

   public ASN1OctetString getEncryptedDigest() {
      return this.encryptedDigest;
   }

   public AlgorithmIdentifier getDigestEncryptionAlgorithm() {
      return this.digEncryptionAlgorithm;
   }

   public ASN1Set getUnauthenticatedAttributes() {
      return this.unauthenticatedAttributes;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(7);
      var1.add(this.version);
      var1.add(this.issuerAndSerialNumber);
      var1.add(this.digAlgorithm);
      if (this.authenticatedAttributes != null) {
         var1.add(new DERTaggedObject(false, 0, this.authenticatedAttributes));
      }

      var1.add(this.digEncryptionAlgorithm);
      var1.add(this.encryptedDigest);
      if (this.unauthenticatedAttributes != null) {
         var1.add(new DERTaggedObject(false, 1, this.unauthenticatedAttributes));
      }

      return new DERSequence(var1);
   }
}
