package io.netty.handler.codec.quic;

import java.util.function.BiConsumer;

interface BoringSSLPrivateKeyMethod {
   int SSL_SIGN_RSA_PKCS1_SHA1 = BoringSSLNativeStaticallyReferencedJniMethods.ssl_sign_rsa_pkcs_sha1();
   int SSL_SIGN_RSA_PKCS1_SHA256 = BoringSSLNativeStaticallyReferencedJniMethods.ssl_sign_rsa_pkcs_sha256();
   int SSL_SIGN_RSA_PKCS1_SHA384 = BoringSSLNativeStaticallyReferencedJniMethods.ssl_sign_rsa_pkcs_sha384();
   int SSL_SIGN_RSA_PKCS1_SHA512 = BoringSSLNativeStaticallyReferencedJniMethods.ssl_sign_rsa_pkcs_sha512();
   int SSL_SIGN_ECDSA_SHA1 = BoringSSLNativeStaticallyReferencedJniMethods.ssl_sign_ecdsa_pkcs_sha1();
   int SSL_SIGN_ECDSA_SECP256R1_SHA256 = BoringSSLNativeStaticallyReferencedJniMethods.ssl_sign_ecdsa_secp256r1_sha256();
   int SSL_SIGN_ECDSA_SECP384R1_SHA384 = BoringSSLNativeStaticallyReferencedJniMethods.ssl_sign_ecdsa_secp384r1_sha384();
   int SSL_SIGN_ECDSA_SECP521R1_SHA512 = BoringSSLNativeStaticallyReferencedJniMethods.ssl_sign_ecdsa_secp521r1_sha512();
   int SSL_SIGN_RSA_PSS_RSAE_SHA256 = BoringSSLNativeStaticallyReferencedJniMethods.ssl_sign_rsa_pss_rsae_sha256();
   int SSL_SIGN_RSA_PSS_RSAE_SHA384 = BoringSSLNativeStaticallyReferencedJniMethods.ssl_sign_rsa_pss_rsae_sha384();
   int SSL_SIGN_RSA_PSS_RSAE_SHA512 = BoringSSLNativeStaticallyReferencedJniMethods.ssl_sign_rsa_pss_rsae_sha512();
   int SSL_SIGN_ED25519 = BoringSSLNativeStaticallyReferencedJniMethods.ssl_sign_ed25519();
   int SSL_SIGN_RSA_PKCS1_MD5_SHA1 = BoringSSLNativeStaticallyReferencedJniMethods.ssl_sign_rsa_pkcs1_md5_sha1();

   void sign(long var1, int var3, byte[] var4, BiConsumer<byte[], Throwable> var5);

   void decrypt(long var1, byte[] var3, BiConsumer<byte[], Throwable> var4);
}
