package com.hypixel.hytale.server.core.asset.type.gameplay;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class WorldMapConfig {
   @Nonnull
   public static final BuilderCodec<WorldMapConfig> CODEC = BuilderCodec.builder(WorldMapConfig.class, WorldMapConfig::new)
      .addField(
         new KeyedCodec<>("DisplaySpawn", Codec.BOOLEAN), (worldMapConfig, o) -> worldMapConfig.displaySpawn = o, worldMapConfig -> worldMapConfig.displaySpawn
      )
      .addField(
         new KeyedCodec<>("DisplayHome", Codec.BOOLEAN), (worldMapConfig, o) -> worldMapConfig.displayHome = o, worldMapConfig -> worldMapConfig.displayHome
      )
      .addField(
         new KeyedCodec<>("DisplayWarps", Codec.BOOLEAN), (worldMapConfig, o) -> worldMapConfig.displayWarps = o, worldMapConfig -> worldMapConfig.displayWarps
      )
      .addField(
         new KeyedCodec<>("DisplayDeathMarker", Codec.BOOLEAN),
         (worldMapConfig, o) -> worldMapConfig.displayDeathMarker = o,
         worldMapConfig -> worldMapConfig.displayDeathMarker
      )
      .addField(
         new KeyedCodec<>("DisplayPlayers", Codec.BOOLEAN),
         (worldMapConfig, o) -> worldMapConfig.displayPlayers = o,
         worldMapConfig -> worldMapConfig.displayPlayers
      )
      .build();
   protected boolean displaySpawn = true;
   protected boolean displayHome = true;
   protected boolean displayWarps = true;
   protected boolean displayDeathMarker = true;
   protected boolean displayPlayers = true;

   public boolean isDisplaySpawn() {
      return this.displaySpawn;
   }

   public boolean isDisplayHome() {
      return this.displayHome;
   }

   public boolean isDisplayWarps() {
      return this.displayWarps;
   }

   public boolean isDisplayDeathMarker() {
      return this.displayDeathMarker;
   }

   public boolean isDisplayPlayers() {
      return this.displayPlayers;
   }
}
