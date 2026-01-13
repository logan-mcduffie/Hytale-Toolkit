package com.hypixel.hytale.component.query;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.ComponentType;
import javax.annotation.Nonnull;

public class ExactArchetypeQuery<ECS_TYPE> implements Query<ECS_TYPE> {
   private final Archetype<ECS_TYPE> archetype;

   public ExactArchetypeQuery(Archetype<ECS_TYPE> archetype) {
      this.archetype = archetype;
   }

   public Archetype<ECS_TYPE> getArchetype() {
      return this.archetype;
   }

   @Override
   public boolean test(@Nonnull Archetype<ECS_TYPE> archetype) {
      return archetype.equals(this.archetype);
   }

   @Override
   public boolean requiresComponentType(@Nonnull ComponentType<ECS_TYPE, ?> componentType) {
      return this.archetype.requiresComponentType(componentType);
   }

   @Override
   public void validateRegistry(ComponentRegistry<ECS_TYPE> registry) {
      this.archetype.validateRegistry(registry);
   }

   @Override
   public void validate() {
      this.archetype.validate();
   }
}
