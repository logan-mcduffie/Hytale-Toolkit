package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.CharsetUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PemReader {
   private static final Pattern CERT_HEADER = Pattern.compile("-+BEGIN\\s[^-\\r\\n]*CERTIFICATE[^-\\r\\n]*-+(?:\\s|\\r|\\n)+");
   private static final Pattern CERT_FOOTER = Pattern.compile("-+END\\s[^-\\r\\n]*CERTIFICATE[^-\\r\\n]*-+(?:\\s|\\r|\\n)*");
   private static final Pattern KEY_HEADER = Pattern.compile("-+BEGIN\\s[^-\\r\\n]*PRIVATE\\s+KEY[^-\\r\\n]*-+(?:\\s|\\r|\\n)+");
   private static final Pattern KEY_FOOTER = Pattern.compile("-+END\\s[^-\\r\\n]*PRIVATE\\s+KEY[^-\\r\\n]*-+(?:\\s|\\r|\\n)*");
   private static final Pattern BODY = Pattern.compile("[a-z0-9+/=][a-z0-9+/=\\r\\n]*", 2);

   static ByteBuf[] readCertificates(File file) throws CertificateException {
      try {
         InputStream in = new FileInputStream(file);

         ByteBuf[] var2;
         try {
            var2 = readCertificates(in);
         } catch (Throwable var5) {
            try {
               in.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }

            throw var5;
         }

         in.close();
         return var2;
      } catch (IOException var6) {
         throw new CertificateException("could not find certificate file: " + file);
      }
   }

   static ByteBuf[] readCertificates(InputStream in) throws CertificateException {
      String content;
      try {
         content = readContent(in);
      } catch (IOException var7) {
         throw new CertificateException("failed to read certificate input stream", var7);
      }

      List<ByteBuf> certs = new ArrayList<>();
      Matcher m = CERT_HEADER.matcher(content);
      int start = 0;

      while (m.find(start)) {
         start = m.end();
         m.usePattern(BODY);
         if (!m.find(start)) {
            break;
         }

         ByteBuf base64 = Unpooled.copiedBuffer(m.group(0), CharsetUtil.US_ASCII);
         start = m.end();
         m.usePattern(CERT_FOOTER);
         if (!m.find(start)) {
            break;
         }

         ByteBuf der = Base64.decode(base64);
         base64.release();
         certs.add(der);
         start = m.end();
         m.usePattern(CERT_HEADER);
      }

      if (certs.isEmpty()) {
         throw new CertificateException("found no certificates in input stream");
      } else {
         return certs.toArray(new ByteBuf[0]);
      }
   }

   static ByteBuf readPrivateKey(File file) throws KeyException {
      try {
         InputStream in = new FileInputStream(file);

         ByteBuf var2;
         try {
            var2 = readPrivateKey(in);
         } catch (Throwable var5) {
            try {
               in.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }

            throw var5;
         }

         in.close();
         return var2;
      } catch (IOException var6) {
         throw new KeyException("could not find key file: " + file);
      }
   }

   static ByteBuf readPrivateKey(InputStream in) throws KeyException {
      String content;
      try {
         content = readContent(in);
      } catch (IOException var6) {
         throw new KeyException("failed to read key input stream", var6);
      }

      int start = 0;
      Matcher m = KEY_HEADER.matcher(content);
      if (!m.find(start)) {
         throw keyNotFoundException();
      } else {
         start = m.end();
         m.usePattern(BODY);
         if (!m.find(start)) {
            throw keyNotFoundException();
         } else {
            ByteBuf base64 = Unpooled.copiedBuffer(m.group(0), CharsetUtil.US_ASCII);
            start = m.end();
            m.usePattern(KEY_FOOTER);
            if (!m.find(start)) {
               throw keyNotFoundException();
            } else {
               ByteBuf der = Base64.decode(base64);
               base64.release();
               return der;
            }
         }
      }
   }

   private static KeyException keyNotFoundException() {
      return new KeyException(
         "could not find a PKCS #8 private key in input stream (see https://netty.io/wiki/sslcontextbuilder-and-private-key.html for more information)"
      );
   }

   private static String readContent(InputStream in) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buf = new byte[8192];

      while (true) {
         int ret = in.read(buf);
         if (ret < 0) {
            return out.toString(CharsetUtil.US_ASCII.name());
         }

         out.write(buf, 0, ret);
      }
   }

   private PemReader() {
   }
}
