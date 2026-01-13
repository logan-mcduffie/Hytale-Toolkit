package io.netty.handler.ssl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

@Deprecated
public final class SelfSignedCertificate {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SelfSignedCertificate.class);
   private static final Date DEFAULT_NOT_BEFORE = new Date(
      SystemPropertyUtil.getLong("io.netty.selfSignedCertificate.defaultNotBefore", System.currentTimeMillis() - 31536000000L)
   );
   private static final Date DEFAULT_NOT_AFTER = new Date(SystemPropertyUtil.getLong("io.netty.selfSignedCertificate.defaultNotAfter", 253402300799000L));
   private static final int DEFAULT_KEY_LENGTH_BITS = SystemPropertyUtil.getInt("io.netty.handler.ssl.util.selfSignedKeyStrength", 2048);
   private final File certificate;
   private final File privateKey;
   private final X509Certificate cert;
   private final PrivateKey key;

   public SelfSignedCertificate() throws CertificateException {
      this(new SelfSignedCertificate.Builder());
   }

   public SelfSignedCertificate(Date notBefore, Date notAfter) throws CertificateException {
      this(new SelfSignedCertificate.Builder().notBefore(notBefore).notAfter(notAfter));
   }

   public SelfSignedCertificate(Date notBefore, Date notAfter, String algorithm, int bits) throws CertificateException {
      this(new SelfSignedCertificate.Builder().notBefore(notBefore).notAfter(notAfter).algorithm(algorithm).bits(bits));
   }

   public SelfSignedCertificate(String fqdn) throws CertificateException {
      this(new SelfSignedCertificate.Builder().fqdn(fqdn));
   }

   public SelfSignedCertificate(String fqdn, String algorithm, int bits) throws CertificateException {
      this(new SelfSignedCertificate.Builder().fqdn(fqdn).algorithm(algorithm).bits(bits));
   }

   public SelfSignedCertificate(String fqdn, Date notBefore, Date notAfter) throws CertificateException {
      this(new SelfSignedCertificate.Builder().fqdn(fqdn).notBefore(notBefore).notAfter(notAfter));
   }

   public SelfSignedCertificate(String fqdn, Date notBefore, Date notAfter, String algorithm, int bits) throws CertificateException {
      this(new SelfSignedCertificate.Builder().fqdn(fqdn).notBefore(notBefore).notAfter(notAfter).algorithm(algorithm).bits(bits));
   }

   public SelfSignedCertificate(String fqdn, SecureRandom random, int bits) throws CertificateException {
      this(new SelfSignedCertificate.Builder().fqdn(fqdn).random(random).bits(bits));
   }

   public SelfSignedCertificate(String fqdn, SecureRandom random, String algorithm, int bits) throws CertificateException {
      this(new SelfSignedCertificate.Builder().fqdn(fqdn).random(random).algorithm(algorithm).bits(bits));
   }

   public SelfSignedCertificate(String fqdn, SecureRandom random, int bits, Date notBefore, Date notAfter) throws CertificateException {
      this(new SelfSignedCertificate.Builder().fqdn(fqdn).notBefore(notBefore).notAfter(notAfter).random(random).bits(bits));
   }

   public SelfSignedCertificate(String fqdn, SecureRandom random, int bits, Date notBefore, Date notAfter, String algorithm) throws CertificateException {
      this(new SelfSignedCertificate.Builder().fqdn(fqdn).random(random).algorithm(algorithm).bits(bits).notBefore(notBefore).notAfter(notAfter));
   }

   private SelfSignedCertificate(SelfSignedCertificate.Builder builder) throws CertificateException {
      if (!builder.generateCertificateBuilder() && !builder.generateBc() && !builder.generateKeytool() && !builder.generateSunMiscSecurity()) {
         throw (CertificateException)builder.failure;
      } else {
         this.certificate = new File(builder.paths[0]);
         this.privateKey = new File(builder.paths[1]);
         this.key = builder.privateKey;

         try {
            FileInputStream certificateInput = new FileInputStream(this.certificate);

            try {
               this.cert = (X509Certificate)CertificateFactory.getInstance("X509").generateCertificate(certificateInput);
            } catch (Throwable var6) {
               try {
                  certificateInput.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }

               throw var6;
            }

            certificateInput.close();
         } catch (Exception var7) {
            throw new CertificateEncodingException(var7);
         }
      }
   }

   public static SelfSignedCertificate.Builder builder() {
      return new SelfSignedCertificate.Builder();
   }

   public File certificate() {
      return this.certificate;
   }

   public File privateKey() {
      return this.privateKey;
   }

   public X509Certificate cert() {
      return this.cert;
   }

   public PrivateKey key() {
      return this.key;
   }

   public void delete() {
      safeDelete(this.certificate);
      safeDelete(this.privateKey);
   }

   static String[] newSelfSignedCertificate(String fqdn, PrivateKey key, X509Certificate cert) throws IOException, CertificateEncodingException {
      ByteBuf wrappedBuf = Unpooled.wrappedBuffer(key.getEncoded());

      String keyText;
      try {
         ByteBuf encodedBuf = Base64.encode(wrappedBuf, true);

         try {
            keyText = "-----BEGIN PRIVATE KEY-----\n" + encodedBuf.toString(CharsetUtil.US_ASCII) + "\n-----END PRIVATE KEY-----\n";
         } finally {
            encodedBuf.release();
         }
      } finally {
         wrappedBuf.release();
      }

      fqdn = fqdn.replaceAll("[^\\w.-]", "x");
      File keyFile = PlatformDependent.createTempFile("keyutil_" + fqdn + '_', ".key", null);
      keyFile.deleteOnExit();
      FileOutputStream keyOut = new FileOutputStream(keyFile);

      try {
         keyOut.write(keyText.getBytes(CharsetUtil.US_ASCII));
         keyOut.close();
         keyOut = null;
      } finally {
         if (keyOut != null) {
            safeClose(keyFile, keyOut);
            safeDelete(keyFile);
         }
      }

      wrappedBuf = Unpooled.wrappedBuffer(cert.getEncoded());

      String certText;
      try {
         ByteBuf var56 = Base64.encode(wrappedBuf, true);

         try {
            certText = "-----BEGIN CERTIFICATE-----\n" + var56.toString(CharsetUtil.US_ASCII) + "\n-----END CERTIFICATE-----\n";
         } finally {
            var56.release();
         }
      } finally {
         wrappedBuf.release();
      }

      File certFile = PlatformDependent.createTempFile("keyutil_" + fqdn + '_', ".crt", null);
      certFile.deleteOnExit();
      FileOutputStream certOut = new FileOutputStream(certFile);

      try {
         certOut.write(certText.getBytes(CharsetUtil.US_ASCII));
         certOut.close();
         certOut = null;
      } finally {
         if (certOut != null) {
            safeClose(certFile, certOut);
            safeDelete(certFile);
            safeDelete(keyFile);
         }
      }

      return new String[]{certFile.getPath(), keyFile.getPath()};
   }

   private static void safeDelete(File certFile) {
      if (!certFile.delete() && logger.isWarnEnabled()) {
         logger.warn("Failed to delete a file: " + certFile);
      }
   }

   private static void safeClose(File keyFile, OutputStream keyOut) {
      try {
         keyOut.close();
      } catch (IOException var3) {
         if (logger.isWarnEnabled()) {
            logger.warn("Failed to close a file: " + keyFile, (Throwable)var3);
         }
      }
   }

   private static boolean isBouncyCastleAvailable() {
      try {
         Class.forName("org.bouncycastle.cert.X509v3CertificateBuilder");
         return true;
      } catch (ClassNotFoundException var1) {
         return false;
      }
   }

   public static final class Builder {
      String fqdn = "localhost";
      SecureRandom random;
      int bits = SelfSignedCertificate.DEFAULT_KEY_LENGTH_BITS;
      Date notBefore = SelfSignedCertificate.DEFAULT_NOT_BEFORE;
      Date notAfter = SelfSignedCertificate.DEFAULT_NOT_AFTER;
      String algorithm = "RSA";
      Throwable failure;
      KeyPair keypair;
      PrivateKey privateKey;
      String[] paths;

      private Builder() {
      }

      public SelfSignedCertificate.Builder fqdn(String fqdn) {
         this.fqdn = ObjectUtil.checkNotNullWithIAE(fqdn, "fqdn");
         return this;
      }

      public SelfSignedCertificate.Builder random(SecureRandom random) {
         this.random = random;
         return this;
      }

      public SelfSignedCertificate.Builder bits(int bits) {
         this.bits = bits;
         return this;
      }

      public SelfSignedCertificate.Builder notBefore(Date notBefore) {
         this.notBefore = ObjectUtil.checkNotNullWithIAE(notBefore, "notBefore");
         return this;
      }

      public SelfSignedCertificate.Builder notAfter(Date notAfter) {
         this.notAfter = ObjectUtil.checkNotNullWithIAE(notAfter, "notAfter");
         return this;
      }

      public SelfSignedCertificate.Builder algorithm(String algorithm) {
         if ("EC".equalsIgnoreCase(algorithm)) {
            this.algorithm = "EC";
         } else {
            if (!"RSA".equalsIgnoreCase(algorithm)) {
               throw new IllegalArgumentException("Algorithm not valid: " + algorithm);
            }

            this.algorithm = "RSA";
         }

         return this;
      }

      private SecureRandom randomOrDefault() {
         return this.random == null ? ThreadLocalInsecureRandom.current() : this.random;
      }

      private void generateKeyPairLocally() {
         if (this.keypair == null) {
            try {
               KeyPairGenerator keyGen = KeyPairGenerator.getInstance(this.algorithm);
               keyGen.initialize(this.bits, this.randomOrDefault());
               this.keypair = keyGen.generateKeyPair();
            } catch (NoSuchAlgorithmException var2) {
               throw new IllegalStateException(var2);
            }

            this.privateKey = this.keypair.getPrivate();
         }
      }

      private void addFailure(Throwable t) {
         if (this.failure != null) {
            t.addSuppressed(this.failure);
         }

         this.failure = t;
      }

      boolean generateBc() {
         if (!SelfSignedCertificate.isBouncyCastleAvailable()) {
            SelfSignedCertificate.logger.debug("Failed to generate a self-signed X.509 certificate because BouncyCastle PKIX is not available in classpath");
            return false;
         } else {
            this.generateKeyPairLocally();

            try {
               this.paths = BouncyCastleSelfSignedCertGenerator.generate(
                  this.fqdn, this.keypair, this.randomOrDefault(), this.notBefore, this.notAfter, this.algorithm
               );
               return true;
            } catch (Throwable var2) {
               SelfSignedCertificate.logger.debug("Failed to generate a self-signed X.509 certificate using Bouncy Castle:", var2);
               this.addFailure(var2);
               return false;
            }
         }
      }

      boolean generateKeytool() {
         if (!KeytoolSelfSignedCertGenerator.isAvailable()) {
            SelfSignedCertificate.logger.debug("Not attempting to generate certificate with keytool because keytool is missing");
            return false;
         } else if (this.random != null) {
            SelfSignedCertificate.logger.debug("Not attempting to generate certificate with keytool because of explicitly set SecureRandom");
            return false;
         } else {
            try {
               KeytoolSelfSignedCertGenerator.generate(this);
               return true;
            } catch (Throwable var2) {
               SelfSignedCertificate.logger.debug("Failed to generate a self-signed X.509 certificate using keytool:", var2);
               this.addFailure(var2);
               return false;
            }
         }
      }

      boolean generateCertificateBuilder() {
         if (!CertificateBuilderCertGenerator.isAvailable()) {
            SelfSignedCertificate.logger.debug("Not attempting to generate a certificate with CertificateBuilder because it's not available on the classpath");
            return false;
         } else {
            try {
               CertificateBuilderCertGenerator.generate(this);
               return true;
            } catch (CertificateException var3) {
               SelfSignedCertificate.logger.debug(var3);
               this.addFailure(var3);
            } catch (Exception var4) {
               String msg = "Failed to generate a self-signed X.509 certificate using CertificateBuilder:";
               SelfSignedCertificate.logger.debug(msg, (Throwable)var4);
               this.addFailure(new CertificateException(msg, var4));
            }

            return false;
         }
      }

      boolean generateSunMiscSecurity() {
         this.generateKeyPairLocally();

         try {
            this.paths = OpenJdkSelfSignedCertGenerator.generate(this.fqdn, this.keypair, this.randomOrDefault(), this.notBefore, this.notAfter, this.algorithm);
            return true;
         } catch (Throwable var3) {
            SelfSignedCertificate.logger.debug("Failed to generate a self-signed X.509 certificate using sun.security.x509:", var3);
            CertificateException certificateException = new CertificateException(
               "No provider succeeded to generate a self-signed certificate. See debug log for the root cause.", var3
            );
            this.addFailure(certificateException);
            return false;
         }
      }

      public SelfSignedCertificate build() throws CertificateException {
         return new SelfSignedCertificate(this);
      }
   }
}
