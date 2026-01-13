package org.bouncycastle.util.io.pem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.util.encoders.Base64;

public class PemReader extends BufferedReader {
   public static final String LAX_PEM_PARSING_SYSTEM_PROPERTY_NAME = "org.bouncycastle.pemreader.lax";
   private static final String BEGIN = "-----BEGIN ";
   private static final String END = "-----END ";
   private static final Logger LOG = Logger.getLogger(PemReader.class.getName());

   public PemReader(Reader var1) {
      super(var1);
   }

   public PemObject readPemObject() throws IOException {
      String var1 = this.readLine();

      while (var1 != null && !var1.startsWith("-----BEGIN ")) {
         var1 = this.readLine();
      }

      if (var1 != null) {
         var1 = var1.substring("-----BEGIN ".length()).trim();
         int var2 = var1.indexOf(45);
         if (var2 > 0 && var1.endsWith("-----") && var1.length() - var2 == 5) {
            String var3 = var1.substring(0, var2);
            return this.loadObject(var3);
         }
      }

      return null;
   }

   private PemObject loadObject(String var1) throws IOException {
      String var3 = "-----END " + var1 + "-----";
      StringBuffer var4 = new StringBuffer();
      ArrayList var5 = new ArrayList();

      String var2;
      while ((var2 = this.readLine()) != null) {
         int var6 = var2.indexOf(58);
         if (var6 >= 0) {
            String var9 = var2.substring(0, var6);
            String var8 = var2.substring(var6 + 1).trim();
            var5.add(new PemHeader(var9, var8));
         } else {
            if (System.getProperty("org.bouncycastle.pemreader.lax", "false").equalsIgnoreCase("true")) {
               String var7 = var2.trim();
               if (!var7.equals(var2) && LOG.isLoggable(Level.WARNING)) {
                  LOG.log(Level.WARNING, "PEM object contains whitespaces on -----END line", (Throwable)(new Exception("trace")));
               }

               var2 = var7;
            }

            if (var2.indexOf(var3) == 0) {
               break;
            }

            var4.append(var2.trim());
         }
      }

      if (var2 == null) {
         throw new IOException(var3 + " not found");
      } else {
         return new PemObject(var1, var5, Base64.decode(var4.toString()));
      }
   }
}
