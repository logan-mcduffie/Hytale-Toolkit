package com.hypixel.hytale.component.query;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.ComponentType;

public class NotQuery<ECS_TYPE> implements Query<ECS_TYPE> {
   private final Query<ECS_TYPE> query;

   public NotQuery(Query<ECS_TYPE> query) {
      this.query = query;
   }

   @Override
   public boolean test(Archetype<ECS_TYPE> archetype) {
      return !this.query.test(archetype);
   }

   @Override
   public boolean requiresComponentType(ComponentType<ECS_TYPE, ?> componentType) {
      return this.query.requiresComponentType(componentType);
   }

   @Override
   public void validateRegistry(ComponentRegistry<ECS_TYPE> registry) {
      this.query.validateRegistry(registry);
   }

   @Override
   public void validate() {
      this.query.validate();
   }
}
