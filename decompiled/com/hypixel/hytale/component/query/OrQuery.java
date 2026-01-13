package com.hypixel.hytale.component.query;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.ComponentType;

public class OrQuery<ECS_TYPE> implements Query<ECS_TYPE> {
   private final Query<ECS_TYPE>[] queries;

   public OrQuery(Query<ECS_TYPE>... queries) {
      this.queries = queries;
   }

   @Override
   public boolean test(Archetype<ECS_TYPE> archetype) {
      for (Query<ECS_TYPE> query : this.queries) {
         if (query.test(archetype)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean requiresComponentType(ComponentType<ECS_TYPE, ?> componentType) {
      for (Query<ECS_TYPE> query : this.queries) {
         if (query.requiresComponentType(componentType)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void validateRegistry(ComponentRegistry<ECS_TYPE> registry) {
      for (Query<ECS_TYPE> query : this.queries) {
         query.validateRegistry(registry);
      }
   }

   @Override
   public void validate() {
      for (Query<ECS_TYPE> query : this.queries) {
         query.validate();
      }
   }
}
