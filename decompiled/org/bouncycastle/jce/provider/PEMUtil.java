package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.util.encoders.Base64;

public class PEMUtil {
   private final String _header1;
   private final String _header2;
   private final String _footer1;
   private final String _footer2;

   PEMUtil(String var1) {
      this._header1 = "-----BEGIN " + var1 + "-----";
      this._header2 = "-----BEGIN X509 " + var1 + "-----";
      this._footer1 = "-----END " + var1 + "-----";
      this._footer2 = "-----END X509 " + var1 + "-----";
   }

   private String readLine(InputStream var1) throws IOException {
      StringBuffer var3 = new StringBuffer();

      int var2;
      do {
         while ((var2 = var1.read()) != 13 && var2 != 10 && var2 >= 0) {
            if (var2 != 13) {
               var3.append((char)var2);
            }
         }
      } while (var2 >= 0 && var3.length() == 0);

      return var2 < 0 ? null : var3.toString();
   }

   ASN1Sequence readPEMObject(InputStream var1) throws IOException {
      StringBuffer var3 = new StringBuffer();

      String var2;
      while ((var2 = this.readLine(var1)) != null && !var2.startsWith(this._header1) && !var2.startsWith(this._header2)) {
      }

      while ((var2 = this.readLine(var1)) != null && !var2.startsWith(this._footer1) && !var2.startsWith(this._footer2)) {
         var3.append(var2);
      }

      if (var3.length() != 0) {
         ASN1Primitive var4 = new ASN1InputStream(Base64.decode(var3.toString())).readObject();
         if (!(var4 instanceof ASN1Sequence)) {
            throw new IOException("malformed PEM data encountered");
         } else {
            return (ASN1Sequence)var4;
         }
      } else {
         return null;
      }
   }
}
