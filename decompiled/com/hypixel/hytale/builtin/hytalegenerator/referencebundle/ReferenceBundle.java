package com.hypixel.hytale.builtin.hytalegenerator.referencebundle;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReferenceBundle {
   @Nonnull
   private final Map<String, Reference> dataLayerMap = new HashMap<>();
   @Nonnull
   private final Map<String, String> layerTypeMap = new HashMap<>();

   public <T extends Reference> void put(@Nonnull String name, @Nonnull Reference reference, @Nonnull Class<T> type) {
      this.dataLayerMap.put(name, reference);
      this.layerTypeMap.put(name, type.getName());
   }

   @Nullable
   public Reference getLayerWithName(@Nonnull String name) {
      return this.dataLayerMap.get(name);
   }

   @Nullable
   public <T extends Reference> T getLayerWithName(@Nonnull String name, @Nonnull Class<T> type) {
      String storedType = this.layerTypeMap.get(name);
      if (storedType == null) {
         return null;
      } else {
         return (T)(!storedType.equals(type.getName()) ? null : this.dataLayerMap.get(name));
      }
   }
}
