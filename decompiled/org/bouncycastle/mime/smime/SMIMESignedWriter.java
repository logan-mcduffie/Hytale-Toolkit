package org.bouncycastle.mime.smime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedDataStreamGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeWriter;
import org.bouncycastle.mime.encoding.Base64OutputStream;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.Strings;

public class SMIMESignedWriter extends MimeWriter {
   public static final Map RFC3851_MICALGS;
   public static final Map RFC5751_MICALGS;
   public static final Map STANDARD_MICALGS;
   private final CMSSignedDataStreamGenerator sigGen;
   private final String boundary;
   private final OutputStream mimeOut;
   private final String contentTransferEncoding;

   private SMIMESignedWriter(SMIMESignedWriter.Builder var1, Map<String, String> var2, String var3, OutputStream var4) {
      super(new Headers(mapToLines(var2), var1.contentTransferEncoding));
      this.sigGen = var1.sigGen;
      this.contentTransferEncoding = var1.contentTransferEncoding;
      this.boundary = var3;
      this.mimeOut = var4;
   }

   @Override
   public OutputStream getContentStream() throws IOException {
      this.headers.dumpHeaders(this.mimeOut);
      this.mimeOut.write(Strings.toByteArray("\r\n"));
      if (this.boundary == null) {
         return null;
      } else {
         this.mimeOut.write(Strings.toByteArray("This is an S/MIME signed message\r\n"));
         this.mimeOut.write(Strings.toByteArray("\r\n--"));
         this.mimeOut.write(Strings.toByteArray(this.boundary));
         this.mimeOut.write(Strings.toByteArray("\r\n"));
         ByteArrayOutputStream var1 = new ByteArrayOutputStream();
         Base64OutputStream var2 = new Base64OutputStream(var1);
         return new SMIMESignedWriter.ContentOutputStream(this.sigGen.open(var2, false, SMimeUtils.createUnclosable(this.mimeOut)), this.mimeOut, var1, var2);
      }
   }

   static {
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
   }

   public static class Builder {
      private static final String[] detHeaders = new String[]{"Content-Type"};
      private static final String[] detValues = new String[]{"multipart/signed; protocol=\"application/pkcs7-signature\""};
      private static final String[] encHeaders = new String[]{"Content-Type", "Content-Disposition", "Content-Transfer-Encoding", "Content-Description"};
      private static final String[] encValues = new String[]{
         "application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data", "attachment; filename=\"smime.p7m\"", "base64", "S/MIME Signed Message"
      };
      private final CMSSignedDataStreamGenerator sigGen = new CMSSignedDataStreamGenerator();
      private final Map<String, String> extraHeaders = new LinkedHashMap<>();
      private final boolean encapsulated;
      private final Map micAlgs = SMIMESignedWriter.STANDARD_MICALGS;
      String contentTransferEncoding = "base64";

      public Builder() {
         this(false);
      }

      public Builder(boolean var1) {
         this.encapsulated = var1;
      }

      public SMIMESignedWriter.Builder withHeader(String var1, String var2) {
         this.extraHeaders.put(var1, var2);
         return this;
      }

      public SMIMESignedWriter.Builder addCertificate(X509CertificateHolder var1) throws CMSException {
         this.sigGen.addCertificate(var1);
         return this;
      }

      public SMIMESignedWriter.Builder addCertificates(Store var1) throws CMSException {
         this.sigGen.addCertificates(var1);
         return this;
      }

      public SMIMESignedWriter.Builder addSignerInfoGenerator(SignerInfoGenerator var1) {
         this.sigGen.addSignerInfoGenerator(var1);
         return this;
      }

      public SMIMESignedWriter build(OutputStream var1) {
         LinkedHashMap var2 = new LinkedHashMap();
         String var3;
         if (this.encapsulated) {
            var3 = null;

            for (int var4 = 0; var4 != encHeaders.length; var4++) {
               var2.put(encHeaders[var4], encValues[var4]);
            }
         } else {
            var3 = this.generateBoundary();
            StringBuilder var6 = new StringBuilder(detValues[0]);
            this.addHashHeader(var6, this.sigGen.getDigestAlgorithms());
            this.addBoundary(var6, var3);
            var2.put(detHeaders[0], var6.toString());

            for (int var5 = 1; var5 < detHeaders.length; var5++) {
               var2.put(detHeaders[var5], detValues[var5]);
            }
         }

         for (Entry var8 : this.extraHeaders.entrySet()) {
            var2.put((String)var8.getKey(), (String)var8.getValue());
         }

         return new SMIMESignedWriter(this, var2, var3, SMimeUtils.autoBuffer(var1));
      }

      private void addHashHeader(StringBuilder var1, List var2) {
         int var3 = 0;
         Iterator var4 = var2.iterator();
         TreeSet var5 = new TreeSet();

         while (var4.hasNext()) {
            AlgorithmIdentifier var6 = (AlgorithmIdentifier)var4.next();
            String var7 = (String)this.micAlgs.get(var6.getAlgorithm());
            if (var7 == null) {
               var5.add("unknown");
            } else {
               var5.add(var7);
            }
         }

         for (String var9 : var5) {
            if (var3 == 0) {
               if (var5.size() != 1) {
                  var1.append("; micalg=\"");
               } else {
                  var1.append("; micalg=");
               }
            } else {
               var1.append(',');
            }

            var1.append(var9);
            var3++;
         }

         if (var3 != 0 && var5.size() != 1) {
            var1.append('"');
         }
      }

      private void addBoundary(StringBuilder var1, String var2) {
         var1.append(";\r\n\tboundary=\"");
         var1.append(var2);
         var1.append("\"");
      }

      private String generateBoundary() {
         SecureRandom var1 = new SecureRandom();
         return "==" + new BigInteger(180, var1).setBit(179).toString(16) + "=";
      }
   }

   private class ContentOutputStream extends OutputStream {
      private final OutputStream main;
      private final OutputStream backing;
      private final ByteArrayOutputStream sigStream;
      private final OutputStream sigBase;

      ContentOutputStream(OutputStream nullx, OutputStream nullxx, ByteArrayOutputStream nullxxx, OutputStream nullxxxx) {
         this.main = nullx;
         this.backing = nullxx;
         this.sigStream = nullxxx;
         this.sigBase = nullxxxx;
      }

      @Override
      public void write(byte[] var1) throws IOException {
         this.main.write(var1);
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         this.main.write(var1, var2, var3);
      }

      @Override
      public void write(int var1) throws IOException {
         this.main.write(var1);
      }

      @Override
      public void close() throws IOException {
         if (SMIMESignedWriter.this.boundary != null) {
            this.main.close();
            this.backing.write(Strings.toByteArray("\r\n--"));
            this.backing.write(Strings.toByteArray(SMIMESignedWriter.this.boundary));
            this.backing.write(Strings.toByteArray("\r\n"));
            this.backing.write(Strings.toByteArray("Content-Type: application/pkcs7-signature; name=\"smime.p7s\"\r\n"));
            this.backing.write(Strings.toByteArray("Content-Transfer-Encoding: base64\r\n"));
            this.backing.write(Strings.toByteArray("Content-Disposition: attachment; filename=\"smime.p7s\"\r\n"));
            this.backing.write(Strings.toByteArray("\r\n"));
            if (this.sigBase != null) {
               this.sigBase.close();
            }

            this.backing.write(this.sigStream.toByteArray());
            this.backing.write(Strings.toByteArray("\r\n--"));
            this.backing.write(Strings.toByteArray(SMIMESignedWriter.this.boundary));
            this.backing.write(Strings.toByteArray("--\r\n"));
         }

         if (this.backing != null) {
            this.backing.close();
         }
      }
   }
}
