package com.hypixel.hytale.builtin.hytalegenerator.props.prefab;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.codecs.EnumCodec;

public enum MoldingDirection {
   NONE,
   UP,
   DOWN,
   NORTH,
   SOUTH,
   EAST,
   WEST;

   public static final Codec<MoldingDirection> CODEC = new EnumCodec<>(MoldingDirection.class, EnumCodec.EnumStyle.LEGACY);
}
