package org.bouncycastle.cmc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Store;

public class SimplePKIResponse implements Encodable {
   private final CMSSignedData certificateResponse;

   private static ContentInfo parseBytes(byte[] var0) throws CMCException {
      try {
         return ContentInfo.getInstance(ASN1Primitive.fromByteArray(var0));
      } catch (Exception var2) {
         throw new CMCException("malformed data: " + var2.getMessage(), var2);
      }
   }

   public SimplePKIResponse(byte[] var1) throws CMCException {
      this(parseBytes(var1));
   }

   public SimplePKIResponse(ContentInfo var1) throws CMCException {
      try {
         this.certificateResponse = new CMSSignedData(var1);
      } catch (CMSException var3) {
         throw new CMCException("malformed response: " + var3.getMessage(), var3);
      }

      if (this.certificateResponse.getSignerInfos().size() != 0) {
         throw new CMCException("malformed response: SignerInfo structures found");
      } else if (this.certificateResponse.getSignedContent() != null) {
         throw new CMCException("malformed response: Signed Content found");
      }
   }

   public Store<X509CertificateHolder> getCertificates() {
      return this.certificateResponse.getCertificates();
   }

   public Store<X509CRLHolder> getCRLs() {
      return this.certificateResponse.getCRLs();
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return this.certificateResponse.getEncoded();
   }
}
