package com.hypixel.hytale.server.core.asset.type.portalworld;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3i;

public class PortalSpawn {
   public static final BuilderCodec<PortalSpawn> CODEC = BuilderCodec.builder(PortalSpawn.class, PortalSpawn::new)
      .append(new KeyedCodec<>("Y", Codec.INTEGER), (spawn, o) -> spawn.checkSpawnY = o, spawn -> spawn.checkSpawnY)
      .documentation("The Y height where to start looking for X,Z candidate.")
      .add()
      .<Integer>append(new KeyedCodec<>("ScanHeight", Codec.INTEGER), (spawn, o) -> spawn.scanHeight = o, spawn -> spawn.scanHeight)
      .documentation("How many blocks to scan downwards after picking a X,Y,Z candidate.")
      .add()
      .<Integer>append(new KeyedCodec<>("MinRadius", Codec.INTEGER), (spawn, o) -> spawn.minRadius = o, spawn -> spawn.minRadius)
      .documentation("Picks a random X,Z point around center at [MinRadius]-[MaxRadius] radius to find chunks.")
      .add()
      .<Integer>append(new KeyedCodec<>("MaxRadius", Codec.INTEGER), (spawn, o) -> spawn.maxRadius = o, spawn -> spawn.maxRadius)
      .documentation("Picks a random X,Z point around center at [MinRadius]-[MaxRadius] radius to find chunks.")
      .add()
      .<Vector3i>append(new KeyedCodec<>("Center", Vector3i.CODEC), (spawn, o) -> spawn.center = o, spawn -> spawn.center)
      .documentation("Picks a random X,Z point around [Center] at Radius radius.")
      .add()
      .<Integer>append(new KeyedCodec<>("ChunkDartThrows", Codec.INTEGER), (spawn, o) -> spawn.chunkDartThrows = o, spawn -> spawn.chunkDartThrows)
      .documentation("How many attempts at picking a spawn.")
      .add()
      .<Integer>append(new KeyedCodec<>("ChecksPerChunk", Codec.INTEGER), (spawn, o) -> spawn.checksPerChunk = o, spawn -> spawn.checksPerChunk)
      .documentation("For every chunk, how many random location checks are done within the chunk.")
      .add()
      .build();
   private Vector3i center = Vector3i.ZERO.clone();
   private int scanHeight = 16;
   private int checkSpawnY;
   private int minRadius;
   private int maxRadius;
   private int chunkDartThrows = 20;
   private int checksPerChunk = 5;

   public Vector3i getCenter() {
      return this.center;
   }

   public int getCheckSpawnY() {
      return this.checkSpawnY;
   }

   public int getScanHeight() {
      return this.scanHeight;
   }

   public int getMinRadius() {
      return this.minRadius;
   }

   public int getMaxRadius() {
      return this.maxRadius;
   }

   public int getChunkDartThrows() {
      return this.chunkDartThrows;
   }

   public int getChecksPerChunk() {
      return this.checksPerChunk;
   }
}
