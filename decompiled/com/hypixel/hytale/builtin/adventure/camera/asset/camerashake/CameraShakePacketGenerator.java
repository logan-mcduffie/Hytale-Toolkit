package com.hypixel.hytale.builtin.adventure.camera.asset.camerashake;

import com.hypixel.hytale.assetstore.map.IndexedAssetMap;
import com.hypixel.hytale.protocol.CachedPacket;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.packets.assets.UpdateCameraShake;
import com.hypixel.hytale.server.core.asset.packet.SimpleAssetPacketGenerator;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nonnull;

public class CameraShakePacketGenerator extends SimpleAssetPacketGenerator<String, CameraShake, IndexedAssetMap<String, CameraShake>> {
   @Nonnull
   public Packet generateInitPacket(@Nonnull IndexedAssetMap<String, CameraShake> assetMap, @Nonnull Map<String, CameraShake> assets) {
      return toCachedPacket(UpdateType.Init, assetMap, assets);
   }

   @Nonnull
   protected Packet generateUpdatePacket(@Nonnull IndexedAssetMap<String, CameraShake> assetMap, @Nonnull Map<String, CameraShake> loadedAssets) {
      return toCachedPacket(UpdateType.AddOrUpdate, assetMap, loadedAssets);
   }

   @Nonnull
   protected Packet generateRemovePacket(@Nonnull IndexedAssetMap<String, CameraShake> assetMap, @Nonnull Set<String> removed) {
      Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.CameraShake> profiles = new Int2ObjectOpenHashMap<>();

      for (String key : removed) {
         int index = assetMap.getIndex(key);
         profiles.put(index, null);
      }

      UpdateCameraShake packet = new UpdateCameraShake();
      packet.type = UpdateType.Remove;
      packet.profiles = profiles;
      return CachedPacket.cache(packet);
   }

   @Nonnull
   protected static Packet toCachedPacket(UpdateType type, @Nonnull IndexedAssetMap<String, CameraShake> assetMap, @Nonnull Map<String, CameraShake> assets) {
      Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.CameraShake> profiles = new Int2ObjectOpenHashMap<>();

      for (Entry<String, CameraShake> entry : assets.entrySet()) {
         int index = assetMap.getIndex(entry.getKey());
         profiles.put(index, entry.getValue().toPacket());
      }

      UpdateCameraShake packet = new UpdateCameraShake();
      packet.type = type;
      packet.profiles = profiles;
      return CachedPacket.cache(packet);
   }
}
