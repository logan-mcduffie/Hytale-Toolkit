package io.netty.handler.ssl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.X509Certificate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

final class KeytoolSelfSignedCertGenerator {
   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss", Locale.ROOT);
   private static final String ALIAS = "alias";
   private static final String PASSWORD = "insecurepassword";
   private static final Path KEYTOOL;
   private static final String KEY_STORE_TYPE;

   private KeytoolSelfSignedCertGenerator() {
   }

   static boolean isAvailable() {
      return KEYTOOL != null;
   }

   static void generate(SelfSignedCertificate.Builder builder) throws IOException, GeneralSecurityException {
      String dirFqdn = builder.fqdn.replaceAll("[^\\w.-]", "x");
      Path directory = Files.createTempDirectory("keytool_" + dirFqdn);
      Path keyStore = directory.resolve("keystore.jks");

      try {
         Process process = new ProcessBuilder()
            .command(
               KEYTOOL.toAbsolutePath().toString(),
               "-genkeypair",
               "-keyalg",
               builder.algorithm,
               "-keysize",
               String.valueOf(builder.bits),
               "-startdate",
               DATE_FORMAT.format(builder.notBefore.toInstant().atZone(ZoneId.systemDefault())),
               "-validity",
               String.valueOf(builder.notBefore.toInstant().until(builder.notAfter.toInstant(), ChronoUnit.DAYS)),
               "-keystore",
               keyStore.toString(),
               "-alias",
               "alias",
               "-keypass",
               "insecurepassword",
               "-storepass",
               "insecurepassword",
               "-dname",
               "CN=" + builder.fqdn,
               "-storetype",
               KEY_STORE_TYPE
            )
            .redirectErrorStream(true)
            .start();

         try {
            if (!process.waitFor(60L, TimeUnit.SECONDS)) {
               process.destroyForcibly();
               throw new IOException("keytool timeout");
            }
         } catch (InterruptedException var27) {
            process.destroyForcibly();
            Thread.currentThread().interrupt();
            throw new InterruptedIOException();
         }

         if (process.exitValue() != 0) {
            ByteBuf buffer = Unpooled.buffer();

            try {
               InputStream stream = process.getInputStream();

               try {
                  while (buffer.writeBytes(stream, 4096) != -1) {
                  }
               } catch (Throwable var29) {
                  if (stream != null) {
                     try {
                        stream.close();
                     } catch (Throwable var26) {
                        var29.addSuppressed(var26);
                     }
                  }

                  throw var29;
               }

               if (stream != null) {
                  stream.close();
               }

               String log = buffer.toString(StandardCharsets.UTF_8);
               throw new IOException("Keytool exited with status " + process.exitValue() + ": " + log);
            } finally {
               buffer.release();
            }
         }

         KeyStore ks = KeyStore.getInstance(KEY_STORE_TYPE);
         InputStream is = Files.newInputStream(keyStore);

         try {
            ks.load(is, "insecurepassword".toCharArray());
         } catch (Throwable var28) {
            if (is != null) {
               try {
                  is.close();
               } catch (Throwable var25) {
                  var28.addSuppressed(var25);
               }
            }

            throw var28;
         }

         if (is != null) {
            is.close();
         }

         PrivateKeyEntry entry = (PrivateKeyEntry)ks.getEntry("alias", new PasswordProtection("insecurepassword".toCharArray()));
         builder.paths = SelfSignedCertificate.newSelfSignedCertificate(builder.fqdn, entry.getPrivateKey(), (X509Certificate)entry.getCertificate());
         builder.privateKey = entry.getPrivateKey();
      } finally {
         Files.deleteIfExists(keyStore);
         Files.delete(directory);
      }
   }

   static {
      String home = System.getProperty("java.home");
      if (home == null) {
         KEYTOOL = null;
      } else {
         Path likely = Paths.get(home).resolve("bin").resolve("keytool");
         if (Files.exists(likely)) {
            KEYTOOL = likely;
         } else {
            KEYTOOL = null;
         }
      }

      KEY_STORE_TYPE = PlatformDependent.javaVersion() >= 11 ? "PKCS12" : "JKS";
   }
}
