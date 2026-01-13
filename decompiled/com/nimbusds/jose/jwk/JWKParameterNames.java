package com.nimbusds.jose.jwk;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class JWKParameterNames {
   public static final String KEY_TYPE = "kty";
   public static final String PUBLIC_KEY_USE = "use";
   public static final String KEY_OPS = "key_ops";
   public static final String ALGORITHM = "alg";
   public static final String KEY_ID = "kid";
   public static final String X_509_CERT_URL = "x5u";
   public static final String X_509_CERT_CHAIN = "x5c";
   public static final String X_509_CERT_SHA_1_THUMBPRINT = "x5t";
   public static final String X_509_CERT_SHA_256_THUMBPRINT = "x5t#S256";
   public static final String EXPIRATION_TIME = "exp";
   public static final String NOT_BEFORE = "nbf";
   public static final String ISSUED_AT = "iat";
   public static final String REVOKED = "revoked";
   public static final String ELLIPTIC_CURVE = "crv";
   public static final String ELLIPTIC_CURVE_X_COORDINATE = "x";
   public static final String ELLIPTIC_CURVE_Y_COORDINATE = "y";
   public static final String ELLIPTIC_CURVE_PRIVATE_KEY = "d";
   public static final String RSA_MODULUS = "n";
   public static final String RSA_EXPONENT = "e";
   public static final String RSA_PRIVATE_EXPONENT = "d";
   public static final String RSA_FIRST_PRIME_FACTOR = "p";
   public static final String RSA_SECOND_PRIME_FACTOR = "q";
   public static final String RSA_FIRST_FACTOR_CRT_EXPONENT = "dp";
   public static final String RSA_SECOND_FACTOR_CRT_EXPONENT = "dq";
   public static final String RSA_FIRST_CRT_COEFFICIENT = "qi";
   public static final String RSA_OTHER_PRIMES = "oth";
   public static final String RSA_OTHER_PRIMES__PRIME_FACTOR = "r";
   public static final String RSA_OTHER_PRIMES__FACTOR_CRT_EXPONENT = "d";
   public static final String RSA_OTHER_PRIMES__FACTOR_CRT_COEFFICIENT = "t";
   public static final String OCT_KEY_VALUE = "k";
   public static final String OKP_SUBTYPE = "crv";
   public static final String OKP_PUBLIC_KEY = "x";
   public static final String OKP_PRIVATE_KEY = "d";
   public static final Set<String> PUBLIC = new HashSet<>(
      Arrays.asList(
         "kty", "use", "key_ops", "alg", "kid", "x5u", "x5c", "x5t", "x5t#S256", "exp", "nbf", "iat", "revoked", "crv", "x", "y", "n", "e", "crv", "x"
      )
   );

   private JWKParameterNames() {
   }
}
