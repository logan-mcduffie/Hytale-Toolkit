package com.hypixel.hytale.builtin.hytalegenerator.biomemap;

import com.hypixel.hytale.builtin.hytalegenerator.biome.BiomeType;
import com.hypixel.hytale.builtin.hytalegenerator.framework.interfaces.functions.BiCarta;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public class SimpleBiomeMap<V> extends BiomeMap<V> {
   private int defaultTransitionRadius;
   private Map<Long, Integer> pairHashToRadius;
   private BiCarta<BiomeType> carta;

   public SimpleBiomeMap(@Nonnull BiCarta<BiomeType> carta) {
      this.carta = carta;
      this.defaultTransitionRadius = 1;
      this.pairHashToRadius = new HashMap<>();
   }

   public void setDefaultRadius(int defaultRadius) {
      if (defaultRadius <= 0) {
         throw new IllegalArgumentException();
      } else {
         this.defaultTransitionRadius = defaultRadius;
      }
   }

   public BiomeType apply(int x, int z, @Nonnull WorkerIndexer.Id id) {
      return this.carta.apply(x, z, id);
   }

   @Override
   public List<BiomeType> allPossibleValues() {
      return this.carta.allPossibleValues();
   }
}
