package org.bouncycastle.asn1.isismtt.ocsp;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.util.Arrays;

public class RequestedCertificate extends ASN1Object implements ASN1Choice {
   public static final int certificate = -1;
   public static final int publicKeyCertificate = 0;
   public static final int attributeCertificate = 1;
   private Certificate cert;
   private byte[] publicKeyCert;
   private byte[] attributeCert;

   public static RequestedCertificate getInstance(Object var0) {
      if (var0 == null || var0 instanceof RequestedCertificate) {
         return (RequestedCertificate)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new RequestedCertificate(Certificate.getInstance(var0));
      } else if (var0 instanceof ASN1TaggedObject) {
         return new RequestedCertificate((ASN1TaggedObject)var0);
      } else {
         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      }
   }

   public static RequestedCertificate getInstance(ASN1TaggedObject var0, boolean var1) {
      if (!var1) {
         throw new IllegalArgumentException("choice item must be explicitly tagged");
      } else {
         return getInstance(var0.getExplicitBaseObject());
      }
   }

   private RequestedCertificate(ASN1TaggedObject var1) {
      if (var1.getTagNo() == 0) {
         this.publicKeyCert = ASN1OctetString.getInstance(var1, true).getOctets();
      } else {
         if (var1.getTagNo() != 1) {
            throw new IllegalArgumentException("unknown tag number: " + var1.getTagNo());
         }

         this.attributeCert = ASN1OctetString.getInstance(var1, true).getOctets();
      }
   }

   public RequestedCertificate(Certificate var1) {
      this.cert = var1;
   }

   public RequestedCertificate(int var1, byte[] var2) {
      this(new DERTaggedObject(var1, new DEROctetString(var2)));
   }

   public int getType() {
      if (this.cert != null) {
         return -1;
      } else {
         return this.publicKeyCert != null ? 0 : 1;
      }
   }

   public byte[] getCertificateBytes() {
      if (this.cert != null) {
         try {
            return this.cert.getEncoded();
         } catch (IOException var2) {
            throw new IllegalStateException("can't decode certificate: " + var2);
         }
      } else {
         return this.publicKeyCert != null ? Arrays.clone(this.publicKeyCert) : Arrays.clone(this.attributeCert);
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      if (this.publicKeyCert != null) {
         return new DERTaggedObject(0, new DEROctetString(this.publicKeyCert));
      } else {
         return (ASN1Primitive)(this.attributeCert != null ? new DERTaggedObject(1, new DEROctetString(this.attributeCert)) : this.cert.toASN1Primitive());
      }
   }
}
