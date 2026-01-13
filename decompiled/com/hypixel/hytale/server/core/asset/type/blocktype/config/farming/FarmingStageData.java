package com.hypixel.hytale.server.core.asset.type.blocktype.config.farming;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.Rangef;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.asset.type.soundevent.validator.SoundEventValidators;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class FarmingStageData {
   @Nonnull
   public static CodecMapCodec<FarmingStageData> CODEC = new CodecMapCodec<>("Type");
   @Nonnull
   public static BuilderCodec<FarmingStageData> BASE_CODEC = BuilderCodec.abstractBuilder(FarmingStageData.class)
      .append(
         new KeyedCodec<>("Duration", ProtocolCodecs.RANGEF),
         (farmingStage, duration) -> farmingStage.duration = duration,
         farmingStage -> farmingStage.duration
      )
      .add()
      .<String>append(
         new KeyedCodec<>("SoundEventId", Codec.STRING), (farmingStage, sound) -> farmingStage.soundEventId = sound, farmingStage -> farmingStage.soundEventId
      )
      .addValidator(SoundEvent.VALIDATOR_CACHE.getValidator())
      .addValidator(SoundEventValidators.MONO)
      .addValidator(SoundEventValidators.ONESHOT)
      .add()
      .afterDecode(farmingStage -> {
         if (farmingStage.soundEventId != null) {
            farmingStage.soundEventIndex = SoundEvent.getAssetMap().getIndex(farmingStage.soundEventId);
         }
      })
      .build();
   protected Rangef duration;
   @Nullable
   protected String soundEventId = null;
   protected transient int soundEventIndex = 0;

   @Nullable
   public Rangef getDuration() {
      return this.duration;
   }

   @Nullable
   public String getSoundEventId() {
      return this.soundEventId;
   }

   public int getSoundEventIndex() {
      return this.soundEventIndex;
   }

   public boolean implementsShouldStop() {
      return false;
   }

   public boolean shouldStop(ComponentAccessor<ChunkStore> commandBuffer, Ref<ChunkStore> sectionRef, Ref<ChunkStore> blockRef, int x, int y, int z) {
      return false;
   }

   public void apply(
      ComponentAccessor<ChunkStore> commandBuffer,
      Ref<ChunkStore> sectionRef,
      Ref<ChunkStore> blockRef,
      int x,
      int y,
      int z,
      @Nullable FarmingStageData previousStage
   ) {
      ChunkSection section = commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
      int worldX = ChunkUtil.worldCoordFromLocalCoord(section.getX(), x);
      int worldY = ChunkUtil.worldCoordFromLocalCoord(section.getY(), y);
      int worldZ = ChunkUtil.worldCoordFromLocalCoord(section.getZ(), z);
      SoundUtil.playSoundEvent3d(
         this.soundEventIndex, SoundCategory.SFX, worldX, worldY, worldZ, commandBuffer.getExternalData().getWorld().getEntityStore().getStore()
      );
      if (previousStage != null) {
         previousStage.remove(commandBuffer, sectionRef, blockRef, x, y, z);
      }
   }

   public void remove(ComponentAccessor<ChunkStore> commandBuffer, Ref<ChunkStore> sectionRef, Ref<ChunkStore> blockRef, int x, int y, int z) {
   }

   @Nonnull
   @Override
   public String toString() {
      return "FarmingStageData{duration=" + this.duration + ", soundEventId='" + this.soundEventId + "'}";
   }
}
