package com.hypixel.hytale.component;

import com.hypixel.hytale.component.query.ReadWriteArchetypeQuery;

public class ReadWriteQuery<ECS_TYPE> implements ReadWriteArchetypeQuery<ECS_TYPE> {
   private final Archetype<ECS_TYPE> read;
   private final Archetype<ECS_TYPE> write;

   public ReadWriteQuery(Archetype<ECS_TYPE> read, Archetype<ECS_TYPE> write) {
      this.read = read;
      this.write = write;
   }

   @Override
   public Archetype<ECS_TYPE> getReadArchetype() {
      return this.read;
   }

   @Override
   public Archetype<ECS_TYPE> getWriteArchetype() {
      return this.write;
   }
}
