package org.bouncycastle.asn1.cmp;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.Certificate;

public class CMPCertificate extends ASN1Object implements ASN1Choice {
   private Certificate x509v3PKCert;
   private int otherTagValue;
   private ASN1Object otherCert;

   /** @deprecated */
   public CMPCertificate(AttributeCertificate var1) {
      this(1, var1);
   }

   public CMPCertificate(int var1, ASN1Object var2) {
      this.otherTagValue = var1;
      this.otherCert = var2;
   }

   public CMPCertificate(Certificate var1) {
      if (var1.getVersionNumber() != 3) {
         throw new IllegalArgumentException("only version 3 certificates allowed");
      } else {
         this.x509v3PKCert = var1;
      }
   }

   public static CMPCertificate getInstance(ASN1TaggedObject var0, boolean var1) {
      if (var0 != null) {
         if (var1) {
            return getInstance(var0.getExplicitBaseObject());
         } else {
            throw new IllegalArgumentException("tag must be explicit");
         }
      } else {
         return null;
      }
   }

   public static CMPCertificate getInstance(Object var0) {
      if (var0 == null || var0 instanceof CMPCertificate) {
         return (CMPCertificate)var0;
      } else {
         if (var0 instanceof byte[]) {
            try {
               var0 = ASN1Primitive.fromByteArray((byte[])var0);
            } catch (IOException var2) {
               throw new IllegalArgumentException("Invalid encoding in CMPCertificate");
            }
         }

         if (var0 instanceof ASN1Sequence) {
            return new CMPCertificate(Certificate.getInstance(var0));
         } else if (var0 instanceof ASN1TaggedObject) {
            ASN1TaggedObject var1 = ASN1TaggedObject.getInstance(var0, 128);
            return new CMPCertificate(var1.getTagNo(), var1.getBaseObject());
         } else {
            throw new IllegalArgumentException("Invalid object: " + var0.getClass().getName());
         }
      }
   }

   public boolean isX509v3PKCert() {
      return this.x509v3PKCert != null;
   }

   public Certificate getX509v3PKCert() {
      return this.x509v3PKCert;
   }

   /** @deprecated */
   public AttributeCertificate getX509v2AttrCert() {
      return AttributeCertificate.getInstance(this.otherCert);
   }

   public int getOtherCertTag() {
      return this.otherTagValue;
   }

   public ASN1Object getOtherCert() {
      return this.otherCert;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return (ASN1Primitive)(this.otherCert != null ? new DERTaggedObject(true, this.otherTagValue, this.otherCert) : this.x509v3PKCert.toASN1Primitive());
   }
}
