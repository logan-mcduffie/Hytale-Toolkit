package com.hypixel.hytale.builtin.hytalegenerator.assets.worldstructures.mapcontentfield;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class BaseHeightContentFieldAsset extends ContentFieldAsset {
   public static final BuilderCodec<BaseHeightContentFieldAsset> CODEC = BuilderCodec.builder(
         BaseHeightContentFieldAsset.class, BaseHeightContentFieldAsset::new, ContentFieldAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Name", Codec.STRING, true), (t, k) -> t.name = k, t -> t.name)
      .add()
      .append(new KeyedCodec<>("Y", Codec.DOUBLE, false), (t, k) -> t.y = k, t -> t.y)
      .add()
      .build();
   private String id;
   private AssetExtraInfo.Data data;
   private String name = "";
   private double y = 0.0;

   private BaseHeightContentFieldAsset() {
   }

   @Override
   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public double getY() {
      return this.y;
   }
}
