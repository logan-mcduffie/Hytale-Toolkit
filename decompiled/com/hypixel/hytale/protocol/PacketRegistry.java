package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorActivateButton;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorAssetListSetup;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorAssetListUpdate;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorAssetPackSetup;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorAssetUpdated;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorAuthorization;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorCapabilities;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorCreateAsset;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorCreateAssetPack;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorCreateDirectory;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorDeleteAsset;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorDeleteAssetPack;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorDeleteDirectory;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorDiscardChanges;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorEnableAssetPack;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorExportAssetFinalize;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorExportAssetInitialize;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorExportAssetPart;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorExportAssets;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorExportComplete;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorExportDeleteAssets;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorFetchAsset;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorFetchAssetReply;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorFetchAutoCompleteData;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorFetchAutoCompleteDataReply;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorFetchJsonAssetWithParents;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorFetchJsonAssetWithParentsReply;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorFetchLastModifiedAssets;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorInitialize;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorJsonAssetUpdated;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorLastModifiedAssets;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorModifiedAssetsCount;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorPopupNotification;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorRebuildCaches;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorRedoChanges;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorRenameAsset;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorRenameDirectory;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorRequestChildrenList;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorRequestChildrenListReply;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorRequestDataset;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorRequestDatasetReply;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorSelectAsset;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorSetGameTime;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorSetupAssetTypes;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorSetupSchemas;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorSubscribeModifiedAssetsChanges;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorUndoChanges;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorUndoRedoReply;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorUpdateAsset;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorUpdateAssetPack;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorUpdateJsonAsset;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorUpdateModelPreview;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorUpdateSecondsPerGameDay;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorUpdateWeatherPreviewLock;
import com.hypixel.hytale.protocol.packets.asseteditor.FailureReply;
import com.hypixel.hytale.protocol.packets.asseteditor.SuccessReply;
import com.hypixel.hytale.protocol.packets.assets.TrackOrUpdateObjective;
import com.hypixel.hytale.protocol.packets.assets.UntrackObjective;
import com.hypixel.hytale.protocol.packets.assets.UpdateAmbienceFX;
import com.hypixel.hytale.protocol.packets.assets.UpdateAudioCategories;
import com.hypixel.hytale.protocol.packets.assets.UpdateBlockBreakingDecals;
import com.hypixel.hytale.protocol.packets.assets.UpdateBlockGroups;
import com.hypixel.hytale.protocol.packets.assets.UpdateBlockHitboxes;
import com.hypixel.hytale.protocol.packets.assets.UpdateBlockParticleSets;
import com.hypixel.hytale.protocol.packets.assets.UpdateBlockSets;
import com.hypixel.hytale.protocol.packets.assets.UpdateBlockSoundSets;
import com.hypixel.hytale.protocol.packets.assets.UpdateBlockTypes;
import com.hypixel.hytale.protocol.packets.assets.UpdateCameraShake;
import com.hypixel.hytale.protocol.packets.assets.UpdateEntityEffects;
import com.hypixel.hytale.protocol.packets.assets.UpdateEntityStatTypes;
import com.hypixel.hytale.protocol.packets.assets.UpdateEntityUIComponents;
import com.hypixel.hytale.protocol.packets.assets.UpdateEnvironments;
import com.hypixel.hytale.protocol.packets.assets.UpdateEqualizerEffects;
import com.hypixel.hytale.protocol.packets.assets.UpdateFieldcraftCategories;
import com.hypixel.hytale.protocol.packets.assets.UpdateFluidFX;
import com.hypixel.hytale.protocol.packets.assets.UpdateFluids;
import com.hypixel.hytale.protocol.packets.assets.UpdateHitboxCollisionConfig;
import com.hypixel.hytale.protocol.packets.assets.UpdateInteractions;
import com.hypixel.hytale.protocol.packets.assets.UpdateItemCategories;
import com.hypixel.hytale.protocol.packets.assets.UpdateItemPlayerAnimations;
import com.hypixel.hytale.protocol.packets.assets.UpdateItemQualities;
import com.hypixel.hytale.protocol.packets.assets.UpdateItemReticles;
import com.hypixel.hytale.protocol.packets.assets.UpdateItemSoundSets;
import com.hypixel.hytale.protocol.packets.assets.UpdateItems;
import com.hypixel.hytale.protocol.packets.assets.UpdateModelvfxs;
import com.hypixel.hytale.protocol.packets.assets.UpdateObjectiveTask;
import com.hypixel.hytale.protocol.packets.assets.UpdateParticleSpawners;
import com.hypixel.hytale.protocol.packets.assets.UpdateParticleSystems;
import com.hypixel.hytale.protocol.packets.assets.UpdateProjectileConfigs;
import com.hypixel.hytale.protocol.packets.assets.UpdateRecipes;
import com.hypixel.hytale.protocol.packets.assets.UpdateRepulsionConfig;
import com.hypixel.hytale.protocol.packets.assets.UpdateResourceTypes;
import com.hypixel.hytale.protocol.packets.assets.UpdateReverbEffects;
import com.hypixel.hytale.protocol.packets.assets.UpdateRootInteractions;
import com.hypixel.hytale.protocol.packets.assets.UpdateSoundEvents;
import com.hypixel.hytale.protocol.packets.assets.UpdateSoundSets;
import com.hypixel.hytale.protocol.packets.assets.UpdateTagPatterns;
import com.hypixel.hytale.protocol.packets.assets.UpdateTrails;
import com.hypixel.hytale.protocol.packets.assets.UpdateTranslations;
import com.hypixel.hytale.protocol.packets.assets.UpdateUnarmedInteractions;
import com.hypixel.hytale.protocol.packets.assets.UpdateViewBobbing;
import com.hypixel.hytale.protocol.packets.assets.UpdateWeathers;
import com.hypixel.hytale.protocol.packets.auth.AuthGrant;
import com.hypixel.hytale.protocol.packets.auth.AuthToken;
import com.hypixel.hytale.protocol.packets.auth.ClientReferral;
import com.hypixel.hytale.protocol.packets.auth.ConnectAccept;
import com.hypixel.hytale.protocol.packets.auth.PasswordAccepted;
import com.hypixel.hytale.protocol.packets.auth.PasswordRejected;
import com.hypixel.hytale.protocol.packets.auth.PasswordResponse;
import com.hypixel.hytale.protocol.packets.auth.ServerAuthToken;
import com.hypixel.hytale.protocol.packets.auth.Status;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolArgUpdate;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolEntityAction;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolExtrudeAction;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolGeneralAction;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolHideAnchors;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolLaserPointer;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolLineAction;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolOnUseInteraction;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolPasteClipboard;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolRotateClipboard;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolSelectionToolAskForClipboard;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolSelectionToolReplyWithClipboard;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolSelectionTransform;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolSelectionUpdate;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolSetEntityLight;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolSetEntityPickupEnabled;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolSetEntityScale;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolSetEntityTransform;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolSetNPCDebug;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolSetTransformationModeState;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolShowAnchor;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolStackArea;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolsSetSoundSet;
import com.hypixel.hytale.protocol.packets.buildertools.PrefabUnselectPrefab;
import com.hypixel.hytale.protocol.packets.camera.CameraShakeEffect;
import com.hypixel.hytale.protocol.packets.camera.RequestFlyCameraMode;
import com.hypixel.hytale.protocol.packets.camera.SetFlyCameraMode;
import com.hypixel.hytale.protocol.packets.camera.SetServerCamera;
import com.hypixel.hytale.protocol.packets.connection.Connect;
import com.hypixel.hytale.protocol.packets.connection.Disconnect;
import com.hypixel.hytale.protocol.packets.connection.Ping;
import com.hypixel.hytale.protocol.packets.connection.Pong;
import com.hypixel.hytale.protocol.packets.entities.ApplyKnockback;
import com.hypixel.hytale.protocol.packets.entities.ChangeVelocity;
import com.hypixel.hytale.protocol.packets.entities.EntityUpdates;
import com.hypixel.hytale.protocol.packets.entities.MountMovement;
import com.hypixel.hytale.protocol.packets.entities.PlayAnimation;
import com.hypixel.hytale.protocol.packets.entities.SetEntitySeed;
import com.hypixel.hytale.protocol.packets.entities.SpawnModelParticles;
import com.hypixel.hytale.protocol.packets.interaction.CancelInteractionChain;
import com.hypixel.hytale.protocol.packets.interaction.DismountNPC;
import com.hypixel.hytale.protocol.packets.interaction.MountNPC;
import com.hypixel.hytale.protocol.packets.interaction.PlayInteractionFor;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.protocol.packets.interface_.AddToServerPlayerList;
import com.hypixel.hytale.protocol.packets.interface_.ChatMessage;
import com.hypixel.hytale.protocol.packets.interface_.CustomHud;
import com.hypixel.hytale.protocol.packets.interface_.CustomPage;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageEvent;
import com.hypixel.hytale.protocol.packets.interface_.EditorBlocksChange;
import com.hypixel.hytale.protocol.packets.interface_.HideEventTitle;
import com.hypixel.hytale.protocol.packets.interface_.KillFeedMessage;
import com.hypixel.hytale.protocol.packets.interface_.Notification;
import com.hypixel.hytale.protocol.packets.interface_.OpenChatWithCommand;
import com.hypixel.hytale.protocol.packets.interface_.RemoveFromServerPlayerList;
import com.hypixel.hytale.protocol.packets.interface_.ResetUserInterfaceState;
import com.hypixel.hytale.protocol.packets.interface_.ServerInfo;
import com.hypixel.hytale.protocol.packets.interface_.ServerMessage;
import com.hypixel.hytale.protocol.packets.interface_.SetPage;
import com.hypixel.hytale.protocol.packets.interface_.ShowEventTitle;
import com.hypixel.hytale.protocol.packets.interface_.UpdateKnownRecipes;
import com.hypixel.hytale.protocol.packets.interface_.UpdateLanguage;
import com.hypixel.hytale.protocol.packets.interface_.UpdatePortal;
import com.hypixel.hytale.protocol.packets.interface_.UpdateServerPlayerList;
import com.hypixel.hytale.protocol.packets.interface_.UpdateServerPlayerListPing;
import com.hypixel.hytale.protocol.packets.interface_.UpdateVisibleHudComponents;
import com.hypixel.hytale.protocol.packets.interface_.WorldSavingStatus;
import com.hypixel.hytale.protocol.packets.inventory.DropCreativeItem;
import com.hypixel.hytale.protocol.packets.inventory.DropItemStack;
import com.hypixel.hytale.protocol.packets.inventory.InventoryAction;
import com.hypixel.hytale.protocol.packets.inventory.MoveItemStack;
import com.hypixel.hytale.protocol.packets.inventory.SetActiveSlot;
import com.hypixel.hytale.protocol.packets.inventory.SetCreativeItem;
import com.hypixel.hytale.protocol.packets.inventory.SmartGiveCreativeItem;
import com.hypixel.hytale.protocol.packets.inventory.SmartMoveItemStack;
import com.hypixel.hytale.protocol.packets.inventory.SwitchHotbarBlockSet;
import com.hypixel.hytale.protocol.packets.inventory.UpdatePlayerInventory;
import com.hypixel.hytale.protocol.packets.machinima.RequestMachinimaActorModel;
import com.hypixel.hytale.protocol.packets.machinima.SetMachinimaActorModel;
import com.hypixel.hytale.protocol.packets.machinima.UpdateMachinimaScene;
import com.hypixel.hytale.protocol.packets.player.ClearDebugShapes;
import com.hypixel.hytale.protocol.packets.player.ClientMovement;
import com.hypixel.hytale.protocol.packets.player.ClientPlaceBlock;
import com.hypixel.hytale.protocol.packets.player.ClientReady;
import com.hypixel.hytale.protocol.packets.player.ClientTeleport;
import com.hypixel.hytale.protocol.packets.player.DamageInfo;
import com.hypixel.hytale.protocol.packets.player.DisplayDebug;
import com.hypixel.hytale.protocol.packets.player.JoinWorld;
import com.hypixel.hytale.protocol.packets.player.LoadHotbar;
import com.hypixel.hytale.protocol.packets.player.MouseInteraction;
import com.hypixel.hytale.protocol.packets.player.RemoveMapMarker;
import com.hypixel.hytale.protocol.packets.player.ReticleEvent;
import com.hypixel.hytale.protocol.packets.player.SaveHotbar;
import com.hypixel.hytale.protocol.packets.player.SetBlockPlacementOverride;
import com.hypixel.hytale.protocol.packets.player.SetClientId;
import com.hypixel.hytale.protocol.packets.player.SetGameMode;
import com.hypixel.hytale.protocol.packets.player.SetMovementStates;
import com.hypixel.hytale.protocol.packets.player.SyncPlayerPreferences;
import com.hypixel.hytale.protocol.packets.player.UpdateMemoriesFeatureStatus;
import com.hypixel.hytale.protocol.packets.player.UpdateMovementSettings;
import com.hypixel.hytale.protocol.packets.serveraccess.RequestServerAccess;
import com.hypixel.hytale.protocol.packets.serveraccess.SetServerAccess;
import com.hypixel.hytale.protocol.packets.serveraccess.UpdateServerAccess;
import com.hypixel.hytale.protocol.packets.setup.AssetFinalize;
import com.hypixel.hytale.protocol.packets.setup.AssetInitialize;
import com.hypixel.hytale.protocol.packets.setup.AssetPart;
import com.hypixel.hytale.protocol.packets.setup.PlayerOptions;
import com.hypixel.hytale.protocol.packets.setup.RemoveAssets;
import com.hypixel.hytale.protocol.packets.setup.RequestAssets;
import com.hypixel.hytale.protocol.packets.setup.RequestCommonAssetsRebuild;
import com.hypixel.hytale.protocol.packets.setup.ServerTags;
import com.hypixel.hytale.protocol.packets.setup.SetTimeDilation;
import com.hypixel.hytale.protocol.packets.setup.SetUpdateRate;
import com.hypixel.hytale.protocol.packets.setup.UpdateFeatures;
import com.hypixel.hytale.protocol.packets.setup.ViewRadius;
import com.hypixel.hytale.protocol.packets.setup.WorldLoadFinished;
import com.hypixel.hytale.protocol.packets.setup.WorldLoadProgress;
import com.hypixel.hytale.protocol.packets.setup.WorldSettings;
import com.hypixel.hytale.protocol.packets.window.ClientOpenWindow;
import com.hypixel.hytale.protocol.packets.window.CloseWindow;
import com.hypixel.hytale.protocol.packets.window.OpenWindow;
import com.hypixel.hytale.protocol.packets.window.SendWindowAction;
import com.hypixel.hytale.protocol.packets.window.UpdateWindow;
import com.hypixel.hytale.protocol.packets.world.ClearEditorTimeOverride;
import com.hypixel.hytale.protocol.packets.world.PlaySoundEvent2D;
import com.hypixel.hytale.protocol.packets.world.PlaySoundEvent3D;
import com.hypixel.hytale.protocol.packets.world.PlaySoundEventEntity;
import com.hypixel.hytale.protocol.packets.world.ServerSetBlock;
import com.hypixel.hytale.protocol.packets.world.ServerSetBlocks;
import com.hypixel.hytale.protocol.packets.world.ServerSetFluid;
import com.hypixel.hytale.protocol.packets.world.ServerSetFluids;
import com.hypixel.hytale.protocol.packets.world.ServerSetPaused;
import com.hypixel.hytale.protocol.packets.world.SetChunk;
import com.hypixel.hytale.protocol.packets.world.SetChunkEnvironments;
import com.hypixel.hytale.protocol.packets.world.SetChunkHeightmap;
import com.hypixel.hytale.protocol.packets.world.SetChunkTintmap;
import com.hypixel.hytale.protocol.packets.world.SetFluids;
import com.hypixel.hytale.protocol.packets.world.SetPaused;
import com.hypixel.hytale.protocol.packets.world.SpawnBlockParticleSystem;
import com.hypixel.hytale.protocol.packets.world.SpawnParticleSystem;
import com.hypixel.hytale.protocol.packets.world.UnloadChunk;
import com.hypixel.hytale.protocol.packets.world.UpdateBlockDamage;
import com.hypixel.hytale.protocol.packets.world.UpdateEditorTimeOverride;
import com.hypixel.hytale.protocol.packets.world.UpdateEditorWeatherOverride;
import com.hypixel.hytale.protocol.packets.world.UpdateEnvironmentMusic;
import com.hypixel.hytale.protocol.packets.world.UpdatePostFxSettings;
import com.hypixel.hytale.protocol.packets.world.UpdateSleepState;
import com.hypixel.hytale.protocol.packets.world.UpdateSunSettings;
import com.hypixel.hytale.protocol.packets.world.UpdateTime;
import com.hypixel.hytale.protocol.packets.world.UpdateTimeSettings;
import com.hypixel.hytale.protocol.packets.world.UpdateWeather;
import com.hypixel.hytale.protocol.packets.worldmap.ClearWorldMap;
import com.hypixel.hytale.protocol.packets.worldmap.TeleportToWorldMapMarker;
import com.hypixel.hytale.protocol.packets.worldmap.TeleportToWorldMapPosition;
import com.hypixel.hytale.protocol.packets.worldmap.UpdateWorldMap;
import com.hypixel.hytale.protocol.packets.worldmap.UpdateWorldMapSettings;
import com.hypixel.hytale.protocol.packets.worldmap.UpdateWorldMapVisible;
import io.netty.buffer.ByteBuf;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PacketRegistry {
   private static final Map<Integer, PacketRegistry.PacketInfo> BY_ID = new HashMap<>();
   private static final Map<Integer, PacketRegistry.PacketInfo> BY_ID_UNMODIFIABLE = Collections.unmodifiableMap(BY_ID);
   private static final Map<Class<? extends Packet>, Integer> BY_TYPE = new HashMap<>();

   private PacketRegistry() {
   }

   private static void register(
      int id,
      String name,
      Class<? extends Packet> type,
      int fixedBlockSize,
      int maxSize,
      boolean compressed,
      BiFunction<ByteBuf, Integer, ValidationResult> validate,
      BiFunction<ByteBuf, Integer, Packet> deserialize
   ) {
      PacketRegistry.PacketInfo existing = BY_ID.get(id);
      if (existing != null) {
         throw new IllegalStateException("Duplicate packet ID " + id + ": '" + name + "' conflicts with '" + existing.name() + "'");
      } else {
         PacketRegistry.PacketInfo info = new PacketRegistry.PacketInfo(id, name, type, fixedBlockSize, maxSize, compressed, validate, deserialize);
         BY_ID.put(id, info);
         BY_TYPE.put(type, id);
      }
   }

   @Nullable
   public static PacketRegistry.PacketInfo getById(int id) {
      return BY_ID.get(id);
   }

   @Nullable
   public static Integer getId(Class<? extends Packet> type) {
      return BY_TYPE.get(type);
   }

   @Nonnull
   public static Map<Integer, PacketRegistry.PacketInfo> all() {
      return BY_ID_UNMODIFIABLE;
   }

   static {
      register(0, "Connect", Connect.class, 82, 38161, false, Connect::validateStructure, Connect::deserialize);
      register(1, "Disconnect", Disconnect.class, 2, 16384007, false, Disconnect::validateStructure, Disconnect::deserialize);
      register(2, "Ping", Ping.class, 29, 29, false, Ping::validateStructure, Ping::deserialize);
      register(3, "Pong", Pong.class, 20, 20, false, Pong::validateStructure, Pong::deserialize);
      register(10, "Status", Status.class, 9, 2587, false, Status::validateStructure, Status::deserialize);
      register(11, "AuthGrant", AuthGrant.class, 1, 49171, false, AuthGrant::validateStructure, AuthGrant::deserialize);
      register(12, "AuthToken", AuthToken.class, 1, 49171, false, AuthToken::validateStructure, AuthToken::deserialize);
      register(13, "ServerAuthToken", ServerAuthToken.class, 1, 32851, false, ServerAuthToken::validateStructure, ServerAuthToken::deserialize);
      register(14, "ConnectAccept", ConnectAccept.class, 1, 70, false, ConnectAccept::validateStructure, ConnectAccept::deserialize);
      register(15, "PasswordResponse", PasswordResponse.class, 1, 70, false, PasswordResponse::validateStructure, PasswordResponse::deserialize);
      register(16, "PasswordAccepted", PasswordAccepted.class, 0, 0, false, PasswordAccepted::validateStructure, PasswordAccepted::deserialize);
      register(17, "PasswordRejected", PasswordRejected.class, 5, 74, false, PasswordRejected::validateStructure, PasswordRejected::deserialize);
      register(18, "ClientReferral", ClientReferral.class, 1, 5141, false, ClientReferral::validateStructure, ClientReferral::deserialize);
      register(20, "WorldSettings", WorldSettings.class, 5, 1677721600, true, WorldSettings::validateStructure, WorldSettings::deserialize);
      register(21, "WorldLoadProgress", WorldLoadProgress.class, 9, 16384014, false, WorldLoadProgress::validateStructure, WorldLoadProgress::deserialize);
      register(22, "WorldLoadFinished", WorldLoadFinished.class, 0, 0, false, WorldLoadFinished::validateStructure, WorldLoadFinished::deserialize);
      register(23, "RequestAssets", RequestAssets.class, 1, 1677721600, true, RequestAssets::validateStructure, RequestAssets::deserialize);
      register(24, "AssetInitialize", AssetInitialize.class, 4, 2121, false, AssetInitialize::validateStructure, AssetInitialize::deserialize);
      register(25, "AssetPart", AssetPart.class, 1, 4096006, true, AssetPart::validateStructure, AssetPart::deserialize);
      register(26, "AssetFinalize", AssetFinalize.class, 0, 0, false, AssetFinalize::validateStructure, AssetFinalize::deserialize);
      register(27, "RemoveAssets", RemoveAssets.class, 1, 1677721600, false, RemoveAssets::validateStructure, RemoveAssets::deserialize);
      register(
         28,
         "RequestCommonAssetsRebuild",
         RequestCommonAssetsRebuild.class,
         0,
         0,
         false,
         RequestCommonAssetsRebuild::validateStructure,
         RequestCommonAssetsRebuild::deserialize
      );
      register(29, "SetUpdateRate", SetUpdateRate.class, 4, 4, false, SetUpdateRate::validateStructure, SetUpdateRate::deserialize);
      register(30, "SetTimeDilation", SetTimeDilation.class, 4, 4, false, SetTimeDilation::validateStructure, SetTimeDilation::deserialize);
      register(31, "UpdateFeatures", UpdateFeatures.class, 1, 8192006, false, UpdateFeatures::validateStructure, UpdateFeatures::deserialize);
      register(32, "ViewRadius", ViewRadius.class, 4, 4, false, ViewRadius::validateStructure, ViewRadius::deserialize);
      register(33, "PlayerOptions", PlayerOptions.class, 1, 327680184, false, PlayerOptions::validateStructure, PlayerOptions::deserialize);
      register(34, "ServerTags", ServerTags.class, 1, 1677721600, false, ServerTags::validateStructure, ServerTags::deserialize);
      register(40, "UpdateBlockTypes", UpdateBlockTypes.class, 10, 1677721600, true, UpdateBlockTypes::validateStructure, UpdateBlockTypes::deserialize);
      register(
         41, "UpdateBlockHitboxes", UpdateBlockHitboxes.class, 6, 1677721600, true, UpdateBlockHitboxes::validateStructure, UpdateBlockHitboxes::deserialize
      );
      register(
         42,
         "UpdateBlockSoundSets",
         UpdateBlockSoundSets.class,
         6,
         1677721600,
         true,
         UpdateBlockSoundSets::validateStructure,
         UpdateBlockSoundSets::deserialize
      );
      register(
         43, "UpdateItemSoundSets", UpdateItemSoundSets.class, 6, 1677721600, true, UpdateItemSoundSets::validateStructure, UpdateItemSoundSets::deserialize
      );
      register(
         44,
         "UpdateBlockParticleSets",
         UpdateBlockParticleSets.class,
         2,
         1677721600,
         true,
         UpdateBlockParticleSets::validateStructure,
         UpdateBlockParticleSets::deserialize
      );
      register(
         45,
         "UpdateBlockBreakingDecals",
         UpdateBlockBreakingDecals.class,
         2,
         1677721600,
         true,
         UpdateBlockBreakingDecals::validateStructure,
         UpdateBlockBreakingDecals::deserialize
      );
      register(46, "UpdateBlockSets", UpdateBlockSets.class, 2, 1677721600, true, UpdateBlockSets::validateStructure, UpdateBlockSets::deserialize);
      register(47, "UpdateWeathers", UpdateWeathers.class, 6, 1677721600, true, UpdateWeathers::validateStructure, UpdateWeathers::deserialize);
      register(48, "UpdateTrails", UpdateTrails.class, 2, 1677721600, true, UpdateTrails::validateStructure, UpdateTrails::deserialize);
      register(
         49,
         "UpdateParticleSystems",
         UpdateParticleSystems.class,
         2,
         1677721600,
         true,
         UpdateParticleSystems::validateStructure,
         UpdateParticleSystems::deserialize
      );
      register(
         50,
         "UpdateParticleSpawners",
         UpdateParticleSpawners.class,
         2,
         1677721600,
         true,
         UpdateParticleSpawners::validateStructure,
         UpdateParticleSpawners::deserialize
      );
      register(
         51, "UpdateEntityEffects", UpdateEntityEffects.class, 6, 1677721600, true, UpdateEntityEffects::validateStructure, UpdateEntityEffects::deserialize
      );
      register(
         52,
         "UpdateItemPlayerAnimations",
         UpdateItemPlayerAnimations.class,
         2,
         1677721600,
         true,
         UpdateItemPlayerAnimations::validateStructure,
         UpdateItemPlayerAnimations::deserialize
      );
      register(53, "UpdateModelvfxs", UpdateModelvfxs.class, 6, 1677721600, true, UpdateModelvfxs::validateStructure, UpdateModelvfxs::deserialize);
      register(54, "UpdateItems", UpdateItems.class, 4, 1677721600, true, UpdateItems::validateStructure, UpdateItems::deserialize);
      register(
         55, "UpdateItemQualities", UpdateItemQualities.class, 6, 1677721600, true, UpdateItemQualities::validateStructure, UpdateItemQualities::deserialize
      );
      register(
         56,
         "UpdateItemCategories",
         UpdateItemCategories.class,
         2,
         1677721600,
         true,
         UpdateItemCategories::validateStructure,
         UpdateItemCategories::deserialize
      );
      register(57, "UpdateItemReticles", UpdateItemReticles.class, 6, 1677721600, true, UpdateItemReticles::validateStructure, UpdateItemReticles::deserialize);
      register(
         58,
         "UpdateFieldcraftCategories",
         UpdateFieldcraftCategories.class,
         2,
         1677721600,
         true,
         UpdateFieldcraftCategories::validateStructure,
         UpdateFieldcraftCategories::deserialize
      );
      register(
         59, "UpdateResourceTypes", UpdateResourceTypes.class, 2, 1677721600, true, UpdateResourceTypes::validateStructure, UpdateResourceTypes::deserialize
      );
      register(60, "UpdateRecipes", UpdateRecipes.class, 2, 1677721600, true, UpdateRecipes::validateStructure, UpdateRecipes::deserialize);
      register(61, "UpdateEnvironments", UpdateEnvironments.class, 7, 1677721600, true, UpdateEnvironments::validateStructure, UpdateEnvironments::deserialize);
      register(62, "UpdateAmbienceFX", UpdateAmbienceFX.class, 6, 1677721600, true, UpdateAmbienceFX::validateStructure, UpdateAmbienceFX::deserialize);
      register(63, "UpdateFluidFX", UpdateFluidFX.class, 6, 1677721600, true, UpdateFluidFX::validateStructure, UpdateFluidFX::deserialize);
      register(64, "UpdateTranslations", UpdateTranslations.class, 2, 1677721600, true, UpdateTranslations::validateStructure, UpdateTranslations::deserialize);
      register(65, "UpdateSoundEvents", UpdateSoundEvents.class, 6, 1677721600, true, UpdateSoundEvents::validateStructure, UpdateSoundEvents::deserialize);
      register(66, "UpdateInteractions", UpdateInteractions.class, 6, 1677721600, true, UpdateInteractions::validateStructure, UpdateInteractions::deserialize);
      register(
         67,
         "UpdateRootInteractions",
         UpdateRootInteractions.class,
         6,
         1677721600,
         true,
         UpdateRootInteractions::validateStructure,
         UpdateRootInteractions::deserialize
      );
      register(
         68,
         "UpdateUnarmedInteractions",
         UpdateUnarmedInteractions.class,
         2,
         20480007,
         true,
         UpdateUnarmedInteractions::validateStructure,
         UpdateUnarmedInteractions::deserialize
      );
      register(
         69,
         "TrackOrUpdateObjective",
         TrackOrUpdateObjective.class,
         1,
         1677721600,
         false,
         TrackOrUpdateObjective::validateStructure,
         TrackOrUpdateObjective::deserialize
      );
      register(70, "UntrackObjective", UntrackObjective.class, 16, 16, false, UntrackObjective::validateStructure, UntrackObjective::deserialize);
      register(
         71, "UpdateObjectiveTask", UpdateObjectiveTask.class, 21, 16384035, false, UpdateObjectiveTask::validateStructure, UpdateObjectiveTask::deserialize
      );
      register(
         72,
         "UpdateEntityStatTypes",
         UpdateEntityStatTypes.class,
         6,
         1677721600,
         true,
         UpdateEntityStatTypes::validateStructure,
         UpdateEntityStatTypes::deserialize
      );
      register(
         73,
         "UpdateEntityUIComponents",
         UpdateEntityUIComponents.class,
         6,
         1677721600,
         true,
         UpdateEntityUIComponents::validateStructure,
         UpdateEntityUIComponents::deserialize
      );
      register(
         74,
         "UpdateHitboxCollisionConfig",
         UpdateHitboxCollisionConfig.class,
         6,
         36864011,
         true,
         UpdateHitboxCollisionConfig::validateStructure,
         UpdateHitboxCollisionConfig::deserialize
      );
      register(
         75,
         "UpdateRepulsionConfig",
         UpdateRepulsionConfig.class,
         6,
         65536011,
         true,
         UpdateRepulsionConfig::validateStructure,
         UpdateRepulsionConfig::deserialize
      );
      register(76, "UpdateViewBobbing", UpdateViewBobbing.class, 2, 1677721600, true, UpdateViewBobbing::validateStructure, UpdateViewBobbing::deserialize);
      register(77, "UpdateCameraShake", UpdateCameraShake.class, 2, 1677721600, true, UpdateCameraShake::validateStructure, UpdateCameraShake::deserialize);
      register(78, "UpdateBlockGroups", UpdateBlockGroups.class, 2, 1677721600, true, UpdateBlockGroups::validateStructure, UpdateBlockGroups::deserialize);
      register(79, "UpdateSoundSets", UpdateSoundSets.class, 6, 1677721600, true, UpdateSoundSets::validateStructure, UpdateSoundSets::deserialize);
      register(
         80,
         "UpdateAudioCategories",
         UpdateAudioCategories.class,
         6,
         1677721600,
         true,
         UpdateAudioCategories::validateStructure,
         UpdateAudioCategories::deserialize
      );
      register(
         81, "UpdateReverbEffects", UpdateReverbEffects.class, 6, 1677721600, true, UpdateReverbEffects::validateStructure, UpdateReverbEffects::deserialize
      );
      register(
         82,
         "UpdateEqualizerEffects",
         UpdateEqualizerEffects.class,
         6,
         1677721600,
         true,
         UpdateEqualizerEffects::validateStructure,
         UpdateEqualizerEffects::deserialize
      );
      register(83, "UpdateFluids", UpdateFluids.class, 6, 1677721600, true, UpdateFluids::validateStructure, UpdateFluids::deserialize);
      register(84, "UpdateTagPatterns", UpdateTagPatterns.class, 6, 1677721600, true, UpdateTagPatterns::validateStructure, UpdateTagPatterns::deserialize);
      register(
         85,
         "UpdateProjectileConfigs",
         UpdateProjectileConfigs.class,
         2,
         1677721600,
         true,
         UpdateProjectileConfigs::validateStructure,
         UpdateProjectileConfigs::deserialize
      );
      register(100, "SetClientId", SetClientId.class, 4, 4, false, SetClientId::validateStructure, SetClientId::deserialize);
      register(101, "SetGameMode", SetGameMode.class, 1, 1, false, SetGameMode::validateStructure, SetGameMode::deserialize);
      register(102, "SetMovementStates", SetMovementStates.class, 2, 2, false, SetMovementStates::validateStructure, SetMovementStates::deserialize);
      register(
         103,
         "SetBlockPlacementOverride",
         SetBlockPlacementOverride.class,
         1,
         1,
         false,
         SetBlockPlacementOverride::validateStructure,
         SetBlockPlacementOverride::deserialize
      );
      register(104, "JoinWorld", JoinWorld.class, 18, 18, false, JoinWorld::validateStructure, JoinWorld::deserialize);
      register(105, "ClientReady", ClientReady.class, 2, 2, false, ClientReady::validateStructure, ClientReady::deserialize);
      register(106, "LoadHotbar", LoadHotbar.class, 1, 1, false, LoadHotbar::validateStructure, LoadHotbar::deserialize);
      register(107, "SaveHotbar", SaveHotbar.class, 1, 1, false, SaveHotbar::validateStructure, SaveHotbar::deserialize);
      register(108, "ClientMovement", ClientMovement.class, 153, 153, false, ClientMovement::validateStructure, ClientMovement::deserialize);
      register(109, "ClientTeleport", ClientTeleport.class, 52, 52, false, ClientTeleport::validateStructure, ClientTeleport::deserialize);
      register(
         110,
         "UpdateMovementSettings",
         UpdateMovementSettings.class,
         252,
         252,
         false,
         UpdateMovementSettings::validateStructure,
         UpdateMovementSettings::deserialize
      );
      register(111, "MouseInteraction", MouseInteraction.class, 44, 20480071, false, MouseInteraction::validateStructure, MouseInteraction::deserialize);
      register(112, "DamageInfo", DamageInfo.class, 29, 32768048, false, DamageInfo::validateStructure, DamageInfo::deserialize);
      register(113, "ReticleEvent", ReticleEvent.class, 4, 4, false, ReticleEvent::validateStructure, ReticleEvent::deserialize);
      register(114, "DisplayDebug", DisplayDebug.class, 19, 32768037, false, DisplayDebug::validateStructure, DisplayDebug::deserialize);
      register(115, "ClearDebugShapes", ClearDebugShapes.class, 0, 0, false, ClearDebugShapes::validateStructure, ClearDebugShapes::deserialize);
      register(
         116, "SyncPlayerPreferences", SyncPlayerPreferences.class, 8, 8, false, SyncPlayerPreferences::validateStructure, SyncPlayerPreferences::deserialize
      );
      register(117, "ClientPlaceBlock", ClientPlaceBlock.class, 20, 20, false, ClientPlaceBlock::validateStructure, ClientPlaceBlock::deserialize);
      register(
         118,
         "UpdateMemoriesFeatureStatus",
         UpdateMemoriesFeatureStatus.class,
         1,
         1,
         false,
         UpdateMemoriesFeatureStatus::validateStructure,
         UpdateMemoriesFeatureStatus::deserialize
      );
      register(119, "RemoveMapMarker", RemoveMapMarker.class, 1, 16384006, false, RemoveMapMarker::validateStructure, RemoveMapMarker::deserialize);
      register(131, "SetChunk", SetChunk.class, 13, 12288040, true, SetChunk::validateStructure, SetChunk::deserialize);
      register(132, "SetChunkHeightmap", SetChunkHeightmap.class, 9, 4096014, true, SetChunkHeightmap::validateStructure, SetChunkHeightmap::deserialize);
      register(133, "SetChunkTintmap", SetChunkTintmap.class, 9, 4096014, true, SetChunkTintmap::validateStructure, SetChunkTintmap::deserialize);
      register(
         134, "SetChunkEnvironments", SetChunkEnvironments.class, 9, 4096014, true, SetChunkEnvironments::validateStructure, SetChunkEnvironments::deserialize
      );
      register(135, "UnloadChunk", UnloadChunk.class, 8, 8, false, UnloadChunk::validateStructure, UnloadChunk::deserialize);
      register(136, "SetFluids", SetFluids.class, 13, 4096018, true, SetFluids::validateStructure, SetFluids::deserialize);
      register(140, "ServerSetBlock", ServerSetBlock.class, 19, 19, false, ServerSetBlock::validateStructure, ServerSetBlock::deserialize);
      register(141, "ServerSetBlocks", ServerSetBlocks.class, 12, 36864017, false, ServerSetBlocks::validateStructure, ServerSetBlocks::deserialize);
      register(142, "ServerSetFluid", ServerSetFluid.class, 17, 17, false, ServerSetFluid::validateStructure, ServerSetFluid::deserialize);
      register(143, "ServerSetFluids", ServerSetFluids.class, 12, 28672017, false, ServerSetFluids::validateStructure, ServerSetFluids::deserialize);
      register(144, "UpdateBlockDamage", UpdateBlockDamage.class, 21, 21, false, UpdateBlockDamage::validateStructure, UpdateBlockDamage::deserialize);
      register(145, "UpdateTimeSettings", UpdateTimeSettings.class, 10, 10, false, UpdateTimeSettings::validateStructure, UpdateTimeSettings::deserialize);
      register(146, "UpdateTime", UpdateTime.class, 13, 13, false, UpdateTime::validateStructure, UpdateTime::deserialize);
      register(
         147,
         "UpdateEditorTimeOverride",
         UpdateEditorTimeOverride.class,
         14,
         14,
         false,
         UpdateEditorTimeOverride::validateStructure,
         UpdateEditorTimeOverride::deserialize
      );
      register(
         148,
         "ClearEditorTimeOverride",
         ClearEditorTimeOverride.class,
         0,
         0,
         false,
         ClearEditorTimeOverride::validateStructure,
         ClearEditorTimeOverride::deserialize
      );
      register(149, "UpdateWeather", UpdateWeather.class, 8, 8, false, UpdateWeather::validateStructure, UpdateWeather::deserialize);
      register(
         150,
         "UpdateEditorWeatherOverride",
         UpdateEditorWeatherOverride.class,
         4,
         4,
         false,
         UpdateEditorWeatherOverride::validateStructure,
         UpdateEditorWeatherOverride::deserialize
      );
      register(
         151,
         "UpdateEnvironmentMusic",
         UpdateEnvironmentMusic.class,
         4,
         4,
         false,
         UpdateEnvironmentMusic::validateStructure,
         UpdateEnvironmentMusic::deserialize
      );
      register(
         152, "SpawnParticleSystem", SpawnParticleSystem.class, 44, 16384049, false, SpawnParticleSystem::validateStructure, SpawnParticleSystem::deserialize
      );
      register(
         153,
         "SpawnBlockParticleSystem",
         SpawnBlockParticleSystem.class,
         30,
         30,
         false,
         SpawnBlockParticleSystem::validateStructure,
         SpawnBlockParticleSystem::deserialize
      );
      register(154, "PlaySoundEvent2D", PlaySoundEvent2D.class, 13, 13, false, PlaySoundEvent2D::validateStructure, PlaySoundEvent2D::deserialize);
      register(155, "PlaySoundEvent3D", PlaySoundEvent3D.class, 38, 38, false, PlaySoundEvent3D::validateStructure, PlaySoundEvent3D::deserialize);
      register(
         156, "PlaySoundEventEntity", PlaySoundEventEntity.class, 16, 16, false, PlaySoundEventEntity::validateStructure, PlaySoundEventEntity::deserialize
      );
      register(157, "UpdateSleepState", UpdateSleepState.class, 36, 65536050, false, UpdateSleepState::validateStructure, UpdateSleepState::deserialize);
      register(158, "SetPaused", SetPaused.class, 1, 1, false, SetPaused::validateStructure, SetPaused::deserialize);
      register(159, "ServerSetPaused", ServerSetPaused.class, 1, 1, false, ServerSetPaused::validateStructure, ServerSetPaused::deserialize);
      register(160, "SetEntitySeed", SetEntitySeed.class, 4, 4, false, SetEntitySeed::validateStructure, SetEntitySeed::deserialize);
      register(161, "EntityUpdates", EntityUpdates.class, 1, 1677721600, true, EntityUpdates::validateStructure, EntityUpdates::deserialize);
      register(162, "PlayAnimation", PlayAnimation.class, 6, 32768024, false, PlayAnimation::validateStructure, PlayAnimation::deserialize);
      register(163, "ChangeVelocity", ChangeVelocity.class, 35, 35, false, ChangeVelocity::validateStructure, ChangeVelocity::deserialize);
      register(164, "ApplyKnockback", ApplyKnockback.class, 38, 38, false, ApplyKnockback::validateStructure, ApplyKnockback::deserialize);
      register(
         165, "SpawnModelParticles", SpawnModelParticles.class, 5, 1677721600, false, SpawnModelParticles::validateStructure, SpawnModelParticles::deserialize
      );
      register(166, "MountMovement", MountMovement.class, 59, 59, false, MountMovement::validateStructure, MountMovement::deserialize);
      register(
         170,
         "UpdatePlayerInventory",
         UpdatePlayerInventory.class,
         2,
         1677721600,
         true,
         UpdatePlayerInventory::validateStructure,
         UpdatePlayerInventory::deserialize
      );
      register(171, "SetCreativeItem", SetCreativeItem.class, 9, 16384019, false, SetCreativeItem::validateStructure, SetCreativeItem::deserialize);
      register(172, "DropCreativeItem", DropCreativeItem.class, 0, 16384010, false, DropCreativeItem::validateStructure, DropCreativeItem::deserialize);
      register(
         173,
         "SmartGiveCreativeItem",
         SmartGiveCreativeItem.class,
         1,
         16384011,
         false,
         SmartGiveCreativeItem::validateStructure,
         SmartGiveCreativeItem::deserialize
      );
      register(174, "DropItemStack", DropItemStack.class, 12, 12, false, DropItemStack::validateStructure, DropItemStack::deserialize);
      register(175, "MoveItemStack", MoveItemStack.class, 20, 20, false, MoveItemStack::validateStructure, MoveItemStack::deserialize);
      register(176, "SmartMoveItemStack", SmartMoveItemStack.class, 13, 13, false, SmartMoveItemStack::validateStructure, SmartMoveItemStack::deserialize);
      register(177, "SetActiveSlot", SetActiveSlot.class, 8, 8, false, SetActiveSlot::validateStructure, SetActiveSlot::deserialize);
      register(
         178,
         "SwitchHotbarBlockSet",
         SwitchHotbarBlockSet.class,
         1,
         16384006,
         false,
         SwitchHotbarBlockSet::validateStructure,
         SwitchHotbarBlockSet::deserialize
      );
      register(179, "InventoryAction", InventoryAction.class, 6, 6, false, InventoryAction::validateStructure, InventoryAction::deserialize);
      register(200, "OpenWindow", OpenWindow.class, 6, 1677721600, true, OpenWindow::validateStructure, OpenWindow::deserialize);
      register(201, "UpdateWindow", UpdateWindow.class, 5, 1677721600, true, UpdateWindow::validateStructure, UpdateWindow::deserialize);
      register(202, "CloseWindow", CloseWindow.class, 4, 4, false, CloseWindow::validateStructure, CloseWindow::deserialize);
      register(203, "SendWindowAction", SendWindowAction.class, 4, 32768027, false, SendWindowAction::validateStructure, SendWindowAction::deserialize);
      register(204, "ClientOpenWindow", ClientOpenWindow.class, 1, 1, false, ClientOpenWindow::validateStructure, ClientOpenWindow::deserialize);
      register(210, "ServerMessage", ServerMessage.class, 2, 1677721600, false, ServerMessage::validateStructure, ServerMessage::deserialize);
      register(211, "ChatMessage", ChatMessage.class, 1, 16384006, false, ChatMessage::validateStructure, ChatMessage::deserialize);
      register(212, "Notification", Notification.class, 2, 1677721600, false, Notification::validateStructure, Notification::deserialize);
      register(213, "KillFeedMessage", KillFeedMessage.class, 1, 1677721600, false, KillFeedMessage::validateStructure, KillFeedMessage::deserialize);
      register(214, "ShowEventTitle", ShowEventTitle.class, 14, 1677721600, false, ShowEventTitle::validateStructure, ShowEventTitle::deserialize);
      register(215, "HideEventTitle", HideEventTitle.class, 4, 4, false, HideEventTitle::validateStructure, HideEventTitle::deserialize);
      register(216, "SetPage", SetPage.class, 2, 2, false, SetPage::validateStructure, SetPage::deserialize);
      register(217, "CustomHud", CustomHud.class, 2, 1677721600, true, CustomHud::validateStructure, CustomHud::deserialize);
      register(218, "CustomPage", CustomPage.class, 4, 1677721600, true, CustomPage::validateStructure, CustomPage::deserialize);
      register(219, "CustomPageEvent", CustomPageEvent.class, 2, 16384007, false, CustomPageEvent::validateStructure, CustomPageEvent::deserialize);
      register(222, "EditorBlocksChange", EditorBlocksChange.class, 30, 139264048, true, EditorBlocksChange::validateStructure, EditorBlocksChange::deserialize);
      register(223, "ServerInfo", ServerInfo.class, 5, 32768023, false, ServerInfo::validateStructure, ServerInfo::deserialize);
      register(
         224,
         "AddToServerPlayerList",
         AddToServerPlayerList.class,
         1,
         1677721600,
         false,
         AddToServerPlayerList::validateStructure,
         AddToServerPlayerList::deserialize
      );
      register(
         225,
         "RemoveFromServerPlayerList",
         RemoveFromServerPlayerList.class,
         1,
         65536006,
         false,
         RemoveFromServerPlayerList::validateStructure,
         RemoveFromServerPlayerList::deserialize
      );
      register(
         226,
         "UpdateServerPlayerList",
         UpdateServerPlayerList.class,
         1,
         131072006,
         false,
         UpdateServerPlayerList::validateStructure,
         UpdateServerPlayerList::deserialize
      );
      register(
         227,
         "UpdateServerPlayerListPing",
         UpdateServerPlayerListPing.class,
         1,
         81920006,
         false,
         UpdateServerPlayerListPing::validateStructure,
         UpdateServerPlayerListPing::deserialize
      );
      register(
         228, "UpdateKnownRecipes", UpdateKnownRecipes.class, 1, 1677721600, false, UpdateKnownRecipes::validateStructure, UpdateKnownRecipes::deserialize
      );
      register(229, "UpdatePortal", UpdatePortal.class, 6, 16384020, false, UpdatePortal::validateStructure, UpdatePortal::deserialize);
      register(
         230,
         "UpdateVisibleHudComponents",
         UpdateVisibleHudComponents.class,
         1,
         4096006,
         false,
         UpdateVisibleHudComponents::validateStructure,
         UpdateVisibleHudComponents::deserialize
      );
      register(
         231,
         "ResetUserInterfaceState",
         ResetUserInterfaceState.class,
         0,
         0,
         false,
         ResetUserInterfaceState::validateStructure,
         ResetUserInterfaceState::deserialize
      );
      register(232, "UpdateLanguage", UpdateLanguage.class, 1, 16384006, false, UpdateLanguage::validateStructure, UpdateLanguage::deserialize);
      register(233, "WorldSavingStatus", WorldSavingStatus.class, 1, 1, false, WorldSavingStatus::validateStructure, WorldSavingStatus::deserialize);
      register(
         234, "OpenChatWithCommand", OpenChatWithCommand.class, 1, 16384006, false, OpenChatWithCommand::validateStructure, OpenChatWithCommand::deserialize
      );
      register(
         240,
         "UpdateWorldMapSettings",
         UpdateWorldMapSettings.class,
         16,
         1677721600,
         false,
         UpdateWorldMapSettings::validateStructure,
         UpdateWorldMapSettings::deserialize
      );
      register(241, "UpdateWorldMap", UpdateWorldMap.class, 1, 1677721600, true, UpdateWorldMap::validateStructure, UpdateWorldMap::deserialize);
      register(242, "ClearWorldMap", ClearWorldMap.class, 0, 0, false, ClearWorldMap::validateStructure, ClearWorldMap::deserialize);
      register(
         243, "UpdateWorldMapVisible", UpdateWorldMapVisible.class, 1, 1, false, UpdateWorldMapVisible::validateStructure, UpdateWorldMapVisible::deserialize
      );
      register(
         244,
         "TeleportToWorldMapMarker",
         TeleportToWorldMapMarker.class,
         1,
         16384006,
         false,
         TeleportToWorldMapMarker::validateStructure,
         TeleportToWorldMapMarker::deserialize
      );
      register(
         245,
         "TeleportToWorldMapPosition",
         TeleportToWorldMapPosition.class,
         8,
         8,
         false,
         TeleportToWorldMapPosition::validateStructure,
         TeleportToWorldMapPosition::deserialize
      );
      register(250, "RequestServerAccess", RequestServerAccess.class, 3, 3, false, RequestServerAccess::validateStructure, RequestServerAccess::deserialize);
      register(
         251, "UpdateServerAccess", UpdateServerAccess.class, 2, 1677721600, false, UpdateServerAccess::validateStructure, UpdateServerAccess::deserialize
      );
      register(252, "SetServerAccess", SetServerAccess.class, 2, 16384007, false, SetServerAccess::validateStructure, SetServerAccess::deserialize);
      register(
         260,
         "RequestMachinimaActorModel",
         RequestMachinimaActorModel.class,
         1,
         49152028,
         false,
         RequestMachinimaActorModel::validateStructure,
         RequestMachinimaActorModel::deserialize
      );
      register(
         261,
         "SetMachinimaActorModel",
         SetMachinimaActorModel.class,
         1,
         1677721600,
         false,
         SetMachinimaActorModel::validateStructure,
         SetMachinimaActorModel::deserialize
      );
      register(
         262, "UpdateMachinimaScene", UpdateMachinimaScene.class, 6, 36864033, true, UpdateMachinimaScene::validateStructure, UpdateMachinimaScene::deserialize
      );
      register(280, "SetServerCamera", SetServerCamera.class, 157, 157, false, SetServerCamera::validateStructure, SetServerCamera::deserialize);
      register(281, "CameraShakeEffect", CameraShakeEffect.class, 9, 9, false, CameraShakeEffect::validateStructure, CameraShakeEffect::deserialize);
      register(282, "RequestFlyCameraMode", RequestFlyCameraMode.class, 1, 1, false, RequestFlyCameraMode::validateStructure, RequestFlyCameraMode::deserialize);
      register(283, "SetFlyCameraMode", SetFlyCameraMode.class, 1, 1, false, SetFlyCameraMode::validateStructure, SetFlyCameraMode::deserialize);
      register(
         290,
         "SyncInteractionChains",
         SyncInteractionChains.class,
         0,
         1677721600,
         false,
         SyncInteractionChains::validateStructure,
         SyncInteractionChains::deserialize
      );
      register(
         291,
         "CancelInteractionChain",
         CancelInteractionChain.class,
         5,
         1038,
         false,
         CancelInteractionChain::validateStructure,
         CancelInteractionChain::deserialize
      );
      register(292, "PlayInteractionFor", PlayInteractionFor.class, 19, 16385065, false, PlayInteractionFor::validateStructure, PlayInteractionFor::deserialize);
      register(293, "MountNPC", MountNPC.class, 16, 16, false, MountNPC::validateStructure, MountNPC::deserialize);
      register(294, "DismountNPC", DismountNPC.class, 0, 0, false, DismountNPC::validateStructure, DismountNPC::deserialize);
      register(300, "FailureReply", FailureReply.class, 5, 1677721600, false, FailureReply::validateStructure, FailureReply::deserialize);
      register(301, "SuccessReply", SuccessReply.class, 5, 1677721600, false, SuccessReply::validateStructure, SuccessReply::deserialize);
      register(
         302, "AssetEditorInitialize", AssetEditorInitialize.class, 0, 0, false, AssetEditorInitialize::validateStructure, AssetEditorInitialize::deserialize
      );
      register(
         303,
         "AssetEditorAuthorization",
         AssetEditorAuthorization.class,
         1,
         1,
         false,
         AssetEditorAuthorization::validateStructure,
         AssetEditorAuthorization::deserialize
      );
      register(
         304,
         "AssetEditorCapabilities",
         AssetEditorCapabilities.class,
         5,
         5,
         false,
         AssetEditorCapabilities::validateStructure,
         AssetEditorCapabilities::deserialize
      );
      register(
         305,
         "AssetEditorSetupSchemas",
         AssetEditorSetupSchemas.class,
         1,
         1677721600,
         true,
         AssetEditorSetupSchemas::validateStructure,
         AssetEditorSetupSchemas::deserialize
      );
      register(
         306,
         "AssetEditorSetupAssetTypes",
         AssetEditorSetupAssetTypes.class,
         1,
         1677721600,
         false,
         AssetEditorSetupAssetTypes::validateStructure,
         AssetEditorSetupAssetTypes::deserialize
      );
      register(
         307,
         "AssetEditorCreateDirectory",
         AssetEditorCreateDirectory.class,
         5,
         32768024,
         false,
         AssetEditorCreateDirectory::validateStructure,
         AssetEditorCreateDirectory::deserialize
      );
      register(
         308,
         "AssetEditorDeleteDirectory",
         AssetEditorDeleteDirectory.class,
         5,
         32768024,
         false,
         AssetEditorDeleteDirectory::validateStructure,
         AssetEditorDeleteDirectory::deserialize
      );
      register(
         309,
         "AssetEditorRenameDirectory",
         AssetEditorRenameDirectory.class,
         5,
         65536051,
         false,
         AssetEditorRenameDirectory::validateStructure,
         AssetEditorRenameDirectory::deserialize
      );
      register(
         310,
         "AssetEditorFetchAsset",
         AssetEditorFetchAsset.class,
         6,
         32768025,
         false,
         AssetEditorFetchAsset::validateStructure,
         AssetEditorFetchAsset::deserialize
      );
      register(
         311,
         "AssetEditorFetchJsonAssetWithParents",
         AssetEditorFetchJsonAssetWithParents.class,
         6,
         32768025,
         false,
         AssetEditorFetchJsonAssetWithParents::validateStructure,
         AssetEditorFetchJsonAssetWithParents::deserialize
      );
      register(
         312,
         "AssetEditorFetchAssetReply",
         AssetEditorFetchAssetReply.class,
         5,
         4096010,
         false,
         AssetEditorFetchAssetReply::validateStructure,
         AssetEditorFetchAssetReply::deserialize
      );
      register(
         313,
         "AssetEditorFetchJsonAssetWithParentsReply",
         AssetEditorFetchJsonAssetWithParentsReply.class,
         5,
         1677721600,
         true,
         AssetEditorFetchJsonAssetWithParentsReply::validateStructure,
         AssetEditorFetchJsonAssetWithParentsReply::deserialize
      );
      register(
         314,
         "AssetEditorAssetPackSetup",
         AssetEditorAssetPackSetup.class,
         1,
         1677721600,
         false,
         AssetEditorAssetPackSetup::validateStructure,
         AssetEditorAssetPackSetup::deserialize
      );
      register(
         315,
         "AssetEditorUpdateAssetPack",
         AssetEditorUpdateAssetPack.class,
         1,
         1677721600,
         false,
         AssetEditorUpdateAssetPack::validateStructure,
         AssetEditorUpdateAssetPack::deserialize
      );
      register(
         316,
         "AssetEditorCreateAssetPack",
         AssetEditorCreateAssetPack.class,
         5,
         1677721600,
         false,
         AssetEditorCreateAssetPack::validateStructure,
         AssetEditorCreateAssetPack::deserialize
      );
      register(
         317,
         "AssetEditorDeleteAssetPack",
         AssetEditorDeleteAssetPack.class,
         1,
         16384006,
         false,
         AssetEditorDeleteAssetPack::validateStructure,
         AssetEditorDeleteAssetPack::deserialize
      );
      register(
         318,
         "AssetEditorEnableAssetPack",
         AssetEditorEnableAssetPack.class,
         2,
         16384007,
         false,
         AssetEditorEnableAssetPack::validateStructure,
         AssetEditorEnableAssetPack::deserialize
      );
      register(
         319,
         "AssetEditorAssetListSetup",
         AssetEditorAssetListSetup.class,
         4,
         1677721600,
         true,
         AssetEditorAssetListSetup::validateStructure,
         AssetEditorAssetListSetup::deserialize
      );
      register(
         320,
         "AssetEditorAssetListUpdate",
         AssetEditorAssetListUpdate.class,
         1,
         1677721600,
         true,
         AssetEditorAssetListUpdate::validateStructure,
         AssetEditorAssetListUpdate::deserialize
      );
      register(
         321,
         "AssetEditorRequestChildrenList",
         AssetEditorRequestChildrenList.class,
         1,
         32768020,
         false,
         AssetEditorRequestChildrenList::validateStructure,
         AssetEditorRequestChildrenList::deserialize
      );
      register(
         322,
         "AssetEditorRequestChildrenListReply",
         AssetEditorRequestChildrenListReply.class,
         1,
         1677721600,
         false,
         AssetEditorRequestChildrenListReply::validateStructure,
         AssetEditorRequestChildrenListReply::deserialize
      );
      register(
         323,
         "AssetEditorUpdateJsonAsset",
         AssetEditorUpdateJsonAsset.class,
         9,
         1677721600,
         true,
         AssetEditorUpdateJsonAsset::validateStructure,
         AssetEditorUpdateJsonAsset::deserialize
      );
      register(
         324,
         "AssetEditorUpdateAsset",
         AssetEditorUpdateAsset.class,
         9,
         53248050,
         false,
         AssetEditorUpdateAsset::validateStructure,
         AssetEditorUpdateAsset::deserialize
      );
      register(
         325,
         "AssetEditorJsonAssetUpdated",
         AssetEditorJsonAssetUpdated.class,
         1,
         1677721600,
         false,
         AssetEditorJsonAssetUpdated::validateStructure,
         AssetEditorJsonAssetUpdated::deserialize
      );
      register(
         326,
         "AssetEditorAssetUpdated",
         AssetEditorAssetUpdated.class,
         1,
         36864033,
         false,
         AssetEditorAssetUpdated::validateStructure,
         AssetEditorAssetUpdated::deserialize
      );
      register(
         327,
         "AssetEditorCreateAsset",
         AssetEditorCreateAsset.class,
         10,
         53248051,
         false,
         AssetEditorCreateAsset::validateStructure,
         AssetEditorCreateAsset::deserialize
      );
      register(
         328,
         "AssetEditorRenameAsset",
         AssetEditorRenameAsset.class,
         5,
         65536051,
         false,
         AssetEditorRenameAsset::validateStructure,
         AssetEditorRenameAsset::deserialize
      );
      register(
         329,
         "AssetEditorDeleteAsset",
         AssetEditorDeleteAsset.class,
         5,
         32768024,
         false,
         AssetEditorDeleteAsset::validateStructure,
         AssetEditorDeleteAsset::deserialize
      );
      register(
         330,
         "AssetEditorDiscardChanges",
         AssetEditorDiscardChanges.class,
         1,
         1677721600,
         false,
         AssetEditorDiscardChanges::validateStructure,
         AssetEditorDiscardChanges::deserialize
      );
      register(
         331,
         "AssetEditorFetchAutoCompleteData",
         AssetEditorFetchAutoCompleteData.class,
         5,
         32768023,
         false,
         AssetEditorFetchAutoCompleteData::validateStructure,
         AssetEditorFetchAutoCompleteData::deserialize
      );
      register(
         332,
         "AssetEditorFetchAutoCompleteDataReply",
         AssetEditorFetchAutoCompleteDataReply.class,
         5,
         1677721600,
         false,
         AssetEditorFetchAutoCompleteDataReply::validateStructure,
         AssetEditorFetchAutoCompleteDataReply::deserialize
      );
      register(
         333,
         "AssetEditorRequestDataset",
         AssetEditorRequestDataset.class,
         1,
         16384006,
         false,
         AssetEditorRequestDataset::validateStructure,
         AssetEditorRequestDataset::deserialize
      );
      register(
         334,
         "AssetEditorRequestDatasetReply",
         AssetEditorRequestDatasetReply.class,
         1,
         1677721600,
         false,
         AssetEditorRequestDatasetReply::validateStructure,
         AssetEditorRequestDatasetReply::deserialize
      );
      register(
         335,
         "AssetEditorActivateButton",
         AssetEditorActivateButton.class,
         1,
         16384006,
         false,
         AssetEditorActivateButton::validateStructure,
         AssetEditorActivateButton::deserialize
      );
      register(
         336,
         "AssetEditorSelectAsset",
         AssetEditorSelectAsset.class,
         1,
         32768020,
         false,
         AssetEditorSelectAsset::validateStructure,
         AssetEditorSelectAsset::deserialize
      );
      register(
         337,
         "AssetEditorPopupNotification",
         AssetEditorPopupNotification.class,
         2,
         1677721600,
         false,
         AssetEditorPopupNotification::validateStructure,
         AssetEditorPopupNotification::deserialize
      );
      register(
         338,
         "AssetEditorFetchLastModifiedAssets",
         AssetEditorFetchLastModifiedAssets.class,
         0,
         0,
         false,
         AssetEditorFetchLastModifiedAssets::validateStructure,
         AssetEditorFetchLastModifiedAssets::deserialize
      );
      register(
         339,
         "AssetEditorLastModifiedAssets",
         AssetEditorLastModifiedAssets.class,
         1,
         1677721600,
         false,
         AssetEditorLastModifiedAssets::validateStructure,
         AssetEditorLastModifiedAssets::deserialize
      );
      register(
         340,
         "AssetEditorModifiedAssetsCount",
         AssetEditorModifiedAssetsCount.class,
         4,
         4,
         false,
         AssetEditorModifiedAssetsCount::validateStructure,
         AssetEditorModifiedAssetsCount::deserialize
      );
      register(
         341,
         "AssetEditorSubscribeModifiedAssetsChanges",
         AssetEditorSubscribeModifiedAssetsChanges.class,
         1,
         1,
         false,
         AssetEditorSubscribeModifiedAssetsChanges::validateStructure,
         AssetEditorSubscribeModifiedAssetsChanges::deserialize
      );
      register(
         342,
         "AssetEditorExportAssets",
         AssetEditorExportAssets.class,
         1,
         1677721600,
         false,
         AssetEditorExportAssets::validateStructure,
         AssetEditorExportAssets::deserialize
      );
      register(
         343,
         "AssetEditorExportAssetInitialize",
         AssetEditorExportAssetInitialize.class,
         6,
         81920066,
         false,
         AssetEditorExportAssetInitialize::validateStructure,
         AssetEditorExportAssetInitialize::deserialize
      );
      register(
         344,
         "AssetEditorExportAssetPart",
         AssetEditorExportAssetPart.class,
         1,
         4096006,
         true,
         AssetEditorExportAssetPart::validateStructure,
         AssetEditorExportAssetPart::deserialize
      );
      register(
         345,
         "AssetEditorExportAssetFinalize",
         AssetEditorExportAssetFinalize.class,
         0,
         0,
         false,
         AssetEditorExportAssetFinalize::validateStructure,
         AssetEditorExportAssetFinalize::deserialize
      );
      register(
         346,
         "AssetEditorExportDeleteAssets",
         AssetEditorExportDeleteAssets.class,
         1,
         1677721600,
         false,
         AssetEditorExportDeleteAssets::validateStructure,
         AssetEditorExportDeleteAssets::deserialize
      );
      register(
         347,
         "AssetEditorExportComplete",
         AssetEditorExportComplete.class,
         1,
         1677721600,
         false,
         AssetEditorExportComplete::validateStructure,
         AssetEditorExportComplete::deserialize
      );
      register(
         348,
         "AssetEditorRebuildCaches",
         AssetEditorRebuildCaches.class,
         5,
         5,
         false,
         AssetEditorRebuildCaches::validateStructure,
         AssetEditorRebuildCaches::deserialize
      );
      register(
         349,
         "AssetEditorUndoChanges",
         AssetEditorUndoChanges.class,
         5,
         32768024,
         false,
         AssetEditorUndoChanges::validateStructure,
         AssetEditorUndoChanges::deserialize
      );
      register(
         350,
         "AssetEditorRedoChanges",
         AssetEditorRedoChanges.class,
         5,
         32768024,
         false,
         AssetEditorRedoChanges::validateStructure,
         AssetEditorRedoChanges::deserialize
      );
      register(
         351,
         "AssetEditorUndoRedoReply",
         AssetEditorUndoRedoReply.class,
         5,
         1677721600,
         false,
         AssetEditorUndoRedoReply::validateStructure,
         AssetEditorUndoRedoReply::deserialize
      );
      register(
         352,
         "AssetEditorSetGameTime",
         AssetEditorSetGameTime.class,
         14,
         14,
         false,
         AssetEditorSetGameTime::validateStructure,
         AssetEditorSetGameTime::deserialize
      );
      register(
         353,
         "AssetEditorUpdateSecondsPerGameDay",
         AssetEditorUpdateSecondsPerGameDay.class,
         8,
         8,
         false,
         AssetEditorUpdateSecondsPerGameDay::validateStructure,
         AssetEditorUpdateSecondsPerGameDay::deserialize
      );
      register(
         354,
         "AssetEditorUpdateWeatherPreviewLock",
         AssetEditorUpdateWeatherPreviewLock.class,
         1,
         1,
         false,
         AssetEditorUpdateWeatherPreviewLock::validateStructure,
         AssetEditorUpdateWeatherPreviewLock::deserialize
      );
      register(
         355,
         "AssetEditorUpdateModelPreview",
         AssetEditorUpdateModelPreview.class,
         30,
         1677721600,
         false,
         AssetEditorUpdateModelPreview::validateStructure,
         AssetEditorUpdateModelPreview::deserialize
      );
      register(360, "UpdateSunSettings", UpdateSunSettings.class, 8, 8, false, UpdateSunSettings::validateStructure, UpdateSunSettings::deserialize);
      register(
         361, "UpdatePostFxSettings", UpdatePostFxSettings.class, 20, 20, false, UpdatePostFxSettings::validateStructure, UpdatePostFxSettings::deserialize
      );
      register(
         400,
         "BuilderToolArgUpdate",
         BuilderToolArgUpdate.class,
         14,
         32768032,
         false,
         BuilderToolArgUpdate::validateStructure,
         BuilderToolArgUpdate::deserialize
      );
      register(
         401,
         "BuilderToolEntityAction",
         BuilderToolEntityAction.class,
         5,
         5,
         false,
         BuilderToolEntityAction::validateStructure,
         BuilderToolEntityAction::deserialize
      );
      register(
         402,
         "BuilderToolSetEntityTransform",
         BuilderToolSetEntityTransform.class,
         54,
         54,
         false,
         BuilderToolSetEntityTransform::validateStructure,
         BuilderToolSetEntityTransform::deserialize
      );
      register(
         403,
         "BuilderToolExtrudeAction",
         BuilderToolExtrudeAction.class,
         24,
         24,
         false,
         BuilderToolExtrudeAction::validateStructure,
         BuilderToolExtrudeAction::deserialize
      );
      register(
         404, "BuilderToolStackArea", BuilderToolStackArea.class, 41, 41, false, BuilderToolStackArea::validateStructure, BuilderToolStackArea::deserialize
      );
      register(
         405,
         "BuilderToolSelectionTransform",
         BuilderToolSelectionTransform.class,
         52,
         16384057,
         false,
         BuilderToolSelectionTransform::validateStructure,
         BuilderToolSelectionTransform::deserialize
      );
      register(
         406,
         "BuilderToolRotateClipboard",
         BuilderToolRotateClipboard.class,
         5,
         5,
         false,
         BuilderToolRotateClipboard::validateStructure,
         BuilderToolRotateClipboard::deserialize
      );
      register(
         407,
         "BuilderToolPasteClipboard",
         BuilderToolPasteClipboard.class,
         12,
         12,
         false,
         BuilderToolPasteClipboard::validateStructure,
         BuilderToolPasteClipboard::deserialize
      );
      register(
         408,
         "BuilderToolSetTransformationModeState",
         BuilderToolSetTransformationModeState.class,
         1,
         1,
         false,
         BuilderToolSetTransformationModeState::validateStructure,
         BuilderToolSetTransformationModeState::deserialize
      );
      register(
         409,
         "BuilderToolSelectionUpdate",
         BuilderToolSelectionUpdate.class,
         24,
         24,
         false,
         BuilderToolSelectionUpdate::validateStructure,
         BuilderToolSelectionUpdate::deserialize
      );
      register(
         410,
         "BuilderToolSelectionToolAskForClipboard",
         BuilderToolSelectionToolAskForClipboard.class,
         0,
         0,
         false,
         BuilderToolSelectionToolAskForClipboard::validateStructure,
         BuilderToolSelectionToolAskForClipboard::deserialize
      );
      register(
         411,
         "BuilderToolSelectionToolReplyWithClipboard",
         BuilderToolSelectionToolReplyWithClipboard.class,
         1,
         139264019,
         true,
         BuilderToolSelectionToolReplyWithClipboard::validateStructure,
         BuilderToolSelectionToolReplyWithClipboard::deserialize
      );
      register(
         412,
         "BuilderToolGeneralAction",
         BuilderToolGeneralAction.class,
         1,
         1,
         false,
         BuilderToolGeneralAction::validateStructure,
         BuilderToolGeneralAction::deserialize
      );
      register(
         413,
         "BuilderToolOnUseInteraction",
         BuilderToolOnUseInteraction.class,
         57,
         57,
         false,
         BuilderToolOnUseInteraction::validateStructure,
         BuilderToolOnUseInteraction::deserialize
      );
      register(
         414, "BuilderToolLineAction", BuilderToolLineAction.class, 24, 24, false, BuilderToolLineAction::validateStructure, BuilderToolLineAction::deserialize
      );
      register(
         415, "BuilderToolShowAnchor", BuilderToolShowAnchor.class, 12, 12, false, BuilderToolShowAnchor::validateStructure, BuilderToolShowAnchor::deserialize
      );
      register(
         416,
         "BuilderToolHideAnchors",
         BuilderToolHideAnchors.class,
         0,
         0,
         false,
         BuilderToolHideAnchors::validateStructure,
         BuilderToolHideAnchors::deserialize
      );
      register(417, "PrefabUnselectPrefab", PrefabUnselectPrefab.class, 0, 0, false, PrefabUnselectPrefab::validateStructure, PrefabUnselectPrefab::deserialize);
      register(
         418,
         "BuilderToolsSetSoundSet",
         BuilderToolsSetSoundSet.class,
         4,
         4,
         false,
         BuilderToolsSetSoundSet::validateStructure,
         BuilderToolsSetSoundSet::deserialize
      );
      register(
         419,
         "BuilderToolLaserPointer",
         BuilderToolLaserPointer.class,
         36,
         36,
         false,
         BuilderToolLaserPointer::validateStructure,
         BuilderToolLaserPointer::deserialize
      );
      register(
         420,
         "BuilderToolSetEntityScale",
         BuilderToolSetEntityScale.class,
         8,
         8,
         false,
         BuilderToolSetEntityScale::validateStructure,
         BuilderToolSetEntityScale::deserialize
      );
      register(
         421,
         "BuilderToolSetEntityPickupEnabled",
         BuilderToolSetEntityPickupEnabled.class,
         5,
         5,
         false,
         BuilderToolSetEntityPickupEnabled::validateStructure,
         BuilderToolSetEntityPickupEnabled::deserialize
      );
      register(
         422,
         "BuilderToolSetEntityLight",
         BuilderToolSetEntityLight.class,
         9,
         9,
         false,
         BuilderToolSetEntityLight::validateStructure,
         BuilderToolSetEntityLight::deserialize
      );
      register(
         423,
         "BuilderToolSetNPCDebug",
         BuilderToolSetNPCDebug.class,
         5,
         5,
         false,
         BuilderToolSetNPCDebug::validateStructure,
         BuilderToolSetNPCDebug::deserialize
      );
   }

   public record PacketInfo(
      int id,
      @Nonnull String name,
      @Nonnull Class<? extends Packet> type,
      int fixedBlockSize,
      int maxSize,
      boolean compressed,
      @Nonnull BiFunction<ByteBuf, Integer, ValidationResult> validate,
      @Nonnull BiFunction<ByteBuf, Integer, Packet> deserialize
   ) {
   }
}
