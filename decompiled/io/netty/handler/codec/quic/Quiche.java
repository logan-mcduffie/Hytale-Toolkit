package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.internal.ClassInitializerUtil;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import org.jetbrains.annotations.Nullable;

final class Quiche {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Quiche.class);
   private static final boolean TRACE_LOGGING_ENABLED = logger.isTraceEnabled();
   private static final IntObjectHashMap<Quiche.QuicTransportErrorHolder> ERROR_MAPPINGS = new IntObjectHashMap<>();
   static final short AF_INET;
   static final short AF_INET6;
   static final int SIZEOF_SOCKADDR_STORAGE;
   static final int SIZEOF_SOCKADDR_IN;
   static final int SIZEOF_SOCKADDR_IN6;
   static final int SOCKADDR_IN_OFFSETOF_SIN_FAMILY;
   static final int SOCKADDR_IN_OFFSETOF_SIN_PORT;
   static final int SOCKADDR_IN_OFFSETOF_SIN_ADDR;
   static final int IN_ADDRESS_OFFSETOF_S_ADDR;
   static final int SOCKADDR_IN6_OFFSETOF_SIN6_FAMILY;
   static final int SOCKADDR_IN6_OFFSETOF_SIN6_PORT;
   static final int SOCKADDR_IN6_OFFSETOF_SIN6_FLOWINFO;
   static final int SOCKADDR_IN6_OFFSETOF_SIN6_ADDR;
   static final int SOCKADDR_IN6_OFFSETOF_SIN6_SCOPE_ID;
   static final int IN6_ADDRESS_OFFSETOF_S6_ADDR;
   static final int SIZEOF_SOCKLEN_T;
   static final int SIZEOF_SIZE_T;
   static final int SIZEOF_TIMESPEC;
   static final int SIZEOF_TIME_T;
   static final int SIZEOF_LONG;
   static final int TIMESPEC_OFFSETOF_TV_SEC;
   static final int TIMESPEC_OFFSETOF_TV_NSEC;
   static final int QUICHE_RECV_INFO_OFFSETOF_FROM;
   static final int QUICHE_RECV_INFO_OFFSETOF_FROM_LEN;
   static final int QUICHE_RECV_INFO_OFFSETOF_TO;
   static final int QUICHE_RECV_INFO_OFFSETOF_TO_LEN;
   static final int SIZEOF_QUICHE_RECV_INFO;
   static final int QUICHE_SEND_INFO_OFFSETOF_TO;
   static final int QUICHE_SEND_INFO_OFFSETOF_TO_LEN;
   static final int QUICHE_SEND_INFO_OFFSETOF_FROM;
   static final int QUICHE_SEND_INFO_OFFSETOF_FROM_LEN;
   static final int QUICHE_SEND_INFO_OFFSETOF_AT;
   static final int SIZEOF_QUICHE_SEND_INFO;
   static final int QUICHE_PROTOCOL_VERSION;
   static final int QUICHE_MAX_CONN_ID_LEN;
   static final int QUICHE_SHUTDOWN_READ;
   static final int QUICHE_SHUTDOWN_WRITE;
   static final int QUICHE_ERR_DONE;
   static final int QUICHE_ERR_BUFFER_TOO_SHORT;
   static final int QUICHE_ERR_UNKNOWN_VERSION;
   static final int QUICHE_ERR_INVALID_FRAME;
   static final int QUICHE_ERR_INVALID_PACKET;
   static final int QUICHE_ERR_INVALID_STATE;
   static final int QUICHE_ERR_INVALID_STREAM_STATE;
   static final int QUICHE_ERR_INVALID_TRANSPORT_PARAM;
   static final int QUICHE_ERR_CRYPTO_FAIL;
   static final int QUICHE_ERR_TLS_FAIL;
   static final int QUICHE_ERR_FLOW_CONTROL;
   static final int QUICHE_ERR_STREAM_LIMIT;
   static final int QUICHE_ERR_FINAL_SIZE;
   static final int QUICHE_ERR_CONGESTION_CONTROL;
   static final int QUICHE_ERR_STREAM_RESET;
   static final int QUICHE_ERR_STREAM_STOPPED;
   static final int QUICHE_ERR_ID_LIMIT;
   static final int QUICHE_ERR_OUT_OF_IDENTIFIERS;
   static final int QUICHE_ERR_KEY_UPDATE;
   static final int QUICHE_ERR_CRYPTO_BUFFER_EXCEEDED;
   static final int QUICHE_CC_RENO;
   static final int QUICHE_CC_CUBIC;
   static final int QUICHE_CC_BBR;
   static final int QUICHE_PATH_EVENT_NEW;
   static final int QUICHE_PATH_EVENT_VALIDATED;
   static final int QUICHE_PATH_EVENT_FAILED_VALIDATION;
   static final int QUICHE_PATH_EVENT_CLOSED;
   static final int QUICHE_PATH_EVENT_REUSED_SOURCE_CONNECTION_ID;
   static final int QUICHE_PATH_EVENT_PEER_MIGRATED;

   private static void loadNativeLibrary() {
      String libName = "netty_quiche42";
      ClassLoader cl = PlatformDependent.getClassLoader(Quiche.class);
      if (!PlatformDependent.isAndroid()) {
         libName = libName + '_' + PlatformDependent.normalizedOs() + '_' + PlatformDependent.normalizedArch();
      }

      try {
         NativeLibraryLoader.load(libName, cl);
      } catch (UnsatisfiedLinkError var3) {
         logger.debug("Failed to load {}", libName, var3);
         throw var3;
      }
   }

   @Nullable
   static native String quiche_version();

   static native boolean quiche_version_is_supported(int var0);

   static native int quiche_negotiate_version(long var0, int var2, long var3, int var5, long var6, int var8);

   static native int quiche_retry(long var0, int var2, long var3, int var5, long var6, int var8, long var9, int var11, int var12, long var13, int var15);

   static native long quiche_conn_new_with_tls(
      long var0, int var2, long var3, int var5, long var6, int var8, long var9, int var11, long var12, long var14, boolean var16
   );

   static native boolean quiche_conn_set_qlog_path(long var0, String var2, String var3, String var4);

   static native int quiche_conn_recv(long var0, long var2, int var4, long var5);

   static native int quiche_conn_send(long var0, long var2, int var4, long var5);

   static native void quiche_conn_free(long var0);

   @Nullable
   static QuicConnectionCloseEvent quiche_conn_peer_error(long connAddr) {
      Object[] error = quiche_conn_peer_error0(connAddr);
      return error == null ? null : new QuicConnectionCloseEvent((Boolean)error[0], (Integer)error[1], (byte[])error[2]);
   }

   private static native Object @Nullable [] quiche_conn_peer_error0(long var0);

   static native long quiche_conn_peer_streams_left_bidi(long var0);

   static native long quiche_conn_peer_streams_left_uni(long var0);

   static native int quiche_conn_stream_priority(long var0, long var2, byte var4, boolean var5);

   static native int quiche_conn_send_quantum(long var0);

   static native byte @Nullable [] quiche_conn_trace_id(long var0);

   static native byte[] quiche_conn_source_id(long var0);

   static native byte[] quiche_conn_destination_id(long var0);

   static native int quiche_conn_stream_recv(long var0, long var2, long var4, int var6, long var7, long var9);

   static native int quiche_conn_stream_send(long var0, long var2, long var4, int var6, boolean var7);

   static native int quiche_conn_stream_shutdown(long var0, long var2, int var4, long var5);

   static native long quiche_conn_stream_capacity(long var0, long var2);

   static native boolean quiche_conn_stream_finished(long var0, long var2);

   static native int quiche_conn_close(long var0, boolean var2, long var3, long var5, int var7);

   static native boolean quiche_conn_is_established(long var0);

   static native boolean quiche_conn_is_in_early_data(long var0);

   static native boolean quiche_conn_is_closed(long var0);

   static native boolean quiche_conn_is_timed_out(long var0);

   static native long @Nullable [] quiche_conn_stats(long var0);

   static native long @Nullable [] quiche_conn_peer_transport_params(long var0);

   static native long quiche_conn_timeout_as_nanos(long var0);

   static native void quiche_conn_on_timeout(long var0);

   static native long quiche_conn_readable(long var0);

   static native long quiche_conn_writable(long var0);

   static native int quiche_stream_iter_next(long var0, long[] var2);

   static native void quiche_stream_iter_free(long var0);

   static native Object @Nullable [] quiche_conn_path_stats(long var0, long var2);

   static native int quiche_conn_dgram_max_writable_len(long var0);

   static native int quiche_conn_dgram_recv_front_len(long var0);

   static native int quiche_conn_dgram_recv(long var0, long var2, int var4);

   static native int quiche_conn_dgram_send(long var0, long var2, int var4);

   static native int quiche_conn_set_session(long var0, byte[] var2);

   static native int quiche_conn_max_send_udp_payload_size(long var0);

   static native int quiche_conn_scids_left(long var0);

   static native long quiche_conn_new_scid(long var0, long var2, int var4, byte[] var5, boolean var6, long var7);

   static native byte @Nullable [] quiche_conn_retired_scid_next(long var0);

   static native long quiche_conn_path_event_next(long var0);

   static native int quiche_path_event_type(long var0);

   static native void quiche_path_event_free(long var0);

   static native Object[] quiche_path_event_new(long var0);

   static native Object[] quiche_path_event_validated(long var0);

   static native Object[] quiche_path_event_failed_validation(long var0);

   static native Object[] quiche_path_event_closed(long var0);

   static native Object[] quiche_path_event_reused_source_connection_id(long var0);

   static native Object[] quiche_path_event_peer_migrated(long var0);

   static native long quiche_config_new(int var0);

   static native void quiche_config_grease(long var0, boolean var2);

   static native void quiche_config_set_max_idle_timeout(long var0, long var2);

   static native void quiche_config_set_max_recv_udp_payload_size(long var0, long var2);

   static native void quiche_config_set_max_send_udp_payload_size(long var0, long var2);

   static native void quiche_config_set_initial_max_data(long var0, long var2);

   static native void quiche_config_set_initial_max_stream_data_bidi_local(long var0, long var2);

   static native void quiche_config_set_initial_max_stream_data_bidi_remote(long var0, long var2);

   static native void quiche_config_set_initial_max_stream_data_uni(long var0, long var2);

   static native void quiche_config_set_initial_max_streams_bidi(long var0, long var2);

   static native void quiche_config_set_initial_max_streams_uni(long var0, long var2);

   static native void quiche_config_set_ack_delay_exponent(long var0, long var2);

   static native void quiche_config_set_max_ack_delay(long var0, long var2);

   static native void quiche_config_set_disable_active_migration(long var0, boolean var2);

   static native void quiche_config_set_cc_algorithm(long var0, int var2);

   static native void quiche_config_set_initial_congestion_window_packets(long var0, int var2);

   static native void quiche_config_enable_hystart(long var0, boolean var2);

   static native void quiche_config_discover_pmtu(long var0, boolean var2);

   static native void quiche_config_enable_dgram(long var0, boolean var2, int var3, int var4);

   static native void quiche_config_set_active_connection_id_limit(long var0, long var2);

   static native void quiche_config_set_stateless_reset_token(long var0, byte[] var2);

   static native void quiche_config_free(long var0);

   private static native void quiche_enable_debug_logging(QuicheLogger var0);

   private static native long buffer_memory_address(ByteBuffer var0);

   static native int sockaddr_cmp(long var0, long var2);

   static long readerMemoryAddress(ByteBuf buf) {
      return memoryAddress(buf, buf.readerIndex(), buf.readableBytes());
   }

   static long writerMemoryAddress(ByteBuf buf) {
      return memoryAddress(buf, buf.writerIndex(), buf.writableBytes());
   }

   static long memoryAddress(ByteBuf buf, int offset, int len) {
      assert buf.isDirect();

      return buf.hasMemoryAddress() ? buf.memoryAddress() + offset : memoryAddressWithPosition(buf.internalNioBuffer(offset, len));
   }

   static long memoryAddressWithPosition(ByteBuffer buf) {
      assert buf.isDirect();

      return buffer_memory_address(buf) + buf.position();
   }

   static ByteBuf allocateNativeOrder(int capacity) {
      ByteBuf buffer = Unpooled.directBuffer(capacity);
      return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? buffer : buffer.order(ByteOrder.LITTLE_ENDIAN);
   }

   static boolean shouldClose(int res) {
      return res == QUICHE_ERR_CRYPTO_FAIL || res == QUICHE_ERR_TLS_FAIL;
   }

   static boolean isSameAddress(ByteBuffer memory, ByteBuffer memory2, int addressOffset) {
      long address1 = memoryAddressWithPosition(memory) + addressOffset;
      long address2 = memoryAddressWithPosition(memory2) + addressOffset;
      return SockaddrIn.cmp(address1, address2) == 0;
   }

   static void setPrimitiveValue(ByteBuffer memory, int offset, int valueType, long value) {
      switch (valueType) {
         case 1:
            memory.put(offset, (byte)value);
            break;
         case 2:
            memory.putShort(offset, (short)value);
            break;
         case 3:
         case 5:
         case 6:
         case 7:
         default:
            throw new IllegalStateException();
         case 4:
            memory.putInt(offset, (int)value);
            break;
         case 8:
            memory.putLong(offset, value);
      }
   }

   static long getPrimitiveValue(ByteBuffer memory, int offset, int valueType) {
      switch (valueType) {
         case 1:
            return memory.get(offset);
         case 2:
            return memory.getShort(offset);
         case 3:
         case 5:
         case 6:
         case 7:
         default:
            throw new IllegalStateException();
         case 4:
            return memory.getInt(offset);
         case 8:
            return memory.getLong(offset);
      }
   }

   static Exception convertToException(int result) {
      return convertToException(result, -1L);
   }

   static Exception convertToException(int result, long code) {
      Quiche.QuicTransportErrorHolder holder = ERROR_MAPPINGS.get(result);
      if (holder == null) {
         QuicheError error = QuicheError.valueOf(result);
         return (Exception)(error == QuicheError.STREAM_RESET ? new QuicStreamResetException(error.message(), code) : new QuicException(error.message()));
      } else {
         Exception exception = new QuicException(holder.error + ": " + holder.quicheErrorName, holder.error);
         if (result == QUICHE_ERR_TLS_FAIL) {
            String lastSslError = BoringSSL.ERR_last_error();
            SSLHandshakeException sslExc = new SSLHandshakeException(lastSslError);
            sslExc.initCause(exception);
            return sslExc;
         } else {
            return (Exception)(result == QUICHE_ERR_CRYPTO_FAIL ? new SSLException(exception) : exception);
         }
      }
   }

   private Quiche() {
   }

   static {
      ClassInitializerUtil.tryLoadClasses(
         Quiche.class,
         byte[].class,
         String.class,
         BoringSSLCertificateCallback.class,
         BoringSSLCertificateVerifyCallback.class,
         BoringSSLHandshakeCompleteCallback.class,
         QuicheLogger.class
      );

      try {
         quiche_version();
      } catch (UnsatisfiedLinkError var1) {
         loadNativeLibrary();
      }

      if (TRACE_LOGGING_ENABLED) {
         quiche_enable_debug_logging(new QuicheLogger(logger));
      }

      AF_INET = (short)QuicheNativeStaticallyReferencedJniMethods.afInet();
      AF_INET6 = (short)QuicheNativeStaticallyReferencedJniMethods.afInet6();
      SIZEOF_SOCKADDR_STORAGE = QuicheNativeStaticallyReferencedJniMethods.sizeofSockaddrStorage();
      SIZEOF_SOCKADDR_IN = QuicheNativeStaticallyReferencedJniMethods.sizeofSockaddrIn();
      SIZEOF_SOCKADDR_IN6 = QuicheNativeStaticallyReferencedJniMethods.sizeofSockaddrIn6();
      SOCKADDR_IN_OFFSETOF_SIN_FAMILY = QuicheNativeStaticallyReferencedJniMethods.sockaddrInOffsetofSinFamily();
      SOCKADDR_IN_OFFSETOF_SIN_PORT = QuicheNativeStaticallyReferencedJniMethods.sockaddrInOffsetofSinPort();
      SOCKADDR_IN_OFFSETOF_SIN_ADDR = QuicheNativeStaticallyReferencedJniMethods.sockaddrInOffsetofSinAddr();
      IN_ADDRESS_OFFSETOF_S_ADDR = QuicheNativeStaticallyReferencedJniMethods.inAddressOffsetofSAddr();
      SOCKADDR_IN6_OFFSETOF_SIN6_FAMILY = QuicheNativeStaticallyReferencedJniMethods.sockaddrIn6OffsetofSin6Family();
      SOCKADDR_IN6_OFFSETOF_SIN6_PORT = QuicheNativeStaticallyReferencedJniMethods.sockaddrIn6OffsetofSin6Port();
      SOCKADDR_IN6_OFFSETOF_SIN6_FLOWINFO = QuicheNativeStaticallyReferencedJniMethods.sockaddrIn6OffsetofSin6Flowinfo();
      SOCKADDR_IN6_OFFSETOF_SIN6_ADDR = QuicheNativeStaticallyReferencedJniMethods.sockaddrIn6OffsetofSin6Addr();
      SOCKADDR_IN6_OFFSETOF_SIN6_SCOPE_ID = QuicheNativeStaticallyReferencedJniMethods.sockaddrIn6OffsetofSin6ScopeId();
      IN6_ADDRESS_OFFSETOF_S6_ADDR = QuicheNativeStaticallyReferencedJniMethods.in6AddressOffsetofS6Addr();
      SIZEOF_SOCKLEN_T = QuicheNativeStaticallyReferencedJniMethods.sizeofSocklenT();
      SIZEOF_SIZE_T = QuicheNativeStaticallyReferencedJniMethods.sizeofSizeT();
      SIZEOF_TIMESPEC = QuicheNativeStaticallyReferencedJniMethods.sizeofTimespec();
      SIZEOF_TIME_T = QuicheNativeStaticallyReferencedJniMethods.sizeofTimeT();
      SIZEOF_LONG = QuicheNativeStaticallyReferencedJniMethods.sizeofLong();
      TIMESPEC_OFFSETOF_TV_SEC = QuicheNativeStaticallyReferencedJniMethods.timespecOffsetofTvSec();
      TIMESPEC_OFFSETOF_TV_NSEC = QuicheNativeStaticallyReferencedJniMethods.timespecOffsetofTvNsec();
      QUICHE_RECV_INFO_OFFSETOF_FROM = QuicheNativeStaticallyReferencedJniMethods.quicheRecvInfoOffsetofFrom();
      QUICHE_RECV_INFO_OFFSETOF_FROM_LEN = QuicheNativeStaticallyReferencedJniMethods.quicheRecvInfoOffsetofFromLen();
      QUICHE_RECV_INFO_OFFSETOF_TO = QuicheNativeStaticallyReferencedJniMethods.quicheRecvInfoOffsetofTo();
      QUICHE_RECV_INFO_OFFSETOF_TO_LEN = QuicheNativeStaticallyReferencedJniMethods.quicheRecvInfoOffsetofToLen();
      SIZEOF_QUICHE_RECV_INFO = QuicheNativeStaticallyReferencedJniMethods.sizeofQuicheRecvInfo();
      QUICHE_SEND_INFO_OFFSETOF_TO = QuicheNativeStaticallyReferencedJniMethods.quicheSendInfoOffsetofTo();
      QUICHE_SEND_INFO_OFFSETOF_TO_LEN = QuicheNativeStaticallyReferencedJniMethods.quicheSendInfoOffsetofToLen();
      QUICHE_SEND_INFO_OFFSETOF_FROM = QuicheNativeStaticallyReferencedJniMethods.quicheSendInfoOffsetofFrom();
      QUICHE_SEND_INFO_OFFSETOF_FROM_LEN = QuicheNativeStaticallyReferencedJniMethods.quicheSendInfoOffsetofFromLen();
      QUICHE_SEND_INFO_OFFSETOF_AT = QuicheNativeStaticallyReferencedJniMethods.quicheSendInfoOffsetofAt();
      SIZEOF_QUICHE_SEND_INFO = QuicheNativeStaticallyReferencedJniMethods.sizeofQuicheSendInfo();
      QUICHE_PROTOCOL_VERSION = QuicheNativeStaticallyReferencedJniMethods.quiche_protocol_version();
      QUICHE_MAX_CONN_ID_LEN = QuicheNativeStaticallyReferencedJniMethods.quiche_max_conn_id_len();
      QUICHE_SHUTDOWN_READ = QuicheNativeStaticallyReferencedJniMethods.quiche_shutdown_read();
      QUICHE_SHUTDOWN_WRITE = QuicheNativeStaticallyReferencedJniMethods.quiche_shutdown_write();
      QUICHE_ERR_DONE = QuicheNativeStaticallyReferencedJniMethods.quiche_err_done();
      QUICHE_ERR_BUFFER_TOO_SHORT = QuicheNativeStaticallyReferencedJniMethods.quiche_err_buffer_too_short();
      QUICHE_ERR_UNKNOWN_VERSION = QuicheNativeStaticallyReferencedJniMethods.quiche_err_unknown_version();
      QUICHE_ERR_INVALID_FRAME = QuicheNativeStaticallyReferencedJniMethods.quiche_err_invalid_frame();
      QUICHE_ERR_INVALID_PACKET = QuicheNativeStaticallyReferencedJniMethods.quiche_err_invalid_packet();
      QUICHE_ERR_INVALID_STATE = QuicheNativeStaticallyReferencedJniMethods.quiche_err_invalid_state();
      QUICHE_ERR_INVALID_STREAM_STATE = QuicheNativeStaticallyReferencedJniMethods.quiche_err_invalid_stream_state();
      QUICHE_ERR_INVALID_TRANSPORT_PARAM = QuicheNativeStaticallyReferencedJniMethods.quiche_err_invalid_transport_param();
      QUICHE_ERR_CRYPTO_FAIL = QuicheNativeStaticallyReferencedJniMethods.quiche_err_crypto_fail();
      QUICHE_ERR_TLS_FAIL = QuicheNativeStaticallyReferencedJniMethods.quiche_err_tls_fail();
      QUICHE_ERR_FLOW_CONTROL = QuicheNativeStaticallyReferencedJniMethods.quiche_err_flow_control();
      QUICHE_ERR_STREAM_LIMIT = QuicheNativeStaticallyReferencedJniMethods.quiche_err_stream_limit();
      QUICHE_ERR_FINAL_SIZE = QuicheNativeStaticallyReferencedJniMethods.quiche_err_final_size();
      QUICHE_ERR_CONGESTION_CONTROL = QuicheNativeStaticallyReferencedJniMethods.quiche_err_congestion_control();
      QUICHE_ERR_STREAM_RESET = QuicheNativeStaticallyReferencedJniMethods.quiche_err_stream_reset();
      QUICHE_ERR_STREAM_STOPPED = QuicheNativeStaticallyReferencedJniMethods.quiche_err_stream_stopped();
      QUICHE_ERR_ID_LIMIT = QuicheNativeStaticallyReferencedJniMethods.quiche_err_id_limit();
      QUICHE_ERR_OUT_OF_IDENTIFIERS = QuicheNativeStaticallyReferencedJniMethods.quiche_err_out_of_identifiers();
      QUICHE_ERR_KEY_UPDATE = QuicheNativeStaticallyReferencedJniMethods.quiche_err_key_update();
      QUICHE_ERR_CRYPTO_BUFFER_EXCEEDED = QuicheNativeStaticallyReferencedJniMethods.quiche_err_crypto_buffer_exceeded();
      QUICHE_CC_RENO = QuicheNativeStaticallyReferencedJniMethods.quiche_cc_reno();
      QUICHE_CC_CUBIC = QuicheNativeStaticallyReferencedJniMethods.quiche_cc_cubic();
      QUICHE_CC_BBR = QuicheNativeStaticallyReferencedJniMethods.quiche_cc_bbr();
      QUICHE_PATH_EVENT_NEW = QuicheNativeStaticallyReferencedJniMethods.quiche_path_event_new();
      QUICHE_PATH_EVENT_VALIDATED = QuicheNativeStaticallyReferencedJniMethods.quiche_path_event_validated();
      QUICHE_PATH_EVENT_FAILED_VALIDATION = QuicheNativeStaticallyReferencedJniMethods.quiche_path_event_failed_validation();
      QUICHE_PATH_EVENT_CLOSED = QuicheNativeStaticallyReferencedJniMethods.quiche_path_event_closed();
      QUICHE_PATH_EVENT_REUSED_SOURCE_CONNECTION_ID = QuicheNativeStaticallyReferencedJniMethods.quiche_path_event_reused_source_connection_id();
      QUICHE_PATH_EVENT_PEER_MIGRATED = QuicheNativeStaticallyReferencedJniMethods.quiche_path_event_peer_migrated();
      ERROR_MAPPINGS.put(QUICHE_ERR_DONE, new Quiche.QuicTransportErrorHolder(QuicTransportError.NO_ERROR, "QUICHE_ERR_DONE"));
      ERROR_MAPPINGS.put(QUICHE_ERR_INVALID_FRAME, new Quiche.QuicTransportErrorHolder(QuicTransportError.FRAME_ENCODING_ERROR, "QUICHE_ERR_INVALID_FRAME"));
      ERROR_MAPPINGS.put(
         QUICHE_ERR_INVALID_STREAM_STATE, new Quiche.QuicTransportErrorHolder(QuicTransportError.STREAM_STATE_ERROR, "QUICHE_ERR_INVALID_STREAM_STATE")
      );
      ERROR_MAPPINGS.put(
         QUICHE_ERR_INVALID_TRANSPORT_PARAM,
         new Quiche.QuicTransportErrorHolder(QuicTransportError.TRANSPORT_PARAMETER_ERROR, "QUICHE_ERR_INVALID_TRANSPORT_PARAM")
      );
      ERROR_MAPPINGS.put(QUICHE_ERR_FLOW_CONTROL, new Quiche.QuicTransportErrorHolder(QuicTransportError.FLOW_CONTROL_ERROR, "QUICHE_ERR_FLOW_CONTROL"));
      ERROR_MAPPINGS.put(QUICHE_ERR_STREAM_LIMIT, new Quiche.QuicTransportErrorHolder(QuicTransportError.STREAM_LIMIT_ERROR, "QUICHE_ERR_STREAM_LIMIT"));
      ERROR_MAPPINGS.put(QUICHE_ERR_ID_LIMIT, new Quiche.QuicTransportErrorHolder(QuicTransportError.CONNECTION_ID_LIMIT_ERROR, "QUICHE_ERR_ID_LIMIT"));
      ERROR_MAPPINGS.put(QUICHE_ERR_FINAL_SIZE, new Quiche.QuicTransportErrorHolder(QuicTransportError.FINAL_SIZE_ERROR, "QUICHE_ERR_FINAL_SIZE"));
      ERROR_MAPPINGS.put(
         QUICHE_ERR_CRYPTO_BUFFER_EXCEEDED, new Quiche.QuicTransportErrorHolder(QuicTransportError.CRYPTO_BUFFER_EXCEEDED, "QUICHE_ERR_CRYPTO_BUFFER_EXCEEDED")
      );
      ERROR_MAPPINGS.put(QUICHE_ERR_KEY_UPDATE, new Quiche.QuicTransportErrorHolder(QuicTransportError.KEY_UPDATE_ERROR, "QUICHE_ERR_KEY_UPDATE"));
      ERROR_MAPPINGS.put(QUICHE_ERR_TLS_FAIL, new Quiche.QuicTransportErrorHolder(QuicTransportError.valueOf(256L), "QUICHE_ERR_TLS_FAIL"));
      ERROR_MAPPINGS.put(QUICHE_ERR_CRYPTO_FAIL, new Quiche.QuicTransportErrorHolder(QuicTransportError.valueOf(256L), "QUICHE_ERR_CRYPTO_FAIL"));
      ERROR_MAPPINGS.put(QUICHE_ERR_BUFFER_TOO_SHORT, new Quiche.QuicTransportErrorHolder(QuicTransportError.PROTOCOL_VIOLATION, "QUICHE_ERR_BUFFER_TOO_SHORT"));
      ERROR_MAPPINGS.put(QUICHE_ERR_UNKNOWN_VERSION, new Quiche.QuicTransportErrorHolder(QuicTransportError.PROTOCOL_VIOLATION, "QUICHE_ERR_UNKNOWN_VERSION"));
      ERROR_MAPPINGS.put(QUICHE_ERR_INVALID_PACKET, new Quiche.QuicTransportErrorHolder(QuicTransportError.PROTOCOL_VIOLATION, "QUICHE_ERR_INVALID_PACKET"));
      ERROR_MAPPINGS.put(QUICHE_ERR_INVALID_STATE, new Quiche.QuicTransportErrorHolder(QuicTransportError.PROTOCOL_VIOLATION, "QUICHE_ERR_INVALID_STATE"));
      ERROR_MAPPINGS.put(
         QUICHE_ERR_CONGESTION_CONTROL, new Quiche.QuicTransportErrorHolder(QuicTransportError.PROTOCOL_VIOLATION, "QUICHE_ERR_CONGESTION_CONTROL")
      );
      ERROR_MAPPINGS.put(QUICHE_ERR_STREAM_STOPPED, new Quiche.QuicTransportErrorHolder(QuicTransportError.PROTOCOL_VIOLATION, "QUICHE_ERR_STREAM_STOPPED"));
      ERROR_MAPPINGS.put(
         QUICHE_ERR_OUT_OF_IDENTIFIERS, new Quiche.QuicTransportErrorHolder(QuicTransportError.PROTOCOL_VIOLATION, "QUICHE_ERR_OUT_OF_IDENTIFIERS")
      );
   }

   private static final class QuicTransportErrorHolder {
      private final QuicTransportError error;
      private final String quicheErrorName;

      QuicTransportErrorHolder(QuicTransportError error, String quicheErrorName) {
         this.error = error;
         this.quicheErrorName = quicheErrorName;
      }
   }
}
