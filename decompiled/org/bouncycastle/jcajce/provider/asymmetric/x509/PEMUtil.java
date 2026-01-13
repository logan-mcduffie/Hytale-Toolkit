package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.util.encoders.Base64;

class PEMUtil {
   private final PEMUtil.Boundaries[] _supportedBoundaries;

   PEMUtil(String var1) {
      this._supportedBoundaries = new PEMUtil.Boundaries[]{
         new PEMUtil.Boundaries(var1), new PEMUtil.Boundaries("X509 " + var1), new PEMUtil.Boundaries("PKCS7")
      };
   }

   private String readLine(InputStream var1) throws IOException {
      StringBuffer var3 = new StringBuffer();

      int var2;
      do {
         while ((var2 = var1.read()) != 13 && var2 != 10 && var2 >= 0) {
            var3.append((char)var2);
         }
      } while (var2 >= 0 && var3.length() == 0);

      if (var2 < 0) {
         return var3.length() == 0 ? null : var3.toString();
      } else {
         if (var2 == 13) {
            var1.mark(1);
            if ((var2 = var1.read()) == 10) {
               var1.mark(1);
            }

            if (var2 > 0) {
               var1.reset();
            }
         }

         return var3.toString();
      }
   }

   private PEMUtil.Boundaries getBoundaries(String var1) {
      for (int var2 = 0; var2 != this._supportedBoundaries.length; var2++) {
         PEMUtil.Boundaries var3 = this._supportedBoundaries[var2];
         if (var3.isTheExpectedHeader(var1) || var3.isTheExpectedFooter(var1)) {
            return var3;
         }
      }

      return null;
   }

   ASN1Sequence readPEMObject(InputStream var1, boolean var2) throws IOException {
      StringBuffer var4 = new StringBuffer();
      PEMUtil.Boundaries var5 = null;

      String var3;
      while (var5 == null && (var3 = this.readLine(var1)) != null) {
         var5 = this.getBoundaries(var3);
         if (var5 != null && !var5.isTheExpectedHeader(var3)) {
            throw new IOException("malformed PEM data: found footer where header was expected");
         }
      }

      if (var5 == null) {
         if (!var2) {
            return null;
         } else {
            throw new IOException("malformed PEM data: no header found");
         }
      } else {
         PEMUtil.Boundaries var6 = null;

         while (var6 == null && (var3 = this.readLine(var1)) != null) {
            var6 = this.getBoundaries(var3);
            if (var6 != null) {
               if (!var5.isTheExpectedFooter(var3)) {
                  throw new IOException("malformed PEM data: header/footer mismatch");
               }
            } else {
               var4.append(var3);
            }
         }

         if (var6 == null) {
            throw new IOException("malformed PEM data: no footer found");
         } else if (var4.length() != 0) {
            try {
               return ASN1Sequence.getInstance(Base64.decode(var4.toString()));
            } catch (Exception var8) {
               throw new IOException("malformed PEM data encountered");
            }
         } else {
            return null;
         }
      }
   }

   private static class Boundaries {
      private final String _header;
      private final String _footer;

      private Boundaries(String var1) {
         this._header = "-----BEGIN " + var1 + "-----";
         this._footer = "-----END " + var1 + "-----";
      }

      public boolean isTheExpectedHeader(String var1) {
         return var1.startsWith(this._header);
      }

      public boolean isTheExpectedFooter(String var1) {
         return var1.startsWith(this._footer);
      }
   }
}
