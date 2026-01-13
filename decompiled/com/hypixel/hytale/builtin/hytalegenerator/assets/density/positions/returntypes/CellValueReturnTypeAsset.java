package com.hypixel.hytale.builtin.hytalegenerator.assets.density.positions.returntypes;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.CacheDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.ConstantDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.MultiCacheDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.positions.returntypes.CellValueReturnType;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.positions.returntypes.ReturnType;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.ReferenceBundle;
import com.hypixel.hytale.builtin.hytalegenerator.seed.SeedBox;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class CellValueReturnTypeAsset extends ReturnTypeAsset {
   public static final BuilderCodec<CellValueReturnTypeAsset> CODEC = BuilderCodec.builder(
         CellValueReturnTypeAsset.class, CellValueReturnTypeAsset::new, ReturnTypeAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Density", DensityAsset.CODEC, true), (t, k) -> t.densityAsset = k, t -> t.densityAsset)
      .add()
      .append(new KeyedCodec<>("DefaultValue", Codec.DOUBLE, false), (t, k) -> t.defaultValue = k, t -> t.defaultValue)
      .add()
      .build();
   private DensityAsset densityAsset = new ConstantDensityAsset();
   private double defaultValue = 0.0;

   @Nonnull
   @Override
   public ReturnType build(@Nonnull SeedBox parentSeed, @Nonnull ReferenceBundle referenceBundle, @Nonnull WorkerIndexer workerIndexer) {
      Density densityNode = this.densityAsset.build(new DensityAsset.Argument(parentSeed, referenceBundle, workerIndexer));
      Density cache = new MultiCacheDensity(densityNode, workerIndexer.getWorkerCount(), CacheDensityAsset.DEFAULT_CAPACITY);
      return new CellValueReturnType(cache, this.defaultValue, workerIndexer.getWorkerCount());
   }

   @Override
   public void cleanUp() {
      this.densityAsset.cleanUp();
   }
}
