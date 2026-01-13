package com.hypixel.hytale.builtin.buildertools;

import com.hypixel.hytale.builtin.buildertools.commands.CopyCommand;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditSession;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditSessionManager;
import com.hypixel.hytale.builtin.buildertools.tooloperations.ToolOperation;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.Axis;
import com.hypixel.hytale.math.matrix.Matrix4d;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.ColorLight;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.ModelTransform;
import com.hypixel.hytale.protocol.packets.buildertools.BrushOrigin;
import com.hypixel.hytale.protocol.packets.buildertools.BrushShape;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolAction;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolArgUpdate;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolEntityAction;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolExtrudeAction;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolGeneralAction;
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
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolStackArea;
import com.hypixel.hytale.protocol.packets.buildertools.PrefabUnselectPrefab;
import com.hypixel.hytale.protocol.packets.interface_.BlockChange;
import com.hypixel.hytale.protocol.packets.interface_.EditorBlocksChange;
import com.hypixel.hytale.protocol.packets.interface_.FluidChange;
import com.hypixel.hytale.protocol.packets.player.LoadHotbar;
import com.hypixel.hytale.protocol.packets.player.SaveHotbar;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.BrushData;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.BuilderTool;
import com.hypixel.hytale.server.core.command.commands.world.entity.EntityCloneCommand;
import com.hypixel.hytale.server.core.command.commands.world.entity.EntityRemoveCommand;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.io.handlers.IPacketHandler;
import com.hypixel.hytale.server.core.io.handlers.SubPacketHandler;
import com.hypixel.hytale.server.core.modules.entity.component.DynamicLight;
import com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.Interactable;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentDynamicLight;
import com.hypixel.hytale.server.core.modules.entity.component.PropComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.PreventPickup;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockPattern;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class BuilderToolsPacketHandler implements SubPacketHandler {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private final IPacketHandler packetHandler;

   public BuilderToolsPacketHandler(IPacketHandler packetHandler) {
      this.packetHandler = packetHandler;
   }

   @Override
   public void registerHandlers() {
      if (BuilderToolsPlugin.get().isDisabled()) {
         this.packetHandler.registerNoOpHandlers(400, 401, 412, 409, 403, 406, 407, 413, 414, 417);
      } else {
         this.packetHandler.registerHandler(106, p -> this.handle((LoadHotbar)p));
         this.packetHandler.registerHandler(107, p -> this.handle((SaveHotbar)p));
         this.packetHandler.registerHandler(400, p -> this.handle((BuilderToolArgUpdate)p));
         this.packetHandler.registerHandler(401, p -> this.handle((BuilderToolEntityAction)p));
         this.packetHandler.registerHandler(412, p -> this.handle((BuilderToolGeneralAction)p));
         this.packetHandler.registerHandler(409, p -> this.handle((BuilderToolSelectionUpdate)p));
         this.packetHandler.registerHandler(403, p -> this.handle((BuilderToolExtrudeAction)p));
         this.packetHandler.registerHandler(406, p -> this.handle((BuilderToolRotateClipboard)p));
         this.packetHandler.registerHandler(407, p -> this.handle((BuilderToolPasteClipboard)p));
         this.packetHandler.registerHandler(413, p -> this.handle((BuilderToolOnUseInteraction)p));
         this.packetHandler.registerHandler(410, p -> this.handle((BuilderToolSelectionToolAskForClipboard)p));
         this.packetHandler.registerHandler(414, p -> this.handle((BuilderToolLineAction)p));
         this.packetHandler.registerHandler(402, p -> this.handle((BuilderToolSetEntityTransform)p));
         this.packetHandler.registerHandler(420, p -> this.handle((BuilderToolSetEntityScale)p));
         this.packetHandler.registerHandler(405, p -> this.handle((BuilderToolSelectionTransform)p));
         this.packetHandler.registerHandler(404, p -> this.handle((BuilderToolStackArea)p));
         this.packetHandler.registerHandler(408, p -> this.handle((BuilderToolSetTransformationModeState)p));
         this.packetHandler.registerHandler(417, p -> this.handle((PrefabUnselectPrefab)p));
         this.packetHandler.registerHandler(421, p -> this.handle((BuilderToolSetEntityPickupEnabled)p));
         this.packetHandler.registerHandler(422, p -> this.handle((BuilderToolSetEntityLight)p));
         this.packetHandler.registerHandler(423, p -> this.handle((BuilderToolSetNPCDebug)p));
      }
   }

   static boolean hasPermission(@Nonnull Player player) {
      if (!player.hasPermission("hytale.editor.builderTools")) {
         player.sendMessage(Message.translation("server.builderTools.usageDenied"));
         return false;
      } else {
         return true;
      }
   }

   static boolean hasPermission(@Nonnull Player player, @Nonnull String permission) {
      if (!player.hasPermission(permission) && !player.hasPermission("hytale.editor.builderTools")) {
         player.sendMessage(Message.translation("server.builderTools.usageDenied"));
         return false;
      } else {
         return true;
      }
   }

   public void handle(@Nonnull BuilderToolSetTransformationModeState packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            if (hasPermission(playerComponent)) {
               LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
               ToolOperation.getOrCreatePrototypeSettings(playerRef.getUuid()).setInSelectionTransformationMode(packet.enabled);
            }
         });
      } else {
         throw new RuntimeException("Unable to process BuilderToolSetTransformationModeState packet. Player ref is invalid!");
      }
   }

   public void handle(@Nonnull BuilderToolArgUpdate packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            if (hasPermission(playerComponent, "hytale.editor.brush.config")) {
               BuilderToolsPlugin.get().onToolArgUpdate(playerRef, playerComponent, packet);
            }
         });
      } else {
         throw new RuntimeException("Unable to process BuilderToolArgUpdate packet. Player ref is invalid!");
      }
   }

   public void handle(@Nonnull LoadHotbar packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());

            assert playerComponent != null;

            playerComponent.getHotbarManager().loadHotbar(ref, packet.inventoryRow, store);
         });
      } else {
         throw new RuntimeException("Unable to process LoadHotbar packet. Player ref is invalid!");
      }
   }

   public void handle(@Nonnull SaveHotbar packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());

            assert playerComponent != null;

            playerComponent.getHotbarManager().saveHotbar(ref, packet.inventoryRow, store);
         });
      } else {
         throw new RuntimeException("Unable to process SaveHotbar packet. Player ref is invalid!");
      }
   }

   public void handle(@Nonnull BuilderToolEntityAction packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            if (hasPermission(playerComponent)) {
               int entityId = packet.entityId;
               Ref<EntityStore> entityReference = world.getEntityStore().getRefFromNetworkId(entityId);
               if (entityReference == null) {
                  playerComponent.sendMessage(Message.translation("server.general.entityNotFound").param("id", entityId));
               } else {
                  LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
                  switch (packet.action) {
                     case Freeze:
                        UUIDComponent uuidComponent = store.getComponent(entityReference, UUIDComponent.getComponentType());
                        if (uuidComponent != null) {
                           CommandManager.get().handleCommand(playerComponent, "npc freeze --toggle --entity " + uuidComponent.getUuid());
                        }
                        break;
                     case Clone:
                        world.execute(() -> EntityCloneCommand.cloneEntity(playerComponent, entityReference, store));
                        break;
                     case Remove:
                        world.execute(() -> EntityRemoveCommand.removeEntity(ref, entityReference, store));
                  }
               }
            }
         });
      } else {
         throw new RuntimeException("Unable to process BuilderToolEntityAction packet. Player ref is invalid!");
      }
   }

   public void handle(@Nonnull BuilderToolGeneralAction packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());

            assert playerComponent != null;

            LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
            switch (packet.action) {
               case HistoryUndo:
                  if (!hasPermission(playerComponent, "hytale.editor.history")) {
                     return;
                  }

                  BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.undo(r, 1, componentAccessor));
                  break;
               case HistoryRedo:
                  if (!hasPermission(playerComponent, "hytale.editor.history")) {
                     return;
                  }

                  BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.redo(r, 1, componentAccessor));
                  break;
               case SelectionCopy:
                  if (!hasPermission(playerComponent, "hytale.editor.selection.clipboard")) {
                     return;
                  }

                  CopyCommand.copySelection(ref, store);
                  break;
               case SelectionPosition1:
               case SelectionPosition2:
                  if (!hasPermission(playerComponent, "hytale.editor.selection.use")) {
                     return;
                  }

                  TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
                  BuilderToolsPlugin.BuilderState builderState = BuilderToolsPlugin.getState(playerComponent, playerRef);
                  Vector3d position = transformComponent.getPosition();
                  Vector3i intTriple = new Vector3i(MathUtil.floor(position.getX()), MathUtil.floor(position.getY()), MathUtil.floor(position.getZ()));
                  BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> {
                     if (packet.action == BuilderToolAction.SelectionPosition1) {
                        builderState.pos1(intTriple, componentAccessor);
                     } else {
                        builderState.pos2(intTriple, componentAccessor);
                     }
                  });
                  break;
               case ActivateToolMode:
                  if (!hasPermission(playerComponent)) {
                     return;
                  }

                  playerComponent.getInventory().setUsingToolsItem(true);
                  break;
               case DeactivateToolMode:
                  if (!hasPermission(playerComponent)) {
                     return;
                  }

                  playerComponent.getInventory().setUsingToolsItem(false);
            }
         });
      } else {
         throw new RuntimeException("Unable to process BuilderToolGeneralAction packet. Player ref is invalid!");
      }
   }

   public void handle(@Nonnull BuilderToolSelectionUpdate packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(
            () -> {
               Player playerComponent = store.getComponent(ref, Player.getComponentType());
               if (hasPermission(playerComponent, "hytale.editor.selection.use")) {
                  LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
                  BuilderToolsPlugin.addToQueue(
                     playerComponent,
                     playerRef,
                     (r, s, componentAccessor) -> s.update(packet.xMin, packet.yMin, packet.zMin, packet.xMax, packet.yMax, packet.zMax)
                  );
               }
            }
         );
      } else {
         throw new RuntimeException("Unable to process BuilderToolSelectionUpdate packet. Player ref is invalid!");
      }
   }

   public void handle(BuilderToolSelectionToolAskForClipboard packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(
            () -> {
               Player playerComponent = store.getComponent(ref, Player.getComponentType());
               if (hasPermission(playerComponent, "hytale.editor.selection.clipboard")) {
                  LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
                  PrototypePlayerBuilderToolSettings prototypeSettings = ToolOperation.getOrCreatePrototypeSettings(playerRef.getUuid());
                  BuilderToolsPlugin.addToQueue(
                     playerComponent,
                     playerRef,
                     (r, s, componentAccessor) -> {
                        BlockSelection selection = s.getSelection();
                        if (selection != null) {
                           EditorBlocksChange editorPacket = selection.toPacket();
                           BlockChange[] blocksChange = editorPacket.blocksChange;
                           prototypeSettings.setBlockChangesForPlaySelectionToolPasteMode(blocksChange);
                           ArrayList<PrototypePlayerBuilderToolSettings.FluidChange> fluidChanges = new ArrayList<>();
                           int anchorX = selection.getAnchorX();
                           int anchorY = selection.getAnchorY();
                           int anchorZ = selection.getAnchorZ();
                           selection.forEachFluid(
                              (x, y, z, fluidId, fluidLevel) -> fluidChanges.add(
                                 new PrototypePlayerBuilderToolSettings.FluidChange(x - anchorX, y - anchorY, z - anchorZ, fluidId, fluidLevel)
                              )
                           );
                           PrototypePlayerBuilderToolSettings.FluidChange[] fluidChangesArray = fluidChanges.toArray(
                              PrototypePlayerBuilderToolSettings.FluidChange[]::new
                           );
                           prototypeSettings.setFluidChangesForPlaySelectionToolPasteMode(fluidChangesArray);
                           FluidChange[] packetFluids = new FluidChange[fluidChangesArray.length];

                           for (int i = 0; i < fluidChangesArray.length; i++) {
                              PrototypePlayerBuilderToolSettings.FluidChange fc = fluidChangesArray[i];
                              packetFluids[i] = new FluidChange(fc.x(), fc.y(), fc.z(), fc.fluidId(), fc.fluidLevel());
                           }

                           playerRef.getPacketHandler().write(new BuilderToolSelectionToolReplyWithClipboard(blocksChange, packetFluids));
                        }
                     }
                  );
               }
            }
         );
      } else {
         throw new RuntimeException("Unable to process BuilderToolSelectionToolAskForClipboard packet. Player ref is invalid!");
      }
   }

   public int toInt(float value) {
      return (int)Math.floor(value + 0.1);
   }

   private void handle(@Nonnull BuilderToolSelectionTransform packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(
            () -> {
               Player playerComponent = store.getComponent(ref, Player.getComponentType());
               if (hasPermission(playerComponent, "hytale.editor.selection.clipboard")) {
                  LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
                  float[] tmx = new float[16];

                  for (int i = 0; i < packet.transformationMatrix.length; i++) {
                     tmx[i] = this.toInt(packet.transformationMatrix[i]);
                  }

                  Matrix4d transformationMatrix = new Matrix4d()
                     .assign(
                        tmx[0], tmx[4], tmx[8], tmx[12], tmx[1], tmx[5], tmx[9], tmx[13], tmx[2], tmx[6], tmx[10], tmx[14], tmx[3], tmx[7], tmx[11], tmx[15]
                     );
                  Vector3i initialSelectionMin = new Vector3i(packet.initialSelectionMin.x, packet.initialSelectionMin.y, packet.initialSelectionMin.z);
                  Vector3i initialSelectionMax = new Vector3i(packet.initialSelectionMax.x, packet.initialSelectionMax.y, packet.initialSelectionMax.z);
                  Vector3f rotationOrigin = new Vector3f(packet.initialRotationOrigin.x, packet.initialRotationOrigin.y, packet.initialRotationOrigin.z);
                  PrototypePlayerBuilderToolSettings prototypeSettings = ToolOperation.getOrCreatePrototypeSettings(playerRef.getUuid());
                  BuilderToolsPlugin.addToQueue(
                     playerComponent,
                     playerRef,
                     (r, s, componentAccessor) -> {
                        int blockCount = s.getSelection().getSelectionVolume();
                        boolean large = blockCount > 20000;
                        if (large) {
                           playerComponent.sendMessage(Message.translation("server.builderTools.selection.large.warning"));
                        }

                        if (prototypeSettings.getBlockChangesForPlaySelectionToolPasteMode() == null) {
                           s.select(initialSelectionMin, initialSelectionMax, "SelectionTranslatePacket", componentAccessor);
                           if (packet.cutOriginal) {
                              s.copyOrCut(
                                 r,
                                 initialSelectionMin.x,
                                 initialSelectionMin.y,
                                 initialSelectionMin.z,
                                 initialSelectionMax.x,
                                 initialSelectionMax.y,
                                 initialSelectionMax.z,
                                 138,
                                 store
                              );
                           } else {
                              s.copyOrCut(
                                 r,
                                 initialSelectionMin.x,
                                 initialSelectionMin.y,
                                 initialSelectionMin.z,
                                 initialSelectionMax.x,
                                 initialSelectionMax.y,
                                 initialSelectionMax.z,
                                 136,
                                 store
                              );
                           }

                           BlockSelection selection = s.getSelection();
                           BlockChange[] blocksChange = selection.toPacket().blocksChange;
                           prototypeSettings.setBlockChangesForPlaySelectionToolPasteMode(blocksChange);
                           ArrayList<PrototypePlayerBuilderToolSettings.FluidChange> fluidChanges = new ArrayList<>();
                           int anchorX = selection.getAnchorX();
                           int anchorY = selection.getAnchorY();
                           int anchorZ = selection.getAnchorZ();
                           selection.forEachFluid(
                              (x, y, z, fluidId, fluidLevel) -> fluidChanges.add(
                                 new PrototypePlayerBuilderToolSettings.FluidChange(x - anchorX, y - anchorY, z - anchorZ, fluidId, fluidLevel)
                              )
                           );
                           prototypeSettings.setFluidChangesForPlaySelectionToolPasteMode(
                              fluidChanges.toArray(PrototypePlayerBuilderToolSettings.FluidChange[]::new)
                           );
                           prototypeSettings.setBlockChangeOffsetOrigin(new Vector3i(selection.getX(), selection.getY(), selection.getZ()));
                        }

                        Vector3i blockChangeOffsetOrigin = prototypeSettings.getBlockChangeOffsetOrigin();
                        if (packet.initialPastePointForClipboardPaste != null) {
                           blockChangeOffsetOrigin = new Vector3i(
                              packet.initialPastePointForClipboardPaste.x,
                              packet.initialPastePointForClipboardPaste.y,
                              packet.initialPastePointForClipboardPaste.z
                           );
                        }

                        if (blockChangeOffsetOrigin == null) {
                           playerComponent.sendMessage(Message.translation("server.builderTools.selection.noBlockChangeOffsetOrigin"));
                        } else {
                           s.transformThenPasteClipboard(
                              prototypeSettings.getBlockChangesForPlaySelectionToolPasteMode(),
                              prototypeSettings.getFluidChangesForPlaySelectionToolPasteMode(),
                              transformationMatrix,
                              rotationOrigin,
                              blockChangeOffsetOrigin,
                              componentAccessor
                           );
                           s.select(initialSelectionMin, initialSelectionMax, "SelectionTranslatePacket", componentAccessor);
                           s.transformSelectionPoints(transformationMatrix, rotationOrigin);
                           if (large) {
                              playerComponent.sendMessage(Message.translation("server.builderTools.selection.large.complete"));
                           }

                           if (packet.isExitingTransformMode) {
                              prototypeSettings.setInSelectionTransformationMode(false);
                           }
                        }
                     }
                  );
               }
            }
         );
      } else {
         throw new RuntimeException("Unable to process BuilderToolSelectionTransform packet. Player ref is invalid!");
      }
   }

   public void handle(@Nonnull BuilderToolExtrudeAction packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(
            () -> {
               Player playerComponent = store.getComponent(ref, Player.getComponentType());
               if (hasPermission(playerComponent, "hytale.editor.selection.modify")) {
                  BuilderTool builderTool = BuilderTool.getActiveBuilderTool(playerComponent);
                  if (builderTool != null && builderTool.getId().equals("Extrude")) {
                     ItemStack activeItemStack = playerComponent.getInventory().getItemInHand();
                     BuilderTool.ArgData args = builderTool.getItemArgData(activeItemStack);
                     int extrudeDepth = (Integer)args.tool().get("ExtrudeDepth");
                     int extrudeRadius = (Integer)args.tool().get("ExtrudeRadius");
                     int blockId = ((BlockPattern)args.tool().get("ExtrudeMaterial")).firstBlock();
                     LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
                     BuilderToolsPlugin.addToQueue(
                        playerComponent,
                        playerRef,
                        (r, s, componentAccessor) -> s.extendFace(
                           packet.x,
                           packet.y,
                           packet.z,
                           packet.xNormal,
                           packet.yNormal,
                           packet.zNormal,
                           extrudeDepth,
                           extrudeRadius,
                           blockId,
                           null,
                           null,
                           componentAccessor
                        )
                     );
                  }
               }
            }
         );
      } else {
         throw new RuntimeException("Unable to process BuilderToolExtrudeAction packet. Player ref is invalid!");
      }
   }

   public void handle(@Nonnull BuilderToolStackArea packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());

            assert playerComponent != null;

            if (hasPermission(playerComponent, "hytale.editor.selection.clipboard")) {
               LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
               BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> {
                  s.select(this.fromBlockPosition(packet.selectionMin), this.fromBlockPosition(packet.selectionMax), "Extrude", componentAccessor);
                  s.stack(r, new Vector3i(packet.xNormal, packet.yNormal, packet.zNormal), packet.numStacks, true, 0, componentAccessor);
               });
            }
         });
      } else {
         throw new RuntimeException("Unable to process BuilderToolStackArea packet. Player ref is invalid!");
      }
   }

   @Nonnull
   public Vector3i fromBlockPosition(@Nonnull BlockPosition position) {
      return new Vector3i(position.x, position.y, position.z);
   }

   public void handle(@Nonnull BuilderToolRotateClipboard packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(
            () -> {
               Player playerComponent = store.getComponent(ref, Player.getComponentType());
               if (hasPermission(playerComponent, "hytale.editor.selection.clipboard")) {
                  Axis axis = packet.axis == com.hypixel.hytale.protocol.packets.buildertools.Axis.X
                     ? Axis.X
                     : (packet.axis == com.hypixel.hytale.protocol.packets.buildertools.Axis.Y ? Axis.Y : Axis.Z);
                  LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
                  BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.rotate(r, axis, packet.angle, componentAccessor));
               }
            }
         );
      } else {
         throw new RuntimeException("Unable to process BuilderToolPasteClipboard packet. Player ref is invalid!");
      }
   }

   public void handle(@Nonnull BuilderToolPasteClipboard packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(
            () -> {
               Player playerComponent = store.getComponent(ref, Player.getComponentType());
               if (hasPermission(playerComponent, "hytale.editor.selection.clipboard")) {
                  LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
                  BuilderToolsPlugin.addToQueue(
                     playerComponent, playerRef, (r, s, componentAccessor) -> s.paste(r, packet.x, packet.y, packet.z, componentAccessor)
                  );
               }
            }
         );
      } else {
         throw new RuntimeException("Unable to process BuilderToolPasteClipboard packet. Player ref is invalid!");
      }
   }

   public void handle(@Nonnull BuilderToolLineAction packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(
            () -> {
               Player playerComponent = store.getComponent(ref, Player.getComponentType());
               if (hasPermission(playerComponent, "hytale.editor.brush.use")) {
                  BuilderTool builderTool = BuilderTool.getActiveBuilderTool(playerComponent);
                  if (builderTool != null && builderTool.getId().equals("Line")) {
                     BuilderTool.ArgData args = builderTool.getItemArgData(playerComponent.getInventory().getItemInHand());
                     BrushData.Values brushData = args.brush();
                     int lineWidth = (Integer)args.tool().get("bLineWidth");
                     int lineHeight = (Integer)args.tool().get("cLineHeight");
                     BrushShape lineShape = BrushShape.valueOf((String)args.tool().get("dLineShape"));
                     BrushOrigin lineOrigin = BrushOrigin.valueOf((String)args.tool().get("eLineOrigin"));
                     int lineWallThickness = (Integer)args.tool().get("fLineWallThickness");
                     int lineSpacing = (Integer)args.tool().get("gLineSpacing");
                     int lineDensity = (Integer)args.tool().get("hLineDensity");
                     BlockPattern lineMaterial = (BlockPattern)args.tool().get("aLineMaterial");
                     LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
                     BuilderToolsPlugin.addToQueue(
                        playerComponent,
                        playerRef,
                        (r, s, componentAccessor) -> s.editLine(
                           packet.xStart,
                           packet.yStart,
                           packet.zStart,
                           packet.xEnd,
                           packet.yEnd,
                           packet.zEnd,
                           lineMaterial,
                           lineWidth,
                           lineHeight,
                           lineWallThickness,
                           lineShape,
                           lineOrigin,
                           lineSpacing,
                           lineDensity,
                           ToolOperation.combineMasks(brushData, s.getGlobalMask()),
                           componentAccessor
                        )
                     );
                  }
               }
            }
         );
      } else {
         throw new RuntimeException("Unable to process BuilderToolLineAction packet. Player ref is invalid!");
      }
   }

   public void handle(@Nonnull BuilderToolOnUseInteraction packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            if (hasPermission(playerComponent, "hytale.editor.brush.use")) {
               LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
               BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.edit(ref, packet, componentAccessor));
            }
         });
      } else {
         throw new RuntimeException("Unable to process BuilderToolOnUseInteraction packet. Player ref is invalid!");
      }
   }

   public void handle(@Nonnull BuilderToolSetEntityTransform packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(
            () -> {
               Player playerComponent = store.getComponent(ref, Player.getComponentType());

               assert playerComponent != null;

               if (hasPermission(playerComponent)) {
                  Ref<EntityStore> entityReference = world.getEntityStore().getRefFromNetworkId(packet.entityId);
                  if (entityReference != null) {
                     TransformComponent transformComponent = store.getComponent(entityReference, TransformComponent.getComponentType());

                     assert transformComponent != null;

                     HeadRotation headRotation = store.getComponent(entityReference, HeadRotation.getComponentType());
                     ModelTransform modelTransform = packet.modelTransform;
                     if (modelTransform != null) {
                        boolean hasPosition = modelTransform.position != null;
                        boolean hasLookOrientation = modelTransform.lookOrientation != null;
                        boolean hasBodyOrientation = modelTransform.bodyOrientation != null;
                        if (hasPosition) {
                           transformComponent.getPosition().assign(modelTransform.position.x, modelTransform.position.y, modelTransform.position.z);
                        }

                        if (hasLookOrientation && headRotation != null) {
                           headRotation.getRotation()
                              .assign(modelTransform.lookOrientation.pitch, modelTransform.lookOrientation.yaw, modelTransform.lookOrientation.roll);
                        }

                        if (hasBodyOrientation) {
                           transformComponent.getRotation()
                              .assign(modelTransform.bodyOrientation.pitch, modelTransform.bodyOrientation.yaw, modelTransform.bodyOrientation.roll);
                        }

                        if (hasPosition || hasLookOrientation || hasBodyOrientation) {
                           transformComponent.markChunkDirty(store);
                        }
                     }
                  }
               }
            }
         );
      } else {
         throw new RuntimeException("Unable to process BuilderToolSetEntityTransform packet. Player ref is invalid!");
      }
   }

   public void handle(@Nonnull PrefabUnselectPrefab packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            if (hasPermission(playerComponent)) {
               LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
               PrefabEditSessionManager prefabEditSessionManager = BuilderToolsPlugin.get().getPrefabEditSessionManager();
               PrefabEditSession prefabEditSession = prefabEditSessionManager.getPrefabEditSession(playerRef.getUuid());
               if (prefabEditSession == null) {
                  playerComponent.sendMessage(Message.translation("server.commands.editprefab.notInEditSession"));
               } else {
                  if (prefabEditSession.clearSelectedPrefab(ref, store)) {
                     playerComponent.sendMessage(Message.translation("server.commands.editprefab.unselected"));
                  } else {
                     playerComponent.sendMessage(Message.translation("server.commands.editprefab.noPrefabSelected"));
                  }
               }
            }
         });
      } else {
         throw new RuntimeException("Unable to process PrefabUnselectPrefab packet. Player ref is invalid!");
      }
   }

   public void handle(@Nonnull BuilderToolSetEntityScale packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            if (hasPermission(playerComponent)) {
               Ref<EntityStore> entityReference = world.getEntityStore().getRefFromNetworkId(packet.entityId);
               if (entityReference != null) {
                  PropComponent propComponent = store.getComponent(entityReference, PropComponent.getComponentType());
                  if (propComponent != null) {
                     EntityScaleComponent scaleComponent = store.getComponent(entityReference, EntityScaleComponent.getComponentType());
                     if (scaleComponent == null) {
                        scaleComponent = new EntityScaleComponent(packet.scale);
                        store.addComponent(entityReference, EntityScaleComponent.getComponentType(), scaleComponent);
                     } else {
                        scaleComponent.setScale(packet.scale);
                     }
                  }
               }
            }
         });
      } else {
         throw new RuntimeException("Unable to process BuilderToolSetEntityScale packet. Player ref is invalid!");
      }
   }

   public void handle(@Nonnull BuilderToolSetEntityPickupEnabled packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            if (hasPermission(playerComponent)) {
               Ref<EntityStore> entityReference = world.getEntityStore().getRefFromNetworkId(packet.entityId);
               if (entityReference != null) {
                  PropComponent propComponent = store.getComponent(entityReference, PropComponent.getComponentType());
                  if (propComponent != null) {
                     if (packet.enabled) {
                        store.ensureComponent(entityReference, Interactable.getComponentType());
                        if (store.getComponent(entityReference, PreventPickup.getComponentType()) != null) {
                           store.removeComponent(entityReference, PreventPickup.getComponentType());
                        }

                        Interactions interactionsComponent = store.getComponent(entityReference, Interactions.getComponentType());
                        if (interactionsComponent == null) {
                           interactionsComponent = new Interactions();
                           store.addComponent(entityReference, Interactions.getComponentType(), interactionsComponent);
                        }

                        interactionsComponent.setInteractionId(InteractionType.Use, "*PickupItem");
                        interactionsComponent.setInteractionHint("server.interactionHints.pickup");
                     } else {
                        if (store.getComponent(entityReference, Interactable.getComponentType()) != null) {
                           store.removeComponent(entityReference, Interactable.getComponentType());
                        }

                        if (store.getComponent(entityReference, Interactions.getComponentType()) != null) {
                           store.removeComponent(entityReference, Interactions.getComponentType());
                        }

                        store.ensureComponent(entityReference, PreventPickup.getComponentType());
                     }
                  }
               }
            }
         });
      } else {
         throw new RuntimeException("Unable to process BuilderToolSetEntityPickupEnabled packet. Player ref is invalid!");
      }
   }

   public void handle(@Nonnull BuilderToolSetEntityLight packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            if (hasPermission(playerComponent)) {
               Ref<EntityStore> entityReference = world.getEntityStore().getRefFromNetworkId(packet.entityId);
               if (entityReference != null) {
                  if (packet.light == null) {
                     store.removeComponent(entityReference, DynamicLight.getComponentType());
                     store.removeComponent(entityReference, PersistentDynamicLight.getComponentType());
                  } else {
                     ColorLight colorLight = new ColorLight(packet.light.radius, packet.light.red, packet.light.green, packet.light.blue);
                     store.putComponent(entityReference, DynamicLight.getComponentType(), new DynamicLight(colorLight));
                     store.putComponent(entityReference, PersistentDynamicLight.getComponentType(), new PersistentDynamicLight(colorLight));
                  }
               }
            }
         });
      } else {
         throw new RuntimeException("Unable to process BuilderToolSetEntityLight packet. Player ref is invalid!");
      }
   }

   public void handle(@Nonnull BuilderToolSetNPCDebug packet) {
      PlayerRef playerRef = this.packetHandler.getPlayerRef();
      Ref<EntityStore> ref = playerRef.getReference();
      if (ref != null && ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         World world = store.getExternalData().getWorld();
         world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            if (hasPermission(playerComponent)) {
               Ref<EntityStore> entityReference = world.getEntityStore().getRefFromNetworkId(packet.entityId);
               if (entityReference != null) {
                  UUIDComponent uuidComponent = store.getComponent(entityReference, UUIDComponent.getComponentType());
                  if (uuidComponent != null) {
                     UUID uuid = uuidComponent.getUuid();
                     String command = packet.enabled ? "npc debug set display --entity " + uuid : "npc debug clear --entity " + uuid;
                     CommandManager.get().handleCommand(playerComponent, command);
                  }
               }
            }
         });
      } else {
         throw new RuntimeException("Unable to process BuilderToolSetNPCDebug packet. Player ref is invalid!");
      }
   }
}
