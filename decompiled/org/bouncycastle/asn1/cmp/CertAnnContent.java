package org.bouncycastle.asn1.cmp;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.Certificate;

public class CertAnnContent extends CMPCertificate {
   /** @deprecated */
   public CertAnnContent(AttributeCertificate var1) {
      super(var1);
   }

   public CertAnnContent(int var1, ASN1Object var2) {
      super(var1, var2);
   }

   public CertAnnContent(Certificate var1) {
      super(var1);
   }

   public static CertAnnContent getInstance(ASN1TaggedObject var0, boolean var1) {
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

   public static CertAnnContent getInstance(Object var0) {
      if (var0 == null || var0 instanceof CertAnnContent) {
         return (CertAnnContent)var0;
      } else if (var0 instanceof CMPCertificate) {
         try {
            return getInstance(((CMPCertificate)var0).getEncoded());
         } catch (IOException var2) {
            throw new IllegalArgumentException(var2.getMessage(), var2);
         }
      } else {
         if (var0 instanceof byte[]) {
            try {
               var0 = ASN1Primitive.fromByteArray((byte[])var0);
            } catch (IOException var3) {
               throw new IllegalArgumentException("Invalid encoding in CertAnnContent");
            }
         }

         if (var0 instanceof ASN1Sequence) {
            return new CertAnnContent(Certificate.getInstance(var0));
         } else if (var0 instanceof ASN1TaggedObject) {
            ASN1TaggedObject var1 = ASN1TaggedObject.getInstance(var0, 128);
            return new CertAnnContent(var1.getTagNo(), var1.getExplicitBaseObject());
         } else {
            throw new IllegalArgumentException("Invalid object: " + var0.getClass().getName());
         }
      }
   }
}
