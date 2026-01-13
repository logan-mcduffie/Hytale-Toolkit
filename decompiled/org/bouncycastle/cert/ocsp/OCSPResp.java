package org.bouncycastle.cert.ocsp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Exception;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.ocsp.ResponseBytes;
import org.bouncycastle.cert.CertIOException;

public class OCSPResp {
   public static final int SUCCESSFUL = 0;
   public static final int MALFORMED_REQUEST = 1;
   public static final int INTERNAL_ERROR = 2;
   public static final int TRY_LATER = 3;
   public static final int SIG_REQUIRED = 5;
   public static final int UNAUTHORIZED = 6;
   private OCSPResponse resp;

   public OCSPResp(OCSPResponse var1) {
      this.resp = var1;
   }

   public OCSPResp(byte[] var1) throws IOException {
      this(new ByteArrayInputStream(var1));
   }

   public OCSPResp(InputStream var1) throws IOException {
      this(new ASN1InputStream(var1));
   }

   private OCSPResp(ASN1InputStream var1) throws IOException {
      try {
         this.resp = OCSPResponse.getInstance(var1.readObject());
      } catch (IllegalArgumentException var3) {
         throw new CertIOException("malformed response: " + var3.getMessage(), var3);
      } catch (ClassCastException var4) {
         throw new CertIOException("malformed response: " + var4.getMessage(), var4);
      } catch (ASN1Exception var5) {
         throw new CertIOException("malformed response: " + var5.getMessage(), var5);
      }

      if (this.resp == null) {
         throw new CertIOException("malformed response: no response data found");
      }
   }

   public int getStatus() {
      return this.resp.getResponseStatus().getIntValue();
   }

   public Object getResponseObject() throws OCSPException {
      ResponseBytes var1 = this.resp.getResponseBytes();
      if (var1 == null) {
         return null;
      } else if (var1.getResponseType().equals(OCSPObjectIdentifiers.id_pkix_ocsp_basic)) {
         try {
            ASN1Primitive var2 = ASN1Primitive.fromByteArray(var1.getResponse().getOctets());
            return new BasicOCSPResp(BasicOCSPResponse.getInstance(var2));
         } catch (Exception var3) {
            throw new OCSPException("problem decoding object: " + var3, var3);
         }
      } else {
         return var1.getResponse();
      }
   }

   public byte[] getEncoded() throws IOException {
      return this.resp.getEncoded();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof OCSPResp)) {
         return false;
      } else {
         OCSPResp var2 = (OCSPResp)var1;
         return this.resp.equals(var2.resp);
      }
   }

   @Override
   public int hashCode() {
      return this.resp.hashCode();
   }

   public OCSPResponse toASN1Structure() {
      return this.resp;
   }
}
