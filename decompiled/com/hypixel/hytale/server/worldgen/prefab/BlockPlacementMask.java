package com.hypixel.hytale.server.worldgen.prefab;

import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.worldgen.util.BlockFluidEntry;
import com.hypixel.hytale.server.worldgen.util.ResolvedBlockArray;
import com.hypixel.hytale.server.worldgen.util.condition.BlockMaskCondition;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockPlacementMask implements BlockMaskCondition {
   public static final BlockPlacementMask.IMask DEFAULT_MASK = new BlockPlacementMask.DefaultMask();
   private BlockPlacementMask.IMask defaultMask;
   private Long2ObjectMap<BlockPlacementMask.Mask> specificMasks;

   public void set(BlockPlacementMask.IMask defaultMask, Long2ObjectMap<BlockPlacementMask.Mask> specificMasks) {
      this.defaultMask = defaultMask;
      this.specificMasks = specificMasks;
   }

   @Override
   public boolean eval(int currentBlock, int currentFluid, @Nonnull BlockFluidEntry entry) {
      BlockPlacementMask.IMask mask = this.specificMasks == null ? null : this.specificMasks.get(MathUtil.packLong(entry.blockId(), entry.fluidId()));
      if (mask == null) {
         mask = this.defaultMask;
      }

      return mask.shouldReplace(currentBlock, currentFluid);
   }

   @Override
   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BlockPlacementMask that = (BlockPlacementMask)o;
         return !this.defaultMask.equals(that.defaultMask) ? false : Objects.equals(this.specificMasks, that.specificMasks);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.defaultMask.hashCode();
      return 31 * result + (this.specificMasks != null ? this.specificMasks.hashCode() : 0);
   }

   @Nonnull
   @Override
   public String toString() {
      return "BlockPlacementMask{defaultMask=" + this.defaultMask + ", specificMasks=" + this.specificMasks + "}";
   }

   public static class BlockArrayEntry implements BlockPlacementMask.IEntry {
      private ResolvedBlockArray blocks;
      private boolean replace;

      public void set(ResolvedBlockArray blocks, boolean replace) {
         this.blocks = blocks;
         this.replace = replace;
      }

      @Override
      public boolean shouldHandle(int current, int fluid) {
         return this.blocks.contains(current, fluid);
      }

      @Override
      public boolean shouldReplace() {
         return this.replace;
      }

      @Override
      public boolean equals(@Nullable Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            BlockPlacementMask.BlockArrayEntry that = (BlockPlacementMask.BlockArrayEntry)o;
            return this.replace != that.replace ? false : this.blocks.equals(that.blocks);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         int result = this.blocks.hashCode();
         return 31 * result + (this.replace ? 1 : 0);
      }

      @Nonnull
      @Override
      public String toString() {
         return "BlockArrayEntry{blocks=" + this.blocks + ", replace=" + this.replace + "}";
      }
   }

   public static class DefaultMask implements BlockPlacementMask.IMask {
      @Override
      public boolean shouldReplace(int block, int fluid) {
         return block == 0 && fluid == 0;
      }

      @Override
      public int hashCode() {
         return 137635105;
      }

      @Override
      public boolean equals(Object o) {
         return o instanceof BlockPlacementMask.DefaultMask;
      }

      @Nonnull
      @Override
      public String toString() {
         return "DefaultMask{}";
      }
   }

   public interface IEntry {
      boolean shouldHandle(int var1, int var2);

      boolean shouldReplace();
   }

   public interface IMask {
      boolean shouldReplace(int var1, int var2);
   }

   public static class Mask implements BlockPlacementMask.IMask {
      private final BlockPlacementMask.IEntry[] entries;

      public Mask(BlockPlacementMask.IEntry[] entries) {
         this.entries = entries;
      }

      @Override
      public boolean shouldReplace(int current, int fluid) {
         for (BlockPlacementMask.IEntry entry : this.entries) {
            if (entry.shouldHandle(current, fluid)) {
               return entry.shouldReplace();
            }
         }

         return false;
      }

      @Override
      public boolean equals(@Nullable Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            BlockPlacementMask.Mask mask = (BlockPlacementMask.Mask)o;
            return Arrays.equals((Object[])this.entries, (Object[])mask.entries);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Arrays.hashCode((Object[])this.entries);
      }

      @Nonnull
      @Override
      public String toString() {
         return "Mask{entries=" + Arrays.toString((Object[])this.entries) + "}";
      }
   }

   public static class WildcardEntry implements BlockPlacementMask.IEntry {
      private final boolean replace;

      public WildcardEntry(boolean replace) {
         this.replace = replace;
      }

      @Override
      public boolean shouldHandle(int block, int fluid) {
         return true;
      }

      @Override
      public boolean shouldReplace() {
         return this.replace;
      }

      @Override
      public boolean equals(@Nullable Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            BlockPlacementMask.WildcardEntry that = (BlockPlacementMask.WildcardEntry)o;
            return this.replace == that.replace;
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.replace ? 1 : 0;
      }

      @Nonnull
      @Override
      public String toString() {
         return "WildcardEntry{replace=" + this.replace + "}";
      }
   }
}
