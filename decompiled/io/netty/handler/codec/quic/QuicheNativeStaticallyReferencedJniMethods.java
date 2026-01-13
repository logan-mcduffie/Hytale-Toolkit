package io.netty.handler.codec.quic;

final class QuicheNativeStaticallyReferencedJniMethods {
   static native int quiche_protocol_version();

   static native int quiche_max_conn_id_len();

   static native int quiche_shutdown_read();

   static native int quiche_shutdown_write();

   static native int quiche_err_done();

   static native int quiche_err_buffer_too_short();

   static native int quiche_err_unknown_version();

   static native int quiche_err_invalid_frame();

   static native int quiche_err_invalid_packet();

   static native int quiche_err_invalid_state();

   static native int quiche_err_invalid_stream_state();

   static native int quiche_err_invalid_transport_param();

   static native int quiche_err_crypto_fail();

   static native int quiche_err_tls_fail();

   static native int quiche_err_flow_control();

   static native int quiche_err_stream_limit();

   static native int quiche_err_final_size();

   static native int quiche_err_stream_stopped();

   static native int quiche_err_stream_reset();

   static native int quiche_err_congestion_control();

   static native int quiche_err_id_limit();

   static native int quiche_err_out_of_identifiers();

   static native int quiche_err_key_update();

   static native int quiche_err_crypto_buffer_exceeded();

   static native int quiche_cc_reno();

   static native int quiche_cc_cubic();

   static native int quiche_cc_bbr();

   static native int quicheRecvInfoOffsetofFrom();

   static native int quicheRecvInfoOffsetofFromLen();

   static native int quicheRecvInfoOffsetofTo();

   static native int quicheRecvInfoOffsetofToLen();

   static native int sizeofQuicheRecvInfo();

   static native int quicheSendInfoOffsetofTo();

   static native int quicheSendInfoOffsetofToLen();

   static native int quicheSendInfoOffsetofFrom();

   static native int quicheSendInfoOffsetofFromLen();

   static native int quicheSendInfoOffsetofAt();

   static native int sizeofQuicheSendInfo();

   static native int afInet();

   static native int afInet6();

   static native int sizeofSockaddrIn();

   static native int sizeofSockaddrIn6();

   static native int sockaddrInOffsetofSinFamily();

   static native int sockaddrInOffsetofSinPort();

   static native int sockaddrInOffsetofSinAddr();

   static native int inAddressOffsetofSAddr();

   static native int sockaddrIn6OffsetofSin6Family();

   static native int sockaddrIn6OffsetofSin6Port();

   static native int sockaddrIn6OffsetofSin6Flowinfo();

   static native int sockaddrIn6OffsetofSin6Addr();

   static native int sockaddrIn6OffsetofSin6ScopeId();

   static native int in6AddressOffsetofS6Addr();

   static native int sizeofSockaddrStorage();

   static native int sizeofSocklenT();

   static native int sizeofSizeT();

   static native int sizeofTimespec();

   static native int timespecOffsetofTvSec();

   static native int timespecOffsetofTvNsec();

   static native int sizeofTimeT();

   static native int sizeofLong();

   static native int quiche_path_event_new();

   static native int quiche_path_event_validated();

   static native int quiche_path_event_failed_validation();

   static native int quiche_path_event_closed();

   static native int quiche_path_event_reused_source_connection_id();

   static native int quiche_path_event_peer_migrated();

   private QuicheNativeStaticallyReferencedJniMethods() {
   }
}
