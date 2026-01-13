package com.hypixel.hytale.builtin.asseteditor;

import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.AssetUpdateQuery;
import com.hypixel.hytale.assetstore.event.AssetMonitorEvent;
import com.hypixel.hytale.assetstore.event.AssetStoreMonitorEvent;
import com.hypixel.hytale.assetstore.event.RegisterAssetStoreEvent;
import com.hypixel.hytale.assetstore.event.RemoveAssetStoreEvent;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.asseteditor.assettypehandler.AssetStoreTypeHandler;
import com.hypixel.hytale.builtin.asseteditor.assettypehandler.AssetTypeHandler;
import com.hypixel.hytale.builtin.asseteditor.assettypehandler.CommonAssetTypeHandler;
import com.hypixel.hytale.builtin.asseteditor.assettypehandler.JsonTypeHandler;
import com.hypixel.hytale.builtin.asseteditor.data.AssetUndoRedoInfo;
import com.hypixel.hytale.builtin.asseteditor.data.ModifiedAsset;
import com.hypixel.hytale.builtin.asseteditor.datasource.DataSource;
import com.hypixel.hytale.builtin.asseteditor.datasource.StandardDataSource;
import com.hypixel.hytale.builtin.asseteditor.event.AssetEditorAssetCreatedEvent;
import com.hypixel.hytale.builtin.asseteditor.event.AssetEditorClientDisconnectEvent;
import com.hypixel.hytale.builtin.asseteditor.event.AssetEditorSelectAssetEvent;
import com.hypixel.hytale.builtin.asseteditor.util.AssetPathUtil;
import com.hypixel.hytale.builtin.asseteditor.util.AssetStoreUtil;
import com.hypixel.hytale.builtin.asseteditor.util.BsonTransformationUtil;
import com.hypixel.hytale.codec.EmptyExtraInfo;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.common.plugin.AuthorInfo;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.common.semver.Semver;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.common.util.FormatUtil;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorAsset;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorAssetListUpdate;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorAssetPackSetup;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorAssetUpdated;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorCapabilities;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorDeleteAssetPack;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorEditorType;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorExportAssetFinalize;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorExportAssetInitialize;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorExportAssetPart;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorExportComplete;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorExportDeleteAssets;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorFetchAssetReply;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorFetchJsonAssetWithParentsReply;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorFileEntry;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorJsonAssetUpdated;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorLastModifiedAssets;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorPopupNotificationType;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorRebuildCaches;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorRequestChildrenListReply;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorSetupSchemas;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorUndoRedoReply;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorUpdateAssetPack;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetInfo;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetPackManifest;
import com.hypixel.hytale.protocol.packets.asseteditor.JsonUpdateCommand;
import com.hypixel.hytale.protocol.packets.asseteditor.JsonUpdateType;
import com.hypixel.hytale.protocol.packets.asseteditor.SchemaFile;
import com.hypixel.hytale.protocol.packets.asseteditor.TimestampedAssetReference;
import com.hypixel.hytale.protocol.packets.assets.UpdateTranslations;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.Options;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.asset.AssetPackRegisterEvent;
import com.hypixel.hytale.server.core.asset.AssetPackUnregisterEvent;
import com.hypixel.hytale.server.core.asset.AssetRegistryLoader;
import com.hypixel.hytale.server.core.asset.common.events.CommonAssetMonitorEvent;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.ServerManager;
import com.hypixel.hytale.server.core.io.handlers.InitialPacketHandler;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.modules.i18n.event.MessagesUpdated;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.plugin.PluginState;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.BsonUtil;
import com.hypixel.hytale.server.core.util.io.FileUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class AssetEditorPlugin extends JavaPlugin {
   private static AssetEditorPlugin instance;
   private final StampedLock globalEditLock = new StampedLock();
   private final Map<UUID, Set<EditorClient>> uuidToEditorClients = new ConcurrentHashMap<>();
   private final Map<EditorClient, AssetPath> clientOpenAssetPathMapping = new ConcurrentHashMap<>();
   private final Set<EditorClient> clientsSubscribedToModifiedAssetsChanges = ConcurrentHashMap.newKeySet();
   @Nonnull
   private Map<String, Schema> schemas = new Object2ObjectOpenHashMap<>();
   private AssetEditorSetupSchemas setupSchemasPacket;
   private final StampedLock initLock = new StampedLock();
   private final Set<EditorClient> initQueue = new HashSet<>();
   @Nonnull
   private AssetEditorPlugin.InitState initState = AssetEditorPlugin.InitState.NOT_INITIALIZED;
   @Nullable
   private ScheduledFuture<?> scheduledReinitFuture;
   private final Map<String, DataSource> assetPackDataSources = new ConcurrentHashMap<>();
   private final AssetTypeRegistry assetTypeRegistry = new AssetTypeRegistry();
   private final UndoRedoManager undoRedoManager = new UndoRedoManager();
   private ScheduledFuture<?> pingClientsTask;

   public static AssetEditorPlugin get() {
      return instance;
   }

   public AssetEditorPlugin(@Nonnull JavaPluginInit init) {
      super(init);
   }

   @NullableDecl
   DataSource registerDataSourceForPack(AssetPack assetPack) {
      PluginManifest manifest = assetPack.getManifest();
      if (manifest == null) {
         this.getLogger().at(Level.SEVERE).log("Could not load asset pack manifest for " + assetPack.getName());
         return null;
      } else {
         StandardDataSource dataSource = new StandardDataSource(assetPack.getName(), assetPack.getRoot(), assetPack.isImmutable(), manifest);
         this.assetPackDataSources.put(assetPack.getName(), dataSource);
         return dataSource;
      }
   }

   @Override
   protected void setup() {
      instance = this;

      for (AssetPack assetPack : AssetModule.get().getAssetPacks()) {
         this.registerDataSourceForPack(assetPack);
      }

      ServerManager.get().registerSubPacketHandlers(AssetEditorGamePacketHandler::new);
      InitialPacketHandler.EDITOR_PACKET_HANDLER_SUPPLIER = AssetEditorPacketHandler::new;

      for (AssetStore<?, ?, ?> assetStore : AssetRegistry.getStoreMap().values()) {
         if (assetStore.getPath() != null && !assetStore.getPath().startsWith("../")) {
            this.assetTypeRegistry.registerAssetType(new AssetStoreTypeHandler(assetStore));
         }
      }

      this.assetTypeRegistry.registerAssetType(new CommonAssetTypeHandler("Texture", "Texture.png", ".png", AssetEditorEditorType.Texture));
      this.assetTypeRegistry.registerAssetType(new CommonAssetTypeHandler("Model", "Model.png", ".blockymodel", AssetEditorEditorType.JsonSource));
      this.assetTypeRegistry.registerAssetType(new CommonAssetTypeHandler("Animation", "Animation.png", ".blockyanim", AssetEditorEditorType.JsonSource));
      this.assetTypeRegistry.registerAssetType(new CommonAssetTypeHandler("Sound", null, ".ogg", AssetEditorEditorType.None));
      this.assetTypeRegistry.registerAssetType(new CommonAssetTypeHandler("UI", null, ".ui", AssetEditorEditorType.Text));
      this.assetTypeRegistry.registerAssetType(new CommonAssetTypeHandler("Language", null, ".lang", AssetEditorEditorType.Text));
      this.getEventRegistry().register(RegisterAssetStoreEvent.class, this::onRegisterAssetStore);
      this.getEventRegistry().register(RemoveAssetStoreEvent.class, this::onUnregisterAssetStore);
      this.getEventRegistry().register(AssetPackRegisterEvent.class, this::onRegisterAssetPack);
      this.getEventRegistry().register(AssetPackUnregisterEvent.class, this::onUnregisterAssetPack);
      this.getEventRegistry().register(AssetStoreMonitorEvent.class, this::onAssetMonitor);
      this.getEventRegistry().register(CommonAssetMonitorEvent.class, this::onAssetMonitor);
      this.getEventRegistry().register(MessagesUpdated.class, this::onI18nMessagesUpdated);
      AssetSpecificFunctionality.setup();
   }

   @Override
   protected void start() {
      for (DataSource dataSource : this.assetPackDataSources.values()) {
         dataSource.start();
      }

      this.pingClientsTask = HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(this::sendPingPackets, 1L, 1L, PacketHandler.PingInfo.PING_FREQUENCY_UNIT);
   }

   @Override
   protected void shutdown() {
      InitialPacketHandler.EDITOR_PACKET_HANDLER_SUPPLIER = null;
      String message = HytaleServer.get().isShuttingDown() ? "Server is shutting down!" : "Asset editor was disabled!";

      for (Set<EditorClient> clients : this.uuidToEditorClients.values()) {
         for (EditorClient client : clients) {
            client.getPacketHandler().disconnect(message);
         }
      }

      this.pingClientsTask.cancel(false);

      for (DataSource dataSource : this.assetPackDataSources.values()) {
         dataSource.shutdown();
      }
   }

   public DataSource getDataSourceForPath(AssetPath path) {
      return this.getDataSourceForPack(path.packId());
   }

   public DataSource getDataSourceForPack(String assetPack) {
      return this.assetPackDataSources.get(assetPack);
   }

   public Collection<DataSource> getDataSources() {
      return this.assetPackDataSources.values();
   }

   public AssetTypeRegistry getAssetTypeRegistry() {
      return this.assetTypeRegistry;
   }

   public Schema getSchema(String id) {
      return this.schemas.get(id);
   }

   public Map<EditorClient, AssetPath> getClientOpenAssetPathMapping() {
      return this.clientOpenAssetPathMapping;
   }

   public Set<EditorClient> getEditorClients(UUID uuid) {
      return this.uuidToEditorClients.get(uuid);
   }

   private void sendPingPackets() {
      for (Set<EditorClient> clients : this.uuidToEditorClients.values()) {
         for (EditorClient client : clients) {
            try {
               client.getPacketHandler().sendPing();
            } catch (Exception var6) {
               this.getLogger().at(Level.SEVERE).withCause(var6).log("Failed to send ping to " + client);
               client.getPacketHandler().disconnect("Exception when sending ping packet!");
            }
         }
      }
   }

   @Nonnull
   private List<EditorClient> getClientsWithOpenAssetPath(AssetPath path) {
      if (this.clientOpenAssetPathMapping.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<EditorClient> list = new ObjectArrayList<>();

         for (Entry<EditorClient, AssetPath> entry : this.clientOpenAssetPathMapping.entrySet()) {
            if (entry.getValue().equals(path)) {
               list.add(entry.getKey());
            }
         }

         return list;
      }
   }

   public AssetPath getOpenAssetPath(EditorClient editorClient) {
      return this.clientOpenAssetPathMapping.get(editorClient);
   }

   private void onRegisterAssetPack(AssetPackRegisterEvent event) {
      if (!this.assetPackDataSources.containsKey(event.getAssetPack().getName())) {
         DataSource dataSource = this.registerDataSourceForPack(event.getAssetPack());
         if (dataSource != null) {
            if (this.getState() == PluginState.ENABLED) {
               dataSource.start();
            }

            AssetTree tempAssetTree = dataSource.loadAssetTree(this.assetTypeRegistry.getRegisteredAssetTypeHandlers().values());
            long globalEditStamp = this.globalEditLock.writeLock();

            try {
               dataSource.getAssetTree().replaceAssetTree(tempAssetTree);
            } finally {
               this.globalEditLock.unlockWrite(globalEditStamp);
            }

            this.broadcastPackAddedOrUpdated(event.getAssetPack().getName(), dataSource.getManifest());

            for (Set<EditorClient> clients : this.uuidToEditorClients.values()) {
               for (EditorClient client : clients) {
                  dataSource.getAssetTree().sendPackets(client);
               }
            }
         }
      }
   }

   private void onUnregisterAssetPack(AssetPackUnregisterEvent event) {
      if (this.assetPackDataSources.containsKey(event.getAssetPack().getName())) {
         DataSource dataSource = this.assetPackDataSources.remove(event.getAssetPack().getName());
         dataSource.shutdown();

         for (Set<EditorClient> clients : this.uuidToEditorClients.values()) {
            for (EditorClient client : clients) {
               client.getPacketHandler().write(new AssetEditorDeleteAssetPack(event.getAssetPack().getName()));
            }
         }
      }
   }

   private void onI18nMessagesUpdated(@Nonnull MessagesUpdated event) {
      if (!this.clientOpenAssetPathMapping.isEmpty()) {
         I18nModule i18nModule = I18nModule.get();
         Map<String, Map<String, String>> changed = event.getChangedMessages();
         Map<String, Map<String, String>> removed = event.getRemovedMessages();
         Map<String, UpdateTranslations[]> updatePackets = new Object2ObjectOpenHashMap<>();

         for (EditorClient client : this.clientOpenAssetPathMapping.keySet()) {
            String languageKey = client.getLanguage();
            UpdateTranslations[] packets = updatePackets.get(languageKey);
            if (packets == null) {
               packets = i18nModule.getUpdatePacketsForChanges(languageKey, changed, removed);
               updatePackets.put(languageKey, packets);
            }

            if (packets.length != 0) {
               client.getPacketHandler().write(packets);
            }
         }
      }
   }

   private void onRegisterAssetStore(@Nonnull RegisterAssetStoreEvent event) {
      AssetStore<?, ? extends JsonAssetWithMap<?, ? extends AssetMap<?, ?>>, ? extends AssetMap<?, ? extends JsonAssetWithMap<?, ?>>> assetStore = (AssetStore<?, ? extends JsonAssetWithMap<?, ? extends AssetMap<?, ?>>, ? extends AssetMap<?, ? extends JsonAssetWithMap<?, ?>>>)event.getAssetStore();
      if (assetStore.getPath() != null && !assetStore.getPath().startsWith("../")) {
         this.assetTypeRegistry.registerAssetType(new AssetStoreTypeHandler(assetStore));
         long stamp = this.initLock.readLock();

         try {
            if (this.initState != AssetEditorPlugin.InitState.NOT_INITIALIZED) {
               if (this.scheduledReinitFuture != null) {
                  this.scheduledReinitFuture.cancel(false);
               }

               this.scheduledReinitFuture = HytaleServer.SCHEDULED_EXECUTOR.schedule(this::tryReinitializeAssetEditor, 1L, TimeUnit.SECONDS);
            }
         } finally {
            this.initLock.unlockRead(stamp);
         }
      }
   }

   private void onUnregisterAssetStore(@Nonnull RemoveAssetStoreEvent event) {
      AssetStore<?, ? extends JsonAssetWithMap<?, ? extends AssetMap<?, ?>>, ? extends AssetMap<?, ? extends JsonAssetWithMap<?, ?>>> assetStore = (AssetStore<?, ? extends JsonAssetWithMap<?, ? extends AssetMap<?, ?>>, ? extends AssetMap<?, ? extends JsonAssetWithMap<?, ?>>>)event.getAssetStore();
      if (assetStore.getPath() != null && !assetStore.getPath().startsWith("../")) {
         this.assetTypeRegistry.unregisterAssetType(new AssetStoreTypeHandler(assetStore));
         long stamp = this.initLock.readLock();

         try {
            if (this.initState != AssetEditorPlugin.InitState.NOT_INITIALIZED) {
               if (this.scheduledReinitFuture != null) {
                  this.scheduledReinitFuture.cancel(false);
               }

               this.scheduledReinitFuture = HytaleServer.SCHEDULED_EXECUTOR.schedule(this::tryReinitializeAssetEditor, 1L, TimeUnit.SECONDS);
            }
         } finally {
            this.initLock.unlockRead(stamp);
         }
      }
   }

   private void tryReinitializeAssetEditor() {
      long stamp = this.initLock.writeLock();

      try {
         switch (this.initState) {
            case INITIALIZING:
               this.scheduledReinitFuture = HytaleServer.SCHEDULED_EXECUTOR.schedule(this::tryReinitializeAssetEditor, 1L, TimeUnit.SECONDS);
               break;
            case INITIALIZED:
               this.initState = AssetEditorPlugin.InitState.INITIALIZING;
               this.scheduledReinitFuture = null;
               this.getLogger().at(Level.INFO).log("Starting asset editor re-initialization");
               ForkJoinPool.commonPool().execute(() -> this.initializeAssetEditor(true));
         }
      } finally {
         this.initLock.unlockWrite(stamp);
      }
   }

   private void onAssetMonitor(@Nonnull AssetMonitorEvent<Void> event) {
      AssetEditorAssetListUpdate packet = new AssetEditorAssetListUpdate();
      packet.pack = event.getAssetPack();
      ObjectArrayList<AssetEditorFileEntry> newFiles = new ObjectArrayList<>();
      DataSource dataSource = this.getDataSourceForPack(event.getAssetPack());
      if (dataSource != null) {
         if (!event.getRemovedFilesAndDirectories().isEmpty()) {
            ObjectArrayList<AssetEditorFileEntry> deletions = new ObjectArrayList<>();

            for (Path path : event.getRemovedFilesAndDirectories()) {
               Path relativePath = PathUtil.relativizePretty(dataSource.getRootPath(), path);
               AssetEditorFileEntry assetFile = dataSource.getAssetTree().removeAsset(relativePath);
               if (assetFile != null) {
                  deletions.add(assetFile);
               }
            }

            packet.deletions = deletions.toArray(AssetEditorFileEntry[]::new);
         }

         if (!event.getRemovedFilesToUnload().isEmpty()) {
            event.getRemovedFilesToUnload().removeIf(p -> {
               Path relativePathx = PathUtil.relativizePretty(dataSource.getRootPath(), p);
               if (!dataSource.shouldReloadAssetFromDisk(relativePathx)) {
                  this.getLogger().at(Level.INFO).log("Skipping reloading %s from file monitor event because there is changes made via the asset editor", p);
                  return true;
               } else {
                  long globalEditStamp = this.globalEditLock.writeLock();

                  try {
                     this.undoRedoManager.clearUndoRedoStack(new AssetPath(event.getAssetPack(), relativePathx));
                  } finally {
                     this.globalEditLock.unlockWrite(globalEditStamp);
                  }

                  return false;
               }
            });
         }

         if (!event.getCreatedOrModifiedDirectories().isEmpty()) {
            for (Path assetFile : event.getCreatedOrModifiedDirectories()) {
               Path relativePath = PathUtil.relativizePretty(dataSource.getRootPath(), assetFile);
               AssetEditorFileEntry addedAsset = dataSource.getAssetTree().ensureAsset(relativePath, true);
               if (addedAsset != null) {
                  newFiles.add(addedAsset);
               }
            }
         }

         if (!event.getCreatedOrModifiedFilesToLoad().isEmpty()) {
            event.getCreatedOrModifiedFilesToLoad()
               .removeIf(
                  pathx -> {
                     Path relativePathx = PathUtil.relativizePretty(dataSource.getRootPath(), pathx);
                     AssetEditorFileEntry addedAssetx = dataSource.getAssetTree().ensureAsset(relativePathx, false);
                     if (addedAssetx != null) {
                        newFiles.add(addedAssetx);
                        return false;
                     } else if (!dataSource.shouldReloadAssetFromDisk(relativePathx)) {
                        this.getLogger()
                           .at(Level.INFO)
                           .log("Skipping reloading %s from file monitor event because there is changes made via the asset editor", pathx);
                        return true;
                     } else {
                        AssetPath assetPath = new AssetPath(event.getAssetPack(), relativePathx);
                        long globalEditStamp = this.globalEditLock.writeLock();

                        try {
                           this.undoRedoManager.clearUndoRedoStack(assetPath);
                        } finally {
                           this.globalEditLock.unlockWrite(globalEditStamp);
                        }

                        List<EditorClient> clientsWithOpenAssetPath = this.getClientsWithOpenAssetPath(assetPath);
                        if (!clientsWithOpenAssetPath.isEmpty()) {
                           AssetEditorAssetUpdated updatePacket = new AssetEditorAssetUpdated(assetPath.toPacket(), dataSource.getAssetBytes(relativePathx));

                           for (EditorClient editorClient : clientsWithOpenAssetPath) {
                              editorClient.getPacketHandler().write(updatePacket);
                           }
                        }

                        return false;
                     }
                  }
               );
            if (!newFiles.isEmpty()) {
               packet.additions = newFiles.toArray(AssetEditorFileEntry[]::new);
            }
         }

         if (!newFiles.isEmpty()) {
            packet.additions = newFiles.toArray(AssetEditorFileEntry[]::new);
         }

         if (packet.deletions != null || packet.additions != null) {
            this.sendPacketToAllEditorUsers(packet);
         }
      }
   }

   public void handleInitializeEditor(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());

      assert playerRefComponent != null;

      String username = playerRefComponent.getUsername();
      this.getLogger().at(Level.INFO).log("%s is attempting to initialize asset editor", username);
      long stamp = this.initLock.writeLock();

      try {
         if (this.initState == AssetEditorPlugin.InitState.NOT_INITIALIZED) {
            this.initState = AssetEditorPlugin.InitState.INITIALIZING;
            ForkJoinPool.commonPool().execute(() -> this.initializeAssetEditor(false));
            this.getLogger().at(Level.INFO).log("%s starting asset editor initialization", username);
         }
      } finally {
         this.initLock.unlockWrite(stamp);
      }
   }

   public void handleInitializeClient(@Nonnull EditorClient editorClient) {
      this.getLogger().at(Level.INFO).log("Initializing %s", editorClient.getUsername());
      this.uuidToEditorClients.computeIfAbsent(editorClient.getUuid(), k -> ConcurrentHashMap.newKeySet()).add(editorClient);
      this.clientOpenAssetPathMapping.put(editorClient, new AssetPath("", Path.of("")));
      I18nModule.get().sendTranslations(editorClient.getPacketHandler(), editorClient.getLanguage());
      long stamp = this.initLock.writeLock();

      try {
         switch (this.initState) {
            case NOT_INITIALIZED:
               this.initState = AssetEditorPlugin.InitState.INITIALIZING;
               this.initQueue.add(editorClient);
               ForkJoinPool.commonPool().execute(() -> this.initializeAssetEditor(false));
               this.getLogger().at(Level.INFO).log("%s starting asset editor initialization", editorClient.getUsername());
               return;
            case INITIALIZING:
               this.getLogger().at(Level.INFO).log("%s waiting for asset editor initialization to complete", editorClient.getUsername());
               this.initQueue.add(editorClient);
               return;
            case INITIALIZED:
         }
      } finally {
         this.initLock.unlockWrite(stamp);
      }

      this.initializeClient(editorClient);
   }

   private void initializeAssetEditor(boolean updateLoadedAssets) {
      long start = System.nanoTime();
      Map<String, Schema> schemas = AssetRegistryLoader.generateSchemas(new SchemaContext(), new BsonDocument());
      schemas.remove("NPCRole.json");
      schemas.remove("other.json");
      AssetEditorSetupSchemas setupSchemasPacket = new AssetEditorSetupSchemas(new SchemaFile[schemas.size()]);
      int i = 0;

      for (Schema schema : schemas.values()) {
         String bytes = Schema.CODEC.encode(schema, EmptyExtraInfo.EMPTY).asDocument().toJson();
         setupSchemasPacket.schemas[i++] = new SchemaFile(bytes);
      }

      for (DataSource dataSource : this.assetPackDataSources.values()) {
         AssetTree tempAssetTree = dataSource.loadAssetTree(this.assetTypeRegistry.getRegisteredAssetTypeHandlers().values());
         long globalEditStamp = this.globalEditLock.writeLock();

         try {
            dataSource.getAssetTree().replaceAssetTree(tempAssetTree);
            this.assetTypeRegistry.setupPacket();
            if (updateLoadedAssets) {
               dataSource.updateRuntimeAssets();
            }
         } finally {
            this.globalEditLock.unlockWrite(globalEditStamp);
         }
      }

      long globalEditStamp = this.globalEditLock.writeLock();

      try {
         this.schemas = schemas;
         this.setupSchemasPacket = setupSchemasPacket;
         this.assetTypeRegistry.setupPacket();
      } finally {
         this.globalEditLock.unlockWrite(globalEditStamp);
      }

      long var31 = this.initLock.writeLock();

      try {
         this.initState = AssetEditorPlugin.InitState.INITIALIZED;
         this.getLogger().at(Level.INFO).log("Asset editor initialization complete! Took: %s", FormatUtil.nanosToString(System.nanoTime() - start));

         for (EditorClient editorClient : this.clientOpenAssetPathMapping.keySet()) {
            this.initializeClient(editorClient);
         }

         this.initQueue.clear();
      } finally {
         this.initLock.unlockWrite(var31);
      }
   }

   private void initializeClient(@Nonnull EditorClient editorClient) {
      DataSource defaultDataSource = this.assetPackDataSources.get("Hytale:Hytale");
      boolean canDiscard = false;
      boolean canEditAssets = editorClient.hasPermission("hytale.editor.asset");
      boolean canEditAssetPacks = editorClient.hasPermission("hytale.editor.packs.edit");
      boolean canCreateAssetPacks = editorClient.hasPermission("hytale.editor.packs.create");
      boolean canDeleteAssetPacks = editorClient.hasPermission("hytale.editor.packs.delete");
      editorClient.getPacketHandler().write(new AssetEditorCapabilities(false, canEditAssets, canCreateAssetPacks, canEditAssetPacks, canDeleteAssetPacks));
      editorClient.getPacketHandler().write(this.setupSchemasPacket);
      this.assetTypeRegistry.sendPacket(editorClient);
      AssetEditorAssetPackSetup packSetupPacket = new AssetEditorAssetPackSetup();
      packSetupPacket.packs = new Object2ObjectOpenHashMap<>();

      for (Entry<String, DataSource> dataSourceEntry : this.assetPackDataSources.entrySet()) {
         DataSource dataSource = dataSourceEntry.getValue();
         PluginManifest manifest = dataSource.getManifest();
         packSetupPacket.packs.put(dataSourceEntry.getKey(), toManifestPacket(manifest));
      }

      editorClient.getPacketHandler().write(packSetupPacket);

      for (DataSource dataSource : this.assetPackDataSources.values()) {
         dataSource.getAssetTree().sendPackets(editorClient);
      }

      this.getLogger().at(Level.INFO).log("Done Initializing %s", editorClient.getUsername());
   }

   public void handleEditorClientDisconnected(@Nonnull EditorClient editorClient, PacketHandler.DisconnectReason disconnectReason) {
      IEventDispatcher<AssetEditorClientDisconnectEvent, AssetEditorClientDisconnectEvent> dispatch = HytaleServer.get()
         .getEventBus()
         .dispatchFor(AssetEditorClientDisconnectEvent.class);
      if (dispatch.hasListener()) {
         dispatch.dispatch(new AssetEditorClientDisconnectEvent(editorClient, disconnectReason));
      }

      this.uuidToEditorClients.compute(editorClient.getUuid(), (uuid, clients) -> {
         if (clients == null) {
            return null;
         } else {
            clients.remove(editorClient);
            return clients.isEmpty() ? null : clients;
         }
      });
      this.clientOpenAssetPathMapping.remove(editorClient);
      this.clientsSubscribedToModifiedAssetsChanges.remove(editorClient);
   }

   public void handleDeleteAssetPack(@Nonnull EditorClient editorClient, @Nonnull String packId) {
      if (packId.equalsIgnoreCase("Hytale:Hytale")) {
         editorClient.sendPopupNotification(AssetEditorPopupNotificationType.Error, Messages.UNKNOWN_ASSETPACK_MESSAGE);
      } else {
         DataSource dataSource = this.getDataSourceForPack(packId);
         if (dataSource == null) {
            editorClient.sendPopupNotification(AssetEditorPopupNotificationType.Error, Messages.UNKNOWN_ASSETPACK_MESSAGE);
         } else {
            AssetModule.get().unregisterPack(packId);

            Path targetPath;
            try {
               targetPath = dataSource.getRootPath().toRealPath();
            } catch (IOException var11) {
               throw new RuntimeException("Failed to resolve the real path for asset pack directory while deleting asset pack '" + packId + "'.", var11);
            }

            boolean isInModsDirectory = false;

            try {
               if (targetPath.startsWith(PluginManager.MODS_PATH.toRealPath())) {
                  isInModsDirectory = true;
               }
            } catch (IOException var10) {
            }

            if (!isInModsDirectory) {
               for (Path modsPath : Options.getOptionSet().valuesOf(Options.MODS_DIRECTORIES)) {
                  try {
                     if (targetPath.startsWith(modsPath.toRealPath())) {
                        isInModsDirectory = true;
                        break;
                     }
                  } catch (IOException var12) {
                  }
               }
            }

            if (!isInModsDirectory) {
               editorClient.sendPopupNotification(
                  AssetEditorPopupNotificationType.Error, Message.translation("server.assetEditor.messages.packOutsideDirectory")
               );
            } else {
               try {
                  FileUtil.deleteDirectory(targetPath);
               } catch (Exception var9) {
                  this.getLogger().at(Level.SEVERE).withCause(var9).log("Failed to delete asset pack %s from disk", packId);
               }
            }
         }
      }
   }

   public void handleUpdateAssetPack(@Nonnull EditorClient editorClient, @Nonnull String packId, @Nonnull AssetPackManifest packetManifest) {
      if (packId.equals("Hytale:Hytale")) {
         editorClient.sendPopupNotification(AssetEditorPopupNotificationType.Error, Messages.UNKNOWN_ASSETPACK_MESSAGE);
      } else {
         DataSource dataSource = this.getDataSourceForPack(packId);
         if (dataSource == null) {
            editorClient.sendPopupNotification(AssetEditorPopupNotificationType.Error, Messages.UNKNOWN_ASSETPACK_MESSAGE);
         } else if (dataSource.isImmutable()) {
            editorClient.sendPopupNotification(AssetEditorPopupNotificationType.Error, Message.translation("server.assetEditor.messages.assetsReadOnly"));
         } else {
            PluginManifest manifest = dataSource.getManifest();
            if (manifest == null) {
               editorClient.sendPopupNotification(AssetEditorPopupNotificationType.Error, Message.translation("server.assetEditor.messages.manifestNotFound"));
            } else {
               boolean didIdentifierChange = false;
               if (packetManifest.name != null && !packetManifest.name.isEmpty() && !manifest.getName().equals(packetManifest.name)) {
                  manifest.setName(packetManifest.name);
                  didIdentifierChange = true;
               }

               if (packetManifest.group != null && !packetManifest.group.isEmpty() && !manifest.getGroup().equals(packetManifest.group)) {
                  manifest.setGroup(packetManifest.group);
                  didIdentifierChange = true;
               }

               if (packetManifest.description != null) {
                  manifest.setDescription(packetManifest.description);
               }

               if (packetManifest.website != null) {
                  manifest.setWebsite(packetManifest.website);
               }

               if (packetManifest.version != null && !packetManifest.version.isEmpty()) {
                  try {
                     manifest.setVersion(Semver.fromString(packetManifest.version));
                  } catch (IllegalArgumentException var14) {
                     this.getLogger().at(Level.WARNING).withCause(var14).log("Invalid version format: %s", packetManifest.version);
                     editorClient.sendPopupNotification(
                        AssetEditorPopupNotificationType.Error, Message.translation("server.assetEditor.messages.invalidVersionFormat")
                     );
                     return;
                  }
               }

               if (packetManifest.authors != null) {
                  List<AuthorInfo> authors = new ObjectArrayList<>();

                  for (com.hypixel.hytale.protocol.packets.asseteditor.AuthorInfo packetAuthor : packetManifest.authors) {
                     AuthorInfo author = new AuthorInfo();
                     author.setName(packetAuthor.name);
                     author.setEmail(packetAuthor.email);
                     author.setUrl(packetAuthor.url);
                     authors.add(author);
                  }

                  manifest.setAuthors(authors);
               }

               Path manifestPath = dataSource.getRootPath().resolve("manifest.json");

               try {
                  BsonUtil.writeSync(manifestPath, PluginManifest.CODEC, manifest, this.getLogger());
                  this.getLogger().at(Level.INFO).log("Saved manifest for pack %s", packId);
                  editorClient.sendPopupNotification(AssetEditorPopupNotificationType.Success, Message.translation("server.assetEditor.messages.manifestSaved"));
               } catch (IOException var13) {
                  this.getLogger().at(Level.SEVERE).withCause(var13).log("Failed to save manifest for pack %s", packId);
                  editorClient.sendPopupNotification(
                     AssetEditorPopupNotificationType.Error, Message.translation("server.assetEditor.messages.manifestSaveFailed")
                  );
               }

               this.broadcastPackAddedOrUpdated(packId, manifest);
               if (didIdentifierChange) {
                  String newPackId = new PluginIdentifier(manifest).toString();
                  Path packPath = dataSource.getRootPath();
                  AssetModule assetModule = AssetModule.get();
                  assetModule.unregisterPack(packId);
                  assetModule.registerPack(newPackId, packPath, manifest);
               }
            }
         }
      }
   }

   public void handleCreateAssetPack(@Nonnull EditorClient editorClient, @Nonnull AssetPackManifest packetManifest, int requestToken) {
      if (packetManifest.name == null || packetManifest.name.isEmpty()) {
         editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.packNameRequired"));
      } else if (packetManifest.group != null && !packetManifest.group.isEmpty()) {
         PluginManifest manifest = new PluginManifest();
         manifest.setName(packetManifest.name);
         manifest.setGroup(packetManifest.group);
         if (packetManifest.description != null) {
            manifest.setDescription(packetManifest.description);
         }

         if (packetManifest.website != null) {
            manifest.setWebsite(packetManifest.website);
         }

         if (packetManifest.version != null && !packetManifest.version.isEmpty()) {
            try {
               manifest.setVersion(Semver.fromString(packetManifest.version));
            } catch (IllegalArgumentException var12) {
               this.getLogger().at(Level.WARNING).withCause(var12).log("Invalid version format: %s", packetManifest.version);
               editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.invalidVersionFormat"));
               return;
            }
         }

         if (packetManifest.authors != null) {
            List<AuthorInfo> authors = new ObjectArrayList<>();

            for (com.hypixel.hytale.protocol.packets.asseteditor.AuthorInfo packetAuthor : packetManifest.authors) {
               AuthorInfo author = new AuthorInfo();
               author.setName(packetAuthor.name);
               author.setEmail(packetAuthor.email);
               author.setUrl(packetAuthor.url);
               authors.add(author);
            }

            manifest.setAuthors(authors);
         }

         String packId = new PluginIdentifier(manifest).toString();
         if (this.assetPackDataSources.containsKey(packId)) {
            editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.packAlreadyExists"));
         } else {
            Path modsPath = PluginManager.MODS_PATH;
            String dirName = AssetPathUtil.removeInvalidFileNameChars(
               packetManifest.group != null ? packetManifest.group + "." + packetManifest.name : packetManifest.name
            );
            Path normalized = Path.of(dirName).normalize();
            if (AssetPathUtil.isInvalidFileName(normalized)) {
               editorClient.sendFailureReply(requestToken, Messages.INVALID_FILENAME_MESSAGE);
            } else {
               Path packPath = modsPath.resolve(normalized).normalize();
               if (!packPath.startsWith(modsPath)) {
                  editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.packOutsideDirectory"));
               } else if (Files.exists(packPath)) {
                  editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.packAlreadyExistsAtPath"));
               } else {
                  try {
                     Files.createDirectories(packPath);
                     Path manifestPath = packPath.resolve("manifest.json");
                     BsonUtil.writeSync(manifestPath, PluginManifest.CODEC, manifest, this.getLogger());
                     AssetModule.get().registerPack(packId, packPath, manifest);
                     editorClient.sendSuccessReply(requestToken, Message.translation("server.assetEditor.messages.packCreated"));
                     this.getLogger().at(Level.INFO).log("Created new pack: %s at %s", packId, packPath);
                  } catch (IOException var11) {
                     this.getLogger().at(Level.SEVERE).withCause(var11).log("Failed to create pack %s", packId);
                     editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.packCreationFailed"));
                  }
               }
            }
         }
      } else {
         editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.packGroupRequired"));
      }
   }

   private static AssetPackManifest toManifestPacket(@Nonnull PluginManifest manifest) {
      AssetPackManifest packet = new AssetPackManifest();
      packet.name = manifest.getName();
      packet.description = manifest.getDescription() != null ? manifest.getDescription() : "";
      packet.group = manifest.getGroup();
      packet.version = manifest.getVersion() != null ? manifest.getVersion().toString() : "";
      packet.website = manifest.getWebsite() != null ? manifest.getWebsite() : "";
      List<com.hypixel.hytale.protocol.packets.asseteditor.AuthorInfo> authors = new ObjectArrayList<>();

      for (AuthorInfo a : manifest.getAuthors()) {
         com.hypixel.hytale.protocol.packets.asseteditor.AuthorInfo authorInfo = new com.hypixel.hytale.protocol.packets.asseteditor.AuthorInfo(
            a.getName(), a.getEmail(), a.getUrl()
         );
         authors.add(authorInfo);
      }

      packet.authors = authors.toArray(new com.hypixel.hytale.protocol.packets.asseteditor.AuthorInfo[0]);
      return packet;
   }

   private void broadcastPackAddedOrUpdated(String packId, PluginManifest manifest) {
      AssetPackManifest manifestPacket = toManifestPacket(manifest);

      for (Set<EditorClient> clients : this.uuidToEditorClients.values()) {
         for (EditorClient client : clients) {
            client.getPacketHandler().write(new AssetEditorUpdateAssetPack(packId, manifestPacket));
         }
      }
   }

   public void handleExportAssets(@Nonnull EditorClient editorClient, @Nonnull List<AssetPath> paths) {
      ObjectArrayList<TimestampedAssetReference> exportedAssets = new ObjectArrayList<>();

      for (AssetPath assetPath : paths) {
         DataSource dataSource = this.getDataSourceForPath(assetPath);
         if (dataSource == null) {
            this.getLogger().at(Level.WARNING).log("%s has no valid data source", assetPath);
            AssetEditorAsset asset = new AssetEditorAsset(null, assetPath.toPacket());
            editorClient.getPacketHandler().write(new AssetEditorExportAssetInitialize(asset, null, 0, true));
         } else if (!this.isValidPath(dataSource, assetPath)) {
            this.getLogger().at(Level.WARNING).log("%s is an invalid path", assetPath);
            AssetEditorAsset asset = new AssetEditorAsset(null, assetPath.toPacket());
            editorClient.getPacketHandler().write(new AssetEditorExportAssetInitialize(asset, null, 0, true));
         } else if (this.assetTypeRegistry.getAssetTypeHandlerForPath(assetPath.path()) == null) {
            this.getLogger().at(Level.WARNING).log("%s is not a valid asset type", assetPath);
            AssetEditorAsset asset = new AssetEditorAsset(null, assetPath.toPacket());
            editorClient.getPacketHandler().write(new AssetEditorExportAssetInitialize(asset, null, 0, true));
         } else if (!dataSource.doesAssetExist(assetPath.path())) {
            editorClient.getPacketHandler().write(new AssetEditorExportDeleteAssets(new AssetEditorAsset[]{new AssetEditorAsset(null, assetPath.toPacket())}));
         } else {
            byte[] bytes = dataSource.getAssetBytes(assetPath.path());
            if (bytes == null) {
               this.getLogger().at(Level.WARNING).log("Tried to load %s for export but failed", assetPath);
               editorClient.getPacketHandler().write(new AssetEditorExportAssetInitialize(new AssetEditorAsset(null, assetPath.toPacket()), null, 0, false));
            } else {
               byte[][] parts = ArrayUtil.split(bytes, 2621440);
               Packet[] packets = new Packet[2 + parts.length];
               packets[0] = new AssetEditorExportAssetInitialize(new AssetEditorAsset(null, assetPath.toPacket()), null, bytes.length, false);

               for (int partIndex = 0; partIndex < parts.length; partIndex++) {
                  packets[1 + partIndex] = new AssetEditorExportAssetPart(parts[partIndex]);
               }

               packets[packets.length - 1] = new AssetEditorExportAssetFinalize();
               editorClient.getPacketHandler().write(packets);
               Instant timestamp = dataSource.getLastModificationTimestamp(assetPath.path());
               exportedAssets.add(new TimestampedAssetReference(assetPath.toPacket(), timestamp != null ? timestamp.toString() : null));
            }
         }
      }

      editorClient.getPacketHandler().write(new AssetEditorExportComplete(exportedAssets.toArray(TimestampedAssetReference[]::new)));
   }

   public void handleSelectAsset(@Nonnull EditorClient editorClient, @Nullable AssetPath assetPath) {
      if (assetPath != null) {
         DataSource dataSource = this.getDataSourceForPath(assetPath);
         if (dataSource == null) {
            return;
         }
      }

      String assetType = null;
      String currentAssetType = null;
      AssetPath currentPath = this.clientOpenAssetPathMapping.get(editorClient);
      if (currentPath != null && !currentPath.equals(AssetPath.EMPTY_PATH)) {
         AssetTypeHandler currentAssetTypeHandler = this.assetTypeRegistry.tryGetAssetTypeHandler(currentPath.path(), editorClient, -1);
         if (currentAssetTypeHandler != null) {
            currentAssetType = currentAssetTypeHandler.getConfig().id;
         }
      }

      if (assetPath != null) {
         AssetTypeHandler assetTypeHandler = this.assetTypeRegistry.tryGetAssetTypeHandler(assetPath.path(), editorClient, -1);
         if (assetTypeHandler == null) {
            return;
         }

         assetType = assetTypeHandler.getConfig().id;
         this.clientOpenAssetPathMapping.put(editorClient, assetPath);
      } else {
         this.clientOpenAssetPathMapping.put(editorClient, AssetPath.EMPTY_PATH);
      }

      IEventDispatcher<AssetEditorSelectAssetEvent, AssetEditorSelectAssetEvent> dispatch = HytaleServer.get()
         .getEventBus()
         .dispatchFor(AssetEditorSelectAssetEvent.class);
      if (dispatch.hasListener()) {
         dispatch.dispatch(new AssetEditorSelectAssetEvent(editorClient, assetType, assetPath, currentAssetType, currentPath));
      }
   }

   public void handleFetchLastModifiedAssets(@Nonnull EditorClient editorClient) {
      long stamp = this.globalEditLock.readLock();

      try {
         AssetEditorLastModifiedAssets packet = this.buildAssetEditorLastModifiedAssetsPacket();
         editorClient.getPacketHandler().write(packet);
      } finally {
         this.globalEditLock.unlockRead(stamp);
      }
   }

   public void handleAssetUpdate(@Nonnull EditorClient editorClient, @Nonnull AssetPath assetPath, @Nonnull byte[] data, int requestToken) {
      DataSource dataSource = this.getDataSourceForPath(assetPath);
      if (dataSource == null) {
         editorClient.sendFailureReply(requestToken, Messages.UNKNOWN_ASSETPACK_MESSAGE);
      } else if (dataSource.isImmutable()) {
         editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.assetsReadOnly"));
      } else if (!this.isValidPath(dataSource, assetPath)) {
         editorClient.sendFailureReply(requestToken, Messages.OUTSIDE_ASSET_ROOT_MESSAGE);
      } else {
         AssetTypeHandler assetTypeHandler = this.assetTypeRegistry.tryGetAssetTypeHandler(assetPath.path(), editorClient, requestToken);
         if (assetTypeHandler != null) {
            long stamp = this.globalEditLock.writeLock();

            label79: {
               try {
                  if (!dataSource.doesAssetExist(assetPath.path())) {
                     this.getLogger().at(Level.WARNING).log("%s does not exist", assetPath);
                     editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.update.doesntExist"));
                     return;
                  }

                  if (!assetTypeHandler.isValidData(data)) {
                     this.getLogger().at(Level.WARNING).log("Failed to validate data for %s", assetPath);
                     editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.createAsset.failed"));
                     return;
                  }

                  if (dataSource.updateAsset(assetPath.path(), data, editorClient)) {
                     this.updateAssetForConnectedClients(assetPath, data, editorClient);
                     this.sendModifiedAssetsUpdateToConnectedUsers();
                     editorClient.sendSuccessReply(requestToken);
                     assetTypeHandler.loadAsset(assetPath, dataSource.getFullPathToAssetData(assetPath.path()), data, editorClient);
                     break label79;
                  }

                  this.getLogger().at(Level.WARNING).log("Failed to update asset %s in data source!", assetPath);
                  editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.update.failed"));
               } finally {
                  this.globalEditLock.unlockWrite(stamp);
               }

               return;
            }

            this.getLogger().at(Level.INFO).log("Updated asset at %s", assetPath);
         }
      }
   }

   public void handleJsonAssetUpdate(
      @Nonnull EditorClient editorClient,
      AssetPath assetPath,
      @Nonnull String assetType,
      int assetIndex,
      @Nonnull JsonUpdateCommand[] commands,
      int requestToken
   ) {
      AssetTypeHandler assetTypeHandler = this.assetTypeRegistry.getAssetTypeHandler(assetType);
      if (!(assetTypeHandler instanceof JsonTypeHandler)) {
         this.getLogger().at(Level.WARNING).log("Invalid asset type %s", assetType);
         editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.unknownAssetType").param("assetType", assetType));
      } else {
         DataSource dataSource;
         if (assetIndex > -1 && assetTypeHandler instanceof AssetStoreTypeHandler) {
            AssetStore assetStore = ((AssetStoreTypeHandler)assetTypeHandler).getAssetStore();
            AssetMap assetMap = assetStore.getAssetMap();
            String keyString = AssetStoreUtil.getIdFromIndex(assetStore, assetIndex);
            Object key = assetStore.decodeStringKey(keyString);
            Path storedPath = assetMap.getPath(key);
            String storedAssetPack = assetMap.getAssetPack(key);
            if (storedPath == null || storedAssetPack == null) {
               editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.unknownAssetIndex"));
               return;
            }

            dataSource = this.getDataSourceForPack(storedAssetPack);
            if (dataSource == null) {
               editorClient.sendFailureReply(requestToken, Messages.UNKNOWN_ASSETPACK_MESSAGE);
               return;
            }

            assetPath = new AssetPath(storedAssetPack, PathUtil.relativizePretty(dataSource.getRootPath(), storedPath));
         } else {
            dataSource = this.getDataSourceForPath(assetPath);
            if (dataSource == null) {
               editorClient.sendFailureReply(requestToken, Messages.UNKNOWN_ASSETPACK_MESSAGE);
               return;
            }
         }

         if (dataSource.isImmutable()) {
            editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.assetsReadOnly"));
         } else if (!this.isValidPath(dataSource, assetPath)) {
            editorClient.sendFailureReply(requestToken, Messages.OUTSIDE_ASSET_ROOT_MESSAGE);
         } else if (!assetPath.path().startsWith(assetTypeHandler.getRootPath())) {
            this.getLogger().at(Level.WARNING).log("%s is not within valid asset directory", assetPath);
            editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.directoryOutsideRoot"));
         } else {
            String fileExtension = PathUtil.getFileExtension(assetPath.path());
            if (!fileExtension.equalsIgnoreCase(assetTypeHandler.getConfig().fileExtension)) {
               this.getLogger()
                  .at(Level.WARNING)
                  .log("File extension not matching. Expected %s, got %s", assetTypeHandler.getConfig().fileExtension, fileExtension);
               this.getLogger()
                  .at(Level.WARNING)
                  .log("File extension not matching. Expected %s, got %s", assetTypeHandler.getConfig().fileExtension, fileExtension);
               editorClient.sendFailureReply(
                  requestToken,
                  Message.translation("server.assetEditor.messages.fileExtensionMismatch").param("fileExtension", assetTypeHandler.getConfig().fileExtension)
               );
            } else {
               long stamp = this.globalEditLock.writeLock();

               try {
                  byte[] bytes = dataSource.getAssetBytes(assetPath.path());
                  if (bytes == null) {
                     this.getLogger().at(Level.WARNING).log("%s does not exist", assetPath);
                     editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.update.doesntExist"));
                     return;
                  }

                  AssetUpdateQuery.RebuildCacheBuilder rebuildCacheBuilder = AssetUpdateQuery.RebuildCache.builder();

                  BsonDocument asset;
                  try {
                     asset = this.applyCommandsToAsset(bytes, assetPath, commands, rebuildCacheBuilder);
                     String json = BsonUtil.toJson(asset) + "\n";
                     bytes = json.getBytes(StandardCharsets.UTF_8);
                  } catch (Exception var23) {
                     this.getLogger().at(Level.WARNING).withCause(var23).log("Failed to apply commands to %s", assetPath);
                     editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.update.failed"));
                     return;
                  }

                  if (dataSource.updateAsset(assetPath.path(), bytes, editorClient)) {
                     AssetUndoRedoInfo undoRedo = this.undoRedoManager.getOrCreateUndoRedoStack(assetPath);
                     undoRedo.redoStack.clear();

                     for (JsonUpdateCommand command : commands) {
                        undoRedo.undoStack.push(command);
                     }

                     this.updateJsonAssetForConnectedClients(assetPath, commands, editorClient);
                     editorClient.sendSuccessReply(requestToken);
                     this.sendModifiedAssetsUpdateToConnectedUsers();
                     ((JsonTypeHandler)assetTypeHandler)
                        .loadAssetFromDocument(
                           assetPath,
                           dataSource.getFullPathToAssetData(assetPath.path()),
                           asset.clone(),
                           new AssetUpdateQuery(rebuildCacheBuilder.build()),
                           editorClient
                        );
                     return;
                  }

                  editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.update.failed"));
               } finally {
                  this.globalEditLock.unlockWrite(stamp);
               }
            }
         }
      }
   }

   public void handleUndo(@Nonnull EditorClient editorClient, @Nonnull AssetPath assetPath, int requestToken) {
      DataSource dataSource = this.getDataSourceForPath(assetPath);
      if (dataSource == null) {
         editorClient.sendFailureReply(requestToken, Messages.UNKNOWN_ASSETPACK_MESSAGE);
      } else if (dataSource.isImmutable()) {
         editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.assetsReadOnly"));
      } else if (!this.isValidPath(dataSource, assetPath)) {
         editorClient.sendFailureReply(requestToken, Messages.OUTSIDE_ASSET_ROOT_MESSAGE);
      } else {
         AssetTypeHandler assetTypeHandler = this.assetTypeRegistry.tryGetAssetTypeHandler(assetPath.path(), editorClient, requestToken);
         if (assetTypeHandler != null) {
            if (!(assetTypeHandler instanceof JsonTypeHandler)) {
               this.getLogger().at(Level.WARNING).log("Undo can only be applied to an instance of JsonTypeHandler");
               editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.invalidAssetType"));
            } else {
               long stamp = this.globalEditLock.writeLock();

               try {
                  AssetUndoRedoInfo undoRedo = this.undoRedoManager.getUndoRedoStack(assetPath);
                  if (undoRedo == null || undoRedo.undoStack.isEmpty()) {
                     this.getLogger().at(Level.INFO).log("Nothing to undo");
                     editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.undo.empty"));
                     return;
                  }

                  JsonUpdateCommand command = undoRedo.undoStack.peek();
                  JsonUpdateCommand undoCommand = new JsonUpdateCommand();
                  undoCommand.rebuildCaches = command.rebuildCaches;
                  if (command.firstCreatedProperty != null) {
                     undoCommand.type = JsonUpdateType.RemoveProperty;
                     undoCommand.path = command.firstCreatedProperty;
                  } else {
                     undoCommand.type = command.type == JsonUpdateType.RemoveProperty ? JsonUpdateType.InsertProperty : JsonUpdateType.SetProperty;
                     undoCommand.path = command.path;
                     undoCommand.value = command.previousValue;
                  }

                  byte[] bytes = dataSource.getAssetBytes(assetPath.path());
                  if (bytes == null) {
                     this.getLogger().at(Level.WARNING).log("%s does not exist", assetPath);
                     editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.update.doesntExist"));
                     return;
                  }

                  AssetUpdateQuery.RebuildCacheBuilder rebuildCacheBuilder = AssetUpdateQuery.RebuildCache.builder();

                  BsonDocument asset;
                  try {
                     asset = this.applyCommandsToAsset(bytes, assetPath, new JsonUpdateCommand[]{undoCommand}, rebuildCacheBuilder);
                     String json = BsonUtil.toJson(asset) + "\n";
                     bytes = json.getBytes(StandardCharsets.UTF_8);
                  } catch (Exception var18) {
                     this.getLogger().at(Level.WARNING).withCause(var18).log("Failed to undo for %s", assetPath);
                     editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.undo.failed"));
                     return;
                  }

                  if (dataSource.updateAsset(assetPath.path(), bytes, editorClient)) {
                     undoRedo.undoStack.poll();
                     undoRedo.redoStack.push(command);
                     this.updateJsonAssetForConnectedClients(assetPath, new JsonUpdateCommand[]{undoCommand}, editorClient);
                     editorClient.getPacketHandler().write(new AssetEditorUndoRedoReply(requestToken, undoCommand));
                     this.sendModifiedAssetsUpdateToConnectedUsers();
                     ((JsonTypeHandler)assetTypeHandler)
                        .loadAssetFromDocument(
                           assetPath,
                           dataSource.getFullPathToAssetData(assetPath.path()),
                           asset.clone(),
                           new AssetUpdateQuery(rebuildCacheBuilder.build()),
                           editorClient
                        );
                     return;
                  }

                  editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.undo.failed"));
               } finally {
                  this.globalEditLock.unlockWrite(stamp);
               }
            }
         }
      }
   }

   public void handleRedo(@Nonnull EditorClient editorClient, @Nonnull AssetPath assetPath, int requestToken) {
      DataSource dataSource = this.getDataSourceForPath(assetPath);
      if (dataSource == null) {
         editorClient.sendFailureReply(requestToken, Messages.UNKNOWN_ASSETPACK_MESSAGE);
      } else if (dataSource.isImmutable()) {
         editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.assetsReadOnly"));
      } else if (!this.isValidPath(dataSource, assetPath)) {
         editorClient.sendFailureReply(requestToken, Messages.OUTSIDE_ASSET_ROOT_MESSAGE);
      } else {
         AssetTypeHandler assetTypeHandler = this.assetTypeRegistry.tryGetAssetTypeHandler(assetPath.path(), editorClient, requestToken);
         if (assetTypeHandler != null) {
            if (!(assetTypeHandler instanceof JsonTypeHandler)) {
               this.getLogger().at(Level.WARNING).log("Redo can only be applied to an instance of JsonTypeHandler");
               editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.invalidAssetType"));
            } else {
               long stamp = this.globalEditLock.writeLock();

               try {
                  AssetUndoRedoInfo undoRedo = this.undoRedoManager.getUndoRedoStack(assetPath);
                  if (undoRedo == null || undoRedo.redoStack.isEmpty()) {
                     this.getLogger().at(Level.WARNING).log("Nothing to redo");
                     editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.redo.empty"));
                     return;
                  }

                  byte[] bytes = dataSource.getAssetBytes(assetPath.path());
                  if (bytes == null) {
                     this.getLogger().at(Level.WARNING).log("%s does not exist", assetPath);
                     editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.update.doesntExist"));
                     return;
                  }

                  JsonUpdateCommand command = undoRedo.redoStack.peek();
                  AssetUpdateQuery.RebuildCacheBuilder rebuildCacheBuilder = AssetUpdateQuery.RebuildCache.builder();

                  BsonDocument asset;
                  try {
                     asset = this.applyCommandsToAsset(bytes, assetPath, new JsonUpdateCommand[]{command}, rebuildCacheBuilder);
                     String json = BsonUtil.toJson(asset) + "\n";
                     bytes = json.getBytes(StandardCharsets.UTF_8);
                  } catch (Exception var17) {
                     this.getLogger().at(Level.WARNING).withCause(var17).log("Failed to redo for %s", assetPath);
                     editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.redo.failed"));
                     return;
                  }

                  if (dataSource.updateAsset(assetPath.path(), bytes, editorClient)) {
                     undoRedo.redoStack.poll();
                     undoRedo.undoStack.push(command);
                     this.updateJsonAssetForConnectedClients(assetPath, new JsonUpdateCommand[]{command}, editorClient);
                     editorClient.getPacketHandler().write(new AssetEditorUndoRedoReply(requestToken, command));
                     this.sendModifiedAssetsUpdateToConnectedUsers();
                     ((JsonTypeHandler)assetTypeHandler)
                        .loadAssetFromDocument(
                           assetPath,
                           dataSource.getFullPathToAssetData(assetPath.path()),
                           asset.clone(),
                           new AssetUpdateQuery(rebuildCacheBuilder.build()),
                           editorClient
                        );
                     return;
                  }

                  editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.redo.failed"));
               } finally {
                  this.globalEditLock.unlockWrite(stamp);
               }
            }
         }
      }
   }

   public void handleFetchAsset(@Nonnull EditorClient editorClient, @Nonnull AssetPath assetPath, int requestToken) {
      DataSource dataSource = this.getDataSourceForPath(assetPath);
      if (dataSource == null) {
         editorClient.sendFailureReply(requestToken, Messages.UNKNOWN_ASSETPACK_MESSAGE);
      } else if (!this.isValidPath(dataSource, assetPath)) {
         editorClient.sendFailureReply(requestToken, Messages.OUTSIDE_ASSET_ROOT_MESSAGE);
      } else if (this.assetTypeRegistry.tryGetAssetTypeHandler(assetPath.path(), editorClient, requestToken) != null) {
         long stamp = this.globalEditLock.readLock();

         try {
            if (!dataSource.doesAssetExist(assetPath.path())) {
               this.getLogger().at(Level.WARNING).log("%s is not a regular file", assetPath);
               editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.fetchAsset.doesntExist"));
               return;
            }

            byte[] asset = dataSource.getAssetBytes(assetPath.path());
            if (asset != null) {
               this.getLogger().at(Level.INFO).log("Got '%s'", assetPath);
               editorClient.getPacketHandler().write(new AssetEditorFetchAssetReply(requestToken, asset));
               return;
            }

            this.getLogger().at(Level.INFO).log("Failed to get '%s'", assetPath);
            editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.fetchAsset.failed"));
         } finally {
            this.globalEditLock.unlockRead(stamp);
         }
      }
   }

   public void handleFetchJsonAssetWithParents(@Nonnull EditorClient editorClient, @Nonnull AssetPath assetPath, boolean isFromOpenedTab, int requestToken) {
      DataSource dataSource = this.getDataSourceForPath(assetPath);
      if (dataSource == null) {
         editorClient.sendFailureReply(requestToken, Messages.UNKNOWN_ASSETPACK_MESSAGE);
      } else if (!this.isValidPath(dataSource, assetPath)) {
         editorClient.sendFailureReply(requestToken, Messages.OUTSIDE_ASSET_ROOT_MESSAGE);
      } else if (this.assetTypeRegistry.tryGetAssetTypeHandler(assetPath.path(), editorClient, requestToken) != null) {
         long stamp = this.globalEditLock.readLock();

         try {
            byte[] asset = dataSource.getAssetBytes(assetPath.path());
            if (asset != null) {
               this.getLogger().at(Level.INFO).log("Got '%s'", assetPath);
               BsonDocument bson = BsonDocument.parse(new String(asset, StandardCharsets.UTF_8));
               Object2ObjectOpenHashMap<com.hypixel.hytale.protocol.packets.asseteditor.AssetPath, String> assets = new Object2ObjectOpenHashMap<>();
               assets.put(assetPath.toPacket(), BsonUtil.translateBsonToJson(bson).getAsJsonObject().toString());
               editorClient.getPacketHandler().write(new AssetEditorFetchJsonAssetWithParentsReply(requestToken, assets));
               return;
            }

            this.getLogger().at(Level.INFO).log("Failed to get '%s'", assetPath);
            editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.fetchAsset.failed"));
         } finally {
            this.globalEditLock.unlockRead(stamp);
         }
      }
   }

   public void handleRequestChildIds(@Nonnull EditorClient editorClient, @Nonnull AssetPath assetPath) {
      DataSource dataSource = this.getDataSourceForPath(assetPath);
      if (dataSource == null) {
         editorClient.sendPopupNotification(AssetEditorPopupNotificationType.Error, Messages.UNKNOWN_ASSETPACK_MESSAGE);
      } else if (!this.isValidPath(dataSource, assetPath)) {
         editorClient.sendPopupNotification(AssetEditorPopupNotificationType.Error, Messages.OUTSIDE_ASSET_ROOT_MESSAGE);
      } else {
         AssetTypeHandler assetTypeHandler = this.assetTypeRegistry.getAssetTypeHandlerForPath(assetPath.path());
         if (!(assetTypeHandler instanceof AssetStoreTypeHandler)) {
            this.getLogger().at(Level.WARNING).log("Invalid asset type for %s", assetPath);
            editorClient.sendPopupNotification(
               AssetEditorPopupNotificationType.Error, Message.translation("server.assetEditor.messages.requestChildIds.assetTypeMissing")
            );
         } else {
            AssetStore assetStore = ((AssetStoreTypeHandler)assetTypeHandler).getAssetStore();
            Object key = assetStore.decodeFilePathKey(assetPath.path());
            Set children = assetStore.getAssetMap().getChildren(key);
            HashSet<String> childrenIds = new HashSet<>();
            if (children != null) {
               for (Object child : children) {
                  if (assetStore.getAssetMap().getPath(child) != null) {
                     childrenIds.add(child.toString());
                  }
               }
            }

            this.getLogger().at(Level.INFO).log("Children ids for '%s': %s", key.toString(), childrenIds);
            editorClient.getPacketHandler().write(new AssetEditorRequestChildrenListReply(assetPath.toPacket(), childrenIds.toArray(String[]::new)));
         }
      }
   }

   public void handleDeleteAsset(@Nonnull EditorClient editorClient, @Nonnull AssetPath assetPath, int requestToken) {
      DataSource dataSource = this.getDataSourceForPath(assetPath);
      if (dataSource == null) {
         editorClient.sendFailureReply(requestToken, Messages.UNKNOWN_ASSETPACK_MESSAGE);
      } else if (dataSource.isImmutable()) {
         editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.assetsReadOnly"));
      } else if (!this.isValidPath(dataSource, assetPath)) {
         editorClient.sendFailureReply(requestToken, Messages.OUTSIDE_ASSET_ROOT_MESSAGE);
      } else {
         AssetTypeHandler assetTypeHandler = this.assetTypeRegistry.tryGetAssetTypeHandler(assetPath.path(), editorClient, requestToken);
         if (assetTypeHandler != null) {
            long stamp = this.globalEditLock.writeLock();

            label68: {
               try {
                  if (!dataSource.doesAssetExist(assetPath.path())) {
                     this.getLogger().at(Level.WARNING).log("%s does not exist", assetPath);
                     editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.deleteAsset.alreadyDeleted"));
                     return;
                  }

                  if (dataSource.deleteAsset(assetPath.path(), editorClient)) {
                     this.undoRedoManager.clearUndoRedoStack(assetPath);
                     AssetEditorFileEntry entry = dataSource.getAssetTree().removeAsset(assetPath.path());
                     AssetEditorAssetListUpdate packet = new AssetEditorAssetListUpdate(assetPath.packId(), null, new AssetEditorFileEntry[]{entry});
                     editorClient.sendSuccessReply(requestToken);
                     this.sendPacketToAllEditorUsersExcept(packet, editorClient);
                     this.sendModifiedAssetsUpdateToConnectedUsers();
                     assetTypeHandler.unloadAsset(assetPath);
                     break label68;
                  }

                  this.getLogger().at(Level.WARNING).log("Failed to delete %s from data source", assetPath);
                  editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.failedToDeleteAsset"));
               } finally {
                  this.globalEditLock.unlockWrite(stamp);
               }

               return;
            }

            this.getLogger().at(Level.INFO).log("Deleted asset %s", assetPath);
         }
      }
   }

   public void handleSubscribeToModifiedAssetsChanges(EditorClient editorClient) {
      this.clientsSubscribedToModifiedAssetsChanges.add(editorClient);
   }

   public void handleUnsubscribeFromModifiedAssetsChanges(EditorClient editorClient) {
      this.clientsSubscribedToModifiedAssetsChanges.remove(editorClient);
   }

   public void handleRenameAsset(@Nonnull EditorClient editorClient, @Nonnull AssetPath oldAssetPath, @Nonnull AssetPath newAssetPath, int requestToken) {
      DataSource dataSource = this.getDataSourceForPath(oldAssetPath);
      if (dataSource == null) {
         editorClient.sendFailureReply(requestToken, Messages.UNKNOWN_ASSETPACK_MESSAGE);
      } else if (dataSource.isImmutable()) {
         editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.assetsReadOnly"));
      } else if (!this.isValidPath(dataSource, oldAssetPath)) {
         editorClient.sendFailureReply(requestToken, Messages.OUTSIDE_ASSET_ROOT_MESSAGE);
      } else if (!this.isValidPath(dataSource, newAssetPath)) {
         editorClient.sendFailureReply(requestToken, Messages.OUTSIDE_ASSET_ROOT_MESSAGE);
      } else {
         AssetTypeHandler assetTypeHandler = this.assetTypeRegistry.tryGetAssetTypeHandler(oldAssetPath.path(), editorClient, requestToken);
         if (assetTypeHandler != null) {
            String fileExtensionNew = PathUtil.getFileExtension(newAssetPath.path());
            if (!fileExtensionNew.equalsIgnoreCase(assetTypeHandler.getConfig().fileExtension)) {
               this.getLogger()
                  .at(Level.WARNING)
                  .log("File extension not matching. Expected %s, got %s", assetTypeHandler.getConfig().fileExtension, fileExtensionNew);
               editorClient.sendFailureReply(
                  requestToken,
                  Message.translation("server.assetEditor.messages.fileExtensionMismatch").param("fileExtension", assetTypeHandler.getConfig().fileExtension)
               );
            } else if (!newAssetPath.path().startsWith(assetTypeHandler.getRootPath())) {
               this.getLogger().at(Level.WARNING).log("%s is not within valid asset directory", newAssetPath);
               editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.directoryOutsideRoot"));
            } else {
               long stamp = this.globalEditLock.writeLock();

               try {
                  if (dataSource.doesAssetExist(newAssetPath.path())) {
                     this.getLogger().at(Level.WARNING).log("%s already exists", newAssetPath);
                     editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.renameAsset.alreadyExists"));
                     return;
                  }

                  byte[] oldAsset = dataSource.getAssetBytes(oldAssetPath.path());
                  if (oldAsset == null) {
                     this.getLogger().at(Level.WARNING).log("%s is not a regular file", oldAssetPath);
                     editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.renameAsset.doesntExist"));
                     return;
                  }

                  if (dataSource.moveAsset(oldAssetPath.path(), newAssetPath.path(), editorClient)) {
                     AssetUndoRedoInfo undoRedo = this.undoRedoManager.clearUndoRedoStack(oldAssetPath);
                     if (undoRedo != null) {
                        this.undoRedoManager.putUndoRedoStack(newAssetPath, undoRedo);
                     }

                     this.getLogger().at(Level.WARNING).log("Moved %s to %s", oldAssetPath, newAssetPath);
                     AssetEditorFileEntry oldEntry = dataSource.getAssetTree().removeAsset(oldAssetPath.path());
                     AssetEditorFileEntry newEntry = dataSource.getAssetTree().ensureAsset(newAssetPath.path(), false);
                     AssetEditorAssetListUpdate packet = new AssetEditorAssetListUpdate(
                        oldAssetPath.packId(), new AssetEditorFileEntry[]{newEntry}, new AssetEditorFileEntry[]{oldEntry}
                     );
                     this.sendPacketToAllEditorUsersExcept(packet, editorClient);
                     editorClient.sendSuccessReply(requestToken);
                     assetTypeHandler.unloadAsset(oldAssetPath);
                     assetTypeHandler.loadAsset(newAssetPath, dataSource.getFullPathToAssetData(newAssetPath.path()), oldAsset, editorClient);
                     return;
                  }

                  this.getLogger().at(Level.WARNING).log("Failed to move file %s to %s", oldAssetPath, newAssetPath);
                  editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.renameAsset.failed"));
               } finally {
                  this.globalEditLock.unlockWrite(stamp);
               }
            }
         }
      }
   }

   public void handleDeleteDirectory(@Nonnull EditorClient editorClient, @Nonnull AssetPath assetPath, int requestToken) {
      DataSource dataSource = this.getDataSourceForPath(assetPath);
      if (dataSource.isImmutable()) {
         editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.assetsReadOnly"));
      } else if (!this.isValidPath(dataSource, assetPath)) {
         editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.directoryOutsideRoot"));
      } else if (!this.getAssetTypeRegistry().isPathInAssetTypeFolder(assetPath.path())) {
         editorClient.sendFailureReply(requestToken, Messages.OUTSIDE_ASSET_ROOT_MESSAGE);
      } else {
         long stamp = this.globalEditLock.writeLock();

         try {
            if (!dataSource.doesDirectoryExist(assetPath.path())) {
               this.getLogger().at(Level.WARNING).log("Directory doesn't exist %s", assetPath);
               editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.createDirectory.alreadyExists"));
               return;
            }

            if (!dataSource.getAssetTree().isDirectoryEmpty(assetPath.path())) {
               this.getLogger().at(Level.WARNING).log("%s must be empty", assetPath);
               editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.deleteDirectory.notEmpty"));
               return;
            }

            if (dataSource.deleteDirectory(assetPath.path())) {
               AssetEditorFileEntry entry = dataSource.getAssetTree().removeAsset(assetPath.path());
               AssetEditorAssetListUpdate packet = new AssetEditorAssetListUpdate(assetPath.packId(), null, new AssetEditorFileEntry[]{entry});
               this.sendPacketToAllEditorUsersExcept(packet, editorClient);
               editorClient.sendSuccessReply(requestToken);
               this.getLogger().at(Level.INFO).log("Deleted directory %s", assetPath);
               return;
            }

            this.getLogger().at(Level.WARNING).log("Directory %s could not be deleted!", assetPath);
            editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.deleteDirectory.failed"));
         } finally {
            this.globalEditLock.unlockWrite(stamp);
         }
      }
   }

   public void handleRenameDirectory(@Nonnull EditorClient editorClient, AssetPath path, AssetPath newPath, int requestToken) {
      editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.renameDirectory.unsupported"));
   }

   public void handleCreateDirectory(@Nonnull EditorClient editorClient, @Nonnull AssetPath assetPath, int requestToken) {
      DataSource dataSource = this.getDataSourceForPath(assetPath);
      if (dataSource == null) {
         editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.createDirectory.noDataSource"));
      } else if (dataSource.isImmutable()) {
         editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.assetsReadOnly"));
      } else if (!this.isValidPath(dataSource, assetPath)) {
         editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.createDirectory.noPath"));
      } else {
         long stamp = this.globalEditLock.writeLock();

         try {
            if (dataSource.doesDirectoryExist(assetPath.path())) {
               this.getLogger().at(Level.WARNING).log("Directory already exists at %s", assetPath);
               editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.createDirectory.alreadyExists"));
               return;
            }

            Path parentDirectoryPath = assetPath.path().getParent();
            if (!dataSource.doesDirectoryExist(parentDirectoryPath)) {
               this.getLogger().at(Level.WARNING).log("Parent directory is missing for %s", assetPath);
               editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.parentDirectoryMissing"));
               return;
            }

            if (dataSource.createDirectory(assetPath.path(), editorClient)) {
               AssetEditorFileEntry entry = dataSource.getAssetTree().ensureAsset(assetPath.path(), true);
               if (entry != null) {
                  AssetEditorAssetListUpdate packet = new AssetEditorAssetListUpdate(assetPath.packId(), new AssetEditorFileEntry[]{entry}, null);
                  this.sendPacketToAllEditorUsersExcept(packet, editorClient);
               }

               editorClient.sendSuccessReply(requestToken);
               this.getLogger().at(Level.WARNING).log("Created directory %s", assetPath);
               return;
            }

            this.getLogger().at(Level.WARNING).log("Failed to create directory %s", assetPath);
            editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.failedToCreateDirectory"));
         } finally {
            this.globalEditLock.unlockWrite(stamp);
         }
      }
   }

   public void handleCreateAsset(
      @Nonnull EditorClient editorClient,
      @Nonnull AssetPath assetPath,
      @Nonnull byte[] data,
      @Nonnull AssetEditorRebuildCaches rebuildCaches,
      String buttonId,
      int requestToken
   ) {
      DataSource dataSource = this.getDataSourceForPath(assetPath);
      if (dataSource == null) {
         editorClient.sendFailureReply(requestToken, Messages.UNKNOWN_ASSETPACK_MESSAGE);
      } else if (dataSource.isImmutable()) {
         editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.assetsReadOnly"));
      } else if (!this.isValidPath(dataSource, assetPath)) {
         editorClient.sendFailureReply(requestToken, Messages.OUTSIDE_ASSET_ROOT_MESSAGE);
      } else {
         AssetTypeHandler assetTypeHandler = this.assetTypeRegistry.tryGetAssetTypeHandler(assetPath.path(), editorClient, requestToken);
         if (assetTypeHandler != null) {
            long stamp = this.globalEditLock.writeLock();

            try {
               if (dataSource.doesAssetExist(assetPath.path())) {
                  this.getLogger().at(Level.WARNING).log("%s already exists", assetPath);
                  editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.createAsset.idAlreadyExists"));
                  return;
               }

               if (!assetTypeHandler.isValidData(data)) {
                  this.getLogger().at(Level.WARNING).log("Failed to validate data for %s", assetPath);
                  editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.createAsset.failed"));
                  return;
               }

               if (dataSource.createAsset(assetPath.path(), data, editorClient)) {
                  this.getLogger().at(Level.INFO).log("Created asset %s", assetPath);
                  AssetEditorFileEntry entry = dataSource.getAssetTree().ensureAsset(assetPath.path(), false);
                  if (entry != null) {
                     AssetEditorAssetListUpdate updatePacket = new AssetEditorAssetListUpdate(assetPath.packId(), new AssetEditorFileEntry[]{entry}, null);
                     this.sendPacketToAllEditorUsersExcept(updatePacket, editorClient);
                  }

                  this.sendModifiedAssetsUpdateToConnectedUsers();
                  AssetUpdateQuery.RebuildCache rebuildCache = new AssetUpdateQuery.RebuildCache(
                     rebuildCaches.blockTextures,
                     rebuildCaches.models,
                     rebuildCaches.modelTextures,
                     rebuildCaches.mapGeometry,
                     rebuildCaches.itemIcons,
                     assetPath.path().startsWith(AssetPathUtil.PATH_DIR_COMMON)
                  );
                  assetTypeHandler.loadAsset(
                     assetPath, dataSource.getFullPathToAssetData(assetPath.path()), data, new AssetUpdateQuery(rebuildCache), editorClient
                  );
                  IEventDispatcher<AssetEditorAssetCreatedEvent, AssetEditorAssetCreatedEvent> dispatch = HytaleServer.get()
                     .getEventBus()
                     .dispatchFor(AssetEditorAssetCreatedEvent.class, assetTypeHandler.getConfig().id);
                  if (dispatch.hasListener()) {
                     dispatch.dispatch(new AssetEditorAssetCreatedEvent(editorClient, assetTypeHandler.getConfig().id, assetPath.path(), data, buttonId));
                  }

                  editorClient.sendSuccessReply(requestToken);
                  return;
               }

               this.getLogger().at(Level.WARNING).log("Failed to create asset %s", assetPath);
               editorClient.sendFailureReply(requestToken, Message.translation("server.assetEditor.messages.createAsset.failed"));
            } finally {
               this.globalEditLock.unlockWrite(stamp);
            }
         }
      }
   }

   private BsonDocument applyCommandsToAsset(
      @Nonnull byte[] bytes, AssetPath path, @Nonnull JsonUpdateCommand[] commands, @Nonnull AssetUpdateQuery.RebuildCacheBuilder rebuildCache
   ) {
      BsonDocument asset = BsonDocument.parse(new String(bytes, StandardCharsets.UTF_8));
      this.getLogger().at(Level.INFO).log("Applying commands to %s with %s", path, asset);

      for (JsonUpdateCommand command : commands) {
         switch (command.type) {
            case SetProperty: {
               BsonValue value = BsonDocument.parse(command.value).get("value");
               this.getLogger().at(Level.INFO).log("Setting property %s to %s", String.join(".", command.path), value);
               if (command.path.length > 0) {
                  BsonTransformationUtil.setProperty(asset, command.path, value);
               } else {
                  asset = (BsonDocument)value;
               }
               break;
            }
            case InsertProperty: {
               BsonValue value = BsonDocument.parse(command.value).get("value");
               this.getLogger().at(Level.INFO).log("Inserting property %s with %s", String.join(".", command.path), value);
               BsonTransformationUtil.insertProperty(asset, command.path, value);
               break;
            }
            case RemoveProperty:
               this.getLogger().at(Level.INFO).log("Removing property %s", String.join(".", command.path));
               BsonTransformationUtil.removeProperty(asset, command.path);
         }
      }

      this.getLogger().at(Level.INFO).log("Updated %s resulting: %s", path, asset);

      for (JsonUpdateCommand command : commands) {
         if (command.rebuildCaches != null) {
            if (command.rebuildCaches.blockTextures) {
               rebuildCache.setBlockTextures(true);
            }

            if (command.rebuildCaches.modelTextures) {
               rebuildCache.setModelTextures(true);
            }

            if (command.rebuildCaches.models) {
               rebuildCache.setModels(true);
            }

            if (command.rebuildCaches.mapGeometry) {
               rebuildCache.setMapGeometry(true);
            }

            if (command.rebuildCaches.itemIcons) {
               rebuildCache.setItemIcons(true);
            }
         }
      }

      return asset;
   }

   private void sendModifiedAssetsUpdateToConnectedUsers() {
      if (!this.clientOpenAssetPathMapping.isEmpty()) {
         if (!this.clientsSubscribedToModifiedAssetsChanges.isEmpty()) {
            AssetEditorLastModifiedAssets lastModifiedAssetsPacket = this.buildAssetEditorLastModifiedAssetsPacket();

            for (EditorClient p : this.clientsSubscribedToModifiedAssetsChanges) {
               p.getPacketHandler().write(lastModifiedAssetsPacket);
            }
         }
      }
   }

   private void sendPacketToAllEditorUsers(@Nonnull Packet packet) {
      for (EditorClient editorClient : this.clientOpenAssetPathMapping.keySet()) {
         editorClient.getPacketHandler().write(packet);
      }
   }

   private void sendPacketToAllEditorUsersExcept(@Nonnull Packet packet, EditorClient ignoreEditorClient) {
      for (EditorClient editorClient : this.clientOpenAssetPathMapping.keySet()) {
         if (!editorClient.equals(ignoreEditorClient)) {
            editorClient.getPacketHandler().write(packet);
         }
      }
   }

   private void updateAssetForConnectedClients(@Nonnull AssetPath assetPath) {
      this.updateAssetForConnectedClients(assetPath, null);
   }

   private void updateAssetForConnectedClients(@Nonnull AssetPath assetPath, EditorClient ignoreEditorClient) {
      DataSource dataSource = this.getDataSourceForPath(assetPath);
      byte[] bytes = dataSource.getAssetBytes(assetPath.path());
      this.updateAssetForConnectedClients(assetPath, bytes, ignoreEditorClient);
   }

   private void updateAssetForConnectedClients(@Nonnull AssetPath assetPath, byte[] bytes, EditorClient ignoreEditorClient) {
      AssetEditorAssetUpdated updatePacket = new AssetEditorAssetUpdated(assetPath.toPacket(), bytes);

      for (Entry<EditorClient, AssetPath> entry : this.clientOpenAssetPathMapping.entrySet()) {
         if (!entry.getKey().equals(ignoreEditorClient) && assetPath.equals(entry.getValue())) {
            entry.getKey().getPacketHandler().write(updatePacket);
         }
      }
   }

   private void updateJsonAssetForConnectedClients(@Nonnull AssetPath assetPath, JsonUpdateCommand[] commands) {
      this.updateJsonAssetForConnectedClients(assetPath, commands, null);
   }

   private void updateJsonAssetForConnectedClients(@Nonnull AssetPath assetPath, JsonUpdateCommand[] commands, EditorClient ignoreEditorClient) {
      AssetEditorJsonAssetUpdated updatePacket = new AssetEditorJsonAssetUpdated(assetPath.toPacket(), commands);

      for (Entry<EditorClient, AssetPath> connectedPlayer : this.clientOpenAssetPathMapping.entrySet()) {
         if (!connectedPlayer.getKey().equals(ignoreEditorClient) && assetPath.equals(connectedPlayer.getValue())) {
            connectedPlayer.getKey().getPacketHandler().write(updatePacket);
         }
      }
   }

   @Nonnull
   private AssetEditorLastModifiedAssets buildAssetEditorLastModifiedAssetsPacket() {
      ArrayList<AssetInfo> allAssets = new ArrayList<>();

      for (Entry<String, DataSource> dataSource : this.assetPackDataSources.entrySet()) {
         if (dataSource.getValue() instanceof StandardDataSource standardDataSource) {
            for (ModifiedAsset assetInfo : standardDataSource.getRecentlyModifiedAssets().values()) {
               allAssets.add(assetInfo.toAssetInfoPacket(dataSource.getKey()));
            }
         }
      }

      return new AssetEditorLastModifiedAssets(allAssets.toArray(new AssetInfo[0]));
   }

   boolean isValidPath(@Nonnull DataSource dataSource, @Nonnull AssetPath assetPath) {
      String assetPathString = PathUtil.toUnixPathString(assetPath.path());
      Path rootPath = dataSource.getRootPath();
      Path absolutePath = rootPath.resolve(assetPathString).toAbsolutePath().normalize();
      if (!absolutePath.startsWith(rootPath)) {
         return false;
      } else {
         String relativePath = PathUtil.toUnixPathString(rootPath.relativize(absolutePath));
         return relativePath.equals(assetPathString);
      }
   }

   static {
      new SchemaContext();
   }

   public static class AssetToDiscard {
      public final AssetPath path;
      @Nullable
      public final Instant lastModificationDate;

      public AssetToDiscard(AssetPath path, @Nullable String lastModificationDate) {
         this.path = path;
         if (lastModificationDate != null) {
            this.lastModificationDate = Instant.parse(lastModificationDate);
         } else {
            this.lastModificationDate = null;
         }
      }
   }

   static enum DiscardResult {
      FAILED,
      SUCCEEDED,
      SUCCEEDED_COMMON_ASSETS_CHANGED;
   }

   static enum InitState {
      NOT_INITIALIZED,
      INITIALIZING,
      INITIALIZED;
   }
}
