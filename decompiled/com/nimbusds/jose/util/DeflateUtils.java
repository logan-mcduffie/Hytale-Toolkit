package com.nimbusds.jose.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class DeflateUtils {
   private static final boolean NOWRAP = true;

   public static byte[] compress(byte[] bytes) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Deflater deflater = null;
      DeflaterOutputStream def = null;

      try {
         deflater = new Deflater(8, true);
         def = new DeflaterOutputStream(out, deflater);
         def.write(bytes);
      } finally {
         if (def != null) {
            def.close();
         }

         if (deflater != null) {
            deflater.end();
         }
      }

      return out.toByteArray();
   }

   public static byte[] decompress(byte[] bytes) throws IOException {
      Inflater inflater = null;
      InflaterInputStream inf = null;

      byte[] var6;
      try {
         inflater = new Inflater(true);
         inf = new InflaterInputStream(new ByteArrayInputStream(bytes), inflater);
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         byte[] buf = new byte[1024];

         int len;
         while ((len = inf.read(buf)) > 0) {
            out.write(buf, 0, len);
         }

         var6 = out.toByteArray();
      } finally {
         if (inf != null) {
            inf.close();
         }

         if (inflater != null) {
            inflater.end();
         }
      }

      return var6;
   }

   private DeflateUtils() {
   }
}
