package com.hypixel.hytale.builtin.hytalegenerator.assets.worldstructures.mapcontentfield;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetCodecMapCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;

public abstract class ContentFieldAsset implements Cleanable, JsonAssetWithMap<String, DefaultAssetMap<String, ContentFieldAsset>> {
   public static final AssetCodecMapCodec<String, ContentFieldAsset> CODEC = new AssetCodecMapCodec<>(
      Codec.STRING, (t, k) -> t.id = k, t -> t.id, (t, data) -> t.data = data, t -> t.data
   );
   public static final Codec<String> CHILD_ASSET_CODEC = new ContainedAssetCodec<>(ContentFieldAsset.class, CODEC);
   public static final Codec<String[]> CHILD_ASSET_CODEC_ARRAY = new ArrayCodec<>(CHILD_ASSET_CODEC, String[]::new);
   public static final BuilderCodec<ContentFieldAsset> ABSTRACT_CODEC = BuilderCodec.abstractBuilder(ContentFieldAsset.class).build();
   private String id;
   private AssetExtraInfo.Data data;

   protected ContentFieldAsset() {
   }

   public String getId() {
      return this.id;
   }

   @Override
   public void cleanUp() {
   }
}
