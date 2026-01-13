package org.bouncycastle.jcajce.provider.asymmetric.dstu;

import java.io.IOException;
import java.security.SignatureException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;

public class SignatureSpiLe extends SignatureSpi {
   void reverseBytes(byte[] var1) {
      for (int var3 = 0; var3 < var1.length / 2; var3++) {
         byte var2 = var1[var3];
         var1[var3] = var1[var1.length - 1 - var3];
         var1[var1.length - 1 - var3] = var2;
      }
   }

   @Override
   protected byte[] engineSign() throws SignatureException {
      byte[] var1 = ASN1OctetString.getInstance(super.engineSign()).getOctets();
      this.reverseBytes(var1);

      try {
         return new DEROctetString(var1).getEncoded();
      } catch (Exception var3) {
         throw new SignatureException(var3.toString());
      }
   }

   @Override
   protected boolean engineVerify(byte[] var1) throws SignatureException {
      Object var2 = null;

      try {
         var2 = ((ASN1OctetString)ASN1OctetString.fromByteArray(var1)).getOctets();
      } catch (IOException var6) {
         throw new SignatureException("error decoding signature bytes.");
      }

      this.reverseBytes((byte[])var2);

      try {
         return super.engineVerify(new DEROctetString((byte[])var2).getEncoded());
      } catch (SignatureException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new SignatureException(var5.toString());
      }
   }
}
