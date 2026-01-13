package com.hypixel.hytale.server.core.asset.type.particle;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.packets.assets.UpdateParticleSpawners;
import com.hypixel.hytale.server.core.asset.packet.DefaultAssetPacketGenerator;
import com.hypixel.hytale.server.core.asset.type.particle.config.ParticleSpawner;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nonnull;

public class ParticleSpawnerPacketGenerator extends DefaultAssetPacketGenerator<String, ParticleSpawner> {
   @Nonnull
   @Override
   public Packet generateInitPacket(DefaultAssetMap<String, ParticleSpawner> assetMap, @Nonnull Map<String, ParticleSpawner> assets) {
      UpdateParticleSpawners packet = new UpdateParticleSpawners();
      packet.type = UpdateType.Init;
      packet.particleSpawners = new Object2ObjectOpenHashMap<>();

      for (Entry<String, ParticleSpawner> entry : assets.entrySet()) {
         packet.particleSpawners.put(entry.getKey(), entry.getValue().toPacket());
      }

      return packet;
   }

   @Nonnull
   @Override
   public Packet generateUpdatePacket(@Nonnull Map<String, ParticleSpawner> loadedAssets) {
      UpdateParticleSpawners packet = new UpdateParticleSpawners();
      packet.type = UpdateType.AddOrUpdate;
      packet.particleSpawners = new Object2ObjectOpenHashMap<>();

      for (Entry<String, ParticleSpawner> entry : loadedAssets.entrySet()) {
         packet.particleSpawners.put(entry.getKey(), entry.getValue().toPacket());
      }

      return packet;
   }

   @Nonnull
   @Override
   public Packet generateRemovePacket(@Nonnull Set<String> removed) {
      UpdateParticleSpawners packet = new UpdateParticleSpawners();
      packet.type = UpdateType.Remove;
      packet.removedParticleSpawners = removed.toArray(String[]::new);
      return packet;
   }
}
