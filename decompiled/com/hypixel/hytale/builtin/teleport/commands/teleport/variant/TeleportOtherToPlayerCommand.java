package com.hypixel.hytale.builtin.teleport.commands.teleport.variant;

import com.hypixel.hytale.builtin.teleport.components.TeleportHistory;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.permissions.HytalePermissions;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class TeleportOtherToPlayerCommand extends CommandBase {
   private static final Message MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD = Message.translation("server.commands.errors.playerNotInWorld");
   private static final Message MESSAGE_COMMANDS_ERRORS_TARGET_NOT_IN_WORLD = Message.translation("server.commands.errors.targetNotInWorld");
   private static final Message MESSAGE_COMMANDS_TELEPORT_TELEPORTED_OTHER_TO_PLAYER = Message.translation("server.commands.teleport.teleportedOtherToPlayer");
   @Nonnull
   private final RequiredArg<PlayerRef> targetPlayerArg = this.withRequiredArg(
      "targetPlayer", "server.commands.teleport.targetPlayer.desc", ArgTypes.PLAYER_REF
   );
   @Nonnull
   private final RequiredArg<PlayerRef> playerArg = this.withRequiredArg("player", "server.commands.argtype.player.desc", ArgTypes.PLAYER_REF);

   public TeleportOtherToPlayerCommand() {
      super("server.commands.teleport.otherToPlayer.desc");
      this.requirePermission(HytalePermissions.fromCommand("teleport.other"));
   }

   @Override
   protected void executeSync(@Nonnull CommandContext context) {
      PlayerRef playerToTpRef = this.playerArg.get(context);
      Ref<EntityStore> sourceRef = playerToTpRef.getReference();
      if (sourceRef != null && sourceRef.isValid()) {
         PlayerRef targetPlayerRef = this.targetPlayerArg.get(context);
         Ref<EntityStore> targetRef = targetPlayerRef.getReference();
         if (targetRef != null && targetRef.isValid()) {
            Store<EntityStore> sourceStore = sourceRef.getStore();
            World sourceWorld = sourceStore.getExternalData().getWorld();
            Store<EntityStore> targetStore = targetRef.getStore();
            World targetWorld = targetStore.getExternalData().getWorld();
            sourceWorld.execute(
               () -> {
                  TransformComponent transformComponent = sourceStore.getComponent(sourceRef, TransformComponent.getComponentType());

                  assert transformComponent != null;

                  HeadRotation headRotationComponent = sourceStore.getComponent(sourceRef, HeadRotation.getComponentType());

                  assert headRotationComponent != null;

                  Vector3d pos = transformComponent.getPosition().clone();
                  Vector3f rotation = headRotationComponent.getRotation().clone();
                  targetWorld.execute(
                     () -> {
                        TransformComponent targetTransformComponent = targetStore.getComponent(targetRef, TransformComponent.getComponentType());

                        assert targetTransformComponent != null;

                        HeadRotation targetHeadRotationComponent = targetStore.getComponent(targetRef, HeadRotation.getComponentType());

                        assert targetHeadRotationComponent != null;

                        Vector3d targetPosition = targetTransformComponent.getPosition().clone();
                        Vector3f targetRotation = targetTransformComponent.getRotation().clone();
                        Vector3f targetHeadRotation = targetHeadRotationComponent.getRotation().clone();
                        sourceWorld.execute(
                           () -> {
                              Teleport teleport = new Teleport(targetWorld, targetPosition, targetRotation)
                                 .withHeadRotation(targetHeadRotation)
                                 .withResetRoll();
                              sourceStore.addComponent(sourceRef, Teleport.getComponentType(), teleport);
                              PlayerRef sourcePlayerRefComponent = sourceStore.getComponent(sourceRef, PlayerRef.getComponentType());

                              assert sourcePlayerRefComponent != null;

                              PlayerRef targetPlayerRefComponent = targetStore.getComponent(targetRef, PlayerRef.getComponentType());

                              assert targetPlayerRefComponent != null;

                              context.sendMessage(
                                 MESSAGE_COMMANDS_TELEPORT_TELEPORTED_OTHER_TO_PLAYER.param("targetName", sourcePlayerRefComponent.getUsername())
                                    .param("toName", targetPlayerRefComponent.getUsername())
                              );
                              sourceStore.ensureAndGetComponent(sourceRef, TeleportHistory.getComponentType())
                                 .append(
                                    sourceWorld,
                                    pos,
                                    rotation,
                                    "Teleport to " + targetPlayerRefComponent.getUsername() + " by " + context.sender().getDisplayName()
                                 );
                           }
                        );
                     }
                  );
               }
            );
         } else {
            context.sendMessage(MESSAGE_COMMANDS_ERRORS_TARGET_NOT_IN_WORLD);
         }
      } else {
         context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
      }
   }
}
