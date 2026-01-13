package com.hypixel.hytale.builtin.fluid;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.AssetArgumentType;
import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeIntPosition;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import javax.annotation.Nonnull;

public class FluidCommand extends AbstractCommandCollection {
   private static final SingleArgumentType<Fluid> FLUID_ARG = new AssetArgumentType("Fluid", Fluid.class, "");

   public FluidCommand() {
      super("fluid", "Fluid debug commands");
      this.addSubCommand(new FluidCommand.SetCommand());
      this.addSubCommand(new FluidCommand.GetCommand());
      this.addSubCommand(new FluidCommand.SetRadiusCommand());
   }

   public static class GetCommand extends AbstractPlayerCommand {
      @Nonnull
      private static final Message MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_LOOKING_AT_BLOCK = Message.translation("server.commands.errors.playerNotLookingAtBlock");
      @Nonnull
      private static final Message MESSAGE_COMMANDS_NO_SECTION_COMPONENT = Message.translation("server.commands.noSectionComponent");
      @Nonnull
      private final OptionalArg<RelativeIntPosition> targetOffset = this.withOptionalArg("offset", "", ArgTypes.RELATIVE_BLOCK_POSITION);

      public GetCommand() {
         super("get", "Gets the fluid at the target position");
      }

      @Override
      protected void execute(
         @Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world
      ) {
         RelativeIntPosition offset = this.targetOffset.get(context);
         Vector3i blockTarget = TargetUtil.getTargetBlock(ref, 8.0, store);
         if (blockTarget == null) {
            playerRef.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_LOOKING_AT_BLOCK);
         } else {
            ChunkStore chunkStore = world.getChunkStore();
            Vector3i pos = offset == null ? blockTarget : offset.getBlockPosition(blockTarget.toVector3d(), chunkStore);
            chunkStore.getChunkSectionReferenceAsync(ChunkUtil.chunkCoordinate(pos.x), ChunkUtil.chunkCoordinate(pos.y), ChunkUtil.chunkCoordinate(pos.z))
               .thenAcceptAsync(
                  section -> {
                     Store<ChunkStore> sectionStore = section.getStore();
                     FluidSection fluidSection = sectionStore.getComponent((Ref<ChunkStore>)section, FluidSection.getComponentType());
                     if (fluidSection == null) {
                        playerRef.sendMessage(MESSAGE_COMMANDS_NO_SECTION_COMPONENT);
                     } else {
                        int index = ChunkUtil.indexBlock(pos.x, pos.y, pos.z);
                        Fluid fluid = fluidSection.getFluid(index);
                        byte level = fluidSection.getFluidLevel(index);
                        playerRef.sendMessage(
                           Message.translation("server.commands.get.success")
                              .param("x", pos.x)
                              .param("y", pos.y)
                              .param("z", pos.z)
                              .param("id", fluid.getId())
                              .param("level", (int)level)
                        );
                     }
                  },
                  world
               );
         }
      }
   }

   public static class SetCommand extends AbstractPlayerCommand {
      @Nonnull
      private static final Message MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_LOOKING_AT_BLOCK = Message.translation("server.commands.errors.playerNotLookingAtBlock");
      @Nonnull
      private static final Message MESSAGE_COMMANDS_SET_UNKNOWN_FLUID = Message.translation("server.commands.set.unknownFluid");
      @Nonnull
      private static final Message MESSAGE_COMMANDS_NO_SECTION_COMPONENT = Message.translation("server.commands.noSectionComponent");
      @Nonnull
      private final RequiredArg<Fluid> fluid = this.withRequiredArg("fluid", "", FluidCommand.FLUID_ARG);
      @Nonnull
      private final RequiredArg<Integer> level = this.withRequiredArg("level", "", ArgTypes.INTEGER);
      @Nonnull
      private final OptionalArg<RelativeIntPosition> targetOffset = this.withOptionalArg("offset", "", ArgTypes.RELATIVE_BLOCK_POSITION);

      public SetCommand() {
         super("set", "Changes the fluid at the target position");
      }

      @Override
      protected void execute(
         @Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world
      ) {
         RelativeIntPosition offset = this.targetOffset.get(context);
         Vector3i blockTarget = TargetUtil.getTargetBlock(ref, 8.0, store);
         if (blockTarget == null) {
            playerRef.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_LOOKING_AT_BLOCK);
         } else {
            ChunkStore chunkStore = world.getChunkStore();
            Vector3i pos = offset == null ? blockTarget : offset.getBlockPosition(blockTarget.toVector3d(), chunkStore);
            Fluid fluid = this.fluid.get(context);
            if (fluid == null) {
               playerRef.sendMessage(MESSAGE_COMMANDS_SET_UNKNOWN_FLUID);
            } else {
               Integer level = this.level.get(context);
               if (level > fluid.getMaxFluidLevel()) {
                  level = fluid.getMaxFluidLevel();
                  playerRef.sendMessage(Message.translation("server.commands.set.maxFluidLevelClamped").param("level", fluid.getMaxFluidLevel()));
               }

               Integer finalLevel = level;
               chunkStore.getChunkSectionReferenceAsync(ChunkUtil.chunkCoordinate(pos.x), ChunkUtil.chunkCoordinate(pos.y), ChunkUtil.chunkCoordinate(pos.z))
                  .thenAcceptAsync(
                     section -> {
                        Store<ChunkStore> sectionStore = section.getStore();
                        FluidSection fluidSection = sectionStore.getComponent((Ref<ChunkStore>)section, FluidSection.getComponentType());
                        if (fluidSection == null) {
                           playerRef.sendMessage(MESSAGE_COMMANDS_NO_SECTION_COMPONENT);
                        } else {
                           int index = ChunkUtil.indexBlock(pos.x, pos.y, pos.z);
                           fluidSection.setFluid(index, fluid, finalLevel.byteValue());
                           playerRef.sendMessage(
                              Message.translation("server.commands.set.success")
                                 .param("x", pos.x)
                                 .param("y", pos.y)
                                 .param("z", pos.z)
                                 .param("id", fluid.getId())
                                 .param("level", finalLevel)
                           );
                           ChunkSection chunkSection = sectionStore.getComponent((Ref<ChunkStore>)section, ChunkSection.getComponentType());
                           WorldChunk worldChunk = sectionStore.getComponent(chunkSection.getChunkColumnReference(), WorldChunk.getComponentType());
                           worldChunk.markNeedsSaving();
                           worldChunk.setTicking(pos.x, pos.y, pos.z, true);
                        }
                     },
                     world
                  );
            }
         }
      }
   }

   public static class SetRadiusCommand extends AbstractPlayerCommand {
      @Nonnull
      private static final Message MESSAGE_COMMANDS_SET_UNKNOWN_FLUID = Message.translation("server.commands.set.unknownFluid");
      @Nonnull
      private static final Message MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_LOOKING_AT_BLOCK = Message.translation("server.commands.errors.playerNotLookingAtBlock");
      @Nonnull
      private final RequiredArg<Integer> radius = this.withRequiredArg("radius", "", ArgTypes.INTEGER);
      @Nonnull
      private final RequiredArg<Fluid> fluid = this.withRequiredArg("fluid", "", FluidCommand.FLUID_ARG);
      @Nonnull
      private final RequiredArg<Integer> level = this.withRequiredArg("level", "", ArgTypes.INTEGER);
      @Nonnull
      private final OptionalArg<RelativeIntPosition> targetOffset = this.withOptionalArg("offset", "", ArgTypes.RELATIVE_BLOCK_POSITION);

      public SetRadiusCommand() {
         super("setradius", "Changes the fluid at the player position in a given radius");
      }

      @Override
      protected void execute(
         @Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world
      ) {
         RelativeIntPosition offset = this.targetOffset.get(context);
         Vector3i blockTarget = TargetUtil.getTargetBlock(ref, 8.0, store);
         if (blockTarget == null) {
            playerRef.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_LOOKING_AT_BLOCK);
         } else {
            ChunkStore chunkStore = world.getChunkStore();
            Vector3i pos = offset == null ? blockTarget : offset.getBlockPosition(blockTarget.toVector3d(), chunkStore);
            Fluid fluid = this.fluid.get(context);
            if (fluid == null) {
               playerRef.sendMessage(MESSAGE_COMMANDS_SET_UNKNOWN_FLUID);
            } else {
               Integer level = this.level.get(context);
               if (level > fluid.getMaxFluidLevel()) {
                  level = fluid.getMaxFluidLevel();
                  playerRef.sendMessage(Message.translation("server.commands.set.maxFluidLevelClamped").param("level", fluid.getMaxFluidLevel()));
               }

               Integer radius = this.radius.get(context);
               int minX = pos.x - radius;
               int maxX = pos.x + radius;
               int minY = pos.y - radius;
               int maxY = pos.y + radius;
               int minZ = pos.z - radius;
               int maxZ = pos.z + radius;
               int minCX = ChunkUtil.chunkCoordinate(minX);
               int maxCX = ChunkUtil.chunkCoordinate(maxX);
               int minCY = ChunkUtil.chunkCoordinate(minY);
               int maxCY = ChunkUtil.chunkCoordinate(maxY);
               int minCZ = ChunkUtil.chunkCoordinate(minZ);
               int maxCZ = ChunkUtil.chunkCoordinate(maxZ);
               Integer finalLevel = level;

               for (int cx = minCX; cx <= maxCX; cx++) {
                  for (int cz = minCZ; cz <= maxCZ; cz++) {
                     int relMinX = MathUtil.clamp(minX - ChunkUtil.minBlock(cx), 0, 32);
                     int relMaxX = MathUtil.clamp(maxX - ChunkUtil.minBlock(cx), 0, 32);
                     int relMinZ = MathUtil.clamp(minZ - ChunkUtil.minBlock(cz), 0, 32);
                     int relMaxZ = MathUtil.clamp(maxZ - ChunkUtil.minBlock(cz), 0, 32);

                     for (int cy = minCY; cy <= maxCY; cy++) {
                        chunkStore.getChunkSectionReferenceAsync(cx, cy, cz).thenAcceptAsync(section -> {
                           Store<ChunkStore> sectionStore = section.getStore();
                           FluidSection fluidSection = sectionStore.getComponent((Ref<ChunkStore>)section, FluidSection.getComponentType());
                           if (fluidSection != null) {
                              int relMinY = MathUtil.clamp(minY - ChunkUtil.minBlock(fluidSection.getY()), 0, 32);
                              int relMaxY = MathUtil.clamp(maxY - ChunkUtil.minBlock(fluidSection.getY()), 0, 32);
                              ChunkSection sectionComp = sectionStore.getComponent((Ref<ChunkStore>)section, ChunkSection.getComponentType());
                              WorldChunk worldChunk = sectionStore.getComponent(sectionComp.getChunkColumnReference(), WorldChunk.getComponentType());

                              for (int y = relMinY; y < relMaxY; y++) {
                                 for (int z = relMinZ; z < relMaxZ; z++) {
                                    for (int x = relMinX; x < relMaxX; x++) {
                                       int index = ChunkUtil.indexBlock(x, y, z);
                                       fluidSection.setFluid(index, fluid, finalLevel.byteValue());
                                       worldChunk.setTicking(pos.x, pos.y, pos.z, true);
                                    }
                                 }
                              }

                              worldChunk.markNeedsSaving();
                           }
                        }, world);
                     }
                  }
               }
            }
         }
      }
   }
}
