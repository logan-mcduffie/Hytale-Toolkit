package org.bouncycastle.eac;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.eac.CVCertificate;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import org.bouncycastle.eac.operator.EACSignatureVerifier;

public class EACCertificateHolder {
   private CVCertificate cvCertificate;

   private static CVCertificate parseBytes(byte[] var0) throws IOException {
      try {
         return CVCertificate.getInstance(var0);
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

   public EACCertificateHolder(byte[] var1) throws IOException {
      this(parseBytes(var1));
   }

   public EACCertificateHolder(CVCertificate var1) {
      this.cvCertificate = var1;
   }

   public CVCertificate toASN1Structure() {
      return this.cvCertificate;
   }

   public PublicKeyDataObject getPublicKeyDataObject() {
      return this.cvCertificate.getBody().getPublicKey();
   }

   public boolean isSignatureValid(EACSignatureVerifier var1) throws EACException {
      try {
         OutputStream var2 = var1.getOutputStream();
         var2.write(this.cvCertificate.getBody().getEncoded("DER"));
         var2.close();
         return var1.verify(this.cvCertificate.getSignature());
      } catch (Exception var3) {
         throw new EACException("unable to process signature: " + var3.getMessage(), var3);
      }
   }
}
