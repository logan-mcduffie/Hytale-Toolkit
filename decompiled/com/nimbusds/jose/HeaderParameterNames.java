package com.nimbusds.jose;

public final class HeaderParameterNames {
   public static final String ALGORITHM = "alg";
   public static final String ENCRYPTION_ALGORITHM = "enc";
   public static final String COMPRESSION_ALGORITHM = "zip";
   public static final String JWK_SET_URL = "jku";
   public static final String JWK = "jwk";
   public static final String KEY_ID = "kid";
   public static final String X_509_CERT_URL = "x5u";
   public static final String X_509_CERT_CHAIN = "x5c";
   public static final String X_509_CERT_SHA_1_THUMBPRINT = "x5t";
   public static final String X_509_CERT_SHA_256_THUMBPRINT = "x5t#S256";
   public static final String TYPE = "typ";
   public static final String CONTENT_TYPE = "cty";
   public static final String CRITICAL = "crit";
   public static final String EPHEMERAL_PUBLIC_KEY = "epk";
   public static final String AGREEMENT_PARTY_U_INFO = "apu";
   public static final String AGREEMENT_PARTY_V_INFO = "apv";
   public static final String INITIALIZATION_VECTOR = "iv";
   public static final String AUTHENTICATION_TAG = "tag";
   public static final String PBES2_SALT_INPUT = "p2s";
   public static final String PBES2_COUNT = "p2c";
   public static final String SENDER_KEY_ID = "skid";
   public static final String BASE64_URL_ENCODE_PAYLOAD = "b64";
   public static final String ISSUER = "iss";
   public static final String SUBJECT = "sub";
   public static final String AUDIENCE = "aud";

   private HeaderParameterNames() {
   }
}
