package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.internal.tcnative.Buffer;
import io.netty.internal.tcnative.Library;
import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import io.netty.util.CharsetUtil;
import io.netty.util.LeakPresenceDetector;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class OpenSsl {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpenSsl.class);
   private static final Throwable UNAVAILABILITY_CAUSE;
   static final List<String> DEFAULT_CIPHERS;
   static final Set<String> AVAILABLE_CIPHER_SUITES;
   private static final Set<String> AVAILABLE_OPENSSL_CIPHER_SUITES;
   private static final Set<String> AVAILABLE_JAVA_CIPHER_SUITES;
   private static final boolean SUPPORTS_KEYMANAGER_FACTORY;
   private static final boolean USE_KEYMANAGER_FACTORY;
   private static final boolean SUPPORTS_OCSP;
   private static final boolean TLSV13_SUPPORTED;
   private static final boolean IS_BORINGSSL;
   private static final boolean IS_AWSLC;
   private static final Set<String> CLIENT_DEFAULT_PROTOCOLS;
   private static final Set<String> SERVER_DEFAULT_PROTOCOLS;
   private static final int SSL_V2_HELLO = 1;
   private static final int SSL_V2 = 2;
   private static final int SSL_V3 = 4;
   private static final int TLS_V1 = 8;
   private static final int TLS_V1_1 = 16;
   private static final int TLS_V1_2 = 32;
   private static final int TLS_V1_3 = 64;
   private static final int supportedProtocolsPacked;
   static final String[] EXTRA_SUPPORTED_TLS_1_3_CIPHERS;
   static final String EXTRA_SUPPORTED_TLS_1_3_CIPHERS_STRING;
   static final String[] NAMED_GROUPS;
   static final boolean JAVAX_CERTIFICATE_CREATION_SUPPORTED;
   private static final String[] DEFAULT_NAMED_GROUPS = new String[]{"x25519", "secp256r1", "secp384r1", "secp521r1"};

   static String checkTls13Ciphers(InternalLogger logger, String ciphers) {
      if (IS_BORINGSSL && !ciphers.isEmpty()) {
         assert EXTRA_SUPPORTED_TLS_1_3_CIPHERS.length > 0;

         Set<String> boringsslTlsv13Ciphers = new HashSet<>(EXTRA_SUPPORTED_TLS_1_3_CIPHERS.length);
         Collections.addAll(boringsslTlsv13Ciphers, EXTRA_SUPPORTED_TLS_1_3_CIPHERS);
         boolean ciphersNotMatch = false;

         for (String cipher : ciphers.split(":")) {
            if (boringsslTlsv13Ciphers.isEmpty()) {
               ciphersNotMatch = true;
               break;
            }

            if (!boringsslTlsv13Ciphers.remove(cipher) && !boringsslTlsv13Ciphers.remove(CipherSuiteConverter.toJava(cipher, "TLS"))) {
               ciphersNotMatch = true;
               break;
            }
         }

         ciphersNotMatch |= !boringsslTlsv13Ciphers.isEmpty();
         if (ciphersNotMatch) {
            if (logger.isInfoEnabled()) {
               StringBuilder javaCiphers = new StringBuilder(128);

               for (String cipher : ciphers.split(":")) {
                  javaCiphers.append(CipherSuiteConverter.toJava(cipher, "TLS")).append(":");
               }

               javaCiphers.setLength(javaCiphers.length() - 1);
               logger.info(
                  "BoringSSL doesn't allow to enable or disable TLSv1.3 ciphers explicitly. Provided TLSv1.3 ciphers: '{}', default TLSv1.3 ciphers that will be used: '{}'.",
                  javaCiphers,
                  EXTRA_SUPPORTED_TLS_1_3_CIPHERS_STRING
               );
            }

            return EXTRA_SUPPORTED_TLS_1_3_CIPHERS_STRING;
         }
      }

      return ciphers;
   }

   static boolean isSessionCacheSupported() {
      return version() >= 269484032L;
   }

   static X509Certificate selfSignedCertificate() throws CertificateException {
      return (X509Certificate)SslContext.X509_CERT_FACTORY
         .generateCertificate(
            new ByteArrayInputStream(
               "-----BEGIN CERTIFICATE-----\nMIICrjCCAZagAwIBAgIIdSvQPv1QAZQwDQYJKoZIhvcNAQELBQAwFjEUMBIGA1UEAxMLZXhhbXBs\nZS5jb20wIBcNMTgwNDA2MjIwNjU5WhgPOTk5OTEyMzEyMzU5NTlaMBYxFDASBgNVBAMTC2V4YW1w\nbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAggbWsmDQ6zNzRZ5AW8E3eoGl\nqWvOBDb5Fs1oBRrVQHuYmVAoaqwDzXYJ0LOwa293AgWEQ1jpcbZ2hpoYQzqEZBTLnFhMrhRFlH6K\nbJND8Y33kZ/iSVBBDuGbdSbJShlM+4WwQ9IAso4MZ4vW3S1iv5fGGpLgbtXRmBf/RU8omN0Gijlv\nWlLWHWijLN8xQtySFuBQ7ssW8RcKAary3pUm6UUQB+Co6lnfti0Tzag8PgjhAJq2Z3wbsGRnP2YS\nvYoaK6qzmHXRYlp/PxrjBAZAmkLJs4YTm/XFF+fkeYx4i9zqHbyone5yerRibsHaXZWLnUL+rFoe\nMdKvr0VS3sGmhQIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQADQi441pKmXf9FvUV5EHU4v8nJT9Iq\nyqwsKwXnr7AsUlDGHBD7jGrjAXnG5rGxuNKBQ35wRxJATKrUtyaquFUL6H8O6aGQehiFTk6zmPbe\n12Gu44vqqTgIUxnv3JQJiox8S2hMxsSddpeCmSdvmalvD6WG4NthH6B9ZaBEiep1+0s0RUaBYn73\nI7CCUaAtbjfR6pcJjrFk5ei7uwdQZFSJtkP2z8r7zfeANJddAKFlkaMWn7u+OIVuB4XPooWicObk\nNAHFtP65bocUYnDpTVdiyvn8DdqyZ/EO8n1bBKBzuSLplk2msW4pdgaFgY7Vw/0wzcFXfUXmL1uy\nG8sQD/wx\n-----END CERTIFICATE-----"
                  .getBytes(CharsetUtil.US_ASCII)
            )
         );
   }

   private static boolean doesSupportOcsp() {
      boolean supportsOcsp = false;
      if (version() >= 268443648L) {
         long sslCtx = -1L;

         try {
            sslCtx = SSLContext.make(16, 1);
            SSLContext.enableOcsp(sslCtx, false);
            supportsOcsp = true;
         } catch (Exception var7) {
         } finally {
            if (sslCtx != -1L) {
               SSLContext.free(sslCtx);
            }
         }
      }

      return supportsOcsp;
   }

   private static boolean doesSupportProtocol(int protocol, int opt) {
      if (opt == 0) {
         return false;
      } else {
         long sslCtx = -1L;

         boolean var5;
         try {
            sslCtx = SSLContext.make(protocol, 2);
            return true;
         } catch (Exception var9) {
            var5 = false;
         } finally {
            if (sslCtx != -1L) {
               SSLContext.free(sslCtx);
            }
         }

         return var5;
      }
   }

   public static boolean isAvailable() {
      return UNAVAILABILITY_CAUSE == null;
   }

   @Deprecated
   public static boolean isAlpnSupported() {
      return version() >= 268443648L;
   }

   public static boolean isOcspSupported() {
      return SUPPORTS_OCSP;
   }

   public static boolean isRenegotiationSupported() {
      return !IS_BORINGSSL && !IS_AWSLC;
   }

   public static int version() {
      return isAvailable() ? SSL.version() : -1;
   }

   public static String versionString() {
      return isAvailable() ? SSL.versionString() : null;
   }

   public static void ensureAvailability() {
      if (UNAVAILABILITY_CAUSE != null) {
         throw (Error)new UnsatisfiedLinkError("failed to load the required native library").initCause(UNAVAILABILITY_CAUSE);
      }
   }

   public static Throwable unavailabilityCause() {
      return UNAVAILABILITY_CAUSE;
   }

   @Deprecated
   public static Set<String> availableCipherSuites() {
      return availableOpenSslCipherSuites();
   }

   public static Set<String> availableOpenSslCipherSuites() {
      return AVAILABLE_OPENSSL_CIPHER_SUITES;
   }

   public static Set<String> availableJavaCipherSuites() {
      return AVAILABLE_JAVA_CIPHER_SUITES;
   }

   public static boolean isCipherSuiteAvailable(String cipherSuite) {
      String converted = CipherSuiteConverter.toOpenSsl(cipherSuite, IS_BORINGSSL);
      if (converted != null) {
         cipherSuite = converted;
      }

      return AVAILABLE_OPENSSL_CIPHER_SUITES.contains(cipherSuite);
   }

   public static boolean supportsKeyManagerFactory() {
      return SUPPORTS_KEYMANAGER_FACTORY;
   }

   @Deprecated
   public static boolean supportsHostnameValidation() {
      return isAvailable();
   }

   static boolean useKeyManagerFactory() {
      return USE_KEYMANAGER_FACTORY;
   }

   static long memoryAddress(ByteBuf buf) {
      assert buf.isDirect();

      if (buf.hasMemoryAddress()) {
         return buf.memoryAddress();
      } else {
         ByteBuffer byteBuffer = buf.internalNioBuffer(0, buf.readableBytes());
         return Buffer.address(byteBuffer) + byteBuffer.position();
      }
   }

   private OpenSsl() {
   }

   private static void loadTcNative() throws Exception {
      String os = PlatformDependent.normalizedOs();
      String arch = PlatformDependent.normalizedArch();
      Set<String> libNames = new LinkedHashSet<>(5);
      String staticLibName = "netty_tcnative";
      if ("linux".equals(os)) {
         for (String classifier : PlatformDependent.normalizedLinuxClassifiers()) {
            libNames.add(staticLibName + "_" + os + '_' + arch + "_" + classifier);
         }

         libNames.add(staticLibName + "_" + os + '_' + arch);
         libNames.add(staticLibName + "_" + os + '_' + arch + "_fedora");
      } else {
         libNames.add(staticLibName + "_" + os + '_' + arch);
      }

      libNames.add(staticLibName + "_" + arch);
      libNames.add(staticLibName);
      NativeLibraryLoader.loadFirstAvailable(PlatformDependent.getClassLoader(SSLContext.class), libNames.toArray(EmptyArrays.EMPTY_STRINGS));
   }

   private static boolean initializeTcNative(String engine) throws Exception {
      return Library.initialize("provided", engine);
   }

   static void releaseIfNeeded(ReferenceCounted counted) {
      if (counted.refCnt() > 0) {
         ReferenceCountUtil.safeRelease(counted);
      }
   }

   static boolean isTlsv13Supported() {
      return TLSV13_SUPPORTED;
   }

   static boolean isOptionSupported(SslContextOption<?> option) {
      if (isAvailable()) {
         if (option == OpenSslContextOption.USE_TASKS || option == OpenSslContextOption.TMP_DH_KEYLENGTH) {
            return true;
         }

         if (isBoringSSL() || isAWSLC()) {
            return option == OpenSslContextOption.ASYNC_PRIVATE_KEY_METHOD
               || option == OpenSslContextOption.PRIVATE_KEY_METHOD
               || option == OpenSslContextOption.CERTIFICATE_COMPRESSION_ALGORITHMS
               || option == OpenSslContextOption.TLS_FALSE_START
               || option == OpenSslContextOption.MAX_CERTIFICATE_LIST_BYTES;
         }
      }

      return false;
   }

   private static Set<String> defaultProtocols(String property) {
      String protocolsString = SystemPropertyUtil.get(property, null);
      Set<String> protocols = new HashSet<>();
      if (protocolsString != null) {
         for (String proto : protocolsString.split(",")) {
            String p = proto.trim();
            protocols.add(p);
         }
      } else {
         protocols.add("TLSv1.2");
         protocols.add("TLSv1.3");
      }

      return protocols;
   }

   static String[] defaultProtocols(boolean isClient) {
      Collection<String> defaultProtocols = isClient ? CLIENT_DEFAULT_PROTOCOLS : SERVER_DEFAULT_PROTOCOLS;

      assert defaultProtocols != null;

      List<String> protocols = new ArrayList<>(defaultProtocols.size());

      for (String proto : defaultProtocols) {
         if (isProtocolSupported(proto)) {
            protocols.add(proto);
         }
      }

      return protocols.toArray(EmptyArrays.EMPTY_STRINGS);
   }

   static boolean isProtocolSupported(String protocol) {
      int bit = getProtocolBit(protocol);
      return bit != -1 && (supportedProtocolsPacked & bit) != 0;
   }

   private static int getProtocolBit(String protocol) {
      if (protocol == null) {
         return -1;
      } else {
         switch (protocol) {
            case "SSLv2Hello":
               return 1;
            case "SSLv2":
               return 2;
            case "SSLv3":
               return 4;
            case "TLSv1":
               return 8;
            case "TLSv1.1":
               return 16;
            case "TLSv1.2":
               return 32;
            case "TLSv1.3":
               return 64;
            default:
               return -1;
         }
      }
   }

   static List<String> unpackSupportedProtocols() {
      List<String> protocols = new ArrayList<>(7);
      if ((supportedProtocolsPacked & 1) != 0) {
         protocols.add("SSLv2Hello");
      }

      if ((supportedProtocolsPacked & 2) != 0) {
         protocols.add("SSLv2");
      }

      if ((supportedProtocolsPacked & 4) != 0) {
         protocols.add("SSLv3");
      }

      if ((supportedProtocolsPacked & 8) != 0) {
         protocols.add("TLSv1");
      }

      if ((supportedProtocolsPacked & 16) != 0) {
         protocols.add("TLSv1.1");
      }

      if ((supportedProtocolsPacked & 32) != 0) {
         protocols.add("TLSv1.2");
      }

      if ((supportedProtocolsPacked & 64) != 0) {
         protocols.add("TLSv1.3");
      }

      return protocols;
   }

   static boolean isBoringSSL() {
      return IS_BORINGSSL;
   }

   static boolean isAWSLC() {
      return IS_AWSLC;
   }

   static {
      Throwable cause = null;
      if (SystemPropertyUtil.getBoolean("io.netty.handler.ssl.noOpenSsl", false)) {
         cause = new UnsupportedOperationException("OpenSSL was explicit disabled with -Dio.netty.handler.ssl.noOpenSsl=true");
         logger.debug("netty-tcnative explicit disabled; " + OpenSslEngine.class.getSimpleName() + " will be unavailable.", cause);
      } else {
         try {
            Class.forName("io.netty.internal.tcnative.SSLContext", false, PlatformDependent.getClassLoader(OpenSsl.class));
         } catch (ClassNotFoundException var68) {
            cause = var68;
            logger.debug("netty-tcnative not in the classpath; " + OpenSslEngine.class.getSimpleName() + " will be unavailable.");
         }

         if (cause == null) {
            try {
               loadTcNative();
            } catch (Throwable var67) {
               cause = var67;
               logger.debug(
                  "Failed to load netty-tcnative; "
                     + OpenSslEngine.class.getSimpleName()
                     + " will be unavailable, unless the application has already loaded the symbols by some other means. See https://netty.io/wiki/forked-tomcat-native.html for more information.",
                  var67
               );
            }

            try {
               String engine = SystemPropertyUtil.get("io.netty.handler.ssl.openssl.engine", null);
               if (engine == null) {
                  logger.debug("Initialize netty-tcnative using engine: 'default'");
               } else {
                  logger.debug("Initialize netty-tcnative using engine: '{}'", engine);
               }

               initializeTcNative(engine);
               cause = null;
            } catch (Throwable var76) {
               if (cause == null) {
                  cause = var76;
               }

               logger.debug(
                  "Failed to initialize netty-tcnative; "
                     + OpenSslEngine.class.getSimpleName()
                     + " will be unavailable. See https://netty.io/wiki/forked-tomcat-native.html for more information.",
                  var76
               );
            }
         }
      }

      UNAVAILABILITY_CAUSE = cause;
      CLIENT_DEFAULT_PROTOCOLS = defaultProtocols("jdk.tls.client.protocols");
      SERVER_DEFAULT_PROTOCOLS = defaultProtocols("jdk.tls.server.protocols");
      if (cause == null) {
         logger.debug("netty-tcnative using native library: {}", SSL.versionString());
         List<String> defaultCiphers = new ArrayList<>();
         Set<String> availableOpenSslCipherSuites = new LinkedHashSet<>(128);
         boolean supportsKeyManagerFactory = false;
         boolean useKeyManagerFactory = false;
         boolean tlsv13Supported = false;
         String[] namedGroups = DEFAULT_NAMED_GROUPS;
         String versionString = versionString();
         IS_BORINGSSL = "BoringSSL".equals(versionString);
         IS_AWSLC = versionString != null && versionString.startsWith("AWS-LC");
         Set<String> defaultConvertedNamedGroups = new LinkedHashSet<>(namedGroups.length);
         if (IS_BORINGSSL || IS_AWSLC) {
            defaultConvertedNamedGroups.add("X25519MLKEM768");
         }

         for (String group : namedGroups) {
            defaultConvertedNamedGroups.add(GroupsConverter.toOpenSsl(group));
         }

         if (!IS_BORINGSSL) {
            EXTRA_SUPPORTED_TLS_1_3_CIPHERS = EmptyArrays.EMPTY_STRINGS;
            EXTRA_SUPPORTED_TLS_1_3_CIPHERS_STRING = "";
         } else {
            EXTRA_SUPPORTED_TLS_1_3_CIPHERS = new String[]{"TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384", "TLS_CHACHA20_POLY1305_SHA256"};
            StringBuilder ciphersBuilder = new StringBuilder(128);

            for (String cipher : EXTRA_SUPPORTED_TLS_1_3_CIPHERS) {
               ciphersBuilder.append(cipher).append(':');
            }

            ciphersBuilder.setLength(ciphersBuilder.length() - 1);
            EXTRA_SUPPORTED_TLS_1_3_CIPHERS_STRING = ciphersBuilder.toString();
         }

         try {
            long sslCtx = SSLContext.make(63, 1);
            Iterator<String> defaultGroupsIter = defaultConvertedNamedGroups.iterator();

            while (defaultGroupsIter.hasNext()) {
               if (!SSLContext.setCurvesList(sslCtx, new String[]{defaultGroupsIter.next()})) {
                  defaultGroupsIter.remove();
                  SSL.clearError();
               }
            }

            namedGroups = defaultConvertedNamedGroups.toArray(EmptyArrays.EMPTY_STRINGS);
            long certBio = 0L;
            long keyBio = 0L;
            long cert = 0L;
            long key = 0L;

            try {
               if (SslProvider.isTlsv13Supported(SslProvider.JDK)) {
                  try {
                     StringBuilder tlsv13Ciphers = new StringBuilder();

                     for (String cipher : SslUtils.TLSV13_CIPHERS) {
                        String converted = CipherSuiteConverter.toOpenSsl(cipher, IS_BORINGSSL);
                        if (converted != null) {
                           tlsv13Ciphers.append(converted).append(':');
                        }
                     }

                     if (tlsv13Ciphers.length() == 0) {
                        tlsv13Supported = false;
                     } else {
                        tlsv13Ciphers.setLength(tlsv13Ciphers.length() - 1);
                        SSLContext.setCipherSuite(sslCtx, tlsv13Ciphers.toString(), true);
                        tlsv13Supported = true;
                     }
                  } catch (Exception var73) {
                     tlsv13Supported = false;
                     SSL.clearError();
                  }
               }

               SSLContext.setCipherSuite(sslCtx, "ALL", false);
               long ssl = SSL.newSSL(sslCtx, true);

               try {
                  for (String c : SSL.getCiphers(ssl)) {
                     if (c != null && !c.isEmpty() && !availableOpenSslCipherSuites.contains(c) && (tlsv13Supported || !SslUtils.isTLSv13Cipher(c))) {
                        availableOpenSslCipherSuites.add(c);
                     }
                  }

                  if (IS_BORINGSSL) {
                     Collections.addAll(availableOpenSslCipherSuites, EXTRA_SUPPORTED_TLS_1_3_CIPHERS);
                     Collections.addAll(availableOpenSslCipherSuites, "AEAD-AES128-GCM-SHA256", "AEAD-AES256-GCM-SHA384", "AEAD-CHACHA20-POLY1305-SHA256");
                  }

                  PemEncoded privateKey = PemPrivateKey.valueOf(
                     "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCCBtayYNDrM3NFnkBbwTd6gaWp\na84ENvkWzWgFGtVAe5iZUChqrAPNdgnQs7Brb3cCBYRDWOlxtnaGmhhDOoRkFMucWEyuFEWUfops\nk0PxjfeRn+JJUEEO4Zt1JslKGUz7hbBD0gCyjgxni9bdLWK/l8YakuBu1dGYF/9FTyiY3QaKOW9a\nUtYdaKMs3zFC3JIW4FDuyxbxFwoBqvLelSbpRRAH4KjqWd+2LRPNqDw+COEAmrZnfBuwZGc/ZhK9\nihorqrOYddFiWn8/GuMEBkCaQsmzhhOb9cUX5+R5jHiL3OodvKid7nJ6tGJuwdpdlYudQv6sWh4x\n0q+vRVLewaaFAgMBAAECggEAP8tPJvFtTxhNJAkCloHz0D0vpDHqQBMgntlkgayqmBqLwhyb18pR\ni0qwgh7HHc7wWqOOQuSqlEnrWRrdcI6TSe8R/sErzfTQNoznKWIPYcI/hskk4sdnQ//Yn9/Jvnsv\nU/BBjOTJxtD+sQbhAl80JcA3R+5sArURQkfzzHOL/YMqzAsn5hTzp7HZCxUqBk3KaHRxV7NefeOE\nxlZuWSmxYWfbFIs4kx19/1t7h8CHQWezw+G60G2VBtSBBxDnhBWvqG6R/wpzJ3nEhPLLY9T+XIHe\nipzdMOOOUZorfIg7M+pyYPji+ZIZxIpY5OjrOzXHciAjRtr5Y7l99K1CG1LguQKBgQDrQfIMxxtZ\nvxU/1cRmUV9l7pt5bjV5R6byXq178LxPKVYNjdZ840Q0/OpZEVqaT1xKVi35ohP1QfNjxPLlHD+K\niDAR9z6zkwjIrbwPCnb5kuXy4lpwPcmmmkva25fI7qlpHtbcuQdoBdCfr/KkKaUCMPyY89LCXgEw\n5KTDj64UywKBgQCNfbO+eZLGzhiHhtNJurresCsIGWlInv322gL8CSfBMYl6eNfUTZvUDdFhPISL\nUljKWzXDrjw0ujFSPR0XhUGtiq89H+HUTuPPYv25gVXO+HTgBFZEPl4PpA+BUsSVZy0NddneyqLk\n42Wey9omY9Q8WsdNQS5cbUvy0uG6WFoX7wKBgQDZ1jpW8pa0x2bZsQsm4vo+3G5CRnZlUp+XlWt2\ndDcp5dC0xD1zbs1dc0NcLeGDOTDv9FSl7hok42iHXXq8AygjEm/QcuwwQ1nC2HxmQP5holAiUs4D\nWHM8PWs3wFYPzE459EBoKTxeaeP/uWAn+he8q7d5uWvSZlEcANs/6e77eQKBgD21Ar0hfFfj7mK8\n9E0FeRZBsqK3omkfnhcYgZC11Xa2SgT1yvs2Va2n0RcdM5kncr3eBZav2GYOhhAdwyBM55XuE/sO\neokDVutNeuZ6d5fqV96TRaRBpvgfTvvRwxZ9hvKF4Vz+9wfn/JvCwANaKmegF6ejs7pvmF3whq2k\ndrZVAoGAX5YxQ5XMTD0QbMAl7/6qp6S58xNoVdfCkmkj1ZLKaHKIjS/benkKGlySVQVPexPfnkZx\np/Vv9yyphBoudiTBS9Uog66ueLYZqpgxlM/6OhYg86Gm3U2ycvMxYjBM1NFiyze21AqAhI+HX+Ot\nmraV2/guSgDgZAhukRZzeQ2RucI=\n-----END PRIVATE KEY-----"
                        .getBytes(CharsetUtil.US_ASCII)
                  );

                  try {
                     SSLContext.setCertificateCallback(sslCtx, null);
                     X509Certificate certificate = selfSignedCertificate();
                     certBio = LeakPresenceDetector.staticInitializer(() -> {
                        try {
                           return ReferenceCountedOpenSslContext.toBIO(ByteBufAllocator.DEFAULT, certificate);
                        } catch (Exception var2x) {
                           PlatformDependent.throwException(var2x);
                           throw new AssertionError(var2x);
                        }
                     });
                     cert = SSL.parseX509Chain(certBio);
                     keyBio = LeakPresenceDetector.staticInitializer(() -> {
                        try {
                           return ReferenceCountedOpenSslContext.toBIO(UnpooledByteBufAllocator.DEFAULT, privateKey.retain());
                        } catch (Exception var2x) {
                           PlatformDependent.throwException(var2x);
                           throw new AssertionError(var2x);
                        }
                     });
                     key = SSL.parsePrivateKey(keyBio, null);
                     SSL.setKeyMaterial(ssl, cert, key);
                     supportsKeyManagerFactory = true;

                     try {
                        boolean propertySet = SystemPropertyUtil.contains("io.netty.handler.ssl.openssl.useKeyManagerFactory");
                        if (!IS_BORINGSSL && !IS_AWSLC) {
                           useKeyManagerFactory = SystemPropertyUtil.getBoolean("io.netty.handler.ssl.openssl.useKeyManagerFactory", true);
                           if (propertySet) {
                              logger.info(
                                 "System property 'io.netty.handler.ssl.openssl.useKeyManagerFactory' is deprecated and so will be ignored in the future"
                              );
                           }
                        } else {
                           useKeyManagerFactory = true;
                           if (propertySet) {
                              logger.info(
                                 "System property 'io.netty.handler.ssl.openssl.useKeyManagerFactory' is deprecated and will be ignored when using BoringSSL or AWS-LC"
                              );
                           }
                        }
                     } catch (Throwable var69) {
                        logger.debug("Failed to get useKeyManagerFactory system property.");
                     }
                  } catch (Exception var70) {
                     logger.debug("KeyManagerFactory not supported", (Throwable)var70);
                     SSL.clearError();
                  } finally {
                     privateKey.release();
                  }
               } finally {
                  SSL.freeSSL(ssl);
                  if (certBio != 0L) {
                     SSL.freeBIO(certBio);
                  }

                  if (keyBio != 0L) {
                     SSL.freeBIO(keyBio);
                  }

                  if (cert != 0L) {
                     SSL.freeX509Chain(cert);
                  }

                  if (key != 0L) {
                     SSL.freePrivateKey(key);
                  }
               }

               String groups = SystemPropertyUtil.get("jdk.tls.namedGroups", null);
               if (groups == null) {
                  namedGroups = defaultConvertedNamedGroups.toArray(EmptyArrays.EMPTY_STRINGS);
               } else {
                  String[] nGroups = groups.split(",");
                  Set<String> supportedNamedGroups = new LinkedHashSet<>(nGroups.length);
                  Set<String> supportedConvertedNamedGroups = new LinkedHashSet<>(nGroups.length);
                  Set<String> unsupportedNamedGroups = new LinkedHashSet<>();

                  for (String namedGroup : nGroups) {
                     String converted = GroupsConverter.toOpenSsl(namedGroup);
                     if (SSLContext.setCurvesList(sslCtx, new String[]{converted})) {
                        supportedConvertedNamedGroups.add(converted);
                        supportedNamedGroups.add(namedGroup);
                     } else {
                        unsupportedNamedGroups.add(namedGroup);
                        SSL.clearError();
                     }
                  }

                  if (supportedNamedGroups.isEmpty()) {
                     namedGroups = defaultConvertedNamedGroups.toArray(EmptyArrays.EMPTY_STRINGS);
                     logger.info(
                        "All configured namedGroups are not supported: {}. Use default: {}.",
                        Arrays.toString(unsupportedNamedGroups.toArray(EmptyArrays.EMPTY_STRINGS)),
                        Arrays.toString((Object[])DEFAULT_NAMED_GROUPS)
                     );
                  } else {
                     String[] groupArray = supportedNamedGroups.toArray(EmptyArrays.EMPTY_STRINGS);
                     if (unsupportedNamedGroups.isEmpty()) {
                        logger.info("Using configured namedGroups -D 'jdk.tls.namedGroup': {} ", Arrays.toString((Object[])groupArray));
                     } else {
                        logger.info(
                           "Using supported configured namedGroups: {}. Unsupported namedGroups: {}. ",
                           Arrays.toString((Object[])groupArray),
                           Arrays.toString(unsupportedNamedGroups.toArray(EmptyArrays.EMPTY_STRINGS))
                        );
                     }

                     namedGroups = supportedConvertedNamedGroups.toArray(EmptyArrays.EMPTY_STRINGS);
                  }
               }
            } finally {
               SSLContext.free(sslCtx);
            }
         } catch (Exception var75) {
            logger.warn("Failed to get the list of available OpenSSL cipher suites.", (Throwable)var75);
         }

         NAMED_GROUPS = namedGroups;
         AVAILABLE_OPENSSL_CIPHER_SUITES = Collections.unmodifiableSet(availableOpenSslCipherSuites);
         Set<String> availableJavaCipherSuites = new LinkedHashSet<>(AVAILABLE_OPENSSL_CIPHER_SUITES.size() * 2);

         for (String cipherx : AVAILABLE_OPENSSL_CIPHER_SUITES) {
            if (!SslUtils.isTLSv13Cipher(cipherx)) {
               String tlsConversion = CipherSuiteConverter.toJava(cipherx, "TLS");
               if (tlsConversion != null) {
                  availableJavaCipherSuites.add(tlsConversion);
               }

               String sslConversion = CipherSuiteConverter.toJava(cipherx, "SSL");
               if (sslConversion != null) {
                  availableJavaCipherSuites.add(sslConversion);
               }
            } else {
               availableJavaCipherSuites.add(cipherx);
            }
         }

         SslUtils.addIfSupported(availableJavaCipherSuites, defaultCiphers, SslUtils.DEFAULT_CIPHER_SUITES);
         SslUtils.addIfSupported(availableJavaCipherSuites, defaultCiphers, SslUtils.TLSV13_CIPHER_SUITES);
         SslUtils.addIfSupported(availableJavaCipherSuites, defaultCiphers, EXTRA_SUPPORTED_TLS_1_3_CIPHERS);
         SslUtils.useFallbackCiphersIfDefaultIsEmpty(defaultCiphers, availableJavaCipherSuites);
         DEFAULT_CIPHERS = Collections.unmodifiableList(defaultCiphers);
         AVAILABLE_JAVA_CIPHER_SUITES = Collections.unmodifiableSet(availableJavaCipherSuites);
         Set<String> availableCipherSuites = new LinkedHashSet<>(AVAILABLE_OPENSSL_CIPHER_SUITES.size() + AVAILABLE_JAVA_CIPHER_SUITES.size());
         availableCipherSuites.addAll(AVAILABLE_OPENSSL_CIPHER_SUITES);
         availableCipherSuites.addAll(AVAILABLE_JAVA_CIPHER_SUITES);
         AVAILABLE_CIPHER_SUITES = availableCipherSuites;
         SUPPORTS_KEYMANAGER_FACTORY = supportsKeyManagerFactory;
         USE_KEYMANAGER_FACTORY = useKeyManagerFactory;
         int supportedProtocolsPackedTemp = 0;
         supportedProtocolsPackedTemp |= 1;
         if (doesSupportProtocol(1, SSL.SSL_OP_NO_SSLv2)) {
            supportedProtocolsPackedTemp |= 2;
         }

         if (doesSupportProtocol(2, SSL.SSL_OP_NO_SSLv3)) {
            supportedProtocolsPackedTemp |= 4;
         }

         if (doesSupportProtocol(4, SSL.SSL_OP_NO_TLSv1)) {
            supportedProtocolsPackedTemp |= 8;
         }

         if (doesSupportProtocol(8, SSL.SSL_OP_NO_TLSv1_1)) {
            supportedProtocolsPackedTemp |= 16;
         }

         if (doesSupportProtocol(16, SSL.SSL_OP_NO_TLSv1_2)) {
            supportedProtocolsPackedTemp |= 32;
         }

         if (tlsv13Supported && doesSupportProtocol(32, SSL.SSL_OP_NO_TLSv1_3)) {
            supportedProtocolsPackedTemp |= 64;
            TLSV13_SUPPORTED = true;
         } else {
            TLSV13_SUPPORTED = false;
         }

         supportedProtocolsPacked = supportedProtocolsPackedTemp;
         SUPPORTS_OCSP = doesSupportOcsp();
         if (logger.isDebugEnabled()) {
            logger.debug("Supported protocols (OpenSSL): {} ", unpackSupportedProtocols());
            logger.debug("Default cipher suites (OpenSSL): {}", DEFAULT_CIPHERS);
         }

         boolean javaxCertificateCreationSupported;
         try {
            javax.security.cert.X509Certificate.getInstance(
               "-----BEGIN CERTIFICATE-----\nMIICrjCCAZagAwIBAgIIdSvQPv1QAZQwDQYJKoZIhvcNAQELBQAwFjEUMBIGA1UEAxMLZXhhbXBs\nZS5jb20wIBcNMTgwNDA2MjIwNjU5WhgPOTk5OTEyMzEyMzU5NTlaMBYxFDASBgNVBAMTC2V4YW1w\nbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAggbWsmDQ6zNzRZ5AW8E3eoGl\nqWvOBDb5Fs1oBRrVQHuYmVAoaqwDzXYJ0LOwa293AgWEQ1jpcbZ2hpoYQzqEZBTLnFhMrhRFlH6K\nbJND8Y33kZ/iSVBBDuGbdSbJShlM+4WwQ9IAso4MZ4vW3S1iv5fGGpLgbtXRmBf/RU8omN0Gijlv\nWlLWHWijLN8xQtySFuBQ7ssW8RcKAary3pUm6UUQB+Co6lnfti0Tzag8PgjhAJq2Z3wbsGRnP2YS\nvYoaK6qzmHXRYlp/PxrjBAZAmkLJs4YTm/XFF+fkeYx4i9zqHbyone5yerRibsHaXZWLnUL+rFoe\nMdKvr0VS3sGmhQIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQADQi441pKmXf9FvUV5EHU4v8nJT9Iq\nyqwsKwXnr7AsUlDGHBD7jGrjAXnG5rGxuNKBQ35wRxJATKrUtyaquFUL6H8O6aGQehiFTk6zmPbe\n12Gu44vqqTgIUxnv3JQJiox8S2hMxsSddpeCmSdvmalvD6WG4NthH6B9ZaBEiep1+0s0RUaBYn73\nI7CCUaAtbjfR6pcJjrFk5ei7uwdQZFSJtkP2z8r7zfeANJddAKFlkaMWn7u+OIVuB4XPooWicObk\nNAHFtP65bocUYnDpTVdiyvn8DdqyZ/EO8n1bBKBzuSLplk2msW4pdgaFgY7Vw/0wzcFXfUXmL1uy\nG8sQD/wx\n-----END CERTIFICATE-----"
                  .getBytes(CharsetUtil.US_ASCII)
            );
            javaxCertificateCreationSupported = true;
         } catch (javax.security.cert.CertificateException var66) {
            javaxCertificateCreationSupported = false;
         }

         JAVAX_CERTIFICATE_CREATION_SUPPORTED = javaxCertificateCreationSupported;
      } else {
         DEFAULT_CIPHERS = Collections.emptyList();
         AVAILABLE_OPENSSL_CIPHER_SUITES = Collections.emptySet();
         AVAILABLE_JAVA_CIPHER_SUITES = Collections.emptySet();
         AVAILABLE_CIPHER_SUITES = Collections.emptySet();
         SUPPORTS_KEYMANAGER_FACTORY = false;
         USE_KEYMANAGER_FACTORY = false;
         SUPPORTS_OCSP = false;
         TLSV13_SUPPORTED = false;
         supportedProtocolsPacked = 0;
         IS_BORINGSSL = false;
         IS_AWSLC = false;
         EXTRA_SUPPORTED_TLS_1_3_CIPHERS = EmptyArrays.EMPTY_STRINGS;
         EXTRA_SUPPORTED_TLS_1_3_CIPHERS_STRING = "";
         NAMED_GROUPS = DEFAULT_NAMED_GROUPS;
         JAVAX_CERTIFICATE_CREATION_SUPPORTED = false;
      }
   }
}
