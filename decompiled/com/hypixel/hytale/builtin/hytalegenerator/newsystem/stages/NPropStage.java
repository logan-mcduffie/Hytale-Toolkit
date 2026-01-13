package com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages;

import com.hypixel.hytale.builtin.hytalegenerator.PropField;
import com.hypixel.hytale.builtin.hytalegenerator.biome.BiomeType;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.GridUtils;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.NBufferBundle;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NCountedPixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NEntityBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NSimplePixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NVoxelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NParametrizedBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.NEntityBufferView;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.NPixelBufferView;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.NVoxelBufferView;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.builtin.hytalegenerator.props.ScanResult;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NPropStage implements NStage {
   public static final double DEFAULT_BACKGROUND_DENSITY = 0.0;
   public static final Class<NCountedPixelBuffer> biomeBufferClass = NCountedPixelBuffer.class;
   public static final Class<BiomeType> biomeTypeClass = BiomeType.class;
   public static final Class<NSimplePixelBuffer> biomeDistanceBufferClass = NSimplePixelBuffer.class;
   public static final Class<NBiomeDistanceStage.BiomeDistanceEntries> biomeDistanceClass = NBiomeDistanceStage.BiomeDistanceEntries.class;
   public static final Class<NVoxelBuffer> materialBufferClass = NVoxelBuffer.class;
   public static final Class<Material> materialClass = Material.class;
   public static final Class<NEntityBuffer> entityBufferClass = NEntityBuffer.class;
   private final NParametrizedBufferType biomeInputBufferType;
   private final NParametrizedBufferType biomeDistanceInputBufferType;
   private final NParametrizedBufferType materialInputBufferType;
   private final NBufferType entityInputBufferType;
   private final NParametrizedBufferType materialOutputBufferType;
   private final NBufferType entityOutputBufferType;
   private final Bounds3i inputBounds_bufferGrid;
   private final Bounds3i inputBounds_voxelGrid;
   private final String stageName;
   private final MaterialCache materialCache;
   private final int runtimeIndex;

   public NPropStage(
      @Nonnull String stageName,
      @Nonnull NParametrizedBufferType biomeInputBufferType,
      @Nonnull NParametrizedBufferType biomeDistanceInputBufferType,
      @Nonnull NParametrizedBufferType materialInputBufferType,
      @Nullable NBufferType entityInputBufferType,
      @Nonnull NParametrizedBufferType materialOutputBufferType,
      @Nonnull NBufferType entityOutputBufferType,
      @Nonnull MaterialCache materialCache,
      @Nonnull List<BiomeType> expectedBiomes,
      int runtimeIndex
   ) {
      assert biomeInputBufferType.isValidType(biomeBufferClass, biomeTypeClass);

      assert biomeDistanceInputBufferType.isValidType(biomeDistanceBufferClass, biomeDistanceClass);

      assert materialInputBufferType.isValidType(materialBufferClass, materialClass);

      assert entityInputBufferType == null || entityInputBufferType.isValidType(entityBufferClass);

      assert materialOutputBufferType.isValidType(materialBufferClass, materialClass);

      assert entityOutputBufferType.isValidType(entityBufferClass);

      this.biomeInputBufferType = biomeInputBufferType;
      this.biomeDistanceInputBufferType = biomeDistanceInputBufferType;
      this.materialInputBufferType = materialInputBufferType;
      this.entityInputBufferType = entityInputBufferType;
      this.materialOutputBufferType = materialOutputBufferType;
      this.entityOutputBufferType = entityOutputBufferType;
      this.stageName = stageName;
      this.materialCache = materialCache;
      this.runtimeIndex = runtimeIndex;
      this.inputBounds_voxelGrid = new Bounds3i();
      Vector3i range = new Vector3i();

      for (BiomeType biome : expectedBiomes) {
         for (PropField propField : biome.getPropFields()) {
            if (propField.getRuntime() == this.runtimeIndex) {
               for (Prop prop : propField.getPropDistribution().getAllPossibleProps()) {
                  Vector3i readRange_voxelGrid = prop.getContextDependency().getReadRange();
                  Vector3i writeRange_voxelGrid = prop.getContextDependency().getWriteRange();
                  range.x = readRange_voxelGrid.x + writeRange_voxelGrid.x;
                  range.y = readRange_voxelGrid.y + writeRange_voxelGrid.y;
                  range.z = readRange_voxelGrid.z + writeRange_voxelGrid.z;
                  this.inputBounds_voxelGrid.encompass(range.clone().add(Vector3i.ALL_ONES));
                  range.scale(-1);
                  this.inputBounds_voxelGrid.encompass(range);
               }
            }
         }
      }

      this.inputBounds_voxelGrid.min.y = 0;
      this.inputBounds_voxelGrid.max.y = 320;
      this.inputBounds_bufferGrid = GridUtils.createBufferBoundsInclusive_fromVoxelBounds(this.inputBounds_voxelGrid);
      GridUtils.setBoundsYToWorldHeight_bufferGrid(this.inputBounds_bufferGrid);
   }

   @Override
   public void run(@Nonnull NStage.Context context) {
      NBufferBundle.Access.View biomeAccess = context.bufferAccess.get(this.biomeInputBufferType);
      NPixelBufferView<BiomeType> biomeInputSpace = new NPixelBufferView<>(biomeAccess, biomeTypeClass);
      NBufferBundle.Access.View biomeDistanceAccess = context.bufferAccess.get(this.biomeDistanceInputBufferType);
      NPixelBufferView<NBiomeDistanceStage.BiomeDistanceEntries> biomeDistanceSpace = new NPixelBufferView<>(biomeDistanceAccess, biomeDistanceClass);
      NBufferBundle.Access.View materialInputAccess = context.bufferAccess.get(this.materialInputBufferType);
      NVoxelBufferView<Material> materialInputSpace = new NVoxelBufferView<>(materialInputAccess, materialClass);
      NBufferBundle.Access.View materialOutputAccess = context.bufferAccess.get(this.materialOutputBufferType);
      NVoxelBufferView<Material> materialOutputSpace = new NVoxelBufferView<>(materialOutputAccess, materialClass);
      NBufferBundle.Access.View entityOutputAccess = context.bufferAccess.get(this.entityOutputBufferType);
      NEntityBufferView entityOutputSpace = new NEntityBufferView(entityOutputAccess);
      Bounds3i localOutputBounds_voxelGrid = materialOutputSpace.getBounds();
      Bounds3i localInputBounds_voxelGrid = this.inputBounds_voxelGrid.clone();
      Bounds3i absoluteOutputBounds_voxelGrid = localOutputBounds_voxelGrid.clone();
      absoluteOutputBounds_voxelGrid.offset(localOutputBounds_voxelGrid.min.clone().scale(-1));
      localInputBounds_voxelGrid.stack(absoluteOutputBounds_voxelGrid);
      localInputBounds_voxelGrid.offset(localOutputBounds_voxelGrid.min);
      localInputBounds_voxelGrid.min.y = 0;
      localInputBounds_voxelGrid.max.y = 320;
      Bounds3d localInputBoundsDouble_voxelGrid = localInputBounds_voxelGrid.toBounds3d();
      materialOutputSpace.copyFrom(materialInputSpace);
      if (this.entityInputBufferType != null) {
         NBufferBundle.Access.View entityInputAccess = context.bufferAccess.get(this.entityInputBufferType);
         NEntityBufferView entityInputSpace = new NEntityBufferView(entityInputAccess);
         entityOutputSpace.copyFrom(entityInputSpace);
      }

      HashSet<BiomeType> biomesInBuffer = new HashSet<>();

      for (int x = localInputBounds_voxelGrid.min.x; x < localInputBounds_voxelGrid.max.x; x++) {
         for (int z = localInputBounds_voxelGrid.min.z; z < localInputBounds_voxelGrid.max.z; z++) {
            biomesInBuffer.add(biomeInputSpace.getContent(x, 0, z));
         }
      }

      Map<PropField, BiomeType> propFieldBiomeMap = new HashMap<>();

      for (BiomeType biome : biomesInBuffer) {
         for (PropField propField : biome.getPropFields()) {
            if (propField.getRuntime() == this.runtimeIndex) {
               propFieldBiomeMap.put(propField, biome);
            }
         }
      }

      for (Entry<PropField, BiomeType> entry : propFieldBiomeMap.entrySet()) {
         PropField propFieldx = entry.getKey();
         BiomeType biome = entry.getValue();
         PositionProvider positionProvider = propFieldx.getPositionProvider();
         Consumer<Vector3d> positionsConsumer = position -> {
            if (localInputBoundsDouble_voxelGrid.contains(position)) {
               Vector3i positionInt_voxelGrid = position.toVector3i();
               BiomeType biomeAtPosition = biomeInputSpace.getContent(positionInt_voxelGrid.x, 0, positionInt_voxelGrid.z);
               if (biomeAtPosition == biome) {
                  Vector3i position2d_voxelGrid = positionInt_voxelGrid.clone();
                  position2d_voxelGrid.setY(0);
                  double distanceToBiomeEdge = biomeDistanceSpace.getContent(position2d_voxelGrid).distanceToClosestOtherBiome(biomeAtPosition);
                  Prop prop = propField.getPropDistribution().propAt(position, context.workerId, distanceToBiomeEdge);
                  Bounds3i propWriteBounds = prop.getWriteBounds().clone();
                  propWriteBounds.offset(positionInt_voxelGrid);
                  if (propWriteBounds.intersects(localOutputBounds_voxelGrid)) {
                     ScanResult scanResult = prop.scan(positionInt_voxelGrid, materialInputSpace, context.workerId);
                     Prop.Context propContext = new Prop.Context(scanResult, materialOutputSpace, entityOutputSpace, context.workerId, distanceToBiomeEdge);
                     prop.place(propContext);
                  }
               }
            }
         };
         PositionProvider.Context positionsContext = new PositionProvider.Context(
            localInputBoundsDouble_voxelGrid.min, localInputBoundsDouble_voxelGrid.max, positionsConsumer, null, context.workerId
         );
         positionProvider.positionsIn(positionsContext);
      }
   }

   @Nonnull
   @Override
   public Map<NBufferType, Bounds3i> getInputTypesAndBounds_bufferGrid() {
      Map<NBufferType, Bounds3i> map = new HashMap<>();
      map.put(this.biomeInputBufferType, this.inputBounds_bufferGrid);
      map.put(this.biomeDistanceInputBufferType, this.inputBounds_bufferGrid);
      map.put(this.materialInputBufferType, this.inputBounds_bufferGrid);
      if (this.entityInputBufferType != null) {
         map.put(this.entityInputBufferType, this.inputBounds_bufferGrid);
      }

      return map;
   }

   @Nonnull
   @Override
   public List<NBufferType> getOutputTypes() {
      return List.of(this.materialOutputBufferType, this.entityOutputBufferType);
   }

   @Nonnull
   @Override
   public String getName() {
      return this.stageName;
   }
}
