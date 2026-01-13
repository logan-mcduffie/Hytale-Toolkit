package com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.VerticalEliminatorPositionProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class VerticalEliminatorPositionProviderAsset extends PositionProviderAsset {
   public static final BuilderCodec<VerticalEliminatorPositionProviderAsset> CODEC = BuilderCodec.builder(
         VerticalEliminatorPositionProviderAsset.class, VerticalEliminatorPositionProviderAsset::new, PositionProviderAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("MaxY", Codec.INTEGER, true), (asset, v) -> asset.maxY = v, asset -> asset.maxY)
      .add()
      .append(new KeyedCodec<>("MinY", Codec.INTEGER, true), (asset, v) -> asset.minY = v, asset -> asset.minY)
      .add()
      .append(
         new KeyedCodec<>("Positions", PositionProviderAsset.CODEC, true), (asset, v) -> asset.positionProviderAsset = v, asset -> asset.positionProviderAsset
      )
      .add()
      .build();
   private int maxY = 0;
   private int minY = 0;
   private PositionProviderAsset positionProviderAsset = new ListPositionProviderAsset();

   @Nonnull
   @Override
   public PositionProvider build(@Nonnull PositionProviderAsset.Argument argument) {
      if (super.skip()) {
         return PositionProvider.noPositionProvider();
      } else {
         PositionProvider positionProvider = this.positionProviderAsset.build(argument);
         return new VerticalEliminatorPositionProvider(this.minY, this.maxY, positionProvider);
      }
   }

   @Override
   public void cleanUp() {
      this.positionProviderAsset.cleanUp();
   }
}
