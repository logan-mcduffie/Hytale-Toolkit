package com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages;

import com.hypixel.hytale.builtin.hytalegenerator.biome.BiomeType;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.GridUtils;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.NStagedChunkGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.NBufferBundle;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NCountedPixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NSimplePixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NVoxelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NParametrizedBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.containers.FloatContainer3d;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.NPixelBufferView;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.NVoxelBufferView;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public class NTerrainStage implements NStage {
   public static final double DEFAULT_BACKGROUND_DENSITY = 0.0;
   public static final double ORIGIN_REACH = 1.0;
   public static final double ORIGIN_REACH_HALF = 0.5;
   public static final double QUARTER_PI = Math.PI / 4;
   public static final Class<NCountedPixelBuffer> biomeBufferClass = NCountedPixelBuffer.class;
   public static final Class<BiomeType> biomeClass = BiomeType.class;
   public static final Class<NSimplePixelBuffer> biomeDistanceBufferClass = NSimplePixelBuffer.class;
   public static final Class<NBiomeDistanceStage.BiomeDistanceEntries> biomeDistanceClass = NBiomeDistanceStage.BiomeDistanceEntries.class;
   public static final Class<NVoxelBuffer> materialBufferClass = NVoxelBuffer.class;
   public static final Class<Material> materialClass = Material.class;
   private final NParametrizedBufferType biomeInputBufferType;
   private final NParametrizedBufferType biomeDistanceInputBufferType;
   private final NParametrizedBufferType materialOutputBufferType;
   private final Bounds3i inputBounds_bufferGrid;
   private final String stageName;
   private final int maxInterpolationRadius_voxelGrid;
   private final MaterialCache materialCache;
   private final WorkerIndexer.Data<FloatContainer3d> densityContainers;

   public NTerrainStage(
      @Nonnull String stageName,
      @Nonnull NParametrizedBufferType biomeInputBufferType,
      @Nonnull NParametrizedBufferType biomeDistanceInputBufferType,
      @Nonnull NParametrizedBufferType materialOutputBufferType,
      int maxInterpolationRadius_voxelGrid,
      @Nonnull MaterialCache materialCache,
      @Nonnull WorkerIndexer workerIndexer
   ) {
      assert biomeInputBufferType.isValidType(biomeBufferClass, biomeClass);

      assert biomeDistanceInputBufferType.isValidType(biomeDistanceBufferClass, biomeDistanceClass);

      assert materialOutputBufferType.isValidType(materialBufferClass, materialClass);

      assert maxInterpolationRadius_voxelGrid >= 0;

      this.biomeInputBufferType = biomeInputBufferType;
      this.biomeDistanceInputBufferType = biomeDistanceInputBufferType;
      this.materialOutputBufferType = materialOutputBufferType;
      this.stageName = stageName;
      this.maxInterpolationRadius_voxelGrid = maxInterpolationRadius_voxelGrid;
      this.materialCache = materialCache;
      this.densityContainers = new WorkerIndexer.Data<>(
         workerIndexer.getWorkerCount(), () -> new FloatContainer3d(NStagedChunkGenerator.SINGLE_BUFFER_TILE_BOUNDS_BUFFER_GRID, 0.0F)
      );
      this.inputBounds_bufferGrid = GridUtils.createColumnBounds_bufferGrid(new Vector3i(), 0, 40);
      this.inputBounds_bufferGrid.min.subtract(Vector3i.ALL_ONES);
      this.inputBounds_bufferGrid.max.add(Vector3i.ALL_ONES);
      GridUtils.setBoundsYToWorldHeight_bufferGrid(this.inputBounds_bufferGrid);
   }

   @Override
   public void run(@Nonnull NStage.Context context) {
      NBufferBundle.Access.View biomeAccess = context.bufferAccess.get(this.biomeInputBufferType);
      NPixelBufferView<BiomeType> biomeSpace = new NPixelBufferView<>(biomeAccess, biomeClass);
      NBufferBundle.Access.View biomeDistanceAccess = context.bufferAccess.get(this.biomeDistanceInputBufferType);
      NPixelBufferView<NBiomeDistanceStage.BiomeDistanceEntries> biomeDistanceSpace = new NPixelBufferView<>(biomeDistanceAccess, biomeDistanceClass);
      NBufferBundle.Access.View materialAccess = context.bufferAccess.get(this.materialOutputBufferType);
      NVoxelBufferView<Material> materialSpace = new NVoxelBufferView<>(materialAccess, materialClass);
      Bounds3i outputBounds_voxelGrid = materialSpace.getBounds();
      FloatContainer3d densityContainer = this.densityContainers.get(context.workerId);
      densityContainer.moveMinTo(outputBounds_voxelGrid.min);
      this.generateDensity(densityContainer, biomeSpace, biomeDistanceSpace, context.workerId);
      this.generateMaterials(biomeSpace, biomeDistanceSpace, densityContainer, materialSpace, context.workerId);
   }

   @Nonnull
   @Override
   public Map<NBufferType, Bounds3i> getInputTypesAndBounds_bufferGrid() {
      Map<NBufferType, Bounds3i> map = new HashMap<>();
      map.put(this.biomeInputBufferType, this.inputBounds_bufferGrid);
      map.put(this.biomeDistanceInputBufferType, this.inputBounds_bufferGrid);
      return map;
   }

   @Nonnull
   @Override
   public List<NBufferType> getOutputTypes() {
      return List.of(this.materialOutputBufferType);
   }

   @Nonnull
   @Override
   public String getName() {
      return this.stageName;
   }

   private void generateDensity(
      @Nonnull FloatContainer3d densityBuffer,
      @Nonnull NPixelBufferView<BiomeType> biomeSpace,
      @Nonnull NPixelBufferView<NBiomeDistanceStage.BiomeDistanceEntries> distanceSpace,
      @Nonnull WorkerIndexer.Id workerId
   ) {
      Bounds3i bounds_voxelGrid = densityBuffer.getBounds_voxelGrid();
      Vector3i position_voxelGrid = new Vector3i(bounds_voxelGrid.min);
      Density.Context densityContext = new Density.Context();
      densityContext.position = position_voxelGrid.toVector3d();
      densityContext.workerId = workerId;

      for (position_voxelGrid.x = bounds_voxelGrid.min.x; position_voxelGrid.x < bounds_voxelGrid.max.x; position_voxelGrid.x++) {
         densityContext.position.x = position_voxelGrid.x;

         for (position_voxelGrid.z = bounds_voxelGrid.min.z; position_voxelGrid.z < bounds_voxelGrid.max.z; position_voxelGrid.z++) {
            densityContext.position.z = position_voxelGrid.z;
            position_voxelGrid.y = 0;
            position_voxelGrid.dropHash();
            BiomeType biomeAtOrigin = biomeSpace.getContent(position_voxelGrid);
            NBiomeDistanceStage.BiomeDistanceEntries biomeDistances = distanceSpace.getContent(position_voxelGrid);
            NTerrainStage.BiomeWeights biomeWeights = createWeights(biomeDistances, biomeAtOrigin, this.maxInterpolationRadius_voxelGrid);
            densityContext.distanceToBiomeEdge = biomeDistances.distanceToClosestOtherBiome(biomeAtOrigin);
            boolean isFirstBiome = true;

            for (NTerrainStage.BiomeWeights.Entry biomeWeight : biomeWeights.entries) {
               Density density = biomeWeight.biomeType.getTerrainDensity();
               if (isFirstBiome) {
                  for (position_voxelGrid.y = bounds_voxelGrid.min.y; position_voxelGrid.y < bounds_voxelGrid.max.y; position_voxelGrid.y++) {
                     position_voxelGrid.dropHash();
                     densityContext.position.y = position_voxelGrid.y;
                     float densityValue = (float)density.process(densityContext);
                     float scaledDensityValue = densityValue * biomeWeight.weight;
                     densityBuffer.set(position_voxelGrid, scaledDensityValue);
                  }
               }

               if (!isFirstBiome) {
                  for (position_voxelGrid.y = bounds_voxelGrid.min.y; position_voxelGrid.y < bounds_voxelGrid.max.y; position_voxelGrid.y++) {
                     position_voxelGrid.dropHash();
                     densityContext.position.y = position_voxelGrid.y;
                     float bufferDensityValue = densityBuffer.get(position_voxelGrid);
                     float densityValue = (float)density.process(densityContext);
                     float scaledDensityValue = densityValue * biomeWeight.weight;
                     densityBuffer.set(position_voxelGrid, bufferDensityValue + scaledDensityValue);
                  }
               }

               isFirstBiome = false;
            }
         }
      }
   }

   private float getOrGenerateDensity(
      @Nonnull Vector3i position_voxelGrid,
      @Nonnull FloatContainer3d densityBuffer,
      @Nonnull NPixelBufferView<BiomeType> biomeSpace,
      @Nonnull NPixelBufferView<NBiomeDistanceStage.BiomeDistanceEntries> distanceSpace,
      @Nonnull WorkerIndexer.Id workerId
   ) {
      return densityBuffer.getBounds_voxelGrid().contains(position_voxelGrid)
         ? densityBuffer.get(position_voxelGrid)
         : this.generateDensity(position_voxelGrid, biomeSpace, distanceSpace, workerId);
   }

   private float generateDensity(
      @Nonnull Vector3i position_voxelGrid,
      @Nonnull NPixelBufferView<BiomeType> biomeSpace,
      @Nonnull NPixelBufferView<NBiomeDistanceStage.BiomeDistanceEntries> distanceSpace,
      @Nonnull WorkerIndexer.Id workerId
   ) {
      if (!distanceSpace.isInsideSpace(position_voxelGrid.x, 0, position_voxelGrid.z)) {
         return 0.0F;
      } else {
         Density.Context densityContext = new Density.Context();
         densityContext.position = position_voxelGrid.toVector3d();
         densityContext.workerId = workerId;
         BiomeType biomeAtOrigin = biomeSpace.getContent(position_voxelGrid.x, 0, position_voxelGrid.z);
         NBiomeDistanceStage.BiomeDistanceEntries biomeDistances = distanceSpace.getContent(position_voxelGrid.x, 0, position_voxelGrid.z);
         NTerrainStage.BiomeWeights biomeWeights = createWeights(biomeDistances, biomeAtOrigin, this.maxInterpolationRadius_voxelGrid);
         float densityResult = 0.0F;

         for (NTerrainStage.BiomeWeights.Entry biomeWeight : biomeWeights.entries) {
            Density density = biomeWeight.biomeType.getTerrainDensity();
            float densityValue = (float)density.process(densityContext);
            float scaledDensityValue = densityValue * biomeWeight.weight;
            densityResult += scaledDensityValue;
         }

         return densityResult;
      }
   }

   private void generateMaterials(
      @Nonnull NPixelBufferView<BiomeType> biomeSpace,
      @Nonnull NPixelBufferView<NBiomeDistanceStage.BiomeDistanceEntries> distanceSpace,
      @Nonnull FloatContainer3d densityBuffer,
      @Nonnull NVoxelBufferView<Material> materialSpace,
      @Nonnull WorkerIndexer.Id workerId
   ) {
      Bounds3i bounds_voxelGrid = materialSpace.getBounds();
      Vector3i position_voxelGrid = new Vector3i();

      for (position_voxelGrid.x = bounds_voxelGrid.min.x; position_voxelGrid.x < bounds_voxelGrid.max.x; position_voxelGrid.x++) {
         for (position_voxelGrid.z = bounds_voxelGrid.min.z; position_voxelGrid.z < bounds_voxelGrid.max.z; position_voxelGrid.z++) {
            position_voxelGrid.y = bounds_voxelGrid.min.y;
            BiomeType biome = biomeSpace.getContent(position_voxelGrid.x, 0, position_voxelGrid.z);
            MaterialProvider<Material> materialProvider = biome.getMaterialProvider();
            NTerrainStage.ColumnData columnData = new NTerrainStage.ColumnData(
               bounds_voxelGrid.min.y, bounds_voxelGrid.max.y, position_voxelGrid.x, position_voxelGrid.z, densityBuffer, materialProvider
            );
            double distanceToOtherBiome_voxelGrid = distanceSpace.getContent(position_voxelGrid).distanceToClosestOtherBiome(biome);

            for (position_voxelGrid.y = bounds_voxelGrid.min.y; position_voxelGrid.y < bounds_voxelGrid.max.y; position_voxelGrid.y++) {
               int i = position_voxelGrid.y - bounds_voxelGrid.min.y;
               MaterialProvider.Context context = new MaterialProvider.Context(
                  position_voxelGrid,
                  0.0,
                  columnData.depthIntoFloor[i],
                  columnData.depthIntoCeiling[i],
                  columnData.spaceAboveFloor[i],
                  columnData.spaceBelowCeiling[i],
                  workerId,
                  (position, id) -> this.getOrGenerateDensity(position, densityBuffer, biomeSpace, distanceSpace, workerId),
                  distanceToOtherBiome_voxelGrid
               );
               Material material = columnData.materialProvider.getVoxelTypeAt(context);
               if (material != null) {
                  materialSpace.set(material, position_voxelGrid.x, position_voxelGrid.y, position_voxelGrid.z);
               } else {
                  materialSpace.set(this.materialCache.EMPTY, position_voxelGrid.x, position_voxelGrid.y, position_voxelGrid.z);
               }
            }
         }
      }
   }

   @Nonnull
   private static NTerrainStage.BiomeWeights createWeights(
      @Nonnull NBiomeDistanceStage.BiomeDistanceEntries distances, @Nonnull BiomeType biomeAtOrigin, double interpolationRange
   ) {
      double circleRadius = interpolationRange + 0.5;
      NTerrainStage.BiomeWeights biomeWeights = new NTerrainStage.BiomeWeights();
      int originIndex = 0;
      double smallestNonOriginDistance = Double.MAX_VALUE;
      double total = 0.0;

      for (int i = 0; i < distances.entries.size(); i++) {
         NBiomeDistanceStage.BiomeDistanceEntry distanceEntry = distances.entries.get(i);
         NTerrainStage.BiomeWeights.Entry weightEntry = new NTerrainStage.BiomeWeights.Entry();
         if (!(distanceEntry.distance_voxelGrid >= interpolationRange)) {
            if (distanceEntry.biomeType == biomeAtOrigin) {
               originIndex = biomeWeights.entries.size();
            } else if (distanceEntry.distance_voxelGrid < smallestNonOriginDistance) {
               smallestNonOriginDistance = distanceEntry.distance_voxelGrid;
            }

            weightEntry.biomeType = distanceEntry.biomeType;
            weightEntry.weight = (float)areaUnderCircleCurve(distanceEntry.distance_voxelGrid, circleRadius, circleRadius);
            biomeWeights.entries.add(weightEntry);
            total += weightEntry.weight;
         }
      }

      if (biomeWeights.entries.size() > 0) {
         NTerrainStage.BiomeWeights.Entry originWeightEntry = biomeWeights.entries.get(originIndex);
         double maxX = 0.5 + smallestNonOriginDistance;
         double originExtraWeight = areaUnderCircleCurve(0.0, maxX, circleRadius);
         originWeightEntry.weight = (float)(originWeightEntry.weight + originExtraWeight);
         total += originExtraWeight;
      }

      for (NTerrainStage.BiomeWeights.Entry entry : biomeWeights.entries) {
         entry.weight /= (float)total;
      }

      return biomeWeights;
   }

   private static double areaUnderCircleCurve(double maxX) {
      if (maxX < 0.0) {
         return 0.0;
      } else {
         return maxX > 1.0 ? Math.PI / 4 : 0.5 * (maxX * Math.sqrt(1.0 - maxX * maxX) + Math.asin(maxX));
      }
   }

   private static double areaUnderCircleCurve(double minX, double maxX, double circleRadius) {
      assert circleRadius >= 0.0;

      assert minX <= maxX;

      minX /= circleRadius;
      maxX /= circleRadius;
      return circleRadius * circleRadius * (areaUnderCircleCurve(maxX) - areaUnderCircleCurve(minX));
   }

   private static class BiomeWeights {
      List<NTerrainStage.BiomeWeights.Entry> entries = new ArrayList<>(3);

      BiomeWeights() {
      }

      static class Entry {
         BiomeType biomeType;
         float weight;
      }
   }

   private class ColumnData {
      int worldX;
      int worldZ;
      MaterialProvider<Material> materialProvider;
      int topExclusive;
      int bottom;
      int arrayLength;
      int[] depthIntoFloor;
      int[] spaceBelowCeiling;
      int[] depthIntoCeiling;
      int[] spaceAboveFloor;
      int top;
      FloatContainer3d densityBuffer;

      private ColumnData(
         int bottom, int topExclusive, int worldX, int worldZ, @Nonnull FloatContainer3d densityBuffer, @Nonnull MaterialProvider<Material> materialProvider
      ) {
         this.topExclusive = topExclusive;
         this.bottom = bottom;
         this.worldX = worldX;
         this.worldZ = worldZ;
         this.arrayLength = topExclusive - bottom;
         this.depthIntoFloor = new int[this.arrayLength];
         this.spaceBelowCeiling = new int[this.arrayLength];
         this.depthIntoCeiling = new int[this.arrayLength];
         this.spaceAboveFloor = new int[this.arrayLength];
         this.top = topExclusive - 1;
         this.densityBuffer = densityBuffer;
         this.materialProvider = materialProvider;
         Vector3i position = new Vector3i(worldX, 0, worldZ);
         Vector3i positionAbove = new Vector3i(worldX, 0, worldZ);
         Vector3i positionBelow = new Vector3i(worldX, 0, worldZ);

         for (int y = this.top; y >= bottom; y--) {
            position.y = y;
            positionAbove.y = y + 1;
            positionBelow.y = y - 1;
            int i = y - bottom;
            float density = densityBuffer.get(position);
            boolean solidity = density > 0.0;
            if (y == this.top) {
               if (solidity) {
                  this.depthIntoFloor[i] = 1;
               } else {
                  this.depthIntoFloor[i] = 0;
               }

               this.spaceAboveFloor[i] = 1073741823;
            } else if (solidity) {
               this.depthIntoFloor[i] = this.depthIntoFloor[i + 1] + 1;
               this.spaceAboveFloor[i] = this.spaceAboveFloor[i + 1];
            } else {
               this.depthIntoFloor[i] = 0;
               if (densityBuffer.get(positionAbove) > 0.0) {
                  this.spaceAboveFloor[i] = 0;
               } else {
                  this.spaceAboveFloor[i] = this.spaceAboveFloor[i + 1] + 1;
               }
            }
         }

         for (int yx = bottom; yx <= this.top; yx++) {
            int i = yx - bottom;
            double density = densityBuffer.get(position);
            boolean solidity = density > 0.0;
            if (yx == bottom) {
               if (solidity) {
                  this.depthIntoCeiling[i] = 1;
               } else {
                  this.depthIntoCeiling[i] = 0;
               }

               this.spaceBelowCeiling[i] = Integer.MAX_VALUE;
            } else if (solidity) {
               this.depthIntoCeiling[i] = this.depthIntoCeiling[i - 1] + 1;
               this.spaceBelowCeiling[i] = this.spaceBelowCeiling[i - 1];
            } else {
               this.depthIntoCeiling[i] = 0;
               if (densityBuffer.get(positionBelow) > 0.0) {
                  this.spaceBelowCeiling[i] = 0;
               } else {
                  this.spaceBelowCeiling[i] = this.spaceBelowCeiling[i - 1] + 1;
               }
            }
         }
      }
   }
}
