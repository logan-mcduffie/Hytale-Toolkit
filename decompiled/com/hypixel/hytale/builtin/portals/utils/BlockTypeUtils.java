package com.hypixel.hytale.builtin.portals.utils;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;

public final class BlockTypeUtils {
   private BlockTypeUtils() {
   }

   public static BlockType getBlockForState(BlockType blockType, String state) {
      String baseKey = blockType.getDefaultStateKey();
      BlockType baseBlock = baseKey == null ? blockType : BlockType.getAssetMap().getAsset(baseKey);
      return "default".equals(state) ? baseBlock : baseBlock.getBlockForState(state);
   }
}
