package io.netty.handler.codec.quic;

import org.jetbrains.annotations.Nullable;

final class QuicheConfig {
   private final boolean isDatagramSupported;
   private long config = -1L;

   QuicheConfig(
      int version,
      @Nullable Boolean grease,
      @Nullable Long maxIdleTimeout,
      @Nullable Long maxSendUdpPayloadSize,
      @Nullable Long maxRecvUdpPayloadSize,
      @Nullable Long initialMaxData,
      @Nullable Long initialMaxStreamDataBidiLocal,
      @Nullable Long initialMaxStreamDataBidiRemote,
      @Nullable Long initialMaxStreamDataUni,
      @Nullable Long initialMaxStreamsBidi,
      @Nullable Long initialMaxStreamsUni,
      @Nullable Long ackDelayExponent,
      @Nullable Long maxAckDelay,
      @Nullable Boolean disableActiveMigration,
      @Nullable Boolean enableHystart,
      @Nullable Boolean discoverPmtu,
      @Nullable QuicCongestionControlAlgorithm congestionControlAlgorithm,
      @Nullable Integer initialCongestionWindowPackets,
      @Nullable Integer recvQueueLen,
      @Nullable Integer sendQueueLen,
      @Nullable Long activeConnectionIdLimit,
      byte @Nullable [] statelessResetToken
   ) {
      long config = Quiche.quiche_config_new(version);

      try {
         if (grease != null) {
            Quiche.quiche_config_grease(config, grease);
         }

         if (maxIdleTimeout != null) {
            Quiche.quiche_config_set_max_idle_timeout(config, maxIdleTimeout);
         }

         if (maxSendUdpPayloadSize != null) {
            Quiche.quiche_config_set_max_send_udp_payload_size(config, maxSendUdpPayloadSize);
         }

         if (maxRecvUdpPayloadSize != null) {
            Quiche.quiche_config_set_max_recv_udp_payload_size(config, maxRecvUdpPayloadSize);
         }

         if (initialMaxData != null) {
            Quiche.quiche_config_set_initial_max_data(config, initialMaxData);
         }

         if (initialMaxStreamDataBidiLocal != null) {
            Quiche.quiche_config_set_initial_max_stream_data_bidi_local(config, initialMaxStreamDataBidiLocal);
         }

         if (initialMaxStreamDataBidiRemote != null) {
            Quiche.quiche_config_set_initial_max_stream_data_bidi_remote(config, initialMaxStreamDataBidiRemote);
         }

         if (initialMaxStreamDataUni != null) {
            Quiche.quiche_config_set_initial_max_stream_data_uni(config, initialMaxStreamDataUni);
         }

         if (initialMaxStreamsBidi != null) {
            Quiche.quiche_config_set_initial_max_streams_bidi(config, initialMaxStreamsBidi);
         }

         if (initialMaxStreamsUni != null) {
            Quiche.quiche_config_set_initial_max_streams_uni(config, initialMaxStreamsUni);
         }

         if (ackDelayExponent != null) {
            Quiche.quiche_config_set_ack_delay_exponent(config, ackDelayExponent);
         }

         if (maxAckDelay != null) {
            Quiche.quiche_config_set_max_ack_delay(config, maxAckDelay);
         }

         if (disableActiveMigration != null) {
            Quiche.quiche_config_set_disable_active_migration(config, disableActiveMigration);
         }

         if (enableHystart != null) {
            Quiche.quiche_config_enable_hystart(config, enableHystart);
         }

         if (discoverPmtu != null) {
            Quiche.quiche_config_discover_pmtu(config, discoverPmtu);
         }

         if (congestionControlAlgorithm != null) {
            switch (congestionControlAlgorithm) {
               case RENO:
                  Quiche.quiche_config_set_cc_algorithm(config, Quiche.QUICHE_CC_RENO);
                  break;
               case CUBIC:
                  Quiche.quiche_config_set_cc_algorithm(config, Quiche.QUICHE_CC_CUBIC);
                  break;
               case BBR:
                  Quiche.quiche_config_set_cc_algorithm(config, Quiche.QUICHE_CC_BBR);
                  break;
               default:
                  throw new IllegalArgumentException("Unknown congestionControlAlgorithm: " + congestionControlAlgorithm);
            }
         }

         if (initialCongestionWindowPackets != null) {
            Quiche.quiche_config_set_initial_congestion_window_packets(config, initialCongestionWindowPackets);
         }

         if (recvQueueLen != null && sendQueueLen != null) {
            this.isDatagramSupported = true;
            Quiche.quiche_config_enable_dgram(config, true, recvQueueLen, sendQueueLen);
         } else {
            this.isDatagramSupported = false;
         }

         if (activeConnectionIdLimit != null) {
            Quiche.quiche_config_set_active_connection_id_limit(config, activeConnectionIdLimit);
         }

         if (statelessResetToken != null) {
            Quiche.quiche_config_set_stateless_reset_token(config, statelessResetToken);
         }

         this.config = config;
      } catch (Throwable var26) {
         Quiche.quiche_config_free(config);
         throw var26;
      }
   }

   boolean isDatagramSupported() {
      return this.isDatagramSupported;
   }

   long nativeAddress() {
      return this.config;
   }

   @Override
   protected void finalize() throws Throwable {
      try {
         this.free();
      } finally {
         super.finalize();
      }
   }

   void free() {
      if (this.config != -1L) {
         try {
            Quiche.quiche_config_free(this.config);
         } finally {
            this.config = -1L;
         }
      }
   }
}
