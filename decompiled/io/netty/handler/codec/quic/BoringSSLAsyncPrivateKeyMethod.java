package io.netty.handler.codec.quic;

import io.netty.util.concurrent.Future;
import javax.net.ssl.SSLEngine;

public interface BoringSSLAsyncPrivateKeyMethod {
   int SSL_SIGN_RSA_PKCS1_SHA1 = BoringSSLPrivateKeyMethod.SSL_SIGN_RSA_PKCS1_SHA1;
   int SSL_SIGN_RSA_PKCS1_SHA256 = BoringSSLPrivateKeyMethod.SSL_SIGN_RSA_PKCS1_SHA256;
   int SSL_SIGN_RSA_PKCS1_SHA384 = BoringSSLPrivateKeyMethod.SSL_SIGN_RSA_PKCS1_SHA384;
   int SSL_SIGN_RSA_PKCS1_SHA512 = BoringSSLPrivateKeyMethod.SSL_SIGN_RSA_PKCS1_SHA512;
   int SSL_SIGN_ECDSA_SHA1 = BoringSSLPrivateKeyMethod.SSL_SIGN_ECDSA_SHA1;
   int SSL_SIGN_ECDSA_SECP256R1_SHA256 = BoringSSLPrivateKeyMethod.SSL_SIGN_ECDSA_SECP256R1_SHA256;
   int SSL_SIGN_ECDSA_SECP384R1_SHA384 = BoringSSLPrivateKeyMethod.SSL_SIGN_ECDSA_SECP384R1_SHA384;
   int SSL_SIGN_ECDSA_SECP521R1_SHA512 = BoringSSLPrivateKeyMethod.SSL_SIGN_ECDSA_SECP521R1_SHA512;
   int SSL_SIGN_RSA_PSS_RSAE_SHA256 = BoringSSLPrivateKeyMethod.SSL_SIGN_RSA_PSS_RSAE_SHA256;
   int SSL_SIGN_RSA_PSS_RSAE_SHA384 = BoringSSLPrivateKeyMethod.SSL_SIGN_RSA_PSS_RSAE_SHA384;
   int SSL_SIGN_RSA_PSS_RSAE_SHA512 = BoringSSLPrivateKeyMethod.SSL_SIGN_RSA_PSS_RSAE_SHA512;
   int SSL_SIGN_ED25519 = BoringSSLPrivateKeyMethod.SSL_SIGN_ED25519;
   int SSL_SIGN_RSA_PKCS1_MD5_SHA1 = BoringSSLPrivateKeyMethod.SSL_SIGN_RSA_PKCS1_MD5_SHA1;

   Future<byte[]> sign(SSLEngine var1, int var2, byte[] var3);

   Future<byte[]> decrypt(SSLEngine var1, byte[] var2);
}
