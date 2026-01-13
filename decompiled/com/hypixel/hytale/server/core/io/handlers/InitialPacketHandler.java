package com.hypixel.hytale.server.core.io.handlers;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.netty.ProtocolUtil;
import com.hypixel.hytale.protocol.packets.auth.ConnectAccept;
import com.hypixel.hytale.protocol.packets.connection.ClientType;
import com.hypixel.hytale.protocol.packets.connection.Connect;
import com.hypixel.hytale.protocol.packets.connection.Disconnect;
import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Options;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.ProtocolVersion;
import com.hypixel.hytale.server.core.io.handlers.login.AuthenticationPacketHandler;
import com.hypixel.hytale.server.core.io.handlers.login.PasswordPacketHandler;
import com.hypixel.hytale.server.core.io.netty.NettyUtil;
import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import io.netty.channel.Channel;
import io.netty.handler.codec.quic.QuicStreamChannel;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InitialPacketHandler extends PacketHandler {
   private static final int MAX_REFERRAL_DATA_SIZE = 4096;
   @Nullable
   public static AuthenticationPacketHandler.AuthHandlerSupplier EDITOR_PACKET_HANDLER_SUPPLIER;
   private boolean receivedConnect;

   public InitialPacketHandler(@Nonnull Channel channel) {
      super(channel, null);
   }

   @Nonnull
   @Override
   public String getIdentifier() {
      return "{Initial(" + NettyUtil.formatRemoteAddress(this.channel) + ")}";
   }

   @Override
   public void registered0(PacketHandler oldHandler) {
      Duration initialTimeout = HytaleServer.get().getConfig().getConnectionTimeouts().getInitialTimeout();
      this.setTimeout("initial", () -> !this.registered, initialTimeout.toMillis(), TimeUnit.MILLISECONDS);
      PacketHandler.logConnectionTimings(this.channel, "Registered", Level.FINE);
   }

   @Override
   public void accept(@Nonnull Packet packet) {
      if (packet.getId() == 0) {
         this.handle((Connect)packet);
      } else if (packet.getId() == 1) {
         this.handle((Disconnect)packet);
      } else {
         this.disconnect("Protocol error: unexpected packet " + packet.getId());
      }
   }

   @Override
   public void disconnect(@Nonnull String message) {
      if (this.receivedConnect) {
         super.disconnect(message);
      } else {
         HytaleLogger.getLogger().at(Level.INFO).log("Silently disconnecting %s because no Connect packet!", NettyUtil.formatRemoteAddress(this.channel));
         ProtocolUtil.closeConnection(this.channel);
      }
   }

   public void handle(@Nonnull Connect packet) {
      this.receivedConnect = true;
      this.clearTimeout();
      PacketHandler.logConnectionTimings(this.channel, "Connect", Level.FINE);
      String clientProtocolHash = packet.protocolHash;
      if (clientProtocolHash.length() > 64) {
         this.disconnect("Invalid Protocol Hash! " + clientProtocolHash.length());
      } else {
         String expectedHash = "6708f121966c1c443f4b0eb525b2f81d0a8dc61f5003a692a8fa157e5e02cea9";
         if (!clientProtocolHash.equals(expectedHash)) {
            this.disconnect("Incompatible protocol!\nServer: " + expectedHash + "\nClient: " + clientProtocolHash);
         } else if (HytaleServer.get().isShuttingDown()) {
            this.disconnect("Server is shutting down!");
         } else if (!HytaleServer.get().isBooted()) {
            this.disconnect("Server is booting up! Please try again in a moment. [" + PluginManager.get().getState() + "]");
         } else {
            ProtocolVersion protocolVersion = new ProtocolVersion(clientProtocolHash);
            String language = packet.language;
            if (language == null) {
               language = "en-US";
            }

            boolean isTcpConnection = !(this.channel instanceof QuicStreamChannel);
            if (isTcpConnection) {
               HytaleLogger.getLogger()
                  .at(Level.INFO)
                  .log("TCP connection from %s - only insecure auth supported", NettyUtil.formatRemoteAddress(this.channel));
            }

            if (packet.uuid == null) {
               this.disconnect("Missing UUID");
            } else if (packet.username != null && !packet.username.isEmpty()) {
               if (packet.referralData != null && packet.referralData.length > 4096) {
                  HytaleLogger.getLogger()
                     .at(Level.WARNING)
                     .log("Rejecting connection from %s - referral data too large: %d bytes (max: %d)", packet.username, packet.referralData.length, 4096);
                  this.disconnect("Referral data exceeds maximum size of 4096 bytes");
               } else {
                  boolean hasIdentityToken = packet.identityToken != null && !packet.identityToken.isEmpty();
                  boolean isEditorClient = packet.clientType == ClientType.Editor;
                  Options.AuthMode authMode = Options.getOptionSet().valueOf(Options.AUTH_MODE);
                  if (hasIdentityToken && authMode == Options.AuthMode.AUTHENTICATED) {
                     if (isTcpConnection) {
                        HytaleLogger.getLogger()
                           .at(Level.WARNING)
                           .log("Rejecting authenticated connection from %s - TCP only supports insecure auth", NettyUtil.formatRemoteAddress(this.channel));
                        this.disconnect("TCP connections only support insecure authentication. Use QUIC for authenticated connections.");
                        return;
                     }

                     AuthenticationPacketHandler.AuthHandlerSupplier supplier = isEditorClient ? EDITOR_PACKET_HANDLER_SUPPLIER : SetupPacketHandler::new;
                     if (isEditorClient && supplier == null) {
                        this.disconnect("Editor isn't supported on this server!");
                        return;
                     }

                     HytaleLogger.getLogger()
                        .at(Level.INFO)
                        .log("Starting authenticated flow for %s (%s) from %s", packet.username, packet.uuid, NettyUtil.formatRemoteAddress(this.channel));
                     NettyUtil.setChannelHandler(
                        this.channel,
                        new AuthenticationPacketHandler(
                           this.channel,
                           protocolVersion,
                           language,
                           supplier,
                           packet.clientType,
                           packet.identityToken,
                           packet.uuid,
                           packet.username,
                           packet.referralData,
                           packet.referralSource
                        )
                     );
                  } else {
                     if (authMode == Options.AuthMode.AUTHENTICATED) {
                        HytaleLogger.getLogger()
                           .at(Level.WARNING)
                           .log(
                              "Rejecting development connection from %s - server requires authentication (auth-mode=%s)",
                              NettyUtil.formatRemoteAddress(this.channel),
                              authMode
                           );
                        this.disconnect("This server requires authentication!");
                        return;
                     }

                     if (authMode == Options.AuthMode.OFFLINE) {
                        if (!Constants.SINGLEPLAYER) {
                           HytaleLogger.getLogger()
                              .at(Level.WARNING)
                              .log("Rejecting connection from %s - offline mode is only valid in singleplayer", NettyUtil.formatRemoteAddress(this.channel));
                           this.disconnect("Offline mode is only available in singleplayer.");
                           return;
                        }

                        if (!SingleplayerModule.isOwner(null, packet.uuid)) {
                           HytaleLogger.getLogger()
                              .at(Level.WARNING)
                              .log(
                                 "Rejecting connection from %s (%s) - offline mode only allows the world owner (%s)",
                                 packet.username,
                                 packet.uuid,
                                 SingleplayerModule.getUuid()
                              );
                           this.disconnect("This world is in offline mode and only the owner can connect.");
                           return;
                        }
                     }

                     HytaleLogger.getLogger()
                        .at(Level.INFO)
                        .log("Starting development flow for %s (%s) from %s", packet.username, packet.uuid, NettyUtil.formatRemoteAddress(this.channel));
                     byte[] passwordChallenge = this.generatePasswordChallengeIfNeeded(packet.uuid);
                     this.write(new ConnectAccept(passwordChallenge));
                     PasswordPacketHandler.SetupHandlerSupplier setupSupplier = isEditorClient && EDITOR_PACKET_HANDLER_SUPPLIER != null
                        ? (ch, pv, lang, auth) -> EDITOR_PACKET_HANDLER_SUPPLIER.create(ch, pv, lang, auth)
                        : SetupPacketHandler::new;
                     NettyUtil.setChannelHandler(
                        this.channel,
                        new PasswordPacketHandler(
                           this.channel,
                           protocolVersion,
                           language,
                           packet.uuid,
                           packet.username,
                           packet.referralData,
                           packet.referralSource,
                           passwordChallenge,
                           setupSupplier
                        )
                     );
                  }
               }
            } else {
               this.disconnect("Missing username");
            }
         }
      }
   }

   private byte[] generatePasswordChallengeIfNeeded(UUID playerUuid) {
      String password = HytaleServer.get().getConfig().getPassword();
      if (password != null && !password.isEmpty()) {
         if (Constants.SINGLEPLAYER) {
            UUID ownerUuid = SingleplayerModule.getUuid();
            if (ownerUuid != null && ownerUuid.equals(playerUuid)) {
               return null;
            }
         }

         byte[] challenge = new byte[32];
         new SecureRandom().nextBytes(challenge);
         return challenge;
      } else {
         return null;
      }
   }

   public void handle(@Nonnull Disconnect packet) {
      this.disconnectReason.setClientDisconnectType(packet.type);
      HytaleLogger.getLogger().at(Level.WARNING).log("Disconnecting %s - Sent disconnect packet???", NettyUtil.formatRemoteAddress(this.channel));
      ProtocolUtil.closeApplicationConnection(this.channel);
   }
}
