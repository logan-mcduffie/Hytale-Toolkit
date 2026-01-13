package com.hypixel.hytale.builtin.hytalegenerator.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelSpace;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.math.vector.Vector3i;
import javax.annotation.Nonnull;

public abstract class Pattern {
   public abstract boolean matches(@Nonnull Pattern.Context var1);

   public abstract SpaceSize readSpace();

   @Nonnull
   public static Pattern noPattern() {
      final SpaceSize space = new SpaceSize(new Vector3i(0, 0, 0), new Vector3i(0, 0, 0));
      return new Pattern() {
         @Override
         public boolean matches(@Nonnull Pattern.Context context) {
            return false;
         }

         @Nonnull
         @Override
         public SpaceSize readSpace() {
            return space;
         }
      };
   }

   @Nonnull
   public static Pattern yesPattern() {
      final SpaceSize space = new SpaceSize(new Vector3i(0, 0, 0), new Vector3i(0, 0, 0));
      return new Pattern() {
         @Override
         public boolean matches(@Nonnull Pattern.Context context) {
            return true;
         }

         @Nonnull
         @Override
         public SpaceSize readSpace() {
            return space;
         }
      };
   }

   public static class Context {
      public Vector3i position;
      public VoxelSpace<Material> materialSpace;
      public WorkerIndexer.Id workerId;

      public Context(@Nonnull Vector3i position, @Nonnull VoxelSpace<Material> materialSpace, WorkerIndexer.Id workerId) {
         this.position = position;
         this.materialSpace = materialSpace;
         this.workerId = workerId;
      }

      public Context(@Nonnull Pattern.Context other) {
         this.position = other.position;
         this.materialSpace = other.materialSpace;
         this.workerId = other.workerId;
      }
   }
}
