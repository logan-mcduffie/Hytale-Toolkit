package com.hypixel.hytale.builtin.buildertools.prefabeditor.saving;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.blocktype.component.BlockPhysics;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.prefab.PrefabSaveException;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabSaver {
   protected static final String EDITOR_BLOCK = "Editor_Block";
   protected static final String EDITOR_BLOCK_PREFAB_AIR = "Editor_Empty";
   protected static final String EDITOR_BLOCK_PREFAB_ANCHOR = "Editor_Anchor";

   @Nonnull
   public static CompletableFuture<Boolean> savePrefab(
      @Nonnull CommandSender sender,
      @Nonnull World world,
      @Nonnull Path pathToSave,
      @Nonnull Vector3i anchorPoint,
      @Nonnull Vector3i minPoint,
      @Nonnull Vector3i maxPoint,
      @Nonnull Vector3i pastePosition,
      @Nonnull Vector3i originalFileAnchor,
      @Nonnull PrefabSaverSettings settings
   ) {
      return CompletableFuture.supplyAsync(() -> {
         BlockSelection blockSelection = copyBlocks(sender, world, anchorPoint, minPoint, maxPoint, pastePosition, originalFileAnchor, settings);
         return blockSelection == null ? false : save(sender, blockSelection, pathToSave, settings);
      }, world);
   }

   @Nullable
   private static BlockSelection copyBlocks(
      @Nonnull CommandSender sender,
      @Nonnull World world,
      @Nonnull Vector3i anchorPoint,
      @Nonnull Vector3i minPoint,
      @Nonnull Vector3i maxPoint,
      @Nonnull Vector3i pastePosition,
      @Nonnull Vector3i originalFileAnchor,
      @Nonnull PrefabSaverSettings settings
   ) {
      ChunkStore chunkStore = world.getChunkStore();
      long start = System.nanoTime();
      int width = maxPoint.x - minPoint.x;
      int height = maxPoint.y - minPoint.y;
      int depth = maxPoint.z - minPoint.z;
      BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
      int editorBlock = assetMap.getIndex("Editor_Block");
      if (editorBlock == Integer.MIN_VALUE) {
         sender.sendMessage(Message.translation("server.commands.editprefab.save.error.unknownBlockIdKey").param("key", "Editor_Block".toString()));
         return null;
      } else {
         int editorBlockPrefabAir = assetMap.getIndex("Editor_Empty");
         if (editorBlockPrefabAir == Integer.MIN_VALUE) {
            sender.sendMessage(Message.translation("server.commands.editprefab.save.error.unknownBlockIdKey").param("key", "Editor_Empty".toString()));
            return null;
         } else {
            int editorBlockPrefabAnchor = assetMap.getIndex("Editor_Anchor");
            if (editorBlockPrefabAnchor == Integer.MIN_VALUE) {
               sender.sendMessage(Message.translation("server.commands.editprefab.save.error.unknownBlockIdKey").param("key", "Editor_Anchor".toString()));
               return null;
            } else {
               int newAnchorX = anchorPoint.x - pastePosition.x;
               int newAnchorY = anchorPoint.y - pastePosition.y;
               int newAnchorZ = anchorPoint.z - pastePosition.z;
               BlockSelection selection = new BlockSelection();
               selection.setPosition(pastePosition.x - originalFileAnchor.x, pastePosition.y - originalFileAnchor.y, pastePosition.z - originalFileAnchor.z);
               selection.setSelectionArea(minPoint, maxPoint);
               selection.setAnchor(newAnchorX, newAnchorY, newAnchorZ);
               int blockCount = 0;
               int fluidCount = 0;
               int top = Math.max(minPoint.y, maxPoint.y);
               int bottom = Math.min(minPoint.y, maxPoint.y);
               Long2ObjectMap<Ref<ChunkStore>> loadedChunks = preloadChunksInSelection(world, chunkStore, minPoint, maxPoint);

               for (int x = minPoint.x; x <= maxPoint.x; x++) {
                  for (int z = minPoint.z; z <= maxPoint.z; z++) {
                     long chunkIndex = ChunkUtil.indexChunkFromBlock(x, z);
                     Ref<ChunkStore> chunkRef = loadedChunks.get(chunkIndex);
                     if (chunkRef != null && chunkRef.isValid()) {
                        WorldChunk worldChunkComponent = chunkStore.getStore().getComponent(chunkRef, WorldChunk.getComponentType());

                        assert worldChunkComponent != null;

                        ChunkColumn chunkColumnComponent = chunkStore.getStore().getComponent(chunkRef, ChunkColumn.getComponentType());

                        assert chunkColumnComponent != null;

                        for (int y = top; y >= bottom; y--) {
                           int sectionIndex = ChunkUtil.indexSection(y);
                           Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(sectionIndex);
                           if (sectionRef != null && sectionRef.isValid()) {
                              BlockSection sectionComponent = chunkStore.getStore().getComponent(sectionRef, BlockSection.getComponentType());

                              assert sectionComponent != null;

                              BlockPhysics blockPhysicsComponent = chunkStore.getStore().getComponent(sectionRef, BlockPhysics.getComponentType());
                              int block = sectionComponent.get(x, y, z);
                              if (settings.isBlocks() && (block != 0 || settings.isEmpty()) && block != editorBlock) {
                                 if (block == editorBlockPrefabAir) {
                                    block = 0;
                                 }

                                 Holder<ChunkStore> holder = worldChunkComponent.getBlockComponentHolder(x, y, z);
                                 if (holder != null) {
                                    holder = holder.clone();
                                    BlockState blockState = BlockState.getBlockState(holder);
                                    if (blockState != null) {
                                       int localX = x - pastePosition.x;
                                       int localY = y - pastePosition.y;
                                       int localZ = z - pastePosition.z;
                                       Vector3i position = blockState.__internal_getPosition();
                                       if (position != null) {
                                          position.assign(localX, localY, localZ);
                                       }
                                    }
                                 }

                                 selection.addBlockAtWorldPos(
                                    x,
                                    y,
                                    z,
                                    block,
                                    sectionComponent.getRotationIndex(x, y, z),
                                    sectionComponent.getFiller(x, y, z),
                                    blockPhysicsComponent != null ? blockPhysicsComponent.get(x, y, z) : 0,
                                    holder
                                 );
                                 blockCount++;
                              }

                              FluidSection fluidSectionComponent = chunkStore.getStore().getComponent(sectionRef, FluidSection.getComponentType());

                              assert fluidSectionComponent != null;

                              int fluid = fluidSectionComponent.getFluidId(x, y, z);
                              if (settings.isBlocks() && (fluid != 0 || settings.isEmpty())) {
                                 byte fluidLevel = fluidSectionComponent.getFluidLevel(x, y, z);
                                 selection.addFluidAtWorldPos(x, y, z, fluid, fluidLevel);
                                 fluidCount++;
                              }
                           }
                        }
                     }
                  }
               }

               if (settings.isEntities()) {
                  Store<EntityStore> store = world.getEntityStore().getStore();
                  BuilderToolsPlugin.forEachCopyableInSelection(world, minPoint.x, minPoint.y, minPoint.z, width, height, depth, e -> {
                     Holder<EntityStore> holder = store.copyEntity(e);
                     selection.addEntityFromWorld(holder);
                  });
               }

               long end = System.nanoTime();
               long diff = end - start;
               BuilderToolsPlugin.get()
                  .getLogger()
                  .at(Level.FINE)
                  .log("Took: %dns (%dms) to execute copy of %d blocks, %d fluids", diff, TimeUnit.NANOSECONDS.toMillis(diff), blockCount, fluidCount);
               return selection;
            }
         }
      }
   }

   @Nonnull
   private static Long2ObjectMap<Ref<ChunkStore>> preloadChunksInSelection(
      @Nonnull World world, @Nonnull ChunkStore chunkStore, @Nonnull Vector3i minPoint, @Nonnull Vector3i maxPoint
   ) {
      LongSet chunkIndices = new LongOpenHashSet();
      int minChunkX = minPoint.x >> 5;
      int maxChunkX = maxPoint.x >> 5;
      int minChunkZ = minPoint.z >> 5;
      int maxChunkZ = maxPoint.z >> 5;

      for (int cx = minChunkX; cx <= maxChunkX; cx++) {
         for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
            chunkIndices.add(ChunkUtil.indexChunk(cx, cz));
         }
      }

      Long2ObjectMap<Ref<ChunkStore>> loadedChunks = new Long2ObjectOpenHashMap<>(chunkIndices.size());

      for (long chunkIndex : chunkIndices) {
         CompletableFuture<Ref<ChunkStore>> future = chunkStore.getChunkReferenceAsync(chunkIndex);

         while (!future.isDone()) {
            world.consumeTaskQueue();
         }

         Ref<ChunkStore> reference = future.join();
         if (reference != null && reference.isValid()) {
            loadedChunks.put(chunkIndex, reference);
         }
      }

      return loadedChunks;
   }

   private static boolean save(
      @Nonnull CommandSender sender, @Nonnull BlockSelection copiedSelection, @Nonnull Path saveFilePath, @Nonnull PrefabSaverSettings settings
   ) {
      if (saveFilePath.getFileSystem() != FileSystems.getDefault()) {
         sender.sendMessage(Message.translation("server.builderTools.cannotSaveToReadOnlyPath").param("path", saveFilePath.toString()));
         return false;
      } else {
         try {
            long start = System.nanoTime();
            BlockSelection postClone = settings.isRelativize() ? copiedSelection.relativize() : copiedSelection.cloneSelection();
            PrefabStore.get().savePrefab(saveFilePath, postClone, settings.isOverwriteExisting());
            long diff = System.nanoTime() - start;
            BuilderToolsPlugin.get()
               .getLogger()
               .at(Level.FINE)
               .log("Took: %dns (%dms) to execute save of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), copiedSelection.getBlockCount());
            return true;
         } catch (PrefabSaveException var9) {
            switch (var9.getType()) {
               case ERROR:
                  BuilderToolsPlugin.get().getLogger().at(Level.WARNING).withCause(var9).log("Exception saving prefab %s", saveFilePath);
                  sender.sendMessage(
                     Message.translation("server.builderTools.errorSavingPrefab")
                        .param("name", saveFilePath.toString())
                        .param("message", var9.getCause().getMessage())
                  );
                  break;
               case ALREADY_EXISTS:
                  BuilderToolsPlugin.get().getLogger().at(Level.WARNING).log("Prefab already exists %s", saveFilePath.toString());
                  sender.sendMessage(Message.translation("server.builderTools.prefabAlreadyExists"));
            }

            return false;
         }
      }
   }
}
