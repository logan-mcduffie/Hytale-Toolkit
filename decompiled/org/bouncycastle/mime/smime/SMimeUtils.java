package org.bouncycastle.mime.smime;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.util.Strings;

class SMimeUtils {
   private static final Map RFC5751_MICALGS;
   private static final Map RFC3851_MICALGS;
   private static final Map STANDARD_MICALGS;
   private static final Map forMic;
   private static final byte[] nl = new byte[2];

   static String lessQuotes(String var0) {
      if (var0 == null || var0.length() <= 1) {
         return var0;
      } else {
         return var0.charAt(0) == '"' && var0.charAt(var0.length() - 1) == '"' ? var0.substring(1, var0.length() - 1) : var0;
      }
   }

   static String getParameter(String var0, List<String> var1) {
      for (String var3 : var1) {
         if (var3.startsWith(var0)) {
            return var3;
         }
      }

      return null;
   }

   static ASN1ObjectIdentifier getDigestOID(String var0) {
      ASN1ObjectIdentifier var1 = (ASN1ObjectIdentifier)forMic.get(Strings.toLowerCase(var0));
      if (var1 == null) {
         throw new IllegalArgumentException("unknown micalg passed: " + var0);
      } else {
         return var1;
      }
   }

   static InputStream autoBuffer(InputStream var0) {
      return (InputStream)(var0 instanceof FileInputStream ? new BufferedInputStream(var0) : var0);
   }

   static OutputStream autoBuffer(OutputStream var0) {
      return (OutputStream)(var0 instanceof FileOutputStream ? new BufferedOutputStream(var0) : var0);
   }

   static OutputStream createUnclosable(OutputStream var0) {
      return new FilterOutputStream(var0) {
         @Override
         public void write(byte[] var1, int var2, int var3) throws IOException {
            if (var1 == null) {
               throw new NullPointerException();
            } else if ((var2 | var3 | var1.length - (var3 + var2) | var2 + var3) < 0) {
               throw new IndexOutOfBoundsException();
            } else {
               this.out.write(var1, var2, var3);
            }
         }

         @Override
         public void close() throws IOException {
         }
      };
   }

   static {
      nl[0] = 13;
      nl[1] = 10;
      HashMap var0 = new HashMap();
      var0.put(CMSAlgorithm.MD5, "md5");
      var0.put(CMSAlgorithm.SHA1, "sha-1");
      var0.put(CMSAlgorithm.SHA224, "sha-224");
      var0.put(CMSAlgorithm.SHA256, "sha-256");
      var0.put(CMSAlgorithm.SHA384, "sha-384");
      var0.put(CMSAlgorithm.SHA512, "sha-512");
      var0.put(CMSAlgorithm.GOST3411, "gostr3411-94");
      var0.put(CMSAlgorithm.GOST3411_2012_256, "gostr3411-2012-256");
      var0.put(CMSAlgorithm.GOST3411_2012_512, "gostr3411-2012-512");
      RFC5751_MICALGS = Collections.unmodifiableMap(var0);
      HashMap var1 = new HashMap();
      var1.put(CMSAlgorithm.MD5, "md5");
      var1.put(CMSAlgorithm.SHA1, "sha1");
      var1.put(CMSAlgorithm.SHA224, "sha224");
      var1.put(CMSAlgorithm.SHA256, "sha256");
      var1.put(CMSAlgorithm.SHA384, "sha384");
      var1.put(CMSAlgorithm.SHA512, "sha512");
      var1.put(CMSAlgorithm.GOST3411, "gostr3411-94");
      var1.put(CMSAlgorithm.GOST3411_2012_256, "gostr3411-2012-256");
      var1.put(CMSAlgorithm.GOST3411_2012_512, "gostr3411-2012-512");
      RFC3851_MICALGS = Collections.unmodifiableMap(var1);
      STANDARD_MICALGS = RFC5751_MICALGS;
      TreeMap var2 = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

      for (Object var4 : STANDARD_MICALGS.keySet()) {
         var2.put(STANDARD_MICALGS.get(var4).toString(), (ASN1ObjectIdentifier)var4);
      }

      for (Object var6 : RFC3851_MICALGS.keySet()) {
         var2.put(RFC3851_MICALGS.get(var6).toString(), (ASN1ObjectIdentifier)var6);
      }

      forMic = Collections.unmodifiableMap(var2);
   }
}
