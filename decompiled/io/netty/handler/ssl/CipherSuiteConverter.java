package io.netty.handler.ssl;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CipherSuiteConverter {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(CipherSuiteConverter.class);
   private static final Pattern JAVA_CIPHERSUITE_PATTERN = Pattern.compile("^(?:TLS|SSL)_((?:(?!_WITH_).)+)_WITH_(.*)_(.*)$");
   private static final Pattern OPENSSL_CIPHERSUITE_PATTERN = Pattern.compile(
      "^(?:((?:(?:EXP-)?(?:(?:DHE|EDH|ECDH|ECDHE|SRP|RSA)-(?:DSS|RSA|ECDSA|PSK)|(?:ADH|AECDH|KRB5|PSK|SRP)))|EXP)-)?(.*)-(.*)$"
   );
   private static final Pattern JAVA_AES_CBC_PATTERN = Pattern.compile("^(AES)_([0-9]+)_CBC$");
   private static final Pattern JAVA_AES_PATTERN = Pattern.compile("^(AES)_([0-9]+)_(.*)$");
   private static final Pattern OPENSSL_AES_CBC_PATTERN = Pattern.compile("^(AES)([0-9]+)$");
   private static final Pattern OPENSSL_AES_PATTERN = Pattern.compile("^(AES)([0-9]+)-(.*)$");
   private static final ConcurrentMap<String, CipherSuiteConverter.CachedValue> j2o = new ConcurrentHashMap<>();
   private static final ConcurrentMap<String, Map<String, String>> o2j = new ConcurrentHashMap<>();
   private static final Map<String, String> j2oTls13;
   private static final Map<String, Map<String, String>> o2jTls13;

   static void clearCache() {
      j2o.clear();
      o2j.clear();
   }

   static boolean isJ2OCached(String key, String value) {
      CipherSuiteConverter.CachedValue cached = j2o.get(key);
      return cached != null && value.equals(cached.value);
   }

   static boolean isO2JCached(String key, String protocol, String value) {
      Map<String, String> p2j = o2j.get(key);
      return p2j == null ? false : value.equals(p2j.get(protocol));
   }

   public static String toOpenSsl(String javaCipherSuite, boolean boringSSL) {
      CipherSuiteConverter.CachedValue converted = j2o.get(javaCipherSuite);
      return converted != null ? converted.value : cacheFromJava(javaCipherSuite, boringSSL);
   }

   private static String cacheFromJava(String javaCipherSuite, boolean boringSSL) {
      String converted = j2oTls13.get(javaCipherSuite);
      if (converted != null) {
         return boringSSL ? converted : javaCipherSuite;
      } else {
         String openSslCipherSuite = toOpenSslUncached(javaCipherSuite, boringSSL);
         j2o.putIfAbsent(javaCipherSuite, CipherSuiteConverter.CachedValue.of(openSslCipherSuite));
         if (openSslCipherSuite == null) {
            return null;
         } else {
            String javaCipherSuiteSuffix = javaCipherSuite.substring(4);
            Map<String, String> p2j = new HashMap<>(4);
            p2j.put("", javaCipherSuiteSuffix);
            p2j.put("SSL", "SSL_" + javaCipherSuiteSuffix);
            p2j.put("TLS", "TLS_" + javaCipherSuiteSuffix);
            o2j.put(openSslCipherSuite, p2j);
            logger.debug("Cipher suite mapping: {} => {}", javaCipherSuite, openSslCipherSuite);
            return openSslCipherSuite;
         }
      }
   }

   static String toOpenSslUncached(String javaCipherSuite, boolean boringSSL) {
      String converted = j2oTls13.get(javaCipherSuite);
      if (converted != null) {
         return boringSSL ? converted : javaCipherSuite;
      } else {
         Matcher m = JAVA_CIPHERSUITE_PATTERN.matcher(javaCipherSuite);
         if (!m.matches()) {
            return null;
         } else {
            String handshakeAlgo = toOpenSslHandshakeAlgo(m.group(1));
            String bulkCipher = toOpenSslBulkCipher(m.group(2));
            String hmacAlgo = toOpenSslHmacAlgo(m.group(3));
            if (handshakeAlgo.isEmpty()) {
               return bulkCipher + '-' + hmacAlgo;
            } else {
               return bulkCipher.contains("CHACHA20") ? handshakeAlgo + '-' + bulkCipher : handshakeAlgo + '-' + bulkCipher + '-' + hmacAlgo;
            }
         }
      }
   }

   private static String toOpenSslHandshakeAlgo(String handshakeAlgo) {
      boolean export = handshakeAlgo.endsWith("_EXPORT");
      if (export) {
         handshakeAlgo = handshakeAlgo.substring(0, handshakeAlgo.length() - 7);
      }

      if ("RSA".equals(handshakeAlgo)) {
         handshakeAlgo = "";
      } else if (handshakeAlgo.endsWith("_anon")) {
         handshakeAlgo = 'A' + handshakeAlgo.substring(0, handshakeAlgo.length() - 5);
      }

      if (export) {
         if (handshakeAlgo.isEmpty()) {
            handshakeAlgo = "EXP";
         } else {
            handshakeAlgo = "EXP-" + handshakeAlgo;
         }
      }

      return handshakeAlgo.replace('_', '-');
   }

   private static String toOpenSslBulkCipher(String bulkCipher) {
      if (bulkCipher.startsWith("AES_")) {
         Matcher m = JAVA_AES_CBC_PATTERN.matcher(bulkCipher);
         if (m.matches()) {
            return m.replaceFirst("$1$2");
         }

         m = JAVA_AES_PATTERN.matcher(bulkCipher);
         if (m.matches()) {
            return m.replaceFirst("$1$2-$3");
         }
      }

      if ("3DES_EDE_CBC".equals(bulkCipher)) {
         return "DES-CBC3";
      } else if ("RC4_128".equals(bulkCipher) || "RC4_40".equals(bulkCipher)) {
         return "RC4";
      } else if ("DES40_CBC".equals(bulkCipher) || "DES_CBC_40".equals(bulkCipher)) {
         return "DES-CBC";
      } else {
         return "RC2_CBC_40".equals(bulkCipher) ? "RC2-CBC" : bulkCipher.replace('_', '-');
      }
   }

   private static String toOpenSslHmacAlgo(String hmacAlgo) {
      return hmacAlgo;
   }

   public static String toJava(String openSslCipherSuite, String protocol) {
      Map<String, String> p2j = o2j.get(openSslCipherSuite);
      if (p2j == null) {
         p2j = cacheFromOpenSsl(openSslCipherSuite);
         if (p2j == null) {
            return null;
         }
      }

      String javaCipherSuite = p2j.get(protocol);
      if (javaCipherSuite == null) {
         String cipher = p2j.get("");
         if (cipher == null) {
            return null;
         }

         javaCipherSuite = protocol + '_' + cipher;
      }

      return javaCipherSuite;
   }

   private static Map<String, String> cacheFromOpenSsl(String openSslCipherSuite) {
      Map<String, String> converted = o2jTls13.get(openSslCipherSuite);
      if (converted != null) {
         return converted;
      } else {
         String javaCipherSuiteSuffix = toJavaUncached0(openSslCipherSuite, false);
         if (javaCipherSuiteSuffix == null) {
            return null;
         } else {
            String javaCipherSuiteSsl = "SSL_" + javaCipherSuiteSuffix;
            String javaCipherSuiteTls = "TLS_" + javaCipherSuiteSuffix;
            Map<String, String> p2j = new HashMap<>(4);
            p2j.put("", javaCipherSuiteSuffix);
            p2j.put("SSL", javaCipherSuiteSsl);
            p2j.put("TLS", javaCipherSuiteTls);
            o2j.putIfAbsent(openSslCipherSuite, p2j);
            CipherSuiteConverter.CachedValue cachedValue = CipherSuiteConverter.CachedValue.of(openSslCipherSuite);
            j2o.putIfAbsent(javaCipherSuiteTls, cachedValue);
            j2o.putIfAbsent(javaCipherSuiteSsl, cachedValue);
            logger.debug("Cipher suite mapping: {} => {}", javaCipherSuiteTls, openSslCipherSuite);
            logger.debug("Cipher suite mapping: {} => {}", javaCipherSuiteSsl, openSslCipherSuite);
            return p2j;
         }
      }
   }

   static String toJavaUncached(String openSslCipherSuite) {
      return toJavaUncached0(openSslCipherSuite, true);
   }

   private static String toJavaUncached0(String openSslCipherSuite, boolean checkTls13) {
      if (checkTls13) {
         Map<String, String> converted = o2jTls13.get(openSslCipherSuite);
         if (converted != null) {
            return converted.get("TLS");
         }
      }

      Matcher m = OPENSSL_CIPHERSUITE_PATTERN.matcher(openSslCipherSuite);
      if (!m.matches()) {
         return null;
      } else {
         String handshakeAlgo = m.group(1);
         boolean export;
         if (handshakeAlgo == null) {
            handshakeAlgo = "";
            export = false;
         } else if (handshakeAlgo.startsWith("EXP-")) {
            handshakeAlgo = handshakeAlgo.substring(4);
            export = true;
         } else if ("EXP".equals(handshakeAlgo)) {
            handshakeAlgo = "";
            export = true;
         } else {
            export = false;
         }

         handshakeAlgo = toJavaHandshakeAlgo(handshakeAlgo, export);
         String bulkCipher = toJavaBulkCipher(m.group(2), export);
         String hmacAlgo = toJavaHmacAlgo(m.group(3));
         String javaCipherSuite = handshakeAlgo + "_WITH_" + bulkCipher + '_' + hmacAlgo;
         return bulkCipher.contains("CHACHA20") ? javaCipherSuite + "_SHA256" : javaCipherSuite;
      }
   }

   private static String toJavaHandshakeAlgo(String handshakeAlgo, boolean export) {
      if (handshakeAlgo.isEmpty()) {
         handshakeAlgo = "RSA";
      } else if ("ADH".equals(handshakeAlgo)) {
         handshakeAlgo = "DH_anon";
      } else if ("AECDH".equals(handshakeAlgo)) {
         handshakeAlgo = "ECDH_anon";
      }

      handshakeAlgo = handshakeAlgo.replace('-', '_');
      return export ? handshakeAlgo + "_EXPORT" : handshakeAlgo;
   }

   private static String toJavaBulkCipher(String bulkCipher, boolean export) {
      if (bulkCipher.startsWith("AES")) {
         Matcher m = OPENSSL_AES_CBC_PATTERN.matcher(bulkCipher);
         if (m.matches()) {
            return m.replaceFirst("$1_$2_CBC");
         }

         m = OPENSSL_AES_PATTERN.matcher(bulkCipher);
         if (m.matches()) {
            return m.replaceFirst("$1_$2_$3");
         }
      }

      if ("DES-CBC3".equals(bulkCipher)) {
         return "3DES_EDE_CBC";
      } else if ("RC4".equals(bulkCipher)) {
         return export ? "RC4_40" : "RC4_128";
      } else if ("DES-CBC".equals(bulkCipher)) {
         return export ? "DES_CBC_40" : "DES_CBC";
      } else if ("RC2-CBC".equals(bulkCipher)) {
         return export ? "RC2_CBC_40" : "RC2_CBC";
      } else {
         return bulkCipher.replace('-', '_');
      }
   }

   private static String toJavaHmacAlgo(String hmacAlgo) {
      return hmacAlgo;
   }

   static void convertToCipherStrings(Iterable<String> cipherSuites, StringBuilder cipherBuilder, StringBuilder cipherTLSv13Builder, boolean boringSSL) {
      for (String c : cipherSuites) {
         if (c == null) {
            break;
         }

         String converted = toOpenSsl(c, boringSSL);
         if (converted == null) {
            converted = c;
         }

         if (!OpenSsl.isCipherSuiteAvailable(converted)) {
            throw new IllegalArgumentException("unsupported cipher suite: " + c + '(' + converted + ')');
         }

         if (!SslUtils.isTLSv13Cipher(converted) && !SslUtils.isTLSv13Cipher(c)) {
            cipherBuilder.append(converted);
            cipherBuilder.append(':');
         } else {
            cipherTLSv13Builder.append(converted);
            cipherTLSv13Builder.append(':');
         }
      }

      if (cipherBuilder.length() == 0 && cipherTLSv13Builder.length() == 0) {
         throw new IllegalArgumentException("empty cipher suites");
      } else {
         if (cipherBuilder.length() > 0) {
            cipherBuilder.setLength(cipherBuilder.length() - 1);
         }

         if (cipherTLSv13Builder.length() > 0) {
            cipherTLSv13Builder.setLength(cipherTLSv13Builder.length() - 1);
         }
      }
   }

   private CipherSuiteConverter() {
   }

   static {
      Map<String, String> j2oTls13Map = new HashMap<>();
      j2oTls13Map.put("TLS_AES_128_GCM_SHA256", "AEAD-AES128-GCM-SHA256");
      j2oTls13Map.put("TLS_AES_256_GCM_SHA384", "AEAD-AES256-GCM-SHA384");
      j2oTls13Map.put("TLS_CHACHA20_POLY1305_SHA256", "AEAD-CHACHA20-POLY1305-SHA256");
      j2oTls13 = Collections.unmodifiableMap(j2oTls13Map);
      Map<String, Map<String, String>> o2jTls13Map = new HashMap<>();
      o2jTls13Map.put("TLS_AES_128_GCM_SHA256", Collections.singletonMap("TLS", "TLS_AES_128_GCM_SHA256"));
      o2jTls13Map.put("TLS_AES_256_GCM_SHA384", Collections.singletonMap("TLS", "TLS_AES_256_GCM_SHA384"));
      o2jTls13Map.put("TLS_CHACHA20_POLY1305_SHA256", Collections.singletonMap("TLS", "TLS_CHACHA20_POLY1305_SHA256"));
      o2jTls13Map.put("AEAD-AES128-GCM-SHA256", Collections.singletonMap("TLS", "TLS_AES_128_GCM_SHA256"));
      o2jTls13Map.put("AEAD-AES256-GCM-SHA384", Collections.singletonMap("TLS", "TLS_AES_256_GCM_SHA384"));
      o2jTls13Map.put("AEAD-CHACHA20-POLY1305-SHA256", Collections.singletonMap("TLS", "TLS_CHACHA20_POLY1305_SHA256"));
      o2jTls13 = Collections.unmodifiableMap(o2jTls13Map);
   }

   private static final class CachedValue {
      private static final CipherSuiteConverter.CachedValue NULL = new CipherSuiteConverter.CachedValue(null);
      final String value;

      static CipherSuiteConverter.CachedValue of(String value) {
         return value != null ? new CipherSuiteConverter.CachedValue(value) : NULL;
      }

      private CachedValue(String value) {
         this.value = value;
      }
   }
}
