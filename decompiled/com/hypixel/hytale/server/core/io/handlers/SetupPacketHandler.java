package com.hypixel.hytale.server.core.io.handlers;

import com.hypixel.hytale.common.util.CompletableFutureUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.protocol.Asset;
import com.hypixel.hytale.protocol.HostAddress;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.netty.ProtocolUtil;
import com.hypixel.hytale.protocol.packets.auth.ClientReferral;
import com.hypixel.hytale.protocol.packets.connection.Disconnect;
import com.hypixel.hytale.protocol.packets.connection.DisconnectType;
import com.hypixel.hytale.protocol.packets.interface_.ServerInfo;
import com.hypixel.hytale.protocol.packets.setup.PlayerOptions;
import com.hypixel.hytale.protocol.packets.setup.RequestAssets;
import com.hypixel.hytale.protocol.packets.setup.ViewRadius;
import com.hypixel.hytale.protocol.packets.setup.WorldLoadFinished;
import com.hypixel.hytale.protocol.packets.setup.WorldLoadProgress;
import com.hypixel.hytale.protocol.packets.setup.WorldSettings;
import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.HytaleServerConfig;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.AssetRegistryLoader;
import com.hypixel.hytale.server.core.asset.common.CommonAssetModule;
import com.hypixel.hytale.server.core.asset.common.PlayerCommonAssets;
import com.hypixel.hytale.server.core.asset.common.events.SendCommonAssetsEvent;
import com.hypixel.hytale.server.core.auth.PlayerAuthentication;
import com.hypixel.hytale.server.core.cosmetics.CosmeticsModule;
import com.hypixel.hytale.server.core.event.events.player.PlayerSetupConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerSetupDisconnectEvent;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.ProtocolVersion;
import com.hypixel.hytale.server.core.io.netty.NettyUtil;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.DumpUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class SetupPacketHandler extends GenericConnectionPacketHandler {
   private final UUID uuid;
   private final String username;
   private final byte[] referralData;
   private final HostAddress referralSource;
   private PlayerCommonAssets assets;
   private boolean receivedRequest;
   private int clientViewRadiusChunks = 6;

   public SetupPacketHandler(@Nonnull Channel channel, @Nonnull ProtocolVersion protocolVersion, String language, UUID uuid, String username) {
      this(channel, protocolVersion, language, uuid, username, null, null);
   }

   public SetupPacketHandler(
      @Nonnull Channel channel,
      @Nonnull ProtocolVersion protocolVersion,
      String language,
      UUID uuid,
      String username,
      byte[] referralData,
      HostAddress referralSource
   ) {
      super(channel, protocolVersion, language);
      this.uuid = uuid;
      this.username = username;
      this.referralData = referralData;
      this.referralSource = referralSource;
      this.auth = null;
      if (referralData != null && referralData.length > 0) {
         HytaleLogger.getLogger()
            .at(Level.INFO)
            .log(
               "Player %s connecting with %d bytes of referral data from %s:%d (unauthenticated - plugins must validate!)",
               username,
               referralData.length,
               referralSource != null ? referralSource.host : "unknown",
               referralSource != null ? referralSource.port : 0
            );
      }
   }

   public SetupPacketHandler(@Nonnull Channel channel, @Nonnull ProtocolVersion protocolVersion, String language, @Nonnull PlayerAuthentication auth) {
      super(channel, protocolVersion, language);
      this.uuid = auth.getUuid();
      this.username = auth.getUsername();
      this.auth = auth;
      this.referralData = auth.getReferralData();
      this.referralSource = auth.getReferralSource();
      if (this.referralData != null && this.referralData.length > 0) {
         HytaleLogger.getLogger()
            .at(Level.INFO)
            .log(
               "Player %s connecting with %d bytes of referral data from %s:%d (authenticated)",
               this.username,
               this.referralData.length,
               this.referralSource != null ? this.referralSource.host : "unknown",
               this.referralSource != null ? this.referralSource.port : 0
            );
      }
   }

   @Nonnull
   @Override
   public String getIdentifier() {
      return "{Setup("
         + NettyUtil.formatRemoteAddress(this.channel)
         + "), "
         + this.username
         + ", "
         + this.uuid
         + ", "
         + (this.auth != null ? "SECURE" : "INSECURE")
         + "}";
   }

   @Override
   public void registered0(@Nonnull PacketHandler oldHandler) {
      this.setTimeout("send-world-settings", () -> this.assets != null, 1L, TimeUnit.SECONDS);
      PlayerSetupConnectEvent event = HytaleServer.get()
         .getEventBus()
         .<Void, PlayerSetupConnectEvent>dispatchFor(PlayerSetupConnectEvent.class)
         .dispatch(new PlayerSetupConnectEvent(this, this.username, this.uuid, this.auth, this.referralData, this.referralSource));
      if (event.isCancelled()) {
         this.disconnect(event.getReason());
      } else {
         ClientReferral clientReferral = event.getClientReferral();
         if (clientReferral != null) {
            this.writeNoCache(clientReferral);
         } else {
            HytaleServerConfig serverConfig = HytaleServer.get().getConfig();
            boolean enableCompression;
            if (!serverConfig.isLocalCompressionEnabled()) {
               enableCompression = !oldHandler.isLocalConnection();
            } else {
               enableCompression = true;
            }

            oldHandler.setCompressionEnabled(enableCompression);
            PlayerRef otherPlayer = Universe.get().getPlayer(this.uuid);
            if (otherPlayer != null) {
               HytaleLogger.getLogger().at(Level.INFO).log("Found match of player %s on %s", this.uuid, otherPlayer.getUsername());
               Channel otherPlayerChannel = otherPlayer.getPacketHandler().getChannel();
               if (!NettyUtil.isFromSameOrigin(otherPlayerChannel, this.channel)) {
                  this.disconnect("You are already logged in on that account!");
                  otherPlayer.sendMessage(Message.translation("server.io.setuppackethandler.otherLoginAttempt"));
                  return;
               }

               Ref<EntityStore> reference = otherPlayer.getReference();
               if (reference != null) {
                  World world = reference.getStore().getExternalData().getWorld();
                  if (world != null) {
                     CompletableFuture<Void> removalFuture = new CompletableFuture<>();
                     world.execute(() -> {
                        otherPlayer.getPacketHandler().disconnect("You logged in again with the account!");
                        world.execute(() -> removalFuture.complete(null));
                     });
                     removalFuture.join();
                  } else {
                     otherPlayer.getPacketHandler().disconnect("You logged in again with the account!");
                  }
               }
            }

            PacketHandler.logConnectionTimings(this.channel, "Load Player Config", Level.FINE);
            WorldSettings worldSettings = new WorldSettings();
            worldSettings.worldHeight = 320;
            Asset[] requiredAssets = CommonAssetModule.get().getRequiredAssets();
            this.assets = new PlayerCommonAssets(requiredAssets);
            worldSettings.requiredAssets = requiredAssets;
            this.write(worldSettings);
            this.write(new ServerInfo(HytaleServer.get().getServerName(), serverConfig.getMotd(), serverConfig.getMaxPlayers()));
            this.setTimeout("receive-assets-request", () -> this.receivedRequest, 120L, TimeUnit.SECONDS);
         }
      }
   }

   @Override
   public void accept(@Nonnull Packet packet) {
      switch (packet.getId()) {
         case 1:
            this.handle((Disconnect)packet);
            break;
         case 23:
            this.handle((RequestAssets)packet);
            break;
         case 32:
            this.handle((ViewRadius)packet);
            break;
         case 33:
            this.handle((PlayerOptions)packet);
            break;
         default:
            this.disconnect("Protocol error: unexpected packet " + packet.getId());
      }
   }

   @Override
   public void closed(ChannelHandlerContext ctx) {
      super.closed(ctx);
      IEventDispatcher<PlayerSetupDisconnectEvent, PlayerSetupDisconnectEvent> dispatcher = HytaleServer.get()
         .getEventBus()
         .dispatchFor(PlayerSetupDisconnectEvent.class);
      if (dispatcher.hasListener()) {
         dispatcher.dispatch(new PlayerSetupDisconnectEvent(this.username, this.uuid, this.auth, this.disconnectReason));
      }

      if (Constants.SINGLEPLAYER) {
         if (Universe.get().getPlayerCount() == 0) {
            HytaleLogger.getLogger().at(Level.INFO).log("No players left on singleplayer server shutting down!");
            HytaleServer.get().shutdownServer();
         } else if (SingleplayerModule.isOwner(this.auth, this.uuid)) {
            HytaleLogger.getLogger().at(Level.INFO).log("Owner left the singleplayer server shutting down!");
            Universe.get().getPlayers().forEach(p -> p.getPacketHandler().disconnect(this.username + " left! Shutting down singleplayer world!"));
            HytaleServer.get().shutdownServer();
         }
      }
   }

   public void handle(@Nonnull Disconnect packet) {
      this.disconnectReason.setClientDisconnectType(packet.type);
      HytaleLogger.getLogger()
         .at(Level.INFO)
         .log(
            "%s - %s at %s left with reason: %s - %s", this.uuid, this.username, NettyUtil.formatRemoteAddress(this.channel), packet.type.name(), packet.reason
         );
      ProtocolUtil.closeApplicationConnection(this.channel);
      if (packet.type == DisconnectType.Crash
         && Constants.SINGLEPLAYER
         && (Universe.get().getPlayerCount() == 0 || SingleplayerModule.isOwner(this.auth, this.uuid))) {
         DumpUtil.dump(true, false);
      }
   }

   public void handle(@Nonnull RequestAssets packet) {
      if (this.receivedRequest) {
         throw new IllegalArgumentException("Received duplicate RequestAssets!");
      } else {
         this.receivedRequest = true;
         PacketHandler.logConnectionTimings(this.channel, "Request Assets", Level.FINE);
         CompletableFuture<Void> future = CompletableFutureUtil._catch(
            HytaleServer.get()
               .getEventBus()
               .<Void, SendCommonAssetsEvent>dispatchForAsync(SendCommonAssetsEvent.class)
               .dispatch(new SendCommonAssetsEvent(this, packet.assets))
               .thenAccept(event -> {
                  if (this.channel.isActive()) {
                     PacketHandler.logConnectionTimings(this.channel, "Send Common Assets", Level.FINE);
                     this.assets.sent(event.getRequestedAssets());
                     AssetRegistryLoader.sendAssets(this);
                     I18nModule.get().sendTranslations(this, this.language);
                     PacketHandler.logConnectionTimings(this.channel, "Send Config Assets", Level.FINE);
                     this.write(new WorldLoadProgress("Loading world...", 0, 0));
                     this.write(new WorldLoadFinished());
                  }
               })
               .exceptionally(throwable -> {
                  if (!this.channel.isActive()) {
                     return null;
                  } else {
                     this.disconnect("An exception occurred while trying to login!");
                     throw new RuntimeException("Exception when player was joining", throwable);
                  }
               })
         );
         this.setTimeout("send-assets", () -> future.isDone() || !future.cancel(true), 120L, TimeUnit.SECONDS);
      }
   }

   public void handle(@Nonnull ViewRadius packet) {
      this.clientViewRadiusChunks = MathUtil.ceil(packet.value / 32.0F);
   }

   public void handle(@Nonnull PlayerOptions packet) {
      if (!this.receivedRequest) {
         throw new IllegalArgumentException("Hasn't received RequestAssets yet!");
      } else {
         PacketHandler.logConnectionTimings(this.channel, "Player Options", Level.FINE);
         if (this.channel.isActive()) {
            if (packet.skin != null) {
               try {
                  CosmeticsModule.get().validateSkin(packet.skin);
               } catch (CosmeticsModule.InvalidSkinException var3) {
                  this.disconnect("Invalid skin! " + var3.getMessage());
                  return;
               }
            }

            CompletableFuture<Void> future = CompletableFutureUtil._catch(
               Universe.get()
                  .addPlayer(this.channel, this.language, this.protocolVersion, this.uuid, this.username, this.auth, this.clientViewRadiusChunks, packet.skin)
                  .thenAccept(player -> {
                     if (this.channel.isActive()) {
                        PacketHandler.logConnectionTimings(this.channel, "Add To Universe", Level.FINE);
                        this.clearTimeout();
                     }
                  })
                  .exceptionally(throwable -> {
                     if (!this.channel.isActive()) {
                        return null;
                     } else {
                        this.disconnect("An exception occurred when adding to the universe!");
                        throw new RuntimeException("Exception when player adding to universe", throwable);
                     }
                  })
            );
            this.setTimeout("add-to-universe", () -> future.isDone() || !future.cancel(true), 60L, TimeUnit.SECONDS);
         }
      }
   }
}
