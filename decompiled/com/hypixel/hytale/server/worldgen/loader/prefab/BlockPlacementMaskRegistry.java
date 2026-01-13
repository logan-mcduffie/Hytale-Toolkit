package com.hypixel.hytale.server.worldgen.loader.prefab;

import com.hypixel.hytale.server.worldgen.loader.util.FileMaskCache;
import com.hypixel.hytale.server.worldgen.prefab.BlockPlacementMask;
import com.hypixel.hytale.server.worldgen.util.ResolvedBlockArray;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class BlockPlacementMaskRegistry extends FileMaskCache<BlockPlacementMask> {
   @Nonnull
   private final Map<BlockPlacementMask, BlockPlacementMask> masks = new HashMap<>();
   @Nonnull
   private final Map<BlockPlacementMask.BlockArrayEntry, BlockPlacementMask.BlockArrayEntry> entries = new HashMap<>();
   private BlockPlacementMask tempMask = new BlockPlacementMask();
   private BlockPlacementMask.BlockArrayEntry tempEntry = new BlockPlacementMask.BlockArrayEntry();

   @Nonnull
   public BlockPlacementMask retainOrAllocateMask(BlockPlacementMask.IMask defaultMask, Long2ObjectMap<BlockPlacementMask.Mask> specificMasks) {
      BlockPlacementMask mask = this.tempMask;
      mask.set(defaultMask, specificMasks);
      BlockPlacementMask old = this.masks.putIfAbsent(mask, mask);
      if (old != null) {
         return old;
      } else {
         this.tempMask = new BlockPlacementMask();
         return mask;
      }
   }

   @Nonnull
   public BlockPlacementMask.BlockArrayEntry retainOrAllocateEntry(ResolvedBlockArray blocks, boolean replace) {
      BlockPlacementMask.BlockArrayEntry entry = this.tempEntry;
      entry.set(blocks, replace);
      BlockPlacementMask.BlockArrayEntry old = this.entries.putIfAbsent(entry, entry);
      if (old != null) {
         return old;
      } else {
         this.tempEntry = new BlockPlacementMask.BlockArrayEntry();
         return entry;
      }
   }
}
