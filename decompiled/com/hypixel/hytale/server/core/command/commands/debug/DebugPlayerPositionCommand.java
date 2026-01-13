package com.hypixel.hytale.server.core.command.commands.debug;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.debug.DebugUtils;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.PendingTeleport;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class DebugPlayerPositionCommand extends AbstractPlayerCommand {
   public DebugPlayerPositionCommand() {
      super("debugplayerposition", "server.commands.debugplayerposition.desc");
   }

   @Override
   protected void execute(
      @NonNullDecl CommandContext context,
      @NonNullDecl Store<EntityStore> store,
      @NonNullDecl Ref<EntityStore> ref,
      @NonNullDecl PlayerRef playerRef,
      @NonNullDecl World world
   ) {
      Transform transform = store.getComponent(ref, TransformComponent.getComponentType()).getTransform();
      Vector3f headRotation = store.getComponent(ref, HeadRotation.getComponentType()).getRotation();
      Teleport teleport = store.getComponent(ref, Teleport.getComponentType());
      PendingTeleport pendingTeleport = store.getComponent(ref, PendingTeleport.getComponentType());
      String teleportFmt = teleport == null ? "none" : fmtPos(teleport.getPosition());
      String pendingTeleportFmt = pendingTeleport == null ? "none" : fmtPos(pendingTeleport.getPosition());
      Message message = Message.translation("server.commands.debugplayerposition.result")
         .param("bodyPosition", fmtPos(transform.getPosition()))
         .param("bodyRotation", fmtRot(transform.getRotation()))
         .param("headRotation", fmtRot(headRotation))
         .param("teleport", teleportFmt)
         .param("pendingTeleport", pendingTeleportFmt);
      playerRef.sendMessage(message);
      Vector3f blue = new Vector3f(0.137F, 0.867F, 0.882F);
      DebugUtils.addSphere(world, transform.getPosition(), blue, 0.5, 30.0F);
      playerRef.sendMessage(Message.translation("server.commands.debugplayerposition.notify").color("#23DDE1"));
   }

   private static String fmtPos(Vector3d vector) {
      String fmt = "%.1f";
      return String.format("%.1f", vector.getX()) + ", " + String.format("%.1f", vector.getY()) + ", " + String.format("%.1f", vector.getZ());
   }

   private static String fmtRot(Vector3f vector) {
      return "Pitch=" + fmtDegrees(vector.getPitch()) + ", Yaw=" + fmtDegrees(vector.getYaw()) + ", Roll=" + fmtDegrees(vector.getRoll());
   }

   private static String fmtDegrees(float radians) {
      return String.format("%.1f", Math.toDegrees(radians)) + "°";
   }
}
