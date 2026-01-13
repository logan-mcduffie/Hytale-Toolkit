package org.bouncycastle.asn1.eac;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class CVCertificateRequest extends ASN1Object {
   private final ASN1TaggedObject original;
   private CertificateBody certificateBody;
   private byte[] innerSignature = null;
   private byte[] outerSignature = null;
   private static final int bodyValid = 1;
   private static final int signValid = 2;

   private CVCertificateRequest(ASN1TaggedObject var1) throws IOException {
      this.original = var1;
      if (var1.hasTag(64, 7)) {
         ASN1Sequence var2 = ASN1Sequence.getInstance(var1.getBaseUniversal(false, 16));
         this.initCertBody(ASN1TaggedObject.getInstance(var2.getObjectAt(0), 64));
         this.outerSignature = ASN1OctetString.getInstance(ASN1TaggedObject.getInstance(var2.getObjectAt(var2.size() - 1)).getBaseUniversal(false, 4))
            .getOctets();
      } else {
         this.initCertBody(var1);
      }
   }

   private void initCertBody(ASN1TaggedObject var1) throws IOException {
      if (var1.hasTag(64, 33)) {
         byte var2 = 0;
         ASN1Sequence var3 = ASN1Sequence.getInstance(var1.getBaseUniversal(false, 16));
         Enumeration var4 = var3.getObjects();

         while (var4.hasMoreElements()) {
            ASN1TaggedObject var5 = ASN1TaggedObject.getInstance(var4.nextElement(), 64);
            switch (var5.getTagNo()) {
               case 55:
                  this.innerSignature = ASN1OctetString.getInstance(var5.getBaseUniversal(false, 4)).getOctets();
                  var2 |= 2;
                  break;
               case 78:
                  this.certificateBody = CertificateBody.getInstance(var5);
                  var2 |= 1;
                  break;
               default:
                  throw new IOException("Invalid tag, not an CV Certificate Request element:" + var5.getTagNo());
            }
         }

         if ((var2 & 3) == 0) {
            throw new IOException("Invalid CARDHOLDER_CERTIFICATE in request:" + var1.getTagNo());
         }
      } else {
         throw new IOException("not a CARDHOLDER_CERTIFICATE in request:" + var1.getTagNo());
      }
   }

   public static CVCertificateRequest getInstance(Object var0) {
      if (var0 instanceof CVCertificateRequest) {
         return (CVCertificateRequest)var0;
      } else if (var0 != null) {
         try {
            return new CVCertificateRequest(ASN1TaggedObject.getInstance(var0, 64));
         } catch (IOException var2) {
            throw new ASN1ParsingException("unable to parse data: " + var2.getMessage(), var2);
         }
      } else {
         return null;
      }
   }

   public CertificateBody getCertificateBody() {
      return this.certificateBody;
   }

   public PublicKeyDataObject getPublicKey() {
      return this.certificateBody.getPublicKey();
   }

   public byte[] getInnerSignature() {
      return Arrays.clone(this.innerSignature);
   }

   public byte[] getOuterSignature() {
      return Arrays.clone(this.outerSignature);
   }

   public boolean hasOuterSignature() {
      return this.outerSignature != null;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      if (this.original != null) {
         return this.original;
      } else {
         DERSequence var1 = new DERSequence(this.certificateBody, EACTagged.create(55, this.innerSignature));
         return EACTagged.create(33, var1);
      }
   }
}
