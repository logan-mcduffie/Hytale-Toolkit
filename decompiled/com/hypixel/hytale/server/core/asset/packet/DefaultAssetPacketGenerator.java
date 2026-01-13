package com.hypixel.hytale.server.core.asset.packet;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.protocol.Packet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public abstract class DefaultAssetPacketGenerator<K, T extends JsonAssetWithMap<K, DefaultAssetMap<K, T>>>
   extends SimpleAssetPacketGenerator<K, T, DefaultAssetMap<K, T>> {
   public abstract Packet generateInitPacket(DefaultAssetMap<K, T> var1, Map<K, T> var2);

   public abstract Packet generateUpdatePacket(Map<K, T> var1);

   @Nullable
   public abstract Packet generateRemovePacket(Set<K> var1);

   public final Packet generateUpdatePacket(DefaultAssetMap<K, T> assetMap, Map<K, T> loadedAssets) {
      return this.generateUpdatePacket(loadedAssets);
   }

   @Nullable
   public final Packet generateRemovePacket(DefaultAssetMap<K, T> assetMap, Set<K> removed) {
      return this.generateRemovePacket(removed);
   }
}
