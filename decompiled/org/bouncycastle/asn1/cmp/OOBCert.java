package org.bouncycastle.asn1.cmp;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.Certificate;

public class OOBCert extends CMPCertificate {
   public OOBCert(AttributeCertificate var1) {
      super(1, var1);
   }

   public OOBCert(int var1, ASN1Object var2) {
      super(var1, var2);
   }

   public OOBCert(Certificate var1) {
      super(var1);
   }

   public static OOBCert getInstance(ASN1TaggedObject var0, boolean var1) {
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

   public static OOBCert getInstance(Object var0) {
      if (var0 == null || var0 instanceof OOBCert) {
         return (OOBCert)var0;
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
               throw new IllegalArgumentException("Invalid encoding in OOBCert");
            }
         }

         if (var0 instanceof ASN1Sequence) {
            return new OOBCert(Certificate.getInstance(var0));
         } else if (var0 instanceof ASN1TaggedObject) {
            ASN1TaggedObject var1 = ASN1TaggedObject.getInstance(var0, 128);
            return new OOBCert(var1.getTagNo(), var1.getExplicitBaseObject());
         } else {
            throw new IllegalArgumentException("Invalid object: " + var0.getClass().getName());
         }
      }
   }
}
