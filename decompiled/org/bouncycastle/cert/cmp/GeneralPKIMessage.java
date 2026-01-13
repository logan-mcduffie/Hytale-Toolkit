package org.bouncycastle.cert.cmp;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.cert.CertIOException;

public class GeneralPKIMessage {
   private final PKIMessage pkiMessage;

   private static PKIMessage parseBytes(byte[] var0) throws IOException {
      try {
         return PKIMessage.getInstance(ASN1Primitive.fromByteArray(var0));
      } catch (ClassCastException var2) {
         throw new CertIOException("malformed data: " + var2.getMessage(), var2);
      } catch (IllegalArgumentException var3) {
         throw new CertIOException("malformed data: " + var3.getMessage(), var3);
      }
   }

   public GeneralPKIMessage(byte[] var1) throws IOException {
      this(parseBytes(var1));
   }

   public GeneralPKIMessage(PKIMessage var1) {
      this.pkiMessage = var1;
   }

   public PKIHeader getHeader() {
      return this.pkiMessage.getHeader();
   }

   public PKIBody getBody() {
      return this.pkiMessage.getBody();
   }

   public boolean hasProtection() {
      return this.pkiMessage.getProtection() != null;
   }

   public PKIMessage toASN1Structure() {
      return this.pkiMessage;
   }
}
