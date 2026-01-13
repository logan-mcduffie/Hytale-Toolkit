package com.hypixel.hytale.server.core.asset.packet;

import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.AssetUpdateQuery;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.protocol.Packet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AssetPacketGenerator<K, T extends JsonAssetWithMap<K, M>, M extends AssetMap<K, T>> {
   public abstract Packet generateInitPacket(M var1, Map<K, T> var2);

   public abstract Packet generateUpdatePacket(M var1, Map<K, T> var2, @Nonnull AssetUpdateQuery var3);

   @Nullable
   public abstract Packet generateRemovePacket(M var1, Set<K> var2, @Nonnull AssetUpdateQuery var3);
}
