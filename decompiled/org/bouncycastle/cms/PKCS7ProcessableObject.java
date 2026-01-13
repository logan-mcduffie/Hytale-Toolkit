package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;

public class PKCS7ProcessableObject implements CMSTypedData {
   private final ASN1ObjectIdentifier type;
   private final ASN1Encodable structure;

   public PKCS7ProcessableObject(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.type = var1;
      this.structure = var2;
   }

   @Override
   public ASN1ObjectIdentifier getContentType() {
      return this.type;
   }

   @Override
   public void write(OutputStream var1) throws IOException, CMSException {
      if (this.structure instanceof ASN1Sequence) {
         for (ASN1Encodable var4 : ASN1Sequence.getInstance(this.structure)) {
            var1.write(var4.toASN1Primitive().getEncoded("DER"));
         }
      } else {
         byte[] var5 = this.structure.toASN1Primitive().getEncoded("DER");
         int var6 = 1;

         while ((var5[var6] & 255) > 127) {
            var6++;
         }

         var1.write(var5, ++var6, var5.length - var6);
      }
   }

   @Override
   public Object getContent() {
      return this.structure;
   }
}
