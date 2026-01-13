package org.bouncycastle.eac;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.eac.CVCertificateRequest;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import org.bouncycastle.eac.operator.EACSignatureVerifier;

public class EACCertificateRequestHolder {
   private CVCertificateRequest request;

   private static CVCertificateRequest parseBytes(byte[] var0) throws IOException {
      try {
         return CVCertificateRequest.getInstance(var0);
      } catch (ClassCastException var2) {
         throw new EACIOException("malformed data: " + var2.getMessage(), var2);
      } catch (IllegalArgumentException var3) {
         throw new EACIOException("malformed data: " + var3.getMessage(), var3);
      } catch (ASN1ParsingException var4) {
         if (var4.getCause() instanceof IOException) {
            throw (IOException)var4.getCause();
         } else {
            throw new EACIOException("malformed data: " + var4.getMessage(), var4);
         }
      }
   }

   public EACCertificateRequestHolder(byte[] var1) throws IOException {
      this(parseBytes(var1));
   }

   public EACCertificateRequestHolder(CVCertificateRequest var1) {
      this.request = var1;
   }

   public CVCertificateRequest toASN1Structure() {
      return this.request;
   }

   public PublicKeyDataObject getPublicKeyDataObject() {
      return this.request.getPublicKey();
   }

   public boolean isInnerSignatureValid(EACSignatureVerifier var1) throws EACException {
      try {
         OutputStream var2 = var1.getOutputStream();
         var2.write(this.request.getCertificateBody().getEncoded("DER"));
         var2.close();
         return var1.verify(this.request.getInnerSignature());
      } catch (Exception var3) {
         throw new EACException("unable to process signature: " + var3.getMessage(), var3);
      }
   }
}
