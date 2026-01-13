package io.netty.handler.codec.quic;

final class BoringSSLNativeStaticallyReferencedJniMethods {
   static native int ssl_verify_none();

   static native int ssl_verify_peer();

   static native int ssl_verify_fail_if_no_peer_cert();

   static native int x509_v_ok();

   static native int x509_v_err_cert_has_expired();

   static native int x509_v_err_cert_not_yet_valid();

   static native int x509_v_err_cert_revoked();

   static native int x509_v_err_unspecified();

   static native int ssl_sign_rsa_pkcs_sha1();

   static native int ssl_sign_rsa_pkcs_sha256();

   static native int ssl_sign_rsa_pkcs_sha384();

   static native int ssl_sign_rsa_pkcs_sha512();

   static native int ssl_sign_ecdsa_pkcs_sha1();

   static native int ssl_sign_ecdsa_secp256r1_sha256();

   static native int ssl_sign_ecdsa_secp384r1_sha384();

   static native int ssl_sign_ecdsa_secp521r1_sha512();

   static native int ssl_sign_rsa_pss_rsae_sha256();

   static native int ssl_sign_rsa_pss_rsae_sha384();

   static native int ssl_sign_rsa_pss_rsae_sha512();

   static native int ssl_sign_ed25519();

   static native int ssl_sign_rsa_pkcs1_md5_sha1();

   private BoringSSLNativeStaticallyReferencedJniMethods() {
   }
}
