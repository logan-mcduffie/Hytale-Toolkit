package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class PKCS7TypedStream extends CMSTypedStream {
   private final ASN1Encodable content;

   public PKCS7TypedStream(ASN1ObjectIdentifier var1, ASN1Encodable var2) throws IOException {
      super(var1);
      this.content = var2;
   }

   public ASN1Encodable getContent() {
      return this.content;
   }

   @Override
   public InputStream getContentStream() {
      try {
         return this.getContentStream(this.content);
      } catch (IOException var2) {
         throw new CMSRuntimeException("unable to convert content to stream: " + var2.getMessage(), var2);
      }
   }

   @Override
   public void drain() throws IOException {
      this.content.toASN1Primitive();
   }

   private InputStream getContentStream(ASN1Encodable var1) throws IOException {
      byte[] var2 = var1.toASN1Primitive().getEncoded("DER");
      int var3 = 0;
      if ((var2[var3++] & 31) == 31) {
         while ((var2[var3++] & 128) != 0) {
         }
      }

      byte var4 = var2[var3++];
      if ((var4 & 128) != 0) {
         var3 += var4 & 127;
      }

      return new ByteArrayInputStream(var2, var3, var2.length - var3);
   }
}
