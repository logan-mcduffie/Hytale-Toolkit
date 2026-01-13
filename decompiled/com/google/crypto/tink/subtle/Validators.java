package com.google.crypto.tink.subtle;

import com.google.crypto.tink.config.internal.TinkFipsUtil;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Locale;
import java.util.regex.Pattern;

public final class Validators {
   private static final String TYPE_URL_PREFIX = "type.googleapis.com/";
   private static final int MIN_RSA_MODULUS_SIZE = 2048;
   private static final String URI_UNRESERVED_CHARS = "([0-9a-zA-Z\\-\\.\\_~])+";
   private static final Pattern GCP_KMS_CRYPTO_KEY_PATTERN = Pattern.compile(
      String.format(
         "^projects/%s/locations/%s/keyRings/%s/cryptoKeys/%s$",
         "([0-9a-zA-Z\\-\\.\\_~])+",
         "([0-9a-zA-Z\\-\\.\\_~])+",
         "([0-9a-zA-Z\\-\\.\\_~])+",
         "([0-9a-zA-Z\\-\\.\\_~])+"
      ),
      2
   );
   private static final Pattern GCP_KMS_CRYPTO_KEY_VERSION_PATTERN = Pattern.compile(
      String.format(
         "^projects/%s/locations/%s/keyRings/%s/cryptoKeys/%s/cryptoKeyVersions/%s$",
         "([0-9a-zA-Z\\-\\.\\_~])+",
         "([0-9a-zA-Z\\-\\.\\_~])+",
         "([0-9a-zA-Z\\-\\.\\_~])+",
         "([0-9a-zA-Z\\-\\.\\_~])+",
         "([0-9a-zA-Z\\-\\.\\_~])+"
      ),
      2
   );

   private Validators() {
   }

   public static void validateTypeUrl(String typeUrl) throws GeneralSecurityException {
      if (!typeUrl.startsWith("type.googleapis.com/")) {
         throw new GeneralSecurityException(String.format("Error: type URL %s is invalid; it must start with %s.\n", typeUrl, "type.googleapis.com/"));
      } else if (typeUrl.length() == "type.googleapis.com/".length()) {
         throw new GeneralSecurityException(String.format("Error: type URL %s is invalid; it has no message name.\n", typeUrl));
      }
   }

   public static void validateAesKeySize(int sizeInBytes) throws InvalidAlgorithmParameterException {
      if (sizeInBytes != 16 && sizeInBytes != 32) {
         throw new InvalidAlgorithmParameterException(String.format("invalid key size %d; only 128-bit and 256-bit AES keys are supported", sizeInBytes * 8));
      }
   }

   public static void validateVersion(int candidate, int maxExpected) throws GeneralSecurityException {
      if (candidate < 0 || candidate > maxExpected) {
         throw new GeneralSecurityException(String.format("key has version %d; only keys with version in range [0..%d] are supported", candidate, maxExpected));
      }
   }

   public static void validateSignatureHash(Enums.HashType hash) throws GeneralSecurityException {
      switch (hash) {
         case SHA256:
         case SHA384:
         case SHA512:
            return;
         default:
            throw new GeneralSecurityException("Unsupported hash: " + hash.name());
      }
   }

   public static void validateRsaModulusSize(int modulusSize) throws GeneralSecurityException {
      if (modulusSize < 2048) {
         throw new GeneralSecurityException(String.format("Modulus size is %d; only modulus size >= 2048-bit is supported", modulusSize));
      } else if (TinkFipsUtil.useOnlyFips() && modulusSize != 2048 && modulusSize != 3072) {
         throw new GeneralSecurityException(String.format("Modulus size is %d; only modulus size of 2048- or 3072-bit is supported in FIPS mode.", modulusSize));
      }
   }

   public static void validateRsaPublicExponent(BigInteger publicExponent) throws GeneralSecurityException {
      if (!publicExponent.testBit(0)) {
         throw new GeneralSecurityException("Public exponent must be odd.");
      } else if (publicExponent.compareTo(BigInteger.valueOf(65536L)) <= 0) {
         throw new GeneralSecurityException("Public exponent must be greater than 65536.");
      }
   }

   public static void validateNotExists(File f) throws IOException {
      if (f.exists()) {
         throw new IOException(String.format("%s exists, please choose another file\n", f));
      }
   }

   public static void validateExists(File f) throws IOException {
      if (!f.exists()) {
         throw new IOException(String.format("Error: %s doesn't exist, please choose another file\n", f));
      }
   }

   public static String validateKmsKeyUriAndRemovePrefix(String expectedPrefix, String kmsKeyUri) {
      if (!kmsKeyUri.toLowerCase(Locale.US).startsWith(expectedPrefix)) {
         throw new IllegalArgumentException(String.format("key URI must start with %s", expectedPrefix));
      } else {
         return kmsKeyUri.substring(expectedPrefix.length());
      }
   }

   public static void validateCryptoKeyUri(String kmsKeyUri) throws GeneralSecurityException {
      if (!GCP_KMS_CRYPTO_KEY_PATTERN.matcher(kmsKeyUri).matches()) {
         if (GCP_KMS_CRYPTO_KEY_VERSION_PATTERN.matcher(kmsKeyUri).matches()) {
            throw new GeneralSecurityException("Invalid Google Cloud KMS Key URI. The URI must point to a CryptoKey, not a CryptoKeyVersion");
         } else {
            throw new GeneralSecurityException(
               "Invalid Google Cloud KMS Key URI. The URI must point to a CryptoKey in the format projects/*/locations/*/keyRings/*/cryptoKeys/*. See https://cloud.google.com/kms/docs/reference/rest/v1/projects.locations.keyRings.cryptoKeys#CryptoKey"
            );
         }
      }
   }
}
